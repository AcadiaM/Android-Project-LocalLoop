package com.example.local_loop.data.model;

/**
 * UserFactory class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userType;

    private String userId;
    private String displayName;

    public LoggedInUser(String userId, String displayName, String userType) {
        this.userId = userId;
        this.displayName = displayName;
        this.userType = userType;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    String getUserType(){
        return userType;
    }
}