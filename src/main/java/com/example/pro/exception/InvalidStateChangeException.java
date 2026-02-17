package com.example.pro.exception;

public class InvalidStateChangeException extends RuntimeException {
    public InvalidStateChangeException(String message) {
        super(message);
    }
}
