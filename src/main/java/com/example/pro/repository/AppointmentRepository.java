package com.example.pro.repository;

import com.example.pro.entity.Appointment;
import com.example.pro.model.AppointmentStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AppointmentRepository extends CrudRepository<Appointment, Long> {
    List<Appointment> findAll();

    List<Appointment> findByPatientId(int patientId);

    List<Appointment> findByPractitionerId(int practitionerId);

    List<Appointment> findByPractitionerIdAndTimeRange_StartDateBetweenAndStatus(int practitionerId, Instant start,
                                                                                 Instant end,
                                                                                 AppointmentStatus status);

    List<Appointment> findByPatientIdAndTimeRange_EndDateGreaterThanAndTimeRange_StartDateLessThanAndStatus(
        int patientId, Instant start, Instant end, AppointmentStatus status);

    default List<Appointment> findOverlappingAppointments(int patientId, Instant start, Instant end,
                                                          AppointmentStatus status) {
        return findByPatientIdAndTimeRange_EndDateGreaterThanAndTimeRange_StartDateLessThanAndStatus(
            patientId, start, end, status);
    }
}
