# Elasticsearch 全文搜索 — 实现方案

> **目标**：将商品搜索从 MySQL FULLTEXT 升级到 Elasticsearch，实现中文分词、高亮、权重排序。
>
> **当前状态**：项目已使用 MySQL `FULLTEXT INDEX` + `MATCH ... AGAINST`（轻量方案），搜索接口在 `ItemMapper.xml:31`。
>
> **依赖前提**：项目已有 Docker Compose（MySQL + Redis + RabbitMQ），已使用 Spring Boot 3.x + MyBatis。

---

## 架构变化

```
现有（轻量）：
  HomeView.vue → GET /api/items?keyword=xxx
      → ItemController.pageQuery()
      → ItemMapper.xml: MATCH(i.title,i.description) AGAINST(...)
      → MySQL FULLTEXT INDEX ft_title_desc

升级后：
  HomeView.vue → GET /api/search?keyword=xxx
      → SearchController.search()
      → ElasticsearchRestTemplate (NativeSearchQuery)
      → ES ik_max_word 分词 + BM25 打分 + 高亮
      → 返回带高亮片段的结果
```

**原则**：MySQL 仍是 source of truth，ES 是搜索加速器。ES 同步失败不影响主流程。

---

## 实施步骤

### 步骤 1：Docker Compose 增加 ES 容器

**文件**：`<项目根目录>/docker-compose.yml`（如果已有，追加 `elasticsearch` 服务）

```yaml
version: '3.8'
services:
  elasticsearch:
    image: elasticsearch:7.17.25
    container_name: campus-es
    environment:
      - discovery.type=single-node
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
      - xpack.security.enabled=false
    ports:
      - "9200:9200"
      - "9300:9300"
    volumes:
      - es-data:/usr/share/elasticsearch/data
      - ./docker/es-plugins:/usr/share/elasticsearch/plugins
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9200"]
      interval: 10s
      timeout: 5s
      retries: 5

  # 项目已有的 mysql、redis、rabbitmq 保持不变...

volumes:
  es-data:
```

IK 分词器安装（在宿主机执行，只需一次）：

```bash
mkdir -p docker/es-plugins/ik
cd docker/es-plugins/ik
# 下载 IK 插件，版本必须与 ES 版本一致（7.17.25）
curl -L -o ik.zip https://github.com/infinilabs/analysis-ik/releases/download/v7.17.25/elasticsearch-analysis-ik-7.17.25.zip
unzip ik.zip && rm ik.zip
# 重启 ES 容器生效
docker-compose restart elasticsearch
```

验证 IK 安装成功：

```bash
curl "http://localhost:9200/_analyze" -H 'Content-Type: application/json' \
  -d '{"analyzer":"ik_max_word","text":"高等数学教材"}'
```

---

### 步骤 2：添加 Maven 依赖

**文件**：`campus-server/pom.xml`

在 `<dependencies>` 内已有 `redisson-spring-boot-starter` 的位置附近追加：

```xml
<!-- Elasticsearch -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
</dependency>
```

**文件**：`campus-server/src/main/resources/application.yml`

```yaml
spring:
  elasticsearch:
    uris: ${CAMPUS_ES_URI:http://localhost:9200}
```

`.env` 文件追加（可选，默认值已足够本地开发）：

```
CAMPUS_ES_URI=http://localhost:9200
```

---

### 步骤 3：创建 ES 文档模型

**新建文件**：`campus-server/src/main/java/com/campus/document/ItemDocument.java`

```java
package com.campus.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "campus_items")
@Setting(shards = 1, replicas = 0)
public class ItemDocument {

    @Id
    private Long itemId;

    /** 标题 — ik_max_word 分词，细粒度索引 */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    /** 描述 — 同上 */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String description;

    /** 分类名 — Keyword 不分词，用于精确过滤和展示 */
    @Field(type = FieldType.Keyword)
    private String categoryName;

    @Field(type = FieldType.Long)
    private Long categoryId;

    @Field(type = FieldType.Double)
    private Double price;

    @Field(type = FieldType.Double)
    private Double originalPrice;

    @Field(type = FieldType.Keyword)
    private String imageUrl;

    @Field(type = FieldType.Integer)
    private Integer viewCount;

    /** 1=在售, 0=下架, 2=已售出 — 搜索时必须过滤 status=1 */
    @Field(type = FieldType.Integer)
    private Integer status;

    @Field(type = FieldType.Keyword)
    private String sellerName;
}
```

