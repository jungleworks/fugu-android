package com.skeleton.mvp.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.skeleton.mvp.FuguColorConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.MainActivity;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.model.creategroup.MembersInfo;
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo;
import com.skeleton.mvp.fragment.ChannelOptionsDialog;
import com.skeleton.mvp.fugudatabase.CommonData;
import com.skeleton.mvp.model.FuguConversation;
import com.skeleton.mvp.ui.AppConstants;
import com.skeleton.mvp.util.FormatStringUtil;
import com.skeleton.mvp.util.Utils;
import com.skeleton.mvp.utils.DateUtils;
import com.skeleton.mvp.utils.FuguUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static com.skeleton.mvp.constant.FuguAppConstant.CUSTOM_ACTION_MESSAGE;
import static com.skeleton.mvp.constant.FuguAppConstant.FILE_MESSAGE;
import static com.skeleton.mvp.constant.FuguAppConstant.FONT_BOLD;
import static com.skeleton.mvp.constant.FuguAppConstant.FONT_ITALIC;
import static com.skeleton.mvp.constant.FuguAppConstant.FONT_REGULAR;
import static com.skeleton.mvp.constant.FuguAppConstant.FONT_SEMIBOLD;
import static com.skeleton.mvp.constant.FuguAppConstant.IMAGE_MESSAGE;
import static com.skeleton.mvp.constant.FuguAppConstant.MESSAGE_READ;
import static com.skeleton.mvp.constant.FuguAppConstant.MESSAGE_SENT;
import static com.skeleton.mvp.constant.FuguAppConstant.POLL_MESSAGE;
import static com.skeleton.mvp.constant.FuguAppConstant.PUBLIC_NOTE;
import static com.skeleton.mvp.constant.FuguAppConstant.TEXT_MESSAGE;
import static com.skeleton.mvp.constant.FuguAppConstant.VIDEO_CALL;
import static com.skeleton.mvp.constant.FuguAppConstant.VIDEO_MESSAGE;

