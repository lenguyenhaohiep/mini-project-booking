package com.example.pro.entity;

import com.example.pro.model.AppointmentStatus;
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
@Document(collection = "appointment")
public class Appointment {
    @Id
    private String id;

    @Field("patient_id")
    private String patientId;

    @Field("practitioner_id")
    private String practitionerId;

    @Field("start_date")
    private Instant startDate;

    @Field("end_date")
    private Instant endDate;

    @Field("status")
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.BOOKED;

    public TimeRange getTimeRange() {
        return new TimeRange(startDate, endDate);
    }
}