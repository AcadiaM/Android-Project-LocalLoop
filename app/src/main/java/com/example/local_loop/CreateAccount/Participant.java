

/**
 * User class of type participant.
 * Basic user class capable of signing up for events.
 */
package com.example.local_loop.CreateAccount;

public class Participant extends User {

    public Participant(String username, String firstName, String lastName, String password, String email) {
        super("Participant",username, firstName, lastName, password, email);
    }

}