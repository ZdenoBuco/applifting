package com.example.applifting.utilities;

import com.example.applifting.exceptions.AppliftingException;
import com.example.applifting.models.InDTOs.AppUserRegistrationInDTO;

import java.util.regex.Pattern;

public class AppUserRegistrationInDTOValidator {
    private static int USERNAME_MAX_LENGTH = 255;
    private static int EMAIL_MAX_LENGTH = 255;
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    public static void validate(AppUserRegistrationInDTO userRegistrationInDTO) {
        if (userRegistrationInDTO.getUsername() == null || userRegistrationInDTO.getUsername().length() > USERNAME_MAX_LENGTH) {
            throw new AppliftingException("Username length cannot be null and must be up to " + USERNAME_MAX_LENGTH + " characters long", 400);
        }
        if (userRegistrationInDTO.getEmail() == null || userRegistrationInDTO.getEmail().length() > EMAIL_MAX_LENGTH) {
                throw new AppliftingException("Email cannot be null and must be up to " + EMAIL_MAX_LENGTH + " characters long", 400);
        }
        if (!pattern.matcher(userRegistrationInDTO.getEmail()).matches()) {
            throw new AppliftingException("Invalid email address", 400);
        }
    }
}
