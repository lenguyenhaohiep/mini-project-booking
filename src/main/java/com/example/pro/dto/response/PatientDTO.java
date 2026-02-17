package com.example.pro.dto.response;

import java.time.LocalDate;

/**
 * Response DTO representing a patient.
 *
 * @param id        the patient ID
 * @param firstName the patient's first name
 * @param lastName  the patient's last name
 * @param birthDate the patient's date of birth
 */
public record PatientDTO (
    Integer id,
    String firstName,
    String lastName,
    LocalDate birthDate) {
}