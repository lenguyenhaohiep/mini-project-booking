package com.example.pro.exception;

public class AvailabilityNotFoundException extends DomainException {
    public AvailabilityNotFoundException(String message) {
        super(message);
    }
}
