package com.example.pro.entity;

import com.example.pro.exception.InvalidStateChangeException;
import com.example.pro.model.AvailabilityStatus;
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
@Table(name = "availability")
public class Availability {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "practitioner_id")
    private Integer practitionerId;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "startDate", column = @Column(name = "start_date")),
        @AttributeOverride(name = "endDate", column = @Column(name = "end_date"))
    })
    private TimeRange timeRange;

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


    @PrePersist
    @PreUpdate
    private void validate() {
        Validator.validateValidId(practitionerId, "practitionerId");
    }
}
