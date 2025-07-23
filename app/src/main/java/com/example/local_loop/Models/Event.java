package com.example.local_loop.Models;

import android.os.Bundle;

import com.example.local_loop.Helpers.DisplayItem;

public class Event implements DisplayItem {
    private final int id;
    private final String title;
    private final String description;
    private final int categoryId;
    private final int organizer;
    private final double fee;
    private final String dateTime;

    // Updated constructor to include categoryName
    public Event(int id, String title, String description, int categoryId, int organizer, double fee, String dateTime) {
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
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getCategoryId() { return categoryId; }
    public int getOrganizer() { return this.organizer; }
    public double getFee() { return fee; }
    public String getDateTime() { return dateTime; }

    public Bundle toBundle(){
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putString("title", title);
        bundle.putString("description", description);
        bundle.putInt("categoryId", categoryId);
        bundle.putInt("organizer", organizer);
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
                bundle.getInt("organizer"),
                bundle.getDouble("fee"),
                bundle.getString("dateTime")
        );
    }

    //To make arrayList sorting more efficient, use unique primary key integer instance var.
    @Override
    public boolean equals(Object obj){
        if (this == obj){
            return true;
        }
        if(!(obj instanceof Event)){
            return false;
        }
        Event other = (Event) obj;
        return this.id == other.getID();
    }

    @Override
    public int hashCode(){
        return id;
    }
}

