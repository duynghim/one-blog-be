package com.onenotebe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Detailed view of a blog post")
public record PostDetailDto(
        @Schema(description = "Post ID", example = "1") Long id,
        @Schema(description = "Post title") String title,
        @Schema(description = "SEO-friendly slug") String slug,
        @Schema(description = "Markdown content") String content,
        @Schema(description = "Featured image URL") String featuredImageUrl,
        @Schema(description = "Creation timestamp") Instant createdAt,
        @Schema(description = "Last update timestamp") Instant updatedAt
) {}