package com.suh.itboy.tracker.Helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.suh.itboy.tracker.Provider.Contract.AppContract;

/**
 * Created by itboy on 8/2/2015.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tracker.db";
    private static final int DATABASE_VERSION = 1;

    private static final String COMMA = ",";
    private static final String TEXT_NO_CASE = " TEXT collate nocase";
    private static final String TEXT = " TEXT,";

    private final String CREATE_LOCATION_TABLE = "CREATE TABLE "
            + AppContract.LocationEntry.TABLE + " ("
            + AppContract.LocationEntry._ID+ " integer primary key autoincrement, "
            + AppContract.LocationEntry.COLUMN_LONGITUDE + " double not null, "
            + AppContract.LocationEntry.COLUMN_LATITUDE + " double not null, "
            + AppContract.LocationEntry.COLUMN_ACCURACY + " integer, "
            + AppContract.LocationEntry.COLUMN_ADDRESS + TEXT_NO_CASE + " "
            + " )";

    private final String CREATE_GEOFENCE_TABLE = "CREATE TABLE "
            + AppContract.GeofenceEntry.TABLE + " ("
            + AppContract.GeofenceEntry._ID+ " integer primary key autoincrement, "
            + AppContract.GeofenceEntry.COLUMN_LOCATION_ID + " integer not null, "
            + AppContract.GeofenceEntry.COLUMN_RADIUS + " integer not null, "
            + AppContract.GeofenceEntry.COLUMN_REQUEST_ID + " integer not null, "
            + AppContract.GeofenceEntry.COLUMN_TRANSITION_TYPE + " integer not null, "
            + AppContract.GeofenceEntry.COLUMN_EXPIRATION_DURATION + " integer not null, "
            + AppContract.GeofenceEntry.COLUMN_TITLE + TEXT_NO_CASE + " , "
            + AppContract.GeofenceEntry.COLUMN_ENTER_STRING + TEXT_NO_CASE + " , "
            + AppContract.GeofenceEntry.COLUMN_EXIT_STRING + TEXT_NO_CASE + " , "
            + AppContract.GeofenceEntry.COLUMN_CREATE_TIME + " datetime default current_timestamp, "
            + AppContract.GeofenceEntry.COLUMN_UPDATE_TIME + " datetime default current_timestamp "
            + " )";

    private final String CREATE_EVENT_TABLE = "CREATE TABLE "
            + AppContract.EventEntry.TABLE + " ("
            + AppContract.EventEntry._ID + " integer primary key autoincrement, "
            + AppContract.EventEntry.COLUMN_GEOFENCE_ID + " integer not null, "
            + AppContract.EventEntry.COLUMN_TRIGGER_LOCATION_ID + " integer not null, "
            + AppContract.EventEntry.COLUMN_TRANSITION_TYPE + " integer not null, "
            + AppContract.EventEntry.COLUMN_CREATE_TIME + " datetime default current_timestamp )";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOCATION_TABLE);
        db.execSQL(CREATE_GEOFENCE_TABLE);
        db.execSQL(CREATE_EVENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + AppContract.LocationEntry.TABLE);
        db.execSQL("DROP TABLE " + AppContract.GeofenceEntry.TABLE);
        db.execSQL("DROP TABLE " + AppContract.EventEntry.TABLE);
        onCreate(db);
    }
}