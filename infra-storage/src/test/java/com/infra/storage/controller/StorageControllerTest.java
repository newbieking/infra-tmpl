package com.infra.storage.controller;

import com.infra.storage.service.StorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StorageController.class)
@ActiveProfiles("test")
class StorageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StorageService storageService;

    @Test
    void uploadShouldReturnObjectName() throws Exception {
        when(storageService.upload(anyString(), any(InputStream.class), anyLong(), anyString()))
                .thenReturn("test.txt");

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "hello".getBytes());
        mockMvc.perform(multipart("/api/v1/storage").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.objectName").value("test.txt"));
    }

    @Test
    void downloadShouldReturnContent() throws Exception {
        when(storageService.download("test.txt")).thenReturn(new ByteArrayInputStream("hello".getBytes()));

        mockMvc.perform(get("/api/v1/storage/test.txt"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"test.txt\""));
    }
}
