package com.example.local_loop.data;

import com.example.local_loop.CreateAccount.User;
import com.example.local_loop.data.model.LoggedInUser;
import com.example.local_loop.database.DatabaseHelper;

import java.io.IOException;
import android.content.Context;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class Login {
    private final DatabaseHelper dbHelper;

    public Login(Context context) {
        dbHelper = new DatabaseHelper(context);
    }


    public Result<LoggedInUser> login(String username, String password) {
        if(dbHelper.checkLogin(username, password)){
            User currentSession = new
        }


        String role = dbHelper.getRoleByUsername(username);
        if (dbHelper.checkLogin(username, password)) {
            return new Result.Success<>(new LoggedInUser(username, username, role));
        } else {
            return new Result.Error(new IOException("Invalid credentials"));
        }
    }

    public void logout() {
        // Not needed for local DB
    }
}
