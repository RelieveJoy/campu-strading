# 校园二手交易平台

Spring Boot + Vue 3 前后端分离的校园二手交易 Web 应用。

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 2.7.3 |
| ORM | MyBatis + PageHelper | 2.2.0 |
| 数据库 | MySQL | 8.0 |
| 缓存 | Redis | 7.x |
| 鉴权 | JWT (jjwt) | 0.9.1 |
| 接口文档 | Knife4j (Swagger) | 3.0.2 |
| 图片存储 | 阿里云 OSS | 3.10.2 |
| 前端框架 | Vue 3 + Element Plus | 3.x |
| 可视化 | ECharts | 5.x |

## 功能模块

- **用户模块**：注册、登录、登出（Redis 黑名单）、个人信息管理、密码修改
- **商品模块**：发布、编辑、下架、上架、分页查询（关键词/分类/价格区间筛选）、商品详情（含卖家信息）
- **交易模块**：创建订单、确认完成、取消订单、订单查询
- **收藏模块**：收藏/取消收藏、收藏列表、收藏状态检查
- **文件上传**：图片上传至阿里云 OSS

## 快速开始

### 环境要求

- JDK 17+
- MySQL 8.0
- Redis 7.x
- Maven 3.6+

### 1. 创建数据库

```sql
CREATE DATABASE campus_trading DEFAULT CHARACTER SET utf8mb4;
```

执行项目规划书中的建表 SQL（user / category / item / orders / favorite 五张表）。

### 2. 配置文件

```bash
cp campus-server/src/main/resources/application-dev.yml.example \
   campus-server/src/main/resources/application-dev.yml
```

编辑 `application-dev.yml`，填入你的数据库、Redis、OSS 配置。

### 3. 启动后端

```bash
cd campus-trading
mvn clean compile
cd campus-server
mvn spring-boot:run
```

### 4. 访问接口文档

浏览器打开 `http://localhost:8080/doc.html`

## 项目结构

```
campus-trading/
├── campus-common/      # 公共模块（工具类、常量、异常、配置属性）
├── campus-pojo/        # POJO 模块（Entity、DTO、VO）
├── campus-server/      # 服务模块（Controller、Service、Mapper）
└── pom.xml             # 父 POM，依赖管理
```

## License

MIT
