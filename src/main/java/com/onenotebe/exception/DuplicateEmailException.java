package com.onenotebe.exception;

/**
 * Unchecked exception thrown when an email already exists.
 */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}