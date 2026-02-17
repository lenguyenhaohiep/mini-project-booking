package com.example.pro.service;

import com.example.pro.entity.Patient;
import com.example.pro.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProPatientService {
    private final PatientRepository patientRepository;

    public Optional<Patient> find(int patientId) {
        return patientRepository.findById(patientId);
    }

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }
}
