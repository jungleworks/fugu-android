package com.skeleton.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Bhavya Rattan on 17/06/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguUploadImageResponse {
    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("thumbnail_url")
        @Expose
        private String thumbnailUrl = "";
        @SerializedName("url")
        @Expose
        private String url = "";

        @SerializedName("image_url_100x100")
        @Expose
        private String image_url_100x100 = "";

        @SerializedName("image_size")
        @Expose
        private Long imageSize;

        @SerializedName("muid")
        @Expose
        private String muid;

        public Long getImageSize() {
            return imageSize;
        }

        public void setImageSize(Long imageSize) {
            this.imageSize = imageSize;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        public String getUrl() {
            return url;
        }

        public String getImage_url_100x100() {
            return image_url_100x100;
        }

        public String getMuid() {
            return muid;
        }

        public void setMuid(String muid) {
            this.muid = muid;
        }
    }
}



