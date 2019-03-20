package com.longfocus.webcorepresence.dashboard.js;

import com.longfocus.webcorepresence.ParseUtils;

public class Update {

    private String i; // instance id
    private Place[] p; // places

    public String getI() {
        return i;
    }

    public void setI(final String i) {
        this.i = i;
    }

    public Place[] getP() {
        return p;
    }

    public void setP(final Place[] p) {
        this.p = p;
    }

    @Override
    public String toString() {
        return ParseUtils.toJson(this);
    }
}
