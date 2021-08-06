package com.skeleton.mvp.utils;

import android.animation.Animator;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.skeleton.mvp.activity.ChatActivity;
import com.skeleton.mvp.model.Message;

import java.util.ArrayList;

import static com.skeleton.mvp.constant.FuguAppConstant.MESSAGE_DELIVERED;
import static com.skeleton.mvp.constant.FuguAppConstant.MESSAGE_READ;
import static com.skeleton.mvp.constant.FuguAppConstant.MESSAGE_SENT;

public class ExtendedTouchListener implements View.OnTouchListener {

    private View swipeView;
    private View root;
    private Message message;
    private Context context;
    private RecyclerView recyclerView;
    private Boolean vibrate = false;
    private float dX, dY;
    private float initialDx = 0f;
    private float initialDy = 0f;
    private View tvReplies;
    private Long channelId;
    private String label;
    private Integer chatType;
    private Boolean isDeleted;
    private ArrayList<Message> messageList;
    private boolean isMoving = false;
    private View itemView;
    private boolean isThreadOpened = false;

    public ExtendedTouchListener(View itemView, View swipeView, View root, Message message,
                                 Context context, RecyclerView recyclerView, View tvReplies,
                                 Integer chatType, String label, Long channelId, Boolean isDeleted,
                                 ArrayList<Message> messageList) {
        this.itemView = itemView;
        this.swipeView = swipeView;
        this.root = root;
        this.context = context;
        this.message = message;
        this.recyclerView = recyclerView;
        this.tvReplies = tvReplies;
        this.channelId = channelId;
        this.chatType = chatType;
        this.label = label;
        this.isDeleted = isDeleted;
        this.messageList = messageList;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                if (context != null) {
                    if (initialDx == 0) {
                        initialDx = swipeView.getX();
                        initialDy = swipeView.getY();
                    }
                    ((ChatActivity) context).onAdapterItemTouch(MotionEvent.ACTION_DOWN);
                    dX = swipeView.getX() - event.getRawX();
                    dY = swipeView.getY() - event.getRawY();

                    Log.e("Down", dX + "  " + dY);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (context != null) {
                    if (event.getRawX() + dX - initialDx > 50 && (message.getMessageStatus() == MESSAGE_SENT || message.getMessageStatus() == MESSAGE_DELIVERED || message.getMessageStatus() == MESSAGE_READ)) {
                        recyclerView.setLayoutFrozen(true);
                        ((ChatActivity) context).onAdapterItemTouch(MotionEvent.ACTION_MOVE);
                        if ((event.getRawX() + dX - initialDx - 50) > 300) {
                            Log.e("if position", (300 + initialDx) + "");

                            swipeView.animate()
                                    .x(300)
                                    .y(initialDy)
                                    .setDuration(100)
                                    .start();

                        } else {
                            Log.e("Position--->", (event.getRawX() + dX - initialDx - 50) + "");
                            if (event.getRawX() + dX - initialDx - 50 > 150) {
                                if (!vibrate) {
                                    vibrate = true;
                                    Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE));
                                    } else {
                                        //deprecated in API 26
                                        vibrator.vibrate(30);
                                    }
                                }
                            }
                            Log.e("else position", (event.getRawX() + dX + initialDx - 50) + "");
                            swipeView.animate()
                                    .x(event.getRawX() + dX - 50)
                                    .y(initialDy)
                                    .setDuration(0)
                                    .start();

                        }
                    }
                    Log.e("Move", (event.getRawX() + dX) + "  " + (event.getRawX() + dY));
                }
                break;

            case MotionEvent.ACTION_UP:
                if (context != null) {
                    vibrate = false;
                    isMoving = false;
                    isThreadOpened = true;
                    new Handler().postDelayed(() -> ((ChatActivity) context).onAdapterItemTouch(MotionEvent.ACTION_UP), 500);

                    if (event.getRawX() + dX - initialDx - 50 > 150) {
                        swipeView.animate()
                                .x(initialDx)
                                .y(initialDy)
                                .setDuration(100)
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                })
                                .start();

                        new Handler().postDelayed(() -> {
                            swipeView.invalidate();
                            new Handler().postDelayed(() -> {
                                if (message.getMessageStatus() == MESSAGE_SENT || message.getMessageStatus() == MESSAGE_DELIVERED || message.getMessageStatus() == MESSAGE_READ) {
                                    tvReplies.performClick();
                                }
//                                swipeView.clearAnimation();
                            }, 10);
                        }, 100);

                    } else {
                        swipeView.animate()
                                .x(initialDx)
                                .y(initialDy)
                                .setDuration(300)
                                .start();
                    }

                    initialDx = 0;
                    initialDy = 0;
                    recyclerView.setLayoutFrozen(false);
                }
                break;
            default:
                return false;
        }
        return false;
    }


    public interface OnAdapterItemTouchListener {
        void onAdapterItemTouch(int state);
    }

}
