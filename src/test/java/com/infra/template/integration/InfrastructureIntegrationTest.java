package com.infra.template.integration;

import com.infra.template.dto.UserDto;
import com.infra.template.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InfrastructureIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("test").withUsername("test").withPassword("test");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @Container
    static ElasticsearchContainer elasticsearch = new ElasticsearchContainer(
            DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:8.15.3")
                    .asCompatibleSubstituteFor("docker.elastic.co/elasticsearch/elasticsearch"))
            .withEnv("xpack.security.enabled", "false");

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));

    @Container
    static GenericContainer<?> minio = new GenericContainer<>(DockerImageName.parse("minio/minio:latest"))
            .withExposedPorts(9000).withCommand("server", "/data")
            .withEnv("MINIO_ROOT_USER", "minioadmin").withEnv("MINIO_ROOT_PASSWORD", "minioadmin123");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
        registry.add("spring.elasticsearch.uris",
                () -> "http://" + elasticsearch.getHost() + ":" + elasticsearch.getMappedPort(9200));
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("infra.minio.endpoint",
                () -> "http://" + minio.getHost() + ":" + minio.getMappedPort(9000));
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    // ==================== Health ====================

    @Test
    @Order(1)
    void healthEndpointShouldRespond() throws Exception {
        mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }

    // ==================== User CRUD (PostgreSQL) ====================

    @Test
    @Order(10)
    void userCreateShouldReturn201() throws Exception {
        String body = objectMapper.writeValueAsString(
                new UserDto(null, "alice", "alice@example.com", null, null));
        mockMvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    @Order(11)
    void userListShouldReturnUsers() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].username").value("alice"));
    }

    @Test
    @Order(12)
    void userGetByIdShouldReturnUser() throws Exception {
        Long id = userRepository.findAll().get(0).getId();
        mockMvc.perform(get("/api/v1/users/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"));
    }

    @Test
    @Order(13)
    void userUpdateShouldModifyFields() throws Exception {
        Long id = userRepository.findAll().get(0).getId();
        String body = objectMapper.writeValueAsString(
                new UserDto(null, "alice-updated", "alice-new@example.com", null, null));
        mockMvc.perform(put("/api/v1/users/" + id).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice-updated"))
                .andExpect(jsonPath("$.email").value("alice-new@example.com"));
    }

    @Test
    @Order(14)
    void userDuplicateUsernameShouldReturn400() throws Exception {
        String body = objectMapper.writeValueAsString(
                new UserDto(null, "alice-updated", "bob@example.com", null, null));
        mockMvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(15)
    void userDeleteShouldRemoveUser() throws Exception {
        Long id = userRepository.findAll().get(0).getId();
        mockMvc.perform(delete("/api/v1/users/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/v1/users/" + id)).andExpect(status().isBadRequest());
    }

    // ==================== Cache (Redis) ====================

    @Test
    @Order(20)
    void cachePutShouldStoreValue() throws Exception {
        mockMvc.perform(put("/api/v1/cache").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"key\":\"greeting\",\"value\":\"hello\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.key").value("greeting"));
    }

    @Test
    @Order(21)
    void cacheGetShouldRetrieveValue() throws Exception {
        mockMvc.perform(get("/api/v1/cache/greeting"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.value").value("hello"));
    }

    @Test
    @Order(22)
    void cacheGetNonExistentShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/cache/nonexistent")).andExpect(status().isNotFound());
    }

    @Test
    @Order(23)
    void cacheDeleteShouldRemoveKey() throws Exception {
        mockMvc.perform(delete("/api/v1/cache/greeting"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.deleted").value(true));
        mockMvc.perform(get("/api/v1/cache/greeting")).andExpect(status().isNotFound());
    }

    // ==================== Events (Kafka) ====================

    @Test
    @Order(30)
    void eventPublishShouldSucceed() throws Exception {
        mockMvc.perform(post("/api/v1/events").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"topic\":\"infra-events\",\"payload\":\"test-message\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("published"));
    }

    @Test
    @Order(31)
    void eventConsumedShouldReturnMessages() throws Exception {
        Thread.sleep(2000);
        mockMvc.perform(get("/api/v1/events/consumed"))
                .andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
    }

    // ==================== Storage (MinIO) ====================

    @Test
    @Order(40)
    void storageUploadShouldSucceed() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "hello minio".getBytes());
        mockMvc.perform(multipart("/api/v1/storage").file(file))
                .andExpect(status().isOk()).andExpect(jsonPath("$.objectName").value("test.txt"));
    }

    @Test
    @Order(41)
    void storageDownloadShouldReturnContent() throws Exception {
        mockMvc.perform(get("/api/v1/storage/test.txt"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"test.txt\""));
    }

    // ==================== Search (Elasticsearch) ====================

    @Test
    @Order(50)
    void searchIndexShouldReturnId() throws Exception {
        mockMvc.perform(post("/api/v1/search/index/test-docs").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Elasticsearch test\",\"body\":\"full text search\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @Order(51)
    void searchQueryShouldReturnResults() throws Exception {
        Thread.sleep(2000);
        mockMvc.perform(post("/api/v1/search").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\":\"Elasticsearch\",\"index\":\"test-docs\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
    }
}
