package com.example.local_loop.Models;

import android.os.Parcelable;

public class Account implements Parcelable {
    private final int userID;
    private final String role;
    private final String username, email, firstName, lastName;


    public Account(int userID, String role, String username, String email, String firstName, String lastName) {
        this.userID = userID;
        this.role = role;
        this.username = username;
        this.email = email;
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
        dest.writeString(this.email);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
    }

    protected Account(android.os.Parcel in) {
        this.userID = in.readInt();
        this.role = in.readString();
        this.username = in.readString();
        this.email = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(android.os.Parcel source) {
            return new Account(source);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
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

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public String toString() {
        return "Account{" +
                "userID=" + userID +
                ", role='" + role + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

    //To make arrayList sorting more efficient, use unique primary key integer instance var.
    @Override
    public boolean equals(Object obj){
        if (this == obj){
            return true;
        }
        if(!(obj instanceof Account)){
            return false;
        }
        Account other = (Account) obj;
        return this.userID == other.getUserID();
    }

    @Override
    public int hashCode(){
        return userID;
    }
}
