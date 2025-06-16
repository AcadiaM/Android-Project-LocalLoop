package com.example.local_loop.Category;

public class Category {
        private final int ID;
        private final String NAME;
        private final String DESCRIPTION;

        public Category(int ID, String name, String description) {
            this.ID = ID;
            this.NAME = name;
            this.DESCRIPTION = description;
        }

        public int getID() { return ID; }
        public String getName() { return NAME; }
        public String getDescription() { return DESCRIPTION; }
}
