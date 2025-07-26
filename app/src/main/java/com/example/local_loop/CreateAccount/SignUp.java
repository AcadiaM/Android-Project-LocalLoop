package com.example.local_loop.CreateAccount;

import com.example.local_loop.Login.LoginActivity;
import com.example.local_loop.R;
import com.example.local_loop.Activity.WelcomePage;
import com.example.local_loop.Helpers.DatabaseHelper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUp extends AppCompatActivity {
    private EditText firstNameInput, lastNameInput, usernameInput,emailInput,passwordInput;
    private String role, user, first, last, email, password;
    Spinner roleInput;
    DatabaseHelper dbHelper;

    private View decorView;

    @SuppressWarnings("deprecation")
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
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if (visibility == 0) {
                decorView.setSystemUiVisibility(hideSystemBars());
            }
        });

        Button backButton = findViewById(R.id.signBackButton);
        backButton.setOnClickListener(v -> onBackPressed());
    }


    public boolean isValid(){
        boolean valid = true;
        if(user.isEmpty() || dbHelper.checkUsername(user) ){
            usernameInput.setError("Invalid Username");
            valid = false;
        }
        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailInput.setError("Invalid Email");
            valid = false;
        }
        if(dbHelper.checkEmail(email)){
            emailInput.setError("Email exists, please head to login page.");
            valid = false;
        }
        if(password.isEmpty() || password.length()<5){
            passwordInput.setError("Invalid Password:must be more than 6 characters");
           valid = false;
        }
        if(first.isEmpty() || last.isEmpty()){
            firstNameInput.setError("First and Last name required.");
            lastNameInput.setError("First and Last name required.");
            valid = false;
        }
        if(role.isEmpty()){
            Toast.makeText(this, "Please select a valid role.", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

    public void initialize(){
        role = String.valueOf(roleInput.getSelectedItem());
        user = usernameInput.getText().toString().trim();
        email =emailInput.getText().toString().trim();
        password = passwordInput.getText().toString().trim();
        first = firstNameInput.getText().toString().trim();
        last = lastNameInput.getText().toString().trim();
    }

    public void haveAccountClick(View view){
        Intent intent = new Intent(SignUp.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    public void onSubmitButton(View view) {
        initialize();

        if(!isValid()){
            Toast.makeText(this,"Sign Up failed: Invalid inputs.",Toast.LENGTH_SHORT).show();
        }
        else{
            User newUser;
            role = role.toLowerCase();
            newUser = new User(first,last, user, email, password, role);

            boolean successful = dbHelper.insertUser(newUser);
            if(successful){
                Intent intent = new Intent(getApplicationContext(), WelcomePage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("user", newUser);
                Toast.makeText(SignUp.this, "Welcome " + first + "! You are logged in as " + role + ".", Toast.LENGTH_SHORT).show();

                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
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