package com.example.pro.dto.response;

/**
 * Response DTO representing a practitioner.
 *
 * @param id         the practitioner ID
 * @param firstName  the practitioner's first name
 * @param lastName   the practitioner's last name
 * @param speciality the practitioner's medical speciality
 */
public record PractitionerDTO(
    Integer id,
    String firstName,
    String lastName,
    String speciality
) {
}