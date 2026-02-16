package com.example.pro.model;

/**
 * Status of an {@link com.example.pro.entity.Availability} slot.
 */
public enum AvailabilityStatus {
    /** The slot is open and can be booked. */
    FREE,
    /** The slot has been reserved by an appointment. */
    UNAVAILABLE
}