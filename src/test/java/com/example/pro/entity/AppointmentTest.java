package com.example.pro.entity;

import com.example.pro.exception.TimeRangeInvalidException;
import com.example.pro.repository.AppointmentRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class AppointmentTest {

    @Nested
    @SpringBootTest
    class JpaValidationTest {
        @Autowired
        private AppointmentRepository appointmentRepository;

        @Test
        void givenInvalidAppointment_whenPersist_thenJpaTriggersValidation() {
            var appointment = Appointment.builder()
                .patientId(-1)
                .practitionerId(1)
                .startDate(Instant.parse("2021-02-10T09:00:00Z"))
                .endDate(Instant.parse("2021-02-10T09:15:00Z"))
                .build();
            var exception = assertThrows(Exception.class, () -> appointmentRepository.save(appointment));
            assertInstanceOf(IllegalArgumentException.class, exception.getCause());
            assertEquals("patientId must be positive", exception.getCause().getMessage());
        }
    }

    @Nested
    class UnitValidationTest {

        private void invokeValidate(Appointment appointment) throws Exception {
            Method validate = Appointment.class.getDeclaredMethod("validate");
            validate.setAccessible(true);
            try {
                validate.invoke(appointment);
            } catch (InvocationTargetException e) {
                throw (Exception) e.getCause();
            }
        }

        @Test
        void givenValidAppointment_whenValidate_thenNoException() {
            var appointment = Appointment.builder()
                .patientId(1)
                .practitionerId(1)
                .startDate(Instant.parse("2021-02-10T09:00:00Z"))
                .endDate(Instant.parse("2021-02-10T09:15:00Z"))
                .build();
            assertDoesNotThrow(() -> invokeValidate(appointment));
        }

        @Test
        void givenNullPatientId_whenValidate_thenThrowsException() {
            var appointment = Appointment.builder()
                .practitionerId(1)
                .startDate(Instant.parse("2021-02-10T09:00:00Z"))
                .endDate(Instant.parse("2021-02-10T09:15:00Z"))
                .build();
            var exception = assertThrows(IllegalArgumentException.class, () -> invokeValidate(appointment));
            assertEquals("patientId must be positive", exception.getMessage());
        }

        @Test
        void givenNegativePatientId_whenValidate_thenThrowsException() {
            var appointment = Appointment.builder()
                .patientId(-1)
                .practitionerId(1)
                .startDate(Instant.parse("2021-02-10T09:00:00Z"))
                .endDate(Instant.parse("2021-02-10T09:15:00Z"))
                .build();
            var exception = assertThrows(IllegalArgumentException.class, () -> invokeValidate(appointment));
            assertEquals("patientId must be positive", exception.getMessage());
        }

        @Test
        void givenNullPractitionerId_whenValidate_thenThrowsException() {
            var appointment = Appointment.builder()
                .patientId(1)
                .startDate(Instant.parse("2021-02-10T09:00:00Z"))
                .endDate(Instant.parse("2021-02-10T09:15:00Z"))
                .build();
            var exception = assertThrows(IllegalArgumentException.class, () -> invokeValidate(appointment));
            assertEquals("practitionerId must be positive", exception.getMessage());
        }

        @Test
        void givenNegativePractitionerId_whenValidate_thenThrowsException() {
            var appointment = Appointment.builder()
                .patientId(1)
                .practitionerId(-1)
                .startDate(Instant.parse("2021-02-10T09:00:00Z"))
                .endDate(Instant.parse("2021-02-10T09:15:00Z"))
                .build();
            var exception = assertThrows(IllegalArgumentException.class, () -> invokeValidate(appointment));
            assertEquals("practitionerId must be positive", exception.getMessage());
        }

        @Test
        void givenNullStartDate_whenValidate_thenThrowsException() {
            var appointment = Appointment.builder()
                .patientId(1)
                .practitionerId(1)
                .endDate(Instant.parse("2021-02-10T09:15:00Z"))
                .build();
            var exception = assertThrows(IllegalArgumentException.class, () -> invokeValidate(appointment));
            assertEquals("Dates cannot be null", exception.getMessage());
        }

        @Test
        void givenNullEndDate_whenValidate_thenThrowsException() {
            var appointment = Appointment.builder()
                .patientId(1)
                .practitionerId(1)
                .startDate(Instant.parse("2021-02-10T09:00:00Z"))
                .build();
            var exception = assertThrows(IllegalArgumentException.class, () -> invokeValidate(appointment));
            assertEquals("Dates cannot be null", exception.getMessage());
        }

        @Test
        void givenStartDateAfterEndDate_whenValidate_thenThrowsException() {
            var appointment = Appointment.builder()
                .patientId(1)
                .practitionerId(1)
                .startDate(Instant.parse("2021-02-10T10:00:00Z"))
                .endDate(Instant.parse("2021-02-10T09:00:00Z"))
                .build();
            var exception = assertThrows(TimeRangeInvalidException.class, () -> invokeValidate(appointment));
            assertEquals("startDate must be before endDate", exception.getMessage());
        }

        @Test
        void givenStartDateEqualsEndDate_whenValidate_thenThrowsException() {
            var instant = Instant.parse("2021-02-10T09:00:00Z");
            var appointment = Appointment.builder()
                .patientId(1)
                .practitionerId(1)
                .startDate(instant)
                .endDate(instant)
                .build();
            var exception = assertThrows(TimeRangeInvalidException.class, () -> invokeValidate(appointment));
            assertEquals("startDate must be before endDate", exception.getMessage());
        }
    }
}