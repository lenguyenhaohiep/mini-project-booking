package com.example.pro.service;

import com.example.pro.entity.TimeSlot;
import com.example.pro.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProTimeSlotService {
    private final TimeSlotRepository timeSlotRepository;

    public List<TimeSlot> findByPractitionerId(int practitionerId) {
        return timeSlotRepository.findByPractitionerId(practitionerId);
    }
}
