package com.example.pro.configuration;

import com.example.pro.entity.Patient;
import com.example.pro.entity.Practitioner;
import com.example.pro.entity.TimeSlot;
import com.example.pro.model.TimeRange;
import com.example.pro.repository.PatientRepository;
import com.example.pro.repository.PractitionerRepository;
import com.example.pro.repository.TimeSlotRepository;
import com.example.pro.service.ProAvailabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class CommandLineStartupRunner implements CommandLineRunner {
    private final PatientRepository patientRepository;
    private final PractitionerRepository practitionerRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final ProAvailabilityService proAvailabilityService;

    @Override
    public void run(String... args) {
        //initialise data
        for (int i = 1; i <= 5; i++) {
            String speciality ="orthodontist";
            //create patient
            patientRepository.save(Patient.builder().firstName("patient_" + i).lastName("example").build());
            //create practitioner
            Practitioner practitioner = practitionerRepository.save(Practitioner.builder().firstName("practitioner" + i).lastName("example").build());
            //create timeSlots for practitioner
            //timeslot from 2021/02/08 at 8H to 2021/02/08 at 12H
            TimeSlot timeSlot1 = TimeSlot.builder().timeRange(new TimeRange(Instant.parse("2021-02-08T08:00:00Z"),
                    Instant.parse("2021-02-08T12:00:00Z"))).practitionerId(practitioner.getId()).build();
            //timeslot from 2021/02/08 at 14H to 2021/02/08 at 17H
            TimeSlot timeSlot2 = TimeSlot.builder().timeRange(new TimeRange(Instant.parse("2021-02-08T14:00:00Z"),
                    Instant.parse("2021-02-08T17:00:00Z"))).practitionerId(practitioner.getId()).build();
            //timeslot from 2021/02/09 at 9H to 2021/02/09 at 17H
            TimeSlot timeSlot3 = TimeSlot.builder().timeRange(new TimeRange(Instant.parse("2021-02-09T09:00:00Z"),
                    Instant.parse("2021-02-09T17:00:00Z"))).practitionerId(practitioner.getId()).build();
            List<TimeSlot> timeSlotList = new ArrayList<>(Arrays.asList(timeSlot1, timeSlot2, timeSlot3));
            if (i % 2 == 0) {
                //timeslot from 2021/02/10 at 9H to 2021/02/10 at 16H
                TimeSlot timeSlot4 = TimeSlot.builder().timeRange(new TimeRange(Instant.parse("2021-02-10T09:00:00Z"),
                        Instant.parse("2021-02-10T16:00:00Z"))).practitionerId(practitioner.getId()).build();
                timeSlotList.add(timeSlot4);
                speciality="general practitioner";
            }
            if (i == 3) {
                //timeslot from 2021/02/11 at 11H to 2021/02/11 at 18H
                TimeSlot timeSlot5 = TimeSlot.builder().timeRange(new TimeRange(Instant.parse("2021-02-11T11:00:00Z"),
                        Instant.parse("2021-02-11T18:00:00Z"))).practitionerId(practitioner.getId()).build();
                timeSlotList.add(timeSlot5);
                speciality="dentist";
            }
            practitioner.setSpeciality(speciality);
            practitioner = practitionerRepository.save(practitioner);
            timeSlotRepository.saveAll(timeSlotList);

            proAvailabilityService.generateAvailabilities(practitioner.getId());
        }
        log.info("------------------created patients---------------- " + patientRepository.findAll());
        log.info("------------------created practitioners---------------- " + practitionerRepository.findAll());
        log.info("------------------created timeSlots---------------- " + timeSlotRepository.findAll());
    }
}
