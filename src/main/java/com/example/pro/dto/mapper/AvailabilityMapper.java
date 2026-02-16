package com.example.pro.dto.mapper;

import com.example.pro.dto.response.AvailabilityDTO;
import com.example.pro.entity.Availability;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AvailabilityMapper {
    AvailabilityMapper INSTANCE = Mappers.getMapper(AvailabilityMapper.class);

    AvailabilityDTO toDTO(Availability availability);
}
