package com.example.local_loop.UserContent;

import android.os.Bundle;

public class Request {
    final protected int requestID;
    final protected int eventID;
    final protected String attendeeID;
    protected String status;

    public Request(int requestID, int eventID, String attendeeID, String status){
        this.requestID = requestID;
        this.eventID = eventID;
        this.attendeeID = attendeeID;
        this.status = status;
    }

    public Bundle toBundle(){
        Bundle bundle = new Bundle();
        bundle.putInt("requestID", requestID);
        bundle.putInt("eventID", eventID);
        bundle.putString("attendeeID", attendeeID);
        bundle.putString("status", status);
        return bundle;
    }

    public static Request fromBundle(Bundle bundle) {
        return new Request(
                bundle.getInt("requestID"),
                bundle.getInt("eventID"),
                bundle.getString("attendeeID"),
                bundle.getString("status")
        );

    }
}
