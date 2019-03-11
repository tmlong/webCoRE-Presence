package com.longfocus.webcorepresence.dashboard.client;

public class Load {

    private String name;
    private Instance instance;
    private long now;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(final Instance instance) {
        this.instance = instance;
    }

    public long getNow() {
        return now;
    }

    public void setNow(final long now) {
        this.now = now;
    }
}
