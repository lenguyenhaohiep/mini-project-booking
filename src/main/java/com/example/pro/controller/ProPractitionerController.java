package com.example.pro.controller;

import com.example.pro.dto.mapper.PractitionerMapper;
import com.example.pro.dto.response.PractitionerDTO;
import com.example.pro.service.ProPractitionerService;
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
@RequestMapping(value = "/practitioners", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ProPractitionerController {
    private final ProPractitionerService proPractitionerService;

    @Operation(description = "Get practitioners")
    @GetMapping
    public List<PractitionerDTO> getPractitioners() {
        return proPractitionerService.findAll().stream().map(PractitionerMapper.INSTANCE::toDTO).toList();
    }
}
