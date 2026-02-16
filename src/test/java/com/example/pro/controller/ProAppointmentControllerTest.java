package com.example.pro.controller;

import com.example.pro.EntityFactory;
import com.example.pro.entity.Availability;
import com.example.pro.entity.Patient;
import com.example.pro.entity.Practitioner;
import com.example.pro.repository.AppointmentRepository;
import com.example.pro.repository.AvailabilityRepository;
import com.example.pro.repository.PatientRepository;
import com.example.pro.repository.PractitionerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.Month;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProAppointmentControllerTest {

    private final EntityFactory entityFactory = new EntityFactory();
    private final LocalDateTime startDate = LocalDateTime.of(2020, Month.FEBRUARY, 5, 11, 0, 0);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PractitionerRepository practitionerRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Test
    void givenValidRequest_whenCreateAppointment_thenReturns201() throws Exception {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        Patient patient = patientRepository.save(Patient.builder().firstName("John").lastName("Doe").build());
        availabilityRepository.save(
            Availability.builder()
                .practitionerId(practitioner.getId())
                .startDate(startDate)
                .endDate(startDate.plusMinutes(15))
                .build()
        );

        String json = """
            {
                "patientId": %d,
                "practitionerId": %d,
                "startDate": "2020-02-05T11:00:00",
                "endDate": "2020-02-05T11:15:00"
            }
            """.formatted(patient.getId(), practitioner.getId());

        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.patientId").value(patient.getId()))
            .andExpect(jsonPath("$.practitionerId").value(practitioner.getId()))
            .andExpect(jsonPath("$.startDate").exists())
            .andExpect(jsonPath("$.endDate").exists());
    }

    @Test
    void givenNullPatientId_whenCreateAppointment_thenReturns400() throws Exception {
        String json = """
            {
                "practitionerId": 1,
                "startDate": "2020-02-05T11:00:00",
                "endDate": "2020-02-05T11:15:00"
            }
            """;

        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
    }

    @Test
    void givenNullPractitionerId_whenCreateAppointment_thenReturns400() throws Exception {
        String json = """
            {
                "patientId": 1,
                "startDate": "2020-02-05T11:00:00",
                "endDate": "2020-02-05T11:15:00"
            }
            """;

        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
    }

    @Test
    void givenNegativePatientId_whenCreateAppointment_thenReturns400() throws Exception {
        String json = """
            {
                "patientId": -1,
                "practitionerId": 1,
                "startDate": "2020-02-05T11:00:00",
                "endDate": "2020-02-05T11:15:00"
            }
            """;

        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
    }

    @Test
    void givenNullStartDate_whenCreateAppointment_thenReturns400() throws Exception {
        String json = """
            {
                "patientId": 1,
                "practitionerId": 1,
                "endDate": "2020-02-05T11:15:00"
            }
            """;

        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
    }

    @Test
    void givenInvalidDateFormat_whenCreateAppointment_thenReturns400() throws Exception {
        String json = """
            {
                "patientId": 1,
                "practitionerId": 1,
                "startDate": "05/02/2020 11:00",
                "endDate": "05/02/2020 11:15"
            }
            """;

        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
    }

    @Test
    void givenInvalidPractitionerId_whenCreateAppointment_thenReturns400() throws Exception {
        Patient patient = patientRepository.save(Patient.builder().firstName("John").lastName("Doe").build());

        String json = """
            {
                "patientId": %d,
                "practitionerId": 999999,
                "startDate": "2020-02-05T11:00:00",
                "endDate": "2020-02-05T11:15:00"
            }
            """.formatted(patient.getId());

        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
    }

    @Test
    void givenNoAvailability_whenCreateAppointment_thenReturns400() throws Exception {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        Patient patient = patientRepository.save(Patient.builder().firstName("John").lastName("Doe").build());

        String json = """
            {
                "patientId": %d,
                "practitionerId": %d,
                "startDate": "2020-02-05T11:00:00",
                "endDate": "2020-02-05T11:15:00"
            }
            """.formatted(patient.getId(), practitioner.getId());

        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
    }

    @Test
    void givenEmptyBody_whenCreateAppointment_thenReturns400() throws Exception {
        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void givenPatientHasSameTimeRangeWithOtherPractitioner_whenCreateAppointment_thenReturns409() throws Exception {
        Practitioner practitionerA = practitionerRepository.save(entityFactory.createPractitioner());
        Practitioner practitionerB = practitionerRepository.save(entityFactory.createPractitioner());
        Patient patient = patientRepository.save(Patient.builder().firstName("John").lastName("Doe").build());

        // patient already has appointment with practitioner A at [11:00, 11:15]
        appointmentRepository.save(entityFactory.createAppointment(
            practitionerA.getId(), patient.getId(), startDate, startDate.plusMinutes(15)
        ));

        // practitioner B has availability at the same time range
        availabilityRepository.save(
            Availability.builder()
                .practitionerId(practitionerB.getId())
                .startDate(startDate)
                .endDate(startDate.plusMinutes(15))
                .build()
        );

        // try to book same patient, same time range, different practitioner
        String json = """
            {
                "patientId": %d,
                "practitionerId": %d,
                "startDate": "2020-02-05T11:00:00",
                "endDate": "2020-02-05T11:15:00"
            }
            """.formatted(patient.getId(), practitionerB.getId());

        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isConflict());
    }

    @Test
    void givenPatientHasOverlappingTimeRangeWithOtherPractitioner_whenCreateAppointment_thenReturns409() throws Exception {
        Practitioner practitionerA = practitionerRepository.save(entityFactory.createPractitioner());
        Practitioner practitionerB = practitionerRepository.save(entityFactory.createPractitioner());
        Patient patient = patientRepository.save(Patient.builder().firstName("John").lastName("Doe").build());

        // patient already has appointment with practitioner A at [11:00, 11:15]
        appointmentRepository.save(entityFactory.createAppointment(
            practitionerA.getId(), patient.getId(), startDate, startDate.plusMinutes(15)
        ));

        // practitioner B has availability at overlapping time range [11:10, 11:25]
        availabilityRepository.save(
            Availability.builder()
                .practitionerId(practitionerB.getId())
                .startDate(startDate.plusMinutes(10))
                .endDate(startDate.plusMinutes(25))
                .build()
        );

        // try to book same patient, overlapping time range, different practitioner
        String json = """
            {
                "patientId": %d,
                "practitionerId": %d,
                "startDate": "2020-02-05T11:10:00",
                "endDate": "2020-02-05T11:25:00"
            }
            """.formatted(patient.getId(), practitionerB.getId());

        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isConflict());
    }
}