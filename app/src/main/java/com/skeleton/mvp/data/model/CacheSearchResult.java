package com.skeleton.mvp.data.model;

/**
 * Created by rajatdhamija on 08/01/18.
 */
public class CacheSearchResult {
    private String name;
    private Long user_id;
    private String user_image;
    private String email;
    private String userUniqueKey;
    private Integer clickCount;

    /**
     * Instantiates a new Search result.
     *
     * @param name       the name
     * @param user_id    the user id
     * @param user_image the user image
     * @param email      the email
     */
    public CacheSearchResult(String name, Long user_id, String user_image, String email, Integer clickCount) {
        this.name = name;
        this.user_id = user_id;
        this.user_image = user_image;
        this.email = email;
        this.clickCount = clickCount;
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
     * Gets user id.
     *
     * @return the user id
     */
    public Long getUser_id() {
        return user_id;
    }

    /**
     * Sets user id.
     *
     * @param user_id the user id
     */
    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    /**
     * Gets user image.
     *
     * @return the user image
     */
    public String getUser_image() {
        return user_image;
    }

    /**
     * Sets user image.
     *
     * @param user_image the user image
     */
    public void setUser_image(String user_image) {
        this.user_image = user_image;
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

    public String getUserUniqueKey() {
        return userUniqueKey;
    }

    public void setUserUniqueKey(String userUniqueKey) {
        this.userUniqueKey = userUniqueKey;
    }

    public Integer getClickCount() {
        if (clickCount == null) {
            clickCount = 0;
        }
        return clickCount;
    }

    public void setClickCount(Integer clickCount) {
        this.clickCount = clickCount;
    }
}
