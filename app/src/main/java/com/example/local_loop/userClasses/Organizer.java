

/**
 * User class of type organizer.
 * Capable of creating events.
 */
package com.example.local_loop.userClasses;

import com.example.local_loop.Login.User;

public class Organizer extends User {

    public Organizer(String username, String firstName, String lastName, String password, String email) {
        super("Organizer",username, firstName, lastName, password, email);
    }

}