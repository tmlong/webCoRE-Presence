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

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.longfocus.webcorepresence.MainActivity;
import com.longfocus.webcorepresence.R;
import com.longfocus.webcorepresence.dashboard.Registration;
import com.longfocus.webcorepresence.location.GeofencingReceiver.GeofencingAction;
import com.longfocus.webcorepresence.location.LocationReceiver.LocationAction;

public class LocationService extends Service {

    private static final String TAG = LocationService.class.getSimpleName();

    private static final long LOCATION_INTERVAL = 30000L;
    private static final float LOCATION_DISTANCE = 100f;

    private static final int NOTIFICATION_ID = 1034;

    private static final String NOTIFICATION_CHANNEL_ID = "longfocus.service.location";

    private static LocationService INSTANCE;

    // Location
    private LocationManager locationManager;
    private LocationListener locationListener;

    // Geofencing
    private GeofencingClient geofencingClient;
    private GeofencingReceiver geofencingReceiver;
    private PendingIntent geofencingPendingIntent;

    public static LocationService getInstance() {
        return INSTANCE;
    }

    public static boolean isRunning() {
        return (INSTANCE != null);
    }

    private Registration getRegistration() {
        return Registration.getInstance(this);
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");

        if (isRunning()) {
            throw new IllegalStateException("location service instance already exists.");
        }

        super.onCreate();

        INSTANCE = this;

        initLocationManager();
        initGeofencingReceiver();
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
        Log.d(TAG, "onStartCommand()");

        if (intent == null) {
            Log.w(TAG, "onStartCommand() intent not available; stopping service.");

            return START_STICKY_COMPATIBILITY;
        }

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

            startGeofencing();

            startInForeground();

            notifyListening(LocationAction.START);
        } catch (SecurityException e) {
            Log.e(TAG, "startListening() app permission is not valid.", e);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "startListening() gps provider is not valid.", e);
        }
    }

    public void stopListening() {
        Log.d(TAG, "stopListening()");

        stopListening(false);
    }

    public void stopListening(final boolean removeNotification) {
        Log.d(TAG, "stopListening() remove notification: " + removeNotification);

        if (locationListener != null) {
            locationManager.removeUpdates(locationListener);

            locationListener = null;
        }

        stopGeofencing();

        stopForeground(removeNotification);

        notifyListening(LocationAction.STOP);
    }

    private void notifyListening(final LocationAction action) {
        Log.d(TAG, "notifyListening()");

        switch (action) {
            case START:
                Toast.makeText(this, getString(R.string.location_updates_started), Toast.LENGTH_SHORT).show();
                break;
            case STOP:
                Toast.makeText(this, getString(R.string.location_updates_stopped), Toast.LENGTH_SHORT).show();
                break;
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(action.asIntent());
    }

    private void initLocationManager() {
        Log.d(TAG, "initLocationManager()");

        if (locationManager == null) {
            Log.d(TAG, "initLocationManager() creating manager...");

            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private void initLocationListener() {
        Log.d(TAG, "initLocationListener()");

        if (locationListener == null) {
            Log.d(TAG, "initLocationListener() creating listener...");

            final UpdatedLocationCallback locationCallback = new UpdatedLocationCallback(this);
            locationListener = new LocationListener(LocationManager.GPS_PROVIDER, locationCallback);
        }
    }

    private void initGeofencingReceiver() {
        Log.d(TAG, "initGeofencingReceiver()");

        if (geofencingReceiver == null) {
            geofencingReceiver = new GeofencingReceiver();
        }
    }

    private void startGeofencing() throws SecurityException {
        Log.d(TAG, "startGeofencing()");

        geofencingClient = LocationServices.getGeofencingClient(this);
        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent());

        if (geofencingReceiver != null) {
            final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);

            broadcastManager.registerReceiver(geofencingReceiver, GeofencingAction.ENTER.asIntentFilter());
            broadcastManager.registerReceiver(geofencingReceiver, GeofencingAction.EXIT.asIntentFilter());
        }
    }

    private void stopGeofencing() {
        Log.d(TAG, "stopGeofencing()");

        geofencingClient.removeGeofences(geofencingPendingIntent);

        if (geofencingReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(geofencingReceiver);
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        Log.d(TAG, "getGeofencingRequest()");

        return new GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(getRegistration().getGeofences())
            .build();
    }

    private PendingIntent getGeofencePendingIntent() {
        Log.d(TAG, "getGeofencePendingIntent()");

        if (geofencingPendingIntent == null) {
            final Intent intent = new Intent(this, GeofencingService.class);

            geofencingPendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return geofencingPendingIntent;
    }

    private PendingIntent getContentIntent() {
        Log.d(TAG, "getContentIntent()");

        final Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        return PendingIntent.getActivity(this,0, intent,0);
    }

    private void startInForeground() {
        Log.d(TAG, "startInForeground()");

        final CharSequence appName = getString(R.string.app_name);
        final CharSequence contextText = getString(R.string.location_listening_for_updates);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_location_searching_black_24dp)
                .setContentText(contextText)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(getContentIntent())
                .setAutoCancel(true);

        LocationReceiver.addStopAction(this, builder);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            builder.setContentTitle(appName);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final CharSequence channelName = getString(R.string.location_service);
            final String channelDescription = getString(R.string.location_listening_for_updates);

            final NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(channelDescription);

            final NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        startForeground(NOTIFICATION_ID, builder.build());
    }
}
