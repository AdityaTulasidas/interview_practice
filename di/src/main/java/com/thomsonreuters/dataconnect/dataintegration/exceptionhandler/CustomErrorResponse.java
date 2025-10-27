package com.thomsonreuters.dataconnect.dataintegration.exceptionhandler;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CustomErrorResponse {


    Error error;

    public CustomErrorResponse(Error error) {
        this.error = error;
    }
}
