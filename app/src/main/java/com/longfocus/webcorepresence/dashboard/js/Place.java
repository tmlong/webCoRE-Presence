package com.longfocus.webcorepresence.dashboard.js;

import android.support.annotation.NonNull;

import com.google.android.gms.location.Geofence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Place implements Serializable {

    private static final String REQUEST_ID_FORMAT = "%s|%s|%s";

    private static final int GEOFENCE_EXPIRATION = -1;
    private static final int GEOFENCE_TRANSITIONS = Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT;

    enum Location {

        INNER("i"),
        OUTER("o");

        private final String type;

        Location(final String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

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

    @NonNull
    public List<Geofence> getGeofences(@NonNull final String instanceId) {
        final List<Geofence> geofences = new ArrayList<>();

        if (hasPosition()) {
            final double[] position = getP();
            final double latitude = position[0];
            final double longitude = position[1];

            geofences.add(new Geofence.Builder().setRequestId(getRequestId(instanceId, Location.INNER)).setCircularRegion(latitude, longitude, getI()).setExpirationDuration(GEOFENCE_EXPIRATION).setTransitionTypes(GEOFENCE_TRANSITIONS).build());
            geofences.add(new Geofence.Builder().setRequestId(getRequestId(instanceId, Location.OUTER)).setCircularRegion(latitude, longitude, getO()).setExpirationDuration(GEOFENCE_EXPIRATION).setTransitionTypes(GEOFENCE_TRANSITIONS).build());
        }

        return geofences;
    }

    private boolean hasPosition() {
        final double[] position = getP();
        return (position != null && position.length >= 2);
    }

    private String getRequestId(final String instanceId, final Location location) {
        return String.format(REQUEST_ID_FORMAT, instanceId, getId(), location.getType());
    }
}
