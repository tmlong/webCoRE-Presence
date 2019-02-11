package com.longfocus.webcorepresence;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.longfocus.webcorepresence.GeofencingReceiver.GeofencingAction;

import java.util.ArrayList;
import java.util.List;

public class GeofencingService extends IntentService {

    private static final String TAG = GeofencingService.class.getSimpleName();

    public GeofencingService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        Log.d(TAG, "onHandleIntent()");

        final GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent == null) {
            Log.e(TAG, "onHandleIntent() no geofencing event.");
            return;
        } else if (geofencingEvent.hasError()) {
            Log.e(TAG, "onHandleIntent() error code: " + geofencingEvent.getErrorCode());
            return;
        }

        final int geofenceTransition = geofencingEvent.getGeofenceTransition();
        final GeofencingAction geofencingAction = GeofencingAction.fromTransition(geofenceTransition);

        if (geofencingAction != null) {
            Log.d(TAG, "onHandleIntent() geofencing action: "  + geofencingAction);

            final Intent geofencingIntent = geofencingAction.asIntent();
            geofencingIntent.putExtra(GeofencingReceiver.REQUEST_IDS_KEY, getRequestIds(geofencingEvent));

            LocalBroadcastManager.getInstance(this).sendBroadcast(geofencingIntent);
        }
    }

    @NonNull
    private String[] getRequestIds(@NonNull final GeofencingEvent geofencingEvent) {
        Log.d(TAG, "getRequestIds()");

        final List<String> requestIds = new ArrayList<>();

        for (final Geofence geofence : geofencingEvent.getTriggeringGeofences()) {
            requestIds.add(geofence.getRequestId());
        }

        return requestIds.toArray(new String[requestIds.size()]);
    }
}
