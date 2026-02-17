package com.example.pro.model;

import com.example.pro.exception.TimeRangeInvalidException;
import com.example.pro.utils.Validator;

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
        Validator.validateValidId(patientId, "patientId");
        Validator.validateValidId(practitionerId, "practitionerId");
        Validator.validateValidRange(startDate, endDate, "startDate", "endDate");
    }
}
