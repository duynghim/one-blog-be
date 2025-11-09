package com.onenotebe.exception;

/**
 * Unchecked exception for signaling rate limit violations.
 */
public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String message) {
        super(message);
    }
}