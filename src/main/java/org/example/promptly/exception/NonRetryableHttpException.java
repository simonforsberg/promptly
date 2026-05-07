package org.example.promptly.exception;

public class NonRetryableHttpException extends RuntimeException {
    public NonRetryableHttpException(String message) {
        super(message);
    }

    public NonRetryableHttpException(String message, Throwable cause) {
        super(message, cause);
    }
}
