package com.longfocus.webcorepresence.location;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class LocationReceiver extends BroadcastReceiver {

    private static final String TAG = LocationReceiver.class.getSimpleName();

    public enum LocationAction {

        RESUME("longfocus.intent.action.LOCATION_RESUME", "Resume"),
        PAUSE("longfocus.intent.action.LOCATION_PAUSE", "Pause");

        private final String name;
        private final String text;

        LocationAction(final String name, final String text) {
            this.name = name;
            this.text = text;
        }

        @Nullable
        public static LocationAction fromIntent(@Nullable final Intent intent) {
            return intent != null ? fromName(intent.getAction()) : null;
        }

        @Nullable
        public static LocationAction fromName(@Nullable final String name) {
            for (final LocationAction action : values()) {
                if (action.getName().equals(name)) {
                    return action;
                }
            }

            return null;
        }

        public String getName() {
            return name;
        }

        public String getText() {
            return text;
        }

        public Intent asIntent() {
            return new Intent(getName());
        }

        public IntentFilter asIntentFilter() {
            return new IntentFilter(getName());
        }
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d(TAG, "onReceive()");

        final LocationAction locationAction = LocationAction.fromIntent(intent);

        Log.d(TAG, "onReceive() action: " + locationAction);

        switch (locationAction) {
            case RESUME:
                LocationService.getInstance().startListening();
                break;
            case PAUSE:
                LocationService.getInstance().stopListening();
                break;
        }
    }

    @NonNull
    public static Intent addResumeAction(final Context context, final NotificationCompat.Builder builder) {
        Log.d(TAG, "addResumeAction()");

        return addAction(context, builder, LocationAction.RESUME);
    }

    @NonNull
    public static Intent addPauseAction(final Context context, final NotificationCompat.Builder builder) {
        Log.d(TAG, "addPauseAction()");

        return addAction(context, builder, LocationAction.PAUSE);
    }

    @NonNull
    private static Intent addAction(final Context context, final NotificationCompat.Builder builder, final LocationAction action) {
        Log.d(TAG, "addAction() action: " + action);

        final Intent intent = new Intent(context, LocationReceiver.class);
        intent.setAction(action.getName());

        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        builder.addAction(0, action.getText(), pendingIntent);

        return intent;
    }
}
