package com.example.local_loop.CreateAccount;

/**
 * User class of type organizer.
 * Capable of creating events.
 */

public class Organizer extends User {

    public Organizer(String firstName, String lastName, String username, String email, String password) {
        super( firstName, lastName, username, email, password, "organizer");
    }

}