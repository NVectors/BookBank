package com.example.bookbank.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.bookbank.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class ViewLocationActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;

    private static final float DEFAULT_ZOOM = 15f;

    private static final String TAG = "MAP";
    private double bookLat;
    private double bookLong;

    private FirebaseFirestore db;

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
        final CollectionReference collectionReference = db.collection("Request");

        initMap();  //Initialize the map
//        /**  Realtime updates, snapshot is the state of the database at any given point of time */
//        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            /** Method is executed whenever any new event occurs in the remote database */
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
//
//                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
//                    // Tests
//                    Log.d(TAG, "BOOK LOCATION-> Latitude: " + String.valueOf(doc.getData().get("latitude")) + " Longitude: " + String.valueOf(doc.getData().get("longitude")));
//
//                    bookLat = (Double) Double.parseDouble(doc.getData().get("latitude").toString());
//                    bookLong = (Double) Double.parseDouble(doc.getData().get("longitude").toString());
//                }
//            }
//        });

//        /** Move to location retrieved from database */
//        //moveCamera(new LatLng(bookLat, bookLong), DEFAULT_ZOOM, "Book's Location");
//
//        /** If exit button is clicked, close the activity */
//        Button exit = (Button) findViewById(R.id.exit_button);
//        exit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
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

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
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
        Toast.makeText(this, "Map is ready!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Map is ready!");
        mMap = googleMap;

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
        mMap.getUiSettings().setMyLocationButtonEnabled(false); // Remove button to go back to current location
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
    }
}