package com.thomsonreuters.dataconnect.dataintegration.exceptionhandler;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DataSyncJobException extends Exception {

    private final String code;
    public DataSyncJobException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
