package com.example.local_loop.userClasses;

/**
 * User class of type administrator.
 * Responsible for moderating events and other users.
 */

public class Admin extends User {

    public Admin(String username, String firstName, String lastName, String password, String email) {
        super("Admin", username, firstName, lastName, password, email);
    }

}