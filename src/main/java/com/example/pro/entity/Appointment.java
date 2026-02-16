package com.example.pro.entity;

import com.example.pro.model.AppointmentStatus;
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
@Table(name = "appointment")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "patient_id")
    private Integer patientId;

    @Column(name = "practitioner_id")
    private Integer practitionerId;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.BOOKED;

    public TimeRange getTimeRange() {
        return new TimeRange(startDate, endDate);
    }

}
