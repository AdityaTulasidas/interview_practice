package com.thomsonreuters.metadataregistry.exceptionhandler;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Error {

    private String message;
    private String code;

    public Error(String message, String code) {
        this.message = message;
        this.code = code;
    }
    public Error() {
    }

}
