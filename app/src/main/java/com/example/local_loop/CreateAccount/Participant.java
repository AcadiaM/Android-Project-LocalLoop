

/**
 * User class of type participant.
 * Basic user class capable of signing up for events.
 */
package com.example.local_loop.CreateAccount;

public class Participant extends User {

    public Participant(String firstName, String lastName, String username, String email, String password) {
        super(firstName, lastName, username, email, password, "participant");
    }

}