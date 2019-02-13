package com.longfocus.webcorepresence.dashboard.client;

import com.longfocus.webcorepresence.dashboard.js.Place;

public class Settings {

    private Place[] places;

    // ignored
    private Object categories;

    public Place[] getPlaces() {
        return places;
    }

    public void setPlaces(final Place[] places) {
        this.places = places;
    }

    public Object getCategories() {
        return categories;
    }

    public void setCategories(final Object categories) {
        this.categories = categories;
    }
}
