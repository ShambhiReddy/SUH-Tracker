package com.suh.itboy.tracker.Service;

import android.app.IntentService;
import android.app.Notification;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.suh.itboy.tracker.Provider.Contract.AppContract;
import com.suh.itboy.tracker.R;

import java.util.List;

/**
 * Created by itboy on 10/6/2015.
 */
public class GeofenceIntentService extends IntentService {
    public static final String TAG = GeofenceIntentService.class.getSimpleName();
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public GeofenceIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Geofence Intent Got.");
        Toast.makeText(this, "Geofence Intent Got", Toast.LENGTH_SHORT).show();
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.d(TAG,"GeoFenceError: " + geofencingEvent.toString());
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence : geofenceList) {
            Cursor cursor = getContentResolver().query(AppContract.GeofenceEntry.CONTENT_URI, null,
                    AppContract.GeofenceEntry.COLUMN_REQUEST_ID + " = ?", new String[]{geofence.getRequestId()}, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    handleGeofenceTransition(cursor, geofenceTransition, geofencingEvent.getTriggeringLocation());
                }
            } else {
                //Error cursor null
                Log.d(TAG, "Cursor null : " + geofence.getRequestId());
            }
        }

        Log.d(TAG, "GeoFenceTransition: " + String.valueOf(geofencingEvent.getGeofenceTransition()));
    }

    private void handleGeofenceTransition(Cursor cursor, int geofenceTransition, Location triggeringLocation) {
        String title = cursor.getString(cursor.getColumnIndex(AppContract.GeofenceEntry.COLUMN_TITLE));
        int transitionType = cursor.getInt(cursor.getColumnIndex(AppContract.GeofenceEntry.COLUMN_TRANSITION_TYPE));

        if (checkTransitionType(transitionType, geofenceTransition)) {
            NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(this);
            notificationCompat
                    .setSmallIcon(getGeofenceIcon(geofenceTransition))
                    .setContentTitle(title)
                    .setContentText(getTransitionString(cursor, geofenceTransition));
            Notification notification = notificationCompat.build();
            NotificationManagerCompat.from(this).notify(geofenceTransition, notification);

            addEventToDatabase(cursor, geofenceTransition, triggeringLocation);
        }
    }

    private void addEventToDatabase(Cursor geofenceCursor, int geofenceTransition, Location triggeringLocation) {
        ContentValues values = new ContentValues();
        values.put(AppContract.EventEntry.COLUMN_GEOFENCE_ID, geofenceCursor.getString(geofenceCursor.getColumnIndex(AppContract.GeofenceEntry._ID)));
        values.put(AppContract.EventEntry.COLUMN_TRANSITION_TYPE, String.valueOf(geofenceTransition));
        values.put(AppContract.LocationEntry.COLUMN_LATITUDE, triggeringLocation.getLatitude());
        values.put(AppContract.LocationEntry.COLUMN_LONGITUDE, triggeringLocation.getLongitude());
        values.put(AppContract.LocationEntry.COLUMN_ACCURACY, triggeringLocation.getAccuracy());
        getContentResolver().insert(AppContract.EventEntry.CONTENT_URI,values);
    }

    private boolean checkTransitionType(int transitionType, int geofenceTransition) {
        if (transitionType == geofenceTransition) return true;

        if (transitionType == (Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT))
            if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
                return true;

        return false;
    }

    private String getTransitionString(Cursor cursor, int geofenceTransition) {
        String enterString = cursor.getString(cursor.getColumnIndex(AppContract.GeofenceEntry.COLUMN_ENTER_STRING));
        String exitString = cursor.getString(cursor.getColumnIndex(AppContract.GeofenceEntry.COLUMN_EXIT_STRING));

        switch (geofenceTransition) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return enterString;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return exitString;
            default:
                return getTransitionInString(geofenceTransition);
        }
    }

    private int getGeofenceIcon(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return R.drawable.enter;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return R.drawable.exit;
            default:
                return R.mipmap.ic_launcher;
        }
    }

    private String getTransitionInString(int geofenceTransition) {

        switch (geofenceTransition) {
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "GEOFENCE_TRANSITION_EXIT";
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "GEOFENCE_TRANSITION_ENTER";
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return "GEOFENCE_TRANSITION_DWELL";
            default:
                return "Unknown Geofence Transition";
        }
    }
}
