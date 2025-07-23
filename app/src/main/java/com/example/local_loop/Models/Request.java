package com.example.local_loop.Models;

import android.os.Bundle;

public class Request {
    final protected int requestID;
    final protected int eventID;
    final protected int participantID;
    protected String status;

    public Request(int requestID, int eventID, int participantID, String status){
        this.requestID = requestID;
        this.eventID = eventID;
        this.participantID = participantID;
        this.status = status;
    }

    public Bundle toBundle(){
        Bundle bundle = new Bundle();
        bundle.putInt("requestID", requestID);
        bundle.putInt("eventID", eventID);
        bundle.putInt("participantID", participantID);
        bundle.putString("status", status);
        return bundle;
    }

    public static Request fromBundle(Bundle bundle) {
        return new Request(
                bundle.getInt("requestID"),
                bundle.getInt("eventID"),
                bundle.getInt("participantID"),
                bundle.getString("status")
        );

    }

    public int getRequestID() {
        return requestID;
    }

    public int getEventID() {
        return eventID;
    }

    public int getParticipantID() {
        return participantID;
    }

    public String getStatus() {
        return status;
    }
}
