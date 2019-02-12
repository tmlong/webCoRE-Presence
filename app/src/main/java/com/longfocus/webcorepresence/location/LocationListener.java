package com.longfocus.webcorepresence.location;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class LocationListener implements android.location.LocationListener {

    private static final String TAG = LocationListener.class.getSimpleName();

    private final Location lastLocation;
    private final LocationCallback callback;

    public LocationListener(@NonNull final String provider) {
        this(provider, null);
    }

    public LocationListener(@NonNull final String provider, @Nullable final LocationCallback callback) {
        Log.d(TAG, "LocationListener() provider: " + provider);

        lastLocation = new Location(provider);
        this.callback = callback;
    }

    @Override
    public void onLocationChanged(final Location location) {
        Log.e(TAG, "onLocationChanged() location: " + location);

        lastLocation.set(location);

        if (callback != null) {
            callback.handle(location);
        }
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
}
