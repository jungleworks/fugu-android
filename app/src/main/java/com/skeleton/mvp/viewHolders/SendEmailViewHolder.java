package com.skeleton.mvp.viewHolders;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.skeleton.mvp.R;
import com.skeleton.mvp.adapter.OnRecyclerViewObjectLongClickListener;
import com.skeleton.mvp.baseRecycler.BaseViewHolder;
import com.skeleton.mvp.baseRecycler.OnRecyclerViewObjectClickListener;
import com.skeleton.mvp.model.GetAllMembers;
import com.skeleton.mvp.utils.FuguUtils;

import java.util.HashMap;

/**
 * Created by rajatdhamija
 * 10/08/18.
 */

public class SendEmailViewHolder extends BaseViewHolder<GetAllMembers, OnRecyclerViewObjectClickListener<GetAllMembers>, OnRecyclerViewObjectLongClickListener<GetAllMembers>> {
    private AppCompatTextView tvName, tvEmail, tvUserIcon;
    private AppCompatImageView ivTick;
    private ImageView ivUserImage;
    private static final HashMap<Integer, Integer> dummyImagesArray;

    static {
        dummyImagesArray = new HashMap<>();
        dummyImagesArray.put(1, R.drawable.ring_grey);
        dummyImagesArray.put(2, R.drawable.ring_indigo);
        dummyImagesArray.put(3, R.drawable.ring_purple);
        dummyImagesArray.put(4, R.drawable.ring_red);
        dummyImagesArray.put(0, R.drawable.ring_teal);
    }

    public SendEmailViewHolder(View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tvName);
        tvEmail = itemView.findViewById(R.id.tvEmail);
        tvUserIcon = itemView.findViewById(R.id.tvUserIcon);
        ivTick = itemView.findViewById(R.id.ivTick);
        ivUserImage = itemView.findViewById(R.id.ivUserImage);
    }

    @Override
    public void onBind(final GetAllMembers item, @Nullable final OnRecyclerViewObjectClickListener<GetAllMembers> listener, @Nullable OnRecyclerViewObjectLongClickListener<GetAllMembers> longListener, @Nullable final BaseViewHolder<GetAllMembers, OnRecyclerViewObjectClickListener<GetAllMembers>, OnRecyclerViewObjectLongClickListener<GetAllMembers>> holder) {
        tvName.setText(item.getFullName());
        tvEmail.setText(item.getEmail());
        if (!TextUtils.isEmpty(item.getUserThumbnailImage())) {
            tvUserIcon.setVisibility(View.GONE);
            final Context activity = itemView.getContext();

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.fugu_ic_channel_icon)
                    .error(R.drawable.fugu_ic_channel_icon)
                    .fitCenter()
                    .priority(Priority.HIGH)
                    .transforms(new CenterCrop(), new RoundedCorners(100));

            Glide.with(activity)
                    .asBitmap()
                    .apply(options)
                    .load(item.getUserThumbnailImage())
                    .into(ivUserImage);


//            Glide.with(activity).load(item.getUserThumbnailImage()).asBitmap()
//                    .centerCrop()
//                    .placeholder(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_channel_icon))
//                    .error(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_channel_icon))
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(new BitmapImageViewTarget(ivUserImage) {
//                        @Override
//                        protected void setResource(Bitmap resource) {
//                            RoundedBitmapDrawable circularBitmapDrawable =
//                                    RoundedBitmapDrawableFactory.create(activity.getResources(), resource);
//                            circularBitmapDrawable.setCircular(true);
//                            ivUserImage.setImageDrawable(circularBitmapDrawable);
//                        }
//                    });
        } else {
            tvUserIcon.setVisibility(View.VISIBLE);
            try {
                tvUserIcon.setText(FuguUtils.Companion.getFirstCharInUpperCase(item.getFullName()));
                ivUserImage.setImageResource(dummyImagesArray.get((int) (item.getUserId() % 5)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (item.isSelected()) {
            ivTick.setVisibility(View.VISIBLE);
        } else {
            ivTick.setVisibility(View.GONE);
        }
        if (listener != null) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(item, holder);
                }
            });

        }
    }
}
