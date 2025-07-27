package com.example.local_loop.Helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.example.local_loop.UserContent.Category;
import com.example.local_loop.CreateAccount.User;
import com.example.local_loop.UserContent.Event;

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


    // USER FUNCTIONS

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

    /**
     * This function finds the username of a user from their email.
     *
     * @param email the email of the desired user.
     * @return the username of the user if found, null otherwise.
     */
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

    /**
     * This function is called at the beginning of the main activity to add the predetermined admin user to the database.
     * The admin data is taken from the function getAdmin in the admin class.
     */
    public void insertAdmin() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("users", null, "username = ?", new String[]{"admin"}, null, null, null);
        if (cursor.getCount() == 0) {
            User admin = User.getAdmin();
            insertUser(admin);
        }
        cursor.close();
    }

    /**
     * This function searches for a user in the database. If found and the user is an organizer, its created events are deleted. Then, the user is deleted.
     *
     * @param email the email of the desired user.
     */
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

    /**
     * This function searches for a user in the database with a username, setting its active value to 0 (inactive).
     *
     * @param username the username of the desired user.
     */
    public void deactivateUser(String username) {
        //The user is active (active=1) and is set to inactive=0
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("active", 0);
        db.update(TABLE_USERS, values, "username = ?", new String[]{username});
    }

    /**
     * This function searches for a user in the database with a username, setting its active value to 1 (active).
     *
     * @param username the username of the desired user.
     */
    public void reactivateUser(String username) {
        //The user is inactive (active=0) and is set to active=1.
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("active", 1);
        db.update(TABLE_USERS, values, "username = ?", new String[]{username});
    }

    /**
     * This function searches for a user with its username and verifies if the user is set to active or inactive.
     *
     * @param username the username of the desired user.
     * @return the active value of the user, 1 if the user is active, 0 otherwise.
     */
    public int isActive(String username) {
        int active = 1; // Default to active.
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{"active"}, "username = ?", new String[]{username}, null, null, null);
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

        Cursor cursor = db.query(TABLE_USERS, null, "role != ?", new String[]{"Admin"}, null, null, null);

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

    /**
     * This function adds a new category to the database.
     *
     * @param name the name of the new category.
     * @param description the description of the new category.
     */
    public void addCategory(String name, String description) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("description", description);
        db.insert("categories", null, values);
    }

    /**
     * This function uses the id of an existing category to update its name in the database.
     *
     * @param id the row id of the category to update.
     * @param newName the new name of the category.
     * @param newDescription the new description of the category.
     */
    public void renameCategory(int id, String newName, String newDescription) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", newName);
        values.put("description", newDescription);
        db.update("categories", values, "id=?", new String[]{String.valueOf(id)});
    }

    /**
     * This function searches for a category using its row id and deletes it from the database.
     *
     * @param id the id of the category to delete.
     */
    public void deleteCategory(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("categories", "id=?", new String[]{String.valueOf(id)});
    }

    /**
     * This function creates a list of all existing categories.
     *
     * @return the list of category objects with all existing categories.
     */
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

    /**
     * This function verifies if the given name of a category exists in the database.
     *
     * @param name the name of the category to search.
     * @return true if the category exists and false otherwise.
     */
    public boolean categoryNameExists(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("categories", new String[]{"id"}, "name = ?", new String[]{name}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // EVENT FUNCTIONS

    /**
     * This function adds a new event into the database.
     *
     * @param title the title of the new event.
     * @param description the description of the new event.
     * @param categoryId the row id of the category the new event will belong to.
     * @param datetime the date and time of the new event.
     * @param fee the given fee of the new event.
     * @param organizer the username of the organizer who created the new event.
     */
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

    /**
     * This function updates an existing event using the row id of the event.
     *
     * @param eventId     the row id of the event.
     * @param title       the new title of the event.
     * @param description the new description of the event.
     * @param fee         the new fee of the event.
     * @param datetime    the new date and time of the event.
     * @param categoryId  the new row id of the category.
     */
    public void updateEvent(int eventId, String title, String description, double fee, String datetime, int categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("fee", fee);
        values.put("datetime", datetime);
        values.put("category_id", categoryId);

        db.update("events", values, "id = ?", new String[]{String.valueOf(eventId)});
        db.close();
    }

    /**
     * This function deletes an event from the database using the row id of the event.
     *
     * @param id the row id of the event to be removed.
     */
    public void deleteEvent(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("events", "id=?", new String[]{String.valueOf(id)});
    }

    /**
     * This function gets a list of all the events created by a given organizer.
     *
     * @param organizer the username of the desired organizer.
     * @return a list of event objects, with all events made by the given organizer.
     */
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

    /**
     * This function searches for and returns a category with the given row id.
     *
     * @param categoryId the row id of the desired category.
     * @return the category corresponding to the row id, null otherwise.
     */
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

    /**
     * This function gets all events in a given category, using the row id of the category.
     *
     * @param categoryId the row id of the desired category.
     * @return the list of event objects with all events in the category.
     */
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

    /**
     * This function verifies if an event exists using the given title.
     *
     * @param title the title of the desired event.
     * @return true if the event is found in the database, false otherwise.
     */
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


    //The functions below will be properly implemented in the fourth deliverable.


    /**
     * This function searches all events in the database using the given query.
     *
     * @param searchQuery the search query of the user.
     * @param categoryFilter filters applied to the search.
     * @return a list of event objects that correspond to the search query and filters.
     */
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

    /**
     * This function allows a participant to submit a request to attend an event, while ensuring that the participant has not already submitted a request to the event.
     *
     * @param eventId the row id of the event.
     * @param attendeeId the id of the attendee.
     */
    public void submitJoinRequest(int eventId, String attendeeId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("event_id", eventId);
        values.put("attendee_id", attendeeId);

        db.insert("Requests", null, values);
        db.close();
    }

    public boolean hasJoinRequest(int eventId, String attendeeId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query("requests",
                new String[]{"request_id"},
                "event_id = ? AND attendee_id = ?",
                new String[]{String.valueOf(eventId), attendeeId},
                null, null, null);

        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();

        return exists;
    }

    public String getStatus(int eventId, String attendeeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("requests", new String[]{"status"}, "event_id = ? AND attendee_id = ?", new String[]{String.valueOf(eventId),attendeeId}, null, null, null);
        if (cursor.moveToFirst()) {
            String status = cursor.getString(0);
            cursor.close();
            return status;
        }
        cursor.close();
        return null;
    }
    public List<Event> getEventsUserRequested(String username) {
        List<Event> events = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT e.id, e.title, e.description, e.fee, e.datetime, e.category_id, e.organizer " +
                "FROM events e " +
                "JOIN requests r ON e.id = r.event_id " +
                "WHERE r.attendee_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{username});

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

    /**
     * Returns a list of Users who have a pending join request for the given event.
     *
     * @param eventId The ID of the event.
     * @return List of User objects who have requested to join the event and are still pending.
     */
    public List<User> getRequestsByEvent(int eventId, String status) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<User> users = new ArrayList<>();

        Cursor cursor = db.query(
                "requests",
                new String[]{"attendee_id"},
                "event_id = ? AND status = ?",
                new String[]{String.valueOf(eventId), status},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            do {
                String attendeeUsername = cursor.getString(0);  // attendee_id = username

                // Retrieve full User details
                User user = getUserByUsername(attendeeUsername);

                if (user != null) {
                    users.add(user);
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return users;
    }

    /**
     * Retrieves a User object by username.
     *
     * @param username The username of the user.
     * @return The User object if found, otherwise null.
     */
    public User getUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS,
                new String[]{"firstName", "lastName", "username", "email", "password", "role"},
                "username = ?",
                new String[]{username},
                null, null, null
        );

        User user = null;

        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getString(0),  // firstName
                    cursor.getString(1),  // lastName
                    cursor.getString(2),  // username
                    cursor.getString(3),  // email
                    cursor.getString(4),  // password
                    cursor.getString(5)   // role
            );
        }

        cursor.close();
        db.close();

        return user;
    }

    /**
     * Updates the status of a join request for a specific attendee and event.
     *
     * @param eventId    The ID of the event.
     * @param attendeeId The ID of the attendee (username).
     * @param newStatus  The new status to set (e.g. "Approved", "Rejected").
     */
    public void updateRequestStatus(int eventId, String attendeeId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", newStatus);

        db.update(
                "requests",
                values,
                "event_id = ? AND attendee_id = ?",
                new String[]{String.valueOf(eventId), attendeeId}
        );

        db.close();
    }

    public void deleteRequest(int eventId, String attendeeId) {
        String request_id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("requests", new String[]{"request_id"}, "event_id = ? AND attendee_id = ?", new String[]{String.valueOf(eventId),attendeeId}, null, null, null);
        if (cursor.moveToFirst()) {
            request_id = cursor.getString(0);
            cursor.close();
        }
        else{
            request_id = null;
        }
        cursor.close();
        SQLiteDatabase db1 = getWritableDatabase();
        db1.delete("requests", "request_id = ?", new String[]{request_id});
    }

}
