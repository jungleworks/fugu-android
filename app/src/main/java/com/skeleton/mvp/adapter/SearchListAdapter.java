package com.skeleton.mvp.adapter;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.skeleton.mvp.activity.ForwardActivity;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse;
import com.skeleton.mvp.data.model.onetoone.CreateChatResponse;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.CommonParams;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.model.FuguConversation;
import com.skeleton.mvp.model.Message;
import com.skeleton.mvp.model.SearchList;
import com.skeleton.mvp.ui.intro.IntroActivity;
import com.skeleton.mvp.utils.CircleLinearLayout;
import com.skeleton.mvp.utils.FuguUtils;
import com.skeleton.mvp.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;

import static com.skeleton.mvp.constant.FuguAppConstant.EN_USER_ID;
import static com.skeleton.mvp.constant.FuguAppConstant.SESSION_EXPIRE;
import static com.skeleton.mvp.ui.AppConstants.CHAT_WITH_USER_ID;

/**
 * Created by rajatdhamija
 * 22/05/18.
 */

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.MyViewHolder> {

    private ArrayList<SearchList> searchList = new ArrayList<>();
    private Context mContext;
    private ForwardActivity forwardActivity;
    private FcCommonResponse fcCommonResponse;
    private Message message;
    private HashMap<Integer, Integer> dummyImagesArray2 = new HashMap<>();

    public SearchListAdapter(ArrayList<SearchList> searchList, Context mContext, Message message) {
        this.searchList = searchList;
        this.mContext = mContext;
        forwardActivity = (ForwardActivity) mContext;
        fcCommonResponse = CommonData.getCommonResponse();
        this.message = message;
        dummyImagesArray2.put(1, R.drawable.rectangle_grey);
        dummyImagesArray2.put(2, R.drawable.rectangle_indigo);
        dummyImagesArray2.put(3, R.drawable.rectangle_purple);
        dummyImagesArray2.put(4, R.drawable.rectangle_red);
        dummyImagesArray2.put(0, R.drawable.rectangle_teal);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.forward_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();
        final SearchList search = searchList.get(position);
        holder.tvName.setText(search.getName());
        try {
            holder.tvEmail.setText(Html.fromHtml(search.getEmail()) + "");
        }catch (Exception e){
            holder.tvEmail.setText("");
        }
        if (search.isGroup()) {
            if (search.getUser_image().equals("https://fuguchat.s3.ap-south-1.amazonaws.com/default/WwX5qYGSEb_1518441286074.png")) {
                holder.ivContactIcon.setVisibility(View.GONE);
                holder.llCircle.setVisibility(View.VISIBLE);
                holder.tvContactIcon.setVisibility(View.GONE);
                switch (search.getMembersInfos().size()) {
                    case 1:
                        holder.rlOne.setVisibility(View.VISIBLE);
                        holder.llRight.setVisibility(View.GONE);
                        holder.rlTwo.setVisibility(View.GONE);
                        holder.rlThree.setVisibility(View.GONE);
                        setImageResource(holder.ivOne, search.getMembersInfos().get(0).getUserImage(),
                                search.getMembersInfos().get(0).getUserId(),
                                search.getMembersInfos().get(0).getFullName(),
                                holder.tvOne);
                        break;
                    case 2:
                        holder.rlOne.setVisibility(View.VISIBLE);
                        holder.rlTwo.setVisibility(View.VISIBLE);
                        holder.llRight.setVisibility(View.VISIBLE);
                        holder.rlThree.setVisibility(View.GONE);
                        setImageResource(holder.ivOne, search.getMembersInfos().get(0).getUserImage(),
                                search.getMembersInfos().get(0).getUserId(),
                                search.getMembersInfos().get(0).getFullName(),
                                holder.tvOne);
                        setImageResource(holder.ivTwo, search.getMembersInfos().get(1).getUserImage(),
                                search.getMembersInfos().get(1).getUserId(),
                                search.getMembersInfos().get(1).getFullName(),
                                holder.tvTwo);
                        break;
                    case 3:
                        holder.rlOne.setVisibility(View.VISIBLE);
                        holder.rlTwo.setVisibility(View.VISIBLE);
                        holder.llRight.setVisibility(View.VISIBLE);
                        holder.rlThree.setVisibility(View.VISIBLE);
                        setImageResource(holder.ivOne, search.getMembersInfos().get(0).getUserImage(),
                                search.getMembersInfos().get(0).getUserId(),
                                search.getMembersInfos().get(0).getFullName(),
                                holder.tvOne);
                        setImageResource(holder.ivTwo, search.getMembersInfos().get(1).getUserImage(),
                                search.getMembersInfos().get(1).getUserId(),
                                search.getMembersInfos().get(1).getFullName(),
                                holder.tvTwo);
                        setImageResource(holder.ivThree, search.getMembersInfos().get(2).getUserImage(),
                                search.getMembersInfos().get(2).getUserId(),
                                search.getMembersInfos().get(2).getFullName(),
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
                        .placeholder(R.drawable.fugu_ic_channel_icon)
                        .error(R.drawable.fugu_ic_channel_icon)
                        .fitCenter()
                        .priority(Priority.HIGH)
                        .transforms(new CenterCrop(), new RoundedCorners(10));

                Glide.with(forwardActivity)
                        .asBitmap()
                        .apply(options)
                        .load(search.getUser_image())
                        .into(holder.ivContactIcon);


//                Glide.with(forwardActivity).load(search.getUser_image()).asBitmap()
//                        .centerCrop()
//                        .placeholder(ContextCompat.getDrawable(forwardActivity, R.drawable.fugu_ic_channel_icon))
//                        .error(ContextCompat.getDrawable(forwardActivity, R.drawable.fugu_ic_channel_icon))
//                        .into(new BitmapImageViewTarget(holder.ivContactIcon) {
//                            @Override
//                            protected void setResource(Bitmap resource) {
//                                RoundedBitmapDrawable circularBitmapDrawable =
//                                        RoundedBitmapDrawableFactory.create(forwardActivity.getResources(), resource);
//                                circularBitmapDrawable.setCircular(true);
//                                holder.ivContactIcon.setImageDrawable(circularBitmapDrawable);
//                            }
//                        });
            }
        } else {
            if (forwardActivity != null) {
                if (!TextUtils.isEmpty(search.getUser_image())) {
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

                    Glide.with(forwardActivity)
                            .asBitmap()
                            .apply(options)
                            .load(search.getUser_image())
                            .into(holder.ivContactIcon);

//                    Glide.with(forwardActivity).load(search.getUser_image()).asBitmap()
//                            .centerCrop()
//                            .placeholder(ContextCompat.getDrawable(forwardActivity, R.drawable.fugu_ic_channel_icon))
//                            .error(ContextCompat.getDrawable(forwardActivity, R.drawable.fugu_ic_channel_icon))
//                            .into(new BitmapImageViewTarget(holder.ivContactIcon) {
//                                @Override
//                                protected void setResource(Bitmap resource) {
//                                    RoundedBitmapDrawable circularBitmapDrawable =
//                                            RoundedBitmapDrawableFactory.create(forwardActivity.getResources(), resource);
//                                    circularBitmapDrawable.setCircular(true);
//                                    holder.ivContactIcon.setImageDrawable(circularBitmapDrawable);
//                                }
//                            });
                } else {
                    holder.llCircle.setVisibility(View.GONE);
                    holder.ivContactIcon.setVisibility(View.VISIBLE);
                    holder.tvContactIcon.setVisibility(View.VISIBLE);
                    holder.tvContactIcon.setText(FuguUtils.Companion.getFirstCharInUpperCase(search.getName()));
                    if (search.getUser_id() % 5 == 1) {
                        holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_greyy));
                    } else if (search.getUser_id() % 5 == 2) {
                        holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_teal));
                    } else if (search.getUser_id() % 5 == 3) {
                        holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_red));
                    } else if (search.getUser_id() % 5 == 4) {
                        holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_indigo));
                    } else {
                        holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_red));
                    }
                }
            }
