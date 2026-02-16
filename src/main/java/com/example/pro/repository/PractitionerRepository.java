package com.example.pro.repository;

import com.example.pro.entity.Practitioner;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PractitionerRepository extends CrudRepository<Practitioner, Integer> {
    List<Practitioner> findAll();
    Optional<Practitioner> findById(int id);
}
