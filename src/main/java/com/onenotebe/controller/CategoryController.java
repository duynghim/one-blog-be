package com.onenotebe.controller;

import com.onenotebe.api.ApiResult;
import com.onenotebe.dto.CategoryDto;
import com.onenotebe.dto.CreateCategoryDto;
import com.onenotebe.dto.UpdateCategoryDto;
import com.onenotebe.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for Category management.
 */
@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Categories", description = "Manage blog categories")
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "List categories", description = "Public endpoint to fetch paginated list of categories")
    @ApiResponse(responseCode = "200", description = "List fetched", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @GetMapping
    @PreAuthorize("isAnonymous() or hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResult<List<CategoryDto>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.debug("Listing categories via API [page={}, size={}]", page, size);
        var data = categoryService.findAll(page, size);
        var cacheControl = CacheControl.maxAge(java.time.Duration.ofSeconds(60)).cachePublic();
        var headers = new HttpHeaders();
        headers.setCacheControl(cacheControl.toString());
        return ResponseEntity.ok().headers(headers).body(ApiResult.success(data));
    }

    @Operation(summary = "Create category", description = "Admin-only endpoint to create a category")
    @ApiResponse(responseCode = "201", description = "Category created", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResult<CategoryDto>> create(@Valid @RequestBody CreateCategoryDto dto) {
        var created = categoryService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResult.success(created));
    }

    @Operation(summary = "Update category", description = "Admin-only endpoint to update a category by ID")
    @ApiResponse(responseCode = "200", description = "Category updated", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResult<CategoryDto>> update(@PathVariable Long id, @Valid @RequestBody UpdateCategoryDto dto) {
        var updated = categoryService.update(id, dto);
        return ResponseEntity.ok(ApiResult.success(updated));
    }

    @Operation(summary = "Delete category", description = "Admin-only endpoint to delete a category by ID")
    @ApiResponse(responseCode = "204", description = "Category deleted")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get category by slug", description = "Public endpoint to fetch a category by slug")
    @ApiResponse(responseCode = "200", description = "Category fetched", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @GetMapping("/{slug}")
    @PreAuthorize("isAnonymous() or hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResult<CategoryDto>> getBySlug(@PathVariable String slug) {
        log.debug("Fetching category by slug via API [slug={}]", slug);
        var category = categoryService.findBySlug(slug);
        var headers = new HttpHeaders();
        if (category != null) {
            // Weak ETag based on id and name for cache validation
            var etag = "W/\"" + category.id() + ":" + category.name().hashCode() + "\"";
            headers.setETag(etag);
        }
        return ResponseEntity.ok().headers(headers).body(ApiResult.success(category));
    }
}