/**
 * Created by rajatdhamija
 * 22/05/18.
 */

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<FuguConversation> FuguConversationList = new ArrayList<>();
    private Activity activity;
    private FuguColorConfig fuguColorConfig;
    private Callback callback;
    private ArrayList<String> colorsArray = new ArrayList<>();
    private long mLastClickTime = 0;
    private static final int DELETED_MESSAGE = 0;
    Typeface boldFont, normalFont, italicFont, semiBoldFont;
    private Long myUserId = 1L;
    private List<WorkspacesInfo> workspacesInfo;

    public ConversationAdapter(Activity activity, LinkedHashMap<Long, FuguConversation> FuguConversationMap, String userName, Long userId, String businessName, String enUserId, Callback callback) {
        inflater = LayoutInflater.from(activity);
        fuguColorConfig = CommonData.getColorConfig();
        this.activity = activity;
        this.myUserId = userId;
        this.callback = callback;
        boldFont = Typeface.createFromAsset(activity.getAssets(), FONT_BOLD);
        normalFont = Typeface.createFromAsset(activity.getAssets(), FONT_REGULAR);
        semiBoldFont = Typeface.createFromAsset(activity.getAssets(), FONT_SEMIBOLD);
        italicFont = Typeface.createFromAsset(activity.getAssets(), FONT_ITALIC);
        if (com.skeleton.mvp.data.db.CommonData.getCommonResponse() != null && com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData() != null) {
            workspacesInfo = com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo();
        }
        FuguConversationList.clear();
        FuguConversationList = new ArrayList<>(FuguConversationMap.values());
        Collections.sort(FuguConversationList, (one, other) -> other.getDateTime().compareTo(one.getDateTime()));
        Collections.sort(FuguConversationList, (one, other) -> other.getIsPinned().compareTo(one.getIsPinned()));
    }

    public void updateList(LinkedHashMap<Long, FuguConversation> FuguConversationMap, Long userId) {
        try {
            FuguConversationList.clear();
            FuguConversationList = new ArrayList<>(FuguConversationMap.values());
            Collections.sort(FuguConversationList, (one, other) -> other.getDateTime().compareTo(one.getDateTime()));
            Collections.sort(FuguConversationList, (one, other) -> other.getIsPinned().compareTo(one.getIsPinned()));
            myUserId = userId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fugu_item_channels, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();
        final FuguConversation currentChannelItem = FuguConversationList.get(position);
        holder.tvChannelName.setTextColor(fuguColorConfig.getFuguTextColorPrimary());
        holder.viewDivider.setBackgroundColor(fuguColorConfig.getFuguBorderColor());
//        holder.tvMessage.setTextColor(Color.GRAY);
        holder.rlRoot.setBackgroundDrawable(FuguColorConfig
                .makeSelector(fuguColorConfig.getFuguChannelItemBg(), fuguColorConfig.getFuguChannelItemBgPressed()));
        try {
            setChannelImage(currentChannelItem, holder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setChannelName(currentChannelItem, holder);
        setChannelMessage(currentChannelItem, holder);
        setChannelDate(currentChannelItem, holder);
        setChannelMutedStatus(currentChannelItem, holder);
        setChannelPinnedStatus(currentChannelItem, holder);
        setChannelCount(currentChannelItem, holder);
        checkIfMessageIsDeleted(currentChannelItem, holder);
        itemClick(currentChannelItem, holder, position);
        itemLongClick(currentChannelItem, holder, position);
        ViewCompat.setTransitionName(holder.ivChannelIcon, currentChannelItem.getLabel());
        if (position == FuguConversationList.size() - 1) {
            holder.viewDivider.setVisibility(View.GONE);
            holder.rlMain.setPadding(Utils.dpToPx(holder.itemView.getContext(), 0f), Utils.dpToPx(holder.itemView.getContext(), 0f), Utils.dpToPx(holder.itemView.getContext(), 0f), Utils.dpToPx(holder.itemView.getContext(), 60f));
        } else {
            holder.viewDivider.setVisibility(View.GONE);
            holder.rlMain.setPadding(Utils.dpToPx(holder.itemView.getContext(), 0f), Utils.dpToPx(holder.itemView.getContext(), 0f), Utils.dpToPx(holder.itemView.getContext(), 0f), Utils.dpToPx(holder.itemView.getContext(), 0f));
        }
    }

    private void itemLongClick(final FuguConversation currentChannelItem, final MyViewHolder holder, int position) {
        holder.rlRoot.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    final String[] muid = new String[1];
                    muid[0] = currentChannelItem.getMuid();
                    FragmentManager manager = ((MainActivity) activity).getSupportFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();
                    boolean isMuted, isPinned;
                    if (((MainActivity) activity).isNetworkConnected()) {
                        isMuted = !currentChannelItem.getNotifications().equals(FuguAppConstant.NOTIFICATION_LEVEL.ALL_MESSAGES.toString());
                        isPinned = currentChannelItem.getIsPinned() == 1;
                        DialogFragment newFragment = ChannelOptionsDialog.newInstance(0, holder.getAdapterPosition(), muid[0], currentChannelItem.getChannelId(), currentChannelItem.getChat_type(), isMuted, isPinned);
                        newFragment.show(ft, "ReactionFragment");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
    }

    private void itemClick(final FuguConversation currentChannelItem, final MyViewHolder holder, final int position) {
        holder.rlRoot.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            holder.tvMessage.setTypeface(boldFont);
//            holder.tvMessage.setTextColor(Color.GRAY);
            if (currentChannelItem.getMessageState() == DELETED_MESSAGE) {
                holder.tvMessage.setTypeface(italicFont);
            } else {
                holder.tvMessage.setTypeface(boldFont);
            }
            holder.tvMessage.setTextColor(Color.parseColor("#b3bec9"));
            holder.tvDate.setTypeface(normalFont);
            holder.tvChannelName.setTypeface(semiBoldFont);
            holder.circularTvMessageCount.setVisibility(View.GONE);
            notifyItemChanged(position);
            mLastClickTime = SystemClock.elapsedRealtime();
            currentChannelItem.setUnreadCount(0);
            callback.onClick(currentChannelItem);
        });
        holder.rlChannelIcon.setOnClickListener(view -> callback.onIconClick(currentChannelItem));
    }

    /**
     * Check if message is deleted then set deleted message text accordingly other keep normal text
     */
    @SuppressLint("SetTextI18n")
    private void checkIfMessageIsDeleted(FuguConversation currentChannelItem, MyViewHolder holder) {
        if (currentChannelItem.getMessageState() == DELETED_MESSAGE) {
            holder.ivDelete.setVisibility(View.VISIBLE);
            holder.tvMessage.setTypeface(italicFont);
//            holder.tvMessage.setTextColor(activity.getResources().getColor(R.color.deleted_message_color));
            if (myUserId.compareTo(currentChannelItem.getLast_sent_by_id()) == 0) {
                holder.tvMessage.setText("You deleted this message");
            } else {
                holder.tvMessage.setText("This message was deleted");
            }
            holder.ivMessageState.setVisibility(View.GONE);
        } else {
            holder.ivDelete.setVisibility(View.GONE);
//            holder.tvMessage.setTextColor(Color.GRAY);
            setChannelMessage(currentChannelItem, holder);
            setChannelCount(currentChannelItem, holder);
        }
    }

    /**
     * Set muted icon if channel is muted otherwise don't set any icon
     */
    private void setChannelMutedStatus(FuguConversation currentChannelItem, MyViewHolder holder) {
        Drawable mIcon = ContextCompat.getDrawable(activity, R.drawable.notifications_muted);
        mIcon.setColorFilter(ContextCompat.getColor(activity, R.color.cardview_dark_background), PorterDuff.Mode.MULTIPLY);
        holder.ivMute.setImageDrawable(mIcon);
        if (!currentChannelItem.getNotifications().equals(FuguAppConstant.NOTIFICATION_LEVEL.ALL_MESSAGES.toString()) && !currentChannelItem.getNotifications().equals("UNMUTED")) {
            holder.ivMute.setVisibility(View.VISIBLE);
        } else {
            holder.ivMute.setVisibility(View.GONE);
        }
    }

    /**
     * Set pinned icon if channel is muted otherwise don't set any icon
     */
    private void setChannelPinnedStatus(FuguConversation currentChannelItem, MyViewHolder holder) {
        Drawable mIcon = ContextCompat.getDrawable(activity, R.drawable.ic_pin_gray);
        mIcon.setColorFilter(ContextCompat.getColor(activity, R.color.cardview_dark_background), PorterDuff.Mode.MULTIPLY);
        holder.ivPin.setImageDrawable(mIcon);
        if (currentChannelItem.getIsPinned() == 1) {
            holder.ivPin.setVisibility(View.VISIBLE);
        } else {
            holder.ivPin.setVisibility(View.GONE);
        }
    }

    /**
     * set channel count if count > 0 otherwise keep it gone
     */
    @SuppressLint("SetTextI18n")
    private void setChannelCount(final FuguConversation currentChannelItem, MyViewHolder holder) {
        if (currentChannelItem.getUnreadCount() > 0) {
            holder.circularTvMessageCount.setVisibility(View.VISIBLE);
            holder.circularTvMessageCount.setText(currentChannelItem.getUnreadCount() + "");
            holder.tvMessage.setTypeface(normalFont);
            holder.tvMessage.setTextColor(Color.BLACK);
            holder.tvDate.setTypeface(boldFont);
            holder.tvChannelName.setTypeface(boldFont);
        } else {
            holder.circularTvMessageCount.setVisibility(View.GONE);
            holder.tvMessage.setTypeface(boldFont);
            holder.tvMessage.setTextColor(Color.parseColor("#b3bec9"));
            holder.tvDate.setTypeface(normalFont);
            holder.tvChannelName.setTypeface(semiBoldFont);
        }
    }

    /**
     * Set channel messsage last sent time/date
     */
    private void setChannelDate(FuguConversation currentChannelItem, MyViewHolder holder) {
        holder.tvDate.setText(DateUtils.getRelativeDate(DateUtils.getInstance().convertToLocal(currentChannelItem.getDateTime()), true));
    }

    /**
     * Set Channel message (font bold if has any unread message)
     * format(group) --> name: message (if last message is of some other person)
     * you: message (if last message is of self)
     * format(one to one) --> message
     */
    @SuppressLint("SetTextI18n")
    private void setChannelMessage(FuguConversation currentChannelItem, MyViewHolder holder) {
        String name = "";
        try {
            if (currentChannelItem.getLast_sent_by_full_name().length() > 20) {
                name = currentChannelItem.getLast_sent_by_full_name().substring(0, 19) + "...";
            } else {
                name = currentChannelItem.getLast_sent_by_full_name();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (currentChannelItem.getMessage_type()) {
            case TEXT_MESSAGE:
            case CUSTOM_ACTION_MESSAGE:
                String message = currentChannelItem.getMessage().replaceAll("\n", "&nbsp;");
                message = message.replaceAll("<br>", "&nbsp;");
                message = message.replaceAll("</br>", "&nbsp;");
                message = message.replaceAll("<br/>", "&nbsp;");
                message = FormatStringUtil.FormatString.INSTANCE.getFormattedString(message).get(0);
                if (myUserId == null || myUserId == 1L) {
                    myUserId = Long.valueOf(workspacesInfo.get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getUserId());
                }
                if (currentChannelItem.getChat_type() == 2 || currentChannelItem.getChat_type() == 7) {
                    if (currentChannelItem.getLast_sent_by_id().equals(myUserId)) {
                        setMessageState(holder, currentChannelItem.getLast_message_status());
                    } else {
                        holder.ivMessageState.setVisibility(View.GONE);
                    }
                    if (currentChannelItem.getChat_type() == 2 && currentChannelItem.getLast_sent_by_id().equals(myUserId)) {
                        setText(holder, "You: " + Html.fromHtml(message) + "");
                    } else {
                        setText(holder, Html.fromHtml(message) + "");
                    }
                } else {
                    holder.ivMessageState.setVisibility(View.GONE);
                    if (currentChannelItem.getLast_sent_by_id().equals(myUserId)) {
                        setText(holder, Html.fromHtml("You: " + message) + "");
                    } else {
                        if (!TextUtils.isEmpty(name)) {
                            setText(holder, Html.fromHtml(name + ": " + message) + "");
                        } else {
                            setText(holder, Html.fromHtml(message) + "");
                        }
                    }
                }
                break;
            case IMAGE_MESSAGE:
                if (currentChannelItem.getChat_type() == 2 || currentChannelItem.getChat_type() == 7) {
                    if (currentChannelItem.getLast_sent_by_id().equals(myUserId)) {
                        setText(holder, "You: Attachment Image");
                    } else {
                        setText(holder, "Attachment Image");
                    }
                    if (currentChannelItem.getChat_type() == 2 && currentChannelItem.getLast_sent_by_id().equals(myUserId)) {
                        setMessageState(holder, currentChannelItem.getLast_message_status());
                    } else {
                        holder.ivMessageState.setVisibility(View.GONE);
                    }
                } else {
                    holder.ivMessageState.setVisibility(View.GONE);
                    if (currentChannelItem.getLast_sent_by_id().equals(myUserId)) {
                        setText(holder, "You: Attachment Image");
                    } else {
                        setText(holder, name + ": Attachment Image");
                    }
                }
                break;
            case FILE_MESSAGE:
                if (currentChannelItem.getChat_type() == 2 || currentChannelItem.getChat_type() == 7) {
                    if (currentChannelItem.getChat_type() == 2 && currentChannelItem.getLast_sent_by_id().equals(myUserId)) {
                        setText(holder, "You: Attachment File");
                    } else {
                        setText(holder, "Attachment File");
                    }
                    if (currentChannelItem.getLast_sent_by_id().equals(myUserId)) {
                        setMessageState(holder, currentChannelItem.getLast_message_status());
                    } else {
                        holder.ivMessageState.setVisibility(View.GONE);
                    }
                } else {
                    holder.ivMessageState.setVisibility(View.GONE);
                    if (currentChannelItem.getLast_sent_by_id().equals(myUserId)) {
                        setText(holder, "You: Attachment File");
                    } else {
                        setText(holder, name + ": Attachment File");
                    }
                }
                break;
            case PUBLIC_NOTE:
                holder.ivMessageState.setVisibility(View.GONE);
                setText(holder, Html.fromHtml(currentChannelItem.getMessage()) + "");
                break;
            case VIDEO_MESSAGE:
                if (currentChannelItem.getChat_type() == 2 || currentChannelItem.getChat_type() == 7) {
                    if (currentChannelItem.getChat_type() == 2 && currentChannelItem.getLast_sent_by_id().equals(myUserId)) {
                        setText(holder, "You: Attachment Video");
                    } else {
                        setText(holder, "Attachment Video");
                    }
                    if (currentChannelItem.getLast_sent_by_id().equals(myUserId)) {
                        setMessageState(holder, currentChannelItem.getLast_message_status());
                    } else {
                        holder.ivMessageState.setVisibility(View.GONE);
                    }
                } else {
                    holder.ivMessageState.setVisibility(View.GONE);
                    if (currentChannelItem.getLast_sent_by_id().equals(myUserId)) {
                        setText(holder, "You: Attachment Video");
                    } else {
                        setText(holder, name + ": Attachment Video");
                    }
                }
                break;
            case VIDEO_CALL:
                holder.ivMessageState.setVisibility(View.GONE);
                if (currentChannelItem.getLast_sent_by_id().equals(myUserId)) {
                    if (currentChannelItem.getChat_type() == 2 || currentChannelItem.getChat_type() == 7) {
                        if (currentChannelItem.getCallType().equals("VIDEO")) {
                            if (currentChannelItem.getMessageState() == 2) {
                                setText(holder, currentChannelItem.getLabel() + " missed a video call with you.");
                            } else {
                                setText(holder, "The video call ended.");
                            }
                        } else {
                            if (currentChannelItem.getMessageState() == 2) {
                                setText(holder, currentChannelItem.getLabel() + " missed a voice call with you.");
                            } else {
                                setText(holder, "The audio call ended.");
                            }
                        }
                    } else {
                        if (currentChannelItem.getCallType().equals("VIDEO")) {
                            setText(holder, "The video call ended.");
                        } else {
                            setText(holder, "The voice call ended.");
                        }
                    }
                } else {
                    if (currentChannelItem.getChat_type() == 2 || currentChannelItem.getChat_type() == 7) {

                        if (currentChannelItem.getCallType().equals("VIDEO")) {
                            if (currentChannelItem.getMessageState() == 2) {
                                setText(holder, "You missed a video call with " + currentChannelItem.getLabel());
                            } else {
                                setText(holder, "The video call ended.");
                            }
                        } else {
                            if (currentChannelItem.getMessageState() == 2) {
                                setText(holder, "You missed a voice call with " + currentChannelItem.getLabel());
                            } else {
                                setText(holder, "The audio call ended.");
                            }
                        }

                    } else {
                        if (currentChannelItem.getCallType().equals("VIDEO")) {
                            setText(holder, "The video call ended.");
                        } else {
                            setText(holder, "The voice call ended.");
                        }
                    }
                }
                break;
            case POLL_MESSAGE:
                if (!currentChannelItem.getLast_sent_by_id().equals(myUserId)) {
                    setText(holder, currentChannelItem.getLast_sent_by_full_name() + ": Created a Poll");
                    setMessageState(holder, currentChannelItem.getLast_message_status());
                } else {
                    setText(holder, "You: Created a Poll");
                }
                break;
            default:
                holder.ivMessageState.setVisibility(View.GONE);
                holder.tvMessage.setText("You have received/sent a message.");
                break;
        }
    }

    private void setMessageState(MyViewHolder holder, int messageState) {
        holder.ivMessageState.setVisibility(View.GONE);
        if (messageState == MESSAGE_SENT) {
            holder.ivMessageState.setImageResource(R.drawable.ic_single_tick);
        } else if (messageState == MESSAGE_READ) {
            holder.ivMessageState.setImageResource(R.drawable.ic_double_tick);
        } else {
            holder.ivMessageState.setImageResource(R.drawable.ic_single_tick);
        }
    }

    private void setText(MyViewHolder holder, String message) {
        holder.tvMessage.setText(message);
    }

    /**
     * Set Channel name (font bold if has any unread message)
     */
    private void setChannelName(FuguConversation currentChannelItem, MyViewHolder holder) {
        ArrayList<MembersInfo> membersInfo = currentChannelItem.getMembersInfo();
        if (!TextUtils.isEmpty(currentChannelItem.getCustomLabel())) {
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < membersInfo.size(); i++) {
                if (i > 0) {
                    name.append(", ");
                }
                name = new StringBuilder(name.toString().concat(membersInfo.get(i).getFullName()));
            }
            holder.tvChannelName.setText(name.toString());
        } else {
            holder.tvChannelName.setText(currentChannelItem.getLabel());
        }
    }

    /**
     * Set Channel Image if url is not empty else set a background color resource with initial of channel
     */
    private void setChannelImage(FuguConversation currentChannelItem, final MyViewHolder holder) {
        if (TextUtils.isEmpty(currentChannelItem.getThumbnailUrl())) {
            holder.tvChannelIcon.setText(FuguUtils.Companion.getFirstCharInUpperCase(currentChannelItem.getLabel()));
            holder.tvChannelIcon.setVisibility(View.VISIBLE);
            holder.ivChannelIcon.setVisibility(View.VISIBLE);
            int value = (int) (currentChannelItem.getChannelId() % 5);
            holder.ivChannelIcon.setImageDrawable(ContextCompat.getDrawable(activity, AppConstants.dummyImagesArray.get(value)));
            holder.tvChannelIcon.setTextColor(fuguColorConfig.getFuguChannelItemBg());
            holder.llCircle.setVisibility(View.GONE);
        } else {
            if (!currentChannelItem.getThumbnailUrl().equals("https://fuguchat.s3.ap-south-1.amazonaws.com/default/WwX5qYGSEb_1518441286074.png")) {
                holder.ivChannelIcon.setVisibility(View.VISIBLE);
                holder.llCircle.setVisibility(View.GONE);
                holder.tvChannelIcon.setVisibility(View.GONE);

                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.fugu_ic_channel_icon)
                        .error(R.drawable.fugu_ic_channel_icon)
                        .fitCenter()
                        .priority(Priority.HIGH)
                        .transforms(new CenterCrop(), new RoundedCorners(10));
                Glide.with(activity)
                        .asBitmap()
                        .apply(options)
                        .load(currentChannelItem.getThumbnailUrl())
                        .into(holder.ivChannelIcon);
            } else {
                holder.ivChannelIcon.setVisibility(View.GONE);
                holder.llCircle.setVisibility(View.VISIBLE);
                holder.tvChannelIcon.setVisibility(View.GONE);
                ArrayList<MembersInfo> membersInfo = currentChannelItem.getMembersInfo();
                switch (membersInfo.size()) {
                    case 1:
                        holder.rlOne.setVisibility(View.VISIBLE);
                        holder.llRight.setVisibility(View.GONE);
                        holder.rlTwo.setVisibility(View.GONE);
                        holder.rlThree.setVisibility(View.GONE);
                        setImageResource(holder.ivOne, membersInfo.get(0).getUserImage(),
                                membersInfo.get(0).getUserId(),
                                membersInfo.get(0).getFullName(),
                                holder.tvOne);
                        break;
                    case 2:
                        holder.rlOne.setVisibility(View.VISIBLE);
                        holder.rlTwo.setVisibility(View.VISIBLE);
                        holder.llRight.setVisibility(View.VISIBLE);
                        holder.rlThree.setVisibility(View.GONE);
                        setImageResource(holder.ivOne, membersInfo.get(0).getUserImage(),
                                membersInfo.get(0).getUserId(),
                                membersInfo.get(0).getFullName(),
                                holder.tvOne);
                        setImageResource(holder.ivTwo, membersInfo.get(1).getUserImage(),
                                membersInfo.get(1).getUserId(),
                                membersInfo.get(1).getFullName(),
                                holder.tvTwo);
                        break;
                    case 3:
                        holder.rlOne.setVisibility(View.VISIBLE);
                        holder.rlTwo.setVisibility(View.VISIBLE);
                        holder.llRight.setVisibility(View.VISIBLE);
                        holder.rlThree.setVisibility(View.VISIBLE);
                        setImageResource(holder.ivOne, membersInfo.get(0).getUserImage(),
                                membersInfo.get(0).getUserId(),
                                membersInfo.get(0).getFullName(),
                                holder.tvOne);
                        setImageResource(holder.ivTwo, membersInfo.get(1).getUserImage(),
                                membersInfo.get(1).getUserId(),
                                membersInfo.get(1).getFullName(),
                                holder.tvTwo);
                        setImageResource(holder.ivThree, membersInfo.get(2).getUserImage(),
                                membersInfo.get(2).getUserId(),
                                membersInfo.get(2).getFullName(),
                                holder.tvThree);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void setImageResource(final ImageView imageView, String url, Long userId, String name, TextView textView) {
        imageView.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(url)) {
            textView.setText(FuguUtils.Companion.getFirstCharInUpperCase(name));
            textView.setVisibility(View.VISIBLE);
            int value = (int) (userId % 5);
            imageView.setImageDrawable(ContextCompat.getDrawable(activity, AppConstants.dummyImagesArray2.get(value)));
            textView.setTextColor(fuguColorConfig.getFuguChannelItemBg());
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
//                    .transforms(new CenterCrop(), new RoundedCorners(10));

            Glide.with(activity)
                    .asBitmap()
                    .apply(options)
                    .load(url)
                    .into(imageView);
        }
    }

    @Override
    public int getItemCount() {
        return FuguConversationList.size();
    }

    public interface Callback {
        void onClick(FuguConversation fuguConversation);

        void onIconClick(FuguConversation fuguConversation);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rlRoot, rlMain, rlChannelIcon;
        private TextView tvChannelName, tvMessage, tvDate, tvChannelIcon;
        private AppCompatImageView ivChannelIcon, ivMessageState;
        private TextView circularTvMessageCount;
        private View viewDivider;
        private ImageView ivMute, ivDelete, ivPin;
        private LinearLayout llCircle;
        private ImageView ivOne, ivTwo, ivThree;
        private RelativeLayout rlOne, rlTwo, rlThree;
        private TextView tvOne, tvTwo, tvThree;
        private LinearLayout llRight;

        public MyViewHolder(View itemView) {
            super(itemView);
            rlRoot = itemView.findViewById(R.id.rlRoot);
            rlMain = itemView.findViewById(R.id.rlMain);
            rlChannelIcon = itemView.findViewById(R.id.rlChannelIcon);
            tvChannelName = itemView.findViewById(R.id.tvChannelName);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvChannelIcon = itemView.findViewById(R.id.tvChannelIcon);
            ivChannelIcon = itemView.findViewById(R.id.ivChannelIcon);
            ivMessageState = itemView.findViewById(R.id.ivMessageState);
            circularTvMessageCount = itemView.findViewById(R.id.circularTvMessageCount);
            viewDivider = itemView.findViewById(R.id.viewDivider);
            ivMute = itemView.findViewById(R.id.ivMute);
            ivPin = itemView.findViewById(R.id.ivPin);
            ivDelete = itemView.findViewById(R.id.ivDeletedMessage);
            llCircle = itemView.findViewById(R.id.llCircle);
            ivOne = itemView.findViewById(R.id.ivOne);
            ivTwo = itemView.findViewById(R.id.ivTwo);
            ivThree = itemView.findViewById(R.id.ivThree);
            llRight = itemView.findViewById(R.id.llRight);
            rlOne = itemView.findViewById(R.id.rlOne);
            rlTwo = itemView.findViewById(R.id.rlTwo);
            rlThree = itemView.findViewById(R.id.rlThree);
            tvOne = itemView.findViewById(R.id.tvOne);
            tvTwo = itemView.findViewById(R.id.tvTwo);
            tvThree = itemView.findViewById(R.id.tvThree);
            llCircle.setClipToOutline(true);
        }
    }
}
