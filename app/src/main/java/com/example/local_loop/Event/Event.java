package com.example.local_loop.Event;

import com.example.local_loop.Category.DisplayItem;

public class Event implements DisplayItem {
    private int id;
    private final String name;
    private final String description;
    private final int categoryId;
    private final String organizer;
    private final double fee;
    private final String dateTime;

    // Updated constructor to include categoryName
    public Event(int id, String name, String description, int categoryId, String organizer, double fee, String dateTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.organizer = organizer;
        this.fee = fee;
        this.dateTime = dateTime;
    }

    @Override
    public int getID() { return id; }
    public void setId(int id) { this.id = id; }
    @Override
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getCategoryId() { return categoryId; }
    public String getOrganizer() { return this.organizer; }
    public double getFee() { return fee; }
    public String getDateTime() { return dateTime; }
}

