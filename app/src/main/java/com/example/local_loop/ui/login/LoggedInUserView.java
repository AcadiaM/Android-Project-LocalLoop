package com.example.local_loop.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private String displayName;
    private String userType;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String displayName, String userType) {
        this.displayName = displayName;
        this.userType = userType;
    }


    String getDisplayName() {
        return displayName;
    }

    String getUserType(){
        return userType;
    }

}