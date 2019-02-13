package com.longfocus.webcorepresence.dashboard;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.longfocus.webcorepresence.ParseUtils;
import com.longfocus.webcorepresence.dashboard.client.Load;
import com.longfocus.webcorepresence.smartapp.UriMappingFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DashboardClient extends WebViewClient {

    private static final String TAG = DashboardClient.class.getSimpleName();

    private static final String REGISTER_PATH = "/dashboard/register";
    private static final String LOAD_PATH = "/dashboard/load";

    private static final String TEXT_HTML = "text/html";
    private static final String APPLICATION_JAVASCRIPT = "application/javascript";

    private static final String UTF_8 = StandardCharsets.UTF_8.name();

    private static final RequestBody EMPTY_BODY = RequestBody.create(null, new byte[0]);

    private final OkHttpClient httpClient = new OkHttpClient();

    private final Context context;
    private final Registration.Callback callback;

    public DashboardClient(final Context context, final Registration.Callback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(final WebView view, final WebResourceRequest request) {
        Log.d(TAG, "shouldInterceptRequest()");

        final Uri url = request.getUrl();

        Log.d(TAG, "shouldInterceptRequest() url: " + url);

        if (isRegisterPath(url)) {
            Log.d(TAG, "shouldInterceptRequest() found register path.");

            return handleRegisterRequest(url);
        } else if (isLoadPath(url)) {
            Log.d(TAG, "shouldInterceptRequest() found load path.");

            if (hasToken(url)) {
                Log.d(TAG, "shouldInterceptRequest() found token.");

                return handleLoadRequest(url);
            }
        }

        return super.shouldInterceptRequest(view, request);
    }

    @Nullable
    private WebResourceResponse handleRegisterRequest(@NonNull final Uri uri) {
        Log.d(TAG, "handleRegisterRequest() uri: " + uri);

        final Request request = new Request.Builder()
                .url(uri.toString())
                .post(EMPTY_BODY)
                .build();

        try {
            final Response response = httpClient.newCall(request).execute();

            Log.d(TAG, "handleRegisterRequest() response: " + response);

            final String data = ParseUtils.getData(response);

            Log.d(TAG, "handleRegisterRequest() data: " + data);

            final Registration registration = Registration.decode(data);
            registration.save(context);

            return new WebResourceResponse(
                    TEXT_HTML,
                    UTF_8,
                    response.code(),
                    response.message(),
                    toHeaders(response.headers()),
                    response.body().byteStream());
        } catch (IOException e) {
            Log.e(TAG, "handleRegisterRequest() error making the request.", e);
            return null;
        }
    }

    @Nullable
    private WebResourceResponse handleLoadRequest(@NonNull final Uri uri) {
        Log.d(TAG, "handleLoadRequest() uri: " + uri);

        final Request request = new Request.Builder()
                .url(uri.toString())
                .build();

        Log.d(TAG, "handleLoadRequest() request: " + request);

        try {
            final Response response = httpClient.newCall(request).execute();

            Log.d(TAG, "handleLoadRequest() response: " + response);

            final String data = ParseUtils.getData(response);

            Log.d(TAG, "handleLoadRequest() data: " + data);

            final String callback = uri.getQueryParameter(UriMappingFactory.CALLBACK_PARAM);
            final String token = uri.getQueryParameter(UriMappingFactory.TOKEN_PARAM);

            Log.d(TAG, "handleLoadRequest() callback: " + callback);
            Log.d(TAG, "handleLoadRequest() token: " + token);

            final String json = ParseUtils.jsonCallback(callback, data);

            Log.d(TAG, "handleLoadRequest() json:" + json);

            final Load load = ParseUtils.fromJson(json, Load.class);

            Log.d(TAG, "handleLoadRequest() load:" + load);

            final Registration registration = Registration.decode(load);
            registration.setToken(token);
            registration.save(context);

            this.callback.handle(registration);

            return new WebResourceResponse(
                    APPLICATION_JAVASCRIPT,
                    UTF_8,
                    response.code(),
                    response.message(),
                    toHeaders(response.headers()),
                    response.body().byteStream());
        } catch (IOException e) {
            Log.e(TAG, "handleLoadRequest() error making the request.", e);
            return null;
        }
    }

    private boolean isRegisterPath(@NonNull final Uri uri) {
        return uri.getPath().startsWith(REGISTER_PATH);
    }

    private boolean isLoadPath(@NonNull final Uri uri) {
        return uri.getPath().endsWith(LOAD_PATH);
    }

    private boolean hasToken(@NonNull final Uri uri) {
        final String token = uri.getQueryParameter(UriMappingFactory.TOKEN_PARAM);
        return !TextUtils.isEmpty(token);
    }

    @NonNull
    public static Map<String, String> toHeaders(@Nullable final Headers headers) {
        final Map<String, String> headersMap = new HashMap<>();

        if (headers != null) {
            for (int i = 0; i < headers.size(); i++) {
                final String key = headers.name(i);
                final String value = headers.value(i);

                headersMap.put(key, value);
            }
        }

        return headersMap;
    }
}
