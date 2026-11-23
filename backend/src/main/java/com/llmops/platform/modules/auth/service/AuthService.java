package com.llmops.platform.modules.auth.service;

import com.llmops.platform.common.exception.BadRequestException;
import com.llmops.platform.common.exception.ResourceNotFoundException;
import com.llmops.platform.config.security.JwtUtils;
import com.llmops.platform.config.security.UserPrincipal;
import com.llmops.platform.modules.auth.dto.AuthDto.*;
import com.llmops.platform.modules.auth.entity.*;
import com.llmops.platform.modules.auth.repository.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final WorkspaceRepository workspaceRepository;
    private final ProjectRepository projectRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       OrganizationRepository organizationRepository,
                       WorkspaceRepository workspaceRepository,
                       ProjectRepository projectRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.workspaceRepository = workspaceRepository;
        this.projectRepository = projectRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered: " + request.getEmail());
        }

        User user = new User(request.getEmail(), passwordEncoder.encode(request.getPassword()), request.getFullName(), Role.ORG_ADMIN);
        user = userRepository.save(user);

        String orgName = request.getOrganizationName() != null && !request.getOrganizationName().isBlank()
                ? request.getOrganizationName() : request.getFullName() + "'s Organization";
        String orgSlug = orgName.toLowerCase().replaceAll("[^a-z0-9]", "-") + "-" + UUID.randomUUID().toString().substring(0, 5);

        Organization org = new Organization(orgName, orgSlug, "ENTERPRISE");
        org = organizationRepository.save(org);

        Workspace workspace = new Workspace(org, "Production", "production", "Production environment for LLM deployments");
        workspace = workspaceRepository.save(workspace);

        Project project = new Project(workspace, "Default Project", "default-project", "Default project for evaluations & traces");
        project = projectRepository.save(project);

        String accessToken = jwtUtils.generateTokenFromUserId(user.getId(), user.getEmail(), 86400000L);
        String refreshToken = jwtUtils.generateTokenFromUserId(user.getId(), user.getEmail(), 604800000L);

        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setUser(mapToUserDto(user));
        response.setDefaultOrganizationId(org.getId());
        response.setDefaultWorkspaceId(workspace.getId());
        response.setDefaultProjectId(project.getId());
        return response;
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String accessToken = jwtUtils.generateAccessToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(authentication);

        Organization org = organizationRepository.findAll().stream().findFirst().orElse(null);
        Workspace ws = org != null ? workspaceRepository.findByOrganizationId(org.getId()).stream().findFirst().orElse(null) : null;
        Project proj = ws != null ? projectRepository.findByWorkspaceId(ws.getId()).stream().findFirst().orElse(null) : null;

        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setUser(mapToUserDto(user));
        response.setDefaultOrganizationId(org != null ? org.getId() : null);
        response.setDefaultWorkspaceId(ws != null ? ws.getId() : null);
        response.setDefaultProjectId(proj != null ? proj.getId() : null);
        return response;
    }

    @Transactional(readOnly = true)
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        if (!jwtUtils.validateToken(request.getRefreshToken())) {
            throw new BadRequestException("Invalid or expired refresh token");
        }
        UUID userId = jwtUtils.getUserIdFromJwt(request.getRefreshToken());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String newAccessToken = jwtUtils.generateTokenFromUserId(user.getId(), user.getEmail(), 86400000L);
        String newRefreshToken = jwtUtils.generateTokenFromUserId(user.getId(), user.getEmail(), 604800000L);

        AuthResponse response = new AuthResponse();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);
        response.setUser(mapToUserDto(user));
        return response;
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return mapToUserDto(user);
    }

    private UserDto mapToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setRole(user.getRole());
        dto.setActive(user.isActive());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
