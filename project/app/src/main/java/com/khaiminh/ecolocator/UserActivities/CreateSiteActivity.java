package com.khaiminh.ecolocator.UserActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.khaiminh.ecolocator.R;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.DatePicker;
import android.widget.TimePicker;
import java.util.Calendar;


import com.google.android.gms.maps.OnMapReadyCallback;

public class CreateSiteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private EditText siteName, siteDescription, siteDateTime, siteAdditionalInfo;
    private GoogleMap mMap;

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
        // Implement logic to create site with the provided details
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Set up the map (e.g., set a marker, move camera)
    }
}
