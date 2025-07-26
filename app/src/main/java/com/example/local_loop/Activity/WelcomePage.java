package com.example.local_loop.Activity;

import static com.example.local_loop.Details.EventDetailsActivity.EXTRA_SOURCE;
import static com.example.local_loop.Details.EventDetailsActivity.SOURCE_ADMIN;
import static com.example.local_loop.Details.EventDetailsActivity.SOURCE_ORGANIZER;
import static com.example.local_loop.Details.EventDetailsActivity.SOURCE_PARTICIPANT;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.local_loop.CreateAccount.User;
import com.example.local_loop.Displays.DisplayItemActivity;
import com.example.local_loop.Displays.UserDisplayActivity;
import com.example.local_loop.Displays.UserEventActivity;
import com.example.local_loop.Login.LoginActivity;
import com.example.local_loop.R;

public class WelcomePage extends AppCompatActivity {
    private View decorView;
    private User user;


    @SuppressLint("SetTextI18n")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_page);

        user = getIntent().getParcelableExtra("user", User.class);
        if (user == null) {
            Log.d("WelcomePage","Session is null girlie");
            finish();
            return;
        }

        TextView welcomeTextView = findViewById(R.id.welcomeTextView);
        Button button1 = findViewById(R.id.firstButton);
        Button button2 = findViewById(R.id.secondButton);
        Button button3 = findViewById(R.id.thirdButton);

        String role = user.getRole();
        String welcomeMessage = "Welcome " + user.getFirstName() + ".\nYou are logged in as " + role + ".";
        welcomeTextView.setText(welcomeMessage);

        switch(role){
            case "participant":
                button1.setText("Browse Events");
                button1.setOnClickListener(this::OnBrowseEventsButton);
                button2.setText("My Events");
                button2.setOnClickListener(this::OnMyEventsButton);
                button3.setVisibility(View.INVISIBLE);
                button3.setEnabled(false);
                break;
            case "organizer":
                button1.setText("Event Management");
                button1.setOnClickListener(this::OnEventsButton);
                button2.setVisibility(View.INVISIBLE);
                button2.setEnabled(false);
                button3.setVisibility(View.INVISIBLE);
                button3.setEnabled(false);
                break;
            case "admin":
                button1.setOnClickListener(this::OnUsersButton);
                button2.setOnClickListener(this::OnListCategoriesButton);
                button3.setOnClickListener(this::OnEventsButton);
                break;
            default:
                Log.d("WelcomePage","Session.getRole is not working");
                finish();
                return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.welcomeTextView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //this is to hide the system bars and make the app immersive
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if (visibility == 0) {
                decorView.setSystemUiVisibility(hideSystemBars());
            }
        });

    }

    public void OnLogoutButton(View view) {
        // Clear the user session and redirect to login
        Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(WelcomePage.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    //ADMIN BUTTONS
    public void OnListCategoriesButton(View view) {
        Intent intent = new Intent(WelcomePage.this, DisplayItemActivity.class);
        intent.putExtra(EXTRA_SOURCE,SOURCE_ADMIN);
        intent.putExtra("user", user);
        startActivity(intent);
    }
    public void OnUsersButton(View view) {
        Intent intent = new Intent(WelcomePage.this, UserDisplayActivity.class);
        intent.putExtra(EXTRA_SOURCE,SOURCE_ADMIN);
        intent.putExtra("user", user);
        startActivity(intent);
    }
    public void OnEventsButton(View view) {
        Intent intent = new Intent(WelcomePage.this, DisplayItemActivity.class);
        intent.putExtra(EXTRA_SOURCE,SOURCE_ORGANIZER);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    //Participant
    public void OnBrowseEventsButton(View view){
        Intent intent = new Intent(WelcomePage.this, UserEventActivity.class);
        intent.putExtra(EXTRA_SOURCE,SOURCE_PARTICIPANT);
        intent.putExtra("user", user);
        startActivity(intent);
    }
    public void OnMyEventsButton(View view){
        Intent intent = new Intent(WelcomePage.this, UserEventActivity.class);
        intent.putExtra(EXTRA_SOURCE,SOURCE_PARTICIPANT);
        intent.putExtra("user", user);
        intent.putExtra("isMyEventsMode", true);
        startActivity(intent);
    }

    //this method is called when the activity gains or loses focus
    @SuppressWarnings("deprecation")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }

    @SuppressWarnings("deprecation")
    private int hideSystemBars(){
        return (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}

