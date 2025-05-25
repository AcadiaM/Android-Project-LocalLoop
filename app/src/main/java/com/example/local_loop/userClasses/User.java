

/**
 * Abstract User class, extended by all types of users: Admin, Participant and Organizer.
 */
package com.example.local_loop.userClasses;
public abstract class User {

    private String type;
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private String email;


    public User(String type, String username, String firstName, String lastName, String password, String email) {
        this.type = type;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getType() {
        return this.type;
    }

    public String getUsername() {
        return this.username;
    }
    public String getFirstName() {
        return this.firstName;
    }
    public String getLastName() {
        return this.lastName;
    }

    public String getPassword() {
        return this.password;
    }
    public String getEmail() {
        return this.email;
    }
}