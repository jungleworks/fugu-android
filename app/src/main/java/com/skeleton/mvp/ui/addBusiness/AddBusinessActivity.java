package com.skeleton.mvp.ui.addBusiness;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.skeleton.mvp.R;
import com.skeleton.mvp.ui.base.BaseActivity;
import com.skeleton.mvp.ui.getstarted.GetStartedActivity;
import com.skeleton.mvp.ui.onboarding.signin.SignInActivity;

import java.util.ArrayList;

public class AddBusinessActivity extends BaseActivity {
    private LinearLayout llCreateWorkspace, llAnotherEmail;
    private RecyclerView rvEmails;
    private ArrayList<String> emailList = new ArrayList<>();
    private EmailAdapter emailAdapter;
    private ImageView ivBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_business);
        llCreateWorkspace = findViewById(R.id.llCreateWorkspace);
        llAnotherEmail = findViewById(R.id.llAnotherEmail);
        rvEmails = findViewById(R.id.rvEmails);
        ivBack=findViewById(R.id.ivBack);
        //Todo correct the logic
//        for (int i = 0; i < CommonData.getCommonResponse().getData().getWorkspacesInfo().size(); i++) {
//            if (!emailList.contains(CommonData.getFinalSignInResponse().getData().get(i).getEmail())) {
//                emailList.add(CommonData.getFinalSignInResponse().getData().get(i).getEmail());
//            }
//        }
        emailAdapter = new EmailAdapter(emailList, this);
        rvEmails.setLayoutManager(new LinearLayoutManager(this));
        rvEmails.setAdapter(emailAdapter);
        llCreateWorkspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddBusinessActivity.this, GetStartedActivity.class));
            }
        });
        llAnotherEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddBusinessActivity.this, SignInActivity.class));
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
