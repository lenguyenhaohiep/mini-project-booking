package com.example.pro.dto.mapper;

import com.example.pro.dto.request.AppointmentRequestDTO;
import com.example.pro.dto.response.AppointmentDTO;
import com.example.pro.entity.Appointment;
import com.example.pro.model.AppointmentRequest;
import org.mapstruct.Mapper;
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
    AppointmentDTO toDTO(Appointment appointment);

    /**
     * Converts an {@link AppointmentRequestDTO} to an {@link com.example.pro.model.AppointmentRequest} entity.
     * The {@code id} field is ignored as it will be generated on persist.
     *
     * @param appointmentRequestDTO the request to convert
     * @return the corresponding entity
     */
    AppointmentRequest fromDTO(AppointmentRequestDTO appointmentRequestDTO);
}
