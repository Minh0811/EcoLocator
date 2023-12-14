package com.khaiminh.ecolocator.UserActivities.UserProfile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.khaiminh.ecolocator.Authentication.LoginActivity;
import com.khaiminh.ecolocator.R;
import com.khaiminh.ecolocator.UserActivities.MainActivity;

public class UserProfileActivity extends AppCompatActivity {
    FirebaseAuth auth;
    Button button;
    TextView textView;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.user_profile_logout);
        textView = findViewById(R.id.user_profile_details);
        user = auth.getCurrentUser();

        if(user == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
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

        // Setup BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Handle home action
                Intent intent = new Intent(getApplicationContext(),  MainActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_locations) {
                // Handle locations action
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Handle profile action
                // Navigate to the User Profile Activity
                Intent intent = new Intent(getApplicationContext(),  UserProfileActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });


        // Optionally, handle reselection
        bottomNav.setOnItemReselectedListener(item -> {
            // Handle item reselection if needed
        });
    }
}