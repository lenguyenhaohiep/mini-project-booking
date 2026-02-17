package com.example.pro.model;

/**
 * Lifecycle status of a {@link com.example.pro.entity.TimeSlot}.
 */
public enum TimeSlotStatus {
    /** Newly created, not yet processed for availability generation. */
    NEW,
    /** Updated since last availability generation. */
    MODIFIED,
    /** Availabilities have been generated from this slot. */
    PLANNED
}