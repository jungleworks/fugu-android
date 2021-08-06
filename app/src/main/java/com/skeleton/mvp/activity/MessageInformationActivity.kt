package com.skeleton.mvp.activity

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.TextUtils
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.FuguColorConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.adapter.MessageInformationAdapter
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.data.network.RestClient
import com.skeleton.mvp.model.Message
import com.skeleton.mvp.model.seenBy.SeenBy
import com.skeleton.mvp.retrofit.APIError
import com.skeleton.mvp.retrofit.CommonParams
import com.skeleton.mvp.retrofit.ResponseResolver
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.utils.EndlessScrolling

class MessageInformationActivity : BaseActivity() {

    private var workspacesInfo: WorkspacesInfo? = null
    private var currentPosition = 0
    private var channelId = -1L
    private var message: Message? = null
    private var isThreadMessage = false
    private var tvMsg: AppCompatTextView? = null
    private var messageList = ArrayList<Message>()
    private var rvSeenBy: androidx.recyclerview.widget.RecyclerView? = null
    private var messageInformationAdapter: MessageInformationAdapter? = null
    private var isCalculationNeeded = false
    private var countDown: CountDownTimer? = null
    private var pageStart: Int = 0
    private var pageSize: Int = 0
    private var swipeRefresh: SwipeRefreshLayout? = null
    private var endlessScrollListerner: EndlessScrolling? = null
    private var ivBack: AppCompatImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_message_info)
        super.onCreate(savedInstanceState)
        tvMsg = findViewById(R.id.tvMsg)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        ivBack = findViewById(R.id.ivBack)
        currentPosition = CommonData.getCurrentSignedInPosition()
        workspacesInfo = CommonData.getCommonResponse().data.workspacesInfo.get(currentPosition)
        swipeRefresh?.setColorSchemeColors(FuguColorConfig().fuguThemeColorPrimary)
        fetchIntentData()
        if (isNetworkConnected) {
//            showLoading()
            apiGetMessageSeenBy(true)


        }
        setUpRecycler()
        swipeRefresh?.setOnRefreshListener {
            pageStart = 0
            apiGetMessageSeenBy(false)
        }
        ivBack?.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setEndlessScrolling() {
        if (endlessScrollListerner == null) {
            endlessScrollListerner = object : EndlessScrolling(rvSeenBy?.layoutManager as androidx.recyclerview.widget.LinearLayoutManager) {
                override fun onLoadMore(currentPages: Int) {
                    pageStart += pageSize
                    apiGetMessageSeenBy(false)
                }

                override fun onHide() {

                }

                override fun onShow() {

                }
            }
            rvSeenBy?.addOnScrollListener(endlessScrollListerner!!)
        }
    }

    private fun setUpRecycler() {

        if (message?.rowType == 0 || (message?.rowType == 2 && !TextUtils.isEmpty(message?.message))) {
            isCalculationNeeded = true
        } else {
            rvSeenBy?.visibility = View.VISIBLE
        }

        rvSeenBy = findViewById(R.id.rvSeenBy)
        rvSeenBy?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        messageInformationAdapter = MessageInformationAdapter(isCalculationNeeded, messageList, this, workspacesInfo!!, currentPosition, channelId, workspacesInfo!!.fullName, workspacesInfo!!.userImage, workspacesInfo!!.userId.toLong())
        rvSeenBy?.itemAnimator = null
        rvSeenBy?.adapter = messageInformationAdapter
    }

    private fun fetchIntentData() {
        if (intent != null) {
            message = intent!!.getSerializableExtra(MESSAGE) as Message
            channelId = intent.getLongExtra(CHANNEL_ID, -1L)
            isThreadMessage = intent.getBooleanExtra(IS_THREAD_MESSAGE, false)
        }
        messageList.add(message!!)
        val seenByMessage = Message()
        seenByMessage.rowType = 102
        messageList.add(seenByMessage)
    }

    private fun apiGetMessageSeenBy(isScrolling: Boolean) {
        val commonParams = CommonParams.Builder()
        if (!isThreadMessage) {
            commonParams.add(MESSAGE_UNIQUE_ID, message!!.muid)
        } else {
            commonParams.add("thread_muid", message!!.muid)
        }
        commonParams.add(PAGE_START, pageStart)
        commonParams.add(EN_USER_ID, workspacesInfo?.enUserId)
        commonParams.add(CHANNEL_ID, channelId)
        RestClient.getApiInterface(true).getMessageSeenBy(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), workspacesInfo?.fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                .enqueue(object : ResponseResolver<SeenBy>() {
                    override fun success(seenBy: SeenBy?) {
                        if (seenBy != null) {
                            if (seenBy.statusCode == 204 || (seenBy.statusCode == 200 && seenBy.data?.messageSeenBy?.size == 0)) {
                                try {
                                    messageInformationAdapter?.updateMessageText(seenBy.data?.message!!)
                                } catch (e: Exception) {
                                    messageInformationAdapter?.updateMessageText("No message info found!")
                                }
                            }

                            swipeRefresh?.isRefreshing = false
                            pageSize = seenBy.data?.pageSize!!
                            hideLoading()
                            if (pageStart == 0) {
                                messageList = ArrayList()
                                messageList.add(message!!)
                                val seenByMessage = Message()
                                seenByMessage.rowType = 102
                                seenByMessage.seenByCount = seenBy.data.seenByCount
                                messageList.add(seenByMessage)
                            }
                            for (user in seenBy.data?.messageSeenBy!!) {
                                val message = Message()
                                message.thumbnailUrl = user.userThumbnailImage
                                message.seenBy = user.fullName
                                message.sentAtUtc = user.seenAt
                                message.userId = user.userId
                                message.rowType = 101
                                message.role = user.role
                                messageList.add(message)
                            }
                            messageInformationAdapter?.updateList(messageList)
                            messageInformationAdapter?.checkSeenBy(true)
                            messageInformationAdapter?.notifyDataSetChanged()
                            setEndlessScrolling()
                            Handler().postDelayed({
                                if (isScrolling) {
                                    messageInformationAdapter?.checkIfScrollNeeded()
                                }
                            }, 300)

                        }
                    }
                    override fun failure(error: APIError?) {
                        hideLoading()
                    }

                })
    }


    fun scrollToBottom(height: Int) {
        rvSeenBy?.post {
            countDown?.cancel()
            countDown = object : CountDownTimer(200, 100) {
                override fun onFinish() {
                    rvSeenBy?.smoothScrollBy(0, height - 420)
                    rvSeenBy?.visibility = View.VISIBLE
                }

                override fun onTick(millisUntilFinished: Long) {

                }

            }.start()
        }
    }
}