package com.skeleton.mvp.activity;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;

import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.adapter.BigGalleryAdapter;
import com.skeleton.mvp.adapter.ShowMoreViewPagerAdapter;
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo;
import com.skeleton.mvp.fragment.ShowMoreDocumentFragment;
import com.skeleton.mvp.fragment.ShowMoreImageFragment;
import com.skeleton.mvp.fragment.ShowMoreVideoFragment;
import com.skeleton.mvp.fugudatabase.CommonData;
import com.skeleton.mvp.model.Media;
import com.skeleton.mvp.model.media.MediaResponse;
import com.skeleton.mvp.retrofit.APIError;
import com.skeleton.mvp.retrofit.CommonParams;
import com.skeleton.mvp.retrofit.ResponseResolver;
import com.skeleton.mvp.retrofit.RestClient;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class ShowMoreActivity extends FuguBaseActivity implements View.OnClickListener, ShowMoreImageFragment.ShowMoreListener, ShowMoreVideoFragment.ShowMoreListener, ShowMoreDocumentFragment.ShowMoreListener {
    private ArrayList<Media> mediaList = new ArrayList<>();
    private ArrayList<Media> documentMediaList = new ArrayList<>();
    private ArrayList<Media> videoMediaList = new ArrayList<>();
    private ArrayList<Media> imageMediaList = new ArrayList<>();
    private RecyclerView rvMembers, rvMedia;
    private BigGalleryAdapter mediaAdapter;
    private ImageView ivBack;
    private int start = 0, end = 20;
    private GridLayoutManager layoutManager;
    private boolean showLoading = true;
    private ViewPager viewPager;
    private ShowMoreViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;
    private List<WorkspacesInfo> workspaceInfo;
    private int currentPos = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_more);
        rvMedia = findViewById(R.id.rvMedia);
        layoutManager = new GridLayoutManager(this, 3);
        rvMedia.setLayoutManager(layoutManager);
        mediaAdapter = new BigGalleryAdapter(this, mediaList);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPagerAdapter = new ShowMoreViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        rvMedia.setAdapter(mediaAdapter);
        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(this);
        workspaceInfo = com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo();
        currentPos = com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition();
    }

    @Override
    public void apiGetMedia(ArrayList<Integer> messageTypeList,int start,int end,ResultCallback callback) {
        JSONArray arrJson = new JSONArray(messageTypeList);
        CommonParams commonParams = new CommonParams.Builder()
                .add(CHANNEL_ID, getIntent().getLongExtra("channel_id", -1l))
                .add(EN_USER_ID, com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getEnUserId())
                .add("page_start", start)
                .add("page_end", end)
                .add("get_data_type", "DEFAULT")
                .add("message_type",arrJson)
                .build();
        RestClient.getApiInterface().getGroupInfo(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, commonParams.getMap()).enqueue(new ResponseResolver<MediaResponse>(this, showLoading, false) {
            @Override
            public void success(final MediaResponse mediaResponse) {

                showLoading = false;

                for (int i = 0; i < mediaResponse.getData().getChatMedia().size(); i++) {

                    if(messageTypeList.get(0) == 10){
                        String localPath = com.skeleton.mvp.fugudatabase.CommonData.getCachedFilePath(mediaResponse.getData().getChatMedia().get(i).getMessage().getUrl(), mediaResponse.getData().getChatMedia().get(i).getMuid());
                        imageMediaList.add(new Media(mediaResponse.getData().getChatMedia().get(i).getMessage().getImageUrl(),
                                mediaResponse.getData().getChatMedia().get(i).getMessage().getThumbnailUrl(),
                                mediaResponse.getData().getChatMedia().get(i).getMessageType(),
                                mediaResponse.getData().getChatMedia().get(i).getMessage().getUrl(),
                                mediaResponse.getData().getChatMedia().get(i).getMessage().getFileName(), localPath,
                                mediaResponse.getData().getChatMedia().get(i).getMessage(),
                                mediaResponse.getData().getChatMedia().get(i).getMuid(),
                                mediaResponse.getData().getChatMedia().get(i).getIsThreadMessage(),
                                mediaResponse.getData().getChatMedia().get(i).getCreatedAt(),
                                mediaResponse.getData().getChatMedia().get(i).getMessageId(),
                                mediaResponse.getData().getChatMedia().get(i).getThreadMessageId()));

                    }
                    else if(messageTypeList.get(0) == 11){
                        String localPath = "";
                        localPath = com.skeleton.mvp.fugudatabase.CommonData.getCachedFilePath(mediaResponse.getData().getChatMedia().get(i).getMessage().getUrl(), mediaResponse.getData().getChatMedia().get(i).getMuid());
                        documentMediaList.add(new Media(mediaResponse.getData().getChatMedia().get(i).getMessage().getImageUrl(),
                                mediaResponse.getData().getChatMedia().get(i).getMessage().getThumbnailUrl(),
                                mediaResponse.getData().getChatMedia().get(i).getMessageType(),
                                mediaResponse.getData().getChatMedia().get(i).getMessage().getUrl(),
                                mediaResponse.getData().getChatMedia().get(i).getMessage().getFileName(), localPath,
                                mediaResponse.getData().getChatMedia().get(i).getMessage(),
                                mediaResponse.getData().getChatMedia().get(i).getMuid(),
                                mediaResponse.getData().getChatMedia().get(i).getIsThreadMessage(),
                                mediaResponse.getData().getChatMedia().get(i).getCreatedAt(),
                                mediaResponse.getData().getChatMedia().get(i).getMessageId(),
                                mediaResponse.getData().getChatMedia().get(i).getThreadMessageId()));
                    }
                    else if(messageTypeList.get(0) == 12){
                        String localPath = "";
                        localPath = com.skeleton.mvp.fugudatabase.CommonData.getCachedFilePath(mediaResponse.getData().getChatMedia().get(i).getMessage().getUrl(), mediaResponse.getData().getChatMedia().get(i).getMuid());
                        videoMediaList.add(new Media(mediaResponse.getData().getChatMedia().get(i).getMessage().getImageUrl(),
                                mediaResponse.getData().getChatMedia().get(i).getMessage().getThumbnailUrl(),
                                mediaResponse.getData().getChatMedia().get(i).getMessageType(),
                                mediaResponse.getData().getChatMedia().get(i).getMessage().getUrl(),
                                mediaResponse.getData().getChatMedia().get(i).getMessage().getFileName(), localPath,
                                mediaResponse.getData().getChatMedia().get(i).getMessage(),
                                mediaResponse.getData().getChatMedia().get(i).getMuid(),
                                mediaResponse.getData().getChatMedia().get(i).getIsThreadMessage(),
                                mediaResponse.getData().getChatMedia().get(i).getCreatedAt(),
                                mediaResponse.getData().getChatMedia().get(i).getMessageId(),
                                mediaResponse.getData().getChatMedia().get(i).getThreadMessageId()));
                    }

                }
                if(messageTypeList.get(0) == 10){
                    callback.onResult(imageMediaList,messageTypeList.get(0));
                }
                else if(messageTypeList.get(0) == 11){
                    callback.onResult(documentMediaList,messageTypeList.get(0));
                }
                else if(messageTypeList.get(0) == 12){
                    callback.onResult(videoMediaList,messageTypeList.get(0));
                }

            }

            @Override
            public void failure(APIError error) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ivBack) {
            onBackPressed();
        }
    }
    public interface ResultCallback{
        void onResult(ArrayList<Media> data,int messageType);
    }

}
