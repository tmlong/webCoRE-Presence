package com.longfocus.webcorepresence.location;

import android.location.Location;
import android.util.Log;

public class NoopLocationCallback implements LocationCallback {

    private static final String TAG = "NoopLocationCallback";

    @Override
    public void handle(final Location location) {
        Log.e(TAG, "handle() location: " + location);
    }
}
