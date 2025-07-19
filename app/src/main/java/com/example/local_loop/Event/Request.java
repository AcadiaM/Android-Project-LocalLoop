package com.example.local_loop.Event;

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
}