---

### 步骤 4：创建 ES Repository

**新建文件**：`campus-server/src/main/java/com/campus/repository/ItemSearchRepository.java`

```java
package com.campus.repository;

import com.campus.document.ItemDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemSearchRepository extends ElasticsearchRepository<ItemDocument, Long> {
}
```

---

### 步骤 5：数据同步 — 商品变更时同步 ES

**修改文件**：`campus-server/src/main/java/com/campus/service/impl/ItemServiceImpl.java`

**改造要点**：

- 新增 `ItemSearchRepository` 和 `CategoryMapper`、`UserMapper` 注入
- 新增 `toDocument(Item)` 转换方法
- `add()`、`update()`、`delete()` 方法末尾增加 ES 同步（用 try-catch 包裹，失败只打日志不抛异常）

```java
// 新增注入
@Autowired
private ItemSearchRepository itemSearchRepository;
@Autowired
private CategoryMapper categoryMapper;
@Autowired
private UserMapper userMapper;

// ── 商品转 ES 文档 ──
private ItemDocument toDocument(Item item) {
    Category category = categoryMapper.getById(item.getCategoryId());
    User seller = userMapper.getById(item.getSellerId());
    return ItemDocument.builder()
            .itemId(item.getItemId())
            .title(item.getTitle())
            .description(item.getDescription())
            .price(item.getPrice())
            .originalPrice(item.getOriginalPrice())
            .categoryId(item.getCategoryId())
            .categoryName(category != null ? category.getName() : null)
            .imageUrl(item.getImageUrl())
            .viewCount(item.getViewCount())
            .status(item.getStatus())
            .sellerName(seller != null ? seller.getUsername() : null)
            .build();
}

// ── 同步 ES（容错）──
private void syncToEs(Item item) {
    try {
        itemSearchRepository.save(toDocument(item));
    } catch (Exception e) {
        log.warn("ES 同步失败（不影响主流程）: {}", e.getMessage());
    }
}

private void deleteFromEs(Long itemId) {
    try {
        itemSearchRepository.deleteById(itemId);
    } catch (Exception e) {
        log.warn("ES 删除失败（不影响主流程）: {}", e.getMessage());
    }
}
```

**在 `add()` 末尾追加**：

```java
syncToEs(item);
```

**在 `update()` 末尾追加**（在 `itemMapper.update(item)` 之后）：

```java
syncToEs(item);
```

**在 `delete()` 末尾追加**（`item.setStatus(0)` 之后）：

```java
syncToEs(item);
// 注意这里是用 status=0 更新，不是真删
```

**在 `relist()` 末尾追加**：

```java
syncToEs(item);
```

---

### 步骤 6：新建 ES 搜索接口

**新建文件**：`campus-server/src/main/java/com/campus/controller/SearchController.java`

> **注意**：项目已有 `SearchController`（见文件列表），请检查现有文件内容后，将以下搜索接口**合并进去**或**替换同名文件**。

