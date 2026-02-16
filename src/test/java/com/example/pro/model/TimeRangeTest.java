package com.example.pro.model;

import com.example.pro.exception.TimeRangeInvalid;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TimeRangeTest {

    private static final LocalDateTime T10 = LocalDateTime.of(2026, 1, 1, 10, 0);
    private static final LocalDateTime T11 = LocalDateTime.of(2026, 1, 1, 11, 0);

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
            Arguments.of(T11, T10, TimeRangeInvalid.class),
            Arguments.of(T10, T10, TimeRangeInvalid.class)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidDatesProvider")
    void givenInvalidDates_whenCreateTimeRange_thenThrows(
        LocalDateTime startDate,
        LocalDateTime endDate,
        Class<Exception> expectedException) {
        assertThatThrownBy(() -> new TimeRange(startDate, endDate))
            .isInstanceOf(expectedException);
    }
}