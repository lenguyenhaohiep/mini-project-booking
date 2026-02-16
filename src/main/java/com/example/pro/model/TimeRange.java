package com.example.pro.model;

import com.example.pro.exception.TimeRangeInvalidException;

import java.time.Instant;

/**
 * Immutable time range defined by a start and end date-time.
 * The compact constructor enforces that both dates are non-null
 * and that {@code startDate} is strictly before {@code endDate}.
 *
 * @param startDate the inclusive start of the time range
 * @param endDate   the exclusive end of the time range
 */
public record TimeRange(
    Instant startDate,
    Instant endDate
) {
    /**
     * Validates that both dates are non-null and that start is before end.
     *
     * @throws IllegalArgumentException if either date is null
     * @throws TimeRangeInvalidException          if {@code startDate} is not before {@code endDate}
     */
    public TimeRange {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        if (!startDate.isBefore(endDate)) {
            throw new TimeRangeInvalidException("startDate must be before endDate");
        }
    }
}