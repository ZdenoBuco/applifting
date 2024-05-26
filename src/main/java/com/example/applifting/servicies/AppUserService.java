package com.example.applifting.servicies;

import com.example.applifting.models.InDTOs.AppUserRegistrationInDTO;
import com.example.applifting.models.OutDTOs.AuthenticationOutDTO;

public interface AppUserService {
    AuthenticationOutDTO create(AppUserRegistrationInDTO appUserDto);
}
