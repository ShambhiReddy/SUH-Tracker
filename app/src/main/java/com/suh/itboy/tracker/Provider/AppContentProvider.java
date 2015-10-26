package com.suh.itboy.tracker.Provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.suh.itboy.tracker.Helper.DatabaseHelper;
import com.suh.itboy.tracker.Provider.Contract.AppContract;

/**
 * Created by itboy on 10/8/2015.
 */
public class AppContentProvider extends ContentProvider {
    private static final String TAG = AppContentProvider.class.getSimpleName();

    private static final UriMatcher URI_MATCHER = buildUriMatcher();

    public static final int LOCATION = 100;
    public static final int LOCATION_ID = 110;

    public static final int GEOFENCE = 200;
    public static final int GEOFENCE_ID = 210;

    public static final int EVENT = 300;
    public static final int EVENT_ID = 310;

    private static final SQLiteQueryBuilder qGeofenceWithLocationQueryBuilder;
    private static final SQLiteQueryBuilder qEventWithGeofenceQueryBuilder;


    static {
        qGeofenceWithLocationQueryBuilder = new SQLiteQueryBuilder();
        qGeofenceWithLocationQueryBuilder.setTables(
                AppContract.GeofenceEntry.TABLE
                        + " INNER JOIN "
                        + AppContract.LocationEntry.TABLE
                        + " ON "
                        + AppContract.GeofenceEntry.TABLE + "." + AppContract.GeofenceEntry.COLUMN_LOCATION_ID
                        + " = "
                        + AppContract.LocationEntry.TABLE + "." + AppContract.LocationEntry._ID
        );

        qEventWithGeofenceQueryBuilder = new SQLiteQueryBuilder();
        qEventWithGeofenceQueryBuilder.setTables(
                AppContract.EventEntry.TABLE
                        + " INNER JOIN "
                        + AppContract.GeofenceEntry.TABLE
                        + " ON "
                        + AppContract.EventEntry.TABLE + "." + AppContract.EventEntry.COLUMN_GEOFENCE_ID
                        + " = "
                        + AppContract.GeofenceEntry.TABLE + "." + AppContract.GeofenceEntry._ID
                        + " INNER JOIN "
                        + AppContract.LocationEntry.TABLE
                        + " ON "
                        + AppContract.EventEntry.TABLE + "." + AppContract.EventEntry.COLUMN_TRIGGER_LOCATION_ID
                        + " = "
                        + AppContract.LocationEntry.TABLE + "." + AppContract.LocationEntry._ID
        );
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AppContract.AUTHORITY;

        matcher.addURI(authority, AppContract.PATH_LOCATION, LOCATION);
        matcher.addURI(authority, AppContract.PATH_LOCATION + "/#", LOCATION_ID);

        matcher.addURI(authority, AppContract.PATH_GEOFENCE, GEOFENCE);
        matcher.addURI(authority, AppContract.PATH_GEOFENCE + "/#", GEOFENCE_ID);

        matcher.addURI(authority, AppContract.PATH_EVENT, EVENT);
        matcher.addURI(authority, AppContract.PATH_EVENT + "/#", EVENT_ID);

        return matcher;
    }

    DatabaseHelper DBHelper;

