package com.skeleton.mvp.data.model.getPublicInfo;

/*********************************
 Created by Amandeep Chauhan     *
 Date :- 29/06/2020              *
 ********************************/

public class GetPublicInfoResponse {
    private long statusCode;
    private String message;
    private Data data;

    public long getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(long value) {
        this.statusCode = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String value) {
        this.message = value;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data value) {
        this.data = value;
    }
}

