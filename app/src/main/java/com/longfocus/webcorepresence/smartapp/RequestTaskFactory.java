package com.longfocus.webcorepresence.smartapp;

import com.longfocus.webcorepresence.UriMappingFactory;
import com.longfocus.webcorepresence.dashboard.Registration;

public class RequestTaskFactory {

    private final UriMappingFactory uriMappingFactory;

    public RequestTaskFactory(final Registration registration) {
        uriMappingFactory = new UriMappingFactory(registration);
    }

    public RequestTask locationEntered(final String place) {
        return new RequestTask(uriMappingFactory.getLocationEntered(place));
    }

    public RequestTask locationExited(final String place) {
        return new RequestTask(uriMappingFactory.getLocationExited(place));
    }

    public RequestTask locationUpdated(final String location) {
        return new RequestTask(uriMappingFactory.getLocationUpdated(location));
    }
}