    @Override
    public boolean onCreate() {
        this.DBHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        Cursor cursor;
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case LOCATION_ID:
                cursor = db.query(
                        AppContract.LocationEntry.TABLE,
                        projection,
                        AppContract.LocationEntry._ID + " = ?",
                        new String[]{uri.getLastPathSegment()},
                        null,
                        null,
                        null
                );
                break;

            case LOCATION:
                cursor = db.query(
                        AppContract.LocationEntry.TABLE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case GEOFENCE_ID:
                cursor = qGeofenceWithLocationQueryBuilder.query(
                        db,
                        projection,
                        AppContract.GeofenceEntry._ID + " = ?",
                        new String[]{uri.getLastPathSegment()},
                        null,
                        null,
                        null);
                break;
            case GEOFENCE:
                cursor = qGeofenceWithLocationQueryBuilder.query(
                        db,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case EVENT:
                cursor = qEventWithGeofenceQueryBuilder.query(
                        db,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case LOCATION_ID:
                return AppContract.LocationEntry.CONTENT_ITEM_TYPE;
            case LOCATION:
                return AppContract.LocationEntry.CONTENT_TYPE;
            case GEOFENCE_ID:
                return AppContract.GeofenceEntry.CONTENT_ITEM_TYPE;
            case GEOFENCE:
                return AppContract.GeofenceEntry.CONTENT_TYPE;
            case EVENT:
                return AppContract.EventEntry.CONTENT_TYPE;
            case EVENT_ID:
                return AppContract.EventEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        Uri returnUri;
        long insertId;
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case LOCATION:
                throw new UnsupportedOperationException("LOCATION INSERT YET NOT IMPLEMENTED");
            case GEOFENCE:
                long location_id = insertLocation(values, db);

                ContentValues geofenceValues = new ContentValues();
                geofenceValues.put(AppContract.GeofenceEntry.COLUMN_TITLE,                  values.getAsString(AppContract.GeofenceEntry.COLUMN_TITLE));
                geofenceValues.put(AppContract.GeofenceEntry.COLUMN_ENTER_STRING,           values.getAsString(AppContract.GeofenceEntry.COLUMN_ENTER_STRING));
                geofenceValues.put(AppContract.GeofenceEntry.COLUMN_EXIT_STRING,            values.getAsString(AppContract.GeofenceEntry.COLUMN_EXIT_STRING));
                geofenceValues.put(AppContract.GeofenceEntry.COLUMN_TRANSITION_TYPE,        values.getAsInteger(AppContract.GeofenceEntry.COLUMN_TRANSITION_TYPE));
                geofenceValues.put(AppContract.GeofenceEntry.COLUMN_EXPIRATION_DURATION,    values.getAsLong(AppContract.GeofenceEntry.COLUMN_EXPIRATION_DURATION));

                geofenceValues.put(AppContract.GeofenceEntry.COLUMN_REQUEST_ID,             values.getAsString(AppContract.GeofenceEntry.COLUMN_REQUEST_ID));
                geofenceValues.put(AppContract.GeofenceEntry.COLUMN_RADIUS,                 values.getAsInteger(AppContract.GeofenceEntry.COLUMN_RADIUS));
                geofenceValues.put(AppContract.GeofenceEntry.COLUMN_LOCATION_ID,            location_id);
                insertId = db.insert(AppContract.GeofenceEntry.TABLE, null,                 geofenceValues);

                returnUri = AppContract.LocationEntry.buildLocationUri(insertId);
                break;
            case EVENT:
                long trigger_location_id = insertLocation(values, db);
                values.put(AppContract.EventEntry.COLUMN_TRIGGER_LOCATION_ID, trigger_location_id);
                insertId = db.insert(AppContract.EventEntry.TABLE, null, values);
                returnUri = AppContract.EventEntry.buildEventEntryUri(insertId);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        if (insertId < 1) {
            Log.d(TAG, "Failed to insert row into " + uri);
            throw new android.database.SQLException("Failed to insert row into " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    private long insertLocation(ContentValues values, SQLiteDatabase db) {
        ContentValues locationValues = new ContentValues();
        locationValues.put(AppContract.LocationEntry.COLUMN_LATITUDE,               values.getAsDouble(AppContract.LocationEntry.COLUMN_LATITUDE));
        locationValues.put(AppContract.LocationEntry.COLUMN_LONGITUDE, values.getAsDouble(AppContract.LocationEntry.COLUMN_LONGITUDE));

        if (values.containsKey(AppContract.LocationEntry.COLUMN_ACCURACY)) {
            locationValues.put(AppContract.LocationEntry.COLUMN_ACCURACY, values.getAsFloat(AppContract.LocationEntry.COLUMN_ACCURACY));
            values.remove(AppContract.LocationEntry.COLUMN_ACCURACY);
        }

        values.remove(AppContract.LocationEntry.COLUMN_LATITUDE);
        values.remove(AppContract.LocationEntry.COLUMN_LONGITUDE);

        return db.insert(AppContract.LocationEntry.TABLE, null,         locationValues);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        switch (URI_MATCHER.match(uri)) {
            case LOCATION:
                break;
            case GEOFENCE:
                Cursor cursor = db.query(AppContract.GeofenceEntry.TABLE,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
                if (cursor.moveToNext()) {
                    db.delete(AppContract.LocationEntry.TABLE,
                            AppContract.LocationEntry._ID + " = ?",
                            new String[]{cursor.getString(cursor.getColumnIndex(AppContract.GeofenceEntry.COLUMN_LOCATION_ID))});

                    cursor.close();

                    int returnValue =  db.delete(AppContract.GeofenceEntry.TABLE, selection, selectionArgs);
                    getContext().getContentResolver().notifyChange(uri, null);
                    return returnValue;
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //TODO: update_time must update
        return 0;
    }
}
