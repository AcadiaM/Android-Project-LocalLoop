package com.example.local_loop.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.local_loop.UserContent.User;
import com.example.local_loop.Helpers.DatabaseHelper;
import com.example.local_loop.R;
import com.google.android.material.textfield.TextInputLayout;

public class AuthActivity extends AppCompatActivity {
    private View decorView;
    private boolean isLoginMode = true;

    private DatabaseHelper db;
    private LoginViewModel loginViewModel;

    private EditText usernameInput, passwordInput;
    private TextInputLayout passwordLayout;
    private Button actionButton, toggleModeButton;
    private ProgressBar loadingProgressBar;

    private EditText firstNameInput, lastNameInput, emailInput, roleInput;
    private TextInputLayout firstNameLayout, lastNameLayout, emailLayout, roleLayout;
    private TextInputLayout usernameLayout;
    private String user;
    private String email;
    private String pass;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        db = new DatabaseHelper(this);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        loginViewModel.getLoginResult().observe(this, result -> {
            loadingProgressBar.setVisibility(View.GONE);
            if (result.getError() != null) {
                usernameInput.setError("Username or password is incorrect");
                passwordInput.setError(null);
            } else if (result.getSuccess() != null) {
                String username = result.getSuccess().getDisplayName();
                User user = db.getUserByUsername(username);
                Intent intent = new Intent(getApplicationContext(), WelcomePage.class);
                intent.putExtra("user", user);
                Toast.makeText(this, "Welcome " + user.getFirstName(), Toast.LENGTH_SHORT).show();
                startActivity(intent);
                finish();
            }
        });

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if (visibility == 0) decorView.setSystemUiVisibility(hideSystemBars());
        });

        initViews();
        setupFieldListeners();
        setLoginMode(getIntent().getBooleanExtra("showLogin", false));

        actionButton.setOnClickListener(v -> {
            if (isLoginMode) attemptLogin();
            else attemptSignup();
        });

        toggleModeButton.setOnClickListener(v -> setLoginMode(!isLoginMode));
    }

    @SuppressWarnings("deprecation")
    private void initViews() {
        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        passwordLayout = findViewById(R.id.passwordLayout);
        actionButton = findViewById(R.id.actionButton);
        toggleModeButton = findViewById(R.id.toggleAuthMode);
        loadingProgressBar = findViewById(R.id.loading);

        firstNameInput = findViewById(R.id.firstNameInputEditText);
        lastNameInput = findViewById(R.id.lastNameInputEditText);
        emailInput = findViewById(R.id.emailInputEditText);
        roleInput = findViewById(R.id.roleInput);

        firstNameLayout = findViewById(R.id.firstNameLayout);
        lastNameLayout = findViewById(R.id.lastNameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        usernameLayout = findViewById(R.id.usernameLayout);
        roleLayout = findViewById(R.id.roleLayout);

        String[] roles = getResources().getStringArray(R.array.person_position);
        roleInput.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(AuthActivity.this);
            builder.setTitle("Select Role");
            builder.setItems(roles, (dialog, which) -> roleInput.setText(roles[which]));
            builder.show();
        });

        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());
    }

    private void setupFieldListeners() {
        passwordInput.addTextChangedListener(new SimpleTextWatcher(s -> {
            if (s.length() >= 6) {
                passwordLayout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                passwordInput.setError(null);
            } else {
                passwordLayout.setEndIconMode(TextInputLayout.END_ICON_NONE);
                passwordInput.setError("Password must be at least 6 chars");
            }
        }));

        TextWatcher watcher = new SimpleTextWatcher(s -> loginViewModel.loginDataChanged(
                usernameInput.getText().toString(), passwordInput.getText().toString()
        ));
        usernameInput.addTextChangedListener(watcher);
        passwordInput.addTextChangedListener(watcher);

        TextWatcher enableButtonWatcher = new SimpleTextWatcher(s -> {
            boolean loginReady = !usernameInput.getText().toString().trim().isEmpty()
                    && passwordInput.getText().toString().trim().length() >= 6;
            if (isLoginMode) {
                actionButton.setEnabled(loginReady);
            } else {
                actionButton.setEnabled(
                        loginReady
                                && !firstNameInput.getText().toString().trim().isEmpty()
                                && !lastNameInput.getText().toString().trim().isEmpty()
                                && !emailInput.getText().toString().trim().isEmpty()
                                && !roleInput.getText().toString().trim().isEmpty()
                );
            }
        });

        usernameInput.addTextChangedListener(enableButtonWatcher);
        passwordInput.addTextChangedListener(enableButtonWatcher);
        firstNameInput.addTextChangedListener(enableButtonWatcher);
        lastNameInput.addTextChangedListener(enableButtonWatcher);
        emailInput.addTextChangedListener(enableButtonWatcher);
        roleInput.addTextChangedListener(enableButtonWatcher);
    }

    private void setLoginMode(boolean isLogin) {
        this.isLoginMode = isLogin;

        actionButton.setText(isLogin ? R.string.sign_in : R.string.sign_up);
        toggleModeButton.setText(isLogin ? R.string.no_account : R.string.yes_account);

        int visibility = isLogin ? View.GONE : View.VISIBLE;
        roleLayout.setVisibility(visibility);
        firstNameLayout.setVisibility(visibility);
        lastNameLayout.setVisibility(visibility);
        emailLayout.setVisibility(visibility);

        TextView title = findViewById(R.id.textViewTitle);
        title.setText(isLogin ? R.string.sign_in : R.string.account_creation);
        setTopMargin(usernameLayout, isLogin ? 16 : 8);

        // 🧼 Clear all fields when switching modes
        clearInputs(usernameInput, passwordInput, firstNameInput, lastNameInput, emailInput, roleInput);
        actionButton.setEnabled(false);

    }

    private void attemptLogin() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        usernameInput.setError(null);

        if (!db.checkLogin(username, password)) {
            loadingProgressBar.setVisibility(View.GONE);
            usernameInput.setError("Username or Password is incorrect");
            return;
        }

        if (db.isActive(username) == 0) {
            loadingProgressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Account is disabled.", Toast.LENGTH_SHORT).show();
            return;
        }

        loginViewModel.login(username, password, db);
    }

    private void attemptSignup() {
        String role = roleInput.getText().toString().trim();
        user = usernameInput.getText().toString().trim();
        email = emailInput.getText().toString().trim();
        pass = passwordInput.getText().toString().trim();
        String first = firstNameInput.getText().toString().trim();
        String last = lastNameInput.getText().toString().trim();

        if (!isValidSignup()) {
            Toast.makeText(this, "Sign Up failed: Invalid inputs.", Toast.LENGTH_SHORT).show();
            return;
        }

        User newUser = new User(first, last, user, email, pass, role);
        if (db.insertUser(newUser)) {
            Intent intent = new Intent(getApplicationContext(), WelcomePage.class);
            intent.putExtra("user", newUser);
            Toast.makeText(this, "Welcome " + first + "! You are logged in as " + role + ".", Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error creating account", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidSignup() {
        boolean valid = true;
        if (user.isEmpty()) {
            usernameInput.setError("Username is required");
            valid = false;
        } else if (db.checkUsername(user)) {
            usernameInput.setError("Username already exists. Try a new one.");
            valid = false;
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Invalid Email");
            valid = false;
        } else if (db.checkEmail(email)) {
            emailInput.setError("Email exists, go to Login!");
            valid = false;
        }
        if (pass.isEmpty() || pass.length() < 6) {
            passwordInput.setError("Password must be at least 6 chars");
            valid = false;
        }
        return valid;
    }

    private void clearInputs(EditText... inputs) {
        for (EditText input : inputs) {
            input.setText("");
            input.setError(null);
        }
    }


    @SuppressWarnings("deprecation")
    private int hideSystemBars() {
        return (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private static class SimpleTextWatcher implements TextWatcher {
        private final java.util.function.Consumer<CharSequence> onChange;
        SimpleTextWatcher(java.util.function.Consumer<CharSequence> onChange) {
            this.onChange = onChange;
        }
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            onChange.accept(s);
        }
        public void afterTextChanged(Editable s) {}
    }

    private void setTopMargin(View view, int dp) {
        int px = (int) (dp * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.topMargin = px;
        view.setLayoutParams(params);
    }

    public static class LoginViewModel extends ViewModel {
        private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
        public LiveData<LoginResult> getLoginResult() { return loginResult; }

        public void login(String username, String password, DatabaseHelper db) {
            if (db.checkLogin(username, password)) {
                loginResult.setValue(new LoginResult(new LoggedInUserView(username)));
            } else {
                loginResult.setValue(new LoginResult(R.string.login_failed));
            }
        }

        public void loginDataChanged(String ignoredUsername, String ignoredPassword) {
            // Validation could be expanded if needed
        }

        public static class LoginResult {
            @Nullable private final LoggedInUserView success;
            @Nullable private final Integer error;

            public LoginResult(@Nullable Integer error) {
                this.error = error;
                this.success = null;
            }

            public LoginResult(@Nullable LoggedInUserView success) {
                this.success = success;
                this.error = null;
            }

            @Nullable public LoggedInUserView getSuccess() { return success; }
            @Nullable public Integer getError() { return error; }
        }

        public static class LoggedInUserView {
            private final String displayName;
            public LoggedInUserView(String displayName) { this.displayName = displayName; }
            public String getDisplayName() { return displayName; }
        }
    }
}
