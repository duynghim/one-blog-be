package com.onenotebe.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "LoginRequest", description = "Credentials for authentication")
public record LoginRequest(
        @NotBlank @Schema(description = "Unique username", example = "admin") String username,
        @NotBlank @Schema(description = "User password", example = "P@ssw0rd") String password
) { }