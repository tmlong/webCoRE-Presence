package com.longfocus.webcorepresence.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.longfocus.webcorepresence.ParseUtils;
import com.longfocus.webcorepresence.dashboard.js.Place;
import com.longfocus.webcorepresence.smartapp.UriMappingFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.util.Base64.DEFAULT;

public class Registration implements Serializable {

    private static final String TAG = Registration.class.getSimpleName();

    private static final String UUID_REGEX = "(.{8})(.{4})(.{4})(.{4})(.{12})";
    private static final String UUID_REPLACEMENT = "$1-$2-$3-$4-$5";

    private static final String URI_SCHEME = "https";
    private static final String URI_PATH_FORMAT = "/api/token/%s/smartapps/installations/%s";

    public static final String KEY = "registration";

    public interface Callback extends com.longfocus.webcorepresence.Callback<Registration> {
    }

    private String host;
    private String apiToken;
    private String appId;
    private String token;
    private String instanceId;
    private String deviceId;
    private Place[] places;

    @NonNull
    public static Registration getInstance(@NonNull final Context context) {
        Log.d(TAG, "getInstance()");

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String registrationJson = sharedPreferences.getString(KEY, "{}");

        Log.d(TAG, "getInstance() registration json: " + registrationJson);

        return ParseUtils.fromJson(registrationJson, Registration.class);
    }

    @NonNull
    public static Registration decode(@Nullable final String dataEncoded) {
        Log.d(TAG, "decode() data (encoded): " + dataEncoded);

        final String data = new String(Base64.decode(dataEncoded, DEFAULT));

        Log.d(TAG, "decode() data (decoded): " + data);

        final int hostLength = data.length() - 32 * 2;

        final String host = data.substring(0, hostLength);
        final String token = data.substring(hostLength, hostLength + 32);
        final String id = data.substring(hostLength + 32, hostLength + 32 * 2);

        //
        // e.g.
        // encoded: Z3JhcGgtbmEwNC11c2Vhc3QyNDFjNWI5ZTg2Yjk4NGM4MzkxYzQ5ZDI3OWI4ZDQ3NTZlZDk5NTk4MWM5N2Q0YjZlYTRlMzI1NzE1MTk1NjkwMA==
        // decoded: graph-na04-useast241c5b9e86b984c8391c49d279b8d4756ed995981c97d4b6ea4e3257151956900
        //

        final Registration registration = new Registration();
        registration.setHost(host);
        registration.setApiToken(uuid(token));
        registration.setAppId(uuid(id));

        Log.d(TAG, "decode() host: " + registration.getHost());
        Log.d(TAG, "decode() api token: " + registration.getApiToken());
        Log.d(TAG, "decode() app id: " + registration.getAppId());

        return registration;
    }

    @NonNull
    public static Registration decode(@NonNull final Uri uri) {
        Log.d(TAG, "decode() uri: " + uri);

        final List<String> pathSegments = uri.getPathSegments();

        if (pathSegments.isEmpty() || pathSegments.size() < 6) {
            throw new IllegalArgumentException("uri is not formatted properly: " + uri);
        }

        //
        // e.g.
        // https://graph-na04-useast2.api.smartthings.com/api/token/<uuid>/smartapps/installations/<uuid>/intf/dashboard/load?token=<uuid>&dashboard=0&dev=&callback=angular.callbacks._0
        //

        final Registration registration = new Registration();
        registration.setHost(uri.getAuthority());
        registration.setApiToken(pathSegments.get(2));
        registration.setAppId(pathSegments.get(5));
        registration.setToken(uri.getQueryParameter(UriMappingFactory.TOKEN_PARAM));

        Log.d(TAG, "decode() host: " + registration.getHost());
        Log.d(TAG, "decode() api token: " + registration.getApiToken());
        Log.d(TAG, "decode() app id: " + registration.getAppId());
        Log.d(TAG, "decode() token: " + registration.getToken());

        return registration;
    }

    private static String uuid(final String uuid) {
        return uuid.replaceFirst(UUID_REGEX, UUID_REPLACEMENT);
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(final String apiToken) {
        this.apiToken = apiToken;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(final String appId) {
        this.appId = appId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(final String instanceId) {
        this.instanceId = instanceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
    }

    public Place[] getPlaces() {
        return places;
    }

    public void setPlaces(final Place[] places) {
        this.places = places;
    }

    public void save(@NonNull final Context context) {
        Log.d(TAG, "save()");

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY, ParseUtils.toJson(this));
        editor.commit();
    }

    @NonNull
    public Uri getUri() {
        return new Uri.Builder()
                .scheme(URI_SCHEME)
                .authority(host)
                .path(String.format(URI_PATH_FORMAT, getApiToken(), getAppId()))
                .build();
    }

    @NonNull
    public List<Geofence> getGeofences() {
        final List<Geofence> geofences = new ArrayList<>();

        for (final Place place : getPlaces()) {
            geofences.addAll(place.getGeofences(getInstanceId()));
        }

        return geofences;
    }
}
