package com.example.local_loop.Helpers;

public enum RequestStatus {
    PENDING("pending"),
    APPROVED("approved"),
    REFUSED("refused"),
    INACTIVE("inactive");

    private final String status;

    private RequestStatus(String status){
        this.status = status;
    }

    public String getReqStatus(){
        return status;
    }
}
