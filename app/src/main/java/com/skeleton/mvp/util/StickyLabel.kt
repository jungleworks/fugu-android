package com.skeleton.mvp.util

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AbsListView
import android.widget.RelativeLayout
import android.widget.TextView
import com.skeleton.mvp.R
import com.skeleton.mvp.model.Message
import com.skeleton.mvp.utils.CustomLinearLayoutManager
import com.skeleton.mvp.utils.DateUtils

class StickyLabel {
    private var TEXT_MESSGAE_SELF: Int = 0
    private var TEXT_MESSGAE_OTHER: Int = 1
    private var IMAGE_MESSGAE_SELF: Int = 2
    private var IMAGE_MESSGAE_OTHER: Int = 3
    private var FILE_MESSGAE_SELF: Int = 4
    private var FILE_MESSGAE_OTHER: Int = 5
    private var HEADER_ITEM = 6
    private var VIDEO_MESSGAE_SELF: Int = 11
    private var VIDEO_MESSGAE_OTHER: Int = 12
    private var VIDEO_CALL_SELF: Int = 13
    private var POLL_SELF: Int = 15
    private var POLL_OTHER: Int = 16
    private var position: Int = 0
    private var previousPos: Int = 0
    private var runAnim = true
    private var runAnim2 = false
    private var handler: android.os.Handler? = null
    private var slideUp: Animation? = null
    private var slideDown: Animation? = null
    private var scrollBottomCount = 0
    private lateinit var dateUtils: DateUtils
    private var messageListGlobal = ArrayList<Message>()
    private var page = 0;

    fun setScrollCount(scrollBottomCount: Int) {
        this.scrollBottomCount = scrollBottomCount
    }

    fun setUpStickyLabel(context: Context, recyclerView: androidx.recyclerview.widget.RecyclerView?, animSlideUp: Animation?, tvDateLabel: TextView?,
                         layoutManager: CustomLinearLayoutManager, messageList: ArrayList<Message>,
                         tvUnread: TextView, scrollManipulation: ScrollManipulation) {
        recyclerView?.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: androidx.recyclerview.widget.RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                dateUtils = DateUtils()
                when (newState) {
                    androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE -> {
                        runAnim2 = false
                        if (handler == null) {
                            handler = android.os.Handler()
                            handler?.postDelayed({
                                if (!runAnim2) {
                                    slideUp = AnimationUtils.loadAnimation(context,
                                            R.anim.fugu_slide_up_time)
                                    tvDateLabel?.startAnimation(animSlideUp)
                                    tvDateLabel?.visibility = View.INVISIBLE
                                    animSlideUp?.setAnimationListener(object : Animation.AnimationListener {
                                        override fun onAnimationStart(animation: Animation) {}

                                        override fun onAnimationEnd(animation: Animation) {
                                            runAnim = true
                                            handler = null
                                        }

                                        override fun onAnimationRepeat(animation: Animation) {

                                        }
                                    })
                                } else {
                                    handler = null
                                }
                            }, 1200)
                        }
                    }
                    androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_SETTLING -> {
                    }
                    AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL -> {
                        tvDateLabel?.clearAnimation()
                        runAnim2 = true
                        if (runAnim) {
                            tvDateLabel?.visibility = View.VISIBLE
                            tvDateLabel?.clearAnimation()
                            slideDown = AnimationUtils.loadAnimation(context,
                                    R.anim.fugu_slide_down_time)
                            tvDateLabel?.startAnimation(slideDown)
                            slideDown?.setAnimationListener(object : Animation.AnimationListener {
                                override fun onAnimationStart(animation: Animation) {
                                    runAnim = false
                                }

                                override fun onAnimationEnd(animation: Animation) {}

                                override fun onAnimationRepeat(animation: Animation) {

                                }
                            })
                        }
                    }
                }
            }

            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                position = layoutManager.findFirstVisibleItemPosition()

//                if (page > 1) {
//                    position = messageListGlobal.size - position
//                }

                if (layoutManager.findLastVisibleItemPosition() == messageListGlobal.size - 2
                        && messageListGlobal.size > 2 && dy < 0) {
                    scrollManipulation.showScroll()
//                    rlScrollBottom.visibility = View.VISIBLE
                    if (scrollBottomCount > 0) {
                        tvUnread.visibility = View.VISIBLE
                    } else {
                        tvUnread.visibility = View.GONE
                    }
                } else if (layoutManager.findLastVisibleItemPosition() == messageListGlobal.size - 1) {
                    scrollManipulation.hideScroll()
//                    rlScrollBottom.visibility = View.GONE
                    tvUnread.visibility = View.GONE
                    scrollBottomCount = 0
                }
                try {
                    if (previousPos > position && messageListGlobal.size != 0) {
                        if (messageListGlobal[position].rowType == TEXT_MESSGAE_SELF
                                || messageListGlobal[position].rowType == TEXT_MESSGAE_OTHER
                                || messageListGlobal[position].rowType == IMAGE_MESSGAE_SELF
                                || messageListGlobal[position].rowType == IMAGE_MESSGAE_OTHER
                                || messageListGlobal[position].rowType == VIDEO_MESSGAE_SELF
                                || messageListGlobal[position].rowType == VIDEO_CALL_SELF
                                || messageListGlobal[position].rowType == POLL_SELF
                                || messageListGlobal[position].rowType == POLL_OTHER
                                || messageListGlobal[position].rowType == VIDEO_MESSGAE_OTHER
                                || messageListGlobal[position].rowType == FILE_MESSGAE_SELF
                                || messageListGlobal[position].rowType == FILE_MESSGAE_OTHER) {
                            if (!TextUtils.isEmpty(messageListGlobal[position].sentAtUtc)) {
                                tvDateLabel?.text =
                                        DateUtils.getDate(dateUtils.convertToLocal(messageListGlobal[position].sentAtUtc))
                            }
                        }
                    } else if (messageListGlobal.size != 0) {
                        try {
                            if (messageListGlobal[position].rowType == HEADER_ITEM) {
                                tvDateLabel?.text = messageListGlobal[position].sentAtUtc
                            }
                        } catch (e: Exception) {

                        }
                    }
                    previousPos = position
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    fun updateMessageList(messageList: ArrayList<Message>) {
        page += 1
        messageListGlobal = ArrayList()
        messageListGlobal.addAll(messageList)
    }


    interface ScrollManipulation {
        fun hideScroll()
        fun showScroll()
    }
}