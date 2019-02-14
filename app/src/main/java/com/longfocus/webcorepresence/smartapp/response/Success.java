package com.longfocus.webcorepresence.smartapp.response;

public class Success {

    private String deviceId;
    private StatusCode status;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
    }

    public StatusCode getStatus() {
        return status;
    }

    public void setStatus(final StatusCode status) {
        this.status = status;
    }
}
