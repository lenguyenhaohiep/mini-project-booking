package com.example.pro.model;

import com.example.pro.utils.Validator;

/**
 * Immutable request for creating an appointment.
 * The compact constructor validates that IDs are positive.
 * Time range validation is handled by {@link TimeRange}.
 *
 * @param patientId      the patient's ID (must be >= 1)
 * @param practitionerId the practitioner's ID (must be >= 1)
 * @param timeRange      the appointment time range
 */
public record AppointmentRequest(
    int patientId,
    int practitionerId,
    TimeRange timeRange
) {
    public AppointmentRequest {
        Validator.validateValidId(patientId, "patientId");
        Validator.validateValidId(practitionerId, "practitionerId");
    }
}
