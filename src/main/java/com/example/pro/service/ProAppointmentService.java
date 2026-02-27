package com.example.pro.service;

import com.example.pro.entity.Appointment;
import com.example.pro.exception.AppointmentOverlapExistedException;
import com.example.pro.exception.AvailabilityNotFoundException;
import com.example.pro.exception.PatientNotFoundException;
import com.example.pro.exception.PractitionerNotFoundException;
import com.example.pro.model.AppointmentRequest;
import com.example.pro.model.AppointmentStatus;
import com.example.pro.repository.AppointmentRepository;
import com.example.pro.repository.AvailabilityRepository;
import com.example.pro.repository.PatientRepository;
import com.example.pro.repository.PractitionerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

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
    private final DistributedLockService distributedLockService;
    private final PlatformTransactionManager transactionManager;

    /**
     * Finds an appointment by its ID.
     *
     * @param appointmentId the appointment ID
     * @return the appointment optional
     */
    public Optional<Appointment> find(String appointmentId) {
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
    public List<Appointment> findByPractitionerId(String practitionerId) {
        return appointmentRepository.findByPractitionerId(practitionerId);
    }

    /**
     * Validates that both the practitioner and the patient exist.
     *
     * @param practitionerId the practitioner's ID
     * @param patientId      the patient's ID
     * @throws PractitionerNotFoundException if the practitioner does not exist
     * @throws PatientNotFoundException      if the patient does not exist
     */
    private void validateParticipants(String practitionerId, String patientId) {
        if (!practitionerRepository.existsById(practitionerId)) {
            throw new PractitionerNotFoundException("Practitioner %s not found".formatted(practitionerId));
        }
        patientRepository.findById(patientId).orElseThrow(
            () -> new PatientNotFoundException("Patient %s not found".formatted(patientId)));
    }

    /**
     * Ensures no overlapping booked appointment exists for the same patient
     * within the requested time range.
     *
     * @param request the appointment request containing patient ID and date range
     * @throws AppointmentOverlapExistedException if an overlapping appointment is found
     */
    private void ensureNoOverlapAppointments(AppointmentRequest request) {
        var overlapping = appointmentRepository.findOverlappingAppointments(
            request.patientId(), request.startDate(), request.endDate(), AppointmentStatus.BOOKED
        );
        if (!overlapping.isEmpty()) throw new AppointmentOverlapExistedException("Other appointment conflicts with time range");
    }


    /**
     * Handles the core appointment creation logic within a transaction.
     * Validates participants, marks the availability as unavailable, checks for
     * overlapping appointments, and persists the new appointment.
     *
     * @param request the appointment request containing patient, practitioner, and time range
     * @return the saved appointment
     * @throws PractitionerNotFoundException      if the practitioner does not exist
     * @throws PatientNotFoundException           if the patient does not exist
     * @throws AvailabilityNotFoundException      if no matching availability is found
     * @throws AppointmentOverlapExistedException if the patient already has a conflicting appointment
     */
    private Appointment processAppointmentRequest(AppointmentRequest request) {
        validateParticipants(request.practitionerId(), request.patientId());

        // lock availability
        var availability = availabilityRepository.findAndMarkAsUnavailableWithLock(
            request.practitionerId(), request.startDate(), request.endDate()
        );
        if (availability == null) {
            throw new AvailabilityNotFoundException("Availability not found");
        }

        ensureNoOverlapAppointments(request);

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

    /**
     * Creates a new appointment.
     * <p>
     * A distributed lock keyed on the patient is acquired before the transaction starts
     * and released after it commits, serialising concurrent booking attempts for the
     * same patient across all pods.
     */
    public Appointment createAppointment(AppointmentRequest request) {
        // lock patient
        String lockResource = "patient:" + request.patientId();
        distributedLockService.acquire(lockResource);
        // lock and appointment process have to be in the different transaction
        try {
            return new TransactionTemplate(transactionManager).execute((status) -> processAppointmentRequest(request));
        } finally {
            distributedLockService.release(lockResource);
        }
    }
}
