package com.khaiminh.ecolocator.Controllers.AdminActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.khaiminh.ecolocator.Controllers.Authentication.LoginActivity;
import com.khaiminh.ecolocator.Controllers.UserActivities.SiteAdapter;
import com.khaiminh.ecolocator.R;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khaiminh.ecolocator.Models.Site;

import java.util.ArrayList;
import java.util.List;

public class AdminMainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    Button button;
    TextView textView;
    FirebaseUser user;
    private RecyclerView recyclerViewSites;
    private SiteAdapter siteAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        user = auth.getCurrentUser();

        textView = findViewById(R.id.admin_details);
        if (user != null) {
            textView.setText(user.getEmail());
        }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        // Setup RecyclerView
        recyclerViewSites = findViewById(R.id.recyclerViewSites);
        recyclerViewSites.setLayoutManager(new LinearLayoutManager(this));
        siteAdapter = new SiteAdapter(new ArrayList<>()); // Initialize with an empty list
        recyclerViewSites.setAdapter(siteAdapter);

        loadSites(); // Method to load sites from Firestore
    }
    private void loadSites() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("locations") // Replace with your actual collection name
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Site> sites = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        sites.add(document.toObject(Site.class)); // Assuming a Site model class
                    }
                    siteAdapter.updateSites(sites);
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }
}