package com.skeleton.mvp.data.model;

/**
 * Created by bhavya on 27/12/17.
 * juggernaut-android-mvp
 */

public class BaseRecyclerModel {

    private int itemType = 0;

    public int getItemType() {
        return itemType;
    }

    public void setItemType(final int itemType) {
        this.itemType = itemType;
    }
}
