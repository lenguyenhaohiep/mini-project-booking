package com.example.pro.dto.mapper;

import com.example.pro.dto.response.PatientDTO;
import com.example.pro.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PatientMapper {
    PatientMapper INSTANCE = Mappers.getMapper(PatientMapper.class);
    PatientDTO toDTO(Patient patient);
}
