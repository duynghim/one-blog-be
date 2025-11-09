package com.onenotebe.controller;

import com.onenotebe.api.ApiResult;
import com.onenotebe.dto.PostDetailDto;
import com.onenotebe.dto.PostSummaryDto;
import com.onenotebe.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Posts", description = "Public blog post endpoints")
public class PostController {

    private final PostService postService;

    @Operation(
            summary = "List posts",
            description = "Returns a list of post summaries",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful response",
                            content = @Content(schema = @Schema(implementation = ApiResult.class))
                    )
            }
    )
    @PreAuthorize("isAnonymous() or hasAnyRole('USER','ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResult<List<PostSummaryDto>>> listPosts(
            @RequestParam(value = "page", required = false) @Min(0) Integer page,
            @RequestParam(value = "size", required = false) @Min(1) @Max(100) Integer size
    ) {
        log.debug("Listing posts endpoint called [page={}, size={}]", page, size);
        var summaries = postService.listAll(page, size);
        return ResponseEntity.ok(ApiResult.success(summaries));
    }

    @Operation(
            summary = "Get post by slug",
            description = "Returns detailed post by slug",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful response",
                            content = @Content(schema = @Schema(implementation = ApiResult.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Post not found",
                            content = @Content(schema = @Schema(implementation = ApiResult.class))
                    )
            }
    )
    @PreAuthorize("isAnonymous() or hasAnyRole('USER','ADMIN')")
    @GetMapping("/{slug}")
    public ResponseEntity<ApiResult<PostDetailDto>> getBySlug(@PathVariable String slug) {
        log.debug("Get post by slug [slug={}]", slug);
        var detail = postService.getBySlug(slug);
        var etag = buildETag(detail);
        return ResponseEntity.ok()
                .eTag(etag)
                .body(ApiResult.success(detail));
    }

    private String buildETag(PostDetailDto detail) {
        // Weak ETag based on id and updatedAt to assist caching
        var updated = detail.updatedAt() != null ? detail.updatedAt().toEpochMilli() : 0L;
        return "W/\"" + detail.id() + "-" + updated + "\"";
    }
}
