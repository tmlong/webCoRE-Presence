package com.longfocus.webcorepresence.smartapp.dashboard;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonParseException;
import com.longfocus.webcorepresence.MainActivity;
import com.longfocus.webcorepresence.ParseUtils;
import com.longfocus.webcorepresence.UriMappingFactory;
import com.longfocus.webcorepresence.dashboard.Registration;
import com.longfocus.webcorepresence.smartapp.ErrorResponse;
import com.longfocus.webcorepresence.smartapp.StatusResponse;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PresenceCreateTask  extends AsyncTask<String, Void, PresenceCreateTask.Success> {

    private static final String TAG = "PresenceCreateTask";

    public class Success extends StatusResponse {

        private String deviceId;

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(final String deviceId) {
            this.deviceId = deviceId;
        }
    }

    public interface SuccessCallback extends MainActivity.Callback<Success> {
    }

    private final OkHttpClient httpClient = new OkHttpClient();

    private final Registration registration;
    private final SuccessCallback callback;

    public PresenceCreateTask(final Registration registration, final SuccessCallback callback) {
        this.registration = registration;
        this.callback = callback;
    }

    @Override
    protected Success doInBackground(final String... params) {
        if (params == null || params.length < 1) {
            throw new IllegalArgumentException("Missing query parameters: [name]");
        }

        final String token = registration.getToken();
        final String name = params[0];

        Log.d(TAG, "doInBackground() token: " + token);
        Log.d(TAG, "doInBackground() name: " + name);

        final Uri uri = new UriMappingFactory(registration.getUri()).getDashboardPresenceCreate(token, name);

        Log.d(TAG, "doInBackground() uri: " + uri);

        final Request request = new Request.Builder()
                .url(uri.toString())
                .build();

        Response response = null;

        try {
            response = httpClient.newCall(request).execute();

            final String json = toJson(response);

            Log.d(TAG, "doInBackground() json: " + json);

            try {
                return ParseUtils.fromJson(json, Success.class);
            } catch (JsonParseException e) {
                final ErrorResponse errorResponse = ParseUtils.fromJson(json, ErrorResponse.class);
                Log.e(TAG, "doInBackground() Unable to parse the response: " + errorResponse.getError(), e);
            }
        } catch (IOException e) {
            Log.e(TAG, "doInBackground() Error making the request.", e);
        } catch (JsonParseException e) {
            Log.e(TAG, "doInBackground() Unable to parse the response.", e);
        } finally {
            if (response != null) {
                response.close();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(final Success result) {
        super.onPostExecute(result);
        callback.handle(result);
    }

    private String toJson(final Response response) throws IOException {
        final String data = response.body().string();
        return ParseUtils.jsonCallback(UriMappingFactory.CALLBACK_NAME, data);
    }
}
