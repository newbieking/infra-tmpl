package com.infra.auth.service;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService("TestSecretKeyForUnitTestOnly0123456789", 86400000);

    @Test
    void generateAndParseToken() {
        String token = jwtService.generateToken("user1", Map.of("role", "ADMIN"));
        assertNotNull(token);

        String userId = jwtService.parseUserId(token);
        assertEquals("user1", userId);
    }

    @Test
    void validateValidTokenShouldReturnTrue() {
        String token = jwtService.generateToken("user1", Map.of());
        assertTrue(jwtService.validateToken(token));
    }

    @Test
    void validateInvalidTokenShouldReturnFalse() {
        assertFalse(jwtService.validateToken("invalid.token.here"));
    }

    @Test
    void validateTamperedTokenShouldReturnFalse() {
        String token = jwtService.generateToken("user1", Map.of());
        assertFalse(jwtService.validateToken(token + "tampered"));
    }
}
