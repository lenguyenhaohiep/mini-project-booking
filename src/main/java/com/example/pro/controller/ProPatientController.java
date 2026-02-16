package com.example.pro.controller;

import com.example.pro.dto.mapper.PatientMapper;
import com.example.pro.dto.response.PatientDTO;
import com.example.pro.service.ProPatientService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/patients", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ProPatientController {
    private final ProPatientService proPatientService;

    @Operation(description = "Get patients")
    @GetMapping
    public List<PatientDTO> getPatients() {
        return proPatientService.findAll().stream().map(PatientMapper.INSTANCE::toDTO).toList();
    }
}
