package com.skeleton.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Bhavya Rattan on 09/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguPutUserDetailsResponse {

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

        @SerializedName("is_whitelabel")
        @Expose
        private Boolean isWhiteLabel;
        @SerializedName("en_user_id")
        @Expose
        private String en_user_id;
        @SerializedName("user_id")
        @Expose
        private Long userId;
        @SerializedName("user_unique_key")
        @Expose
        private String userUniqueKey;
        @SerializedName("business_id")
        @Expose
        private Integer businessId;
        @SerializedName("business_name")
        @Expose
        private String businessName;
        @SerializedName("full_name")
        @Expose
        private String fullName;
        @SerializedName("app_secret_key")
        @Expose
        private String appSecretKey;
        @SerializedName("conversations")
        @Expose
        private List<FuguConversation> conversations = null;
        @SerializedName("email")
        @Expose
        private String email;

        public Long getUserId() {
            return userId;
        }

        public String getEn_user_id() {
            return en_user_id;
        }

        public String getBusinessName() {
            if (businessName == null) {
                businessName = "";
            }
            return businessName;
        }

        public String getFullName() {
            return fullName;
        }

        public String getAppSecretKey() {
            return appSecretKey;
        }

        public void setAppSecretKey(String appSecretKey) {
            this.appSecretKey = appSecretKey;
        }

        public List<FuguConversation> getConversations() {
            return conversations;
        }

        public Boolean getWhiteLabel() {
            if (isWhiteLabel == null) {
                isWhiteLabel = false;
            }
            return isWhiteLabel;
        }

        public void setWhiteLabel(Boolean whiteLabel) {
            isWhiteLabel = whiteLabel;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}