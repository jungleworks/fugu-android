package com.skeleton.mvp.data.model.live;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * The type Data.
 */
public class Data {

    @SerializedName("link")
    @Expose
    private String link = "";
    @SerializedName("token")
    @Expose
    private String token = "";

    /**
     * Gets link.
     *
     * @return the link
     */
    public String getLink() {
        return link;
    }

    /**
     * Sets link.
     *
     * @param link the link
     */
    public void setLink(final String link) {
        this.link = link;
    }

    /**
     * Gets token.
     *
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets token.
     *
     * @param token the token
     */
    public void setToken(final String token) {
        this.token = token;
    }

}
