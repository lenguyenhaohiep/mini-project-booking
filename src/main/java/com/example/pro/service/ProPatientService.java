package com.example.pro.service;

import com.example.pro.entity.Patient;
import com.example.pro.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProPatientService {
    @Autowired
    private PatientRepository patientRepository;

    public Patient find(String patientId) {
        return patientRepository.findById(patientId).orElseThrow();
    }

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }
}
