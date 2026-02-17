package com.example.pro.utils;

import com.example.pro.exception.TimeRangeInvalidException;

import java.time.Instant;

/**
 * Utility class providing common validation methods for entities and models.
 */
public final class Validator {
    private Validator() {

    }

    /**
     * Validates that the given ID is non-null and positive.
     *
     * @param id        the ID to validate
     * @param fieldName the field name used in the error message
     * @throws IllegalArgumentException if the ID is null or less than 1
     */
    public static void validateValidId(Integer id, String fieldName) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException("%s must be positive".formatted(fieldName));
        }
    }

    /**
     * Validates that both dates are non-null and that {@code startDate} is strictly before {@code endDate}.
     *
     * @param startDate      the start date to validate
     * @param endDate        the end date to validate
     * @param fieldNameStart the start date field name used in the error message
     * @param fieldNameEnd   the end date field name used in the error message
     * @throws IllegalArgumentException  if either date is null
     * @throws TimeRangeInvalidException if {@code startDate} is not before {@code endDate}
     */
    public static void validateValidRange(Instant startDate, Instant endDate, String fieldNameStart, String fieldNameEnd) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        if (!startDate.isBefore(endDate)) {
            throw new TimeRangeInvalidException("%s must be before %s".formatted(fieldNameStart, fieldNameEnd));
        }
    }

    /**
     * Validates that the given string is non-null and not blank.
     *
     * @param value     the string to validate
     * @param fieldName the field name used in the error message
     * @throws IllegalArgumentException if the value is null or blank
     */
    public static void validateNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("%s must not be null or blank".formatted(fieldName));
        }
    }

}