package com.longfocus.webcorepresence.smartapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.longfocus.webcorepresence.Callback;
import com.longfocus.webcorepresence.ParseUtils;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RequestTask extends AsyncTask<String, Void, String> {

    private static final String TAG = RequestTask.class.getSimpleName();

    public interface JsonCallback extends Callback<String> {
    }

    private final OkHttpClient httpClient = new OkHttpClient();

    private final Uri uri;
    private final JsonCallback callback;

    public RequestTask(final Uri uri) {
        this(uri, null);
    }

    public RequestTask(final Uri uri, final JsonCallback callback) {
        this.uri = uri;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(final String... params) {
        Log.d(TAG, "doInBackground() uri: " + uri);

        final Request request = new Request.Builder()
                .url(uri.toString())
                .build();

        Response response = null;

        try {
            response = httpClient.newCall(request).execute();

            return toJson(response);
        } catch (IOException e) {
            Log.e(TAG, "doInBackground() error making the request.", e);
        } finally {
            if (response != null) {
                response.close();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(final String json) {
        Log.d(TAG, "onPostExecute() json: " + json);

        super.onPostExecute(json);

        if (callback != null) {
            callback.handle(json);
        }
    }

    private String toJson(@NonNull final Response response) throws IOException {
        Log.d(TAG, "toJson()");

        final String data = response.body().string();
        return ParseUtils.jsonCallback(UriMappingFactory.CALLBACK_NAME, data);
    }
}
