package com.suh.itboy.tracker.Model;

import android.database.Cursor;
import android.location.Location;

import com.google.android.gms.location.Geofence;
import com.suh.itboy.tracker.Provider.Contract.AppContract;
import com.suh.itboy.tracker.R;

/**
 * Created by itboy on 10/15/2015.
 */
public class EventItemModel {
    private static final String TAG = EventItemModel.class.getSimpleName();
    private long id;
    private String createTime;
    private Location triggerLocation;
    private int transitionType;
    private GeofenceListItemModel geofenceListItemModel;

    public void parseFromCursor(Cursor cursor) {
        setId(cursor.getLong(cursor.getColumnIndex(AppContract.EventEntry._ID)));
        this.createTime = cursor.getString(cursor.getColumnIndex(AppContract.EventEntry.COLUMN_CREATE_TIME));
        this.transitionType = cursor.getInt(cursor.getColumnIndex(AppContract.EventEntry.COLUMN_TRANSITION_TYPE));

        triggerLocation = new Location(TAG);
        triggerLocation.setLatitude(cursor.getDouble(cursor.getColumnIndex(AppContract.LocationEntry.COLUMN_LATITUDE)));
        triggerLocation.setLongitude(cursor.getDouble(cursor.getColumnIndex(AppContract.LocationEntry.COLUMN_LONGITUDE)));
        triggerLocation.setAccuracy(cursor.getFloat(cursor.getColumnIndex(AppContract.LocationEntry.COLUMN_ACCURACY)));

        geofenceListItemModel = new GeofenceListItemModel(
                cursor.getColumnName(
                        cursor.getColumnIndex(AppContract.GeofenceEntry.COLUMN_TITLE)),
                        0.0, 0.0
        );
        geofenceListItemModel.setEnterString(cursor.getString(cursor.getColumnIndex(AppContract.GeofenceEntry.COLUMN_ENTER_STRING)));
        geofenceListItemModel.setExitString(cursor.getString(cursor.getColumnIndex(AppContract.GeofenceEntry.COLUMN_EXIT_STRING)));
    }

    public String getCreateTime() {
        return createTime;
    }

    public Location getTriggerLocation() {
        return triggerLocation;
    }

    public int getTransitionType() {
        return transitionType;
    }

    public GeofenceListItemModel getGeofenceListItemModel() {
        return geofenceListItemModel;
    }

    public String getTransitionString() {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return geofenceListItemModel.getEnterString();
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return geofenceListItemModel.getExitString();
            default:
                return "Unknown";
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
