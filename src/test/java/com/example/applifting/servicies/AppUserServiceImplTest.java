package com.example.applifting.servicies;

import com.example.applifting.exceptions.AppliftingException;
import com.example.applifting.models.AppUser;
import com.example.applifting.models.InDTOs.AppUserRegistrationInDTO;
import com.example.applifting.repositories.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUserServiceImplTest {
    @InjectMocks
    private AppUserServiceImpl underTest;
    @Mock
    private AppUserRepository appUserRepository;
    private static final String USERNAME = "example user name";
    private static final String EMAIL = "example@email.com";
    private static final UUID ID = UUID.randomUUID();
    private static final String ACCESS_TOKEN = UUID.randomUUID().toString();

    private AppUserRegistrationInDTO appUserRegistrationInDTO;
    private AppUser expectedAppUser;
    private AppUser actualAppUser;


    @BeforeEach
    void setUp() {
        appUserRegistrationInDTO = AppUserRegistrationInDTO.builder()
                .username(USERNAME)
                .email(EMAIL)
                .build();
        expectedAppUser = AppUser.builder()
                .id(ID)
                .username(USERNAME)
                .email(EMAIL)
                .accessToken(ACCESS_TOKEN)
                .build();
        when(appUserRepository.existsAppUserByEmail(EMAIL)).thenReturn(false);
    }

    @Test
    void create_everythingIsOk_createsAppUser() {
        underTest.create(appUserRegistrationInDTO);

        getActualAppUser();
        actualAppUser.setId(ID);
        actualAppUser.setAccessToken(ACCESS_TOKEN);

        assertEquals(expectedAppUser, actualAppUser);
    }

    @Test
    void create_userWithEmailAlreadyExists_throwsException() {
        when(appUserRepository.existsAppUserByEmail(EMAIL)).thenReturn(true);

        AppliftingException exception = assertThrows(AppliftingException.class, () -> underTest.create(appUserRegistrationInDTO));
        assertEquals(exception.getMessage(), "A user with this email already exists. Please choose a different email.");
        assertEquals(400, exception.getStatusCode());

    }

    private void getActualAppUser() {
        ArgumentCaptor<AppUser> argumentCaptor = ArgumentCaptor.forClass(AppUser.class);
        Mockito.verify(appUserRepository).save(argumentCaptor.capture());
        actualAppUser = argumentCaptor.getValue();
    }
}