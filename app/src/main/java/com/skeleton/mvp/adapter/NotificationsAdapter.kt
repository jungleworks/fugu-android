package com.skeleton.mvp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.ChatActivity
import com.skeleton.mvp.activity.FuguInnerChatActivity
import com.skeleton.mvp.activity.MainActivity
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.data.db.ChatDatabase
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.CommonResponse
import com.skeleton.mvp.fragment.NotificationsActivity
import com.skeleton.mvp.model.FuguConversation
import com.skeleton.mvp.model.homeNotifications.Notification
import com.skeleton.mvp.retrofit.APIError
import com.skeleton.mvp.retrofit.ResponseResolver
import com.skeleton.mvp.ui.AppConstants
import com.skeleton.mvp.ui.AppConstants.EXTRA_ALREADY_MEMBER
import com.skeleton.mvp.ui.yourspaces.YourSpacesActivity
import com.skeleton.mvp.utils.DateUtils
import com.skeleton.mvp.utils.FuguUtils.Companion.getFirstCharInUpperCase
import com.skeleton.mvp.utils.StringUtil
import com.skeleton.mvp.utils.UniqueIMEIID


/**
 * Created
 * rajatdhamija on 29/07/18.
 */
class NotificationsAdapter(mContext: Context, notificationsList: ArrayList<Notification>) : androidx.recyclerview.widget.RecyclerView.Adapter<NotificationsAdapter.MyViewHolder>() {

    private var mContext = mContext
    private var notificationsList = ArrayList<Notification>()
    private val colorsArray = java.util.ArrayList<String>()
    private var currentAppSecretKey = ""
    private val workspacesList = ArrayList<String>()

    init {
        this.notificationsList = notificationsList
        colorsArray.add("#282828")
        colorsArray.add("#009688")
        colorsArray.add("#F44336")
        colorsArray.add("#9C27B0")
        colorsArray.add("#673AB7")
        colorsArray.add("#3F51B5")
        currentAppSecretKey = CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey
        workspacesList.clear()
        for (workspace in CommonData.getCommonResponse().data.workspacesInfo) {
            workspacesList.add(workspace.fuguSecretKey)
        }

    }

