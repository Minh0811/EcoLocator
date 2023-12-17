package com.khaiminh.ecolocator.UserActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.khaiminh.ecolocator.R;

public class SiteDetailsActivity extends AppCompatActivity {

    private TextView tvSiteName, tvSiteDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_details);

        tvSiteName = findViewById(R.id.tvSiteName);
        tvSiteDescription = findViewById(R.id.tvSiteDescription);

        String siteId = getIntent().getStringExtra("siteId");
        if (siteId != null) {
            fetchSiteDetails(siteId);
        } else {
            // Handle the case where siteId is not passed or is null
        }

        Button backButton = findViewById(R.id.backButton); // assuming you have a button with id backButton
        backButton.setOnClickListener(v -> finish()); // finish this activity to return to HomeActivity
    }

    private void fetchSiteDetails(String siteId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("locations").document(siteId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                String description = documentSnapshot.getString("description");
                // ... extract other details

                updateUIDetails(name, description);
            } else {
                // Handle case where the document does not exist
            }
        }).addOnFailureListener(e -> {
            // Handle any errors
        });
    }

    private void updateUIDetails(String name, String description) {
        tvSiteName.setText(name);
        tvSiteDescription.setText(description);
        // ... update other UI components
    }
}

