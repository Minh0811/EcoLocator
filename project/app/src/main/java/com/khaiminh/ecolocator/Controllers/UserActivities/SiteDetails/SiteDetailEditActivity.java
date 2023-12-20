package com.khaiminh.ecolocator.Controllers.UserActivities.SiteDetails;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.firestore.DocumentSnapshot;
import com.khaiminh.ecolocator.R;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        String newName = editTextSiteName.getText().toString().trim();
        String newDescription = editTextSiteDescription.getText().toString().trim();
        String newAdditionalInfo = editTextAdditionalInfo.getText().toString().trim();

        // Validate input
        if (newName.isEmpty() || newDescription.isEmpty()) {
            Toast.makeText(this, "Name and description cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch current site details
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("locations").document(siteId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String currentName = document.getString("name"); // Assuming the field is named 'name'
                    // Now create the notification with the current name
                    createNotificationInFirestore(currentName);

                    // Then update the site details
                    updateSiteDetails(newName, newDescription, newAdditionalInfo);
                } else {
                    Log.d("Firestore", "No such document");
                }
            } else {
                Log.d("Firestore", "get failed with ", task.getException());
            }
        });
    }

    private void updateSiteDetails(String name, String description, String additionalInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("locations").document(siteId)
                .update("name", name, "description", description, "additionalInfo", additionalInfo)
                .addOnSuccessListener(aVoid -> Toast.makeText(SiteDetailEditActivity.this, "Site updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(SiteDetailEditActivity.this, "Error updating site", Toast.LENGTH_SHORT).show());
    }


    private void createNotificationInFirestore(String siteName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> notification = new HashMap<>();
        notification.put("title", "Site Updated: " + siteName);
        notification.put("description", "The site '" + siteName + "' you are participating in has been updated.");
        // Assuming you have a method to get the current list of participants
        List<String> participants = getCurrentParticipants(siteId); // Implement this method
        notification.put("participants", participants);

        db.collection("notifications").add(notification)
                .addOnSuccessListener(documentReference -> Log.d("Notification", "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w("Notification", "Error adding document", e));
    }


    private List<String> getCurrentParticipants(String siteId) {
        final List<String> participants = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("locations").document(siteId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<String> participantIds = (List<String>) document.get("participants");
                    if (participantIds != null) {
                        participants.addAll(participantIds);
                    }
                } else {
                    Log.d("Firestore", "No such document");
                }
            } else {
                Log.d("Firestore", "get failed with ", task.getException());
            }
        });

        return participants;
    }

}
