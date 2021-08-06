package com.easyfilepicker.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.easyfilepicker.R;
import com.easyfilepicker.filter.entity.Directory;
import com.easyfilepicker.filter.entity.ImageFile;
import com.easyfilepicker.filter.entity.VideoFile;

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by gurmail on 15/01/19.
 *
 * @author gurmail
 */
public class ImageDirectoryAdapter extends BaseAdapter<Directory, ImageDirectoryAdapter.ViewHolder> {

    private static final int COLUMN_NUMBER = 2;

    public ImageDirectoryAdapter(Context ctx) {
        this(ctx, new ArrayList<Directory>());
    }

    public ImageDirectoryAdapter(Context ctx, ArrayList<Directory> directories) {
        super(ctx, directories);
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.vw_layout_item_image_pick, parent, false);
        ViewGroup.LayoutParams params = itemView.getLayoutParams();
        if (params != null) {
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            params.height = width / COLUMN_NUMBER;
        }
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mIvThumbnail.setVisibility(View.VISIBLE);
        holder.folderInfo.setVisibility(View.VISIBLE);
        if (mList.get(position).getFiles().get(0) instanceof ImageFile) {
            ImageFile file = (ImageFile) mList.get(position).getFiles().get(0);
            RequestOptions options = new RequestOptions();
            Glide.with(mContext)
                    .load(file.getPath())
                    .apply(options.centerCrop())
                    .transition(withCrossFade())
                    .into(holder.mIvThumbnail);
        } else {
            VideoFile file = (VideoFile) mList.get(position).getFiles().get(0);
            RequestOptions options = new RequestOptions();
            Glide.with(mContext)
                    .load(file.getPath())
                    .apply(options.centerCrop())
                    .transition(withCrossFade())
                    .into(holder.mIvThumbnail);
        }


        holder.mIvThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.OnSelectStateChanged(true, mList.get(holder.getAdapterPosition()));
            }
        });

        holder.totalCount.setText("" + mList.get(position).getFiles().size());
        holder.directoryName.setText(mList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mIvThumbnail;
        private RelativeLayout folderInfo;
        private TextView totalCount;
        private TextView directoryName;
        private ImageView cbx;

        public ViewHolder(View itemView) {
            super(itemView);
            mIvThumbnail = itemView.findViewById(R.id.iv_thumbnail);
            folderInfo = itemView.findViewById(R.id.folder_info);
            totalCount = itemView.findViewById(R.id.total_count);
            directoryName = itemView.findViewById(R.id.directory_name);
            cbx = itemView.findViewById(R.id.cbx);
            cbx.setVisibility(View.GONE);
        }
    }
}
