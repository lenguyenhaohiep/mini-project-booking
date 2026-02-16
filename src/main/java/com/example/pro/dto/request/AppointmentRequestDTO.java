package com.example.pro.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

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

    @NotNull @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime startDate,

    @NotNull @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime endDate
) {
}