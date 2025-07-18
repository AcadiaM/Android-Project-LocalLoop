package com.example.local_loop.CreateAccount;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * A User class, extended by all types of users: Admin,  and .
 */

public class User implements Parcelable {

    private final int userID;
    private final String role;
    private final String username, email, password, firstName, lastName;


    public User(int userID, String role, String username, String email, String password, String firstName, String lastName) {
        this.userID = userID;
        this.role = role;

        this.username = username;
        this.password = password;
        this.email = email;

        this.firstName = firstName;
        this.lastName = lastName;
    }

    protected User(Parcel in) {
        userID = in.readInt();
        role = in.readString();
        username = in.readString();
        email = in.readString();
        password = in.readString();
        firstName = in.readString();
        lastName = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public long getUserID(){return this.userID; }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(userID);
        parcel.writeString(role);
        parcel.writeString(username);
        parcel.writeString(email);
        parcel.writeString(password);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
    }
}