package com.onenotebe.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LoginResponse", description = "JWT token response")
public record LoginResponse(
        @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiJ9...") String token
) { }