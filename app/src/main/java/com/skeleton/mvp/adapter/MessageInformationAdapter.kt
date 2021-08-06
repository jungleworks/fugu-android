package com.skeleton.mvp.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.SystemClock
import android.text.*
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.Status
import com.github.lzyzsd.circleprogress.DonutProgress
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.ImageDisplayActivity
import com.skeleton.mvp.activity.MessageInformationActivity
import com.skeleton.mvp.activity.PollDetailsActivity
import com.skeleton.mvp.activity.VideoPlayerActivity
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.IMAGE_MAP
import com.skeleton.mvp.data.db.ChatDatabase
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.model.Image
import com.skeleton.mvp.model.Message
import com.skeleton.mvp.ui.profile.ProfileActivity
import com.skeleton.mvp.util.FormatStringUtil
import com.skeleton.mvp.util.Log
import com.skeleton.mvp.util.ValidationUtil
import com.skeleton.mvp.utils.*
import com.skeleton.mvp.utils.FuguUtils.Companion.getFirstCharInUpperCase
import java.io.File
import java.io.FileOutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageInformationAdapter(isCalculationNeeded: Boolean, messageList: ArrayList<Message>, mContext: Context, workspaceInfo: WorkspacesInfo, currentPos: Int, chanelId: Long,
                                userName: String, userImage: String, userId: Long) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {
    private var messageList = ArrayList<Message>()
    private var mContext: Context
    private var TEXT_MESSGAE_SELF: Int = 0
    private var IMAGE_MESSGAE_SELF: Int = 2
    private var FILE_MESSGAE_SELF: Int = 4
    private var VIDEO_MESSGAE_SELF: Int = 11
    private var POLL_SELF: Int = 15
    private var SEEN_BY_OBJECT: Int = 102
    private var USER_OBJECT: Int = 101
    private var workspaceInfo: WorkspacesInfo? = null
    private var currentPos = 0
    private var channelId: Long = -1L
    private var mLastClickTime: Long = -1L
    private var userId: Long = -1L
    private var userName: String = ""
    private var userImage: String = ""
    private var viewOneHeight = 0
    private var screenHeight = 0
    private var isCalculationNeeded = false
    var displayMetrics = DisplayMetrics()
    private var seenUserAdapter: SeenUserAdapter? = null
    private var seenBy = false
    private var messageText = ""

    init {
        this.isCalculationNeeded = isCalculationNeeded
        this.messageList = messageList
        this.mContext = mContext
        this.workspaceInfo = workspaceInfo
        this.currentPos = currentPos
        this.channelId = chanelId
        this.userId = userId
        this.userName = userName
        this.userImage = userImage
        (mContext as MessageInformationActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenHeight = displayMetrics.heightPixels
        seenUserAdapter = SeenUserAdapter(ArrayList())

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        when (viewType) {
            TEXT_MESSGAE_SELF, IMAGE_MESSGAE_SELF, FILE_MESSGAE_SELF, VIDEO_MESSGAE_SELF, POLL_SELF -> {
                return MessageViewHolder(LayoutInflater.from(mContext)
                        .inflate(R.layout.item_message_object, parent, false))
            }
            SEEN_BY_OBJECT -> {
                return SeenByViewHolder(LayoutInflater.from(mContext)
                        .inflate(R.layout.item_seen_by, parent, false))
            }
            USER_OBJECT -> {
                return UserViewHolder(LayoutInflater.from(mContext)
                        .inflate(R.layout.item_user_seen, parent, false))
            }
            else -> {
                return UserViewHolder(LayoutInflater.from(mContext)
                        .inflate(R.layout.item_text_message_self, parent, false))
            }
        }
    }

    class SeenByViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var cvSeenBy: LinearLayout = itemView.findViewById(R.id.cvSeenBy)
        var llSeenByLayout: LinearLayout = itemView.findViewById(R.id.llSeenByLayout)
        var tvSeenBy: AppCompatTextView = itemView.findViewById(R.id.tvSeenBy)
        var ivStatus: AppCompatImageView = itemView.findViewById(R.id.ivStatus)
        var tvNoOne: AppCompatTextView = itemView.findViewById(R.id.tvNoOne)
        val viewDivider: View = itemView.findViewById(R.id.viewDivider)
    }

    class MessageViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var tvTime: AppCompatTextView = itemView.findViewById(R.id.tvTime)
        var tvMessage: AppCompatTextView = itemView.findViewById(R.id.tvMsg)
        var tvImageMsg: AppCompatTextView = itemView.findViewById(R.id.tvImageMsg)
        var tvImageTime: AppCompatTextView = itemView.findViewById(R.id.tvImageTime)
        var llMessageLayout: LinearLayout = itemView.findViewById(R.id.llMessageLayout)
        var llImageLayout: LinearLayoutCompat = itemView.findViewById(R.id.llImageLayout)
        var pbDownloading: ProgressBar = itemView.findViewById(R.id.pbDownloading)
        var tvImageSize: AppCompatTextView = itemView.findViewById(R.id.tvImageSize)
        val llDownloadImage: LinearLayout = itemView.findViewById(R.id.llDownloadImage)
        var ivImageMessage: AppCompatImageView = itemView.findViewById(R.id.ivImageMsg)
        var ivStar: AppCompatImageView = itemView.findViewById(R.id.ivStar)
        val circleProgress: DonutProgress = itemView.findViewById(R.id.circle_progress)
        val circle_progress_file: DonutProgress = itemView.findViewById(R.id.circle_progress_file)
        val ivCrossCancel: AppCompatImageView = itemView.findViewById(R.id.ivCrossCancel)
        val ivPlay: ImageView = itemView.findViewById(R.id.ivPlay)
        val ivFilePlay: AppCompatImageView = itemView.findViewById(R.id.ivFilePlay)
        val ivFileDownload: AppCompatImageView = itemView.findViewById(R.id.ivFileDownload)

        val llMainMessage: LinearLayout = itemView.findViewById(R.id.llMainMessage)
        val tvFileTime: AppCompatTextView = itemView.findViewById(R.id.tvFileTime)
        val ivFileImage: AppCompatImageView = itemView.findViewById(R.id.ivFileImage)
        val ivStarFile: AppCompatImageView = itemView.findViewById(R.id.ivStarFile)
        val tvFileName: AppCompatTextView = itemView.findViewById(R.id.tvFileName)
        val tvFileExtension: AppCompatTextView = itemView.findViewById(R.id.tvFileExtension)
        val tvFileExt: AppCompatTextView = itemView.findViewById(R.id.tvFileExt)
        val tvFileSize: AppCompatTextView = itemView.findViewById(R.id.tvFileSize)

        val tvQuestion: AppCompatTextView = itemView.findViewById(R.id.tvQuestion)
        val llRadioGroup: LinearLayout = itemView.findViewById(R.id.llRadioGroup)
        val radioOne: RadioButton = itemView.findViewById(R.id.radioOne)
        val radioTwo: RadioButton = itemView.findViewById(R.id.radioTwo)
        val checkboxGroup: LinearLayout = itemView.findViewById(R.id.checkboxGroup)
        val cbOne: CheckBox = itemView.findViewById(R.id.cbOne)
        val cbTwo: CheckBox = itemView.findViewById(R.id.cbTwo)
        val btnViewMore: TextView = itemView.findViewById(R.id.btnViewMore)
        val tvPollTime: AppCompatTextView = itemView.findViewById(R.id.tvPollTime)
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
        val llPollLayout: LinearLayout = itemView.findViewById(R.id.llPollLayout)


    }

    class UserViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var tvSeenBy: AppCompatTextView = itemView.findViewById(R.id.tvSeenBy)
        var tvSeenAt: AppCompatTextView = itemView.findViewById(R.id.tvSeenAt)
        var tvSeenAtDate: AppCompatTextView = itemView.findViewById(R.id.tvSeenAtDate)
        val ivUserImage: ImageView = itemView.findViewById(R.id.ivUserImage)
        val tvUserIcon: AppCompatTextView = itemView.findViewById(R.id.tvUserIcon)

    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, pos: Int) {
        val position: Int = holder.adapterPosition

        when (getItemViewType(position)) {
            TEXT_MESSGAE_SELF -> {
                setTextMessage(messageList[position], holder)
            }
            IMAGE_MESSGAE_SELF -> {
                setImageMessage(messageList[position], holder)
            }
            VIDEO_MESSGAE_SELF -> {
                setVideoMessage(messageList[position], holder)
            }
            FILE_MESSGAE_SELF -> {
                setFileMessage(messageList[position], holder)
            }
            POLL_SELF -> {
                setPollMessage(messageList[position], holder)
            }
            SEEN_BY_OBJECT -> {
                setSeenByObject(messageList[position], holder)
            }
            USER_OBJECT -> {
                setUserSeenBy(messageList[position], holder)
            }
        }
    }

    private fun setSeenByObject(message: Message, holder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
        val seenByViewHolder = holder as SeenByViewHolder
        if (seenBy) {
            if (message.seenByCount == 0) {
                seenByViewHolder.tvSeenBy.text = "SEEN BY"
            } else {
                seenByViewHolder.tvSeenBy.text = "SEEN BY (" + message.seenByCount.toString() + ")"
            }

            if (messageList.size <= 2) {
                seenByViewHolder.tvNoOne.text = messageText
                seenByViewHolder.tvNoOne.visibility = View.VISIBLE
                seenByViewHolder.llSeenByLayout.setBackgroundColor(Color.WHITE)
                val params = seenByViewHolder.llSeenByLayout.layoutParams as LinearLayout.LayoutParams
                params.setMargins(10, 150, 0, 0)
                seenByViewHolder.llSeenByLayout.layoutParams = params

                seenByViewHolder.ivStatus.visibility = View.GONE
                seenByViewHolder.tvSeenBy.visibility = View.GONE
                seenByViewHolder.viewDivider.visibility = View.GONE
            } else {
                seenByViewHolder.llSeenByLayout.setBackgroundColor(ContextCompat.getColor(seenByViewHolder.itemView.context, R.color.seen_by_color))
                val params = seenByViewHolder.llSeenByLayout.layoutParams as LinearLayout.LayoutParams
                params.height = LinearLayout.LayoutParams.WRAP_CONTENT
                seenByViewHolder.llSeenByLayout.layoutParams = params
                seenByViewHolder.tvNoOne.visibility = View.GONE
                seenByViewHolder.ivStatus.visibility = View.VISIBLE
                seenByViewHolder.tvSeenBy.visibility = View.VISIBLE
                seenByViewHolder.viewDivider.visibility = View.VISIBLE
            }
        } else {
            val params = seenByViewHolder.llSeenByLayout.layoutParams as LinearLayout.LayoutParams
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT
            seenByViewHolder.llSeenByLayout.layoutParams = params
            seenByViewHolder.tvNoOne.visibility = View.GONE
            seenByViewHolder.ivStatus.visibility = View.GONE
            seenByViewHolder.tvSeenBy.visibility = View.GONE
            seenByViewHolder.viewDivider.visibility = View.GONE
        }
    }

    private fun setUserSeenBy(message: Message, holder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
        val userViewHolder = holder as UserViewHolder
        userViewHolder.tvSeenAt.text = DateUtils.getTime(DateUtils().convertToLocal(message.sentAtUtc))
        if (!TextUtils.isEmpty(message.role) && message.role.toLowerCase().equals("guest")) {

            val s = message.seenBy + " (GUEST)"
            val ss1 = SpannableString(s)
            ss1.setSpan(RelativeSizeSpan(0.8f), s.length - 7, s.length, 0) // set size
            ss1.setSpan(ForegroundColorSpan(Color.GRAY), s.length - 7, s.length, 0)// set color
            userViewHolder.tvSeenBy.text = ss1
//            userViewHolder.tvSeenBy.text = message.seenBy + " (GUEST)"
        } else {
            userViewHolder.tvSeenBy.text = message.seenBy
        }

        userViewHolder.tvSeenAtDate.text = DateUtils.getDate(DateUtils().convertToLocal(message.sentAtUtc))

        if (!TextUtils.isEmpty(message.thumbnailUrl) && !(mContext as MessageInformationActivity).isFinishing) {
            val options = RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .fitCenter()
                    .priority(Priority.HIGH)
                    .transforms(CenterCrop(), RoundedCorners(10))
            Glide.with(mContext as MessageInformationActivity)
                    .asBitmap()
                    .apply(options)
                    .load(message.thumbnailUrl)
                    .into(userViewHolder.ivUserImage)
            holder.tvUserIcon.visibility = View.GONE
        } else {
            holder.tvUserIcon.visibility = View.VISIBLE
            if (!TextUtils.isEmpty(message.seenBy)) {
                holder.tvUserIcon.text = getFirstCharInUpperCase(message.seenBy)
            }
            when (message.userId.toLong() % 5) {
                1L -> holder.ivUserImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_purple))
                2L -> holder.ivUserImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_teal))
                3L -> holder.ivUserImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_red))
                4L -> holder.ivUserImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_indigo))
                else -> {
                    holder.ivUserImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ring_red))
                }
            }
        }

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

    private fun setPollMessage(message: Message, holder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
        val selfPollMessage: MessageViewHolder = holder as MessageViewHolder
        selfPollMessage.llImageLayout.visibility = View.GONE
        selfPollMessage.llMessageLayout.visibility = View.GONE
        selfPollMessage.llPollLayout.visibility = View.VISIBLE
        selfPollMessage.llMainMessage.visibility = View.GONE
        selfPollMessage.tvQuestion.text = Html.fromHtml(FormatStringUtil.FormatString.getFormattedString(message.question).get(0))
//        setTime(selfPollMessage.tvTime, message.sentAtUtc)
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
            selfPollMessage.btnViewMore.setTextColor(mContext.resources.getColor(R.color.colorPrimary))
            if (message.pollOptions.size - 2 == 1) {
                selfPollMessage.btnViewMore.text = "+" + (message.pollOptions.size - 2) + " more option"
            } else {
                selfPollMessage.btnViewMore.text = "+" + (message.pollOptions.size - 2) + " more options"
            }
//            selfPollMessage.btnViewMore.setTypeface(proxyNovaSB)
        } else {
//            selfPollMessage.btnViewMore.setTypeface(proxyNovaRegular)
            selfPollMessage.btnViewMore.text = "View Details"
            selfPollMessage.btnViewMore.setTextColor(mContext.resources.getColor(R.color.black))
        }

        setFilledBackGroundCheckBoxOther(message, selfPollMessage)
        setFilledBackGroundRadioOther(message, selfPollMessage)

        val isExpied = checkIfExpired(message)
        val formatter = SimpleDateFormat("dd MMM, hh:mm a")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = TimeInMillis(message)
        if (isExpied) {
            selfPollMessage.tvExpiryTime.text = "Poll Expired"
            selfPollMessage.tvExpiryTime.setTextColor(mContext.resources.getColor(R.color.red))
        } else {
            selfPollMessage.tvExpiryTime.text = "Poll Expiry: " + formatter.format(calendar.time)
            selfPollMessage.tvExpiryTime.setTextColor(mContext.resources.getColor(R.color.black))
        }
        setFilledBackGroundCheckBoxOther(message, selfPollMessage)
        setFilledBackGroundRadioOther(message, selfPollMessage)
        selfPollMessage.cbOne.isClickable = false
        selfPollMessage.cbOne.isFocusableInTouchMode = false

        selfPollMessage.cbTwo.isClickable = false
        selfPollMessage.cbTwo.isFocusableInTouchMode = false

        selfPollMessage.radioOne.isClickable = false
        selfPollMessage.radioOne.isFocusableInTouchMode = false

        selfPollMessage.radioTwo.isClickable = false
        selfPollMessage.radioTwo.isFocusableInTouchMode = false
        selfPollMessage.btnViewMore.setOnClickListener {
            val intent = Intent(mContext, PollDetailsActivity::class.java)
            intent.putExtra("message", message)
            intent.putExtra(FuguAppConstant.USER_ID, userId)
            intent.putExtra(FuguAppConstant.FULL_NAME, userName)
            intent.putExtra(FuguAppConstant.USER_IMAGE, userImage)
            intent.putExtra(FuguAppConstant.CHANNEL_ID, channelId)
            intent.putExtra(FuguAppConstant.DATE_TIME, message.sentAtUtc)
            intent.putExtra("expire_time", message.expireTime)
            intent.putExtra("from_name", message.fromName)
            (mContext as MessageInformationActivity).startActivityForResult(intent, 112)

        }
        calculateHeight(selfPollMessage.itemView)

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

    private fun setFileMessage(message: Message, holder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
        val messageViewHolder = holder as MessageViewHolder
        messageViewHolder.llImageLayout.visibility = View.GONE
        messageViewHolder.llMessageLayout.visibility = View.GONE
        messageViewHolder.llPollLayout.visibility = View.GONE
        messageViewHolder.llMainMessage.visibility = View.VISIBLE
        messageViewHolder.tvFileSize.text = message.fileSize
        var image = FuguAppConstant.IMAGE_MAP[message.fileExtension]
        if (image == null) {
            val mimeType = FuguMimeUtils.guessMimeTypeFromExtension(message.fileExtension.toLowerCase())
            if (mimeType != null)
                image = IMAGE_MAP[mimeType.split("/")[0]]
        }
        if (image != null) {
            messageViewHolder.ivFileImage.setImageResource(image)
//            messageViewHolder.ivFileImage.setColorFilter(Color.parseColor("#b3bec9"))
            messageViewHolder.tvFileExt.visibility = View.GONE
        } else {
            messageViewHolder.ivFileImage.setImageResource(R.drawable.file_model)
            messageViewHolder.tvFileExt.visibility = View.VISIBLE
        }
        messageViewHolder.tvFileName.text = message.fileName

        if (message.fileExtension.length > 5) {
            messageViewHolder.tvFileExtension.text = "File"
        } else {
            messageViewHolder.tvFileExtension.text = message.fileExtension
            messageViewHolder.tvFileExt.text = message.fileExtension
        }
        messageViewHolder.tvFileTime.text = DateUtils.getTime(DateUtils().convertToLocal(message.sentAtUtc))
        if (message.isStarred == 1) {
            messageViewHolder.ivStarFile.visibility = View.VISIBLE
        } else {
            messageViewHolder.ivStarFile.visibility = View.GONE
        }
        setFileDownloadStatus(messageViewHolder.ivFileDownload, messageViewHolder.circle_progress_file, messageViewHolder.ivFilePlay, message)
        messageViewHolder.circle_progress_file.progress = message.currentprogress.toFloat()
        messageViewHolder.ivFileDownload.setOnClickListener {
            val fullPath = Environment.getExternalStorageDirectory().absolutePath + "/Download"
            val downloadId = downloadFileFromUrl(fullPath, message, 0, "File")
            if (downloadId != 1) {
                message.downloadId = downloadId
            }
            messageViewHolder.circle_progress_file.visibility = View.VISIBLE
            messageViewHolder.ivFileDownload.visibility = View.GONE
        }

        messageViewHolder.itemView.setOnClickListener {
            try {
                val myFile: File
                if (!TextUtils.isEmpty(message.sentFilePath)) {
                    myFile = File(message.sentFilePath)
                    if (FuguAppConstant.SUPPORTED_FILE_FORMATS.contains(message.fileExtension.toLowerCase()) && !TextUtils.isEmpty(myFile.toString())) {
                        MessageAdapter.FileOpen.openFile(mContext, myFile)
                    }
                } else if (!TextUtils.isEmpty(message.filePath)) {
                    myFile = File(message.filePath)
                    if (FuguAppConstant.SUPPORTED_FILE_FORMATS.contains(message.fileExtension.toLowerCase()) && !TextUtils.isEmpty(myFile.toString())) {
                        MessageAdapter.FileOpen.openFile(mContext, myFile)
                    }
                } else if (!TextUtils.isEmpty(message.image_url)) {
                    myFile = File(message.image_url)
                    if (FuguAppConstant.SUPPORTED_FILE_FORMATS.contains(message.fileExtension.toLowerCase()) && !TextUtils.isEmpty(myFile.toString())) {
                        MessageAdapter.FileOpen.openFile(mContext, myFile)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        calculateHeight(messageViewHolder.itemView)
    }

    private fun setVideoMessage(message: Message, holder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
        val messageViewHolder = holder as MessageViewHolder
        messageViewHolder.llImageLayout.visibility = View.VISIBLE
        messageViewHolder.llMessageLayout.visibility = View.GONE
        messageViewHolder.llMainMessage.visibility = View.GONE
        messageViewHolder.llPollLayout.visibility = View.GONE
        if (!TextUtils.isEmpty(message.alteredMessage)) {
            messageViewHolder.tvImageMsg.visibility = View.VISIBLE
            var messageString = ""
            if (message.messageState == 4) {
                if (!TextUtils.isEmpty(message.alteredMessage)) {
                    messageString = message.alteredMessage + " <font  color='grey'><small> (edited)</small></font>"
                } else {
                    messageString = message.message + " <font  color='grey'><small> (edited)</small></font>"
                }
            } else {
                if (!TextUtils.isEmpty(message.alteredMessage)) {
                    messageString = message.alteredMessage
                } else {
                    messageString = message.message
                }
            }
            messageViewHolder.tvImageMsg.text = Html.fromHtml(messageString)
        } else {
            messageViewHolder.tvImageMsg.visibility = View.GONE
        }
        messageViewHolder.tvImageTime.text = DateUtils.getTime(DateUtils().convertToLocal(message.sentAtUtc))
        if (!TextUtils.isEmpty(message.image_url_100x100)) {
            Glide.with(mContext)
                    .load(message.image_url_100x100)
                    .into(messageViewHolder.ivImageMessage)
        } else {
            Glide.with(mContext)
                    .load(message.thumbnailUrl)
                    .into(messageViewHolder.ivImageMessage)
        }
        if (message.isStarred == 1) {
            messageViewHolder.ivStar.visibility = View.VISIBLE
        } else {
            messageViewHolder.ivStar.visibility = View.GONE
        }
        setVideoDownloadStatus(messageViewHolder.llDownloadImage, messageViewHolder.circleProgress, messageViewHolder.ivPlay, message, messageViewHolder.ivCrossCancel)
        messageViewHolder.tvImageSize.text = message.fileSize
        messageViewHolder.circleProgress.progress = message.currentprogress.toFloat()
        messageViewHolder.llDownloadImage.setOnClickListener {
            val fullPath = Environment.getExternalStorageDirectory().absolutePath + "/Download"
            val downloadIdVideo = downloadFileFromUrl(fullPath, message, 0, "Video")
            if (downloadIdVideo != 1) {
                message.downloadId = downloadIdVideo
            }
            holder.circleProgress.visibility = View.VISIBLE
            holder.ivCrossCancel.visibility = View.VISIBLE
            messageViewHolder.llDownloadImage.visibility = View.GONE
        }
        messageViewHolder.ivPlay.setOnClickListener {
            val mIntent = Intent(mContext, VideoPlayerActivity::class.java)
            mIntent.putExtra("url", message.filePath)
            mContext.startActivity(mIntent)
        }
        if (message.isStarred == 1) {
            messageViewHolder.ivStar.visibility = View.VISIBLE
        } else {
            messageViewHolder.ivStar.visibility = View.GONE
        }
        calculateHeight(messageViewHolder.itemView)
    }

    private fun setImageMessage(message: Message, holder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
        val messageViewHolder = holder as MessageViewHolder
        messageViewHolder.llImageLayout.visibility = View.VISIBLE
        messageViewHolder.llMessageLayout.visibility = View.GONE
        messageViewHolder.llMainMessage.visibility = View.GONE
        messageViewHolder.llPollLayout.visibility = View.GONE
        if (!TextUtils.isEmpty(message.alteredMessage)) {
            messageViewHolder.tvImageMsg.visibility = View.VISIBLE
            var messageString = ""
            if (message.messageState == 4) {
                if (!TextUtils.isEmpty(message.alteredMessage)) {
                    messageString = message.alteredMessage + " <font  color='grey'><small> (edited)</small></font>"
                } else {
                    messageString = message.message + " <font  color='grey'><small> (edited)</small></font>"
                }
            } else {
                messageString = if (!TextUtils.isEmpty(message.alteredMessage)) {
                    message.alteredMessage
                } else {
                    message.message
                }
            }
            manipulateAndSetText(messageViewHolder.tvImageMsg, messageString, message.messageState)
//            messageViewHolder.tvImageMsg.text = Html.fromHtml(messageString)
        } else {
            messageViewHolder.tvImageMsg.visibility = View.GONE
        }
        messageViewHolder.tvImageSize.text = message.fileSize
        messageViewHolder.tvImageTime.text = DateUtils.getTime(DateUtils().convertToLocal(message.sentAtUtc))
        setImage(messageViewHolder.pbDownloading, messageViewHolder.llDownloadImage, messageViewHolder.ivImageMessage, message)
        if (message.isStarred == 1) {
            messageViewHolder.ivStar.visibility = View.VISIBLE
        } else {
            messageViewHolder.ivStar.visibility = View.GONE
        }
        var extension = message.image_url!!.split(".")[message.image_url.split(".").size - 1]

        if (extension.equals("png")) {
            extension = "jpg"
        }

        messageViewHolder.llDownloadImage.setOnClickListener {
            val downloadId = downloadFileFromUrl(extension, message, 0, "Image")
            message.downloadId = downloadId
            messageViewHolder.pbDownloading.visibility = View.VISIBLE
            messageViewHolder.llDownloadImage.visibility = View.GONE
        }
        messageViewHolder.ivImageMessage.setOnClickListener {

            val fileName = message.fileName + "_" + message.muid + "." + extension
            val filePathNormal = File(getNormalDirectory(extension) + "/" + fileName)
            val filePathPrivate = File(getPrivateDirectory(extension) + "/" + fileName)

            if (filePathNormal.exists() || filePathPrivate.exists()) {
                val profileImageIntent = Intent(mContext, ImageDisplayActivity::class.java)
                val profileImage = Image(message.image_url, message.image_url, "imageOne", "", "")
                profileImageIntent.putExtra("image", profileImage)
                profileImageIntent.putExtra("isFromProfileActivity", true)
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(mContext as MessageInformationActivity,
                        messageViewHolder.ivImageMessage, "imageOne")
                (mContext as MessageInformationActivity).startActivity(profileImageIntent, options.toBundle())
            }
        }
        calculateHeight(messageViewHolder.itemView)
    }

    private fun calculateHeight(itemView: View) {
        if (isCalculationNeeded) {
            itemView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    itemView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    viewOneHeight = itemView.height
                }
            })
        }
    }

    fun checkIfScrollNeeded() {
        if (isCalculationNeeded) {
            (mContext as MessageInformationActivity).scrollToBottom(viewOneHeight)
        }
    }

    private fun setTextMessage(message: Message, holder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
        val messageViewHolder = holder as MessageViewHolder
        messageViewHolder.llImageLayout.visibility = View.GONE
        messageViewHolder.llMessageLayout.visibility = View.VISIBLE
        messageViewHolder.llMainMessage.visibility = View.GONE
        messageViewHolder.llPollLayout.visibility = View.GONE
        var messageString = ""
        if (message.messageState == 4) {
            if (!TextUtils.isEmpty(message.alteredMessage)) {
                messageString = message.alteredMessage + " <font  color='grey'><small> (edited)</small></font>"
            } else {
                messageString = message.message + " <font  color='grey'><small> (edited)</small></font>"
            }
        } else {
            if (!TextUtils.isEmpty(message.alteredMessage)) {
                messageString = message.alteredMessage
            } else {
                messageString = message.message
            }
        }
        manipulateAndSetText(messageViewHolder.tvMessage, messageString, message.messageState)
        messageViewHolder.tvTime.text = DateUtils.getTime(DateUtils().convertToLocal(message.sentAtUtc))
        calculateHeight(messageViewHolder.itemView)
    }

    override fun getItemViewType(position: Int): Int {
        return messageList[position].rowType
    }

    fun setImage(pbDownloading: ProgressBar?, llDownloadImage: LinearLayout?, ivImageMessage: AppCompatImageView, message: Message) {


        var extension = message.image_url!!.split(".")[message.image_url.split(".").size - 1]

        if (((mContext is MessageInformationActivity && !(mContext as MessageInformationActivity).isFinishing))) {
            try {
                val options: RequestOptions?
                var displayFileUrl: String?

                if (extension.contains("gif")) {
                    llDownloadImage?.visibility = View.GONE
                    pbDownloading?.visibility = View.GONE
                    options = RequestOptions()
                            .fitCenter()
                    Glide.with(mContext as MessageInformationActivity)
                            .asGif()
                            .load(message.image_url)
                            .apply(options)
                            .into(ivImageMessage)

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
                        var imageHeight = FuguAppConstant.MAX_HEIGHT
                        var imageWidth = FuguAppConstant.MAX_WIDTH
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
                            imageHeight = FuguAppConstant.MAX_HEIGHT
                            imageWidth = FuguAppConstant.MAX_WIDTH
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
                                    && (message.messageStatus == FuguAppConstant.MESSAGE_SENT
                                            || message.messageStatus == FuguAppConstant.MESSAGE_DELIVERED)) {
                                exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_DATETIME, message.sentAtUtc)
                                exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MODEL, message.id.toString())
                                if (message.threadMessage) {
                                    exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MAKE, channelId.toString() + message.muid)
                                } else {
                                    exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MAKE, channelId.toString())
                                }
                                exif.saveAttributes()
//                                messageMap[message.muid] = message
//                                getFromSdcard()
                            }

                            displayFileUrl = link
                            options = RequestOptions()
                                    .dontAnimate()
                                    .placeholder(R.drawable.placeholder)
                                    .error(R.drawable.placeholder)
                                    .override(imageWidth, imageHeight)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .priority(Priority.HIGH)

                        } else {
                            llDownloadImage?.visibility = View.VISIBLE
                            pbDownloading?.visibility = View.GONE

                            options = RequestOptions()
                                    .dontAnimate()
                                    .placeholder(R.drawable.placeholder)
                                    .error(R.drawable.placeholder)
                                    .priority(Priority.HIGH)
                                    .override(imageWidth, imageHeight)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .transforms(CenterCrop(), RoundedCorners(3), BlurTransform(5, 3))
                        }

                        Glide.with(mContext as MessageInformationActivity)
                                .asBitmap()
                                .apply(options)
                                .load(displayFileUrl)
                                .into(ivImageMessage)

                    } catch (e: java.lang.Exception) {
                        val optionss = RequestOptions()
                                .dontAnimate()
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.placeholder)
                                .priority(Priority.HIGH)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .transforms(CenterCrop(), RoundedCorners(3), BlurTransform(5, 3))

                        Glide.with(mContext as MessageInformationActivity)
                                .asBitmap()
                                .apply(optionss)
                                .load(message.thumbnailUrl)
                                .into(ivImageMessage)

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getNormalDirectory(extension: String): String? {
        try {
            var filePath = (Environment.getExternalStorageDirectory()).toString() + File.separator + FuguAppConstant.APP_NAME_SHORT  +
                    File.separator + CommonData.getWorkspaceResponse(workspaceInfo!!.fuguSecretKey).workspaceName.replace(" ".toRegex(), "").replace("'s".toRegex(), "") + File.separator + FuguAppConstant.FILE_TYPE_MAP[extension.toLowerCase()]!!.directory
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
            var filePath = (Environment.getExternalStorageDirectory()).toString() + File.separator + FuguAppConstant.APP_NAME_SHORT  +
                    File.separator + CommonData.getWorkspaceResponse(workspaceInfo!!.fuguSecretKey).workspaceName.replace(" ".toRegex(), "").replace("'s".toRegex(), "") + File.separator + FuguAppConstant.FILE_TYPE_MAP[extension.toLowerCase()]!!.directory
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

                                currentOrderItem.currentprogress = (progress.currentBytes * 100 / progress.totalBytes).toInt()
                                currentOrderItem.downloadStatus = FuguAppConstant.DownloadStatus.DOWNLOAD_IN_PROGRESS.downloadStatus
                                notifyItemChanged(0)

//                                val mIntent = getIntentExtraData(position, (progress.currentBytes * 100 / progress.totalBytes).toInt(), currentOrderItem.uuid, FuguAppConstant.DownloadStatus.DOWNLOAD_IN_PROGRESS.downloadStatus)
//                                LocalBroadcastManager.getInstance(mContext).sendBroadcast(mIntent)
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

                            if (currentOrderItem.messageType == FuguAppConstant.IMAGE_MESSAGE) {
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
                                            if (!currentOrderItem.threadMessage) {
                                                exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MAKE, channelId.toString())
                                            } else {
                                                exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MAKE, channelId.toString() + currentOrderItem.muid)
                                            }

                                            exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MODEL, currentOrderItem.id.toString())
                                            exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_DATETIME, currentOrderItem.sentAtUtc)
                                            exif.saveAttributes()
