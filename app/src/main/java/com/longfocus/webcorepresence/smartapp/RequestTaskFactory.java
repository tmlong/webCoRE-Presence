package com.longfocus.webcorepresence.smartapp;

import android.content.Context;

import com.longfocus.webcorepresence.ParseUtils;
import com.longfocus.webcorepresence.dashboard.Registration;
import com.longfocus.webcorepresence.smartapp.request.Location;

public class RequestTaskFactory {

    private final UriMappingFactory uriMappingFactory;

    public RequestTaskFactory(final Registration registration) {
        uriMappingFactory = new UriMappingFactory(registration);
    }

    public static RequestTaskFactory getInstance(final Context context) {
        final Registration registration = Registration.getInstance(context);
        return new RequestTaskFactory(registration);
    }

    public RequestTask dashboardLoad(final RequestTask.JsonCallback callback) {
        return new RequestTask(uriMappingFactory.getDashboardLoad(), callback);
    }

    public RequestTask dashboardPresenceCreate(final RequestTask.JsonCallback callback, final String name) {
        return new RequestTask(uriMappingFactory.getDashboardPresenceCreate(name), callback);
    }

    public RequestTask locationEntered(final String place) {
        return new RequestTask(uriMappingFactory.getLocationEntered(place));
    }

    public RequestTask locationExited(final String place) {
        return new RequestTask(uriMappingFactory.getLocationExited(place));
    }

    public RequestTask locationUpdated(final Location location) {
        return locationUpdated(ParseUtils.toJson(location));
    }

    public RequestTask locationUpdated(final String location) {
        return new RequestTask(uriMappingFactory.getLocationUpdated(location));
    }
}
