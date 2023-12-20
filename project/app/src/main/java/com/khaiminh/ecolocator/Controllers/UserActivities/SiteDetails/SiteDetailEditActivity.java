package com.khaiminh.ecolocator.Controllers.UserActivities.SiteDetails;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.khaiminh.ecolocator.R;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;


public class SiteDetailEditActivity extends AppCompatActivity {

    private EditText editTextSiteName, editTextSiteDescription, editTextAdditionalInfo;
    private String siteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_detail_edit);

        // Initialize UI components
        editTextSiteName = findViewById(R.id.editTextSiteName);
        editTextSiteDescription = findViewById(R.id.editTextSiteDescription);
        editTextAdditionalInfo = findViewById(R.id.editTextAdditionalInfo);
        Button buttonSave = findViewById(R.id.buttonSave);

        // Retrieve the site ID passed from the previous activity
        siteId = getIntent().getStringExtra("siteId");

        // Set up button click listener
        buttonSave.setOnClickListener(v -> saveSiteDetails());

        Button backButton = findViewById(R.id.backButton); // assuming you have a button with id backButton
        backButton.setOnClickListener(v -> finish()); // finish this activity to return to HomeActivity
    }

    private void saveSiteDetails() {
        String name = editTextSiteName.getText().toString().trim();
        String description = editTextSiteDescription.getText().toString().trim();
        String additionalInfo = editTextAdditionalInfo.getText().toString().trim();

        // Validate input
        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Name and description cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update site details in the database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("locations").document(siteId)
                .update("name", name, "description", description, "additionalInfo", additionalInfo)
                .addOnSuccessListener(aVoid -> Toast.makeText(SiteDetailEditActivity.this, "Site updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(SiteDetailEditActivity.this, "Error updating site", Toast.LENGTH_SHORT).show());
    }
}
