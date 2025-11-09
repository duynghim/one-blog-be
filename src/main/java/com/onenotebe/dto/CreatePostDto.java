package com.onenotebe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

@Schema(name = "CreatePostDto", description = "Payload to create or update a post")
public record CreatePostDto(
        @NotBlank @Schema(description = "Post title", example = "Introducing Spring Boot 3") String title,
        @NotBlank @Schema(description = "Markdown content", example = "# Hello World") String content,
        @Schema(description = "Featured image URL", example = "https://cdn.example.com/banner.png") String featuredImageUrl,
        @Schema(description = "IDs of categories to attach") Set<Long> categoryIds
) {}