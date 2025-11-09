package com.onenotebe.exception;

/**
 * Unchecked exception for business logic when a resource is not found.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}