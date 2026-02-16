package com.example.pro.service;

import com.example.pro.entity.Appointment;
import com.example.pro.exception.AppointmentOverlapExisted;
import com.example.pro.exception.AvailabilityNotFound;
import com.example.pro.exception.PatientNotFound;
import com.example.pro.exception.PractitionerNotFound;
import com.example.pro.model.AppointmentRequest;
import com.example.pro.model.AppointmentStatus;
import com.example.pro.model.AvailabilityStatus;
import com.example.pro.repository.AppointmentRepository;
import com.example.pro.repository.AvailabilityRepository;
import com.example.pro.repository.PatientRepository;
import com.example.pro.repository.PractitionerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProAppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PractitionerRepository practitionerRepository;
    private final PatientRepository patientRepository;
    private final AvailabilityRepository availabilityRepository;

    /**
     * Finds an appointment by its ID.
     *
     * @param appointmentId the appointment ID
     * @return the appointment optional
     */
    public Optional<Appointment> find(long appointmentId) {
        return appointmentRepository.findById(appointmentId);
    }

    /**
     * Retrieves all appointments.
     *
     * @return list of all appointments
     */
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    /**
     * Retrieves all appointments for a given practitioner.
     *
     * @param practitionerId the practitioner's ID
     * @return list of appointments for the practitioner
     */
    public List<Appointment> findByPractitionerId(int practitionerId) {
        return appointmentRepository.findByPractitionerId(practitionerId);
    }

    /**
     * Validates that both the practitioner and the patient exist.
     *
     * @param practitionerId the practitioner's ID
     * @param patientId      the patient's ID
     * @throws PractitionerNotFound if the practitioner does not exist
     * @throws PatientNotFound      if the patient does not exist
     */
    private void validateParticipants(int practitionerId, int patientId) {
        if (!practitionerRepository.existsById(practitionerId)) {
            throw new PractitionerNotFound("Practitioner %s not found".formatted(practitionerId));
        }

        // Lock to avoid 2 appointments in the same range and patient but different practitioner
        patientRepository.findForUpdateById(patientId).orElseThrow(
            () -> new PatientNotFound("Patient %s not found".formatted(patientId)));
    }

    /**
     * Ensures no overlapping booked appointment exists for the same patient
     * within the requested time range.
     *
     * @param request the appointment request containing patient ID and date range
     * @throws AppointmentOverlapExisted if an overlapping appointment is found
     */
    private void ensureNoOverlapAppointments(AppointmentRequest request) {
        var overlapping = appointmentRepository.findOverlappingAppointments(
            request.patientId(), request.startDate(), request.endDate(), AppointmentStatus.BOOKED
        );
        if (!overlapping.isEmpty()) throw new AppointmentOverlapExisted("Other appointment conflicts with time range");
    }

    /**
     * Creates a new appointment after validating participants, checking for
     * overlaps, and securing a free availability slot. Marks the matching
     * availability as BOOKED on success.
     *
     * @param request the appointment request
     * @return the persisted appointment
     * @throws PractitionerNotFound      if the practitioner does not exist
     * @throws PatientNotFound           if the patient does not exist
     * @throws AppointmentOverlapExisted if an overlapping appointment exists
     * @throws AvailabilityNotFound      if no matching free availability slot is found
     */
    @Transactional
    public Appointment createAppointment(AppointmentRequest request) {
        validateParticipants(request.practitionerId(), request.patientId());

        // Lock to avoid 2 appointments in the same range and practitioner
        var availability = availabilityRepository.findForUpdate(
            request.practitionerId(), request.startDate(), request.endDate(), AvailabilityStatus.FREE
        ).orElseThrow(() -> new AvailabilityNotFound("Availability not found"));

        ensureNoOverlapAppointments(request);

        availability.markAsBooked();
        availabilityRepository.save(availability);

        var appointment = Appointment.builder()
            .practitionerId(request.practitionerId())
            .patientId(request.patientId())
            .startDate(request.startDate())
            .endDate(request.endDate())
            .build();
        var bookedAppointment = appointmentRepository.save(appointment);

        log.info("Created appointment successfully {}", request);
        return bookedAppointment;
    }
}
