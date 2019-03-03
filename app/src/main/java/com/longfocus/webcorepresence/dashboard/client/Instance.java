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

    // ignored
    private Object account;
    private Object contacts;
    private Object devices;
    private Object pistons;
    private Object lifx;
    private Object virtualDevices;
    private Object globalVars;

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

    public Object getAccount() {
        return account;
    }

    public void setAccount(final Object account) {
        this.account = account;
    }

    public Object getContacts() {
        return contacts;
    }

    public void setContacts(final Object contacts) {
        this.contacts = contacts;
    }

    public Object getDevices() {
        return devices;
    }

    public void setDevices(Object devices) {
        this.devices = devices;
    }

    public Object getPistons() {
        return pistons;
    }

    public void setPistons(final Object pistons) {
        this.pistons = pistons;
    }

    public Object getLifx() {
        return lifx;
    }

    public void setLifx(final Object lifx) {
        this.lifx = lifx;
    }

    public Object getVirtualDevices() {
        return virtualDevices;
    }

    public void setVirtualDevices(final Object virtualDevices) {
        this.virtualDevices = virtualDevices;
    }

    public Object getGlobalVars() {
        return globalVars;
    }

    public void setGlobalVars(final Object globalVars) {
        this.globalVars = globalVars;
    }
}
