package com.example.pro.controller;

import com.example.pro.entity.Practitioner;
import com.example.pro.service.ProPractitionerService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/practitioners", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProPractitionerController {
    @Autowired
    private ProPractitionerService proPractitionerService;

    @Operation(description = "Get practitioners")
    @GetMapping
    public List<Practitioner> getPractitioners() {
        return proPractitionerService.findAll();
    }
}
