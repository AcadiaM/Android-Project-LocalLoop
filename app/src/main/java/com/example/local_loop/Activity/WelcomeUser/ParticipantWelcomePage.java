package com.example.local_loop.Activity.WelcomeUser;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//import com.example.local_loop.Event.UserEventActivity;
//import com.example.local_loop.Event.UserMyEvent;
import com.example.local_loop.Account.Account;
import com.example.local_loop.R;
import com.example.local_loop.Activity.Main.LoginActivity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

//The normal user welcome page that displays a welcome message with the username and user type
public class ParticipantWelcomePage extends AppCompatActivity {

    private View decorView;
    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_page);

        Intent intent = getIntent();
        account = getIntent().getParcelableExtra("user", Account.class);

        if(account == null){
            Log.d("ParticipantWelcomePage", "account is null");
            Intent fail = new Intent(ParticipantWelcomePage.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(fail);
            finish();
            //return to home page
        }

        TextView welcomeTextView = findViewById(R.id.welcomeTextView);
        String welcomeMessage = "Welcome " + account.getFirstName() + ".\nYou are logged in as " + account.getRole() + ".";
        welcomeTextView.setText(welcomeMessage);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //this is to hide the system bars and make the app immersive
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

    }

//    public void OnBrowseEventsButton(View view){
//        Intent intent = new Intent(ParticipantWelcomePage.this, UserEventActivity.class);
//        intent.putExtra(EXTRA_SOURCE, SOURCE_PARTICIPANT);
//        intent.putExtra("username", getIntent().getStringExtra("username"));// Pass the username to the ParticipantWelcomePage
//        intent.putExtra("userType", getIntent().getStringExtra("userType")); // Pass the userType to the ParticipantWelcomePage
//        startActivity(intent);
//    }

    public void OnLogoutButton(View view) {
        // Clear the user account and redirect to login
        Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ParticipantWelcomePage.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

//    public void OnMyEventsButton(View view){
//        Intent intent = new Intent(ParticipantWelcomePage.this, UserMyEvent.class);
//        intent.putExtra(EXTRA_SOURCE, SOURCE_PARTICIPANT);
//        intent.putExtra("username", getIntent().getStringExtra("username"));// Pass the username to the ParticipantWelcomePage
//        intent.putExtra("userType", getIntent().getStringExtra("userType")); // Pass the userType to the ParticipantWelcomePage
//        startActivity(intent);
//    }

    //this method is called when the activity gains or loses focus
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }
    private int hideSystemBars(){
        return (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

}