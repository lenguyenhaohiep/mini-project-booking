package com.example.pro.repository;

import com.example.pro.entity.Availability;
import com.example.pro.model.AvailabilityStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailabilityRepository extends MongoRepository<Availability, String>, AvailabilityRepositoryCustom {
    List<Availability> findByPractitionerId(String practitionerId);
    List<Availability> findByPractitionerIdAndStatus(String practitionerId, AvailabilityStatus status);
    @Query("{ 'practitioner_id': ?0, 'start_date': { $gte: ?1, $lte: ?2 } }")
    List<Availability> findByPractitionerIdAndStartDateBetween(String practitionerId, Instant start, Instant end);
}