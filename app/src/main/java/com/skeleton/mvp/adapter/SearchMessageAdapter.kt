package com.skeleton.mvp.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.ChatActivity
import com.skeleton.mvp.activity.FuguInnerChatActivity
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.datastructure.ChannelStatus
import com.skeleton.mvp.model.FuguConversation
import com.skeleton.mvp.model.SearchMessage
import com.skeleton.mvp.utils.DateUtils
import java.util.*


/**
 * Created
 * rajatdhamija on 03/07/18.
 */
class SearchMessageAdapter(messagesList: ArrayList<SearchMessage>, mContext: FragmentActivity?, searchedText: String, isClubbed: Boolean, isStarredMessages: Boolean) : androidx.recyclerview.widget.RecyclerView.Adapter<SearchMessageAdapter.MyViewHolder>() {
    private var messagesList: ArrayList<SearchMessage>
    private var mContext: FragmentActivity?
    private var searchedText: String
    private var myUserId: Long
    private var enUserId: String
    private var userName: String
    private var isClubbed: Boolean
    private var isStarredMessages: Boolean

    init {
        this.messagesList = messagesList
        this.mContext = mContext
        this.searchedText = searchedText
        val workspacesInfo: WorkspacesInfo = com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo.get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition())
        myUserId = workspacesInfo.userId.toLong()
        enUserId = workspacesInfo.enUserId
        userName = workspacesInfo.fullName
        this.isClubbed = isClubbed
        this.isStarredMessages = isStarredMessages
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_search_message, parent, false))
    }

    fun updateList(messagesList: ArrayList<SearchMessage>, searchedText: String) {
        messagesList.sortWith(Comparator { one, other -> other.date.compareTo(one.date) })
        this.messagesList = messagesList
        this.searchedText = searchedText
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, pos: Int) {
        val position = holder.adapterPosition
        val searchMessage: SearchMessage = messagesList[position]
        holder.tvDate.text = DateUtils.getDate(DateUtils.getInstance().convertToLocal(searchMessage.date))
        holder.tvMessage.text = Html.fromHtml(searchMessage.message)
        if (searchMessage.messageType == TEXT_MESSAGE) {
            holder.llFileView.visibility = View.GONE
            holder.ivMessage.visibility = View.GONE
            holder.rlImageMessage.visibility = View.GONE
        } else if (searchMessage.messageType == IMAGE_MESSAGE || searchMessage.messageType == VIDEO_MESSAGE) {
            holder.llFileView.visibility = View.GONE
            holder.ivMessage.visibility = View.VISIBLE
            holder.rlImageMessage.visibility = View.VISIBLE

            Glide.with(holder.itemView.context)
                    .load(searchMessage.imageUrl)
                    .into(holder.ivMessage)

            if (searchMessage.messageType == VIDEO_MESSAGE) {
                holder.ivVideo.visibility = View.VISIBLE
            } else {
                holder.ivVideo.visibility = View.GONE
            }

        } else if (searchMessage.messageType == FILE_MESSAGE) {
            holder.llFileView.visibility = View.VISIBLE
            holder.ivMessage.visibility = View.GONE
            holder.tvFileName.text = searchMessage.fileName
            holder.tvFileSize.text = searchMessage.fileSize
        } else {
            holder.llFileView.visibility = View.GONE
            holder.ivMessage.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(searchMessage.message)) {
            holder.tvMessage.visibility = View.VISIBLE
        } else {
            holder.tvMessage.visibility = View.GONE
        }

        holder.tvTime.text = DateUtils.getTime(DateUtils().convertToLocal(searchMessage.date))
        holder.tvName.text = searchMessage.fullName
        holder.tvChannelName.text = searchMessage.channelName
        if (searchMessage.isThreadMessage) {
            holder.tvThread.visibility = View.VISIBLE
        } else {
            holder.tvThread.visibility = View.GONE
        }

        val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )
        if (!isStarredMessages) {
            var searchedTextList = searchedText.split(" ")
            for (text in searchedTextList) {
                setHighLightedText(holder.tvMessage, text)
            }
        }
        if (isClubbed) {
            if (position > 0) {
                val searchMessage2: SearchMessage = messagesList[position - 1]
                if (DateUtils.getDate(DateUtils.getInstance().convertToLocal(searchMessage.date)) == DateUtils.getDate(DateUtils.getInstance().convertToLocal(searchMessage2.date))
                        && searchMessage.channelName == searchMessage2.channelName) {

                    params.setMargins(0, 0, 0, 0)
                    holder.llRoot.layoutParams = params
                    holder.llRoot.setBackgroundResource(R.drawable.rectangle_border_no_top)
                    holder.llRoot.setPadding(0, dpToPx(12), 0, dpToPx(10))
                    holder.tvDate.visibility = View.GONE
                    holder.tvChannelName.visibility = View.GONE
                } else {
                    params.setMargins(0, dpToPx(15), 0, 0)
                    holder.llRoot.layoutParams = params
                    holder.llRoot.setBackgroundResource(R.drawable.rectangle_border_normal)
                    holder.llRoot.setPadding(0, dpToPx(12), 0, dpToPx(10))
                    holder.tvDate.visibility = View.VISIBLE
                    holder.tvChannelName.visibility = View.VISIBLE
                }
            } else {
                params.setMargins(0, 0, 0, 0)
                holder.llRoot.layoutParams = params
                holder.llRoot.setBackgroundResource(R.drawable.rectangle_border_normal)
                holder.llRoot.setPadding(0, dpToPx(12), 0, dpToPx(10))
                holder.tvDate.visibility = View.VISIBLE
                holder.tvChannelName.visibility = View.VISIBLE
            }
        } else {
            params.setMargins(0, dpToPx(15), 0, 0)
            holder.llRoot.layoutParams = params
            holder.llRoot.setBackgroundResource(R.drawable.rectangle_border_normal)
            holder.llRoot.setPadding(0, dpToPx(12), 0, dpToPx(10))
            holder.tvDate.visibility = View.VISIBLE
            holder.tvChannelName.visibility = View.VISIBLE
        }
        holder.itemView.setOnClickListener {
            if (!searchMessage.isThreadMessage) {
                val conversation = FuguConversation()
                conversation.label = holder.tvChannelName.text.toString()
                conversation.channelId = searchMessage.channelId
                conversation.labelId = -1L
                conversation.defaultMessage = ""
                conversation.channelStatus = ChannelStatus.OPEN.getOrdinal()
                conversation.thumbnailUrl = ""
                conversation.url = ""
                conversation.businessName = holder.tvChannelName.text.toString()
                conversation.userId = myUserId
                conversation.enUserId = enUserId
                conversation.isOpenChat = true
                conversation.userName = userName
                conversation.isTimeSet = 1
                conversation.chat_type = searchMessage.chatType
                conversation.status = 1
                conversation.last_sent_by_id = -1L
                conversation.unreadCount = 0
                conversation.notifications = searchMessage.notifications

                val chatIntent = Intent(mContext, ChatActivity::class.java)
                chatIntent.putExtra("IS_SEARCHED_MESSAGE", "IS_SEARCHED_MESSAGE")
                chatIntent.putExtra("messageSearchIndex", searchMessage.messageId)
                chatIntent.putExtra("messageSearchMuid", searchMessage.muid)
                chatIntent.putExtra(FuguAppConstant.CONVERSATION, Gson().toJson(conversation, FuguConversation::class.java))
                (mContext as Context).startActivity(chatIntent)
            } else {
                var notificationIntent = Intent(mContext, FuguInnerChatActivity::class.java)
                notificationIntent.putExtra("muid", searchMessage.muid)
                notificationIntent.putExtra(CHANNEL_ID, searchMessage.channelId)
                notificationIntent.putExtra("scroll", true)
                notificationIntent.putExtra("chatType", searchMessage.chatType)
                (mContext as Context).startActivity(notificationIntent)
            }
        }
    }

    class MyViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var tvTime: TextView = itemView.findViewById(R.id.tvTime)
        var tvChannelName: TextView = itemView.findViewById(R.id.tvChannelName)
        var tvDate: TextView = itemView.findViewById(R.id.tvDate)
        var tvName: TextView = itemView.findViewById(R.id.tvName)
        var tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        var tvThread: TextView = itemView.findViewById(R.id.tvThread)
        var llRoot: LinearLayout = itemView.findViewById(R.id.llRoot)
        var ivMessage: AppCompatImageView = itemView.findViewById(R.id.ivMessage)
        var llFileView: LinearLayout = itemView.findViewById(R.id.llFileView)
        var tvFileName: AppCompatTextView = itemView.findViewById(R.id.tvFileName)
        var tvFileSize: AppCompatTextView = itemView.findViewById(R.id.tvFileSize)
        var ivVideo: AppCompatImageView = itemView.findViewById(R.id.ivVideo)
        var rlImageMessage: RelativeLayout = itemView.findViewById(R.id.rlImageMessage)
    }

    private fun dpToPx(dpParam: Int): Int {
        val d = mContext?.resources?.displayMetrics?.density
        return (dpParam * d!!).toInt()
    }

    fun setHighLightedText(tv: TextView, textToHighlight: String) {
        val tvt = tv.text.toString()
        var ofe = tvt.indexOf(textToHighlight, 0, ignoreCase = true)
        val wordToSpan = SpannableString(tv.text)
        var ofs = 0
        while (ofs < tvt.length && ofe != -1) {
            ofe = tvt.indexOf(textToHighlight, ofs, ignoreCase = true)
            if (ofe == -1)
                break
            else {
                wordToSpan.setSpan(BackgroundColorSpan(Color.parseColor("#fff6d1")), ofe, ofe + textToHighlight.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                tv.setText(wordToSpan, TextView.BufferType.SPANNABLE)
            }
            ofs = ofe + 1
        }
    }
}