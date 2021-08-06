package com.easyfilepicker.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.easyfilepicker.Constant;
import com.easyfilepicker.DividerListItemDecoration;
import com.easyfilepicker.R;
import com.easyfilepicker.adapter.AudioPickAdapter;
import com.easyfilepicker.adapter.OnSelectStateListener;
import com.easyfilepicker.filter.FileFilter;
import com.easyfilepicker.filter.callback.FilterResultCallback;
import com.easyfilepicker.filter.entity.AudioFile;
import com.easyfilepicker.filter.entity.Directory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AudioPickActivity extends BaseActivity {
    public static final String IS_NEED_RECORDER = "IsNeedRecorder";
    public static final String IS_TAKEN_AUTO_SELECTED = "IsTakenAutoSelected";
    public static final String IS_MEDIA_PERVIEW_REQUIRED = "is_media_preview_required";

    private RecyclerView mRecyclerView;
    private AudioPickAdapter mAdapter;
    private ArrayList<AudioFile> mSelectedList = new ArrayList<>();

    private Toolbar myToolbar;
    private TextView textView;
    private MediaPlayer mediaPlayer;

    private boolean isMediaPreview;

    @Override
    public void permissionGranted() {
        loadData();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vw_activity_image_pick);


        initView();
    }

    private void initView() {
        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        textView = myToolbar.findViewById(R.id.tv_toolbar_name);
        TextView tvDone = myToolbar.findViewById(R.id.tvDone);
        tvDone.setVisibility(View.GONE);
        setToolbar(textView, getString(R.string.file_picker));

        isMediaPreview = getIntent().getBooleanExtra(IS_MEDIA_PERVIEW_REQUIRED, false);

        mRecyclerView = findViewById(R.id.rv_image_pick);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerListItemDecoration(this,
                LinearLayoutManager.VERTICAL, R.drawable.vw_divider_rv_file));
        mAdapter = new AudioPickAdapter(this, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnSelectStateListener(new OnSelectStateListener<AudioFile>() {
            @Override
            public void OnSelectStateChanged(boolean state, AudioFile file) {

                if (isMediaPreview) {
                    if (!state) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                        }
                        return;
                    }

                    Uri myUri = Uri.parse(file.getPath());
                    mediaPlayer = getMediaPlayer();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }

                    try {
                        mediaPlayer.setDataSource(AudioPickActivity.this, myUri);
                        mediaPlayer.prepareAsync();
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mediaPlayer = mp;
                                mediaPlayer.start();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mediaPlayer.release();
                        }
                    });
                }

                mSelectedList.clear();
                mSelectedList.add(file);

            }
        });
    }

    private void loadData() {
        FileFilter.getAudios(this, new FilterResultCallback<AudioFile>() {
            @Override
            public void onResult(List<Directory<AudioFile>> directories) {
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

    private void refreshData(List<Directory<AudioFile>> directories) {

        List<AudioFile> list = new ArrayList<>();
        for (Directory<AudioFile> directory : directories) {
            list.addAll(directory.getFiles());
        }

        for (AudioFile file : mSelectedList) {
            int index = list.indexOf(file);
            if (index != -1) {
                list.get(index).setSelected(true);
            }
        }
        mAdapter.refresh(list);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isMediaPreview)
            stopMedia();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.hippo_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int i = item.getItemId();
        if (i == R.id.menu_done) {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(Constant.RESULT_PICK_AUDIO, mSelectedList);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        } else {
            onBackPressed();
            return true;
        }
    }

    private MediaPlayer getMediaPlayer() {
        if (mediaPlayer == null) {
            synchronized (AudioPickActivity.this) {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }
            }
        }
        return mediaPlayer;
    }

    private void stopMedia() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {

        }
    }

    /**
     * Set toolbar data
     *
     * @param title title to be displayed
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
}
