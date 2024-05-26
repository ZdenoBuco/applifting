package com.example.applifting.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AppliftingException extends RuntimeException {
    private Integer statusCode;

    public AppliftingException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
