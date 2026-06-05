The user wants me to list all the test commands so they can manually reproduce the tests.## 全部测试命令

### 1. 运行全部单元测试（无需 Docker）

```bash
gradlew.bat test --tests "com.infra.template.service.*" --tests "com.infra.template.controller.CacheControllerTest" --tests "com.infra.template.controller.SearchControllerTest" --tests "com.infra.template.controller.StorageControllerTest" --tests "com.infra.template.controller.EventControllerTest" --tests "com.infra.template.controller.GlobalExceptionHandlerTest"
```

### 2. 逐个运行各测试类

**Service 层：**
```bash
gradlew.bat test --tests "com.infra.template.service.CacheServiceTest"
gradlew.bat test --tests "com.infra.template.service.SearchServiceTest"
gradlew.bat test --tests "com.infra.template.service.StorageServiceTest"
gradlew.bat test --tests "com.infra.template.service.EventServiceTest"
gradlew.bat test --tests "com.infra.template.service.KafkaProducerServiceTest"
```

**Controller 层：**
```bash
gradlew.bat test --tests "com.infra.template.controller.CacheControllerTest"
gradlew.bat test --tests "com.infra.template.controller.SearchControllerTest"
gradlew.bat test --tests "com.infra.template.controller.StorageControllerTest"
gradlew.bat test --tests "com.infra.template.controller.EventControllerTest"
gradlew.bat test --tests "com.infra.template.controller.GlobalExceptionHandlerTest"
```

### 3. 集成测试（需先启动 Docker）

```bash
make up
gradlew.bat test --tests "com.infra.template.integration.InfrastructureIntegrationTest"
gradlew.bat test --tests "com.infra.template.controller.UserControllerTest"
gradlew.bat test --tests "com.infra.template.ApplicationTests"
```

### 4. 一键运行全部测试

```bash
gradlew.bat test
```