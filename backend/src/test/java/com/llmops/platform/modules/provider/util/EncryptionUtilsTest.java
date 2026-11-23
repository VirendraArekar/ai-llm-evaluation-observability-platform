package com.llmops.platform.modules.provider.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionUtilsTest {

    private EncryptionUtils encryptionUtils;

    @BeforeEach
    void setUp() {
        encryptionUtils = new EncryptionUtils("AetherLLMPlatformSecretKeyEncryption2026MasterKey!");
    }

    @Test
    @DisplayName("Should encrypt and decrypt sensitive LLM API keys using AES-256 GCM")
    void testEncryptDecryptApiKey() {
        String originalApiKey = "sk-proj-testApiKeySecret1234567890abcdef";

        EncryptionUtils.EncryptedPayload payload = encryptionUtils.encrypt(originalApiKey);
        assertNotNull(payload);
        assertNotNull(payload.getCipherText());
        assertNotNull(payload.getIv());
        assertNotEquals(originalApiKey, payload.getCipherText());

        String decrypted = encryptionUtils.decrypt(payload.getCipherText(), payload.getIv());
        assertEquals(originalApiKey, decrypted);
    }
}
