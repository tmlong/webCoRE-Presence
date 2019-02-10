package com.longfocus.webcorepresence.dashboard.js;

import java.io.Serializable;

public class Place implements Serializable {

    private String id; // place id
    private String n; // name
    private boolean h; // home flag
    private float i; // inner radius (meters)
    private float o; // outer radius (meters)
    private double[] p; // position (latitude, longitude)
    private Meta meta;
    private String $$hashKey;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getN() {
        return n;
    }

    public void setN(final String n) {
        this.n = n;
    }

    public boolean isH() {
        return h;
    }

    public void setH(final boolean h) {
        this.h = h;
    }

    public float getI() {
        return i;
    }

    public void setI(final float i) {
        this.i = i;
    }

    public float getO() {
        return o;
    }

    public void setO(final float o) {
        this.o = o;
    }

    public double[] getP() {
        return p;
    }

    public void setP(final double[] p) {
        this.p = p;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(final Meta meta) {
        this.meta = meta;
    }

    public String get$$hashKey() {
        return $$hashKey;
    }

    public void set$$hashKey(final String $$hashKey) {
        this.$$hashKey = $$hashKey;
    }
}
