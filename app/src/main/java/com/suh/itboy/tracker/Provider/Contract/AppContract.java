package com.suh.itboy.tracker.Provider.Contract;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by itboy on 10/8/2015.
 */
public class AppContract {
    public static final String AUTHORITY = "com.suh.itboy.tracker";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_LOCATION = LocationEntry.TABLE;
    public static final String PATH_GEOFENCE = GeofenceEntry.TABLE;
    public static final String PATH_EVENT = EventEntry.TABLE;

    public static final class LocationEntry implements BaseColumns {
        public static final String TABLE = "location";

        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_ACCURACY = "accuracy";
        public static final String COLUMN_ADDRESS = "address";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + PATH_LOCATION;

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class GeofenceEntry implements BaseColumns {
        public static final String TABLE = "geofence";

        public static final String COLUMN_LOCATION_ID = "location_id";
        public static final String COLUMN_REQUEST_ID = "request_id";
        public static final String COLUMN_RADIUS = "radius";
        public static final String COLUMN_EXPIRATION_DURATION = "expiration_duration";
        public static final String COLUMN_CREATE_TIME = "create_time";
        public static final String COLUMN_UPDATE_TIME = "update_time";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ENTER_STRING = "enter_string";
        public static final String COLUMN_EXIT_STRING = "exit_string";
        public static final String COLUMN_TRANSITION_TYPE = "transition_type";


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_GEOFENCE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PATH_GEOFENCE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + PATH_GEOFENCE;

        public static Uri buildGeofenceUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class EventEntry implements BaseColumns {
        public static final String TABLE = "event";

        public static final String COLUMN_GEOFENCE_ID = "geofence_id";
        public static final String COLUMN_TRANSITION_TYPE = "transition_type";
        public static final String COLUMN_CREATE_TIME = "create_time";
        public static final String COLUMN_TRIGGER_LOCATION_ID = "trigger_location_id";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENT).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PATH_EVENT;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + PATH_EVENT;

        public static Uri buildEventEntryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
