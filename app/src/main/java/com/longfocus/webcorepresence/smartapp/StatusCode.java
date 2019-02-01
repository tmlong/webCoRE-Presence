package com.longfocus.webcorepresence.smartapp;

public enum StatusCode {

    // Success
    SUCCESS("ST_SUCCESS"),

    // Errors
    COULD_NOT_CREATE_DEVICE("ERR_COULD_NOT_CREATE_DEVICE"),
    INVALID_TOKEN("ERR_INVALID_TOKEN");

    private final String code;

    StatusCode(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
