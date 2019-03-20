package com.longfocus.webcorepresence.dashboard.js;

import com.longfocus.webcorepresence.ParseUtils;

public class Register {

    private String e; // raw endpoint
    private String a; // raw access token
    private String i; // instance id
    private String d; // device id

    public String getE() {
        return e;
    }

    public void setE(final String e) {
        this.e = e;
    }

    public String getA() {
        return a;
    }

    public void setA(final String a) {
        this.a = a;
    }

    public String getI() {
        return i;
    }

    public void setI(final String i) {
        this.i = i;
    }

    public String getD() {
        return d;
    }

    public void setD(final String d) {
        this.d = d;
    }

    @Override
    public String toString() {
        return ParseUtils.toJson(this);
    }
}