```java
package com.campus.controller;

import com.campus.document.ItemDocument;
import com.campus.result.PageResult;
import com.campus.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/search")
@Slf4j
@Tag(name = "ES 全文搜索")
public class SearchController {

    @Autowired
    private ElasticsearchRestTemplate esTemplate;

    @Operation(summary = "搜索商品（ES 全文检索 + 高亮）")
    @GetMapping
    public Result<PageResult> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {

        // ① Bool Query：must（计分）+ filter（不计分但过滤）
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 关键词：标题权重 3.0，描述权重 1.0
        if (keyword != null && !keyword.isBlank()) {
            boolQuery.must(QueryBuilders.multiMatchQuery(keyword)
                    .field("title", 3.0f)
                    .field("description", 1.0f));
        }

        // 过滤：只搜在售
        boolQuery.filter(QueryBuilders.termQuery("status", 1));

        // 过滤：分类
        if (categoryId != null) {
            boolQuery.filter(QueryBuilders.termQuery("categoryId", categoryId));
        }

        // 过滤：价格区间
        if (minPrice != null || maxPrice != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("price")
                    .gte(minPrice != null ? minPrice : 0)
                    .lte(maxPrice != null ? maxPrice : Double.MAX_VALUE));
        }

        // ② 高亮配置
        HighlightBuilder highlightBuilder = new HighlightBuilder()
                .field("title").preTags("<em>").postTags("</em>").fragmentSize(100)
                .field("description").preTags("<em>").postTags("</em>").fragmentSize(200);

        // ③ 分页（ES 页从 0 开始）
        PageRequest pageable = PageRequest.of(page - 1, pageSize);

        // ④ 组装查询
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withHighlightBuilder(highlightBuilder)
                .withPageable(pageable)
                .build();

        // ⑤ 执行
        SearchHits<ItemDocument> hits = esTemplate.search(query, ItemDocument.class);

        // ⑥ 组装结果
        List<Map<String, Object>> records = new ArrayList<>();
        for (SearchHit<ItemDocument> hit : hits) {
            ItemDocument doc = hit.getContent();
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("itemId", doc.getItemId());
            map.put("title", doc.getTitle());
            map.put("description", doc.getDescription());
            map.put("price", doc.getPrice());
            map.put("originalPrice", doc.getOriginalPrice());
            map.put("imageUrl", doc.getImageUrl());
            map.put("categoryName", doc.getCategoryName());
            map.put("sellerName", doc.getSellerName());
            map.put("viewCount", doc.getViewCount());

            // 高亮：优先用高亮值
            if (hit.getHighlightField("title") != null) {
                map.put("titleHighlighted",
                        String.join("", hit.getHighlightField("title")));
            } else {
                map.put("titleHighlighted", doc.getTitle());
            }
            if (hit.getHighlightField("description") != null) {
                map.put("descriptionHighlighted",
                        String.join("", hit.getHighlightField("description")));
            } else {
                map.put("descriptionHighlighted",
                        doc.getDescription() != null ? doc.getDescription() : "");
            }

            records.add(map);
        }

        return Result.success(new PageResult(hits.getTotalHits(), records));
    }
}
```

---

### 步骤 7：前端适配

#### 7.1 新增搜索 API 函数

**修改文件**：`campus-trading-client/src/api/item.js`

在文件末尾追加：

```javascript
// ES 搜索
export const searchItems = (params) => request({ url: '/search', method: 'get', params })
```

#### 7.2 搜索模式判断

**修改文件**：`campus-trading-client/src/views/HomeView.vue`

**改动 1**：在 `<script setup>` 中修改 `fetchItems`，有 keyword 时走 ES 搜索：

```javascript
// 在文件的 import 区域追加
import { getItems, searchItems } from '../api/item'

// 修改 fetchItems 方法
const fetchItems = async () => {
  loading.value = true
  try {
    // 有关键词 → ES 搜索；纯浏览 → MySQL 分页
    if (keyword.value) {
      const params = { keyword: keyword.value, page: page.value, pageSize }
      if (categoryId.value != null) params.categoryId = categoryId.value
      if (minPrice.value != null) params.minPrice = minPrice.value
      if (maxPrice.value != null) params.maxPrice = maxPrice.value
      const res = await searchItems(params)
      items.value = res.data.records   // ES 返回格式不同
      total.value = res.data.total
    } else {
      const params = { page: page.value, pageSize }
      if (categoryId.value != null) params.categoryId = categoryId.value
      if (minPrice.value != null) params.minPrice = minPrice.value
      if (maxPrice.value != null) params.maxPrice = maxPrice.value
      const res = await getItems(params)
      items.value = res.data.records
      total.value = res.data.total
    }
  } catch (e) {
    items.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}
```

**改动 2**：卡片标题支持高亮渲染：

```html
<!-- 原来 -->
<h3 class="card-title">{{ item.title }}</h3>

<!-- 改为 -->
<h3 class="card-title" v-html="item.titleHighlighted || item.title"></h3>
```

**改动 3**：在 `<style scoped>` 末尾追加高亮样式：

```css
/* ES 搜索高亮 */
.card-title :deep(em) {
  color: var(--color-primary);
  font-style: normal;
  font-weight: 700;
}
```

---

### 步骤 8：首次全量索引（一次性脚本）

已有的历史商品不会自动进 ES，需要启动时触发一次全量同步。

**新建文件**：`campus-server/src/main/java/com/campus/runner/EsIndexInitializer.java`

