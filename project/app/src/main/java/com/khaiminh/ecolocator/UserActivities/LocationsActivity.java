package com.khaiminh.ecolocator.UserActivities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

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

import java.util.ArrayList;
import java.util.List;

public class LocationsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private SearchView searchView;
    private List<Marker> markers = new ArrayList<>();

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
}
