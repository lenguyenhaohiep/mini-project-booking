package com.example.pro.dto.mapper;

import com.example.pro.dto.response.AvailabilityDTO;
import com.example.pro.entity.Availability;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AvailabilityMapper {
    AvailabilityMapper INSTANCE = Mappers.getMapper(AvailabilityMapper.class);

    @Mapping(source = "timeRange.startDate", target = "startDate")
    @Mapping(source = "timeRange.endDate", target = "endDate")
    AvailabilityDTO toDTO(Availability availability);
}
