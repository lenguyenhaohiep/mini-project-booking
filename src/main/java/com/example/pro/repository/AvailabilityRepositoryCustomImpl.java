package com.example.pro.repository;

import com.example.pro.entity.Availability;
import com.example.pro.model.AvailabilityStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public class AvailabilityRepositoryCustomImpl implements AvailabilityRepositoryCustom {
    private final MongoTemplate mongoTemplate;

    // findAndModify to have lock on the availability
    public Availability findAndMarkAsUnavailableWithLock(String practitionerId, Instant startDate, Instant endDate) {
        Query query = new Query(
            Criteria.where("practitioner_id").is(practitionerId)
                .and("start_date").is(startDate)
                .and("end_date").is(endDate)
                .and("status").is(AvailabilityStatus.FREE)
        );
        Update update = new Update().set("status", AvailabilityStatus.UNAVAILABLE);
        try {
            return mongoTemplate.findAndModify(query, update, Availability.class);
        } catch (Exception e) {
            log.warn("Error marking availability as unavailable: {}", e.getMessage());
            return null;
        }
    }
}
