package com.skeleton.mvp.data.model;

/**
 * Created by rajatdhamija on 17/01/18.
 */
public class Group {
    private String name;
    private String isMuted;
    private Long channelId;

    /**
     * Instantiates a new Group.
     *
     * @param name    the name
     * @param isMuted the is muted
     */
    public Group(String name, String isMuted, Long channelId) {
        this.name = name;
        this.isMuted = isMuted;
        this.channelId = channelId;
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
     * Gets muted.
     *
     * @return the muted
     */
    public String getMuted() {
        return isMuted;
    }

    /**
     * Sets muted.
     *
     * @param muted the muted
     */
    public void setMuted(String muted) {
        isMuted = muted;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }
}
