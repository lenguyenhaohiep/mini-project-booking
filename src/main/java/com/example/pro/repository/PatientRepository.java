package com.example.pro.repository;

import com.example.pro.entity.Patient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends CrudRepository<Patient, Integer> {
    List<Patient> findAll();
    Optional<Patient> findById(int integer);
}
