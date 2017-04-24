package com.test.juliya.foremnotion.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.test.juliya.foremnotion.R;
import com.test.juliya.foremnotion.controller.CurrentLocationListener;
import com.test.juliya.foremnotion.controller.GoogleRouteService;
import com.test.juliya.foremnotion.controller.LocationService;
import com.test.juliya.foremnotion.controller.UpdateListCallback;
import com.test.juliya.foremnotion.model.CardModel;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by juliya on 22.04.2017.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener,
        CurrentLocationListener, GoogleRouteService.OnSearchRecieved {
    @Bind(R.id.map)
    MapView mapView;
    @Bind(R.id.reset_btn)
    FloatingActionButton resetBtn;

    private GoogleMap googleMap;
    private FragmentActivity activity;
    private LocationService locationService;
    private GoogleRouteService routeService;
    private LatLng currentLatLng, prevLatLng;
    private UpdateListCallback listener;
    private static final int PERMISSIONS_REQUEST_LOCATION = 1;

    public static MapFragment getInstance(UpdateListCallback listener) {
        MapFragment fragment = new MapFragment();
        fragment.listener = listener;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        ButterKnife.bind(this, view);
        activity = getActivity();
        locationService = LocationService.getLocationInstance(activity.getApplicationContext());
        routeService = new GoogleRouteService(this, getActivity());
        prepareMapFragment(savedInstanceState);
        return view;
    }

    private void getCurrentLocation() {
        locationService.setListener(this);
        locationService.startSearch();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null && locationService != null &&
                locationService.isOnline()&&locationService.checkGPSConnection()) mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }

    private void prepareMapFragment(Bundle savedInstanceState) {
        if (locationService.checkPermissions() && locationService.checkGPSConnection()
                && locationService.isOnline()){
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);
        } else {
//            Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            getActivity().startActivityForResult(onGPS, PERMISSIONS_REQUEST_LOCATION);
            showDialog();
        }

    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Check your network, GPS and restart the app, please.");
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getActivity().finish();
            }
        });
        builder.create().show();
    }


    @Override
    @SuppressWarnings("ResourceType")
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setOnMapClickListener(this);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.setMyLocationEnabled(false);
        getCurrentLocation();
    }


    @Override
    public void onMapClick(LatLng newLatLng) {
        // for make a route we use only internet connection
        if (locationService.isOnline()) {
            googleMap.addMarker(new MarkerOptions().position(newLatLng));
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(newLatLng));
            if (prevLatLng != null) {
                makePathInMap(newLatLng, prevLatLng);
            }
            prevLatLng = newLatLng;
        } else
            Toast.makeText(getActivity(), "no internet connection", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetCurrentLocation(Location currentLocation) {
        currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(currentLatLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
        prevLatLng = currentLatLng;
        Log.d("LOG", "onLocationMarkerPut");
    }

    private void makePathInMap(LatLng newLatLng, LatLng prevLatLng) {
        if (prevLatLng == null) {
            prevLatLng = newLatLng;
        }
        routeService.sendRequest(prevLatLng.latitude, prevLatLng.longitude,
                newLatLng.latitude, newLatLng.longitude);
    }

    @Override
    public void onSearch(ArrayList<LatLng> pointList, CardModel cardModel) {
        if (pointList == null || cardModel == null) return;
        drawRouteInMap(pointList, cardModel.getValueDistance());
        if (listener != null) {
            listener.onGetNewPoint(cardModel);
        }
    }

    private void drawRouteInMap(ArrayList<LatLng> pointList, int distanceMetres) {
        googleMap.addPolyline(new PolylineOptions()
                .addAll(pointList)
                .width(12)
                .color(getResources().getColor(distanceMetres < 100 ? R.color.colorAccent : android.R.color.holo_green_light))
                .geodesic(true)
        );
    }

    @OnClick(R.id.reset_btn)
    void onFabClick() {
        listener.onGetNewPoint(null);
        googleMap.clear();
        currentLatLng = null;
        prevLatLng = null;
    }
}