//                                            imageFiles.add(ImageItem(directoy + File.separator + currentOrderItem.fileName + "_" + currentOrderItem.muid + ".jpeg",
//                                                    currentOrderItem.muid, currentOrderItem.id.toString(), currentOrderItem.sentAtUtc, messageMap[currentOrderItem.muid]))
//                                            imageFiles.sortWith(Comparator { one, other -> other.transitionName!!.compareTo(one.transitionName!!) })
//                                            imageFiles.reverse()
                                        } else {
                                            val exif = androidx.exifinterface.media.ExifInterface(file.absolutePath)
                                            if (!currentOrderItem.threadMessage) {
                                                exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MAKE, channelId.toString())
                                            } else {
                                                exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MAKE, channelId.toString() + currentOrderItem.muid)
                                            }
                                            exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_DATETIME, currentOrderItem.sentAtUtc)
                                            exif.setAttribute(androidx.exifinterface.media.ExifInterface.TAG_MODEL, currentOrderItem.id.toString())
                                            exif.saveAttributes()
//                                            imageFiles.add(ImageItem(directoy + File.separator + currentOrderItem.fileName + "_" + currentOrderItem.muid + ".jpg",
//                                                    currentOrderItem.muid, currentOrderItem.id.toString(), currentOrderItem.sentAtUtc, messageMap[currentOrderItem.muid]))
//                                            imageFiles.sortWith(Comparator { one, other -> other.transitionName!!.compareTo(one.transitionName!!) })
//                                            imageFiles.reverse()
                                        }

                                        currentOrderItem.downloadStatus = FuguAppConstant.DownloadStatus.DOWNLOAD_COMPLETED.downloadStatus
                                        currentOrderItem.filePath = dirPath + "/" + currentOrderItem.fileName
                                        currentOrderItem.unsentFilePath = currentOrderItem.filePath
                                        currentOrderItem.sentFilePath = currentOrderItem.filePath
                                        currentOrderItem.currentprogress = 100
                                        currentOrderItem.downloadStatus = FuguAppConstant.DownloadStatus.DOWNLOAD_COMPLETED.downloadStatus
                                        notifyItemChanged(0)
