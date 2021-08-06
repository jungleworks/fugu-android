package com.skeleton.mvp.ui.creategroup;

import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.FuguConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.ChatActivity;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse;
import com.skeleton.mvp.data.model.onetoone.CreateChatResponse;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.CommonParams;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.model.FuguConversation;
import com.skeleton.mvp.model.FuguSearchResult;
import com.skeleton.mvp.ui.intro.IntroActivity;
import com.skeleton.mvp.ui.search.ShareSearchActivity;
import com.skeleton.mvp.util.Log;
import com.skeleton.mvp.utils.FuguUtils;
import com.skeleton.mvp.utils.StringUtil;

import java.util.ArrayList;

import static com.skeleton.mvp.constant.FuguAppConstant.EN_USER_ID;
import static com.skeleton.mvp.constant.FuguAppConstant.SESSION_EXPIRE;
import static com.skeleton.mvp.ui.AppConstants.CHAT_WITH_USER_ID;

/**
 * Created by Rajat Dhamija
 * 02/01/18.
 */

public class ShareSearchResultAdapter extends RecyclerView.Adapter<ShareSearchResultAdapter.MyViewHolder> {

    private ArrayList<FuguSearchResult> searchResultList = new ArrayList<>();
    private Context mContext;
    private ShareSearchActivity searchActivity = null;
    private FcCommonResponse fcCommonResponse;

    public ShareSearchResultAdapter(ArrayList<FuguSearchResult> searchResultList, Context mContext, boolean isParentSearch) {
        this.searchResultList = searchResultList;
        this.mContext = mContext;
        searchActivity = (ShareSearchActivity) mContext;
        fcCommonResponse = CommonData.getCommonResponse();
    }

    @Override
    public ShareSearchResultAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.share_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ShareSearchResultAdapter.MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();
        final FuguSearchResult searchResult = searchResultList.get(position);
        holder.tvName.setText(searchResult.getName());
        holder.tvEmail.setText(searchResult.getEmail());
        holder.tvContactIcon.setText(FuguUtils.Companion.getFirstCharInUpperCase(searchResult.getName()));
        holder.tvContactIcon.setVisibility(View.VISIBLE);
        if (searchResult.isGroup()) {
            Glide.with(mContext).load(searchResult.getUser_image())
                    .into(holder.ivContactIcon);
        } else {
            if (!TextUtils.isEmpty(searchResult.getUser_image())) {
                holder.tvContactIcon.setVisibility(View.GONE);

                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.fugu_ic_channel_icon)
                        .error(R.drawable.fugu_ic_channel_icon)
                        .fitCenter()
                        .priority(Priority.HIGH);

                Glide.with(searchActivity)
                        .asBitmap()
                        .apply(options)
                        .load(searchResult.getUser_image())
                        .into(holder.ivContactIcon);

//                Glide.with(searchActivity).load(searchResult.getUser_image()).asBitmap()
//                        .centerCrop()
//                        .placeholder(ContextCompat.getDrawable(searchActivity, R.drawable.fugu_ic_channel_icon))
//                        .error(ContextCompat.getDrawable(searchActivity, R.drawable.fugu_ic_channel_icon))
//                        .into(new BitmapImageViewTarget(holder.ivContactIcon) {
//                            @Override
//                            protected void setResource(Bitmap resource) {
//                                RoundedBitmapDrawable circularBitmapDrawable =
//                                        RoundedBitmapDrawableFactory.create(searchActivity.getResources(), resource);
//                                circularBitmapDrawable.setCircular(true);
//                                holder.ivContactIcon.setImageDrawable(circularBitmapDrawable);
//                            }
//                        });
                holder.tvContactIcon.setVisibility(View.GONE);
            } else {
                holder.tvContactIcon.setVisibility(View.VISIBLE);
                if (searchResult.getUser_id() % 5 == 1) {
                    holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_greyy));
                } else if (searchResult.getUser_id() % 5 == 2) {
                    holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_teal));
                } else if (searchResult.getUser_id() % 5 == 3) {
                    holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_red));
                } else if (searchResult.getUser_id() % 5 == 4) {
                    holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_indigo));
                } else {
                    holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_red));
                }
            }
        }
        holder.ivContactIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("test", "test");
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchResult.isGroup()) {
                    CommonParams commonParams = new CommonParams.Builder()
                            .add(EN_USER_ID, fcCommonResponse.getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getEnUserId())
                            .add(CHAT_WITH_USER_ID, searchResult.getUser_id())
                            .build();
                    searchActivity.showLoading();
                    if (searchActivity.isNetworkConnected()) {
                        RestClient.getApiInterface(false).createOneToOneChat(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), fcCommonResponse.getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey(),
                                1,
                                BuildConfig.VERSION_CODE,
                                commonParams.getMap()).enqueue(new ResponseResolver<CreateChatResponse>() {
                            @Override
                            public void onSuccess(CreateChatResponse createChatResponse) {
                                Long channelId = Long.valueOf(createChatResponse.getData().getChannelId());
                                Intent chatIntent = new Intent(searchActivity, ChatActivity.class);
                                FuguConversation conversation = new FuguConversation();
                                conversation.setBusinessName(searchResult.getName());
                                conversation.setOpenChat(true);
                                conversation.setChannelId(channelId);
                                conversation.setLabel(searchResult.getName());
                                conversation.setUserName(StringUtil.toCamelCase(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFullName()));
                                conversation.setUserId(Long.valueOf(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getUserId()));
                                conversation.setEnUserId(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getEnUserId());
                                chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
                                searchActivity.startActivity(chatIntent);
                                searchActivity.hideLoading();
                                searchActivity.finish();

                            }

                            @Override
                            public void onError(ApiError error) {
                                searchActivity.hideLoading();
                                if (error.getStatusCode() == SESSION_EXPIRE) {
                                    CommonData.clearData();
                                    FuguConfig.clearFuguData(searchActivity);
                                    searchActivity.finishAffinity();
                                    searchActivity.startActivity(new Intent(searchActivity, IntroActivity.class));
                                } else {
                                    searchActivity.showErrorMessage(error.getMessage());
                                }
                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                searchActivity.hideLoading();
                            }
                        });
                    } else {
                        searchActivity.showErrorMessage(R.string.error_internet_not_connected);
                    }
                } else {
                    Long channelId = searchResult.getUser_id();
                    Intent chatIntent = new Intent(searchActivity, ChatActivity.class);
                    FuguConversation conversation = new FuguConversation();
                    conversation.setBusinessName(searchResult.getName());
                    conversation.setOpenChat(true);
                    conversation.setChannelId(channelId);
                    conversation.setLabel(searchResult.getName());
                    conversation.setUserName(StringUtil.toCamelCase(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFullName()));
                    conversation.setUserId(Long.valueOf(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getUserId()));
                    conversation.setEnUserId(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getEnUserId());
                    conversation.setChat_type(4);
                    conversation.setJoined(searchResult.isJoined());
                    chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
                    searchActivity.startActivity(chatIntent);
                    searchActivity.finish();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchResultList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvEmail, tvContactIcon;
        private ImageView ivContactIcon;
        private RelativeLayout rlRoot;

        MyViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvContactIcon = itemView.findViewById(R.id.tvChannelIcon);
            ivContactIcon = itemView.findViewById(R.id.ivChannelIcon);
            rlRoot = itemView.findViewById(R.id.rlRoot);
        }
    }

}
