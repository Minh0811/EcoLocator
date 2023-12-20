package com.khaiminh.ecolocator.Controllers.UserActivities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

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
import com.khaiminh.ecolocator.Controllers.UserActivities.SiteDetails.SiteDetailsActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.tasks.OnSuccessListener;
import android.Manifest;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.location.Location;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class LocationsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private SearchView searchView;
    private List<Marker> markers = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;
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

        searchView = findViewById(R.id.searchView);
        setupSearchView();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterMarkers(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterMarkers(newText);
                return false;
            }
        });
    }

    private void filterMarkers(String text) {
        for (Marker marker : markers) {
            if (marker.getTitle().toLowerCase().contains(text.toLowerCase())) {
                marker.setVisible(true);
            } else {
                marker.setVisible(false);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Check for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mMap.setMyLocationEnabled(true);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations, this can be null.
                            if (location != null) {
                                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                            }
                        }
                    });
        }

        loadLocations();
    }

    private void loadLocations() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("locations").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    GeoPoint geoPoint = document.getGeoPoint("coordinates");
                    String name = document.getString("name");
                    if (geoPoint != null) {
                        LatLng location = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                        Marker marker = mMap.addMarker(new MarkerOptions().position(location).title(name));
                        markers.add(marker);
                        marker.setTag(document.getId()); // Store the document ID in the marker
                    }
                }
            } else {
                Log.w("LocationsActivity", "Error getting documents.", task.getException());
            }
        });

        mMap.setOnMarkerClickListener(marker -> {
            String siteId = (String) marker.getTag();
            new AlertDialog.Builder(LocationsActivity.this)
                    .setTitle("View Site Details")
                    .setMessage("Do you want to view the details of this site?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Intent intent = new Intent(LocationsActivity.this, SiteDetailsActivity.class);
                        intent.putExtra("siteId", siteId);
                        startActivity(intent);
                    })
                    .setNegativeButton("No", null)
                    .show();

            return true;
        });
    }
    // Add this method to handle the permission request response
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted. If the map is already ready, set the user's location.
                if (mMap != null) {
                    onMapReady(mMap);
                }
            } else {
                // Permission was denied. You can add further actions to handle this case.
            }
        }
    }


    // Add a constant for the location permission request
    private static final int REQUEST_LOCATION_PERMISSION = 1;
}
