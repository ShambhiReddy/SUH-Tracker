package com.suh.itboy.tracker.Helper.GoogleApi;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.suh.itboy.tracker.Helper.GeofenceHelper;

/**
 * Created by itboy on 10/7/2015.
 */
public class GoogleApiHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = GoogleApiHelper.class.getSimpleName();
    private static final int PLAY_SERVICES_REQUEST_CODE = 10022;

    Context context;

    GoogleApiClient mGoogleApiClient;
    LocationServicesHelper locationServicesHelper;
    GeofenceHelper geofenceHelper;

    LocationListener locationListener = null;

    public GoogleApiHelper(Context context) {
        this.context = context;
        checkPlayServices();
        checkLocationServices();
        buildGoogleApiClient();
        connect();
        locationServicesHelper = new LocationServicesHelper(this);
        geofenceHelper = new GeofenceHelper(context);
    }

    public void connect() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    public void disconnect() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public boolean isConnected() {
        if (mGoogleApiClient != null) {
            return mGoogleApiClient.isConnected();
        } else {
            return false;
        }
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            /*if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, context, PLAY_SERVICES_REQUEST_CODE).show();
            } else {
            }*/
            Log.d(TAG, "Play Services is not suppported on this device");
            return false;
        }
        return true;
    }
    public void checkLocationServices() {
        // Get Location Manager and check for GPS & Network location services
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // Build the alert dialog
            /*AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Location Services Not Active");
            builder.setMessage("Please enable Location Services and GPS");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();*/
            Log.d(TAG, "Location Service Not Enabled");
        }
    }

    public Location getLastKnownLocation() {
        Location location = null;
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        return location;
    }

    public void addLocationLisetener(LocationListener locationListener) {
        this.locationListener = locationListener;
    }
    public void removeLocationLisetener() {
        this.locationListener = null;
    }

    public void updateGeofenceApi() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            geofenceHelper.update(mGoogleApiClient);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationServicesHelper.startLocationUpdates(mGoogleApiClient);
        geofenceHelper.addGeofenceApi(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: googleApiClient.connect()");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: connectionResult.toString() = " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: location => " + location.getLongitude() + "," + location.getLatitude());

        if (locationListener != null) {
            Log.d(TAG, "Location listener forward");
            locationListener.onLocationChanged(location);
        }
    }
}
