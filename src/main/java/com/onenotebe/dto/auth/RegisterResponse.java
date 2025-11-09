package com.onenotebe.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "RegisterResponse", description = "Successful registration response")
public record RegisterResponse(
        @Schema(description = "User ID", example = "42") Long id,
        @Schema(description = "Username", example = "newuser") String username,
        @Schema(description = "Email", example = "user@example.com") String email
) { }