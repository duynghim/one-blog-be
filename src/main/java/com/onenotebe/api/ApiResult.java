package com.onenotebe.api;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Standardized API response wrapper to ensure consistent, secure payloads.
 * Controllers must always return this wrapper instead of raw DTOs.
 */
@Schema(description = "Standard API response wrapper containing either data or error")
public record ApiResult<T>(
        @Schema(description = "Indicates if the request was successful", example = "true") boolean success,
        @Schema(description = "Successful response payload") T data,
        @Schema(description = "Error information when success=false") ApiError error) {

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(true, data, null);
    }

    public static <T> ApiResult<T> error(ApiError error) {
        return new ApiResult<>(false, null, error);
    }

}