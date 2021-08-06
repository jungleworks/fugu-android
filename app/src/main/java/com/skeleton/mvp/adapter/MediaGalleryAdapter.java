package com.skeleton.mvp.adapter;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.ForwardActivity;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.fugudatabase.CommonData;
import com.skeleton.mvp.model.Media;
import com.skeleton.mvp.model.Message;
import com.skeleton.mvp.util.Log;
import com.skeleton.mvp.util.MimeTypes;
import com.skeleton.mvp.utils.FuguMimeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static com.skeleton.mvp.constant.FuguAppConstant.IMAGE_MAP;

/**
 * Created
 * rajatdhamija on 25/07/18.
 */

public class MediaGalleryAdapter extends RecyclerView.Adapter<MediaGalleryAdapter.MyViewHolder> {
    private ArrayList<Media> images;
    private Context mContext;

    public MediaGalleryAdapter(ArrayList<Media> images, Context mContext) {
        this.images = images;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MediaGalleryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_fullscreen_preview, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MediaGalleryAdapter.MyViewHolder holder, int position) {
        Log.e("Holder", " Executed");
        final Media image = images.get(position);
        if (image.getMessageType() == 10) {
            holder.imageViewPreview.setVisibility(View.VISIBLE);
            holder.mVideoView.setVisibility(View.GONE);
            holder.llFile.setVisibility(View.GONE);
            holder.ivPlay.setVisibility(View.GONE);


            if(images.get(position).getMessage().getImageUrl().split("\\.")[images.get(position).getMessage().getImageUrl().split("\\.").length - 1].equals("gif")){
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        //.dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .fitCenter()
                        .priority(Priority.HIGH);

                Glide.with(mContext)
                        .asGif()
                        .apply(options)
                        .load(image.getImageUrl())
                        .into(holder.imageViewPreview);
            } else{
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
                        .load(image.getImageUrl())
                        .into(holder.imageViewPreview);
            }


        } else if (image.getMessageType() == 11) {
            String[] extensions = image.getFileurl().split(Pattern.quote("."));
            holder.tvFile.setText(extensions[extensions.length - 1].toUpperCase());
            holder.tvFileExt.setText(extensions[extensions.length - 1].toUpperCase());
            holder.ivPlay.setVisibility(View.GONE);
            Integer imageSource = IMAGE_MAP.get(extensions[extensions.length - 1].toLowerCase());
            if (imageSource == null) {
                String mimeType = FuguMimeUtils.guessMimeTypeFromExtension(extensions[extensions.length - 1].toLowerCase());
                if (mimeType != null)
                    imageSource = IMAGE_MAP.get(mimeType.split("/")[0]);
            }
            if (imageSource != null) {
                holder.ivFile.setImageResource(imageSource);
                holder.ivFile.setColorFilter(Color.parseColor("#FFFFFF"));
                holder.tvFileExt.setVisibility(View.GONE);
            } else {
                holder.ivFile.setImageResource(R.drawable.file_model);
                holder.ivFile.clearColorFilter();
                holder.tvFileExt.setVisibility(View.VISIBLE);
            }
            holder.llFile.setVisibility(View.VISIBLE);
            holder.tvFileName.setText(image.getFileName());
            holder.imageViewPreview.setVisibility(View.GONE);
            holder.mVideoView.setVisibility(View.GONE);
            holder.llOpen.setOnClickListener(v -> {
                try {
                    FileOpen.openFile(mContext, new File(image.getLocalPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else if (image.getMessageType() == 12) {
            final MediaController mediaController = new MediaController(mContext);
            holder.ivPlay.setVisibility(View.VISIBLE);
            holder.llFile.setVisibility(View.GONE);
            holder.imageViewPreview.setVisibility(View.VISIBLE);
            holder.mVideoView.setVisibility(View.GONE);
            holder.mVideoView.stopPlayback();
            BitmapPool bitmapPool = Glide.get(mContext).getBitmapPool();
            int microSecond = 1000000;
//            VideoBitmapDecoder videoBitmapDecoder = new VideoBitmapDecoder(microSecond);
//            FileDescriptorBitmapDecoder fileDescriptorBitmapDecoder = new FileDescriptorBitmapDecoder(videoBitmapDecoder, bitmapPool, DecodeFormat.PREFER_ARGB_8888);

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
                    .into(holder.imageViewPreview);


            holder.ivPlay.setOnClickListener(v -> {
                holder.ivPlay.setVisibility(View.GONE);
                holder.imageViewPreview.setVisibility(View.GONE);
                holder.mVideoView.setVisibility(View.VISIBLE);
                mediaController.setAnchorView(holder.mVideoView);
                mediaController.setMediaPlayer(holder.mVideoView);
                mediaController.setEnabled(true);
                holder.mVideoView.setMediaController(mediaController);
                holder.mVideoView.setVideoURI(Uri.parse(image.getLocalPath()));
                holder.mVideoView.requestFocus();
                holder.mVideoView.setOnPreparedListener(mp -> holder.mVideoView.start());

            });
        }

        holder.buttonViewOption.setOnClickListener(view -> {
            //creating a popup menu
            PopupMenu popup = new PopupMenu(mContext, holder.buttonViewOption);
            //inflating menu from xml resource
            if (image.getMessageType() == 10) {
                popup.inflate(R.menu.groupinformation_media_menu);
            } else {
                popup.inflate(R.menu.group_information_file_video_menu);
            }

            //adding click listener
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.forward:
                        Intent mIntent = new Intent(mContext, ForwardActivity.class);
                        //int position = 0;

                        Message forwardMessage = new Message();
                        Media media = images.get(position);
                        forwardMessage.setFileName(media.getMessage().getFileName());
                        forwardMessage.setUrl(media.getMessage().getUrl());
//                                if(media.getMessageType() != 12){
//                                    forwardMessage.setFilePath(CommonData.getFileLocalPath(media.getMuid()));
//                                }
//                                if(media.getMessageType() == 12){
                        forwardMessage.setFilePath(CommonData.getCachedFilePath(media.getMessage().getUrl(), media.getMuid()));
//                                }
                        Log.i("QWERTY URL", media.getMessage().getUrl());
                        Log.i("QWERTY THUMBNAILURL", media.getMessage().getThumbnailUrl());
                        Log.i("QWERTY FILE PATH", CommonData.getFileLocalPath(media.getMuid()));
                        forwardMessage.setImage_url(media.getMessage().getImageUrl());
                        forwardMessage.setMessageType(media.getMessageType());
                        forwardMessage.setThumbnailUrl(media.getMessage().getThumbnailUrl());
                        if (media.getMessageType() == FuguAppConstant.IMAGE_MESSAGE) {
                            forwardMessage.setSharableThumbnailUrl(media.getMessage().getThumbnailUrl());
                        } else if (media.getMessageType() == FuguAppConstant.FILE_MESSAGE) {
                            forwardMessage.setSharableThumbnailUrl(media.getMessage().getUrl());
                        } else {
                            //forwardMessage.setSharableThumbnailUrl(media.getMessage().getUrl());
                            forwardMessage.setSharableThumbnailUrl(media.getMessage().getUrl());
                        }

                        if (media.getMessageType() == FuguAppConstant.VIDEO_MESSAGE) {
                            forwardMessage.setSharableImage_url(media.getMessage().getUrl());
                            forwardMessage.setSharableImage_url_100x100(media.getMessage().getUrl());
                        } else {
                            forwardMessage.setSharableImage_url(media.getMessage().getImageUrl());
                            forwardMessage.setSharableImage_url_100x100(media.getMessage().getImageUrl100x100());
                        }

                        forwardMessage.setImageWidth(media.getMessage().getImageWidth());
                        forwardMessage.setImageHeight(media.getMessage().getImageHeight());
                        forwardMessage.setMuid(media.getMuid());
                        forwardMessage.setFileSize(media.getMessage().getFileSize());
                        //forwardMessage.setFilePath(media.getThumbnailUrl());
                        mIntent.putExtra("MESSAGE", forwardMessage);
//                                mIntent.putExtra("BUSINESS_NAME", intent.getStringExtra("channelName"));
//                                mIntent.putExtra("chatType", intent.getStringExtra("chatType"));
//                                mIntent.putExtra("chatType", media.getMessageType());
                        mContext.startActivity(mIntent);
                        break;
                    case R.id.viewInGallery:
                        //handle menu4 click
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);

                        String imageViewInGalleryPath = getImageLocalPath(position);
                        intent.setDataAndType(FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider",
                                new File(imageViewInGalleryPath)),
                                "image/*");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        mContext.startActivity(intent);
                        break;
                    case R.id.share:
                        //handle menu5 click
                        Uri path;
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        if (images.get(position).getMessageType() == FuguAppConstant.IMAGE_MESSAGE) {

                            String link = getImageLocalPath(position);

                            path = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", new File(link));
                        } else {
                            path = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", new File(CommonData.getCachedFilePath(images.get(position).getMessage().getUrl(), images.get(position).getMuid())));
                        }
                        try {
                            InputStream stream = mContext.getContentResolver().openInputStream(path);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        //ExifInterface exeif = new ExifInterface(CommonData.getCachedFilePath(images.get(position).getMessage().getImageUrl(),images.get(position).getMuid()));
                        if (images.get(position).getMessageType() == FuguAppConstant.IMAGE_MESSAGE) {
                            sharingIntent.setType("image/*");
                        } else if (images.get(position).getMessageType() == FuguAppConstant.VIDEO_MESSAGE) {
                            sharingIntent.setType("video/*");
                        } else {
                            try {
                                String fileUrl = images.get(position).getMessage().getUrl();
                                if (fileUrl.split("\\.").length > 0) {
                                    String fileExtension = fileUrl.split("\\.")[fileUrl.split("\\.").length - 1];
                                    sharingIntent.setType(MimeTypes.lookupMimeType(fileExtension));
                                } else {
                                    sharingIntent.setType("/*");
                                }
                            } catch (Exception e) {
                                sharingIntent.setType("/*");
                                e.printStackTrace();
                            }
                            //sharingIntent.setType( MimeTypes.lookupMimeType("pdf"));
                        }
                        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        sharingIntent.putExtra(Intent.EXTRA_STREAM, path);
                        mContext.startActivity(Intent.createChooser(sharingIntent, "Share Via"));
                        break;
                }
                return false;
            });
            //displaying the popup
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    private String getImageLocalPath(int position) {
        String normalPath = Environment.getExternalStorageDirectory() + File.separator + FuguAppConstant.APP_NAME_SHORT +
                File.separator + CommonData.getWorkspaceResponse(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey()).getWorkspaceName().replaceAll(" ", "").replaceAll("'s", "") + File.separator + FuguAppConstant.IMAGE;
        String privatePath = Environment.getExternalStorageDirectory() + File.separator + FuguAppConstant.APP_NAME_SHORT +
                File.separator + CommonData.getWorkspaceResponse(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey()).getWorkspaceName().replaceAll(" ", "").replaceAll("'s", "") + File.separator + FuguAppConstant.PRIVATE_IMAGES;
        File jpgFile = new File(normalPath + File.separator + images.get(position).getMessage().getFileName() + "_" + images.get(position).getMuid() + ".jpg");
        File jpgPrivateFile = new File(privatePath + File.separator + images.get(position).getMessage().getFileName() + "_" + images.get(position).getMuid() + ".jpg");
        File jpegFile = new File(normalPath + File.separator + images.get(position).getMessage().getFileName() + "_" + images.get(position).getMuid() + ".jpeg");
        File jpegPrivateFile = new File(privatePath + File.separator + images.get(position).getMessage().getFileName() + "_" + images.get(position).getMuid() + ".jpeg");

        String link = "";
        if (jpgFile.exists()) {
            link = jpgFile.getAbsolutePath();
        } else if (jpgPrivateFile.exists()) {
            link = jpgPrivateFile.getAbsolutePath();
        } else if (jpegFile.exists()) {
            link = jpegFile.getAbsolutePath();
        } else if (jpegPrivateFile.exists()) {
            link = jpegPrivateFile.getAbsolutePath();
        }
        return link;
        //path = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", new File(link));
    }

    public static class FileOpen {

        static void openFile(Context context, File url) throws IOException {
            try {
                Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", url);
                Intent openFileIntent = new Intent(Intent.ACTION_VIEW);
//                String extension = checkUrl(url);
                String filePath = url.getAbsolutePath();
                String extension = filePath.substring(filePath.lastIndexOf(".") + 1);
                String mimeType = FuguMimeUtils.guessMimeTypeFromExtension(extension.toLowerCase());
                if (mimeType == null)
                    mimeType = "*/*";
                openFileIntent.setDataAndType(uri, mimeType);
                openFileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                openFileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                openFileIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                try {
                    context.startActivity(openFileIntent);
                } catch (ActivityNotFoundException e) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setMessage("You may not have a proper app for viewing this content.");
                    alert.setPositiveButton("Ok", null);
                    alert.setCancelable(false);
                    alert.show();
                }
            } catch (Exception e) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setMessage("You may not have a proper app for viewing this content.");
                alert.setPositiveButton("Ok", null);
                alert.setCancelable(false);
                alert.show();
            }
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private PhotoView imageViewPreview;
        private ImageView ivPlay;
        private LinearLayout llFile, llOpen;
        private TextView tvFile;
        private AppCompatTextView tvFileExt;
        private ImageView ivFile;
        private TextView tvFileName;
        private VideoView mVideoView;
        private TextView buttonViewOption;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageViewPreview = itemView.findViewById(R.id.image_preview);
            ivPlay = itemView.findViewById(R.id.ivPlay);
            llFile = itemView.findViewById(R.id.llFile);
            llOpen = itemView.findViewById(R.id.llOpen);
            tvFile = itemView.findViewById(R.id.tvFile);
            tvFileExt = itemView.findViewById(R.id.tvFileExt);
            ivFile = itemView.findViewById(R.id.ivFile);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            mVideoView = itemView.findViewById(R.id.videoView);
            buttonViewOption = itemView.findViewById(R.id.textViewOptions);
        }
    }

}
