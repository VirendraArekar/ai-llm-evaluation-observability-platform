package com.llmops.platform.config.security;

import com.llmops.platform.modules.auth.entity.ApiKey;
import com.llmops.platform.modules.auth.repository.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyAuthenticationFilter(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String apiKeyHeader = request.getHeader("X-API-Key");
        if (!StringUtils.hasText(apiKeyHeader)) {
            String authHeader = request.getHeader("Authorization");
            if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ak_")) {
                apiKeyHeader = authHeader.substring(7);
            }
        }

        if (StringUtils.hasText(apiKeyHeader) && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String hashedKey = hashKey(apiKeyHeader);
                Optional<ApiKey> apiKeyOpt = apiKeyRepository.findByKeyHash(hashedKey);
                if (apiKeyOpt.isPresent()) {
                    ApiKey apiKey = apiKeyOpt.get();
                    if (apiKey.isActive() && (apiKey.getExpiresAt() == null || apiKey.getExpiresAt().isAfter(Instant.now()))) {
                        apiKey.setLastUsedAt(Instant.now());
                        apiKeyRepository.save(apiKey);

                        List<SimpleGrantedAuthority> authorities = Arrays.stream(apiKey.getScopes().split(","))
                                .map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope.trim()))
                                .collect(Collectors.toList());
                        authorities.add(new SimpleGrantedAuthority("ROLE_API_KEY"));

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                apiKey, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                logger.error("API key validation failed: {}", e);
            }
        }

        filterChain.doFilter(request, response);
    }

    public static String hashKey(String rawKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(rawKey.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(encodedhash);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing API key", e);
        }
    }
}
