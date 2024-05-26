package com.example.applifting.controllers;

import com.example.applifting.models.InDTOs.AppUserRegistrationInDTO;
import com.example.applifting.models.OutDTOs.AuthenticationOutDTO;
import com.example.applifting.servicies.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AppUserService appUserService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationOutDTO> signUpUser(@RequestBody AppUserRegistrationInDTO appUserRegistrationDTO) {
        return ResponseEntity.status(201).body(appUserService.create(appUserRegistrationDTO));
    }
}
