package com.onenotebe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for creating a Category. Only name is required; slug is auto-generated.
 */
@Schema(name = "CreateCategoryRequest", description = "Request payload to create a new category")
public record CreateCategoryDto(
        @NotBlank
        @Schema(description = "Category name", example = "Programming") String name
) {}