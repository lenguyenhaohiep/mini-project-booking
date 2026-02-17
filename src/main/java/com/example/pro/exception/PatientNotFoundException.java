package com.example.pro.exception;

public class PatientNotFoundException extends DomainException {
    public PatientNotFoundException(String message) {
        super(message);
    }
}
