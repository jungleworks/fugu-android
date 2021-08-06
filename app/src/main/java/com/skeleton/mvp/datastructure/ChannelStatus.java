package com.skeleton.mvp.datastructure;

/**
 * Created by ankit on 14/09/17.
 */

public enum ChannelStatus {
    OPEN(1),
    CLOSED(2);

    private int ordinal;

    ChannelStatus(int ordinal){
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
