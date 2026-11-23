package com.llmops.platform.modules.tracing.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PiiRedactionUtilsTest {

    @Test
    @DisplayName("Should redact emails, credit cards, and sensitive keys from trace payloads")
    void testRedactPii() {
        String input = "User email is john.doe@example.com and card number is 4532-1234-5678-9010 with key sk-abcdef1234567890abcdef1234567890.";

        String redacted = PiiRedactionUtils.redact(input);

        assertFalse(redacted.contains("john.doe@example.com"));
        assertTrue(redacted.contains("[REDACTED_EMAIL]"));

        assertFalse(redacted.contains("4532-1234-5678-9010"));
        assertTrue(redacted.contains("[REDACTED_CREDIT_CARD]"));
    }
}
