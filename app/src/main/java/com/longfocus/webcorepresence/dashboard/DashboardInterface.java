package com.longfocus.webcorepresence.dashboard;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings.Secure;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.longfocus.webcorepresence.ParseUtils;
import com.longfocus.webcorepresence.dashboard.js.Place;
import com.longfocus.webcorepresence.dashboard.js.Register;
import com.longfocus.webcorepresence.dashboard.js.StatusRequest;
import com.longfocus.webcorepresence.dashboard.js.StatusResponse;
import com.longfocus.webcorepresence.dashboard.js.Update;

/**
 * https://github.com/ady624/webCoRE/blob/master/dashboard/js/modules/dashboard.module.js
 */
public class DashboardInterface {

    private static final String TAG = DashboardInterface.class.getSimpleName();

    private static final String PLATFORM_NAME = "Android";

    private final Context context;
    private final Registration.Callback callback;

    public DashboardInterface(@NonNull final Context context, @NonNull final Registration.Callback callback) {
        this.context = context;
        this.callback = callback;
    }

    @JavascriptInterface
    public String getPlatformName() {
        return PLATFORM_NAME;
    }

    @JavascriptInterface
    public String getStatus(@Nullable final String json) {
        Log.d(TAG, "getStatus() json: " + json);

        final StatusRequest statusRequest = ParseUtils.fromJson(json, StatusRequest.class);

        Log.d(TAG, "getStatus() request i: " + statusRequest.getI());

        final StatusResponse statusResponse = new StatusResponse();
        statusResponse.setDni(Secure.getString(context.getContentResolver(), Secure.ANDROID_ID));
        statusResponse.setS(Registration.getInstance(context).getDeviceId());

        Log.d(TAG, "getStatus() response dni: " + statusResponse.getDni());
        Log.d(TAG, "getStatus() response s: " + statusResponse.getS());

        return ParseUtils.toJson(statusResponse);
    }

    @JavascriptInterface
    public String register(@Nullable final String json) {
        Log.d(TAG, "register() json: " + json);

        final Register register = ParseUtils.fromJson(json, Register.class);

        Log.d(TAG, "register() e: " + register.getE());
        Log.d(TAG, "register() a: " + register.getA());
        Log.d(TAG, "register() i: " + register.getI());
        Log.d(TAG, "register() d: " + register.getD());

        final Uri endpoint = Uri.parse(register.getE());
        final Registration registration = Registration.decode(endpoint);
        registration.setToken(register.getA());
        registration.setInstanceId(register.getI());
        registration.setDeviceId(register.getD());
        registration.save(context);

        return null;
    }

    @JavascriptInterface
    public String update(@Nullable final String json) {
        Log.d(TAG, "update() json: " + json);

        final Update update = ParseUtils.fromJson(json, Update.class);

        Log.d(TAG, "update() i: " + update.getI());

        for (final Place place : update.getP()) {
            Log.d(TAG, "update() p id: " + place.getId());
            Log.d(TAG, "update() p n: " + place.getN());
            Log.d(TAG, "update() p h: " + place.isH());
            Log.d(TAG, "update() p i: " + place.getI());
            Log.d(TAG, "update() p o: " + place.getO());
            Log.d(TAG, "update() p p: " + place.getP());
            Log.d(TAG, "update() p meta: " + place.getMeta());
            Log.d(TAG, "update() p hash: " + place.get$$hashKey());
        }

        final Registration registration = Registration.getInstance(context);
        registration.setPlaces(update.getP());
        registration.save(context);

        callback.handle(registration);

        return null;
    }
}
