package com.skeleton.mvp.ui.selectdomain;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skeleton.mvp.R;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.Domain;
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse;
import com.skeleton.mvp.ui.base.BaseActivity;

import java.util.ArrayList;

import static com.skeleton.mvp.ui.AppConstants.EXTRA_ALREADY_MEMBER;

public class SelectDomainActivity extends BaseActivity {
    private ArrayList<Domain> domainsList = new ArrayList<>();
    private FcCommonResponse fcCommonResponse;
    private SelectDomainAdapter selectDomainAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_domain);
        initViews();
    }

    /**
     * Initialize Views
     */
    private void initViews() {
        RecyclerView rvDomainList = findViewById(R.id.rvDomainList);
        if (CommonData.getCommonResponse() != null) {
            fcCommonResponse = CommonData.getCommonResponse();
            if (getIntent().hasExtra(EXTRA_ALREADY_MEMBER)) {
                domainsList.add(new Domain("Add Workspace", "Add Workspace"));
            }
            for (int i = 0; i < fcCommonResponse.getData().getWorkspacesInfo().size(); i++) {
                domainsList.add(new Domain(fcCommonResponse.getData().getWorkspacesInfo().get(i).getWorkspace(), fcCommonResponse.getData().getWorkspacesInfo().get(i).getWorkspaceName()));
            }
            if (getIntent().hasExtra(EXTRA_ALREADY_MEMBER)) {
                selectDomainAdapter = new SelectDomainAdapter(domainsList, this, EXTRA_ALREADY_MEMBER);
            } else {
                selectDomainAdapter = new SelectDomainAdapter(domainsList, this, "no");
            }
            rvDomainList.setLayoutManager(new LinearLayoutManager(this));
            rvDomainList.setAdapter(selectDomainAdapter);
        }
    }
}
