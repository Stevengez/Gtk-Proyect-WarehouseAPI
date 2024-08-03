package com.gtk.warehouse.api.model;

public class APIStatus {
    private String status = "Error";

    public APIStatus(String status){
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
