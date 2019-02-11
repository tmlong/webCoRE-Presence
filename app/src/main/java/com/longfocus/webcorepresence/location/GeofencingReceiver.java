package com.longfocus.webcorepresence.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.longfocus.webcorepresence.dashboard.Registration;
import com.longfocus.webcorepresence.smartapp.RequestTaskFactory;

public class GeofencingReceiver extends BroadcastReceiver {

    private static final String TAG = GeofencingReceiver.class.getSimpleName();

    public static final String REQUEST_IDS_KEY = "requestIds";

    public enum GeofencingAction {

        ENTER("longfocus.intent.action.GEOFENCE_ENTER", Geofence.GEOFENCE_TRANSITION_ENTER),
        EXIT("longfocus.intent.action.GEOFENCE_EXIT", Geofence.GEOFENCE_TRANSITION_EXIT);

        private final String name;
        private final int transition;

        GeofencingAction(final String name, final int transition) {
            this.name = name;
            this.transition = transition;
        }

        @Nullable
        public static GeofencingAction fromIntent(@Nullable final Intent intent) {
            return intent != null ? fromName(intent.getAction()) : null;
        }

        @Nullable
        public static GeofencingAction fromName(@Nullable final String name) {
            for (final GeofencingAction action : values()) {
                if (action.getName().equals(name)) {
                    return action;
                }
            }

            return null;
        }

        @Nullable
        public static GeofencingAction fromTransition(final int transition) {
            for (final GeofencingAction action : values()) {
                if (action.getTransition() == transition) {
                    return action;
                }
            }

            return null;
        }

        public String getName() {
            return name;
        }

        public int getTransition() {
            return transition;
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

        final GeofencingAction geofencingAction = GeofencingAction.fromIntent(intent);
        final String[] requestIds = intent.getStringArrayExtra(REQUEST_IDS_KEY);
        final Registration registration = Registration.getInstance(context);
        final RequestTaskFactory requestTaskFactory = new RequestTaskFactory(registration);

        for (final String requestId : requestIds) {
            switch (geofencingAction) {
                case ENTER:
                    requestTaskFactory.locationEntered(requestId).execute();
                    break;
                case EXIT:
                    requestTaskFactory.locationExited(requestId).execute();
                    break;
            }
        }
    }
}
