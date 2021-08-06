
package com.skeleton.mvp.data.model.object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo;

public class ObjectResponse {

    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private WorkspacesInfo data;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public WorkspacesInfo getData() {
        return data;
    }

    public void setData(WorkspacesInfo data) {
        this.data = data;
    }
}
