package com.longfocus.webcorepresence.smartapp;

public class ErrorResponse {

    private String name;
    private StatusCode error;
    private String now;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public StatusCode getError() {
        return error;
    }

    public void setError(final StatusCode error) {
        this.error = error  ;
    }

    public String getNow() {
        return now;
    }

    public void setNow(final String now) {
        this.now = now;
    }
}
