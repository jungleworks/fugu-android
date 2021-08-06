package com.skeleton.mvp.utils;

import androidx.annotation.NonNull;

import java.io.File;
import java.net.URI;

public class FuguFile extends File {
    private Long channelId;

    public FuguFile(@NonNull String pathname) {
        super(pathname);
    }

    public FuguFile(String parent, @NonNull String child) {
        super(parent, child);
    }

    public FuguFile(File parent, @NonNull String child) {
        super(parent, child);
    }

    public FuguFile(@NonNull URI uri) {
        super(uri);
    }

    public final void setFileChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public final Long getFileChannelId() {
        return channelId;
    }
}
