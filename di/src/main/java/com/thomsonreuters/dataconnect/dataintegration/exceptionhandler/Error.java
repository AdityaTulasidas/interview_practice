package com.thomsonreuters.dataconnect.dataintegration.exceptionhandler;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Error extends RuntimeException {

    private final String message;
    private final String code;

    public Error(String message, String code) {
        this.message = message;
        this.code = code;
    }
}
