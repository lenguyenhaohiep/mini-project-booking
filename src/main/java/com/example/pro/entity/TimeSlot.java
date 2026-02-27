package com.example.pro.entity;

import com.example.pro.exception.InvalidStateChangeException;
import com.example.pro.model.TimeRange;
import com.example.pro.model.TimeSlotStatus;
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
@Document(collection = "time_slot")
public class TimeSlot {
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
}