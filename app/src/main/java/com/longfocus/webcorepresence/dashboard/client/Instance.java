package com.longfocus.webcorepresence.dashboard.client;

public class Instance {

    private String id;
    private String locationId;
    private String name;
    private String uri;
    private String deviceVersion;
    private String coreVersion;
    private boolean enabled;
    private Settings settings;
    private String token;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(final String locationId) {
        this.locationId = locationId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(final String uri) {
        this.uri = uri;
    }

    public String getDeviceVersion() {
        return deviceVersion;
    }

    public void setDeviceVersion(final String deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    public String getCoreVersion() {
        return coreVersion;
    }

    public void setCoreVersion(final String coreVersion) {
        this.coreVersion = coreVersion;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(final Settings settings) {
        this.settings = settings;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }
}
