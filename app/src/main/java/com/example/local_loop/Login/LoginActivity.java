package com.example.local_loop.Login;

import android.app.Activity;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.local_loop.CreateAccount.SignUp;
import com.example.local_loop.R;
import com.example.local_loop.database.DatabaseHelper;
import com.example.local_loop.databinding.ActivityLoginBinding;
import com.example.local_loop.Welcome.WelcomePage;
import com.example.local_loop.Welcome.AdminWelcomePage;
import com.example.local_loop.Welcome.OrganizerWelcomePage;
import com.google.android.material.textfield.TextInputLayout;

import android.content.Intent;

import java.util.Objects;


public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private View decorView;
    DatabaseHelper db = new DatabaseHelper(LoginActivity.this);

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory(this))
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        TextInputLayout passwordLayout = findViewById(R.id.passwordLayout);

        // Initially hide the eye icon
        passwordLayout.setEndIconVisible(false);

        // Watch for password text changes
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Show icon only when input length >= 6
                passwordLayout.setEndIconVisible(s.length() >= 6);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;

        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        loginViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
            }
            if (loginResult.getSuccess() != null) {
                updateUiWithUser(loginResult.getSuccess());
            }
            setResult(Activity.RESULT_OK);

            //Complete and destroy login activity once successful
            finish();
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(Objects.requireNonNull(usernameEditText.getText()).toString(),
                        Objects.requireNonNull(passwordEditText.getText()).toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(Objects.requireNonNull(usernameEditText.getText()).toString(),
                        Objects.requireNonNull(passwordEditText.getText()).toString());
            }
            return false;
        });


        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            if (db.isActive(Objects.requireNonNull(usernameEditText.getText()).toString()) == 1) {
                loginViewModel.login(usernameEditText.getText().toString(),
                        Objects.requireNonNull(passwordEditText.getText()).toString());
            } else {
                Toast.makeText(LoginActivity.this, "Error: account is disabled.", Toast.LENGTH_LONG).show();
                loadingProgressBar.setVisibility(View.GONE);
            }
        });

        //this is to hide the system bars and make the app immersive
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if (visibility == 0) {
                decorView.setSystemUiVisibility(hideSystemBars());
            }
        });

        Button backButton = findViewById(R.id.loginBackButton);
        backButton.setOnClickListener(v -> onBackPressed());
    }

    public void onNoAccountClick(View view){
        Intent intent = new Intent(LoginActivity.this, SignUp.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void updateUiWithUser(LoggedInUserView loggedInUser) {
        String firstname = db.getFirstByUsername(loggedInUser.getDisplayName());
        String userType = db.getRoleByUsername(loggedInUser.getDisplayName());
        userType = userType.trim().toLowerCase();
        Toast.makeText(this, "UserType: " + userType, Toast.LENGTH_LONG).show();
        if (userType.equalsIgnoreCase("admin")) {
            Intent intent = new Intent(getApplicationContext(), AdminWelcomePage.class);
            intent.putExtra("firstname", firstname);
            intent.putExtra("username", loggedInUser.getDisplayName());// Pass the username to the WelcomePage
            intent.putExtra("userType", userType); // Pass the userType to the WelcomePage
            startActivity(intent);
        }
        else if (userType.equalsIgnoreCase("organizer")) {
                Intent intent = new Intent(getApplicationContext(), OrganizerWelcomePage.class);
                intent.putExtra("firstname", firstname);
                intent.putExtra("username", loggedInUser.getDisplayName());// Pass the username to the WelcomePage
                intent.putExtra("userType", userType); // Pass the userType to the WelcomePage
                startActivity(intent);
            }
        else if (userType.equalsIgnoreCase("participant")) {
            Intent intent = new Intent(getApplicationContext(), WelcomePage.class);
            intent.putExtra("firstname", firstname);
            intent.putExtra("username", loggedInUser.getDisplayName());// Pass the username to the WelcomePage
            intent.putExtra("userType", userType); // Pass the userType to the WelcomePage
            startActivity(intent);
        }
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
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