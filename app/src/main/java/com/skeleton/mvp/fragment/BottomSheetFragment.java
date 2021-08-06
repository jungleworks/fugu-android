package com.skeleton.mvp.fragment;

/**
 * Created by rajatdhamija
 * 19/04/18.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.ChatActivity;
import com.skeleton.mvp.adapter.BottomSheetEmojiAdapter;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo;
import com.skeleton.mvp.model.Emoji;
import com.skeleton.mvp.utils.DateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static com.skeleton.mvp.constant.FuguAppConstant.FILE_MESSAGE;
import static com.skeleton.mvp.constant.FuguAppConstant.IMAGE_MESSAGE;
import static com.skeleton.mvp.constant.FuguAppConstant.MESSAGE_DELIVERED;
import static com.skeleton.mvp.constant.FuguAppConstant.MESSAGE_READ;
import static com.skeleton.mvp.constant.FuguAppConstant.MESSAGE_SENT;
import static com.skeleton.mvp.constant.FuguAppConstant.VIDEO_MESSAGE;


public class BottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    private LinearLayout copy, react, reply, delete, email, star, edit, forward, info;
    private ChatActivity fuguChatActivity;
    private int position;
    private String Muid;
    private int messsagetype;
    private boolean IsSelf;
    private int MessageStatus;
    private String SentAtUtc;
    private TextView tvDelete;
    private int chatType;
    private ArrayList<String> emojiMap = new ArrayList<>();
    private View viewDivider;

    private TextView tvStar;
    private int isStarred = 0;
    private int[] location;
    private String downloadStatus;
    private GridLayoutManager gridLayoutManager;

    public BottomSheetFragment() {

    }

    public static BottomSheetFragment newInstance(int arg, int pos, String muid, int messageType,
                                                  boolean isSelf, int messsageStatus, String sentAtUtc, int isStarred, int[] location, String downloadStatus, int chatType) {
        BottomSheetFragment frag = new BottomSheetFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        frag.setPostion(pos);
        frag.setMuid(muid);
        frag.setIsSelf(isSelf);
        frag.setMessageType(messageType);
        frag.setMessageStatus(messsageStatus);
        frag.setSentAtUtc(sentAtUtc);
        frag.setIsStarred(isStarred);
        frag.setLocation(location);
        frag.setDownloadStatus(downloadStatus);
        frag.setChatType(chatType);
        return frag;
    }

    private void setChatType(int chatType) {
        this.chatType = chatType;
    }

    private void setDownloadStatus(String downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    private void setSentAtUtc(String sentAtUtc) {
        SentAtUtc = sentAtUtc;
    }

    private void setMessageStatus(int messsageStatus) {
        MessageStatus = messsageStatus;
    }

    private void setIsSelf(boolean isSelf) {
        IsSelf = isSelf;
    }

    private void setMessageType(int messageType) {
        messsagetype = messageType;
    }

    private void setMuid(String muid) {
        Muid = muid;
    }

    private void setPostion(int pos) {
        position = pos;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        new BottomSheetDialog(requireContext(), getTheme());
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);
        fuguChatActivity = (ChatActivity) getActivity();

        RecyclerView rvEmoji = view.findViewById(R.id.rvEmoji);
        BottomSheetEmojiAdapter emojiAdapter = new BottomSheetEmojiAdapter(loadJSONFromAsset(), getActivity(), Muid);
        gridLayoutManager = new GridLayoutManager(getActivity(), 7) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rvEmoji.setLayoutManager(gridLayoutManager);
        rvEmoji.setAdapter(emojiAdapter);
        ViewCompat.setNestedScrollingEnabled(rvEmoji, false);
        copy = view.findViewById(R.id.copy);
        react = view.findViewById(R.id.react);
        reply = view.findViewById(R.id.reply);
        delete = view.findViewById(R.id.delete);
        email = view.findViewById(R.id.email);
        edit = view.findViewById(R.id.edit);
        forward = view.findViewById(R.id.forward);
        info = view.findViewById(R.id.info);
        viewDivider=view.findViewById(R.id.viewDivider);

        star = view.findViewById(R.id.star);
        tvDelete = view.findViewById(R.id.tvDelete);
        tvStar = view.findViewById(R.id.tvStar);
        if (messsagetype == 1) {
            copy.setVisibility(View.VISIBLE);
        } else {
            copy.setVisibility(View.GONE);
        }
        String localDate = DateUtils.getFormattedDate(new Date());
        int newTime = DateUtils.getTimeInMinutes(DateUtils.getInstance().convertToUTC(localDate));
        int oldTime = DateUtils.getTimeInMinutes(SentAtUtc);

        if (IsSelf && messsagetype != 15) {
            email.setVisibility(View.GONE);
        } else {
            email.setVisibility(View.GONE);
        }
        WorkspacesInfo workspacesInfo = CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition());
        if ((messsagetype == 1 || messsagetype == 10) && IsSelf && ((Math.abs(newTime - oldTime)) < (workspacesInfo.getConfig().getEditMessageDuration()) / 60
                || workspacesInfo.getConfig().getEditMessageDuration() == 0)
                && workspacesInfo.getConfig().getEditMessage() == 1) {
            String roles = workspacesInfo.getConfig().getEditMessageRole();
            roles = roles.replace("[", "");
            roles = roles.replace("]", "");
            roles = roles.replaceAll("\"", "");
            String[] rolesArray = roles.split(",");
            ArrayList<String> rolesList = new ArrayList<>(Arrays.asList(rolesArray));
            String presentRole = workspacesInfo.getRole();
            if (rolesList.contains(presentRole)) {
                edit.setVisibility(View.VISIBLE);
            } else {
                edit.setVisibility(View.GONE);
            }
        } else {
            edit.setVisibility(View.GONE);
        }

        if ((messsagetype == IMAGE_MESSAGE || messsagetype == VIDEO_MESSAGE || messsagetype == FILE_MESSAGE)
                && downloadStatus.equals(FuguAppConstant.DownloadStatus.DOWNLOAD_COMPLETED.toString())) {
            forward.setVisibility(View.VISIBLE);
        } else {
            forward.setVisibility(View.GONE);
        }

        if (IsSelf && ((Math.abs(newTime - oldTime)) < (workspacesInfo.getConfig().getDeleteMessageDuration()) / 60
                || workspacesInfo.getConfig().getDeleteMessageDuration() == 0)
                && workspacesInfo.getConfig().getDeleteMessage() == 1) {
            String roles = workspacesInfo.getConfig().getDeleteMessageRole();
            roles = roles.replace("[", "");
            roles = roles.replace("]", "");
            roles = roles.replaceAll("\"", "");
            String[] rolesArray = roles.split(",");
            ArrayList<String> rolesList = new ArrayList<>(Arrays.asList(rolesArray));
            String presentRole = workspacesInfo.getRole();
            if (rolesList.contains(presentRole)) {
                if (messsagetype == 15) {
                    tvDelete.setText("Delete Poll");
                } else {
                    tvDelete.setText("Delete Message");
                }
                delete.setVisibility(View.VISIBLE);
            } else {
                delete.setVisibility(View.GONE);
            }
        } else {
            delete.setVisibility(View.GONE);
        }
        if ((MessageStatus == MESSAGE_SENT || MessageStatus == MESSAGE_DELIVERED || MessageStatus == MESSAGE_READ) && IsSelf && (chatType == FuguAppConstant.ChatType.O2O || chatType == FuguAppConstant.ChatType.PUBLIC_GROUP || chatType == FuguAppConstant.ChatType.PRIVATE_GROUP)) {
            info.setVisibility(View.VISIBLE);
        } else {
            info.setVisibility(View.GONE);
        }

        if ((MessageStatus == MESSAGE_SENT || MessageStatus == MESSAGE_DELIVERED || MessageStatus == MESSAGE_READ) && messsagetype != 15) {
            rvEmoji.setVisibility(View.VISIBLE);
            viewDivider.setVisibility(View.VISIBLE);
            react.setVisibility(View.VISIBLE);
            reply.setVisibility(View.VISIBLE);
        } else {
            viewDivider.setVisibility(View.GONE);
            rvEmoji.setVisibility(View.GONE);
            react.setVisibility(View.GONE);
            reply.setVisibility(View.GONE);
        }

        if (messsagetype != 15) {
            if (isStarred == 1) {
                tvStar.setText("Unstar Message");
            } else {
                tvStar.setText("Star Message");
            }
            star.setVisibility(View.VISIBLE);
        } else {
            star.setVisibility(View.GONE);
        }

        if (MessageStatus == MESSAGE_SENT || MessageStatus == MESSAGE_DELIVERED || MessageStatus == MESSAGE_READ) {
            if (messsagetype != 15) {
                star.setVisibility(View.VISIBLE);
                if (isStarred == 1) {
                    tvStar.setText("Unstar Message");
                } else {
                    tvStar.setText("Star Message");
                }
            } else {
                star.setVisibility(View.GONE);
            }

            if (edit.getVisibility() == View.VISIBLE) {
                edit.setVisibility(View.VISIBLE);
            } else {
                edit.setVisibility(View.GONE);
            }
        } else {
            star.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
        }

        if (messsagetype == 15 && delete.getVisibility() == View.GONE && info.getVisibility() == View.GONE) {
            dismiss();
        }

        copy.setOnClickListener(this);
        reply.setOnClickListener(this);
        react.setOnClickListener(this);
        delete.setOnClickListener(this);
        email.setOnClickListener(this);
        star.setOnClickListener(this);
        edit.setOnClickListener(this);
        forward.setOnClickListener(this);
        info.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {

            case R.id.info:
                fuguChatActivity.openMessageInfo(false, Muid);
                break;

            case R.id.react:
                if (fuguChatActivity.isNetworkConnected()) {
                    fuguChatActivity.openDialog(Muid);
                } else {
                    new AlertDialog.Builder(fuguChatActivity)
                            .setMessage("Please check your Internet connection and try again.")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                }
                break;
            case R.id.reply:
                if (fuguChatActivity.isNetworkConnected()) {
                    try {
                        fuguChatActivity.openThread(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    new AlertDialog.Builder(fuguChatActivity)
                            .setMessage("Please check your Internet connection and try again.")
                            .setPositiveButton("ok", (dialog, which) -> {
                            }).show();
                }
                break;
            case R.id.edit:
                fuguChatActivity.editText(position);
                break;
            case R.id.copy:
                fuguChatActivity.copyText(position);
                Toast.makeText(getActivity(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                break;
            case R.id.email:
                break;
            case R.id.star:
                fuguChatActivity.starMessage(position, Muid, isStarred, location);
                break;
            case R.id.forward:
                fuguChatActivity.forwardMessage(position, Muid);
                break;
            case R.id.delete:
                if (fuguChatActivity.isNetworkConnected() || !(MessageStatus == MESSAGE_SENT || MessageStatus == MESSAGE_DELIVERED || MessageStatus == MESSAGE_READ)) {
                    new AlertDialog.Builder(fuguChatActivity)
                            .setMessage("Are you sure you want to delete this message.")
                            .setPositiveButton("Delete for everyone", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    fuguChatActivity.deleteMessage(position, Muid, MessageStatus);
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();

                } else {
                    new AlertDialog.Builder(fuguChatActivity)
                            .setMessage("Please check your Internet connection and try again.")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                }
                break;
        }
    }

    public ArrayList<Emoji> loadJSONFromAsset() {
        ArrayList<Emoji> emojiList = new ArrayList<>();
        emojiMap = CommonData.getEmojiMap();
        for (int i = emojiMap.size() - 1; i > -0; i--) {
            emojiList.add(new Emoji(emojiMap.get(i), emojiMap.get(i)));
        }
        return emojiList;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (fuguChatActivity != null) {
            fuguChatActivity.setOnLongClickValue();
        }
        super.onCancel(dialog);
    }

    public void setIsStarred(int isStarred) {
        this.isStarred = isStarred;
    }

    public void setLocation(int[] location) {
        this.location = location;
    }
}
