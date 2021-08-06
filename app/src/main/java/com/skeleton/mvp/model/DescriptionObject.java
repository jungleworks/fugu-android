package com.skeleton.mvp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cl-macmini-01 on 12/15/17.
 */

public class DescriptionObject {

    @SerializedName("header")
    private String header;

    @SerializedName("content")
    private String content;

    public String getHeader() {
        return header;
    }

    public String getContent() {
        return content;
    }

    public void setHeader(final String header) {
        this.header = header;
    }

    public void setContent(final String content) {
        this.content = content;
    }
}
