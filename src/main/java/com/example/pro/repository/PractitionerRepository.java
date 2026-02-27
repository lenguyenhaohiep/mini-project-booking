package com.example.pro.repository;

import com.example.pro.entity.Practitioner;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PractitionerRepository extends MongoRepository<Practitioner, String> {
    List<Practitioner> findAll();
    Optional<Practitioner> findById(String id);
}