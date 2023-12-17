package com.khaiminh.ecolocator.UserActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.khaiminh.ecolocator.R;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


import com.google.android.gms.maps.OnMapReadyCallback;
import android.Manifest;
import android.widget.Toast;


public class CreateSiteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private EditText siteName, siteDescription, siteDateTime, siteAdditionalInfo;
    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_site);

        siteName = findViewById(R.id.siteName);
        siteDescription = findViewById(R.id.siteDescription);
        siteDateTime = findViewById(R.id.siteDateTime);
        siteAdditionalInfo = findViewById(R.id.siteAdditionalInfo);

        // Initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        siteDateTime.setOnClickListener(v -> showDateTimePicker());

        Button btnCreateSite = findViewById(R.id.btnCreateSite);
        btnCreateSite.setOnClickListener(v -> createSite());

        Button backButton = findViewById(R.id.backButton); // assuming you have a button with id backButton
        backButton.setOnClickListener(v -> finish()); // finish this activity to return to HomeActivity
    }

    private void showDateTimePicker() {
        // Get Current Date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Date Picker Dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        // Time Picker Dialog
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(CreateSiteActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        calendar.set(Calendar.MINUTE, minute);
                                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year
                                                + " " + hourOfDay + ":" + minute;
                                        siteDateTime.setText(selectedDate);
                                    }
                                }, hour, minute, false);
                        timePickerDialog.show();
                    }
                }, year, month, day);
        datePickerDialog.show();
    }


    private void createSite() {
        String name = siteName.getText().toString().trim();
        String description = siteDescription.getText().toString().trim();
        String dateTime = siteDateTime.getText().toString().trim();
        String additionalInfo = siteAdditionalInfo.getText().toString().trim();
        TextView tvCoordinates = findViewById(R.id.tvCoordinates);
        String coordinates = tvCoordinates.getText().toString();

        // Validate input data
        if (name.isEmpty() || description.isEmpty() || dateTime.isEmpty() || coordinates.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse coordinates
        String[] parts = coordinates.split(", ");
        double latitude = Double.parseDouble(parts[0].split(": ")[1]);
        double longitude = Double.parseDouble(parts[1].split(": ")[1]);
        GeoPoint geoPoint = new GeoPoint(latitude, longitude);

        // Parse the dateTime string to Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Date parsedDate;
        try {
            parsedDate = dateFormat.parse(dateTime);
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert Date to Timestamp
        Timestamp timestamp = new Timestamp(parsedDate);

        // Create a new location object
        Map<String, Object> location = new HashMap<>();
        location.put("name", name);
        location.put("description", description);
        location.put("dateTime", timestamp);  // Use Timestamp here
        location.put("additionalInfo", additionalInfo);
        location.put("coordinates", geoPoint);

        // Save to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("locations")
                .add(location)
                .addOnSuccessListener(documentReference -> Toast.makeText(CreateSiteActivity.this, "Location added", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(CreateSiteActivity.this, "Error adding location", Toast.LENGTH_SHORT).show());
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    // Enable MyLocation Layer of Google Map
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {
            mMap.setMyLocationEnabled(true);
        }
        // Set a listener for map click.
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Clearing all the markers
                mMap.clear();

                // Adding a marker at the touched position
                mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));

                // Displaying the coordinates
                displayCoordinates(latLng);
            }
        });
    }

    private void displayCoordinates(LatLng latLng) {
        TextView tvCoordinates = findViewById(R.id.tvCoordinates);
        String coordinates = "Latitude: " + latLng.latitude + ", Longitude: " + latLng.longitude;
        tvCoordinates.setText(coordinates);
    }


    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to access the location for the map")
                    .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(CreateSiteActivity.this,
                            new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE))
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); // Add this line

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                // Permission was denied
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
