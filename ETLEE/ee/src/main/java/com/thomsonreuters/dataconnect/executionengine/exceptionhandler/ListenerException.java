package com.thomsonreuters.dataconnect.executionengine.exceptionhandler;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ListenerException extends RuntimeException {
    private final String code;

    public ListenerException(String message, String code) {
        super(message);
        this.code = code;
    }
}