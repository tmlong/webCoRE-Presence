package com.longfocus.webcorepresence.smartapp.request;

import android.support.annotation.NonNull;

public class Location {

    private final long timestamp = System.currentTimeMillis();

    private String provider;
    private double latitude;
    private double longitude;
    private double altitude;
    private String floor;
    private float horizontalAccuracy;
    private float verticalAccuracy;
    private float speed;
    private float bearing;

    @NonNull
    public static Location fromLocation(@NonNull final android.location.Location geoLocation) {
        final Location location = new Location();
        location.setProvider(geoLocation.getProvider());
        location.setLatitude(geoLocation.getLatitude());
        location.setLongitude(geoLocation.getLongitude());
        location.setAltitude(geoLocation.getAltitude());
        location.setHorizontalAccuracy(geoLocation.getAccuracy());
        location.setSpeed(geoLocation.getSpeed());
        location.setBearing(geoLocation.getBearing());

        return location;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(final String provider) {
        this.provider = provider;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(final double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(final double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(final double altitude) {
        this.altitude = altitude;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(final String floor) {
        this.floor = floor;
    }

    public float getHorizontalAccuracy() {
        return horizontalAccuracy;
    }

    public void setHorizontalAccuracy(final float horizontalAccuracy) {
        this.horizontalAccuracy = horizontalAccuracy;
    }

    public float getVerticalAccuracy() {
        return verticalAccuracy;
    }

    public void setVerticalAccuracy(final float verticalAccuracy) {
        this.verticalAccuracy = verticalAccuracy;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(final float speed) {
        this.speed = speed;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(final float bearing) {
        this.bearing = bearing;
    }

    @NonNull
    public android.location.Location toGeoLocation() {
        final android.location.Location geoLocation = new android.location.Location(getProvider());
        geoLocation.setLatitude(getLatitude());
        geoLocation.setLongitude(getLongitude());
        geoLocation.setAltitude(getAltitude());
        geoLocation.setAccuracy(getHorizontalAccuracy());
        geoLocation.setSpeed(getSpeed());
        geoLocation.setBearing(getBearing());

        return geoLocation;
    }

    public float distanceTo(@NonNull final Location location) {
        return location.toGeoLocation().distanceTo(toGeoLocation());
    }
}
