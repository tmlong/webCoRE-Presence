package com.longfocus.webcorepresence;

import android.net.Uri;

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

    private Uri uri;

    public UriMappingFactory(final Uri uri) {
        this.uri = uri;
    }

    public Uri getDashboardLoad(final String token) {
        return getBuilder(DASHBOARD_LOAD_PATH)
                .appendQueryParameter(TOKEN_PARAM, token)
                .build();
    }

    public Uri getDashboardPresenceCreate(final String token, final String name) {
        return getBuilder(DASHBOARD_PRESENCE_CREATE_PATH)
                .appendQueryParameter(TOKEN_PARAM, token)
                .appendQueryParameter(NAME_PARAM, name)
                .build();
    }

    public Uri getLocationEntered(final String device, final String place) {
        return getLocation(LOCATION_ENTERED_PATH, device, place);
    }

    public Uri getLocationExited(final String device, final String place) {
        return getLocation(LOCATION_EXITED_PATH, device, place);
    }

    public Uri getLocationUpdated(final String device, final String location) {
        return getBuilder(LOCATION_UPDATED_PATH)
                .appendQueryParameter(DEVICE_PARAM, device)
                .appendQueryParameter(LOCATION_PARAM, location)
                .build();
    }

    private Uri getLocation(final String path, final String dni, final String place) {
        return getBuilder(path)
                .appendQueryParameter(DEVICE_PARAM, dni)
                .appendQueryParameter(PLACE_PARAM, place)
                .build();
    }

    private Uri.Builder getBuilder(final String path) {
        return new Uri.Builder()
                .scheme(uri.getScheme())
                .authority(uri.getAuthority())
                .path(uri.getPath())
                .appendEncodedPath(path)
                .appendQueryParameter(CALLBACK_PARAM, CALLBACK_NAME);
    }
}
