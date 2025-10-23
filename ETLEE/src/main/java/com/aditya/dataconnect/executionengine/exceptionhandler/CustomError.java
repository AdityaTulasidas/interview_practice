package com.aditya.dataconnect.executionengine.exceptionhandler;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CustomError extends RuntimeException {

    private final String message;
    private final String code;

    public CustomError(String message, String code) {
        this.message = message;
        this.code = code;
    }
}
