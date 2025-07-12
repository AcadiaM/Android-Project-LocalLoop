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
import com.example.local_loop.Event.Event;

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

        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "firstName TEXT NOT NULL," +
                "lastName TEXT NOT NULL," +
                "username TEXT UNIQUE NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL, " +
                "active INTEGER NOT NULL DEFAULT 1);");

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
                "category_id INTEGER, " +
                "organizer TEXT NOT NULL, " +  // Track who created it
                "FOREIGN KEY(category_id) REFERENCES categories(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(organizer) REFERENCES users(username) ON DELETE CASCADE);");

        db.execSQL("CREATE TABLE requests (" +
                "request_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "event_id INTEGER, " +
                "attendee_id TEXT NOT NULL, " +
                "status TEXT DEFAULT 'pending', " +
                "FOREIGN KEY(event_id) REFERENCES Events(event_id) ON DELETE CASCADE, " +
                "FOREIGN KEY(attendee_id) REFERENCES Users(username) ON DELETE CASCADE);");
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


    // USER FUNCTIONS

    public boolean checkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"id"}, "username = ?", new String[]{username}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"id"}, "email = ?", new String[]{email}, null, null, null);
        boolean exists = cursor.moveToFirst();
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

    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, "username = ? AND password = ? AND active = ?", new String[]{username, password, "1"}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public String getRoleByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{"role"}, "username = ?", new String[]{username}, null, null, null);
        if (cursor.moveToFirst()) {
            String role = cursor.getString(0);
            cursor.close();
            return role;
        }
        cursor.close();
        return null;
    }

    public String getUsernameByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{"username"}, "email = ?", new String[]{email}, null, null, null);
        if (cursor.moveToFirst()) {
            String username = cursor.getString(0);
            cursor.close();
            return username;
        }
        cursor.close();
        return null;
    }

    public void insertAdmin() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("users", null, "username = ?", new String[]{"admin"}, null, null, null);
        if (cursor.getCount() == 0) {
            User admin = Admin.getAdmin();
            insertUser(admin);
        }
        cursor.close();
    }

    public void deleteUser(String email) {
        SQLiteDatabase db = getWritableDatabase();

        // Get the user ID
        Cursor cursor = db.query("users", new String[]{"id"}, "email = ?", new String[]{email}, null, null, null);
        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(0);
            // Delete events created by this user
            db.delete("events", "organizer = ?", new String[]{String.valueOf(userId)});
        }
        cursor.close();

        // Then delete user
        db.delete("users", "email = ?", new String[]{email});
    }


    public void deactivateUser(String email) {
        //The user is active (active=1) and is set to inactive=0
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("active", 0);
        db.update(TABLE_USERS, values, "email = ?", new String[]{email});
    }

    public void reactivateUser(String email) {
        //The user is inactive (active=0) and is set to active=1.
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("active", 1);
        db.update(TABLE_USERS, values, "email = ?", new String[]{email});
    }

    public int isActive(String email) {
        int active = 1; // Default to active.
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, "email = ?", new String[]{email}, null, null, null);
        if (cursor.moveToFirst()) {
            active = cursor.getInt(7);
        }
        cursor.close();
        return active;
    }

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

    // CATEGORY FUNCTIONS

    public void addCategory(String name, String description) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("description", description);
        db.insert("categories", null, values);
    }

    public void renameCategory(int id, String newName, String newDescription) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", newName);
        values.put("description", newDescription);
        db.update("categories", values, "id=?", new String[]{String.valueOf(id)});
    }

    public void deleteCategory(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("categories", "id=?", new String[]{String.valueOf(id)});
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("categories", null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String description = cursor.getString(2);
            categories.add(new Category(id, name, description));
        }
        cursor.close();
        return categories;
    }

    public boolean categoryNameExists(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("categories", new String[]{"id"}, "name = ?", new String[]{name}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // EVENT FUNCTIONS

    public void addEvent(String title, String description, int categoryId, String datetime, double fee, String organizer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("title", title);
        values.put("description", description);
        values.put("fee", fee);
        values.put("datetime", datetime);
        values.put("category_id", categoryId);
        values.put("organizer", organizer);

        db.insert("events", null, values);
        db.close();
    }


    public boolean updateEvent(int eventId, String title, String description, double fee, String datetime, int categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("fee", fee);
        values.put("datetime", datetime);
        values.put("category_id", categoryId);

        int rowsAffected = db.update("events", values, "id = ?", new String[]{String.valueOf(eventId)});
        db.close();
        return rowsAffected > 0;
    }

    public boolean eventTitleExistsInCategory(String title, int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                "events",
                new String[]{"COUNT(*)"},
                "title = ? AND category_id = ?",
                new String[]{title, String.valueOf(categoryId)},
                null,
                null,
                null
        );

        boolean exists = false;
        if (cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            exists = count > 0;
        }
        cursor.close();
        db.close();
        return exists;
    }


    public void deleteEvent(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("events", "id=?", new String[]{String.valueOf(id)});
    }

    public List<Event> getEventsByCategory(int categoryId) {
        List<Event> eventList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT e.id, e.title, e.description, e.category_id, " +
                "e.organizer, e.fee, e.datetime " +
                "FROM events e " +
                "JOIN categories c ON e.category_id = c.id " +
                "WHERE e.category_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(categoryId)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                int catId = cursor.getInt(cursor.getColumnIndexOrThrow("category_id"));
                String organizer = cursor.getString(cursor.getColumnIndexOrThrow("organizer"));
                double fee = cursor.getDouble(cursor.getColumnIndexOrThrow("fee"));
                String datetime = cursor.getString(cursor.getColumnIndexOrThrow("datetime"));

                eventList.add(new Event(id, title, description, catId, organizer, fee, datetime));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return eventList;
    }

    public List<Event> getEventsByOrganizer(String organizer) {
        List<Event> events = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        // Join Events with Categories to get category name
        String query = "SELECT e.id, e.title, e.description, e.category_id, e.organizer, e.fee, e.datetime " +
                "FROM Events e " +
                "JOIN Categories c ON e.category_id = c.id " +
                "WHERE e.organizer = ?";

        Cursor cursor = db.rawQuery(query, new String[]{organizer});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("category_id"));
                String org = cursor.getString(cursor.getColumnIndexOrThrow("organizer"));
                double fee = cursor.getDouble(cursor.getColumnIndexOrThrow("fee"));
                String dateTime = cursor.getString(cursor.getColumnIndexOrThrow("datetime"));

                Event event = new Event(id, title, description, categoryId, org, fee, dateTime);
                events.add(event);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return events;
    }

    public Category getCategoryById(int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("categories", null, "id=?", new String[]{String.valueOf(categoryId)}, null, null, null);
        if (cursor.moveToFirst()) {
            Category category = new Category(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("description"))
            );
            cursor.close();
            return category;
        }
        return null;
    }

    public List<Event> getEventsByCategoryId(int categoryId) {
        List<Event> events = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("events", null, "category_id=?", new String[]{String.valueOf(categoryId)}, null, null, null);

        while (cursor.moveToNext()) {
            Event event = new Event(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("category_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("organizer")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("fee")),
                    cursor.getString(cursor.getColumnIndexOrThrow("datetime"))
            );
            events.add(event);
        }
        cursor.close();
        return events;
    }

    public boolean eventTitleExists(String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                "events",
                new String[]{"id"},
                "title = ?",
                new String[]{title},
                null,
                null,
                null
        );

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public List<Event> searchEvents(String searchQuery, String categoryFilter) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Event> events = new ArrayList<>();

        String selection = "1=1";
        List<String> selectionArgs = new ArrayList<>();

        if (searchQuery != null && !searchQuery.isEmpty()) {
            selection += " AND events.title LIKE ?";
            selectionArgs.add("%" + searchQuery + "%");
        }

        if (categoryFilter != null && !categoryFilter.isEmpty()) {
            selection += " AND categories.name = ?";
            selectionArgs.add(categoryFilter);
        }

        String query = "SELECT events.* FROM events " +
                "INNER JOIN categories ON events.category_id = categories.id " +
                "WHERE " + selection;

        Cursor cursor = db.rawQuery(query, selectionArgs.toArray(new String[0]));

        while (cursor.moveToNext()) {
            Event event = new Event(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("category_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("organizer")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("fee")),
                    cursor.getString(cursor.getColumnIndexOrThrow("datetime"))
            );
            events.add(event);
        }
        cursor.close();
        return events;
    }

    public boolean submitJoinRequest(int eventId, int attendeeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("event_id", eventId);
        values.put("attendee_id", attendeeId);

        long result = db.insert("Requests", null, values);
        return result != -1;
    }

}
