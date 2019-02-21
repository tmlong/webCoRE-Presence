package com.longfocus.webcorepresence.smartapp;

import android.content.Context;

import com.longfocus.webcorepresence.dashboard.Registration;
import com.longfocus.webcorepresence.smartapp.request.Location;

public class RequestTaskFactory {

    private final UriMappingFactory uriMappingFactory;

    public RequestTaskFactory(final Registration registration) {
        this(new UriMappingFactory(registration));
    }

    public RequestTaskFactory(final UriMappingFactory uriMappingFactory) {
        this.uriMappingFactory = uriMappingFactory;
    }

    public static RequestTaskFactory getInstance(final Context context) {
        final Registration registration = Registration.getInstance(context);
        return new RequestTaskFactory(registration);
    }

    public RequestTask dashboardLoad() {
        return new RequestTask(uriMappingFactory.getDashboardLoad());
    }

    public RequestTask dashboardPresenceCreate(final String name) {
        return new RequestTask(uriMappingFactory.getDashboardPresenceCreate(name));
    }

    public RequestTask locationEntered(final String place) {
        return new RequestTask(uriMappingFactory.getLocationEntered(place));
    }

    public RequestTask locationExited(final String place) {
        return new RequestTask(uriMappingFactory.getLocationExited(place));
    }

    public RequestTask locationUpdated(final Location location) {
        return new RequestTask(uriMappingFactory.getLocationUpdated(location));
    }
}
