package com.example.local_loop.CreateAccount;

public class UserFactory {

    private int userID = 0;
    private String role, username, email, password, firstName, lastName;



    public UserFactory(String role, String username, String email, String password, String firstName, String lastName) {
        this.role = role;
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User signUpUser(){
        User newUser = new User(userID,role,username,email,password,firstName,lastName);
        return newUser;
    }

    public void setUserID(long id){
        this.userID = (int) id;
    }

    public int getUserID() {
        return userID;
    }
    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
