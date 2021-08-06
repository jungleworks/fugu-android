package com.easyfilepicker.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.easyfilepicker.Constant;
import com.easyfilepicker.DividerGridItemDecoration;
import com.easyfilepicker.R;
import com.easyfilepicker.adapter.ImageDirectoryAdapter;
import com.easyfilepicker.adapter.OnSelectStateListener;
import com.easyfilepicker.adapter.VideoPickAdapter;
import com.easyfilepicker.filter.FileFilter;
import com.easyfilepicker.filter.callback.FilterResultCallback;
import com.easyfilepicker.filter.entity.Directory;
import com.easyfilepicker.filter.entity.VideoFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class VideoPickActivity extends BaseActivity {
    public static final String THUMBNAIL_PATH = "FilePick";
    public static final String IS_NEED_CAMERA = "IsNeedCamera";
    public static final String IS_TAKEN_AUTO_SELECTED = "IsTakenAutoSelected";

    private final int COLUMN_NUMBER = 2;
    private RecyclerView mRecyclerView;
    private VideoPickAdapter mAdapter;
    private ImageDirectoryAdapter directoryAdapter;
    private boolean isNeedCamera;
    private boolean isTakenAutoSelected;
    private ArrayList<VideoFile> mSelectedList = new ArrayList<>();
    private ProgressBar mProgressBar;

    private GridLayoutManager layoutManager;
    private boolean isDirectory;
    private Toolbar myToolbar;
    private TextView textView;

    @Override
    public void permissionGranted() {
        loadData();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vw_activity_image_pick);

        isNeedCamera = false;//getIntent().getBooleanExtra(IS_NEED_CAMERA, false);
        isTakenAutoSelected = false;//getIntent().getBooleanExtra(IS_TAKEN_AUTO_SELECTED, true);
        initView();
    }

    private void initView() {
        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        textView = myToolbar.findViewById(R.id.tv_toolbar_name);
        setToolbar(textView, getString(R.string.video_picker));

        mRecyclerView = findViewById(R.id.rv_image_pick);
        mProgressBar = findViewById(R.id.pb_file_pick);

        layoutManager = new GridLayoutManager(this, COLUMN_NUMBER);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(this));

        mAdapter = new VideoPickAdapter(this, isNeedCamera);
        //mRecyclerView.setAdapter(mAdapter);

        directoryAdapter = new ImageDirectoryAdapter(this);
        mRecyclerView.setAdapter(directoryAdapter);

        directoryAdapter.setOnSelectStateListener(new OnSelectStateListener<Directory>() {
            @Override
            public void OnSelectStateChanged(boolean state, Directory file) {
                isDirectory = true;
                textView.setText(file.getName());
                layoutManager.setSpanCount(3);
                mRecyclerView.setLayoutManager(layoutManager);
                List<VideoFile> list = new ArrayList<>();
                list.addAll(file.getFiles());
                mAdapter.refresh(list);
                mRecyclerView.setAdapter(mAdapter);
                runLayoutAnimation(mRecyclerView);
            }
        });

        mAdapter.setOnSelectStateListener(new OnSelectStateListener<VideoFile>() {
            @Override
            public void OnSelectStateChanged(boolean state, VideoFile file) {
                mSelectedList.add(file);

                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(Constant.RESULT_PICK_VIDEO, mSelectedList);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


        File folder = new File(getExternalCacheDir().getAbsolutePath() + File.separator + THUMBNAIL_PATH);
        if (!folder.exists()) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }

    }

    @Override
    public void onBackPressed() {
        if(isDirectory) {
            isDirectory = false;
            textView.setText(getString(R.string.video_picker));
            layoutManager.setSpanCount(COLUMN_NUMBER);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(directoryAdapter);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constant.REQUEST_CODE_TAKE_VIDEO:
                if (resultCode == RESULT_OK) {
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    File file = new File(mAdapter.mVideoPath);
                    Uri contentUri = Uri.fromFile(file);
                    mediaScanIntent.setData(contentUri);
                    sendBroadcast(mediaScanIntent);

                    loadData();
                }
                break;
        }
    }

    private void loadData() {
        FileFilter.getVideos(this, new FilterResultCallback<VideoFile>() {
            @Override
            public void onResult(List<Directory<VideoFile>> directories) {
                mProgressBar.setVisibility(View.GONE);
                // Refresh folder list
                if (isNeedFolderList) {
                    ArrayList<Directory> list = new ArrayList<>();
                    Directory all = new Directory();
                    all.setName(getResources().getString(R.string.vw_all));
                    list.add(all);
                    list.addAll(directories);
                    mFolderHelper.fillData(list);
                }
                refreshData(directories);
            }
        });
    }

    private void refreshData(List<Directory<VideoFile>> directories) {
        if(directories.size()>4) {
            List<Directory> directoryList = new ArrayList<>();
            directoryList.addAll(directories);
            directoryAdapter.refresh(directoryList);
        } else {
            boolean tryToFindTaken = isTakenAutoSelected;
            List<VideoFile> list = new ArrayList<>();
            for (Directory<VideoFile> directory : directories) {
                list.addAll(directory.getFiles());

                // auto-select taken file?
                if (tryToFindTaken) {
                    tryToFindTaken = findAndAddTaken(directory.getFiles());   // if taken file was found, we're done
                }
            }

            for (VideoFile file : mSelectedList) {
                int index = list.indexOf(file);
                if (index != -1) {
                    list.get(index).setSelected(true);
                }
            }
            mAdapter.refresh(list);
            mRecyclerView.setAdapter(mAdapter);

        }
        runLayoutAnimation(mRecyclerView);
    }

    private boolean findAndAddTaken(List<VideoFile> list) {
        for (VideoFile videoFile : list) {
            if (videoFile.getPath().equals(mAdapter.mVideoPath)) {
                mSelectedList.add(videoFile);
                return true;   // taken file was found and added
            }
        }
        return false;    // taken file wasn't found
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.hippo_layout_animation_from_bottom);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    /**
     * Set toolbar data
     *
     * @param title   title to be displayed
     * @return action bar
     */
    public ActionBar setToolbar(TextView textView, String title) {

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.picker_primary_color)));
            ab.setHomeAsUpIndicator(R.drawable.picker_ic_arrow_back);

            ab.setTitle("");

            textView.setText(title);
            textView.setTextColor(getResources().getColor(R.color.tb_text_color));
        }
        return getSupportActionBar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            // close this context and return to preview context (if there is any)
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
