package com.aditya.ETLExecutionEngine.exception;

public class DataSyncJobException extends Exception {
    private final int code;

    public DataSyncJobException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

