package com.example.pro.controller;

import com.example.pro.dto.mapper.AppointmentMapper;
import com.example.pro.dto.request.AppointmentRequestDTO;
import com.example.pro.dto.response.AppointmentDTO;
import com.example.pro.service.ProAppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/appointments", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
public class ProAppointmentController {
    private final ProAppointmentService proAppointmentService;

    @Operation(description = "Get appointments by practitionerId")
    @GetMapping("/{practitionerId}")
    public List<AppointmentDTO> getAppointmentsByPractitioner(@Positive @PathVariable final Integer practitionerId) {
        return proAppointmentService.findByPractitionerId(practitionerId)
            .stream().map(AppointmentMapper.INSTANCE::toDTO).toList();
    }

    @Operation(description = "Get all appointments")
    @GetMapping
    public List<AppointmentDTO> getAppointments() {
        return proAppointmentService.findAll()
            .stream().map(AppointmentMapper.INSTANCE::toDTO).toList();
    }

    @Operation(description = "Create an appointment")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentDTO createAppointment(@Valid @RequestBody final AppointmentRequestDTO payload) {
        var appointmentRequest = AppointmentMapper.INSTANCE.fromDTO(payload);
        var appointment = proAppointmentService.createAppointment(appointmentRequest);
        return AppointmentMapper.INSTANCE.toDTO(appointment);
    }
}
