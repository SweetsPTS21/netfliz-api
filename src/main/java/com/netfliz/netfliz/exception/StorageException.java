package com.netfliz.netfliz.exception;

/**
 * Exception thrown when an error occurs during storage operations.
 */
public class StorageException extends RuntimeException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
