package com.infra.auth.controller;

import com.infra.auth.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "认证服务（JWT）")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "验证凭据并签发 JWT Token")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        // TODO: 实际场景应查询数据库验证
        if (!"admin".equals(username) || !"admin123".equals(password)) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }

        String token = jwtService.generateToken("1", Map.of("username", username, "role", "ADMIN"));
        return ResponseEntity.ok(Map.of(
                "token", token,
                "tokenType", "Bearer",
                "expiresIn", 86400
        ));
    }

    @GetMapping("/verify")
    @Operation(summary = "验证 Token", description = "校验 JWT Token 有效性并返回用户信息")
    public ResponseEntity<Map<String, Object>> verify(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

        if (!jwtService.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
        }

        String userId = jwtService.parseUserId(token);
        return ResponseEntity.ok(Map.of("userId", userId, "valid", true));
    }
}
