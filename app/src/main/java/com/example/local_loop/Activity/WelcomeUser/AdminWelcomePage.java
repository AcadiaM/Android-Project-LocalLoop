package com.example.local_loop.Activity.WelcomeUser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;

//import com.example.local_loop.Category.CategoryActivity;
//import com.example.local_loop.Event.EventActivity;
import com.example.local_loop.Account.Account;
import com.example.local_loop.R;
import com.example.local_loop.Activity.Main.LoginActivity;
//import com.example.local_loop.UserList.UserList;
//The admin welcome page that displays a welcome message with the username and user type

public class AdminWelcomePage extends AppCompatActivity {
    private View decorView;
    private Account account;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.admin_welcome_page);

        TextView welcomeTextView = findViewById(R.id.adminWelcomeTextView);
        Intent intent = getIntent();

        account = getIntent().getParcelableExtra("user", Account.class);

        if(account == null){
            Log.d("AdminWelcomePage", "account is null");
            Intent fail = new Intent(AdminWelcomePage.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(fail);
            finish();
            //return to home page
        }

        String welcomeMessage = "Welcome " + account.getFirstName() + ".\nYou are logged in as " + account.getRole() + ".";
        welcomeTextView.setText(welcomeMessage);


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
    public void OnLogoutButton(View view) {
        // Clear the user account and redirect to login
        Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AdminWelcomePage.this, LoginActivity.class);
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

    /*
    public void OnListCategoriesButton(View view) {
        username = getIntent().getStringExtra("username");
        userType = getIntent().getStringExtra("userType");
        Intent intent = new Intent(AdminWelcomePage.this, CategoryActivity.class);
        intent.putExtra(EXTRA_SOURCE, SOURCE_ADMIN);
        intent.putExtra("username", username);// Pass the username to the ParticipantWelcomePage
        intent.putExtra("userType", userType); // Pass the userType to the ParticipantWelcomePage
        startActivity(intent);
    }

    public void OnUsersButton(View view) {
        username = getIntent().getStringExtra("username");
        userType = getIntent().getStringExtra("userType");
        Intent intent = new Intent(AdminWelcomePage.this, UserList.class);
        intent.putExtra("username", username);// Pass the username to the ParticipantWelcomePage
        intent.putExtra("userType", userType); // Pass the userType to the ParticipantWelcomePage
        startActivity(intent);
    }

    public void OnEventsButton(View view) {
        username = getIntent().getStringExtra("username");
        userType = getIntent().getStringExtra("userType");
        Intent intent = new Intent(AdminWelcomePage.this, EventActivity.class);
        intent.putExtra(EXTRA_SOURCE, SOURCE_ADMIN);
        intent.putExtra("username", username);// Pass the username to the ParticipantWelcomePage
        intent.putExtra("userType", userType); // Pass the userType to the ParticipantWelcomePage
        startActivity(intent);
    }


     */
}

