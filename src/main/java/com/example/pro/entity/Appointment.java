package com.example.pro.entity;

import com.example.pro.model.AppointmentStatus;
import com.example.pro.model.TimeRange;
import com.example.pro.utils.Validator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "startDate", column = @Column(name = "start_date")),
        @AttributeOverride(name = "endDate", column = @Column(name = "end_date"))
    })
    private TimeRange timeRange;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.BOOKED;

    @PrePersist
    @PreUpdate
    private void validate() {
        Validator.validateValidId(patientId, "patientId");
        Validator.validateValidId(practitionerId, "practitionerId");
    }
}
