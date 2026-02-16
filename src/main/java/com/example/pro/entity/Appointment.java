package com.example.pro.entity;

import com.example.pro.model.AppointmentStatus;
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
@Table(name = "appointment")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "patient_id")
    private Integer patientId;

    @Column(name = "practitioner_id")
    private Integer practitionerId;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.BOOKED;

    public TimeRange getTimeRange() {
        return new TimeRange(startDate, endDate);
    }

    @PrePersist
    @PreUpdate
    private void validate() {
        if (patientId == null || patientId < 1) {
            throw new IllegalArgumentException("patientId must be positive");
        }
        if (practitionerId == null || practitionerId < 1) {
            throw new IllegalArgumentException("practitionerId must be positive");
        }
        if (startDate == null || endDate == null || !startDate.isBefore(endDate)) {
            throw new IllegalArgumentException("startDate must be before endDate");
        }
    }
}
