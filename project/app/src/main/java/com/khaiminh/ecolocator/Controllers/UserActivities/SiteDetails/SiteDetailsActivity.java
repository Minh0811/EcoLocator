package com.khaiminh.ecolocator.Controllers.UserActivities.SiteDetails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.khaiminh.ecolocator.Models.Participant;
import com.khaiminh.ecolocator.R;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;


import java.util.ArrayList;
import java.util.List;

public class SiteDetailsActivity extends AppCompatActivity implements OnMapReadyCallback{

    private TextView tvSiteName, tvSiteDescription, tvdateTime, tvadditionalInfo;

    private Button joinSiteButton, leaveSiteButton;
    private String siteId, adminUid, name, description;

    private String dateTimeString, additionalInfoString;
    private List<String> participants;
    private boolean isParticipant;

    private ListView participantsListView;
    private ArrayAdapter<String> participantsAdapter;

    private RecyclerView recyclerView;
    private ParticipantsAdapter adapter;
    private List<Participant> participantList;

    private double latitude, longitude;
    private MapView mapView;
    private GoogleMap googleMap;
    private Button editSiteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_details);

        tvSiteName = findViewById(R.id.tvSiteName);
        tvSiteDescription = findViewById(R.id.tvSiteDescription);
        tvdateTime = findViewById(R.id.tvDateTime);
        tvadditionalInfo = findViewById(R.id.tvAdditionalInfo);
        joinSiteButton = findViewById(R.id.joinSiteButton);
        leaveSiteButton = findViewById(R.id.leaveSiteButton);
        editSiteButton = findViewById(R.id.editSiteButton);
        siteId = getIntent().getStringExtra("siteId");
        Log.d("SiteDetailsActivity", "onCreate: siteId = " + siteId);
        if (siteId != null) {
            fetchSiteDetails(siteId);
        } else {
            Log.d("SiteDetailsActivity", "onCreate: siteId is NULL");
        }

        Button backButton = findViewById(R.id.backButton); // assuming you have a button with id backButton
        backButton.setOnClickListener(v -> finish()); // finish this activity to return to HomeActivity

        joinSiteButton.setOnClickListener(v -> joinSite());
        leaveSiteButton.setOnClickListener(v -> leaveSite());

        recyclerView = findViewById(R.id.recyclerViewParticipants);
        participantList = new ArrayList<>();
        adapter = new ParticipantsAdapter(participantList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Initialize the MapView
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this); // 'this' refers to the SiteDetailsActivity instance

        // After fetching site details and determining the admin
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getUid().equals(adminUid)) {
            editSiteButton.setVisibility(View.VISIBLE); // Show the edit button for admin
            editSiteButton.setOnClickListener(v -> navigateToEditScreen());

        }
    }
    private void navigateToEditScreen() {
        // Intent to navigate to the site detail edit screen
        Intent intent = new Intent(SiteDetailsActivity.this, SiteDetailEditActivity.class);
        intent.putExtra("siteId", siteId); // Pass the site ID or other necessary data
        startActivity(intent);
    }
    private void fetchSiteDetails(String siteId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("locations").document(siteId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Log.d("SiteDetailsActivity", "fetchSiteDetails: Document exists");
                name = documentSnapshot.getString("name");
                description = documentSnapshot.getString("description");
                adminUid = documentSnapshot.getString("admin");


                Timestamp timestamp = documentSnapshot.getTimestamp("dateTime");
                if (timestamp != null) {
                    SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    dateTimeString = "Date: " + sdfDate.format(timestamp.toDate()) + ", Time: " + sdfTime.format(timestamp.toDate());
                } else {
                    dateTimeString = "Date: N/A, Time: N/A"; // Or any default value
                }

                additionalInfoString = documentSnapshot.getString("additionalInfo");

                // Fetch the GeoPoint
                GeoPoint geoPoint = documentSnapshot.getGeoPoint("coordinates");
                if (geoPoint != null) {
                    latitude = geoPoint.getLatitude();
                    longitude = geoPoint.getLongitude();
                    updateMap();
                    Log.d("SiteDetailsActivity", "Coordinates: Latitude = " + latitude + ", Longitude = " + longitude);
                } else {
                    // Handle the case where the GeoPoint is null
                    Log.e("SiteDetailsActivity", "GeoPoint is null");
                    // Optionally, set default values or handle the error
                }

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (currentUser != null) {
                    if (currentUser.getUid().equals(adminUid)) {
                        // Current user is the admin
                        editSiteButton.setVisibility(View.VISIBLE); // Show the edit button for admin
                        editSiteButton.setOnClickListener(v -> navigateToEditScreen());

                        joinSiteButton.setVisibility(View.GONE); // Hide the join button
                        leaveSiteButton.setVisibility(View.GONE); // Hide the leave button
                    } else {
                        // Current user is not the admin
                        isParticipant = participants != null && participants.contains(currentUser.getUid());

                        joinSiteButton.setVisibility(isParticipant ? View.GONE : View.VISIBLE);
                        leaveSiteButton.setVisibility(isParticipant ? View.GONE : View.VISIBLE);
                    }
                }

                participants = (List<String>) documentSnapshot.get("participants");
                if (participants != null) {
                    fetchParticipantDetails(participants, db);
                }

                updateUIDetails();
                isParticipant = participants != null && participants.contains(currentUser.getUid());
                Log.d("SiteDetailsActivity", "fetchSiteDetails: isParticipant = " + isParticipant);
            } else {
                Log.d("SiteDetailsActivity", "fetchSiteDetails: Document does not exist");
            }
        }).addOnFailureListener(e -> {
            Log.e("SiteDetailsActivity", "fetchSiteDetails: Error fetching site details", e);
        });
    }



    private void updateMap() {
        if (googleMap != null && latitude != 0 && longitude != 0) {
            LatLng siteLocation = new LatLng(latitude, longitude);
            googleMap.clear(); // Clear existing markers
            googleMap.addMarker(new MarkerOptions().position(siteLocation).title("Site Location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(siteLocation, 10)); // Adjust zoom level as needed
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        // If coordinates are already fetched, update the map immediately
        if (latitude != 0 && longitude != 0) {
            updateMap();
        }
    }
    private void fetchParticipantDetails(List<String> participantIds, FirebaseFirestore db) {
        Log.d("SiteDetailsActivity", "Fetching participant details");
        for (String participantId : participantIds) {
            db.collection("users").document(participantId).get().addOnSuccessListener(userSnapshot -> {
                if (userSnapshot.exists()) {
                    String username = userSnapshot.getString("name");
                    String email = userSnapshot.getString("email");
                    if (username != null && email != null) {
                        participantList.add(new Participant(username, email));
                    } else {
                        participantList.add(new Participant("Unknown User", "No Email"));
                    }
                    adapter.notifyDataSetChanged(); // Update the RecyclerView immediately
                    Log.d("SiteDetailsActivity", "Fetched details for participant: " + username);
                }
            }).addOnFailureListener(e -> {
                Log.e("SiteDetailsActivity", "Error fetching participant details", e);
                // Optionally, add a placeholder or a message indicating a fetch error
                participantList.add(new Participant("Fetch Error", ""));
                adapter.notifyDataSetChanged();
            });

        }
    }



    private void joinSite() {
        Log.d("SiteDetailsActivity", "joinSite called");
        if (siteId == null) {
            // Handle the case where siteId is null
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference siteRef = db.collection("locations").document(siteId);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            if (participants != null && participants.contains(currentUser.getUid())) {
                Toast.makeText(this, "You are already a participant.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update site's participants
            siteRef.update("participants", FieldValue.arrayUnion(currentUser.getUid()))
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "You have successfully joined the site.", Toast.LENGTH_SHORT).show();
                        // Update user's siteJoined field
                        DocumentReference userRef = db.collection("users").document(currentUser.getUid());
                        userRef.update("siteJoined", FieldValue.arrayUnion(siteId));
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                    });
        }
        isParticipant = true;
        leaveSiteButton.setEnabled(true);
    }
    private void leaveSite() {
        Log.d("SiteDetailsActivity", "leaveSite() called");

        if (siteId == null || !isParticipant) {
            Log.d("SiteDetailsActivity", "Site ID is null or user is not a participant");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference siteRef = db.collection("locations").document(siteId);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            Log.d("SiteDetailsActivity", "Current user is not null");

            siteRef.update("participants", FieldValue.arrayRemove(currentUser.getUid()))
                    .addOnSuccessListener(aVoid -> {
                        Log.d("SiteDetailsActivity", "Successfully left the site");
                        Toast.makeText(this, "You have left the site.", Toast.LENGTH_SHORT).show();

                        DocumentReference userRef = db.collection("users").document(currentUser.getUid());
                        userRef.update("siteJoined", FieldValue.arrayRemove(siteId));
                        isParticipant = false;
                        leaveSiteButton.setEnabled(false);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("SiteDetailsActivity", "Error leaving site", e);
                    });
        } else {
            Log.d("SiteDetailsActivity", "Current user is null");
        }
    }



    private void updateUIDetails() {
        tvSiteName.setText(name);
        tvSiteDescription.setText(description);
        tvdateTime.setText(dateTimeString);
        tvadditionalInfo.setText(additionalInfoString);
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}

