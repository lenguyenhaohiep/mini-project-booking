package com.example.pro.utils;

import com.example.pro.model.TimeRange;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class TimeRangeUtil {

    private TimeRangeUtil() {
    }

    /**
     * Returns the later of two {@link Instant} values.
     *
     * @param dateTime1 first date-time
     * @param dateTime2 second date-time
     * @return the later of {@code a} and {@code b}, or {@code a} if equal
     */
    private static Instant max(Instant dateTime1, Instant dateTime2) {
        return dateTime1.isAfter(dateTime2) ? dateTime1 : dateTime2;
    }

    /**
     * Filters out invalid time ranges (where start >= end), sorts by start date,
     * and merges overlapping or adjacent time ranges into a minimal set.
     *
     * @param timeRanges the time ranges to process
     * @return sorted, non-overlapping list of merged time ranges
     */
    public static List<TimeRange> sortAndMerge(List<TimeRange> timeRanges) {
        return timeRanges.stream()
            .filter(timeRange -> timeRange.startDate().isBefore(timeRange.endDate()))
            .sorted(Comparator.comparing(TimeRange::startDate).thenComparing(TimeRange::endDate))
            .collect(
                ArrayList::new,
                (List<TimeRange> list, TimeRange current) -> {
                    if (list.isEmpty()) {
                        list.add(current);
                    } else {
                        TimeRange last = list.getLast();
                        if (last.endDate().isBefore(current.startDate())) {
                            list.add(current);
                        } else {
                            var newTimeRange = new TimeRange(
                                last.startDate(),
                                max(current.endDate(), last.endDate())
                            );
                            list.set(list.size() - 1, newTimeRange);
                        }
                    }
                },
                List::addAll
            );
    }

    /**
     * Subtracts {@code toSubtract} from {@code baseRanges}, returning the remaining
     * portions of {@code baseRanges} that do not overlap with any time range in {@code toSubtract}.
     * Both inputs must be sorted and non-overlapping (as produced by {@link #sortAndMerge}).
     *
     * @param baseRanges the base time ranges to subtract from
     * @param toSubtract the time ranges to remove
     * @return the remaining free time ranges after subtraction
     */
    public static List<TimeRange> subtractOverlapRanges(List<TimeRange> baseRanges, List<TimeRange> toSubtract) {
        if (baseRanges.isEmpty() || toSubtract.isEmpty()) {
            return baseRanges;
        }

        List<TimeRange> result = new ArrayList<>();
        int j = 0;

        for (TimeRange current : baseRanges) {
            Instant start = current.startDate();
            Instant end = current.endDate();

            // skip time range end before current
            while (j < toSubtract.size() && toSubtract.get(j).endDate().isBefore(start)) {
                j++;
            }

            int k = j;
            // overlap detection
            while (k < toSubtract.size() && toSubtract.get(k).startDate().isBefore(end)) {
                TimeRange overlap = toSubtract.get(k);
                // get range before overlap start
                if (start.isBefore(overlap.startDate())) {
                    result.add(new TimeRange(start, overlap.startDate()));
                }
                // overlap end will be next start
                start = max(start, overlap.endDate());
                k++;
            }

            // if overlap inside
            if (start.isBefore(end)) {
                result.add(new TimeRange(start, end));
            }
        }

        return result;
    }
}
