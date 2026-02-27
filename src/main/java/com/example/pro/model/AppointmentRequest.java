package com.example.pro.model;

import com.example.pro.exception.TimeRangeInvalidException;
import com.example.pro.utils.Validator;

import java.time.Instant;

/**
 * Immutable request for creating an appointment.
 * The compact constructor validates that IDs are non-blank, dates are non-null,
 * and {@code startDate} is strictly before {@code endDate}.
 *
 * @param patientId      the patient's ID
 * @param practitionerId the practitioner's ID
 * @param startDate      the inclusive start of the appointment
 * @param endDate        the exclusive end of the appointment
 */
public record AppointmentRequest(
    String patientId,
    String practitionerId,
    Instant startDate,
    Instant endDate
) {
    /**
     * Validates request fields.
     *
     * @throws IllegalArgumentException if an ID is blank or a date is null
     * @throws TimeRangeInvalidException          if {@code startDate} is not before {@code endDate}
     */
    public AppointmentRequest {
        Validator.validateNotBlank(patientId, "patientId");
        Validator.validateNotBlank(practitionerId, "practitionerId");
        Validator.validateValidRange(startDate, endDate, "startDate", "endDate");
    }
}
