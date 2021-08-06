package com.skeleton.mvp.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.FuguColorConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.MainActivity
import com.skeleton.mvp.adapter.NotificationsAdapter
import com.skeleton.mvp.constant.FuguAppConstant.EN_USER_ID
import com.skeleton.mvp.constant.FuguAppConstant.PAGE_START
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.model.homeNotifications.HomeNotificationsResponse
import com.skeleton.mvp.model.homeNotifications.Notification
import com.skeleton.mvp.retrofit.*
import com.skeleton.mvp.socket.SocketConnection
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.utils.EndlessScrolling
import org.json.JSONObject

/**
 * Created \
 * rajatdhamija on 26/07/18.
 */

class NotificationsActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {
    private var rvNotifications: androidx.recyclerview.widget.RecyclerView? = null

    private var llMarkRead: LinearLayout? = null
    private var notificationsAdapter: NotificationsAdapter? = null
    private var notificationsList = ArrayList<Notification>()
    private lateinit var mainActivity: MainActivity
    //    private var mClient: FayeClient? = null
    private var swipeNotifications: SwipeRefreshLayout? = null
    private var backendPageSize = 20
    private var pageStartMain = 1
    private var endlessScrolling: EndlessScrolling? = null
    private var llNotifications: LinearLayout? = null
    private var mContext: Context? = null
    private var ivBack: AppCompatImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_notifications)
        mContext = this
        swipeNotifications = findViewById(R.id.swipeNotifications)
        llNotifications = findViewById(R.id.llNoNotifications)
        swipeNotifications?.setOnRefreshListener(this)
        swipeNotifications?.setColorSchemeColors(FuguColorConfig().fuguThemeColorPrimary)
        llMarkRead = findViewById(R.id.llMarkRead)
        ivBack = findViewById(R.id.ivBack)
        ivBack?.setOnClickListener { onBackPressed() }
        llMarkRead?.setOnClickListener {
            if ((mContext as NotificationsActivity).isNetworkConnected) {
                (mContext as NotificationsActivity).showLoading()
                apiMarkAllNotificationsRead()
            } else {
                (mContext as NotificationsActivity).showErrorMessage(R.string.fugu_no_internet_connection_retry)
            }
        }
        rvNotifications = findViewById(R.id.rvNotifications)
        rvNotifications?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(mContext)
        Thread {
            kotlin.run {
                notificationsList = ArrayList()
                notificationsList = CommonData.getHomeNotifications()
                (mContext as NotificationsActivity).runOnUiThread {
                    if (notificationsList.size > 0) {
//                        (mContext as NotificationsActivity).inflateMenu(true)
                        llNotifications?.visibility = View.GONE
                        llMarkRead?.visibility = View.VISIBLE
                    } else {
//                        (mContext as NotificationsActivity).inflateMenu(false)
                        llNotifications?.visibility = View.VISIBLE
                        llMarkRead?.visibility = View.GONE
                    }
                    notificationsAdapter = NotificationsAdapter((mContext as NotificationsActivity), notificationsList)
                    rvNotifications?.adapter = notificationsAdapter
                }
            }
        }.start()
        apiGetNotifications(1)
    }


    fun apiMarkAllNotificationsRead() {
        val commonParams = CommonParams.Builder()
                .add("en_user_id", CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()].enUserId)
                .build()
        RestClient.getApiInterface().markAllNotificationsRead(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.map)
                .enqueue(object : ResponseResolver<CommonResponse>() {
                    override fun success(commonResponse: CommonResponse?) {
                        Thread {
                            kotlin.run {
                                for (notification in notificationsList) {
                                    notification.readAt = notification.updatedAt
                                }
                                CommonData.setHomeNotifications(notificationsList)
                                (mContext as NotificationsActivity).runOnUiThread {
                                    (mContext as NotificationsActivity).hideLoading()
                                    notificationsAdapter?.updateNotifications(notificationsList)
                                    notificationsAdapter?.notifyDataSetChanged()
                                }
                            }
                        }.start()
                    }

                    override fun failure(error: APIError?) {
                        (mContext as NotificationsActivity).hideLoading()
                    }

                })
    }

    fun apiGetNotifications(pageStart: Int) {
        try {
            val commonParams = CommonParams.Builder()
            commonParams.add(EN_USER_ID, CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()].enUserId)
            commonParams.add(PAGE_START, pageStart)
            RestClient.getApiInterface().getNotifications(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                    .enqueue(object : ResponseResolver<HomeNotificationsResponse>() {
                        override fun success(homeNotificationsResponse: HomeNotificationsResponse?) {
                            try {
                                val data = CommonData.getCommonResponse()
                                data.data.unread_notification_count = 0
                                CommonData.setCommonResponse(data)
                                if (endlessScrolling == null && rvNotifications != null) {
                                    endlessScrolling = object : EndlessScrolling(rvNotifications?.layoutManager as androidx.recyclerview.widget.LinearLayoutManager?) {
                                        override fun onLoadMore(currentPages: Int) {

                                            pageStartMain += backendPageSize
                                            apiGetNotifications(pageStartMain)
                                        }

                                        override fun onHide() {
                                        }

                                        override fun onShow() {
                                        }

                                    }
                                    rvNotifications?.addOnScrollListener(endlessScrolling!!)
                                }
                                backendPageSize = homeNotificationsResponse?.data?.notificationpageSize!!
                                try {
                                    if (pageStart == 1) {
                                        pageStartMain = pageStart
                                        notificationsList = ArrayList()
                                        endlessScrolling!!.setCurrentPage(0)
                                    }
                                    notificationsList.addAll(homeNotificationsResponse.data?.notifications!!)
                                    if (notificationsList.size > 0) {
//                                        (mContext as NotificationsActivity).inflateMenu(true)
                                        llNotifications?.visibility = View.GONE
                                        llMarkRead?.visibility = View.VISIBLE
                                    } else {
//                                        (mContext as NotificationsActivity).inflateMenu(false)
                                        llNotifications?.visibility = View.VISIBLE
                                        llMarkRead?.visibility = View.GONE
                                    }
                                    notificationsAdapter?.updateNotifications(notificationsList)
                                    notificationsAdapter?.notifyDataSetChanged()
                                    swipeNotifications?.isRefreshing = false
                                    Thread {
                                        kotlin.run {
                                            CommonData.setHomeNotifications(notificationsList)
                                        }
                                    }.start()

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun failure(error: APIError?) {
                            swipeNotifications?.isRefreshing = false
                        }

                    })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    fun setUpfayeConection() {
//        FuguConfig.getClient { fayeClient ->
//            mClient = fayeClient
//            if (!mClient?.isConnectedServer!!) {
//                mClient?.connectServer()
//            }
//            mClient?.listener = object : FayeClientListener {
//                override fun onErrorReceived(fc: FayeClient?, msg: String?, channel: String?) {
//
//                }
//
//                override fun onConnectedServer(fc: FayeClient?) {
//                    Log.e("Faye", "Connected")
//                    if (!TextUtils.isEmpty(CommonData.getCommonResponse().data.userInfo.userChannel)) {
//                        fc?.subscribeChannel("/" + CommonData.getCommonResponse().data.userInfo.userChannel)
//                    }
//                }
//
//                override fun onDisconnectedServer(fc: FayeClient?) {
//                    Log.e("Faye", "onDisconnectedServer")
//                }
//
//                override fun onReceivedMessage(fc: FayeClient?, msg: String?, channel: String?) {
//                    Log.e("Faye", "onReceivedMessage")
//                }
//
//            }
//        }
//    }

    fun terminateFayeConnection() {
//        if (mClient != null) {
//            mClient!!.disconnectServer()
//            mClient!!.unsubscribeChannel("/" + CommonData.getCommonResponse().data.userInfo.userChannel)
//        }
    }

    fun publishMessage(notificationId: Int) {
        val jsonObject = JSONObject()
        jsonObject.put("notification_id", notificationId)
        jsonObject.put("notification_type", 10)
        jsonObject.put("user_id", CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()].userId.toLong())

        SocketConnection.readNotification(jsonObject)

//        if (!mClient!!.channels.contains("/" + CommonData.getCommonResponse().data.userInfo.userChannel)) {
//            if (!mClient?.isConnectedServer!!) {
//                mClient?.connectServer()
//            }
//            mClient?.subscribeChannel("/" + CommonData.getCommonResponse().data.userInfo.userChannel)
//            mClient!!.publishNotification("/" + CommonData.getCommonResponse().data.userInfo.userChannel, jsonObject, null, null)
//        } else {
//            mClient!!.publishNotification("/" + CommonData.getCommonResponse().data.userInfo.userChannel, jsonObject, null, null)
//        }

    }

    override fun onRefresh() {
        if ((mContext as NotificationsActivity).isNetworkConnected) {
            swipeNotifications?.isRefreshing = true
            pageStartMain = 1
            apiGetNotifications(1)
            try {
                endlessScrolling!!.setCurrentPage(0)
            } catch (e: Exception) {

            }

        } else {
            (mContext as NotificationsActivity).showErrorMessage(R.string.fugu_no_internet_connection_retry)
        }
    }


}
