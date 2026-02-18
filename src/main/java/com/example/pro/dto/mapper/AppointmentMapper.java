package com.example.pro.dto.mapper;

import com.example.pro.dto.request.AppointmentRequestDTO;
import com.example.pro.dto.response.AppointmentDTO;
import com.example.pro.entity.Appointment;
import com.example.pro.model.AppointmentRequest;
import com.example.pro.model.TimeRange;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * MapStruct mapper for converting between {@link Appointment} entity,
 * {@link AppointmentDTO} response, and {@link AppointmentRequestDTO} request.
 */
@Mapper
public interface AppointmentMapper {
    AppointmentMapper INSTANCE = Mappers.getMapper(AppointmentMapper.class);

    /**
     * Converts an {@link Appointment} entity to an {@link AppointmentDTO} response.
     *
     * @param appointment the entity to convert
     * @return the corresponding DTO
     */
    @Mapping(source = "timeRange.startDate", target = "startDate")
    @Mapping(source = "timeRange.endDate", target = "endDate")
    AppointmentDTO toDTO(Appointment appointment);

    /**
     * Converts an {@link AppointmentRequestDTO} to an {@link AppointmentRequest}.
     *
     * @param dto the request DTO to convert
     * @return the corresponding domain request
     */
    default AppointmentRequest fromDTO(AppointmentRequestDTO dto) {
        return new AppointmentRequest(
            dto.patientId(),
            dto.practitionerId(),
            new TimeRange(dto.startDate(), dto.endDate())
        );
    }
}
