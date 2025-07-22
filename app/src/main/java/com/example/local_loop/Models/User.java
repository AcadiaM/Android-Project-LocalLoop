package com.example.local_loop.Models;



public class User {

    //admin XPI76SZUqyCjVxgnUjm0

    private int userID;
    private String role;
    private String username, email, password, firstName, lastName;


    public User(int userID, String role, String username, String email, String password, String firstName, String lastName){
        this.userID = userID;
        this.role = role;

        this.username = username;
        this.password = password;
        this.email = email;

        this.firstName = firstName;
        this.lastName = lastName;

    }
    public User(String role, String username, String email, String password, String firstName, String lastName){
        this(-1,role,username,email, password,firstName,lastName);
    }



    public int getUserID(){return this.userID; }

    public void setUserID(long id){
        this.userID = (int) id;
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
    public String getRole() {
        return this.role;
    }

}