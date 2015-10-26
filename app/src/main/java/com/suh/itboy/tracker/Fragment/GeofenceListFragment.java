package com.suh.itboy.tracker.Fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.suh.itboy.tracker.Adapter.GeofenceListAdapter;
import com.suh.itboy.tracker.App;
import com.suh.itboy.tracker.MapsActivity;
import com.suh.itboy.tracker.Provider.Contract.AppContract;
import com.suh.itboy.tracker.R;

public class GeofenceListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int REQUEST_MAP_LOCATION = 324;
    GeofenceListAdapter geofenceListAdapter;

    public GeofenceListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_geofence_list, container, false);

        setupRecyclerView(view);

        return view;
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        geofenceListAdapter = new GeofenceListAdapter();
        recyclerView.setAdapter(geofenceListAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int itemPosition = viewHolder.getAdapterPosition();
//                Toast.makeText(MainActivity.this, itemPosition + " item Swiped.", Toast.LENGTH_SHORT).show();
                if (geofenceListAdapter.remove(itemPosition, getActivity().getContentResolver()) != 0) {
                    Toast.makeText(getActivity(), "Item Removed at: " + itemPosition, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Error to remove item at: " + itemPosition, Toast.LENGTH_SHORT).show();
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void addNewGeofence(View view) {
        Intent intent = new Intent(getActivity(), MapsActivity.class);
        startActivityForResult(intent, REQUEST_MAP_LOCATION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_MAP_LOCATION) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                LatLng latLng = (LatLng) bundle.get(MapsActivity.EXTRA_MAP_LOCATION);
                int radius = bundle.getInt(MapsActivity.EXTRA_MAP_RADIUS);

                Toast.makeText(getActivity(), "Radius: " + String.valueOf(radius)
                        + "\n" + "Longitude: " + latLng.longitude
                        + "\n" + "Latitude: " + latLng.latitude
                        , Toast.LENGTH_LONG).show();

                ContentValues contentValues = new ContentValues();
                contentValues.put(AppContract.GeofenceEntry.COLUMN_TITLE, bundle.getString(MapsActivity.EXTRA_TITLE));
                contentValues.put(AppContract.GeofenceEntry.COLUMN_ENTER_STRING, bundle.getString(MapsActivity.EXTRA_ENTER_STRING));
                contentValues.put(AppContract.GeofenceEntry.COLUMN_EXIT_STRING, bundle.getString(MapsActivity.EXTRA_EXIT_STRING));
                contentValues.put(AppContract.GeofenceEntry.COLUMN_TRANSITION_TYPE, bundle.getInt(MapsActivity.EXTRA_TRANSITION_TYPE));
                contentValues.put(AppContract.GeofenceEntry.COLUMN_EXPIRATION_DURATION, bundle.getLong(MapsActivity.EXTRA_EXPIRATION_DURATION));
                contentValues.put(AppContract.GeofenceEntry.COLUMN_RADIUS, radius);
                contentValues.put(AppContract.GeofenceEntry.COLUMN_REQUEST_ID, getRequestIdForGeofence());
                contentValues.put(AppContract.LocationEntry.COLUMN_LATITUDE, latLng.latitude);
                contentValues.put(AppContract.LocationEntry.COLUMN_LONGITUDE, latLng.longitude);
                getActivity().getContentResolver().insert(AppContract.GeofenceEntry.CONTENT_URI, contentValues);
            }
        }
    }

    private String getRequestIdForGeofence() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), AppContract.GeofenceEntry.CONTENT_URI, null, null, null, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        geofenceListAdapter.swapCursor(data);
        App.getGoogleApiHelper().updateGeofenceApi();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        geofenceListAdapter.swapCursor(null);
    }

}
