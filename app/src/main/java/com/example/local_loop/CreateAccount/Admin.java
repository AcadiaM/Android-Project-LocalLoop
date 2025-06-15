package com.example.local_loop.CreateAccount;

/**
 * User class of type administrator.
 * Responsible for moderating events and other users.
 */

public class Admin extends User {

    private static Admin admin;

    public Admin(String firstName, String lastName, String username, String email, String password) {
        super(firstName, lastName, username, email, password, "admin");
    }

    public static Admin getAdmin(){
        if(admin == null){
            admin = new Admin( "admin", "admin", "admin","admin", "XPI76SZUqyCjVxgnUjm0");
        }
        return admin;
    }

}