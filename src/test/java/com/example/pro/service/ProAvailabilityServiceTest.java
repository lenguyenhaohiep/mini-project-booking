package com.example.pro.service;

import com.example.pro.EntityFactory;
import com.example.pro.entity.Availability;
import com.example.pro.entity.Practitioner;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.Month;
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
        LocalDateTime startDate = LocalDateTime.of(2020, Month.FEBRUARY, 5, 11, 0, 0);
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(), startDate, startDate.plusHours(1)));

        List<Availability> availabilities = proAvailabilityService.generateAvailabilities(practitioner.getId());

        assertEquals(4, availabilities.size());

        List<LocalDateTime> availabilitiesStartDate = availabilities.stream().map(Availability::getStartDate).collect(Collectors.toList());
        ArrayList<LocalDateTime> expectedStartDate = new ArrayList<>();
        expectedStartDate.add(startDate);
        expectedStartDate.add(startDate.plusMinutes(15));
        expectedStartDate.add(startDate.plusMinutes(30));
        expectedStartDate.add(startDate.plusMinutes(45));
        assertTrue(availabilitiesStartDate.containsAll(expectedStartDate));
    }

    @Test
    void checkAvailabilitiesAreNotDuplicated() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        LocalDateTime startDate = LocalDateTime.of(2020, Month.FEBRUARY, 5, 11, 0, 0);
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(), startDate, startDate.plusHours(1)));

        availabilityRepository.save(Availability.builder().practitionerId(practitioner.getId()).startDate(startDate).endDate(startDate.plusMinutes(15)).build());
        availabilityRepository.save(Availability.builder().practitionerId(practitioner.getId()).startDate(startDate.plusMinutes(15)).endDate(startDate.plusMinutes(30)).build());
        availabilityRepository.save(Availability.builder().practitionerId(practitioner.getId()).startDate(startDate.plusMinutes(35)).endDate(startDate.plusMinutes(45)).build());
        availabilityRepository.save(Availability.builder().practitionerId(practitioner.getId()).startDate(startDate.plusMinutes(45)).endDate(startDate.plusHours(1)).build());

        proAvailabilityService.generateAvailabilities(practitioner.getId());

        List<Availability> availabilities = proAvailabilityService.findByPractitionerId(practitioner.getId());
        assertEquals(4, availabilities.size());
    }

    @Test
    void generateAvailabilityWithOneAppointment() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        LocalDateTime startDate = LocalDateTime.of(2020, Month.FEBRUARY, 5, 11, 0, 0);
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(), startDate, startDate.plusHours(1)));
        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plusMinutes(30),
                startDate.plusMinutes(45)));

        List<Availability> availabilities = proAvailabilityService.generateAvailabilities(practitioner.getId());

        assertEquals(3, availabilities.size());

        List<LocalDateTime> availabilitiesStartDate = availabilities.stream().map(Availability::getStartDate).collect(Collectors.toList());
        ArrayList<LocalDateTime> expectedStartDate = new ArrayList<>();
        expectedStartDate.add(startDate);
        expectedStartDate.add(startDate.plusMinutes(15));
        expectedStartDate.add(startDate.plusMinutes(45));
        assertTrue(availabilitiesStartDate.containsAll(expectedStartDate));
    }

    @Test
    void generateAvailabilityWithExistingAppointments() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        LocalDateTime startDate = LocalDateTime.of(2020, Month.FEBRUARY, 5, 11, 0, 0);
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(), startDate, startDate.plusHours(1)));
        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate,
                startDate.plusMinutes(15)));

        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plusMinutes(30),
                startDate.plusMinutes(45)));

        List<Availability> availabilities = proAvailabilityService.generateAvailabilities(practitioner.getId());

        assertEquals(2, availabilities.size());

        List<LocalDateTime> availabilitiesStartDate = availabilities.stream().map(Availability::getStartDate).collect(Collectors.toList());
        ArrayList<LocalDateTime> expectedStartDate = new ArrayList<>();
        expectedStartDate.add(startDate.plusMinutes(15));
        expectedStartDate.add(startDate.plusMinutes(45));
        assertTrue(availabilitiesStartDate.containsAll(expectedStartDate));
    }

    @Test
    void generateAvailabilitiesWithExistingTwentyMinutesAppointment() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        LocalDateTime startDate = LocalDateTime.of(2020, Month.FEBRUARY, 5, 11, 0, 0);
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(), startDate, startDate.plusHours(1)));
        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plusMinutes(15),
                startDate.plusMinutes(35)));

        List<Availability> availabilities = proAvailabilityService.generateAvailabilities(practitioner.getId());

        assertTrue(availabilities.size() >= 2);
    }

    @Test
    void generateAvailabilitiesWithAppointmentOnTwoAvailabilities() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        LocalDateTime startDate = LocalDateTime.of(2020, Month.FEBRUARY, 5, 11, 0, 0);
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(), startDate, startDate.plusHours(1)));
        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plusMinutes(20),
                startDate.plusMinutes(35)));

        List<Availability> availabilities = proAvailabilityService.generateAvailabilities(practitioner.getId());

        assertTrue(availabilities.size() >= 2);
    }

    @Test
    void generateOptimalAvailabilitiesWithExistingTwentyMinutesAppointment() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        LocalDateTime startDate = LocalDateTime.of(2020, Month.FEBRUARY, 5, 11, 0, 0);
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(), startDate, startDate.plusHours(1)));
        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plusMinutes(15),
                startDate.plusMinutes(35)));

        List<Availability> availabilities = proAvailabilityService.generateAvailabilities(practitioner.getId());

        assertEquals(3, availabilities.size());

        List<LocalDateTime> availabilitiesStartDate = availabilities.stream().map(Availability::getStartDate).collect(Collectors.toList());
        ArrayList<LocalDateTime> expectedStartDate = new ArrayList<>();
        expectedStartDate.add(startDate);
        expectedStartDate.add(startDate.plusMinutes(35));
        expectedStartDate.add(startDate.plusMinutes(50));
        assertTrue(availabilitiesStartDate.containsAll(expectedStartDate));
    }

    @Test
    void generateOptimalAvailabilitiesWithAppointmentOnTwoAvailabilities() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        LocalDateTime startDate = LocalDateTime.of(2020, Month.FEBRUARY, 5, 11, 0, 0);
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(), startDate, startDate.plusHours(1)));
        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plusMinutes(20),
                startDate.plusMinutes(35)));

        List<Availability> availabilities = proAvailabilityService.generateAvailabilities(practitioner.getId());

        assertEquals(3, availabilities.size());

        List<LocalDateTime> availabilitiesStartDate = availabilities.stream().map(Availability::getStartDate).collect(Collectors.toList());
        ArrayList<LocalDateTime> expectedStartDate = new ArrayList<>();
        expectedStartDate.add(startDate);
        expectedStartDate.add(startDate.plusMinutes(35));
        expectedStartDate.add(startDate.plusMinutes(50));
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
        LocalDateTime startDate = LocalDateTime.of(2026, Month.FEBRUARY, 5, 11, 0, 0);
        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
            patient_id,
            startDate,
            startDate.plusMinutes(15)));
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(),
            startDate,
            startDate.plusMinutes(15)));

        List<Availability> availabilities = proAvailabilityService.generateAvailabilities(practitioner.getId());
        assertThat(availabilities).isEmpty();
    }

    @Test
    void givenThatAvailabilityCouldExceedSlotEndDateAndNoOverlapWithNextSlot_whenGenerateAvailabilities_thenCreated() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        LocalDateTime startDate = LocalDateTime.of(2026, Month.FEBRUARY, 5, 11, 0, 0);

        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
            patient_id,
            startDate.plusMinutes(20),
            startDate.plusMinutes(35)));

        // 11:00 - 12:00
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(), startDate, startDate.plusHours(1)));
        // 13:00 - 14:00
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(),
            startDate.plusHours(2),
            startDate.plusHours(3)));

        List<Availability> availabilities = proAvailabilityService.generateAvailabilities(practitioner.getId());

        assertThat(availabilities).hasSize(7)
            .extracting(Availability::getStartDate)
            .containsExactly(
                startDate,                                      // 11:00
                startDate.plusMinutes(35),                      // 11:35
                startDate.plusMinutes(50),                      // 11:50
                startDate.plusHours(2),                         // 13:00
                startDate.plusHours(2).plusMinutes(15),         // 13:15
                startDate.plusHours(2).plusMinutes(30),         // 13:30
                startDate.plusHours(2).plusMinutes(45)          // 13:45
            );
    }

    @Test
    void givenThatAvailabilityCouldExceedSlotEndDateAndOverlapWithNextSlot_whenGenerateAvailabilities_thenCreated() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        LocalDateTime startDate = LocalDateTime.of(2026, Month.FEBRUARY, 5, 11, 0, 0);

        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
            patient_id,
            startDate.plusMinutes(20),
            startDate.plusMinutes(35)));
        // 11:00 - 12:00
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(), startDate, startDate.plusHours(1)));
        // 12:01 - 13:01
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(),
            startDate.plusHours(1).plusMinutes(1),
            startDate.plusHours(2).plusMinutes(1)));

        List<Availability> availabilities = proAvailabilityService.generateAvailabilities(practitioner.getId());

        assertThat(availabilities)
            .hasSize(6)
            .extracting(Availability::getStartDate)
            .containsExactly(
                startDate,                                                  // 11:00
                startDate.plusMinutes(35),                                  // 11:35
                startDate.plusHours(1).plusMinutes(1),                      // 12:01
                startDate.plusHours(1).plusMinutes(16),                     // 12:16
                startDate.plusHours(1).plusMinutes(31),                     // 12:31
                startDate.plusHours(1).plusMinutes(46)                      // 12:46
            );
    }

    @Test
    void givenThatAvailabilityCouldExceedSlotEndDateAndOverlapWithExistingAppointment_whenGenerateAvailabilities_thenCreated() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        LocalDateTime startDate = LocalDateTime.of(2026, Month.FEBRUARY, 5, 11, 0, 0);

        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
            patient_id,
            startDate.plusMinutes(20),
            startDate.plusMinutes(35)));
        // 11:00 - 12:00
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(), startDate, startDate.plusHours(1)));

        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
            patient_id,
            startDate.plusMinutes(60),
            startDate.plusMinutes(75)));

        List<Availability> availabilities = proAvailabilityService.generateAvailabilities(practitioner.getId());

        assertThat(availabilities).hasSize(2)
            .extracting(Availability::getStartDate)
            .containsExactly(
                startDate,                  // 11:00
                startDate.plusMinutes(35)   // 11:35
            );
    }

    @Test
    void givenThatAvailabilityCouldExceedSlotEndDateAndOverlapWithExistingAvailability_whenGenerateAvailabilities_thenCreated() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        LocalDateTime startDate = LocalDateTime.of(2026, Month.FEBRUARY, 5, 11, 0, 0);

        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
            patient_id,
            startDate.plusMinutes(20),
            startDate.plusMinutes(35)));
        // 11:00 - 12:00
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(), startDate, startDate.plusHours(1)));

        availabilityRepository.save(entityFactory.createAvailability(practitioner.getId(),
            startDate.plusMinutes(60),
            startDate.plusMinutes(75)));

        List<Availability> availabilities = proAvailabilityService.generateAvailabilities(practitioner.getId());

        assertThat(availabilities)
            .hasSize(2)
            .extracting(Availability::getStartDate)
            .containsExactly(
                startDate,                  // 11:00
                startDate.plusMinutes(35)   // 11:35
            );
    }
}
