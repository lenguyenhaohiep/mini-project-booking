package com.example.pro.dto.response;

import java.time.Instant;

/**
 * Response DTO representing a practitioner's availability slot.
 *
 * @param id             the availability ID
 * @param practitionerId the ID of the practitioner
 * @param startDate      the availability slot start date and time
 * @param endDate        the availability slot end date and time
 */
public record AvailabilityDTO(
    Long id,
    Integer practitionerId,
    Instant startDate,
    Instant endDate
) {
}