package com.example.local_loop.Welcome;

import static com.example.local_loop.Event.EventDetailsActivity.EXTRA_SOURCE;
import static com.example.local_loop.Event.EventDetailsActivity.SOURCE_ADMIN;
import static com.example.local_loop.Event.EventDetailsActivity.SOURCE_ORGANIZER;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.local_loop.Category.CategoryActivity;
import com.example.local_loop.Event.EventActivity;
import com.example.local_loop.R;
import com.example.local_loop.Login.LoginActivity;
import com.example.local_loop.UserList.UserList;
//The admin welcome page that displays a welcome message with the username and user type

public class AdminWelcomePage extends AppCompatActivity {
    private String username, userType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.admin_welcome_page);

        TextView welcomeTextView = findViewById(R.id.adminWelcomeTextView);

        // Retrieve the username and userType from the Intent
        username = getIntent().getStringExtra("username");
        userType = getIntent().getStringExtra("userType");

        String welcomeMessage = "Welcome " + username + ".\nYou are logged in as " + userType + ".";
        welcomeTextView.setText(welcomeMessage);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.adminWelcomeTextView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void OnListCategoriesButton(View view) {
        username = getIntent().getStringExtra("username");
        userType = getIntent().getStringExtra("userType");
        Intent intent = new Intent(AdminWelcomePage.this, CategoryActivity.class);
        intent.putExtra(EXTRA_SOURCE, SOURCE_ADMIN);
        intent.putExtra("username", username);// Pass the username to the WelcomePage
        intent.putExtra("userType", userType); // Pass the userType to the WelcomePage
        startActivity(intent);
    }

    public void OnLogoutButton(View view) {
        // Clear the user session and redirect to login
        Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AdminWelcomePage.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void OnUsersButton(View view) {
        username = getIntent().getStringExtra("username");
        userType = getIntent().getStringExtra("userType");
        Intent intent = new Intent(AdminWelcomePage.this, UserList.class);
        intent.putExtra("username", username);// Pass the username to the WelcomePage
        intent.putExtra("userType", userType); // Pass the userType to the WelcomePage
        startActivity(intent);
    }

    public void OnEventsButton(View view) {
        username = getIntent().getStringExtra("username");
        userType = getIntent().getStringExtra("userType");
        Intent intent = new Intent(AdminWelcomePage.this, EventActivity.class);
        intent.putExtra(EXTRA_SOURCE, SOURCE_ADMIN);
        intent.putExtra("username", username);// Pass the username to the WelcomePage
        intent.putExtra("userType", userType); // Pass the userType to the WelcomePage
        startActivity(intent);
    }
}

