package com.skeleton.mvp.utils;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;

public class PaletteBitmap {
    public final Palette palette;
    public final Bitmap bitmap;

    public PaletteBitmap(@NonNull Bitmap bitmap, @NonNull Palette palette) {
        this.bitmap = bitmap;
        this.palette = palette;
    }
}