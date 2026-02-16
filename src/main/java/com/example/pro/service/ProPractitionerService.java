package com.example.pro.service;

import com.example.pro.entity.Practitioner;
import com.example.pro.repository.PractitionerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProPractitionerService {
    private final PractitionerRepository practitionerRepository;

    public Optional<Practitioner> find(int practitionerId) {
        return practitionerRepository.findById(practitionerId);
    }

    public List<Practitioner> findAll() {
        return practitionerRepository.findAll();
    }
}
