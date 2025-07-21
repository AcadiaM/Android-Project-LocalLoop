package com.example.local_loop.UserContent;

import android.os.Bundle;

import com.example.local_loop.Helpers.DisplayItem;

public class Event implements DisplayItem {
    private int id;
    private final String title;
    private String description;
    private final int categoryId;
    private final String organizer;
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

    @Override
    public int getID() { return id; }
    @Override
    public String getName(){return title;}
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getCategoryId() { return categoryId; }
    public String getOrganizer() { return this.organizer; }
    public double getFee() { return fee; }
    public String getDateTime() { return dateTime; }

    public Bundle toBundle(){
        Bundle bundle = new Bundle();
        bundle.putInt("event_id", id);
        bundle.putString("title", title);
        bundle.putString("description", description);
        bundle.putInt("categoryId", categoryId);
        bundle.putString("organizer", organizer);
        bundle.putDouble("fee", fee);
        bundle.putString("dateTime", dateTime);
        return bundle;
    }

    public static Event fromBundle(Bundle bundle) {
        return new Event(
                bundle.getInt("id"),
                bundle.getString("title"),
                bundle.getString("description"),
                bundle.getInt("categoryId"),
                bundle.getString("organizer"),
                bundle.getDouble("fee"),
                bundle.getString("dateTime")
        );
    }
}

