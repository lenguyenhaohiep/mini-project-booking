package com.example.pro.repository;

import com.example.pro.entity.Availability;

import java.time.Instant;

public interface AvailabilityRepositoryCustom {
    Availability findAndMarkAsUnavailableWithLock(String practitionerId, Instant startDate, Instant endDate);
}
