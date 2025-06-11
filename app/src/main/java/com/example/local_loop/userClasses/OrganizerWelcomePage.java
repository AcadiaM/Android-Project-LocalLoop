package com.example.local_loop.userClasses;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.local_loop.R;
//The organizer welcome page that displays a welcome message with the username and user type

public class OrganizerWelcomePage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.organizer_welcome_page);

        TextView welcomeTextView = findViewById(R.id.organizerWelcomeTextView);

        // Retrieve the username and userType from the Intent
        String username = getIntent().getStringExtra("username");
        String userType = getIntent().getStringExtra("userType");

        String welcomeMessage = "Welcome " + username + ". You are logged in as " + userType + ". ORGANIZER WELCOME";
        welcomeTextView.setText(welcomeMessage);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.organizerWelcomeTextView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }}
