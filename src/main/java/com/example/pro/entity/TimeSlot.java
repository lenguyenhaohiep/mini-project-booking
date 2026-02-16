package com.example.pro.entity;

import com.example.pro.model.TimeRange;
import com.example.pro.model.TimeSlotStatus;
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
@Table(name = "time_slot")
public class TimeSlot {
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
    private TimeSlotStatus status = TimeSlotStatus.NEW;

    public void markAsPlanned() {
        setStatus(TimeSlotStatus.PLANNED);
    }

    public TimeRange getTimeRange() {
        return new TimeRange(startDate, endDate);
    }
}
