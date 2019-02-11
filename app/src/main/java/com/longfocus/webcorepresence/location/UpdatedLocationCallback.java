package com.longfocus.webcorepresence.location;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.longfocus.webcorepresence.dashboard.Registration;
import com.longfocus.webcorepresence.smartapp.RequestTaskFactory;
import com.longfocus.webcorepresence.smartapp.location.Location;

public class UpdatedLocationCallback implements LocationCallback {

    private static final String TAG = UpdatedLocationCallback.class.getSimpleName();

    private final Context context;

    public UpdatedLocationCallback(final Context context) {
        this.context = context;
    }

    @Override
    public void handle(final android.location.Location location) {
        Log.d(TAG, "handle()");

        final Registration registration = Registration.getInstance(context);
        final RequestTaskFactory requestTaskFactory = new RequestTaskFactory(registration);
        final String locationJson = new Gson().toJson(Location.fromLocation(location));

        requestTaskFactory.locationUpdated(locationJson).execute();
    }
}
