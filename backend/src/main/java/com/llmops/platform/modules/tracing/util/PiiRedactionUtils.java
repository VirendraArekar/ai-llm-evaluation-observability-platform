package com.llmops.platform.modules.tracing.util;

import java.util.regex.Pattern;

public class PiiRedactionUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
    private static final Pattern CREDIT_CARD_PATTERN = Pattern.compile("\\b(?:\\d{4}[ -]?){3}\\d{4}\\b");
    private static final Pattern SSN_PATTERN = Pattern.compile("\\b\\d{3}-\\d{2}-\\d{4}\\b");
    private static final Pattern API_KEY_PATTERN = Pattern.compile("(?i)(sk-[a-zA-Z0-9]{20,}|bearer\\s+[a-zA-Z0-9._-]{20,}|key-[a-zA-Z0-9]{16,})");

    public static String redact(String input) {
        if (input == null || input.isBlank()) return input;

        String result = input;
        result = EMAIL_PATTERN.matcher(result).replaceAll("[REDACTED_EMAIL]");
        result = CREDIT_CARD_PATTERN.matcher(result).replaceAll("[REDACTED_CREDIT_CARD]");
        result = SSN_PATTERN.matcher(result).replaceAll("[REDACTED_SSN]");
        result = API_KEY_PATTERN.matcher(result).replaceAll("[REDACTED_API_KEY]");
        return result;
    }
}
