package com.example.local_loop.Helpers;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class InputValidation {
   Context context;
   DatabaseHelper db;


    public InputValidation(Context context) {
        this.context = context;
    }

    public boolean checkEmpty(TextInputEditText textInput, TextInputLayout textLayout, String message) {
        String value = textInput.getText().toString().trim();

        if (value.isEmpty()) {
            textLayout.setError(message);
            hideKeyboard(textInput);
            return false;
        } else {
            textLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean checkEmail(TextInputEditText textInput, TextInputLayout textLayout, String message) {
        String value = textInput.getText().toString().trim();
        if (value.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            textLayout.setError(message);
            hideKeyboard(textInput);
            return false;
        } else {
            textLayout.setErrorEnabled(false);
        }
        return true;
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }



}

/*
    public boolean isValid(){
        boolean valid = true;
        if(username.isEmpty() || dbHelper.checkUser(username) ){
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
 */