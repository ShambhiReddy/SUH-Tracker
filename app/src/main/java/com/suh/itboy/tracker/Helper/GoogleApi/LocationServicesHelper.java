package com.suh.itboy.tracker.Helper.GoogleApi;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by itboy on 10/7/2015.
 */
public class LocationServicesHelper {
    private static final String TAG = LocationServicesHelper.class.getSimpleName();

    LocationRequest mLocationRequest;
    LocationListener locationListener;

    public LocationServicesHelper(LocationListener locationListener) {
        this.locationListener = locationListener;
        buildLocationRequest();
    }

    public LocationRequest buildLocationRequest() {

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(60 * 1000)
                .setFastestInterval(1000);

        return mLocationRequest;
    }

    public void startLocationUpdates(GoogleApiClient mGoogleApiClient) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, locationListener);
        }
    }

    public void stopLocationUpdates(GoogleApiClient mGoogleApiClient) {
        Log.d(TAG, "stopLocationUpdates");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationListener);
            mGoogleApiClient.disconnect();
        }
    }
}
