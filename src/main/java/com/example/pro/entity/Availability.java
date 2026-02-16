package com.example.pro.entity;

import com.example.pro.exception.InvalidStateChangeException;
import com.example.pro.model.AvailabilityStatus;
import com.example.pro.model.TimeRange;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "availability")
public class Availability {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "practitioner_id")
    private Integer practitionerId;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private AvailabilityStatus status = AvailabilityStatus.FREE;

    public void markAsUnavailable() {
        if (AvailabilityStatus.UNAVAILABLE == status) {
            throw new InvalidStateChangeException("Cannot process unavailable availability");
        }
        setStatus(AvailabilityStatus.UNAVAILABLE);
    }

    public TimeRange getTimeRange() {
        return new TimeRange(startDate, endDate);
    }

    @PrePersist
    @PreUpdate
    private void validate() {
        if (practitionerId == null || practitionerId < 1) {
            throw new IllegalArgumentException("practitionerId must be positive");
        }
        if (startDate == null || endDate == null || !startDate.isBefore(endDate)) {
            throw new IllegalArgumentException("startDate must be before endDate");
        }
    }
}
