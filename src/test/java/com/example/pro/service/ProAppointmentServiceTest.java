package com.example.pro.service;

import com.example.pro.EntityFactory;
import com.example.pro.entity.Appointment;
import com.example.pro.entity.Availability;
import com.example.pro.entity.Patient;
import com.example.pro.entity.Practitioner;
import com.example.pro.exception.AppointmentOverlapExisted;
import com.example.pro.exception.AvailabilityNotFound;
import com.example.pro.exception.PatientNotFound;
import com.example.pro.exception.PractitionerNotFound;
import com.example.pro.model.AppointmentRequest;
import com.example.pro.model.AvailabilityStatus;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ProAppointmentServiceTest extends IntegrationBaseTest {

    private final EntityFactory entityFactory = new EntityFactory();

    private final LocalDateTime startDate = LocalDateTime.of(2026, Month.FEBRUARY, 5, 11, 0, 0);

    @Test
    void givenAppointmentExists_whenFindById_thenReturn() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        Patient patient = patientRepository.save(Patient.builder().firstName("John").lastName("Doe").build());
        var appointment = appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
            patient.getId(),
            startDate,
            startDate.plusMinutes(15)));

        assertThat(proAppointmentService.find(appointment.getId())).isPresent();
    }

    @Test
    void givenValidData_whenCreateAppointment_thenSavesAndDeletesAvailability() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        Patient patient = patientRepository.save(Patient.builder().firstName("John").lastName("Doe").build());
        Availability availability = availabilityRepository.save(
            Availability.builder()
                .practitionerId(practitioner.getId())
                .startDate(startDate)
                .endDate(startDate.plusMinutes(15))
                .build()
        );
        assertThat(availability.getStatus()).isEqualTo(AvailabilityStatus.FREE);

        var request = new AppointmentRequest(patient.getId(),
            practitioner.getId(),
            startDate,
            startDate.plusMinutes(15));

        Appointment saved = proAppointmentService.createAppointment(request);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPractitionerId()).isEqualTo(practitioner.getId());
        assertThat(saved.getPatientId()).isEqualTo(patient.getId());
        assertThat(saved.getStartDate()).isEqualTo(startDate);
        assertThat(saved.getEndDate()).isEqualTo(startDate.plusMinutes(15));

        // availability should be booked after booking
        assertThat(availabilityRepository.findById(availability.getId()).get().getStatus())
            .isEqualTo(AvailabilityStatus.UNAVAILABLE);
    }

    @Test
    void givenInvalidPractitionerId_whenCreateAppointment_thenThrowsPractitionerNotFound() {
        var request = new AppointmentRequest(1, 999999, startDate, startDate.plusMinutes(15));

        assertThatThrownBy(() -> proAppointmentService.createAppointment(request))
            .isInstanceOf(PractitionerNotFound.class);
    }

    @Test
    void givenInvalidPatientId_whenCreateAppointment_thenThrowsPatientNotFound() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());

        var request = new AppointmentRequest(999999, practitioner.getId(), startDate, startDate.plusMinutes(15));

        assertThatThrownBy(() -> proAppointmentService.createAppointment(request))
            .isInstanceOf(PatientNotFound.class);
    }

    @Test
    void givenDuplicateAppointment_whenCreateAppointment_thenThrowsAppointmentAlreadyExisted() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        Patient patient = patientRepository.save(Patient.builder().firstName("John").lastName("Doe").build());
        availabilityRepository.save(
            Availability.builder()
                .practitionerId(practitioner.getId())
                .startDate(startDate)
                .endDate(startDate.plusMinutes(15))
                .build()
        );

        // create first appointment
        var first = new AppointmentRequest(patient.getId(), practitioner.getId(), startDate, startDate.plusMinutes(15));
        proAppointmentService.createAppointment(first);

        // attempt duplicate
        var duplicate = new AppointmentRequest(patient.getId(),
            practitioner.getId(),
            startDate,
            startDate.plusMinutes(15));

        assertThatThrownBy(() -> proAppointmentService.createAppointment(duplicate))
            .isInstanceOf(AppointmentOverlapExisted.class);
    }

    @Test
    void givenNoAvailability_whenCreateAppointment_thenThrowsAvailabilityNotFound() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        Patient patient = patientRepository.save(Patient.builder().firstName("John").lastName("Doe").build());

        var request = new AppointmentRequest(patient.getId(),
            practitioner.getId(),
            startDate,
            startDate.plusMinutes(15));

        assertThatThrownBy(() -> proAppointmentService.createAppointment(request))
            .isInstanceOf(AvailabilityNotFound.class);
    }

    @Test
    void givenExistingAppointment_whenFindByPractitionerId_thenReturnsAppointments() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        Patient patient = patientRepository.save(Patient.builder().firstName("John").lastName("Doe").build());
        appointmentRepository.save(entityFactory.createAppointment(
            practitioner.getId(), patient.getId(), startDate, startDate.plusMinutes(15)
        ));

        List<Appointment> result = proAppointmentService.findByPractitionerId(practitioner.getId());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getPractitionerId()).isEqualTo(practitioner.getId());
    }

    @Test
    void givenExistingAppointments_whenFindAll_thenReturnsAll() {
        long countBefore = appointmentRepository.count();
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        Patient patient = patientRepository.save(Patient.builder().firstName("John").lastName("Doe").build());
        appointmentRepository.save(entityFactory.createAppointment(
            practitioner.getId(), patient.getId(), startDate, startDate.plusMinutes(15)
        ));

        List<Appointment> result = proAppointmentService.findAll();

        assertThat(result).hasSize((int) (countBefore + 1));
    }

    @Test
    void givenConcurrentBookings_whenCreateAppointment_thenOnlyOneCreated() throws Exception {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        Patient patientA = patientRepository.save(Patient.builder().firstName("John").lastName("Doe").build());
        Patient patientB = patientRepository.save(Patient.builder().firstName("James").lastName("Smith").build());
        Availability availability = availabilityRepository.save(
            Availability.builder()
                .practitionerId(practitioner.getId())
                .startDate(startDate)
                .endDate(startDate.plusMinutes(15))
                .build()
        );

        AtomicReference<RuntimeException> caughtException = new AtomicReference<>();

        Thread vt1 = Thread.startVirtualThread(() -> {
            try {
                var request = new AppointmentRequest(patientA.getId(),
                    practitioner.getId(),
                    startDate,
                    startDate.plusMinutes(15));
                proAppointmentService.createAppointment(request);
            } catch (RuntimeException e) {
                caughtException.set(e);
            }
        });
        Thread vt2 = Thread.startVirtualThread(() -> {
            try {
                var request = new AppointmentRequest(patientB.getId(),
                    practitioner.getId(),
                    startDate,
                    startDate.plusMinutes(15));
                proAppointmentService.createAppointment(request);
            } catch (RuntimeException e) {
                caughtException.set(e);
            }
        });

        vt1.join();
        vt2.join();

        assertThat(appointmentRepository.findByPractitionerId(practitioner.getId())).hasSize(1);
        assertThat(caughtException.get()).isNotNull().isInstanceOf(AvailabilityNotFound.class);
        assertThat(availabilityRepository.findById(availability.getId()))
            .isPresent().get().extracting(Availability::getStatus).isEqualTo(AvailabilityStatus.UNAVAILABLE);
    }

    @Test
    void givenRuntimeException_whenCreateAppointment_thenRollbackTransaction() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        Patient patient = patientRepository.save(Patient.builder().firstName("John").lastName("Doe").build());
        Availability availability = availabilityRepository.save(
            Availability.builder()
                .practitionerId(practitioner.getId())
                .startDate(startDate)
                .endDate(startDate.plusMinutes(15))
                .build()
        );

        long appointmentCountBefore = appointmentRepository.count();

        var request = new AppointmentRequest(patient.getId(),
            practitioner.getId(),
            startDate,
            startDate.plusMinutes(15));

        try (MockedStatic<Appointment> mockedAppointment = Mockito.mockStatic(Appointment.class,
            Mockito.CALLS_REAL_METHODS)) {
            Appointment.AppointmentBuilder builderMock = Mockito.mock(Appointment.AppointmentBuilder.class);
            mockedAppointment.when(Appointment::builder).thenReturn(builderMock);
            Mockito.when(builderMock.build()).thenThrow(new RuntimeException("Build failed"));

            assertThatThrownBy(() -> proAppointmentService.createAppointment(request))
                .isInstanceOf(RuntimeException.class);

            // Verify rollback: appointment was not persisted
            assertThat(appointmentRepository.count()).isEqualTo(appointmentCountBefore);
            // Verify rollback: availability still exists
            assertThat(availabilityRepository.findById(availability.getId()))
                .isPresent().get().extracting(Availability::getStatus).isEqualTo(AvailabilityStatus.FREE);
        }
    }
}