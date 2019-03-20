package com.longfocus.webcorepresence;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String[] ACCESS_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final int ACCESS_PERMISSIONS_REQUEST_CODE = 34;

    private static final String DASHBOARD_INTERFACE = "BridgeCommander";
    private static final String DASHBOARD_URL = "https://dashboard.webcore.co";

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        private static final String TAG = "ServiceConnection";

        @Override
        public void onServiceConnected(final ComponentName name, final IBinder service) {
            Log.d(TAG, "onServiceConnected() name: " + name);

            final LocationService.ServiceBinder binder = (LocationService.ServiceBinder) service;
            locationService = binder.getService();

            invalidateOptionsMenu();
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
            Log.d(TAG, "onServiceDisconnected() name: " + name);

            locationService = null;
        }
    };

    // Views
    private WebView webViewDashboard;

    // Events
    private DefaultReceiver defaultReceiver;

    // Services
    private LocationService locationService;

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
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");

        super.onDestroy();

        if (serviceConnection != null) {
            unbindService(serviceConnection);
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");

        super.onResume();

        registerLocationReceivers();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");

        super.onPause();

        unregisterLocationReceivers();
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu()");

        menu.findItem(R.id.action_add_presence).collapseActionView();
        menu.findItem(R.id.action_add_presence).setVisible(canInitPresenceDevice());

        if (hasLocationService()) {
            final boolean isListening = locationService.isListening();

            menu.findItem(R.id.action_location_on).setVisible(isListening);
            menu.findItem(R.id.action_location_off).setVisible(!isListening);
            menu.findItem(R.id.action_refresh).setVisible(true);
            menu.findItem(R.id.action_refresh).setEnabled(isListening);
            menu.findItem(R.id.action_refresh).setIcon(isListening ? R.drawable.ic_refresh_black_24dp : R.drawable.ic_refresh_disabled_black_24dp);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu()");

        getMenuInflater().inflate(R.menu.menu_main, menu);

        configureAddPresenceMenuItem(menu);

        return true;
    }

    private void configureAddPresenceMenuItem(final Menu menu) {
        Log.d(TAG, "configureAddPresenceMenuItem()");

        final MenuItem menuItem = menu.findItem(R.id.action_add_presence);
        final View actionView = menuItem.getActionView();
        final EditText editText = actionView.findViewById(R.id.editText_menu);

        editText.setHint(R.string.app_name);

        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(final MenuItem item) {
                editText.requestFocus();

                getInputService().showSoftInput(getCurrentFocus(), InputMethodManager.SHOW_IMPLICIT);

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(final MenuItem item) {
                getInputService().hideSoftInputFromWindow(editText.getWindowToken(), 0);

                return true;
            }
        });

        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getInputService().hideSoftInputFromWindow(v.getWindowToken(), 0);

                new PresenceCreateListener(this, menuItem).onSubmit(v.getText().toString());
            }

            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected()");

        switch (item.getItemId()) {
            case R.id.action_add_presence:
                break;
            case R.id.action_location_on:
                if (hasLocationService()) {
                    locationService.stopListening();
                }
                break;
            case R.id.action_location_off:
                if (hasLocationService()) {
                    locationService.startListening();
                }
                break;
            case R.id.action_refresh:
                locationService.refresh();
                break;
            default:
        }

        return super.onOptionsItemSelected(item);
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

        // views
        webViewDashboard = findViewById(R.id.webView_dashboard);
    }

    private void initDashboard() {
        Log.d(TAG, "initDashboard()");

        webViewDashboard.getSettings().setJavaScriptEnabled(true);

        // configure callbacks
        webViewDashboard.addJavascriptInterface(new DashboardInterface(this, new DashboardCallback()), DASHBOARD_INTERFACE);
        webViewDashboard.setWebViewClient(new DashboardClient(this, new DashboardCallback()));

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

        if (!canStartLocationService()) return;

        final Intent intent = new Intent(this, LocationService.class);

        startService(intent);
        bindService(intent, serviceConnection, 0);
    }

    private void registerLocationReceivers() {
        Log.d(TAG, "registerLocationReceivers()");

        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);

        if (defaultReceiver == null) {
            defaultReceiver = new DefaultReceiver();
        }

        broadcastManager.registerReceiver(defaultReceiver, LocationAction.RESUME.asIntentFilter());
        broadcastManager.registerReceiver(defaultReceiver, LocationAction.PAUSE.asIntentFilter());
    }

    private void unregisterLocationReceivers() {
        Log.d(TAG, "unregisterLocationReceivers()");

        if (defaultReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(defaultReceiver);
        }
    }

    private InputMethodManager getInputService() {
        return (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private boolean hasLocationService() {
        return (locationService != null);
    }

    private boolean canStartLocationService() {
        return hasPresenceDevice();
    }

    private boolean canInitPresenceDevice() {
        return hasToken();
    }

    private boolean hasToken() {
        final Registration registration = Registration.getInstance(this);
        return (registration != null && !TextUtils.isEmpty(registration.getToken()));
    }

    private boolean hasPresenceDevice() {
        final Registration registration = Registration.getInstance(this);
        return (registration != null && !TextUtils.isEmpty(registration.getDeviceId()));
    }

    private class DashboardCallback implements Registration.Callback {

        private static final String TAG = "DashboardCallback";

        @Override
        public void handle(final Registration registration) {
            Log.d(TAG, "handle() registration: " + registration);

            webViewDashboard.post(() -> webViewDashboard.setWebViewClient(null));

            invalidateOptionsMenu();

            if (!hasLocationService()) {
                initLocation();
            }
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

                if (hasLocationService()) {
                    locationService.startListening();
                } else {
                    initLocation();
                }
            } catch (JsonParseException e) {
                final Error error = ParseUtils.fromJson(json, Error.class);
                Log.e(TAG, "handle() unable to parse the response: " + error.getError(), e);
            }
        }
    }

    private class PresenceCreateListener {

        private static final String TAG = "PresenceCreateListener";

        private final Context context;
        private final MenuItem menuItem;

        private PresenceCreateListener(final Context context, final MenuItem menuItem) {
            this.context = context;
            this.menuItem = menuItem;
        }

        public boolean onSubmit(final String name) {
            Log.d(TAG, "onSubmit name: " + name);

            if (hasPresenceDevice()) {
                warnExistingPresenceDevice(name);
            } else {
                createPresenceDevice(name);
                menuItem.collapseActionView();
            }

            return false;
        }

        private void warnExistingPresenceDevice(final String name) {
            Log.d(TAG, "warnExistingPresenceDevice() name: " + name);

            final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setTitle(R.string.create_presence_title)
                    .setIcon(R.drawable.ic_warning_black_24dp)
                    .setMessage(R.string.create_presence_warning)
                    .setPositiveButton(R.string.ok, (dialog, id) -> {
                        createPresenceDevice(name);
                        menuItem.collapseActionView();
                    })
                    .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    });

            alertBuilder.create().show();
        }

        private void createPresenceDevice(final String name) {
            Log.d(TAG, "createPresenceDevice() name: " + name);

            final RequestTaskFactory requestTaskFactory = RequestTaskFactory.getInstance(getApplicationContext());
            final RequestTask requestTask = requestTaskFactory.dashboardPresenceCreate(name);

            requestTask.setCallback(new PresenceCreateCallback(getApplicationContext()));
            requestTask.execute();
        }
    }

    private class DefaultReceiver extends BroadcastReceiver {

        private static final String TAG = "DefaultReceiver";

        @Override
        public void onReceive(final Context context, final Intent intent) {
            Log.d(TAG, "onReceive()");

            final LocationAction locationAction = LocationAction.fromName(intent.getAction());

            Log.d(TAG, "onReceive() action: " + locationAction);

            invalidateOptionsMenu();
        }
    }
}
