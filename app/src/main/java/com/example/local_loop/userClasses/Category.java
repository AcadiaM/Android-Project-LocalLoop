package com.example.local_loop.userClasses;

public class Category {
        private final int id;
        private final String name;
        private final String description;

        public Category(int id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
}
