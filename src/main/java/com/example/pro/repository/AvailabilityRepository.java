package com.example.pro.repository;

import com.example.pro.entity.Availability;
import com.example.pro.model.AvailabilityStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailabilityRepository extends CrudRepository<Availability, Long> {
    List<Availability> findByPractitionerId(int practitionerId);
    List<Availability> findByPractitionerIdAndStatus(int practitionerId, AvailabilityStatus status);
    List<Availability> findByPractitionerIdAndStartDateBetween(int practitionerId, Instant start, Instant end);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Availability> findForUpdateByPractitionerIdAndStartDateAndEndDateAndStatus(
        int practitionerId, Instant startDate, Instant endDate, AvailabilityStatus status
    );

    default Optional<Availability> findForUpdate(
        int practitionerId,
        Instant startDate,
        Instant endDate,
        AvailabilityStatus status
    ) {
        return findForUpdateByPractitionerIdAndStartDateAndEndDateAndStatus(
            practitionerId, startDate, endDate, status
        );
    }
}
