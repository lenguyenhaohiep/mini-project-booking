package com.example.pro.exception;

public class AppointmentOverlapExistedException extends DomainException {
    public AppointmentOverlapExistedException(String message) {
        super(message);
    }
}
