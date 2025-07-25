package com.example.local_loop.Activity;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.*;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import com.example.local_loop.Account.Account;
import com.example.local_loop.R;
import com.example.local_loop.Helpers.*;


public class LoginActivity  extends AppCompatActivity{
    private View decorView;
    private InputValidation validation;
    private EditText usernameInput,passwordInput;
    private String username, password;
    private Account account;
    DatabaseHelper db;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        //Initialzing the Activity Pages/values
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        validation = new InputValidation(this,db);

        decorView = getWindow().getDecorView();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new DatabaseHelper(LoginActivity.this);
        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        account = null;
    }

    //onStart!!

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
        hideKeyboard(view);
        username = usernameInput.getText().toString().trim();
        password = passwordInput.getText().toString().trim();
        Intent intent = null;

        if(!isValid()){
            return;
        }

        intent = new Intent(getApplicationContext(), WelcomePage.class);

        if(intent == null){
            Log.d("LoginPage","INTENT IS NULL ???");
        }else{
            intent.putExtra("user", account);
            Toast.makeText(this, "Role: " + account.getRole(), Toast.LENGTH_LONG).show();
            startActivity(intent);
        }
    }

    public boolean isValid(){

        if(!(validation.isFilled(usernameInput,passwordInput))){
            return false;
        }
        //Check login
        account = db.checkLogin(username,password);

        if(account == null){
            Log.d("LoginPage", "checkLogin returned null");
            Toast.makeText(getApplicationContext(), "Invalid Credentials, please try again!", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

}

// TODO: Fix Database Methods!!!