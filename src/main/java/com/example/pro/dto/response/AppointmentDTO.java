package com.example.pro.dto.response;

import java.time.LocalDateTime;

/**
 * Response DTO representing an appointment.
 *
 * @param id             the appointment ID
 * @param patientId      the ID of the patient
 * @param practitionerId the ID of the practitioner
 * @param startDate      the appointment start date and time
 * @param endDate        the appointment end date and time
 */
public record AppointmentDTO(
    Long id,
    Integer patientId,
    Integer practitionerId,
    LocalDateTime startDate,
    LocalDateTime endDate
) {
}