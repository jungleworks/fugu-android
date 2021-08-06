package com.skeleton.mvp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.ShowMoreActivity;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.fugudatabase.CommonData;
import com.skeleton.mvp.model.Media;
import com.skeleton.mvp.util.Log;
import com.skeleton.mvp.utils.FuguMimeUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.skeleton.mvp.constant.FuguAppConstant.IMAGE_MAP;


/**
 * Created by Lincoln on 31/03/16.
 */

public class BigGalleryAdapter extends RecyclerView.Adapter<BigGalleryAdapter.MyViewHolder> {

    private List<Media> images;
    private Context mContext;
    private String url;
    private String fileName;
    private String dirPath;

    public BigGalleryAdapter(Context context, List<Media> images) {
        mContext = context;
        this.images = images;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Media image = images.get(position);
        if (image.getMessageType() == 10) {
            holder.thumbnail.setVisibility(View.VISIBLE);
            holder.llFile.setVisibility(View.GONE);
            String dirPath = Environment.getExternalStorageDirectory() + File.separator + FuguAppConstant.APP_NAME_SHORT +
                    File.separator + CommonData.getWorkspaceResponse(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey()).getWorkspaceName().replaceAll(" ", "").replaceAll("'s", "") + File.separator + FuguAppConstant.IMAGE;

            String dirPathPrivate = Environment.getExternalStorageDirectory() + File.separator + FuguAppConstant.APP_NAME_SHORT +
                    File.separator + CommonData.getWorkspaceResponse(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey()).getWorkspaceName().replaceAll(" ", "").replaceAll("'s", "") + File.separator + FuguAppConstant.PRIVATE_IMAGES;

            File jpgFile = new File(dirPath + File.separator + images.get(position).getMessage().getFileName() + "_" + images.get(position).getMuid() + ".jpg");
            File jpgPrivateFile = new File(dirPathPrivate + File.separator + images.get(position).getMessage().getFileName() + "_" + images.get(position).getMuid() + ".jpg");
            File jpegFile = new File(dirPath + File.separator + images.get(position).getMessage().getFileName() + "_" + images.get(position).getMuid() + ".jpeg");
            File jpegPrivateFile = new File(dirPathPrivate + File.separator + images.get(position).getMessage().getFileName() + "_" + images.get(position).getMuid() + ".jpeg");

            if (jpgFile.exists() || jpgPrivateFile.exists() ||
                    jpegFile.exists() || jpegPrivateFile.exists() || images.get(position).getMessage().getImageUrl().split("\\.")[images.get(position).getMessage().getImageUrl().split("\\.").length - 1].equals("gif")) {
                holder.llDownload.setVisibility(View.GONE);
            } else {
                holder.llDownload.setVisibility(View.VISIBLE);
                holder.tvSize.setText(images.get(position).getMessage().getFileSize());
                holder.llDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadFileFromUrl(position, holder.llDownload, holder.pbDownloading, null);
                    }
                });
            }
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

        } else if (image.getMessageType() == 11) {
            if (TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getCachedFilePath(images.get(position).getMessage().getUrl(), images.get(position).getMuid()))) {

                holder.llFileDownload.setVisibility(View.VISIBLE);
                holder.llOpen.setVisibility(View.GONE);
                holder.tvFileSizeOpen.setText(images.get(position).getMessage().getFileSize());
                String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download";
            } else {
                holder.llFileDownload.setVisibility(View.GONE);
                holder.llOpen.setVisibility(View.VISIBLE);
            }
            holder.llDownload.setVisibility(View.GONE);
            String[] extensions = image.getFileurl().split(Pattern.quote("."));
            holder.tvFile.setText(extensions[extensions.length - 1].toUpperCase());
            holder.tvFileExt.setText(extensions[extensions.length - 1].toUpperCase());
            Integer imageSource = IMAGE_MAP.get(extensions[extensions.length - 1].toLowerCase());
            if (imageSource == null) {
                String mimeType = FuguMimeUtils.guessMimeTypeFromExtension(extensions[extensions.length - 1].toLowerCase());
                if (mimeType != null)
                    imageSource = IMAGE_MAP.get(mimeType.split("/")[0]);
            }
            if (imageSource != null) {
                holder.ivFile.setImageResource(imageSource);
                holder.tvFileExt.setVisibility(View.GONE);
            } else {
                holder.ivFile.setImageResource(R.drawable.file_model);
                holder.tvFileExt.setVisibility(View.VISIBLE);
            }
            holder.llFile.setVisibility(View.VISIBLE);
            holder.thumbnail.setVisibility(View.GONE);
            holder.llFileDownload.setOnClickListener(v -> downloadFileFromUrl(position, holder.llFileDownload, holder.pbFileDownloading, holder.llOpen));
            holder.llOpen.setOnClickListener(v -> {
                try {
                    FileOpen.openFile(mContext, new File(image.getLocalPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
//            holder.ivPlay.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getCachedFilePath(images.get(position).getMessage().getUrl(), images.get(position).getMuid()))) {
                holder.llDownload.setVisibility(View.VISIBLE);
                holder.tvSize.setText(images.get(position).getMessage().getFileSize());
                String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download";
                //downloadIdVideo = downloadFileFromUrl(fullPath, position, "Video");
                holder.llDownload.setOnClickListener(v -> downloadFileFromUrl(position, holder.llDownload, holder.pbDownloading, null));
            } else {
                holder.llDownload.setVisibility(View.GONE);
            }

            holder.thumbnail.setVisibility(View.VISIBLE);
            holder.llFile.setVisibility(View.GONE);

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .fitCenter()
                    .priority(Priority.HIGH);

            Glide.with(mContext)
                    .asBitmap()
                    .apply(options)
                    .load(image.getThumbnailUrl())
                    .into(holder.thumbnail);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_big, parent, false);
        return new MyViewHolder(itemView);
    }

    public void updateList(ArrayList<Media> itemList) {
        this.images.addAll(itemList);
        notifyDataSetChanged();
    }

    public int downloadFileFromUrl(int position, LinearLayout llDownload, ProgressBar pbDownloading, LinearLayout llFileOpen) {

        if (images.get(position).getMessageType() == FuguAppConstant.VIDEO_MESSAGE) {
            url = images.get(position).getMessage().getUrl();
            dirPath = Environment.getExternalStorageDirectory() + File.separator + FuguAppConstant.APP_NAME_SHORT +
                    File.separator + CommonData.getWorkspaceResponse(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey()).getWorkspaceName().replaceAll(" ", "").replaceAll("'s", "") + File.separator + FuguAppConstant.VIDEO;
            fileName = images.get(position).getMessage().getFileName();
        } else if (images.get(position).getMessageType() == FuguAppConstant.IMAGE_MESSAGE) {
            url = images.get(position).getMessage().getImageUrl();
            dirPath = Environment.getExternalStorageDirectory() + File.separator + FuguAppConstant.APP_NAME_SHORT +
                    File.separator + CommonData.getWorkspaceResponse(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey()).getWorkspaceName().replaceAll(" ", "").replaceAll("'s", "") + File.separator + FuguAppConstant.IMAGE;
            if (com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getMediaVisibility() == 0) {
                dirPath = Environment.getExternalStorageDirectory() + File.separator + FuguAppConstant.APP_NAME_SHORT +
                        File.separator + CommonData.getWorkspaceResponse(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey()).getWorkspaceName().replaceAll(" ", "").replaceAll("'s", "") + File.separator + FuguAppConstant.PRIVATE_IMAGES;
            }
            fileName = images.get(position).getMessage().getFileName() + "_" + images.get(position).getMuid() + "." + "jpg";
        } else if (images.get(position).getMessageType() == FuguAppConstant.FILE_MESSAGE) {
            url = images.get(position).getMessage().getUrl();
            dirPath = Environment.getExternalStorageDirectory() + File.separator + FuguAppConstant.APP_NAME_SHORT +
                    File.separator + CommonData.getWorkspaceResponse(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey()).getWorkspaceName().replaceAll(" ", "").replaceAll("'s", "") + File.separator + FuguAppConstant.DOCUMENT;
            fileName = images.get(position).getMessage().getFileName();
        }


        int downloadId = PRDownloader.download(url, dirPath, fileName)
                .build()
                .setOnStartOrResumeListener(() -> {
                    llDownload.setVisibility(View.GONE);
                    pbDownloading.setVisibility(View.VISIBLE);
                })
                .setOnPauseListener(() -> pbDownloading.setVisibility(View.GONE))
                .setOnCancelListener(() -> pbDownloading.setVisibility(View.GONE))
                .setOnProgressListener(progress -> {
                    Log.i("progresscurrentbyte", progress.currentBytes + "");
                    Log.i("progresstotalbytes", progress.totalBytes + "");
                    Log.i("progress", ((progress.currentBytes * 100) / progress.totalBytes) + "");
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        llDownload.setVisibility(View.GONE);
                        pbDownloading.setVisibility(View.GONE);
                        if (images.get(position).getMessageType() == FuguAppConstant.FILE_MESSAGE && llFileOpen != null) {
                            llFileOpen.setVisibility(View.VISIBLE);
                        }

                        if (images.get(position).getMessageType() == FuguAppConstant.VIDEO_MESSAGE || images.get(position).getMessageType() == FuguAppConstant.FILE_MESSAGE) {
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
                            images.get(position).setLocalPath(dirPath + "/" + fileName);
                            com.skeleton.mvp.fugudatabase.CommonData.setCachedFilePath(images.get(position).getMessage().getUrl(), images.get(position).getMuid(), dirPath + "/" + fileName);
                            com.skeleton.mvp.fugudatabase.CommonData.setFileLocalPath(images.get(position).getMuid(), dirPath + "/" + fileName);
                            com.skeleton.mvp.fugudatabase.CommonData.setFilesMap(images.get(position).getMuid(), images.get(position).getMessage().getUrl(), dirPath + "/" + fileName);
//                                }
//                            }) ;

                        } else if (images.get(position).getMessageType() == FuguAppConstant.IMAGE_MESSAGE) {
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
                            try {
                                Intent exifIntent = ((ShowMoreActivity) mContext).getIntent();
                                ExifInterface exif = new ExifInterface(link);
                                if (images.get(position).getIsThreadMessage() == 1) {
                                    exif.setAttribute(ExifInterface.TAG_MODEL, images.get(position).getThreadMessageId().toString());
                                    exif.setAttribute(ExifInterface.TAG_MAKE, exifIntent.getLongExtra("channel_id", 0) + images.get(position).getMuid());
                                } else {
                                    exif.setAttribute(ExifInterface.TAG_MODEL, images.get(position).getMessageId().toString());
                                    exif.setAttribute(ExifInterface.TAG_MAKE, String.valueOf(exifIntent.getLongExtra("channel_id", 0)));
                                }

                                Log.i("exif", String.valueOf(exifIntent.getLongExtra("channel_id", 0)));
                                exif.setAttribute(ExifInterface.TAG_DATETIME, images.get(position).getCreatedAt());

                                exif.saveAttributes();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    images.get(position).setLocalPath(dirPath + "/" + fileName);
                                    com.skeleton.mvp.fugudatabase.CommonData.setCachedFilePath(images.get(position).getMessage().getImageUrl(), images.get(position).getMuid(), dirPath + "/" + fileName);
                                    com.skeleton.mvp.fugudatabase.CommonData.setFileLocalPath(images.get(position).getMuid(), dirPath + "/" + fileName);
                                    com.skeleton.mvp.fugudatabase.CommonData.setFilesMap(images.get(position).getMuid(), images.get(position).getMessage().getImageUrl(), dirPath + "/" + fileName);
                                }
                            });

                        }
                        Log.i("Download Completed", "True");
                    }

                    @Override
                    public void onError(Error error) {

                    }

                });
        return downloadId;
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
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

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            //if(!TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getCachedFilePath(images.get(rv.getChildPosition(child)).getMessage().getUrl(),images.get(rv.getChildPosition(child)).getMuid()))){
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            //}

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public static class FileOpen {
        public static void openFile(Context context, File file) throws IOException {
            try {
                Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                Intent fileOpenIntent = new Intent(Intent.ACTION_VIEW);
                String filePath = file.getAbsolutePath();
                String extension = filePath.substring(filePath.lastIndexOf(".") + 1);
                String mimeType = FuguMimeUtils.guessMimeTypeFromExtension(extension.toLowerCase());
                if (mimeType == null)
                    mimeType = "*/*";
                fileOpenIntent.setDataAndType(uri, mimeType);
                fileOpenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                fileOpenIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                fileOpenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                context.startActivity(fileOpenIntent);
            } catch (Exception e) {
                new AlertDialog.Builder(context)
                        .setMessage("You may not have a proper app for viewing this content.")
                        .setPositiveButton("Ok", null)
                        .setCancelable(false)
                        .show();
            }
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        LinearLayout llFile, llOpen;
        TextView tvFile;
        AppCompatTextView tvFileExt;
        ImageView ivFile;
        ImageView ivPlay;
        //ImageView ivDownload;
        LinearLayout llDownload;
        TextView tvSize;
        ProgressBar pbDownloading;

        TextView tvFileSizeOpen;
        LinearLayout llFileDownload;
        ProgressBar pbFileDownloading;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.thumbnail);
            ivFile = view.findViewById(R.id.ivFile);
            //ivPlay = view.findViewById(R.id.ivPlay);
            llFile = view.findViewById(R.id.llFile);
            tvFile = view.findViewById(R.id.tvFile);
            tvFileExt = view.findViewById(R.id.tvFileExt);
            llOpen = view.findViewById(R.id.llOpen);
            //ivDownload = view.findViewById(R.id.ivDownload);
            llDownload = view.findViewById(R.id.llDownload);
            tvSize = view.findViewById(R.id.tvSize);
            pbDownloading = view.findViewById(R.id.pbDownloading);
            tvFileSizeOpen = view.findViewById(R.id.tvFileSizeOpen);
            llFileDownload = view.findViewById(R.id.llFileDownload);
            pbFileDownloading = view.findViewById(R.id.pbFileDownloading);
        }
    }
}