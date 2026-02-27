package com.example.pro.repository;

import com.example.pro.entity.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends MongoRepository<Patient, String> {
    List<Patient> findAll();
    Optional<Patient> findById(String id);
}