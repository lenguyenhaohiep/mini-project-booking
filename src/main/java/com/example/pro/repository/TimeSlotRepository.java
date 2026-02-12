package com.example.pro.repository;

import com.example.pro.entity.TimeSlot;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeSlotRepository extends CrudRepository<TimeSlot, String> {
    List<TimeSlot> findByPractitionerId(Integer practitionerId);
}
