package com.skeleton.mvp.ui.creategroup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.FuguConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.ChatActivity;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.db.ChatDatabase;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse;
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo;
import com.skeleton.mvp.data.model.onetoone.CreateChatResponse;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.CommonParams;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.model.FuguCacheSearchResult;
import com.skeleton.mvp.model.FuguConversation;
import com.skeleton.mvp.model.FuguSearchResult;
import com.skeleton.mvp.ui.intro.IntroActivity;
import com.skeleton.mvp.ui.search.CreateGroupSearchActivity;
import com.skeleton.mvp.utils.FuguUtils;
import com.skeleton.mvp.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static com.skeleton.mvp.constant.FuguAppConstant.EN_USER_ID;
import static com.skeleton.mvp.constant.FuguAppConstant.SESSION_EXPIRE;
import static com.skeleton.mvp.ui.AppConstants.CHAT_WITH_USER_ID;

/**
 * Created by Rajat Dhamija
 * 02/01/18.
 */

public class CreateGroupSearchResultAdapter extends RecyclerView.Adapter<CreateGroupSearchResultAdapter.MyViewHolder> {

    private ArrayList<FuguSearchResult> searchResultList = new ArrayList<>();
    private Context mContext;
    private CreateGroupActivity createGroupActivity = null;
    private CreateGroupSearchActivity homeActivity = null;
    private boolean isParentSearch;
    private FcCommonResponse fcCommonResponse;
    private HashMap<Integer, Integer> dummyImagesArray2 = new HashMap<>();
    private Typeface normalFont;
    public CreateGroupSearchResultAdapter(ArrayList<FuguSearchResult> searchResultList, Context mContext, boolean isParentSearch) {
        this.searchResultList = searchResultList;
        this.isParentSearch = isParentSearch;
        this.mContext = mContext;
        if (isParentSearch) {
            homeActivity = (CreateGroupSearchActivity) mContext;
        } else {
            createGroupActivity = (CreateGroupActivity) mContext;
        }
        fcCommonResponse = CommonData.getCommonResponse();
        dummyImagesArray2.put(1, R.drawable.rectangle_grey);
        dummyImagesArray2.put(2, R.drawable.rectangle_indigo);
        dummyImagesArray2.put(3, R.drawable.rectangle_purple);
        dummyImagesArray2.put(4, R.drawable.rectangle_red);
        dummyImagesArray2.put(0, R.drawable.rectangle_teal);
        normalFont = Typeface.createFromAsset(mContext.getAssets(), FuguAppConstant.FONT_REGULAR);
    }

    @Override
    public CreateGroupSearchResultAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    public void updateList(ArrayList<FuguSearchResult> searchResultList) {
        this.searchResultList = searchResultList;
    }

