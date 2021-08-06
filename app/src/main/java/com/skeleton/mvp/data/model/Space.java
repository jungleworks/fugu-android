package com.skeleton.mvp.data.model;

/**
 * Created by rajatdhamija on 05/02/18.
 */

public class Space {
    private String name;
    private String accessToken;
    private boolean isCurrentBusiness;
    private int businessId;
    private boolean hasAnyUnreadCount = false;

    public Space(String name, boolean isCurrentBusiness, int businessId, boolean hasAnyUnreadCount) {
        this.name = name;
        this.isCurrentBusiness = isCurrentBusiness;
        this.businessId = businessId;
        this.hasAnyUnreadCount = hasAnyUnreadCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public boolean isCurrentBusiness() {
        return isCurrentBusiness;
    }

    public void setCurrentBusiness(boolean currentBusiness) {
        isCurrentBusiness = currentBusiness;
    }

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public boolean isHasAnyUnreadCount() {
        return hasAnyUnreadCount;
    }

    public void setHasAnyUnreadCount(boolean hasAnyUnreadCount) {
        this.hasAnyUnreadCount = hasAnyUnreadCount;
    }
}
