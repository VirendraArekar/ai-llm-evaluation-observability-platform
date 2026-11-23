package com.llmops.platform.modules.evaluation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeterministicEvalServiceTest {

    private DeterministicEvalService evalService;

    @BeforeEach
    void setUp() {
        evalService = new DeterministicEvalService();
    }

    @Test
    @DisplayName("Should evaluate exact match accurately")
    void testExactMatch() {
        assertTrue(evalService.exactMatch("Hello World", "hello world"));
        assertTrue(evalService.exactMatch("  Testing 123  ", "Testing 123"));
        assertFalse(evalService.exactMatch("Hello World", "Different string"));
    }

    @Test
    @DisplayName("Should validate valid and invalid JSON schemas")
    void testJsonValidation() {
        String validJson = "{\"status\": \"OK\", \"code\": 200}";
        String invalidJson = "{\"status\": \"OK\", code: 200";

        assertTrue(evalService.isValidJson(validJson));
        assertFalse(evalService.isValidJson(invalidJson));
    }

    @Test
    @DisplayName("Should calculate token overlap Jaccard similarity")
    void testTokenOverlap() {
        String text1 = "The quick brown fox jumps over the lazy dog";
        String text2 = "The fast brown fox leaps over the lazy dog";

        double overlap = evalService.tokenOverlapJaccard(text1, text2);
        assertTrue(overlap > 0.5, "Expected high token overlap");
    }

    @Test
    @DisplayName("Should match regex patterns correctly")
    void testRegexMatch() {
        assertTrue(evalService.regexMatch("Order #12345 confirmed", "\\d{5}"));
        assertFalse(evalService.regexMatch("No digits here", "\\d{5}"));
    }
}
