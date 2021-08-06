package com.skeleton.mvp.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.skeleton.mvp.R;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.fugudatabase.CommonData;
import com.skeleton.mvp.model.FuguAttachmentModel;

import java.util.ArrayList;


public class FuguAttachmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FuguAppConstant {
    private LayoutInflater inflater;
    private ArrayList<FuguAttachmentModel> attachmentList = new ArrayList<>();
    private Context context;
    private OnAttachListener mOnAttach;
    private boolean showDocs;
    private boolean isOneToOne;
    private boolean showEmpty;

    public void showEmpty(boolean showEmpty) {
        this.showEmpty = showEmpty;
        if (showEmpty) {
            attachmentList.add(new FuguAttachmentModel(R.drawable.ic_gallery_video, "",
                    SELECT_NONE, R.drawable.ring_white, false));
            attachmentList.add(new FuguAttachmentModel(R.drawable.ic_gallery_video, "",
                    SELECT_NONE, R.drawable.ring_white, false));
            attachmentList.add(new FuguAttachmentModel(R.drawable.ic_gallery_video, "",
                    SELECT_NONE, R.drawable.ring_white, false));
        }
    }

    public FuguAttachmentAdapter(Context context, boolean showDocs, boolean isOneToOne) {
        inflater = LayoutInflater.from(context);
        this.showDocs = showDocs;
        this.isOneToOne = isOneToOne;
        this.context = context;
        attachmentList.add(new FuguAttachmentModel(R.drawable.ic_gallery_camera,
                context.getResources().getString(R.string.fugu_camera), OPEN_CAMERA_ADD_IMAGE, R.drawable.ring_white, true));
        attachmentList.add(new FuguAttachmentModel(R.drawable.ic_gallery,
                context.getResources().getString(R.string.fugu_gallery), REQUEST_CODE_PICK_IMAGE, R.drawable.ring_bluee, true));
        if (CommonData.getSupportedFileTypes() != null && CommonData.getSupportedFileTypes().contains("video")) {
            attachmentList.add(new FuguAttachmentModel(R.drawable.ic_gallery_video, "Video",
                    REQUEST_CODE_PICK_VIDEO, R.drawable.ring_orangee, true));
        }
        if (showDocs) {
            if (!isOneToOne) {
                attachmentList.add(new FuguAttachmentModel(R.drawable.ic_poll,
                        context.getResources().getString(R.string.fugu_poll), START_POLL, R.drawable.ring_greenn, true));
            }
            if (CommonData.getSupportedFileTypes() != null && CommonData.getSupportedFileTypes().contains("audio")) {
                attachmentList.add(new FuguAttachmentModel(R.drawable.ic_gallery_music,
                        context.getResources().getString(R.string.fugu_audio), REQUEST_CODE_PICK_AUDIO, R.drawable.ring_redd, true));
            }
            if (CommonData.getSupportedFileTypes() != null && CommonData.getSupportedFileTypes().contains("file")) {
                attachmentList.add(new FuguAttachmentModel(R.drawable.ic_gallery_file,
                        context.getResources().getString(R.string.fugu_document), REQUEST_CODE_PICK_FILE, R.drawable.ring_purplee, true));
            }
        }
    }

    public void setOnAttachListener(OnAttachListener OnAttachListener) {
        mOnAttach = OnAttachListener;
    }

    public interface OnAttachListener {
        public void onAttach(int action);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fugu_item_dialog_attach, parent, false);
        AttachmentViewHolder holder = new AttachmentViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final AttachmentViewHolder attachmentViewHolder = (AttachmentViewHolder) holder;
        final FuguAttachmentModel currentAttachmentItem = attachmentList.get(position);

        attachmentViewHolder.tvAttachmentType.setText(currentAttachmentItem.getText());
        if (position == 0) {
            attachmentViewHolder.ivImageBig.setImageResource(currentAttachmentItem.getImageIcon());
            attachmentViewHolder.llImageBg.setVisibility(View.GONE);
            attachmentViewHolder.ivImageBig.setVisibility(View.VISIBLE);
        } else {
            attachmentViewHolder.ivImage.setImageResource(currentAttachmentItem.getImageIcon());
            attachmentViewHolder.llImageBg.setVisibility(View.VISIBLE);
            attachmentViewHolder.ivImageBig.setVisibility(View.GONE);
        }
        attachmentViewHolder.llImageBg.setBackgroundResource(currentAttachmentItem.getColor());
        attachmentViewHolder.itemView.setOnClickListener(view -> {
            if (mOnAttach != null) {
                mOnAttach.onAttach(currentAttachmentItem.getAction());
            }
        });

        switch (position) {
            case 0:
            case 3:
                ObjectAnimator anim = ObjectAnimator.ofFloat(holder.itemView, "scaleX", 0.5f, 1.05f, 1.0f);
                anim.setDuration(500); // duration 3 seconds
                anim.start();

                // Make the object height 50%
                ObjectAnimator anim2 = ObjectAnimator.ofFloat(holder.itemView, "scaleY", 0.5f, 1.05f, 1.0f);
                anim2.setDuration(500); // duration 3 seconds
                anim2.start();
                break;
            case 1:
            case 4:
                ObjectAnimator anim3 = ObjectAnimator.ofFloat(holder.itemView, "scaleX", 0.5f, 1.05f, 1.0f);
                anim3.setDuration(600); // duration 3 seconds
                anim3.start();

                // Make the object height 50%
                ObjectAnimator anim4 = ObjectAnimator.ofFloat(holder.itemView, "scaleY", 0.5f, 1.05f, 1.0f);
                anim4.setDuration(600); // duration 3 seconds
                anim4.start();
                break;
            case 2:
            case 5:
                ObjectAnimator anim5 = ObjectAnimator.ofFloat(holder.itemView, "scaleX", 0.5f, 1.05f, 1.0f);
                anim5.setDuration(700); // duration 3 seconds
                anim5.start();

                // Make the object height 50%
                ObjectAnimator anim6 = ObjectAnimator.ofFloat(holder.itemView, "scaleY", 0.5f, 1.05f, 1.0f);
                anim6.setDuration(700); // duration 3 seconds
                anim6.start();
                break;
        }
//
//        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(holder.itemView, "scaleX", 0.5f, 1.0f);
//        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(holder.itemView, "scaleY", 0.5f, 1.0f);
//        scaleDownX.setDuration(2000);
//        scaleDownY.setDuration(2000);
//
//        AnimatorSet scaleDown = new AnimatorSet();
//        scaleDown.play(scaleDownX).with(scaleDownY);
//
//        scaleDownX.addUpdateListener(valueAnimator -> {
////            View p = (View) v.getParent();
//            holder.itemView.invalidate();
//        });
    }

    @Override
    public int getItemCount() {
        return attachmentList.size();
    }

    class AttachmentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAttachmentType;
        private ImageView ivImage, ivImageBig;
        private LinearLayout llImageBg;

        public AttachmentViewHolder(View itemView) {
            super(itemView);
            tvAttachmentType = (TextView) itemView.findViewById(R.id.tvAttachmentType);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivImageBig = itemView.findViewById(R.id.ivImageBig);
            llImageBg = itemView.findViewById(R.id.llImageBg);
        }
    }
}