    fun updateNotifications(notificationsList: ArrayList<Notification>) {
        this.notificationsList = notificationsList
        currentAppSecretKey = CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_home_notification, parent, false))
    }


    override fun getItemCount(): Int {
        return notificationsList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, pos: Int) {
        val position = holder.adapterPosition
        val notification: Notification = notificationsList[position]
        if (TextUtils.isEmpty(notification.actionByUserImage)) {
            holder.llCircle.visibility = View.VISIBLE
            holder.ivUserImage.visibility = View.GONE
            holder.tvName.visibility = View.VISIBLE
            holder.tvName.text = getFirstCharInUpperCase(notification.actionByUserName)
            when {
                notification.actionByUserId.toLong() == 1L -> {
                    holder.llCircle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_grey))
                }
                notification.actionByUserId.toLong() == 2L -> {
                    holder.llCircle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_teal))
                }
                notification.actionByUserId.toLong() == 3L -> {
                    holder.llCircle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_red))
                }
                notification.actionByUserId.toLong() == 4L -> {
                    holder.llCircle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_indigo))
                }
                else -> {
                    holder.llCircle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_red))
                }
            }
        } else {
            holder.tvName.visibility = View.GONE
            holder.llCircle.visibility = View.GONE
            holder.ivUserImage.visibility = View.VISIBLE

            val options = RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.fugu_ic_channel_icon)
                    .error(R.drawable.fugu_ic_channel_icon)
                    .fitCenter()
                    .priority(Priority.HIGH)
                    .transforms(CenterCrop(), RoundedCorners(10))


            Glide.with(mContext)
                    .asBitmap()
                    .apply(options)
                    .load(notification.actionByUserImage)
                    .into(holder.ivUserImage)

        }

        holder.tvNotificationTime.text = (DateUtils.getDate(DateUtils.getInstance().convertToLocal(notification.updatedAt))
                + " at " + DateUtils.getTime(DateUtils.getInstance().convertToLocal(notification.updatedAt)))
        if (CommonData.getWorkpscesMap()[notification.appSecretKey] != null &&
                notification.appSecretKey != currentAppSecretKey) {
            holder.tvNotificationText.text = notification.notificationTitle + " (" + CommonData.getWorkpscesMap()[notification.appSecretKey] + ")"
            val str = SpannableStringBuilder(holder.tvNotificationText.text)
            str.setSpan(android.text.style.StyleSpan(android.graphics.Typeface.BOLD), (holder.tvNotificationText.text.length - CommonData.getWorkpscesMap()[notification.appSecretKey]!!.length) - 1, holder.tvNotificationText.text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            val str2 = SpannableStringBuilder(notification.actionByUserName)
            str2.setSpan(android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, notification.actionByUserName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            holder.tvNotificationText.text = str2
            holder.tvNotificationText.append(str)

        } else {
            val str2 = SpannableStringBuilder(notification.actionByUserName)
            str2.setSpan(android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, notification.actionByUserName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            holder.tvNotificationText.text = str2
            holder.tvNotificationText.append(notification.notificationTitle)
        }

        if (!TextUtils.isEmpty(notification.readAt)) {
            holder.llRoot.setBackgroundColor(mContext.resources.getColor(R.color.white))
            holder.tvNotificationTime.setTextColor(mContext.resources.getColor(R.color.gray_dark))
        } else {
            holder.llRoot.setBackgroundColor(mContext.resources.getColor(R.color.colorPrimaryLight))
            holder.tvNotificationTime.setTextColor(mContext.resources.getColor(R.color.colorPrimary))
        }

        holder.itemView.setOnClickListener {

            if ((mContext as NotificationsActivity).isNetworkConnected) {
                if (holder.adapterPosition > -1) {
                    notificationsList[holder.adapterPosition].readAt = notification.updatedAt
                }
                CommonData.setHomeNotifications(notificationsList)
                notifyDataSetChanged()
                (mContext as NotificationsActivity).publishMessage(notification.notificationId)
                when (notification.notificationType) {
                    MESSAGE_NOTIFICATION, EDIT_MESSAGE_NOTIFICATION -> openChat(notification)
                    ADD_MEMBER_NOTIFICATION -> openChat(notification)
                    GROUP_INFO_NOTIFICATION -> openChat(notification)
                    TASK_ASSIGNED_NOTIFICATION -> {
                        val data = Intent()
                        data.putExtra("openTasksTab", true)
                        data.putExtra(APP_SECRET_KEY, notification.appSecretKey)
                        (mContext as NotificationsActivity).setResult(Activity.RESULT_OK, data)
                        (mContext as NotificationsActivity).finish()
                    }
                    MEET_SCHEDULED_NOTIFICATION -> {
                        val data = Intent()
                        data.putExtra("openMeetTab", true)
                        data.putExtra(APP_SECRET_KEY, notification.appSecretKey)
                        (mContext as NotificationsActivity).setResult(Activity.RESULT_OK, data)
                        (mContext as NotificationsActivity).finish()
                    }
                    NEW_WORKSPACE_NOTIFICATION -> {

                        if (notification.appSecretKey == CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey) {
                            (mContext as NotificationsActivity).showLoading()
//                            (mContext as NotificationActivity).changeBusinessAndOpenChat(ArrayList<String>(CommonData.getWorkpscesMap().keys).indexOf(notification.appSecretKey), false, object : MainActivity.OpenChat {
//                                override fun openChat() {
//                                    startIntent(notification)
//                                    (mContext as NotificationActivity).hideLoading()
//                                }
//                            })
                        }
                        if (CommonData.getWorkpscesMap()[notification.appSecretKey] != null) {
                            if (notification.appSecretKey != CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey) {
//                                (mContext as NotificationActivity).showLoading()
//                                (mContext as NotificationActivity).changeBusinessAndOpenChat(ArrayList<String>(CommonData.getWorkpscesMap().keys).indexOf(notification.appSecretKey), false, object : MainActivity.OpenChat {
//                                    override fun openChat() {
//                                        (mContext as NotificationActivity).hideLoading()
//                                        (mContext as NotificationActivity).navigateToHome()
//                                    }
//                                })
                            } else {
                                (mContext as NotificationsActivity).hideLoading()
//                                (mContext as NotificationsActivity).navigateToHome()
                            }
                        } else {
                            val intent1 = Intent(mContext, YourSpacesActivity::class.java)
                            intent1.putExtra("API_HIT", true)
                            intent1.putExtra(EXTRA_ALREADY_MEMBER, EXTRA_ALREADY_MEMBER)
                            mContext.startActivity(intent1)
                        }
                    }
                }
            } else {
                try {
                    (mContext as MainActivity).showErrorMessage(R.string.fugu_not_connected_to_internet)
                } catch (e: Exception) {

                }
            }
        }
    }

    private fun openChat(notification: Notification) {
        if (workspacesList.contains(notification.appSecretKey)) {
            if (notification.appSecretKey != CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey) {
                (mContext as NotificationsActivity).showLoading()
                changeBusinessAndOpenChat(ArrayList<String>(CommonData.getWorkpscesMap().keys).indexOf(notification.appSecretKey), false, notification)
            } else {
                startIntent(notification)
            }
        }
    }

    private fun changeBusinessAndOpenChat(position: Int, isAnimation: Boolean, notification: Notification) {
        (mContext as NotificationsActivity).showLoading()
        val commonParams = com.skeleton.mvp.data.network.CommonParams.Builder()
                .add(AppConstants.WORKSPACE_ID, CommonData.getCommonResponse().getData().workspacesInfo[position].workspaceId)
                .add(AppConstants.TOKEN, CommonData.getFcmToken())
                .add(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId((mContext as NotificationsActivity)))
                .add(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails((mContext as NotificationsActivity)))
                .build()

        com.skeleton.mvp.data.network.RestClient.getApiInterface(true).switchWorkspace(CommonData.getCommonResponse().data.userInfo.accessToken, BuildConfig.VERSION_CODE, AppConstants.ANDROID, commonParams.map)
                .enqueue(object : ResponseResolver<CommonResponse>() {
                    override fun success(t: com.skeleton.mvp.data.model.CommonResponse?) {
                        CommonData.setCurrentSignedInPosition(position)
                        (mContext as NotificationsActivity).hideLoading()
                        startIntent(notification)
                    }

                    override fun failure(error: APIError?) {
                        (mContext as NotificationsActivity).hideLoading()
                    }

                })
    }


    private fun startIntent(notification: Notification) {
        val conversation = FuguConversation()
        if (notification.chatType == FuguAppConstant.ChatType.O2O) {
            conversation.businessName = notification.actionByUserName
            conversation.label = notification.actionByUserName
        } else {
            conversation.businessName = ""
            conversation.label = ""
        }

        conversation.isOpenChat = true
        conversation.channelId = notification.channelId
        conversation.chat_type = notification.chatType
        conversation.userName = StringUtil.toCamelCase(CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].fullName)
        conversation.userId = java.lang.Long.valueOf(CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].userId)
        conversation.enUserId = CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].enUserId
        conversation.unreadCount = ChatDatabase.getNotifications(notification.channelId).size
        if (notification.isTagged == 0 && TextUtils.isEmpty(notification.threadMuid)) {
            val chatIntent = Intent(mContext, ChatActivity::class.java)
            chatIntent.putExtra(FuguAppConstant.CONVERSATION, Gson().toJson(conversation, FuguConversation::class.java))
            mContext.startActivity(chatIntent)
        } else {
            val backstack = Intent(mContext, ChatActivity::class.java)
            backstack.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            backstack.putExtra("NotificationMuid", notification.muid)
            backstack.putExtra("isNotificationMessage", true)
            backstack.putExtra(FuguAppConstant.CONVERSATION, Gson().toJson(conversation, FuguConversation::class.java))
            val notificationIntent = Intent(mContext, FuguInnerChatActivity::class.java)
            notificationIntent.putExtra("muid", notification.muid)
            notificationIntent.putExtra(CHANNEL_ID, notification.channelId)
            notificationIntent.putExtra("scroll", true)

            if (notification.chatType == FuguAppConstant.ChatType.O2O) {
                notificationIntent.putExtra("BUSINESS_NAME", notification.actionByUserName)
                notificationIntent.putExtra("label", notification.actionByUserName)
            } else {
                notificationIntent.putExtra("BUSINESS_NAME", "")
                notificationIntent.putExtra("label", "")
            }
            notificationIntent.putExtra("chatType", notification.chatType)
            mContext.startActivities(arrayOf(backstack, notificationIntent))
            (mContext as NotificationsActivity).finish()
        }
    }


    class MyViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var ivUserImage: AppCompatImageView = itemView.findViewById(R.id.ivUserImage)
        var tvNotificationText: TextView = itemView.findViewById(R.id.tvNotificationText)
        var tvNotificationTime: TextView = itemView.findViewById(R.id.tvNotificationTime)
        var llRoot: LinearLayout = itemView.findViewById(R.id.llRoot)
        var llCircle: AppCompatImageView = itemView.findViewById(R.id.llCircle)
        var tvName: TextView = itemView.findViewById(R.id.tvName)

    }

    interface PublishMessage {
        fun publishMessage(notificationId: Int)
    }
}