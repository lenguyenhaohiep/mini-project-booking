package com.example.pro.utils;

import com.example.pro.model.TimeRange;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TimeRangeUtilTest {

    private static final Instant T10 = Instant.parse("2026-01-01T10:00:00Z");
    private static final Instant T11 = Instant.parse("2026-01-01T11:00:00Z");
    private static final Instant T12 = Instant.parse("2026-01-01T12:00:00Z");
    private static final Instant T13 = Instant.parse("2026-01-01T13:00:00Z");
    private static final Instant T14 = Instant.parse("2026-01-01T14:00:00Z");
    private static final Instant T15 = Instant.parse("2026-01-01T15:00:00Z");
    private static final Instant T16 = Instant.parse("2026-01-01T16:00:00Z");

    // test empty input
    @Test
    void givenEmptyList_whenSortAndMerge_thenReturnsEmpty() {
        List<TimeRange> result = TimeRangeUtil.sortAndMerge(Collections.emptyList());

        assertThat(result).isEmpty();
    }

    // Test non overlapping 3 time ranges, not sorted then expected sorted
    @Test
    void given3NonOverlappingUnsortedTimeRanges_whenSortAndMerge_thenReturnsSorted() {
        List<TimeRange> input = List.of(
            new TimeRange(T14, T15),
            new TimeRange(T10, T11),
            new TimeRange(T12, T13)
        );

        List<TimeRange> result = TimeRangeUtil.sortAndMerge(input);

        assertThat(result).containsExactly(
            new TimeRange(T10, T11),
            new TimeRange(T12, T13),
            new TimeRange(T14, T15)
        );
    }

    // test 2 overlapping 2 time ranges [a,b][b,c] then expected [a,c]
    @Test
    void given2AdjacentTimeRanges_whenSortAndMerge_thenReturnsMerged() {
        List<TimeRange> input = List.of(
            new TimeRange(T10, T12),
            new TimeRange(T12, T14)
        );

        List<TimeRange> result = TimeRangeUtil.sortAndMerge(input);

        assertThat(result).containsExactly(new TimeRange(T10, T14));
    }

    // test 2 overlapping 2 time ranges [a,b],[c,d] and c<b then expected [a,max(b,d)]
    @Test
    void given2OverlappingTimeRanges_whenSortAndMerge_thenReturnsMerged() {
        List<TimeRange> input = List.of(
            new TimeRange(T10, T13),
            new TimeRange(T12, T15)
        );

        List<TimeRange> result = TimeRangeUtil.sortAndMerge(input);

        assertThat(result).containsExactly(new TimeRange(T10, T15));
    }

    // test subtract with first empty list
    @Test
    void givenEmptyBaseList_whenSubtractOverlapRanges_thenReturnsEmpty() {
        List<TimeRange> toSubtract = List.of(new TimeRange(T10, T12));

        List<TimeRange> result = TimeRangeUtil.subtractOverlapRanges(Collections.emptyList(), toSubtract);

        assertThat(result).isEmpty();
    }

    // test subtract with empty second list
    @Test
    void givenEmptySubtractList_whenSubtractOverlapRanges_thenReturnsBase() {
        List<TimeRange> base = List.of(new TimeRange(T10, T12), new TimeRange(T13, T15));

        List<TimeRange> result = TimeRangeUtil.subtractOverlapRanges(base, Collections.emptyList());

        assertThat(result).containsExactly(
            new TimeRange(T10, T12),
            new TimeRange(T13, T15)
        );
    }

    // test subtract 2 time ranges: no overlap at all
    @Test
    void givenNoOverlap_whenSubtractOverlapRanges_thenReturnsBase() {
        List<TimeRange> base = List.of(new TimeRange(T12, T13));
        List<TimeRange> toSubtract = List.of(new TimeRange(T14, T15));

        List<TimeRange> result = TimeRangeUtil.subtractOverlapRanges(base, toSubtract);

        assertThat(result).containsExactly(new TimeRange(T12, T13));

        List<TimeRange> toSubtract2 = List.of(new TimeRange(T10, T11));

        result = TimeRangeUtil.subtractOverlapRanges(base, toSubtract2);

        assertThat(result).containsExactly(new TimeRange(T12, T13));
    }

    // test subtract 2 time ranges: the second is left overlap
    @Test
    void givenSecondLeftOverlaps_whenSubtractOverlapRanges_thenReturnsRightRemainder() {
        List<TimeRange> base = List.of(new TimeRange(T11, T14));
        List<TimeRange> toSubtract = List.of(new TimeRange(T10, T12));

        List<TimeRange> result = TimeRangeUtil.subtractOverlapRanges(base, toSubtract);

        assertThat(result).containsExactly(new TimeRange(T12, T14));
    }

    // test subtract 2 time ranges: the second is right overlap
    @Test
    void givenSecondRightOverlaps_whenSubtractOverlapRanges_thenReturnsLeftRemainder() {
        List<TimeRange> base = List.of(new TimeRange(T11, T14));
        List<TimeRange> toSubtract = List.of(new TimeRange(T13, T16));

        List<TimeRange> result = TimeRangeUtil.subtractOverlapRanges(base, toSubtract);

        assertThat(result).containsExactly(new TimeRange(T11, T13));
    }

    // test subtract 2 time ranges: the second is inside overlap
    @Test
    void givenSecondInsideBase_whenSubtractOverlapRanges_thenReturnsTwoParts() {
        List<TimeRange> base = List.of(new TimeRange(T10, T15));
        List<TimeRange> toSubtract = List.of(new TimeRange(T12, T13));

        List<TimeRange> result = TimeRangeUtil.subtractOverlapRanges(base, toSubtract);

        assertThat(result).containsExactly(
            new TimeRange(T10, T12),
            new TimeRange(T13, T15)
        );
    }

    // test subtract 2 time ranges: the second is outside overlap
    @Test
    void givenSecondCoversEntireBase_whenSubtractOverlapRanges_thenReturnsEmpty() {
        List<TimeRange> base = List.of(new TimeRange(T12, T13));
        List<TimeRange> toSubtract = List.of(new TimeRange(T10, T15));

        List<TimeRange> result = TimeRangeUtil.subtractOverlapRanges(base, toSubtract);

        assertThat(result).isEmpty();
    }

    // test subtract 2 time ranges: the second is inside overlap
    @Test
    void givenTwoRangesOverlapBase_whenSubtract_thenReturnValidFreeRanges() {
        List<TimeRange> base = List.of(new TimeRange(T10, T15));
        List<TimeRange> toSubtract = List.of(new TimeRange(T11, T12), new TimeRange(T13, T16));

        List<TimeRange> result = TimeRangeUtil.subtractOverlapRanges(base, toSubtract);

        assertThat(result).containsExactly(
            new TimeRange(T10, T11),
            new TimeRange(T12, T13)
        );
    }
}
