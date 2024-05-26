package com.example.applifting.models.InDTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppUserRegistrationInDTO {
    private String username;
    private String email;
}
