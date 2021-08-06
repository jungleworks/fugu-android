package com.skeleton.mvp.activity

import android.app.AlertDialog
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.adapter.SearchMessageAdapter
import com.skeleton.mvp.constant.FuguAppConstant.EN_USER_ID
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.fcCommon.Data
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.data.network.CommonParams
import com.skeleton.mvp.data.network.ResponseResolver
import com.skeleton.mvp.data.network.RestClient
import com.skeleton.mvp.model.SearchMessage
import com.skeleton.mvp.model.starredmessage.StarredMessagelResponse
import com.skeleton.mvp.ui.base.BaseActivity

class StarredMessagesActivity : BaseActivity() {
    var data: Data? = null
    var messageList = ArrayList<SearchMessage>()
    var rvStarredMessage: androidx.recyclerview.widget.RecyclerView? = null
    var searchMessageAdapter: SearchMessageAdapter? = null
    var llNoResults: LinearLayout? = null
    var toolbar: androidx.appcompat.widget.Toolbar? = null
    var ivBack: AppCompatImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starred_messages)
        llNoResults = findViewById(R.id.llNoResults)
        data = CommonData.getCommonResponse().data
        toolbar = findViewById(R.id.toolbar)
        ivBack = findViewById(R.id.ivBack)
        rvStarredMessage = findViewById(R.id.rvStarredMessage)
        rvStarredMessage?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        showLoading()
        apiGetStarredMessages()
        toolbar?.inflateMenu(R.menu.unstarall_menu)
        toolbar?.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.unstarAll -> AlertDialog.Builder(this@StarredMessagesActivity)
                        .setMessage("Are you sure you want to unstar all messages?")
                        .setPositiveButton("Yes") { _, _ ->
                            showLoading()
                            apiUnstarAll()
                        }.setNegativeButton("No") { _, _ -> }.show()
                else -> {
                }
            }
            false
        }
        ivBack?.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

    private fun apiUnstarAll() {
        val commonParams = CommonParams.Builder()
                .add(EN_USER_ID, CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()].enUserId)
                .add("unstar_all", true)
                .build()
        RestClient.getApiInterface(false).starMessage(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey,
                1, BuildConfig.VERSION_CODE, commonParams.map).enqueue(object : ResponseResolver<com.skeleton.mvp.data.model.CommonResponse>() {
            override fun onSuccess(t: com.skeleton.mvp.data.model.CommonResponse?) {
                hideLoading()
                llNoResults?.visibility = View.VISIBLE
            }

            override fun onError(error: ApiError?) {
                hideLoading()
            }

            override fun onFailure(throwable: Throwable?) {
                hideLoading()
            }


        })
    }

    private fun apiGetStarredMessages() {
        val commonParams = CommonParams.Builder()
                .add(EN_USER_ID, data?.workspacesInfo!![CommonData.getCurrentSignedInPosition()]!!.enUserId)
                .add("page_start", 1)
                .add("page_end", 200)
        RestClient.getApiInterface(false).getStarredMessages(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), data?.workspacesInfo!![CommonData.getCurrentSignedInPosition()]!!.fuguSecretKey,
                1, BuildConfig.VERSION_CODE, commonParams.build().map).enqueue(object : ResponseResolver<StarredMessagelResponse>() {
            override fun onSuccess(starredmessageResponse: StarredMessagelResponse?) {
                if (starredmessageResponse?.data?.starredMessages?.size!! > 0) {
                    llNoResults?.visibility = View.GONE
                    val userId = data?.workspacesInfo!![CommonData.getCurrentSignedInPosition()].userId.toLong()
                    for (i in starredmessageResponse.data?.starredMessages?.indices!!) {
                        val starredMessage = starredmessageResponse.data?.starredMessages!![i]
                        var name = ""
                        if (!TextUtils.isEmpty(starredMessage.label)) {
                            name = starredMessage.label!!
                        } else if (!TextUtils.isEmpty(starredMessage.userName)) {
                            name = starredMessage.userName!!
                        }
                        val isThreadMessage = !TextUtils.isEmpty(starredMessage.threadMuid)
                        val message = Html.fromHtml(starredMessage.message).toString()
                        messageList.add(SearchMessage(name,
                                message,
                                starredMessage.dateTime,
                                starredMessage.userName,
                                isThreadMessage,
                                starredMessage.chatType!!,
                                userId,
                                starredMessage.messageIndex!!,
                                "",
                                starredMessage.muid,
                                starredMessage.channelId,
                                "UNMUTED",
                                starredMessage.messageType,
                                starredMessage.fileName,
                                starredMessage.fileSize,
                                "", starredMessage.thumbnailUrl))
                    }
                } else {
                    llNoResults?.visibility = View.VISIBLE
                }
                hideLoading()
                searchMessageAdapter = SearchMessageAdapter(messageList, this@StarredMessagesActivity, "", false, true)
                rvStarredMessage?.adapter = searchMessageAdapter
            }

            override fun onError(error: ApiError?) {
                hideLoading()
            }

            override fun onFailure(throwable: Throwable?) {
                hideLoading()
            }

        })
    }
}