//            holder.tvCount.formatString(search.getCount() + "");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!search.isGroup() && search.isFromSearch()) {
                    int count = 0;
                    int j = 0;
                    boolean flag = false;
                    CommonParams commonParams = new CommonParams.Builder()
                            .add(EN_USER_ID, CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getEnUserId())
                            .add(CHAT_WITH_USER_ID, search.getUser_id())
                            .build();
                    forwardActivity.showLoading();
                    if (forwardActivity.isNetworkConnected()) {
                        RestClient.getApiInterface(false).createOneToOneChat(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), fcCommonResponse.getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey(),
                                1,
                                BuildConfig.VERSION_CODE,
                                commonParams.getMap()).enqueue(new ResponseResolver<CreateChatResponse>() {
                            @Override
                            public void onSuccess(CreateChatResponse createChatResponse) {
                                Long channelId = Long.valueOf(createChatResponse.getData().getChannelId());
                                Intent chatIntent = new Intent(forwardActivity, ChatActivity.class);
                                FuguConversation conversation = new FuguConversation();
                                conversation.setBusinessName(search.getName());
                                conversation.setOpenChat(true);
                                conversation.setChannelId(channelId);
                                conversation.setUserName(StringUtil.toCamelCase(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFullName()));
                                conversation.setUserId(Long.valueOf(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getUserId()));
                                conversation.setEnUserId(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getEnUserId());
                                conversation.setLabel(search.getName());
                                if(search.isGroup() == false){
                                    conversation.setChat_type(2);
                                }
                                chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
                                chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                chatIntent.putExtra("MESSAGE", message);

                                forwardActivity.startActivity(chatIntent);
                                forwardActivity.hideLoading();
                                forwardActivity.finish();

                            }

                            @Override
                            public void onError(ApiError error) {
                                forwardActivity.hideLoading();
                                if (error.getStatusCode() == SESSION_EXPIRE) {
                                    CommonData.clearData();
                                    FuguConfig.clearFuguData(forwardActivity);
                                    forwardActivity.finishAffinity();
                                    forwardActivity.startActivity(new Intent(forwardActivity, IntroActivity.class));
                                } else {
                                    forwardActivity.showErrorMessage(error.getMessage());
                                }
                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                forwardActivity.hideLoading();
                            }
                        });
                    } else {
                        forwardActivity.showErrorMessage(R.string.error_internet_not_connected);
                    }
                } else {
                    Long channelId = search.getUser_id();
                    Intent chatIntent = new Intent(forwardActivity, ChatActivity.class);
                    FuguConversation conversation = new FuguConversation();
                    conversation.setBusinessName(search.getName());
                    conversation.setOpenChat(true);
                    conversation.setChannelId(channelId);
                    conversation.setUserName(StringUtil.toCamelCase(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFullName()));
                    conversation.setUserId(Long.valueOf(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getUserId()));
                    conversation.setEnUserId(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getEnUserId());
                    if(!search.isGroup()){
                        conversation.setChat_type(2);
                    } else{
                        conversation.setChat_type(4);
                    }

                    conversation.setLabel(search.getName());
                    conversation.setJoined(search.isJoined());
                    chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    chatIntent.putExtra("MESSAGE", message);
                    chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
                    forwardActivity.startActivity(chatIntent);
                    forwardActivity.finish();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvEmail, tvContactIcon;
        private ImageView ivContactIcon;
        private CircleLinearLayout llCircle;
        private LinearLayout llRight;
        private ImageView ivOne, ivTwo, ivThree;
        private RelativeLayout rlOne, rlTwo, rlThree;
        private TextView tvOne, tvTwo, tvThree;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
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
        }
    }

    private void setImageResource(final ImageView imageView, String url, Long userId, String name, TextView textView) {
        imageView.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(url)) {
            textView.setText(FuguUtils.Companion.getFirstCharInUpperCase(name));
            textView.setVisibility(View.VISIBLE);
//            Glide.clear(imageView);
            int value = (int) (userId % 5);
            imageView.setImageDrawable(ContextCompat.getDrawable(forwardActivity, dummyImagesArray2.get(value)));
        } else {
            textView.setVisibility(View.GONE);

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.fugu_ic_channel_icon)
                    .error(R.drawable.fugu_ic_channel_icon)
                    .fitCenter()
                    .priority(Priority.HIGH);

            Glide.with(forwardActivity)
                    .asBitmap()
                    .apply(options)
                    .load(url)
                    .into(imageView);

//            Glide.with(forwardActivity).load(url).asBitmap()
//                    .centerCrop()
//                    .placeholder(ContextCompat.getDrawable(forwardActivity, R.drawable.placeholder))
//                    .error(ContextCompat.getDrawable(forwardActivity, R.drawable.placeholder))
//                    .into(imageView);
        }
    }
}
