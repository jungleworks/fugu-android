package com.skeleton.mvp.fragment;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.skeleton.mvp.R;

/**
 * Created by rajatdhamija
 * 17/04/18.
 */

public class ImageDialog extends DialogFragment {
    private String imageUrl;

    public static ImageDialog newInstance(int arg, String url) {
        ImageDialog frag = new ImageDialog();
        Bundle args = new Bundle();
        frag.setArguments(args);
        frag.setImageUrl(url);
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fugu_image_dialog, container);
        PhotoView ivImage = view.findViewById(R.id.ivImage);

        RequestOptions options = new RequestOptions()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .priority(Priority.HIGH);

        Glide.with(getActivity())
                .asBitmap()
                .apply(options)
                .load(imageUrl)
                .into(ivImage);

//        Glide.with(getActivity()).load(imageUrl)
//                .placeholder(ContextCompat.getDrawable(getActivity(), R.drawable.placeholder))
//                .dontAnimate()
//                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                .error(ContextCompat.getDrawable(getActivity(), R.drawable.placeholder))
//                .into(ivImage);
        TextView tvCross = view.findViewById(R.id.tvCross);
        tvCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        return view;
    }

    private void setImageUrl(String url) {
        imageUrl = url;
    }

    @Override
    public void onStart() {
        super.onStart();
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        @SuppressWarnings("ConstantConditions") WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
        lp.dimAmount = 1.0f;
        getDialog().getWindow().setAttributes(lp);
        getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getDialog().setCancelable(true);
        getDialog().setCanceledOnTouchOutside(false);
    }
}
