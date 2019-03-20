package com.longfocus.webcorepresence.dashboard.js;

import com.longfocus.webcorepresence.ParseUtils;

import java.io.Serializable;

public class Meta implements Serializable {

    private double d;
    private boolean p;

    public double getD() {
        return d;
    }

    public void setD(final double d) {
        this.d = d;
    }

    public boolean isP() {
        return p;
    }

    public void setP(final boolean p) {
        this.p = p;
    }

    @Override
    public String toString() {
        return ParseUtils.toJson(this);
    }
}
