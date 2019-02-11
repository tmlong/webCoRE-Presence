package com.longfocus.webcorepresence.location;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class LocationReceiver extends BroadcastReceiver {

    private static final String TAG = "LocationReceiver";

    public enum LocationAction {

        START("longfocus.intent.action.LOCATION_START", "Start"),
        STOP("longfocus.intent.action.LOCATION_STOP", "Stop");

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
        Log.d(TAG, "onReceive() intent action: " + intent.getAction());

        final LocationAction locationAction = LocationAction.fromIntent(intent);

        switch (locationAction) {
            case START:
                LocationService.getInstance().startListening();
                break;
            case STOP:
                LocationService.getInstance().stopListening();
                break;
        }
    }

    public static Intent addStartAction(final Context context, final NotificationCompat.Builder builder) {
        Log.d(TAG, "addStartAction()");

        return addAction(context, builder, LocationAction.START);
    }

    public static Intent addStopAction(final Context context, final NotificationCompat.Builder builder) {
        Log.d(TAG, "addStopAction()");

        return addAction(context, builder, LocationAction.STOP);
    }

    private static Intent addAction(final Context context, final NotificationCompat.Builder builder, final LocationAction action) {
        Log.d(TAG, "addAction() action: " + action);

        final Intent intent = new Intent(context, LocationReceiver.class);
        intent.setAction(action.getName());

        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        builder.addAction(0, action.getText(), pendingIntent);

        return intent;
    }
}
