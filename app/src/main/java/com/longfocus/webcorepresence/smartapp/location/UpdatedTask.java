package com.longfocus.webcorepresence.smartapp.location;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.longfocus.webcorepresence.UriMappingFactory;
import com.longfocus.webcorepresence.dashboard.Registration;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class UpdatedTask extends AsyncTask<String, Void, Void> {

    private static final String TAG = "UpdatedTask";

    private final OkHttpClient httpClient = new OkHttpClient();

    private final Registration registration;

    public UpdatedTask(final Registration registration) {
        this.registration = registration;
    }

    @Override
    protected Void doInBackground(final String... params) {
        if (params == null || params.length < 2) {
            throw new IllegalArgumentException("Missing query parameters: [deviceId, location]");
        }

        final String deviceId = params[0];
        final String location = params[1];

        Log.d(TAG, "doInBackground() deviceId: " + deviceId);
        Log.d(TAG, "doInBackground() location: " + location);

        final Uri uri = new UriMappingFactory(registration).getLocationUpdated(location);

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
