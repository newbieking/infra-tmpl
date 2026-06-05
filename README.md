# infra-tmpl

生产级 Java 基础设施模板项目。克隆即用，一条命令启动完整中间件栈。

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 17 (LTS) |
| 框架 | Spring Boot | 3.3.5 |
| 构建 | Gradle (Kotlin DSL) | 8.x |
| 数据库 | PostgreSQL | 16 |
| 缓存 | Redis | 7 |
| 搜索 | Elasticsearch | 8.15 |
| 对象存储 | MinIO | latest |
| 消息队列 | Kafka (KRaft) | 3.7 |
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
# 克隆项目
git clone <repo-url> infra-tmpl
cd infra-tmpl

# 启动基础设施（PostgreSQL、Redis、ES、MinIO、Kafka、Prometheus、Grafana、Jaeger）
make up

# 启动应用
make run
```

应用启动后访问：

| 地址 | 用途 |
|------|------|
| http://localhost:8080/swagger-ui.html | API 文档 |
| http://localhost:8080/actuator/health | 健康检查 |
| http://localhost:8080/actuator/prometheus | Prometheus 指标 |
| http://localhost:3000 | Grafana 面板（admin/admin） |
| http://localhost:16686 | Jaeger 链路追踪 |
| http://localhost:9001 | MinIO 控制台（minioadmin/minioadmin123） |

### 停止

```bash
make down
```

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

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/v1/users` | GET/POST/PUT/DELETE | 用户 CRUD（PostgreSQL） |
| `/api/v1/cache/{key}` | GET/PUT/DELETE | 缓存操作（Redis） |
| `/api/v1/search` | POST | 全文搜索（Elasticsearch） |
| `/api/v1/search/index/{index}` | POST | 索引文档 |
| `/api/v1/storage` | POST/GET | 文件上传下载（MinIO） |
| `/api/v1/events` | POST/GET | 消息发布消费（Kafka） |
| `/actuator/health` | GET | 全服务健康检查 |
| `/actuator/prometheus` | GET | Prometheus 抓取端点 |

## 项目结构

```
src/main/java/com/infra/template/
├── Application.java                  # 启动入口
├── config/                           # Bean 配置（Redis、Kafka、ES、MinIO、OpenAPI）
├── controller/                       # REST 控制器（薄层，委托给 Service）
├── service/                          # 业务逻辑，每个基础设施组件一个 Service
├── repository/                       # Spring Data JPA
├── entity/                           # JPA 实体
├── dto/                              # 数据传输对象（Java Record + Jakarta 校验）
├── kafka/                            # Kafka 生产者/消费者
├── health/                           # 自定义健康检查（MinIO、Kafka）
└── metrics/                          # Micrometer/Prometheus 配置

src/test/java/com/infra/template/
├── service/                          # Service 层单元测试（Mockito）
├── controller/                       # Controller 层单元测试（MockMvc）
└── integration/                      # 集成测试（Testcontainers）
```

## 测试

### 运行全部测试

```bash
make test
# 或
gradlew.bat test
```

### 分层运行

```bash
# 单元测试（无需 Docker）
gradlew.bat test --tests "com.infra.template.service.*"
gradlew.bat test --tests "com.infra.template.controller.*"

# 集成测试（需要 Docker）
gradlew.bat test --tests "com.infra.template.integration.*"
```

### 测试覆盖

| 测试类 | 类型 | 用例数 | 覆盖范围 |
|--------|------|--------|---------|
| `CacheServiceTest` | 单元 | 7 | Redis put/get/delete、TTL |
| `SearchServiceTest` | 单元 | 4 | ES index/search、空结果、null score |
| `StorageServiceTest` | 单元 | 3 | MinIO upload/download、异常传播 |
| `EventServiceTest` | 单元 | 2 | Kafka publish 委托 |
| `KafkaProducerServiceTest` | 单元 | 2 | KafkaTemplate 调用、失败处理 |
| `CacheControllerTest` | 单元 | 6 | REST + 参数校验 |
| `SearchControllerTest` | 单元 | 4 | REST + 参数校验 |
| `StorageControllerTest` | 单元 | 2 | 文件上传下载 |
| `EventControllerTest` | 单元 | 5 | REST + 参数校验 |
| `GlobalExceptionHandlerTest` | 单元 | 2 | 400/500 异常映射 |
| `InfrastructureIntegrationTest` | 集成 | 22 | 全栈 CRUD + 健康检查 |
| `UserControllerTest` | 集成 | 3 | User CRUD 端到端 |
| `ApplicationTests` | 集成 | 1 | Spring 上下文加载 |

## 配置

### 环境变量

所有基础设施连接信息支持环境变量覆盖，均有默认值：

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `DB_HOST` | localhost | PostgreSQL 地址 |
| `DB_PORT` | 5432 | PostgreSQL 端口 |
| `REDIS_HOST` | localhost | Redis 地址 |
| `ES_HOST` | localhost | Elasticsearch 地址 |
| `KAFKA_HOST` | localhost | Kafka 地址 |
| `MINIO_ENDPOINT` | http://localhost:9000 | MinIO 地址 |

### Profile

| Profile | 用途 |
|---------|------|
| `local` | 默认，连接 Docker Compose 服务 |
| `test` | 测试环境，Testcontainers 自动管理 |

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

## 不包含

- 认证/授权（按需添加）
- CI/CD 流水线
- Kubernetes 部署清单

## License

MIT
