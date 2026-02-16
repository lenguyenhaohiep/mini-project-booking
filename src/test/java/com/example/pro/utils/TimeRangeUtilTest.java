package com.example.pro.utils;

import com.example.pro.model.TimeRange;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TimeRangeUtilTest {

    private static final LocalDateTime T10 = LocalDateTime.of(2026, 1, 1, 10, 0);
    private static final LocalDateTime T11 = LocalDateTime.of(2026, 1, 1, 11, 0);
    private static final LocalDateTime T12 = LocalDateTime.of(2026, 1, 1, 12, 0);
    private static final LocalDateTime T13 = LocalDateTime.of(2026, 1, 1, 13, 0);
    private static final LocalDateTime T14 = LocalDateTime.of(2026, 1, 1, 14, 0);
    private static final LocalDateTime T15 = LocalDateTime.of(2026, 1, 1, 15, 0);
    private static final LocalDateTime T16 = LocalDateTime.of(2026, 1, 1, 16, 0);

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