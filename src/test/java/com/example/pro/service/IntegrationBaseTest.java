package com.example.pro.service;

import com.example.pro.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public class IntegrationBaseTest {
    @Autowired
    public ProAppointmentService proAppointmentService;

    @Autowired
    public AppointmentRepository appointmentRepository;

    @Autowired
    public AvailabilityRepository availabilityRepository;

    @Autowired
    public PractitionerRepository practitionerRepository;

    @Autowired
    public PatientRepository patientRepository;

    @Autowired
    public TimeSlotRepository timeSlotRepository;

    @Autowired
    public ProAvailabilityService proAvailabilityService;

    @BeforeEach
    public void setUp() {
        appointmentRepository.deleteAll();
        availabilityRepository.deleteAll();
        timeSlotRepository.deleteAll();
        patientRepository.deleteAll();
        practitionerRepository.deleteAll();
    }
}
