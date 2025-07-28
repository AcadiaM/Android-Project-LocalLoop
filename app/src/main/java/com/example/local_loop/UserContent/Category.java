package com.example.local_loop.UserContent;

import com.example.local_loop.Helpers.DisplayItem;

public class Category implements DisplayItem {
    private final int ID;
    private String NAME;
    private final String DESCRIPTION;

    public Category(int ID, String name, String description) {
            this.ID = ID;
            this.NAME = name;
            this.DESCRIPTION = description;
    }

    @Override
    public int getID() { return ID; }

    @Override
    public String getName() { return NAME; }
    @Override
    public void setName(String NAME) {this.NAME = NAME;}
    public String getDescription() { return DESCRIPTION; }



}
