package com.example.pro.entity;

import com.example.pro.model.AvailabilityStatus;
import com.example.pro.model.TimeRange;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private AvailabilityStatus status = AvailabilityStatus.FREE;

    public void markAsBooked() {
        setStatus(AvailabilityStatus.UNAVAILABLE);
    }

    public TimeRange getTimeRange() {
        return new TimeRange(startDate, endDate);
    }
}
