package com.example.pro.service;

import com.example.pro.entity.TimeSlot;
import com.example.pro.repository.TimeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProTimeSlotService {
    @Autowired
    private TimeSlotRepository timeSlotRepository;

    public List<TimeSlot> findByPractitionerId(Integer practitionerId) {
        return timeSlotRepository.findByPractitionerId(practitionerId);
    }
}
