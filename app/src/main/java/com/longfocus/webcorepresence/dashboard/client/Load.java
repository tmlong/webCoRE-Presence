package com.longfocus.webcorepresence.dashboard.client;

public class Load {

    private String name;
    private long now;
    private Instance instance;

    // ignored
    private Object location;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public long getNow() {
        return now;
    }

    public void setNow(final long now) {
        this.now = now;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(final Instance instance) {
        this.instance = instance;
    }

    public Object getLocation() {
        return location;
    }

    public void setLocation(final Object location) {
        this.location = location;
    }
}
