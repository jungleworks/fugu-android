package com.skeleton.mvp.fragment;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.model.Media;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class SlideshowDialogFragment extends DialogFragment {
    private String TAG = SlideshowDialogFragment.class.getSimpleName();
    private ArrayList<Media> images;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private int selectedPosition = 0;
    private ImageView ivPlay;
    private static final Map<String, String> mimeTypeSet = new HashMap<String, String>() {{
        put(".doc", "application/msword");
        put(".ppt", "application/vnd.ms-powerpoint");
        put(".pdf", "application/pdf");
        put(".xls", "application/vnd.ms-excel");
        put(".zip", "application/x-wav");
        put(".rtf", "application/rtf");
        put(".wav", "audio/x-wav");
        put(".jpg", "image/jpeg");
        put(".csv", "text/csv");
        put(".apk", "application/vnd.android.package-archive");
        put(".3gp", "video/*");
        put("default", "*/*");
    }};
    ;

    public static SlideshowDialogFragment newInstance() {
        SlideshowDialogFragment f = new SlideshowDialogFragment();
        return f;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_slider, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        images = (ArrayList<Media>) getArguments().getSerializable("images");
        selectedPosition = getArguments().getInt("position");

        Log.e(TAG, "position: " + selectedPosition);
        Log.e(TAG, "images size: " + images.size());

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        setCurrentItem(selectedPosition);

        return v;
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
    }

    //	page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    //	adapter
    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);

            final PhotoView imageViewPreview = view.findViewById(R.id.image_preview);
            final ImageView ivPlay = view.findViewById(R.id.ivPlay);
            LinearLayout llClose = view.findViewById(R.id.llClose);
            LinearLayout llFile = view.findViewById(R.id.llFile);
            LinearLayout llOpen = view.findViewById(R.id.llOpen);
            TextView tvFile = view.findViewById(R.id.tvFile);
            ImageView ivFile = view.findViewById(R.id.ivFile);
            TextView tvFileName = view.findViewById(R.id.tvFileName);
            final VideoView mVideoView = view.findViewById(R.id.videoView);
            llClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            final Media image = images.get(position);
            if (image.getMessageType() == 10) {
                imageViewPreview.setVisibility(View.VISIBLE);
                mVideoView.setVisibility(View.GONE);
                llFile.setVisibility(View.GONE);
                ivPlay.setVisibility(View.GONE);

                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .fitCenter()
                        .priority(Priority.HIGH)
                        .transforms(new CenterCrop(), new RoundedCorners(100));

                Glide.with(getActivity())
                        .asBitmap()
                        .apply(options)
                        .load(image.getThumbnailUrl())
                        .into(imageViewPreview);

//                Glide.with(getActivity()).load(image.getImageUrl())
//                        .thumbnail(0.5f)
//                        .crossFade()
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .placeholder(R.drawable.placeholder)
//                        .error(R.drawable.placeholder)
//                        .into(imageViewPreview);


            } else if (image.getMessageType() == 11) {
                String extensions[] = image.getFileurl().split(Pattern.quote("."));
                tvFile.setText(extensions[extensions.length - 1].toUpperCase());
                ivPlay.setVisibility(View.GONE);
                switch (extensions[extensions.length - 1]) {
                    case "txt":
                        ivFile.setImageResource(R.drawable.txt);
                        break;
                    case "pdf":
                        ivFile.setImageResource(R.drawable.pdf);
                        break;
                    case "csv":
                        ivFile.setImageResource(R.drawable.csv);
                        break;
                    case "doc":
                        ivFile.setImageResource(R.drawable.doc);
                    case "docx":
                        ivFile.setImageResource(R.drawable.doc);
                        break;
                    case "ppt":
                    case "pptx":
                        ivFile.setImageResource(R.drawable.ppt);
                        break;
                    case "xls":
                        ivFile.setImageResource(R.drawable.excel);
                    case "xlsx":
                        ivFile.setImageResource(R.drawable.excel);
                        break;
                    case "apk":
                        ivFile.setImageResource(R.drawable.android);
                        break;
                    case "ipa":
                        ivFile.setImageResource(R.drawable.apple);
                        break;
                    case "mp3":
                    case "3gp":
                    case "midi":
                    case "mpeg":
                    case "x-aiff":
                    case "x-wav":
                    case "webm":
                    case "ogg":
                    case "m4a":
                    case "wav":
                        ivFile.setImageResource(R.drawable.music);
                        break;
                    default:
                        ivFile.setImageResource(R.drawable.attachment);
                        break;
                }
                llFile.setVisibility(View.VISIBLE);
                tvFileName.setText(image.getFileName());
                imageViewPreview.setVisibility(View.GONE);
                mVideoView.setVisibility(View.GONE);
                llOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            FileOpen.openFile(getActivity(), new File(image.getLocalPath()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else if (image.getMessageType() == 12) {
                final MediaController mediaController = new MediaController(getActivity());
                ivPlay.setVisibility(View.VISIBLE);
                llFile.setVisibility(View.GONE);
                imageViewPreview.setVisibility(View.VISIBLE);
                mVideoView.setVisibility(View.GONE);

                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .fitCenter()
                        .priority(Priority.HIGH)
                        .transforms(new CenterCrop(), new RoundedCorners(10));

                Glide.with(getActivity())
                        .asBitmap()
                        .apply(options)
                        .load(image.getThumbnailUrl())
                        .into(imageViewPreview);

//                Glide.with(getActivity()).load(image.getThumbnailUrl())
//                        .thumbnail(0.5f)
//                        .crossFade()
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .placeholder(R.drawable.placeholder)
//                        .error(R.drawable.placeholder)
//                        .into(imageViewPreview);
                ivPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ivPlay.setVisibility(View.GONE);
                        imageViewPreview.setVisibility(View.GONE);
                        mVideoView.setVisibility(View.VISIBLE);
                        mediaController.setAnchorView(mVideoView);
                        mediaController.setMediaPlayer(mVideoView);
                        mediaController.setEnabled(true);
                        mVideoView.setMediaController(mediaController);
                        mVideoView.setVideoURI(Uri.parse(image.getLocalPath()));
                        mVideoView.requestFocus();
                        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mVideoView.start();
                            }
                        });
                    }
                });


            }

            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

    public static class FileOpen {

        static void openFile(Context context, File url) throws IOException {
            try {
                Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", url);
                Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                String docType = checkUrl(url);
                pdfOpenintent.setDataAndType(uri, mimeTypeSet.get(docType.toLowerCase()));
                pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pdfOpenintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pdfOpenintent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                try {
                    context.startActivity(pdfOpenintent);
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

    static String checkUrl(File url) {
        if (url.toString().toLowerCase().contains(".doc") || url.toString().toLowerCase().contains(".docx")) {
            return ".doc";
        } else if (url.toString().toLowerCase().contains(".pdf")) {
            return ".pdf";
        } else if (url.toString().toLowerCase().contains(".ppt") || url.toString().toLowerCase().contains(".pptx")) {
            return ".ppt";
        } else if (url.toString().toLowerCase().contains(".xls") || url.toString().toLowerCase().contains(".xlsx")) {
            return ".xls";
        } else if (url.toString().toLowerCase().contains(".zip") || url.toString().toLowerCase().contains(".rar")) {
            return ".zip";
        } else if (url.toString().toLowerCase().contains(".rtf")) {
            return ".rtf";
        } else if (url.toString().toLowerCase().contains(".wav") || url.toString().toLowerCase().contains(".mp3")) {
            return ".wav";
        } else if (url.toString().toLowerCase().contains(".gif")) {
            return ".gif";
        } else if (url.toString().toLowerCase().contains(".jpg") || url.toString().toLowerCase().contains(".jpeg") || url.toString().toLowerCase().contains(".png")) {
            return ".jpg";
        } else if (url.toString().toLowerCase().contains(".csv")) {
            return ".csv";
        } else if (url.toString().toLowerCase().contains(".txt")) {
            return ".txt";
        } else if (url.toString().toLowerCase().contains(".apk")) {
            return ".apk";
        } else if (url.toString().toLowerCase().contains(".3gp") || url.toString().toLowerCase().contains(".mpg") ||
                url.toString().toLowerCase().contains(".mpeg") || url.toString().toLowerCase().contains(".mpe") || url.toString().toLowerCase().contains(".mp4") || url.toString().toLowerCase().contains(".avi")) {
            return ".3gp";
        } else {
            return "default";
        }
    }
}
