package com.example.local_loop.Helper;

public enum RequestStatus {
    PENDING("pending"),
    APPROVED("approved"),
    REFUSED("refused"),
    INACTIVE("inactive");

    private final String status;

    private RequestStatus(String status){
        this.status = status;
    }

    public String getREQSTATUS(){
        return status;
    }
}
