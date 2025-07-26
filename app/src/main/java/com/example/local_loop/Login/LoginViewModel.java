package com.example.local_loop.Login;

import android.util.Patterns;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.local_loop.R;
import com.example.local_loop.Helpers.DatabaseHelper;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    public LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password, DatabaseHelper db) {
        if (db.checkLogin(username, password)) {
            loginResult.setValue(new LoginResult(new LoggedInUserView(username)));
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
    }


    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(com.example.local_loop.R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, com.example.local_loop.R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    private boolean isUserNameValid(String username) {
        if (username == null) return false;
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    // ----- Inner Classes -----

    public static class LoginFormState {
        @Nullable private final Integer usernameError;
        @Nullable private final Integer passwordError;
        private final boolean isDataValid;

        public LoginFormState(@Nullable Integer usernameError, @Nullable Integer passwordError) {
            this.usernameError = usernameError;
            this.passwordError = passwordError;
            this.isDataValid = false;
        }

        public LoginFormState(boolean isDataValid) {
            this.usernameError = null;
            this.passwordError = null;
            this.isDataValid = isDataValid;
        }

        @Nullable public Integer getUsernameError() {
            return usernameError;
        }

        @Nullable public Integer getPasswordError() {
            return passwordError;
        }

        public boolean isDataValid() {
            return isDataValid;
        }
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

        @Nullable public LoggedInUserView getSuccess() {
            return success;
        }

        @Nullable public Integer getError() {
            return error;
        }
    }

    public static class LoggedInUserView {
        private final String displayName;

        public LoggedInUserView(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