    @Override
    public void onBindViewHolder(final CreateGroupSearchResultAdapter.MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();
        final FuguSearchResult searchResult = searchResultList.get(position);
        holder.tvName.setText(searchResult.getName());
        if (!TextUtils.isEmpty(searchResult.getName())) {
            holder.tvContactIcon.setText(FuguUtils.Companion.getFirstCharInUpperCase(searchResult.getName()));
        }
        if (!TextUtils.isEmpty(searchResult.getEmail())) {
            holder.tvEmail.setVisibility(View.VISIBLE);
            holder.tvEmail.setText(Html.fromHtml(searchResult.getEmail()) + "");
        }else{
            holder.tvEmail.setVisibility(View.GONE);
        }

        holder.tvContactIcon.setVisibility(View.VISIBLE);
        if (searchResult.isGroup()) {
            if (searchResult.getUser_image().equals("https://fuguchat.s3.ap-south-1.amazonaws.com/default/WwX5qYGSEb_1518441286074.png")) {
                holder.ivContactIcon.setVisibility(View.GONE);
                holder.llCircle.setVisibility(View.VISIBLE);
                holder.tvContactIcon.setVisibility(View.GONE);
                switch (searchResult.getMembersInfos().size()) {
                    case 1:
                        holder.rlOne.setVisibility(View.VISIBLE);
                        holder.llRight.setVisibility(View.GONE);
                        holder.rlTwo.setVisibility(View.GONE);
                        holder.rlThree.setVisibility(View.GONE);
                        setImageResource(holder.ivOne, searchResult.getMembersInfos().get(0).getUserImage(),
                                searchResult.getMembersInfos().get(0).getUserId(),
                                searchResult.getMembersInfos().get(0).getFullName(),
                                holder.tvOne);
                        break;
                    case 2:
                        holder.rlOne.setVisibility(View.VISIBLE);
                        holder.rlTwo.setVisibility(View.VISIBLE);
                        holder.llRight.setVisibility(View.VISIBLE);
                        holder.rlThree.setVisibility(View.GONE);
                        setImageResource(holder.ivOne, searchResult.getMembersInfos().get(0).getUserImage(),
                                searchResult.getMembersInfos().get(0).getUserId(),
                                searchResult.getMembersInfos().get(0).getFullName(),
                                holder.tvOne);
                        setImageResource(holder.ivTwo, searchResult.getMembersInfos().get(1).getUserImage(),
                                searchResult.getMembersInfos().get(1).getUserId(),
                                searchResult.getMembersInfos().get(1).getFullName(),
                                holder.tvTwo);
                        break;
                    case 3:
                        holder.rlOne.setVisibility(View.VISIBLE);
                        holder.rlTwo.setVisibility(View.VISIBLE);
                        holder.llRight.setVisibility(View.VISIBLE);
                        holder.rlThree.setVisibility(View.VISIBLE);
                        setImageResource(holder.ivOne, searchResult.getMembersInfos().get(0).getUserImage(),
                                searchResult.getMembersInfos().get(0).getUserId(),
                                searchResult.getMembersInfos().get(0).getFullName(),
                                holder.tvOne);
                        setImageResource(holder.ivTwo, searchResult.getMembersInfos().get(1).getUserImage(),
                                searchResult.getMembersInfos().get(1).getUserId(),
                                searchResult.getMembersInfos().get(1).getFullName(),
                                holder.tvTwo);
                        setImageResource(holder.ivThree, searchResult.getMembersInfos().get(2).getUserImage(),
                                searchResult.getMembersInfos().get(2).getUserId(),
                                searchResult.getMembersInfos().get(2).getFullName(),
                                holder.tvThree);
                        break;
                    default:
                        break;
                }
            } else {
                holder.ivContactIcon.setVisibility(View.VISIBLE);
                holder.llCircle.setVisibility(View.GONE);
                holder.tvContactIcon.setVisibility(View.GONE);

                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder)
                        .fitCenter()
                        .priority(Priority.HIGH)
                        .transforms(new CenterCrop(), new RoundedCorners(10));

                Glide.with(homeActivity)
                        .asBitmap()
                        .apply(options)
                        .load(searchResult.getUser_image())
                        .into(holder.ivContactIcon);


//                Glide.with(homeActivity).load(searchResult.getUser_image()).asBitmap()
//                        .centerCrop()
//                        .placeholder(ContextCompat.getDrawable(homeActivity, R.drawable.fugu_ic_channel_icon))
//                        .error(ContextCompat.getDrawable(homeActivity, R.drawable.fugu_ic_channel_icon))
//                        .into(new BitmapImageViewTarget(holder.ivContactIcon) {
//                            @Override
//                            protected void setResource(Bitmap resource) {
//                                RoundedBitmapDrawable circularBitmapDrawable =
//                                        RoundedBitmapDrawableFactory.create(homeActivity.getResources(), resource);
//                                circularBitmapDrawable.setCircular(true);
//                                holder.ivContactIcon.setImageDrawable(circularBitmapDrawable);
//                            }
//                        });
            }
        } else {
            if (createGroupActivity != null) {
                if (!TextUtils.isEmpty(searchResult.getUser_image())) {
                    holder.llCircle.setVisibility(View.GONE);
                    holder.ivContactIcon.setVisibility(View.VISIBLE);
                    holder.tvContactIcon.setVisibility(View.GONE);

                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.fugu_ic_channel_icon)
                            .error(R.drawable.fugu_ic_channel_icon)
                            .fitCenter()
                            .priority(Priority.HIGH)
                            .transforms(new CenterCrop(), new RoundedCorners(10));

                    Glide.with(createGroupActivity)
                            .asBitmap()
                            .apply(options)
                            .load(searchResult.getUser_image())
                            .into(holder.ivContactIcon);

//                    Glide.with(createGroupActivity).load(searchResult.getUser_image()).asBitmap()
//                            .centerCrop()
//                            .placeholder(ContextCompat.getDrawable(createGroupActivity, R.drawable.fugu_ic_channel_icon))
//                            .error(ContextCompat.getDrawable(createGroupActivity, R.drawable.fugu_ic_channel_icon))
//                            .into(new BitmapImageViewTarget(holder.ivContactIcon) {
//                                @Override
//                                protected void setResource(Bitmap resource) {
//                                    RoundedBitmapDrawable circularBitmapDrawable =
//                                            RoundedBitmapDrawableFactory.create(createGroupActivity.getResources(), resource);
//                                    circularBitmapDrawable.setCircular(true);
//                                    holder.ivContactIcon.setImageDrawable(circularBitmapDrawable);
//                                }
//                            });
                } else {
                    holder.llCircle.setVisibility(View.GONE);
                    holder.ivContactIcon.setVisibility(View.VISIBLE);
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
            } else {
                if (!TextUtils.isEmpty(searchResult.getUser_image())) {
                    holder.llCircle.setVisibility(View.GONE);
                    holder.ivContactIcon.setVisibility(View.VISIBLE);
                    holder.tvContactIcon.setVisibility(View.GONE);


                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.profile_placeholder)
                            .error(R.drawable.profile_placeholder)
                            .fitCenter()
                            .priority(Priority.HIGH)
                            .transforms(new CenterCrop(), new RoundedCorners(10));

                    Glide.with(homeActivity)
                            .asBitmap()
                            .apply(options)
                            .load(searchResult.getUser_image())
                            .into(holder.ivContactIcon);

//                    Glide.with(homeActivity).load(searchResult.getUser_image()).asBitmap()
//                            .centerCrop()
//                            .placeholder(ContextCompat.getDrawable(homeActivity, R.drawable.fugu_ic_channel_icon))
//                            .error(ContextCompat.getDrawable(homeActivity, R.drawable.fugu_ic_channel_icon))
//                            .into(new BitmapImageViewTarget(holder.ivContactIcon) {
//                                @Override
//                                protected void setResource(Bitmap resource) {
//                                    RoundedBitmapDrawable circularBitmapDrawable =
//                                            RoundedBitmapDrawableFactory.create(homeActivity.getResources(), resource);
//                                    circularBitmapDrawable.setCircular(true);
//                                    holder.ivContactIcon.setImageDrawable(circularBitmapDrawable);
//                                }
//                            });
                } else {
                    holder.llCircle.setVisibility(View.GONE);
                    holder.ivContactIcon.setVisibility(View.VISIBLE);
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
//            holder.tvCount.formatString(searchResult.getCount() + "");
        }
        if (isParentSearch) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!searchResult.isGroup()) {
                        int count = 0;
                        int j = 0;
                        boolean flag = false;
                        ArrayList<FuguCacheSearchResult> searchResults = new ArrayList<>();
                        if (com.skeleton.mvp.fugudatabase.CommonData.getSearchResults(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey()) != null) {
                            searchResults = com.skeleton.mvp.fugudatabase.CommonData.getSearchResults(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey());
                            for (int i = 0; i < searchResults.size(); i++) {
                                if (searchResult.getUser_id().compareTo(searchResults.get(i).getUser_id()) == 0) {
                                    flag = true;
                                    j = i;
                                }
                            }
                            if (flag) {
                                count = searchResults.get(j).getClickCount();
                                count = count + 1;
                                searchResults.remove(j);
                                searchResults.add(new FuguCacheSearchResult(searchResult.getName(), searchResult.getUser_id(), searchResult.getUser_image(), searchResult.getEmail(), count, searchResult.getMembersInfos(), searchResult.isGroup(), searchResult.getChatType()));
                            } else {
                                searchResults.add(new FuguCacheSearchResult(searchResult.getName(), searchResult.getUser_id(), searchResult.getUser_image(), searchResult.getEmail(), count, searchResult.getMembersInfos(), searchResult.isGroup(), searchResult.getChatType()));
                            }
                        } else {
                            searchResults.add(new FuguCacheSearchResult(searchResult.getName(), searchResult.getUser_id(), searchResult.getUser_image(), searchResult.getEmail(), count, searchResult.getMembersInfos(), searchResult.isGroup(), searchResult.getChatType()));
                        }
                        com.skeleton.mvp.fugudatabase.CommonData.setSearchResults(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo().get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey(), searchResults);
                        CommonParams commonParams = new CommonParams.Builder()
                                .add(EN_USER_ID, CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getEnUserId())
                                .add(CHAT_WITH_USER_ID, searchResult.getUser_id())
                                .build();
                        homeActivity.showLoading();
                        if (homeActivity.isNetworkConnected()) {
                            RestClient.getApiInterface(false).createOneToOneChat(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), fcCommonResponse.getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey(),
                                    1,
                                    BuildConfig.VERSION_CODE,
                                    commonParams.getMap()).enqueue(new ResponseResolver<CreateChatResponse>() {
                                @Override
                                public void onSuccess(CreateChatResponse createChatResponse) {
                                    WorkspacesInfo workspacesInfo = CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition());
                                    Long channelId = Long.valueOf(createChatResponse.getData().getChannelId());
                                    Intent chatIntent = new Intent(homeActivity, ChatActivity.class);
                                    FuguConversation conversation = new FuguConversation();
                                    conversation.setBusinessName(searchResult.getName());
                                    conversation.setOpenChat(true);
                                    conversation.setChannelId(channelId);
                                    conversation.setLabel(searchResult.getName());
                                    conversation.setUserName(StringUtil.toCamelCase(workspacesInfo.getFullName()));
                                    conversation.setUserId(Long.valueOf(workspacesInfo.getUserId()));
                                    conversation.setEnUserId(workspacesInfo.getEnUserId());
                                    conversation.setChat_type(searchResult.getChatType());
                                    chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
                                    final FuguConversation dbConversation = new FuguConversation(channelId, Long.valueOf(workspacesInfo.getUserId()),
                                            workspacesInfo.getFullName(), "", "", searchResult.getName(), searchResult.getUser_image(), 1, 2,
                                            Long.valueOf(workspacesInfo.getUserId()), 1,
                                            0, "", "UNMUTED", new ArrayList<>(), "",
                                            1,
                                            "VIDEO", 1);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            LinkedHashMap<Long, FuguConversation> conversationMap = ChatDatabase.INSTANCE.getConversationMap(workspacesInfo.getFuguSecretKey());
                                            conversationMap.put(channelId, dbConversation);
                                            ChatDatabase.INSTANCE.setConversationMap(conversationMap, workspacesInfo.getFuguSecretKey());
                                        }
                                    }).start();
                                    homeActivity.startActivity(chatIntent);
                                    homeActivity.hideLoading();
                                    homeActivity.finish();
                                }

                                @Override
                                public void onError(ApiError error) {
                                    homeActivity.hideLoading();
                                    if (error.getStatusCode() == SESSION_EXPIRE) {
                                        CommonData.clearData();

                                        FuguConfig.clearFuguData(homeActivity);
                                        homeActivity.finishAffinity();
                                        homeActivity.startActivity(new Intent(homeActivity, IntroActivity.class));
                                    } else {
                                        homeActivity.showErrorMessage(error.getMessage());
                                    }
                                }

                                @Override
                                public void onFailure(Throwable throwable) {
                                    homeActivity.hideLoading();
                                }
                            });
                        } else {
                            homeActivity.showErrorMessage(R.string.error_internet_not_connected);
                        }
                    } else {
                        WorkspacesInfo workspacesInfo = CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition());
                        Long channelId = searchResult.getUser_id();
                        Intent chatIntent = new Intent(homeActivity, ChatActivity.class);
                        FuguConversation conversation = new FuguConversation();
                        conversation.setBusinessName(searchResult.getName());
                        conversation.setOpenChat(true);
                        conversation.setChannelId(channelId);
                        conversation.setUserName(StringUtil.toCamelCase(workspacesInfo.getFullName()));
                        conversation.setUserId(Long.valueOf(workspacesInfo.getUserId()));
                        conversation.setEnUserId(workspacesInfo.getEnUserId());
                        conversation.setChat_type(searchResult.getChatType());
                        conversation.setLabel(searchResult.getName());
                        conversation.setJoined(searchResult.isJoined());
                        chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
                        final FuguConversation dbConversation = new FuguConversation(channelId, Long.valueOf(workspacesInfo.getUserId()),
                                workspacesInfo.getFullName(), "", "", searchResult.getName(), searchResult.getUser_image(), 1, 2,
                                Long.valueOf(workspacesInfo.getUserId()), 1,
                                0, "", "UNMUTED", new ArrayList<>(), "",
                                1, "VIDEO", 1);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                LinkedHashMap<Long, FuguConversation> conversationMap = ChatDatabase.INSTANCE.getConversationMap(workspacesInfo.getFuguSecretKey());
                                conversationMap.put(channelId, dbConversation);
                                ChatDatabase.INSTANCE.setConversationMap(conversationMap, workspacesInfo.getFuguSecretKey());
                            }
                        }).start();
                        homeActivity.startActivity(chatIntent);
                        homeActivity.finish();
                    }

                }
            });
        } else {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return searchResultList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvEmail, tvContactIcon;
        private ImageView ivContactIcon;
        private LinearLayout llCircle;
        private LinearLayout llRight;
        private ImageView ivOne, ivTwo, ivThree;
        private RelativeLayout rlOne, rlTwo, rlThree;
        private TextView tvOne, tvTwo, tvThree;

        MyViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
//            tvCount = itemView.findViewById(R.id.tvCount);
            tvContactIcon = itemView.findViewById(R.id.tvContactIcon);
            ivContactIcon = itemView.findViewById(R.id.ivContactImage);
            llCircle = itemView.findViewById(R.id.llCircle);
            llRight = itemView.findViewById(R.id.llRight);
            ivOne = itemView.findViewById(R.id.ivOne);
            ivTwo = itemView.findViewById(R.id.ivTwo);
            ivThree = itemView.findViewById(R.id.ivThree);

            rlOne = itemView.findViewById(R.id.rlOne);
            rlTwo = itemView.findViewById(R.id.rlTwo);
            rlThree = itemView.findViewById(R.id.rlThree);

            tvOne = itemView.findViewById(R.id.tvOne);
            tvTwo = itemView.findViewById(R.id.tvTwo);
            tvThree = itemView.findViewById(R.id.tvThree);
            tvEmail.setTypeface(normalFont);
            llCircle.setClipToOutline(true);
        }
    }

    public interface AddChip {
        void addChip(String fullName, String email, Long userId);
    }

    private void setImageResource(final ImageView imageView, String url, Long userId, String name, TextView textView) {
        imageView.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(url)) {
            textView.setText(FuguUtils.Companion.getFirstCharInUpperCase(name));
            textView.setVisibility(View.VISIBLE);
//            Glide.clear(imageView);
            int value = (int) (userId % 5);
            imageView.setImageDrawable(ContextCompat.getDrawable(homeActivity, dummyImagesArray2.get(value)));
        } else {
            textView.setVisibility(View.GONE);


            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .fitCenter()
                    .priority(Priority.HIGH);
//                    .transforms(new CenterCrop(), new RoundedCorners(10));

            Glide.with(homeActivity)
                    .asBitmap()
                    .apply(options)
                    .load(url)
                    .into(imageView);

//            Glide.with(homeActivity).load(url).asBitmap()
//                    .centerCrop()
//                    .placeholder(ContextCompat.getDrawable(homeActivity, R.drawable.placeholder))
//                    .error(ContextCompat.getDrawable(homeActivity, R.drawable.placeholder))
//                    .into(imageView);
        }
    }
}
