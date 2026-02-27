package com.example.pro.repository;

import com.example.pro.entity.TimeSlot;
import com.example.pro.model.TimeSlotStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeSlotRepository extends MongoRepository<TimeSlot, String> {
    List<TimeSlot> findByPractitionerId(String practitionerId);
    List<TimeSlot> findByPractitionerIdAndStatusIn(String practitionerId, List<TimeSlotStatus> statuses);
}