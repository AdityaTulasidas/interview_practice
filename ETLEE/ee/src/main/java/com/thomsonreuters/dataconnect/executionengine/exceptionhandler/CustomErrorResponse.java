package com.thomsonreuters.dataconnect.executionengine.exceptionhandler;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CustomErrorResponse {

    CustomError customError;

    public CustomErrorResponse(CustomError customError) {
        this.customError = customError;
    }
}
