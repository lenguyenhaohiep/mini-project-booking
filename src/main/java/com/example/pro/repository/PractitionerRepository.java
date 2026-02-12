package com.example.pro.repository;

import com.example.pro.entity.Practitioner;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PractitionerRepository extends CrudRepository<Practitioner, String> {
    List<Practitioner> findAll();

}
