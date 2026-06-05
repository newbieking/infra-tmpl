package com.infra.storage.service;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private StorageService storageService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(storageService, "bucket", "test-bucket");
    }

    @Test
    void uploadShouldReturnObjectName() throws Exception {
        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(null);

        String result = storageService.upload("test.txt",
                new ByteArrayInputStream("content".getBytes()), 7, "text/plain");

        assertEquals("test.txt", result);
    }

    @Test
    void downloadShouldReturnStream() throws Exception {
        GetObjectResponse response = new GetObjectResponse(
                new okhttp3.Headers.Builder().build(), "test-bucket", "", "test.txt",
                new ByteArrayInputStream("content".getBytes()));
        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(response);

        InputStream result = storageService.download("test.txt");
        assertNotNull(result);
    }

    @Test
    void uploadShouldPropagateException() throws Exception {
        when(minioClient.putObject(any(PutObjectArgs.class)))
                .thenThrow(new RuntimeException("MinIO down"));

        assertThrows(RuntimeException.class,
                () -> storageService.upload("x", new ByteArrayInputStream(new byte[0]), 0, "text/plain"));
    }
}