//                                        val mIntent = getIntentExtraData(position, 100, currentOrderItem.uuid, FuguAppConstant.DownloadStatus.DOWNLOAD_COMPLETED.downloadStatus)
//                                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(mIntent)
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
                                if (currentOrderItem.messageType == FuguAppConstant.VIDEO_MESSAGE) {
                                    currentOrderItem.sharableImage_url = currentOrderItem.url
                                    currentOrderItem.sharableThumbnailUrl = currentOrderItem.url
                                    currentOrderItem.sharableImage_url_100x100 = currentOrderItem.url
                                }
                                messageList.set(position, currentOrderItem)
                                currentOrderItem.currentprogress = 100
                                currentOrderItem.downloadStatus = FuguAppConstant.DownloadStatus.DOWNLOAD_COMPLETED.downloadStatus
                                notifyItemChanged(0)
//                                val mIntent = getIntentExtraData(position, 100, currentOrderItem.uuid, FuguAppConstant.DownloadStatus.DOWNLOAD_COMPLETED.downloadStatus)
//                                LocalBroadcastManager.getInstance(mContext).sendBroadcast(mIntent)
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
                            }
                        }

                        override fun onError(error: Error) {
                            Log.e("Progress", "onError")
                            currentOrderItem.downloadId = 0
                            currentOrderItem.currentprogress = 0
                            currentOrderItem.downloadStatus = FuguAppConstant.DownloadStatus.DOWNLOAD_FAILED.downloadStatus
                            notifyItemChanged(0)
//                            val mIntent = getIntentExtraData(position, 0, currentOrderItem.uuid, FuguAppConstant.DownloadStatus.DOWNLOAD_FAILED.downloadStatus)
//                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(mIntent)
                        }
                    })
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }
    }

    private fun getDirectoryWithMediaCheck(extension: String): String? {
        try {
            var filePath = (Environment.getExternalStorageDirectory()).toString() + File.separator + FuguAppConstant.APP_NAME_SHORT  +
                    File.separator + CommonData.getWorkspaceResponse(workspaceInfo!!.fuguSecretKey).workspaceName.replace(" ".toRegex(), "").replace("'s".toRegex(), "") + File.separator + FuguAppConstant.FILE_TYPE_MAP[extension.toLowerCase()]!!.directory
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

    private fun setVideoDownloadStatus(ivFileDownload: LinearLayout, circleProgress: DonutProgress, ivFilePlay: ImageView, message: Message, ivCrossCancel: ImageView) {
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

    private fun setFileDownloadStatus(ivFileDownload: AppCompatImageView, circleProgress: DonutProgress, ivFilePlay: AppCompatImageView, message: Message) {
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
                        //                        if (mediaPlayer != null && isPlaying) {
//                            val currentOrder = messageList.get(oldPos)
//                            mediaPlayer?.stop()
//                            mediaPlayer = null
//                            currentOrder.isAudioPlaying = false
//                            recyclerView.adapter?.notifyItemChanged(oldPos)
//                            isPlaying = false
//                            if (oldPos != position) {
//                                playAudio(message.getFilePath(), position)
//                            } else {
//                                message.setAudioPlaying(true)
//                                recyclerView.adapter?.notifyItemChanged(position)
//                            }
//                        } else {
//                            playAudio(message.getFilePath(), position)
//                        }
//                        if (FuguAppConstant.SUPPORTED_AUDIO_FORMATS.contains(message.getFileExtension().toLowerCase())) {
//                            if (!message.isAudioPlaying()) {
//                                ivFilePlay.setImageResource(R.drawable.song_pause)
//                                message.setAudioPlaying(true)
//                            } else {
//                                ivFilePlay.setImageResource(R.drawable.music_player)
//                                message.setAudioPlaying(false)
//                            }
//                        }
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

    private fun TimeInMillis(message: Message): Long {
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

    private fun setFilledBackGroundCheckBox(message: Message, selfPollMessage: MessageViewHolder) {
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

    private fun setOptionsTextOther(optionOne: String?, optionTwo: String?, selfPollMessage: MessageViewHolder, multiSelect: Boolean) {
        if (!multiSelect) {
            selfPollMessage.radioOne.text = optionOne
            selfPollMessage.radioTwo.text = optionTwo
        } else {
            selfPollMessage.cbOne.text = optionOne
            selfPollMessage.cbTwo.text = optionTwo
        }
    }

    private fun setFilledBackGroundCheckBoxOther(message: Message, selfPollMessage: MessageViewHolder) {
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

    private fun setFilledBackGroundRadio(message: Message, selfPollMessage: MessageViewHolder) {
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

    private fun setFilledBackGroundRadioOther(message: Message, selfPollMessage: MessageViewHolder) {
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

    private fun dpToPx(dpParam: Int): Int {
        val d = mContext.resources.displayMetrics.density
        return (dpParam * d).toInt()
    }

    fun updateList(messageList: ArrayList<Message>) {
        this.messageList = messageList
    }

    fun checkSeenBy(seenBy: Boolean) {
        this.seenBy = seenBy
    }

    private fun manipulateAndSetText(tvMessage: AppCompatTextView, message: String?, messageState: Int = 1) {
        val textArray = message.toString().split(" ")
        var text = ""
        for (i in textArray) {
            if (i.toLowerCase().contains("http") ||
                    i.toLowerCase().contains("www.")) {
                text = "$text <a href=\"" + i + "\">$i</a> "
            } else {
                text = "$text $i"
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
        tvMessage.setLinkTextColor(mContext.resources.getColor(R.color.color_tag))
        tvMessage.text = s

        tvMessage.movementMethod = BetterLinkMovementMethod.getInstance()
        val replacements = MyEmojiParser.getUnicodeCandidates(s.toString())
        val messagee = MyEmojiParser.removeAllEmojis(s.toString())

        if (replacements.size in 1..2 && messagee.isEmpty()) {
            val ss1 = SpannableString(s)
            ss1.setSpan(RelativeSizeSpan(2f), 0, s.length, 0)
            tvMessage.text = ss1
        } else {
            if (messageState == 4) {
                val messagee = MyEmojiParser.removeAllEmojis(s.substring(0, s.length - 9))
                if (replacements.size in 1..2 && messagee.isEmpty()) {
                    val ss1 = SpannableString(s)
                    ss1.setSpan(RelativeSizeSpan(1f), s.length - 9, s.length, 0)
                    ss1.setSpan(RelativeSizeSpan(2f), 0, s.length - 9, 0)
                    tvMessage.text = ss1
                } else {
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

        BetterLinkMovementMethod.linkifyHtmlNone(tvMessage).setOnLinkClickListener(urlClickListener)
    }

    fun updateMessageText(messageText: String) {
        this.messageText = messageText
    }

    private val urlClickListener = BetterLinkMovementMethod.OnLinkClickListener { _, url ->
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
                    clickableLink = "http://" + clickableLink
                }

                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(clickableLink)
                mContext.startActivity(i)
            } catch (e: java.lang.Exception) {

            }
        } else {
            try {
                openProfile(url, mContext)
            } catch (e: Exception) {

            }

        }
        true
    }

}