package com.example.local_loop.Activity.WelcomeUser;

import static androidx.core.content.IntentCompat.getParcelableExtra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//import com.example.local_loop.Event.EventActivity;
import com.example.local_loop.Account.Account;
import com.example.local_loop.R;
import com.example.local_loop.Activity.Main.LoginActivity;

//The organizer welcome page that displays a welcome message with the username and user type

public class OrganizerWelcomePage extends AppCompatActivity {
    private Account account;
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.organizer_welcome_page);
        TextView welcomeTextView = findViewById(R.id.organizerWelcomeTextView);

        Intent intent = getIntent();
        account = getIntent().getParcelableExtra("user", Account.class);

        if(account == null){
            Log.d("OrganizerWelcomePage", "account is null");
            Intent fail = new Intent(OrganizerWelcomePage.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(fail);
            finish();
            //return to home page
        }

        String welcomeMessage = "Welcome " + account.getFirstName() + ".\nYou are logged in as " + account.getRole() + ".";
        welcomeTextView.setText(welcomeMessage);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.organizerWelcomeTextView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //this is to hide the system bars and make the app immersive
        decorView = getWindow().getDecorView();
    }

    /*
    public void OnEventsOrganizerButton(View view) {
        Intent intent = new Intent(OrganizerWelcomePage.this, EventActivity.class);
        intent.putExtra(EXTRA_SOURCE, SOURCE_ORGANIZER);
        intent.putExtra("username", username);// Pass the username to the ParticipantWelcomePage
        intent.putExtra("userType", userType); // Pass the userType to the ParticipantWelcomePage
        startActivity(intent);
    } */


    public void OnLogoutButton(View view) {
        // Clear the user account and redirect to login
        Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(OrganizerWelcomePage.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
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
