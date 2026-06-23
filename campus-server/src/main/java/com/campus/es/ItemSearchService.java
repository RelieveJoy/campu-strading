package com.campus.es;

import com.campus.dto.ItemPageQueryDTO;
import com.campus.entity.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.campus.mapper.ItemMapper;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemSearchService {

    @Autowired(required = false)
    private ElasticsearchOperations esTemplate;
    @Autowired
    private ItemMapper itemMapper;

    private boolean available() { return esTemplate != null; }

    /** 全量同步所有商品到 ES（启动时调用一次） */
    public void indexAll() {
        if (!available()) return;
        try {
            var dto = new com.campus.dto.ItemPageQueryDTO();
            dto.setPage(1);
            dto.setPageSize(1000);
            var items = itemMapper.pageQuery(dto);
            if (items.isEmpty()) return;
            var docs = items.stream().map(vo -> {
                var item = itemMapper.getById(vo.getItemId());
                return toDocument(item);
            }).collect(Collectors.toList());
            esTemplate.save(docs);
            log.info("ES 全量索引完成，共 {} 条", docs.size());
        } catch (Exception e) {
            log.warn("ES 全量索引失败: {}", e.getMessage());
        }
    }

    /** 同步单个商品到 ES */
    public void index(Item item) {
        if (!available()) return;
        try {
            esTemplate.save(toDocument(item));
        } catch (Exception e) {
            log.warn("ES 同步失败: itemId={}, {}", item.getItemId(), e.getMessage());
        }
    }

    /** 从 ES 删除 */
    public void delete(Long itemId) {
        if (!available()) return;
        try {
            esTemplate.delete(String.valueOf(itemId), ItemDocument.class);
        } catch (Exception e) {
            log.warn("ES 删除失败: itemId={}, {}", itemId, e.getMessage());
        }
    }

    /** ES 关键词搜索，返回匹配的 itemId 列表 */
    public List<Long> search(ItemPageQueryDTO dto) {
        if (!available()) return Collections.emptyList();
        if (dto.getKeyword() == null || dto.getKeyword().isBlank()) {
            return Collections.emptyList();
        }
        try {
            // 动态构建 filter 数组
            var filters = new java.util.ArrayList<String>();
            if (dto.getCategoryId() != null) filters.add("{ \"term\": { \"categoryId\": " + dto.getCategoryId() + " } }");
            if (dto.getItemCondition() != null) filters.add("{ \"term\": { \"itemCondition\": " + dto.getItemCondition() + " } }");
            if (dto.getStatus() != null) filters.add("{ \"term\": { \"status\": " + dto.getStatus() + " } }");
            String filterJson = filters.isEmpty() ? "" : ", \"filter\": [" + String.join(",", filters) + "]";

            var q = new StringQuery("""
                {
                  "bool": {
                    "must": [
                      { "bool": { "should": [
                        { "multi_match": { "query": "%s", "fields": ["title","description"] } },
                        { "match": { "title.char": { "query": "%s", "operator": "and" } } }
                      ], "minimum_should_match": 1 } }
                    ]%s
                  }
                }
                """.formatted(
                    dto.getKeyword().replace("\"", "\\\""),
                    dto.getKeyword().replace("\"", "\\\""),
                    filterJson
                ));
            return esTemplate.search(q, ItemDocument.class)
                    .getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .map(ItemDocument::getItemId)
                    .toList();
        } catch (Exception e) {
            log.warn("ES 搜索异常，回退 MySQL: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private ItemDocument toDocument(Item item) {
        return ItemDocument.builder()
                .itemId(item.getItemId())
                .title(item.getTitle())
                .description(item.getDescription())
                .price(item.getPrice())
                .imageUrl(item.getImageUrl())
                .categoryId(item.getCategoryId())
                .status(item.getStatus())
                .itemCondition(item.getItemCondition())
                .viewCount(item.getViewCount())
                .sellerId(item.getSellerId())
                .build();
    }
}
