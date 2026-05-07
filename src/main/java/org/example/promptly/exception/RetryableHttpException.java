package org.example.promptly.exception;

public class RetryableHttpException extends RuntimeException {
    public RetryableHttpException(String message) {
        super(message);
    }

    public RetryableHttpException(String message, Throwable cause) {
        super(message, cause);
    }
}
