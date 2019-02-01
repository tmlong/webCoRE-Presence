package com.longfocus.webcorepresence.dashboard;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.longfocus.webcorepresence.MainActivity;
import com.longfocus.webcorepresence.UriMappingFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;

public class DashboardClient extends WebViewClient {

    private static final String TAG = "DashboardClient";

    private static final String API_HOST = "api.smartthings.com";

    private static final String REGISTER_PATH = "/dashboard/register";

    private static final String TEXT_HTML = "text/html";
    private static final String UTF_8 = StandardCharsets.UTF_8.name();

    private static final RequestBody EMPTY_BODY = RequestBody.create(null, new byte[0]);

    public interface RegistrationCallback extends MainActivity.Callback<Registration> {
    }

    private final OkHttpClient httpClient = new OkHttpClient();

    private final RegistrationCallback callback;

    public DashboardClient(final RegistrationCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onLoadResource(final WebView view, final String url) {
        final Uri uri = Uri.parse(url);

        Log.v(TAG, "onLoadResource() url: " + url);

        if (hasApiHost(uri)) {
            Log.d(TAG, "onLoadResource() found api host: " + API_HOST);

            if (hasToken(uri)) {
                Log.d(TAG, "onLoadResource() found token query parameter.");

                final Registration registration = Registration.decode(uri);

                callback.handle(registration);
            }
        }

        super.onLoadResource(view, url);
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(final WebView view, final WebResourceRequest request) {
        //
        // Disabled; Using onLoadResource to determine info
        //
        if (Boolean.TRUE) return super.shouldInterceptRequest(view, request);

        final Uri url = request.getUrl();

        Log.v(TAG, "shouldInterceptRequest() url: " + url);

        if (hasRegisterPath(url)) {
            Log.d(TAG, "shouldInterceptRequest() found register path: " + REGISTER_PATH);

            return handleRegisterRequest(url);
        }

        return super.shouldInterceptRequest(view, request);
    }

    private WebResourceResponse handleRegisterRequest(final Uri uri) {
        final Request request = new Request.Builder()
                .url(uri.toString())
                .post(EMPTY_BODY)
                .build();

        Log.d(TAG, "handleRegisterRequest() request: " + request);

        try {
            final Response response = httpClient.newCall(request).execute();

            Log.d(TAG, "handleRegisterRequest() response: " + response);

            final String data = parseResponse(response);
            final Registration registration = Registration.decode(data);

            callback.handle(registration);

            return new WebResourceResponse(
                    TEXT_HTML,
                    UTF_8,
                    response.code(),
                    response.message(),
                    toHeaders(response.headers()),
                    response.body().byteStream());
        } catch (IOException e) {
            Log.e(TAG, "handleRegisterRequest() Error making the request.", e);
            return null;
        }
    }

    private String parseResponse(final Response response) throws IOException {
        final ResponseBody body = response.body();
        final BufferedSource source = body != null ? body.source() : null;

        if (source != null) {
            source.request(Integer.MAX_VALUE);
            return source.buffer().snapshot().utf8();
        }

        return null;
    }

    private boolean hasRegisterPath(final Uri uri) {
        return uri.getPath().startsWith(REGISTER_PATH);
    }

    private boolean hasApiHost(final Uri uri) {
        final String host = uri.getHost();
        return (host != null && host.endsWith(API_HOST));
    }

    private boolean hasToken(final Uri uri) {
        final String token = uri.getQueryParameter(UriMappingFactory.TOKEN_PARAM);
        return !TextUtils.isEmpty(token);
    }

    public static Map<String, String> toHeaders(final Headers headers) {
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
