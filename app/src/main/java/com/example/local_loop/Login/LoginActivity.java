package com.example.local_loop.Login;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.local_loop.database.DatabaseHelper;
import com.example.local_loop.databinding.ActivityLoginBinding;
import com.example.local_loop.Main.MainActivity;
import com.example.local_loop.Welcome.WelcomePage;
import com.example.local_loop.Welcome.AdminWelcomePage;
import com.example.local_loop.Welcome.OrganizerWelcomePage;
import android.content.Intent;


public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory(this))
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
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
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
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
            }
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
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                DatabaseHelper db = new DatabaseHelper(LoginActivity.this);
                if (db.isActive(usernameEditText.getText().toString()) == 1) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                } else {
                    Toast.makeText(LoginActivity.this, "Error: account is disabled.", Toast.LENGTH_LONG).show();
                    loadingProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public void backButtonPressed(View view) {
        // Handle back button press
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void updateUiWithUser(LoggedInUserView loggedInUser) {
        DatabaseHelper db = new DatabaseHelper(this);
        String userType = db.getRoleByUsername(loggedInUser.getDisplayName());
        userType = userType.trim().toLowerCase();
        Toast.makeText(this, "UserType: " + userType, Toast.LENGTH_LONG).show();
        if (userType.equalsIgnoreCase("admin")) {
            Intent intent = new Intent(getApplicationContext(), AdminWelcomePage.class);
            intent.putExtra("username", loggedInUser.getDisplayName());// Pass the username to the WelcomePage
            intent.putExtra("userType", userType); // Pass the userType to the WelcomePage
            startActivity(intent);
        }
        else if (userType.equalsIgnoreCase("organizer")) {
                Intent intent = new Intent(getApplicationContext(), OrganizerWelcomePage.class);
                intent.putExtra("username", loggedInUser.getDisplayName());// Pass the username to the WelcomePage
                intent.putExtra("userType", userType); // Pass the userType to the WelcomePage
                startActivity(intent);
            }
        else if (userType.equalsIgnoreCase("participant")) {
            Intent intent = new Intent(getApplicationContext(), WelcomePage.class);
            intent.putExtra("username", loggedInUser.getDisplayName());// Pass the username to the WelcomePage
            intent.putExtra("userType", userType); // Pass the userType to the WelcomePage
            startActivity(intent);
        }
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}