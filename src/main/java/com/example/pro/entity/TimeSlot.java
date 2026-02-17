package com.example.pro.entity;

import com.example.pro.exception.InvalidStateChangeException;
import com.example.pro.model.TimeRange;
import com.example.pro.model.TimeSlotStatus;
import com.example.pro.utils.Validator;
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
@Table(name = "time_slot")
public class TimeSlot {
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
    private TimeSlotStatus status = TimeSlotStatus.NEW;

    public void markAsPlanned() {
        if (TimeSlotStatus.PLANNED == status) {
            throw new InvalidStateChangeException("Cannot plan a processed timeslot");
        }
        setStatus(TimeSlotStatus.PLANNED);
    }

    public TimeRange getTimeRange() {
        return new TimeRange(startDate, endDate);
    }

    @PrePersist
    @PreUpdate
    private void validate() {
        Validator.validateValidId(practitionerId, "practitionerId");
        Validator.validateValidRange(startDate, endDate, "startDate", "endDate");
    }
}
