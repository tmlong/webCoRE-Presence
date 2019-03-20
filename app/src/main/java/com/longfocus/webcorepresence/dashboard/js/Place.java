package com.longfocus.webcorepresence.dashboard.js;

import android.support.annotation.NonNull;

import com.google.android.gms.location.Geofence;
import com.longfocus.webcorepresence.ParseUtils;
import com.longfocus.webcorepresence.smartapp.request.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Place implements Serializable {

    private static final String REQUEST_ID_FORMAT = "%s|%s|%s";

    private static final int GEOFENCE_EXPIRATION = -1;
    private static final int GEOFENCE_TRANSITIONS = Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT;

    enum LocationType {

        INNER("i"),
        OUTER("o");

        private final String type;

        LocationType(final String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    private String id; // place id
    private String n; // name
    private boolean h; // home flag
    private double i; // inner radius (meters)
    private double o; // outer radius (meters)
    private double[] p; // position (latitude, longitude)
    private Meta meta;

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

    public double getI() {
        return i;
    }

    public void setI(final double i) {
        this.i = i;
    }

    public double getO() {
        return o;
    }

    public void setO(final double o) {
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

    @NonNull
    public List<Geofence> getGeofences(@NonNull final String instanceId) {
        final List<Geofence> geofences = new ArrayList<>();

        if (hasPosition()) {
            final Location location = getLocation();

            // inner
            geofences.add(new Geofence.Builder()
                    .setRequestId(getRequestId(instanceId, LocationType.INNER))
                    .setCircularRegion(location.getLatitude(), location.getLongitude(), (float) getI())
                    .setExpirationDuration(GEOFENCE_EXPIRATION)
                    .setTransitionTypes(GEOFENCE_TRANSITIONS)
                    .build());

            // outer
            geofences.add(new Geofence.Builder()
                    .setRequestId(getRequestId(instanceId, LocationType.OUTER))
                    .setCircularRegion(location.getLatitude(), location.getLongitude(), (float) getO())
                    .setExpirationDuration(GEOFENCE_EXPIRATION)
                    .setTransitionTypes(GEOFENCE_TRANSITIONS)
                    .build());
        }

        return geofences;
    }

    @NonNull
    public Location getLocation() {
        final double[] position = getP();
        final double latitude = position[0];
        final double longitude = position[1];

        final Location location = new Location();
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        return location;
    }

    @Override
    public String toString() {
        return ParseUtils.toJson(this);
    }

    private boolean hasPosition() {
        final double[] position = getP();
        return (position != null && position.length >= 2);
    }

    private String getRequestId(final String instanceId, final LocationType locationType) {
        return String.format(REQUEST_ID_FORMAT, instanceId, getId(), locationType.getType());
    }
}
