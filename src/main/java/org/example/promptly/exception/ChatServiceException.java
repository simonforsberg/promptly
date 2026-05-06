package org.example.promptly.exception;

public class ChatServiceException extends RuntimeException {
    public ChatServiceException(String message) {
        super(message);
    }
}
