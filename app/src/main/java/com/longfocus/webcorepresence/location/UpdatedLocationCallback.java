package com.longfocus.webcorepresence.location;

import android.util.Log;

import com.google.gson.Gson;
import com.longfocus.webcorepresence.dashboard.Registration;
import com.longfocus.webcorepresence.smartapp.location.Location;
import com.longfocus.webcorepresence.smartapp.location.UpdatedTask;

public class UpdatedLocationCallback implements LocationCallback {

    private static final String TAG = "UpdatedLocationCallback";

    private final Registration registration;

    public UpdatedLocationCallback(final Registration registration) {
        Log.d(TAG, "UpdatedLocationCallback()");

        this.registration = registration;
    }

    @Override
    public void handle(final android.location.Location location) {
        Log.d(TAG, "handle()");

        final String locationJson = new Gson().toJson(Location.fromLocation(location));

        new UpdatedTask(registration).execute(registration.getDeviceId(), locationJson);
    }
}
