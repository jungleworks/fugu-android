package com.skeleton.mvp.ui.addchannel;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skeleton.mvp.R;
import com.skeleton.mvp.data.model.Group;
import com.skeleton.mvp.ui.base.BaseActivity;

import java.util.ArrayList;

public class AddChannelActivity extends BaseActivity {
    private RecyclerView rvGroupList;
    private ArrayList<Group> groupList = new ArrayList<>();
    private GroupAdapter groupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_channel);
        rvGroupList = findViewById(R.id.rvGroupList);
        groupAdapter = new GroupAdapter(groupList, this);
        rvGroupList.setLayoutManager(new LinearLayoutManager(this));
        rvGroupList.setAdapter(groupAdapter);
    }
}
