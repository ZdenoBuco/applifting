package com.example.applifting.utilities;

import com.example.applifting.exceptions.AppliftingException;
import com.example.applifting.models.InDTOs.AppUserRegistrationInDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppUserRegistrationInDTOValidatorTest {
    private final static String VALID_USERNAME = "validUsername";
    private final static String VALID_EMAIL = "valid.email@example.com";

    @Test
    void validate_withValidInput_doesNotThrowException() {
        AppUserRegistrationInDTO validUser = new AppUserRegistrationInDTO();
        validUser.setUsername(VALID_USERNAME);
        validUser.setEmail(VALID_EMAIL);

        assertDoesNotThrow(() -> AppUserRegistrationInDTOValidator.validate(validUser));
    }

    @Test
    void validate_withNullUsername_throwsException() {
        AppUserRegistrationInDTO userWithNullUsername = new AppUserRegistrationInDTO();
        userWithNullUsername.setUsername(null);
        userWithNullUsername.setEmail(VALID_EMAIL);

        assertThrows(AppliftingException.class, () -> AppUserRegistrationInDTOValidator.validate(userWithNullUsername));
    }

    @Test
    void validate_withTooLongUsername_throwsException() {
        AppUserRegistrationInDTO userWithLongUsername = new AppUserRegistrationInDTO();
        userWithLongUsername.setUsername("a".repeat(AppUserRegistrationInDTOValidator.USERNAME_MAX_LENGTH+1));
        userWithLongUsername.setEmail(VALID_EMAIL);

        assertThrows(AppliftingException.class, () -> AppUserRegistrationInDTOValidator.validate(userWithLongUsername));
    }

    @Test
    void validate_withNullEmail_throwsException() {
        AppUserRegistrationInDTO userWithNullEmail = new AppUserRegistrationInDTO();
        userWithNullEmail.setUsername(VALID_USERNAME);
        userWithNullEmail.setEmail(null);

        assertThrows(AppliftingException.class, () -> AppUserRegistrationInDTOValidator.validate(userWithNullEmail));
    }

    @Test
    void validate_withTooLongEmail_throwsException() {
        AppUserRegistrationInDTO userWithLongEmail = new AppUserRegistrationInDTO();
        userWithLongEmail.setUsername(VALID_USERNAME);
        userWithLongEmail.setEmail("a".repeat(AppUserRegistrationInDTOValidator.EMAIL_MAX_LENGTH-11) + "@example.com");

        assertThrows(AppliftingException.class, () -> AppUserRegistrationInDTOValidator.validate(userWithLongEmail));
    }

    @Test
    void validate_withInvalidEmailFormat_throwsException() {
        AppUserRegistrationInDTO userWithInvalidEmail = new AppUserRegistrationInDTO();
        userWithInvalidEmail.setUsername(VALID_USERNAME);
        userWithInvalidEmail.setEmail("invalid-email@");

        assertThrows(AppliftingException.class, () -> AppUserRegistrationInDTOValidator.validate(userWithInvalidEmail));
    }
}