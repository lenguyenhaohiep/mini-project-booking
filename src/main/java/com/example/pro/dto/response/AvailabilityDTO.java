package com.example.pro.dto.response;

import java.time.LocalDateTime;

/**
 * Response DTO representing a practitioner's availability slot.
 *
 * @param id             the availability ID
 * @param practitionerId the ID of the practitioner
 * @param startDate      the availability slot start date and time
 * @param endDate        the availability slot end date and time
 */
public record AvailabilityDTO(
    Integer id,
    Integer practitionerId,
    LocalDateTime startDate,
    LocalDateTime endDate
) {
}