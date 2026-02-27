package com.example.pro.repository;

import com.example.pro.entity.Appointment;
import com.example.pro.model.AppointmentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {
    List<Appointment> findAll();

    List<Appointment> findByPatientId(String patientId);

    List<Appointment> findByPractitionerId(String practitionerId);

    @Query("{ 'practitioner_id': ?0, 'start_date': { $gte: ?1, $lte: ?2 }, 'status': ?3 }")
    List<Appointment> findByPractitionerIdAndStartDateBetweenAndStatus(String practitionerId, Instant start,
                                                                       Instant end,
                                                                       AppointmentStatus status);

    List<Appointment> findByPatientIdAndEndDateGreaterThanAndStartDateLessThanAndStatus(String patientId,
                                                                                        Instant start,
                                                                                        Instant end,
                                                                                        AppointmentStatus status);

    default List<Appointment> findOverlappingAppointments(String patientId, Instant start, Instant end,
                                                  AppointmentStatus status) {
        return findByPatientIdAndEndDateGreaterThanAndStartDateLessThanAndStatus(
            patientId, start, end, status
        );
    }
}
