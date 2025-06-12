package com.example.local_loop.userClasses;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.local_loop.R;
import com.example.local_loop.ui.login.LoginActivity;
//The admin welcome page that displays a welcome message with the username and user type

public class AdminWelcomePage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.admin_welcome_page);

        TextView welcomeTextView = findViewById(R.id.adminWelcomeTextView);

        // Retrieve the username and userType from the Intent
        String username = getIntent().getStringExtra("username");
        String userType = getIntent().getStringExtra("userType");

        String welcomeMessage = "Welcome " + username + ". You are logged TEST in as " + userType + ". ADMIN WELCOME!";
        welcomeTextView.setText(welcomeMessage);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.adminWelcomeTextView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void OnListCategoriesButton(View view) {
        //Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
        //startActivityForResult(intent,0);
        Intent intent = new Intent(AdminWelcomePage.this, CategoryActivity.class);
        startActivity(intent);
    }
}

