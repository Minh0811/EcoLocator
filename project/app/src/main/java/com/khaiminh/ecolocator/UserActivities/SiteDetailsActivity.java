package com.khaiminh.ecolocator.UserActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khaiminh.ecolocator.R;

import java.util.List;

public class SiteDetailsActivity extends AppCompatActivity {

    private TextView tvSiteName, tvSiteDescription;
    private Button joinSiteButton, leaveSiteButton;
    private String siteId, adminUid, name, description;
    private List<String> participants;
    private boolean isParticipant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_details);

        tvSiteName = findViewById(R.id.tvSiteName);
        tvSiteDescription = findViewById(R.id.tvSiteDescription);
        joinSiteButton = findViewById(R.id.joinSiteButton);
        leaveSiteButton = findViewById(R.id.leaveSiteButton);

        siteId = getIntent().getStringExtra("siteId");
        if (siteId != null) {
            fetchSiteDetails(siteId);
        } else {
            // Handle the case where siteId is not passed or is null
        }

        Button backButton = findViewById(R.id.backButton); // assuming you have a button with id backButton
        backButton.setOnClickListener(v -> finish()); // finish this activity to return to HomeActivity

        joinSiteButton.setOnClickListener(v -> joinSite());
        leaveSiteButton.setOnClickListener(v -> leaveSite());
    }

    private void fetchSiteDetails(String siteId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("locations").document(siteId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                name = documentSnapshot.getString("name"); // Assign values here
                description = documentSnapshot.getString("description");
                adminUid = documentSnapshot.getString("admin");
                participants = (List<String>) documentSnapshot.get("participants");

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null && !currentUser.getUid().equals(adminUid)) {
                    joinSiteButton.setVisibility(View.VISIBLE);
                    leaveSiteButton.setVisibility(View.VISIBLE);

                    isParticipant = participants != null && participants.contains(currentUser.getUid());
                    leaveSiteButton.setEnabled(isParticipant);
                }

                updateUIDetails(); // Call updateUIDetails without parameters
            } else {
                // Handle case where the document does not exist
            }
        }).addOnFailureListener(e -> {
            // Handle any errors
        });
    }


    private void joinSite() {
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

            siteRef.update("participants", FieldValue.arrayUnion(currentUser.getUid()))
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "You have successfully joined the site.", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                    });
        }
        isParticipant = true;
        leaveSiteButton.setEnabled(true);
    }
    private void leaveSite() {
        if (siteId == null || !isParticipant) {
            // Handle invalid state or user not a participant
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference siteRef = db.collection("locations").document(siteId);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            siteRef.update("participants", FieldValue.arrayRemove(currentUser.getUid()))
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "You have left the site.", Toast.LENGTH_SHORT).show();
                        isParticipant = false;
                        leaveSiteButton.setEnabled(false);
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                    });
        }
    }


    private void updateUIDetails() {
        tvSiteName.setText(name); // Use class-level variables
        tvSiteDescription.setText(description);
    }
}

