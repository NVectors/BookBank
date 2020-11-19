package com.example.bookbank.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.bookbank.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SetLocationActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionsGranted = false; //By default

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 01;
    private static final float DEFAULT_ZOOM = 15f;

    private EditText mSearchText;
    private ImageView mGPS;
    private static final String TAG = "MAP";
    private double currentLat;
    private double currentLong;

    private FirebaseFirestore db;

    /**
     * Get Location permission from user after starting the activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location);
        mSearchText =(EditText) findViewById(R.id.map_search);
        mGPS = (ImageView) findViewById(R.id.icon_gps);

        /** Get request document name from RequestsActivity */
        // -> ADD THIS -> final String requestDoc = getIntent().getStringExtra("REQUEST_DOC");
        final String requestDoc = "iyijYWL2nsbRtuTwcnYc"; //Just to test for now

        /** Get instance of Firestore */
        db = FirebaseFirestore.getInstance();

        /** Get top level reference to the book in collection  by ID */
        final DocumentReference requestReference = db.collection("Request").document(requestDoc);

        getLocationPermission();
        initMap();  //Initialize the map
        init();     //Initialize the search bar & current location button
        /** Permissions is granted, get the device current location */
        if (mLocationPermissionsGranted){
            getDeviceLocation();    // Will also mark and move camera to the current location found
        }
        else{
            /** Hide the GPS widget that gets device current location */
            mGPS.setVisibility(View.INVISIBLE); // Device location permission is not granted
        }

        /** If the confirm button is clicked, store required data into the database and exit */
        Button confirmed = (Button) findViewById(R.id.confirm_location);
        confirmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Log.d(TAG, "Confirming Location at: Latitude: " + currentLat + " Longitude: " + currentLong);
                    Log.d(TAG, "CURRENT LOCATION-> Latitude: " + currentLat + " Longtitude: " + currentLong);

                    if (currentLat != 0 && currentLong != 0){
                        /** Update the fields for the document in firestore */
                        db.collection("Request").document(requestDoc).update("latitude", currentLat);
                        db.collection("Request").document(requestDoc).update("longitude", currentLong);
                        finish();
                    }
                    else{
                        Toast.makeText(SetLocationActivity.this, "Search for an Address", Toast.LENGTH_SHORT).show();
                    }
            }
        });
    }

    /**
     * Initializing of the search bar to keyword search for address
     */
    private void init(){
        Log.d(TAG, "Initializing the search bar!");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
                    //Execute method for searching
                    geoLocate();
                    mSearchText.getText().clear();
                }
                return false;
            }
        });

        Log.d(TAG, "Initializing the gps widget!");
        mGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "CLicked GPS icon!");
                if (mLocationPermissionsGranted) {
                    getDeviceLocation(); //Go to device current location
                }
                else{
                    Toast.makeText(SetLocationActivity.this, "Device's location permissions is denied", Toast.LENGTH_SHORT).show();
                }
            }
        });

        hideSoftKeyboard();
    }

    /**
     * Given a keyword input in the search bar, use geographical location to search
     */
    private void geoLocate() {
        Log.d(TAG, "Search bar was used! GeoLocating!");

        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(SetLocationActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            // Get only 1 result
            list = geocoder.getFromLocationName(searchString,1);
        }
        catch (IOException e){
            Log.e(TAG,"GeoLocating: IOException: " + e.getMessage());
            hideSoftKeyboard();
            Toast.makeText(SetLocationActivity.this, "Unable to get Current Location", Toast.LENGTH_SHORT).show();
        }

        if (list.size() > 0){ // List is not empty
            Address address = list.get(0);

            Log.d(TAG, "Found a location: " + address.toString());

            // Move the camera to the location found and pin mark it with a title
            currentLat = address.getLatitude();
            currentLong = address.getLongitude();
            moveCamera(new LatLng(currentLat, currentLong), DEFAULT_ZOOM, address.getAddressLine(0));
        }

    }

    /**
     *  Initialize the Map fragment
     */
    private void initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        Log.d(TAG, "Initializing Map!");
        // Map ID
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.set_map);
        mapFragment.getMapAsync(this);
    }

    /**
     *  Ask for permission from user before getting the current location of the device
     *  If we can get the current location, move the map to where it is pinned.
     */
    private void getDeviceLocation() {
        Log.d(TAG, "Getting the device location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Found Location!");
                            Location currentLocation = (Location) task.getResult();
                            currentLat = currentLocation.getLatitude();
                            currentLong = currentLocation.getLongitude();
                            moveCamera(new LatLng(currentLat, currentLong), DEFAULT_ZOOM, "My Location");
                        } else {
                            Log.d(TAG, "Current location is null!");
                            Toast.makeText(SetLocationActivity.this, "Unable to get Current Location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Security Exception!");
        }
    }

    /**
     * Moves the camera in the map fragment to the given latitude and longitude as parameters
     * Sets a pin marker at the given location with a title as well (if not "My Location")
     * @param latLng
     * @param zoom
     */
    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "Moving camera to: Lat: " + latLng.latitude + ", Lng: " + latLng.longitude);

        hideSoftKeyboard();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location")){ // Title != "My Location"
            MarkerOptions options = new MarkerOptions().position(latLng).title(title);
            /** Allow user to long press the marker to enable dragging */
            options.draggable(true);    // Allow adjustments to where the marker is on the map
            mMap.addMarker(options);    // Add marker to the map
        }
    }

    /**
     * Check if Location permission access is allowed by user
     */
    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
            }
        } else {
            // Ask user for permission
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Map is ready!");
        mMap = googleMap;

        /** Listen for marker drags by the user */
        mMap.setOnMarkerDragListener(this);

        mMap.getUiSettings().setMyLocationButtonEnabled(false); // Remove button to go back to current location

        if (mLocationPermissionsGranted) {
            getDeviceLocation(); // Get current location

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true); // Set blue marker of where current location is
        }

    }

    /**
     * Close and hide the keyboard
     */
    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchText.getWindowToken(), 0);
    }

    /**
     * Called initially when a marker is dragged
     * @param marker
     */
    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    /**
     * Called constantly when a marker is dragged
     * @param marker
     */
    @Override
    public void onMarkerDrag(Marker marker) {

    }

    /**
     * Called at the end of drag of a marker
     * Get the position of the marker and convert to latitude and longitude.
     * Store latitude and longitude into variables, for later access to store into firestore
     * @param marker
     */
    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng location = marker.getPosition();
        currentLat = location.latitude;
        currentLong = location.longitude;

        Log.d(TAG, "DRAGGED MARKER -> LATITUDE: " + currentLat + " LONGITUDE: " + currentLong);
    }
}