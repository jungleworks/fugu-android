package com.skeleton.mvp.utils.linkPreview;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by bhavya on 09/08/17.
 */

public interface DowloadImageCallback {

    /**
     *
     * @param imageView
     *            ImageView to receive the bitmap.
     * @param loadedBitmap
     *            Bitmap downloaded from url.
     * @param url
     *            Image url.
     */
    void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url);

}