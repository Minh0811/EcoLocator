package com.khaiminh.ecolocator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;


import io.realm.kotlin.mongodb.App;
import io.realm.kotlin.mongodb.AppConfiguration;
import io.realm.kotlin.mongodb.Credentials;


public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText usernameEditText = findViewById(R.id.username);
        EditText passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login);
        Button registerButton = findViewById(R.id.register);


        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            // Call a method to handle login
            loginUser(username, password);
        });

        registerButton.setOnClickListener(v -> {
            // Handle registration
        });
    }

    private void loginUser(String username, String password) {
        // Configure your Realm App instance
        App app = new App(new AppConfiguration.Builder("YOUR_REALM_APP_ID").build());

        // Login with email/password
        app.loginAsync(Credentials.emailPassword(username, password), result -> {
            if (result.isSuccess()) {
                Log.v("AUTH", "Successfully authenticated using email and password.");
                // Navigate to your main activity
            } else {
                Log.e("AUTH", "Authentication failed: " + result.getError());
                // Handle error
            }
        });
    }

}