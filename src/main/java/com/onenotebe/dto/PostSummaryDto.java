package com.onenotebe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Summary view of a blog post")
public record PostSummaryDto(
        @Schema(description = "Post ID", example = "1") Long id,
        @Schema(description = "Post title", example = "My First Post") String title,
        @Schema(description = "SEO-friendly slug", example = "my-first-post") String slug,
        @Schema(description = "Creation timestamp") Instant createdAt,
        @Schema(description = "Featured image URL", example = "https://cdn.example.com/img.png") String featuredImageUrl
) {}