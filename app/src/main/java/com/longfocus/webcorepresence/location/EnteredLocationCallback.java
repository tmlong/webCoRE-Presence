package com.longfocus.webcorepresence.location;

import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.longfocus.webcorepresence.Callback;
import com.longfocus.webcorepresence.dashboard.Registration;
import com.longfocus.webcorepresence.smartapp.location.EnteredTask;

public class EnteredLocationCallback implements Callback<Geofence> {

    private static final String TAG = EnteredLocationCallback.class.getSimpleName();

    private final Registration registration;

    public EnteredLocationCallback(final Registration registration) {
        Log.d(TAG, "EnteredLocationCallback()");

        this.registration = registration;
    }

    @Override
    public void handle(final Geofence geofence) {
        new EnteredTask(registration).execute(registration.getDeviceId(), geofence.getRequestId());
    }
}
