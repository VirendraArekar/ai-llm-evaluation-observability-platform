package com.llmops.platform.modules.evaluation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class DeterministicEvalService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean exactMatch(String generated, String expected) {
        if (generated == null && expected == null) return true;
        if (generated == null || expected == null) return false;
        return generated.trim().equalsIgnoreCase(expected.trim());
    }

    public boolean regexMatch(String generated, String regexPattern) {
        if (generated == null || regexPattern == null) return false;
        try {
            return Pattern.compile(regexPattern, Pattern.DOTALL).matcher(generated).find();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isValidJson(String generated) {
        if (generated == null || generated.isBlank()) return false;
        try {
            objectMapper.readTree(generated.trim());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public double tokenOverlapJaccard(String generated, String expected) {
        if (generated == null || expected == null) return 0.0;
        Set<String> genTokens = new HashSet<>(Arrays.asList(generated.toLowerCase().split("\\W+")));
        Set<String> expTokens = new HashSet<>(Arrays.asList(expected.toLowerCase().split("\\W+")));
        genTokens.remove("");
        expTokens.remove("");

        if (genTokens.isEmpty() && expTokens.isEmpty()) return 1.0;
        if (genTokens.isEmpty() || expTokens.isEmpty()) return 0.0;

        Set<String> intersection = new HashSet<>(genTokens);
        intersection.retainAll(expTokens);

        Set<String> union = new HashSet<>(genTokens);
        union.addAll(expTokens);

        return (double) intersection.size() / union.size();
    }
}
