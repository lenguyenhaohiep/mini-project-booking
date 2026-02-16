package com.example.pro.dto.mapper;

import com.example.pro.dto.response.PractitionerDTO;
import com.example.pro.entity.Practitioner;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PractitionerMapper {
    PractitionerMapper INSTANCE = Mappers.getMapper(PractitionerMapper.class);

    PractitionerDTO toDTO(Practitioner practitioner);
}
