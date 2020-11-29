package com.example.bookbank.activities;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.bookbank.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class ViewLocationActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;

    private static final float DEFAULT_ZOOM = 15f;

    private static final String TAG = "MAP";
    private double bookLat;
    private double bookLong;

    private FirebaseFirestore db;
    private LatLng location;

    /**
     * Get Location permission from user after starting the activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_location);

        /** Get request document name from RequestsActivity */
        final String requestDoc = getIntent().getStringExtra("REQUEST_DOC");

        /** Get instance of Firestore */
        db = FirebaseFirestore.getInstance();

        /** Get top level reference to the collection Request */
        final DocumentReference requestReference = db.collection("Request").document(requestDoc);

        initMap();  //Initialize the map

        requestReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null  && document.exists()) {
                        String docLat = document.getData().get("latitude").toString();
                        String docLong = document.getData().get("longitude").toString();

                        // Tests
                        Log.d(TAG, "BOOK LOCATION-> Latitude: " + String.valueOf(docLat)
                                + " Longitude: " + String.valueOf(docLong));

                        bookLat = (Double) Double.parseDouble(docLat.toString());
                        bookLong = (Double) Double.parseDouble(docLong.toString());

                        try {
                            geoLocate(bookLat,bookLong);
                        } catch (IOException e) {
                            Toast.makeText(ViewLocationActivity.this, "No location is found in the database", Toast.LENGTH_SHORT).show();
                        }

                    } else { // Document is null
                        finish();
                        Toast.makeText(getApplicationContext(), "Location does not exist", Toast.LENGTH_LONG).show();
                    }
                } else { // Task is not successful
                    Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });


        /** If exit button is clicked, close the activity */
        Button exit = (Button) findViewById(R.id.exit_button);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    /**
     *  Initialize the Map fragment
     */
    private void initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        Log.d(TAG, "Initializing Map!");
        // Map ID
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.view_map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Moves the camera in the map fragment to the given latitude and longitude as parameters
     * Sets a pin marker at the given location with a title as well
     * @param latLng
     * @param zoom
     */
    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "Moving camera to: Lat: " + latLng.latitude + ", Lng: " + latLng.longitude);

        /** Move the camera to where the point of location is */
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        /** Add marker to the point of location to make it viable to user */
        MarkerOptions options = new MarkerOptions().position(latLng).title(title);
        mMap.addMarker(options);
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
        Log.d(TAG, "Map is ready!");
        mMap = googleMap;
    }

    /**
     * Given a latitude and longitude, use geographical location to search for address
     */
    private void geoLocate(double latitude, double longitude) throws IOException {
        Log.d(TAG, "GeoLocating!");
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

            Log.d(TAG, "Found: " + address + city + state + country + postalCode + knownName);

            /** Get point of location address to set as the title of the pin marker */
            String info = (address);

            /** Move to location retrieved from database */
            moveCamera(new LatLng(bookLat, bookLong), DEFAULT_ZOOM, info);

        } catch (IOException e) {
            Log.e(TAG, "GeoLocating: IOException: " + e.getMessage());
        }
    }
}