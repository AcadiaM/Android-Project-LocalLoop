package com.example.local_loop.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.example.local_loop.Category.Category;
import com.example.local_loop.CreateAccount.Admin;
import com.example.local_loop.CreateAccount.User;
import com.example.local_loop.CreateAccount.UserFactory;
import com.example.local_loop.Event.Event;
import com.example.local_loop.Event.Request;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "LocalLoop.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_USERS = "users";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * The initialization of all tables in the database.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");


        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "role TEXT NOT NULL CHECK(role IN ('admin', 'organizer', 'participant'))," +
                "firstName TEXT NOT NULL," +
                "lastName TEXT NOT NULL," +
                "username TEXT UNIQUE NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "isActive INTEGER NOT NULL DEFAULT 1);");

        db.execSQL("CREATE TABLE categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "description TEXT NOT NULL);");

        db.execSQL("CREATE TABLE events (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "description TEXT, " +
                "fee REAL, " +
                "datetime TEXT, " +
                "category_id INTEGER NOT NULL, " +
                "organizer INTEGER NOT NULL, " +  // Track who created it
                "FOREIGN KEY(category_id) REFERENCES categories(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(organizer) REFERENCES users(id) ON DELETE CASCADE);");

        db.execSQL("CREATE TABLE requests (" +
                "request_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "event_id INTEGER, " +
                "attendee_id TEXT NOT NULL, " +
                "status TEXT DEFAULT 'pending', " +
                "FOREIGN KEY(event_id) REFERENCES events(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(attendee_id) REFERENCES users(username) ON DELETE CASCADE);");
    }

    /**
     * This function deletes the most recent version of all tables in the database and uses the onCreate function to reinitialize them.
     *
     * @param db The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS events;");
        db.execSQL("DROP TABLE IF EXISTS categories;");
        db.execSQL("DROP TABLE IF EXISTS users;");
        db.execSQL("DROP TABLE IF EXISTS requests;");
        onCreate(db);
    }

    /**
     * This function runs the default SQLite onConfigure database function. It also overrides the default configuration, such that the database can accept foreign key constraints.
     *
     * @param db The database.
     */
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    /**
     * This function checks if the desired username has already been taken by another user in the database.
     *
     * @param username entered username.
     * @return true if username exists in database, false otherwise.
     */
    public boolean checkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"id"}, "username = ?", new String[]{username}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    /**
     * This function checks if the desired email address has already been entered by another user in the database.
     *
     * @param email entered email.
     * @return true if email exists in database, false otherwise.
     */

    public boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"id"}, "email = ?", new String[]{email}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    /**
     * Writes a new user object to the database.
     *
     * @param user The user object to be added to the database.
     * @return true if the insertion into the database was successful, false otherwise. Also logs the result of the insert.
     */

    public long insertUser(UserFactory user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("firstName", user.getFirstName());
        values.put("lastName", user.getLastName());
        values.put("username", user.getUsername());
        values.put("email", user.getEmail());
        values.put("password", user.getPassword());
        values.put("role", user.getRole());

        try {
            long result = db.insert(TABLE_USERS, null, values);
            Log.d("DB", "Insert result: " + result);
            return result;
        } catch (Exception e) {
            Log.e("DB", "Insert failed", e);
            return -1;
        }
    }

    public User getUser(String username){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_USERS,
                null,
                "username = ? AND password = ? AND active = ?",
                new String[]{username, , "1"},
                null, null, null
        );

    }

    /**
     * This function verifies that a user exists in the database, checking for username and password.
     *
     * @param username the desired user's username.
     * @param password the desired user's password.
     * @return true if the user exists in the database, false otherwise.
     */

    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, "username = ? AND password = ? AND active = ?", new String[]{username, password, "1"}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    /**
     * This function finds the role of a user from their username.
     *
     * @param username the username of the desired user.
     * @return the role of the user if found, null otherwise.
     */



    /**
     * This function is called at the beginning of the main activity to add the predetermined admin user to the database.
     * The admin data is taken from the function getAdmin in the admin class.
     */

    public void insertAdmin() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("users", null, "username = ?", new String[]{"admin"}, null, null, null);
        if (cursor.getCount() == 0) {
            //User admin = Admin.getAdmin();
            //insertUser(admin);
        }
        cursor.close();
    }

    /**
     * This function searches for a user in the database. If found and the user is an organizer, its created events are deleted. Then, the user is deleted.
     *
     //* @param email the email of the desired user.
     */

    public void deleteUser(int userID) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("users", "id = ?", new String[]{String.valueOf(userID)});

    }

    /**
     * This function searches for a user in the database with a username, setting its active value to 0 (inactive).
     *
     * @param //username the username of the desired user.
     */

    public void deactivateUser(int userID) {
        //The user is active (active=1) and is set to inactive=0
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("active", 0);
        db.update(TABLE_USERS, values, "id = ?", new String[]{String.valueOf(userID)});
    }

    /**
     * This function searches for a user in the database with a username, setting its active value to 1 (active).
     *
     * @param //username the username of the desired user.
     */

    public void reactivateUser(int userID) {
        //The user is inactive (active=0) and is set to active=1.
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("active", 1);
        db.update(TABLE_USERS, values, "id = ?", new String[]{String.valueOf(userID)});
    }

    /**
     * This function searches for a user with its username and verifies if the user is set to active or inactive.
     *
     * @param //username the username of the desired user.
     * @return the active value of the user, 1 if the user is active, 0 otherwise.
     */

    public int isActive(int userID) {
        int active = 1; // Default to active.
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{"active"}, "id = ?", new String[]{String.valueOf(userID)}, null, null, null);
        if (cursor.moveToFirst()) {
            active = cursor.getInt(0);
        }
        cursor.close();
        return active;
    }

    /**
     * This function lists all users that are not admins, creating an object user for each to be saved a list.
     *
     * @return a list of user objects comprising all users other than the admin user.
     */
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS, null, "role != ?", new String[]{"admin"}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                User user = new User(
                        cursor.getString(1), // firstName
                        cursor.getString(2), // lastName
                        cursor.getString(3), // username
                        cursor.getString(4), // email
                        cursor.getString(5), // password
                        cursor.getString(6)  // role
                );
                users.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return users;
    }


}
