package com.example.pro.model;

import com.example.pro.exception.TimeRangeInvalidException;

import java.time.Instant;

/**
 * Immutable request for creating an appointment.
 * The compact constructor validates that IDs are positive, dates are non-null,
 * and {@code startDate} is strictly before {@code endDate}.
 *
 * @param patientId      the patient's ID (must be >= 1)
 * @param practitionerId the practitioner's ID (must be >= 1)
 * @param startDate      the inclusive start of the appointment
 * @param endDate        the exclusive end of the appointment
 */
public record AppointmentRequest(
    int patientId,
    int practitionerId,
    Instant startDate,
    Instant endDate
) {
    /**
     * Validates request fields.
     *
     * @throws IllegalArgumentException if an ID is negative or a date is null
     * @throws TimeRangeInvalidException          if {@code startDate} is not before {@code endDate}
     */
    public AppointmentRequest {
        if (practitionerId < 1) {
            throw new IllegalArgumentException("practitionerId should be positive");
        }
        if (patientId < 1) {
            throw new IllegalArgumentException("patientId should be positive");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        if (!startDate.isBefore(endDate)) {
            throw new TimeRangeInvalidException("startDate must be before endDate");
        }
    }
}
