package com.infra.auth.controller;

import com.infra.auth.entity.SysUser;
import com.infra.auth.service.AuthService;
import com.infra.auth.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "认证服务（JWT + BCrypt）")
public class AuthController {

    private final JwtService jwtService;
    private final AuthService authService;

    public AuthController(JwtService jwtService, AuthService authService) {
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "数据库验证凭据，BCrypt 校验密码，签发 JWT Token")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        try {
            SysUser user = authService.authenticate(username, password);
            String token = jwtService.generateToken(
                    String.valueOf(user.getId()),
                    Map.of("username", user.getUsername(), "role", user.getRole()));
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "tokenType", "Bearer",
                    "expiresIn", 86400,
                    "userId", user.getId(),
                    "role", user.getRole()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "BCrypt 加密密码后存入数据库")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        try {
            SysUser user = authService.register(body.get("username"), body.get("password"),
                    body.getOrDefault("role", "USER"));
            return ResponseEntity.status(201).body(Map.of(
                    "userId", user.getId(),
                    "username", user.getUsername(),
                    "role", user.getRole()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/change-password")
    @Operation(summary = "修改密码", description = "验证旧密码后更新为新密码")
    public ResponseEntity<Map<String, Object>> changePassword(
            @RequestHeader("X-User-Token") String token,
            @RequestBody Map<String, String> body) {
        try {
            String userId = jwtService.parseUserId(token);
            authService.changePassword(Long.parseLong(userId), body.get("oldPassword"), body.get("newPassword"));
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/verify")
    @Operation(summary = "验证 Token", description = "校验 JWT Token 有效性并返回用户信息")
    public ResponseEntity<Map<String, Object>> verify(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

        if (!jwtService.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
        }

        Map<String, Object> claims = jwtService.parseClaims(token);
        return ResponseEntity.ok(Map.of("userId", claims.get("sub"), "valid", true, "claims", claims));
    }
}
