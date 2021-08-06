package com.skeleton.mvp.model.mentions;

import com.google.gson.annotations.SerializedName;


/**
 * The local JSON file users.json contains sample user data that is loaded and used
 *  to demonstrate '@' mentions.
 */
public class User {

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

}
