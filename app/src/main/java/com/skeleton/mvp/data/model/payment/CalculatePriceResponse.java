package com.skeleton.mvp.data.model.payment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CalculatePriceResponse {
    @SerializedName("statusCode")
    @Expose
    private long statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
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

    public class Data {
        @SerializedName("totalPrice")
        @Expose
        private String totalPrice;
        @SerializedName("no_of_users")
        @Expose
        private long noOfUsers;
        @SerializedName("currency")
        @Expose
        private String currency;

        public String getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(String value) {
            this.totalPrice = value;
        }

        public long getNoOfUsers() {
            return noOfUsers;
        }

        public void setNoOfUsers(long value) {
            this.noOfUsers = value;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String value) {
            this.currency = value;
        }
    }


}
