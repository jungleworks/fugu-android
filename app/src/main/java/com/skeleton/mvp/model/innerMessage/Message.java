
package com.skeleton.mvp.model.innerMessage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.skeleton.mvp.constant.FuguAppConstant;

public class Message {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("muid")
    @Expose
    private String muid;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("message_type")
    @Expose
    private Integer messageType;
    @SerializedName("date_time")
    @Expose
    private String dateTime;
    @SerializedName("user_reaction")
    @Expose
    private com.skeleton.mvp.model.UserReaction userReaction;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("thumbnail_url")
    @Expose
    private String thumbnailUrl = "";

    @SerializedName("image_url")
    @Expose
    private String imageUrl = "";

    @SerializedName("url")
    @Expose
    private String url = "";
    @SerializedName("file_name")
    @Expose
    String fileName = "";
    @SerializedName("file_size")
    @Expose
    String fileSize = "";
    @SerializedName("file_extension")
    @Expose
    String fileExtension = "";

    @SerializedName("user_image")
    @Expose
    String userImage = "";
    @SerializedName("message_state")
    @Expose
    private int messageState;

    @SerializedName("user_type")
    @Expose
    private int userType = FuguAppConstant.UserType.CUSTOMER;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getMuid() {
        return muid;
    }

    public void setMuid(String muid) {
        this.muid = muid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public com.skeleton.mvp.model.UserReaction getUserReaction() {
        return userReaction;
    }

    public void setUserReaction(com.skeleton.mvp.model.UserReaction userReaction) {
        this.userReaction = userReaction;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public int getMessageState() {
        return messageState;
    }

    public void setMessageState(int messageState) {
        this.messageState = messageState;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }
}
