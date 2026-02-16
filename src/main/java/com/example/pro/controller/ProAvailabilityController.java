package com.example.pro.controller;

import com.example.pro.dto.mapper.AvailabilityMapper;
import com.example.pro.dto.response.AvailabilityDTO;
import com.example.pro.model.AvailabilityStatus;
import com.example.pro.service.ProAvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/availabilities", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
public class ProAvailabilityController {
    private final ProAvailabilityService proAvailabilityService;

    @Operation(description = "Get availabilities by practitionerId")
    @GetMapping
    public List<AvailabilityDTO> getAvailabilities(@Positive @RequestParam final Integer practitionerId) {
        return proAvailabilityService.findByPractitionerId(practitionerId)
            .stream().filter(availability -> AvailabilityStatus.FREE == availability.getStatus())
            .map(AvailabilityMapper.INSTANCE::toDTO).toList();
    }
}
