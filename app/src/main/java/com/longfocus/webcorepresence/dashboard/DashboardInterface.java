package com.longfocus.webcorepresence.dashboard;

import android.net.Uri;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.longfocus.webcorepresence.Callback;
import com.longfocus.webcorepresence.ParseUtils;

/**
 * https://github.com/ady624/webCoRE/blob/master/dashboard/js/modules/dashboard.module.js
 */
public class DashboardInterface {

    private static final String TAG = "DashboardInterface";

    class StatusIn {

        private String i; // instance id
    }

    class StatusOut {

        private String dni; // host device id
        private String s; // presence sensor id
    }

    class Register {

        private String e; // raw endpoint
        private String a; // raw access token
        private String i; // instance id
        private String d; // device id
    }

    class Meta {

        private float d;
        private boolean p;
    }

    class Place {

        private boolean h; // home flag
        private float i; // inner radius (meters)
        private String id; // place id
        private Meta meta; // meta
        private String n; // name
        private float o; // outer radius (meters)
        private float[] p; // position (latitude, longitude)
        private String $$hashKey;
    }

    class Update {

        private String i; // instance id
        private Place[] p; // places
    }

    public interface RegistrationCallback extends Callback<Registration> {
    }

    private final RegistrationCallback callback;

    private Registration registration;

    public DashboardInterface(final RegistrationCallback callback) {
        this.callback = callback;
    }

    @JavascriptInterface
    public String getPlatformName() {
        return "Android";
    }

    @JavascriptInterface
    public String getStatus(final String json) {
        Log.d(TAG, "getStatus() json: " + json);

        final StatusIn statusIn = ParseUtils.fromJson(json, StatusIn.class);
        Log.d(TAG, "getStatus() i: " + statusIn.i);

        final StatusOut statusOut = new StatusOut();

        if (registration != null) {
            statusOut.s = registration.getDeviceId();
        }

        return ParseUtils.toJson(statusOut);
    }

    @JavascriptInterface
    public String register(final String json) {
        Log.d(TAG, "register() json: " + json);

        final Register register = ParseUtils.fromJson(json, Register.class);
        Log.d(TAG, "register() e: " + register.e);
        Log.d(TAG, "register() a: " + register.a);
        Log.d(TAG, "register() i: " + register.i);
        Log.d(TAG, "register() d: " + register.d);

        final Uri endpoint = Uri.parse(register.e);

        registration = Registration.decode(endpoint);
        registration.setDeviceId(register.d);

        callback.handle(registration);

        return null;
    }

    @JavascriptInterface
    public String update(final String json) {
        Log.d(TAG, "update() json: " + json);

        final Update update = ParseUtils.fromJson(json, Update.class);
        Log.d(TAG, "update() h: " + update.i);
        Log.d(TAG, "update() p: " + update.p.length);

        return null;
    }
}
