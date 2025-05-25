/**
 * User class of type participant.
 * Basic user class capable of signing up for events.
 */
package com.example.local_loop.userClasses;
public class Participant extends User {

    public Participant(String username, String password, String email) {
        super("Participant", username, password, email);
    }

}