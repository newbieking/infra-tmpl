package com.infra.storage.controller;

import com.infra.storage.service.StorageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/storage")
@Tag(name = "Storage", description = "存储服务（MinIO）")
public class StorageController {

    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws Exception {
        String objectName = storageService.upload(
                file.getOriginalFilename(), file.getInputStream(), file.getSize(), file.getContentType());
        return ResponseEntity.ok(Map.of("objectName", objectName, "size", String.valueOf(file.getSize())));
    }

    @GetMapping("/{objectName}")
    public ResponseEntity<InputStreamResource> download(@PathVariable String objectName) throws Exception {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + objectName + "\"")
                .body(new InputStreamResource(storageService.download(objectName)));
    }
}
