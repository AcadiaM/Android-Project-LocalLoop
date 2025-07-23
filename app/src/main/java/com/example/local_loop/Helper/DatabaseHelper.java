package com.example.local_loop.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.local_loop.Models.Account;
import com.example.local_loop.Models.User;
import com.example.local_loop.Models.Category;
import com.example.local_loop.Models.Event;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
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
    private static final String REQUEST_ID = "request_id";
    private static final String REQUESTS_EVENT_ID = "event_id";
    private static final String REQUESTS_PARTICPANT_ID = "attendee_id";
    private static final String REQUESTS_STATUS = "status";




    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * The initialization of all tables in the database.
     *
     * @param db The database.
     */


    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                USERS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USERS_ROLE + " TEXT NOT NULL CHECK(" + USERS_ROLE + " IN ('admin', 'organizer', 'participant')), " +
                USERS_USERNAME + " TEXT UNIQUE NOT NULL, " +
                USERS_EMAIL + " TEXT UNIQUE NOT NULL, " +
                USERS_PASSWORD + " TEXT NOT NULL, " +
                USERS_FIRSTNAME + " TEXT NOT NULL, " +
                USERS_LASTNAME + " TEXT NOT NULL, " +
                USERS_ISACTIVE + " INTEGER NOT NULL DEFAULT 1);");

        db.execSQL("CREATE TABLE " + TABLE_CATEGORIES + " (" +
                CATEGORIES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CATEGORIES_NAME + " TEXT UNIQUE NOT NULL, " +
                CATEGORIES_DESCRIPTION + " TEXT NOT NULL);");

        db.execSQL("CREATE TABLE " + TABLE_EVENTS + " (" +
                EVENTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EVENTS_TITLE + " TEXT UNIQUE NOT NULL, " +
                EVENTS_DESCRIPTION + " TEXT, " +
                EVENTS_FEE + " REAL, " +
                EVENTS_DATETIME + " TEXT, " +
                EVENTS_CATEGORY_ID + " INTEGER NOT NULL, " +
                EVENTS_ORGANIZER + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + EVENTS_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + CATEGORIES_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY(" + EVENTS_ORGANIZER + ") REFERENCES " + TABLE_USERS + "(" + USERS_ID + ") ON DELETE CASCADE);");

        db.execSQL("CREATE TABLE " + TABLE_REQUESTS + " (" +
                REQUEST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                REQUESTS_EVENT_ID + " INTEGER NOT NULL, " +
                REQUESTS_PARTICPANT_ID + " INTEGER NOT NULL, " +
                REQUESTS_STATUS + " TEXT DEFAULT 'pending', " +
                "FOREIGN KEY(" + REQUESTS_EVENT_ID + ") REFERENCES " + TABLE_EVENTS + "(" + EVENTS_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY(" + REQUESTS_PARTICPANT_ID + ") REFERENCES " + TABLE_USERS + "(" + USERS_ID + ") ON DELETE CASCADE);");
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
    public long insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("firstName", user.getFirstName());
        values.put("lastName", user.getLastName());
        values.put("username", user.getUsername());
        values.put("email", user.getEmail());
        values.put("password", user.getPassword());
        values.put("role", user.getRole());

       return db.insert(TABLE_USERS, null, values);
    }

    /**
     * This function verifies that a user exists in the database, checking for username and password.
     *
     * @param username the desired user's username.
     * @param password the desired user's password.
     * @return true if the user exists in the database, false otherwise.
     */
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


    /**
     * This function is called at the beginning of the main activity to add the predetermined admin user to the database.
     * The admin data is taken from the function getAdmin in the admin class.
     */
    public void insertAdmin() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, USERS_USERNAME+" = ?", new String[]{"admin"}, null, null, null);
        if (cursor.getCount() == 0) {
            User admin = new User("admin","admin","-","XPI76SZUqyCjVxgnUjm0","ADMIN","-");
            admin.setUserID(insertUser(admin));
        }
        cursor.close();
    }

    /**
     * This function searches for a user in the database. If found and the user is an organizer, its created events are deleted. Then, the user is deleted.
     *
     * @param userID the email of the desired user.
     */
    public void deleteUser(int userID) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_USERS, USERS_ID+" = ?", new String[]{String.valueOf(userID)});
    }

    /**
     * This function searches for a user in the database with a username, setting its active value to 0 (inactive).
     *
     * @param userID the username of the desired user.
     */


    public void setUserState(int userID, int status) {
        //The user is active (active=1) and is set to inactive=0
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERS_ISACTIVE, status);
        db.update(TABLE_USERS, values, USERS_ID+" = ?", new String[]{String.valueOf(userID)});
    }

    /**
     * This function searches for a user with its username and verifies if the user is set to active or inactive.
     *
     * @param userID the username of the desired user.
     * @return the active value of the user, 1 if the user is active, 0 otherwise.
     */
    public int getUserState(int userID) {
        int active = 1; // Default to active.
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{USERS_ISACTIVE}, USERS_ID+"= ?", new String[]{String.valueOf(userID)}, null, null, null);
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

    //Using Account since they are parcelable
    public List<Account> getUsers() {

        List<Account> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS, null, USERS_ROLE+" != ?", new String[]{"admin"}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Account user = new Account(
                        cursor.getInt(0), // userID
                        cursor.getString(1), // role
                        cursor.getString(2), // username
                        cursor.getString(3), // email
                        cursor.getString(5), // firstName
                        cursor.getString(6)  // Lastname
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
        values.put(CATEGORIES_NAME, name);
        values.put(CATEGORIES_DESCRIPTION, description);
        db.insert(TABLE_CATEGORIES, null, values);

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
        values.put(CATEGORIES_NAME, newName);
        values.put(CATEGORIES_DESCRIPTION, newDescription);
        db.update(TABLE_CATEGORIES, values, CATEGORIES_ID+"=?", new String[]{String.valueOf(id)});
    }

    /**
     * This function searches for a category using its row id and deletes it from the database.
     *
     * @param id the id of the category to delete.
     */
    public void deleteCategory(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CATEGORIES, CATEGORIES_ID+"=?", new String[]{String.valueOf(id)});
    }

    /**
     * This function creates a list of all existing categories.
     *
     * @return the list of category objects with all existing categories.
     */
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES, null, null, null, null, null, null);

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
    public boolean checkCategoryName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES, new String[]{CATEGORIES_ID}, CATEGORIES_NAME+"=?", new String[]{name}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // EVENT FUNCTIONS

    /**
     * This function adds a new event into the database.
     *
     * @param event for the event
     */
    public Event addEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(EVENTS_TITLE, event.getTitle());
        values.put(EVENTS_DESCRIPTION, event.getDescription());
        values.put(EVENTS_FEE, event.getFee());
        values.put(EVENTS_DATETIME, event.getDateTime());
        values.put(EVENTS_CATEGORY_ID, event.getCategoryId());
        values.put(EVENTS_ORGANIZER, event.getOrganizer());
        long id = db.insert(TABLE_EVENTS, null, values);
        db.close();
        if(id<1){
            Log.d("Event adding", "failed to add to DB");
        }

        Event e = new Event((int)id, event.getTitle(), event.getDescription(), event.getCategoryId(),event.getOrganizer(),event.getFee(),event.getDateTime());
        return e;

    }

    /**
     * This function updates an existing event using the row id of the event.
     *y.
     */
    
    //update the event item and send that to the DB
    public void updateEvent(int eventId, String title, String description, double fee, String datetime, int categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EVENTS_TITLE, title);
        values.put(EVENTS_DESCRIPTION, description);
        values.put(EVENTS_FEE, fee);
        values.put(EVENTS_DATETIME, datetime);
        values.put(EVENTS_CATEGORY_ID, categoryId);

        db.update(TABLE_EVENTS, values, EVENTS_ID+"=?", new String[]{String.valueOf(eventId)});
        db.close();
    }

    /**
     * This function deletes an event from the database using the row id of the event.
     *
     * @param id the row id of the event to be removed.
     */
    public void deleteEvent(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_EVENTS, USERS_ID+"=?", new String[]{String.valueOf(id)});
    }

    /**
     * This function gets a list of all the events created by a given organizer.
     *
     * @param organizerID of the desired organizer.
     * @return a list of event objects, with all events made by the given organizer.
     */

    //TODO --> check if I queried incorrecltly
    public List<Event> getEventsByOrganizer(int organizerID) {
        List<Event> events = new ArrayList<>();

        Log.d("EventsbyOrg", "this started");

        SQLiteDatabase db = this.getReadableDatabase();

        // Join Events with Categories to get category name
        String query = "SELECT e.id, e.title, e.description, e.category_id, e.organizer, e.fee, e.datetime " +
                "FROM Events e " +
                "JOIN Categories c ON e.category_id = c.id " +
                "WHERE e.organizer = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(organizerID)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(EVENTS_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(EVENTS_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(EVENTS_DESCRIPTION));
                int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(EVENTS_CATEGORY_ID));
                Integer org = cursor.getInt(cursor.getColumnIndexOrThrow(EVENTS_ORGANIZER));
                double fee = cursor.getDouble(cursor.getColumnIndexOrThrow(EVENTS_FEE));
                String dateTime = cursor.getString(cursor.getColumnIndexOrThrow(EVENTS_DATETIME));

                Event event = new Event(id, title, description, categoryId, org, fee, dateTime);
                events.add(event);

            } while (cursor.moveToNext());
        }
        Log.d("EventsbyOrg", "this succeeded");

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
    public Category getCategory(int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES, null, CATEGORIES_ID+"=?", new String[]{String.valueOf(categoryId)}, null, null, null);
        if (cursor.moveToFirst()) {
            Category category = new Category(
                    cursor.getInt(cursor.getColumnIndexOrThrow(CATEGORIES_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CATEGORIES_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CATEGORIES_DESCRIPTION))
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
    public List<Event> getEventByCategory(int categoryId) {
        List<Event> events = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EVENTS, null, EVENTS_CATEGORY_ID+"=?", new String[]{String.valueOf(categoryId)}, null, null, null);

        while (cursor.moveToNext()) {
            Event event = new Event(
                    cursor.getInt(cursor.getColumnIndexOrThrow(EVENTS_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(EVENTS_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(EVENTS_DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(EVENTS_CATEGORY_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(EVENTS_ORGANIZER)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(EVENTS_FEE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(EVENTS_DATETIME))
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
                TABLE_EVENTS,
                new String[]{EVENTS_ID},
                EVENTS_TITLE+" = ?",
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
                    cursor.getInt(cursor.getColumnIndexOrThrow(EVENTS_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(EVENTS_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(EVENTS_DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(EVENTS_CATEGORY_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(EVENTS_ORGANIZER)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(EVENTS_FEE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(EVENTS_DATETIME))
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
     * @param participantID the id of the attendee.
     */
    public void submitRequest(int eventId, int participantID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(REQUESTS_EVENT_ID, eventId);
        values.put(REQUESTS_PARTICPANT_ID, participantID);
        db.insert(TABLE_REQUESTS, null, values);
        db.close();
    }

    //Checks if request exist and status!

    public RequestStatus getRequestStatus(int eventId, int participantID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_REQUESTS, new String[]{REQUESTS_STATUS}, REQUESTS_EVENT_ID+" = ? AND "+ REQUESTS_PARTICPANT_ID+" = ?", new String[]{String.valueOf(eventId),String.valueOf(participantID)}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                String status = cursor.getString(0);
                cursor.close();
                return RequestStatus.valueOf(status);
            }
            cursor.close();
            return RequestStatus.INACTIVE;
        }
    }

    public List<Event> getParticipantEventRequests(int userID) {
        List<Event> events = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT e.id, e.title, e.description, e.fee, e.datetime, e.category_id, e.organizer " +
                "FROM events e " +
                "JOIN requests r ON e.id = r.event_id " +
                "WHERE r.attendee_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userID)});

        while (cursor.moveToNext()) {
            Event event = new Event(
                    cursor.getInt(cursor.getColumnIndexOrThrow(EVENTS_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(EVENTS_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(EVENTS_DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(EVENTS_CATEGORY_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(EVENTS_ORGANIZER)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(EVENTS_FEE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(EVENTS_DATETIME))
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
    public List<Account> getPendingRequestsByEvent(int eventId) {

        SQLiteDatabase db = this.getReadableDatabase();
        List<Account> pendingUsers = new ArrayList<>();

        Cursor cursor = db.query(
                TABLE_REQUESTS,
                new String[]{REQUESTS_PARTICPANT_ID},
                EVENTS_ID+" = ? AND "+REQUESTS_STATUS+"= ?",
                new String[]{String.valueOf(eventId), RequestStatus.PENDING.getREQSTATUS()},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            do {
                int participantID = cursor.getInt(2);  // attendee_id = userID
                // Retrieve full User details
                Account user = getUserFromDB(participantID);
                if (user != null) {
                    pendingUsers.add(user);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return pendingUsers;
    }


    /**
     * Updates the status of a join request for a specific attendee and event.
     *
     * @param eventId    The ID of the event.
     * @param participantID The ID of the attendee (username).
     * @param newStatus  The new status to set (e.g. "Approved", "Rejected").
     */
    public void updateRequestStatus(int eventId, int participantID, RequestStatus newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(REQUESTS_STATUS, newStatus.getRequestStatus());
        db.update(TABLE_REQUESTS, values,EVENTS_ID+" = ? AND "+REQUESTS_PARTICPANT_ID+"= ?",
                new String[]{String.valueOf(eventId), String.valueOf(participantID)});

        db.close();
    }

    public Account getUserFromDB(int userID){
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


    //For messages to Users
    public String getUsername(int userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{USERS_USERNAME}, USERS_ID+" = ?", new String[]{String.valueOf(userID)}, null, null, null);
        if (cursor.moveToFirst()) {
            String status = cursor.getString(0);
            cursor.close();
            return status;
        }
        cursor.close();
        return null;
    }

    public  List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_EVENTS, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String description = cursor.getString(2);
            int categoryID = cursor.getInt(3);
            int organizer = cursor.getInt(4);
            int fee = cursor.getInt(5);
            String dateTime = cursor.getString(6);

            events.add(new Event(id, title, description, categoryID, organizer, fee, dateTime));
        }
        cursor.close();
        return events;
    }





}
