package com.example.applifting.servicies;

import com.example.applifting.exceptions.AppliftingException;
import com.example.applifting.models.AppUser;
import com.example.applifting.models.InDTOs.AppUserRegistrationInDTO;
import com.example.applifting.models.OutDTOs.AuthenticationOutDTO;
import com.example.applifting.repositories.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {
    private final AppUserRepository appUserRepository;

    public AuthenticationOutDTO create(AppUserRegistrationInDTO appUserDto) {

        if (appUserRepository.existsAppUserByEmail(appUserDto.getEmail())) {
            throw new AppliftingException("A user with this email already exists. Please choose a different email.", 400);
        }

        String accessToken = UUID.randomUUID().toString();

        appUserRepository.save(AppUser.builder()
                .id(UUID.randomUUID())
                .username(appUserDto.getUsername())
                .email(appUserDto.getEmail())
                .accessToken(accessToken)
                .build());
        return AuthenticationOutDTO.builder().accessToken(accessToken).build();
    }
}
