package com.khaiminh.ecolocator.UserActivities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.khaiminh.ecolocator.R;
import androidx.appcompat.app.AppCompatActivity;

public class LocationsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_locations);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });

        // Initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("locations").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    GeoPoint geoPoint = document.getGeoPoint("coordinates");
                    String name = document.getString("name");
                    if (geoPoint != null) {
                        LatLng location = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                        Marker marker = mMap.addMarker(new MarkerOptions().position(location).title(name));
                        marker.setTag(document.getId()); // Store the document ID in the marker
                    }
                }
            } else {
                Log.w("LocationsActivity", "Error getting documents.", task.getException());
            }
        });

        // Set a listener for marker click
        mMap.setOnMarkerClickListener(marker -> {
            // Retrieve the site ID from the marker
            String siteId = (String) marker.getTag();

            // Display an AlertDialog
            new AlertDialog.Builder(LocationsActivity.this)
                    .setTitle("View Site Details")
                    .setMessage("Do you want to view the details of this site?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // User chose 'Yes', proceed to show site details
                        Intent intent = new Intent(LocationsActivity.this, SiteDetailsActivity.class);
                        intent.putExtra("siteId", siteId); // Pass the site ID
                        startActivity(intent);
                    })
                    .setNegativeButton("No", null) // User chose 'No'
                    .show();

            return true; // Return true to indicate that we have handled the event
        });
    }


}
