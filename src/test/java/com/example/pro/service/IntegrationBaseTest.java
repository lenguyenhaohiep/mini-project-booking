package com.example.pro.service;

import com.example.pro.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public class IntegrationBaseTest {
    @Autowired
    protected ProAppointmentService proAppointmentService;

    @Autowired
    protected AppointmentRepository appointmentRepository;

    @Autowired
    protected AvailabilityRepository availabilityRepository;

    @Autowired
    protected PractitionerRepository practitionerRepository;

    @Autowired
    protected PatientRepository patientRepository;

    @Autowired
    protected TimeSlotRepository timeSlotRepository;

    @Autowired
    protected ProAvailabilityService proAvailabilityService;

    @BeforeEach
    public void setUp() {
        appointmentRepository.deleteAll();
        availabilityRepository.deleteAll();
        timeSlotRepository.deleteAll();
        patientRepository.deleteAll();
        practitionerRepository.deleteAll();
    }
}
