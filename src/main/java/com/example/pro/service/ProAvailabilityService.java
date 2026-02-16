package com.example.pro.service;

import com.example.pro.entity.Appointment;
import com.example.pro.entity.Availability;
import com.example.pro.entity.TimeSlot;
import com.example.pro.model.AppointmentStatus;
import com.example.pro.model.AvailabilityStatus;
import com.example.pro.model.TimeRange;
import com.example.pro.repository.AppointmentRepository;
import com.example.pro.repository.AvailabilityRepository;
import com.example.pro.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.example.pro.model.TimeSlotStatus.MODIFIED;
import static com.example.pro.model.TimeSlotStatus.NEW;
import static com.example.pro.utils.TimeRangeUtil.sortAndMerge;
import static com.example.pro.utils.TimeRangeUtil.subtractOverlapRanges;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProAvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final AppointmentRepository appointmentRepository;
    private final TimeSlotRepository timeSlotRepository;

    private static final Duration APPOINTMENT_DURATION = Duration.ofMinutes(15);
    private static final Duration MAX_SLOT_EXTENSION = Duration.ofMinutes(14);

    /**
     * Retrieves all existing availabilities for a given practitioner.
     *
     * @param practitionerId the practitioner's ID
     * @return list of availabilities for the practitioner
     */
    public List<Availability> findByPractitionerId(int practitionerId) {
        return availabilityRepository.findByPractitionerId(practitionerId);
    }

    /**
     * Retrieves only free (bookable) availabilities for a given practitioner.
     *
     * @param practitionerId the practitioner's ID
     * @return list of free availabilities for the practitioner
     */
    public List<Availability> findFreeAvailabilitiesByPractitionerId(int practitionerId) {
        return availabilityRepository.findByPractitionerIdAndStatus(practitionerId, AvailabilityStatus.FREE);
    }

    /**
     * Splits a free time range into fixed {@code APPOINTMENT_DURATION}-minute
     * availability slots for a practitioner. Any remaining time shorter than
     * one full slot is discarded.
     *
     * @param timeRange      the free time range to split
     * @param practitionerId the practitioner's ID
     * @return list of availability slots within the time range
     */
    private List<Availability> splitSingleTimeRangeIntoAvailabilities(TimeRange timeRange, Integer practitionerId) {
        List<Availability> availabilities = new ArrayList<>();
        Instant start = timeRange.startDate();
        while (!start.plus(APPOINTMENT_DURATION).isAfter(timeRange.endDate())) {
            Instant end = start.plus(APPOINTMENT_DURATION);
            availabilities.add(
                Availability.builder().startDate(start).endDate(end).practitionerId(practitionerId).build()
            );
            start = end;
        }
        return availabilities;
    }

    /**
     * Generates availability slots for all given free time ranges by delegating
     * to {@link #splitSingleTimeRangeIntoAvailabilities(TimeRange, Integer)} for each range.
     *
     * @param timeRanges     the free time ranges to split into slots
     * @param practitionerId the practitioner's ID
     * @return flat list of availability slots across all time ranges
     */
    private List<Availability> generateAvailabilities(List<TimeRange> timeRanges, Integer practitionerId) {
        return timeRanges.stream()
            .map(timeRange -> splitSingleTimeRangeIntoAvailabilities(timeRange, practitionerId))
            .flatMap(List::stream).toList();
    }

    /**
     * Generates and persists new availability slots for a practitioner.
     * Fetches the practitioner's timeslots, merges overlapping ones, subtracts
     * existing appointments and previously generated availabilities, then splits
     * the remaining free time ranges into bookable slots.
     *
     * @param practitionerId the practitioner's ID
     * @return list of newly created availability slots
     */
    @Transactional
    public List<Availability> generateAvailabilities(int practitionerId) {
        log.info("Availabilities generation starts for practitioner {}", practitionerId);

        List<TimeSlot> timeslots = timeSlotRepository.findByPractitionerIdAndStatusIn(practitionerId,
            List.of(NEW, MODIFIED));
        if (timeslots.isEmpty()) {
            log.info("No unplanned timeslots found for practitioner {}", practitionerId);
            return Collections.emptyList();
        }

        List<TimeRange> mergedTimeSlots = sortAndMerge(timeslots.stream().map(TimeSlot::getTimeRange).toList());
        var range = new TimeRange(mergedTimeSlots.getFirst().startDate(), mergedTimeSlots.getLast().endDate());
        List<TimeRange> extendedSlots = extendTimeSlots(mergedTimeSlots);
        List<TimeRange> occupiedRanges = getOccupiedTimeRanges(practitionerId, range);
        List<TimeRange> freeRanges = subtractOverlapRanges(extendedSlots, occupiedRanges);
        List<Availability> availabilities = generateAvailabilities(freeRanges, practitionerId);

        availabilityRepository.saveAll(availabilities);
        timeslots.forEach(TimeSlot::markAsPlanned);
        timeSlotRepository.saveAll(timeslots);

        log.info("Generated {} availabilities, marked {} timeslots as PLANNED for practitioner {}",
            availabilities.size(), timeslots.size(), practitionerId);
        return availabilities;
    }

    /**
     * Retrieves all occupied time ranges for a practitioner within the given range.
     * Combines booked appointments and existing availabilities, then sorts and merges
     * them into non-overlapping time ranges.
     *
     * @param practitionerId the practitioner's ID
     * @param range          the time range to search within
     * @return sorted, non-overlapping list of occupied time ranges
     */
    private List<TimeRange> getOccupiedTimeRanges(int practitionerId, TimeRange range) {
        List<Appointment> appointments = appointmentRepository.findByPractitionerIdAndStartDateBetweenAndStatus(
            practitionerId, range.startDate(), range.endDate(), AppointmentStatus.BOOKED
        );
        List<Availability> existingAvailabilities = availabilityRepository.findByPractitionerIdAndStartDateBetween(
            practitionerId, range.startDate(), range.endDate()
        );
        var occupiedRanges = Stream.concat(
            appointments.stream().map(Appointment::getTimeRange),
            existingAvailabilities.stream().map(Availability::getTimeRange)
        ).toList();

        return sortAndMerge(occupiedRanges);
    }

    /**
     * Extends each timeslot's end by up to {@code APPOINTMENT_DURATION - 1} minutes,
     * capped by the gap to the next timeslot, so that a trailing appointment starting
     * near the end of a timeslot can overflow without overlapping the next one.
     *
     * @param timeslots sorted, non-overlapping timeslots to extend
     * @return new list of extended time ranges
     */
    private List<TimeRange> extendTimeSlots(List<TimeRange> timeslots) {
        List<TimeRange> extended = new ArrayList<>();
        for (int i = 0; i < timeslots.size(); i++) {
            var current = timeslots.get(i);
            var nextSlot = (i + 1) < timeslots.size() ? timeslots.get(i + 1) : null;
            var extendedMinutes = MAX_SLOT_EXTENSION.toMinutes();
            if (nextSlot != null) {
                extendedMinutes = Math.min(
                    extendedMinutes,
                    Duration.between(current.endDate(), nextSlot.startDate()).toMinutes()
                );
            }
            extended.add(new TimeRange(current.startDate(), current.endDate().plus(Duration.ofMinutes(extendedMinutes))));
        }
        return extended;
    }
}
