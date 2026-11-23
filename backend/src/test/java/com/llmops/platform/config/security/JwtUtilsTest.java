package com.llmops.platform.config.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 86400000L);
        ReflectionTestUtils.setField(jwtUtils, "refreshExpirationMs", 604800000L);
    }

    @Test
    @DisplayName("Should successfully generate and validate valid JWT token")
    void testGenerateAndValidateToken() {
        UUID userId = UUID.randomUUID();
        String email = "engineer@aetherllm.ai";

        String token = jwtUtils.generateTokenFromUserId(userId, email, 86400000L);
        assertNotNull(token);
        assertTrue(jwtUtils.validateToken(token));

        assertEquals(userId, jwtUtils.getUserIdFromJwt(token));
    }

    @Test
    @DisplayName("Should reject malformed or tampered JWT token")
    void testRejectInvalidToken() {
        assertFalse(jwtUtils.validateToken("invalid.jwt.token.string"));
    }
}
