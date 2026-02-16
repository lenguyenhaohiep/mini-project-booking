package com.example.pro;

import com.example.pro.entity.Availability;
import com.github.javafaker.Faker;
import com.example.pro.entity.Appointment;
import com.example.pro.entity.Practitioner;
import com.example.pro.entity.TimeSlot;

import java.time.LocalDateTime;
import java.util.Locale;

public class EntityFactory {
    final Faker faker = new Faker(Locale.FRANCE);

    public Practitioner createPractitioner() {
        return Practitioner.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .build();
    }

    public TimeSlot createTimeSlot(Integer practitionerId, LocalDateTime startDate, LocalDateTime endDate) {
        return TimeSlot.builder()
                .practitionerId(practitionerId)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    public Appointment createAppointment(Integer practitionerId, Integer patientId, LocalDateTime start, LocalDateTime end) {
        return Appointment.builder()
                .practitionerId(practitionerId)
                .patientId(patientId)
                .startDate(start)
                .endDate(end)
                .build();
    }

    public Availability createAvailability(Integer practitionerId, LocalDateTime start, LocalDateTime end) {
        return Availability.builder()
            .practitionerId(practitionerId)
            .startDate(start)
            .endDate(end)
            .build();
    }
}
