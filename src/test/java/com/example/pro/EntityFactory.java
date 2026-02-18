package com.example.pro;

import com.example.pro.entity.Availability;
import com.example.pro.model.TimeRange;
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

    public TimeSlot createTimeSlot(Integer practitionerId, Instant startDate, Instant endDate) {
        return TimeSlot.builder()
                .practitionerId(practitionerId)
                .timeRange(new TimeRange(startDate, endDate))
                .build();
    }

    public Appointment createAppointment(Integer practitionerId, Integer patientId, Instant start, Instant end) {
        return Appointment.builder()
                .practitionerId(practitionerId)
                .patientId(patientId)
                .timeRange(new TimeRange(start, end))
                .build();
    }

    public Availability createAvailability(Integer practitionerId, Instant start, Instant end) {
        return Availability.builder()
            .practitionerId(practitionerId)
            .timeRange(new TimeRange(start, end))
            .build();
    }
}
