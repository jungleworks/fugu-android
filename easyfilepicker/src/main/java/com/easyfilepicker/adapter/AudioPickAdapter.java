package com.easyfilepicker.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.easyfilepicker.R;
import com.easyfilepicker.Util;
import com.easyfilepicker.filter.entity.AudioFile;

import java.util.ArrayList;


public class AudioPickAdapter extends BaseAdapter<AudioFile, AudioPickAdapter.AudioPickViewHolder> implements RecyclerItemClickListener {

    private RecyclerView recyclerView;
    public AudioPickAdapter(Context ctx, RecyclerView recyclerView) {
        this(ctx, new ArrayList<AudioFile>(), recyclerView);
    }

    public AudioPickAdapter(Context ctx, ArrayList<AudioFile> list, RecyclerView recyclerView) {
        super(ctx, list);
        this.recyclerView = recyclerView;
    }

    @Override
    public AudioPickViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.vw_layout_item_audio_pick, parent, false);
        return new AudioPickViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(final AudioPickViewHolder holder, final int position) {
        final AudioFile file = mList.get(position);

        holder.mTvTitle.setText(file.getName());
        holder.mTvTitle.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        if (holder.mTvTitle.getMeasuredWidth() >
                Util.getScreenWidth(mContext) - Util.dip2px(mContext, 10 + 32 + 10 + 48 + 10 * 2)) {
            holder.mTvTitle.setLines(2);
        } else {
            holder.mTvTitle.setLines(1);
        }
        holder.mCbx.setVisibility(View.VISIBLE);
        holder.mTvDuration.setText(Util.getDurationString(file.getDuration()));
        if (file.isSelected()) {
            holder.mCbx.setSelected(true);
        } else {
            holder.mCbx.setSelected(false);
        }

//        holder.mCbx.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectItem(holder);
////                if(mList.get(holder.getAdapterPosition()).isSelected()) {
////                    holder.mCbx.setSelected(false);
////                    mList.get(holder.getAdapterPosition()).setSelected(holder.mCbx.isSelected());
////
////                    if (mListener != null) {
////                        mListener.OnSelectStateChanged(holder.mCbx.isSelected(), mList.get(holder.getAdapterPosition()));
////                    }
////                } else {
////                    holder.mCbx.setSelected(true);
////                    mList.get(holder.getAdapterPosition()).setSelected(holder.mCbx.isSelected());
////
////                    if (mListener != null) {
////                        mListener.OnSelectStateChanged(holder.mCbx.isSelected(), mList.get(holder.getAdapterPosition()));
////                    }
////                }
//
//            }
//        });
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                holder.mCbx.setSelected(true);
//                mList.get(holder.getAdapterPosition()).setSelected(holder.mCbx.isSelected());
//
//                if (mListener != null) {
//                    mListener.OnSelectStateChanged(holder.mCbx.isSelected(), mList.get(holder.getAdapterPosition()));
//                }
//            }
//        });
    }

    private void selectItem(AudioPickViewHolder holder) {
        for(int i=0;i<mList.size();i++) {
            if(i == holder.getAdapterPosition()) {
                if(mList.get(i).isSelected()) {
                    holder.mCbx.setSelected(false);
                } else {
                    holder.mCbx.setSelected(true);
                }
            } else {
                holder.mCbx.setSelected(false);
            }
        }
        notifyDataSetChanged();

//        if(mList.get(holder.getAdapterPosition()).isSelected()) {
//            holder.mCbx.setSelected(false);
//            mList.get(holder.getAdapterPosition()).setSelected(holder.mCbx.isSelected());
//
//        } else {
//            holder.mCbx.setSelected(true);
//            mList.get(holder.getAdapterPosition()).setSelected(holder.mCbx.isSelected());
//        }

        if (mListener != null) {
            mListener.OnSelectStateChanged(holder.mCbx.isSelected(), mList.get(holder.getAdapterPosition()));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onItemClick(View viewClicked, View parentView) {
        int positionInList = recyclerView.getChildLayoutPosition(parentView);
        if(positionInList!=RecyclerView.NO_POSITION){
            int i1 = viewClicked.getId();
            if (i1 == R.id.main_layout) {
                for (int i = 0; i < mList.size(); i++) {
                    if (i == positionInList) {
                        mList.get(i).setSelected(true);
                        if(mList.get(i).isPlaying()) {
                            mList.get(i).setPlaying(false);
                        } else {
                            mList.get(i).setPlaying(true);
                        }
                    } else {
                        mList.get(i).setSelected(false);
                        mList.get(i).setPlaying(false);
                    }
                }
                notifyDataSetChanged();
                if (mListener != null) {
                    mListener.OnSelectStateChanged(mList.get(positionInList).isPlaying(), mList.get(positionInList));
                }
            }
        }
    }

    @Override
    public void onItemSelected(View viewClicked, View parentView, boolean b) {

    }

    class AudioPickViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mainLayout;
        private TextView mTvTitle;
        private TextView mTvDuration;
        private ImageView mCbx;

        public AudioPickViewHolder(final View itemView, final RecyclerItemClickListener itemClickListener) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.main_layout);
            mTvTitle = itemView.findViewById(R.id.tv_audio_title);
            mTvDuration = itemView.findViewById(R.id.tv_duration);
            mCbx = itemView.findViewById(R.id.cbx);
            mCbx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(mainLayout, itemView);
                }
            });
            mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(mainLayout, itemView);
                }
            });
        }
    }
}
