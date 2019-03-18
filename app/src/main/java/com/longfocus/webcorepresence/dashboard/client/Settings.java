package com.longfocus.webcorepresence.dashboard.client;

import com.longfocus.webcorepresence.ParseUtils;
import com.longfocus.webcorepresence.dashboard.js.Place;

public class Settings {

    private Place[] places;

    public Place[] getPlaces() {
        return places;
    }

    public void setPlaces(final Place[] places) {
        this.places = places;
    }

    @Override
    public String toString() {
        return ParseUtils.toJson(this);
    }
}