```java
package com.campus.runner;

import com.campus.document.ItemDocument;
import com.campus.entity.Category;
import com.campus.entity.Item;
import com.campus.entity.User;
import com.campus.mapper.CategoryMapper;
import com.campus.mapper.ItemMapper;
import com.campus.mapper.UserMapper;
import com.campus.repository.ItemSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 应用启动后检查 ES 索引是否为空，为空则全量同步。
 * 幂等：已有数据不重复写入（ES save = upsert）。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EsIndexInitializer {

    private final ItemSearchRepository itemSearchRepository;
    private final ItemMapper itemMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        try {
            long count = itemSearchRepository.count();
            if (count > 0) {
                log.info("ES 索引已有 {} 条文档，跳过全量同步", count);
                return;
            }

            log.info("开始全量同步商品到 ES...");
            List<Item> items = itemMapper.getAll();  // 如果 ItemMapper 没有 getAll，需要加一个
            for (Item item : items) {
                Category category = categoryMapper.getById(item.getCategoryId());
                User seller = userMapper.getById(item.getSellerId());
                ItemDocument doc = ItemDocument.builder()
                        .itemId(item.getItemId())
                        .title(item.getTitle())
                        .description(item.getDescription())
                        .price(item.getPrice())
                        .originalPrice(item.getOriginalPrice())
                        .categoryId(item.getCategoryId())
                        .categoryName(category != null ? category.getName() : null)
                        .imageUrl(item.getImageUrl())
                        .viewCount(item.getViewCount())
                        .status(item.getStatus())
                        .sellerName(seller != null ? seller.getUsername() : null)
                        .build();
                itemSearchRepository.save(doc);
            }
            log.info("ES 全量同步完成，共 {} 条", items.size());
        } catch (Exception e) {
            log.warn("ES 全量同步失败（不影响正常运行）: {}", e.getMessage());
        }
    }
}
```

如果 `ItemMapper` 没有 `getAll()`，新增：

```java
// ItemMapper.java 追加
@Select("select * from item")
List<Item> getAll();
```

---

### 步骤 9：CategoryMapper 补充（如果不存在）

ES 同步需要查分类名。检查 `CategoryMapper` 是否有 `getById` 方法，如果没有：

**新建文件**：`campus-server/src/main/java/com/campus/mapper/CategoryMapper.java`

```java
package com.campus.mapper;

import com.campus.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CategoryMapper {

    @Select("select * from category where category_id = #{categoryId}")
    Category getById(Long categoryId);
}
```

---

## 实施顺序

```
□ 步骤 1：Docker Compose 加 ES + 安装 IK 分词器
□ 步骤 2：Maven 依赖 + application.yml
□ 步骤 3：新建 ItemDocument.java
□ 步骤 4：新建 ItemSearchRepository.java
□ 步骤 9：补充 CategoryMapper（如需要）
□ 步骤 5：改造 ItemServiceImpl（注入 + toDocument + syncToEs + 四个方法追加同步）
□ 步骤 6：新建 SearchController.java（注意合并，不要覆盖已有 SearchController）
□ 步骤 7：前端 HomeView.vue 适配
□ 步骤 8：新建 EsIndexInitializer.java 全量同步
□ 验证：docker-compose up -d → 启动项目 → 发布一个商品 → GET /api/search?keyword=xxx
```

---

## 测试验证

```bash
# 1. 确认 ES 运行
curl http://localhost:9200

# 2. 确认 IK 可用
curl "http://localhost:9200/_analyze" -H 'Content-Type: application/json' \
  -d '{"analyzer":"ik_max_word","text":"高等数学"}'

# 3. 发布一个测试商品（或全量同步后）
# 4. 搜索
curl "http://localhost:8080/api/search?keyword=高等数学&page=1&pageSize=10"

# 预期响应格式：
# {
#   "code": 1,
#   "data": {
#     "total": 3,
#     "records": [
#       {
#         "itemId": 1,
#         "title": "高等数学教材",
#         "titleHighlighted": "<em>高等</em><em>数学</em>教材",
#         "descriptionHighlighted": "...",
#         "price": 15.0,
#         ...
#       }
#     ]
#   }
# }
```

---

## 关键技术决策说明

| 决策 | 原因 |
|------|------|
| ES 版本选 7.17.x | 最后的 Apache 2.0 开源版本，生产验证最多 |
| `ik_max_word` 索引 + `ik_smart` 搜索 | 索引时分细粒度高召回，搜索时粗粒度防噪音 |
| title 权重 3 : description 权重 1 | 标题命中比描述命中更相关 |
| ES 同步 try-catch 容错 | ES 不可用时不影响下单/编辑主流程 |
| `filter` 不参与打分（价格/分类/状态） | 只有关键词参与 BM25 相关性计算 |
| 保留原有 `ItemController.pageQuery()` | 无关键词的浏览场景仍走 MySQL（简单、可靠） |
