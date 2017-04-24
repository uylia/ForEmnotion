package com.test.juliya.foremnotion.controller;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.test.juliya.foremnotion.model.CardModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import cz.msebera.android.httpclient.Header;

/**
 * Created by juliya on 23.04.2017.
 */

public class GoogleRouteService extends AsyncHttpResponseHandler {
    private Context context;
    private OnSearchRecieved listener;
    private int distanceMetres = 0;
    private String startAddress = "", endAddress = "";

    public GoogleRouteService(OnSearchRecieved listener, Context context) {
        this.listener = listener;
        this.context = context;
    }

    public String makeStringURL(double sourcelat, double sourcelon, double destlat, double destlon) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString(sourcelon));
        urlString.append("&destination=");
        urlString.append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlon));
        urlString.append("&sensor=false&mode=walking&alternatives=false");
        Log.d("REQUEST", urlString.toString());
        return urlString.toString();
    }

    /* make request to get points betweens two markers 
    *  request is made in another thread*/
    public void sendRequest(double sourcelat, double sourcelon, double destlat, double destlon) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        client.post(makeStringURL(sourcelat, sourcelon, destlat, destlon), params, this);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        String s = new String(responseBody);
        if (listener != null) {
            listener.onSearch(getRoutePointList(s), makeCardModel());
        }
    }

    private CardModel makeCardModel() {
        if(distanceMetres == 0 || startAddress.equals("") || endAddress.equals("")) return null;
        CardModel card = new CardModel();
        card.setTitleSource(startAddress);
        card.setTitleDestination(endAddress);
        card.setValueDistance(distanceMetres);
        return card;
    }

    /* parse responce from google */
    private ArrayList<LatLng> getRoutePointList(String result) {
        JSONObject json;
        JSONArray routeArray, legsArray;
        JSONObject routes, distance;
        JSONObject overviewPolylines;
        String pointsString;

        try {
            json = new JSONObject(result);
            routeArray = json.getJSONArray("routes");
            if (routeArray.length() > 0){
                routes = routeArray.getJSONObject(0);
                legsArray = routes.getJSONArray("legs");
                distance = legsArray.getJSONObject(0);
                distanceMetres = distance.getJSONObject("distance").getInt("value");
                endAddress = distance.getString("end_address");
                startAddress = distance.getString("start_address");
                overviewPolylines = routes.getJSONObject("overview_polyline");
                pointsString = overviewPolylines.getString("points");
                return decodePoly(pointsString);
            } else
                return null;
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private ArrayList<LatLng> decodePoly(String pointsString) {
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = pointsString.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = pointsString.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = pointsString.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

    }


    public interface OnSearchRecieved {
        void onSearch(ArrayList<LatLng> pointList, CardModel cardModel);
    }
}
