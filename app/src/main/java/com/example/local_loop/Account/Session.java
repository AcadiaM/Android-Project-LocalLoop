package com.example.local_loop.Account;

import android.os.Parcelable;

public class Session implements Parcelable {
    private final int userID;
    private final String role;
    private final String username, firstName, lastName;
    // Events, Categories or other items?

    public Session(int userID, String role, String username, String firstName, String lastName) {
        this.userID = userID;
        this.role = role;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeInt(this.userID);
        dest.writeString(this.role);
        dest.writeString(this.username);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
    }

    protected Session(android.os.Parcel in) {
        this.userID = in.readInt();
        this.role = in.readString();
        this.username = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
    }

    public static final Creator<Session> CREATOR = new Creator<Session>() {
        @Override
        public Session createFromParcel(android.os.Parcel source) {
            return new Session(source);
        }

        @Override
        public Session[] newArray(int size) {
            return new Session[size];
        }
    };

    public int getUserID() {
        return userID;
    }

    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
