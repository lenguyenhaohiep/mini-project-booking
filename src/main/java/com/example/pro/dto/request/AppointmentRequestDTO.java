package com.example.pro.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

/**
 * Request payload for creating an appointment.
 *
 * @param patientId      the ID of the patient
 * @param practitionerId the ID of the practitioner
 * @param startDate      the appointment start date and time
 * @param endDate        the appointment end date and time
 */
public record AppointmentRequestDTO(
    @NotNull @Positive
    Integer patientId,

    @NotNull @Positive
    Integer practitionerId,

    @NotNull
    Instant startDate,

    @NotNull
    Instant endDate
) {
}
