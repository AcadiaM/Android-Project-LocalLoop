package com.example.local_loop.Welcome;

import static com.example.local_loop.Event.EventDetailsActivity.EXTRA_SOURCE;
import static com.example.local_loop.Event.EventDetailsActivity.SOURCE_PARTICIPANT;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.local_loop.Event.UserEventActivity;
import com.example.local_loop.Event.UserMyEvent;
import com.example.local_loop.R;
import com.example.local_loop.Login.LoginActivity;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

//The normal user welcome page that displays a welcome message with the username and user type
public class WelcomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_page);

        TextView welcomeTextView = findViewById(R.id.welcomeTextView);

        // Retrieve the username and userType from the Intent
        String username = getIntent().getStringExtra("username");
        String userType = getIntent().getStringExtra("userType");

        String welcomeMessage = "Welcome " + username + ".\nYou are logged in as " + userType + ".";
        welcomeTextView.setText(welcomeMessage);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void OnBrowseEventsButton(View view){
        Intent intent = new Intent(WelcomePage.this, UserEventActivity.class);
        intent.putExtra(EXTRA_SOURCE, SOURCE_PARTICIPANT);
        intent.putExtra("username", getIntent().getStringExtra("username"));// Pass the username to the WelcomePage
        intent.putExtra("userType", getIntent().getStringExtra("userType")); // Pass the userType to the WelcomePage
        startActivity(intent);
    }

    public void OnLogoutButton(View view) {
        // Clear the user session and redirect to login
        Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(WelcomePage.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void OnMyEventsButton(View view){
        Intent intent = new Intent(WelcomePage.this, UserMyEvent.class);
        intent.putExtra(EXTRA_SOURCE, SOURCE_PARTICIPANT);
        intent.putExtra("username", getIntent().getStringExtra("username"));// Pass the username to the WelcomePage
        intent.putExtra("userType", getIntent().getStringExtra("userType")); // Pass the userType to the WelcomePage
        startActivity(intent);
    }
}