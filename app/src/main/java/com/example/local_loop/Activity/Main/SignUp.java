package com.example.local_loop.Activity.Main;

import com.example.local_loop.Account.Account;
import com.example.local_loop.Account.User;
import com.example.local_loop.R;
import com.example.local_loop.Activity.WelcomeUser.OrganizerWelcomePage;
import com.example.local_loop.Activity.WelcomeUser.ParticipantWelcomePage;
import com.example.local_loop.Helpers.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUp extends AppCompatActivity {
    private EditText usernameInput,emailInput,passwordInput, firstNameInput, lastNameInput;
    private String role, username, email, password, first, last;
    Spinner roleInput;
    DatabaseHelper dbHelper;
    private View decorView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        firstNameInput =findViewById(R.id.firstNameInputEditText);
        lastNameInput = findViewById(R.id.lastNameInputEditText);
        usernameInput = findViewById(R.id.userNameInputEditText);
        emailInput = findViewById(R.id.emailInputEditText);
        passwordInput = findViewById(R.id.passwordInputEditText);
        roleInput = findViewById(R.id.roles);
        dbHelper = new DatabaseHelper(this);

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

    //Button Methods

    public void backButtonPressed(View view) {
        // Handle back button press
        Intent intent = new Intent(SignUp.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void haveAccountClick(View view){
        Intent intent = new Intent(SignUp.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    public void onSubmitButton(View view) {
        hideKeyboard(view);
        initialize();

        if(!isValid()){
            Toast.makeText(this,"Sign Up failed: Invalid inputs.",Toast.LENGTH_SHORT).show();
        }
        else{
            User newUser = new User(role, username, email, password, first,last);
            newUser.setUserID(dbHelper.insertUser(newUser));

            if(newUser.getUserID() != -1){
                Intent intent;
                Account userAccount = new Account(newUser.getUserID(),role,username,email,first,last);

                if(role.equalsIgnoreCase("participant")){
                    intent = new Intent(getApplicationContext(), ParticipantWelcomePage.class);
                } else{
                    intent = new Intent(getApplicationContext(), OrganizerWelcomePage.class);
                }

                Toast.makeText(SignUp.this, "Welcome " + first + "! You are logged in as " + role + ".", Toast.LENGTH_SHORT).show();
                intent.putExtra("user", userAccount);
                startActivity(intent);
            }
            else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Sign Up Validation
    public void initialize(){
        role = String.valueOf(roleInput.getSelectedItem()).toLowerCase();
        username = usernameInput.getText().toString().trim();
        email =emailInput.getText().toString().trim();
        password = passwordInput.getText().toString().trim();
        first = firstNameInput.getText().toString().trim();
        last = lastNameInput.getText().toString().trim();
    }

    public boolean isValid(){
        boolean valid = isInputFilled(usernameInput, username) & isInputFilled(emailInput, email) & isInputFilled(passwordInput, password) &
                isInputFilled(firstNameInput, first) & isInputFilled(lastNameInput, last);

        //Do not evaluate values until all inputs are filled
        if(!valid){
            return false;
        }

        //Check Role
        if(role.isEmpty()){
            Toast.makeText(this, "Please select a valid role.", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        //Check Username
        if(dbHelper.checkUsername(username)){
            usernameInput.setText(null);
            usernameInput.setError("Username Invalid: This username is in use.");
            valid = false;
        }else{
            usernameInput.setError(null);
        }

        //Check email
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailInput.setText(null);
            emailInput.setError("Invalid Email");
            valid = false;
        }

        if(dbHelper.checkEmail(email)){
            emailInput.setText(null);
            emailInput.setError("Email exists, please head to login page.");
            valid = false;
        }else{
            emailInput.setError(null);
        }

        //Check Password
        if(password.isEmpty() || password.length()<5){
            passwordInput.setText(null);
            passwordInput.setError("Password must be at least 5 characters long.");
            valid = false;
        }else{
            passwordInput.setError(null);
        }

        return valid;
    }

    //This code checks if any inputs are empty and then sends an error message
    private boolean isInputFilled(EditText input, String value){
        if(value.isEmpty()){
            input.setError("This field is required.");
            return false;
        }
        input.setError(null);
        return true;
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

}