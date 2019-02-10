package com.longfocus.webcorepresence.dashboard;

import java.io.Serializable;

public class Place implements Serializable {

    private String id;
    private String name;
    private boolean home;
    private float innerRadiusMeters;
    private float outerRadiusMeters;
    private double[] position; // (latitude, longitude)

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isHome() {
        return home;
    }

    public void setHome(final boolean home) {
        this.home = home;
    }

    public float getInnerRadiusMeters() {
        return innerRadiusMeters;
    }

    public void setInnerRadiusMeters(final float innerRadiusMeters) {
        this.innerRadiusMeters = innerRadiusMeters;
    }

    public float getOuterRadiusMeters() {
        return outerRadiusMeters;
    }

    public void setOuterRadiusMeters(final float outerRadiusMeters) {
        this.outerRadiusMeters = outerRadiusMeters;
    }

    public double[] getPosition() {
        return position;
    }

    public void setPosition(final double[] position) {
        this.position = position;
    }
}
