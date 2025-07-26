package com.example.local_loop.CreateAccount;

/**
 * A User class representing all types of users: Admin, Participant, and Organizer.
 */
public class User {

    private final String firstName, lastName, username, email, password, role;

    private static User admin;  // Singleton instance for the predefined admin.

    public User(String firstName, String lastName, String username, String email, String password, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public static User getAdmin(){
        if(admin == null){
            admin = new User("admin", "admin", "admin", "admin", "XPI76SZUqyCjVxgnUjm0", "admin");
        }
        return admin;
    }

    public String getUsername() { return this.username; }
    public String getFirstName() { return this.firstName; }
    public String getLastName() { return this.lastName; }
    public String getPassword() { return this.password; }
    public String getEmail() { return this.email; }
    public String getRole() { return this.role; }
}
