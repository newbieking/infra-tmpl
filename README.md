# infra-tmpl

生产级 Java 基础设施模板项目。克隆即用，一条命令启动完整中间件栈。

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 17 (LTS) |
| 框架 | Spring Boot | 3.3.5 |
| 微服务 | Spring Cloud Alibaba | 2023.0.3.2 |
| 构建 | Gradle (Kotlin DSL) | 8.x |
| 数据库 | PostgreSQL | 16 |
| 缓存 | Redis | 7 |
| 搜索 | Elasticsearch | 8.15 |
| 对象存储 | MinIO | latest |
| 消息队列 | Kafka (KRaft) | 3.7 |
| 注册/配置中心 | Nacos | 2.4.3 |
| 分布式事务 | Seata (AT 模式) | 2.0.0 |
| 限流熔断 | Sentinel | 1.8.8 |
| 监控 | Prometheus + Grafana | — |
| 链路追踪 | Jaeger (OpenTelemetry) | — |
| API 文档 | Springdoc OpenAPI | 2.6.0 |

## 快速开始

### 前置条件

- JDK 17+
- Docker Desktop 4.34.x（推荐）
- Git

### 启动

```bash
git clone <repo-url> infra-tmpl
cd infra-tmpl

# 启动全部基础设施（11 个服务）
make up

# 启动应用
make run
```

### 访问地址

| 地址 | 用途 |
|------|------|
| http://localhost:8080/swagger-ui.html | API 文档 |
| http://localhost:8080/actuator/health | 健康检查 |
| http://localhost:8848/nacos | Nacos 控制台（nacos/nacos） |
| http://localhost:8858 | Sentinel 控制台（sentinel/sentinel） |
| http://localhost:7091 | Seata 控制台 |
| http://localhost:3000 | Grafana 面板（admin/admin） |
| http://localhost:16686 | Jaeger 链路追踪 |
| http://localhost:9001 | MinIO 控制台（minioadmin/minioadmin123） |

## Makefile 命令

| 命令 | 说明 |
|------|------|
| `make up` | 启动全部基础设施容器 |
| `make down` | 停止并移除容器 |
| `make build` | 编译项目（跳过测试） |
| `make test` | 运行全部测试 |
| `make run` | 启动 Spring Boot 应用 |
| `make clean` | 清理构建产物 |
| `make logs` | 查看容器日志 |
| `make ps` | 查看运行中的容器 |

## REST API

### 基础设施演示

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/v1/users` | GET/POST/PUT/DELETE | 用户 CRUD（PostgreSQL） |
| `/api/v1/cache/{key}` | GET/PUT/DELETE | 缓存操作（Redis） |
| `/api/v1/search` | POST | 全文搜索（Elasticsearch） |
| `/api/v1/search/index/{index}` | POST | 索引文档 |
| `/api/v1/storage` | POST/GET | 文件上传下载（MinIO） |
| `/api/v1/events` | POST/GET | 消息发布消费（Kafka） |

### 微服务组件演示

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/v1/tx/order` | POST | Seata 分布式事务（AT 模式） |
| `/api/v1/flow/query` | GET | Sentinel 限流熔断降级 |
| `/api/v1/flow/slow` | GET | Sentinel 慢查询保护 |
| `/api/v1/config` | GET | Nacos 动态配置热更新 |

### 运维端点

| 端点 | 方法 | 说明 |
|------|------|------|
| `/actuator/health` | GET | 全服务健康检查 |
| `/actuator/prometheus` | GET | Prometheus 指标抓取 |

## 项目结构

```
src/main/java/com/infra/template/
├── Application.java                  # 启动入口
├── config/                           # Bean 配置（Redis、Kafka、ES、MinIO、OpenAPI）
├── controller/                       # REST 控制器
│   ├── UserController.java           #   用户 CRUD
│   ├── CacheController.java          #   Redis 缓存
│   ├── SearchController.java         #   ES 搜索
│   ├── StorageController.java        #   MinIO 存储
│   ├── EventController.java          #   Kafka 事件
│   ├── TransactionController.java    #   Seata 分布式事务
│   ├── FlowControlController.java    #   Sentinel 限流熔断
│   ├── NacosConfigController.java    #   Nacos 配置中心
│   └── GlobalExceptionHandler.java   #   全局异常处理
├── service/                          # 业务逻辑
├── repository/                       # Spring Data JPA
├── entity/                           # JPA 实体
├── dto/                              # 数据传输对象（Java Record + Jakarta 校验）
├── kafka/                            # Kafka 生产者/消费者
├── health/                           # 自定义健康检查（MinIO、Kafka、Nacos）
└── metrics/                          # Micrometer/Prometheus 配置
```

