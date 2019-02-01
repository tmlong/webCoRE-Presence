package com.longfocus.webcorepresence;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import com.longfocus.webcorepresence.dashboard.DashboardClient;
import com.longfocus.webcorepresence.dashboard.Registration;
import com.longfocus.webcorepresence.smartapp.dashboard.PresenceCreateTask;

import static android.Manifest.permission;
import static android.view.View.OnClickListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String[] ACCESS_PERMISSIONS = {
            permission.ACCESS_FINE_LOCATION
    };

    private static final int ACCESS_PERMISSIONS_REQUEST_CODE = 34;

    private static final String REGISTRATION_KEY = "registration";
    private static final String DEVICE_ID_KEY = "deviceId";

    private static final String DASHBOARD_URL = "https://dashboard.webcore.co";

    public interface Callback<S> {

        void handle(S source);
    }

    // Views
    private WebView webView;
    private EditText editText;
    private Button button;

    // State
    private Registration registration;
    private String deviceId;

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(final Registration registration) {
        this.registration = registration;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            setRegistration((Registration) savedInstanceState.getSerializable(REGISTRATION_KEY));
            setDeviceId(savedInstanceState.getString(DEVICE_ID_KEY));

            Log.d(TAG, "onCreate() registration = " + getRegistration());
            Log.d(TAG, "onCreate() deviceId = " + getDeviceId());
        }

        initViews();
        initDashboard();

        if (checkSelfPermission(permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initLocation();
        } else {
            requestPermissions(ACCESS_PERMISSIONS, ACCESS_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        Log.d(TAG, "onSaveInstanceState()");

        super.onSaveInstanceState(outState);

        if (outState != null) {
            outState.putSerializable(REGISTRATION_KEY, getRegistration());
            outState.putSerializable(DEVICE_ID_KEY, getDeviceId());
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult() requestCode: " + requestCode);

        switch (requestCode) {
            case ACCESS_PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initLocation();
                }
                break;
        }
    }

    private void initViews() {
        Log.d(TAG, "initViews()");

        webView = findViewById(R.id.webView);
        editText = findViewById(R.id.editText);
        button = findViewById(R.id.button);
    }

    private void initDashboard() {
        Log.d(TAG, "initDashboard()");

        webView.getSettings().setJavaScriptEnabled(true);

        if (!hasPresence()) {
            webView.setWebViewClient(new DashboardClient(new DashboardCallback()));
        }

        webView.loadUrl(DASHBOARD_URL);
    }

    private void initLocation() {
        Log.d(TAG, "initLocation()");

        if (!hasPresence() || LocationService.isRunning()) return;

        final Bundle extras = new Bundle();
        extras.putSerializable(REGISTRATION_KEY, getRegistration());
        extras.putString(DEVICE_ID_KEY, getDeviceId());

        final Intent intent = new Intent(this, LocationService.class);
        intent.putExtras(extras);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private boolean hasPresence() {
        return !TextUtils.isEmpty(getDeviceId());
    }

    private class DashboardCallback implements DashboardClient.RegistrationCallback {

        private static final String TAG = "DashboardCallback";

        @Override
        public void handle(final Registration registration) {
            Log.d(TAG, "handle() registration: " + registration);

            webView.setWebViewClient(null);

            setRegistration(registration);

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    button.setEnabled(true);
                    button.setOnClickListener(new OnClickListener() {
                        public void onClick(final View v) {
                            Log.d(TAG, "onClick()");

                            final String name = editText.getText().toString();

                            new PresenceCreateTask(getRegistration(), new PresenceCreateCallback()).execute(name);
                        }
                    });
                }
            });
        }
    }

    private class PresenceCreateCallback implements PresenceCreateTask.SuccessCallback {

        private static final String TAG = "PresenceCreateCallback";

        @Override
        public void handle(final PresenceCreateTask.Success success) {
            Log.d(TAG, "handle() success: " + success);

            setDeviceId(success.getDeviceId());

            initLocation();
        }
    }
}
