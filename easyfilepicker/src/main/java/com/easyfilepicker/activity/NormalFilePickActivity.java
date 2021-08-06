package com.easyfilepicker.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.easyfilepicker.*;
import com.easyfilepicker.adapter.NormalFilePickAdapter;
import com.easyfilepicker.adapter.OnSelectStateListener;
import com.easyfilepicker.filter.FileFilter;
import com.easyfilepicker.filter.callback.FilterResultCallback;
import com.easyfilepicker.filter.entity.Directory;
import com.easyfilepicker.filter.entity.NormalFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.easyfilepicker.Constant.SUFFIX;


public class NormalFilePickActivity extends BaseActivity {

    private static final String TAG = NormalFilePickActivity.class.getSimpleName();
    public static final int DEFAULT_MAX_NUMBER = 9;
    private static final int REQUEST_CODE = 6384;
    private int mMaxNumber;
    private RecyclerView mRecyclerView;
    private NormalFilePickAdapter mAdapter;
    private ArrayList<NormalFile> mSelectedList = new ArrayList<>();
    private ProgressBar mProgressBar;
    private String[] mSuffix;
    private Toolbar myToolbar;
    private TextView textView;

    @Override
    public void permissionGranted() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        }, 2000);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vw_activity_image_pick);

        mMaxNumber = getIntent().getIntExtra(Constant.MAX_NUMBER, DEFAULT_MAX_NUMBER);
        mSuffix = getIntent().getStringArrayExtra(SUFFIX);
        initView();
    }

    private void initView() {
        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        textView = myToolbar.findViewById(R.id.tv_toolbar_name);
        setToolbar(textView, getString(R.string.file_picker));

        mProgressBar = findViewById(R.id.pb_file_pick);
        mProgressBar.setVisibility(View.VISIBLE);

        mRecyclerView = findViewById(R.id.rv_image_pick);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerListItemDecoration(this,
                LinearLayoutManager.VERTICAL, R.drawable.vw_divider_rv_file));
        mAdapter = new NormalFilePickAdapter(this, mMaxNumber);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnSelectStateListener(new OnSelectStateListener<NormalFile>() {
            @Override
            public void OnSelectStateChanged(boolean state, NormalFile file) {
                if (file == null) {
                    openFileManager();
                } else {
                    mSelectedList.add(file);
                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra(Constant.RESULT_PICK_FILE, mSelectedList);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });


    }

    private void loadData() {
        FileFilter.getFiles(this, new FilterResultCallback<NormalFile>() {
            @Override
            public void onResult(List<Directory<NormalFile>> directories) {
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
        }, mSuffix);
    }

    private void refreshData(List<Directory<NormalFile>> directories) {
        mProgressBar.setVisibility(View.GONE);
        List<NormalFile> list = new ArrayList<>();
        for (Directory<NormalFile> directory : directories) {
            list.addAll(directory.getFiles());
        }

        for (NormalFile file : mSelectedList) {
            int index = list.indexOf(file);
            if (index != -1) {
                list.get(index).setSelected(true);
            }
        }

        mAdapter.refresh(list);
    }


    private void openFileManager() {
        // Use the GET_CONTENT intent from the utility class
        Intent target = FileUtils.createGetContentIntent();
        // Create the chooser Intent
        Intent intent = Intent.createChooser(
                target, "");
        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            // The reason for the existence of aFileChooser
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                final Uri uri = data.getData();
                String path = FileUtils.getPath(this, uri);
                if(TextUtils.isEmpty(path))
                    path = FileUtils.getRealPathFromURI(this, uri);
                NormalFile file = new NormalFile();
                String fileName = Util.extractFileNameWithoutSuffix(path);
                file.setPath(path);
                file.setName(fileName);
                file.setSize(new File(path).length());

                mSelectedList.add(file);
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(Constant.RESULT_PICK_FILE, mSelectedList);
                setResult(RESULT_OK, intent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
