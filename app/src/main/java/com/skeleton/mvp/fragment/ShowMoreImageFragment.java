package com.skeleton.mvp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.MediaActivity;
import com.skeleton.mvp.activity.ShowMoreActivity;
import com.skeleton.mvp.adapter.BigGalleryAdapter;
import com.skeleton.mvp.adapter.ShowMoreViewPagerAdapter;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.fugudatabase.CommonData;
import com.skeleton.mvp.model.Media;
import com.skeleton.mvp.utils.EndlessScrolling;

import java.io.File;
import java.util.ArrayList;

public class ShowMoreImageFragment extends Fragment {

    private ArrayList<Media> mediaList = new ArrayList<>();
    private RecyclerView rvMembers, rvMedia;
    private BigGalleryAdapter mediaAdapter;
    private int start = 0, end = 20;
    private GridLayoutManager layoutManager;
    private boolean showLoading = true;
    private ViewPager viewPager;
    private ShowMoreViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;
    private Context mContext;
    private ShowMoreListener mListener;
    private ArrayList<Integer> imageMessagetypeList = new ArrayList<>();
    private EndlessScrolling endlessScrolling;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_more_image, container, false);
        rvMedia = view.findViewById(R.id.rvShowMoreImage);
        layoutManager = new GridLayoutManager(mContext, 3);

        rvMedia.setLayoutManager(layoutManager);
        mediaAdapter = new BigGalleryAdapter(mContext, mediaList);
        rvMedia.setAdapter(mediaAdapter);
        setEndlessScrolling();

        rvMedia.addOnItemTouchListener(new BigGalleryAdapter.RecyclerTouchListener(mContext.getApplicationContext(), rvMedia, new BigGalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                String dirPath = Environment.getExternalStorageDirectory() + File.separator + FuguAppConstant.APP_NAME_SHORT  +
                        File.separator + CommonData.getWorkspaceResponse(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey()).getWorkspaceName().replaceAll(" ", "").replaceAll("'s", "") + File.separator + FuguAppConstant.IMAGE;

                String dirPathPrivate = Environment.getExternalStorageDirectory() + File.separator + FuguAppConstant.APP_NAME_SHORT  +
                        File.separator + CommonData.getWorkspaceResponse(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey()).getWorkspaceName().replaceAll(" ", "").replaceAll("'s", "") + File.separator + FuguAppConstant.PRIVATE_IMAGES;

                File jpgFile = new File(dirPath + File.separator + mediaList.get(position).getMessage().getFileName() + "_" + mediaList.get(position).getMuid() + ".jpg");
                File jpgPrivateFile = new File(dirPathPrivate + File.separator + mediaList.get(position).getMessage().getFileName() + "_" + mediaList.get(position).getMuid() + ".jpg");
                File jpegFile = new File(dirPath + File.separator + mediaList.get(position).getMessage().getFileName() + "_" + mediaList.get(position).getMuid() + ".jpeg");
                File jpegPrivateFile = new File(dirPathPrivate + File.separator + mediaList.get(position).getMessage().getFileName() + "_" + mediaList.get(position).getMuid() + ".jpeg");

                if(jpgFile.exists() || jpegFile.exists() || jpgPrivateFile.exists() || jpegPrivateFile.exists() || mediaList.get(position).getMessage().getImageUrl().split("\\.")[mediaList.get(position).getMessage().getImageUrl().split("\\.").length - 1].equals("gif")) {
                    Intent intent = new Intent((ShowMoreActivity) mContext, MediaActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("images", mediaList);
                    intent.putExtra("isFromShowMore",1);
                    startActivity(intent);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        imageMessagetypeList = new ArrayList<>();
        imageMessagetypeList.add(10);
        mListener.apiGetMedia(imageMessagetypeList, 0, 20, new ShowMoreActivity.ResultCallback() {
            @Override
            public void onResult(ArrayList<Media> data,int messageType) {
                if(messageType == 10){
                    mediaList.clear();
                    mediaList=data;
//                mediaAdapter.updateList(mediaList);
                    mediaAdapter = new BigGalleryAdapter(mContext, mediaList);
                    rvMedia.setAdapter(mediaAdapter);
                    //mediaAdapter.notifyDataSetChanged();
                }

            }
        });

        return view;
    }

    private void setEndlessScrolling(){
        endlessScrolling=new EndlessScrolling((LinearLayoutManager) rvMedia.getLayoutManager()) {
            @Override
            public void onLoadMore(int currentPages) {
                start = end+1;
                end = start + 25;
                imageMessagetypeList = new ArrayList<>();
                imageMessagetypeList.add(10);
                mListener.apiGetMedia(imageMessagetypeList, start, end, (data, messageType) -> {
                    if(messageType == 10){
                        mediaList=new ArrayList<>();
                        mediaList.addAll(data);
                        mediaAdapter.notifyDataSetChanged();
                    }

                });
            }

            @Override
            public void onHide() {

            }

            @Override
            public void onShow() {

            }
        };
        rvMedia.setOnScrollListener(endlessScrolling);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        mListener = (ShowMoreListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface ShowMoreListener{
        public void apiGetMedia(ArrayList<Integer> messageTypeList, int start, int end, ShowMoreActivity.ResultCallback callback);
    }
}
