package com.example.local_loop.Helpers;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.example.local_loop.Account.Account;
import com.example.local_loop.Account.User;
//import com.example.local_loop.Category.Category;
//import com.example.local_loop.Event.Event;
//import com.example.local_loop.Event.Request;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    //Database
    private static final String DATABASE_NAME = "LocalLoop.db";
    private static final int DATABASE_VERSION = 1;

    //User Table Values
    private static final String TABLE_USERS = "users";
    private static final String USERS_ID = "id";
    private static final String USERS_ROLE = "role";
    private static final String USERS_USERNAME = "username";
    private static final String USERS_EMAIL = "email";
    private static final String USERS_PASSWORD = "password";
    private static final String USERS_FIRSTNAME = "firstName";
    private static final String USERS_LASTNAME = "lastName";
    private static final String USERS_ISACTIVE = "isActive";

    //Category Table Values
    private static final String TABLE_CATEGORIES = "categories";
    private static final String CATEGORIES_ID = "id";
    private static final String CATEGORIES_NAME = "name";
    private static final String CATEGORIES_DESCRIPTION = "description";

    //Events table Values
    private static final String TABLE_EVENTS = "events";
    private static final String EVENTS_ID = "id";
    private static final String EVENTS_TITLE = "title";
    private static final String EVENTS_DESCRIPTION = "description";
    private static final String EVENTS_FEE = "fee";
    private static final String EVENTS_DATETIME = "datetime";
    private static final String EVENTS_CATEGORY_ID = "category_id";
    private static final String EVENTS_ORGANIZER = "organizer";

    //Requests
    private static final String TABLE_REQUESTS = "requests";
    private static final String REQUESTS_REQUEST_ID = "request_id";
    private static final String REQUESTS_EVENT_ID = "event_id";
    private static final String REQUESTS_ATTENDEE_ID = "attendee_id";
    private static final String REQUESTS_STATUS = "status";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "role TEXT NOT NULL CHECK(role IN ('admin', 'organizer', 'participant'))," +
                "username TEXT UNIQUE NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "firstName TEXT NOT NULL," +
                "lastName TEXT NOT NULL," +
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
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS events;");
        db.execSQL("DROP TABLE IF EXISTS categories;");
        db.execSQL("DROP TABLE IF EXISTS users;");
        db.execSQL("DROP TABLE IF EXISTS requests;");
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    /*
    users table columns:
    0- id: INTEGER PRIMARY KEY AUTOINCREMENT
    1- role: TEXT with check constraint
    2- username: UNIQUE TEXT
    3- email: UNIQUE TEXT
    4- password: TEXT
    5- firstName: TEXT
    6- lastName: TEXT
    7- isActive: INTEGER default 1

    categories table columns order:
    id (INTEGER PRIMARY KEY AUTOINCREMENT)
    1name (TEXT NOT NULL)
    description (TEXT NOT NULL)

    events table columns order:
    id (INTEGER PRIMARY KEY AUTOINCREMENT)
    title (TEXT NOT NULL)
    description (TEXT)
    fee (REAL)
    datetime (TEXT)
    category_id (INTEGER NOT NULL, foreign key references categories(id))
    organizer (INTEGER NOT NULL, foreign key references users(id))

    */

    public void insertAdmin() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("users", null, "username = ?", new String[]{"admin"}, null, null, null);
        if (cursor.getCount() == 0) {
            User admin = new User("admin","admin","-","XPI76SZUqyCjVxgnUjm0","ADMIN","-");
            admin.setUserID(insertUser(admin));
        }
        cursor.close();
    }

    public boolean checkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,new String[]{USERS_ID},USERS_USERNAME+" =?",new String[]{username},null,null,null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,new String[]{USERS_ID},USERS_EMAIL+" =?",new String[]{email},null,null,null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public long insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERS_FIRSTNAME, user.getFirstName());
        values.put(USERS_LASTNAME, user.getLastName());
        values.put(USERS_USERNAME, user.getUsername());
        values.put(USERS_EMAIL, user.getEmail());
        values.put(USERS_PASSWORD, user.getPassword());
        values.put(USERS_ROLE, user.getRole());

        try {
            long result = db.insert(TABLE_USERS, null, values);
            Log.d("DB", "Insert result: " + result);
            return result;
        } catch (Exception e) {
            Log.e("DB", "Insert failed", e);
            return -1;
        }
    }

    public Account checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Account session = null;

        Cursor cursor = db.query(TABLE_USERS, new String[]{USERS_ID,USERS_ROLE,USERS_EMAIL,USERS_FIRSTNAME,USERS_LASTNAME},
                USERS_USERNAME + " = ? AND " + USERS_PASSWORD + " = ? AND " + USERS_ISACTIVE + " = ?", new String[]{username, password, "1"}, null, null, null);

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            String role = cursor.getString(1);
            String email = cursor.getString(2);
            String firstName = cursor.getString(3);
            String lastName = cursor.getString(4);
            session = new Account(id, role, username,email, firstName, lastName);
        }

        cursor.close();
        return session;
    }

    public Account startSession(int userID){
        SQLiteDatabase db = getReadableDatabase();
        Account session = null;

        Cursor cursor = db.query(TABLE_USERS,
                new String[]{USERS_ROLE,USERS_USERNAME,USERS_EMAIL,USERS_FIRSTNAME,USERS_LASTNAME},
                USERS_ID+" = ?", new String[]{String.valueOf(userID)}, null, null, null);

        if (cursor.moveToFirst()) {
            String role = cursor.getString(0);
            String username = cursor.getString(1);
            String email = cursor.getString(2);
            String firstName = cursor.getString(3);
            String lastName = cursor.getString(4);

            session = new Account(userID, role, username,email,firstName,lastName);
        }

        cursor.close();
        return session;
    }


    public void setUserActivity(int userID, boolean isActive) {
        //Takes a bool and changes the value of isActive. No need to worry about if its a valid call, no errors!

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        //Updating
        values.put(USERS_ISACTIVE, isActive ? 1: 0);
        db.update(TABLE_USERS, values, USERS_ID+" = ?", new String[]{String.valueOf(userID)});
        db.close();
    }

    public void deleteUser(int userID) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_USERS, USERS_ID+" = ?", new String[]{String.valueOf(userID)});
        db.close();
    }


    public int isActive(int userID) {
        int active = 1; // Default to active.
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{USERS_ISACTIVE}, USERS_ID+" = ?",
                new String[]{String.valueOf(userID)}, null, null, null);

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
    public List<Account> getUsers() {
        List<Account> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS,new String[]{USERS_ID,USERS_ROLE,USERS_USERNAME,USERS_EMAIL,USERS_FIRSTNAME,USERS_LASTNAME}
        , USERS_ROLE+"!= ?", new String[]{"admin"}, USERS_ROLE, null, USERS_ID);
        if (cursor.moveToFirst()) {
            do {
                Account user = new Account(
                        cursor.getInt(0),     // userID
                        cursor.getString(1),  // role
                        cursor.getString(2),  // username
                        cursor.getString(3),  // email
                        cursor.getString(4),  // firstName
                        cursor.getString(5)   // lastName
                );
                users.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return users;
    }
}

