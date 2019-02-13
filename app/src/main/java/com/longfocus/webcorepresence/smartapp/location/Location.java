package com.longfocus.webcorepresence.smartapp.location;

public class Location {

    private final long timestamp = System.currentTimeMillis();

    private double latitude;
    private double longitude;
    private double altitude;
    private String floor;
    private float horizontalAccuracy;
    private float verticalAccuracy;
    private float speed;
    private float bearing;

    public static Location fromLocation(final android.location.Location geoLocation) {
        final Location location = new Location();
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
}
