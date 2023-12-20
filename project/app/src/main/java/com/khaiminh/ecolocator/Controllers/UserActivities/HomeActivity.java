package com.khaiminh.ecolocator.Controllers.UserActivities;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.khaiminh.ecolocator.R;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import android.widget.PopupWindow;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khaiminh.ecolocator.Models.Site; // Ensure you have a Site model class
public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private RecyclerView recyclerViewSites;
    private SiteAdapter siteAdapter; // Adapter for the sites

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.d(TAG, "onCreate: Started");

        Button btnCreateSite = findViewById(R.id.btnCreateSite);
        btnCreateSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Create Site button clicked");
                Intent intent = new Intent(HomeActivity.this, CreateSiteActivity.class);
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_locations) {
                startActivity(new Intent(this, LocationsActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });

        // Setup RecyclerView
        recyclerViewSites = findViewById(R.id.recyclerViewSites);
        recyclerViewSites.setLayoutManager(new LinearLayoutManager(this));
        siteAdapter = new SiteAdapter(new ArrayList<>()); // Initialize with an empty list
        recyclerViewSites.setAdapter(siteAdapter);

        loadSites(); // Method to load sites from Firestore
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_notify) {
            // Handle notification button click
            showNotification();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showNotification() {
        ListView listView = findViewById(R.id.notification_listview);

        if (listView.getVisibility() == View.VISIBLE) {
            listView.setVisibility(View.GONE);
        } else {
            fetchNotifications(); // Fetch and display notifications
            listView.setVisibility(View.VISIBLE);
        }
    }

    private void fetchNotifications() {
        String currentUserId = getCurrentUserId(); // Implement this method to get the current user's ID
        // Query your database to fetch notifications
        // For example, if using Firebase Firestore:
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("notifications")
                .whereArrayContains("participants", currentUserId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Handle the fetched data
                    List<String> notificationDescriptions = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String description = document.getString("description");
                        notificationDescriptions.add(description);
                    }
                    updateListView(notificationDescriptions);
                })
                .addOnFailureListener(e -> {
                    // Handle any errors
                    Log.e(TAG, "Error fetching notifications", e);
                });
    }
    private void updateListView(List<String> notifications) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notifications);
        ListView listView = findViewById(R.id.notification_listview);
        listView.setAdapter(adapter);
    }
    private String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return (currentUser != null) ? currentUser.getUid() : null;
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
