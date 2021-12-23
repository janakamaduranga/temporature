package com.qardio.sensor.temporature.exception;

public class TemperatureException extends RuntimeException{
    public static final int INVALID_DATE_FORMAT = 301;

    private final int errorCode;

    public TemperatureException(Throwable throwable,
                                String message,
                                int errorCode) {
        super(message, throwable);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
