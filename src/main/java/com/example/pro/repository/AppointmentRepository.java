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

    List<Appointment> findByPractitionerIdAndStartDateBetweenAndStatus(int practitionerId, Instant start,
                                                                       Instant end,
                                                                       AppointmentStatus status);

    List<Appointment> findByPatientIdAndEndDateGreaterThanAndStartDateLessThanAndStatus(int patientId,
                                                                                        Instant start,
                                                                                        Instant end,
                                                                                        AppointmentStatus status);

    default List<Appointment> findOverlappingAppointments(int patientId, Instant start, Instant end,
                                                  AppointmentStatus status) {
        return findByPatientIdAndEndDateGreaterThanAndStartDateLessThanAndStatus(
            patientId, start, end, status
        );
    }
}
