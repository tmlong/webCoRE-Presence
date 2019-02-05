package com.longfocus.webcorepresence.location;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.longfocus.webcorepresence.MainActivity;
import com.longfocus.webcorepresence.R;
import com.longfocus.webcorepresence.dashboard.Registration;
import com.longfocus.webcorepresence.location.LocationReceiver.LocationAction;

public class LocationService extends Service {

    private static final String TAG = "LocationService";

    private static final long LOCATION_INTERVAL = 10000L;
    private static final float LOCATION_DISTANCE = 0f;

    private static final int NOTIFICATION_ID = 1034;

    private static final String NOTIFICATION_CHANNEL_ID = "longfocus.service.location";
    private static final String NOTIFICATION_CHANNEL_NAME = "Location Service";
    private static final String NOTIFICATION_CHANNEL_DESCRIPTION = "Listening for location updates.";

    private static LocationService INSTANCE;

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

        if (isRunning()) {
            throw new IllegalStateException("instance already exists.");
        }

        super.onCreate();

        INSTANCE = this;

        initLocationManager();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");

        super.onDestroy();

        INSTANCE = null;

        stopListening();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.d(TAG, "onStartCommand() flags: " + flags);
        Log.d(TAG, "onStartCommand() startId: " + startId);

        if (intent == null) {
            Log.d(TAG, "onStartCommand() intent not available; stopping service.");

            return START_STICKY_COMPATIBILITY;
        }

        registration = (Registration) intent.getSerializableExtra("registration");
        deviceId = intent.getStringExtra("deviceId");

        Log.d(TAG, "onStartCommand() registration: " + registration);
        Log.d(TAG, "onStartCommand() deviceId: " + deviceId);

        startListening();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        Log.d(TAG, "onBind()");

        return null;
    }

    public boolean isListening() {
        Log.d(TAG, "isListening()");

        return (locationListener != null);
    }

    public void startListening() {
        Log.d(TAG, "startListening()");

        initLocationListener();

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, locationListener);

            startInForeground();

            notifyListening("Location updates were started.", LocationAction.START);
        } catch (SecurityException e) {
            Log.e(TAG, "startListening() app permission is not valid.", e);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "startListening() gps provider is not valid.", e);
        }
    }

    public void stopListening() {
        Log.d(TAG, "stopListening()");

        if (locationListener != null) {
            locationManager.removeUpdates(locationListener);

            locationListener = null;
        }

        stopForeground(true);

        notifyListening("Location updates were stopped.", LocationAction.STOP);
    }

    private void notifyListening(final String message, final LocationAction action) {
        Log.d(TAG, "notifyLocationUpdates()");

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        LocalBroadcastManager.getInstance(this).sendBroadcast(action.asIntent());
    }

    private void initLocationManager() {
        Log.d(TAG, "initLocationManager()");

        if (locationManager == null) {
            Log.d(TAG, "initLocationManager() creating manager.");

            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private void initLocationListener() {
        Log.d(TAG, "initLocationListener()");

        if (locationListener == null) {
            Log.d(TAG, "initLocationListener() creating listener.");

            final UpdatedLocationCallback locationCallback = new UpdatedLocationCallback(registration, deviceId);
            locationListener = new LocationListener(LocationManager.GPS_PROVIDER, locationCallback);
        }
    }

    private PendingIntent getContentIntent() {
        final Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        return PendingIntent.getActivity(this,0, intent,0);
    }

    private void startInForeground() {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_location_searching_black_24dp)
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
