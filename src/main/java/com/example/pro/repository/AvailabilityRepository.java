package com.example.pro.repository;

import com.example.pro.entity.Availability;
import com.example.pro.model.AvailabilityStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailabilityRepository extends CrudRepository<Availability, Integer> {
    List<Availability> findByPractitionerId(int id);
    List<Availability> findByPractitionerIdAndStartDateBetween(int id, LocalDateTime start, LocalDateTime end);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Availability> findForUpdateByPractitionerIdAndStartDateAndEndDateAndStatus(
        int practitionerId, LocalDateTime startDate, LocalDateTime endDate, AvailabilityStatus status
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    default Optional<Availability> findForUpdate(
        int practitionerId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        AvailabilityStatus status
    ) {
        return findForUpdateByPractitionerIdAndStartDateAndEndDateAndStatus(
            practitionerId, startDate, endDate, status
        );
    }
}
