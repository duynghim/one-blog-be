package com.onenotebe.exception;

import com.onenotebe.api.ApiError;
import com.onenotebe.api.ApiResult;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<Void>> handleValidation(MethodArgumentNotValidException ex) {
        var message = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining(", "));
        log.warn("Validation failed: {}", message);
        var error = new ApiError("BAD_REQUEST", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResult.error(error));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResult<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        var message = ex.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.joining(", "));
        log.warn("Constraint violation: {}", message);
        var error = new ApiError("BAD_REQUEST", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResult.error(error));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResult<Void>> handleNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        var error = new ApiError("NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResult.error(error));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResult<Void>> handleAuth(AuthenticationException ex) {
        log.warn("Authentication failure: {}", ex.getMessage());
        var error = new ApiError("UNAUTHORIZED", "Authentication required or failed");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResult.error(error));
    }

    @ExceptionHandler({DuplicateUsernameException.class, DuplicateEmailException.class})
    public ResponseEntity<ApiResult<Void>> handleDuplicate(RuntimeException ex) {
        log.warn("Duplicate resource: {}", ex.getMessage());
        var error = new ApiError("CONFLICT", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResult.error(error));
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ApiResult<Void>> handleRateLimit(RateLimitExceededException ex) {
        log.warn("Rate limit exceeded: {}", ex.getMessage());
        var error = new ApiError("TOO_MANY_REQUESTS", ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(ApiResult.error(error));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResult<Void>> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        var error = new ApiError("FORBIDDEN", "You do not have permission to perform this action");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResult.error(error));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<Void>> handleFallback(Exception ex) {
        log.error("Unhandled exception occurred", ex);
        var error = new ApiError("INTERNAL_ERROR", "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResult.error(error));
    }

    private String formatFieldError(FieldError fe) {
        return fe.getField() + ": " + fe.getDefaultMessage();
    }
}