package com.example.local_loop.Event;

public class Event {
    private int id;
    private final String title;
    private String description;
    private final int categoryId;
    private String organizer;
    private final double fee;
    private final String dateTime;

    // Updated constructor to include categoryName
    public Event(int id, String title, String description, int categoryId, String organizer, double fee, String dateTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.organizer = organizer;
        this.fee = fee;
        this.dateTime = dateTime;
    }

    public int getID() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getCategoryId() { return categoryId; }
    public String getOrganizer() { return this.organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }
    public double getFee() { return fee; }
    public String getDateTime() { return dateTime; }
}

