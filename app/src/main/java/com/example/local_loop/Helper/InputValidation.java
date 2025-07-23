package com.example.local_loop.Helper;

import android.content.Context;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class InputValidation{
    private final Context context;
    private DatabaseHelper db;

    public InputValidation(Context context, DatabaseHelper db) {
        this.context = context;
        this.db = db;
    }

    public boolean isFilled(EditText ... inputs){
        if(inputs == null){
            return false;
        }
        boolean isFilled = true;
        for(EditText input : inputs){
            if(input == null || input.getText().toString().trim().isEmpty()){
                input.setError("This field is required.");
                isFilled = false;
            }
            input.setError(null);
        }
        return isFilled;
    }

    public boolean validateRole(String role){
        if(role.isEmpty()){
            Toast.makeText(context, "Please select a valid role.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean validateUsername(EditText usernameInput){
        if(db.checkUsername(usernameInput.getText().toString().trim())){
            usernameInput.setText(null);
            usernameInput.setError("Username Invalid: This username is in use.");
            return false;
        }else{
            usernameInput.setError(null);
            return true;
        }
    }

    public boolean validateEmail(EditText emailInput){
        String email = emailInput.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailInput.setText(null);
            emailInput.setError("Invalid Email");
            return false;
        }

        if(db.checkEmail(email)){
            emailInput.setText(null);
            emailInput.setError("Email exists, please head to login page.");
            return false;
        }
        emailInput.setError(null);
        return true;
    }


    public boolean validatePassword(EditText passwordInput){
        String password = passwordInput.getText().toString();
        if(password.length()<5){
            passwordInput.setText(null);
            passwordInput.setError("Password must be at least 5 characters long.");
            return false;
        }else{
            passwordInput.setError(null);
            return true;
        }
    }


    //Category Dialog takes a layout
    public boolean validateLayout(String inputText, TextInputLayout layout) {
        if (inputText == null || inputText.trim().isEmpty()) {
            layout.setError("This field is required");
            return false;
        } else {
            layout.setError(null);
            return true;
        }
    }

}