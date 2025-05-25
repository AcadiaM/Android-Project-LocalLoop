package com.example.local_loop.data;

import com.example.local_loop.data.model.LoggedInUser;
import com.example.local_loop.database.DatabaseHelper;

import java.io.IOException;
import android.content.Context;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    private final DatabaseHelper dbHelper;

    public LoginDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public Result<LoggedInUser> login(String email, String password) {
        if (dbHelper.checkLogin(email, password)) {
            String displayName = dbHelper.getUserNameByEmail(email);
            return new Result.Success<>(new LoggedInUser(email, displayName));
        } else {
            return new Result.Error(new IOException("Invalid credentials"));
        }
    }

    public void logout() {
        // Not needed for local DB
    }
}
