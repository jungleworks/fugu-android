
package com.skeleton.mvp.model.media;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Message implements Serializable {

    @SerializedName("message")
    @Expose
    private String message = "";

    @SerializedName("image_url")
    @Expose
    private String imageUrl = "";
    @SerializedName("thumbnail_url")
    @Expose
    private String thumbnailUrl = "";
    @SerializedName("url")
    @Expose
    private String url = "";
    @SerializedName("file_size")
    @Expose
    private String fileSize = "";
    @SerializedName("file_name")
    @Expose
    private String fileName = "";
    @SerializedName("image_width")
    @Expose
    private int imageWidth = 350;
    @SerializedName("image_height")
    @Expose
    private int imageHeight = 350;
    @SerializedName("image_url_100x100")
    @Expose
    private String imageUrl100x100 = "";




    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public String getImageUrl100x100() {
        return imageUrl100x100;
    }

    public void setImageUrl100x100(String imageUrl100x100) {
        this.imageUrl100x100 = imageUrl100x100;
    }
}
