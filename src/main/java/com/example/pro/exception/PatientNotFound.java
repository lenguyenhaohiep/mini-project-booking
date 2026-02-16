package com.example.pro.exception;

public class PatientNotFound extends DomainException {
    public PatientNotFound(String message) {
        super(message);
    }
}
