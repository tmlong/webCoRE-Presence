package com.longfocus.webcorepresence.location;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.longfocus.webcorepresence.smartapp.RequestTaskFactory;
import com.longfocus.webcorepresence.smartapp.request.Location;

public class UpdatedLocationCallback implements LocationCallback {

    private static final String TAG = UpdatedLocationCallback.class.getSimpleName();

    private final Context context;

    public UpdatedLocationCallback(@NonNull final Context context) {
        this.context = context;
    }

    @Override
    public void handle(@NonNull final android.location.Location geoLocation) {
        Log.d(TAG, "handle()");

        final RequestTaskFactory requestTaskFactory = RequestTaskFactory.getInstance(context);
        final Location location = Location.fromLocation(geoLocation);

        requestTaskFactory.locationUpdated(location).execute();

        LocationService.getInstance().updateLocation(location);
    }
}
