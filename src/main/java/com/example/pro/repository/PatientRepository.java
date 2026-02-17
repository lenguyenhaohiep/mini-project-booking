package com.example.pro.repository;

import com.example.pro.entity.Patient;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends CrudRepository<Patient, Integer> {
    List<Patient> findAll();
    Optional<Patient> findById(int integer);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Patient> findForUpdateById(int integer);
}
