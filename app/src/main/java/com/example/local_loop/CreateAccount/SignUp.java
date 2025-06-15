package com.example.local_loop.CreateAccount;

import com.example.local_loop.Main.MainActivity;
import com.example.local_loop.R;
import com.example.local_loop.database.DatabaseHelper;
import com.example.local_loop.Welcome.OrganizerWelcomePage;
import com.example.local_loop.Welcome.WelcomePage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
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
    }


    public void backButtonPressed(View view) {
        // Handle back button press
        Intent intent = new Intent(SignUp.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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
            passwordInput.setError("Invalid Password:must be more than 5 characters");
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
    public void onSubmitButton(View view) {
        initialize();

        if(!isValid()){
            Toast.makeText(this,"Sign Up failed: Invalid inputs.",Toast.LENGTH_SHORT).show();
        }
        else{
            User newUser;
            if(role.equalsIgnoreCase("organizer")){
                newUser = new Organizer(user,first,last,password,email);
            }
            else {
                newUser = new Participant(user, first, last, password, email);
            }

            boolean successful = dbHelper.insertUser(newUser);
            if(successful){
                Intent intent;
                if(role.equalsIgnoreCase("organizer")){
                    intent = new Intent(getApplicationContext(), OrganizerWelcomePage.class);
                }
                else{
                    intent = new Intent(getApplicationContext(), WelcomePage.class);
                }
                intent.putExtra("username", first);
                intent.putExtra("userType", role);

                Toast.makeText(SignUp.this, "Welcome " + first + "! You are logged in as " + role + ".", Toast.LENGTH_SHORT).show();

                startActivity(intent);
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}