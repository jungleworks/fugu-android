package com.skeleton.mvp.data.model;

/**
 * Created by rajatdhamija on 02/01/18.
 */

public class Domain {
    private String domain;
    private String businessName;

    public Domain(String domain, String businessName) {
        this.domain = domain;
        this.businessName = businessName;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
}
