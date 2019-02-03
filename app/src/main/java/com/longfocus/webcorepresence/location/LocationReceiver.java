package com.longfocus.webcorepresence.location;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.longfocus.webcorepresence.R;

public class LocationReceiver extends BroadcastReceiver {

    private static final String TAG = "LocationReceiver";

    public static final String LOCATION_STOP_ACTION = "longfocus.intent.action.LOCATION_STOP";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d(TAG, "onReceive() intent action: " + intent.getAction());

        switch (intent.getAction()) {
            case LOCATION_STOP_ACTION:
                LocationService.getInstance().stopLocationUpdates();
                break;
        }
    }

    public static Intent addStopAction(final Context context, final NotificationCompat.Builder builder) {
        Log.d(TAG, "addStopAction()");

        final Intent intent = new Intent(context, LocationReceiver.class);
        intent.setAction(LOCATION_STOP_ACTION);

        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        builder.addAction(R.drawable.common_full_open_on_phone, "Stop", pendingIntent);

        return intent;
    }
}
