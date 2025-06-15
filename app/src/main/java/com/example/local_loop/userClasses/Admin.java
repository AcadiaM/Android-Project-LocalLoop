package com.example.local_loop.userClasses;

import com.example.local_loop.Login.User;

/**
 * User class of type administrator.
 * Responsible for moderating events and other users.
 */

public class Admin extends User {

    //private static Admin admin;

    public Admin(String role ,String username, String firstName, String lastName, String password, String email) {
        super("Admin",username, firstName, lastName, password, email);
    }

    /*public static Admin getAdmin(){
        if(admin == null){
            admin = new Admin();
        }
        return admin;
    } */

}