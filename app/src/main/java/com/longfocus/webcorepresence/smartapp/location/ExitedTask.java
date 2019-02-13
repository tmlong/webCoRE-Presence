package com.longfocus.webcorepresence.smartapp.location;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.longfocus.webcorepresence.dashboard.Registration;
import com.longfocus.webcorepresence.smartapp.UriMappingFactory;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ExitedTask extends AsyncTask<String, Void, Void> {

    private static final String TAG = ExitedTask.class.getSimpleName();

    private final OkHttpClient httpClient = new OkHttpClient();

    private final Registration registration;

    public ExitedTask(final Registration registration) {
        this.registration = registration;
    }

    public Registration getRegistration() {
        return registration;
    }

    @Override
    protected Void doInBackground(final String... params) {
        if (params == null || params.length < 2) {
            throw new IllegalArgumentException("Missing query parameters: [deviceId, place]");
        }

        final String deviceId = params[0];
        final String place = params[1];

        Log.d(TAG, "doInBackground() deviceId: " + deviceId);
        Log.d(TAG, "doInBackground() place: " + place);

        final Uri uri = new UriMappingFactory(registration).getLocationExited(place);

        Log.d(TAG, "doInBackground() uri: " + uri);

        final Request request = new Request.Builder()
                .url(uri.toString())
                .build();

        try {
            httpClient.newCall(request).execute();
        } catch (IOException e) {
            Log.e(TAG, "doInBackground() Error making the request.", e);
        }

        return null;
    }
}
