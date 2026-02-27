package com.example.pro;

import com.example.pro.entity.Availability;
import com.github.javafaker.Faker;
import com.example.pro.entity.Appointment;
import com.example.pro.entity.Practitioner;
import com.example.pro.entity.TimeSlot;

import java.time.Instant;
import java.util.Locale;

public class EntityFactory {
    final Faker faker = new Faker(Locale.FRANCE);

    public Practitioner createPractitioner() {
        return Practitioner.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .build();
    }

    public TimeSlot createTimeSlot(String practitionerId, Instant startDate, Instant endDate) {
        return TimeSlot.builder()
                .practitionerId(practitionerId)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    public Appointment createAppointment(String practitionerId, String patientId, Instant start, Instant end) {
        return Appointment.builder()
                .practitionerId(practitionerId)
                .patientId(patientId)
                .startDate(start)
                .endDate(end)
                .build();
    }

    public Availability createAvailability(String practitionerId, Instant start, Instant end) {
        return Availability.builder()
            .practitionerId(practitionerId)
            .startDate(start)
            .endDate(end)
            .build();
    }
}
