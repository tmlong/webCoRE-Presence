package com.longfocus.webcorepresence.smartapp;

import android.net.Uri;

import com.longfocus.webcorepresence.dashboard.Registration;

public class UriMappingFactory {

    private static final String DASHBOARD_LOAD_PATH = "intf/dashboard/load";
    private static final String DASHBOARD_PRESENCE_CREATE_PATH = "intf/dashboard/presence/create";

    private static final String LOCATION_ENTERED_PATH = "intf/location/entered";
    private static final String LOCATION_EXITED_PATH = "intf/location/exited";
    private static final String LOCATION_UPDATED_PATH = "intf/location/updated";

    public static final String CALLBACK_NAME = "longfocus";

    public static final String TOKEN_PARAM = "token";
    public static final String DEVICE_PARAM = "device";
    public static final String NAME_PARAM = "name";
    public static final String PLACE_PARAM = "place";
    public static final String LOCATION_PARAM = "location";
    public static final String CALLBACK_PARAM = "callback";

    private final Registration registration;

    public UriMappingFactory(final Registration registration) {
        this.registration = registration;
    }

    public Uri getDashboardLoad() {
        return getBuilder(DASHBOARD_LOAD_PATH).build();
    }

    public Uri getDashboardPresenceCreate(final String name) {
        return getBuilder(DASHBOARD_PRESENCE_CREATE_PATH)
                .appendQueryParameter(NAME_PARAM, name)
                .build();
    }

    public Uri getLocationEntered(final String place) {
        return getLocation(LOCATION_ENTERED_PATH, place);
    }

    public Uri getLocationExited(final String place) {
        return getLocation(LOCATION_EXITED_PATH, place);
    }

    public Uri getLocationUpdated(final String location) {
        return getBuilder(LOCATION_UPDATED_PATH)
                .appendQueryParameter(LOCATION_PARAM, location)
                .build();
    }

    private Uri getLocation(final String path, final String place) {
        return getBuilder(path)
                .appendQueryParameter(PLACE_PARAM, place)
                .build();
    }

    private Uri.Builder getBuilder(final String path) {
        return registration.getUri().buildUpon()
                .appendEncodedPath(path)
                .appendQueryParameter(CALLBACK_PARAM, CALLBACK_NAME)
                .appendQueryParameter(TOKEN_PARAM, registration.getToken())
                .appendQueryParameter(DEVICE_PARAM, registration.getDeviceId());
    }
}
