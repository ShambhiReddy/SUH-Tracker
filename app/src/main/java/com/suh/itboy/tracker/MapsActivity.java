package com.suh.itboy.tracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.suh.itboy.tracker.Helper.GeofenceHelper;
import com.suh.itboy.tracker.Model.GeofenceListItemModel;
import com.suh.itboy.tracker.Provider.Contract.AppContract;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String EXTRA_MAP_LOCATION = "EXTRA_MAP_LOCATION";
    public static final String EXTRA_MAP_RADIUS = "EXTRA_MAP_RADIUS";
    public static final String EXTRA_EXPIRATION_DURATION = "EXTRA_EXPIRATION_DURATION";
    public static final String EXTRA_ENTER_STRING = "EXTRA_ENTER_STRING";
    public static final String EXTRA_EXIT_STRING = "EXTRA_EXIT_STRING";
    public static final String EXTRA_TRANSITION_TYPE = "EXTRA_TRANSITION_TYPE";
    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    AlertDialog alertDialog;
    private GoogleMap mMap;
    private LatLng requestedLocation;

    String requestedTitle;
    String enterString;
    String exitString;
    long expirationDuration;
    int transitionType;
    int requestedRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        alertDialog = (new AlertDialog.Builder(this))
                .setTitle("Enter Details")
                .setView(R.layout.map_dialog_layout)
                .setCancelable(false)
                .setPositiveButton("OK", null)
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();


        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText radiusView = (EditText) alertDialog.findViewById(R.id.radius);
                        EditText titleView = (EditText) alertDialog.findViewById(R.id.title);
                        EditText enterStringView = (EditText) alertDialog.findViewById(R.id.enterString);
                        EditText exitStringView = (EditText) alertDialog.findViewById(R.id.exitString);
                        EditText expirationDurationView = (EditText) alertDialog.findViewById(R.id.expirationDuration);
                        Spinner transitionTypeView = (Spinner) alertDialog.findViewById(R.id.transitionType);



                        if (TextUtils.isEmpty(radiusView.getText().toString())) {
                            Toast.makeText(MapsActivity.this, "Enter value of radius at least 60.", Toast.LENGTH_SHORT).show();
                            radiusView.setError("Radius must be at least 60.");
                            return;

                        } else if(TextUtils.isEmpty(expirationDurationView.getText().toString())) {
                            Toast.makeText(MapsActivity.this, "Enter duration in milli seconds", Toast.LENGTH_SHORT).show();
                            expirationDurationView.setError("Enter duration in milli seconds");
                            return;
                        }
                        requestedRadius = Integer.parseInt(radiusView.getText().toString());
                        requestedTitle = titleView.getText().toString();
                        enterString = enterStringView.getText().toString();
                        exitString = exitStringView.getText().toString();
                        expirationDuration = Long.parseLong(expirationDurationView.getText().toString());
                        transitionType = getTransitionTypeFromSpinner(transitionTypeView.getSelectedItemPosition());

/*                        Toast.makeText(MapsActivity.this, "Radius: " + radius.getText().toString()
                                + "\n Title: " + title.getText().toString(), Toast.LENGTH_SHORT).show();*/
                        if (requestedRadius < 60) {
                            Toast.makeText(MapsActivity.this, "Radius must be at least 60.", Toast.LENGTH_SHORT).show();
                            radiusView.setError("Radius must be at least 60.");

                        } else if (TextUtils.isEmpty(requestedTitle)) {
                            Toast.makeText(MapsActivity.this, "Please enter title .", Toast.LENGTH_SHORT).show();
                            titleView.setError("Please enter title!");

                        } else if (TextUtils.isEmpty(enterString)) {
                            Toast.makeText(MapsActivity.this, "Please type enter string! .", Toast.LENGTH_SHORT).show();
                            enterStringView.setError("Please type enter string!");
                        } else if (TextUtils.isEmpty(exitString)) {
                            Toast.makeText(MapsActivity.this, "Please type exit string! .", Toast.LENGTH_SHORT).show();
                            exitStringView.setError("Please type exit string!");
                        } else if (expirationDuration < (10*60*100) && expirationDuration != -1) {
                            Toast.makeText(MapsActivity.this, "Enter Duration minimum 60000 (6 min)", Toast.LENGTH_SHORT).show();
                            expirationDurationView.setError("Enter Duration minimum 60000 (6 min).");
                        } else {
                            returnOkResult();
                        }

                    }
                });
            }
        });

    }

    private int getTransitionTypeFromSpinner(int selectedItemPosition) {

        switch (selectedItemPosition) {
            case 0:
                return (Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);
            case 1:
                return Geofence.GEOFENCE_TRANSITION_ENTER;
            case 2:
                return Geofence.GEOFENCE_TRANSITION_EXIT;
            case 3:
                return Geofence.GEOFENCE_TRANSITION_DWELL;
            case 4:
                return (Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL);
            default:
                throw new UnsupportedOperationException("Unknown transition type: " + String.valueOf(selectedItemPosition));
        }
    }

    private void returnOkResult() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TITLE, requestedTitle);
        intent.putExtra(EXTRA_MAP_LOCATION, requestedLocation);
        intent.putExtra(EXTRA_MAP_RADIUS, requestedRadius);
        intent.putExtra(EXTRA_EXPIRATION_DURATION, expirationDuration);
        intent.putExtra(EXTRA_ENTER_STRING, enterString);
        intent.putExtra(EXTRA_EXIT_STRING, exitString);
        intent.putExtra(EXTRA_TRANSITION_TYPE, transitionType);
        setResult(RESULT_OK, intent);
        finish();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        addMapMarkers(googleMap);


            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(final LatLng latLng) {
                    requestedLocation = latLng;
//                    ((EditText)alertDialog.findViewById(R.id.location)).setText(latLng.latitude + ", " + latLng.longitude);
                    alertDialog.show();
                }
            });
            /*mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    Toast.makeText(MapsActivity.this, "Map: Longitutde: " + location.getLongitude() + "\n" + "Latitude: " + location.getLatitude(), Toast.LENGTH_SHORT).show();
                }
            });*/
        mMap.setMyLocationEnabled(true);
    }

    private void addMapMarkers(GoogleMap googleMap) {
        List<GeofenceListItemModel> mGeofenceList = getGeofenceList();

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        Location location = App.getGoogleApiHelper().getLastKnownLocation();

        if (location != null) {
            // Add a marker in current location

            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(currentLocation).title("Your Location").snippet("Tracking Locations")).showInfoWindow();
/*            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));*/
            boundsBuilder.include(currentLocation);
        } else {
            if (mGeofenceList.size() < 1)
                return;
        }

        for (GeofenceListItemModel item : mGeofenceList) {
            LatLng position = new LatLng(item.getLatitude(), item.getLongitude());
            googleMap.addMarker(new MarkerOptions()
                            .position(position)
                            .title(item.getTitle())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker))
            );

            googleMap.addCircle(new CircleOptions()
                            .center(position)
                            .strokeColor(Color.BLUE)
                            .fillColor(0x400000ff)
                            .strokeWidth(1)
                            .radius(item.getRadius())
            );

            boundsBuilder.include(position);
        }
        int padding = 20;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), padding));
    }

    private List<GeofenceListItemModel> getGeofenceList() {
        Cursor cursor = getContentResolver().query(AppContract.GeofenceEntry.CONTENT_URI, null, null, null, null);
        return GeofenceHelper.geofenceListFromCursor(cursor);
    }
}
