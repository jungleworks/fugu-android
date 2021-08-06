@file:Suppress("NAME_SHADOWING")

package com.skeleton.mvp.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.adapter.PollOptionsAdapter
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.model.Message
import com.skeleton.mvp.model.User
import com.skeleton.mvp.socket.SocketConnection
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.util.AppConstant.MESSAGE
import com.skeleton.mvp.utils.DateUtils
import com.skeleton.mvp.utils.UniqueIMEIID
import io.socket.client.Socket
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class PollDetailsActivity : BaseActivity(), PollOptionsAdapter.OnTotalVotesChanged {
    private var rvPollOptions: androidx.recyclerview.widget.RecyclerView? = null
    private var pollOptionsAdapter: PollOptionsAdapter? = null
    private var message: Message? = null
    private var optionsList = ArrayList<PollOptionsAdapter.Option>()
    private var tvQuestion: AppCompatTextView? = null
    private var tvTotalVotes: AppCompatTextView? = null
    private var tvPollExpiry: AppCompatTextView? = null
    private var tvUsername: AppCompatTextView? = null
    private var userId: Long? = null
    private var channelId: Long? = null

    //    private var mClient: FayeClient? = null
    private var userName: String = ""
    private var userImage: String = ""
    private var ivBack: AppCompatImageView? = null
    private var expireTime: Long? = null
    private var dateTime: String = ""
    private var fromName: String = ""
    private var isExpied: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poll_details)
        initViews()
        fetchIntentData()
        setData()
        setUpFayeConnection()
    }

    private fun setUpFayeConnection() {
//        NotificationSockets.init(applicationContext, false) // Changed activity context to application context to avoid memory leak
        SocketConnection.initSocketConnection(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().userInfo.accessToken,
                com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo.get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).enUserId,
                com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.userId,
                com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.userChannel, "Poll", false,
                com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.pushToken)


        SocketConnection.setSocketListeners(object : SocketConnection.SocketClientCallback {
            override fun onCalling(messageJson: String) {}

            override fun onPresent(messageJson: String) {}

            override fun onMessageSent(messageJson: String) {}

            override fun onThreadMessageSent(messageJson: String) {}

            override fun onMessageReceived(messageJson: String) {}

            override fun onTypingStarted(messageJson: String) {}

            override fun onTypingStopped(messageJson: String) {}

            override fun onThreadMessageReceived(messageJson: String) {}

            override fun onReadAll(messageJson: String) {}

            override fun onPinChat(messageJson: String) {}

            override fun onUnpinChat(messageJson: String) {}

            override fun onPollVoteReceived(messageJson: String) {

                try {
                    val messageJson = JSONObject(messageJson)
                    if (messageJson.getString(CHANNEL_ID).equals(channelId?.toString()) && messageJson.has(MESSAGE_TYPE) && messageJson.getInt(MESSAGE_TYPE) == POLL_MESSAGE
                            && messageJson.getString(MESSAGE_UNIQUE_ID).equals(message?.muid)
                            && messageJson.getLong(USER_ID).compareTo(userId!!) != 0) {
                        val user = User()
                        user.userId = messageJson.getLong(USER_ID)
                        user.fullName = messageJson.getString(FULL_NAME)
                        user.userImage = messageJson.getString("user_image")

                        for (i in 0 until optionsList.size) {
                            if (message?.pollOptions!![i].puid.equals(messageJson.getString("puid"))) {
                                if (message?.multipleSelect!!) {
                                    if (optionsList[i].voteMap!![messageJson.getLong(USER_ID)] == null) {
                                        optionsList[i].voteMap!!.put(messageJson.getLong(USER_ID), user)
                                        optionsList[i].users.add(user)
                                        message?.total_votes = message?.total_votes!! + 1
                                        break
                                    } else {
                                        optionsList[i].voteMap!!.remove(messageJson.getLong(USER_ID))
                                        for (j in 0..optionsList[i].users.size - 1) {
                                            if (optionsList[i].users[j].userId.compareTo(messageJson.getLong(USER_ID)) == 0) {
                                                optionsList[i].users.removeAt(j)
                                                break
                                            }
                                        }
                                        message?.total_votes = message?.total_votes!! - 1
                                    }
                                } else {
                                    if (optionsList[i].voteMap!![messageJson.getLong(USER_ID)] == null) {
                                        optionsList[i].voteMap!!.put(messageJson.getLong(USER_ID), user)
                                        optionsList[i].users.add(user)
                                        message?.total_votes = message?.total_votes!! + 1
                                    }
                                }
                            } else {
                                if (!message?.multipleSelect!!) {
                                    if (optionsList[i].voteMap!![messageJson.getLong(USER_ID)] != null) {
                                        optionsList[i].voteMap!!.remove(messageJson.getLong(USER_ID))
                                        for (j in 0..optionsList[i].users.size - 1) {
                                            if (optionsList[i].users[j].userId.compareTo(messageJson.getLong(USER_ID)) == 0) {
                                                optionsList[i].users.removeAt(j)
                                                break
                                            }
                                        }
                                        message?.total_votes = message?.total_votes!! - 1
                                    }
                                }
                            }
                        }
                        runOnUiThread {
                            tvTotalVotes?.text = "Total Votes: " + message?.total_votes
                            pollOptionsAdapter = PollOptionsAdapter(this@PollDetailsActivity, optionsList, message?.multipleSelect!!, userId!!, message?.total_votes!!, userName, userImage, isExpied)
                            rvPollOptions?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@PollDetailsActivity)
                            rvPollOptions?.itemAnimator = null
                            rvPollOptions?.adapter = pollOptionsAdapter
                            rvPollOptions?.isNestedScrollingEnabled = false
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onReactionReceived(messageJson: String) {
            }

            override fun onVideoCallReceived(messageJson: String) {
            }

            override fun onAudioCallReceived(messageJson: String) {
            }

            override fun onChannelSubscribed() {
            }

            override fun onConnect() {
            }

            override fun onDisconnect() {
            }

            override fun onConnectError(socket: Socket, message: String) {
            }

            override fun onErrorReceived(messageJson: String) {
            }

            override fun onTaskAssigned(messageJson: String) {}

            override fun onMeetScheduled(messageJson: String) {}

            override fun onUpdateNotificationCount(messageJson: String) {}

        })
        SocketConnection.subscribeChannel(channelId!!)


//        FuguConfig.getClient { fayeClient ->
//            mClient = fayeClient
//            mClient?.connectServer()
//            var messageJson: JSONObject?
//            mClient?.listener = object : FayeClientListener {
//                override fun onConnectedServer(fc: FayeClient?) {
//                    Handler().postDelayed({
//                        fc?.subscribeChannel("/" + channelId)
//                    }, 1000)
//                }
//
//                override fun onDisconnectedServer(fc: FayeClient?) {
//                }
//
//                override fun onReceivedMessage(fc: FayeClient?, msg: String?, channel: String?) {
//                    val mngr = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//                    val taskList = mngr.getRunningTasks(10)
//                    try {
//                        messageJson = JSONObject(msg)
//                        if (taskList[0].topActivity.className == "com.skeleton.mvp.activity.PollDetailsActivity"
//                                && channel?.equals("/" + channelId)!! && messageJson!!.has(MESSAGE_TYPE) && messageJson!!.getInt(MESSAGE_TYPE) == POLL_MESSAGE
//                                && messageJson!!.getString(MESSAGE_UNIQUE_ID).equals(message?.muid)
//                                && messageJson!!.getLong(USER_ID).compareTo(userId!!) != 0) {
//                            val user = User()
//                            user.userId = messageJson!!.getLong(USER_ID)
//                            user.fullName = messageJson!!.getString(FULL_NAME)
//                            user.userImage = messageJson!!.getString("user_image")
//
//                            for (i in 0..optionsList.size - 1) {
//                                if (message?.pollOptions!![i].puid.equals(messageJson!!.getString("puid"))) {
//                                    if (message?.multipleSelect!!) {
//                                        if (optionsList[i].voteMap!![messageJson!!.getLong(USER_ID)] == null) {
//                                            optionsList[i].voteMap!!.put(messageJson!!.getLong(USER_ID), user)
//                                            optionsList[i].users.add(user)
//                                            message?.total_votes = message?.total_votes!! + 1
//                                            break
//                                        } else {
//                                            optionsList[i].voteMap!!.remove(messageJson!!.getLong(USER_ID))
//                                            for (j in 0..optionsList[i].users.size - 1) {
//                                                if (optionsList[i].users[j].userId.compareTo(messageJson!!.getLong(USER_ID)) == 0) {
//                                                    optionsList[i].users.removeAt(j)
//                                                    break
//                                                }
//                                            }
//                                            message?.total_votes = message?.total_votes!! - 1
//                                        }
//                                    } else {
//                                        if (optionsList[i].voteMap!![messageJson!!.getLong(USER_ID)] == null) {
//                                            optionsList[i].voteMap!!.put(messageJson!!.getLong(USER_ID), user)
//                                            optionsList[i].users.add(user)
//                                            message?.total_votes = message?.total_votes!! + 1
//                                        }
//                                    }
//                                } else {
//                                    if (!message?.multipleSelect!!) {
//                                        if (optionsList[i].voteMap!![messageJson!!.getLong(USER_ID)] != null) {
//                                            optionsList[i].voteMap!!.remove(messageJson!!.getLong(USER_ID))
//                                            for (j in 0..optionsList[i].users.size - 1) {
//                                                if (optionsList[i].users[j].userId.compareTo(messageJson!!.getLong(USER_ID)) == 0) {
//                                                    optionsList[i].users.removeAt(j)
//                                                    break
//                                                }
//                                            }
//                                            message?.total_votes = message?.total_votes!! - 1
//                                        }
//                                    }
//                                }
//                            }
//                            runOnUiThread {
//                                tvTotalVotes?.text = "Total Votes: " + message?.total_votes
//                                pollOptionsAdapter = PollOptionsAdapter(this@PollDetailsActivity, optionsList, message?.multipleSelect!!, userId!!, message?.total_votes!!, userName, userImage, isExpied)
//                                rvPollOptions?.layoutManager = LinearLayoutManager(this@PollDetailsActivity)
//                                rvPollOptions?.itemAnimator = null
//                                rvPollOptions?.adapter = pollOptionsAdapter
//                            }
//
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//
//                override fun onErrorReceived(fc: FayeClient?, msg: String?, channel: String?) {
//                }
//
//            }
//
//        }
    }

    private fun initViews() {
        rvPollOptions = findViewById(R.id.rvPollOptions)
        tvQuestion = findViewById(R.id.tvQuestion)
        tvTotalVotes = findViewById(R.id.tvTotalVotes)
        ivBack = findViewById(R.id.ivBack)
        tvPollExpiry = findViewById(R.id.tvPollExpiry)
        tvUsername = findViewById(R.id.tvUserName)
        ivBack?.setOnClickListener {
            onBackPressed()
        }
    }

    private fun fetchIntentData() {
        message = intent.getSerializableExtra(MESSAGE) as Message
        userId = intent.getLongExtra(USER_ID, -1L)
        channelId = intent.getLongExtra(CHANNEL_ID, -1L)
        userImage = intent.getStringExtra(USER_IMAGE) ?: ""
        userName = intent.getStringExtra(FULL_NAME) ?: ""
        expireTime = intent.getLongExtra("expire_time", -1L)
        dateTime = intent.getStringExtra(DATE_TIME) ?: ""
        fromName = intent.getStringExtra("from_name") ?: ""
        tvUsername?.text = fromName
        isExpied = checkIfExpired(dateTime, expireTime!!)
        val formatter = SimpleDateFormat("dd MMM, hh:mm a")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = TimeInMillis(dateTime, expireTime!!)
        if (isExpied) {
            tvPollExpiry?.text = "Poll Expired"
            tvPollExpiry?.setTextColor(resources.getColor(R.color.red))
        } else {
            tvPollExpiry?.text = "Poll Expiry: " + formatter.format(calendar.time)
            tvPollExpiry?.setTextColor(resources.getColor(R.color.black))
        }
    }

    private fun TimeInMillis(dateTime: String, expireTime: Long): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        var timeInMilliseconds = 0L
        try {
            val mDate = sdf.parse(DateUtils.getInstance().convertToLocal(dateTime))
            timeInMilliseconds = mDate.time
            timeInMilliseconds += expireTime * 1000L
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return timeInMilliseconds
    }

    private fun checkIfExpired(dateTime: String, expireTime: Long): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        var timeInMilliseconds = 0L
        try {
            val mDate = sdf.parse(DateUtils.getInstance().convertToLocal(dateTime))
            timeInMilliseconds = mDate.time
            timeInMilliseconds += expireTime * 1000L
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val calendar = Calendar.getInstance()
        calendar.time = Date()
        return (timeInMilliseconds.compareTo(calendar.timeInMillis)) < 0
    }

    @SuppressLint("SetTextI18n")
    private fun setData() {
        tvQuestion?.text = message?.question
        tvTotalVotes?.text = "Total Votes: " + message?.total_votes

        for (i in 0..message?.pollOptions?.size!! - 1) {
            optionsList.add(PollOptionsAdapter.Option(message?.pollOptions!![i]?.puid,
                    message?.pollOptions!![i]?.label,
                    message?.pollOptions!![i]?.voteMap,
                    message?.pollOptions!![i]?.users as ArrayList<User>))
        }

        if (optionsList.size > 0) {
            Collections.sort(optionsList, { one, other -> other.users.size.compareTo(one.users.size) })
        }
        pollOptionsAdapter = PollOptionsAdapter(this, optionsList, message?.multipleSelect!!, userId!!, message?.total_votes!!, userName, userImage, isExpied)
        rvPollOptions?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        rvPollOptions?.itemAnimator = null
        rvPollOptions?.adapter = pollOptionsAdapter
        rvPollOptions?.isNestedScrollingEnabled = false

    }

    @SuppressLint("SetTextI18n")
    override fun onTotalVotesChanged(totalVotes: Int, voteMap: HashMap<Long, User>, pos: Int, jsonObject: JSONObject, optionsList: ArrayList<PollOptionsAdapter.Option>) {
        tvTotalVotes?.text = "Total Votes: " + totalVotes
        message?.total_votes = totalVotes
        message?.pollOptions!![pos].voteMap = voteMap
        pollOptionsAdapter = PollOptionsAdapter(this, optionsList, message?.multipleSelect!!, userId!!, message?.total_votes!!, userName, userImage, isExpied)
        rvPollOptions?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        rvPollOptions?.itemAnimator = null
        rvPollOptions?.adapter = pollOptionsAdapter
        rvPollOptions?.isNestedScrollingEnabled = false
        jsonObject.put(MESSAGE_UNIQUE_ID, message?.muid)
        jsonObject.put("message_poll", true)
        jsonObject.put(FuguAppConstant.MESSAGE_TYPE, FuguAppConstant.POLL_MESSAGE)
        jsonObject.put(FuguAppConstant.IS_TYPING, 0)
        jsonObject.put(USER_ID, userId!!)
        jsonObject.put(FULL_NAME, userName)
        val devicePayload = JSONObject()
        devicePayload.put(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(this@PollDetailsActivity))
        devicePayload.put(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER)
        devicePayload.put(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME)
        devicePayload.put(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails(this@PollDetailsActivity))
        jsonObject.put("device_payload", devicePayload)
        jsonObject.put(CHANNEL_ID, channelId)
        SocketConnection.sendPollVote(jsonObject)
//        if (mClient != null) {
//            mClient?.publish(channelId, "/" + channelId, jsonObject);
//        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        finish()
    }
}
