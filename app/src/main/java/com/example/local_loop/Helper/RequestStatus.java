package com.example.local_loop.Helper;

public enum RequestStatus {
    PENDING("pending"),
    APPROVED("approved"),
    REFUSED("refused");

    private final String status;

    private RequestStatus(String status){
        this.status = status;
    }

    public String getRequestStatus(){
        return status;
    }
}
