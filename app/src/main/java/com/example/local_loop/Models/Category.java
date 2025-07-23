package com.example.local_loop.Models;

import android.os.Bundle;

import com.example.local_loop.Helpers.DisplayItem;

public class Category implements DisplayItem {
    private final int ID;
    private final String NAME;
    private final String DESCRIPTION;

    public Category(int ID, String name, String description) {
            this.ID = ID;
            this.NAME = name;
            this.DESCRIPTION = description;
    }

    @Override
    public int getID() { return ID; }

    @Override
    public String getTitle() { return NAME; }
    public String getDescription() { return DESCRIPTION; }

    public Bundle toBundle(){
        Bundle bundle = new Bundle();
        bundle.putInt("ID", ID);
        bundle.putString("name", NAME);
        bundle.putString("description", DESCRIPTION);
        return bundle;
    }

    public static Category fromBundle(Bundle bundle) {
        return new Category(
                bundle.getInt("ID"),
                bundle.getString("name"),
                bundle.getString("description")
        );

    }

    //To make arrayList sorting more efficient, use unique primary key integer instance var.
    @Override
    public boolean equals(Object obj){
        if (this == obj){
            return true;
        }
        if(!(obj instanceof Category)){
            return false;
        }
        Category other = (Category) obj;
        return this.ID == other.getID();
    }

    @Override
    public int hashCode(){
        return ID;
    }



}
