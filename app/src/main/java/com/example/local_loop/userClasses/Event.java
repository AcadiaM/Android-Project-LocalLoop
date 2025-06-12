package com.example.local_loop.userClasses;

public class Event {
        private int id;
        private int categoryId;
        private String title;

        public Event(int id, int categoryId, String title) {
            this.id = id;
            this.categoryId = categoryId;
            this.title = title;
        }

        public int getId() { return id; }
        public int getCategoryId() { return categoryId; }
        public String getTitle() { return title; }
}
