package com.onenotebe.exception;

/**
 * Unchecked exception thrown when a username already exists.
 */
public class DuplicateUsernameException extends RuntimeException {
    public DuplicateUsernameException(String message) {
        super(message);
    }
}