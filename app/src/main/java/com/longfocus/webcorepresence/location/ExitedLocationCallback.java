package com.longfocus.webcorepresence.location;

import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.longfocus.webcorepresence.Callback;
import com.longfocus.webcorepresence.dashboard.Registration;
import com.longfocus.webcorepresence.smartapp.location.ExitedTask;

public class ExitedLocationCallback implements Callback<Geofence> {

    private static final String TAG = ExitedLocationCallback.class.getSimpleName();

    private final Registration registration;

    public ExitedLocationCallback(final Registration registration) {
        Log.d(TAG, "ExitedLocationCallback()");

        this.registration = registration;
    }

    @Override
    public void handle(final Geofence geofence) {
        new ExitedTask(registration).execute(registration.getDeviceId(), geofence.getRequestId());
    }
}
