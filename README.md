# infra-tmpl

生产级 Java 微服务基础设施模板。多模块 Gradle 项目，每个服务独立部署，通过 Nacos 注册发现、Gateway 统一路由。

## 架构

```
                          ┌─────────────┐
                          │   Gateway   │ :8080
                          │  (SC-Gateway)│
                          └──────┬──────┘
                                 │
            ┌────────────┬───────┼───────┬────────────┐
            │            │       │       │            │
       ┌────▼────┐ ┌────▼────┐ ┌▼──────┐ ┌▼──────┐ ┌──▼──────┐
       │  User   │ │  Order  │ │Search │ │Storage│ │  Event  │
       │  :8081  │ │  :8082  │ │ :8083 │ │ :8084 │ │  :8085  │
       │Postgres │ │Postgres │ │  ES   │ │ MinIO │ │Kafka+Rds│
       └────┬────┘ └────┬────┘ └──────┘ └──────┘ └─────────┘
            │            │
       ┌────▼────────────▼────┐
       │   Seata (AT 模式)    │
       │   分布式事务协调      │
       └──────────────────────┘
```

## 模块

| 模块 | 端口 | 职责 | 技术 |
|------|------|------|------|
| `infra-common` | — | 公共代码（DTO、异常处理、OpenAPI） | — |
| `infra-gateway` | 8080 | API 网关、路由、Sentinel 限流 | Spring Cloud Gateway |
| `infra-user` | 8081 | 用户 CRUD | PostgreSQL + Redis |
| `infra-order` | 8082 | 订单（分布式事务演示） | Seata AT + OpenFeign |
| `infra-search` | 8083 | 全文搜索 | Elasticsearch |
| `infra-storage` | 8084 | 文件存储 | MinIO |
| `infra-event` | 8085 | 消息 + 缓存 | Kafka + Redis |

## 基础设施组件

| 组件 | 端口 | 控制台 |
|------|------|--------|
| PostgreSQL | 5432 | — |
| Redis | 6379 | — |
| Elasticsearch | 9200 | — |
| MinIO | 9000/9001 | http://localhost:9001 |
| Kafka | 9092 | — |
| Nacos | 8848 | http://localhost:8848/nacos |
| Seata Server | 8091 | http://localhost:7091 |
| Sentinel | 8858 | http://localhost:8858 |
| Prometheus | 9091 | — |
| Grafana | 3000 | http://localhost:3000 |
| Jaeger | 16686 | http://localhost:16686 |

## 快速开始

```bash
# 1. 启动基础设施（Nacos、Seata、Sentinel、PostgreSQL、Redis、ES、MinIO、Kafka）
make up

# 2. 构建全部服务
make build

# 3. 分别启动各服务（各开一个终端）
make run-gateway    # :8080
make run-user       # :8081
make run-order      # :8082
make run-search     # :8083
make run-storage    # :8084
make run-event      # :8085
```

### Docker Compose 一键启动

```bash
make up-all   # 构建镜像并启动全部容器
```

## REST API（通过 Gateway :8080 访问）

| 端点 | 方法 | 服务 | 说明 |
|------|------|------|------|
| `/api/v1/users` | CRUD | user | 用户管理 |
| `/api/v1/orders` | POST | order | 创建订单（Seata 分布式事务） |
| `/api/v1/search` | POST | search | 全文搜索 |
| `/api/v1/storage` | POST/GET | storage | 文件上传下载 |
| `/api/v1/events` | POST/GET | event | 消息发布消费 |
| `/api/v1/cache` | CRUD | event | Redis 缓存 |

## 测试

```bash
make test
```

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| Java | JDK | 17 |
| 框架 | Spring Boot | 3.3.5 |
| 微服务 | Spring Cloud Alibaba | 2023.0.3.2 |
| 网关 | Spring Cloud Gateway | 2023.0.3 |
| 注册/配置 | Nacos | 2.4.3 |
| 分布式事务 | Seata (AT) | 2.0.0 |
| 限流熔断 | Sentinel | 1.8.8 |
| 服务调用 | OpenFeign | — |
| 数据库 | PostgreSQL | 16 |
| 缓存 | Redis | 7 |
| 搜索 | Elasticsearch | 8.15 |
| 存储 | MinIO | latest |
| 消息 | Kafka | 3.7 |
| 监控 | Prometheus + Grafana | — |
| 链路 | Jaeger | — |

## 项目结构

```
infra-tmpl/
├── build.gradle.kts              # 根构建（公共配置）
├── settings.gradle.kts           # 多模块声明
├── gradle/libs.versions.toml     # 版本目录
├── Makefile                      # 常用命令
├── docker/                       # Docker Compose + 基础设施配置
├── infra-common/                 # 公共模块
│   └── src/main/java/com/infra/common/
│       ├── dto/                  #   DTO（UserDto、EventMessage 等）
│       └── config/               #   通用配置（OpenAPI、全局异常）
├── infra-gateway/                # 网关服务
├── infra-user/                   # 用户服务
├── infra-order/                  # 订单服务（Seata）
├── infra-search/                 # 搜索服务（ES）
├── infra-storage/                # 存储服务（MinIO）
└── infra-event/                  # 事件服务（Kafka + Redis）
```

## License

MIT
