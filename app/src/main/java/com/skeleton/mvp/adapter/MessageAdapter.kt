package com.skeleton.mvp.adapter

import android.Manifest
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.NinePatchDrawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.SystemClock
import android.text.*
import android.text.style.RelativeSizeSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.app.SharedElementCallback
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.setPadding
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.Status
import com.github.chrisbanes.photoview.PhotoView
import com.github.lzyzsd.circleprogress.DonutProgress
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.FuguColorConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.*
import com.skeleton.mvp.community.JoinCommunityActivity
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.data.db.ChatDatabase
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.fragment.EmojiReactionsDialog
import com.skeleton.mvp.fragment.ImageDialog
import com.skeleton.mvp.fragment.PollUsersBottomSheetFragment
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.model.*
import com.skeleton.mvp.ui.profile.ProfileActivity
import com.skeleton.mvp.util.FormatStringUtil
import com.skeleton.mvp.util.Log
import com.skeleton.mvp.util.MessageDiffCallback
import com.skeleton.mvp.util.ValidationUtil
import com.skeleton.mvp.utils.*
import com.skeleton.mvp.utils.FuguUtils.Companion.getFirstCharInUpperCase
import com.skeleton.mvp.utils.beatAnimation.AVLoadingIndicatorViewFugu
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Suppress("DEPRECATION", "unused", "PrivatePropertyName", "ClickableViewAccessibility")
/**
 * Created
 * rajatdhamija on 28/05/18.
 */

class MessageAdapter(messageList: ArrayList<Message>, mContext: Context, label: String, channelId: Long?, recyclerView: androidx.recyclerview.widget.RecyclerView,
                     chatType: Int, userId: Long, fullName: String, userImage: String, muid: String) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>(), Animation.AnimationListener {
    private var messageList = ArrayList<Message>()
    private var mContext: Context
    private var TEXT_MESSGAE_SELF: Int = 0
    private var TEXT_MESSGAE_OTHER: Int = 1
    private var IMAGE_MESSGAE_SELF: Int = 2
    private var IMAGE_MESSGAE_OTHER: Int = 3
    private var FILE_MESSGAE_SELF: Int = 4
    private var FILE_MESSGAE_OTHER: Int = 5
    private var HEADER_ITEM = 6
    private var PUBLIC_NOTE = 7
    private var UNREAD_ITEM = 8
    private var MESSAGE_DELETED_SELF = 9
    private var MESSAGE_DELETED_OTHER = 10
    private var VIDEO_MESSGAE_SELF: Int = 11
    private var VIDEO_MESSGAE_OTHER: Int = 12
    private var VIDEO_CALL_SELF: Int = 13
    private var VIDEO_CALL_OTHER: Int = 14
    private var POLL_SELF: Int = 15
    private var POLL_OTHER: Int = 16
    private var CUSTOM_ACTION: Int = 17
    private var dateUtil: DateUtils = DateUtils.getInstance()
    private var colorConfig: FuguColorConfig = FuguColorConfig()
    private var mLastClickTime: Long = -1L
    private var chatActivity: ChatActivity? = null
    private lateinit var fuguInnerChatActivity: FuguInnerChatActivity
    private var isMessageDeleted = false
    private var animation: Animation? = null
    private var label: String = ""
    private var channelId: Long?
    private var receiverRegistered: Boolean = false
    private var mProgressReceiver: BroadcastReceiver? = null
    private val recyclerView: androidx.recyclerview.widget.RecyclerView
    private val chatType: Int
    private var mOnRetry: OnRetryListener? = null
    private var isPlaying = false
    private var mediaPlayer: MediaPlayer? = null
    private var oldPos: Int = 0
    private var isAlreadyAnimated: Boolean = false
    private var userId: Long = -1L
    private var userImage: String = ""
    private var userName: String = ""
    var proxyNovaSB: Typeface? = null
    var proxyNovaRegular: Typeface? = null
    private var workspaceInfo: List<WorkspacesInfo>? = null
    private var currentPos: Int = 0
    private var onLongClick = false
    var imageFiles = ArrayList<ImageItem>()
    var listFile = ArrayList<File>()
    var clickedPos = 0
    var muid = ""
    var messageMap = HashMap<String, Message>()
    var isAnimationPlaying = false
    var onLinkLongClick = false
    private var downloadIdVideo: Int = 0

    //edited//
    private var TEXT_MESSAGE_THREAD: Int = 18
    private var IMAGE_MESSAGE_THREAD: Int = 19
    private var VIDEO_MESSAGE_THREAD: Int = 20
    private var FILE_MESSAGE_THREAD: Int = 21
    private var THREAD_MESSAGE_DELETED_SELF: Int = 22
    private var THREAD_MESSAGE_DELETED_OTHER: Int = 23
    private var ft: FragmentTransaction? = null
    private var performClickCount = 0
    private var userType = UserType.BOT
    private var onlyAdminCanReply = false

    init {
        this.messageList = messageList
        this.mContext = mContext
        animation = AnimationUtils.loadAnimation(mContext, R.anim.emoji_anim)
        animation?.setAnimationListener(this)
        this.label = label
        this.channelId = channelId
        this.recyclerView = recyclerView
        this.chatType = chatType
        this.userId = userId
        this.userImage = userImage
        this.userName = fullName
        proxyNovaSB = Typeface.createFromAsset(mContext.assets, FONT_SEMIBOLD)
        proxyNovaRegular = Typeface.createFromAsset(mContext.assets, FONT_REGULAR)
        workspaceInfo = com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo
        currentPos = com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()
        this.muid = muid
        messageMap = HashMap()

        for (i in 0 until messageList.size) {
            messageMap.put(messageList[i].muid, messageList[i])
        }
        getFromSdcard()
        prepareExitTransition()
    }

    companion object {
        const val STORAGE_PERMISSION_TO_DOWNLOAD_REQUEST = 906
    }

    fun updateOnlyAdminCanReply(onlyAdminCanReply: Boolean) {
        this.onlyAdminCanReply = onlyAdminCanReply
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        when (viewType) {
            TEXT_MESSGAE_SELF -> return SelfTextMessageViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_text_message_self, parent, false))
            TEXT_MESSGAE_OTHER -> return OtherTextMessageViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_text_message_other, parent, false))
            IMAGE_MESSGAE_SELF -> return SelfImageMessageViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_image_message_self, parent, false))
            IMAGE_MESSGAE_OTHER -> return OtherImageMessageViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_image_message_other, parent, false))
            FILE_MESSGAE_SELF -> return SelfFileMessageViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_file_message_self, parent, false))
            FILE_MESSGAE_OTHER -> return OtherFileMessageViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_file_message_other, parent, false))
            HEADER_ITEM -> return DateViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_header, parent, false))
            PUBLIC_NOTE -> return PublicNoteViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_public_note, parent, false))
            UNREAD_ITEM -> return UnreadViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_unread_layout, parent, false))
            MESSAGE_DELETED_SELF -> return SelfDeletedMessageViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_deleted_self, parent, false))
            MESSAGE_DELETED_OTHER -> return OtherDeletedMessageViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_deleted_other, parent, false))
            VIDEO_MESSGAE_SELF -> return SelfVideoMessageViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_video_self, parent, false))
            VIDEO_MESSGAE_OTHER -> return OtherVideoMessageViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_video_other, parent, false))
            VIDEO_CALL_SELF -> return SelfVideoCallMessage(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_video_call_self, parent, false))
            VIDEO_CALL_OTHER -> return OtherVideoCallMessage(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_video_call_other, parent, false))
            POLL_SELF -> return SelfPollMessage(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_poll_self, parent, false))
            POLL_OTHER -> return OtherPollMessage(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_poll_other, parent, false))
            CUSTOM_ACTION -> return CustomActionViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_custom_action, parent, false))
            TEXT_MESSAGE_THREAD -> return ThreadTextMessageViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_text_message_thread, parent, false))
            IMAGE_MESSAGE_THREAD -> return ThreadImageMessageViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_image_message_thread, parent, false))
            VIDEO_MESSAGE_THREAD -> return ThreadVideoMessageViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_video_message_thread, parent, false))
            FILE_MESSAGE_THREAD -> return ThreadFileMessageViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_file_message_thread, parent, false))

            else -> {
                return SelfTextMessageViewHolder(LayoutInflater.from(mContext)
                        .inflate(R.layout.fugu_item_message_left, parent, false))
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        try {
            return messageList[position].rowType
        } catch (e: Exception) {
            return messageList[messageList.size - 1].rowType
        }
    }

    fun updateUserType(userType: Int) {
        this.userType = userType
    }

    fun getUserType(): Int {
        return userType
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, pos: Int) {
        val position: Int = holder.adapterPosition
        val message: Message = messageList[position]
        when (getItemViewType(position)) {
            TEXT_MESSGAE_SELF -> {
                try {
                    setTextMessageSelf(holder, position, message)
                } catch (e: Exception) {
                }
            }
            TEXT_MESSGAE_OTHER -> {
                try {
                    setTextMessageOther(holder, position, message)
                } catch (e: Exception) {
                }
            }
            IMAGE_MESSGAE_SELF -> setImageMessageSelf(holder, position, message)
            IMAGE_MESSGAE_OTHER -> setImageMessageOther(holder, position, message)
            FILE_MESSGAE_SELF -> setFileMessageSelf(holder, position, message)
            FILE_MESSGAE_OTHER -> setFileMessageOther(holder, position, message)
            HEADER_ITEM -> setDateMessage(holder, position, message)
            PUBLIC_NOTE -> setPublicNote(holder, message)
            UNREAD_ITEM -> setUnreadItem(holder, position, message)
            MESSAGE_DELETED_SELF -> setDeletedMessageSelf(holder, message, position)
            MESSAGE_DELETED_OTHER -> setDeletedMessageOther(holder, message, position)
            VIDEO_MESSGAE_SELF -> setVideoMessageSelf(holder, position, message)
            VIDEO_MESSGAE_OTHER -> setVideoMessageOther(holder, position, message)
            VIDEO_CALL_SELF -> setVideoCallMessageSelf(holder, position, message)
            VIDEO_CALL_OTHER -> setVideoCallMessageOther(holder, position, message)
            POLL_SELF -> setPollMessageSelf(holder, position, message)
            POLL_OTHER -> setPollMessageOther(holder, position, message)
            CUSTOM_ACTION -> setCustomAction(holder, position, message)
            TEXT_MESSAGE_THREAD -> setTextMessageThread(holder, position, message)
            IMAGE_MESSAGE_THREAD -> setImageMessageThread(holder, position, message)
            VIDEO_MESSAGE_THREAD -> setVideoMessageThread(holder, position, message)
            FILE_MESSAGE_THREAD -> setFileMessageThread(holder, position, message)
            else -> {
                Log.i("Do Nothing", "Do Nothing")
            }
        }
    }

    private fun setCustomAction(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, message: Message) {
        val cutomActionViewHolder: CustomActionViewHolder = holder as CustomActionViewHolder
        setOtherMessageBackground(null, cutomActionViewHolder.llRoot, cutomActionViewHolder.llMessageBg, position, null, message.userId)
        cutomActionViewHolder.tvMessage.text = Html.fromHtml(FormatStringUtil.FormatString.getFormattedString(message.message).get(0))
        cutomActionViewHolder.rvCustomActions.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(holder.itemView.context)
        val customActionsAdapter = CustomActionsAdapter(mContext as ChatActivity, message.customActions, message.muid, holder.adapterPosition)
        cutomActionViewHolder.rvCustomActions.adapter = customActionsAdapter
        setTime(cutomActionViewHolder.tvTime, message.sentAtUtc)
    }

    private fun setPollMessageOther(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, message: Message) {
        val selfPollMessage: OtherPollMessage = holder as OtherPollMessage
        setOtherMessageBackground(null, selfPollMessage.llRoot, selfPollMessage.llMessage, position, selfPollMessage.tvUserName, message.userId)
        selfPollMessage.tvUserName.text = message.fromName
        selfPollMessage.tvUserName.setOnClickListener {
            if (message.userType != 0) {
                openProfile("mention://" + message.userId.toString(), mContext)
            }
        }
//        setBackgroundColor(selfPollMessage.llMessage.background as NinePatchDrawable, colorConfig.fuguBgMessageFrom)
        selfPollMessage.tvQuestion.text = Html.fromHtml(FormatStringUtil.FormatString.getFormattedString(message.question).get(0))
        setTime(selfPollMessage.tvTime, message.sentAtUtc)
        selfPollMessage.tvTotalVotes.text = "Total Votes: " + message.total_votes.toString()
        if (!message.multipleSelect) {
            selfPollMessage.llRadioGroup.visibility = View.VISIBLE
            selfPollMessage.checkboxGroup.visibility = View.GONE
            if (message.pollOptions[0].voteMap.size == 1) {
                selfPollMessage.radioOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " vote"
            } else {
                selfPollMessage.radioOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " votes"
            }
            if (message.pollOptions[1].voteMap.size == 1) {
                selfPollMessage.radioTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " vote"
            } else {
                selfPollMessage.radioTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " votes"
            }
            setOptionsTextOther(message.pollOptions.get(0).label, message.pollOptions.get(1).label, selfPollMessage, false)
            if (message.pollOptions[0].voteMap[userId] != null) {
                selfPollMessage.radioOne.isChecked = true
                selfPollMessage.radioTwo.isChecked = false
                selfPollMessage.llRadioOne.setBackgroundResource(R.drawable.rectangle_border_radio_blue)
                selfPollMessage.llRadioTwo.setBackgroundResource(R.drawable.rectangle_border_radio_gray)
            } else if (message.pollOptions[1].voteMap[userId] != null) {
                selfPollMessage.radioTwo.isChecked = true
                selfPollMessage.radioOne.isChecked = false
                selfPollMessage.llRadioTwo.setBackgroundResource(R.drawable.rectangle_border_radio_blue)
                selfPollMessage.llRadioOne.setBackgroundResource(R.drawable.rectangle_border_radio_gray)
            } else {
                selfPollMessage.radioOne.isChecked = false
                selfPollMessage.radioTwo.isChecked = false
                selfPollMessage.llRadioTwo.setBackgroundResource(R.drawable.rectangle_border_radio_gray)
                selfPollMessage.llRadioOne.setBackgroundResource(R.drawable.rectangle_border_radio_gray)
            }
        } else {
            selfPollMessage.llRadioGroup.visibility = View.GONE
            selfPollMessage.checkboxGroup.visibility = View.VISIBLE
            if (message.pollOptions[0].voteMap.size == 1) {
                selfPollMessage.cbOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " vote"
            } else {
                selfPollMessage.cbOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " votes"
            }
            if (message.pollOptions[1].voteMap.size == 1) {
                selfPollMessage.cbTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " vote"
            } else {
                selfPollMessage.cbTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " votes"
            }
            setOptionsTextOther(message.pollOptions.get(0).label, message.pollOptions.get(1).label, selfPollMessage, true)
            if (message.pollOptions[0].voteMap.containsKey(userId)) {
                selfPollMessage.cbOne.isChecked = true
                selfPollMessage.llCheckBoxOne.setBackgroundResource(R.drawable.rectangle_border_radio_blue)
            } else {
                selfPollMessage.cbOne.isChecked = false
                selfPollMessage.llCheckBoxOne.setBackgroundResource(R.drawable.rectangle_border_radio_gray)
            }
            if (message.pollOptions[1].voteMap.containsKey(userId)) {
                selfPollMessage.cbTwo.isChecked = true
                selfPollMessage.llCheckBoxTwo.setBackgroundResource(R.drawable.rectangle_border_radio_blue)
            } else {
                selfPollMessage.cbTwo.isChecked = false
                selfPollMessage.llCheckBoxTwo.setBackgroundResource(R.drawable.rectangle_border_radio_gray)
            }
        }
        if (message.pollOptions.size > 2) {
//            selfPollMessage.btnViewMore.setTextColor(mContext.resources.getColor(R.color.mentionAndLinks))
            if (message.pollOptions.size - 2 == 1) {
                selfPollMessage.btnViewMore.text = "+" + (message.pollOptions.size - 2) + " more option"
            } else {
                selfPollMessage.btnViewMore.text = "+" + (message.pollOptions.size - 2) + " more options"
            }
            selfPollMessage.btnViewMore.typeface = proxyNovaSB
        } else {
            selfPollMessage.btnViewMore.typeface = proxyNovaRegular
            selfPollMessage.btnViewMore.text = "View Details"
            selfPollMessage.btnViewMore.setTextColor(mContext.resources.getColor(R.color.black))
        }
        setFilledBackGroundCheckBoxOther(message, selfPollMessage)
        setFilledBackGroundRadioOther(message, selfPollMessage)
        selfPollMessage.llCheckBoxOne.setOnClickListener {
            if (message.pollOptions[0].users.size > 0) {
                val manager = (mContext as ChatActivity).supportFragmentManager
                val ft = manager.beginTransaction()

                val newFragment = PollUsersBottomSheetFragment.newInstance(0, mContext, message.pollOptions[0].users as ArrayList<User>, true, message.pollOptions[0].label, selfPollMessage.cbOne.isChecked)
                newFragment.show(ft, "PollUsersFragmentDialog")
            } else {
                Toast.makeText(mContext, "No votes yet!", Toast.LENGTH_LONG).show()
            }
        }
        selfPollMessage.cbOne.setOnClickListener {
            checkBoxOneClickedOther(selfPollMessage, message, false)
        }

        val isExpired = checkIfExpired(message)
        val formatter = SimpleDateFormat("dd MMM, hh:mm a")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis(message)
        if (isExpired) {
            selfPollMessage.tvExpiryTime.text = "Poll Expired"
            selfPollMessage.tvExpiryTime.setTextColor(mContext.resources.getColor(R.color.red))
        } else {
            selfPollMessage.tvExpiryTime.text = "Poll Expiry: " + formatter.format(calendar.time)
            selfPollMessage.tvExpiryTime.setTextColor(mContext.resources.getColor(R.color.black))
        }
        setFilledBackGroundCheckBoxOther(message, selfPollMessage)
        setFilledBackGroundRadioOther(message, selfPollMessage)
        if (!isExpired) {
            selfPollMessage.cbOne.setOnClickListener {
                checkBoxOneClickedOther(selfPollMessage, message, false)
            }
            selfPollMessage.cbTwo.setOnClickListener {
                checkBoxTwoClickedOther(message, selfPollMessage, false)
            }
            selfPollMessage.radioOne.setOnClickListener {
                if (!checkIfExpired(message)) {
                    radioButtonClickedOther(selfPollMessage.radioOne, message, selfPollMessage)
                } else {
                    selfPollMessage.radioOne.isChecked = !selfPollMessage.radioOne.isChecked
                }
            }
            selfPollMessage.radioTwo.setOnClickListener {
                if (!checkIfExpired(message)) {
                    radioButtonClickedOther(selfPollMessage.radioTwo, message, selfPollMessage)
                } else {
                    selfPollMessage.radioTwo.isChecked = !selfPollMessage.radioTwo.isChecked
                }
            }
        } else {
            selfPollMessage.cbOne.isClickable = false
            selfPollMessage.cbOne.isFocusableInTouchMode = false

            selfPollMessage.cbTwo.isClickable = false
            selfPollMessage.cbTwo.isFocusableInTouchMode = false

            selfPollMessage.radioOne.isClickable = false
            selfPollMessage.radioOne.isFocusableInTouchMode = false

            selfPollMessage.radioTwo.isClickable = false
            selfPollMessage.radioTwo.isFocusableInTouchMode = false

        }

        selfPollMessage.llCheckBoxTwo.setOnClickListener {
            if (message.pollOptions[1].users.size > 0) {
                val manager = (mContext as ChatActivity).supportFragmentManager
                val ft = manager.beginTransaction()

                val newFragment = PollUsersBottomSheetFragment.newInstance(0, mContext, message.pollOptions[1].users as ArrayList<User>, true, message.pollOptions[1].label, selfPollMessage.cbTwo.isChecked)
                newFragment.show(ft, "PollUsersFragmentDialog")
            } else {
                Toast.makeText(mContext, "No votes yet!", Toast.LENGTH_LONG).show()
            }
        }
        selfPollMessage.llRadioOne.setOnClickListener {
            if (message.pollOptions[0].users.size > 0) {
                val manager = (mContext as ChatActivity).supportFragmentManager
                val ft = manager.beginTransaction()

                val newFragment = PollUsersBottomSheetFragment.newInstance(0, mContext, message.pollOptions[0].users as ArrayList<User>, false, message.pollOptions[0].label, selfPollMessage.radioOne.isChecked)
                newFragment.show(ft, "PollUsersFragmentDialog")
            } else {
                Toast.makeText(mContext, "No votes yet!", Toast.LENGTH_LONG).show()
            }
        }
        selfPollMessage.llRadioTwo.setOnClickListener {
            if (message.pollOptions[1].users.size > 0) {
                val manager = (mContext as ChatActivity).supportFragmentManager
                val ft = manager.beginTransaction()

                val newFragment = PollUsersBottomSheetFragment.newInstance(0, mContext, message.pollOptions[1].users as ArrayList<User>, false, message.pollOptions[1].label, selfPollMessage.radioTwo.isChecked)
                newFragment.show(ft, "PollUsersFragmentDialog")
            } else {
                Toast.makeText(mContext, "No votes yet!", Toast.LENGTH_LONG).show()
            }
        }
        selfPollMessage.btnViewMore.setOnClickListener {
            val intent = Intent(mContext, PollDetailsActivity::class.java)
            intent.putExtra("message", message)
            intent.putExtra(USER_ID, userId)
            intent.putExtra(FULL_NAME, userName)
            intent.putExtra(USER_IMAGE, userImage)
            intent.putExtra(CHANNEL_ID, channelId)
            intent.putExtra(DATE_TIME, message.sentAtUtc)
            intent.putExtra("expire_time", message.expireTime)
            intent.putExtra("from_name", message.fromName)
            (mContext as ChatActivity).startActivityForResult(intent, 112)
        }
    }

    private fun setTextMessageThread(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, message: Message) {
        val textMessageThreadViewHolder: ThreadTextMessageViewHolder = holder as ThreadTextMessageViewHolder

        if (message.messageState == 0) {
            //rlImageMessage.setVisibility(View.GONE)
            textMessageThreadViewHolder.tvUserMessage.visibility = View.VISIBLE
            textMessageThreadViewHolder.ivDeleted.visibility = View.VISIBLE
            textMessageThreadViewHolder.llEmojis.visibility = View.GONE
            //textMessageThreadViewHolder.tvUserMessage.setVisibility(View.GONE)
            if (message.userId!!.compareTo(java.lang.Long.valueOf(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userId)) == 0) {
                textMessageThreadViewHolder.tvUserMessage.text = "You deleted this message"
                textMessageThreadViewHolder.tvUserMessage.setTextColor(mContext.resources.getColor(R.color.deleted_message_color))
                textMessageThreadViewHolder.tvUserMessage.setTypeface(textMessageThreadViewHolder.tvUserMessage.typeface, Typeface.ITALIC)
            } else {
                textMessageThreadViewHolder.tvUserMessage.text = "This message was deleted"
                textMessageThreadViewHolder.tvUserMessage.setTextColor(mContext.resources.getColor(R.color.deleted_message_color))
                textMessageThreadViewHolder.tvUserMessage.setTypeface(textMessageThreadViewHolder.tvUserMessage.typeface, Typeface.ITALIC)
            }
        } else {
            textMessageThreadViewHolder.ivDeleted.visibility = View.GONE
            val messageString = if (message.messageState == 4) {
                if (!TextUtils.isEmpty(message.alteredMessage)) {
                    message.alteredMessage + " <font color='grey'><small> (edited)</small></font>"
                } else {
                    message.message + " <font color='grey'><small> (edited)</small></font>"
                }
            } else {
                if (!TextUtils.isEmpty(message.alteredMessage)) {
                    message.alteredMessage
                } else {
                    message.message
                }
            }
            manipulateAndSetText(textMessageThreadViewHolder.tvUserMessage, messageString, message.messageState)
            setItemLongClick(textMessageThreadViewHolder.itemView, message, false, textMessageThreadViewHolder.tvTime)
            setItemLongClick(textMessageThreadViewHolder.tvUserMessage, message, false, textMessageThreadViewHolder.tvTime)
            setStarredMessage(textMessageThreadViewHolder.ivStar, message.isStarred)
            setEmojisCardView(textMessageThreadViewHolder.llEmojis, textMessageThreadViewHolder.emojiLayoutList, textMessageThreadViewHolder.emojiTextList, textMessageThreadViewHolder.emojiCountList, message, position)
            setArrowClick(message, textMessageThreadViewHolder.icDownArrow, false, textMessageThreadViewHolder.tvTime)
        }

        try {
            val options = RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .fitCenter()
                    .priority(Priority.HIGH)
                    .transforms(CenterCrop(), RoundedCorners(10))

            Glide.with(mContext as FuguInnerChatActivity)
                    .asBitmap()
                    .apply(options)
                    .load(message.userImage)
                    .into(textMessageThreadViewHolder.ivUserImage)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val date = DateUtils.getDate(dateUtil.convertToLocalWithoutYear(message.sentAtUtc))
            if (message.messageState == 4) {
                textMessageThreadViewHolder.tvTime.text = date + " at " + DateUtils.getTime(dateUtil.convertToLocal(message.sentAtUtc))
                textMessageThreadViewHolder.tvTime.append(Html.fromHtml("<font><small>&nbsp;&nbsp;(edited)</small></font>"))
            } else {
                textMessageThreadViewHolder.tvTime.text = date + " at " + DateUtils.getTime(dateUtil.convertToLocal(message.sentAtUtc))
            }
        } catch (e: Exception) {
            textMessageThreadViewHolder.tvTime.text = ""
        }

        if (message.threadMessageCount == 0) {
            textMessageThreadViewHolder.tvReplyCount.text = "No replies"
        } else if (message.threadMessageCount == 1) {
            textMessageThreadViewHolder.tvReplyCount.text = (message.threadMessageCount).toString() + " reply"
        } else {
            textMessageThreadViewHolder.tvReplyCount.text = (message.threadMessageCount).toString() + " replies"
        }
        //setEmojis(textMessageThreadViewHolder.llEmojis, textMessageThreadViewHolder.emojiLayoutList, textMessageThreadViewHolder.emojiTextList, textMessageThreadViewHolder.emojiCountList, message)
        textMessageThreadViewHolder.tvUserName.text = message.getfromName()


    }

    private fun setImageMessageThread(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, message: Message) {
        val imageMessageThreadViewHolder: ThreadImageMessageViewHolder = holder as ThreadImageMessageViewHolder

        try {
            val options = RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .fitCenter()
                    .priority(Priority.HIGH)
                    .transforms(CenterCrop(), RoundedCorners(10))

            Glide.with(mContext as FuguInnerChatActivity)
                    .asBitmap()
                    .apply(options)
                    .load(message.userImage)
                    .into(imageMessageThreadViewHolder.ivUserImage)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (message.messageState == 0) {
            //rlImageMessage.setVisibility(View.GONE)
            imageMessageThreadViewHolder.tvUserMessage.visibility = View.VISIBLE
            imageMessageThreadViewHolder.ivDeleted.visibility = View.VISIBLE
            imageMessageThreadViewHolder.rlImageMessage.visibility = View.GONE
            imageMessageThreadViewHolder.tvUserImageMessage.visibility = View.GONE
            imageMessageThreadViewHolder.llEmojis.visibility = View.GONE

            if (message.userId!!.compareTo(java.lang.Long.valueOf(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userId)) == 0) {
                imageMessageThreadViewHolder.tvUserMessage.text = "You deleted this message"
                imageMessageThreadViewHolder.tvUserMessage.setTextColor(mContext.resources.getColor(R.color.deleted_message_color))
                imageMessageThreadViewHolder.tvUserMessage.setTypeface(imageMessageThreadViewHolder.tvUserMessage.typeface, Typeface.ITALIC)
            } else {
                imageMessageThreadViewHolder.tvUserMessage.text = "This message was deleted"
                imageMessageThreadViewHolder.tvUserMessage.setTextColor(mContext.resources.getColor(R.color.deleted_message_color))
                imageMessageThreadViewHolder.tvUserMessage.setTypeface(imageMessageThreadViewHolder.tvUserMessage.typeface, Typeface.ITALIC)
            }
        } else {

            //rlImageMessage.setVisibility(View.VISIBLE)
            imageMessageThreadViewHolder.rlImageMessage.visibility = View.VISIBLE
            imageMessageThreadViewHolder.tvUserImageMessage.visibility = View.VISIBLE
            imageMessageThreadViewHolder.tvUserMessage.visibility = View.GONE
            imageMessageThreadViewHolder.ivDeleted.visibility = View.GONE
            if (!TextUtils.isEmpty(message.message)) {
                imageMessageThreadViewHolder.tvUserImageMessage.visibility = View.VISIBLE
                var messageString = ""
                if (!TextUtils.isEmpty(message.alteredMessage)) {
                    messageString = message.alteredMessage
                } else {
                    messageString = message.message
                }
                manipulateAndSetText(imageMessageThreadViewHolder.tvUserImageMessage, messageString, message.messageState)
            } else {
                imageMessageThreadViewHolder.tvUserImageMessage.visibility = View.GONE
            }

            setImage(imageMessageThreadViewHolder.pbDownloading, imageMessageThreadViewHolder.llDownloadImage, imageMessageThreadViewHolder.ivImageMessage, message, position)
            var extension = message.image_url!!.split(".")[message.image_url.split(".").size - 1]

            if (extension.toLowerCase().equals("png")) {
                extension = "jpg"
            }
            val fileName = message.fileName + "_" + message.muid + "." + extension
            val filePathNormal = File(getNormalDirectory(extension) + "/" + fileName)
            val filePathPrivate = File(getPrivateDirectory(extension) + "/" + fileName)

            if (workspaceInfo?.get(currentPos)?.autoDownloadLevel!!.equals(FuguAppConstant.AutoDownloadLevel.BOTH.toString())
                    || checkConnection(imageMessageThreadViewHolder.itemView.context).equals(workspaceInfo?.get(currentPos)?.autoDownloadLevel)) {
                if (!filePathNormal.exists() && !filePathPrivate.exists() && message.downloadStatus != FuguAppConstant.DownloadStatus.DOWNLOAD_IN_PROGRESS.downloadStatus
                        && message.downloadStatus != FuguAppConstant.DownloadStatus.DOWNLOAD_COMPLETED.downloadStatus) {
                    if (!extension.toLowerCase().equals("gif") && checkAndObtainStoragePermission()) {
                        val downloadId = downloadFileFromUrl(extension, message, position, "Image")
                        message.downloadId = downloadId
                        imageMessageThreadViewHolder.pbDownloading.visibility = View.VISIBLE
                        imageMessageThreadViewHolder.llDownloadImage.visibility = View.GONE
                    }
                }
            }


            if (!TextUtils.isEmpty(message.fileSize)) {
                imageMessageThreadViewHolder.tvImageSize.visibility = View.VISIBLE
                imageMessageThreadViewHolder.tvImageSize.text = message.fileSize
            } else {
                imageMessageThreadViewHolder.tvImageSize.visibility = View.GONE
            }

            imageMessageThreadViewHolder.llDownloadImage.setOnClickListener {
                if (checkAndObtainStoragePermission()) {
                    val extension = message.image_url!!.split(".")[message.image_url.split(".").size - 1]
                    val downloadId = downloadFileFromUrl(extension, message, position, "Image")
                    message.downloadId = downloadId
                    imageMessageThreadViewHolder.pbDownloading.visibility = View.VISIBLE
                    imageMessageThreadViewHolder.llDownloadImage.visibility = View.GONE
                }
            }

            setArrowClick(message, imageMessageThreadViewHolder.icDownArrow, false, imageMessageThreadViewHolder.tvTime)
            setItemLongClick(imageMessageThreadViewHolder.itemView, message, false, imageMessageThreadViewHolder.tvTime)
            setItemLongClick(imageMessageThreadViewHolder.ivImageMessage, message, false, imageMessageThreadViewHolder.tvTime)
            setEmojisCardView(imageMessageThreadViewHolder.llEmojis, imageMessageThreadViewHolder.emojiLayoutList, imageMessageThreadViewHolder.emojiTextList, imageMessageThreadViewHolder.emojiCountList, message, position)
            setStarredMessage(imageMessageThreadViewHolder.ivStar, message.isStarred)

            imageMessageThreadViewHolder.ivImageMessage.setOnClickListener {
                if (isChatActivity()) {
                    if ((mContext as ChatActivity).getState() != MotionEvent.ACTION_MOVE) {
                        try {
                            imageMessageThreadViewHolder.ivImageMessage.transitionName = message.id.toString()
                            val imageIntent = Intent(mContext, ImageDisplayViewpagerActivity::class.java)
                            var extension = message.image_url!!.split(".")[message.image_url.split(".").size - 1]
                            if (extension.toLowerCase().equals("png")) {
                                extension = "jpg"
                            }
                            val fileName = message.fileName + "_" + message.muid + "." + extension
                            val filePathNormal = File(getNormalDirectory(extension) + "/" + fileName)
                            val filePathPrivate = File(getPrivateDirectory(extension) + "/" + fileName)
                            var link = ""
                            if (filePathNormal.exists()) {
                                link = filePathNormal.absolutePath
                            } else {
                                link = filePathPrivate.absolutePath
                            }
                            var isImageFound = false
                            if (filePathNormal.exists() || filePathPrivate.exists()) {
                                if (extension == "gif") {
                                    link = message.image_url
                                }
                                for (i in 0 until imageFiles.size) {
                                    var pos = i
                                    if (imageFiles[i].absolutepath == link) {

                                        if (imageFiles[i].transitionName === null || imageFiles[i].transitionName == "0" || imageFiles[i].transitionName != message.id.toString()) {
                                            val exif = androidx.exifinterface.media.ExifInterface(link)
                                            exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_DATETIME, message.sentAtUtc)
                                            exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MODEL, message.id.toString())
                                            exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MAKE, channelId.toString() + muid)
                                            exif.saveAttributes()
                                            imageFiles[i].transitionName = message.id.toString()
                                            imageFiles.sortWith(Comparator { one, other -> other.transitionName!!.compareTo(one.transitionName!!) })
                                            imageFiles.reverse()
                                            for (x in 0 until imageFiles.size) {
                                                if (imageFiles[x].absolutepath == link) {
                                                    pos = x
                                                }
                                            }
                                        }

                                        if (isChatActivity()) {
                                            ChatActivity.PagerPosition.currentViewPagerPosition = pos
                                            imageIntent.putExtra("currentPos", ChatActivity.PagerPosition.currentViewPagerPosition)
                                        } else {
                                            FuguInnerChatActivity.currentViewPagerposition = pos
                                            imageIntent.putExtra("currentPos", FuguInnerChatActivity.currentViewPagerposition)
                                        }
                                        isImageFound = true
                                        break
                                    } else {
                                        ChatActivity.PagerPosition.currentViewPagerPosition = 0
                                        FuguInnerChatActivity.currentViewPagerposition = 0
                                    }
                                }
                                if (!isImageFound) {
                                    try {
                                        val file = File(link)
                                        if (file.exists()) {
                                            val exif = androidx.exifinterface.media.ExifInterface(link)
                                            exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_DATETIME, message.sentAtUtc)
                                            exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MODEL, message.id.toString())
                                            exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MAKE, channelId.toString() + muid)
                                            exif.saveAttributes()
                                            messageMap[message.muid] = message
                                            getFromSdcard()
                                            for (i in 0 until imageFiles.size) {
                                                if (imageFiles[i].absolutepath == link) {
                                                    if (isChatActivity()) {
                                                        ChatActivity.PagerPosition.currentViewPagerPosition = i
                                                        imageIntent.putExtra("currentPos", ChatActivity.PagerPosition.currentViewPagerPosition)
                                                    } else {
                                                        FuguInnerChatActivity.currentViewPagerposition = i
                                                        imageIntent.putExtra("currentPos", FuguInnerChatActivity.currentViewPagerposition)
                                                    }
                                                    isImageFound = true
                                                    break
                                                } else {
                                                    ChatActivity.PagerPosition.currentViewPagerPosition = 0
                                                    FuguInnerChatActivity.currentViewPagerposition = 0
                                                }
                                            }
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                Log.e("CurrentPos", ChatActivity.PagerPosition.currentViewPagerPosition.toString())
                                val options: ActivityOptionsCompat? = if (mContext is ChatActivity) {
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(mContext as ChatActivity,
                                            imageMessageThreadViewHolder.ivImageMessage as View, imageFiles[ChatActivity.PagerPosition.currentViewPagerPosition].transitionName)
                                } else {
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(mContext as FuguInnerChatActivity,
                                            imageMessageThreadViewHolder.ivImageMessage as View, imageFiles[FuguInnerChatActivity.currentViewPagerposition].transitionName)
                                }
                                val presentViewHolderList = ArrayList<String>()
                                val first: Int
                                val last: Int
                                if (recyclerView.layoutManager is CustomLinearLayoutManager) {
                                    first = (recyclerView.layoutManager as CustomLinearLayoutManager).findFirstVisibleItemPosition()
                                    last = (recyclerView.layoutManager as CustomLinearLayoutManager).findLastVisibleItemPosition()
                                } else {
                                    first = (recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).findFirstVisibleItemPosition()
                                    last = (recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).findLastVisibleItemPosition()
                                }
                                for (item in first..last) {
                                    presentViewHolderList.add(messageList[item].id.toString())
                                }
                                imageIntent.putExtra("isChatActivity", isChatActivity())
                                imageIntent.putExtra("imageList", imageFiles)
                                imageIntent.putExtra("presentViewHolderList", presentViewHolderList)
                                imageIntent.putExtra("channelName", label)
                                imageIntent.putExtra("MESSAGE", message)
                                imageIntent.putExtra("chatType", chatType)
                                imageIntent.putExtra(CHANNEL_ID, channelId)
                                imageIntent.putExtra(APP_SECRET_KEY, workspaceInfo?.get(currentPos)?.fuguSecretKey)
                                if (options != null) {
                                    mContext.startActivity(imageIntent, options.toBundle())
                                }
                            } else if (extension == "gif") {
                                val imageIntent = Intent(mContext, ImageDisplayActivity::class.java)
                                var extension = message.image_url!!.split(".")[message.image_url.split(".").size - 1]
                                if (extension.toLowerCase() == "png") {
                                    extension = "jpg"
                                }
                                val fileName = message.fileName + "_" + message.muid + "." + extension
                                val filePathNormal = File(getNormalDirectory(extension) + "/" + fileName)
                                val filePathPrivate = File(getPrivateDirectory(extension) + "/" + fileName)
                                var link = ""
                                link = if (filePathNormal.exists()) {
                                    filePathNormal.absolutePath
                                } else {
                                    filePathPrivate.absolutePath
                                }
                                if (filePathNormal.exists() || filePathPrivate.exists() || extension == "gif") {
                                    if (extension == "gif") {
                                        link = message.image_url
                                    }

                                    val image = Image(link, link, message.muid, message.sentAtUtc, label)
                                    imageIntent.putExtra("image", image)
                                    imageIntent.putExtra("MESSAGE", message)
                                    imageIntent.putExtra("BUSINESS_NAME", label)
                                    imageIntent.putExtra("chatType", chatType)
                                    mContext.startActivity(imageIntent)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    try {
                        if (!isChatActivity() && position == 0) {
                            imageMessageThreadViewHolder.ivImageMessage.transitionName = "imageOne"
                            val imageIntent = Intent(mContext, ImageDisplayActivity::class.java)
                            var extension = message.image_url!!.split(".")[message.image_url.split(".").size - 1]
                            if (extension.toLowerCase() == "png") {
                                extension = "jpg"
                            }
                            val fileName = message.fileName + "_" + message.muid + "." + extension
                            val filePathNormal = File(getNormalDirectory(extension) + "/" + fileName)
                            val filePathPriavte = File(getPrivateDirectory(extension) + "/" + fileName)
                            var link = ""
                            var options: ActivityOptionsCompat? = null

                            options = ActivityOptionsCompat.makeSceneTransitionAnimation(fuguInnerChatActivity,
                                    imageMessageThreadViewHolder.ivImageMessage, "imageOne")

                            if (filePathNormal.exists()) {
                                link = filePathNormal.absolutePath
                            } else {
                                link = filePathPriavte.absolutePath
                            }
                            if (filePathNormal.exists() || filePathPriavte.exists() || extension == "gif") {
                                if (extension == "gif") {
                                    link = message.image_url
                                }

                                val image = Image(link, link, "imageOne", message.sentAtUtc, label)
                                imageIntent.putExtra("image", image)
                                imageIntent.putExtra("MESSAGE", message)
                                imageIntent.putExtra("BUSINESS_NAME", label)
                                imageIntent.putExtra("chatType", chatType)
                                mContext.startActivity(imageIntent, options.toBundle())
                            }
                        } else {
                            imageMessageThreadViewHolder.ivImageMessage.transitionName = message.id.toString()
                            val imageIntent = Intent(mContext, ImageDisplayViewpagerActivity::class.java)
                            var extension = message.image_url!!.split(".")[message.image_url.split(".").size - 1]
                            if (extension.toLowerCase().equals("png")) {
                                extension = "jpg"
                            }
                            val fileName = message.fileName + "_" + message.muid + "." + extension
                            val filePathNormal = File(getNormalDirectory(extension) + "/" + fileName)
                            val filePathPrivate = File(getPrivateDirectory(extension) + "/" + fileName)
                            var link = ""
                            if (filePathNormal.exists()) {
                                link = filePathNormal.absolutePath
                            } else {
                                link = filePathPrivate.absolutePath
                            }
                            var isImageFound = false
                            if (filePathNormal.exists() || filePathPrivate.exists()) {
                                if (extension == "gif") {
                                    link = message.image_url
                                }
                                var options: ActivityOptionsCompat? = null
                                for (i in 0 until imageFiles.size) {
                                    var pos = i
                                    if (imageFiles[i].absolutepath == link) {

                                        if (imageFiles[i].transitionName === null || imageFiles[i].transitionName == "0" || imageFiles[i].transitionName != message.id.toString()) {
                                            val exif = androidx.exifinterface.media.ExifInterface(link)
                                            exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_DATETIME, message.sentAtUtc)
                                            exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MODEL, message.id.toString())
                                            exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MAKE, channelId.toString() + muid)
                                            exif.saveAttributes()
                                            imageFiles[i].transitionName = message.id.toString()
                                            imageFiles.sortWith(Comparator { one, other -> other.transitionName!!.compareTo(one.transitionName!!) })
                                            imageFiles.reverse()
                                            for (x in 0 until imageFiles.size) {
                                                if (imageFiles[x].absolutepath == link) {
                                                    pos = x
                                                }
                                            }
                                        }

                                        if (isChatActivity()) {
                                            ChatActivity.PagerPosition.currentViewPagerPosition = pos
                                            imageIntent.putExtra("currentPos", ChatActivity.PagerPosition.currentViewPagerPosition)
                                        } else {
                                            FuguInnerChatActivity.currentViewPagerposition = pos
                                            imageIntent.putExtra("currentPos", FuguInnerChatActivity.currentViewPagerposition)
                                        }
                                        isImageFound = true
                                        break
                                    } else {
                                        ChatActivity.PagerPosition.currentViewPagerPosition = 0
                                        FuguInnerChatActivity.currentViewPagerposition = 0
                                    }
                                }
                                if (!isImageFound) {
                                    try {
                                        val file = File(link)
                                        if (file.exists()) {
                                            val exif = androidx.exifinterface.media.ExifInterface(link)
                                            exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_DATETIME, message.sentAtUtc)
                                            exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MODEL, message.id.toString())
                                            exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MAKE, channelId.toString() + muid)
                                            exif.saveAttributes()
                                            messageMap[message.muid] = message
                                            getFromSdcard()
                                            for (i in 0 until imageFiles.size) {
                                                if (imageFiles[i].absolutepath == link) {
                                                    if (isChatActivity()) {
                                                        ChatActivity.PagerPosition.currentViewPagerPosition = i
                                                        imageIntent.putExtra("currentPos", ChatActivity.PagerPosition.currentViewPagerPosition)
                                                    } else {
                                                        FuguInnerChatActivity.currentViewPagerposition = i
                                                        imageIntent.putExtra("currentPos", FuguInnerChatActivity.currentViewPagerposition)
                                                    }
                                                    isImageFound = true
                                                    break
                                                } else {
                                                    ChatActivity.PagerPosition.currentViewPagerPosition = 0
                                                    FuguInnerChatActivity.currentViewPagerposition = 0
                                                }
                                            }
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                Log.e("CurrentPos", ChatActivity.PagerPosition.currentViewPagerPosition.toString())
                                options = if (mContext is ChatActivity) {
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(mContext as ChatActivity,
                                            imageMessageThreadViewHolder.ivImageMessage as View, imageFiles[ChatActivity.PagerPosition.currentViewPagerPosition].transitionName)
                                } else {
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(mContext as FuguInnerChatActivity,
                                            imageMessageThreadViewHolder.ivImageMessage as View, imageFiles[FuguInnerChatActivity.currentViewPagerposition].transitionName)
                                }
                                val presentViewHolderList = ArrayList<String>()
                                var first: Int
                                var last: Int
                                if (recyclerView.layoutManager is CustomLinearLayoutManager) {
                                    first = (recyclerView.layoutManager as CustomLinearLayoutManager).findFirstVisibleItemPosition()
                                    last = (recyclerView.layoutManager as CustomLinearLayoutManager).findLastVisibleItemPosition()
                                } else {
                                    first = (recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).findFirstVisibleItemPosition()
                                    last = (recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).findLastVisibleItemPosition()
                                }
                                for (item in first..last) {
                                    presentViewHolderList.add(messageList[item].id.toString())
                                }
                                imageIntent.putExtra("isChatActivity", isChatActivity())
                                imageIntent.putExtra("imageList", imageFiles)
                                imageIntent.putExtra("presentViewHolderList", presentViewHolderList)
                                imageIntent.putExtra("channelName", label)
                                imageIntent.putExtra("MESSAGE", message)
                                imageIntent.putExtra("chatType", chatType)
                                imageIntent.putExtra(CHANNEL_ID, channelId)
                                imageIntent.putExtra(APP_SECRET_KEY, workspaceInfo?.get(currentPos)?.fuguSecretKey)
                                mContext.startActivity(imageIntent, options.toBundle())
                            } else if (extension == "gif") {
                                val imageIntent = Intent(mContext, ImageDisplayActivity::class.java)
                                var extension = message.image_url!!.split(".")[message.image_url.split(".").size - 1]
                                if (extension.toLowerCase() == "png") {
                                    extension = "jpg"
                                }
                                val fileName = message.fileName + "_" + message.muid + "." + extension
                                val filePathNormal = File(getNormalDirectory(extension) + "/" + fileName)
                                val filePathPriavte = File(getPrivateDirectory(extension) + "/" + fileName)
                                var link = ""
                                if (filePathNormal.exists()) {
                                    link = filePathNormal.absolutePath
                                } else {
                                    link = filePathPriavte.absolutePath
                                }
                                if (filePathNormal.exists() || filePathPriavte.exists() || extension.equals("gif")) {
                                    if (extension == "gif") {
                                        link = message.image_url
                                    }

                                    val image = Image(link, link, message.muid, message.sentAtUtc, label)
                                    imageIntent.putExtra("image", image)
                                    imageIntent.putExtra("MESSAGE", message)
                                    imageIntent.putExtra("BUSINESS_NAME", label)
                                    imageIntent.putExtra("chatType", chatType)
                                    mContext.startActivity(imageIntent)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }



        try {
            imageMessageThreadViewHolder.tvImageTime.text = DateUtils.getTime(dateUtil.convertToLocal(message.sentAtUtc))
            val date = DateUtils.getDate(dateUtil.convertToLocalWithoutYear(message.sentAtUtc))
            if (message.messageState == 4) {
                imageMessageThreadViewHolder.tvTime.text = date + " at " + DateUtils.getTime(dateUtil.convertToLocal(message.sentAtUtc))
                imageMessageThreadViewHolder.tvTime.append(Html.fromHtml("<font><small>&nbsp;&nbsp;(edited)</small></font>"))
            } else {
                imageMessageThreadViewHolder.tvTime.text = date + " at " + DateUtils.getTime(dateUtil.convertToLocal(message.sentAtUtc))
            }
        } catch (e: Exception) {

        }

        imageMessageThreadViewHolder.tvUserName.text = message.getfromName()

        if (message.threadMessageCount == 0) {
            imageMessageThreadViewHolder.tvReplyCount.text = "No replies"
        } else if (message.threadMessageCount == 1) {
            imageMessageThreadViewHolder.tvReplyCount.text = (message.threadMessageCount).toString() + " reply"
        } else {
            imageMessageThreadViewHolder.tvReplyCount.text = (message.threadMessageCount).toString() + " replies"
        }

//        imageMessageThreadViewHolder.ivImageMessage.setOnClickListener {
//            showImageDialogFuguInnerChat(mContext as FuguInnerChatActivity, message.image_url)
//        }

    }

    private fun setVideoMessageThread(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, message: Message) {
        val videoMessageThreadViewHolder: ThreadVideoMessageViewHolder = holder as ThreadVideoMessageViewHolder
        // videoMessageThreadViewHolder.ivPlay.visibility = View.VISIBLE
        if (message.messageState == 0) {
            videoMessageThreadViewHolder.tvUserMessage.visibility = View.VISIBLE
            videoMessageThreadViewHolder.tvUserImageMessage.visibility = View.GONE
            videoMessageThreadViewHolder.ivDeleted.visibility = View.VISIBLE
            videoMessageThreadViewHolder.rlImageMessage.visibility = View.GONE
            videoMessageThreadViewHolder.ivPlay.visibility = View.GONE
            videoMessageThreadViewHolder.llEmojis.visibility = View.GONE
            if (message.userId!!.compareTo(java.lang.Long.valueOf(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userId)) == 0) {
                videoMessageThreadViewHolder.tvUserMessage.text = "You deleted this message"
                videoMessageThreadViewHolder.tvUserMessage.setTextColor(mContext.resources.getColor(R.color.deleted_message_color))
                videoMessageThreadViewHolder.tvUserMessage.setTypeface(videoMessageThreadViewHolder.tvUserMessage.typeface, Typeface.ITALIC)
            } else {
                videoMessageThreadViewHolder.tvUserMessage.text = "This message was deleted"
                videoMessageThreadViewHolder.tvUserMessage.setTextColor(mContext.resources.getColor(R.color.deleted_message_color))
                videoMessageThreadViewHolder.tvUserMessage.setTypeface(videoMessageThreadViewHolder.tvUserMessage.typeface, Typeface.ITALIC)
            }
        } else {
            videoMessageThreadViewHolder.tvUserImageMessage.visibility = View.VISIBLE
            videoMessageThreadViewHolder.tvUserMessage.visibility = View.GONE
            videoMessageThreadViewHolder.ivDeleted.visibility = View.GONE
            videoMessageThreadViewHolder.rlImageMessage.visibility = View.VISIBLE
            if (!TextUtils.isEmpty(message.message)) {
                videoMessageThreadViewHolder.tvUserImageMessage.visibility = View.VISIBLE
                //val formattedStrings3 = FormatStringUtil.FormatString.getFormattedString(message.message)
                //videoMessageThreadViewHolder.tvUserImageMessage.setText(Html.fromHtml(formattedStrings3[0]))
                manipulateAndSetText(videoMessageThreadViewHolder.tvUserImageMessage, message.message, message.messageState)
            } else {
                videoMessageThreadViewHolder.tvUserImageMessage.visibility = View.GONE
            }
            setArrowClick(message, videoMessageThreadViewHolder.icDownArrow, false, videoMessageThreadViewHolder.tvTime)
            setItemLongClick(videoMessageThreadViewHolder.itemView, message, false, videoMessageThreadViewHolder.tvTime)
            setItemLongClick(videoMessageThreadViewHolder.ivMsgImage, message, false, videoMessageThreadViewHolder.tvTime)
            videoMessageThreadViewHolder.tvFileSize.text = message.fileSize
            setThreadVideoDownloadStatus(videoMessageThreadViewHolder.llDownload, videoMessageThreadViewHolder.circleProgress, videoMessageThreadViewHolder.ivPlay, message, videoMessageThreadViewHolder.ivCrossCancel)
            setEmojisCardView(videoMessageThreadViewHolder.llEmojis, videoMessageThreadViewHolder.emojiLayoutList, videoMessageThreadViewHolder.emojiTextList, videoMessageThreadViewHolder.emojiCountList, message, position)
            setStarredMessage(videoMessageThreadViewHolder.ivStar, message.isStarred)
        }
        try {
            videoMessageThreadViewHolder.tvImageTime.text = DateUtils.getTime(dateUtil.convertToLocal(message.sentAtUtc))
            val date = DateUtils.getDate(dateUtil.convertToLocalWithoutYear(message.sentAtUtc))
            if (message.messageState == 4) {
                videoMessageThreadViewHolder.tvTime.text = date + " at " + DateUtils.getTime(dateUtil.convertToLocal(message.sentAtUtc))
                videoMessageThreadViewHolder.tvTime.append(Html.fromHtml("<font><small>&nbsp;&nbsp;(edited)</small></font>"))
            } else {
                videoMessageThreadViewHolder.tvTime.text = date + " at " + DateUtils.getTime(dateUtil.convertToLocal(message.sentAtUtc))
            }
        } catch (e: Exception) {

        }
        val requestOptions = RequestOptions()
                .dontAnimate()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transforms(CenterCrop(), RoundedCorners(10))
        try {
            if (mContext is ChatActivity) {
                Glide.with(mContext as ChatActivity)
                        .asBitmap()
                        .apply(requestOptions)
                        .load(message.thumbnailUrl)
                        .into(videoMessageThreadViewHolder.ivMsgImage)
            } else {
                Glide.with(mContext as FuguInnerChatActivity)
                        .asBitmap()
                        .apply(requestOptions)
                        .load(message.thumbnailUrl)
                        .into(videoMessageThreadViewHolder.ivMsgImage)
            }
        } catch (e: Exception) {

        }

        videoMessageThreadViewHolder.llDownload.setOnClickListener {
            if (getIsNetworkConnected() && checkAndObtainStoragePermission()) {
                performClickCount = 1
                val fullPath = Environment.getExternalStorageDirectory().absolutePath + "/Download"
                downloadIdVideo = downloadFileFromUrl(fullPath, message, position, "Video")
                if (downloadIdVideo != 1) {
                    message.downloadId = downloadIdVideo
                }
                holder.circleProgress.visibility = View.VISIBLE
                holder.ivCrossCancel.visibility = View.VISIBLE
                holder.llDownload.visibility = View.GONE
            }
        }

        videoMessageThreadViewHolder.rlDownloading.setOnClickListener {
            if (downloadIdVideo != 0) {
                PRDownloader.pause(downloadIdVideo)
                Log.e("downloadIdVideo", downloadIdVideo.toString())
            } else if (message.downloadId != 0) {
                PRDownloader.pause(message.downloadId)
                Log.e("message.downloadId", (message.downloadId).toString())
            }
            message.downloadStatus = DOWNLOAD_FAILED
            holder.llDownload.visibility = View.VISIBLE
            holder.circleProgress.visibility = View.GONE
            holder.ivCrossCancel.visibility = View.GONE
        }

        videoMessageThreadViewHolder.ivPlay.setOnClickListener {
            try {
                if (isChatActivity()) {
                    val mIntent = Intent(chatActivity, VideoPlayerActivity::class.java)
                    mIntent.putExtra("url", message.filePath)
                    chatActivity?.startActivity(mIntent)

                } else {
                    val mIntent = Intent(fuguInnerChatActivity, VideoPlayerActivity::class.java)
                    mIntent.putExtra("url", message.filePath)
                    fuguInnerChatActivity.startActivity(mIntent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (message.downloadId != 0 && message.downloadStatus == DOWNLOAD_IN_PROGRESS && performClickCount == 0) {
            Log.e("Download Started", "Download Started")
            performClickCount = 1
            //videoMessageThreadViewHolder.llDownload.performClick()
            if (getIsNetworkConnected() && checkAndObtainStoragePermission()) {
                performClickCount = 1
                val fullPath = Environment.getExternalStorageDirectory().absolutePath + "/Download"
                downloadIdVideo = downloadFileFromUrl(fullPath, message, position, "Video")
                if (downloadIdVideo != 0) {
                    message.downloadId = downloadIdVideo
                }
                holder.circleProgress.visibility = View.VISIBLE
                holder.ivCrossCancel.visibility = View.VISIBLE
                holder.llDownload.visibility = View.GONE
            }
        }

        try {
            val options = RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .fitCenter()
                    .priority(Priority.HIGH)
                    .transforms(CenterCrop(), RoundedCorners(10))

            Glide.with(mContext as FuguInnerChatActivity)
                    .asBitmap()
                    .apply(options)
                    .load(message.userImage)
                    .into(videoMessageThreadViewHolder.ivUserImage)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (message.threadMessageCount == 0) {
            videoMessageThreadViewHolder.tvReplyCount.text = "No replies"
        } else if (message.threadMessageCount == 1) {
            videoMessageThreadViewHolder.tvReplyCount.text = (message.threadMessageCount).toString() + " reply"
        } else {
            videoMessageThreadViewHolder.tvReplyCount.text = (message.threadMessageCount).toString() + " replies"
        }

        videoMessageThreadViewHolder.tvUserName.text = message.getfromName()
    }

    private fun setFileMessageThread(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, message: Message) {
        val fileMessageThreadViewHolder: ThreadFileMessageViewHolder = holder as ThreadFileMessageViewHolder
        if (message.messageState == 0) {
            fileMessageThreadViewHolder.llFile.visibility = View.GONE
            fileMessageThreadViewHolder.ivDeleted.visibility = View.VISIBLE
            fileMessageThreadViewHolder.tvUserMessage.visibility = View.VISIBLE
            fileMessageThreadViewHolder.llEmojis.visibility = View.GONE
            if (message.userId!!.compareTo(java.lang.Long.valueOf(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userId)) == 0) {
                fileMessageThreadViewHolder.tvUserMessage.text = "You deleted this message"
                fileMessageThreadViewHolder.tvUserMessage.setTextColor(mContext.resources.getColor(R.color.deleted_message_color))
                fileMessageThreadViewHolder.tvUserMessage.setTypeface(fileMessageThreadViewHolder.tvUserMessage.typeface, Typeface.ITALIC)
            } else {
                fileMessageThreadViewHolder.tvUserMessage.text = "This message was deleted"
                fileMessageThreadViewHolder.tvUserMessage.setTextColor(mContext.resources.getColor(R.color.deleted_message_color))
                fileMessageThreadViewHolder.tvUserMessage.setTypeface(fileMessageThreadViewHolder.tvUserMessage.typeface, Typeface.ITALIC)
            }
        } else {
            fileMessageThreadViewHolder.llFile.visibility = View.VISIBLE
            fileMessageThreadViewHolder.ivDeleted.visibility = View.GONE
            fileMessageThreadViewHolder.tvUserMessage.visibility = View.GONE

            var image = IMAGE_MAP[message.fileExtension.toLowerCase()]
            if (image == null) {
                val mimeType = FuguMimeUtils.guessMimeTypeFromExtension(message.fileExtension.toLowerCase())
                if (mimeType != null)
                    image = IMAGE_MAP[mimeType.split("/")[0]]
            }
            if (image != null) {
                fileMessageThreadViewHolder.ivFileImage.setImageResource(image)
//                fileMessageThreadViewHolder.ivFileImage.setColorFilter(Color.parseColor("#b3bec9"))
                fileMessageThreadViewHolder.tvFileExt.visibility = View.GONE
            } else {
                fileMessageThreadViewHolder.ivFileImage.setImageResource(R.drawable.file_model)
//                fileMessageThreadViewHolder.ivFileImage.clearColorFilter()
                fileMessageThreadViewHolder.tvFileExt.visibility = View.VISIBLE
            }

            fileMessageThreadViewHolder.tvFileName.text = message.fileName

            if (message.fileExtension.length > 5) {
                fileMessageThreadViewHolder.tvFileExtension.text = "File"
            } else {
                fileMessageThreadViewHolder.tvFileExtension.text = message.fileExtension
                fileMessageThreadViewHolder.tvFileExt.text = message.fileExtension.toUpperCase()
            }

            if (SUPPORTED_AUDIO_FORMATS.contains(message.fileExtension)) {
                fileMessageThreadViewHolder.ivFilePlay.visibility = View.VISIBLE
            } else {
                fileMessageThreadViewHolder.ivFilePlay.visibility = View.GONE
                setFileCLickListener(fileMessageThreadViewHolder.llFile, message)
            }

            if (FuguAppConstant.SUPPORTED_AUDIO_FORMATS.contains(message.fileExtension.toLowerCase())) {
                if (!message.isAudioPlaying) {
                    fileMessageThreadViewHolder.ivFilePlay.setImageResource(R.drawable.music_player)
                } else {
                    fileMessageThreadViewHolder.ivFilePlay.setImageResource(R.drawable.song_pause)
                }
            }

            setDownloadClick(fileMessageThreadViewHolder.ivFileDownload, fileMessageThreadViewHolder.circleProgress, message, position)

            setThreadFileDownloadStatus(fileMessageThreadViewHolder.ivFileDownload, fileMessageThreadViewHolder.circleProgress, fileMessageThreadViewHolder.ivFilePlay, message, position)
            fileMessageThreadViewHolder.ivFilePlay.setImageResource(R.drawable.music_player)
            fileMessageThreadViewHolder.tvFileSize.text = message.fileSize

            fileMessageThreadViewHolder.tvFileName.text = message.fileName
            setArrowClick(message, fileMessageThreadViewHolder.icDownArrow, false, fileMessageThreadViewHolder.tvTime)
            setItemLongClick(fileMessageThreadViewHolder.itemView, message, false, fileMessageThreadViewHolder.tvTime)
            setItemLongClick(fileMessageThreadViewHolder.llFile, message, false, fileMessageThreadViewHolder.tvTime)
            setEmojisCardView(fileMessageThreadViewHolder.llEmojis, fileMessageThreadViewHolder.emojiLayoutList, fileMessageThreadViewHolder.emojiTextList, fileMessageThreadViewHolder.emojiCountList, message, position
            )
            setStarredMessage(fileMessageThreadViewHolder.ivStar, message.isStarred)
        }
        try {
            val date = DateUtils.getDate(dateUtil.convertToLocalWithoutYear(message.sentAtUtc))
            if (message.messageState == 4) {
                fileMessageThreadViewHolder.tvTime.text = date + " at " + DateUtils.getTime(dateUtil.convertToLocal(message.sentAtUtc))
                fileMessageThreadViewHolder.tvTime.append(Html.fromHtml("<font><small>&nbsp;&nbsp;(edited)</small></font>"))
            } else {
                fileMessageThreadViewHolder.tvTime.text = date + " at " + DateUtils.getTime(dateUtil.convertToLocal(message.sentAtUtc))
            }
        } catch (e: Exception) {

        }
        fileMessageThreadViewHolder.tvUserName.text = message.getfromName()

        if (message.threadMessageCount == 0) {
            fileMessageThreadViewHolder.tvReplyCount.text = "No replies"
        } else if (message.threadMessageCount == 1) {
            fileMessageThreadViewHolder.tvReplyCount.text = (message.threadMessageCount).toString() + " reply"
        } else {
            fileMessageThreadViewHolder.tvReplyCount.text = (message.threadMessageCount).toString() + " replies"
        }

        //fileMessageThreadViewHolder.tvFileTime.text = DateUtils.getTime(dateUtil.convertToLocal(message.sentAtUtc))
        try {
            val options = RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .fitCenter()
                    .priority(Priority.HIGH)
                    .transforms(CenterCrop(), RoundedCorners(10))

            Glide.with(mContext as FuguInnerChatActivity)
                    .asBitmap()
                    .apply(options)
                    .load(message.userImage)
                    .into(fileMessageThreadViewHolder.ivUserImage)
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private fun timeInMillis(message: Message): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        var timeInMilliseconds = 0L
        try {
            val mDate = sdf.parse(DateUtils.getInstance().convertToLocal(message.sentAtUtc))
            timeInMilliseconds = mDate.time
            timeInMilliseconds += message.expireTime * 1000L
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return timeInMilliseconds
    }

    @SuppressLint("SetTextI18n")
    private fun setPollMessageSelf(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, message: Message) {
        val selfPollMessage: SelfPollMessage = holder as SelfPollMessage

        setItemLongClick(selfPollMessage.itemView, message, true, selfPollMessage.tvTime)
        setSelfMessageBackground(null, selfPollMessage.llRoot, selfPollMessage.llMessage, position, message.userId)
//        setBackgroundColor(selfPollMessage.llMessage.background as NinePatchDrawable, colorConfig.fuguBgMessageYou)
        selfPollMessage.tvQuestion.text = Html.fromHtml(FormatStringUtil.FormatString.getFormattedString(message.question).get(0))
        setTime(selfPollMessage.tvTime, message.sentAtUtc)
        selfPollMessage.tvTotalVotes.text = "Total Votes: " + message.total_votes.toString()
        if (!message.multipleSelect) {
            selfPollMessage.llRadioGroup.visibility = View.VISIBLE
            selfPollMessage.checkboxGroup.visibility = View.GONE
            if (message.pollOptions[0].voteMap.size == 1) {
                selfPollMessage.radioOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " vote"
            } else {
                selfPollMessage.radioOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " votes"
            }
            if (message.pollOptions[1].voteMap.size == 1) {
                selfPollMessage.radioTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " vote"
            } else {
                selfPollMessage.radioTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " votes"
            }
            setOptionsText(message.pollOptions[0].label, message.pollOptions.get(1).label, selfPollMessage, false)
            when {
                message.pollOptions[0].voteMap[userId] != null -> {
                    selfPollMessage.radioOne.isChecked = true
                    selfPollMessage.radioTwo.isChecked = false
                    selfPollMessage.llRadioOne.setBackgroundResource(R.drawable.rectangle_border_radio_blue)
                    selfPollMessage.llRadioTwo.setBackgroundResource(R.drawable.rectangle_border_radio_gray)
                }
                message.pollOptions[1].voteMap[userId] != null -> {
                    selfPollMessage.radioTwo.isChecked = true
                    selfPollMessage.radioOne.isChecked = false
                    selfPollMessage.llRadioTwo.setBackgroundResource(R.drawable.rectangle_border_radio_blue)
                    selfPollMessage.llRadioOne.setBackgroundResource(R.drawable.rectangle_border_radio_gray)
                }
                else -> {
                    selfPollMessage.radioOne.isChecked = false
                    selfPollMessage.radioTwo.isChecked = false
                    selfPollMessage.llRadioTwo.setBackgroundResource(R.drawable.rectangle_border_radio_gray)
                    selfPollMessage.llRadioOne.setBackgroundResource(R.drawable.rectangle_border_radio_gray)
                }
            }
        } else {
            selfPollMessage.llRadioGroup.visibility = View.GONE
            selfPollMessage.checkboxGroup.visibility = View.VISIBLE
            if (message.pollOptions[0].voteMap.size == 1) {
                selfPollMessage.cbOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " vote"

            } else {
                selfPollMessage.cbOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " votes"

            }
            if (message.pollOptions[1].voteMap.size == 1) {
                selfPollMessage.cbTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " vote"

            } else {
                selfPollMessage.cbTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " votes"

            }
            setOptionsText(message.pollOptions.get(0).label, message.pollOptions.get(1).label, selfPollMessage, true)
            if (message.pollOptions[0].voteMap.containsKey(userId)) {
                selfPollMessage.cbOne.isChecked = true
                selfPollMessage.llCheckBoxOne.setBackgroundResource(R.drawable.rectangle_border_radio_blue)
            } else {
                selfPollMessage.cbOne.isChecked = false
                selfPollMessage.llCheckBoxOne.setBackgroundResource(R.drawable.rectangle_border_radio_gray)
            }
            if (message.pollOptions[1].voteMap.containsKey(userId)) {
                selfPollMessage.cbTwo.isChecked = true
                selfPollMessage.llCheckBoxTwo.setBackgroundResource(R.drawable.rectangle_border_radio_blue)
            } else {
                selfPollMessage.cbTwo.isChecked = false
                selfPollMessage.llCheckBoxTwo.setBackgroundResource(R.drawable.rectangle_border_radio_gray)
            }

        }
        if (message.pollOptions.size > 2) {
            selfPollMessage.btnViewMore.setTextColor(mContext.resources.getColor(R.color.mentionAndLinks))
            if (message.pollOptions.size - 2 == 1) {
                selfPollMessage.btnViewMore.text = "+ " + (message.pollOptions.size - 2) + " more option"
            } else {
                selfPollMessage.btnViewMore.text = "+ " + (message.pollOptions.size - 2) + " more options"
            }
            selfPollMessage.btnViewMore.typeface = proxyNovaSB
        } else {
            selfPollMessage.btnViewMore.typeface = proxyNovaSB
            selfPollMessage.btnViewMore.text = "View Details"
            selfPollMessage.btnViewMore.setTextColor(mContext.resources.getColor(R.color.black))
        }
        val isExpired = checkIfExpired(message)
        val formatter = SimpleDateFormat("dd MMM, hh:mm a")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis(message)
        if (isExpired) {
            selfPollMessage.tvExpiryTime.text = "Poll Expired"
            selfPollMessage.tvExpiryTime.setTextColor(mContext.resources.getColor(R.color.red))
        } else {
            selfPollMessage.tvExpiryTime.text = "Poll Expiry: " + formatter.format(calendar.time)
            selfPollMessage.tvExpiryTime.setTextColor(mContext.resources.getColor(R.color.black))
        }
        setFilledBackGroundCheckBox(message, selfPollMessage)
        setFilledBackGroundRadio(message, selfPollMessage)
        if (!isExpired) {
            selfPollMessage.cbOne.setOnClickListener {
                checkBoxOneClicked(selfPollMessage, message, false)
            }
            selfPollMessage.cbTwo.setOnClickListener {
                checkBoxTwoClicked(message, selfPollMessage, false)
            }
            selfPollMessage.radioOne.setOnClickListener {
                if (!checkIfExpired(message)) {
                    radioButtonClicked(selfPollMessage.radioOne, message, selfPollMessage)
                } else {
                    selfPollMessage.radioOne.isChecked = !selfPollMessage.radioOne.isChecked
                }
            }
            selfPollMessage.radioTwo.setOnClickListener {
                if (!checkIfExpired(message)) {
                    radioButtonClicked(selfPollMessage.radioTwo, message, selfPollMessage)
                } else {
                    selfPollMessage.radioTwo.isChecked = !selfPollMessage.radioTwo.isChecked
                }
            }
        } else {
            selfPollMessage.cbOne.isClickable = false
            selfPollMessage.cbOne.isFocusableInTouchMode = false

            selfPollMessage.cbTwo.isClickable = false
            selfPollMessage.cbTwo.isFocusableInTouchMode = false

            selfPollMessage.radioOne.isClickable = false
            selfPollMessage.radioOne.isFocusableInTouchMode = false

            selfPollMessage.radioTwo.isClickable = false
            selfPollMessage.radioTwo.isFocusableInTouchMode = false

        }
        selfPollMessage.llCheckBoxOne.setOnClickListener {
            if (message.pollOptions[0].users.size > 0) {
                val manager = (mContext as ChatActivity).supportFragmentManager
                val ft = manager.beginTransaction()

                val newFragment = PollUsersBottomSheetFragment.newInstance(0, mContext, message.pollOptions[0].users as ArrayList<User>, true, message.pollOptions[0].label, selfPollMessage.cbOne.isChecked)
                newFragment.show(ft, "PollUsersFragmentDialog")
            } else {
                Toast.makeText(mContext, "No votes yet!", Toast.LENGTH_LONG).show()
            }
        }

        selfPollMessage.llCheckBoxTwo.setOnClickListener {
            if (message.pollOptions[1].users.size > 0) {
                val manager = (mContext as ChatActivity).supportFragmentManager
                val ft = manager.beginTransaction()

                val newFragment = PollUsersBottomSheetFragment.newInstance(0, mContext, message.pollOptions[1].users as ArrayList<User>, true, message.pollOptions[1].label, selfPollMessage.cbTwo.isChecked)
                newFragment.show(ft, "PollUsersFragmentDialog")
            } else {
                Toast.makeText(mContext, "No votes yet!", Toast.LENGTH_LONG).show()
            }
        }

        selfPollMessage.llRadioOne.setOnClickListener {
            if (message.pollOptions[0].users.size > 0) {
                val manager = (mContext as ChatActivity).supportFragmentManager
                val ft = manager.beginTransaction()

                val newFragment = PollUsersBottomSheetFragment.newInstance(0, mContext, message.pollOptions[0].users as ArrayList<User>, false, message.pollOptions[0].label, selfPollMessage.radioOne.isChecked)
                newFragment.show(ft, "PollUsersFragmentDialog")
            } else {
                Toast.makeText(mContext, "No votes yet!", Toast.LENGTH_LONG).show()
            }
        }
        selfPollMessage.llRadioTwo.setOnClickListener {
            if (message.pollOptions[1].users.size > 0) {
                val manager = (mContext as ChatActivity).supportFragmentManager
                val ft = manager.beginTransaction()

                val newFragment = PollUsersBottomSheetFragment.newInstance(0, mContext, message.pollOptions[1].users as ArrayList<User>, false, message.pollOptions[1].label, selfPollMessage.radioTwo.isChecked)
                newFragment.show(ft, "PollUsersFragmentDialog")
            } else {
                Toast.makeText(mContext, "No votes yet!", Toast.LENGTH_LONG).show()
            }
        }


        selfPollMessage.btnViewMore.setOnClickListener {
            val intent = Intent(mContext, PollDetailsActivity::class.java)
            intent.putExtra("message", message)
            intent.putExtra(FULL_NAME, userName)
            intent.putExtra(USER_IMAGE, userImage)
            intent.putExtra(USER_ID, userId)
            intent.putExtra(CHANNEL_ID, channelId)
            intent.putExtra(DATE_TIME, message.sentAtUtc)
            intent.putExtra("expire_time", message.expireTime)
            intent.putExtra("from_name", message.fromName)
            (mContext as ChatActivity).startActivityForResult(intent, 112)
        }
    }

    private fun checkIfExpired(message: Message): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        var timeInMilliseconds = 0L
        try {
            val mDate = sdf.parse(DateUtils.getInstance().convertToLocal(message.sentAtUtc))
            timeInMilliseconds = mDate.time
            timeInMilliseconds += message.expireTime * 1000L
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val calendar = Calendar.getInstance()
        calendar.time = Date()
        return (timeInMilliseconds.compareTo(calendar.timeInMillis)) < 0
    }

    @SuppressLint("SetTextI18n")
    private fun checkBoxTwoClicked(message: Message, selfPollMessage: SelfPollMessage, updateCheckBox: Boolean) {
        val isExpired = checkIfExpired(message)
        if (!isExpired) {
            if (updateCheckBox) {
                selfPollMessage.cbTwo.isChecked = !selfPollMessage.cbTwo.isChecked
            }
            if (selfPollMessage.cbTwo.isChecked) {
                selfPollMessage.llCheckBoxTwo.setBackgroundResource(R.drawable.rectangle_border_radio_blue)
                sendPoll(true, message, 1)
                val user = User()
                user.userId = userId
                user.userImage = userImage
                user.fullName = userName
                user.userId = userId
                message.pollOptions[1].users.add(user)
                message.pollOptions[1].voteMap[userId] = user
                if (message.pollOptions[1].voteMap.size == 1) {
                    selfPollMessage.cbTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " vote"
                } else {
                    selfPollMessage.cbTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " votes"
                }
                message.total_votes += 1
            } else {
                selfPollMessage.llCheckBoxTwo.setBackgroundResource(R.drawable.rectangle_border_radio_gray)
                sendPoll(false, message, 1)
                val user = User()
                user.userId = userId
                user.userImage = userImage
                user.fullName = userName
                user.userId = userId
                message.pollOptions[1].voteMap.remove(userId)
                var size = message.pollOptions[1].users.size - 1
                for (j in 0..size) {
                    if (size >= j) {
                        if (message.pollOptions[1].users[j].userId.compareTo(userId) == 0) {
                            message.pollOptions[1].users.removeAt(j)
                            size -= 1
                        }
                    }
                }
                if (message.pollOptions[1].voteMap.size == 1) {
                    selfPollMessage.cbTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " vote"
                } else {
                    selfPollMessage.cbTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " votes"

                }
                message.total_votes -= 1
            }
            setFilledBackGroundCheckBox(message, selfPollMessage)
            selfPollMessage.tvTotalVotes.text = "Total Votes: " + message.total_votes.toString()
        } else {
            selfPollMessage.cbTwo.isChecked = !selfPollMessage.cbTwo.isChecked
        }
    }


    @SuppressLint("SetTextI18n")
    private fun checkBoxTwoClickedOther(message: Message, selfPollMessage: OtherPollMessage, updateCheckBox: Boolean) {
        val isExpired = checkIfExpired(message)
        if (!isExpired) {
            if (updateCheckBox) {
                selfPollMessage.cbTwo.isChecked = !selfPollMessage.cbTwo.isChecked
            }
            if (selfPollMessage.cbTwo.isChecked) {
                selfPollMessage.llCheckBoxTwo.setBackgroundResource(R.drawable.rectangle_border_radio_blue)
                sendPoll(true, message, 1)
                val user = User()
                user.userId = userId
                user.userImage = userImage
                user.fullName = userName
                message.pollOptions[1].users.add(user)
                message.pollOptions[1].voteMap.put(userId, user)
                if (message.pollOptions[1].voteMap.size == 1) {
                    selfPollMessage.cbTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " vote"
                } else {
                    selfPollMessage.cbTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " votes"
                }
                message.total_votes += 1
            } else {
                selfPollMessage.llCheckBoxTwo.setBackgroundResource(R.drawable.rectangle_border_radio_gray)
                sendPoll(false, message, 1)
                val user = User()
                user.userId = userId
                user.userImage = userImage
                user.fullName = userName
                user.userId = userId
                message.pollOptions[1].voteMap.remove(userId)
                var size = message.pollOptions[0].users.size - 1
                for (j in 0..size) {
                    if (size >= j) {
                        if (message.pollOptions[0].users[j].userId.compareTo(userId) == 0) {
                            message.pollOptions[0].users.removeAt(j)
                            size -= 1
                        }
                    }
                }
                if (message.pollOptions[1].voteMap.size == 1) {
                    selfPollMessage.cbTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " vote"
                } else {
                    selfPollMessage.cbTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " votes"

                }
                message.total_votes -= 1
            }
            setFilledBackGroundCheckBoxOther(message, selfPollMessage)
            selfPollMessage.tvTotalVotes.text = "Total Votes: " + message.total_votes.toString()
        } else {
            selfPollMessage.cbTwo.isChecked = !selfPollMessage.cbTwo.isChecked
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkBoxOneClicked(selfPollMessage: SelfPollMessage, message: Message, updateCheckBox: Boolean) {
        val isExpired = checkIfExpired(message)
        if (!isExpired) {
            if (updateCheckBox) {
                selfPollMessage.cbOne.isChecked = !selfPollMessage.cbOne.isChecked
            }
            if (selfPollMessage.cbOne.isChecked) {
                selfPollMessage.llCheckBoxOne.setBackgroundResource(R.drawable.rectangle_border_radio_blue)
                sendPoll(true, message, 0)
                val user = User()
                user.userId = userId
                user.userImage = userImage
                user.fullName = userName
                message.pollOptions[0].users.add(user)
                message.pollOptions[0].voteMap.put(userId, user)
                if (message.pollOptions[0].voteMap.size == 1) {
                    selfPollMessage.cbOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " vote"
                } else {
                    selfPollMessage.cbOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " votes"
                }
                message.total_votes += 1
            } else {
                selfPollMessage.llCheckBoxOne.setBackgroundResource(R.drawable.rectangle_border_radio_gray)
                sendPoll(false, message, 0)
                val user = User()
                user.userId = userId
                user.userImage = userImage
                user.fullName = userName
                user.userId = userId
                var size = message.pollOptions[0].users.size - 1
                for (j in 0..size) {
                    if (size >= j) {
                        if (message.pollOptions[0].users[j].userId.compareTo(userId) == 0) {
                            message.pollOptions[0].users.removeAt(j)
                            size -= 1
                        }
                    }
                }
                message.pollOptions[0].voteMap.remove(userId)
                if (message.pollOptions[0].voteMap.size == 1) {
                    selfPollMessage.cbOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " vote"
                } else {
                    selfPollMessage.cbOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " votes"
                }
                message.total_votes -= 1
            }
            setFilledBackGroundCheckBox(message, selfPollMessage)
            selfPollMessage.tvTotalVotes.text = "Total Votes: " + message.total_votes.toString()
        } else {
            selfPollMessage.cbOne.isChecked = !selfPollMessage.cbOne.isChecked
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkBoxOneClickedOther(selfPollMessage: OtherPollMessage, message: Message, updateCheckBox: Boolean) {
        val isExpired = checkIfExpired(message)
        if (!isExpired) {
            if (updateCheckBox) {
                selfPollMessage.cbOne.isChecked = !selfPollMessage.cbOne.isChecked
            }
            if (selfPollMessage.cbOne.isChecked) {
                selfPollMessage.llCheckBoxOne.setBackgroundResource(R.drawable.rectangle_border_radio_blue)
                sendPoll(true, message, 0)
                val user = User()
                user.userId = userId
                user.userImage = userImage
                user.fullName = userName
                message.pollOptions[0].users.add(user)
                message.pollOptions[0].voteMap.put(userId, user)
                if (message.pollOptions[0].voteMap.size == 1) {
                    selfPollMessage.cbOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " vote"
                } else {
                    selfPollMessage.cbOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " votes"
                }
                message.total_votes += 1
            } else {
                selfPollMessage.llCheckBoxOne.setBackgroundResource(R.drawable.rectangle_border_radio_gray)
                sendPoll(false, message, 0)
                val user = User()
                user.userId = userId
                user.userImage = userImage
                user.fullName = userName
                user.userId = userId

                var size = message.pollOptions[0].users.size - 1
                for (j in 0..size) {
                    if (size >= j) {
                        if (message.pollOptions[0].users[j].userId.compareTo(userId) == 0) {
                            message.pollOptions[0].users.removeAt(j)
                            size -= 1
                        }
                    }
                }

                message.pollOptions[0].voteMap.remove(userId)
                if (message.pollOptions[0].voteMap.size == 1) {
                    selfPollMessage.cbOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " vote"
                } else {
                    selfPollMessage.cbOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " votes"
                }
                message.total_votes -= 1
            }
            setFilledBackGroundCheckBoxOther(message, selfPollMessage)
            selfPollMessage.tvTotalVotes.text = "Total Votes: " + message.total_votes.toString()
        } else {
            selfPollMessage.cbOne.isChecked = !selfPollMessage.cbOne.isChecked
        }
    }

    private fun setFilledBackGroundCheckBox(message: Message, selfPollMessage: SelfPollMessage) {
        if (message.pollOptions[0].voteMap.size > 0) {
            val params = selfPollMessage.cbOneView.layoutParams
            val width = dpToPx(300 * (message.pollOptions[0].voteMap.size) / message.total_votes)
            params.width = width
            selfPollMessage.cbOneView.layoutParams = params
            selfPollMessage.cbOneView.invalidate()
            selfPollMessage.cbOneView.visibility = View.VISIBLE
        } else {
            selfPollMessage.cbOneView.visibility = View.GONE
        }
        if (message.pollOptions[1].voteMap.size > 0) {
            val params = selfPollMessage.cbTwoView.layoutParams
            val width = dpToPx(300 * (message.pollOptions[1].voteMap.size) / message.total_votes)
            params.width = width
            selfPollMessage.cbTwoView.layoutParams = params
            selfPollMessage.cbTwoView.invalidate()
            selfPollMessage.cbTwoView.visibility = View.VISIBLE
        } else {
            selfPollMessage.cbTwoView.visibility = View.GONE
        }
    }


    private fun setFilledBackGroundCheckBoxOther(message: Message, selfPollMessage: OtherPollMessage) {
        if (message.pollOptions[0].voteMap.size > 0) {
            val params = selfPollMessage.cbOneView.layoutParams
            val width = dpToPx(300 * (message.pollOptions[0].voteMap.size) / message.total_votes)
            params.width = width
            selfPollMessage.cbOneView.layoutParams = params
            selfPollMessage.cbOneView.invalidate()
            selfPollMessage.cbOneView.visibility = View.VISIBLE
        } else {
            selfPollMessage.cbOneView.visibility = View.GONE
        }
        if (message.pollOptions[1].voteMap.size > 0) {
            val params = selfPollMessage.cbTwoView.layoutParams
            val width = dpToPx(300 * (message.pollOptions[1].voteMap.size) / message.total_votes)
            params.width = width
            selfPollMessage.cbTwoView.layoutParams = params
            selfPollMessage.cbTwoView.invalidate()
            selfPollMessage.cbTwoView.visibility = View.VISIBLE
        } else {
            selfPollMessage.cbTwoView.visibility = View.GONE
        }
    }

    private fun setFilledBackGroundRadio(message: Message, selfPollMessage: SelfPollMessage) {
        if (message.pollOptions[0].voteMap.size > 0) {
            val params = selfPollMessage.radioOneView.layoutParams
            val width = dpToPx(300 * (message.pollOptions[0].voteMap.size) / message.total_votes)
            params.width = width
            selfPollMessage.radioOneView.layoutParams = params
            selfPollMessage.radioOneView.invalidate()
            selfPollMessage.radioOneView.visibility = View.VISIBLE
        } else {
            selfPollMessage.radioOneView.visibility = View.GONE
        }
        if (message.pollOptions[1].voteMap.size > 0) {
            val params = selfPollMessage.radioTwoView.layoutParams
            val width = dpToPx(300 * (message.pollOptions[1].voteMap.size) / message.total_votes)
            params.width = width
            selfPollMessage.radioTwoView.layoutParams = params
            selfPollMessage.radioTwoView.invalidate()
            selfPollMessage.radioTwoView.visibility = View.VISIBLE
        } else {
            selfPollMessage.radioTwoView.visibility = View.GONE
        }
    }

    private fun setFilledBackGroundRadioOther(message: Message, selfPollMessage: OtherPollMessage) {
        if (message.pollOptions[0].voteMap.size > 0) {
            val params = selfPollMessage.radioOneView.layoutParams
            val width = dpToPx(300 * (message.pollOptions[0].voteMap.size) / message.total_votes)
            params.width = width
            selfPollMessage.radioOneView.layoutParams = params
            selfPollMessage.radioOneView.invalidate()
            selfPollMessage.radioOneView.visibility = View.VISIBLE
        } else {
            selfPollMessage.radioOneView.visibility = View.GONE
        }
        if (message.pollOptions[1].voteMap.size > 0) {
            val params = selfPollMessage.radioTwoView.layoutParams
            val width = dpToPx(300 * (message.pollOptions[1].voteMap.size) / message.total_votes)
            params.width = width
            selfPollMessage.radioTwoView.layoutParams = params
            selfPollMessage.radioTwoView.invalidate()
            selfPollMessage.radioTwoView.visibility = View.VISIBLE
        } else {
            selfPollMessage.radioTwoView.visibility = View.GONE
        }
    }

    private fun sendPoll(isVoted: Boolean, message: Message, index: Int) {
        val jsonObject = JSONObject()
        jsonObject.put("puid", message.pollOptions[index].puid)
        jsonObject.put("is_voted", isVoted)
        jsonObject.put(MESSAGE_UNIQUE_ID, message.muid)
        chatActivity = mContext as ChatActivity
        chatActivity?.sendPollOption(jsonObject)
    }

    @SuppressLint("SetTextI18n")
    private fun radioButtonClicked(view: View, message: Message, selfPollMessage: SelfPollMessage) {
        when (view.id) {
            R.id.llRadioOne -> {
                if (!selfPollMessage.radioOne.isChecked) {
                    selfPollMessage.llRadioOne.setBackgroundResource(R.drawable.rectangle_border_radio_blue)
                    selfPollMessage.llRadioTwo.setBackgroundResource(R.drawable.rectangle_border_radio_gray)
                    selfPollMessage.radioOne.isChecked = true
                    selfPollMessage.radioTwo.isChecked = false
                    sendPoll(true, message, 0)
                    for (i in 0 until message.pollOptions.size) {
                        val poll = message.pollOptions[i]
                        if (poll.voteMap[userId] != null) {
                            val user = User()
                            user.userId = userId
                            user.userImage = userImage
                            user.fullName = userName
                            user.userId = userId
                            var size = poll.users.size - 1
                            for (j in 0..size) {
                                if (size >= j) {
                                    if (poll.users[j].userId.compareTo(userId) == 0) {
                                        poll.users.removeAt(j)
                                        size -= 1
                                    }
                                }
                            }
                            poll.voteMap.remove(userId)
                            message.total_votes -= 1
                            message.pollOptions[i] = poll
                            break
                        }
                    }
                    message.total_votes += 1
                    val user = User()
                    user.userId = userId
                    user.userImage = userImage
                    user.fullName = userName
                    message.pollOptions[0].users.add(user)
                    message.pollOptions[0].voteMap.put(userId, user)
                    if (message.pollOptions[0].voteMap.size == 1) {
                        selfPollMessage.radioOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " vote"
                    } else {
                        selfPollMessage.radioOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " votes"
                    }
                    if (message.pollOptions[1].voteMap.size == 1) {
                        selfPollMessage.radioTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " vote"
                    } else {
                        selfPollMessage.radioTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " votes"
                    }
                } else {
                    selfPollMessage.llRadioOne.setBackgroundResource(R.drawable.rectangle_border_radio_blue)
                }
                selfPollMessage.tvTotalVotes.text = "Total Votes: " + message.total_votes.toString()
                setFilledBackGroundRadio(message, selfPollMessage)
            }
            R.id.llRadioTwo -> {
                if (!selfPollMessage.radioTwo.isChecked) {
                    selfPollMessage.llRadioTwo.setBackgroundResource(R.drawable.rectangle_border_radio_blue)
                    selfPollMessage.llRadioOne.setBackgroundResource(R.drawable.rectangle_border_radio_gray)
                    selfPollMessage.radioOne.isChecked = false
                    selfPollMessage.radioTwo.isChecked = true
                    sendPoll(true, message, 1)
                    for (i in 0 until message.pollOptions.size) {
                        val poll = message.pollOptions[i]
                        if (poll.voteMap[userId] != null) {
                            val user = User()
                            user.userId = userId
                            user.userImage = userImage
                            user.fullName = userName
                            user.userId = userId
                            var size = poll.users.size - 1
                            for (j in 0..size) {
                                if (size >= j) {
                                    if (poll.users[j].userId.compareTo(userId) == 0) {
                                        poll.users.removeAt(j)
                                        size -= 1
                                    }
                                }
                            }
                            poll.voteMap.remove(userId)
                            message.total_votes -= 1
                            message.pollOptions[i] = poll
                            break
                        }
                    }
                    message.total_votes += 1
                    val user = User()
                    user.userId = userId
                    user.userImage = userImage
                    user.fullName = userName
                    message.pollOptions[1].users.add(user)
                    message.pollOptions[1].voteMap[userId] = user
                    if (message.pollOptions[0].voteMap.size == 1) {
                        selfPollMessage.radioOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " vote"

                    } else {
                        selfPollMessage.radioOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " votes"

                    }
                    if (message.pollOptions[1].voteMap.size == 1) {
                        selfPollMessage.radioTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " vote"

                    } else {
                        selfPollMessage.radioTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " votes"

                    }
                } else {
                    selfPollMessage.llRadioTwo.setBackgroundResource(R.drawable.rectangle_border_radio_blue)
                }
                selfPollMessage.tvTotalVotes.text = "Total Votes: " + message.total_votes.toString()
                setFilledBackGroundRadio(message, selfPollMessage)
            }
            R.id.radioOne -> {
                if (selfPollMessage.radioOne.isChecked) {
                    selfPollMessage.llRadioOne.setBackgroundResource(R.drawable.rectangle_border_radio_blue)
                    selfPollMessage.llRadioTwo.setBackgroundResource(R.drawable.rectangle_border_radio_gray)
                    selfPollMessage.radioTwo.isChecked = false
                    sendPoll(true, message, 0)
                    for (i in 0 until message.pollOptions.size) {
                        val poll = message.pollOptions[i]
                        if (poll.voteMap[userId] != null) {
                            val user = User()
                            user.userId = userId
                            user.userImage = userImage
                            user.fullName = userName
                            user.userId = userId
                            var size = poll.users.size - 1
                            for (j in 0..size) {
                                if (size >= j) {
                                    if (poll.users[j].userId.compareTo(userId) == 0) {
                                        poll.users.removeAt(j)
                                        size -= 1
                                    }
                                }
                            }
                            poll.voteMap.remove(userId)
                            message.total_votes -= 1
                            message.pollOptions[i] = poll
                        }
                    }
                    message.total_votes += 1
                    val user = User()
                    user.userId = userId
                    user.userImage = userImage
                    user.fullName = userName
                    message.pollOptions[0].users.add(user)
                    message.pollOptions[0].voteMap[userId] = user
                    if (message.pollOptions[0].voteMap.size == 1) {
                        selfPollMessage.radioOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " vote"

                    } else {
                        selfPollMessage.radioOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " votes"

                    }
                    if (message.pollOptions[1].voteMap.size == 1) {
                        selfPollMessage.radioTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " vote"

                    } else {
                        selfPollMessage.radioTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " votes"

                    }
                } else {
                    selfPollMessage.llRadioOne.setBackgroundResource(R.drawable.rectangle_border_radio_gray)
                    selfPollMessage.llRadioTwo.setBackgroundResource(R.drawable.rectangle_border_radio_blue)
                }
                selfPollMessage.tvTotalVotes.text = "Total Votes: " + message.total_votes.toString()
                setFilledBackGroundRadio(message, selfPollMessage)
            }
            R.id.radioTwo -> {
                if (selfPollMessage.radioTwo.isChecked) {
                    selfPollMessage.llRadioOne.setBackgroundResource(R.drawable.rectangle_border_radio_gray)
                    selfPollMessage.llRadioTwo.setBackgroundResource(R.drawable.rectangle_border_radio_blue)
                    selfPollMessage.radioOne.isChecked = false
                    sendPoll(true, message, 1)
                    for (i in 0 until message.pollOptions.size) {
                        val poll = message.pollOptions[i]
                        if (poll.voteMap[userId] != null) {
                            val user = User()
                            user.userId = userId
                            user.userImage = userImage
                            user.fullName = userName
                            user.userId = userId
                            var size = poll.users.size - 1
                            for (j in 0..size) {
                                if (size >= j) {
                                    if (poll.users[j].userId.compareTo(userId) == 0) {
                                        poll.users.removeAt(j)
                                        size -= 1
                                    }
                                }
                            }
                            poll.voteMap.remove(userId)
                            message.total_votes -= 1
                            message.pollOptions[i] = poll
                        }
                    }
                    message.total_votes += 1
                    val user = User()
                    user.userId = userId
                    user.userImage = userImage
                    user.fullName = userName
                    message.pollOptions[1].users.add(user)
                    message.pollOptions[1].voteMap[userId] = user
                    if (message.pollOptions[0].voteMap.size == 1) {
                        selfPollMessage.radioOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " vote"

                    } else {
                        selfPollMessage.radioOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " votes"

                    }
                    if (message.pollOptions[1].voteMap.size == 1) {
                        selfPollMessage.radioTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " vote"

                    } else {
                        selfPollMessage.radioTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " votes"

                    }
                } else {
                    selfPollMessage.llRadioOne.setBackgroundResource(R.drawable.rectangle_border_radio_blue)
                    selfPollMessage.llRadioTwo.setBackgroundResource(R.drawable.rectangle_border_radio_gray)
                }
                selfPollMessage.tvTotalVotes.text = "Total Votes: " + message.total_votes.toString()
                setFilledBackGroundRadio(message, selfPollMessage)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun radioButtonClickedOther(view: View, message: Message, selfPollMessage: OtherPollMessage) {
        when (view.id) {
            R.id.llRadioOne -> {
                if (!selfPollMessage.radioOne.isChecked) {
                    selfPollMessage.llRadioOne.setBackgroundResource(R.drawable.rectangle_border_radio_blue)
                    selfPollMessage.llRadioTwo.setBackgroundResource(R.drawable.rectangle_border_radio_gray)
                    selfPollMessage.radioOne.isChecked = true
                    selfPollMessage.radioTwo.isChecked = false
                    sendPoll(true, message, 0)
                    for (i in 0 until message.pollOptions.size) {
                        val poll = message.pollOptions[i]
                        if (poll.voteMap[userId] != null) {
                            val user = User()
                            user.userId = userId
                            user.userImage = userImage
                            user.fullName = userName
                            user.userId = userId
                            var size = poll.users.size - 1
                            for (j in 0..size) {
                                if (size >= j) {
                                    if (poll.users[j].userId.compareTo(userId) == 0) {
                                        poll.users.removeAt(j)
                                        size -= 1
                                    }

                                }
                            }
                            poll.voteMap.remove(userId)
                            message.total_votes -= 1
                            message.pollOptions[i] = poll
                            break
                        }
                    }
                    message.total_votes += 1
                    val user = User()
                    user.userId = userId
                    user.userImage = userImage
                    user.fullName = userName
                    message.pollOptions[0].users.add(user)
                    message.pollOptions[0].voteMap.put(userId, user)
                    if (message.pollOptions[0].voteMap.size == 1) {
                        selfPollMessage.radioOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " vote"

                    } else {
                        selfPollMessage.radioOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " votes"

                    }
                    if (message.pollOptions[1].voteMap.size == 1) {
                        selfPollMessage.radioTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " vote"

                    } else {
                        selfPollMessage.radioTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " votes"

                    }
                } else {
                    selfPollMessage.llRadioOne.setBackgroundResource(R.drawable.rectangle_border_radio_blue)
                }
                selfPollMessage.tvTotalVotes.text = "Total Votes: " + message.total_votes.toString()
                setFilledBackGroundRadioOther(message, selfPollMessage)
            }
            R.id.llRadioTwo -> {
                if (!selfPollMessage.radioTwo.isChecked) {
                    selfPollMessage.llRadioTwo.setBackgroundResource(R.drawable.rectangle_border_radio_blue)
                    selfPollMessage.llRadioOne.setBackgroundResource(R.drawable.rectangle_border_radio_gray)
                    selfPollMessage.radioOne.isChecked = false
                    selfPollMessage.radioTwo.isChecked = true
                    sendPoll(true, message, 1)
                    for (i in 0 until message.pollOptions.size) {
                        val poll = message.pollOptions[i]
                        if (poll.voteMap[userId] != null) {
                            val user = User()
                            user.userId = userId
                            user.userImage = userImage
                            user.fullName = userName
                            user.userId = userId
                            var size = poll.users.size - 1
                            for (j in 0..size) {
                                if (size >= j) {
                                    if (poll.users[j].userId.compareTo(userId) == 0) {
                                        poll.users.removeAt(j)
                                        size -= 1
                                    }

                                }
                            }
                            poll.voteMap.remove(userId)
                            message.total_votes -= 1
                            message.pollOptions[i] = poll
                            break
                        }
                    }
                    message.total_votes += 1
                    val user = User()
                    user.userId = userId
                    user.userImage = userImage
                    user.fullName = userName
                    message.pollOptions[1].users.add(user)
                    message.pollOptions[1].voteMap.put(userId, user)
                    if (message.pollOptions[0].voteMap.size == 1) {
                        selfPollMessage.radioOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " vote"

                    } else {
                        selfPollMessage.radioOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " votes"

                    }
                    if (message.pollOptions[1].voteMap.size == 1) {
                        selfPollMessage.radioTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " vote"

                    } else {
                        selfPollMessage.radioTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " votes"

                    }
                } else {
                    selfPollMessage.llRadioTwo.setBackgroundResource(R.drawable.rectangle_border_radio_blue)
                }
                selfPollMessage.tvTotalVotes.text = "Total Votes: " + message.total_votes.toString()
                setFilledBackGroundRadioOther(message, selfPollMessage)
            }
            R.id.radioOne -> {
                if (selfPollMessage.radioOne.isChecked) {
                    selfPollMessage.llRadioOne.setBackgroundResource(R.drawable.rectangle_border_radio_blue)
                    selfPollMessage.llRadioTwo.setBackgroundResource(R.drawable.rectangle_border_radio_gray)
                    selfPollMessage.radioTwo.isChecked = false
                    sendPoll(true, message, 0)
                    for (i in 0 until message.pollOptions.size) {
                        val poll = message.pollOptions[i]
                        if (poll.voteMap[userId] != null) {
                            val user = User()
                            user.userId = userId
                            user.userImage = userImage
                            user.fullName = userName
                            user.userId = userId
                            var size = poll.users.size - 1
                            for (j in 0..size) {
                                if (size >= j) {
                                    if (poll.users[j].userId.compareTo(userId) == 0) {
                                        poll.users.removeAt(j)
                                        size -= 1
                                    }
                                }
                            }
                            poll.voteMap.remove(userId)
                            message.total_votes -= 1
                            message.pollOptions[i] = poll
                        }
                    }
                    message.total_votes += 1
                    val user = User()
                    user.userId = userId
                    user.userImage = userImage
                    user.fullName = userName
                    message.pollOptions[0].users.add(user)
                    message.pollOptions[0].voteMap.put(userId, user)
                    if (message.pollOptions[0].voteMap.size == 1) {
                        selfPollMessage.radioOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " vote"

                    } else {
                        selfPollMessage.radioOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " votes"

                    }
                    if (message.pollOptions[1].voteMap.size == 1) {
                        selfPollMessage.radioTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " vote"

                    } else {
                        selfPollMessage.radioTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " votes"

                    }
                } else {
                    selfPollMessage.llRadioOne.setBackgroundResource(R.drawable.rectangle_border_radio_gray)
                    selfPollMessage.llRadioTwo.setBackgroundResource(R.drawable.rectangle_border_radio_blue)
                }
                selfPollMessage.tvTotalVotes.text = "Total Votes: " + message.total_votes.toString()
                setFilledBackGroundRadioOther(message, selfPollMessage)
            }
            R.id.radioTwo -> {
                if (selfPollMessage.radioTwo.isChecked) {
                    selfPollMessage.llRadioOne.setBackgroundResource(R.drawable.rectangle_border_radio_gray)
                    selfPollMessage.llRadioTwo.setBackgroundResource(R.drawable.rectangle_border_radio_blue)
                    selfPollMessage.radioOne.isChecked = false
                    sendPoll(true, message, 1)
                    for (i in 0 until message.pollOptions.size) {
                        val poll = message.pollOptions[i]
                        if (poll.voteMap[userId] != null) {
                            val user = User()
                            user.userId = userId
                            user.userImage = userImage
                            user.fullName = userName
                            user.userId = userId
                            var size = poll.users.size - 1
                            for (j in 0..size) {
                                if (size >= j) {
                                    if (poll.users[j].userId.compareTo(userId) == 0) {
                                        poll.users.removeAt(j)
                                        size -= 1
                                    }

                                }
                            }
                            poll.voteMap.remove(userId)
                            message.total_votes -= 1
                            message.pollOptions[i] = poll
                        }
                    }
                    message.total_votes += 1
                    val user = User()
                    user.userId = userId
                    user.userImage = userImage
                    user.fullName = userName
                    message.pollOptions[1].users.add(user)
                    message.pollOptions[1].voteMap.put(userId, user)
                    if (message.pollOptions[0].voteMap.size == 1) {
                        selfPollMessage.radioOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " vote"

                    } else {
                        selfPollMessage.radioOneVotes.text = message.pollOptions[0].voteMap.size.toString() + " votes"

                    }
                    if (message.pollOptions[1].voteMap.size == 1) {
                        selfPollMessage.radioTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " vote"

                    } else {
                        selfPollMessage.radioTwoVotes.text = message.pollOptions[1].voteMap.size.toString() + " votes"

                    }
                } else {
                    selfPollMessage.llRadioOne.setBackgroundResource(R.drawable.rectangle_border_radio_blue)
                    selfPollMessage.llRadioTwo.setBackgroundResource(R.drawable.rectangle_border_radio_gray)
                }
                selfPollMessage.tvTotalVotes.text = "Total Votes: " + message.total_votes.toString()
                setFilledBackGroundRadioOther(message, selfPollMessage)
            }
        }
    }

    private fun setOptionsText(optionOne: String?, optionTwo: String?, selfPollMessage: SelfPollMessage, multiSelect: Boolean) {
        if (!multiSelect) {
            selfPollMessage.radioOne.text = optionOne
            selfPollMessage.radioTwo.text = optionTwo
        } else {
            selfPollMessage.cbOne.text = optionOne
            selfPollMessage.cbTwo.text = optionTwo
        }
    }

    private fun setOptionsTextOther(optionOne: String?, optionTwo: String?, selfPollMessage: OtherPollMessage, multiSelect: Boolean) {
        if (!multiSelect) {
            selfPollMessage.radioOne.text = optionOne
            selfPollMessage.radioTwo.text = optionTwo
        } else {
            selfPollMessage.cbOne.text = optionOne
            selfPollMessage.cbTwo.text = optionTwo
        }
    }

    private fun setVideoCallMessageSelf(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, message: Message) {
        val selfVideoCallMessage: SelfVideoCallMessage = holder as SelfVideoCallMessage
        setSelfMessageBackground(null, selfVideoCallMessage.rlRoot, selfVideoCallMessage.llMessage, position, message.userId)
//        setBackgroundColor(selfVideoCallMessage.llMessage.background as NinePatchDrawable, colorConfig.fuguBgMessageYou)
        if (message.messageState == 2) {
            selfVideoCallMessage.callAgain.text = "Call Back"
            selfVideoCallMessage.tvMessage.text = "Missed Call"
            when (message.callType) {
                "VIDEO" -> {
                    selfVideoCallMessage.tvDuration.text = "The video call"
                }
                "HANGOUTS" -> {
                    selfVideoCallMessage.tvDuration.text = "The hangouts call"
                }
                else -> {
                    selfVideoCallMessage.tvDuration.text = "The voice call"
                }
            }
        } else {
            selfVideoCallMessage.callAgain.text = "Call Again"
            when (message.callType) {
                "VIDEO" -> {
                    selfVideoCallMessage.tvMessage.text = "The video call ended."
                }
                "HANGOUTS" -> {
                    selfVideoCallMessage.tvDuration.text = "The hangouts call"
                }
                else -> {
                    selfVideoCallMessage.tvMessage.text = "The voice call ended."
                }
            }
        }
        selfVideoCallMessage.callAgain.setOnClickListener {
            try {
                if (message.callType == "HANGOUTS") {
                    (mContext as ChatActivity).makeHangoutsCall()
                } else
                    (mContext as ChatActivity).openVideoCallActivity(message.callType)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        setTime(selfVideoCallMessage.tvTime, message.sentAtUtc)
        setDuration(selfVideoCallMessage.tvDuration, selfVideoCallMessage.ivCallIcon, message.videoCallDuration)
    }

    private fun setVideoCallMessageOther(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, message: Message) {
        val otherVideoCallMessage: OtherVideoCallMessage = holder as OtherVideoCallMessage
        setOtherMessageBackground(null, otherVideoCallMessage.rlRoot, otherVideoCallMessage.llMessage, position, null, message.userId)
        if (message.messageState == 2) {

            otherVideoCallMessage.llMessage.setBackgroundResource(R.drawable.missed_call_bubble_other)
            otherVideoCallMessage.ivCallIcon.setImageResource(R.drawable.ic_missed_call)
            otherVideoCallMessage.tvMessage.text = "Missed Call"
            otherVideoCallMessage.callAgain.text = "Call Back"
            when (message.callType) {
                "VIDEO" -> {
                    otherVideoCallMessage.tvDuration.text = "The video call"
                }
                "HANGOUTS" -> {
                    otherVideoCallMessage.tvDuration.text = "The hangouts call"
                }
                else -> {
                    otherVideoCallMessage.tvDuration.text = "The voice call"
                }
            }
//            otherVideoCallMessage.tvMessage.setTextColor((mContext as ChatActivity).resources.getColor(R.color.missed_call_color))
        } else {
            otherVideoCallMessage.callAgain.text = "Call Again"
            otherVideoCallMessage.ivCallIcon.setImageResource(R.drawable.ic_picked_call)
            otherVideoCallMessage.llMessage.setBackgroundResource(R.drawable.picked_call_bubble_other)
            when (message.callType) {
                "VIDEO" -> {
                    otherVideoCallMessage.tvMessage.text = "The video call ended."
                }
                "HANGOUTS" -> {
                    otherVideoCallMessage.tvDuration.text = "The hangouts call"
                }
                else -> {
                    otherVideoCallMessage.tvMessage.text = "The voice call ended."
                }
            }
//            otherVideoCallMessage.tvMessage.setTextColor((mContext as ChatActivity).resources.getColor(R.color.white))
        }
        otherVideoCallMessage.callAgain.setOnClickListener {
            try {
                if (message.callType == "HANGOUTS") {
                    (mContext as ChatActivity).makeHangoutsCall()
                } else
                    (mContext as ChatActivity).openVideoCallActivity(message.callType)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        setTime(otherVideoCallMessage.tvTime, message.sentAtUtc)
        setDuration(otherVideoCallMessage.tvDuration, otherVideoCallMessage.ivCallIcon, message.videoCallDuration)
    }

    private fun setDuration(tvDuration: TextView, ivCallIcon: ImageView, videoCallDuration: Long?) {
        if (videoCallDuration?.compareTo(-1L) != 0) {
            val hours = videoCallDuration!! / 3600
            val minutes = (videoCallDuration % 3600) / 60
            val seconds = videoCallDuration % 60
            if (hours.compareTo(0L) != 0) {
                if (hours.compareTo(1L) == 0) {
                    tvDuration.text = String.format("%2d hr %2d mins %2d secs", hours, minutes, seconds)
                } else {
                    tvDuration.text = String.format("%2d hrs %2d mins %2d secs", hours, minutes, seconds)
                }
            } else if (minutes.compareTo(0L) != 0) {
                if (minutes.compareTo(1L) == 0) {
                    tvDuration.text = String.format("%2d min %2d secs", minutes, seconds)
                } else {
                    tvDuration.text = String.format("%2d mins %2d secs", minutes, seconds)
                }
            } else {
                if (minutes.compareTo(1L) == 0) {
                    tvDuration.text = String.format("%2d sec", seconds)
                } else {
                    tvDuration.text = String.format("%2d secs", seconds)
                }
            }
        } else {

        }
    }

    private fun setVideoMessageOther(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, message: Message) {
        val otherVideoMessageViewHolder: OtherVideoMessageViewHolder = holder as OtherVideoMessageViewHolder
        val isSpiked = setOtherMessageBackground(otherVideoMessageViewHolder.tvReplies, otherVideoMessageViewHolder.llRoot, otherVideoMessageViewHolder.llImageMessage, position, otherVideoMessageViewHolder.tvUserName, message.userId)
        if (chatActivity != null && ((chatType != ChatType.BOT) || (chatType == ChatType.BOT && userType == UserType.SELF_BOT))) {
            otherVideoMessageViewHolder.ivImageMessage.setOnTouchListener(ExtendedTouchListener(otherVideoMessageViewHolder.itemView, otherVideoMessageViewHolder.llMessage,
                    otherVideoMessageViewHolder.llRoot,
                    message,
                    chatActivity,
                    recyclerView,
                    otherVideoMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
            otherVideoMessageViewHolder.tvImageTime.setOnTouchListener(ExtendedTouchListener(otherVideoMessageViewHolder.itemView, otherVideoMessageViewHolder.llMessage,
                    otherVideoMessageViewHolder.llRoot,
                    message,
                    chatActivity,
                    recyclerView,
                    otherVideoMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
        }
//        setBackgroundColor(otherVideoMessageViewHolder.llImageMessage.background as NinePatchDrawable, colorConfig.fuguBgMessageFrom)
        setTime(otherVideoMessageViewHolder.tvImageTime, message.sentAtUtc)
        setStarredMessage(otherVideoMessageViewHolder.ivStar, message.isStarred)


        val options = RequestOptions()
                .dontAnimate()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transforms(CenterCrop(), RoundedCorners(3), BlurTransform(5, 3))
        try {
            if (mContext is ChatActivity) {
                Glide.with(mContext as ChatActivity)
                        .asBitmap()
                        .apply(options)
                        .load(message.thumbnailUrl)
                        .into(otherVideoMessageViewHolder.ivImageMessage)
            } else {
                Glide.with(mContext as FuguInnerChatActivity)
                        .asBitmap()
                        .apply(options)
                        .load(message.thumbnailUrl)
                        .into(otherVideoMessageViewHolder.ivImageMessage)
            }
        } catch (e: Exception) {

        }


//        setImage(null, null, otherVideoMessageViewHolder.ivImageMessage, message, position)
        if (!TextUtils.isEmpty(message.message)) {
            otherVideoMessageViewHolder.tvImagewithMessage.visibility = View.VISIBLE
            if (message.messageState == 4) {
                val messageString = message.message + " <font color='grey'><small> (edited)</small></font>"
                manipulateAndSetText(otherVideoMessageViewHolder.tvImagewithMessage, messageString, message.messageState)
            } else {
                manipulateAndSetText(otherVideoMessageViewHolder.tvImagewithMessage, message.message, message.messageState)
            }
        } else {
            otherVideoMessageViewHolder.tvImagewithMessage.visibility = View.GONE

        }
        if (message.threadMessage) {
            otherVideoMessageViewHolder.tvReplies.visibility = View.VISIBLE
            setReplies(otherVideoMessageViewHolder.tvReplies, message.threadMessageCount)
        } else {
            otherVideoMessageViewHolder.tvReplies.visibility = View.GONE
        }

        setImageHeightAndWidth(otherVideoMessageViewHolder.ivImageMessage, otherVideoMessageViewHolder.rlImageMessage, otherVideoMessageViewHolder.llImageMessage, message, isSpiked)
        setItemLongClick(otherVideoMessageViewHolder.itemView, message, false, otherVideoMessageViewHolder.tvImageTime)
        setItemLongClick(otherVideoMessageViewHolder.ivImageMessage, message, false, otherVideoMessageViewHolder.tvImageTime)
        setEmojisCardView(otherVideoMessageViewHolder.llEmojis, otherVideoMessageViewHolder.emojiLayoutList, otherVideoMessageViewHolder.emojiTextList, otherVideoMessageViewHolder.emojiCountList, message, position
        )
        otherVideoMessageViewHolder.llForward.setOnClickListener {
            setForwardClick(message)
        }
        otherVideoMessageViewHolder.tvFileSize.text = message.fileSize
        setVideoDownloadStatus(otherVideoMessageViewHolder.llDownload, otherVideoMessageViewHolder.circleProgress, otherVideoMessageViewHolder.ivPlay, otherVideoMessageViewHolder.llForward, message, otherVideoMessageViewHolder.ivCrossCancel)
        otherVideoMessageViewHolder.llDownload.setOnClickListener {
            if (getIsNetworkConnected() && checkAndObtainStoragePermission()) {
                performClickCount = 1
                val fullPath = Environment.getExternalStorageDirectory().absolutePath + "/Download"
                downloadIdVideo = downloadFileFromUrl(fullPath, message, position, "Video")
                if (downloadIdVideo != 1) {
                    message.downloadId = downloadIdVideo
                }
                holder.circleProgress.visibility = View.VISIBLE
                holder.ivCrossCancel.visibility = View.VISIBLE
                holder.llDownload.visibility = View.GONE
            }
        }
        otherVideoMessageViewHolder.rlDownloading.setOnClickListener {
            PRDownloader.pause(downloadIdVideo)
            holder.llDownload.visibility = View.VISIBLE
            holder.circleProgress.visibility = View.GONE
            holder.ivCrossCancel.visibility = View.GONE
        }


        if (message.downloadId != 0 && message.progressUpdate && message.downloadStatus == DOWNLOAD_IN_PROGRESS && performClickCount == 0) {
            Log.e("DOWNLOAD PERFORM CLICK", "PERFORMED")
            message.progressUpdate = false
            holder.llDownload.performClick()
        }

        otherVideoMessageViewHolder.ivPlay.setOnClickListener {
            try {
                if (isChatActivity()) {
                    val mIntent = Intent(chatActivity, VideoPlayerActivity::class.java)
                    mIntent.putExtra("url", message.filePath)
                    chatActivity?.startActivity(mIntent)
                } else {
                    val mIntent = Intent(fuguInnerChatActivity, VideoPlayerActivity::class.java)
                    mIntent.putExtra("url", message.filePath)
                    fuguInnerChatActivity.startActivity(mIntent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        setReplyClick(otherVideoMessageViewHolder.tvReplies, message, false, otherVideoMessageViewHolder.tvImagewithMessage)
        otherVideoMessageViewHolder.tvUserName.text = message.fromName

        otherVideoMessageViewHolder.tvUserName.setOnClickListener {
            if (message.userType != 0) {
                openProfile("mention://" + message.userId.toString(), mContext)
            }
        }
        if (message.isAnimate && !isAlreadyAnimated) {
            isAlreadyAnimated = true
            setAnimation(2000, Color.parseColor("#fff6d1"), Color.parseColor("#00FFFFFF"), otherVideoMessageViewHolder.llRoot)
        } else {
            setAnimation(0, Color.parseColor("#00FFFFFF"), Color.parseColor("#00FFFFFF"), otherVideoMessageViewHolder.llRoot)
        }
        otherVideoMessageViewHolder.llImageMessage.setPadding(0)
        otherVideoMessageViewHolder.llImageMessage.clipToOutline = true
        setReplyData(otherVideoMessageViewHolder.tvReplyTextList, otherVideoMessageViewHolder.ivReplyImageList, otherVideoMessageViewHolder.llReplyList, message)
    }

    private fun setVideoMessageSelf(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, message: Message) {
        val selfVideoMessageViewHolder: SelfVideoMessageViewHolder = holder as SelfVideoMessageViewHolder
        if (chatActivity != null && ((chatType != ChatType.BOT) || (chatType == ChatType.BOT && userType == UserType.SELF_BOT))) {
            selfVideoMessageViewHolder.ivImageMessage.setOnTouchListener(ExtendedTouchListener(selfVideoMessageViewHolder.itemView, selfVideoMessageViewHolder.llImageMessage,
                    selfVideoMessageViewHolder.llRoot,
                    message,
                    chatActivity,
                    recyclerView,
                    selfVideoMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
            selfVideoMessageViewHolder.tvImageTime.setOnTouchListener(ExtendedTouchListener(selfVideoMessageViewHolder.itemView, selfVideoMessageViewHolder.llImageMessage,
                    selfVideoMessageViewHolder.llRoot,
                    message,
                    chatActivity,
                    recyclerView,
                    selfVideoMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
        }

        selfVideoMessageViewHolder.tvFileSize.text = message.fileSize

        setSelfMessageBackground(selfVideoMessageViewHolder.tvReplies, selfVideoMessageViewHolder.llRoot, selfVideoMessageViewHolder.llImageMessage, position, message.userId)
//        setBackgroundColor(selfVideoMessageViewHolder.llImageMessage.background as NinePatchDrawable, colorConfig.fuguBgMessageYou)
        setTime(selfVideoMessageViewHolder.tvImageTime, message.sentAtUtc)
        setStarredMessage(selfVideoMessageViewHolder.ivStar, message.isStarred)
        val requestOptions = RequestOptions()
                .dontAnimate()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transforms(CenterCrop(), RoundedCorners(3), BlurTransform(5, 3))
        try {
            if (mContext is ChatActivity) {
                Glide.with(mContext as ChatActivity)
                        .asBitmap()
                        .apply(requestOptions)
                        .load(message.thumbnailUrl)
                        .into(selfVideoMessageViewHolder.ivImageMessage)
            } else {
                Glide.with(mContext as FuguInnerChatActivity)
                        .asBitmap()
                        .apply(requestOptions)
                        .load(message.thumbnailUrl)
                        .into(selfVideoMessageViewHolder.ivImageMessage)
            }
        } catch (e: Exception) {

        }
        setMessageStatus(selfVideoMessageViewHolder.ivMessageState, message.messageStatus, true)
        if (!TextUtils.isEmpty(message.message)) {
            selfVideoMessageViewHolder.tvImagewithMessage.visibility = View.VISIBLE
            if (message.messageState == 4) {
                val messageString = message.message + " <font color='grey'><small> (edited)</small></font>"
                manipulateAndSetText(selfVideoMessageViewHolder.tvImagewithMessage, messageString, message.messageState)
            } else {
                manipulateAndSetText(selfVideoMessageViewHolder.tvImagewithMessage, message.message, message.messageState)
            }
        } else {
            selfVideoMessageViewHolder.tvImagewithMessage.visibility = View.GONE
        }
        if (message.threadMessage) {
            selfVideoMessageViewHolder.tvReplies.visibility = View.VISIBLE
            setReplies(selfVideoMessageViewHolder.tvReplies, message.threadMessageCount)
        } else {
            selfVideoMessageViewHolder.tvReplies.visibility = View.GONE
        }
        setImageHeightAndWidth(selfVideoMessageViewHolder.ivImageMessage, selfVideoMessageViewHolder.rlImageMessage, selfVideoMessageViewHolder.llImageMessage, message, true)
        setItemLongClick(selfVideoMessageViewHolder.itemView, message, true, selfVideoMessageViewHolder.tvImageTime)
        setItemLongClick(selfVideoMessageViewHolder.ivImageMessage, message, true, selfVideoMessageViewHolder.tvImageTime)
        setEmojisCardView(selfVideoMessageViewHolder.llEmojis, selfVideoMessageViewHolder.emojiLayoutList, selfVideoMessageViewHolder.emojiTextList, selfVideoMessageViewHolder.emojiCountList, message, position
        )
        selfVideoMessageViewHolder.llForward.setOnClickListener {
            setForwardClick(message)
        }

        setReplyClick(selfVideoMessageViewHolder.tvReplies, message, false, selfVideoMessageViewHolder.tvImagewithMessage)
        selfVideoMessageViewHolder.ivPlay.setOnClickListener {
            try {
                if (isChatActivity()) {
                    val mIntent = Intent(chatActivity, VideoPlayerActivity::class.java)
                    mIntent.putExtra("url", message.filePath)
                    chatActivity?.startActivity(mIntent)
                } else {
                    val mIntent = Intent(fuguInnerChatActivity, VideoPlayerActivity::class.java)
                    mIntent.putExtra("url", message.filePath)
                    fuguInnerChatActivity.startActivity(mIntent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        if (message.messageStatus == MESSAGE_DELIVERED || message.messageStatus == MESSAGE_SENT || message.messageStatus == MESSAGE_READ) {
            setVideoDownloadStatus(selfVideoMessageViewHolder.llDownload, selfVideoMessageViewHolder.circleProgress, selfVideoMessageViewHolder.ivPlay, selfVideoMessageViewHolder.llForward, message, selfVideoMessageViewHolder.ivCrossCancel)
        } else {
            setVideoUploadStatus(selfVideoMessageViewHolder.tvImageTime, selfVideoMessageViewHolder.circleProgress, selfVideoMessageViewHolder.llForward, selfVideoMessageViewHolder.btnRetry, selfVideoMessageViewHolder.ivPlay, message)
        }
        selfVideoMessageViewHolder.btnRetry.setOnClickListener {
            if (getIsNetworkConnected()) {
                selfVideoMessageViewHolder.circleProgress.visibility = View.VISIBLE
                selfVideoMessageViewHolder.btnRetry.visibility = View.GONE
                selfVideoMessageViewHolder.llForward.visibility = View.GONE

                if (mOnRetry != null) {
                    val fileDetails = FuguFileDetails()
                    if (!TextUtils.isEmpty(message.unsentFilePath)) {
                        fileDetails.filePath = message.unsentFilePath
                    } else {
                        fileDetails.filePath = message.image_url
                    }
                    fileDetails.fileExtension = message.fileExtension
                    fileDetails.fileSize = message.fileSize
                    fileDetails.fileName = message.fileName
                    message.messageStatus = FuguAppConstant.MESSAGE_UNSENT
                    try {
                        mOnRetry?.onRetry(message.message, message.image_url, message.messageIndex,
                                FuguAppConstant.VIDEO_MESSAGE, fileDetails, message.muid,
                                message.thumbnailUrl, message.image_url, message.url, message.image_url_100x100, message.imageHeight, message.imageWidth)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        if (chatActivity != null) {
                            showErrorMessage(chatActivity!!, "Something went wrong please try again!")
                            chatActivity?.cancelMessage(selfVideoMessageViewHolder.position)
                        } else {
                            showErrorMessage(fuguInnerChatActivity, "Something went wrong please try again!")
                            fuguInnerChatActivity.cancelMessage(selfVideoMessageViewHolder.position)
                        }
                    }
                }
            }
        }
        if (message.isAnimate && !isAlreadyAnimated) {
            isAlreadyAnimated = true
            setAnimation(2000, Color.parseColor("#fff6d1"), Color.parseColor("#00FFFFFF"), selfVideoMessageViewHolder.llRoot)
        } else {
            setAnimation(0, Color.parseColor("#00FFFFFF"), Color.parseColor("#00FFFFFF"), selfVideoMessageViewHolder.llRoot)
        }
        selfVideoMessageViewHolder.llDownload.setOnClickListener {
            if (getIsNetworkConnected() && checkAndObtainStoragePermission()) {
                performClickCount = 1
                val fullPath = Environment.getExternalStorageDirectory().absolutePath + "/Download"
                downloadIdVideo = downloadFileFromUrl(fullPath, message, position, "Video")
                if (downloadIdVideo != 1) {
                    message.downloadId = downloadIdVideo
                }
                holder.circleProgress.visibility = View.VISIBLE
                holder.ivCrossCancel.visibility = View.VISIBLE
                holder.llDownload.visibility = View.GONE
            }
        }
        selfVideoMessageViewHolder.rlDownloading.setOnClickListener {
            PRDownloader.pause(downloadIdVideo)
            holder.llDownload.visibility = View.VISIBLE
            holder.circleProgress.visibility = View.GONE
            holder.ivCrossCancel.visibility = View.GONE
        }
        selfVideoMessageViewHolder.llImageMessage.setPadding(0)
        selfVideoMessageViewHolder.llImageMessage.clipToOutline = true
        setReplyData(selfVideoMessageViewHolder.tvReplyTextList, selfVideoMessageViewHolder.ivReplyImageList, selfVideoMessageViewHolder.llReplyList, message)
    }

    private fun dpToPx(dpParam: Int): Int {
        val d = mContext.resources.displayMetrics.density
        return (dpParam * d).toInt()
    }

    @SuppressLint("SetTextI18n")
    private fun setUnreadItem(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, message: Message) {
        try {
            val unreadViewHolder: UnreadViewHolder = holder as UnreadViewHolder
            if (message.count == 1) {
                unreadViewHolder.tvMessage.text = message.count.toString() + " Unread Message"
            } else {
                unreadViewHolder.tvMessage.text = message.count.toString() + " Unread Messages"
            }
        } catch (e: Exception) {
//            Crashlytics.logException(e)
            setViewModelInCatch(holder, position, message, true)
        }
    }

    private fun setTextMessageSelf(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, message: Message) {
        val selfTextMessageViewHolder: SelfTextMessageViewHolder = holder as SelfTextMessageViewHolder
        setSelfMessageBackground(selfTextMessageViewHolder.tvReplies, selfTextMessageViewHolder.rlRoot, selfTextMessageViewHolder.llMessage, position, message.userId)
//        setBackgroundColor(selfTextMessageViewHolder.llMessage.background as NinePatchDrawable, colorConfig.fuguBgMessageYou)
        try {
            if (chatActivity != null && ((chatType != ChatType.BOT) || (chatType == ChatType.BOT && userType == UserType.SELF_BOT))) {
                selfTextMessageViewHolder.tvMessage.setOnTouchListener(ExtendedTouchListener(selfTextMessageViewHolder.itemView, selfTextMessageViewHolder.llMainMessage,
                        selfTextMessageViewHolder.rlRoot,
                        message,
                        chatActivity,
                        recyclerView,
                        selfTextMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
                selfTextMessageViewHolder.llMessage.setOnTouchListener(ExtendedTouchListener(selfTextMessageViewHolder.itemView, selfTextMessageViewHolder.llMainMessage,
                        selfTextMessageViewHolder.rlRoot,
                        message,
                        chatActivity,
                        recyclerView,
                        selfTextMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
                selfTextMessageViewHolder.tvTime.setOnTouchListener(ExtendedTouchListener(selfTextMessageViewHolder.itemView, selfTextMessageViewHolder.llMainMessage,
                        selfTextMessageViewHolder.rlRoot,
                        message,
                        chatActivity,
                        recyclerView,
                        selfTextMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))

            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        var messageString = if (message.messageState == 4) {
            if (!TextUtils.isEmpty(message.alteredMessage)) {
                message.alteredMessage + " <font color='grey'><small> (edited)</small></font>"
            } else {
                message.message + " <font color='grey'><small> (edited)</small></font>"
            }
        } else {
            if (!TextUtils.isEmpty(message.alteredMessage)) {
                message.alteredMessage
            } else {
                message.message
            }
        }
        manipulateAndSetText(selfTextMessageViewHolder.tvMessage, messageString, message.messageState)

        setTime(selfTextMessageViewHolder.tvTime, message.sentAtUtc)
        setMessageStatus(selfTextMessageViewHolder.ivMessageState, message.messageStatus, false)
        setStarredMessage(selfTextMessageViewHolder.ivStar, message.isStarred)
        if (message.threadMessage) {
            selfTextMessageViewHolder.llReplies.visibility = View.VISIBLE
            setReplies(selfTextMessageViewHolder.tvReplies, message.threadMessageCount)
        } else {
            selfTextMessageViewHolder.llReplies.visibility = View.GONE
        }
        setItemLongClick(selfTextMessageViewHolder.itemView, message, true, selfTextMessageViewHolder.tvTime)
        setItemLongClick(selfTextMessageViewHolder.tvMessage, message, true, selfTextMessageViewHolder.tvTime)
        setEmojisCardView(selfTextMessageViewHolder.llEmojis, selfTextMessageViewHolder.emojiLayoutList, selfTextMessageViewHolder.emojiTextList, selfTextMessageViewHolder.emojiCountList, message, position
        )
        messageRetryFunctionality(selfTextMessageViewHolder.llRetry, selfTextMessageViewHolder.tvTryAgain, selfTextMessageViewHolder.tvCancel, message, position)
        setReplyClick(selfTextMessageViewHolder.tvReplies, message, false, selfTextMessageViewHolder.tvMessage)
        if (message.isAnimate && !isAlreadyAnimated) {
            isAlreadyAnimated = true
            setAnimation(2000, Color.parseColor("#fff6d1"), Color.parseColor("#00FFFFFF"), selfTextMessageViewHolder.rlRoot)
        } else {
            if (!message.editMode) {
                setAnimation(0, Color.parseColor("#00FFFFFF"), Color.parseColor("#00FFFFFF"), selfTextMessageViewHolder.rlRoot)
            } else {
                selfTextMessageViewHolder.rlRoot.setBackgroundColor(Color.parseColor("#fff6d1"))
            }
        }

        if (message.editMode) {
            selfTextMessageViewHolder.rlRoot.setBackgroundColor(Color.parseColor("#fff6d1"))
        } else {
            selfTextMessageViewHolder.rlRoot.setBackgroundColor(Color.parseColor("#00FFFFFF"))
        }

        setReplyData(selfTextMessageViewHolder.tvReplyTextList, selfTextMessageViewHolder.ivReplyImageList, selfTextMessageViewHolder.llReplyList, message)
    }

    private fun setStarredMessage(ivStar: AppCompatImageView, starred: Int) {
        if (starred == 1) {
            ivStar.visibility = View.VISIBLE
        } else {
            ivStar.visibility = View.GONE
        }
    }

    private fun setAnimation(duration: Long, colorOne: Int, colorTwo: Int, rlRoot: View) {
        ObjectAnimator.ofObject(rlRoot, "backgroundColor", ArgbEvaluator(), colorOne, colorTwo)
                .setDuration(duration)
                .start()
    }

    private fun messageRetryFunctionality(llRetry: LinearLayout, tvTryAgain: TextView, tvCancel: TextView, message: Message, position: Int) {
        if (message.isExpired && (message.messageStatus != MESSAGE_SENT || message.messageStatus != MESSAGE_READ || message.messageStatus != MESSAGE_DELIVERED)) {
            llRetry.visibility = View.VISIBLE
            tvTryAgain.setOnClickListener {
                if (isChatActivity()) {
                    chatActivity?.sendMessage(position)
                } else {
                    fuguInnerChatActivity.sendMessage(position)
                }
            }
            tvCancel.setOnClickListener {
                if (isChatActivity()) {
                    chatActivity?.cancelMessage(position)
                } else {
                    fuguInnerChatActivity.cancelMessage(position)
                }
            }
        } else {
            llRetry.visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setEmojis(llEmojis: LinearLayout, emojiLayoutList: ArrayList<LinearLayout>,
                          emojiTextViewsList: ArrayList<TextView>, emojiCountTextViewsList: ArrayList<TextView>, message: Message, position: Int) {
        if (message.userReaction != null && message.userReaction.reaction != null && message.userReaction.reaction.size > 0) {
            var randomSelf = -1
            if (message.userReaction.reaction.size > 3) {
                for (i in 0 until message.userReaction.reaction.size) {
                    val userId = com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userId.toString()
                    if (message.userReaction.reaction[i].users.contains(userId)) {
                        val reactions = message.userReaction.reaction
                        val ran = Random()
                        if (i > 2) {
                            randomSelf = 2
                            Collections.swap(reactions, i, randomSelf)
                        } else {
                            randomSelf = i
                        }
                        break
                    }
                }
            } else {
                for (i in 0 until message.userReaction.reaction.size) {
                    val userId = com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userId.toString()
                    if (message.userReaction.reaction[i].users.contains(userId)) {
                        randomSelf = i
                    }
                }
            }
            for (item in 0..2) {
                emojiLayoutList[item].setOnClickListener {
                    if (!isMessageDeleted) {
                        if (isChatActivity()) {
                            chatActivity?.getClickedEmoji(emojiTextViewsList[item].text.toString().trim { it <= ' ' }.split(" ".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0], true, message.muid)
                        } else {
                            fuguInnerChatActivity.getClickedEmoji(emojiTextViewsList[item].text.toString().trim { it <= ' ' }.split(" ".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0], true, message.muid)
                        }
                        animation = AnimationUtils.loadAnimation(mContext,
                                R.anim.emoji_anim)
                        emojiTextViewsList[item].startAnimation(animation)
                    }
                }
                emojiTextViewsList[item].setOnClickListener {
                    if (!isMessageDeleted) {
                        if (isChatActivity()) {
                            chatActivity?.getClickedEmoji(emojiTextViewsList[item].text.toString().trim { it <= ' ' }.split(" ".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0], true, message.muid)
                        } else {
                            fuguInnerChatActivity.getClickedEmoji(emojiTextViewsList[item].text.toString().trim { it <= ' ' }.split(" ".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0], true, message.muid)
                        }
                        animation = AnimationUtils.loadAnimation(mContext,
                                R.anim.emoji_anim)
                        emojiTextViewsList[item].startAnimation(animation)
                    }
                }
                emojiCountTextViewsList[item].setOnClickListener {
                    if (!isMessageDeleted) {
                        if (isChatActivity()) {
                            chatActivity?.getClickedEmoji(emojiTextViewsList[item].text.toString().trim { it <= ' ' }.split(" ".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0], true, message.muid)
                        } else {
                            fuguInnerChatActivity.getClickedEmoji(emojiTextViewsList[item].text.toString().trim { it <= ' ' }.split(" ".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0], true, message.muid)
                        }
                        animation = AnimationUtils.loadAnimation(mContext,
                                R.anim.emoji_anim)
                        emojiTextViewsList[item].startAnimation(animation)
                    }
                }
            }

            for (item in emojiTextViewsList.indices) {
                emojiTextViewsList[item].setOnLongClickListener {
                    if (!isMessageDeleted) {
                        openReactionDialog(message.userReaction)
                    }
                    true
                }
                if (item < 3) {
                    emojiLayoutList[item].setOnLongClickListener {
                        if (!isMessageDeleted) {
                            openReactionDialog(message.userReaction)
                        }
                        true
                    }
                    emojiCountTextViewsList[item].setOnLongClickListener {
                        if (!isMessageDeleted) {
                            openReactionDialog(message.userReaction)
                        }
                        true
                    }
                }
            }

            emojiTextViewsList[4].setOnClickListener {
                if (getIsNetworkConnected() && (message.messageStatus == FuguAppConstant.MESSAGE_READ
                                || message.messageStatus == FuguAppConstant.MESSAGE_DELIVERED
                                || message.messageStatus == FuguAppConstant.MESSAGE_SENT)) {
                    if (!isMessageDeleted) {
                        if (isChatActivity()) {
                            chatActivity?.openDialog(message.muid)
                        } else {
                            fuguInnerChatActivity.openDialog(message.muid)
                        }
                    }
                }
            }

            val sizeOfReactions = message.userReaction.reaction.size

            emojiLayoutList[3].setOnClickListener {
                if (!isMessageDeleted) {
                    openReactionDialog(message.userReaction)
                }
            }
            emojiCountTextViewsList[3].setOnClickListener {
                if (!isMessageDeleted) {
                    openReactionDialog(message.userReaction)
                }
            }
            emojiCountTextViewsList[3].setOnClickListener {
                if (!isMessageDeleted) {
                    openReactionDialog(message.userReaction)
                }
            }

            if (sizeOfReactions > 0) {
                llEmojis.visibility = View.VISIBLE
                var item = 0
                while (item < emojiLayoutList.size - 1 && item < sizeOfReactions) {
                    emojiLayoutList[item].visibility = View.VISIBLE
                    if (item < 3) {
                        emojiTextViewsList[item].text = message.userReaction.reaction[item].reaction
                        emojiCountTextViewsList[item].text = message.userReaction.reaction[item].users.size.toString() + ""
                        if (emojiLayoutList[4].visibility == View.GONE) {
                            emojiLayoutList[4].visibility = View.VISIBLE
                        }
                    } else {
                        if (emojiLayoutList[4].visibility == View.VISIBLE) {
                            emojiLayoutList[4].visibility = View.GONE
                        }
                        emojiTextViewsList[item].text = "+" + (message.userReaction.reaction.size - 3)
                    }
                    item++
                }
                if (sizeOfReactions <= 4) {
                    for (goneItem in sizeOfReactions until emojiLayoutList.size - 1) {
                        emojiLayoutList[goneItem].visibility = View.GONE
                    }
                }
            } else {
                llEmojis.visibility = View.GONE
            }

            for (item in emojiLayoutList.indices) {
                if (item == randomSelf) {
                    emojiLayoutList[item].setBackgroundResource(R.drawable.round_small_rectangle_border_layout_blue)
                    emojiCountTextViewsList[item].setTextColor(mContext.resources.getColor(R.color.mentionAndLinks))
                    if (emojiLayoutList[4].visibility == View.VISIBLE) {
                        emojiLayoutList[4].visibility = View.GONE
                    }
                } else {
                    emojiLayoutList[item].setBackgroundResource(R.drawable.round_small_rectangle_border_layout_white)
                    emojiCountTextViewsList[item].setTextColor(mContext.resources.getColor(R.color.black))
                }
            }

        } else if (!isChatActivity() && position == 0) {
            llEmojis.visibility = View.VISIBLE
            for (emojiLayout in emojiLayoutList) {
                emojiLayout.visibility = View.GONE
            }
            emojiLayoutList[4].visibility = View.VISIBLE
            emojiLayoutList[4].setOnClickListener {
                fuguInnerChatActivity.openDialog(message.muid)
            }
        } else {
            llEmojis.visibility = View.GONE

        }
    }

    @SuppressLint("SetTextI18n")
    private fun setEmojisCardView(llEmojis: LinearLayout, emojiLayoutList: ArrayList<CardView>,
                                  emojiTextViewsList: ArrayList<TextView>, emojiCountTextViewsList: ArrayList<TextView>, message: Message, position: Int) {
        if (message.userReaction != null && message.userReaction.reaction != null && message.userReaction.reaction.size > 0) {
            var randomSelf = -1
            if (message.userReaction.reaction.size > 3) {
                for (i in 0 until message.userReaction.reaction.size) {
                    val userId = com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userId.toString()
                    if (message.userReaction.reaction[i].users.contains(userId)) {
                        val reactions = message.userReaction.reaction
                        val ran = Random()
                        if (i > 2) {
                            randomSelf = 2
                            Collections.swap(reactions, i, randomSelf)
                        } else {
                            randomSelf = i
                        }
                        break
                    }
                }
            } else {
                for (i in 0 until message.userReaction.reaction.size) {
                    val userId = com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userId.toString()
                    if (message.userReaction.reaction[i].users.contains(userId)) {
                        randomSelf = i
                    }
                }
            }
            for (item in 0..2) {
                emojiLayoutList[item].setOnClickListener {
                    if (!isMessageDeleted) {
                        if (isChatActivity()) {
                            chatActivity?.getClickedEmoji(emojiTextViewsList[item].text.toString().trim { it <= ' ' }.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0], true, message.muid)
                        } else {
                            fuguInnerChatActivity.getClickedEmoji(emojiTextViewsList[item].text.toString().trim { it <= ' ' }.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0], true, message.muid)
                        }
                        animation = AnimationUtils.loadAnimation(mContext,
                                R.anim.emoji_anim)
                        emojiTextViewsList[item].startAnimation(animation)
                    }
                }
                emojiTextViewsList[item].setOnClickListener {
                    if (!isMessageDeleted) {
                        if (isChatActivity()) {
                            chatActivity?.getClickedEmoji(emojiTextViewsList[item].text.toString().trim { it <= ' ' }.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0], true, message.muid)
                        } else {
                            fuguInnerChatActivity.getClickedEmoji(emojiTextViewsList[item].text.toString().trim { it <= ' ' }.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0], true, message.muid)
                        }
                        animation = AnimationUtils.loadAnimation(mContext,
                                R.anim.emoji_anim)
                        emojiTextViewsList[item].startAnimation(animation)
                    }
                }
                emojiCountTextViewsList[item].setOnClickListener {
                    if (!isMessageDeleted) {
                        if (isChatActivity()) {
                            chatActivity?.getClickedEmoji(emojiTextViewsList[item].text.toString().trim { it <= ' ' }.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0], true, message.muid)
                        } else {
                            fuguInnerChatActivity.getClickedEmoji(emojiTextViewsList[item].text.toString().trim { it <= ' ' }.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0], true, message.muid)
                        }
                        animation = AnimationUtils.loadAnimation(mContext,
                                R.anim.emoji_anim)
                        emojiTextViewsList[item].startAnimation(animation)
                    }
                }
            }

            for (item in emojiTextViewsList.indices) {
                emojiTextViewsList[item].setOnLongClickListener {
                    if (!isMessageDeleted) {
                        openReactionDialog(message.userReaction)
                    }
                    true
                }
                if (item < 3) {
                    emojiLayoutList[item].setOnLongClickListener {
                        if (!isMessageDeleted) {
                            openReactionDialog(message.userReaction)
                        }
                        true
                    }
                    emojiCountTextViewsList[item].setOnLongClickListener {
                        if (!isMessageDeleted) {
                            openReactionDialog(message.userReaction)
                        }
                        true
                    }
                }
            }

            emojiTextViewsList[4].setOnClickListener {
                if (getIsNetworkConnected() && (message.messageStatus == FuguAppConstant.MESSAGE_READ
                                || message.messageStatus == FuguAppConstant.MESSAGE_DELIVERED
                                || message.messageStatus == FuguAppConstant.MESSAGE_SENT)) {
                    if (!isMessageDeleted) {
                        if (isChatActivity()) {
                            val location = IntArray(2)
                            emojiTextViewsList[4].getLocationInWindow(location)
                            chatActivity?.openReactionDialog(message.muid, location)
                        } else {
                            val location = IntArray(2)
                            emojiTextViewsList[4].getLocationInWindow(location)
                            fuguInnerChatActivity.openReactionDialogLocation(message.muid, location)
                        }
                    }
                }
            }

            val sizeOfReactions = message.userReaction.reaction.size

            emojiLayoutList[3].setOnClickListener {
                if (!isMessageDeleted) {
                    openReactionDialog(message.userReaction)
                }
            }
            emojiCountTextViewsList[3].setOnClickListener {
                if (!isMessageDeleted) {
                    openReactionDialog(message.userReaction)
                }
            }
            emojiCountTextViewsList[3].setOnClickListener {
                if (!isMessageDeleted) {
                    openReactionDialog(message.userReaction)
                }
            }

            if (sizeOfReactions > 0) {
                llEmojis.visibility = View.VISIBLE
                var item = 0
                while (item < emojiLayoutList.size - 1 && item < sizeOfReactions) {
                    emojiLayoutList[item].visibility = View.VISIBLE
                    if (item < 3) {
                        emojiTextViewsList[item].text = message.userReaction.reaction[item].reaction

                        if (message.userReaction.reaction[item].users.size > 1) {
                            emojiCountTextViewsList[item].visibility = View.VISIBLE
                        } else {
                            emojiCountTextViewsList[item].visibility = View.GONE
                        }

                        emojiCountTextViewsList[item].text = message.userReaction.reaction[item].users.size.toString() + ""
                        if (emojiLayoutList[4].visibility == View.GONE) {
                            emojiLayoutList[4].visibility = View.VISIBLE
                        }
                    } else {
                        if (emojiLayoutList[4].visibility == View.VISIBLE) {
                            emojiLayoutList[4].visibility = View.GONE
                        }
                        emojiTextViewsList[item].text = "+" + (message.userReaction.reaction.size - 3)
                    }
                    item++
                }
                if (sizeOfReactions <= 4) {
                    for (goneItem in sizeOfReactions until emojiLayoutList.size - 1) {
                        emojiLayoutList[goneItem].visibility = View.GONE
                    }
                }
            } else {
                llEmojis.visibility = View.GONE
            }

            for (item in emojiLayoutList.indices) {
                if (item == randomSelf) {
                    emojiLayoutList[item].setBackgroundResource(R.drawable.round_small_rectangle_border_layout_blue)
                    emojiCountTextViewsList[item].setTextColor(mContext.resources.getColor(R.color.mentionAndLinks))
                    if (emojiLayoutList[4].visibility == View.VISIBLE) {
                        emojiLayoutList[4].visibility = View.GONE
                    }
                } else {
                    emojiLayoutList[item].setBackgroundResource(R.drawable.round_small_rectangle_border_layout_white)
                    emojiCountTextViewsList[item].setTextColor(mContext.resources.getColor(R.color.black))
                }
            }

        } else if (!isChatActivity() && position == 0) {
            llEmojis.visibility = View.VISIBLE
            for (emojiLayout in emojiLayoutList) {
                emojiLayout.visibility = View.GONE
            }
            emojiLayoutList[4].visibility = View.VISIBLE
            emojiLayoutList[4].setOnClickListener {
                fuguInnerChatActivity.openDialog(message.muid)
            }
        } else {
            llEmojis.visibility = View.GONE

        }
    }


    private fun setTextMessageOther(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, message: Message) {
        val otherTextMessageViewHolder: OtherTextMessageViewHolder = holder as OtherTextMessageViewHolder
        setOtherMessageBackground(otherTextMessageViewHolder.tvReplies, otherTextMessageViewHolder.rlRoot, otherTextMessageViewHolder.llMessage, position, otherTextMessageViewHolder.tvUserName, message.userId)
//        setBackgroundColor(otherTextMessageViewHolder.llMessage.background as NinePatchDrawable, colorConfig.fuguBgMessageFrom)
        try {
            if (chatActivity != null && ((chatType != ChatType.BOT) || (chatType == ChatType.BOT && userType == UserType.SELF_BOT))) {
                otherTextMessageViewHolder.tvMessage.setOnTouchListener(ExtendedTouchListener(otherTextMessageViewHolder.itemView, otherTextMessageViewHolder.llMainMessage,
                        otherTextMessageViewHolder.rlRoot,
                        message,
                        chatActivity,
                        recyclerView,
                        otherTextMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
                otherTextMessageViewHolder.llMessage.setOnTouchListener(ExtendedTouchListener(otherTextMessageViewHolder.itemView, otherTextMessageViewHolder.llMainMessage,
                        otherTextMessageViewHolder.rlRoot,
                        message,
                        chatActivity,
                        recyclerView,
                        otherTextMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
                otherTextMessageViewHolder.tvTime.setOnTouchListener(ExtendedTouchListener(otherTextMessageViewHolder.itemView, otherTextMessageViewHolder.llMainMessage,
                        otherTextMessageViewHolder.rlRoot,
                        message,
                        chatActivity,
                        recyclerView,
                        otherTextMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
                otherTextMessageViewHolder.tvUserName.setOnTouchListener(ExtendedTouchListener(otherTextMessageViewHolder.itemView, otherTextMessageViewHolder.llMainMessage,
                        otherTextMessageViewHolder.rlRoot,
                        message,
                        chatActivity,
                        recyclerView,
                        otherTextMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        var messageString = ""
        if (message.messageState == 4) {
            if (!TextUtils.isEmpty(message.alteredMessage)) {
                messageString = message.alteredMessage + " <font color='grey'><small> (edited)</small></font>"
            } else {
                messageString = message.message + " <font color='grey'><small> (edited)</small></font>"
            }
        } else {
            if (!TextUtils.isEmpty(message.alteredMessage)) {
                messageString = message.alteredMessage
            } else {
                messageString = message.message
            }
        }
        manipulateAndSetText(otherTextMessageViewHolder.tvMessage, messageString, message.messageState)
        setTime(otherTextMessageViewHolder.tvTime, message.sentAtUtc)
        setStarredMessage(otherTextMessageViewHolder.ivStar, message.isStarred)
        if (message.threadMessage) {
            otherTextMessageViewHolder.llReplies.visibility = View.VISIBLE
            setReplies(otherTextMessageViewHolder.tvReplies, message.threadMessageCount)
        } else {
            otherTextMessageViewHolder.llReplies.visibility = View.GONE
        }
        setItemLongClick(otherTextMessageViewHolder.itemView, message, false, otherTextMessageViewHolder.tvTime)
        setItemLongClick(otherTextMessageViewHolder.tvMessage, message, false, otherTextMessageViewHolder.tvTime)
        setEmojisCardView(otherTextMessageViewHolder.llEmojis, otherTextMessageViewHolder.emojiLayoutList, otherTextMessageViewHolder.emojiTextList, otherTextMessageViewHolder.emojiCountList, message, position)
        setReplyClick(otherTextMessageViewHolder.tvReplies, message, false, otherTextMessageViewHolder.tvMessage)
        setUserName(otherTextMessageViewHolder.tvUserName, otherTextMessageViewHolder.tvMessage, otherTextMessageViewHolder.tvTime, message)
        if (message.isAnimate && !isAlreadyAnimated) {
            isAlreadyAnimated = true
            setAnimation(2000, Color.parseColor("#fff6d1"), Color.parseColor("#00FFFFFF"), otherTextMessageViewHolder.rlRoot)
        } else {
            setAnimation(0, Color.parseColor("#00FFFFFF"), Color.parseColor("#00FFFFFF"), otherTextMessageViewHolder.rlRoot)
        }
        setReplyData(otherTextMessageViewHolder.tvReplyTextList, otherTextMessageViewHolder.ivReplyImageList, otherTextMessageViewHolder.llReplyList, message)
    }

    private fun setReplyData(tvReplyTextList: java.util.ArrayList<AppCompatTextView>, ivReplyImageList: java.util.ArrayList<AppCompatImageView>, llReplyList: java.util.ArrayList<LinearLayout>, message: Message) {
        for (layout in llReplyList) {
            layout.clipToOutline = true
        }
        if (message.thread_message_data != null) {
            when (message.thread_message_data.size) {
                0 -> {
                    for (layout in llReplyList) {
                        layout.visibility = View.GONE
                    }
                    for (layout in ivReplyImageList) {
                        layout.visibility = View.GONE
                    }
                }
                1 -> {
                    llReplyList[0].visibility = View.VISIBLE
                    llReplyList[1].visibility = View.GONE
                    llReplyList[2].visibility = View.GONE
                    ivReplyImageList[1].visibility = View.GONE
                    ivReplyImageList[2].visibility = View.GONE
                    tvReplyTextList[1].visibility = View.GONE
                    tvReplyTextList[2].visibility = View.GONE
                    setReplyImageOrText(ivReplyImageList[0], tvReplyTextList[0], 0, message)
                }
                2 -> {
                    llReplyList[0].visibility = View.VISIBLE
                    llReplyList[1].visibility = View.VISIBLE
                    llReplyList[2].visibility = View.GONE
                    ivReplyImageList[2].visibility = View.GONE
                    tvReplyTextList[2].visibility = View.GONE
                    setReplyImageOrText(ivReplyImageList[0], tvReplyTextList[0], 0, message)
                    setReplyImageOrText(ivReplyImageList[1], tvReplyTextList[1], 1, message)
                }
                3 -> {
                    for (layout in llReplyList) {
                        layout.visibility = View.VISIBLE
                    }
                    setReplyImageOrText(ivReplyImageList[0], tvReplyTextList[0], 0, message)
                    setReplyImageOrText(ivReplyImageList[1], tvReplyTextList[1], 1, message)
                    setReplyImageOrText(ivReplyImageList[2], tvReplyTextList[2], 2, message)
                }
            }
        }
    }

    private fun setReplyImageOrText(appCompatImageView: AppCompatImageView, appCompatTextView: AppCompatTextView, pos: Int, message: Message) {
        if (!TextUtils.isEmpty(message.thread_message_data[pos].userImage50x50)) {
            appCompatImageView.visibility = View.VISIBLE
            appCompatTextView.visibility = View.GONE

            Glide.with(mContext)
                    .load(message.thread_message_data[pos].userImage50x50)
                    .into(appCompatImageView)
        } else {
            appCompatImageView.visibility = View.GONE
            appCompatTextView.visibility = View.VISIBLE
            appCompatTextView.text = getFirstCharInUpperCase(message.thread_message_data[pos].fullName)
        }
    }

    private fun setUserName(tvUserName: TextView, tvMessage: AppCompatTextView, tvTime: AppCompatTextView, message: Message) {
        tvUserName.text = message.fromName
        if (tvUserName.length() > tvMessage.length() + tvTime.length() && tvUserName.visibility == View.VISIBLE) {
            val length: Int
            when (tvMessage.length()) {
                1 -> {
                    length = tvUserName.length() - tvMessage.length() - tvTime.length() + 2
                    for (i in 0 until length) {
                        tvMessage.append(mContext.getString(R.string.space))
                    }
                }
                2 -> {
                    length = tvUserName.length() - tvMessage.length() - tvTime.length()
                    for (i in 0 until length) {
                        tvMessage.append(mContext.getString(R.string.space))
                    }
                }
                3 -> {
                    length = tvUserName.length() - tvMessage.length() - tvTime.length() - 1
                    for (i in 0 until length) {
                        tvMessage.append(mContext.getString(R.string.space))
                    }
                }
                else -> {
                    length = tvUserName.length() - tvMessage.length() - tvTime.length() - 1
                    for (i in 0 until length) {
                        tvMessage.append(mContext.getString(R.string.space))
                    }
                }
            }
        }
        tvUserName.setOnClickListener {
            if (message.userType != 0) {
                openProfile("mention://" + message.userId.toString(), mContext)
            }
        }

    }

    fun deleteImageFromImageList(messageId: String) {
        for (i in 0 until imageFiles.size) {
            if (imageFiles[i].transitionName.equals(messageId)) {
                imageFiles.removeAt(i)
                imageFiles.sortWith(Comparator { one, other -> other.transitionName!!.compareTo(one.transitionName!!) })
                imageFiles.reverse()
                break
            }
        }
    }

    private fun setImageMessageSelf(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, message: Message) {
        val selfImageMessageViewHolder: SelfImageMessageViewHolder = holder as SelfImageMessageViewHolder
        val isSpiked = setSelfMessageBackground(selfImageMessageViewHolder.tvReplies, selfImageMessageViewHolder.llRoot, selfImageMessageViewHolder.llImageMessage, position, message.userId)
        if (chatActivity != null && ((chatType != ChatType.BOT) || (chatType == ChatType.BOT && userType == UserType.SELF_BOT))) {
            selfImageMessageViewHolder.ivImageMessage.setOnTouchListener(ExtendedTouchListener(selfImageMessageViewHolder.itemView, selfImageMessageViewHolder.llImageMessage,
                    selfImageMessageViewHolder.llRoot,
                    message,
                    chatActivity,
                    recyclerView,
                    selfImageMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
            selfImageMessageViewHolder.tvImagewithMessage.setOnTouchListener(ExtendedTouchListener(selfImageMessageViewHolder.itemView, selfImageMessageViewHolder.llImageMessage,
                    selfImageMessageViewHolder.llRoot,
                    message,
                    chatActivity,
                    recyclerView,
                    selfImageMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
            selfImageMessageViewHolder.tvImageTime.setOnTouchListener(ExtendedTouchListener(selfImageMessageViewHolder.itemView, selfImageMessageViewHolder.llImageMessage,
                    selfImageMessageViewHolder.llRoot,
                    message,
                    chatActivity,
                    recyclerView,
                    selfImageMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
            selfImageMessageViewHolder.llDownloadImage.setOnTouchListener(ExtendedTouchListener(selfImageMessageViewHolder.itemView, selfImageMessageViewHolder.llImageMessage,
                    selfImageMessageViewHolder.llRoot,
                    message,
                    chatActivity,
                    recyclerView,
                    selfImageMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
        }
//        setBackgroundColor(selfImageMessageViewHolder.llImageMessage.background as NinePatchDrawable, colorConfig.fuguBgMessageYou)
        setTime(selfImageMessageViewHolder.tvImageTime, message.sentAtUtc)
        setStarredMessage(selfImageMessageViewHolder.ivStar, message.isStarred)
        setImage(selfImageMessageViewHolder.pbDownloading, selfImageMessageViewHolder.llDownloadImage, selfImageMessageViewHolder.ivImageMessage, message, position)
        setMessageStatus(selfImageMessageViewHolder.ivMessageState, message.messageStatus, true)
        var extension = message.image_url!!.split(".")[message.image_url.split(".").size - 1]
        if (extension.toLowerCase() == "png") {
            extension = "jpg"
        }
        val fileName = message.fileName + "_" + message.muid + "." + extension

        val filePathNormal = File(getNormalDirectory(extension) + "/" + fileName)
        val filePathPrivate = File(getPrivateDirectory(extension) + "/" + fileName)


        if (workspaceInfo?.get(currentPos)?.autoDownloadLevel!!.equals(FuguAppConstant.AutoDownloadLevel.BOTH.toString()) || checkConnection(selfImageMessageViewHolder.itemView.context).equals(workspaceInfo?.get(currentPos)?.autoDownloadLevel)) {
            if (!filePathNormal.exists() && !filePathPrivate.exists() && message.downloadStatus != FuguAppConstant.DownloadStatus.DOWNLOAD_IN_PROGRESS.downloadStatus
                    && message.downloadStatus != FuguAppConstant.DownloadStatus.DOWNLOAD_COMPLETED.downloadStatus) {
                if (extension.toLowerCase() != "gif" && checkAndObtainStoragePermission()) {
                    val downloadId = downloadFileFromUrl(extension, message, position, "Image")
                    if (downloadId != 1) {
                        message.downloadId = downloadId
                    }
                    selfImageMessageViewHolder.pbDownloading.visibility = View.VISIBLE
                    selfImageMessageViewHolder.llDownloadImage.visibility = View.GONE
                }
            }
        }

        if (!TextUtils.isEmpty(message.fileSize)) {
            selfImageMessageViewHolder.tvImageSize.visibility = View.VISIBLE
            selfImageMessageViewHolder.tvImageSize.text = message.fileSize
        } else {
            selfImageMessageViewHolder.tvImageSize.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(message.message)) {
            selfImageMessageViewHolder.tvImagewithMessage.visibility = View.VISIBLE
            var messageString = if (message.messageState == 4) {
                if (!TextUtils.isEmpty(message.alteredMessage)) {
                    message.alteredMessage + " <font color='grey'><small> (edited)</small></font>"
                } else {
                    message.message + " <font color='grey'><small> (edited)</small></font>"
                }
            } else {
                if (!TextUtils.isEmpty(message.alteredMessage)) {
                    message.alteredMessage
                } else {
                    message.message
                }
            }
            manipulateAndSetText(selfImageMessageViewHolder.tvImagewithMessage, messageString, message.messageState)
        } else {
            selfImageMessageViewHolder.tvImagewithMessage.visibility = View.GONE

        }
        if (message.threadMessage) {
            selfImageMessageViewHolder.llReplies.visibility = View.VISIBLE
            setReplies(selfImageMessageViewHolder.tvReplies, message.threadMessageCount)
        } else {
            selfImageMessageViewHolder.llReplies.visibility = View.GONE
        }
        setImageHeightAndWidth(selfImageMessageViewHolder.ivImageMessage, selfImageMessageViewHolder.rlImageMessage, selfImageMessageViewHolder.llImageMessage, message, isSpiked)
        setItemLongClick(selfImageMessageViewHolder.itemView, message, true, selfImageMessageViewHolder.tvImageTime)
        setItemLongClick(selfImageMessageViewHolder.ivImageMessage, message, true, selfImageMessageViewHolder.tvImageTime)
        setItemLongClick(selfImageMessageViewHolder.tvImagewithMessage, message, true, selfImageMessageViewHolder.tvImageTime)
        setEmojisCardView(selfImageMessageViewHolder.llEmojis, selfImageMessageViewHolder.emojiLayoutList, selfImageMessageViewHolder.emojiTextList, selfImageMessageViewHolder.emojiCountList, message, position)
        selfImageMessageViewHolder.llForward.setOnClickListener {
            setForwardClick(message)
        }
        setReplyClick(selfImageMessageViewHolder.tvReplies, message, false, selfImageMessageViewHolder.tvImagewithMessage)
        selfImageMessageViewHolder.ivImageMessage.setOnClickListener {
            if (isChatActivity()) {
                if ((mContext as ChatActivity).getState() != MotionEvent.ACTION_MOVE) {
                    var link = if (filePathNormal.exists()) {
                        filePathNormal.absolutePath
                    } else {
                        filePathPrivate.absolutePath
                    }
                    if (filePathNormal.exists() || filePathPrivate.exists() || extension == "gif") {
                        if (extension == "gif") {
                            link = message.image_url
                        }
                    }
                    try {
                        selfImageMessageViewHolder.ivImageMessage.transitionName = message.id.toString()
                        val imageIntent = Intent(mContext, ImageDisplayViewpagerActivity::class.java)
                        var extension = message.image_url!!.split(".")[message.image_url.split(".").size - 1]
                        if (extension.toLowerCase() == "png") {
                            extension = "jpg"
                        }
                        val fileName = message.fileName + "_" + message.muid + "." + extension
                        val filePathNormal = File(getNormalDirectory(extension) + "/" + fileName)
                        val filePathPrivate = File(getPrivateDirectory(extension) + "/" + fileName)
                        var link = ""
                        if (filePathNormal.exists()) {
                            link = filePathNormal.absolutePath
                        } else {
                            link = filePathPrivate.absolutePath
                        }
                        if ((filePathNormal.exists() || filePathPrivate.exists()) && !extension.equals("gif")) {

                            if (extension == "gif") {
                                link = message.image_url
                            }
                            var options: ActivityOptionsCompat? = null
                            var isImageFound = false
                            for (i in 0 until imageFiles.size) {
                                var pos = i
                                if (imageFiles[i].absolutepath.equals(link)) {

                                    if (imageFiles[i].transitionName === null || imageFiles[i].transitionName.equals("0") || !imageFiles[i].transitionName.equals(message.id.toString())) {
                                        val exif = androidx.exifinterface.media.ExifInterface(link)
                                        exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_DATETIME, message.sentAtUtc)
                                        exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MODEL, message.id.toString())
                                        exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MAKE, channelId.toString() + muid)
                                        exif.saveAttributes()
                                        imageFiles[i].transitionName = message.id.toString()
                                        imageFiles.sortWith(Comparator { one, other -> other.transitionName!!.compareTo(one.transitionName!!) })
                                        imageFiles.reverse()

                                        for (x in 0 until imageFiles.size) {
                                            if (imageFiles[x].absolutepath.equals(link)) {
                                                pos = x
                                            }
                                        }
                                    }
                                    if (isChatActivity()) {
                                        ChatActivity.PagerPosition.currentViewPagerPosition = pos
                                        imageIntent.putExtra("currentPos", ChatActivity.PagerPosition.currentViewPagerPosition)
                                    } else {
                                        FuguInnerChatActivity.currentViewPagerposition = pos
                                        imageIntent.putExtra("currentPos", FuguInnerChatActivity.currentViewPagerposition)
                                    }
                                    isImageFound = true
                                    break
                                } else {
                                    ChatActivity.PagerPosition.currentViewPagerPosition = 0
                                    FuguInnerChatActivity.currentViewPagerposition = 0
                                }
                            }


                            if (!isImageFound) {
                                try {
                                    val file = File(link)
                                    if (file.exists()) {
                                        val exif = androidx.exifinterface.media.ExifInterface(link)
                                        exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_DATETIME, message.sentAtUtc)
                                        exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MODEL, message.id.toString())
                                        exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MAKE, channelId.toString() + muid)
                                        exif.saveAttributes()
                                        messageMap[message.muid] = message
                                        getFromSdcard()
                                        for (i in 0 until imageFiles.size) {
                                            if (imageFiles[i].absolutepath.equals(link)) {
                                                if (isChatActivity()) {
                                                    ChatActivity.PagerPosition.currentViewPagerPosition = i
                                                    imageIntent.putExtra("currentPos", ChatActivity.PagerPosition.currentViewPagerPosition)
                                                } else {
                                                    FuguInnerChatActivity.currentViewPagerposition = i
                                                    imageIntent.putExtra("currentPos", FuguInnerChatActivity.currentViewPagerposition)
                                                }
                                                break
                                            } else {
                                                ChatActivity.PagerPosition.currentViewPagerPosition = 0
                                                FuguInnerChatActivity.currentViewPagerposition = 0
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            if (mContext is ChatActivity) {
                                options = ActivityOptionsCompat.makeSceneTransitionAnimation(mContext as ChatActivity,
                                        selfImageMessageViewHolder.ivImageMessage as View, imageFiles[ChatActivity.PagerPosition.currentViewPagerPosition].transitionName)
                            } else {
                                options = ActivityOptionsCompat.makeSceneTransitionAnimation(mContext as FuguInnerChatActivity,
                                        selfImageMessageViewHolder.ivImageMessage as View, imageFiles[FuguInnerChatActivity.currentViewPagerposition].transitionName)
                            }
                            val presentViewHolderList = ArrayList<String>()
                            val first: Int
                            val last: Int
                            if (recyclerView.layoutManager is CustomLinearLayoutManager) {
                                first = (recyclerView.layoutManager as CustomLinearLayoutManager).findFirstVisibleItemPosition()
                                last = (recyclerView.layoutManager as CustomLinearLayoutManager).findLastVisibleItemPosition()
                            } else {
                                first = (recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).findFirstVisibleItemPosition()
                                last = (recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).findLastVisibleItemPosition()
                            }
                            for (item in first..last) {
                                presentViewHolderList.add(messageList[item].id.toString())
                            }
                            imageIntent.putExtra("isChatActivity", isChatActivity())
                            imageIntent.putExtra("imageList", imageFiles)
                            imageIntent.putExtra("presentViewHolderList", presentViewHolderList)
                            imageIntent.putExtra("channelName", label)
                            imageIntent.putExtra("MESSAGE", message)
                            imageIntent.putExtra("chatType", chatType)
                            imageIntent.putExtra(CHANNEL_ID, channelId)
                            imageIntent.putExtra(APP_SECRET_KEY, workspaceInfo?.get(currentPos)?.fuguSecretKey)
                            mContext.startActivity(imageIntent, options.toBundle())
                        } else if (extension == "gif") {
                            val imageIntent = Intent(mContext, ImageDisplayActivity::class.java)
                            var extension = message.image_url!!.split(".")[message.image_url.split(".").size - 1]
                            if (extension.toLowerCase() == "png") {
                                extension = "jpg"
                            }
                            val fileName = message.fileName + "_" + message.muid + "." + extension
                            val filePathNormal = File(getNormalDirectory(extension) + "/" + fileName)
                            val filePathPrivate = File(getPrivateDirectory(extension) + "/" + fileName)
                            var link = ""
                            if (filePathNormal.exists()) {
                                link = filePathNormal.absolutePath
                            } else {
                                link = filePathPrivate.absolutePath
                            }
                            if (filePathNormal.exists() || filePathPrivate.exists() || extension.equals("gif")) {
                                if (extension == "gif") {
                                    link = message.image_url
                                }

                                val image = Image(link, link, message.muid, message.sentAtUtc, label)
                                imageIntent.putExtra("image", image)
                                imageIntent.putExtra("MESSAGE", message)
                                imageIntent.putExtra("BUSINESS_NAME", label)
                                imageIntent.putExtra("chatType", chatType)
                                mContext.startActivity(imageIntent)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                var link = if (filePathNormal.exists()) {
                    filePathNormal.absolutePath
                } else {
                    filePathPrivate.absolutePath
                }
                if (filePathNormal.exists() || filePathPrivate.exists() || extension.equals("gif")) {
                    if (extension == "gif") {
                        link = message.image_url
                    }
                }
                try {
                    selfImageMessageViewHolder.ivImageMessage.transitionName = message.id.toString()
                    val imageIntent = Intent(mContext, ImageDisplayViewpagerActivity::class.java)
                    var extension = message.image_url!!.split(".")[message.image_url.split(".").size - 1]
                    if (extension.toLowerCase() == "png") {
                        extension = "jpg"
                    }
                    val fileName = message.fileName + "_" + message.muid + "." + extension
                    val filePathNormal = File(getNormalDirectory(extension) + "/" + fileName)
                    val filePathPrivate = File(getPrivateDirectory(extension) + "/" + fileName)
                    var link = ""
                    link = if (filePathNormal.exists()) {
                        filePathNormal.absolutePath
                    } else {
                        filePathPrivate.absolutePath
                    }
                    if ((filePathNormal.exists() || filePathPrivate.exists()) && !extension.equals("gif")) {

                        if (extension == "gif") {
                            link = message.image_url
                        }
                        var options: ActivityOptionsCompat? = null
                        var isImageFound = false
                        for (i in 0 until imageFiles.size) {
                            var pos = i
                            if (imageFiles[i].absolutepath == link) {

                                if (imageFiles[i].transitionName === null || imageFiles[i].transitionName == "0" || imageFiles[i].transitionName != message.id.toString()) {
                                    val exif = androidx.exifinterface.media.ExifInterface(link)
                                    exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_DATETIME, message.sentAtUtc)
                                    exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MODEL, message.id.toString())
                                    exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MAKE, channelId.toString() + muid)
                                    exif.saveAttributes()
                                    imageFiles[i].transitionName = message.id.toString()
                                    imageFiles.sortWith(Comparator { one, other -> other.transitionName!!.compareTo(one.transitionName!!) })
                                    imageFiles.reverse()

                                    for (x in 0 until imageFiles.size) {
                                        if (imageFiles[x].absolutepath.equals(link)) {
                                            pos = x
                                        }
                                    }
                                }
                                if (isChatActivity()) {
                                    ChatActivity.PagerPosition.currentViewPagerPosition = pos
                                    imageIntent.putExtra("currentPos", ChatActivity.PagerPosition.currentViewPagerPosition)
                                } else {
                                    FuguInnerChatActivity.currentViewPagerposition = pos
                                    imageIntent.putExtra("currentPos", FuguInnerChatActivity.currentViewPagerposition)
                                }
                                isImageFound = true
                                break
                            } else {
                                ChatActivity.PagerPosition.currentViewPagerPosition = 0
                                FuguInnerChatActivity.currentViewPagerposition = 0
                            }
                        }


                        if (!isImageFound) {
                            try {
                                val file = File(link)
                                if (file.exists()) {
                                    val exif = androidx.exifinterface.media.ExifInterface(link)
                                    exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_DATETIME, message.sentAtUtc)
                                    exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MODEL, message.id.toString())
                                    exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MAKE, channelId.toString() + muid)
                                    exif.saveAttributes()
                                    messageMap[message.muid] = message
                                    getFromSdcard()
                                    for (i in 0 until imageFiles.size) {
                                        if (imageFiles[i].absolutepath.equals(link)) {
                                            if (isChatActivity()) {
                                                ChatActivity.PagerPosition.currentViewPagerPosition = i
                                                imageIntent.putExtra("currentPos", ChatActivity.PagerPosition.currentViewPagerPosition)
                                            } else {
                                                FuguInnerChatActivity.currentViewPagerposition = i
                                                imageIntent.putExtra("currentPos", FuguInnerChatActivity.currentViewPagerposition)
                                            }
                                            break
                                        } else {
                                            ChatActivity.PagerPosition.currentViewPagerPosition = 0
                                            FuguInnerChatActivity.currentViewPagerposition = 0
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        if (mContext is ChatActivity) {
                            options = ActivityOptionsCompat.makeSceneTransitionAnimation(mContext as ChatActivity,
                                    selfImageMessageViewHolder.ivImageMessage as View, imageFiles[ChatActivity.PagerPosition.currentViewPagerPosition].transitionName)
                        } else {
                            options = ActivityOptionsCompat.makeSceneTransitionAnimation(mContext as FuguInnerChatActivity,
                                    selfImageMessageViewHolder.ivImageMessage as View, imageFiles[FuguInnerChatActivity.currentViewPagerposition].transitionName)
                        }
                        val presentViewHolderList = ArrayList<String>()
                        val first: Int
                        val last: Int
                        if (recyclerView.layoutManager is CustomLinearLayoutManager) {
                            first = (recyclerView.layoutManager as CustomLinearLayoutManager).findFirstVisibleItemPosition()
                            last = (recyclerView.layoutManager as CustomLinearLayoutManager).findLastVisibleItemPosition()
                        } else {
                            first = (recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).findFirstVisibleItemPosition()
                            last = (recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).findLastVisibleItemPosition()
                        }
                        for (item in first..last) {
                            presentViewHolderList.add(messageList[item].id.toString())
                        }
                        imageIntent.putExtra("isChatActivity", isChatActivity())
                        imageIntent.putExtra("imageList", imageFiles)
                        imageIntent.putExtra("presentViewHolderList", presentViewHolderList)
                        imageIntent.putExtra("channelName", label)
                        imageIntent.putExtra("MESSAGE", message)
                        imageIntent.putExtra("chatType", chatType)
                        imageIntent.putExtra(CHANNEL_ID, channelId)
                        imageIntent.putExtra(APP_SECRET_KEY, workspaceInfo?.get(currentPos)?.fuguSecretKey)
                        mContext.startActivity(imageIntent, options.toBundle())
                    } else if (extension.equals("gif")) {
                        val imageIntent = Intent(mContext, ImageDisplayActivity::class.java)
                        var extension = message.image_url!!.split(".")[message.image_url.split(".").size - 1]
                        if (extension.toLowerCase().equals("png")) {
                            extension = "jpg"
                        }
                        val fileName = message.fileName + "_" + message.muid + "." + extension
                        val filePathNormal = File(getNormalDirectory(extension) + "/" + fileName)
                        val filePathPrivate = File(getPrivateDirectory(extension) + "/" + fileName)
                        var link = ""
                        if (filePathNormal.exists()) {
                            link = filePathNormal.absolutePath
                        } else {
                            link = filePathPrivate.absolutePath
                        }
                        if (filePathNormal.exists() || filePathPrivate.exists() || extension.equals("gif")) {
                            if (extension == "gif") {
                                link = message.image_url
                            }

                            val image = Image(link, link, message.muid, message.sentAtUtc, label)
                            imageIntent.putExtra("image", image)
                            imageIntent.putExtra("MESSAGE", message)
                            imageIntent.putExtra("BUSINESS_NAME", label)
                            imageIntent.putExtra("chatType", chatType)
                            mContext.startActivity(imageIntent)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        setImageUploadStatus(selfImageMessageViewHolder.circleProgress, selfImageMessageViewHolder.llForward, selfImageMessageViewHolder.btnRetry, message)
        selfImageMessageViewHolder.btnRetry.setOnClickListener {
            if (getIsNetworkConnected()) {
                selfImageMessageViewHolder.circleProgress.visibility = View.VISIBLE
                selfImageMessageViewHolder.btnRetry.visibility = View.GONE
                selfImageMessageViewHolder.llForward.visibility = View.GONE

                val fileDetails = FuguFileDetails()
                fileDetails.filePath = message.image_url
                fileDetails.fileExtension = message.fileExtension
                fileDetails.fileSize = message.fileSize
                fileDetails.fileName = message.fileName

                try {
                    mOnRetry?.onRetry(message.message, message.image_url, message.messageIndex,
                            FuguAppConstant.IMAGE_MESSAGE, fileDetails, message.muid,
                            message.thumbnailUrl, message.image_url, message.url, message.image_url_100x100, message.imageHeight, message.imageWidth)
                } catch (e: Exception) {
                    e.printStackTrace()
                    if (chatActivity != null) {
                        showErrorMessage(chatActivity!!, "Something went wrong please try again!")
                        chatActivity?.cancelMessage(selfImageMessageViewHolder.position)
                    } else {
                        showErrorMessage(fuguInnerChatActivity, "Something went wrong please try again!")
                        fuguInnerChatActivity.cancelMessage(selfImageMessageViewHolder.position)
                    }
                }
            }
        }

        if (message.isAnimate && !isAlreadyAnimated) {
            isAlreadyAnimated = true
            setAnimation(2000, Color.parseColor("#fff6d1"), Color.parseColor("#00FFFFFF"), selfImageMessageViewHolder.llRoot)
        } else {
            if (!message.editMode) {
                setAnimation(0, Color.parseColor("#00FFFFFF"), Color.parseColor("#00FFFFFF"), selfImageMessageViewHolder.llRoot)
            } else {
                selfImageMessageViewHolder.llRoot.setBackgroundColor(Color.parseColor("#fff6d1"))
            }
        }
        if (message.editMode) {
            selfImageMessageViewHolder.llRoot.setBackgroundColor(Color.parseColor("#fff6d1"))
        } else {
            selfImageMessageViewHolder.llRoot.setBackgroundColor(Color.parseColor("#00FFFFFF"))
        }
        selfImageMessageViewHolder.llDownloadImage.setOnClickListener {
            if (checkAndObtainStoragePermission()) {
                val extension = message.image_url!!.split(".")[message.image_url.split(".").size - 1]
                val downloadId = downloadFileFromUrl(extension, message, position, "Image")
                if (downloadId != 1) {
                    message.downloadId = downloadId
                }
                selfImageMessageViewHolder.pbDownloading.visibility = View.VISIBLE
                selfImageMessageViewHolder.llDownloadImage.visibility = View.GONE
            }
        }
        selfImageMessageViewHolder.llImageMessage.setPadding(0)
        selfImageMessageViewHolder.llImageMessage.clipToOutline = true
        setReplyData(selfImageMessageViewHolder.tvReplyTextList, selfImageMessageViewHolder.ivReplyImageList, selfImageMessageViewHolder.llReplyList, message)
    }

    private fun setImageUploadStatus(circleProgress: DonutProgress, llForward: LinearLayout, fuguBtnRetry: AppCompatButton, currentOrderItem2: Message) {
        when (currentOrderItem2.uploadStatus) {
            FuguAppConstant.UPLOAD_FAILED -> if (currentOrderItem2.messageStatus == FuguAppConstant.MESSAGE_READ
                    || currentOrderItem2.messageStatus == FuguAppConstant.MESSAGE_DELIVERED
                    || currentOrderItem2.messageStatus == FuguAppConstant.MESSAGE_SENT) {
                circleProgress.visibility = View.GONE
                fuguBtnRetry.visibility = View.GONE
                llForward.visibility = View.GONE
            } else {
                circleProgress.visibility = View.GONE
                fuguBtnRetry.visibility = View.VISIBLE
                llForward.visibility = View.GONE
            }
            FuguAppConstant.UPLOAD_IN_PROGRESS -> {
                circleProgress.visibility = View.VISIBLE
                fuguBtnRetry.visibility = View.GONE
                llForward.visibility = View.GONE
            }
            FuguAppConstant.UPLOAD_COMPLETED -> {
                circleProgress.visibility = View.GONE
                fuguBtnRetry.visibility = View.GONE
                llForward.visibility = View.GONE
            }
            else -> {
                circleProgress.visibility = View.GONE
                fuguBtnRetry.visibility = View.GONE
                llForward.visibility = View.GONE
            }
        }
        if (currentOrderItem2.messageStatus == FuguAppConstant.MESSAGE_READ
                || currentOrderItem2.messageStatus == FuguAppConstant.MESSAGE_DELIVERED

                || currentOrderItem2.messageStatus == FuguAppConstant.MESSAGE_SENT) {
            circleProgress.visibility = View.GONE
            fuguBtnRetry.visibility = View.GONE
            llForward.visibility = View.GONE
        }
    }

    private fun setVideoUploadStatus(tvImageTime: AppCompatTextView, circleProgress: DonutProgress, llForward: LinearLayout, fuguBtnRetry: AppCompatButton, ivPlay: ImageView, currentOrderItem2: Message) {
        when (currentOrderItem2.uploadStatus) {
            FuguAppConstant.UPLOAD_FAILED -> if (currentOrderItem2.messageStatus == FuguAppConstant.MESSAGE_READ
                    || currentOrderItem2.messageStatus == FuguAppConstant.MESSAGE_DELIVERED
                    || currentOrderItem2.messageStatus == FuguAppConstant.MESSAGE_SENT) {
                circleProgress.visibility = View.GONE
                fuguBtnRetry.visibility = View.GONE
                llForward.visibility = View.GONE
                ivPlay.visibility = View.VISIBLE

            } else {
                fuguBtnRetry.visibility = View.VISIBLE
                circleProgress.visibility = View.GONE
                llForward.visibility = View.GONE
                ivPlay.visibility = View.GONE
            }
            FuguAppConstant.UPLOAD_IN_PROGRESS -> {
                circleProgress.visibility = View.VISIBLE
                fuguBtnRetry.visibility = View.GONE
                llForward.visibility = View.GONE
                ivPlay.visibility = View.GONE
            }
            FuguAppConstant.UPLOAD_COMPLETED -> {

                Thread {
                    kotlin.run {
                        CommonData.setFileLocalPath(currentOrderItem2.uuid, currentOrderItem2.filePath)
                        if (!TextUtils.isEmpty(currentOrderItem2.filePath)) {
                            CommonData.setCachedFilePath(currentOrderItem2.url, currentOrderItem2.uuid, currentOrderItem2.filePath)
                        }
                    }
                }.start()

                circleProgress.visibility = View.GONE
                fuguBtnRetry.visibility = View.GONE

                if (currentOrderItem2.messageStatus == FuguAppConstant.MESSAGE_READ
                        || currentOrderItem2.messageStatus == FuguAppConstant.MESSAGE_DELIVERED
                        || currentOrderItem2.messageStatus == FuguAppConstant.MESSAGE_SENT) {
                    ivPlay.visibility = View.VISIBLE
                    llForward.visibility = View.GONE
                } else {
                    ivPlay.visibility = View.GONE
                    llForward.visibility = View.GONE
                }
            }
            else -> {
                tvImageTime.append(10.toString())
                circleProgress.visibility = View.GONE
                fuguBtnRetry.visibility = View.GONE

                if (currentOrderItem2.messageStatus == FuguAppConstant.MESSAGE_READ
                        || currentOrderItem2.messageStatus == FuguAppConstant.MESSAGE_DELIVERED
                        || currentOrderItem2.messageStatus == FuguAppConstant.MESSAGE_SENT) {
                    ivPlay.visibility = View.VISIBLE
                    llForward.visibility = View.GONE
                } else {
                    ivPlay.visibility = View.GONE
                    llForward.visibility = View.GONE
                }
            }
        }
        if (currentOrderItem2.messageStatus == FuguAppConstant.MESSAGE_READ
                || currentOrderItem2.messageStatus == FuguAppConstant.MESSAGE_DELIVERED

                || currentOrderItem2.messageStatus == FuguAppConstant.MESSAGE_SENT) {
            circleProgress.visibility = View.GONE
            fuguBtnRetry.visibility = View.GONE
            llForward.visibility = View.GONE
            ivPlay.visibility = View.VISIBLE
        }
    }

    private fun setForwardClick(message: Message) {
        if (isChatActivity()) {
            val mIntent = Intent(chatActivity, ForwardActivity::class.java)
            mIntent.putExtra("MESSAGE", message)
            mIntent.putExtra("BUSINESS_NAME", label)
            mIntent.putExtra("chatType", chatType)
            chatActivity?.startActivity(mIntent)
        } else {
            val mIntent = Intent(fuguInnerChatActivity, ForwardActivity::class.java)
            mIntent.putExtra("MESSAGE", message)
            mIntent.putExtra("BUSINESS_NAME", label)
            mIntent.putExtra("chatType", chatType)
            fuguInnerChatActivity.startActivity(mIntent)
        }
    }

    private fun setImageMessageOther(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, message: Message) {
        val otherImageMessageViewHolder: OtherImageMessageViewHolder = holder as OtherImageMessageViewHolder
        if (chatActivity != null && ((chatType != ChatType.BOT) || (chatType == ChatType.BOT && userType == UserType.SELF_BOT))) {
            otherImageMessageViewHolder.ivImageMessage.setOnTouchListener(ExtendedTouchListener(otherImageMessageViewHolder.itemView, otherImageMessageViewHolder.llMessage,
                    otherImageMessageViewHolder.llRoot,
                    message,
                    chatActivity,
                    recyclerView,
                    otherImageMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
            otherImageMessageViewHolder.tvImagewithMessage.setOnTouchListener(ExtendedTouchListener(otherImageMessageViewHolder.itemView, otherImageMessageViewHolder.llMessage,
                    otherImageMessageViewHolder.llRoot,
                    message,
                    chatActivity,
                    recyclerView,
                    otherImageMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
            otherImageMessageViewHolder.tvImageTime.setOnTouchListener(ExtendedTouchListener(otherImageMessageViewHolder.itemView, otherImageMessageViewHolder.llMessage,
                    otherImageMessageViewHolder.llRoot,
                    message,
                    chatActivity,
                    recyclerView,
                    otherImageMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
            otherImageMessageViewHolder.llDownloadImage.setOnTouchListener(ExtendedTouchListener(otherImageMessageViewHolder.itemView, otherImageMessageViewHolder.llMessage,
                    otherImageMessageViewHolder.llRoot,
                    message,
                    chatActivity,
                    recyclerView,
                    otherImageMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
        }
        setOtherMessageBackground(otherImageMessageViewHolder.tvReplies, otherImageMessageViewHolder.llRoot, otherImageMessageViewHolder.llImageMessage, position, otherImageMessageViewHolder.tvUserName, message.userId)
//        setBackgroundColor(otherImageMessageViewHolder.llImageMessage.background as NinePatchDrawable, colorConfig.fuguBgMessageFrom)
        setTime(otherImageMessageViewHolder.tvImageTime, message.sentAtUtc)
        otherImageMessageViewHolder.llDownloadImage.visibility = View.GONE
        setImage(otherImageMessageViewHolder.pbDownloading, otherImageMessageViewHolder.llDownloadImage, otherImageMessageViewHolder.ivImageMessage, message, position)
        setStarredMessage(otherImageMessageViewHolder.ivStar, message.isStarred)
        var extension = message.image_url!!.split(".")[message.image_url.split(".").size - 1]

        if (extension.toLowerCase() == "png") {
            extension = "jpg"
        }
        val fileName = message.fileName + "_" + message.muid + "." + extension
        val filePathNormal = File(getNormalDirectory(extension) + "/" + fileName)
        val filePathPrivate = File(getPrivateDirectory(extension) + "/" + fileName)


        if (workspaceInfo?.get(currentPos)?.autoDownloadLevel!! == FuguAppConstant.AutoDownloadLevel.BOTH.toString()
                || checkConnection(otherImageMessageViewHolder.itemView.context) == workspaceInfo?.get(currentPos)?.autoDownloadLevel) {
            if (!filePathNormal.exists() && !filePathPrivate.exists() && message.downloadStatus != FuguAppConstant.DownloadStatus.DOWNLOAD_IN_PROGRESS.downloadStatus
                    && message.downloadStatus != FuguAppConstant.DownloadStatus.DOWNLOAD_COMPLETED.downloadStatus) {
                if (extension.toLowerCase() != "gif" && checkAndObtainStoragePermission()) {
                    val downloadId = downloadFileFromUrl(extension, message, position, "Image")
                    message.downloadId = downloadId
                    otherImageMessageViewHolder.pbDownloading.visibility = View.VISIBLE
                    otherImageMessageViewHolder.llDownloadImage.visibility = View.GONE
                }

            }
        }
        if (!TextUtils.isEmpty(message.fileSize)) {
            otherImageMessageViewHolder.tvImageSize.visibility = View.VISIBLE
            otherImageMessageViewHolder.tvImageSize.text = message.fileSize
        } else {
            otherImageMessageViewHolder.tvImageSize.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(message.message)) {
            otherImageMessageViewHolder.tvImagewithMessage.visibility = View.VISIBLE

            val messageString = if (message.messageState == 4) {
                if (!TextUtils.isEmpty(message.alteredMessage)) {
                    message.alteredMessage + " <font color='grey'><small> (edited)</small></font>"
                } else {
                    message.message + " <font color='grey'><small> (edited)</small></font>"
                }
            } else {
                if (!TextUtils.isEmpty(message.alteredMessage)) {
                    message.alteredMessage
                } else {
                    message.message
                }
            }
            manipulateAndSetText(otherImageMessageViewHolder.tvImagewithMessage, messageString, message.messageState)
        } else {
            otherImageMessageViewHolder.tvImagewithMessage.visibility = View.GONE

        }
        if (message.threadMessage) {
            otherImageMessageViewHolder.llReplies.visibility = View.VISIBLE
            setReplies(otherImageMessageViewHolder.tvReplies, message.threadMessageCount)
        } else {
            otherImageMessageViewHolder.llReplies.visibility = View.GONE
        }
        setImageHeightAndWidth(otherImageMessageViewHolder.ivImageMessage, otherImageMessageViewHolder.rlImageMessage, otherImageMessageViewHolder.llImageMessage, message, true)
        setItemLongClick(otherImageMessageViewHolder.itemView, message, false, otherImageMessageViewHolder.tvImageTime)
        setItemLongClick(otherImageMessageViewHolder.ivImageMessage, message, false, otherImageMessageViewHolder.tvImageTime)
        setItemLongClick(otherImageMessageViewHolder.tvImagewithMessage, message, false, otherImageMessageViewHolder.tvImageTime)
        setEmojisCardView(otherImageMessageViewHolder.llEmojis, otherImageMessageViewHolder.emojiLayoutList, otherImageMessageViewHolder.emojiTextList, otherImageMessageViewHolder.emojiCountList, message, position)
        otherImageMessageViewHolder.llForward.setOnClickListener {
            setForwardClick(message)
        }
        setReplyClick(otherImageMessageViewHolder.tvReplies, message, false, otherImageMessageViewHolder.tvImagewithMessage)
        otherImageMessageViewHolder.tvUserName.text = message.fromName
        otherImageMessageViewHolder.ivImageMessage.setOnClickListener {
            if (isChatActivity()) {
                if ((mContext as ChatActivity).getState() != MotionEvent.ACTION_MOVE) {
                    try {
                        otherImageMessageViewHolder.ivImageMessage.transitionName = message.id.toString()
                        val imageIntent = Intent(mContext, ImageDisplayViewpagerActivity::class.java)
                        var extension = message.image_url!!.split(".")[message.image_url.split(".").size - 1]
                        if (extension.toLowerCase().equals("png")) {
                            extension = "jpg"
                        }
                        val fileName = message.fileName + "_" + message.muid + "." + extension
                        val filePathNormal = File(getNormalDirectory(extension) + "/" + fileName)
                        val filePathPrivate = File(getPrivateDirectory(extension) + "/" + fileName)
                        var link = if (filePathNormal.exists()) {
                            filePathNormal.absolutePath
                        } else {
                            filePathPrivate.absolutePath
                        }
                        var isImageFound = false
                        if (filePathNormal.exists() || filePathPrivate.exists()) {
                            if (extension == "gif") {
                                link = message.image_url
                            }
                            var options: ActivityOptionsCompat? = null
                            for (i in 0 until imageFiles.size) {
                                var pos = i
                                if (imageFiles[i].absolutepath == link) {

                                    if (imageFiles[i].transitionName === null || imageFiles[i].transitionName.equals("0") || !imageFiles[i].transitionName.equals(message.id.toString())) {
                                        val exif = androidx.exifinterface.media.ExifInterface(link)
                                        exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_DATETIME, message.sentAtUtc)
                                        exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MODEL, message.id.toString())
                                        exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MAKE, channelId.toString() + muid)
                                        exif.saveAttributes()
                                        imageFiles[i].transitionName = message.id.toString()
                                        imageFiles.sortWith(Comparator { one, other -> other.transitionName!!.compareTo(one.transitionName!!) })
                                        imageFiles.reverse()
                                        for (x in 0 until imageFiles.size) {
                                            if (imageFiles[x].absolutepath == link) {
                                                pos = x
                                            }
                                        }
                                    }

                                    if (isChatActivity()) {
                                        ChatActivity.PagerPosition.currentViewPagerPosition = pos
                                        imageIntent.putExtra("currentPos", ChatActivity.PagerPosition.currentViewPagerPosition)
                                    } else {
                                        FuguInnerChatActivity.currentViewPagerposition = pos
                                        imageIntent.putExtra("currentPos", FuguInnerChatActivity.currentViewPagerposition)
                                    }
                                    isImageFound = true
                                    break
                                } else {
                                    ChatActivity.PagerPosition.currentViewPagerPosition = 0
                                    FuguInnerChatActivity.currentViewPagerposition = 0
                                }
                            }
                            if (!isImageFound) {
                                try {
                                    val file = File(link)
                                    if (file.exists()) {
                                        val exif = androidx.exifinterface.media.ExifInterface(link)
                                        exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_DATETIME, message.sentAtUtc)
                                        exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MODEL, message.id.toString())
                                        exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MAKE, channelId.toString() + muid)
                                        exif.saveAttributes()
                                        messageMap[message.muid] = message
                                        getFromSdcard()
                                        for (i in 0 until imageFiles.size) {
                                            if (imageFiles[i].absolutepath == link) {
                                                if (isChatActivity()) {
                                                    ChatActivity.PagerPosition.currentViewPagerPosition = i
                                                    imageIntent.putExtra("currentPos", ChatActivity.PagerPosition.currentViewPagerPosition)
                                                } else {
                                                    FuguInnerChatActivity.currentViewPagerposition = i
                                                    imageIntent.putExtra("currentPos", FuguInnerChatActivity.currentViewPagerposition)
                                                }
                                                isImageFound = true
                                                break
                                            } else {
                                                ChatActivity.PagerPosition.currentViewPagerPosition = 0
                                                FuguInnerChatActivity.currentViewPagerposition = 0
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            Log.e("CurrentPos", ChatActivity.PagerPosition.currentViewPagerPosition.toString())
                            if (mContext is ChatActivity) {
                                options = ActivityOptionsCompat.makeSceneTransitionAnimation(mContext as ChatActivity,
                                        otherImageMessageViewHolder.ivImageMessage as View, imageFiles[ChatActivity.PagerPosition.currentViewPagerPosition].transitionName)
                            } else {
                                options = ActivityOptionsCompat.makeSceneTransitionAnimation(mContext as FuguInnerChatActivity,
                                        otherImageMessageViewHolder.ivImageMessage as View, imageFiles[FuguInnerChatActivity.currentViewPagerposition].transitionName)
                            }
                            val presentViewHolderList = ArrayList<String>()
                            var first = 0
                            var last = 0
                            if (recyclerView.layoutManager is CustomLinearLayoutManager) {
                                first = (recyclerView.layoutManager as CustomLinearLayoutManager).findFirstVisibleItemPosition()
                                last = (recyclerView.layoutManager as CustomLinearLayoutManager).findLastVisibleItemPosition()
                            } else {
                                first = (recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).findFirstVisibleItemPosition()
                                last = (recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).findLastVisibleItemPosition()
                            }
                            var currentAdapterPosition = 0
                            for (item in first..last) {
                                presentViewHolderList.add(messageList[item].id.toString())
                            }
                            imageIntent.putExtra("isChatActivity", isChatActivity())
                            imageIntent.putExtra("imageList", imageFiles)
                            imageIntent.putExtra("presentViewHolderList", presentViewHolderList)
                            imageIntent.putExtra("channelName", label)
                            imageIntent.putExtra("MESSAGE", message)
                            imageIntent.putExtra("chatType", chatType)
                            imageIntent.putExtra(CHANNEL_ID, channelId)
                            imageIntent.putExtra(APP_SECRET_KEY, workspaceInfo?.get(currentPos)?.fuguSecretKey)
                            mContext.startActivity(imageIntent, options.toBundle())
                        } else if (extension.equals("gif")) {
                            val imageIntent = Intent(mContext, ImageDisplayActivity::class.java)
                            var extension = message.image_url!!.split(".")[message.image_url.split(".").size - 1]
                            if (extension.toLowerCase() == "png") {
                                extension = "jpg"
                            }
                            val fileName = message.fileName + "_" + message.muid + "." + extension
                            val filePathNormal = File(getNormalDirectory(extension) + "/" + fileName)
                            val filePathPriavte = File(getPrivateDirectory(extension) + "/" + fileName)
                            var link = ""
                            if (filePathNormal.exists()) {
                                link = filePathNormal.absolutePath
                            } else {
                                link = filePathPrivate.absolutePath
                            }
                            if (filePathNormal.exists() || filePathPriavte.exists() || extension.equals("gif")) {
                                if (extension == "gif") {
                                    link = message.image_url
                                }

                                val image = Image(link, link, message.muid, message.sentAtUtc, label)
                                imageIntent.putExtra("image", image)
                                imageIntent.putExtra("MESSAGE", message)
                                imageIntent.putExtra("BUSINESS_NAME", label)
                                imageIntent.putExtra("chatType", chatType)
                                mContext.startActivity(imageIntent)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                try {
                    otherImageMessageViewHolder.ivImageMessage.transitionName = message.id.toString()
                    val imageIntent = Intent(mContext, ImageDisplayViewpagerActivity::class.java)
                    var extension = message.image_url!!.split(".")[message.image_url.split(".").size - 1]
                    if (extension.toLowerCase() == "png") {
                        extension = "jpg"
                    }
                    val fileName = message.fileName + "_" + message.muid + "." + extension
                    val filePathNormal = File(getNormalDirectory(extension) + "/" + fileName)
                    val filePathPrivate = File(getPrivateDirectory(extension) + "/" + fileName)
                    var link = if (filePathNormal.exists()) {
                        filePathNormal.absolutePath
                    } else {
                        filePathPrivate.absolutePath
                    }
                    var isImageFound = false
                    if (filePathNormal.exists() || filePathPrivate.exists()) {
                        if (extension == "gif") {
                            link = message.image_url
                        }
                        var options: ActivityOptionsCompat? = null
                        for (i in 0 until imageFiles.size) {
                            var pos = i
                            if (imageFiles[i].absolutepath.equals(link)) {

                                if (imageFiles[i].transitionName === null || imageFiles[i].transitionName.equals("0") || !imageFiles[i].transitionName.equals(message.id.toString())) {
                                    val exif = androidx.exifinterface.media.ExifInterface(link)
                                    exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_DATETIME, message.sentAtUtc)
                                    exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MODEL, message.id.toString())
                                    exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MAKE, channelId.toString() + muid)
                                    exif.saveAttributes()
                                    imageFiles[i].transitionName = message.id.toString()
                                    imageFiles.sortWith(Comparator { one, other -> other.transitionName!!.compareTo(one.transitionName!!) })
                                    imageFiles.reverse()
                                    for (x in 0 until imageFiles.size) {
                                        if (imageFiles[x].absolutepath.equals(link)) {
                                            pos = x
                                        }
                                    }
                                }

                                if (isChatActivity()) {
                                    ChatActivity.PagerPosition.currentViewPagerPosition = pos
                                    imageIntent.putExtra("currentPos", ChatActivity.PagerPosition.currentViewPagerPosition)
                                } else {
                                    FuguInnerChatActivity.currentViewPagerposition = pos
                                    imageIntent.putExtra("currentPos", FuguInnerChatActivity.currentViewPagerposition)
                                }
                                isImageFound = true
                                break
                            } else {
                                ChatActivity.PagerPosition.currentViewPagerPosition = 0
                                FuguInnerChatActivity.currentViewPagerposition = 0
                            }
                        }
                        if (!isImageFound) {
                            try {
                                val file = File(link)
                                if (file.exists()) {
                                    val exif = androidx.exifinterface.media.ExifInterface(link)
                                    exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_DATETIME, message.sentAtUtc)
                                    exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MODEL, message.id.toString())
                                    exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MAKE, channelId.toString() + muid)
                                    exif.saveAttributes()
                                    messageMap[message.muid] = message
                                    getFromSdcard()
                                    for (i in 0 until imageFiles.size) {
                                        if (imageFiles[i].absolutepath.equals(link)) {
                                            if (isChatActivity()) {
                                                ChatActivity.PagerPosition.currentViewPagerPosition = i
                                                imageIntent.putExtra("currentPos", ChatActivity.PagerPosition.currentViewPagerPosition)
                                            } else {
                                                FuguInnerChatActivity.currentViewPagerposition = i
                                                imageIntent.putExtra("currentPos", FuguInnerChatActivity.currentViewPagerposition)
                                            }
                                            isImageFound = true
                                            break
                                        } else {
                                            ChatActivity.PagerPosition.currentViewPagerPosition = 0
                                            FuguInnerChatActivity.currentViewPagerposition = 0
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        Log.e("CurrentPos", ChatActivity.PagerPosition.currentViewPagerPosition.toString())
                        options = if (mContext is ChatActivity) {
                            ActivityOptionsCompat.makeSceneTransitionAnimation(mContext as ChatActivity,
                                    otherImageMessageViewHolder.ivImageMessage as View, imageFiles[ChatActivity.PagerPosition.currentViewPagerPosition].transitionName)
                        } else {
                            ActivityOptionsCompat.makeSceneTransitionAnimation(mContext as FuguInnerChatActivity,
                                    otherImageMessageViewHolder.ivImageMessage as View, imageFiles[FuguInnerChatActivity.currentViewPagerposition].transitionName)
                        }
                        val presentViewHolderList = ArrayList<String>()
                        var first: Int
                        var last: Int
                        if (recyclerView.layoutManager is CustomLinearLayoutManager) {
                            first = (recyclerView.layoutManager as CustomLinearLayoutManager).findFirstVisibleItemPosition()
                            last = (recyclerView.layoutManager as CustomLinearLayoutManager).findLastVisibleItemPosition()
                        } else {
                            first = (recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).findFirstVisibleItemPosition()
                            last = (recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).findLastVisibleItemPosition()
                        }
                        var currentAdapterPosition = 0
                        for (item in first..last) {
                            presentViewHolderList.add(messageList[item].id.toString())
                        }
                        imageIntent.putExtra("isChatActivity", isChatActivity())
                        imageIntent.putExtra("imageList", imageFiles)
                        imageIntent.putExtra("presentViewHolderList", presentViewHolderList)
                        imageIntent.putExtra("channelName", label)
                        imageIntent.putExtra("MESSAGE", message)
                        imageIntent.putExtra("chatType", chatType)
                        imageIntent.putExtra(CHANNEL_ID, channelId)
                        imageIntent.putExtra(APP_SECRET_KEY, workspaceInfo?.get(currentPos)?.fuguSecretKey)
                        mContext.startActivity(imageIntent, options.toBundle())
                    } else if (extension == "gif") {
                        val imageIntent = Intent(mContext, ImageDisplayActivity::class.java)
                        var extension = message.image_url!!.split(".")[message.image_url.split(".").size - 1]
                        if (extension.toLowerCase() == "png") {
                            extension = "jpg"
                        }
                        val fileName = message.fileName + "_" + message.muid + "." + extension
                        val filePathNormal = File(getNormalDirectory(extension) + "/" + fileName)
                        val filePathPrivate = File(getPrivateDirectory(extension) + "/" + fileName)
                        var link = ""
                        if (filePathNormal.exists()) {
                            link = filePathNormal.absolutePath
                        } else {
                            link = filePathPrivate.absolutePath
                        }
                        if (filePathNormal.exists() || filePathPrivate.exists() || extension.equals("gif")) {
                            if (extension == "gif") {
                                link = message.image_url
                            }

                            val image = Image(link, link, message.muid, message.sentAtUtc, label)
                            imageIntent.putExtra("image", image)
                            imageIntent.putExtra("MESSAGE", message)
                            imageIntent.putExtra("BUSINESS_NAME", label)
                            imageIntent.putExtra("chatType", chatType)
                            mContext.startActivity(imageIntent)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        otherImageMessageViewHolder.tvUserName.setOnClickListener {
            if (message.userType != 0) {
                openProfile("mention://" + message.userId.toString(), mContext)
            }
        }
        if (message.isAnimate && !isAlreadyAnimated) {
            isAlreadyAnimated = true
            setAnimation(2000, Color.parseColor("#fff6d1"), Color.parseColor("#00FFFFFF"), otherImageMessageViewHolder.llRoot)
        } else {
            setAnimation(0, Color.parseColor("#00FFFFFF"), Color.parseColor("#00FFFFFF"), otherImageMessageViewHolder.llRoot)
        }
        otherImageMessageViewHolder.llDownloadImage.setOnClickListener {
            if (checkAndObtainStoragePermission()) {
                val extension = message.image_url!!.split(".")[message.image_url.split(".").size - 1]
                val downloadId = downloadFileFromUrl(extension, message, position, "Image")
                message.downloadId = downloadId
                otherImageMessageViewHolder.pbDownloading.visibility = View.VISIBLE
                otherImageMessageViewHolder.llDownloadImage.visibility = View.GONE
            }
        }
        otherImageMessageViewHolder.llImageMessage.setPadding(0)
        otherImageMessageViewHolder.llImageMessage.clipToOutline = true
        setReplyData(otherImageMessageViewHolder.tvReplyTextList, otherImageMessageViewHolder.ivReplyImageList, otherImageMessageViewHolder.llReplyList, message)
    }

    private fun setFileMessageSelf(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, message: Message) {
        val fileMessageViewHolder: SelfFileMessageViewHolder = holder as SelfFileMessageViewHolder
        if (chatActivity != null && ((chatType != ChatType.BOT) || (chatType == ChatType.BOT && userType == UserType.SELF_BOT))) {
            fileMessageViewHolder.tvFileTime.setOnTouchListener(ExtendedTouchListener(fileMessageViewHolder.itemView, fileMessageViewHolder.llMainMessage,
                    fileMessageViewHolder.llRoot,
                    message,
                    chatActivity,
                    recyclerView,
                    fileMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
            fileMessageViewHolder.llFile.setOnTouchListener(ExtendedTouchListener(fileMessageViewHolder.itemView, fileMessageViewHolder.llMainMessage,
                    fileMessageViewHolder.llRoot,
                    message,
                    chatActivity,
                    recyclerView,
                    fileMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
            fileMessageViewHolder.tvFileSize.setOnTouchListener(ExtendedTouchListener(fileMessageViewHolder.itemView, fileMessageViewHolder.llMainMessage,
                    fileMessageViewHolder.llRoot,
                    message,
                    chatActivity,
                    recyclerView,
                    fileMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
            fileMessageViewHolder.tvFileExtension.setOnTouchListener(ExtendedTouchListener(fileMessageViewHolder.itemView, fileMessageViewHolder.llMainMessage,
                    fileMessageViewHolder.llRoot,
                    message,
                    chatActivity,
                    recyclerView,
                    fileMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
            fileMessageViewHolder.tvFileName.setOnTouchListener(ExtendedTouchListener(fileMessageViewHolder.itemView, fileMessageViewHolder.llMainMessage,
                    fileMessageViewHolder.llRoot,
                    message,
                    chatActivity,
                    recyclerView,
                    fileMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
        }
        setSelfMessageBackground(fileMessageViewHolder.tvReplies, fileMessageViewHolder.llRoot, fileMessageViewHolder.llMessage, position, message.userId)
//        setBackgroundColor(fileMessageViewHolder.llMessage.background as NinePatchDrawable, colorConfig.fuguBgMessageYou)
        setTime(fileMessageViewHolder.tvFileTime, message.sentAtUtc)
        setStarredMessage(fileMessageViewHolder.ivStar, message.isStarred)
        this.setMessageStatus(fileMessageViewHolder.ivMessageState, message.messageStatus, false)
        if (message.threadMessage) {
            fileMessageViewHolder.llReplies.visibility = View.VISIBLE
            setReplies(fileMessageViewHolder.tvReplies, message.threadMessageCount)
        } else {
            fileMessageViewHolder.llReplies.visibility = View.GONE
        }
        var image = IMAGE_MAP[message.fileExtension]
        if (image == null) {
            val mimeType = FuguMimeUtils.guessMimeTypeFromExtension(message.fileExtension.toLowerCase())
            if (mimeType != null)
                image = IMAGE_MAP[mimeType.split("/")[0]]
        }
        if (image != null) {
            fileMessageViewHolder.ivFileImage.setImageResource(image)
//            fileMessageViewHolder.ivFileImage.setColorFilter(Color.parseColor("#b3bec9"))
            fileMessageViewHolder.tvFileExt.visibility = View.GONE
        } else {
            fileMessageViewHolder.ivFileImage.setImageResource(R.drawable.file_model)
//            fileMessageViewHolder.ivFileImage.clearColorFilter()
            fileMessageViewHolder.tvFileExt.visibility = View.VISIBLE
        }
        fileMessageViewHolder.tvFileName.text = message.fileName

        if (message.fileExtension.length > 5) {
            fileMessageViewHolder.tvFileExtension.text = "File"
        } else {
            fileMessageViewHolder.tvFileExtension.text = message.fileExtension
            fileMessageViewHolder.tvFileExt.text = message.fileExtension.toUpperCase()
        }


        fileMessageViewHolder.tvFileSize.text = message.fileSize
        fileMessageViewHolder.circleProgress.visibility = View.VISIBLE
        this.setItemLongClick(fileMessageViewHolder.itemView, message, true, fileMessageViewHolder.tvFileTime)
        this.setItemLongClick(fileMessageViewHolder.llFile, message, true, fileMessageViewHolder.tvFileTime)
        setEmojisCardView(fileMessageViewHolder.llEmojis, fileMessageViewHolder.emojiLayoutList, fileMessageViewHolder.emojiTextList, fileMessageViewHolder.emojiCountList, message, position
        )
        fileMessageViewHolder.llForward.setOnClickListener {
            setForwardClick(message)
        }
        setReplyClick(fileMessageViewHolder.tvReplies, message, false, null)
        setFileUploadStatus(fileMessageViewHolder.ivFilePlay, fileMessageViewHolder.circleProgress, fileMessageViewHolder.ivFileDownload, fileMessageViewHolder.ivFileUpload, fileMessageViewHolder.llForward, message, position)
        setDownloadClick(fileMessageViewHolder.ivFileDownload, fileMessageViewHolder.circleProgress, message, position)
        setUploadClick(fileMessageViewHolder.ivFileUpload, fileMessageViewHolder.circleProgress, message)
        fileMessageViewHolder.ivFilePlay.setImageResource(R.drawable.music_player)
        if (FuguAppConstant.SUPPORTED_AUDIO_FORMATS.contains(message.fileExtension.toLowerCase())) {
            if (!message.isAudioPlaying) {
                fileMessageViewHolder.ivFilePlay.setImageResource(R.drawable.music_player)
            } else {
                fileMessageViewHolder.ivFilePlay.setImageResource(R.drawable.song_pause)
            }
        } else {
            setFileCLickListener(fileMessageViewHolder.llFile, message)
        }
        if (message.isAnimate && !isAlreadyAnimated) {
            isAlreadyAnimated = true
            setAnimation(2000, Color.parseColor("#fff6d1"), Color.parseColor("#00FFFFFF"), fileMessageViewHolder.llRoot)
        } else {
            setAnimation(0, Color.parseColor("#00FFFFFF"), Color.parseColor("#00FFFFFF"), fileMessageViewHolder.llRoot)
        }
        setReplyData(fileMessageViewHolder.tvReplyTextList, fileMessageViewHolder.ivReplyImageList, fileMessageViewHolder.llReplyList, message)
    }

    private fun setFileCLickListener(llFile: LinearLayoutCompat, message: Message) {
        llFile.setOnClickListener {
            Handler().postDelayed({
                if (isChatActivity()) {
                    if ((mContext as ChatActivity).getState() != MotionEvent.ACTION_MOVE) {
                        try {
                            val myFile: File
                            if (!TextUtils.isEmpty(message.sentFilePath)) {
                                myFile = File(message.sentFilePath)
                                if (!TextUtils.isEmpty(myFile.toString())) {
                                    FileOpen.openFile(mContext, myFile)
                                }
                            } else if (!TextUtils.isEmpty(message.filePath)) {
                                myFile = File(message.filePath)
                                if (!TextUtils.isEmpty(myFile.toString())) {
                                    FileOpen.openFile(mContext, myFile)
                                }
                            } else if (!TextUtils.isEmpty(message.image_url)) {
                                myFile = File(message.image_url)
                                if (!TextUtils.isEmpty(myFile.toString())) {
                                    FileOpen.openFile(mContext, myFile)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    try {
                        val myFile: File
                        if (!TextUtils.isEmpty(message.sentFilePath)) {
                            myFile = File(message.sentFilePath)
                            if (!TextUtils.isEmpty(myFile.toString())) {
                                FileOpen.openFile(mContext, myFile)
                            }
                        } else if (!TextUtils.isEmpty(message.filePath)) {
                            myFile = File(message.filePath)
                            if (!TextUtils.isEmpty(myFile.toString())) {
                                FileOpen.openFile(mContext, myFile)
                            }
                        } else if (!TextUtils.isEmpty(message.image_url)) {
                            myFile = File(message.image_url)
                            if (!TextUtils.isEmpty(myFile.toString())) {
                                FileOpen.openFile(mContext, myFile)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }, 0)
        }
    }

    private fun setUploadClick(ivFileUpload: AppCompatImageView, circleProgress: DonutProgress, message: Message) {
        ivFileUpload.setOnClickListener {
            if (getIsNetworkConnected()) {
                if (mOnRetry != null) {
                    val fileDetails = FuguFileDetails()
                    if (!TextUtils.isEmpty(message.unsentFilePath)) {
                        fileDetails.filePath = message.unsentFilePath
                    } else {
                        fileDetails.filePath = message.image_url
                    }
                    fileDetails.fileExtension = message.fileExtension
                    fileDetails.fileSize = message.fileSize
                    fileDetails.fileName = message.fileName
                    circleProgress.visibility = View.VISIBLE
                    ivFileUpload.visibility = View.GONE
                    message.messageStatus = FuguAppConstant.MESSAGE_UNSENT
                    try {
                        mOnRetry?.onRetry(message.message, message.image_url, message.messageIndex,
                                FuguAppConstant.FILE_MESSAGE, fileDetails, message.uuid,
                                message.thumbnailUrl, message.image_url, message.url, message.image_url_100x100, message.imageHeight, message.imageWidth)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        if (chatActivity != null) {
                            showErrorMessage(chatActivity!!, "Something went wrong please try again!")
                        } else {
                            showErrorMessage(fuguInnerChatActivity, "Something went wrong please try again!")
                        }
                    }
                }
            }
        }
    }

    private fun setDownloadClick(ivFileDownload: AppCompatImageView, circleProgress: DonutProgress, message: Message, position: Int) {
        ivFileDownload.setOnClickListener {
            if (getIsNetworkConnected() && checkAndObtainStoragePermission()) {
                val fullPath = Environment.getExternalStorageDirectory().absolutePath + "/Download"
                val downloadId = downloadFileFromUrl(fullPath, message, position, "File")
                if (downloadId != 1) {
                    message.downloadId = downloadId
                }
                circleProgress.visibility = View.VISIBLE
                ivFileDownload.visibility = View.GONE

            }
        }
    }

    private fun setFileUploadStatus(ivFilePlay: AppCompatImageView, circleProgress: DonutProgress, ivFileDownload: AppCompatImageView, ivFileUpload: AppCompatImageView, llForward: LinearLayout, currentOrderItem2: Message, position: Int) {
        if (currentOrderItem2.messageType == FuguAppConstant.FILE_MESSAGE) {
            when (currentOrderItem2.uploadStatus) {
                FuguAppConstant.UPLOAD_FAILED -> {
                    currentOrderItem2.currentprogress = 0
                    if ((currentOrderItem2.messageStatus == FuguAppConstant.MESSAGE_SENT
                                    || currentOrderItem2.messageStatus == FuguAppConstant.MESSAGE_DELIVERED
                                    || currentOrderItem2.messageStatus == FuguAppConstant.MESSAGE_READ) && !TextUtils.isEmpty(CommonData.getFileLocalPath(currentOrderItem2.uuid))) {
                        ivFileDownload.visibility = View.GONE
                        circleProgress.visibility = View.GONE
                        ivFileUpload.visibility = View.GONE
                        if (FuguAppConstant.SUPPORTED_AUDIO_FORMATS.contains(currentOrderItem2.fileExtension.toLowerCase())) {
                            ivFilePlay.visibility = View.VISIBLE
                        } else {
                            ivFilePlay.visibility = View.GONE
                        }
                        llForward.visibility = View.GONE
                    } else {
                        if (!TextUtils.isEmpty(CommonData.getFileLocalPath(currentOrderItem2.uuid))) {
                            currentOrderItem2.unsentFilePath = currentOrderItem2.filePath
                            ivFileDownload.visibility = View.GONE
                            circleProgress.visibility = View.GONE
                            ivFileUpload.visibility = View.VISIBLE
                            ivFilePlay.visibility = View.GONE
                            llForward.visibility = View.GONE
                        } else {
                            ivFileDownload.visibility = View.VISIBLE
                            circleProgress.visibility = View.GONE
                            ivFileUpload.visibility = View.GONE
                            ivFilePlay.visibility = View.GONE
                            llForward.visibility = View.GONE
                        }
                    }
                }
                FuguAppConstant.UPLOAD_IN_PROGRESS -> {
                    currentOrderItem2.unsentFilePath = currentOrderItem2.filePath
                    if (getIsNetworkConnected()) {
                        ivFileDownload.visibility = View.GONE
                        circleProgress.visibility = View.VISIBLE
                        ivFileUpload.visibility = View.GONE
                        ivFilePlay.visibility = View.GONE
                        llForward.visibility = View.GONE
                    } else {
                        currentOrderItem2.uploadStatus = FuguAppConstant.UPLOAD_FAILED
                        if (currentOrderItem2.messageStatus != FuguAppConstant.MESSAGE_READ
                                || currentOrderItem2.messageStatus != FuguAppConstant.MESSAGE_DELIVERED
                                || currentOrderItem2.messageStatus != FuguAppConstant.MESSAGE_SENT) {
                            ivFileDownload.visibility = View.GONE
                            circleProgress.visibility = View.GONE
                            ivFileUpload.visibility = View.VISIBLE
                            ivFilePlay.visibility = View.GONE
                            llForward.visibility = View.GONE
                        } else {
                            currentOrderItem2.uploadStatus = FuguAppConstant.UPLOAD_COMPLETED
                            ivFileDownload.visibility = View.GONE
                            circleProgress.visibility = View.GONE
                            ivFileUpload.visibility = View.GONE
                            ivFilePlay.visibility = View.GONE
                            llForward.visibility = View.VISIBLE
                        }
                    }
                }
                FuguAppConstant.UPLOAD_COMPLETED -> {
                    currentOrderItem2.currentprogress = 100
                    if (TextUtils.isEmpty(currentOrderItem2.sentFilePath)) {
                        currentOrderItem2.unsentFilePath = currentOrderItem2.filePath
                        currentOrderItem2.sentFilePath = currentOrderItem2.filePath
                        Thread {
                            kotlin.run {
                                if (!TextUtils.isEmpty(currentOrderItem2.filePath)) {
                                    CommonData.setCachedFilePath(currentOrderItem2.url, currentOrderItem2.uuid, currentOrderItem2.filePath)
                                }
                                CommonData.setFileLocalPath(currentOrderItem2.uuid, currentOrderItem2.filePath)
                            }
                        }.start()

                    }
                    ivFileDownload.visibility = View.GONE
                    circleProgress.visibility = View.GONE
                    ivFileUpload.visibility = View.GONE
                    //llForward.setVisibility(View.VISIBLE)
                    llForward.visibility = View.GONE

                    if (FuguAppConstant.SUPPORTED_AUDIO_FORMATS.contains(currentOrderItem2.fileExtension.toLowerCase())) {
                        ivFilePlay.visibility = View.VISIBLE
                        ivFilePlay.setOnClickListener {
                            if (mediaPlayer != null && isPlaying) {
                                val currentOrder = messageList.get(oldPos)
                                mediaPlayer?.stop()
                                mediaPlayer = null
                                currentOrder.isAudioPlaying = false
                                recyclerView.adapter?.notifyItemChanged(oldPos)
                                isPlaying = false
                                if (oldPos != position) {
                                    if (!TextUtils.isEmpty(currentOrderItem2.filePath)) {
                                        playAudio(currentOrderItem2.filePath, position)
                                    } else if (!TextUtils.isEmpty(currentOrderItem2.image_url)) {
                                        playAudio(currentOrderItem2.image_url, position)
                                    }
                                } else {
                                    currentOrderItem2.isAudioPlaying = true
                                    recyclerView.adapter?.notifyItemChanged(position)
                                }
                            } else {
                                if (!TextUtils.isEmpty(currentOrderItem2.filePath)) {
                                    playAudio(currentOrderItem2.filePath, position)
                                } else if (!TextUtils.isEmpty(currentOrderItem2.image_url)) {
                                    playAudio(currentOrderItem2.image_url, position)
                                }
                            }
                            if (FuguAppConstant.SUPPORTED_AUDIO_FORMATS.contains(currentOrderItem2.fileExtension.toLowerCase())) {
                                if (!currentOrderItem2.isAudioPlaying) {
                                    ivFilePlay.setImageResource(R.drawable.song_pause)
                                    currentOrderItem2.isAudioPlaying = true
                                } else {
                                    ivFilePlay.setImageResource(R.drawable.music_player)
                                    currentOrderItem2.isAudioPlaying = false
                                }
                            }
                        }
                    } else {
                        ivFilePlay.visibility = View.GONE
                    }
                }
                else -> if (!TextUtils.isEmpty(currentOrderItem2.sentFilePath)) {
                    ivFileDownload.visibility = View.GONE
                    circleProgress.visibility = View.GONE
                    ivFileUpload.visibility = View.GONE
                    ivFilePlay.visibility = View.GONE
                    //llForward.setVisibility(View.VISIBLE)
                    llForward.visibility = View.GONE
                } else if (!TextUtils.isEmpty(currentOrderItem2.unsentFilePath)) {
                    ivFileDownload.visibility = View.GONE
                    circleProgress.visibility = View.VISIBLE
                    ivFileUpload.visibility = View.GONE
                    ivFilePlay.visibility = View.GONE
                    llForward.visibility = View.GONE
                } else {
                    ivFileDownload.visibility = View.VISIBLE
                    circleProgress.visibility = View.GONE
                    ivFileUpload.visibility = View.GONE
                    ivFilePlay.visibility = View.GONE
                    llForward.visibility = View.GONE
                }
            }
        }
    }

    private fun setFileMessageOther(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, message: Message) {
        val otherFileMessageViewHolder: OtherFileMessageViewHolder = holder as OtherFileMessageViewHolder
        if (chatActivity != null && ((chatType != ChatType.BOT) || (chatType == ChatType.BOT && userType == UserType.SELF_BOT))) {
            otherFileMessageViewHolder.llFile.setOnTouchListener(ExtendedTouchListener(otherFileMessageViewHolder.itemView, otherFileMessageViewHolder.llMessage,
                    otherFileMessageViewHolder.llRoot,
                    message,
                    chatActivity,
                    recyclerView,
                    otherFileMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
            otherFileMessageViewHolder.tvFileTime.setOnTouchListener(ExtendedTouchListener(otherFileMessageViewHolder.itemView, otherFileMessageViewHolder.llMessage,
                    otherFileMessageViewHolder.llRoot,
                    message,
                    chatActivity,
                    recyclerView,
                    otherFileMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
            otherFileMessageViewHolder.tvFileSize.setOnTouchListener(ExtendedTouchListener(otherFileMessageViewHolder.itemView, otherFileMessageViewHolder.llMessage,
                    otherFileMessageViewHolder.llRoot,
                    message,
                    chatActivity,
                    recyclerView,
                    otherFileMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
            otherFileMessageViewHolder.tvFileExtension.setOnTouchListener(ExtendedTouchListener(otherFileMessageViewHolder.itemView, otherFileMessageViewHolder.llMessage,
                    otherFileMessageViewHolder.llRoot,
                    message,
                    chatActivity,
                    recyclerView,
                    otherFileMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
            otherFileMessageViewHolder.tvFileName.setOnTouchListener(ExtendedTouchListener(otherFileMessageViewHolder.itemView, otherFileMessageViewHolder.llMessage,
                    otherFileMessageViewHolder.llRoot,
                    message,
                    chatActivity,
                    recyclerView,
                    otherFileMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
        }
        setOtherMessageBackground(otherFileMessageViewHolder.tvReplies, otherFileMessageViewHolder.llRoot, otherFileMessageViewHolder.llMessage, position, otherFileMessageViewHolder.tvUserName, message.userId)
//        setBackgroundColor(fileMessageViewHolder.llMessage.background as NinePatchDrawable, colorConfig.fuguBgMessageFrom)
        setTime(otherFileMessageViewHolder.tvFileTime, message.sentAtUtc)
        setStarredMessage(otherFileMessageViewHolder.ivStar, message.isStarred)
        if (message.threadMessage) {
            otherFileMessageViewHolder.llReplies.visibility = View.VISIBLE
            setReplies(otherFileMessageViewHolder.tvReplies, message.threadMessageCount)
        } else {
            otherFileMessageViewHolder.llReplies.visibility = View.GONE
        }
        var image = IMAGE_MAP[message.fileExtension.toLowerCase()]
        if (image == null) {
            val mimeType = FuguMimeUtils.guessMimeTypeFromExtension(message.fileExtension.toLowerCase())
            if (mimeType != null)
                image = IMAGE_MAP[mimeType.split("/")[0]]
        }
        if (image != null) {
            otherFileMessageViewHolder.ivFileImage.setImageResource(image)
//            fileMessageViewHolder.ivFileImage.setColorFilter(Color.parseColor("#FFFFFF"))
            otherFileMessageViewHolder.tvFileExt.visibility = View.GONE
        } else {
            otherFileMessageViewHolder.ivFileImage.setImageResource(R.drawable.file_model)
//            fileMessageViewHolder.ivFileImage.clearColorFilter()
            otherFileMessageViewHolder.tvFileExt.visibility = View.VISIBLE
        }
        otherFileMessageViewHolder.tvFileName.text = message.fileName
        if (message.fileExtension.length > 5) {
            otherFileMessageViewHolder.tvFileExtension.text = "File"
        } else {
            otherFileMessageViewHolder.tvFileExtension.text = message.fileExtension
            otherFileMessageViewHolder.tvFileExt.text = message.fileExtension.toUpperCase()
        }
        otherFileMessageViewHolder.tvFileSize.text = message.fileSize
        if (SUPPORTED_AUDIO_FORMATS.contains(message.fileExtension)) {
            otherFileMessageViewHolder.ivFilePlay.visibility = View.VISIBLE
        } else {
            otherFileMessageViewHolder.ivFilePlay.visibility = View.GONE
            setFileCLickListener(otherFileMessageViewHolder.llFile, message)
        }
        setItemLongClick(otherFileMessageViewHolder.itemView, message, false, otherFileMessageViewHolder.tvFileTime)
        setItemLongClick(otherFileMessageViewHolder.llFile, message, false, otherFileMessageViewHolder.tvFileTime)
        setEmojisCardView(otherFileMessageViewHolder.llEmojis, otherFileMessageViewHolder.emojiLayoutList, otherFileMessageViewHolder.emojiTextList, otherFileMessageViewHolder.emojiCountList, message, position
        )
        otherFileMessageViewHolder.llForward.setOnClickListener {
            setForwardClick(message)
        }
        setReplyClick(otherFileMessageViewHolder.tvReplies, message, false, null)
        otherFileMessageViewHolder.tvUserName.text = message.fromName
        setDownloadClick(otherFileMessageViewHolder.ivFileDownload, otherFileMessageViewHolder.circleProgress, message, position)
        setFileDownloadStatus(otherFileMessageViewHolder.ivFileDownload, otherFileMessageViewHolder.circleProgress, otherFileMessageViewHolder.ivFilePlay, otherFileMessageViewHolder.llForward, message, position)
        otherFileMessageViewHolder.ivFilePlay.setImageResource(R.drawable.music_player)
        if (FuguAppConstant.SUPPORTED_AUDIO_FORMATS.contains(message.fileExtension.toLowerCase())) {
            if (!message.isAudioPlaying) {
                otherFileMessageViewHolder.ivFilePlay.setImageResource(R.drawable.music_player)
            } else {
                otherFileMessageViewHolder.ivFilePlay.setImageResource(R.drawable.song_pause)
            }
        }
        otherFileMessageViewHolder.tvUserName.setOnClickListener {
            if (message.userType != 0) {
                openProfile("mention://" + message.userId.toString(), mContext)
            }
        }

        if (message.isAnimate && !isAlreadyAnimated) {
            isAlreadyAnimated = true
            setAnimation(2000, Color.parseColor("#fff6d1"), Color.parseColor("#00FFFFFF"), otherFileMessageViewHolder.llRoot)
        } else {
            setAnimation(0, Color.parseColor("#00FFFFFF"), Color.parseColor("#00FFFFFF"), otherFileMessageViewHolder.llRoot)
        }
        setReplyData(otherFileMessageViewHolder.tvReplyTextList, otherFileMessageViewHolder.ivReplyImageList, otherFileMessageViewHolder.llReplyList, message)
    }

    private fun setThreadFileDownloadStatus(ivFileDownload: AppCompatImageView, circleProgress: DonutProgress, ivFilePlay: AppCompatImageView, message: Message, position: Int) {
        when (message.downloadStatus) {
            FuguAppConstant.DOWNLOAD_FAILED -> {
                ivFileDownload.visibility = View.VISIBLE
                circleProgress.visibility = View.GONE
                ivFilePlay.visibility = View.GONE

            }
            FuguAppConstant.DOWNLOAD_IN_PROGRESS -> {
                ivFileDownload.visibility = View.GONE
                circleProgress.visibility = View.VISIBLE
                ivFilePlay.visibility = View.GONE
            }
            FuguAppConstant.DOWNLOAD_PAUSED -> {
                ivFileDownload.visibility = View.VISIBLE
                circleProgress.visibility = View.GONE
                ivFilePlay.visibility = View.GONE

            }
            FuguAppConstant.DOWNLOAD_COMPLETED -> {
                ivFileDownload.visibility = View.GONE
                circleProgress.visibility = View.GONE
                ivFilePlay.visibility = View.VISIBLE
                if (FuguAppConstant.SUPPORTED_AUDIO_FORMATS.contains(message.fileExtension.toLowerCase())) {
                    ivFilePlay.setOnClickListener {
                        if (mediaPlayer != null && isPlaying) {
                            val currentOrder = messageList.get(oldPos)
                            mediaPlayer?.stop()
                            mediaPlayer = null
                            currentOrder.isAudioPlaying = false
                            recyclerView.adapter?.notifyItemChanged(oldPos)
                            isPlaying = false
                            if (oldPos != position) {
                                playAudio(message.filePath, position)
                            } else {
                                message.isAudioPlaying = true
                                recyclerView.adapter?.notifyItemChanged(position)
                            }
                        } else {
                            playAudio(message.filePath, position)
                        }
                        if (FuguAppConstant.SUPPORTED_AUDIO_FORMATS.contains(message.fileExtension.toLowerCase())) {
                            if (!message.isAudioPlaying) {
                                ivFilePlay.setImageResource(R.drawable.song_pause)
                                message.isAudioPlaying = true
                            } else {
                                ivFilePlay.setImageResource(R.drawable.music_player)
                                message.isAudioPlaying = false
                            }
                        }
                    }
                } else {
                    ivFilePlay.visibility = View.GONE
                }
            }
            else -> {
                ivFileDownload.visibility = View.VISIBLE
                circleProgress.visibility = View.GONE
                ivFilePlay.visibility = View.GONE
            }
        }
    }

    private fun setFileDownloadStatus(ivFileDownload: AppCompatImageView, circleProgress: DonutProgress, ivFilePlay: AppCompatImageView, llForward: LinearLayout, message: Message, position: Int) {
        when (message.downloadStatus) {
            FuguAppConstant.DOWNLOAD_FAILED -> {
                ivFileDownload.visibility = View.VISIBLE
                circleProgress.visibility = View.GONE
                ivFilePlay.visibility = View.GONE
                llForward.visibility = View.GONE
            }
            FuguAppConstant.DOWNLOAD_IN_PROGRESS -> {
                ivFileDownload.visibility = View.GONE
                circleProgress.visibility = View.VISIBLE
                ivFilePlay.visibility = View.GONE
                llForward.visibility = View.GONE
            }
            FuguAppConstant.DOWNLOAD_PAUSED -> {
                ivFileDownload.visibility = View.VISIBLE
                circleProgress.visibility = View.GONE
                ivFilePlay.visibility = View.GONE
                llForward.visibility = View.GONE
            }
            FuguAppConstant.DOWNLOAD_COMPLETED -> {
                ivFileDownload.visibility = View.GONE
                circleProgress.visibility = View.GONE
                ivFilePlay.visibility = View.VISIBLE
                //llForward.setVisibility(View.VISIBLE)
                llForward.visibility = View.GONE
                if (FuguAppConstant.SUPPORTED_AUDIO_FORMATS.contains(message.fileExtension.toLowerCase())) {
                    ivFilePlay.setOnClickListener {
                        if (mediaPlayer != null && isPlaying) {
                            val currentOrder = messageList.get(oldPos)
                            mediaPlayer?.stop()
                            mediaPlayer = null
                            currentOrder.isAudioPlaying = false
                            recyclerView.adapter?.notifyItemChanged(oldPos)
                            isPlaying = false
                            if (oldPos != position) {
                                playAudio(message.filePath, position)
                            } else {
                                message.isAudioPlaying = true
                                recyclerView.adapter?.notifyItemChanged(position)
                            }
                        } else {
                            playAudio(message.filePath, position)
                        }
                        if (FuguAppConstant.SUPPORTED_AUDIO_FORMATS.contains(message.fileExtension.toLowerCase())) {
                            if (!message.isAudioPlaying) {
                                ivFilePlay.setImageResource(R.drawable.song_pause)
                                message.isAudioPlaying = true
                            } else {
                                ivFilePlay.setImageResource(R.drawable.music_player)
                                message.isAudioPlaying = false
                            }
                        }
                    }
                } else {
                    ivFilePlay.visibility = View.GONE
                }
            }
            else -> {
                ivFileDownload.visibility = View.VISIBLE
                circleProgress.visibility = View.GONE
                ivFilePlay.visibility = View.GONE
            }
        }
    }


    private fun setThreadVideoDownloadStatus(ivFileDownload: LinearLayout, circleProgress: DonutProgress, ivFilePlay: ImageView, message: Message, ivCrossCancel: ImageView) {
        when (message.downloadStatus) {
            FuguAppConstant.DOWNLOAD_FAILED -> {
                ivFileDownload.visibility = View.VISIBLE
                circleProgress.visibility = View.GONE
                ivCrossCancel.visibility = View.GONE
                ivFilePlay.visibility = View.GONE
            }
            FuguAppConstant.DOWNLOAD_IN_PROGRESS -> {
                ivFileDownload.visibility = View.GONE
                circleProgress.visibility = View.VISIBLE
                ivCrossCancel.visibility = View.VISIBLE
                ivFilePlay.visibility = View.GONE
            }
            FuguAppConstant.DOWNLOAD_PAUSED -> {
                ivFileDownload.visibility = View.VISIBLE
                circleProgress.visibility = View.GONE
                ivCrossCancel.visibility = View.GONE
                ivFilePlay.visibility = View.GONE
            }
            FuguAppConstant.DOWNLOAD_COMPLETED -> {
                ivFileDownload.visibility = View.GONE
                circleProgress.visibility = View.GONE
                ivCrossCancel.visibility = View.GONE
                ivFilePlay.visibility = View.VISIBLE
            }
            else -> {
                ivFileDownload.visibility = View.VISIBLE
                circleProgress.visibility = View.GONE
                ivCrossCancel.visibility = View.GONE
                ivFilePlay.visibility = View.GONE
            }
        }
    }

    private fun setVideoDownloadStatus(ivFileDownload: LinearLayout, circleProgress: DonutProgress, ivFilePlay: ImageView, llForward: LinearLayout, message: Message, ivCrossCancel: ImageView) {
        when (message.downloadStatus) {
            FuguAppConstant.DOWNLOAD_FAILED -> {
                ivFileDownload.visibility = View.VISIBLE
                circleProgress.visibility = View.GONE
                ivCrossCancel.visibility = View.GONE
                ivFilePlay.visibility = View.GONE
                llForward.visibility = View.GONE
            }
            FuguAppConstant.DOWNLOAD_IN_PROGRESS -> {
                ivFileDownload.visibility = View.GONE
                circleProgress.visibility = View.VISIBLE
                ivCrossCancel.visibility = View.VISIBLE
                ivFilePlay.visibility = View.GONE
                llForward.visibility = View.GONE
            }
            FuguAppConstant.DOWNLOAD_PAUSED -> {
                ivFileDownload.visibility = View.VISIBLE
                circleProgress.visibility = View.GONE
                ivCrossCancel.visibility = View.GONE
                ivFilePlay.visibility = View.GONE
                llForward.visibility = View.GONE
            }
            FuguAppConstant.DOWNLOAD_COMPLETED -> {
                ivFileDownload.visibility = View.GONE
                circleProgress.visibility = View.GONE
                ivCrossCancel.visibility = View.GONE
                ivFilePlay.visibility = View.VISIBLE
                //llForward.setVisibility(View.VISIBLE)
                llForward.visibility = View.GONE
            }
            else -> {
                ivFileDownload.visibility = View.VISIBLE
                circleProgress.visibility = View.GONE
                ivCrossCancel.visibility = View.GONE
                ivFilePlay.visibility = View.GONE
            }
        }
    }


    private fun playAudio(url: String, position: Int) {
        Log.e("to be updated adapter", "to be updated")
        oldPos = position
        if (!isPlaying) {
            isPlaying = true
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()
                mediaPlayer?.reset()
            }
            try {
                mediaPlayer?.setDataSource(url)
                mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
                mediaPlayer?.prepare()
                mediaPlayer?.start()

            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            } catch (e: IOException) {
                isPlaying = false
                mediaPlayer?.stop()
                mediaPlayer = null
                val currentOrderItem = messageList.get(position)
                currentOrderItem.isAudioPlaying = true
                currentOrderItem.filePath = ""
                notifyDataSetChanged()
                showErrorMessage(mContext, "File no longer exist ! Please Download the file again.")
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        try {
            mediaPlayer?.setOnCompletionListener {
                isPlaying = false
                this@MessageAdapter.mediaPlayer?.stop()
                this@MessageAdapter.mediaPlayer = null
                val currentOrderItem = messageList.get(position)
                currentOrderItem.isAudioPlaying = false
                notifyDataSetChanged()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setDeletedMessageSelf(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, message: Message, position: Int) {
        val selfDeletedMessageViewHolder: SelfDeletedMessageViewHolder = holder as SelfDeletedMessageViewHolder
        setSelfMessageBackground(selfDeletedMessageViewHolder.tvReplies, selfDeletedMessageViewHolder.llRoot, selfDeletedMessageViewHolder.llMessageBg, position, message.userId)
//        setBackgroundColor(selfDeletedMessageViewHolder.llMessageBg.background as NinePatchDrawable, colorConfig.fuguBgMessageYou)
        selfDeletedMessageViewHolder.tvMessage.text = message.message + " "
        selfDeletedMessageViewHolder.tvMessage.setTextColor(mContext.resources.getColor(R.color.deleted_message_color))
        selfDeletedMessageViewHolder.tvMessage.setTypeface(selfDeletedMessageViewHolder.tvMessage.typeface, Typeface.ITALIC)
        if (message.sentAtUtc.isEmpty()) {
            selfDeletedMessageViewHolder.tvTime.visibility = View.GONE
        } else {
            selfDeletedMessageViewHolder.tvTime.text = DateUtils.getTime(dateUtil.convertToLocal(message.sentAtUtc))
            selfDeletedMessageViewHolder.tvTime.visibility = View.VISIBLE
        }
        if (message.threadMessage) {
            selfDeletedMessageViewHolder.llReplies.visibility = View.VISIBLE
            setReplies(selfDeletedMessageViewHolder.tvReplies, message.threadMessageCount)
        } else {
            selfDeletedMessageViewHolder.llReplies.visibility = View.GONE
        }
        setReplyClick(selfDeletedMessageViewHolder.tvReplies, message, true, selfDeletedMessageViewHolder.tvMessage)
        selfDeletedMessageViewHolder.llRoot.setOnTouchListener(ExtendedTouchListener(selfDeletedMessageViewHolder.itemView, selfDeletedMessageViewHolder.llRoot,
                selfDeletedMessageViewHolder.llRoot,
                message,
                chatActivity,
                recyclerView,
                selfDeletedMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
        selfDeletedMessageViewHolder.tvTime.setOnTouchListener(ExtendedTouchListener(selfDeletedMessageViewHolder.itemView, selfDeletedMessageViewHolder.llRoot,
                selfDeletedMessageViewHolder.llRoot,
                message,
                chatActivity,
                recyclerView,
                selfDeletedMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))

        setReplyData(selfDeletedMessageViewHolder.tvReplyTextList, selfDeletedMessageViewHolder.ivReplyImageList, selfDeletedMessageViewHolder.llReplyList, message)

    }

    @SuppressLint("SetTextI18n")
    private fun setDeletedMessageOther(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, message: Message, position: Int) {
        val deletedMessageViewHolder: OtherDeletedMessageViewHolder = holder as OtherDeletedMessageViewHolder

        deletedMessageViewHolder.llRoot.setOnTouchListener(ExtendedTouchListener(deletedMessageViewHolder.itemView, deletedMessageViewHolder.llRoot,
                deletedMessageViewHolder.llRoot,
                message,
                chatActivity,
                recyclerView,
                deletedMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))
        deletedMessageViewHolder.tvTime.setOnTouchListener(ExtendedTouchListener(deletedMessageViewHolder.itemView, deletedMessageViewHolder.llRoot,
                deletedMessageViewHolder.llRoot,
                message,
                chatActivity,
                recyclerView,
                deletedMessageViewHolder.tvReplies, chatType, label, channelId, false, messageList))

        setOtherMessageBackground(deletedMessageViewHolder.tvReplies, deletedMessageViewHolder.llRoot, deletedMessageViewHolder.llMessageBg, position, deletedMessageViewHolder.tvUserName, message.userId)
//        setBackgroundColor(deletedMessageViewHolder.llMessageBg.background as NinePatchDrawable, colorConfig.fuguBgMessageFrom)
        deletedMessageViewHolder.tvMessage.text = message.message + " "
        deletedMessageViewHolder.tvMessage.setTextColor(mContext.resources.getColor(R.color.deleted_message_color_other))
        deletedMessageViewHolder.tvMessage.setTypeface(deletedMessageViewHolder.tvMessage.typeface, Typeface.ITALIC)
        if (message.sentAtUtc.isEmpty()) {
            deletedMessageViewHolder.tvTime.visibility = View.GONE
        } else {
            deletedMessageViewHolder.tvTime.text = DateUtils.getTime(dateUtil.convertToLocal(message.sentAtUtc))
            deletedMessageViewHolder.tvTime.visibility = View.VISIBLE
        }
        if (message.threadMessage) {
            deletedMessageViewHolder.llReplies.visibility = View.VISIBLE
            setReplies(deletedMessageViewHolder.tvReplies, message.threadMessageCount)
        } else {
            deletedMessageViewHolder.llReplies.visibility = View.GONE
        }
        deletedMessageViewHolder.tvUserName.text = message.fromName

        setReplyClick(deletedMessageViewHolder.tvReplies, message, true, deletedMessageViewHolder.tvMessage)
        deletedMessageViewHolder.tvUserName.setOnClickListener {
            if (message.userType != 0) {
                openProfile("mention://" + message.userId.toString(), mContext)
            }
        }
        setReplyData(deletedMessageViewHolder.tvReplyTextList, deletedMessageViewHolder.ivReplyImageList, deletedMessageViewHolder.llReplyList, message)
    }

    fun updateMessageList(messageList: ArrayList<Message>) {
        //this.messageList = messageList
        try {
            val diffCallback = MessageDiffCallback(this.messageList, messageList)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            this.messageList = ArrayList<Message>()
            this.messageList.addAll(messageList)
            diffResult.dispatchUpdatesTo(this)
            for (i in 0 until messageList.size) {
                messageMap.put(messageList[i].muid, messageList[i])
            }

            for (image in imageFiles) {
                image.message = messageMap[image.muid]
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateThreadMessageList(messageList: ArrayList<Message>) {
        this.messageList = messageList
        try {
            for (i in 0 until messageList.size) {
                messageMap.put(messageList[i].muid, messageList[i])
            }

            for (image in imageFiles) {
                image.message = messageMap[image.muid]
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun setSelfMessageBackground(tvReplies: TextView?, viewMessage: View, viewBg: View, position: Int, userId: Long): Boolean {

//        viewUsername?.setTextColor(Color.parseColor(arrayOfColors[(userId % 10).toInt()]))

        if (messageList.size == 1) {
//            viewBg.setBackgroundResource(R.drawable.chat_bg_right)
            viewMessage.setPadding(dpToPx(0), dpToPx(20), dpToPx(7), dpToPx(1))
            viewBg.setPadding(dpToPx(6), dpToPx(6), dpToPx(6), dpToPx(6))
            tvReplies?.setPadding(dpToPx(0), dpToPx(0), dpToPx(7), dpToPx(0))
            return true
        } else {
            if (position == 0) {
//                viewBg.setBackgroundResource(R.drawable.chat_bg_right)
                viewMessage.setPadding(dpToPx(0), dpToPx(20), dpToPx(7), dpToPx(1))
                viewBg.setPadding(dpToPx(6), dpToPx(6), dpToPx(6), dpToPx(6))
                tvReplies?.setPadding(dpToPx(0), dpToPx(0), dpToPx(7), dpToPx(0))
                return true
            } else if (getItemViewType(position - 1) == TEXT_MESSGAE_SELF
                    || getItemViewType(position - 1) == IMAGE_MESSGAE_SELF
                    || getItemViewType(position - 1) == FILE_MESSGAE_SELF
                    || getItemViewType(position - 1) == VIDEO_MESSGAE_SELF
                    || getItemViewType(position - 1) == VIDEO_CALL_SELF
                    || getItemViewType(position - 1) == POLL_SELF
                    || getItemViewType(position - 1) == CUSTOM_ACTION
                    || getItemViewType(position - 1) == MESSAGE_DELETED_SELF) {
//                viewBg.setBackgroundResource(R.drawable.chat_bg_right_normal)
                viewMessage.setPadding(dpToPx(0), dpToPx(5), dpToPx(7), dpToPx(1))
                viewBg.setPadding(dpToPx(6), dpToPx(6), dpToPx(6), dpToPx(6))
                tvReplies?.setPadding(dpToPx(0), dpToPx(0), dpToPx(7), dpToPx(0))
                return false
            } else {
//                viewBg.setBackgroundResource(R.drawable.chat_bg_right)
//                viewBg.setBackgroundResource(R.drawable.chat_bg_right)
                viewMessage.setPadding(dpToPx(0), dpToPx(20), dpToPx(7), dpToPx(1))
                viewBg.setPadding(dpToPx(6), dpToPx(6), dpToPx(6), dpToPx(6))
                tvReplies?.setPadding(dpToPx(0), dpToPx(0), dpToPx(7), dpToPx(0))
                return true
            }
        }
    }


    private fun setOtherMessageBackground(tvReplies: TextView?, viewMessage: View, viewBg: View, position: Int, viewUsername: TextView?, userId: Long): Boolean {

        viewUsername?.setTextColor(Color.parseColor(arrayOfColors[(userId % 10).toInt()]))

        if (messageList.size == 1) {
            viewBg.setBackgroundResource(R.drawable.chat_bg_leftt)
            viewMessage.setPadding(dpToPx(8), dpToPx(20), dpToPx(0), dpToPx(1))
            viewBg.setPadding(dpToPx(6), dpToPx(6), dpToPx(6), dpToPx(6))
            tvReplies?.setPadding(dpToPx(0), dpToPx(0), dpToPx(0), dpToPx(0))

            if (chatType == ChatType.O2O || chatType == ChatType.BOT) {
                if (viewUsername != null) {
                    if (!isChatActivity() && messageList.size > 0 && messageList.get(0).userType == UserType.SCRUM_BOT) {
                        viewUsername.visibility = View.VISIBLE
                    } else {
                        viewUsername.visibility = View.GONE
                    }
                }
            } else {
                if (viewUsername != null) {
                    viewUsername.visibility = View.VISIBLE
                }
            }
            return true
        } else {
            if (position == 0) {
                viewMessage.setPadding(dpToPx(8), dpToPx(20), dpToPx(0), dpToPx(1))
                viewBg.setPadding(dpToPx(6), dpToPx(6), dpToPx(6), dpToPx(6))
                viewUsername?.setPadding(dpToPx(8), dpToPx(0), dpToPx(0), dpToPx(0))
//                viewBg.setBackgroundResource(R.drawable.chat_bg_leftt)

                if (chatType == ChatType.O2O || chatType == ChatType.BOT) {
                    if (viewUsername != null) {
                        if (!isChatActivity() && messageList.size > 0 && messageList.get(0).userType == UserType.SCRUM_BOT) {
                            viewUsername.visibility = View.VISIBLE
                        } else {
                            viewUsername.visibility = View.GONE
                        }
                    }
                } else {
                    if (viewUsername != null) {
                        viewUsername.visibility = View.VISIBLE
                    }
                }
                return true
            } else if ((getItemViewType(position - 1) == TEXT_MESSGAE_OTHER
                            || getItemViewType(position - 1) == IMAGE_MESSGAE_OTHER
                            || getItemViewType(position - 1) == FILE_MESSGAE_OTHER
                            || getItemViewType(position - 1) == VIDEO_MESSGAE_OTHER
                            || getItemViewType(position - 1) == VIDEO_CALL_OTHER
                            || getItemViewType(position - 1) == POLL_OTHER
                            || getItemViewType(position - 1) == CUSTOM_ACTION
                            || getItemViewType(position - 1) == MESSAGE_DELETED_OTHER) && messageList[position - 1].userId.compareTo(messageList[position].userId) == 0) {
//                viewBg.setBackgroundResource(R.drawable.chat_bg_right_normal)
                if (viewUsername != null) {
                    viewUsername.visibility = View.GONE
                }
                viewMessage.setPadding(dpToPx(8), dpToPx(3), dpToPx(0), dpToPx(1))
                viewBg.setPadding(dpToPx(6), dpToPx(6), dpToPx(6), dpToPx(6))
                tvReplies?.setPadding(dpToPx(0), dpToPx(0), dpToPx(15), dpToPx(0))
                return false
            } else {
//                viewBg.setBackgroundResource(R.drawable.chat_bg_leftt)

                if (chatType == ChatType.O2O || chatType == ChatType.BOT) {
                    if (viewUsername != null) {
                        if (!isChatActivity() && messageList.size > 0 && messageList.get(0).userType == UserType.SCRUM_BOT) {
                            viewUsername.visibility = View.VISIBLE
                        } else {
                            viewUsername.visibility = View.GONE
                        }
                    }
                } else {
                    if (viewUsername != null) {
                        viewUsername.visibility = View.VISIBLE
                    }
                }
                viewMessage.setPadding(dpToPx(8), dpToPx(20), dpToPx(0), dpToPx(1))
                viewBg.setPadding(dpToPx(6), dpToPx(6), dpToPx(6), dpToPx(6))
                tvReplies?.setPadding(dpToPx(0), dpToPx(0), dpToPx(0), dpToPx(0))
                return true
            }
        }

    }


    private fun setPublicNote(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, message: Message) {
        val publicNoteViewHolder: PublicNoteViewHolder = holder as PublicNoteViewHolder
        publicNoteViewHolder.tvMessage.text = message.message
    }

    private fun setDateMessage(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, message: Message) {
        try {
            val dateViewHolder: DateViewHolder = holder as DateViewHolder
            dateViewHolder.tvDate.text = message.sentAtUtc
//            val drawable = dateViewHolder.tvDate.background as GradientDrawable
//            dateViewHolder.tvDate.setTextColor(colorConfig.fuguChannelDateText)
        } catch (e: Exception) {
//            Crashlytics.logException(e)
            setViewModelInCatch(holder, position, message, false)
        }
    }


    private fun setMessageStatus(ivMessageState: AppCompatImageView, messageStatus: Int?, isImage: Boolean) {
        if (isImage) {
            when (messageStatus) {
                MESSAGE_SENT -> ivMessageState.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_single_tick))
                MESSAGE_UNSENT -> ivMessageState.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.fugu_ic_waiting_white))
                MESSAGE_DELIVERED -> ivMessageState.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_double_tick))
                MESSAGE_READ -> ivMessageState.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_double_tick))
                MESSAGE_FILE_RETRY -> ivMessageState.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.fugu_ic_waiting_white))
                MESSAGE_IMAGE_RETRY -> ivMessageState.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.fugu_ic_waiting_white))
            }
        } else {
            when (messageStatus) {
                MESSAGE_SENT -> ivMessageState.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_single_tick))
                MESSAGE_UNSENT -> ivMessageState.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.fugu_ic_waiting))
                MESSAGE_DELIVERED -> ivMessageState.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_double_tick))
                MESSAGE_READ -> ivMessageState.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_double_tick))
                MESSAGE_FILE_RETRY -> ivMessageState.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.fugu_ic_waiting))
                MESSAGE_IMAGE_RETRY -> ivMessageState.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.fugu_ic_waiting))
            }
        }
    }


    private fun setImageHeightAndWidth(imageView: ImageView, rlImageMessage: RelativeLayout, llImageMessage: LinearLayoutCompat, message: Message, isSpiked: Boolean) {

        if (message.imageHeight != 0 && message.imageWidth != 0) {
            val ratio = message.imageHeight.toFloat() / message.imageWidth.toFloat()
            if (ratio < 1) {

                if (ratio < 0.4) {
                    rlImageMessage.layoutParams.height = (dpToPx(MIN_HEIGHT)).toInt()
                } else {
                    rlImageMessage.layoutParams.height = (dpToPx(MAX_HEIGHT) * ratio).toInt()
                }
                rlImageMessage.layoutParams.width = dpToPx(MAX_WIDTH)
                llImageMessage.layoutParams.width = dpToPx(MAX_WIDTH_OUTER)
            } else {
                rlImageMessage.layoutParams.height = dpToPx(MAX_HEIGHT)
                rlImageMessage.layoutParams.width = dpToPx(MAX_WIDTH)
                llImageMessage.layoutParams.width = dpToPx(MAX_WIDTH_OUTER)
            }
        } else {
            rlImageMessage.layoutParams.height = dpToPx(MAX_HEIGHT)
            rlImageMessage.layoutParams.width = dpToPx(MAX_WIDTH)
            llImageMessage.layoutParams.width = dpToPx(MAX_WIDTH_OUTER)
        }

//        val curveRadius = 20F

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
//            rlImageMessage.outlineProvider = object : ViewOutlineProvider() {
//
//                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//                override fun getOutline(view: View?, outline: Outline?) {
//                    outline?.setRoundRect(0,0, view!!.width, (view.height+curveRadius).toInt(), curveRadius)
//                }
//            }
//
//            rlImageMessage.clipToOutline = true
//
//        }


    }


    private fun manipulateAndSetText(tvMessage: AppCompatTextView, message: String?, messageState: Int = 1) {
        val textArray = message.toString().split(" ")
        var text = ""
        for (i in textArray) {
            text = if (i.toLowerCase().contains("http") ||
                    i.toLowerCase().contains("www.")) {
                "$text <a href=\"$i\">$i</a> "
            } else {
                "$text $i"
            }
        }

        text = text.replace("Http", "http")
        text = text.replace("Https", "https")
        text = text.replace("WWW.", "www.")

        text = text.trim().replace("\n", "<br>")
        val s = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(text.toString(), Html.FROM_HTML_OPTION_USE_CSS_COLORS) as Spannable
        } else {
            Html.fromHtml(text.toString()) as Spannable
        }
        for (u in s.getSpans(0, s.length, URLSpan::class.java)) {
            s.setSpan(object : UnderlineSpan() {
                override fun updateDrawState(tp: TextPaint) {
                    tp.isUnderlineText = false
                }
            }, s.getSpanStart(u), s.getSpanEnd(u), 0)
        }
//        tvMessage.setLinkTextColor(mContext.resources.getColor(R.color.color_tag))
        tvMessage.text = s

        tvMessage.movementMethod = BetterLinkMovementMethod.getInstance()
        val replacements = MyEmojiParser.getUnicodeCandidates(s.toString())
        val messageWithoutEmojis = MyEmojiParser.removeAllEmojis(s.toString())

        if (replacements.size in 1..2 && messageWithoutEmojis.isEmpty()) {
            val ss1 = SpannableString(s)
            ss1.setSpan(RelativeSizeSpan(2f), 0, s.length, 0)
            tvMessage.text = ss1
        } else {
            if (messageState == 4) {
                try {
                    val messageWithoutEmojis = MyEmojiParser.removeAllEmojis(s.substring(0, s.length - 9))
                    if (replacements.size in 1..2 && messageWithoutEmojis.isEmpty()) {
                        val ss1 = SpannableString(s)
                        ss1.setSpan(RelativeSizeSpan(1f), s.length - 9, s.length, 0)
                        ss1.setSpan(RelativeSizeSpan(2f), 0, s.length - 9, 0)
                        tvMessage.text = ss1
                    } else {
                        val ss1 = SpannableString(s)
                        ss1.setSpan(RelativeSizeSpan(1f), 0, s.length, 0)
                        tvMessage.text = ss1
                    }
                } catch (e: Exception) {
                    val ss1 = SpannableString(s)
                    ss1.setSpan(RelativeSizeSpan(1f), 0, s.length, 0)
                    tvMessage.text = ss1
                }
            } else {
                val ss1 = SpannableString(s)
                ss1.setSpan(RelativeSizeSpan(1f), 0, s.length, 0)
                tvMessage.text = ss1
            }
        }

//        tvMessage.movementMethod=LinkMovementMethod.getInstance()
//        BetterLinkMovementMethod.linkifyHtml(tvMessage).setOnLinkClickListener(urlClickListener)
//        if (!tvMessage.text.toString().toLowerCase().contains("http")) {
//
//        } else {
//            BetterLinkMovementMethod.linkifyHtml(tvMessage).setOnLinkClickListener(urlClickListener)
//        }
        BetterLinkMovementMethod.linkifyHtmlNone(tvMessage).setOnLinkLongClickListener(
                urlClickListenerLong).setOnLinkClickListener(urlClickListener)
    }

    fun setOnLongClickValue(onLongClick: Boolean) {
        this.onLongClick = onLongClick
    }

    private val urlClickListener = BetterLinkMovementMethod.OnLinkClickListener { _, url ->
        if (!onLongClick) {
            if (url.toLowerCase().contains("http") || url.toLowerCase().contains("www.")) {
                var clickableLink = url
                clickableLink = clickableLink.replace("<b>", "")
                clickableLink = clickableLink.replace("<i>", "")
                clickableLink = clickableLink.replace("</i>", "")
                clickableLink = clickableLink.replace("</b>", "")
                clickableLink = clickableLink.replace("</br>", "")
                clickableLink = clickableLink.replace("<br>", "")
                try {
                    if (clickableLink.startsWith("www.")) {
                        clickableLink = "http://$clickableLink"
                    }
                    if (clickableLink.matches(Regex("https:\\/\\/app\\.fugu\\.chat\\/[0-9a-zA-Z]+[\\-0-9a-zA-Z]*\\/join($|\\/)")) ||
                            clickableLink.matches(Regex("https:\\/\\/spacedev\\.officechat\\.io\\/[0-9a-zA-Z]+[\\-0-9a-zA-Z]*\\/join($|\\/)"))) {
                        val intent = Intent(mContext, JoinCommunityActivity::class.java)
                        intent.action = Intent.ACTION_VIEW
                        intent.data = Uri.parse(clickableLink)
                        mContext.startActivity(intent)
                        return@OnLinkClickListener true
                    }
                    if (clickableLink.matches(Regex("https:\\/\\/meet\\.google\\.com\\/[a-z]{3}\\-[a-z]{4}\\-[a-z]{3}($|\\?.*)"))) {
                        mContext.joinHangoutsCall(clickableLink)
                        return@OnLinkClickListener true
                    }
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(clickableLink)
                    mContext.startActivity(i)
                } catch (e: java.lang.Exception) {
                    try {
                        if (isChatActivity()) {
                            showErrorMessage(chatActivity!!, "$clickableLink is not a valid link!")
                        } else {
                            showErrorMessage(chatActivity!!, "$clickableLink is not a valid link!")
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                try {
                    openProfile(url, mContext)
                } catch (e: Exception) {

                }

            }
        }
        onLongClick = false
        true
    }

    private val urlClickListenerLong = BetterLinkMovementMethod.OnLinkLongClickListener { _, url ->
        //        val clipboard = mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        //val clip = ClipData.newPlainText("", Html.fromHtml((url)))
        //clipboard.primaryClip = clip
        if (url.toLowerCase().contains("http") || url.toLowerCase().contains("www.")) {
            val parsedUrl = url.replace("<b>", "").replace("<i>", "").replace("</i>", "").replace("</b>", "").replace("</br>", "").replace("<br>", "").trim()
            onLinkLongClick = true
            Log.i("LinkOnLongClick", "true")
            if (isChatActivity()) {
                chatActivity?.copyUrl(parsedUrl)
            } else {
                fuguInnerChatActivity.copyUrl(parsedUrl)
            }
        }
//        onLinkLongClick = false
        true
    }

    private fun openProfile(url: String, activity: Context) {
        @Suppress("NAME_SHADOWING")
        var url = url

        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        if (!TextUtils.isEmpty(url.trim { it <= ' ' })) {
            val mIntent = Intent(Intent(activity, ProfileActivity::class.java))
            if (!ValidationUtil.checkEmail(url)) {
                url = url.split("mention://".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            }
            if (url == com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userId.toString()) {
                mIntent.putExtra("no_chat", "no_chat")
            }
            mIntent.putExtra("open_profile", url)
            if (url != "-1") {
                activity.startActivity(mIntent)
            }
        }
    }

    private fun setTime(tvImageTime: AppCompatTextView, sentAtUtc: String?) {
        tvImageTime.text = DateUtils.getTime(dateUtil.convertToLocal(sentAtUtc))
    }

    private fun setBackgroundColor(drawable: NinePatchDrawable, fuguBgMessageFrom: Int) {
//        drawable.setColorFilter(fuguBgMessageFrom, PorterDuff.Mode.MULTIPLY)
    }

    fun setImage(pbDownloading: ProgressBar?, llDownloadImage: LinearLayout?, ivImageMessage: AppCompatImageView, message: Message, position: Int) {


        var extension = message.image_url!!.split(".")[message.image_url.split(".").size - 1]

        if (((mContext is ChatActivity && !(mContext as ChatActivity).isFinishing))
                || (mContext is FuguInnerChatActivity && !(mContext as FuguInnerChatActivity).isFinishing)) {
            try {
                val options: RequestOptions?
                var displayFileUrl: String?

                if (extension.contains("gif")) {
                    llDownloadImage?.visibility = View.GONE
                    pbDownloading?.visibility = View.GONE
                    options = RequestOptions()
                            .fitCenter()
                    if (mContext is ChatActivity) {
                        Glide.with(mContext as ChatActivity)
                                .asGif()
                                .load(message.image_url)
                                .apply(options)
                                .into(ivImageMessage)
                    } else {
                        Glide.with(mContext as FuguInnerChatActivity)
                                .asGif()
                                .load(message.image_url)
                                .apply(options)
                                .into(ivImageMessage)
                    }
                } else {
                    var fileName: String
                    if (!TextUtils.isEmpty(extension)) {

                        if (extension.toLowerCase().equals("png")) {
                            extension = "jpg"
                        }

                        fileName = message.fileName + "_" + message.muid + "." + extension
                    } else {
                        fileName = message.fileName + "_" + message.muid
                    }
                    try {
                        val filePathNormal = File(getNormalDirectory(extension) + "/" + fileName)
                        val filePathPriavte = File(getPrivateDirectory(extension) + "/" + fileName)
                        if (!TextUtils.isEmpty(message.image_url_100x100)) {
                            displayFileUrl = message.image_url_100x100
                        } else {
                            displayFileUrl = message.thumbnailUrl
                        }
                        var imageHeight = MAX_HEIGHT
                        var imageWidth = MAX_WIDTH
                        if (message.imageHeight != 0) {
                            if (message.imageHeight > 4000) {
                                imageHeight = message.imageHeight / 4
                                imageWidth = message.imageWidth / 4
                            } else if (message.imageHeight > 2000) {
                                imageHeight = message.imageHeight / 2
                                imageWidth = message.imageWidth / 2
                            } else {
                                imageHeight = message.imageHeight
                                imageWidth = message.imageWidth
                            }
                        } else {
                            imageHeight = MAX_HEIGHT
                            imageWidth = MAX_WIDTH
                        }
                        if (filePathNormal.exists() || filePathPriavte.exists()) {
                            llDownloadImage?.visibility = View.GONE
                            pbDownloading?.visibility = View.GONE
                            var link = ""
                            if (filePathNormal.exists()) {
                                link = filePathNormal.absolutePath
                            } else {
                                link = filePathPriavte.absolutePath
                            }

                            val exif = androidx.exifinterface.media.ExifInterface(link)
                            if (!exif.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_MODEL).equals(message.id.toString())
                                    && (message.messageStatus == MESSAGE_SENT
                                            || message.messageStatus == MESSAGE_DELIVERED)) {
                                exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_DATETIME, message.sentAtUtc)
                                exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MODEL, message.id.toString())
                                exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MAKE, channelId.toString() + muid)
                                exif.saveAttributes()



                                messageMap[message.muid] = message
                                getFromSdcard()
                            }

                            displayFileUrl = link

                            if (!isChatActivity() && position == 0) {
                                options = RequestOptions()
                                        .dontAnimate()
                                        .placeholder(R.drawable.placeholder)
                                        .error(R.drawable.placeholder)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .priority(Priority.HIGH)
                            } else {
                                options = RequestOptions()
                                        .dontAnimate()
                                        .placeholder(R.drawable.placeholder)
                                        .error(R.drawable.placeholder)
                                        .override(imageWidth, imageHeight)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .priority(Priority.HIGH)
                            }


                        } else {
                            llDownloadImage?.visibility = View.VISIBLE
                            pbDownloading?.visibility = View.GONE

                            if (!isChatActivity() && position == 0) {
                                options = RequestOptions()
                                        .dontAnimate()
                                        .placeholder(R.drawable.placeholder)
                                        .error(R.drawable.placeholder)
                                        .priority(Priority.HIGH)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .transforms(CenterCrop(), RoundedCorners(3), BlurTransform(5, 3))
                            } else {
                                options = RequestOptions()
                                        .dontAnimate()
                                        .placeholder(R.drawable.placeholder)
                                        .error(R.drawable.placeholder)
                                        .priority(Priority.HIGH)
                                        .override(imageWidth, imageHeight)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .transforms(CenterCrop(), RoundedCorners(3), BlurTransform(5, 3))
                            }

                        }

                        if (mContext is ChatActivity) {
                            Glide.with(mContext as ChatActivity)
                                    .asBitmap()
                                    .apply(options)
                                    .load(displayFileUrl)
                                    .into(ivImageMessage)
                        } else {
                            Glide.with(mContext as FuguInnerChatActivity)
                                    .asBitmap()
                                    .apply(options)
                                    .load(displayFileUrl)
                                    .into(ivImageMessage)
                        }
                    } catch (e: java.lang.Exception) {
                        val optionss = RequestOptions()
                                .dontAnimate()
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.placeholder)
                                .priority(Priority.HIGH)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .transforms(CenterCrop(), RoundedCorners(3), BlurTransform(5, 3))
                        try {
                            if (mContext is ChatActivity) {
                                Glide.with(mContext as ChatActivity)
                                        .asBitmap()
                                        .apply(optionss)
                                        .load(message.thumbnailUrl)
                                        .into(ivImageMessage)
                            } else {
                                Glide.with(mContext as FuguInnerChatActivity)
                                        .asBitmap()
                                        .apply(optionss)
                                        .load(message.thumbnailUrl)
                                        .into(ivImageMessage)
                            }
                        } catch (e: Exception) {

                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun getNormalDirectory(extension: String): String? {
        try {
            var filePath = (Environment.getExternalStorageDirectory()).toString() + File.separator + FuguAppConstant.APP_NAME_SHORT +
                    File.separator + CommonData.getWorkspaceResponse(workspaceInfo!!.get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).fuguSecretKey).workspaceName.replace(" ".toRegex(), "").replace("'s".toRegex(), "") + File.separator + FuguAppConstant.FILE_TYPE_MAP[extension.toLowerCase()]!!.directory
            val filePathArray = filePath.split("/")
            val folder = File(filePath)
            if (!folder.exists()) {
                folder.mkdirs()
            }
            return filePath
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun getPrivateDirectory(extension: String): String? {
        try {
            var filePath = (Environment.getExternalStorageDirectory()).toString() + File.separator + FuguAppConstant.APP_NAME_SHORT +
                    File.separator + CommonData.getWorkspaceResponse(workspaceInfo!!.get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).fuguSecretKey).workspaceName.replace(" ".toRegex(), "").replace("'s".toRegex(), "") + File.separator + FuguAppConstant.FILE_TYPE_MAP[extension.toLowerCase()]!!.directory
            val filePathArray = filePath.split("/")
            filePath = filePath.replace(FuguAppConstant.IMAGE, FuguAppConstant.PRIVATE_IMAGES)
            val folder = File(filePath)
            if (!folder.exists()) {
                folder.mkdirs()
            }
            if (filePathArray[filePathArray.size - 1].equals(FuguAppConstant.IMAGE)) {
                if (com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[currentPos].mediaVisibility == 0) {
                    val f = File(filePath + "/.nomedia")
                    if (!f.exists()) {
                        f.createNewFile()
                    }
                }
            }
            return filePath
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }


    private fun getDirectoryWithMediaCheck(extension: String): String? {
        try {
            var filePath = (Environment.getExternalStorageDirectory()).toString() + File.separator + FuguAppConstant.APP_NAME_SHORT +
                    File.separator + CommonData.getWorkspaceResponse(workspaceInfo!!.get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).fuguSecretKey).workspaceName.replace(" ".toRegex(), "").replace("'s".toRegex(), "") + File.separator + FuguAppConstant.FILE_TYPE_MAP[extension.toLowerCase()]!!.directory
            val filePathArray = filePath.split("/")
            if (filePathArray[filePathArray.size - 1].equals(FuguAppConstant.IMAGE)) {
                if (com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[currentPos].mediaVisibility == 0) {
                    filePath = filePath.replace(FuguAppConstant.IMAGE, FuguAppConstant.PRIVATE_IMAGES)
                }
            }
            Log.i("Path", filePath)
            val folder = File(filePath)
            if (!folder.exists()) {
                folder.mkdirs()
            }
            if (filePathArray[filePathArray.size - 1].equals(FuguAppConstant.IMAGE)) {
                if (com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[currentPos].mediaVisibility == 0) {
                    val f = File(filePath + "/.nomedia")
                    if (!f.exists()) {
                        f.createNewFile()
                    }
                }
            }
            return filePath
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setReplies(tvReplies: TextView, count: Int) {
        if (count == 1) {
            tvReplies.text = count.toString() + " Reply"
        } else if (count > 1) {
            tvReplies.text = count.toString() + " Replies"
        }
    }


    private fun setArrowClick(message: Message, itemView: View?, isSelf: Boolean, tvTime: AppCompatTextView?) {
        itemView?.setOnClickListener {
            Handler().postDelayed({
                if (!onLinkLongClick) {
                    onLongClick = true
                    if (isChatActivity() && (mContext as ChatActivity).getState() != MotionEvent.ACTION_MOVE && ((chatType != ChatType.BOT) || (chatType == ChatType.BOT && userType == UserType.SELF_BOT))) {
                        if (message.messageType == 15) {
                            val localDate = DateUtils.getFormattedDate(Date())
                            val newTime = DateUtils.getTimeInMinutes(DateUtils.getInstance().convertToUTC(localDate))
                            val oldTime = DateUtils.getTimeInMinutes(message.sentAtUtc)
                            if ((newTime - oldTime < com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].config.deleteMessageDuration / 60
                                            || com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].config.deleteMessageDuration == 0)
                                    && com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].config.deleteMessage == 1) {
                                var roles = com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].config.deleteMessageRole
                                roles = roles.replace("[", "")
                                roles = roles.replace("]", "")
                                roles = roles.replace("\"".toRegex(), "")
                                val rolesArray = roles.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                val rolesList = java.util.ArrayList(Arrays.asList(*rolesArray))
                                val presentRole = com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].role
                                if (rolesList.contains(presentRole)) {
                                    val location = IntArray(2)
                                    tvTime?.getLocationInWindow(location)
                                    try {
                                        chatActivity?.openBottomSheet(message.muid, isSelf, location)
                                    } catch (e: Exception) {

                                    }

                                } else {

                                }
                            } else {
                            }
                        } else {
                            val location = IntArray(2)
                            tvTime?.getLocationInWindow(location)
                            try {
                                chatActivity?.openBottomSheet(message.muid, isSelf, location)
                            } catch (e: Exception) {

                            }
                        }
                        true
                    } else if (!isChatActivity() && ((chatType != ChatType.BOT) || (chatType == ChatType.BOT && userType == UserType.SELF_BOT))) {
                        val location = IntArray(2)
                        tvTime?.getLocationInWindow(location)
                        try {
                            fuguInnerChatActivity.openBottomSheet(message.muid, message.messageType, isSelf, isMessageDeleted, message.messageStatus, message.sentAtUtc, message.messageState, message.isStarred, location)
                        } catch (e: Exception) {

                        }
                        true
                    } else {
                        false
                    }
                } else {
                    onLinkLongClick = false
                    false
                }
            }, 100)
        }
    }

    private fun setItemLongClick(itemView: View?, message: Message, isSelf: Boolean, tvTime: AppCompatTextView?) {

        itemView?.setOnLongClickListener {
            Handler().postDelayed({
                if (!onLinkLongClick) {
                    onLongClick = true
                    if (isChatActivity() && (mContext as ChatActivity).getState() != MotionEvent.ACTION_MOVE && ((chatType != ChatType.BOT) || (chatType == ChatType.BOT && userType == UserType.SELF_BOT))) {
                        val location = IntArray(2)
                        tvTime?.getLocationInWindow(location)
                        try {
                            chatActivity?.openBottomSheet(message.muid, isSelf, location)
                        } catch (e: Exception) {

                        }
                        true
                    } else if (!isChatActivity() && ((chatType != ChatType.BOT) || (chatType == ChatType.BOT && userType == UserType.SELF_BOT))) {
                        val location = IntArray(2)
                        tvTime?.getLocationInWindow(location)
                        try {
                            fuguInnerChatActivity.openBottomSheet(message.muid, message.messageType, isSelf, isMessageDeleted, message.messageStatus, message.sentAtUtc, message.messageState, message.isStarred, location)
                        } catch (e: Exception) {

                        }
                        true
                    } else {
                        onLinkLongClick = false
                        false
                    }
                }
            }, 100)
        }
    }

    private fun isChatActivity(): Boolean {
        return if (mContext is ChatActivity) {
            chatActivity = mContext as ChatActivity
            true
        } else {
            fuguInnerChatActivity = mContext as FuguInnerChatActivity
            false
        }
    }

    fun messageDeleted() {
        isMessageDeleted = true
    }

    fun switchPerformClickCount() {
        performClickCount = 0
    }

    private fun getIsNetworkConnected(): Boolean {
        return if (isChatActivity()) {
            chatActivity!!.isNetworkConnected
        } else {
            fuguInnerChatActivity.isNetworkConnected
        }
    }

    private fun openReactionDialog(reaction: UserReaction) {
        val manager: FragmentManager = if (isChatActivity()) {
            chatActivity!!.supportFragmentManager
        } else {
            fuguInnerChatActivity.supportFragmentManager
        }
        val ft = manager.beginTransaction()
        val newFragment = EmojiReactionsDialog.newInstance(0, reaction)
        newFragment.show(ft, "ReactionFragment")

    }

    override fun onAnimationRepeat(animation: Animation?) {

    }

    override fun onAnimationEnd(animation: Animation?) {
    }

    override fun onAnimationStart(animation: Animation?) {
    }

    private fun setReplyClick(itemView: TextView?, message: Message, isDeleted: Boolean, tvMessage: TextView?) {
        itemView?.setOnClickListener {
            if ((mContext as ChatActivity).isNetworkConnected) {

                if (isChatActivity()) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return@setOnClickListener
                    }
                    mLastClickTime = SystemClock.elapsedRealtime()
                    if (!TextUtils.isEmpty(message.muid)) {
                        if (PRDownloader.getStatus(downloadIdVideo) == Status.RUNNING) {
                            PRDownloader.pause(downloadIdVideo)
                        }
                        tvMessage?.transitionName = message.muid
                        itemView.transitionName = message.muid + "reply"
                        val innectChatIntent = Intent(mContext, FuguInnerChatActivity::class.java)
                        innectChatIntent.putExtra(FuguAppConstant.CHANNEL_ID, channelId)
                        innectChatIntent.putExtra(FuguAppConstant.MESSAGE_UNIQUE_ID, message.muid)
                        innectChatIntent.putExtra("BUSINESS_NAME", label)
                        innectChatIntent.putExtra("chatType", chatType)
                        innectChatIntent.putExtra("label", label)
                        innectChatIntent.putExtra(MESSAGE, message)
                        innectChatIntent.putExtra("isFromChatActivity", 1)
                        innectChatIntent.putExtra("downloadId", message.downloadId)
                        innectChatIntent.putExtra("userType", userType)
                        innectChatIntent.putExtra("only_admin_can_message", onlyAdminCanReply)
                        performClickCount = 0
                        if (isDeleted) {
                            innectChatIntent.putExtra(FuguAppConstant.DELETED_MESSAGE, FuguAppConstant.DELETED_MESSAGE)
                        }
                        if (isChatActivity()) {
                            chatActivity?.openedThread()
                        }
                        val listItems = ArrayList<Message>()
                        listItems.addAll(messageList)
                        object : Thread() {
                            override fun run() {
                                super.run()
                                ChatDatabase.setMessageList(listItems, channelId)
                            }
                        }.start()
                        if (tvMessage != null && message.messageType == TEXT_MESSAGE) {
                            chatActivity?.startActivityForResult(innectChatIntent, 20022)
                        } else {
                            isAnimationPlaying = false
                            chatActivity?.startActivityForResult(innectChatIntent, 20022)
                        }
                    }
                }
            } else {
                AlertDialog.Builder(mContext as ChatActivity)
                        .setMessage("Please check your Internet connection and try again.")
                        .setPositiveButton("ok") { dialog, which -> }.show()

            }
        }
//        }
    }

    private fun showImageDialog(activity: Context, imgUrl: String, thumbnailUrl: Drawable) {
        try {
            val dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar)
            //setting custom layout to dialog
            dialog.setContentView(R.layout.fugu_image_dialog)
            val lp = dialog.window!!.attributes
            lp.dimAmount = 1.0f // Dim level. 0.0 - no dim, 1.0 - completely opaque
            dialog.window!!.attributes = lp
            dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(false)
            val ivImage: PhotoView
            val tvCross: TextView
            val pbLoading: AVLoadingIndicatorViewFugu
            ivImage = dialog.findViewById(R.id.ivImage)
            pbLoading = dialog.findViewById(R.id.pbLoading)
            pbLoading.visibility = View.VISIBLE


            val options = RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .fitCenter()
                    .priority(Priority.HIGH)
                    .transforms(CenterCrop(), RoundedCorners(3))


            Glide.with(mContext as ChatActivity)
                    .asBitmap()
                    .apply(options)
                    .load(imgUrl)
                    .listener(object : RequestListener<Bitmap> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            pbLoading.visibility = View.GONE
                            return false
                        }

                    })
                    .into(ivImage)
            tvCross = dialog.findViewById(R.id.tvCross)
            tvCross.setOnClickListener({ dialog.dismiss() })
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showImageDialogFuguInnerChat(activity: Context, imgUrl: String) {
        val manager = (mContext as FuguInnerChatActivity).supportFragmentManager
        ft = manager.beginTransaction()
        val newFragment = ImageDialog.newInstance(0, imgUrl)
        newFragment.show(ft!!, "ImageFragment")
    }

    private fun showVideoDialogFuguInnerChat(message: Message) {

        if (!TextUtils.isEmpty(message.filePath)) {

            val intent = Intent(mContext as FuguInnerChatActivity, VideoPlayerActivity::class.java)
            intent.putExtra("url", message.filePath)
            (mContext as FuguInnerChatActivity).startActivity(intent)
        } else {
            val alert: AlertDialog.Builder = AlertDialog.Builder(mContext as FuguInnerChatActivity)
            alert.setMessage("File not downloaded!")
            alert.setPositiveButton("Ok", null)
            alert.setCancelable(false)
            alert.show()
        }
    }

    private fun checkAndObtainStoragePermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissionsNeeded: ArrayList<String> = ArrayList()
            val requiredPermissions = arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
            for (permission in requiredPermissions) {
                if ((mContext as Activity).checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                    permissionsNeeded.add(permission)
            }
            return if (permissionsNeeded.isNotEmpty()) {
//                Toast.makeText((mContext as Activity), "Storage permission is required to download files.", Toast.LENGTH_SHORT).show()
//                Toast.makeText((mContext as Activity), "${permissionsNeeded[0].replace("android.permission.", "")} permission not granted.", Toast.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions((mContext as Activity), permissionsNeeded.toTypedArray() as Array<out String>, STORAGE_PERMISSION_TO_DOWNLOAD_REQUEST)
                false
            } else
                true
        } else
            return true
    }

    private fun downloadFileFromUrl(dirPath: String, currentOrderItem: Message, position: Int, fileType: String): Int {
        var directorypath = dirPath
        if (dirPath.toLowerCase().equals("png"))
            directorypath = "jpg"
        try {
            Log.e("Download", directorypath)
            val currentProgress = intArrayOf(-1)
            var url = ""
            var fileName = ""
            var directoy = ""
            if (!TextUtils.isEmpty(currentOrderItem.url)) {
                directoy = directorypath
                url = currentOrderItem.url
                fileName = currentOrderItem.fileName
            } else {
                url = currentOrderItem.image_url
                directoy = getDirectoryWithMediaCheck(directorypath)!!
                fileName = currentOrderItem.fileName + "_" + currentOrderItem.muid + "." + directorypath
            }
            Log.e("Download id", currentOrderItem.downloadId.toString() + " " + PRDownloader.getStatus(currentOrderItem.downloadId))
            if (currentOrderItem.downloadId != 0) {
                if (PRDownloader.getStatus(currentOrderItem.downloadId).equals(Status.RUNNING) || PRDownloader.getStatus(currentOrderItem.downloadId).equals(Status.QUEUED)
                        || PRDownloader.getStatus(currentOrderItem.downloadId).equals(Status.UNKNOWN)) {
                    return 0
                }
            }

            return PRDownloader.download(url, directoy, fileName)
                    .build()
                    .setOnStartOrResumeListener { android.util.Log.i("TAG: complete", "DownloadStarted") }
                    .setOnPauseListener { currentOrderItem.downloadStatus = FuguAppConstant.DownloadStatus.DOWNLOAD_PAUSED.downloadStatus }
                    .setOnCancelListener { }
                    .setOnProgressListener { progress ->
                        if (currentProgress[0] < (progress.currentBytes * 100).toInt() / progress.totalBytes / 10) {
                            Log.i("insideIf1", currentProgress[0].toString())
                            Log.i("insideIf2", (progress.currentBytes * 100 / progress.totalBytes).toString())
                            currentProgress[0]++
                            if (!fileType.equals("Image")) {
                                val mIntent = getIntentExtraData(position, (progress.currentBytes * 100 / progress.totalBytes).toInt(), currentOrderItem.uuid, FuguAppConstant.DownloadStatus.DOWNLOAD_IN_PROGRESS.downloadStatus)
                                LocalBroadcastManager.getInstance(mContext).sendBroadcast(mIntent)
                            }
                        }
                        currentOrderItem.currentprogress = (progress.currentBytes * 100 / progress.totalBytes).toInt()
                        currentOrderItem.downloadStatus = FuguAppConstant.DownloadStatus.DOWNLOAD_IN_PROGRESS.downloadStatus
                        Log.e("Position", position.toString())
                        Log.e("Progress", (progress.currentBytes * 100 / progress.totalBytes).toString())
                    }
                    .start(object : OnDownloadListener {
                        override fun onDownloadComplete() {
                            Log.e("Progress", "Completed")

                            if (currentOrderItem.messageType == IMAGE_MESSAGE) {
                                Handler().postDelayed({
                                    try {
                                        var file = File(directoy + File.separator + fileName)
                                        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                                        val inputStream = mContext.contentResolver.openInputStream(FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", File(file.absolutePath)))!!

                                        val fileOutputStream = FileOutputStream(file)
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                                        val buffer = ByteArray(1024)

                                        val bytesRead = 0
                                        var i = inputStream.read(buffer)
                                        while (i > -1) {
                                            i = inputStream.read(buffer)
                                            fileOutputStream.write(buffer, 0, bytesRead)
                                        }
                                        fileOutputStream.close()
                                        file = File(directoy + File.separator + currentOrderItem.fileName + "_" + currentOrderItem.muid + ".jpg")
                                        if (!file.exists()) {
                                            file = File(directoy + File.separator + currentOrderItem.fileName + "_" + currentOrderItem.muid + ".jpeg")
                                            val exif = androidx.exifinterface.media.ExifInterface(file.absolutePath)
                                            if (!isChatActivity() && position == 0) {
                                                exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MAKE, channelId!!.toString())
                                            } else {
                                                exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MAKE, channelId!!.toString() + muid)
                                            }

                                            exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MODEL, currentOrderItem.id.toString())
                                            exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_DATETIME, currentOrderItem.sentAtUtc)
                                            exif.saveAttributes()
                                            imageFiles.add(ImageItem(directoy + File.separator + currentOrderItem.fileName + "_" + currentOrderItem.muid + ".jpeg",
                                                    currentOrderItem.muid, currentOrderItem.id.toString(), currentOrderItem.sentAtUtc, messageMap[currentOrderItem.muid]))
                                            imageFiles.sortWith(Comparator { one, other -> other.transitionName!!.compareTo(one.transitionName!!) })
                                            imageFiles.reverse()
                                        } else {
                                            val exif = androidx.exifinterface.media.ExifInterface(file.absolutePath)
                                            exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MAKE, channelId!!.toString() + muid)
                                            exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_DATETIME, currentOrderItem.sentAtUtc)
                                            exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MODEL, currentOrderItem.id.toString())
                                            exif.saveAttributes()
                                            imageFiles.add(ImageItem(directoy + File.separator + currentOrderItem.fileName + "_" + currentOrderItem.muid + ".jpg",
                                                    currentOrderItem.muid, currentOrderItem.id.toString(), currentOrderItem.sentAtUtc, messageMap[currentOrderItem.muid]))
                                            imageFiles.sortWith(Comparator { one, other -> other.transitionName!!.compareTo(one.transitionName!!) })
                                            imageFiles.reverse()
                                        }

                                        currentOrderItem.downloadStatus = FuguAppConstant.DownloadStatus.DOWNLOAD_COMPLETED.downloadStatus
                                        currentOrderItem.filePath = dirPath + "/" + currentOrderItem.fileName
                                        currentOrderItem.unsentFilePath = currentOrderItem.filePath
                                        currentOrderItem.sentFilePath = currentOrderItem.filePath

                                        val mIntent = getIntentExtraData(position, 100, currentOrderItem.uuid, FuguAppConstant.DownloadStatus.DOWNLOAD_COMPLETED.downloadStatus)
                                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(mIntent)
                                        val listItems = ArrayList<Message>()
                                        listItems.addAll(messageList)
                                        object : Thread() {
                                            override fun run() {
                                                super.run()
                                                Thread {
                                                    kotlin.run {
                                                        CommonData.setCachedFilePath(currentOrderItem.url, currentOrderItem.uuid, currentOrderItem.filePath)
                                                        CommonData.setFileLocalPath(currentOrderItem.uuid, currentOrderItem.filePath)
                                                        CommonData.setFilesMap(currentOrderItem.uuid, currentOrderItem.url, dirPath + "/" + currentOrderItem.fileName)
                                                        ChatDatabase.setMessageList(messageList, channelId)
                                                    }
                                                }.start()

                                            }
                                        }.start()

                                    } catch (e: java.lang.Exception) {
                                        e.printStackTrace()
                                    }

                                }, 0)

                            } else {

                                currentOrderItem.downloadStatus = FuguAppConstant.DownloadStatus.DOWNLOAD_COMPLETED.downloadStatus
                                currentOrderItem.filePath = dirPath + "/" + currentOrderItem.fileName
                                currentOrderItem.unsentFilePath = currentOrderItem.filePath
                                currentOrderItem.sentFilePath = currentOrderItem.filePath
                                if (currentOrderItem.messageType == VIDEO_MESSAGE) {
                                    currentOrderItem.sharableImage_url = currentOrderItem.url
                                    currentOrderItem.sharableThumbnailUrl = currentOrderItem.url
                                    currentOrderItem.sharableImage_url_100x100 = currentOrderItem.url
                                }
                                if (position > 0 && position < messageList.size) {
                                    messageList.set(position, currentOrderItem)
                                }

                                val mIntent = getIntentExtraData(position, 100, currentOrderItem.uuid, FuguAppConstant.DownloadStatus.DOWNLOAD_COMPLETED.downloadStatus)
                                LocalBroadcastManager.getInstance(mContext).sendBroadcast(mIntent)
                                val listItems = ArrayList<Message>()
                                listItems.addAll(messageList)
                                object : Thread() {
                                    override fun run() {
                                        super.run()
                                        Thread {
                                            kotlin.run {
                                                try {
                                                    CommonData.setCachedFilePath(currentOrderItem.url, currentOrderItem.uuid, currentOrderItem.filePath)
                                                    CommonData.setFileLocalPath(currentOrderItem.uuid, currentOrderItem.filePath)
                                                    CommonData.setFilesMap(currentOrderItem.uuid, currentOrderItem.url, dirPath + "/" + currentOrderItem.fileName)
                                                    messageList.set(position, currentOrderItem)
                                                    ChatDatabase.setMessageList(messageList, channelId)
                                                    ChatDatabase.getMessageList(channelId)
                                                } catch (e: java.lang.Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                        }.start()

                                    }
                                }.start()
                            }
                        }

                        override fun onError(error: Error) {
                            Log.e("Progress", "onError" + error.isConnectionError + error.isServerError)
                            currentOrderItem.downloadId = 0
                            currentOrderItem.downloadStatus = FuguAppConstant.DownloadStatus.DOWNLOAD_FAILED.downloadStatus
                            val mIntent = getIntentExtraData(position, 0, currentOrderItem.uuid, FuguAppConstant.DownloadStatus.DOWNLOAD_FAILED.downloadStatus)
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(mIntent)
                        }
                    })
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }
    }

    private fun getIntentExtraData(position: Int, progress: Int, muid: String, status: Int): Intent {
        val mIntent = Intent(FuguAppConstant.PROGRESS_INTENT)
        mIntent.putExtra(FuguAppConstant.POSITION, position)
        mIntent.putExtra(FuguAppConstant.PROGRESS, progress)
        mIntent.putExtra(FuguAppConstant.MESSAGE_UNIQUE_ID, muid)
        mIntent.putExtra(FuguAppConstant.STATUS_UPLOAD, status)
        return mIntent
    }

    /**
     * Attach observers(broadcast) for progress updates
     *
     * @param attach boolean to set if observer has to be attacked or detached
     */
    fun attachObservers(attach: Boolean) {
        if (attach) {
            attachObserver()
        } else {
            detachObserver()
        }
    }

    /**
     * Detach observer (unregister broadcast)
     */
    private fun detachObserver() {
        if(mProgressReceiver != null) {
            try {
                LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mProgressReceiver!!)
                receiverRegistered = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    fun checkConnection(context: Context): String {
        val connMgr: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetworkInfo = connMgr.activeNetworkInfo

        if (activeNetworkInfo != null) { // connected to the internet
            if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return FuguAppConstant.AutoDownloadLevel.WIFI.toString()
            } else if (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                return FuguAppConstant.AutoDownloadLevel.MOBILE_NETWORK.toString()
            }
        }
        return FuguAppConstant.AutoDownloadLevel.NONE.toString()
    }

    /**
     * Attach observer (register broadcast)
     */
    private fun attachObserver() {
        if (mProgressReceiver == null) {
            if (receiverRegistered)
                return
            initializeReceiver()
        }
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mProgressReceiver!!, IntentFilter(PROGRESS_INTENT))
        receiverRegistered = true
    }

    /**
     * Initialize receiver to receive broadcasts to update progress bar
     */
    private fun initializeReceiver() {
        mProgressReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val activity = if (isChatActivity()) {
                    chatActivity!!
                } else {
                    fuguInnerChatActivity
                }
                activity.runOnUiThread(object : Runnable {
                    override fun run() {
                        try {
                            var position = intent.getIntExtra(FuguAppConstant.POSITION, 0)
                            val muid = intent.getStringExtra(FuguAppConstant.MESSAGE_UNIQUE_ID)
                            if (messageList[position].muid == muid) {
                                checkHolderAndUpdateProgress(messageList[position], position)
                            } else {
                                for (i in messageList.size downTo 1) {
                                    if (messageList[i].uuid == muid) {
                                        position = i
                                        checkHolderAndUpdateProgress(messageList[i], position)
                                        break
                                    }
                                }
                            }
                        } catch (e: Exception) {

                        }

                    }

                    //check holder type and update progress accordingly
                    private fun checkHolderAndUpdateProgress(currentOrderItem: Message, position: Int) {
                        CommonData.updateDownloadMap(intent.getStringExtra(FuguAppConstant.MESSAGE_UNIQUE_ID), position)

                        currentOrderItem.currentprogress = intent.getIntExtra(FuguAppConstant.PROGRESS, 0)
                        currentOrderItem.uploadStatus = intent.getIntExtra(FuguAppConstant.STATUS_UPLOAD, 1)
                        //setThreadHolderVideoProgress(currentOrderItem,0)
                        when {
                            recyclerView.findViewHolderForAdapterPosition(position) is OtherImageMessageViewHolder -> setOtherHolderImageProgress(currentOrderItem, position)
                            recyclerView.findViewHolderForAdapterPosition(position) is OtherFileMessageViewHolder -> setOtherHolderFileProgress(currentOrderItem, position)
                            recyclerView.findViewHolderForAdapterPosition(position) is SelfImageMessageViewHolder -> setSelfHolderImageProgress(currentOrderItem, position)
                            recyclerView.findViewHolderForAdapterPosition(position) is SelfFileMessageViewHolder -> setSelfHolderFileProgress(currentOrderItem, position)
                            recyclerView.findViewHolderForAdapterPosition(position) is SelfVideoMessageViewHolder -> setSelfHolderVideoProgress(currentOrderItem, position)
                            recyclerView.findViewHolderForAdapterPosition(position) is OtherVideoMessageViewHolder -> setOtherHolderVideoProgress(currentOrderItem, position)
                            recyclerView.findViewHolderForAdapterPosition(position) is ThreadImageMessageViewHolder -> setThreadHolderImageProgress(currentOrderItem, position)
                            recyclerView.findViewHolderForAdapterPosition(position) is ThreadVideoMessageViewHolder -> setThreadHolderVideoProgress(currentOrderItem, position)
                            recyclerView.findViewHolderForAdapterPosition(position) is ThreadFileMessageViewHolder -> setThreadHolderFileProgress(currentOrderItem, position)
                        }
                    }

                    private fun setThreadHolderFileProgress(currentOrderItem: Message, position: Int) {
                        val threadFileMessageViewHolder: ThreadFileMessageViewHolder = recyclerView.findViewHolderForAdapterPosition(position) as ThreadFileMessageViewHolder
                        threadFileMessageViewHolder.circleProgress.visibility = View.VISIBLE
                        threadFileMessageViewHolder.circleProgress.progress = currentOrderItem.currentprogress.toFloat()
                        threadFileMessageViewHolder.circleProgress.textSize = 0f
                        threadFileMessageViewHolder.ivFilePlay.visibility = View.GONE
                        recyclerView.adapter?.notifyItemChanged(position)
                    }

                    private fun setThreadHolderVideoProgress(currentOrderItem: Message, position: Int) {
                        val threadVideoMessageViewHolder: ThreadVideoMessageViewHolder = recyclerView.findViewHolderForAdapterPosition(position) as ThreadVideoMessageViewHolder
                        threadVideoMessageViewHolder.circleProgress.visibility = View.VISIBLE
                        threadVideoMessageViewHolder.ivCrossCancel.visibility = View.VISIBLE
                        threadVideoMessageViewHolder.llDownload.visibility = View.GONE
                        threadVideoMessageViewHolder.circleProgress.progress = currentOrderItem.currentprogress.toFloat()
                        threadVideoMessageViewHolder.circleProgress.textSize = 0f
                        currentOrderItem.downloadStatus = intent.getIntExtra(FuguAppConstant.STATUS_UPLOAD, 1)
                        recyclerView.adapter?.notifyItemChanged(position)
                    }

                    private fun setThreadHolderImageProgress(currentOrderItem: Message, position: Int) {
                        //val selfImageMessageViewHolder: SelfImageMessageViewHolder = recyclerView.findViewHolderForAdapterPosition(position) as SelfImageMessageViewHolder
                        //currentOrderItem.currentprogress = intent.getIntExtra(FuguAppConstant.PROGRESS, 0)
                        //selfImageMessageViewHolder.circleProgress.setVisibility(View.VISIBLE)
                        //selfImageMessageViewHolder.circleProgress.setProgress(currentOrderItem.currentprogress.toFloat())
                        //selfImageMessageViewHolder.circleProgress.setTextSize(0f)
                        currentOrderItem.downloadStatus = intent.getIntExtra(FuguAppConstant.STATUS_UPLOAD, 1)
                        recyclerView.adapter?.notifyItemChanged(position)

                    }

                    private fun setOtherHolderImageProgress(currentOrderItem: Message, position: Int) {
                        val otherImageMessageViewHolder: OtherImageMessageViewHolder = recyclerView.findViewHolderForAdapterPosition(position) as OtherImageMessageViewHolder
//                        otherImageMessageViewHolder.circleProgress.setVisibility(View.VISIBLE)
//                        otherImageMessageViewHolder.circleProgress.setProgress(currentOrderItem.currentprogress.toFloat())
//                        otherImageMessageViewHolder.circleProgress.setTextSize(0f)
                        currentOrderItem.downloadStatus = intent.getIntExtra(FuguAppConstant.STATUS_UPLOAD, 1)
                        recyclerView.adapter?.notifyItemChanged(position)
                    }

                    private fun setSelfHolderImageProgress(currentOrderItem: Message, position: Int) {
                        val selfImageMessageViewHolder: SelfImageMessageViewHolder = recyclerView.findViewHolderForAdapterPosition(position) as SelfImageMessageViewHolder
                        currentOrderItem.currentprogress = intent.getIntExtra(FuguAppConstant.PROGRESS, 0)
                        selfImageMessageViewHolder.circleProgress.visibility = View.VISIBLE
                        selfImageMessageViewHolder.circleProgress.progress = currentOrderItem.currentprogress.toFloat()
                        selfImageMessageViewHolder.circleProgress.textSize = 0f
                        currentOrderItem.downloadStatus = intent.getIntExtra(FuguAppConstant.STATUS_UPLOAD, 1)
                        recyclerView.adapter?.notifyItemChanged(position)
                    }

                    private fun setOtherHolderVideoProgress(currentOrderItem: Message, position: Int) {
                        val otherImageMessageViewHolder: OtherVideoMessageViewHolder = recyclerView.findViewHolderForAdapterPosition(position) as OtherVideoMessageViewHolder
                        otherImageMessageViewHolder.circleProgress.visibility = View.VISIBLE
                        otherImageMessageViewHolder.ivCrossCancel.visibility = View.VISIBLE
                        otherImageMessageViewHolder.llDownload.visibility = View.GONE
                        otherImageMessageViewHolder.circleProgress.progress = currentOrderItem.currentprogress.toFloat()
                        otherImageMessageViewHolder.circleProgress.textSize = 0f
                        currentOrderItem.downloadStatus = intent.getIntExtra(FuguAppConstant.STATUS_UPLOAD, 1)
                        recyclerView.adapter?.notifyItemChanged(position)
                    }

                    private fun setSelfHolderVideoProgress(currentOrderItem: Message, position: Int) {
                        val selfImageMessageViewHolder: SelfVideoMessageViewHolder = recyclerView.findViewHolderForAdapterPosition(position) as SelfVideoMessageViewHolder
                        currentOrderItem.currentprogress = intent.getIntExtra(FuguAppConstant.PROGRESS, 0)
                        selfImageMessageViewHolder.circleProgress.visibility = View.VISIBLE
                        selfImageMessageViewHolder.circleProgress.progress = currentOrderItem.currentprogress.toFloat()
                        selfImageMessageViewHolder.circleProgress.textSize = 0f
                        currentOrderItem.downloadStatus = intent.getIntExtra(FuguAppConstant.STATUS_UPLOAD, 1)
                        recyclerView.adapter?.notifyItemChanged(position)
                    }

                    private fun setOtherHolderFileProgress(currentOrderItem: Message, position: Int) {
                        val otherFileMessageViewHolder: OtherFileMessageViewHolder = recyclerView.findViewHolderForAdapterPosition(position) as OtherFileMessageViewHolder
                        otherFileMessageViewHolder.circleProgress.visibility = View.VISIBLE
                        otherFileMessageViewHolder.circleProgress.progress = currentOrderItem.currentprogress.toFloat()
                        otherFileMessageViewHolder.circleProgress.textSize = 0f
                        otherFileMessageViewHolder.ivFilePlay.visibility = View.GONE
                        recyclerView.adapter?.notifyItemChanged(position)
                    }

                    private fun setSelfHolderFileProgress(currentOrderItem: Message, position: Int) {
                        val selfFileMessageViewHolder: SelfFileMessageViewHolder = recyclerView.findViewHolderForAdapterPosition(position) as SelfFileMessageViewHolder
                        currentOrderItem.currentprogress = intent.getIntExtra(FuguAppConstant.PROGRESS, 0)
                        selfFileMessageViewHolder.circleProgress.visibility = View.VISIBLE
                        selfFileMessageViewHolder.ivFilePlay.visibility = View.GONE
                        selfFileMessageViewHolder.circleProgress.progress = currentOrderItem.currentprogress.toFloat()
                        selfFileMessageViewHolder.circleProgress.textSize = 0f
                        currentOrderItem.downloadStatus = intent.getIntExtra(FuguAppConstant.STATUS_UPLOAD, 1)
                        recyclerView.adapter?.notifyItemChanged(position)
                    }
                })
            }
        }
    }

    interface OnRetryListener {
        fun onRetry(message: String, file: String, messageIndex: Int, messageType: Int, fileDetails: FuguFileDetails?, uuid: String, thumbnailUrl: String, imageUrl: String, imageUrl100x100: String, url: String, height: Int, width: Int)
    }

    fun setOnRetryListener(OnRetryListener: OnRetryListener) {
        mOnRetry = OnRetryListener
    }


    private fun showErrorMessage(context: Context, message: String) {
        val alert = AlertDialog.Builder(context)
        alert.setMessage(message)
        alert.setPositiveButton("Ok", null)
        alert.setCancelable(false)
        alert.show()
    }

    fun stopMusic() {
        if (mediaPlayer != null && isPlaying) {
            isPlaying = false
            mediaPlayer?.stop()
            mediaPlayer = null
            for (i in messageList.indices) {
                if (messageList.get(i).rowType == FILE_MESSGAE_SELF || messageList.get(i).rowType == FILE_MESSGAE_OTHER)
                    if (messageList.get(i).isAudioPlaying) {
                        messageList.get(i).isAudioPlaying = false
                        recyclerView.adapter?.notifyItemChanged(i)
                    }
            }
        }
    }

    object FileOpen {

        @Throws(IOException::class)
        internal fun openFile(context: Context, url: File) {
            try {
                val uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", url)
                val openFileIntent = Intent(Intent.ACTION_VIEW)
                val filePath = url.absolutePath
                val extension = filePath.substring(filePath.lastIndexOf(".") + 1)
                openFileIntent.setDataAndType(uri, FuguMimeUtils.guessMimeTypeFromExtension(extension.toLowerCase().replace(".", ""))
                        ?: "*/*")
                openFileIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                openFileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                openFileIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                try {
                    context.startActivity(openFileIntent)
                } catch (e: ActivityNotFoundException) {
                    showErrorMessage(context, "You may not have a proper app for viewing this content.")
                }

            } catch (e: Exception) {
                showErrorMessage(context, "You may not have a proper app for viewing this content.")
            }
        }

        private fun showErrorMessage(context: Context, message: String) {
            val alert = AlertDialog.Builder(context)
            alert.setMessage(message)
            alert.setPositiveButton("Ok", null)
            alert.setCancelable(false)
            alert.show()
        }
    }

    class SelfTextMessageViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var tvTime: AppCompatTextView = itemView.findViewById(R.id.tvTime)
        var tvMessage: AppCompatTextView = itemView.findViewById(R.id.tvMsg)
        var rlMessage: RelativeLayout = itemView.findViewById(R.id.rlMessage)
        var ivMessageState: AppCompatImageView = itemView.findViewById(R.id.ivMessageState)
        val tvReplies: TextView = itemView.findViewById(R.id.tvReplies)
        val rlRoot: RelativeLayout = itemView.findViewById(R.id.rlRoot)
        val llEmojis: LinearLayout = itemView.findViewById(R.id.llEmojis)
        val llMessage: LinearLayout = itemView.findViewById(R.id.llMessage)
        val emojiLayoutList: ArrayList<CardView> = ArrayList()
        val emojiTextList: ArrayList<TextView> = ArrayList()
        val emojiCountList: ArrayList<TextView> = ArrayList()
        val llRetry: LinearLayout = itemView.findViewById(R.id.llRetry)
        val llMainMessage: LinearLayoutCompat = itemView.findViewById(R.id.llMainMessage)
        val tvTryAgain: TextView = itemView.findViewById(R.id.tvTryAgain)
        val tvCancel: TextView = itemView.findViewById(R.id.tvCancel)
        val ivStar: AppCompatImageView = itemView.findViewById(R.id.ivStar)
        val llReplies: LinearLayout = itemView.findViewById(R.id.llReplies)
        val llReplyList: ArrayList<LinearLayout> = ArrayList()
        val tvReplyTextList: ArrayList<AppCompatTextView> = ArrayList()
        val ivReplyImageList: ArrayList<AppCompatImageView> = ArrayList()

        init {
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl2))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl3))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl4))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl5))

            emojiTextList.add(itemView.findViewById(R.id.tvEmoji))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji2))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji3))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji4))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji5))

            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount2))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount3))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount4))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount5))

            llReplyList.add(itemView.findViewById(R.id.llReplyOne))
            llReplyList.add(itemView.findViewById(R.id.llReplyTwo))
            llReplyList.add(itemView.findViewById(R.id.llReplyThree))

            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyOne))
            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyTwo))
            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyThree))

            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyOne))
            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyTwo))
            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyThree))
        }
    }

    class OtherTextMessageViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var tvTime: AppCompatTextView = itemView.findViewById(R.id.tvTime)
        var tvMessage: AppCompatTextView = itemView.findViewById(R.id.tvMsg)
        var rlMessage: RelativeLayout = itemView.findViewById(R.id.rlMessage)
        var tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        val tvReplies: TextView = itemView.findViewById(R.id.tvReplies)
        val rlRoot: RelativeLayout = itemView.findViewById(R.id.rlRoot)
        val llMessage: LinearLayout = itemView.findViewById(R.id.llMessage)
        val llEmojis: LinearLayout = itemView.findViewById(R.id.llEmojis)
        val emojiLayoutList: ArrayList<CardView> = ArrayList()
        val emojiTextList: ArrayList<TextView> = ArrayList()
        val emojiCountList: ArrayList<TextView> = ArrayList()
        val llMainMessage: LinearLayoutCompat = itemView.findViewById(R.id.llMainMessage)
        val ivStar: AppCompatImageView = itemView.findViewById(R.id.ivStar)
        val llReplies: LinearLayout = itemView.findViewById(R.id.llReplies)
        val llReplyList: ArrayList<LinearLayout> = ArrayList()
        val tvReplyTextList: ArrayList<AppCompatTextView> = ArrayList()
        val ivReplyImageList: ArrayList<AppCompatImageView> = ArrayList()

        init {
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl2))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl3))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl4))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl5))

            emojiTextList.add(itemView.findViewById(R.id.tvEmoji))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji2))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji3))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji4))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji5))

            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount2))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount3))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount4))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount5))

            llReplyList.add(itemView.findViewById(R.id.llReplyOne))
            llReplyList.add(itemView.findViewById(R.id.llReplyTwo))
            llReplyList.add(itemView.findViewById(R.id.llReplyThree))

            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyOne))
            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyTwo))
            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyThree))

            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyOne))
            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyTwo))
            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyThree))
        }
    }

    class SelfImageMessageViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {


        var tvImageTime: AppCompatTextView = itemView.findViewById(R.id.tvImageTime)
        var ivImageMessage: AppCompatImageView = itemView.findViewById(R.id.ivImageMsg)
        var rlImageMessage: RelativeLayout = itemView.findViewById(R.id.rlImageMessage)
        var llImageMessage: LinearLayoutCompat = itemView.findViewById(R.id.llImageMessage)
        var ivMessageState: AppCompatImageView = itemView.findViewById(R.id.ivMessageState)
        var tvImagewithMessage: AppCompatTextView = itemView.findViewById(R.id.tvImgWithText)
        val tvReplies: TextView = itemView.findViewById(R.id.tvReplies)
        val llRoot: LinearLayoutCompat = itemView.findViewById(R.id.llRoot)
        val llEmojis: LinearLayout = itemView.findViewById(R.id.llEmojis)
        val emojiLayoutList: ArrayList<CardView> = ArrayList()
        val emojiTextList: ArrayList<TextView> = ArrayList()
        val emojiCountList: ArrayList<TextView> = ArrayList()
        val llForward: LinearLayout = itemView.findViewById(R.id.llForward)
        val circleProgress: DonutProgress = itemView.findViewById(R.id.circle_progress)
        val btnRetry: AppCompatButton = itemView.findViewById(R.id.btnRetry)
        val ivStar: AppCompatImageView = itemView.findViewById(R.id.ivStar)
        val llDownloadImage: LinearLayout = itemView.findViewById(R.id.llDownloadImage)
        var pbDownloading: ProgressBar = itemView.findViewById(R.id.pbDownloading)
        var tvImageSize: AppCompatTextView = itemView.findViewById(R.id.tvImageSize)
        val llReplies: LinearLayout = itemView.findViewById(R.id.llReplies)
        val llReplyList: ArrayList<LinearLayout> = ArrayList()
        val tvReplyTextList: ArrayList<AppCompatTextView> = ArrayList()
        val ivReplyImageList: ArrayList<AppCompatImageView> = ArrayList()

        init {
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl2))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl3))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl4))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl5))

            emojiTextList.add(itemView.findViewById(R.id.tvEmoji))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji2))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji3))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji4))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji5))

            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount2))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount3))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount4))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount5))

            llReplyList.add(itemView.findViewById(R.id.llReplyOne))
            llReplyList.add(itemView.findViewById(R.id.llReplyTwo))
            llReplyList.add(itemView.findViewById(R.id.llReplyThree))

            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyOne))
            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyTwo))
            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyThree))

            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyOne))
            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyTwo))
            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyThree))

        }
    }

    class OtherImageMessageViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var tvImageTime: AppCompatTextView = itemView.findViewById(R.id.tvImageTime)
        var ivImageMessage: AppCompatImageView = itemView.findViewById(R.id.ivImageMsg)
        var tvImagewithMessage: AppCompatTextView = itemView.findViewById(R.id.tvImgWithText)
        var rlImageMessage: RelativeLayout = itemView.findViewById(R.id.rlImageMessage)
        var tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        var llImageMessage: LinearLayoutCompat = itemView.findViewById(R.id.llImageMessage)
        val tvReplies: TextView = itemView.findViewById(R.id.tvReplies)
        val llRoot: LinearLayoutCompat = itemView.findViewById(R.id.llRoot)
        val llEmojis: LinearLayout = itemView.findViewById(R.id.llEmojis)
        val emojiLayoutList: ArrayList<CardView> = ArrayList()
        val emojiTextList: ArrayList<TextView> = ArrayList()
        val emojiCountList: ArrayList<TextView> = ArrayList()
        val llForward: LinearLayout = itemView.findViewById(R.id.llForward)

        //        val circleProgress: DonutProgress = itemView.findViewById(R.id.circle_progress)
        val ivStar: AppCompatImageView = itemView.findViewById(R.id.ivStar)
        val llDownloadImage: LinearLayout = itemView.findViewById(R.id.llDownloadImage)
        val llMessage: LinearLayout = itemView.findViewById(R.id.llMessage)
        var pbDownloading: ProgressBar = itemView.findViewById(R.id.pbDownloading)
        var tvImageSize: AppCompatTextView = itemView.findViewById(R.id.tvImageSize)
        val llReplies: LinearLayout = itemView.findViewById(R.id.llReplies)
        val llReplyList: ArrayList<LinearLayout> = ArrayList()
        val tvReplyTextList: ArrayList<AppCompatTextView> = ArrayList()
        val ivReplyImageList: ArrayList<AppCompatImageView> = ArrayList()

        init {
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl2))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl3))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl4))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl5))

            emojiTextList.add(itemView.findViewById(R.id.tvEmoji))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji2))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji3))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji4))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji5))

            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount2))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount3))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount4))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount5))

            llReplyList.add(itemView.findViewById(R.id.llReplyOne))
            llReplyList.add(itemView.findViewById(R.id.llReplyTwo))
            llReplyList.add(itemView.findViewById(R.id.llReplyThree))

            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyOne))
            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyTwo))
            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyThree))

            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyOne))
            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyTwo))
            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyThree))
        }
    }


    class SelfVideoMessageViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {


        var tvImageTime: AppCompatTextView = itemView.findViewById(R.id.tvImageTime)
        var ivImageMessage: AppCompatImageView = itemView.findViewById(R.id.ivImageMsg)
        var rlImageMessage: RelativeLayout = itemView.findViewById(R.id.rlImageMessage)
        var llImageMessage: LinearLayoutCompat = itemView.findViewById(R.id.llImageMessage)
        var ivMessageState: AppCompatImageView = itemView.findViewById(R.id.ivMessageState)
        var tvImagewithMessage: AppCompatTextView = itemView.findViewById(R.id.tvImgWithText)
        val ivCrossCancel: AppCompatImageView = itemView.findViewById(R.id.ivCrossCancel)
        val tvReplies: TextView = itemView.findViewById(R.id.tvReplies)
        val llRoot: LinearLayoutCompat = itemView.findViewById(R.id.llRoot)
        val llEmojis: LinearLayout = itemView.findViewById(R.id.llEmojis)
        val emojiLayoutList: ArrayList<CardView> = ArrayList()
        val emojiTextList: ArrayList<TextView> = ArrayList()
        val emojiCountList: ArrayList<TextView> = ArrayList()
        val llForward: LinearLayout = itemView.findViewById(R.id.llForward)
        val circleProgress: DonutProgress = itemView.findViewById(R.id.circle_progress)
        val btnRetry: AppCompatButton = itemView.findViewById(R.id.btnRetry)
        val ivPlay: ImageView = itemView.findViewById(R.id.ivPlay)
        val llDownload: LinearLayout = itemView.findViewById(R.id.llDownload)
        val tvFileSize: TextView = itemView.findViewById(R.id.tvFileSize)
        val ivStar: AppCompatImageView = itemView.findViewById(R.id.ivStar)
        val rlDownloading: RelativeLayout = itemView.findViewById(R.id.rl_downloading)

        val llReplies: LinearLayout = itemView.findViewById(R.id.llReplies)
        val llReplyList: ArrayList<LinearLayout> = ArrayList()
        val tvReplyTextList: ArrayList<AppCompatTextView> = ArrayList()
        val ivReplyImageList: ArrayList<AppCompatImageView> = ArrayList()

        init {
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl2))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl3))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl4))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl5))

            emojiTextList.add(itemView.findViewById(R.id.tvEmoji))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji2))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji3))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji4))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji5))

            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount2))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount3))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount4))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount5))
            llReplyList.add(itemView.findViewById(R.id.llReplyOne))
            llReplyList.add(itemView.findViewById(R.id.llReplyTwo))
            llReplyList.add(itemView.findViewById(R.id.llReplyThree))

            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyOne))
            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyTwo))
            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyThree))

            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyOne))
            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyTwo))
            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyThree))

        }
    }

    class OtherVideoMessageViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var tvImageTime: AppCompatTextView = itemView.findViewById(R.id.tvImageTime)
        var ivImageMessage: AppCompatImageView = itemView.findViewById(R.id.ivImageMsg)
        var tvImagewithMessage: AppCompatTextView = itemView.findViewById(R.id.tvImgWithText)
        var rlImageMessage: RelativeLayout = itemView.findViewById(R.id.rlImageMessage)
        var tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        var llImageMessage: LinearLayoutCompat = itemView.findViewById(R.id.llImageMessage)
        val tvReplies: TextView = itemView.findViewById(R.id.tvReplies)
        val llRoot: LinearLayoutCompat = itemView.findViewById(R.id.llRoot)
        val llEmojis: LinearLayout = itemView.findViewById(R.id.llEmojis)
        val emojiLayoutList: ArrayList<CardView> = ArrayList()
        val emojiTextList: ArrayList<TextView> = ArrayList()
        val emojiCountList: ArrayList<TextView> = ArrayList()
        val llForward: LinearLayout = itemView.findViewById(R.id.llForward)
        val circleProgress: DonutProgress = itemView.findViewById(R.id.circle_progress)
        val ivDownload: ImageView = itemView.findViewById(R.id.ivDownload)
        val llDownload: LinearLayout = itemView.findViewById(R.id.llDownload)
        val llMessage: LinearLayout = itemView.findViewById(R.id.llMessage)
        val ivPlay: ImageView = itemView.findViewById(R.id.ivPlay)
        val tvFileSize: TextView = itemView.findViewById(R.id.tvFileSize)
        val ivStar: AppCompatImageView = itemView.findViewById(R.id.ivStar)
        val ivCrossCancel: AppCompatImageView = itemView.findViewById(R.id.ivCrossCancel)
        val rlDownloading: RelativeLayout = itemView.findViewById(R.id.rl_downloading)
        val llReplies: LinearLayout = itemView.findViewById(R.id.llReplies)
        val llReplyList: ArrayList<LinearLayout> = ArrayList()
        val tvReplyTextList: ArrayList<AppCompatTextView> = ArrayList()
        val ivReplyImageList: ArrayList<AppCompatImageView> = ArrayList()

        init {
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl2))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl3))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl4))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl5))

            emojiTextList.add(itemView.findViewById(R.id.tvEmoji))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji2))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji3))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji4))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji5))

            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount2))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount3))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount4))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount5))

            llReplyList.add(itemView.findViewById(R.id.llReplyOne))
            llReplyList.add(itemView.findViewById(R.id.llReplyTwo))
            llReplyList.add(itemView.findViewById(R.id.llReplyThree))

            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyOne))
            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyTwo))
            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyThree))

            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyOne))
            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyTwo))
            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyThree))
        }
    }

    class SelfFileMessageViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var tvFileTime: AppCompatTextView = itemView.findViewById(R.id.tvFileTime)
        var tvFileName: AppCompatTextView = itemView.findViewById(R.id.tvFileName)
        var tvFileSize: AppCompatTextView = itemView.findViewById(R.id.tvFileSize)
        var tvFileExtension: AppCompatTextView = itemView.findViewById(R.id.tvFileExtension)
        var tvFileExt: AppCompatTextView = itemView.findViewById(R.id.tvFileExt)
        var ivFileImage: AppCompatImageView = itemView.findViewById(R.id.ivFileImage)
        var llMessage: LinearLayoutCompat = itemView.findViewById(R.id.llMessage)
        var ivMessageState: AppCompatImageView = itemView.findViewById(R.id.ivMessageState)
        val tvReplies: TextView = itemView.findViewById(R.id.tvReplies)
        val llRoot: LinearLayoutCompat = itemView.findViewById(R.id.llRoot)
        val ivFilePlay: AppCompatImageView = itemView.findViewById(R.id.ivFilePlay)
        val llEmojis: LinearLayout = itemView.findViewById(R.id.llEmojis)
        val emojiLayoutList: ArrayList<CardView> = ArrayList()
        val emojiTextList: ArrayList<TextView> = ArrayList()
        val emojiCountList: ArrayList<TextView> = ArrayList()
        val llForward: LinearLayout = itemView.findViewById(R.id.llForward)
        val circleProgress: DonutProgress = itemView.findViewById(R.id.circle_progress)
        val ivFileDownload: AppCompatImageView = itemView.findViewById(R.id.ivFileDownload)
        val ivFileUpload: AppCompatImageView = itemView.findViewById(R.id.ivFileUpload)
        val llFile: LinearLayoutCompat = itemView.findViewById(R.id.llFile)
        val llMainMessage: LinearLayout = itemView.findViewById(R.id.llMainMessage)
        val ivStar: AppCompatImageView = itemView.findViewById(R.id.ivStar)
        val llReplies: LinearLayout = itemView.findViewById(R.id.llReplies)
        val llReplyList: ArrayList<LinearLayout> = ArrayList()
        val tvReplyTextList: ArrayList<AppCompatTextView> = ArrayList()
        val ivReplyImageList: ArrayList<AppCompatImageView> = ArrayList()

        init {
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl2))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl3))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl4))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl5))

            emojiTextList.add(itemView.findViewById(R.id.tvEmoji))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji2))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji3))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji4))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji5))

            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount2))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount3))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount4))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount5))
            llReplyList.add(itemView.findViewById(R.id.llReplyOne))
            llReplyList.add(itemView.findViewById(R.id.llReplyTwo))
            llReplyList.add(itemView.findViewById(R.id.llReplyThree))

            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyOne))
            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyTwo))
            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyThree))

            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyOne))
            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyTwo))
            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyThree))

        }
    }

    class OtherFileMessageViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var tvFileTime: AppCompatTextView = itemView.findViewById(R.id.tvFileTime)
        var tvFileName: AppCompatTextView = itemView.findViewById(R.id.tvFileName)
        var tvFileSize: AppCompatTextView = itemView.findViewById(R.id.tvFileSize)
        var tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        var tvFileExtension: AppCompatTextView = itemView.findViewById(R.id.tvFileExtension)
        var tvFileExt: AppCompatTextView = itemView.findViewById(R.id.tvFileExt)
        var ivFileImage: AppCompatImageView = itemView.findViewById(R.id.ivFileImage)
        var llMessage: LinearLayoutCompat = itemView.findViewById(R.id.llMessage)
        val tvReplies: TextView = itemView.findViewById(R.id.tvReplies)
        val llRoot: LinearLayoutCompat = itemView.findViewById(R.id.llRoot)
        val ivFilePlay: AppCompatImageView = itemView.findViewById(R.id.ivFilePlay)
        val llEmojis: LinearLayout = itemView.findViewById(R.id.llEmojis)
        val emojiLayoutList: ArrayList<CardView> = ArrayList()
        val emojiTextList: ArrayList<TextView> = ArrayList()
        val emojiCountList: ArrayList<TextView> = ArrayList()
        val llForward: LinearLayout = itemView.findViewById(R.id.llForward)
        val circleProgress: DonutProgress = itemView.findViewById(R.id.circle_progress)
        val ivFileDownload: AppCompatImageView = itemView.findViewById(R.id.ivFileDownload)
        val llFile: LinearLayoutCompat = itemView.findViewById(R.id.llFile)
        val llMainMessage: LinearLayout = itemView.findViewById(R.id.llMainMessage)
        val ivStar: AppCompatImageView = itemView.findViewById(R.id.ivStar)
        val llReplies: LinearLayout = itemView.findViewById(R.id.llReplies)
        val llReplyList: ArrayList<LinearLayout> = ArrayList()
        val tvReplyTextList: ArrayList<AppCompatTextView> = ArrayList()
        val ivReplyImageList: ArrayList<AppCompatImageView> = ArrayList()

        init {
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl2))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl3))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl4))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl5))

            emojiTextList.add(itemView.findViewById(R.id.tvEmoji))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji2))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji3))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji4))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji5))

            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount2))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount3))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount4))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount5))

            llReplyList.add(itemView.findViewById(R.id.llReplyOne))
            llReplyList.add(itemView.findViewById(R.id.llReplyTwo))
            llReplyList.add(itemView.findViewById(R.id.llReplyThree))

            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyOne))
            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyTwo))
            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyThree))

            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyOne))
            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyTwo))
            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyThree))
        }
    }

    class DateViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)

    }

    class UnreadViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
    }

    class PublicNoteViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)

    }

    class OtherDeletedMessageViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvMsg)
        val tvReplies: TextView = itemView.findViewById(R.id.tvReplies)
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val llMessageBg: LinearLayout = itemView.findViewById(R.id.llMessageBg)
        val llRoot: LinearLayout = itemView.findViewById(R.id.llRoot)
        val llReplies: LinearLayout = itemView.findViewById(R.id.llReplies)
        val llReplyList: ArrayList<LinearLayout> = ArrayList()
        val tvReplyTextList: ArrayList<AppCompatTextView> = ArrayList()
        val ivReplyImageList: ArrayList<AppCompatImageView> = ArrayList()

        init {

            llReplyList.add(itemView.findViewById(R.id.llReplyOne))
            llReplyList.add(itemView.findViewById(R.id.llReplyTwo))
            llReplyList.add(itemView.findViewById(R.id.llReplyThree))

            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyOne))
            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyTwo))
            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyThree))

            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyOne))
            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyTwo))
            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyThree))
        }
    }

    class SelfDeletedMessageViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvMsg)
        val tvReplies: TextView = itemView.findViewById(R.id.tvReplies)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val llMessageBg: LinearLayout = itemView.findViewById(R.id.llMessageBg)
        val llRoot: LinearLayout = itemView.findViewById(R.id.llRoot)
        val llReplies: LinearLayout = itemView.findViewById(R.id.llReplies)
        val llReplyList: ArrayList<LinearLayout> = ArrayList()
        val tvReplyTextList: ArrayList<AppCompatTextView> = ArrayList()
        val ivReplyImageList: ArrayList<AppCompatImageView> = ArrayList()

        init {
            llReplyList.add(itemView.findViewById(R.id.llReplyOne))
            llReplyList.add(itemView.findViewById(R.id.llReplyTwo))
            llReplyList.add(itemView.findViewById(R.id.llReplyThree))

            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyOne))
            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyTwo))
            tvReplyTextList.add(itemView.findViewById(R.id.tvReplyThree))

            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyOne))
            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyTwo))
            ivReplyImageList.add(itemView.findViewById(R.id.ivReplyThree))
        }
    }

    class SelfVideoCallMessage(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvMsg)
        val tvTime: AppCompatTextView = itemView.findViewById(R.id.tvTime)
        val callAgain: TextView = itemView.findViewById(R.id.callAgain)
        val llMessage: LinearLayout = itemView.findViewById(R.id.llMessage)
        val rlRoot: RelativeLayout = itemView.findViewById(R.id.rlRoot)
        val ivCallIcon: ImageView = itemView.findViewById(R.id.ivCallIcon)
        val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
    }

    class OtherVideoCallMessage(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvMsg)
        val tvTime: AppCompatTextView = itemView.findViewById(R.id.tvTime)
        val callAgain: TextView = itemView.findViewById(R.id.callAgain)
        val llMessage: LinearLayout = itemView.findViewById(R.id.llMessage)
        val rlRoot: RelativeLayout = itemView.findViewById(R.id.rlRoot)
        val ivCallIcon: ImageView = itemView.findViewById(R.id.ivCallIcon)
        val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
    }

    class SelfPollMessage(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val tvQuestion: AppCompatTextView = itemView.findViewById(R.id.tvQuestion)
        val llRadioGroup: LinearLayout = itemView.findViewById(R.id.llRadioGroup)
        val radioOne: RadioButton = itemView.findViewById(R.id.radioOne)
        val radioTwo: RadioButton = itemView.findViewById(R.id.radioTwo)
        val checkboxGroup: LinearLayout = itemView.findViewById(R.id.checkboxGroup)
        val cbOne: CheckBox = itemView.findViewById(R.id.cbOne)
        val cbTwo: CheckBox = itemView.findViewById(R.id.cbTwo)
        val btnViewMore: TextView = itemView.findViewById(R.id.btnViewMore)
        val llRoot: LinearLayout = itemView.findViewById(R.id.llRoot)
        val llMessage: LinearLayout = itemView.findViewById(R.id.llMessageBg)
        val tvTime: AppCompatTextView = itemView.findViewById(R.id.tvTime)
        val llRadioOne: LinearLayout = itemView.findViewById(R.id.llRadioOne)
        val llRadioTwo: LinearLayout = itemView.findViewById(R.id.llRadioTwo)
        val llCheckBoxOne: LinearLayout = itemView.findViewById(R.id.llCheckBoxOne)
        val llCheckBoxTwo: LinearLayout = itemView.findViewById(R.id.llCheckBoxTwo)
        val cbTwoVotes: AppCompatTextView = itemView.findViewById(R.id.cbTwoVotes)
        val cbOneVotes: AppCompatTextView = itemView.findViewById(R.id.cbOneVotes)
        val radioTwoVotes: AppCompatTextView = itemView.findViewById(R.id.radioTwoVotes)
        val radioOneVotes: AppCompatTextView = itemView.findViewById(R.id.radioOneVotes)
        val tvTotalVotes: AppCompatTextView = itemView.findViewById(R.id.tvTotalVotes)
        val radioOneView: View = itemView.findViewById(R.id.radioOneView)
        val radioTwoView: View = itemView.findViewById(R.id.radioTwoView)
        val cbOneView: View = itemView.findViewById(R.id.cbOneView)
        val cbTwoView: View = itemView.findViewById(R.id.cbTwoView)
        val tvExpiryTime: AppCompatTextView = itemView.findViewById(R.id.tvExpiryTime)
    }

    class OtherPollMessage(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val tvQuestion: AppCompatTextView = itemView.findViewById(R.id.tvQuestion)
        val llRadioGroup: LinearLayout = itemView.findViewById(R.id.llRadioGroup)
        val radioOne: RadioButton = itemView.findViewById(R.id.radioOne)
        val radioTwo: RadioButton = itemView.findViewById(R.id.radioTwo)
        val checkboxGroup: LinearLayout = itemView.findViewById(R.id.checkboxGroup)
        val cbOne: CheckBox = itemView.findViewById(R.id.cbOne)
        val cbTwo: CheckBox = itemView.findViewById(R.id.cbTwo)
        val btnViewMore: AppCompatTextView = itemView.findViewById(R.id.btnViewMore)
        val llRoot: LinearLayout = itemView.findViewById(R.id.llRoot)
        val llMessage: LinearLayout = itemView.findViewById(R.id.llMessageBg)
        val tvTime: AppCompatTextView = itemView.findViewById(R.id.tvTime)
        val llRadioOne: LinearLayout = itemView.findViewById(R.id.llRadioOne)
        val llRadioTwo: LinearLayout = itemView.findViewById(R.id.llRadioTwo)
        val llCheckBoxOne: LinearLayout = itemView.findViewById(R.id.llCheckBoxOne)
        val llCheckBoxTwo: LinearLayout = itemView.findViewById(R.id.llCheckBoxTwo)
        val cbTwoVotes: AppCompatTextView = itemView.findViewById(R.id.cbTwoVotes)
        val cbOneVotes: AppCompatTextView = itemView.findViewById(R.id.cbOneVotes)
        val radioTwoVotes: AppCompatTextView = itemView.findViewById(R.id.radioTwoVotes)
        val radioOneVotes: AppCompatTextView = itemView.findViewById(R.id.radioOneVotes)
        val tvTotalVotes: AppCompatTextView = itemView.findViewById(R.id.tvTotalVotes)
        val radioOneView: View = itemView.findViewById(R.id.radioOneView)
        val radioTwoView: View = itemView.findViewById(R.id.radioTwoView)
        val cbOneView: View = itemView.findViewById(R.id.cbOneView)
        val cbTwoView: View = itemView.findViewById(R.id.cbTwoView)
        val tvExpiryTime: AppCompatTextView = itemView.findViewById(R.id.tvExpiryTime)
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
    }

    class CustomActionViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val tvMessage: AppCompatTextView = itemView.findViewById(R.id.tvMessage)
        val rvCustomActions: androidx.recyclerview.widget.RecyclerView = itemView.findViewById(R.id.rvCustomActions)
        val llRoot: LinearLayout = itemView.findViewById(R.id.llRoot)
        val llMessageBg: LinearLayout = itemView.findViewById(R.id.llMessageBg)
        val tvTime: AppCompatTextView = itemView.findViewById(R.id.tvTime)
    }

    class ThreadTextMessageViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val llCar: LinearLayout = itemView.findViewById(R.id.car)
        val ivUserImage: ImageView = itemView.findViewById(R.id.ivUserImage)
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        val tvUserMessage: AppCompatTextView = itemView.findViewById(R.id.tvUserMessage)
        val ivDeleted: ImageView = itemView.findViewById(R.id.ivDeleted)
        val llEmojis: LinearLayout = itemView.findViewById(R.id.llEmojis)
        val tvTime: AppCompatTextView = itemView.findViewById(R.id.tvTime)
        val tvEdited: TextView = itemView.findViewById(R.id.tvEdited)
        val emojiLayoutList: ArrayList<CardView> = ArrayList()
        val emojiTextList: ArrayList<TextView> = ArrayList()
        val emojiCountList: ArrayList<TextView> = ArrayList()
        val tvReplyCount: TextView = itemView.findViewById(R.id.tvReplyCount)
        val ivStar: AppCompatImageView = itemView.findViewById(R.id.ivStar)
        val icDownArrow: AppCompatImageView = itemView.findViewById(R.id.ic_down_arrow)

        init {
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl2))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl3))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl4))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl5))

            emojiTextList.add(itemView.findViewById(R.id.tvEmoji))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji2))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji3))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji4))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji5))

            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount2))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount3))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount4))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount5))


        }
    }

    class ThreadImageMessageViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val llCar: LinearLayout = itemView.findViewById(R.id.car)
        val ivUserImage: ImageView = itemView.findViewById(R.id.ivUserImage)
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        val llEmojis: LinearLayout = itemView.findViewById(R.id.llEmojis)

        val tvTime: AppCompatTextView = itemView.findViewById(R.id.tvTime)
        val tvEdited: TextView = itemView.findViewById(R.id.tvEdited)
        val emojiLayoutList: ArrayList<CardView> = ArrayList()
        val emojiTextList: ArrayList<TextView> = ArrayList()
        val emojiCountList: ArrayList<TextView> = ArrayList()

        //val ivMsgImage : ImageView = itemView.findViewById(R.id.ivMsgImage)
        val tvImageTime: TextView = itemView.findViewById(R.id.tvImageTime)

        //val ivMessageState : ImageView = itemView.findViewById(R.id.ivImageMessageState)
        val tvUserImageMessage: AppCompatTextView = itemView.findViewById(R.id.tvUserImageMessage)
        val tvReplyCount: TextView = itemView.findViewById(R.id.tvReplyCount)
        val tvUserMessage: TextView = itemView.findViewById(R.id.tvUserMessage)
        val ivStar: AppCompatImageView = itemView.findViewById(R.id.ivStar)
        val llDownloadImage: LinearLayout = itemView.findViewById(R.id.llDownloadImage)
        var pbDownloading: ProgressBar = itemView.findViewById(R.id.pbDownloading)
        var tvImageSize: AppCompatTextView = itemView.findViewById(R.id.tvImageSize)
        var ivImageMessage: AppCompatImageView = itemView.findViewById(R.id.ivImageMsg)
        val ivDeleted: ImageView = itemView.findViewById(R.id.ivDeleted)
        val rlImageMessage: RelativeLayout = itemView.findViewById(R.id.rlImageMessage)
        val icDownArrow: AppCompatImageView = itemView.findViewById(R.id.ic_down_arrow)

        init {
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl2))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl3))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl4))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl5))

            emojiTextList.add(itemView.findViewById(R.id.tvEmoji))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji2))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji3))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji4))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji5))

            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount2))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount3))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount4))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount5))


        }
    }

    class ThreadVideoMessageViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        val llCar: LinearLayout = itemView.findViewById(R.id.car)
        val ivUserImage: ImageView = itemView.findViewById(R.id.ivUserImage)
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        val llEmojis: LinearLayout = itemView.findViewById(R.id.llEmojis)
        val emojiLayoutList: ArrayList<CardView> = ArrayList()
        val emojiTextList: ArrayList<TextView> = ArrayList()
        val emojiCountList: ArrayList<TextView> = ArrayList()
        val ivMsgImage: AppCompatImageView = itemView.findViewById(R.id.ivImageMsg)
        val tvImageTime: TextView = itemView.findViewById(R.id.tvImageTime)

        //val ivMessageState : ImageView = itemView.findViewById(R.id.ivImageMessageState)
        val tvUserImageMessage: AppCompatTextView = itemView.findViewById(R.id.tvUserImageMessage)
        val ivPlay: ImageView = itemView.findViewById(R.id.ivPlay)
        val tvUserMessage: TextView = itemView.findViewById(R.id.tvUserMessage)
        val tvTime: AppCompatTextView = itemView.findViewById(R.id.tvTime)
        val tvReplyCount: TextView = itemView.findViewById(R.id.tvReplyCount)
        val ivStar: AppCompatImageView = itemView.findViewById(R.id.ivStar)
        val ivCrossCancel: AppCompatImageView = itemView.findViewById(R.id.ivCrossCancel)
        val rlDownloading: RelativeLayout = itemView.findViewById(R.id.rl_downloading)
        val llDownload: LinearLayout = itemView.findViewById(R.id.llDownload)
        val circleProgress: DonutProgress = itemView.findViewById(R.id.circle_progress)
        var rlImageMessage: RelativeLayout = itemView.findViewById(R.id.rlImageMessage)
        val tvFileSize: TextView = itemView.findViewById(R.id.tvFileSize)
        val ivDownload: ImageView = itemView.findViewById(R.id.ivDownload)
        val ivDeleted: ImageView = itemView.findViewById(R.id.ivDeleted)
        val icDownArrow: AppCompatImageView = itemView.findViewById(R.id.ic_down_arrow)

        init {
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl2))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl3))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl4))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl5))

            emojiTextList.add(itemView.findViewById(R.id.tvEmoji))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji2))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji3))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji4))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji5))

            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount2))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount3))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount4))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount5))


        }
    }

    class ThreadFileMessageViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val llCar: LinearLayout = itemView.findViewById(R.id.car)
        val ivUserImage: ImageView = itemView.findViewById(R.id.ivUserImage)
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        val tvTime: AppCompatTextView = itemView.findViewById(R.id.tvTime)
        val tvEdited: TextView = itemView.findViewById(R.id.tvEdited)
        val llEmojis: LinearLayout = itemView.findViewById(R.id.llEmojis)
        val emojiLayoutList: ArrayList<CardView> = ArrayList()
        val emojiTextList: ArrayList<TextView> = ArrayList()
        val emojiCountList: ArrayList<TextView> = ArrayList()
        val tvUserMessage: TextView = itemView.findViewById(R.id.tvUserMessage)

        // val llFileRoot: RelativeLayout = itemView.findViewById(R.id.llFileRoot)
        //val ivFileType : ImageView = itemView.findViewById(R.id.ivFileType)
        //val tvFileName : TextView = itemView.findViewById(R.id.tvFileName)
        //val tvFileSize : TextView = itemView.findViewById(R.id.tvFileSize)
        //val tvExtension : TextView = itemView.findViewById(R.id.tvExtension)
        val ivStar: AppCompatImageView = itemView.findViewById(R.id.ivStar)

        //        var tvFileTime: AppCompatTextView = itemView.findViewById(R.id.tvFileTime)
        var tvFileName: AppCompatTextView = itemView.findViewById(R.id.tvFileName)
        var tvFileSize: AppCompatTextView = itemView.findViewById(R.id.tvFileSize)
        var tvFileExtension: AppCompatTextView = itemView.findViewById(R.id.tvFileExtension)
        var tvFileExt: AppCompatTextView = itemView.findViewById(R.id.tvFileExt)
        var ivFileImage: AppCompatImageView = itemView.findViewById(R.id.ivFileImage)
        val ivFilePlay: AppCompatImageView = itemView.findViewById(R.id.ivFilePlay)
        val ivFileDownload: AppCompatImageView = itemView.findViewById(R.id.ivFileDownload)
        val llFile: LinearLayoutCompat = itemView.findViewById(R.id.llFile)
        val circleProgress: DonutProgress = itemView.findViewById(R.id.circle_progress)
        val ivDeleted: ImageView = itemView.findViewById(R.id.ivDeleted)
        val tvReplyCount: TextView = itemView.findViewById(R.id.tvReplyCount)
        val icDownArrow: AppCompatImageView = itemView.findViewById(R.id.ic_down_arrow)

        init {
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl2))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl3))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl4))
            emojiLayoutList.add(itemView.findViewById(R.id.emojiLl5))

            emojiTextList.add(itemView.findViewById(R.id.tvEmoji))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji2))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji3))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji4))
            emojiTextList.add(itemView.findViewById(R.id.tvEmoji5))

            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount2))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount3))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount4))
            emojiCountList.add(itemView.findViewById(R.id.tvEmojiCount5))

        }
    }


    interface SendPollOption {
        fun sendPollOption(jsonObject: JSONObject)
    }

    interface CommentInterface {
        fun onCancelClick(muid: String)
        fun onDoneClick(muid: String, comment: String, pos: Int)
    }

    fun getFromSdcard() {
        imageFiles = ArrayList<ImageItem>()
        listFile = ArrayList()
        Thread {
            kotlin.run {
                try {
                    imageFiles = ArrayList<ImageItem>()
                    val file = File(android.os.Environment.getExternalStorageDirectory(), FuguAppConstant.APP_NAME_SHORT +
                            File.separator + workspaceInfo?.get(currentPos)?.workspaceName?.replace(" ".toRegex(), "")?.replace("'s".toRegex(), "")
                            + File.separator + FuguAppConstant.IMAGE)

                    val privateFiles = File(android.os.Environment.getExternalStorageDirectory(), FuguAppConstant.APP_NAME_SHORT +
                            File.separator + workspaceInfo?.get(currentPos)?.workspaceName?.replace(" ".toRegex(), "")?.replace("'s".toRegex(), "")
                            + File.separator + FuguAppConstant.PRIVATE_IMAGES)


                    if (file.isDirectory) {
                        try {

                            for (fileItem in file.listFiles()) {
                                listFile.add(fileItem)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    if (privateFiles.isDirectory) {
                        try {
                            for (fileItem in privateFiles.listFiles()) {
                                listFile.add(fileItem)
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                    for (i in listFile.indices) {
                        val exifFile = androidx.exifinterface.media.ExifInterface(listFile[i].absolutePath)
                        if (!TextUtils.isEmpty(exifFile.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_MAKE)) && exifFile.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_MAKE).equals((channelId.toString() + muid))) {
                            val muidwithExtension = (listFile[i].absoluteFile).toString().split("_").get((listFile[i].absoluteFile).toString().split("_").size - 1)
                            val muidArray = muidwithExtension.split(".")
                            var muid: String = ""
                            if (muidArray.size > 2) {
                                for (text in 0..muidArray.size - 2) {
                                    if (text == 0) {
                                        muid = muidArray[text]
                                    } else {
                                        muid = muid + "." + muidArray[text]
                                    }
                                }
                            } else {
                                muid = muidArray[0]
                            }

                            imageFiles.add(ImageItem(listFile[i].absolutePath, muid, exifFile.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_MODEL), exifFile.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_DATETIME), messageMap[muid]))
                        }
                    }
                    imageFiles.sortWith(Comparator { one, other -> other.transitionName!!.compareTo(one.transitionName!!) })
                    Log.e("Transition Name", (imageFiles.size - 1).toString())
                    for (i in 0..imageFiles.size - 1) {
                        Log.e("Transition Name", imageFiles[i].transitionName)
                    }
                    Log.e("Transition Name", "==============================")
                    imageFiles.reverse()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    fun getImageFilesList(): ArrayList<ImageItem> {
        return imageFiles
    }

    fun setImageFilesList(imageFiles: ArrayList<ImageItem>) {
        this.imageFiles = imageFiles
    }


    fun prepareExitTransition() {
        if (isChatActivity()) {
            chatActivity?.setExitSharedElementCallback(object : SharedElementCallback() {
                override fun onMapSharedElements(names: MutableList<String>, sharedElements: MutableMap<String, View>) {
                    try {
                        val first = (recyclerView.layoutManager as CustomLinearLayoutManager).findFirstVisibleItemPosition()
                        val last = (recyclerView.layoutManager as CustomLinearLayoutManager).findLastVisibleItemPosition()
                        var currentAdapterPosition = 0
                        for (item in first..last) {
                            if (messageList[item].id.toString().equals(imageFiles[ChatActivity.PagerPosition.currentViewPagerPosition].transitionName)) {
                                currentAdapterPosition = item
                                clickedPos = item
                            }
                        }
                        val selectedViewHolder = recyclerView.findViewHolderForAdapterPosition(currentAdapterPosition)
                        if (selectedViewHolder?.itemView == null) {
                            if (clickedPos != currentAdapterPosition && !isAnimationPlaying) {
                                names.clear()
                                sharedElements.clear()
                                names.add(ViewCompat.getTransitionName(recyclerView.findViewHolderForAdapterPosition(currentAdapterPosition)!!.itemView)!!)
                                sharedElements.put(ViewCompat.getTransitionName(recyclerView.findViewHolderForAdapterPosition(currentAdapterPosition)!!.itemView)!!,
                                        recyclerView.findViewHolderForAdapterPosition(currentAdapterPosition)!!.itemView)
                            }
                        } else if ((mContext as ChatActivity).getState() != MotionEvent.ACTION_MOVE && !isAnimationPlaying) {
                            sharedElements[names.get(0)] = selectedViewHolder.itemView.findViewById(R.id.ivImageMsg)
                        } else {
                            sharedElements[names.get(0)] = selectedViewHolder.itemView.findViewById(R.id.ivImageMsg)
                        }
                        isAnimationPlaying = false
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
        } else {
            fuguInnerChatActivity.setExitSharedElementCallback(object : SharedElementCallback() {
                override fun onMapSharedElements(names: MutableList<String>, sharedElements: MutableMap<String, View>) {
                    try {
                        if (clickedPos != 0) {
                            val first = (recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).findFirstVisibleItemPosition()
                            val last = (recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).findLastVisibleItemPosition()
                            var currentAdapterPosition = 0
                            for (item in first..last) {
                                if (messageList[item].id.toString().equals(imageFiles[FuguInnerChatActivity.currentViewPagerposition].transitionName)) {
                                    currentAdapterPosition = item
                                    clickedPos = item
                                }
                            }
                            val selectedViewHolder = recyclerView.findViewHolderForAdapterPosition(currentAdapterPosition)
                            if (selectedViewHolder?.itemView == null) {
                                if (clickedPos != currentAdapterPosition) {
                                    names.clear()
                                    sharedElements.clear()
                                    names.add(ViewCompat.getTransitionName(recyclerView.findViewHolderForAdapterPosition(currentAdapterPosition)!!.itemView)!!)
                                    sharedElements.put(ViewCompat.getTransitionName(recyclerView.findViewHolderForAdapterPosition(currentAdapterPosition)!!.itemView)!!,
                                            recyclerView.findViewHolderForAdapterPosition(currentAdapterPosition)!!.itemView)
                                }
                            } else {
                                sharedElements[names.get(0)] = selectedViewHolder.itemView.findViewById(R.id.ivImageMsg)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })

        }
    }

    private fun setViewModelInCatch(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int, message: Message, isFromUnreadViewHolder: Boolean) {
        if (holder is OtherTextMessageViewHolder) {
            setTextMessageOther(holder, position, message)
        } else if (holder is SelfTextMessageViewHolder) {
            setTextMessageSelf(holder, position, message)
        } else if (holder is SelfImageMessageViewHolder) {
            setImageMessageSelf(holder, position, message)
        } else if (holder is OtherImageMessageViewHolder) {
            setImageMessageOther(holder, position, message)
        } else if (holder is SelfFileMessageViewHolder) {
            setFileMessageSelf(holder, position, message)
        } else if (holder is OtherFileMessageViewHolder) {
            setFileMessageOther(holder, position, message)
        } else if (holder is PublicNoteViewHolder) {
            setPublicNote(holder, message)
        } else if (holder is SelfDeletedMessageViewHolder) {
            setDeletedMessageSelf(holder, message, position)
        } else if (holder is OtherDeletedMessageViewHolder) {
            setDeletedMessageOther(holder, message, position)
        } else if (holder is SelfVideoMessageViewHolder) {
            setVideoMessageSelf(holder, position, message)
        } else if (holder is OtherVideoMessageViewHolder) {
            setVideoMessageOther(holder, position, message)
        } else if (holder is SelfVideoCallMessage) {
            setVideoCallMessageSelf(holder, position, message)
        } else if (holder is OtherVideoCallMessage) {
            setVideoCallMessageOther(holder, position, message)
        } else if (holder is SelfPollMessage) {
            setPollMessageSelf(holder, position, message)
        } else if (holder is OtherPollMessage) {
            setPollMessageOther(holder, position, message)
        } else if (holder is CustomActionViewHolder) {
            setCustomAction(holder, position, message)
        } else if (holder is ThreadTextMessageViewHolder) {
            setTextMessageThread(holder, position, message)
        } else if (holder is ThreadImageMessageViewHolder) {
            setImageMessageThread(holder, position, message)
        } else if (holder is ThreadFileMessageViewHolder) {
            setFileMessageThread(holder, position, message)
        } else if (holder is ThreadVideoMessageViewHolder) {
            setVideoMessageThread(holder, position, message)
        } else if (holder is UnreadViewHolder && !isFromUnreadViewHolder) {
            setUnreadItem(holder, position, message)
        } else if (holder is DateViewHolder && isFromUnreadViewHolder) {
            setDateMessage(holder, position, message)
        }
    }

    fun getMessages(): ArrayList<Message> {
        return messageList
    }
}
