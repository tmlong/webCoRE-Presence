package com.longfocus.webcorepresence.location;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.longfocus.webcorepresence.ParseUtils;
import com.longfocus.webcorepresence.dashboard.Registration;
import com.longfocus.webcorepresence.smartapp.RequestTaskFactory;
import com.longfocus.webcorepresence.smartapp.location.Location;

public class UpdatedLocationCallback implements LocationCallback {

    private static final String TAG = UpdatedLocationCallback.class.getSimpleName();

    private final Context context;

    public UpdatedLocationCallback(@NonNull final Context context) {
        this.context = context;
    }

    @Override
    public void handle(@NonNull final android.location.Location location) {
        Log.d(TAG, "handle()");

        getRequestTaskFactory().locationUpdated(toParameter(location)).execute();
    }

    @NonNull
    private RequestTaskFactory getRequestTaskFactory() {
        final Registration registration = Registration.getInstance(context);
        return new RequestTaskFactory(registration);
    }

    @NonNull
    private String toParameter(@NonNull final android.location.Location location) {
        return ParseUtils.toJson(Location.fromLocation(location));
    }
}
