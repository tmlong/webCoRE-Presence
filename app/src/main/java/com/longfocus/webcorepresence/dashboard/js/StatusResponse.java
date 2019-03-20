package com.longfocus.webcorepresence.dashboard.js;

import com.longfocus.webcorepresence.ParseUtils;

public class StatusResponse {

    private String dni; // host device id
    private String s; // presence sensor id

    public String getDni() {
        return dni;
    }

    public void setDni(final String dni) {
        this.dni = dni;
    }

    public String getS() {
        return s;
    }

    public void setS(final String s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return ParseUtils.toJson(this);
    }
}
