package com.example.pro.exception;

public class PractitionerNotFoundException extends DomainException {
    public PractitionerNotFoundException(String message) {
        super(message);
    }
}
