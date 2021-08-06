package com.skeleton.mvp.data.model.groupTasks;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TaskWork {
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("file_name")
    @Expose
    private String fileName;
    @SerializedName("file_size")
    @Expose
    private String fileSize;
    @SerializedName("muid")
    @Expose
    private String muid;

    public String getURL() {
        return url;
    }

    public void setURL(String value) {
        this.url = value;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String value) {
        this.fileName = value;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String value) {
        this.fileSize = value;
    }

    public String getMuid() {
        return muid;
    }

    public void setMuid(String muid) {
        this.muid = muid;
    }
}
