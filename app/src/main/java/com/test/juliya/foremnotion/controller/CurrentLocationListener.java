package com.test.juliya.foremnotion.controller;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by juliya on 22.04.2017.
 */

public interface CurrentLocationListener {
    void onGetCurrentLocation(Location currentLocation);
}
