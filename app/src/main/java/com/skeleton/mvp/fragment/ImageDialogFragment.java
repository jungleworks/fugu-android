package com.skeleton.mvp.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.skeleton.mvp.R;

/**
 * Created by rajatdhamija
 * 09/07/18.
 */

public class ImageDialogFragment extends DialogFragment {
    private String Url, ImageUri;
    private Drawable Drawable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_dialog, container);
        PhotoView ivImage = view.findViewById(R.id.ivImage);
        String image = "";
        if (!TextUtils.isEmpty(ImageUri)) {
            image = ImageUri;
        } else {
            image = Url;
        }

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(Drawable)
                .error(R.drawable.placeholder)
                .fitCenter()
                .priority(Priority.HIGH);

        Glide.with(this)
                .asBitmap()
                .apply(options)
                .load(image)
                .into(ivImage);

//        Glide.with(this).load(image)
//                .placeholder(Drawable)
//                .dontAnimate()
//                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                .error(ContextCompat.getDrawable(getActivity(), R.drawable.placeholder))
//                .into(ivImage);
        return view;

    }

    public static ImageDialogFragment newInstance(int arg, String url, Drawable drawable, String imageUri) {
        ImageDialogFragment frag = new ImageDialogFragment();
        frag.setUrl(url);
        frag.setDrawable(drawable);
        frag.setImageURI(imageUri);
        return frag;
    }

    private void setImageURI(String imageUri) {
        ImageUri = imageUri;
    }

    private void setDrawable(Drawable drawable) {
        Drawable = drawable;
    }

    private void setUrl(String url) {
        Url = url;
    }
}
