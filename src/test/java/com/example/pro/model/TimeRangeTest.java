package com.example.pro.model;

import com.example.pro.exception.TimeRangeInvalidException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TimeRangeTest {

    private static final Instant T10 = Instant.parse("2026-01-01T10:00:00Z");
    private static final Instant T11 = Instant.parse("2026-01-01T11:00:00Z");

    @Test
    void givenValidDates_whenCreateTimeRange_thenCreated() {
        TimeRange timeRange = new TimeRange(T10, T11);

        assertThat(timeRange.startDate()).isEqualTo(T10);
        assertThat(timeRange.endDate()).isEqualTo(T11);
    }

    static Stream<Arguments> invalidDatesProvider() {
        return Stream.of(
            Arguments.of(null, T11, IllegalArgumentException.class),
            Arguments.of(T10, null, IllegalArgumentException.class),
            Arguments.of(T11, T10, TimeRangeInvalidException.class),
            Arguments.of(T10, T10, TimeRangeInvalidException.class)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidDatesProvider")
    void givenInvalidDates_whenCreateTimeRange_thenThrows(
        Instant startDate,
        Instant endDate,
        Class<Exception> expectedException) {
        assertThatThrownBy(() -> new TimeRange(startDate, endDate))
            .isInstanceOf(expectedException);
    }
}
