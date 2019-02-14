package com.longfocus.webcorepresence;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonParseException;
import com.longfocus.webcorepresence.dashboard.DashboardClient;
import com.longfocus.webcorepresence.dashboard.DashboardInterface;
import com.longfocus.webcorepresence.dashboard.Registration;
import com.longfocus.webcorepresence.location.LocationReceiver.LocationAction;
import com.longfocus.webcorepresence.location.LocationService;
import com.longfocus.webcorepresence.smartapp.RequestTask;
import com.longfocus.webcorepresence.smartapp.RequestTaskFactory;
import com.longfocus.webcorepresence.smartapp.response.Error;
import com.longfocus.webcorepresence.smartapp.response.Success;

import static android.view.View.OnClickListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String[] ACCESS_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final int ACCESS_PERMISSIONS_REQUEST_CODE = 34;

    private static final String DASHBOARD_INTERFACE = "BridgeCommander";
    private static final String DASHBOARD_URL = "https://dashboard.webcore.co";

    // Views
    private WebView webViewDashboard;
    private EditText editTextPresenceName;
    private Button buttonInitPresence;
    private Button buttonControlLocation;

    // Events
    private DefaultReceiver defaultReceiver;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initDashboard();
        initLocation();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");

        if (defaultReceiver == null) {
            defaultReceiver = new DefaultReceiver();
        }

        registerLocationReceivers();

        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");

        if (defaultReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(defaultReceiver);
        }

        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult() requestCode: " + requestCode);

        switch (requestCode) {
            case ACCESS_PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationService();
                }
                break;
        }
    }

    private void initViews() {
        Log.d(TAG, "initViews()");

        webViewDashboard = findViewById(R.id.webView_dashboard);
        editTextPresenceName = findViewById(R.id.edit_presenceName);
        buttonInitPresence = findViewById(R.id.button_initPresence);
        buttonControlLocation = findViewById(R.id.button_controlLocation);

        buttonControlLocation.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                Log.d(TAG, "onClick()");

                final LocationService locationService = LocationService.getInstance();

                if (locationService == null) {
                    initLocation();
                } else {
                    if (locationService.isListening()) {
                        locationService.stopListening();
                    } else {
                        locationService.startListening();
                    }
                }
            }
        });
    }

    private void initDashboard() {
        Log.d(TAG, "initDashboard()");

        webViewDashboard.getSettings().setJavaScriptEnabled(true);

        if (!hasPresenceDevice()) {
            webViewDashboard.addJavascriptInterface(new DashboardInterface(this, new DashboardCallback(this)), DASHBOARD_INTERFACE);
            webViewDashboard.setWebViewClient(new DashboardClient(this, new DashboardCallback(this)));
        }

        webViewDashboard.loadUrl(DASHBOARD_URL);
    }

    private void initLocation() {
        Log.d(TAG, "initLocation()");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final int permissionResult = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

            Log.d(TAG, "initLocation() permissionResult: " + permissionResult);

            if (permissionResult == PackageManager.PERMISSION_GRANTED) {
                startLocationService();
            } else {
                requestPermissions(ACCESS_PERMISSIONS, ACCESS_PERMISSIONS_REQUEST_CODE);
            }
        } else {
            final int permissionResult = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

            Log.d(TAG, "initLocation() permissionResult: " + permissionResult);

            if (permissionResult == PackageManager.PERMISSION_GRANTED) {
                startLocationService();
            } else {
                ActivityCompat.requestPermissions(this, ACCESS_PERMISSIONS, ACCESS_PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    private void startLocationService() {
        Log.d(TAG, "startLocationService()");

        if (!isLocationServiceReady()) return;

        final Intent intent = new Intent(this, LocationService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private void registerLocationReceivers() {
        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);

        broadcastManager.registerReceiver(defaultReceiver, LocationAction.START.asIntentFilter());
        broadcastManager.registerReceiver(defaultReceiver, LocationAction.STOP.asIntentFilter());
    }

    private boolean isLocationServiceReady() {
        return (hasPresenceDevice() && !LocationService.isRunning());
    }

    private boolean hasPresenceDevice() {
        final Registration registration = Registration.getInstance(this);
        return (registration != null && !TextUtils.isEmpty(registration.getDeviceId()));
    }

    private class DashboardCallback implements Registration.Callback {

        private static final String TAG = "DashboardCallback";

        private final Context context;

        private DashboardCallback(final Context context) {
            this.context = context;
        }

        @Override
        public void handle(final Registration registration) {
            Log.d(TAG, "handle() registration: " + registration);

            webViewDashboard.post(new Runnable() {

                @Override
                public void run() {
                    webViewDashboard.removeJavascriptInterface(DASHBOARD_INTERFACE);
                    webViewDashboard.setWebViewClient(null);
                }
            });

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    editTextPresenceName.setEnabled(true);

                    buttonInitPresence.setEnabled(true);
                    buttonInitPresence.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(final View v) {
                            Log.d(TAG, "onClick()");

                            final String name = editTextPresenceName.getText().toString();

                            final RequestTaskFactory requestTaskFactory = RequestTaskFactory.getInstance(context);
                            requestTaskFactory.dashboardPresenceCreate(new PresenceCreateCallback(context), name).execute();
                        }
                    });
                }
            });
        }
    }

    private class PresenceCreateCallback implements RequestTask.JsonCallback {

        private static final String TAG = "PresenceCreateCallback";

        private final Context context;

        private PresenceCreateCallback(final Context context) {
            this.context = context;
        }

        @Override
        public void handle(final String json) {
            Log.d(TAG, "handle() json: " + json);

            try {
                final Success success = ParseUtils.fromJson(json, Success.class);

                Log.d(TAG, "handle() success: " + success);

                final Registration registration = Registration.getInstance(context);
                registration.setDeviceId(success.getDeviceId());
                registration.save(context);

                Log.d(TAG, "handle() registration: " + registration);

                initLocation();
            } catch (JsonParseException e) {
                final Error error = ParseUtils.fromJson(json, Error.class);
                Log.e(TAG, "handle() unable to parse the response: " + error.getError(), e);
            }
        }
    }

    private class DefaultReceiver extends BroadcastReceiver {

        private static final String TAG = "DefaultReceiver";

        @Override
        public void onReceive(final Context context, final Intent intent) {
            Log.d(TAG, "onReceive()");

            final LocationAction locationAction = LocationAction.fromName(intent.getAction());

            Log.d(TAG, "onReceive() action: " + locationAction);

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    buttonControlLocation.setText(getText());
                }

                private String getText() {
                    switch (locationAction) {
                        case START: return getString(R.string.stop_location);
                        case STOP: return getString(R.string.start_location);
                    }

                    throw new IllegalArgumentException("location action is not available.");
                }
            });
        }
    }
}
