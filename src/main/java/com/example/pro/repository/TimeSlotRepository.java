package com.example.pro.repository;

import com.example.pro.entity.TimeSlot;
import com.example.pro.model.TimeSlotStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeSlotRepository extends CrudRepository<TimeSlot, Long> {
    List<TimeSlot> findByPractitionerId(int practitionerId);
    List<TimeSlot> findByPractitionerIdAndStatusIn(int practitionerId, List<TimeSlotStatus> statuses);
}
