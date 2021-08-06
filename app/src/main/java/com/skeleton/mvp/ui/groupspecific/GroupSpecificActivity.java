package com.skeleton.mvp.ui.groupspecific;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import android.view.View;
import android.widget.ImageView;

import com.skeleton.mvp.R;
import com.skeleton.mvp.ui.addchannel.AddChannelActivity;
import com.skeleton.mvp.ui.base.BaseActivity;

public class GroupSpecificActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivAdd;
    private AppCompatButton btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_specific);
        ivAdd = findViewById(R.id.ivAdd);
        btnAdd = findViewById(R.id.btnAdd);
        ivAdd.setOnClickListener(this);
        btnAdd.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivAdd:
                startActivity(new Intent(GroupSpecificActivity.this, AddChannelActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case R.id.btnAdd:
                startActivity(new Intent(GroupSpecificActivity.this, AddChannelActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            default:
                break;
        }
    }
}
