package com.longfocus.webcorepresence.location;

import android.os.Bundle;
import android.util.Log;

public class LocationListener implements android.location.LocationListener {

    private static final String TAG = "LocationListener";

    private final android.location.Location lastLocation;
    private final LocationCallback callback;

    public LocationListener(final String provider) {
        this(provider, null);
    }

    public LocationListener(final String provider, final LocationCallback callback) {
        Log.d(TAG, "LocationListener() provider: " + provider);

        lastLocation = new android.location.Location(provider);
        this.callback = callback != null ? callback : new NoopLocationCallback();
    }

    @Override
    public void onLocationChanged(final android.location.Location location) {
        Log.e(TAG, "onLocationChanged() location: " + location);

        lastLocation.set(location);
        callback.handle(location);
    }

    @Override
    public void onStatusChanged(final String provider, final int status, final Bundle extras) {
    }

    @Override
    public void onProviderDisabled(final String provider) {
    }

    @Override
    public void onProviderEnabled(final String provider) {
    }

    public android.location.Location getLastLocation() {
        return lastLocation;
    }
}
