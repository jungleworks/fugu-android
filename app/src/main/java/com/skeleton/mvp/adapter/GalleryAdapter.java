package com.skeleton.mvp.adapter;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.skeleton.mvp.R;
import com.skeleton.mvp.model.Media;
import com.skeleton.mvp.utils.FuguMimeUtils;

import java.util.List;
import java.util.regex.Pattern;

import static com.skeleton.mvp.constant.FuguAppConstant.IMAGE_MAP;


/**
 * Created by Lincoln on 31/03/16.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {

    private List<Media> images;
    private Context mContext;

    public GalleryAdapter(Context context, List<Media> images) {
        mContext = context;
        this.images = images;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Media image = images.get(position);
        if (image.getMessageType() == 10) {
            holder.thumbnail.setVisibility(View.VISIBLE);
            holder.rlFile.setVisibility(View.GONE);
            holder.ivPlay.setVisibility(View.GONE);


            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .fitCenter()
                    .priority(Priority.HIGH)
                    .transforms(new CenterCrop(), new RoundedCorners(15));

            Glide.with(mContext)
                    .asBitmap()
                    .apply(options)
                    .load(image.getThumbnailUrl())
                    .into(holder.thumbnail);

//            Glide.with(mContext).load(image.getThumbnailUrl())
//                    .thumbnail(0.5f)
//                    .crossFade()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .placeholder(R.drawable.placeholder)
//                    .error(R.drawable.placeholder)
//                    .into(holder.thumbnail);
        } else if (image.getMessageType() == 11) {
            holder.ivPlay.setVisibility(View.GONE);
            String[] extensions = image.getFileurl().split(Pattern.quote("."));
            holder.tvFile.setText(extensions[extensions.length - 1].toUpperCase());
            Integer imageSource = IMAGE_MAP.get(extensions[extensions.length - 1].toLowerCase());
            if (imageSource == null) {
                String mimeType = FuguMimeUtils.guessMimeTypeFromExtension(extensions[extensions.length - 1].toLowerCase());
                if (mimeType != null)
                    imageSource = IMAGE_MAP.get(mimeType.split("/")[0]);
            }
            if (imageSource != null) {
                holder.ivFile.setImageResource(imageSource);
                holder.tvFile.setVisibility(View.GONE);
            } else {
                holder.ivFile.setImageResource(R.drawable.file_model);
                holder.tvFile.setVisibility(View.VISIBLE);
            }
            holder.rlFile.setVisibility(View.VISIBLE);
            holder.thumbnail.setVisibility(View.GONE);
        } else {
            holder.ivPlay.setVisibility(View.VISIBLE);
            holder.thumbnail.setVisibility(View.VISIBLE);
            holder.rlFile.setVisibility(View.GONE);
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .fitCenter()
                    .priority(Priority.HIGH);

            Glide.with(mContext)
                    .asBitmap()
                    .apply(options)
                    .load(image.getThumbnailUrl())
                    .into(holder.thumbnail);


//            Glide.with(mContext).load(image.getThumbnailUrl())
//                    .thumbnail(0.5f)
//                    .crossFade()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .placeholder(R.drawable.placeholder)
//                    .error(R.drawable.placeholder)
//                    .into(holder.thumbnail);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_thumbnail, parent, false);

        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        RelativeLayout rlFile;
        TextView tvFile;
        ImageView ivFile;
        ImageView ivPlay;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.thumbnail);
            ivFile = view.findViewById(R.id.ivFile);
            ivPlay = view.findViewById(R.id.ivPlay);
            rlFile = view.findViewById(R.id.rlFile);
            tvFile = view.findViewById(R.id.tvFile);
        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public interface ClickListener {
        void onClick(View view, int position);

//        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                if (e.getAction()==MotionEvent.ACTION_UP) {
                    clickListener.onClick(child, rv.getChildPosition(child));
                }
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}