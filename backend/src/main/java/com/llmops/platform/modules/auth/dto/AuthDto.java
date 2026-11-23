package com.llmops.platform.modules.auth.dto;

import com.llmops.platform.modules.auth.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public class AuthDto {

    public static class LoginRequest {
        @NotBlank @Email
        private String email;
        @NotBlank
        private String password;

        public LoginRequest() {}
        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        @NotBlank @Email
        private String email;
        @NotBlank @Size(min = 6)
        private String password;
        @NotBlank
        private String fullName;
        private String organizationName;

        public RegisterRequest() {}

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getOrganizationName() { return organizationName; }
        public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
    }

    public static class AuthResponse {
        private String accessToken;
        private String refreshToken;
        private String tokenType = "Bearer";
        private UserDto user;
        private UUID defaultOrganizationId;
        private UUID defaultWorkspaceId;
        private UUID defaultProjectId;

        public AuthResponse() {}

        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
        public String getTokenType() { return tokenType; }
        public void setTokenType(String tokenType) { this.tokenType = tokenType; }
        public UserDto getUser() { return user; }
        public void setUser(UserDto user) { this.user = user; }
        public UUID getDefaultOrganizationId() { return defaultOrganizationId; }
        public void setDefaultOrganizationId(UUID defaultOrganizationId) { this.defaultOrganizationId = defaultOrganizationId; }
        public UUID getDefaultWorkspaceId() { return defaultWorkspaceId; }
        public void setDefaultWorkspaceId(UUID defaultWorkspaceId) { this.defaultWorkspaceId = defaultWorkspaceId; }
        public UUID getDefaultProjectId() { return defaultProjectId; }
        public void setDefaultProjectId(UUID defaultProjectId) { this.defaultProjectId = defaultProjectId; }
    }

    public static class RefreshTokenRequest {
        @NotBlank
        private String refreshToken;

        public RefreshTokenRequest() {}
        public RefreshTokenRequest(String refreshToken) { this.refreshToken = refreshToken; }
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }

    public static class UserDto {
        private UUID id;
        private String email;
        private String fullName;
        private String avatarUrl;
        private Role role;
        private boolean active;
        private Instant createdAt;

        public UserDto() {}

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
        public Role getRole() { return role; }
        public void setRole(Role role) { this.role = role; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    }

    public static class OrganizationDto {
        private UUID id;
        private String name;
        private String slug;
        private String plan;
        private Instant createdAt;

        public OrganizationDto() {}

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSlug() { return slug; }
        public void setSlug(String slug) { this.slug = slug; }
        public String getPlan() { return plan; }
        public void setPlan(String plan) { this.plan = plan; }
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    }

    public static class WorkspaceDto {
        private UUID id;
        private UUID organizationId;
        private String name;
        private String slug;
        private String description;
        private Instant createdAt;

        public WorkspaceDto() {}

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public UUID getOrganizationId() { return organizationId; }
        public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSlug() { return slug; }
        public void setSlug(String slug) { this.slug = slug; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    }

    public static class ProjectDto {
        private UUID id;
        private UUID workspaceId;
        private String name;
        private String slug;
        private String description;
        private Instant createdAt;
        private long totalTraces;
        private long totalEvaluations;

        public ProjectDto() {}

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public UUID getWorkspaceId() { return workspaceId; }
        public void setWorkspaceId(UUID workspaceId) { this.workspaceId = workspaceId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSlug() { return slug; }
        public void setSlug(String slug) { this.slug = slug; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
        public long getTotalTraces() { return totalTraces; }
        public void setTotalTraces(long totalTraces) { this.totalTraces = totalTraces; }
        public long getTotalEvaluations() { return totalEvaluations; }
        public void setTotalEvaluations(long totalEvaluations) { this.totalEvaluations = totalEvaluations; }
    }

    public static class ApiKeyDto {
        private UUID id;
        private UUID projectId;
        private String name;
        private String keyPrefix;
        private String scopes;
        private Instant lastUsedAt;
        private Instant expiresAt;
        private boolean active;
        private Instant createdAt;
        private String rawKey;

        public ApiKeyDto() {}

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public UUID getProjectId() { return projectId; }
        public void setProjectId(UUID projectId) { this.projectId = projectId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getKeyPrefix() { return keyPrefix; }
        public void setKeyPrefix(String keyPrefix) { this.keyPrefix = keyPrefix; }
        public String getScopes() { return scopes; }
        public void setScopes(String scopes) { this.scopes = scopes; }
        public Instant getLastUsedAt() { return lastUsedAt; }
        public void setLastUsedAt(Instant lastUsedAt) { this.lastUsedAt = lastUsedAt; }
        public Instant getExpiresAt() { return expiresAt; }
        public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
        public String getRawKey() { return rawKey; }
        public void setRawKey(String rawKey) { this.rawKey = rawKey; }
    }

    public static class CreateApiKeyRequest {
        @NotBlank
        private String name;
        private String scopes = "traces:write,evaluations:read";
        private Integer expiresInDays;

        public CreateApiKeyRequest() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getScopes() { return scopes; }
        public void setScopes(String scopes) { this.scopes = scopes; }
        public Integer getExpiresInDays() { return expiresInDays; }
        public void setExpiresInDays(Integer expiresInDays) { this.expiresInDays = expiresInDays; }
    }
}
