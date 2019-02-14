package com.longfocus.webcorepresence.smartapp.response;

public class Error {

    private String name;
    private StatusCode error;
    private long now;

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

    public long getNow() {
        return now;
    }

    public void setNow(final long now) {
        this.now = now;
    }
}
