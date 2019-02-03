package com.longfocus.webcorepresence.location;

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
import android.widget.Toast;

import com.google.gson.Gson;
import com.longfocus.webcorepresence.Callback;
import com.longfocus.webcorepresence.MainActivity;
import com.longfocus.webcorepresence.R;
import com.longfocus.webcorepresence.dashboard.Registration;
import com.longfocus.webcorepresence.smartapp.location.Location;
import com.longfocus.webcorepresence.smartapp.location.UpdatedTask;

public class LocationService extends Service {

    private static final String TAG = "LocationService";

    private static final long LOCATION_INTERVAL = 15000L;
    private static final float LOCATION_DISTANCE = 0f;

    private static final int NOTIFICATION_ID = 1034;

    private static final String NOTIFICATION_CHANNEL_ID = "longfocus.service.location";
    private static final String NOTIFICATION_CHANNEL_NAME = "Location Service";
    private static final String NOTIFICATION_CHANNEL_DESCRIPTION = "BlamoWamoYaKnow";

    private static LocationService INSTANCE;

    interface LocationCallback extends Callback<android.location.Location> {
    }

    class UpdatedLocationCallback implements LocationCallback {

        private final Registration registration;
        private final String deviceId;

        public UpdatedLocationCallback(final Registration registration, final String deviceId) {
            this.registration = registration;
            this.deviceId = deviceId;
        }

        @Override
        public void handle(final android.location.Location location) {
            final String locationJson = new Gson().toJson(Location.fromLocation(location));

            new UpdatedTask(registration).execute(deviceId, locationJson);
        }
    }

    class LocationListener implements android.location.LocationListener {

        private final android.location.Location lastLocation;
        private final LocationCallback locationCallback;

        public LocationListener(final String provider) {
            this(provider, null);
        }

        public LocationListener(final String provider, final LocationCallback callback) {
            Log.d(TAG, "LocationListener() provider: " + provider);

            lastLocation = new android.location.Location(provider);
            locationCallback = callback;
        }

        @Override
        public void onLocationChanged(final android.location.Location location) {
            Log.e(TAG, "onLocationChanged() location: " + location);

            lastLocation.set(location);

            if (locationCallback != null) {
                locationCallback.handle(location);
            }
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

    // Location
    private LocationManager locationManager;
    private LocationListener locationListener;

    public static LocationService getInstance() {
        return INSTANCE;
    }

    public static boolean isRunning() {
        return (INSTANCE != null);
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        super.onCreate();

        INSTANCE = this;

        initLocationManager();
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

        if (intent == null) {
            Log.d(TAG, "onStartCommand() intent not available; stopping service.");

            stopSelf();

            return START_STICKY_COMPATIBILITY;
        }

        registration = (Registration) intent.getSerializableExtra("registration");
        deviceId = intent.getStringExtra("deviceId");

        Log.d(TAG, "onStartCommand() registration: " + registration);
        Log.d(TAG, "onStartCommand() deviceId: " + deviceId);

        startLocationUpdates();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        Log.d(TAG, "onBind()");
        return null;
    }

    public void startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates()");

        if (locationListener == null) {
            final UpdatedLocationCallback locationCallback = new UpdatedLocationCallback(registration, deviceId);
            locationListener = new LocationListener(LocationManager.GPS_PROVIDER, locationCallback);
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, locationListener);
        } catch (SecurityException e) {
            Log.e(TAG, "initLocationManager() app permission is not valid.", e);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "initLocationManager() gps provider is not valid.", e);
        }
    }

    public void stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates()");

        Toast.makeText(this, "webCoRE Presence location updates were stopped.", Toast.LENGTH_SHORT).show();

        locationManager.removeUpdates(locationListener);

        stopForeground(true);
        stopSelf(NOTIFICATION_ID);
    }

    private void initLocationManager() {
        Log.d(TAG, "initLocationManager()");

        if (locationManager == null) {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private PendingIntent getContentIntent() {
        final Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        return PendingIntent.getActivity(this,0, intent,0);
    }

    private void startInForeground() {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.common_full_open_on_phone)
                .setContentTitle("webCoRE Presence")
                .setContentText("Listening for location updates every 15s.")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(getContentIntent())
                .setAutoCancel(true);

        LocationReceiver.addStopAction(this, builder);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(NOTIFICATION_CHANNEL_DESCRIPTION);

            final NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        startForeground(NOTIFICATION_ID, builder.build());
    }
}
