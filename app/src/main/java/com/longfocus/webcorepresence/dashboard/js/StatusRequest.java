package com.longfocus.webcorepresence.dashboard.js;

import com.longfocus.webcorepresence.ParseUtils;

public class StatusRequest {

    private String i; // instance id

    public String getI() {
        return i;
    }

    public void setI(final String i) {
        this.i = i;
    }

    @Override
    public String toString() {
        return ParseUtils.toJson(this);
    }
}
