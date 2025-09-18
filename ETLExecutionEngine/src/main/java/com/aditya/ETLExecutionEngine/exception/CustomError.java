package com.aditya.ETLExecutionEngine.exception;

public class CustomError extends RuntimeException {
    private final int code;

    public CustomError(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

