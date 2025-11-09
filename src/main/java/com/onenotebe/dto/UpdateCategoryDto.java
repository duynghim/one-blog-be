package com.onenotebe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for updating a Category. Changing the name will regenerate the slug.
 */
@Schema(name = "UpdateCategoryRequest", description = "Request payload to update an existing category")
public record UpdateCategoryDto(
        @NotBlank
        @Schema(description = "Updated category name", example = "Java Programming") String name
) {}