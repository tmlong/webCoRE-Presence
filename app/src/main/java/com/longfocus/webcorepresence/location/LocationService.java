package com.longfocus.webcorepresence.location;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
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
import com.longfocus.webcorepresence.dashboard.js.Place;
import com.longfocus.webcorepresence.location.GeofencingReceiver.GeofencingAction;
import com.longfocus.webcorepresence.location.LocationReceiver.LocationAction;
import com.longfocus.webcorepresence.smartapp.request.Location;

public class LocationService extends Service {

    private static final String TAG = LocationService.class.getSimpleName();

    private static final long LOCATION_INTERVAL = 120000L;
    private static final float LOCATION_DISTANCE = 100f;

    private static final int NOTIFICATION_ID = 1034;

    private static final String NOTIFICATION_CHANNEL_ID = "longfocus.service.location";

    private static LocationService INSTANCE;

    private final IBinder serviceBinder = new ServiceBinder();

    private boolean listening;

    // Location
    private LocationManager locationManager;
    private LocationListener locationListener;

    // Geofencing
    private GeofencingClient geofencingClient;
    private GeofencingReceiver geofencingReceiver;
    private PendingIntent geofencingPendingIntent;

    public class ServiceBinder extends Binder {

        public LocationService getService() {
            return LocationService.this;
        }
    }

    public static LocationService getInstance() {
        return INSTANCE;
    }

    private Registration getRegistration() {
        return Registration.getInstance(this);
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");

        INSTANCE = this;

        initLocationManager();
        initLocationListener();
        initGeofencingReceiver();
        initGeofencingClient();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");

        INSTANCE = null;

        stopListening();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.d(TAG, "onStartCommand()");

        if (intent == null) {
            Log.w(TAG, "onStartCommand() intent not available.");

            // Lollipop does not seem to cooperate when being removed from the foreground state.
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
                stopInForeground(false);
            }

            return START_NOT_STICKY;
        }

        if (!isListening()) {
            startListening();
        }

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        Log.d(TAG, "onBind()");

        return serviceBinder;
    }

    public boolean isListening() {
        Log.d(TAG, "isListening()");

        return listening;
    }

    public void startListening() {
        Log.d(TAG, "startListening()");

        try {
            startLocationUpdates();

            startGeofencing();

            startInForeground();

            notifyListening(LocationAction.RESUME);

            listening = true;
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

        stopLocationUpdates();

        stopGeofencing();

        stopInForeground(removeNotification);

        notifyListening(LocationAction.PAUSE);

        listening = false;
    }

    public void refresh() throws SecurityException {
        Log.d(TAG, "refresh()");

        startLocationUpdates();
    }

    public void updateLocation(@NonNull final Location location) {
        Log.d(TAG, "updateLocation()");

        final Registration registration = getRegistration();

        final Place currentPlace = registration.getPlace(location);

        final boolean isHome = (currentPlace != null && currentPlace.isH());

        final NotificationCompat.Builder builder = getNotificationBuilder()
                .setSmallIcon(isHome ? R.drawable.ic_location_home_black_24dp : R.drawable.ic_location_searching_black_24dp)
                .setContentTitle(isHome ? getString(R.string.currently_home)
                        : (currentPlace != null ? getString(R.string.currently_at, currentPlace.getN()) : getString(R.string.currently_away)))
                .setContentText(getString(R.string.location_updates_listening));

        LocationReceiver.addPauseAction(this, builder);

        getNotificationManager().notify(NOTIFICATION_ID, builder.build());
    }

    private void notifyListening(@NonNull final LocationAction action) {
        Log.d(TAG, "notifyListening()");

        switch (action) {
            case RESUME:
                Toast.makeText(this, getString(R.string.location_updates_started), Toast.LENGTH_SHORT).show();
                break;
            case PAUSE:
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
            Log.d(TAG, "initGeofencingReceiver() creating receiver...");

            geofencingReceiver = new GeofencingReceiver();
        }
    }

    private void initGeofencingClient() {
        Log.d(TAG, "initGeofencingClient()");

        if (geofencingClient == null) {
            Log.d(TAG, "initGeofencingClient() creating client...");

            geofencingClient = LocationServices.getGeofencingClient(this);
        }
    }

    private void startLocationUpdates() throws SecurityException {
        Log.d(TAG, "startLocationUpdates()");

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, locationListener);
    }

    private void stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates()");

        locationManager.removeUpdates(locationListener);
    }

    private void startGeofencing() throws SecurityException {
        Log.d(TAG, "startGeofencing()");

        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent());

        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);

        broadcastManager.registerReceiver(geofencingReceiver, GeofencingAction.ENTER.asIntentFilter());
        broadcastManager.registerReceiver(geofencingReceiver, GeofencingAction.EXIT.asIntentFilter());
    }

    private void stopGeofencing() {
        Log.d(TAG, "stopGeofencing()");

        geofencingClient.removeGeofences(geofencingPendingIntent);

        LocalBroadcastManager.getInstance(this).unregisterReceiver(geofencingReceiver);
    }

    @NonNull
    private GeofencingRequest getGeofencingRequest() {
        Log.d(TAG, "getGeofencingRequest()");

        return new GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(getRegistration().getGeofences())
            .build();
    }

    @NonNull
    private PendingIntent getGeofencePendingIntent() {
        Log.d(TAG, "getGeofencePendingIntent()");

        if (geofencingPendingIntent == null) {
            final Intent intent = new Intent(this, GeofencingService.class);

            geofencingPendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return geofencingPendingIntent;
    }

    @NonNull
    private PendingIntent getContentIntent() {
        Log.d(TAG, "getContentIntent()");

        final Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        return PendingIntent.getActivity(this,0, intent,0);
    }

    private void startInForeground() {
        Log.d(TAG, "startInForeground()");

        final NotificationCompat.Builder builder = getNotificationBuilder()
            .setSmallIcon(R.drawable.ic_location_searching_black_24dp)
            .setContentText(getString(R.string.location_updates_listening));

        LocationReceiver.addPauseAction(this, builder);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final CharSequence channelName = getString(R.string.location_service);
            final String channelDescription = getString(R.string.location_updates_listening);

            final NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(channelDescription);

            getNotificationManager().createNotificationChannel(channel);
        }

        startForeground(NOTIFICATION_ID, builder.build());
    }

    private void stopInForeground(final boolean removeNotification) {
        Log.d(TAG, "stopInForeground()");

        final NotificationCompat.Builder builder = getNotificationBuilder()
            .setSmallIcon(R.drawable.ic_location_disabled_black_24dp)
            .setContentText(getString(R.string.location_updates_paused));

        LocationReceiver.addResumeAction(this, builder);

        getNotificationManager().notify(NOTIFICATION_ID, builder.build());

        stopForeground(removeNotification);
    }

    @NonNull
    private NotificationCompat.Builder getNotificationBuilder() {
        Log.d(TAG, "getBuilder()");

        final CharSequence appName = getString(R.string.app_name);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(getContentIntent())
                .setAutoCancel(false);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            builder.setContentTitle(appName);
        }

        return builder;
    }

    @NonNull
    private NotificationManager getNotificationManager() {
        Log.d(TAG, "getNotificationManager()");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return getSystemService(NotificationManager.class);
        }

        return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
