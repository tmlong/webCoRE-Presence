package com.longfocus.webcorepresence.location;

import android.location.Location;
import android.util.Log;

public class NoopLocationCallback implements LocationCallback {

    private static final String TAG = NoopLocationCallback.class.getSimpleName();

    @Override
    public void handle(final Location location) {
        Log.d(TAG, "handle() location: " + location);
    }
}
