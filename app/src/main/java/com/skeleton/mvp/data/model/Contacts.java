package com.skeleton.mvp.data.model;

/**
 * Created by rajatdhamija on 05/01/18.
 */
public class Contacts {
    private String name;
    private String phone;
    private String email;

    /**
     * Instantiates a new Contacts.
     *
     * @param name  the name
     * @param email the email
     */
    public Contacts(String name, String phone, String email) {
        this.name = name;
        this.email = email;
        this.phone=phone;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
