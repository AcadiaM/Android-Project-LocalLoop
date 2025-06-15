package com.example.local_loop.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.local_loop.Category.Category;
import com.example.local_loop.userClasses.Event;

import java.util.ArrayList;
import java.util.List;

public class DBCategoryHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "EventCategories.db";
    private static final int DB_VERSION = 1;

    public DBCategoryHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");
        db.execSQL("CREATE TABLE categories (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, description Text NOT NULL);");

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
        onCreate(db);
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

    public boolean categoryNameExists(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("categories", new String[]{"id"}, "name = ?", new String[]{name}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
}
