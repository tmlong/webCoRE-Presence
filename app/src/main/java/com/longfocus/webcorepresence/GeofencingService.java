package com.longfocus.webcorepresence;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.longfocus.webcorepresence.dashboard.Registration;
import com.longfocus.webcorepresence.location.EnteredLocationCallback;
import com.longfocus.webcorepresence.location.ExitedLocationCallback;

public class GeofencingService extends IntentService {

    private static final String TAG = GeofencingService.class.getSimpleName();

    public GeofencingService() {
        super("GeofencingService");
    }

    private Registration registration;

    @Override
    public int onStartCommand(@Nullable final Intent intent, final int flags, final int startId) {
        Log.d(TAG, "onStartCommand() flags: " + flags);
        Log.d(TAG, "onStartCommand() startId: " + startId);

        if (intent == null) {
            Log.d(TAG, "onStartCommand() intent not available; stopping service.");

            return START_STICKY_COMPATIBILITY;
        }

        final String registrationJson = intent.getStringExtra(Registration.KEY);
        registration = ParseUtils.fromJson(registrationJson, Registration.class);

        Log.d(TAG, "onStartCommand() registration: " + registration);

        return super.onStartCommand(intent, flags, startId);
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

        Log.d(TAG, "onHandleIntent() geofence trans: "  + geofenceTransition);

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.d(TAG, "onHandleIntent() geofence enter");

            for (final Geofence geofence : geofencingEvent.getTriggeringGeofences()) {
                new EnteredLocationCallback(registration).handle(geofence);
            }
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.d(TAG, "onHandleIntent() geofence exit");

            for (final Geofence geofence : geofencingEvent.getTriggeringGeofences()) {
                new ExitedLocationCallback(registration).handle(geofence);
            }
        }
    }
}
