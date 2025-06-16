package com.example.local_loop.database;


import static com.example.local_loop.CreateAccount.Admin.getAdmin;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.example.local_loop.Category.Category;
import com.example.local_loop.CreateAccount.Admin;
import com.example.local_loop.CreateAccount.User;
import com.example.local_loop.Events.Event;


import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "LocalLoop.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_USERS = "users";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");

        db.execSQL( "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "firstName TEXT NOT NULL," +
                "lastName TEXT NOT NULL," +
                "username TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL, " +
                "active INTEGER NOT NULL DEFAULT 1);");

        db.execSQL("CREATE TABLE categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "name TEXT NOT NULL, "+
                "description Text NOT NULL);");

        db.execSQL("CREATE TABLE events (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "category_id INTEGER, " +
                "title TEXT NOT NULL, " +
                "FOREIGN KEY(category_id) REFERENCES categories(id) ON DELETE CASCADE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS events;");
        db.execSQL("DROP TABLE IF EXISTS categories;");
        db.execSQL("DROP TABLE IF EXISTS users;");
        onCreate(db);
    }

    //Inserts user into database, ensures no duplicates (fails on unique columns)
    public boolean checkUsername(String username){
        boolean exists ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =db.rawQuery("SELECT id FROM users WHERE username =?", new String[]{username});
        exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }
    public boolean checkEmail(String email){
        boolean exists;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE email =?", new String[]{email});
        exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public boolean insertUser(User user) {
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
            return result != -1;
        } catch (Exception e) {
            Log.e("DB", "Insert failed", e);
            return false;
        }
    }

    // Check if login credentials are valid
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE username = ? AND password = ? AND active = 1",
                new String[]{username, password}
        );
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    //Get role by username
    public String getRoleByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT role FROM " + TABLE_USERS + " WHERE username = ?",
                new String[]{username}
        );
        if (cursor.moveToFirst()) {
            String role = cursor.getString(0);
            cursor.close();
            return role;
        }
        cursor.close();
        return null;
    }

    //Inserts admin user
    public void insertAdmin() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = 'admin'", null);
        if (cursor.getCount() == 0) {
            User admin = Admin.getAdmin();
            insertUser(admin);
//        //Hardcoded admin account.
        }
        cursor.close();
    }

    public void deleteUser(String email) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("users", "email=?", new String[]{email});
    }

    public void deactivateUser(String email) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("active", 0);

        db.update(TABLE_USERS, values, "email = ?", new String[]{email});
    }

    // Add new category
    public void addCategory(String name, String description) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("description", description);
        db.insert("categories", null, values);
    }

    // Rename category
    public void renameCategory(int id, String newName, String newDescription) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", newName);
        values.put("description", newDescription);
        db.update("categories", values,"id=?", new String[]{String.valueOf(id)});
    }

    // Delete category (and all its events due to ON DELETE CASCADE)
    public void deleteCategory(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("categories", "id=?", new String[]{String.valueOf(id)});
    }

    // Get all categories
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM categories", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String description = cursor.getString(2);
            categories.add(new Category(id, name, description));
        }
        cursor.close();
        return categories;
    }

    // Add an event to a category
    public void addEvent(int categoryId, String title) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("category_id", categoryId);
        values.put("title", title);
        db.insert("events", null, values);
    }

    // Rename an event
    public void renameEvent(int id, String newTitle) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", newTitle);
        db.update("events", values, "id=?", new String[]{String.valueOf(id)});
    }

    // Delete an event
    public void deleteEvent(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("events", "id=?", new String[]{String.valueOf(id)});
    }

    // Get all events in a category
    public List<Event> getEventsByCategory(int categoryId) {
        List<Event> events = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, title FROM events WHERE category_id=?", new String[]{String.valueOf(categoryId)});
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            events.add(new Event(id, categoryId, title));
        }
        cursor.close();
        return events;
    }

    //Ensures category exists
    public boolean categoryNameExists(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("categories", new String[]{"id"}, "name = ?", new String[]{name}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    //Gets all users other than admins
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE role != 'admin'", null);

        if (cursor.moveToFirst()) {
            do {
                // Replace constructor arguments based on your User class definition
                User user = new User(
                        cursor.getString(1), // firstName
                        cursor.getString(2), // lastName
                        cursor.getString(3), // userName
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