package com.example.pro.service;

import com.example.pro.EntityFactory;
import com.example.pro.entity.Availability;
import com.example.pro.entity.Practitioner;
import com.example.pro.model.AvailabilityStatus;
import com.example.pro.model.TimeRange;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ProAvailabilityServiceTest extends IntegrationBaseTest{
    private final  EntityFactory entityFactory = new EntityFactory();
    private  final static Integer patient_id=657679;

    @Test
    void generateAvailabilities() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        Instant startDate = Instant.parse("2020-02-05T11:00:00Z");
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(), startDate, startDate.plus(Duration.ofHours(1))));

        List<Availability> availabilities = proAvailabilityService.generateAvailabilities(practitioner.getId());

        assertEquals(4, availabilities.size());

        List<Instant> availabilitiesStartDate = availabilities.stream().map(a -> a.getTimeRange().startDate()).collect(Collectors.toList());
        ArrayList<Instant> expectedStartDate = new ArrayList<>();
        expectedStartDate.add(startDate);
        expectedStartDate.add(startDate.plus(Duration.ofMinutes(15)));
        expectedStartDate.add(startDate.plus(Duration.ofMinutes(30)));
        expectedStartDate.add(startDate.plus(Duration.ofMinutes(45)));
        assertTrue(availabilitiesStartDate.containsAll(expectedStartDate));
    }

    @Test
    void checkAvailabilitiesAreNotDuplicated() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        Instant startDate = Instant.parse("2020-02-05T11:00:00Z");
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(), startDate, startDate.plus(Duration.ofHours(1))));

        availabilityRepository.save(Availability.builder().practitionerId(practitioner.getId()).timeRange(new TimeRange(startDate, startDate.plus(Duration.ofMinutes(15)))).build());
        availabilityRepository.save(Availability.builder().practitionerId(practitioner.getId()).timeRange(new TimeRange(startDate.plus(Duration.ofMinutes(15)), startDate.plus(Duration.ofMinutes(30)))).build());
        availabilityRepository.save(Availability.builder().practitionerId(practitioner.getId()).timeRange(new TimeRange(startDate.plus(Duration.ofMinutes(35)), startDate.plus(Duration.ofMinutes(45)))).build());
        availabilityRepository.save(Availability.builder().practitionerId(practitioner.getId()).timeRange(new TimeRange(startDate.plus(Duration.ofMinutes(45)), startDate.plus(Duration.ofHours(1)))).build());

        proAvailabilityService.generateAvailabilities(practitioner.getId());

        List<Availability> availabilities = proAvailabilityService.findByPractitionerId(practitioner.getId());
        assertEquals(4, availabilities.size());
    }

    @Test
    void generateAvailabilityWithOneAppointment() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        Instant startDate = Instant.parse("2020-02-05T11:00:00Z");
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(), startDate, startDate.plus(Duration.ofHours(1))));
        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plus(Duration.ofMinutes(30)),
                startDate.plus(Duration.ofMinutes(45))));

        List<Availability> availabilities = proAvailabilityService.generateAvailabilities(practitioner.getId());

        assertEquals(3, availabilities.size());

        List<Instant> availabilitiesStartDate = availabilities.stream().map(a -> a.getTimeRange().startDate()).collect(Collectors.toList());
        ArrayList<Instant> expectedStartDate = new ArrayList<>();
        expectedStartDate.add(startDate);
        expectedStartDate.add(startDate.plus(Duration.ofMinutes(15)));
        expectedStartDate.add(startDate.plus(Duration.ofMinutes(45)));
        assertTrue(availabilitiesStartDate.containsAll(expectedStartDate));
    }

    @Test
    void generateAvailabilityWithExistingAppointments() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        Instant startDate = Instant.parse("2020-02-05T11:00:00Z");
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(), startDate, startDate.plus(Duration.ofHours(1))));
        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate,
                startDate.plus(Duration.ofMinutes(15))));

        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plus(Duration.ofMinutes(30)),
                startDate.plus(Duration.ofMinutes(45))));

        List<Availability> availabilities = proAvailabilityService.generateAvailabilities(practitioner.getId());

        assertEquals(2, availabilities.size());

        List<Instant> availabilitiesStartDate = availabilities.stream().map(a -> a.getTimeRange().startDate()).collect(Collectors.toList());
        ArrayList<Instant> expectedStartDate = new ArrayList<>();
        expectedStartDate.add(startDate.plus(Duration.ofMinutes(15)));
        expectedStartDate.add(startDate.plus(Duration.ofMinutes(45)));
        assertTrue(availabilitiesStartDate.containsAll(expectedStartDate));
    }

    @Test
    void generateAvailabilitiesWithExistingTwentyMinutesAppointment() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        Instant startDate = Instant.parse("2020-02-05T11:00:00Z");
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(), startDate, startDate.plus(Duration.ofHours(1))));
        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plus(Duration.ofMinutes(15)),
                startDate.plus(Duration.ofMinutes(35))));

        List<Availability> availabilities = proAvailabilityService.generateAvailabilities(practitioner.getId());

        assertTrue(availabilities.size() >= 2);
    }

    @Test
    void generateAvailabilitiesWithAppointmentOnTwoAvailabilities() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        Instant startDate = Instant.parse("2020-02-05T11:00:00Z");
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(), startDate, startDate.plus(Duration.ofHours(1))));
        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plus(Duration.ofMinutes(20)),
                startDate.plus(Duration.ofMinutes(35))));

        List<Availability> availabilities = proAvailabilityService.generateAvailabilities(practitioner.getId());

        assertTrue(availabilities.size() >= 2);
    }

    @Test
    void generateOptimalAvailabilitiesWithExistingTwentyMinutesAppointment() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        Instant startDate = Instant.parse("2020-02-05T11:00:00Z");
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(), startDate, startDate.plus(Duration.ofHours(1))));
        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plus(Duration.ofMinutes(15)),
                startDate.plus(Duration.ofMinutes(35))));

        List<Availability> availabilities = proAvailabilityService.generateAvailabilities(practitioner.getId());

        assertEquals(3, availabilities.size());

        List<Instant> availabilitiesStartDate = availabilities.stream().map(a -> a.getTimeRange().startDate()).collect(Collectors.toList());
        ArrayList<Instant> expectedStartDate = new ArrayList<>();
        expectedStartDate.add(startDate);
        expectedStartDate.add(startDate.plus(Duration.ofMinutes(35)));
        expectedStartDate.add(startDate.plus(Duration.ofMinutes(50)));
        assertTrue(availabilitiesStartDate.containsAll(expectedStartDate));
    }

    @Test
    void generateOptimalAvailabilitiesWithAppointmentOnTwoAvailabilities() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        Instant startDate = Instant.parse("2020-02-05T11:00:00Z");
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(), startDate, startDate.plus(Duration.ofHours(1))));
        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plus(Duration.ofMinutes(20)),
                startDate.plus(Duration.ofMinutes(35))));

        List<Availability> availabilities = proAvailabilityService.generateAvailabilities(practitioner.getId());

        assertEquals(3, availabilities.size());

        List<Instant> availabilitiesStartDate = availabilities.stream().map(a -> a.getTimeRange().startDate()).collect(Collectors.toList());
        ArrayList<Instant> expectedStartDate = new ArrayList<>();
        expectedStartDate.add(startDate);
        expectedStartDate.add(startDate.plus(Duration.ofMinutes(35)));
        expectedStartDate.add(startDate.plus(Duration.ofMinutes(50)));
        assertTrue(availabilitiesStartDate.containsAll(expectedStartDate));
    }

    @Test
    void givenNoSlot_whenGenerateAvailabilities_thenReturnEmptyList() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        List<Availability> availabilities = proAvailabilityService.generateAvailabilities(practitioner.getId());

        assertThat(availabilities).isEmpty();
    }

    @Test
    void givenSlotThatAlreadyGenerated_whenGenerateAvailabilities_thenReturnEmptyList() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        Instant startDate = Instant.parse("2026-02-05T11:00:00Z");
        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
            patient_id,
            startDate,
            startDate.plus(Duration.ofMinutes(15))));
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(),
            startDate,
            startDate.plus(Duration.ofMinutes(15))));

        List<Availability> availabilities = proAvailabilityService.generateAvailabilities(practitioner.getId());
        assertThat(availabilities).isEmpty();
    }

    @Test
    void givenThatAvailabilityCouldExceedSlotEndDateAndNoOverlapWithNextSlot_whenGenerateAvailabilities_thenCreated() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        Instant startDate = Instant.parse("2026-02-05T11:00:00Z");

        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
            patient_id,
            startDate.plus(Duration.ofMinutes(20)),
            startDate.plus(Duration.ofMinutes(35))));

        // 11:00 - 12:00
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(), startDate, startDate.plus(Duration.ofHours(1))));
        // 13:00 - 14:00
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(),
            startDate.plus(Duration.ofHours(2)),
            startDate.plus(Duration.ofHours(3))));

        List<Availability> availabilities = proAvailabilityService.generateAvailabilities(practitioner.getId());

        assertThat(availabilities).hasSize(7)
            .extracting(a -> a.getTimeRange().startDate())
            .containsExactly(
                startDate,                                                          // 11:00
                startDate.plus(Duration.ofMinutes(35)),                            // 11:35
                startDate.plus(Duration.ofMinutes(50)),                            // 11:50
                startDate.plus(Duration.ofHours(2)),                               // 13:00
                startDate.plus(Duration.ofHours(2)).plus(Duration.ofMinutes(15)),  // 13:15
                startDate.plus(Duration.ofHours(2)).plus(Duration.ofMinutes(30)),  // 13:30
                startDate.plus(Duration.ofHours(2)).plus(Duration.ofMinutes(45))   // 13:45
            );
    }

    @Test
    void givenThatAvailabilityCouldExceedSlotEndDateAndOverlapWithNextSlot_whenGenerateAvailabilities_thenCreated() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        Instant startDate = Instant.parse("2026-02-05T11:00:00Z");

        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
            patient_id,
            startDate.plus(Duration.ofMinutes(20)),
            startDate.plus(Duration.ofMinutes(35))));
        // 11:00 - 12:00
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(), startDate, startDate.plus(Duration.ofHours(1))));
        // 12:01 - 13:01
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(),
            startDate.plus(Duration.ofMinutes(61)),
            startDate.plus(Duration.ofMinutes(121))));

        List<Availability> availabilities = proAvailabilityService.generateAvailabilities(practitioner.getId());

        assertThat(availabilities)
            .hasSize(6)
            .extracting(a -> a.getTimeRange().startDate())
            .containsExactly(
                startDate,                                      // 11:00
                startDate.plus(Duration.ofMinutes(35)),        // 11:35
                startDate.plus(Duration.ofMinutes(61)),        // 12:01
                startDate.plus(Duration.ofMinutes(76)),        // 12:16
                startDate.plus(Duration.ofMinutes(91)),        // 12:31
                startDate.plus(Duration.ofMinutes(106))        // 12:46
            );
    }

    @Test
    void givenThatAvailabilityCouldExceedSlotEndDateAndOverlapWithExistingAppointment_whenGenerateAvailabilities_thenCreated() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        Instant startDate = Instant.parse("2026-02-05T11:00:00Z");

        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
            patient_id,
            startDate.plus(Duration.ofMinutes(20)),
            startDate.plus(Duration.ofMinutes(35))));
        // 11:00 - 12:00
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(), startDate, startDate.plus(Duration.ofHours(1))));

        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
            patient_id,
            startDate.plus(Duration.ofMinutes(60)),
            startDate.plus(Duration.ofMinutes(75))));

        List<Availability> availabilities = proAvailabilityService.generateAvailabilities(practitioner.getId());

        assertThat(availabilities).hasSize(2)
            .extracting(a -> a.getTimeRange().startDate())
            .containsExactly(
                startDate,                                  // 11:00
                startDate.plus(Duration.ofMinutes(35))     // 11:35
            );
    }

    @Test
    void givenThatAvailabilityCouldExceedSlotEndDateAndOverlapWithExistingAvailability_whenGenerateAvailabilities_thenCreated() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        Instant startDate = Instant.parse("2026-02-05T11:00:00Z");

        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
            patient_id,
            startDate.plus(Duration.ofMinutes(20)),
            startDate.plus(Duration.ofMinutes(35))));
        // 11:00 - 12:00
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(), startDate, startDate.plus(Duration.ofHours(1))));

        availabilityRepository.save(entityFactory.createAvailability(practitioner.getId(),
            startDate.plus(Duration.ofMinutes(60)),
            startDate.plus(Duration.ofMinutes(75))));

        List<Availability> availabilities = proAvailabilityService.generateAvailabilities(practitioner.getId());

        assertThat(availabilities)
            .hasSize(2)
            .extracting(a -> a.getTimeRange().startDate())
            .containsExactly(
                startDate,                                  // 11:00
                startDate.plus(Duration.ofMinutes(35))     // 11:35
            );
    }

    @Test
    void givenAvailabilitiesUnavailableAndFree_whenFindFreeAvailabilitiesByPractitionerId_thenReturnOnlyAvailable() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        Instant start = Instant.parse("2026-02-05T11:00:00Z");

        Availability free1 = availabilityRepository.save(Availability.builder()
            .practitionerId(practitioner.getId())
            .timeRange(new TimeRange(start, start.plus(Duration.ofMinutes(15))))
            .build());

        Availability free2 = availabilityRepository.save(Availability.builder()
            .practitionerId(practitioner.getId())
            .timeRange(new TimeRange(start.plus(Duration.ofMinutes(15)), start.plus(Duration.ofMinutes(30))))
            .status(AvailabilityStatus.FREE)
            .build());

        Availability unavailable = availabilityRepository.save(Availability.builder()
            .practitionerId(practitioner.getId())
            .timeRange(new TimeRange(start.plus(Duration.ofMinutes(30)), start.plus(Duration.ofMinutes(45))))
            .status(AvailabilityStatus.UNAVAILABLE)
            .build());

        availabilityRepository.save(unavailable);

        List<Availability> result = proAvailabilityService.findFreeAvailabilitiesByPractitionerId(practitioner.getId());

        assertThat(result)
            .extracting(Availability::getStatus)
            .containsOnly(AvailabilityStatus.FREE);
        assertThat(result).hasSize(2);
    }
}
