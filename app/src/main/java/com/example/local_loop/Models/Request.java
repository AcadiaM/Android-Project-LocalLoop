package com.example.local_loop.Models;

import android.os.Bundle;

public class Request {
    final protected int requestID;
    final protected int eventID;
    final protected String participantID;
    protected String status;

    public Request(int requestID, int eventID, String participantID, String status){
        this.requestID = requestID;
        this.eventID = eventID;
        this.participantID = participantID;
        this.status = status;
    }

    public Bundle toBundle(){
        Bundle bundle = new Bundle();
        bundle.putInt("requestID", requestID);
        bundle.putInt("eventID", eventID);
        bundle.putString("participantID", participantID);
        bundle.putString("status", status);
        return bundle;
    }

    public static Request fromBundle(Bundle bundle) {
        return new Request(
                bundle.getInt("requestID"),
                bundle.getInt("eventID"),
                bundle.getString("participantID"),
                bundle.getString("status")
        );

    }
}
