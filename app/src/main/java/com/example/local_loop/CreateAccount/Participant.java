package com.example.local_loop.CreateAccount;

/**
 * User class of type participant.
 * Basic user class capable of signing up for events.
 */

public class Participant extends User {

    public Participant(String firstName, String lastName, String username, String email, String password) {
        super(firstName, lastName, username, email, password, "participant");
    }

}