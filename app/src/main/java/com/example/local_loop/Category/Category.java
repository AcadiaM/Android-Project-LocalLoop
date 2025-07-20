package com.example.local_loop.Category;

public class Category{
        private final int ID;
        private final String NAME;
        private final String DESCRIPTION;
        private int image_resource;

        public Category(int ID, String name, String description) {
            this.ID = ID;
            this.NAME = name;
            this.DESCRIPTION = description;
            this.image_resource = 0;
        }

        public int getID() { return ID; }
        public String getName() { return NAME; }
        public String getDescription() { return DESCRIPTION; }

    public int getImage_resource() {
        return image_resource;
    }

    public void setImage_resource(int image_resource) {
        this.image_resource = image_resource;
    }
}
