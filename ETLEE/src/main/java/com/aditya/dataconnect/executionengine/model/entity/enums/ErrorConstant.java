package com.aditya.dataconnect.executionengine.model.entity.enums;

import com.aditya.dataconnect.executionengine.constant.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorConstant {
    JOB_ID_ERROR("Error processing the argument 'job_id'. No property found ", Constants.BAD_REQUEST),
    JOB_ID_UPDATE_ERROR("Data is already there in DB ", Constants.BAD_REQUEST),
    JOB_WRITE_ERROR("Error while writing into DB ", Constants.BAD_REQUEST),
    JOB_READ_ERROR("Error while reading from DB ", Constants.BAD_REQUEST),
    FILE_READ_ERROR("Error while reading file ", Constants.BAD_REQUEST),
    DATASOURCE_NOT_FOUND("Error while connecting DB ", Constants.BAD_REQUEST),
    METAOBJECT_NOT_FOUND("MetaObject not found", Constants.BAD_REQUEST),
    JOB_NOT_FOUND("DataSync Job not found", Constants.BAD_REQUEST),
    PRIMARY_KEY_ERROR("Primary key violation", Constants.BAD_REQUEST),
    HOST_REGION_NOT_FOUND("Host region not found",Constants.NOT_FOUND ),
    DATABASE_TYPE_NOT_FOUND("Database type not found",Constants.BAD_REQUEST ),
    INVALID_EXEC_LEG("Could not find regional job",Constants.BAD_REQUEST);
    private String message;
    private String code;

}