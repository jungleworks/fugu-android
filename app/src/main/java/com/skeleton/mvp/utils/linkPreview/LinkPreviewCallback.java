package com.skeleton.mvp.utils.linkPreview;

/**
 * Created by bhavya on 09/08/17.
 */

public interface LinkPreviewCallback {

    void onPre();

    /**
     *
     * @param sourceContent
     *            Class with all contents from preview.
     * @param isNull
     *            Indicates if the content is null.
     */
    void onPos(SourceContent sourceContent, boolean isNull);
}
