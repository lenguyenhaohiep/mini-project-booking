package com.example.pro.repository;

import com.example.pro.entity.Appointment;
import com.example.pro.model.AppointmentStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends CrudRepository<Appointment, Long> {
    List<Appointment> findAll();

    List<Appointment> findByPractitionerId(int practitionerId);

    List<Appointment> findByPractitionerIdAndStartDateBetweenAndStatus(int practitionerId, LocalDateTime start,
                                                                       LocalDateTime end,
                                                                       AppointmentStatus status);

    List<Appointment> findByPatientIdAndEndDateGreaterThanAndStartDateLessThanAndStatus(int patientId,
                                                                                        LocalDateTime date1,
                                                                                        LocalDateTime date2,
                                                                                        AppointmentStatus status);

    default List<Appointment> findOverlappingAppointments(int patientId, LocalDateTime start, LocalDateTime end,
                                                  AppointmentStatus status) {
        return findByPatientIdAndEndDateGreaterThanAndStartDateLessThanAndStatus(
            patientId, start, end, status
        );
    }
}
