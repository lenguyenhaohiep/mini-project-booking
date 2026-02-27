package com.example.pro.entity;

import com.example.pro.exception.InvalidStateChangeException;
import com.example.pro.model.AvailabilityStatus;
import com.example.pro.model.TimeRange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "availability")
public class Availability {
    @Id
    private String id;

    @Field("practitioner_id")
    private String practitionerId;

    @Field("start_date")
    private Instant startDate;

    @Field("end_date")
    private Instant endDate;

    @Field("status")
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
}