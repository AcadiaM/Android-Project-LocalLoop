package com.example.local_loop.Event;

public class Request {
    final private int requestID;
    final private int eventID;
    final private String attendeeID;
    private String status;

    public Request(int requestID, int eventID, String attendeeID, String status){
        this.requestID = requestID;
        this.eventID = eventID;
        this.attendeeID = attendeeID;
        this.status = status;
    }

    public int getRequestID(){
        return this.requestID;
    }
    public int getEventID(){
        return this.eventID;
    }
    public String getAttendeeID(){
        return this.attendeeID;
    }
    public String getStatus(){
        return this.status;
    }
    public void setStatus(String status){
        this.status = status;
    }
}
