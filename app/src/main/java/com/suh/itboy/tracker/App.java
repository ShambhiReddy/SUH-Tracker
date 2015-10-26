package com.suh.itboy.tracker;

import android.app.Application;

import com.suh.itboy.tracker.Helper.DatabaseHelper;
import com.suh.itboy.tracker.Helper.GoogleApi.GoogleApiHelper;

/**
 * Created by itboy on 10/8/2015.
 */
public class App extends Application {
    private GoogleApiHelper googleApiHelper;
    private DatabaseHelper databaseHelper;

    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        databaseHelper = new DatabaseHelper(getApplicationContext());
        googleApiHelper = new GoogleApiHelper(getApplicationContext());
    }

    public static synchronized App getInstance() {
        return mInstance;
    }

    public GoogleApiHelper getGoogleApiHelperInstance() {
        return this.googleApiHelper;
    }
    public static GoogleApiHelper getGoogleApiHelper() {
        return getInstance().getGoogleApiHelperInstance();
    }

    public DatabaseHelper getDatabaseHelperInstance() {
        return databaseHelper;
    }
    public static DatabaseHelper getDatabaseHelper() {
        return getInstance().getDatabaseHelperInstance();
    }
}
