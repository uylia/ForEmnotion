package com.test.juliya.foremnotion.controller;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

/**
 * Created by juliya on 22.04.2017.
 */

public class LocationService implements LocationListener {
    private static volatile LocationService instance;
    private Context context;
    private LocationManager manager;
    private Location currentLocation;
    private CurrentLocationListener listener;
    private ConnectivityManager connectivityManager;

    public static LocationService getLocationInstance(Context context) {
        synchronized (LocationService.class) {
            if (instance == null)
                instance = new LocationService(context);
        }
        return instance;
    }

    private LocationService(Context context) {
        this.context = context;
        manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void setListener(CurrentLocationListener listener) {
        this.listener = listener;
    }

    @SuppressWarnings("ResourceType")
    @Override
    public void onLocationChanged(Location location) {
        if (!checkPermissions()) return;
        manager.removeUpdates(this);
        currentLocation = location;
        manager.removeUpdates(this);
        Log.d("LOG", "onLocationChanged - " + location.getProvider());
        if (listener != null) {
            listener.onGetCurrentLocation(currentLocation);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @SuppressWarnings("ResourceType")
    @Override
    public void onProviderEnabled(String provider) {
        //update current location
        Log.d("LOG", "onProviderEnabled - " + provider);
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            startSearch();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("LOG", "onProviderDisabled - " + provider);

    }

    public boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e("DEBUG", "no permission");
                return false;
            }
        }
        return true;
    }

    public boolean checkGPSConnection() {
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean isOnline() {
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("ResourceType")
    public void startSearch() {
        if (manager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
            currentLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (listener != null) listener.onGetCurrentLocation(currentLocation);
        } else {
            //find current location with GPS
            manager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, Looper.getMainLooper());
        }
        Log.d("DEBUG", "current location definition....");
    }

}
