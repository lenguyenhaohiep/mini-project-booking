package com.example.pro.exception;

public class AppointmentOverlapExisted extends DomainException {
    public AppointmentOverlapExisted(String message) {
        super(message);
    }
}
