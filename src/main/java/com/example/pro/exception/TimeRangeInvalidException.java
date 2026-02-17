package com.example.pro.exception;

public class TimeRangeInvalidException extends DomainException {
    public TimeRangeInvalidException(String message) {
        super(message);
    }
}