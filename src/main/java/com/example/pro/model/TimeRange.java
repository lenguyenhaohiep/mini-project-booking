package com.example.pro.model;

import com.example.pro.exception.TimeRangeInvalid;

import java.time.LocalDateTime;

/**
 * Immutable time range defined by a start and end date-time.
 * The compact constructor enforces that both dates are non-null
 * and that {@code startDate} is strictly before {@code endDate}.
 *
 * @param startDate the inclusive start of the time range
 * @param endDate   the exclusive end of the time range
 */
public record TimeRange(
    LocalDateTime startDate,
    LocalDateTime endDate
) {
    /**
     * Validates that both dates are non-null and that start is before end.
     *
     * @throws IllegalArgumentException if either date is null
     * @throws TimeRangeInvalid          if {@code startDate} is not before {@code endDate}
     */
    public TimeRange {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        if (!startDate.isBefore(endDate)) {
            throw new TimeRangeInvalid("startDate must be before endDate");
        }
    }

    public boolean isOverlap(TimeRange timeRange) {
        return this.startDate.isBefore(timeRange.endDate) && timeRange.startDate.isBefore(this.endDate);
    }
}