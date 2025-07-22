package com.example.local_loop.Activity.Main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.example.local_loop.Models.Account;
import com.example.local_loop.Models.User;
import com.example.local_loop.Helpers.DatabaseHelper;
import com.example.local_loop.Helpers.InputValidation;
import com.example.local_loop.R;

public class SignUp extends AppCompatActivity {
    private EditText usernameInput, emailInput, passwordInput, firstNameInput, lastNameInput;
    private String role, username, email, password, first, last;
    Spinner roleInput;
    DatabaseHelper dbHelper;
    private View decorView;
    private InputValidation validation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        validation = new InputValidation(this, dbHelper);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Initialize text inputs
        firstNameInput = findViewById(R.id.firstNameInputEditText);
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
        if (hasFocus) {
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }

    private int hideSystemBars() {
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

    public void haveAccountClick(View view) {
        Intent intent = new Intent(SignUp.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void onSubmitButton(View view) {
        hideKeyboard(view);
        initialize();

        if (!isValid()) {
            Toast.makeText(this, "Sign Up failed: Invalid inputs.", Toast.LENGTH_SHORT).show();
            return;
        }

        User newUser = new User(role, username, email, password, first, last);
        newUser.setUserID(dbHelper.insertUser(newUser));

        if (newUser.getUserID() == -1) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(getApplicationContext(), WelcomePage.class);
        Account account = new Account(newUser.getUserID(), role, username, email, first, last);

        if (intent == null) {
            Log.d("LoginPage", "INTENT IS NULL ???");
        } else {
            Toast.makeText(SignUp.this, "Welcome " + first + "! You are logged in as " + role + ".", Toast.LENGTH_SHORT).show();
            startActivity(intent);
            intent.putExtra("user", account);
            Toast.makeText(this, "Role: " + account.getRole(), Toast.LENGTH_LONG).show();
            startActivity(intent);
        }
    }

    public void initialize(){
        role = String.valueOf(roleInput.getSelectedItem()).toLowerCase();
        username = usernameInput.getText().toString().trim();
        email =emailInput.getText().toString().trim();
        password = passwordInput.getText().toString().trim();
        first = firstNameInput.getText().toString().trim();
        last = lastNameInput.getText().toString().trim();
    }

    public boolean isValid(){
        //Check if all values are filled
        //Do not evaluate values until all inputs are filled
        if(!(validation.isFilled(usernameInput,emailInput,passwordInput,firstNameInput,lastNameInput) & validation.validateRole(role))){
            return false;
        }
        return validation.validatePassword(passwordInput) && validation.validateEmail(emailInput) && validation.validateUsername(usernameInput) ;
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}
