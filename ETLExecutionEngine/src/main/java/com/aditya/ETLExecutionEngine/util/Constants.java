package com.aditya.ETLExecutionEngine.util;

public class Constants {
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int NOT_FOUND = 404;

    public static final ErrorCode JOB_READ_ERROR = new ErrorCode("Job read error", 1001);
    public static final ErrorCode JOB_WRITE_ERROR = new ErrorCode("Job write error", 1002);
    public static final ErrorCode DATASOURCE_NOT_FOUND = new ErrorCode("Datasource not found", 1003);
    public static final ErrorCode DATABASE_TYPE_NOT_FOUND = new ErrorCode("Database type not found", 1004);

    public static final String LOOKUP = "LOOKUP";
    public static final String REFERENCE = "REFERENCE";
    public static final String INSERT_LIST_KEY = "INSERT_LIST";
    public static final String UPDATE_LIST_KEY = "UPDATE_LIST";
    public static final int BATCH_SIZE = 1000;

    public static class ErrorCode {
        private final String message;
        private final int code;

        public ErrorCode(String message, int code) {
            this.message = message;
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public int getCode() {
            return code;
        }
    }
}
