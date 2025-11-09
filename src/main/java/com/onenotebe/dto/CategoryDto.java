package com.onenotebe.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for transferring Category data to clients.
 * Implemented as a Java 21 record for immutability and clarity.
 */
@Schema(name = "Category", description = "Category representation for API responses")
public record CategoryDto(
        @Schema(description = "Category ID", example = "1") Long id,
        @Schema(description = "Category name", example = "Programming") String name,
        @Schema(description = "URL-friendly slug", example = "programming") String slug
) {}