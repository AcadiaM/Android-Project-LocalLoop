package com.example.local_loop.Helpers;

public enum RequestStatus {
    PENDING("pending");

    private final String status;

    private RequestStatus(String status){
        this.status = status;
    }

    public String getRequestStatus(){
        return status;
    }
}
