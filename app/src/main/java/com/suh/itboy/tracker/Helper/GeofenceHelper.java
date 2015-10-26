package com.suh.itboy.tracker.Helper;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.suh.itboy.tracker.App;
import com.suh.itboy.tracker.Model.GeofenceListItemModel;
import com.suh.itboy.tracker.Provider.Contract.AppContract;
import com.suh.itboy.tracker.Service.GeofenceIntentService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by itboy on 10/6/2015.
 */
public class GeofenceHelper {
//    public static final int GEOFENCE_RADIUS_METER = 100;
    private static final String TAG = GeofenceHelper.class.getSimpleName();
    Context context;
    List<GeofenceListItemModel> geofenceEntries = new ArrayList<>();
    private PendingIntent pendingIntent;

    public GeofenceHelper(Context context) {
        this.context = context;

        Cursor cursor = App.getInstance().getContentResolver().query(AppContract.GeofenceEntry.CONTENT_URI, null, null, null, null);
        geofenceListFromCursor(cursor);
    }

    public void addGeofenceApi(GoogleApiClient googleApiClient) {
        GeofencingRequest request = getGeofenceRequest();
        if (request == null)
            return;
        LocationServices.GeofencingApi.addGeofences(
                googleApiClient,
                request,
                getGeofencePendingIntent()
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    Log.d(TAG, "Geofence Added.");
                } else {
                    Log.d(TAG, String.valueOf(getErrorString(status.getStatusCode())));
                }

            }
        });
    }

    private String getErrorString(int statusCode) {
        switch (statusCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GEOFENCE_NOT_AVAILABLE";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "GEOFENCE_TOO_MANY_GEOFENCES";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "GEOFENCE_TOO_MANY_PENDING_INTENTS";
            default:
                return "Unknown Error Status code.";
        }
    }

    public void removeGeofenceApi(GoogleApiClient googleApiClient) {
        LocationServices.GeofencingApi.removeGeofences(googleApiClient, getGeofencePendingIntent());
    }

    public List<Geofence> populateGeofenceList() {
        List<Geofence> geofenceList = new ArrayList<>();
        for (GeofenceListItemModel item : geofenceEntries) {
            geofenceList.add(new Geofence.Builder()
                    .setRequestId(item.getRequestId())
                    .setCircularRegion(item.getLatitude(), item.getLongitude(), item.getRadius())
                    .setTransitionTypes(item.getTransitionType())
                    .setExpirationDuration(item.getExpirationDuration())
                    .build());
        }
        return geofenceList;
    }

    public static List<GeofenceListItemModel> geofenceListFromCursor(Cursor cursor) {
        List<GeofenceListItemModel> geofenceList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                GeofenceListItemModel item = geofenceItemFromCursor(cursor);
                geofenceList.add(item);

            }
        }

        return geofenceList;
    }

    @NonNull
    public static GeofenceListItemModel geofenceItemFromCursor(Cursor cursor) {
        String title = cursor.getString(cursor.getColumnIndex(AppContract.GeofenceEntry.COLUMN_TITLE));
        String requestId = cursor.getString(cursor.getColumnIndex(AppContract.GeofenceEntry.COLUMN_REQUEST_ID));
        String updateTime = cursor.getString(cursor.getColumnIndex(AppContract.GeofenceEntry.COLUMN_UPDATE_TIME));
        Double latitude = cursor.getDouble(cursor.getColumnIndex(AppContract.LocationEntry.COLUMN_LATITUDE));
        Double longitude = cursor.getDouble(cursor.getColumnIndex(AppContract.LocationEntry.COLUMN_LONGITUDE));
        int radius = cursor.getInt(cursor.getColumnIndex(AppContract.GeofenceEntry.COLUMN_RADIUS));
        int transitionType = cursor.getInt(cursor.getColumnIndex(AppContract.GeofenceEntry.COLUMN_TRANSITION_TYPE));
        long expirationDuration = cursor.getLong(cursor.getColumnIndex(AppContract.GeofenceEntry.COLUMN_EXPIRATION_DURATION));

        GeofenceListItemModel item = new GeofenceListItemModel(title,latitude,longitude);
        item.setRequestId(requestId);
        item.setRadius(radius);
        item.setExpirationDuration(expirationDuration);
        item.setTransitionType(transitionType);
        item.setUpdateTime(cursor.getString(cursor.getColumnIndex(AppContract.GeofenceEntry.COLUMN_UPDATE_TIME)));
        return item;
    }

    private GeofencingRequest getGeofenceRequest() {
        List<Geofence> geofenceList = populateGeofenceList();
        if (geofenceList.size() < 1)
            return null;

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.addGeofences(geofenceList);
        builder.setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);

        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (pendingIntent == null) {
            Intent intent = new Intent(context, GeofenceIntentService.class);
            pendingIntent = PendingIntent.getService(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return pendingIntent;
    }

    public void update(GoogleApiClient googleApiClient) {
        Log.d(TAG, "Geofence update request.");
        Cursor cursor = App.getInstance().getContentResolver().query(AppContract.GeofenceEntry.CONTENT_URI, null, null, null, null);
        if (needUpdate(cursor)) {
            Log.d(TAG, "needUpdate: true");
            geofenceEntries = geofenceListFromCursor(cursor);
            removeGeofenceApi(googleApiClient);
            addGeofenceApi(googleApiClient);
        }
        else
            Log.d(TAG, "needUpdate: false");
    }

    private boolean needUpdate(Cursor cursor) {
        if (cursor.getCount() != geofenceEntries.size())
            return true;
        else {
            int i =0;
            for (GeofenceListItemModel item : geofenceEntries) {
                if (cursor.moveToPosition(i)) {
                    if (!item.getUpdateTime().equalsIgnoreCase(cursor.getString(cursor.getColumnIndex(AppContract.GeofenceEntry.COLUMN_UPDATE_TIME))))
                        return true;
                    if (item.getLatitude() != cursor.getDouble(cursor.getColumnIndex(AppContract.LocationEntry.COLUMN_LATITUDE)))
                        return true;
                    if (item.getLongitude() != cursor.getDouble(cursor.getColumnIndex(AppContract.LocationEntry.COLUMN_LONGITUDE)))
                        return true;
                }
            }
        }
        return false;
    }
}
