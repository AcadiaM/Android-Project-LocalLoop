package com.example.local_loop.Account;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.local_loop.R;
import com.example.local_loop.database.DatabaseHelper;
import com.example.local_loop.Main.MainActivity;
import com.example.local_loop.Welcome.WelcomePage;
import com.example.local_loop.Welcome.AdminWelcomePage;
import com.example.local_loop.Welcome.OrganizerWelcomePage;

import android.content.Intent;

public class LoginActivity  extends AppCompatActivity{
    private View decorView;
    private EditText usernameInput,passwordInput;
    private String username, password;
    private Session session;
    DatabaseHelper db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseHelper db = new DatabaseHelper(LoginActivity.this);

        usernameInput = findViewById(R.id.userNameInputEditText);
        passwordInput = findViewById(R.id.passwordInputEditText);
        db = new DatabaseHelper(this);
        session = null;
    }

    public void backButtonPressed(View view) {
        // Handle back button press
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void onNoAccountClick(View view){
        Intent intent = new Intent(LoginActivity.this, SignUp.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }



    public void onSignIn(View view){
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        Intent intent = null;

        session = db.checkLogin(username,password);


        switch(session.getRole()){
            case "admin":
                intent = new Intent(getApplicationContext(), AdminWelcomePage.class);
                break;
            case "organizer":
                intent = new Intent(getApplicationContext(), OrganizerWelcomePage.class);
            case "participant":
                intent = new Intent(getApplicationContext(), WelcomePage.class);
            default:

                //Error message!
        }
        intent.putExtra("user",session);
        Toast.makeText(this, "Role: " + session.getRole(), Toast.LENGTH_LONG).show();
        startActivity(intent);

    }
    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
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