## 测试

```bash
# 全部测试（63 个用例）
make test

# 单元测试（无需 Docker）
gradlew.bat test --tests "com.infra.template.service.*"
gradlew.bat test --tests "com.infra.template.controller.*"

# 集成测试（需要 Docker）
gradlew.bat test --tests "com.infra.template.integration.*"
```

| 测试类 | 类型 | 用例 | 覆盖范围 |
|--------|------|------|---------|
| `CacheServiceTest` | 单元 | 7 | Redis put/get/delete、TTL |
| `SearchServiceTest` | 单元 | 4 | ES index/search |
| `StorageServiceTest` | 单元 | 3 | MinIO upload/download |
| `EventServiceTest` | 单元 | 2 | Kafka publish |
| `KafkaProducerServiceTest` | 单元 | 2 | KafkaTemplate |
| `CacheControllerTest` | 单元 | 6 | REST + 校验 |
| `SearchControllerTest` | 单元 | 4 | REST + 校验 |
| `StorageControllerTest` | 单元 | 2 | 文件操作 |
| `EventControllerTest` | 单元 | 5 | REST + 校验 |
| `GlobalExceptionHandlerTest` | 单元 | 2 | 400/500 映射 |
| `TransactionControllerTest` | 单元 | 1 | Seata 事务 |
| `FlowControlControllerTest` | 单元 | 3 | Sentinel 限流 |
| `NacosConfigControllerTest` | 单元 | 1 | Nacos 配置 |
| `InfrastructureIntegrationTest` | 集成 | 22 | 全栈 CRUD |
| `UserControllerTest` | 集成 | 3 | User 端到端 |
| `ApplicationTests` | 集成 | 1 | 上下文加载 |

## 配置

### 环境变量

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `DB_HOST` | localhost | PostgreSQL |
| `REDIS_HOST` | localhost | Redis |
| `ES_HOST` | localhost | Elasticsearch |
| `KAFKA_SERVER` | localhost:9092 | Kafka |
| `MINIO_ENDPOINT` | http://localhost:9000 | MinIO |
| `NACOS_ADDR` | localhost:8848 | Nacos |
| `SENTINEL_DASHBOARD` | localhost:8858 | Sentinel |
| `SEATA_ENABLED` | true | Seata 开关 |

### Profile

| Profile | 用途 |
|---------|------|
| `local` | 默认，连接 Docker Compose 服务 |
| `test` | 测试环境，禁用 Nacos/Sentinel/Seata，Testcontainers 管理数据层 |

## 微服务组件说明

### Nacos（注册/配置中心）

- **服务注册**：应用启动自动注册到 Nacos，其他服务可通过服务名发现
- **配置中心**：支持 `@RefreshScope` 动态刷新配置，修改 Nacos 配置即时生效
- **控制台**：http://localhost:8848/nacos（nacos/nacos）

### Seata（分布式事务）

- **默认模式**：AT（Automatic Transaction）模式，对业务代码零侵入
- **使用方式**：在方法上添加 `@GlobalTransactional` 注解
- **工作原理**：自动拦截 SQL 生成 undo_log，异常时自动回滚所有分支事务
- **控制台**：http://localhost:7091

### Sentinel（限流熔断降级）

- **限流**：控制 QPS/线程数，超出阈值直接拒绝
- **熔断**：慢调用比例/异常比例达到阈值自动熔断
- **降级**：熔断后返回兜底响应（fallback）
- **使用方式**：`@SentinelResource` 注解 + 控制台规则配置
- **控制台**：http://localhost:8858（sentinel/sentinel）

## 版本兼容性

| Docker Desktop | Docker Engine | Testcontainers | 兼容性 |
|---|---|---|---|
| **4.34.x** | **27.x** | **1.20.4** | ✅ 推荐，零配置 |
| 4.35-4.37 | 25-27.x | 1.20.4 | ✅ |
| 4.38-4.40 | 28.x | 1.20.4 | ⚠️ 可能需要 httpclient5 |
| 4.41+ | 29.x | 1.20.4 | ❌ 需要 httpclient5 hack |

## 构建配置

- **JVM 参数**：`-Xmx2g -XX:+UseG1GC`（`gradle.properties`）
- **版本管理**：`gradle/libs.versions.toml`（Gradle Version Catalog）
- **并行构建** + **构建缓存** 已启用

## License

MIT
