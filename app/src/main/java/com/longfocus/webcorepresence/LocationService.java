package com.longfocus.webcorepresence;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.longfocus.webcorepresence.dashboard.Registration;
import com.longfocus.webcorepresence.smartapp.location.Location;
import com.longfocus.webcorepresence.smartapp.location.UpdatedTask;

/**
 * https://graph-na04-useast2.api.smartthings.com/api/token/f05ac424-789e-4320-8a52-e34af2f87cda/smartapps/installations/09ab31f9-4c40-433d-a30f-b3288e1da7d9/intf/dashboard/load?token=374c6542-7cb2-4a71-8c74-c40eaf223dec&dashboard=0&dev=1548901285634&callback=angular.callbacks._3
 * :1790dd51513cd5b351e08930e90fbbb4:
 */
public class LocationService extends Service {

    private static final String TAG = "LocationService";

    private static final int LOCATION_INTERVAL = 5000;
    private static final float LOCATION_DISTANCE = 0f;

    private static final int NOTIFICATION_ID = 34;

    private static final String NOTIFICATION_CHANNEL_ID = "longfocus.service.location";
    private static final String NOTIFICATION_CHANNEL_NAME = "Location Service";
    private static final String NOTIFICATION_CHANNEL_DESCRIPTION = "BlamoWamoYaKnow";

    private static LocationService INSTANCE;

    class LocationListener implements android.location.LocationListener {

        private android.location.Location lastLocation;

        public LocationListener(final String provider) {
            Log.d(TAG, "LocationListener() provider: " + provider);

            lastLocation = new android.location.Location(provider);
        }

        @Override
        public void onLocationChanged(final android.location.Location location) {
            Log.e(TAG, "onLocationChanged() location: " + location);

            lastLocation.set(location);

            final String locationJson = new Gson().toJson(Location.fromLocation(location));
            new UpdatedTask(registration).execute(deviceId, locationJson);
        }

        @Override
        public void onStatusChanged(final String provider, final int status, final Bundle extras) {
        }

        @Override
        public void onProviderDisabled(final String provider) {
        }

        @Override
        public void onProviderEnabled(final String provider) {
        }
    }

    // Extras
    private Registration registration;
    private String deviceId;

    public static boolean isRunning() {
        return INSTANCE != null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        super.onCreate();

        INSTANCE = this;

        final LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    new LocationListener(LocationManager.GPS_PROVIDER));
        } catch (java.lang.SecurityException e) {
            Log.e(TAG, "onCreate() fail to request location update, ignore", e);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "onCreate() gps provider does not exist " + e.getMessage(), e);
        }

        startInForeground();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();

        INSTANCE = null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.d(TAG, "onStartCommand() flags: " + flags);
        Log.d(TAG, "onStartCommand() startId: " + startId);

        registration = (Registration) intent.getSerializableExtra("registration");
        deviceId = intent.getStringExtra("deviceId");

        Log.d(TAG, "onStartCommand() registration: " + registration);
        Log.d(TAG, "onStartCommand() deviceId: " + deviceId);

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    private void startInForeground() {
        final Intent intent = new Intent(this, MainActivity.class);
        final PendingIntent pendingIntent=PendingIntent.getActivity(this,0, intent,0);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("TEST")
                .setContentText("HELLO")
                .setTicker("TICKER")
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(NOTIFICATION_CHANNEL_DESCRIPTION);

            final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        startForeground(NOTIFICATION_ID, builder.build());
    }
}
