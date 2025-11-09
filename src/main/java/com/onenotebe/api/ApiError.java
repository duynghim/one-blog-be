package com.onenotebe.api;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error payload with code and message")
public record ApiError(
        @Schema(description = "Machine-readable error code", example = "NOT_FOUND") String code,
        @Schema(description = "Human-readable error message", example = "Post not found") String message
) {}