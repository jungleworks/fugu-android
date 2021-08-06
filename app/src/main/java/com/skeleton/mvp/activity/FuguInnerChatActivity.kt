package com.skeleton.mvp.activity

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.content.ClipboardManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.transition.ChangeBounds
import android.transition.Transition
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.core.content.PermissionChecker
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.downloader.PRDownloader
import com.easyfilepicker.activity.AudioPickActivity
import com.easyfilepicker.activity.ImagePickActivity
import com.easyfilepicker.activity.NormalFilePickActivity
import com.easyfilepicker.activity.VideoPickActivity
import com.easyfilepicker.filter.entity.AudioFile
import com.easyfilepicker.filter.entity.ImageFile
import com.easyfilepicker.filter.entity.NormalFile
import com.easyfilepicker.filter.entity.VideoFile
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.percolate.mentions.Mentions
import com.percolate.mentions.QueryListener
import com.percolate.mentions.SuggestionsListener
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.ChatActivity.UserSearchApi
import com.skeleton.mvp.adapter.BottomSheetEmojiAdapter
import com.skeleton.mvp.adapter.EmojiAdapter
import com.skeleton.mvp.adapter.FuguAttachmentAdapter
import com.skeleton.mvp.adapter.MessageAdapter
import com.skeleton.mvp.adapter.MessageAdapter.OnRetryListener
import com.skeleton.mvp.adapter.mentions.UsersAdapter
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.db.ChatDatabase.getMessageList
import com.skeleton.mvp.data.db.ChatDatabase.getUnsentMessageMapByChannel
import com.skeleton.mvp.data.db.ChatDatabase.setMessageList
import com.skeleton.mvp.data.db.ChatDatabase.setUnsentMessageMapByChannel
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.data.network.CommonParams
import com.skeleton.mvp.data.network.RestClient
import com.skeleton.mvp.fragment.*
import com.skeleton.mvp.model.*
import com.skeleton.mvp.model.Message
import com.skeleton.mvp.model.innerMessage.ThreadedMessagesResponse
import com.skeleton.mvp.model.media.MediaResponse
import com.skeleton.mvp.model.mentions.Mention
import com.skeleton.mvp.model.userSearch.UserSearch
import com.skeleton.mvp.photoEditor.EmojiBSFragment
import com.skeleton.mvp.photoEditor.EmojiBSFragment.EmojiListener
import com.skeleton.mvp.photoEditor.PropertiesBSFragment
import com.skeleton.mvp.photoEditor.TextEditorDialogFragment
import com.skeleton.mvp.pushNotification.PushReceiver.PushChannel.pushMuid
import com.skeleton.mvp.retrofit.APIError
import com.skeleton.mvp.retrofit.CommonResponse
import com.skeleton.mvp.retrofit.MultipartParams
import com.skeleton.mvp.retrofit.ResponseResolver
import com.skeleton.mvp.socket.SocketConnection.SocketClientCallback
import com.skeleton.mvp.socket.SocketConnection.initSocketConnection
import com.skeleton.mvp.socket.SocketConnection.reactOnMessage
import com.skeleton.mvp.socket.SocketConnection.sendThreadMessage
import com.skeleton.mvp.socket.SocketConnection.setSocketListeners
import com.skeleton.mvp.socket.SocketConnection.subscribeChannel
import com.skeleton.mvp.socket.SocketConnection.unsubscribeChannel
import com.skeleton.mvp.ui.AppConstants
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.ui.dialog.ProgressDialog
import com.skeleton.mvp.util.*
import com.skeleton.mvp.util.FormatStringUtil.FormatString.getFormattedString
import com.skeleton.mvp.utils.*
import com.skeleton.mvp.utils.ProgressRequestBody.UploadCallbacks
import com.skeleton.mvp.utils.RecyclerItemClickListener
import com.theartofdev.edmodo.cropper.CropImage
import io.socket.client.Socket
import ja.burhanrashid52.photoeditor.*
import ja.burhanrashid52.photoeditor.PhotoEditor.OnSaveListener
import okhttp3.MultipartBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

class FuguInnerChatActivity : BaseActivity(), View.OnClickListener, View.OnLongClickListener, EmojiAdapter.GetClickedEmoji, QueryListener, SuggestionsListener, UploadCallbacks, Readfunctionality, Animation.AnimationListener, OnPhotoEditorListener, PropertiesBSFragment.Properties, EmojiListener {
    private var tvTitle: TextView? = null
    private val tvUserName: TextView? = null
    private val tvUserMessage: TextView? = null
    private val tvUserImageMessage: TextView? = null
    private val tvFileName: TextView? = null
    private val tvFileSize: TextView? = null
    private val tvExtension: TextView? = null
    private val ivUserImage: ImageView? = null
    private val ivBack: ImageView? = null
    private val ivMsgImage: ImageView? = null
    private val ivFileType: ImageView? = null
    private var imageRecycler: RecyclerView? = null
    private var imageLlMentions: LinearLayoutCompat? = null
    private var imageText = ""
    private val emojiLayouts = ArrayList<LinearLayout>()
    private val emojiTexts = ArrayList<TextView>()
    private val emojiCounts = ArrayList<TextView>()
    private var reactedPosition = -1
    private var myUserId = ""
    private var myName = ""
    private var messageType = FuguAppConstant.TEXT_MESSAGE
    private val rlImageMessage: RelativeLayout? = null
    private val llFileRoot: RelativeLayout? = null
    private var message: Message? = null
    private var animation: Animation? = null
    var isSwapped = false
    var emojiPopup: EmojiPopup? = null
    private var ivEmoji: ImageView? = null
    private var isDialogOpened = false
    private var rlRoot: RelativeLayout? = null
    private var etMsg: EmojiGifEditText? = null
    private var channelId = -1L
    private var ft: FragmentTransaction? = null
    private var fuguThreadMessageList: ArrayList<Message> = ArrayList()
    private var dateUtils: DateUtils? = null
    private var dateItemCount = 0
    private var fuguMessageAdapter: MessageAdapter? = null
    private val conversation = FuguConversation()
    private lateinit var rvMessages: RecyclerView
    private var clickedEmojiMuid = ""
    private var ivSend: ImageView? = null
    private var ivAttachment: ImageView? = null
    private var mLastClickTime: Long = 0
    private var fuguImageUtils: FuguImageUtils? = null
    private var workspacesInfo: WorkspacesInfo? = null
    private var isTyping = FuguAppConstant.TYPING_SHOW_MESSAGE
    private val isFayeChannelActive = false
    private var sentAtUTC = ""
    private var llRoot: LinearLayout? = null
    private var taggedUsers: ArrayList<Int>? = ArrayList()
    private var mentions: Mentions? = null
    private var mentionsInImage: Mentions? = null
    private var emojiMap = ArrayList<String>()
    private var usersAdapter: UsersAdapter? = null
    private var usersAdapterImage: UsersAdapter? = null
    private var rvMentions: RecyclerView? = null

    private var llMention: LinearLayoutCompat? = null
    private val membersList: ArrayList<Member>? = ArrayList()
    private val membersMap = HashMap<Long, Member>()
    private var fetchMemberCallInProgress = false
    private var threadMuid = ""
    private val mMessageIndices = LinkedHashMap<String?, Int>()
    private val scrollView: NestedScrollView? = null
    private val onSubscribe = FuguAppConstant.CHANNEL_UNSUBSCRIBED
    private val fuguDateUtil = DateUtils.getInstance()
    private var extension: String? = null
    private var isThread = false
    private var muid: String? = null
    private var unsentMessageMap = java.util.LinkedHashMap<String, Message>()
    private val isBottom = false
    private val isFirstScrollDone = false
    private val tvReplyCount: TextView? = null
    var messageList = ArrayList<Message>()
    private var isChatDeleted = false
    private var isMessageDeleted = false
    private val ivDeleted: ImageView? = null
    private val llEmojis: LinearLayout? = null
    private var tvCannotReply: LinearLayout? = null
    private var llMessageLayout: LinearLayout? = null
    private var toolbar: Toolbar? = null
    private var showToolbar = true
    private val ivPlay: ImageView? = null
    private val TEXT_MESSAGE_SELF = 0
    private val TEXT_MESSAGE_OTHER = 1
    private val IMAGE_MESSAGE_SELF = 2
    private val IMAGE_MESSAGE_OTHER = 3
    private val FILE_MESSAGE_SELF = 4
    private val FILE_MESSAGE_OTHER = 5
    private val HEADER_ITEM = 6
    private val PUBLIC_NOTE = 7
    private val UNREAD_ITEM = 8
    private val MESSAGE_DELETED_SELF = 9
    private val MESSAGE_DELETED_OTHER = 10
    private val VIDEO_MESSAGE_SELF = 11
    private val VIDEO_MESSAGE_OTHER = 12
    private val supportedFormats = ArrayList(listOf("pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "ipa", "apk", "csv", "txt", "3gp", "mp3", "midi", "mpeg", "x-aiff", "mpeg", "x-wav", "webm", "ogg", "m4a", "wav", "mp4", "mov", "flv", "mkv", "mts", "mpeg", "mpg"))
    private val iv: ImageView? = null
    private val rl: RelativeLayout? = null
    private var isKeyBoardVisible = false
    private var dialogView: View? = null
    private var dialog: Dialog? = null
    private var isMessageInEditMode = false
    private var firstEditMuid = ""
    private var mentionsArrayList = ArrayList<Mention>()
    private var ivCancelEdit: ImageView? = null
    private val tvEdited: TextView? = null
    private var maxUploadSize = 0L
    private var swipeRefresh: LinearLayout? = null
    private val dateUtil = DateUtils.getInstance()
    private val TEXT_MESSAGE_THREAD = 18
    private val IMAGE_MESSAGE_THREAD = 19
    private val VIDEO_MESSAGE_THREAD = 20
    private val FILE_MESSAGE_THREAD = 21
    private val THREAD_MESSAGE_DELETED_SELF = 22
    private val THREAD_MESSAGE_DELETED_OTHER = 23
    private var imageWithMessageDialog: Dialog? = null
    private var pbLoading: ProgressBar? = null
    private var imgUndo: AppCompatImageView? = null
    private var imgEdit: AppCompatImageView? = null
    private var imgEmoji: AppCompatImageView? = null
    private var imgText: AppCompatImageView? = null
    private val imgCrop: AppCompatImageView? = null
    private val imgSend: AppCompatImageView? = null
    private var mPhotoEditor: PhotoEditor? = null
    private var mPhotoEditorView: PhotoEditorView? = null
    private var mPropertiesBSFragment: PropertiesBSFragment? = null
    private var mEmojiBSFragment: EmojiBSFragment? = null
    private var messagePosition = -1
    private var searchPrefix = "-"
    private var isMentionSelected = false
    private var countDownTimer: CountDownTimer? = null
    private var userCount = 0L
    private var userPageSize = 0
    private var filteredMembers: MutableList<Member> = ArrayList()
    private var userRole = "USER"
    private var only_admin_can_message = false
    private var userType = FuguAppConstant.UserType.BOT
    private var rvEmoji: RecyclerView? = null
    private var viewTranslucent: View? = null
    private var threadBottomSheetFragment: BottomSheetDialogFragment? = null
    private val keyboardListener = KeyboardUtil.SoftKeyboardToggleListener { isVisible: Boolean -> isKeyBoardVisible = isVisible }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fugu_inner_chat)
        //postponeEnterTransition();
        maxUploadSize = CommonData.getCommonResponse().getData().fuguConfig.max_upload_file_size
        fuguImageUtils = FuguImageUtils(this@FuguInnerChatActivity)
        channelId = intent.getLongExtra(FuguAppConstant.CHANNEL_ID, -1)
        animation = AnimationUtils.loadAnimation(applicationContext,
                R.anim.emoji_anim)
        animation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
        })
        dateUtils = DateUtils.getInstance()
        KeyboardUtil.addKeyboardToggleListener(this, keyboardListener)
        initViews()
        setUpEmojiPopup()
        if (conversation.chat_type == FuguAppConstant.ChatType.DEFAULT_GROUP || conversation.chat_type == FuguAppConstant.ChatType.GENERAL_GROUP || conversation.chat_type == FuguAppConstant.ChatType.PUBLIC_GROUP || conversation.chat_type == FuguAppConstant.ChatType.PRIVATE_GROUP) {
            setUpMentions()
            setupMentionsList()
        }
        messageList = getMessageList(channelId)
        if (intent.hasExtra(MESSAGE)) {
            Log.e(MESSAGE, intent.getSerializableExtra(MESSAGE).toString())
        }
        ivEmoji!!.setOnClickListener { v: View? ->
            if (isDialogOpened) {
                isDialogOpened = false
                ivEmoji!!.setImageResource(R.drawable.ic_happiness)
            } else {
                isDialogOpened = true
                ivEmoji!!.setImageResource(R.drawable.ic_keyboard)
            }
            emojiPopup!!.toggle()
        }
        val manager = this@FuguInnerChatActivity.supportFragmentManager
        ft = manager.beginTransaction()
        conversation.businessName = intent.getStringExtra("BUSINESS_NAME")
        conversation.label = intent.getStringExtra("label")
        if (intent.hasExtra("chatType")) {
            conversation.chat_type = intent.getIntExtra("chatType", 2)
        }
        rvMessages.itemAnimator = null
        workspacesInfo = CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()]
        myUserId = workspacesInfo!!.userId
        myName = workspacesInfo!!.fullName
        if (conversation.chat_type != 2) {
            tvTitle!!.text = conversation.label
        }
        fetchMembers()
        setUpSocketListeners("Create thread")
        Handler().postDelayed({
            if (isNetworkAvailable()) {
                pbLoading!!.visibility = View.VISIBLE
                apiGetThreadedMessages(muid)
            }
        }, 200)
        try {
            if (message!!.userType == FuguAppConstant.UserType.SCRUM_BOT && conversation.chat_type == FuguAppConstant.ChatType.BOT) {
                llMessageLayout!!.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpEmojiPopup() {
        emojiPopup = EmojiPopup.Builder.fromRootView(rlRoot)
                .setOnEmojiBackspaceClickListener { }
                .setOnEmojiClickListener { emoji, imageView -> }
                .setOnEmojiPopupShownListener { }
                .setOnSoftKeyboardOpenListener { }
                .setOnEmojiPopupDismissListener {
                    isDialogOpened = false
                    ivEmoji!!.setImageResource(R.drawable.ic_happiness)
                }
                .setOnSoftKeyboardCloseListener { }
                .build(etMsg!!)
    }

    private val mThreadDeleteChatReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                val channelIdReceived = intent.getLongExtra(FuguAppConstant.CHANNEL_ID, -1L)
                if (intent.hasExtra(FuguAppConstant.APP_SECRET_KEY)
                        && intent.getStringExtra(FuguAppConstant.APP_SECRET_KEY) == CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey && channelIdReceived.compareTo(channelId) == 0) {
                    val pos = mMessageIndices[intent.getStringExtra("thread_muid")]!!
                    if (intent.getStringExtra("thread_muid") == fuguThreadMessageList[pos].uuid) {
                        val text: String
                        val rowType: Int
                        if (fuguThreadMessageList[pos].rowType == TEXT_MESSAGE_SELF || fuguThreadMessageList[pos].rowType == IMAGE_MESSAGE_SELF || fuguThreadMessageList[pos].rowType == VIDEO_MESSAGE_SELF || fuguThreadMessageList[pos].rowType == FILE_MESSAGE_SELF || fuguThreadMessageList[pos].rowType == MESSAGE_DELETED_SELF) {
                            rowType = MESSAGE_DELETED_SELF
                            text = "You deleted this message. "
                        } else {
                            rowType = MESSAGE_DELETED_OTHER
                            text = "This message was deleted. "
                        }
                        if (fuguThreadMessageList[pos].messageType == FuguAppConstant.IMAGE_MESSAGE && (rowType == MESSAGE_DELETED_SELF || rowType == MESSAGE_DELETED_OTHER)) {
                            try {
                                val extensions2 = fuguThreadMessageList[pos].image_url.split(Pattern.quote(".").toRegex()).toTypedArray()
                                var extension2 = extensions2[extensions2.size - 1]
                                if (extension2.toLowerCase() == "png") {
                                    extension2 = "jpg"
                                }
                                val fileName = fuguThreadMessageList[pos].fileName + "_" + fuguThreadMessageList[pos].muid + "." + extension2
                                val filePath = File(getDirectory(extension2) + "/" + fileName)
                                if (filePath.exists()) {
                                    filePath.delete()
                                    fuguMessageAdapter!!.deleteImageFromImageList(fuguThreadMessageList[pos].id.toString())
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        fuguThreadMessageList[pos].rowType = rowType
                        fuguThreadMessageList[pos].message = text
                        fuguMessageAdapter!!.updateThreadMessageList(fuguThreadMessageList)
                        fuguMessageAdapter!!.notifyDataSetChanged()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private val mDeleteChatReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val channelIdReceived = intent.getLongExtra(FuguAppConstant.CHANNEL_ID, -1L)
            if (intent.hasExtra(FuguAppConstant.APP_SECRET_KEY)
                    && intent.getStringExtra(FuguAppConstant.APP_SECRET_KEY) == CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey && channelIdReceived.compareTo(channelId) == 0 && intent.getStringExtra(FuguAppConstant.MESSAGE_UNIQUE_ID) == message!!.uuid) {
                Thread {
                    val messageListLocal = getMessageList(channelId)
                    for (i in messageListLocal.indices.reversed()) {
                        val message = messageListLocal[i]
                        if (message.muid == intent.getStringExtra(FuguAppConstant.MESSAGE_UNIQUE_ID)) {
                            var rowType = message.rowType
                            val text: String
                            if (rowType == TEXT_MESSAGE_SELF || rowType == IMAGE_MESSAGE_SELF || rowType == VIDEO_MESSAGE_SELF || rowType == FILE_MESSAGE_SELF || rowType == MESSAGE_DELETED_SELF) {
                                rowType = MESSAGE_DELETED_SELF
                                text = "You deleted this message. "
                            } else {
                                text = "This message was deleted. "
                                rowType = MESSAGE_DELETED_OTHER
                            }
                            message.rowType = rowType
                            message.message = text
                            message.messageState = 0
                            messageListLocal[i] = message
                            setMessageList(messageListLocal, channelId)
                            val messageType = message.messageType
                            if (messageType == FuguAppConstant.TEXT_MESSAGE) {
                                message.rowType = TEXT_MESSAGE_THREAD
                            } else if (messageType == FuguAppConstant.IMAGE_MESSAGE) {
                                message.rowType = IMAGE_MESSAGE_THREAD
                            } else if (messageType == FuguAppConstant.VIDEO_MESSAGE) {
                                message.rowType = VIDEO_MESSAGE_THREAD
                            } else if (messageType == FuguAppConstant.FILE_MESSAGE) {
                                message.rowType = FILE_MESSAGE_THREAD
                            }
                            runOnUiThread {
                                fuguThreadMessageList[0] = message
                                fuguMessageAdapter!!.updateThreadMessageList(fuguThreadMessageList)
                                fuguMessageAdapter!!.notifyItemChanged(0)
                            }
                            break
                        }
                    }
                }.start()
                isMessageDeleted = true
//                tvUserMessage.setText("This message was deleted");
//                tvUserMessage.setTextColor(getResources().getColor(R.color.deleted_message_color));
//                tvUserMessage.setTypeface(tvUserMessage.getTypeface(), Typeface.ITALIC);
//                ivDeleted.setVisibility(View.VISIBLE);
//                llEmojis.setVisibility(View.GONE);
//                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//                params.addRule(RelativeLayout.ABOVE, R.id.tvCannotReply);
//                params.addRule(RelativeLayout.BELOW, R.id.my_toolbar);
                //scrollView.setLayoutParams(params);
                val view = this@FuguInnerChatActivity.currentFocus
                if (view != null) {
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
                llMessageLayout!!.visibility = View.GONE
                tvCannotReply!!.visibility = View.VISIBLE
                fuguMessageAdapter!!.messageDeleted()
                toolbar!!.menu.clear()
                isMessageInEditMode = false
                ivSend!!.setImageResource(R.drawable.ivsend)
                ivCancelEdit!!.visibility = View.GONE
                ivAttachment!!.visibility = View.VISIBLE
                firstEditMuid = ""
            }
        }
    }

    private fun isMyChannelIdAndThread(messageJson: JSONObject): Boolean {
        return try {
            val messageChannelId = messageJson.getLong(FuguAppConstant.CHANNEL_ID)
            messageChannelId.compareTo(channelId) == 0 && messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID) == muid
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun setUpSocketListeners(callingMethod: String) {
//        init(applicationContext, false) // Changed activity context to application context to avoid memory leak
        initSocketConnection(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                workspacesInfo!!.enUserId,
                CommonData.getCommonResponse().data.userInfo.userId,
                CommonData.getCommonResponse().data.userInfo.userChannel, callingMethod, false,
                CommonData.getCommonResponse().data.userInfo.pushToken)
        setSocketListeners(object : SocketClientCallback {
            override fun onUnpinChat(messageJson: String) {}
            override fun onPinChat(messageJson: String) {}
            override fun onCalling(messageJson: String) {}
            override fun onPresent(messageJson: String) {}
            override fun onErrorReceived(messageJson: String) {}
            override fun onAudioCallReceived(messageJson: String) {}
            override fun onVideoCallReceived(messageJson: String) {}
            override fun onThreadMessageSent(message: String) {
                try {
                    val messageJson = JSONObject(message)
                    if (isMyChannelIdAndThread(messageJson)) {
                        unsentMessageMap.remove(messageJson.getString(FuguAppConstant.THREAD_MUID))
                        setUnsentMessageMapByChannel(channelId, unsentMessageMap)
                        val pos = mMessageIndices[messageJson.getString(FuguAppConstant.THREAD_MUID)]!!
                        fuguThreadMessageList[pos].messageStatus = FuguAppConstant.MESSAGE_SENT
                        fuguThreadMessageList[pos].uploadStatus = FuguAppConstant.UploadStatus.UPLOAD_COMPLETED.uploadStatus
                        fuguThreadMessageList[pos].downloadStatus = FuguAppConstant.DownloadStatus.DOWNLOAD_COMPLETED.downloadStatus
                        setUnsentMessageMapByChannel(channelId, unsentMessageMap)
                        runOnUiThread {
                            fuguMessageAdapter!!.notifyItemChanged(pos)
                            if (conversation.chat_type != 2) {
                                if (showToolbar) {
                                    try {
                                        val receivedUserId = messageJson.getLong(FuguAppConstant.USER_ID)
                                        if (receivedUserId.compareTo(java.lang.Long.valueOf(myUserId)) == 0) {
                                            toolbar!!.menu.clear()
                                            toolbar!!.inflateMenu(R.menu.unfollow_menu)
                                        }
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                            when (fuguThreadMessageList.size - 1 - dateItemCount) {
                                0 ->                                     //tvReplyCount.setText("No replies");
                                    fuguThreadMessageList[0].threadMessageCount = 0
                                1 ->                                     //tvReplyCount.setText((fuguThreadMessageList.size() - dateItemCount) + " reply");
                                    fuguThreadMessageList[0].threadMessageCount = fuguThreadMessageList.size - dateItemCount - 1
                                -1 -> {
                                }
                                else ->                                     //tvReplyCount.setText((fuguThreadMessageList.size() - dateItemCount) + " replies");
                                    fuguThreadMessageList[0].threadMessageCount = fuguThreadMessageList.size - dateItemCount - 1
                            }
                            fuguMessageAdapter!!.notifyItemChanged(0)
                        }
                    }
                    if (unsentMessageMap.size > 0) {
                        sendFirstUnsentMessageOfList()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onMessageSent(messageJson: String) {}
            override fun onMessageReceived(messageJson: String) {}
            override fun onTypingStarted(messageJson: String) {}
            override fun onTypingStopped(messageJson: String) {}
            override fun onThreadMessageReceived(message: String) {
                var messageJson: JSONObject? = null
                var isNewMessage = true
                var messageIndex = 0
                try {
                    messageJson = JSONObject(message)
                    for (i in fuguThreadMessageList.indices.reversed()) {
                        if (fuguThreadMessageList[i].muid != null && fuguThreadMessageList[i].muid == messageJson.getString(FuguAppConstant.THREAD_MUID)) {
                            isNewMessage = false
                            messageIndex = i
                            break
                        }
                    }
                    if (isMyChannelIdAndThread(messageJson) && isNewMessage) {
                        createMessageObjectAndAddToList(messageJson)
                    } else if (!isMyChannelIdAndThread(messageJson) && isNewMessage) {
                        fuguThreadMessageList[messageIndex].id = messageJson.getLong("thread_message_id")
                    }
                    if ((rvMessages.layoutManager as LinearLayoutManager?)!!.findLastVisibleItemPosition() == rvMessages.layoutManager!!.itemCount - 2) {
                        runOnUiThread { rvMessages.scrollToPosition(rvMessages.layoutManager!!.itemCount - 1) }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onReadAll(messageJson: String) {}
            override fun onPollVoteReceived(messageJson: String) {}
            override fun onChannelSubscribed() {
                if (unsentMessageMap.size > 0) {
                    sendFirstUnsentMessageOfList()
                }
            }

            override fun onReactionReceived(message: String) {
                var messageJson: JSONObject? = null
                try {
                    messageJson = JSONObject(message)
                    if (isMyChannelIdAndThread(messageJson!!) && myUserId != messageJson!!.getString(FuguAppConstant.USER_ID)) {
                        if (messageJson!!.has(FuguAppConstant.THREAD_MUID)) {
                            clickedEmojiMuid = messageJson!!.getString(FuguAppConstant.THREAD_MUID)
                            if (clickedEmojiMuid == fuguThreadMessageList[0].uuid) {
                                emojiReceived(messageJson!!.getString(FuguAppConstant.USER_REACTION_EMOJI), false, messageJson!![FuguAppConstant.USER_ID].toString(), messageJson!!.getString(FuguAppConstant.FULL_NAME), false)
                            } else {
                                emojiReceived(messageJson!!.getString(FuguAppConstant.USER_REACTION_EMOJI),
                                        false, messageJson!![FuguAppConstant.USER_ID].toString(),
                                        messageJson!!.getString(FuguAppConstant.FULL_NAME), true)
                            }
                        } else {
                            clickedEmojiMuid = messageJson!!.getString("muid")
                            emojiReceived(messageJson!!.getString(FuguAppConstant.USER_REACTION_EMOJI), false, messageJson!![FuguAppConstant.USER_ID].toString(), messageJson!!.getString(FuguAppConstant.FULL_NAME), false)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onConnect() {}
            override fun onDisconnect() {}
            override fun onConnectError(socket: Socket, message: String) {}
            override fun onTaskAssigned(messageJson: String) {}
            override fun onMeetScheduled(messageJson: String) {}
            override fun onUpdateNotificationCount(messageJson: String) {}
        })
        subscribeChannel(channelId)
    }

    private fun apiFollowThread(isFollow: Int) {
        val commonParams = com.skeleton.mvp.retrofit.CommonParams.Builder()
                .add(FuguAppConstant.MESSAGE_UNIQUE_ID, muid)
                .add(FuguAppConstant.CHANNEL_ID, channelId)
                .add(FuguAppConstant.EN_USER_ID, workspacesInfo!!.enUserId)
                .add("following_status", isFollow)
                .build()
        com.skeleton.mvp.retrofit.RestClient.getApiInterface().editFollowingStatus(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.map)
                .enqueue(object : ResponseResolver<CommonResponse>(this@FuguInnerChatActivity, true, false) {
                    override fun success(commonResponse: CommonResponse) {
                        toolbar!!.menu.clear()
                        if (conversation.chat_type != 2) {
                            if (showToolbar) {
                                if (isFollow == 1) {
                                    toolbar!!.inflateMenu(R.menu.unfollow_menu)
                                } else {
                                    toolbar!!.inflateMenu(R.menu.follow_menu)
                                }
                            }
                        }
                    }

                    override fun failure(error: APIError) {}
                })
    }

    private fun createMessageObjectAndAddToList(messageJson: JSONObject) {
        try {
            var url: String? = ""
            if (messageJson.getInt(FuguAppConstant.MESSAGE_TYPE) == FuguAppConstant.IMAGE_MESSAGE) {
                url = messageJson.optString("image_url", "")
            } else if (messageJson.getInt(FuguAppConstant.MESSAGE_TYPE) == FuguAppConstant.FILE_MESSAGE) {
                url = messageJson.optString("url", "")
            }
            val sentUuid = try {
                messageJson.getString("thread_muid")
            } catch (e: Exception) {
                UUID.randomUUID().toString()
            }
            var urlFile: String? = ""
            urlFile = if (messageJson.has("url")) {
                messageJson.getString("url")
            } else {
                ""
            }
            messageType = messageJson.getInt(FuguAppConstant.MESSAGE_TYPE)
            var isSelf = TEXT_MESSAGE_SELF
            if (messageType == FuguAppConstant.TEXT_MESSAGE) {
                isSelf = TEXT_MESSAGE_OTHER
                if (messageJson.getLong(FuguAppConstant.USER_ID).toString() == myUserId) {
                    isSelf = TEXT_MESSAGE_SELF
                }
            } else if (messageType == FuguAppConstant.IMAGE_MESSAGE) {
                isSelf = IMAGE_MESSAGE_OTHER
                if (messageJson.getLong(FuguAppConstant.USER_ID).toString() == myUserId) {
                    isSelf = IMAGE_MESSAGE_SELF
                }
            } else if (messageType == FuguAppConstant.VIDEO_MESSAGE) {
                isSelf = VIDEO_MESSAGE_OTHER
                if (messageJson.getLong(FuguAppConstant.USER_ID).toString() == myUserId) {
                    isSelf = VIDEO_MESSAGE_SELF
                }
            } else if (messageType == FuguAppConstant.FILE_MESSAGE) {
                isSelf = FILE_MESSAGE_OTHER
                if (messageJson.getLong(FuguAppConstant.USER_ID).toString() == myUserId) {
                    isSelf = FILE_MESSAGE_SELF
                }
            }
            val messageToBeAdded = Message(0, messageJson.getString(FuguAppConstant.FULL_NAME), messageJson.getString(FuguAppConstant.USER_ID).toLong(),
                    messageJson.getString(TEXTMESSAGE),
                    messageJson.getString(FuguAppConstant.DATE_TIME),
                    isSelf,
                    FuguAppConstant.MESSAGE_SENT,
                    fuguThreadMessageList.size,
                    url,
                    if (messageJson.has("thumbnail_url")) messageJson.getString("thumbnail_url") else "",
                    messageJson.getInt(FuguAppConstant.MESSAGE_TYPE),
                    if (onSubscribe == 1) true else true,
                    sentUuid, conversation.chat_type, "",
                    "")
            val formattedStrings = getFormattedString(messageJson.getString(TEXTMESSAGE))
            messageToBeAdded.alteredMessage = formattedStrings[0]
            messageToBeAdded.formattedMessage = formattedStrings[1]
            if (messageJson.getInt(FuguAppConstant.MESSAGE_TYPE) == 12) {
                messageToBeAdded.rowType = 12
            }
            messageToBeAdded.isThreadMessageClick = false
            if (messageJson.has("image_height")) {
                messageToBeAdded.imageHeight = messageJson.getInt("image_height")
            }
            if (messageJson.has("image_width")) {
                messageToBeAdded.imageHeight = messageJson.getInt("image_width")
            }
            if (messageJson.has("file_name")) {
                messageToBeAdded.fileName = messageJson.getString("file_name")
            }
            if (messageJson.has("file_size")) {
                messageToBeAdded.fileSize = messageJson.getString("file_size")
            }
            if (messageToBeAdded.messageType == FuguAppConstant.IMAGE_MESSAGE) {
                messageToBeAdded.uploadStatus = 3
                messageToBeAdded.downloadStatus = 0
            }
            if (messageToBeAdded.messageType == FuguAppConstant.VIDEO_MESSAGE) {
                messageToBeAdded.uploadStatus = 0
                messageToBeAdded.downloadStatus = 0
                messageToBeAdded.fileSize = messageJson.getString(FuguAppConstant.FILE_SIZE)
                messageToBeAdded.fileName = messageJson.getString(FuguAppConstant.FILE_NAME)
            }
            if (messageToBeAdded.messageType == FuguAppConstant.FILE_MESSAGE) {
                messageToBeAdded.uploadStatus = 0
                messageToBeAdded.downloadStatus = 0
                messageToBeAdded.fileSize = messageJson.getString(FuguAppConstant.FILE_SIZE)
                messageToBeAdded.fileName = messageJson.getString(FuguAppConstant.FILE_NAME)
                messageToBeAdded.url = messageJson.getString("url")
                val link = messageJson.getString("url").split("\\.".toRegex()).toTypedArray()
                messageToBeAdded.fileExtension = link[link.size - 1]
            }
            if (messageJson.has("thread_message_id")) {
                messageToBeAdded.id = messageJson.getLong("thread_message_id")
            }
            messageToBeAdded.threadMuid = messageJson.getString("muid")
            mMessageIndices[messageJson.getString("thread_muid")] = fuguThreadMessageList.size
            fuguThreadMessageList.add(messageToBeAdded)
            runOnUiThread {
                fuguMessageAdapter!!.updateThreadMessageList(fuguThreadMessageList)
                fuguMessageAdapter!!.notifyItemInserted(fuguThreadMessageList.size)
                if (conversation.chat_type != 2) {
                    if (showToolbar) {
                        try {
                            val receivedUserId = messageJson.getLong(FuguAppConstant.USER_ID)
                            if (receivedUserId.compareTo(java.lang.Long.valueOf(myUserId)) == 0) {
                                toolbar!!.menu.clear()
                                toolbar!!.inflateMenu(R.menu.unfollow_menu)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
                when (fuguThreadMessageList.size - 1 - dateItemCount) {
                    0 ->
                        //tvReplyCount.setText("No replies");
                        fuguThreadMessageList[0].threadMessageCount = 0
                    -1 -> {
                    }
                    else ->
                        //tvReplyCount.setText((fuguThreadMessageList.size() - dateItemCount) + " replies");
                        fuguThreadMessageList[0].threadMessageCount = fuguThreadMessageList.size - dateItemCount - 1
                }
                fuguMessageAdapter!!.notifyItemChanged(0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stateChangeListeners() {
        etMsg!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.isNotEmpty()) {
                    ivSend!!.isClickable = true
                    ivSend!!.alpha = 1f
                } else {
                    ivSend!!.isClickable = false
                    ivSend!!.alpha = 0.5f
                }
            }

            private var timer = Timer()
            private val DELAY: Long = 3000
            override fun afterTextChanged(editable: Editable) {
                if (isTyping != FuguAppConstant.TYPING_STARTED) {
                    // publish start typing event
                    if (channelId > -1 && !etMsg!!.text.toString().isEmpty()) {
                        isTyping = FuguAppConstant.TYPING_STARTED
                    }
                }
                timer.cancel()
                timer = Timer()
                timer.schedule(
                        object : TimerTask() {
                            override fun run() {
//                                stopTyping();
                            }
                        },
                        DELAY
                )
            }
        })
    }

    //Initialize inner chat views
    private fun initViews() {
        conversation.businessName = intent.getStringExtra("BUSINESS_NAME")
        if (intent.hasExtra("chatType")) {
            conversation.chat_type = intent.getIntExtra("chatType", 2)
        }
        rvEmoji = findViewById(R.id.rvEmoji)
        viewTranslucent = findViewById(R.id.viewTranslucent)
        rvMentions = findViewById(R.id.rv_mentions)
        llMention = findViewById(R.id.ll_mentions_layout)
        ivEmoji = findViewById(R.id.ivEmoji)
        etMsg = findViewById(R.id.etMsg)
        rlRoot = findViewById(R.id.rlRoot)
        ivAttachment = findViewById(R.id.ivAttachment)
        ivSend = findViewById(R.id.ivSend)
        llRoot = findViewById(R.id.llRoot)
        pbLoading = findViewById(R.id.pbLoading)
        tvCannotReply = findViewById(R.id.tvCannotReply)
        llMessageLayout = findViewById(R.id.llMessageLayout)
        toolbar = findViewById(R.id.my_toolbar)
        tvTitle = findViewById(R.id.tvTitle)
        ivCancelEdit = findViewById(R.id.ivCancelEdit)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        rvMessages = findViewById(R.id.rvMessages)
        rvMessages.layoutManager = LinearLayoutManager(this)
        findViewById<View>(R.id.ivBack).setOnClickListener { v: View? -> onBackPressed() }
        setListeners()
        if (TextUtils.isEmpty(conversation.businessName)) {
            conversation.businessName = ""
        }
        if (muid == null) {
            muid = ""
        }
        fuguMessageAdapter = MessageAdapter(fuguThreadMessageList, this, conversation.businessName, channelId, rvMessages, conversation.chat_type, -1L, myName, "", muid!!)
        rvMessages.adapter = fuguMessageAdapter
        fetchIntentData()
        try {
            fuguMessageAdapter!!.updateUserType(userType)
        } catch (e: Exception) {
        }
        toolbar?.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.follow -> apiFollowThread(1)
                R.id.unfollow -> AlertDialog.Builder(this@FuguInnerChatActivity)
                        .setMessage("Are you sure you want to unfollow this thread?")
                        .setPositiveButton("Yes") { dialog: DialogInterface?, which: Int -> apiFollowThread(0) }.setNegativeButton("No") { dialog: DialogInterface?, which: Int -> }.show()
                else -> {
                }
            }
            false
        })
    }

    private fun setTime(tvImageTime: TextView, sentAtUtc: String) {
        tvImageTime.text = DateUtils.getTime(dateUtil.convertToLocal(sentAtUtc))
    }

    fun getEmojiByUnicode(unicode: Int): String {
        return String(Character.toChars(unicode))
    }

    fun initClickedEmojiMuid(muid: String) {
        if (threadBottomSheetFragment != null) {
            threadBottomSheetFragment!!.dismiss()
        }
        if (!TextUtils.isEmpty(muid)) {
            clickedEmojiMuid = muid
        }
    }

    //Set onCLick and OnLong click events
    private fun setListeners() {
        ivAttachment!!.setOnClickListener(this)
        ivSend!!.setOnClickListener(this)
        ivCancelEdit!!.setOnClickListener(this)
        for (i in emojiTexts.indices) {
            emojiTexts[i].setOnClickListener(this)
            emojiTexts[i].setOnLongClickListener(this)
            if (emojiLayouts.size > i) {
                emojiLayouts[i].setOnLongClickListener(this)
                emojiLayouts[i].setOnClickListener(this)
            }
            if (emojiCounts.size > i) {
                emojiCounts[i].setOnClickListener(this)
                emojiCounts[i].setOnLongClickListener(this)
            }
        }
        stateChangeListeners()
        etMsg!!.setCommitListener { uri: Uri, sendUri: Uri? ->
            val dimens = ArrayList<Int>()
            val localDate = DateUtils.getFormattedDate(Date())
            val fuguFileDetails = fuguImageUtils!!.saveFile(uri, FuguAppConstant.FILE_TYPE_MAP["gif"], channelId, localDate)
            if (sendUri != null) {
                showGifImageDialogUriLink(uri, sendUri, this@FuguInnerChatActivity, uri.toString())
            } else {
                showGifImageDialog(uri, uri, this@FuguInnerChatActivity, uri.toString(), dimens, fuguFileDetails, fuguFileDetails.filePath)
            }
        }
    }

    private fun startAnimation(position: Int) {
        animation = AnimationUtils.loadAnimation(applicationContext,
                R.anim.emoji_anim)
        emojiTexts[position].startAnimation(animation)
        emojiReceived(emojiTexts[position].text.toString(), true, myUserId, myName, false)
    }

    //fetch intent data from chat adapter
    private fun fetchIntentData() {
        if (getUnsentMessageMapByChannel(channelId) != null) {
            unsentMessageMap = getUnsentMessageMapByChannel(channelId)
        }
        if (intent.hasExtra(TEXTMESSAGE)) {
            message = intent.getSerializableExtra(TEXTMESSAGE) as Message
            pushMuid = message!!.muid
            //setUpMessageView(false);
            muid = message!!.uuid
            if (message!!.messageType == FuguAppConstant.TEXT_MESSAGE) {
                message!!.rowType = TEXT_MESSAGE_THREAD
            } else if (message!!.messageType == FuguAppConstant.IMAGE_MESSAGE) {
                message!!.rowType = IMAGE_MESSAGE_THREAD
            } else if (message!!.messageType == FuguAppConstant.VIDEO_MESSAGE) {
                message!!.rowType = VIDEO_MESSAGE_THREAD
            } else if (message!!.messageType == FuguAppConstant.FILE_MESSAGE) {
                message!!.rowType = FILE_MESSAGE_THREAD
            }
            fuguThreadMessageList.add(0, message!!)
            runOnUiThread {
                fuguMessageAdapter!!.updateThreadMessageList(fuguThreadMessageList)
                fuguMessageAdapter!!.notifyDataSetChanged()
                Log.e("List size", fuguThreadMessageList.size.toString() + "")
            }
        } else {
            if (intent.hasExtra(FuguAppConstant.MESSAGE_UNIQUE_ID)) {
                muid = intent.getStringExtra(FuguAppConstant.MESSAGE_UNIQUE_ID)
                pushMuid = muid!!
            }
        }
        if (intent.hasExtra(FuguAppConstant.DELETED_MESSAGE)) {
            if (fuguMessageAdapter != null) {
                fuguMessageAdapter!!.messageDeleted()
            }
            //message.setRowType(THREAD_MESSAGE_DELETED_OTHER);
            showToolbar = false
            //            llEmojis.setVisibility(View.GONE);
            llMessageLayout!!.visibility = View.GONE
            tvCannotReply!!.visibility = View.VISIBLE
            val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            params.addRule(RelativeLayout.ABOVE, R.id.tvCannotReply)
            params.addRule(RelativeLayout.BELOW, R.id.my_toolbar)
        }
        if (intent.hasExtra("userType")) {
            userType = intent.getIntExtra("userType", 1)
        }
    }

    fun loadJSONFromAsset(): ArrayList<Emoji> {
        val emojiList = ArrayList<Emoji>()
        emojiMap = CommonData.getEmojiMap()
        for (i in emojiMap.size - 1 downTo -0 + 1) {
            emojiList.add(Emoji(emojiMap[i], emojiMap[i]))
        }
        return emojiList
    }

    private fun openReactionDialog(reaction: UserReaction) {
        val manager = this@FuguInnerChatActivity.supportFragmentManager
        ft = manager.beginTransaction()
        val newFragment: DialogFragment = EmojiReactionsDialog.newInstance(0, reaction)
        newFragment.show(ft!!, "ReactionFragment")
    }

    private fun showImageDialog(activity: Context, imgUrl: String) {
        val manager = this@FuguInnerChatActivity.supportFragmentManager
        ft = manager.beginTransaction()
        val newFragment = ImageDialog.newInstance(0, imgUrl)
        newFragment.show(ft!!, "ImageFragment")
    }

    private fun publishEmojiOnFaye(emoji: String, isThread: Boolean) {
        publishEmoji(emoji, isThread)
    }

    private fun publishEmoji(emoji: String, isThread: Boolean) {
        if (isNetworkAvailable()) {
            try {
                val localDate = DateUtils.getFormattedDate(Date())
                val messageJson = JSONObject()
                messageJson.put(FuguAppConstant.FULL_NAME, myName)
                messageJson.put(FuguAppConstant.CHANNEL_ID, channelId)
                messageJson.put(FuguAppConstant.DATE_TIME, DateUtils.getInstance().convertToUTC(localDate))
                if (isThread) {
                    // if(message.getUuid().equals(clickedEmojiMuid)){
                    if (fuguThreadMessageList[0].uuid == clickedEmojiMuid) {
                        messageJson.put("is_thread_reaction", false)
                        messageJson.put(FuguAppConstant.MESSAGE_UNIQUE_ID, message!!.uuid)
                        //messageJson.put("thread_muid", clickedEmojiMuid);
                    } else {
                        messageJson.put("is_thread_reaction", true)
                        messageJson.put(FuguAppConstant.MESSAGE_UNIQUE_ID, message!!.uuid)
                        messageJson.put("thread_muid", clickedEmojiMuid)
                    }
                } else {
                    messageJson.put("is_thread_reaction", false)
                    messageJson.put(FuguAppConstant.MESSAGE_UNIQUE_ID, message!!.uuid)
                }
                messageJson.put(FuguAppConstant.CHANNEL_ID, channelId)
                messageJson.put(FuguAppConstant.IS_TYPING, FuguAppConstant.TYPING_SHOW_MESSAGE)
                val devicePayload = JSONObject()
                devicePayload.put(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(this@FuguInnerChatActivity))
                devicePayload.put(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER)
                devicePayload.put(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME)
                devicePayload.put(FuguAppConstant.DEVICE_DETAILS, com.skeleton.mvp.fugudatabase.CommonData.deviceDetails(this@FuguInnerChatActivity))
                messageJson.put("device_payload", devicePayload)
                messageJson.put(FuguAppConstant.USER_ID, myUserId)
                messageJson.put(FuguAppConstant.USER_TYPE, FuguAppConstant.ANDROID_USER)
                messageJson.put(FuguAppConstant.USER_REACTION_EMOJI, emoji)
                reactOnMessage(messageJson)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onBackPressed() {
        if (isMessageInEditMode) {
            cancelEditing()
        } else {
            if (emojiPopup != null && emojiPopup!!.isShowing) {
                emojiPopup!!.dismiss()
                isDialogOpened = false
                ivEmoji!!.setImageResource(R.drawable.ic_happiness)
            } else {
                val view = this.currentFocus
                if (view != null) {
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
                if (!isChatDeleted) {
                    updateChatScreenData()
                } else {
                    val intent = Intent()
                    intent.putExtra("BUNDLE", ArrayList<Any>())
                    intent.putExtra("only_admin_can_message", only_admin_can_message)
                    intent.putExtra("userRole", userRole)
                    intent.putExtra(Intent.EXTRA_TEXT, FuguAppConstant.CLEAR_INTENT)
                    setResult(RESULT_OK, intent)
                }
                finish()
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ivAttachment -> {
                val bottomSheetFragment = AttachmentSheetFragment().newInstance(0, 2, true)
                bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
            }
            R.id.ivCancelEdit -> cancelEditing()
            R.id.ivSend -> if (!isMessageInEditMode) {
//                    if (!mClient.getChannels().contains("/" + channelId)) {
//                        mClient.subscribeChannel("/" + channelId);
//                    }
                runOnUiThread {
                    if (fuguThreadMessageList.size - 1 < 20) {
                        rvMessages.smoothScrollToPosition(fuguThreadMessageList.size - 1)
                    } else {
                        rvMessages.scrollToPosition(fuguThreadMessageList.size - 1)
                    }
                }
                if (channelId > -1 && !etMsg!!.text.toString().trim { it <= ' ' }.isEmpty()) {
                    isTyping = FuguAppConstant.TYPING_SHOW_MESSAGE
                    threadMuid = UUID.randomUUID().toString()
                    val finalmessage = getTaggedMessage(etMsg, mentions)
                    publishOnFaye(finalmessage, FuguAppConstant.TEXT_MESSAGE, getString(R.string.fugu_empty), getString(R.string.fugu_empty), null, FuguAppConstant.NOTIFICATION_DEFAULT, threadMuid)
                    unsentMessageMap[threadMuid] = fuguThreadMessageList[fuguThreadMessageList.size - 1]
                    setUnsentMessageMapByChannel(channelId, unsentMessageMap)
                }
            } else {
                if (!TextUtils.isEmpty(firstEditMuid)) {
                    if (isNetworkAvailable()) {
                        apiEditMessage()
                    }
                } else {
                    firstEditMuid = ""
                    etMsg!!.setText("")
                    isMessageInEditMode = false
                    ivAttachment!!.visibility = View.VISIBLE
                    ivCancelEdit!!.visibility = View.GONE
                    ivSend!!.setImageResource(R.drawable.ivsend)
                }
            }
        }
    }

    private fun cancelEditing() {
        try {
            val message = fuguThreadMessageList[mMessageIndices[firstEditMuid]!!]
            message.editMode = false
            fuguThreadMessageList[mMessageIndices[firstEditMuid]!!] = message
            fuguMessageAdapter!!.updateThreadMessageList(fuguThreadMessageList)
            fuguMessageAdapter!!.notifyItemChanged(mMessageIndices[firstEditMuid]!!)
            etMsg!!.setText("")
            firstEditMuid = ""
            isMessageInEditMode = false
            ivAttachment!!.visibility = View.VISIBLE
            ivCancelEdit!!.visibility = View.GONE
            ivSend!!.setImageResource(R.drawable.ivsend)
        } catch (e: Exception) {
            val message = fuguThreadMessageList[0]
            message.editMode = false
            fuguThreadMessageList[0] = message
            fuguMessageAdapter!!.updateThreadMessageList(fuguThreadMessageList)
            fuguMessageAdapter!!.notifyItemChanged(0)
            etMsg!!.setText("")
            firstEditMuid = ""
            isMessageInEditMode = false
            ivAttachment!!.visibility = View.VISIBLE
            ivCancelEdit!!.visibility = View.GONE
            ivSend!!.setImageResource(R.drawable.ivsend)
        }
    }

    private fun apiEditMessage() {
        KeyboardUtil.toggleKeyboardVisibility(this)
        val commonParams = com.skeleton.mvp.retrofit.CommonParams.Builder()
        commonParams.add(FuguAppConstant.EN_USER_ID, workspacesInfo!!.enUserId)
        commonParams.add(FuguAppConstant.MESSAGE_UNIQUE_ID, muid)
        if (firstEditMuid != muid) {
            commonParams.add(FuguAppConstant.THREAD_MUID, firstEditMuid)
        } else {
            commonParams.add(FuguAppConstant.MESSAGE_UNIQUE_ID, firstEditMuid)
        }
        commonParams.add(FuguAppConstant.CHANNEL_ID, channelId)
        commonParams.add(TEXTMESSAGE, getTaggedMessage(etMsg, mentions))
        commonParams.add(FuguAppConstant.FORMATTED_MESSAGE, getFormattedString(getTaggedMessage(etMsg, mentions))[1])
        if (mentionsArrayList.size != 0) {
            taggedUsers = ArrayList()
            for (mention in mentionsArrayList) {
                if (getTaggedMessage(etMsg, mentions).contains(mention.mentionName)) {
                    if (!taggedUsers!!.contains(mention.userId.toInt())) {
                        taggedUsers!!.add(mention.userId.toInt())
                    }
                }
            }
        }
        if (taggedUsers != null && taggedUsers!!.size != 0) {
            val jsonArrayTaggedUsers = JSONArray()
            for (id in taggedUsers!!) {
                jsonArrayTaggedUsers.put(id)
            }
            commonParams.add(FuguAppConstant.TAGGED_USERS, jsonArrayTaggedUsers)
            if (jsonArrayTaggedUsers.toString().contains("-1")) {
                commonParams.add("tagged_all", true)
            }
        }
        com.skeleton.mvp.retrofit.RestClient.getApiInterface().editMessage(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), workspacesInfo!!.fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                .enqueue(object : ResponseResolver<CommonResponse?>(this, true, false) {
                    override fun success(commonResponse: CommonResponse?) {
                        val message = fuguThreadMessageList[mMessageIndices[firstEditMuid]!!]
                        message.message = getTaggedMessage(etMsg, mentions)
                        message.formattedMessage = getFormattedString(getTaggedMessage(etMsg, mentions))[1]
                        message.alteredMessage = getFormattedString(getTaggedMessage(etMsg, mentions))[0]
                        message.editMode = false
                        taggedUsers = ArrayList()
                        mentionsArrayList = ArrayList()
                        message.messageState = 4
                        fuguThreadMessageList[mMessageIndices[firstEditMuid]!!] = message
                        fuguMessageAdapter!!.updateThreadMessageList(fuguThreadMessageList)
                        fuguMessageAdapter!!.notifyDataSetChanged()
                        etMsg!!.setText("")
                        firstEditMuid = ""
                        isMessageInEditMode = false
                        ivAttachment!!.visibility = View.VISIBLE
                        ivCancelEdit!!.visibility = View.GONE
                        ivSend!!.setImageResource(R.drawable.ivsend)
                    }

                    override fun failure(error: APIError) {
                        val alert = AlertDialog.Builder(this@FuguInnerChatActivity)
                        alert.setMessage(error.message)
                        alert.setPositiveButton("Ok", null)
                        alert.show()
                        cancelEditing()
                    }
                })
    }

    private fun getTaggedMessage(emojiGifEditText: EmojiGifEditText?, mentions: Mentions?): String {
        val initialMessage = emojiGifEditText!!.text.toString().trim { it <= ' ' }
        val removeAmp = initialMessage.replace("&".toRegex(), "&amp;")
        val removeLt = removeAmp.replace("<".toRegex(), "&lt;")
        val removeGt = removeLt.replace(">".toRegex(), "&gt;")
        val removeQuotes = removeGt.replace("\"".toRegex(), "&quot;")
        var removsinglequote = removeQuotes.replace("'".toRegex(), "&#39;")
        val actionableMentionList = ArrayList<Mention>()
        actionableMentionList.addAll(mentionsArrayList)
        if (mentions != null) {
            for (i in actionableMentionList.indices) {
                if (removsinglequote.contains(actionableMentionList[i].mentionName)) {
                    val prefix = "<a style=\"color=#007bff;text-decoration:none\" contenteditable=\"false\"  data-uid=\"" + actionableMentionList[i].userId + "\"    href=\"" + "mention://" + actionableMentionList[i].userId + "\" class=\"tagged-agent\">"
                    val postfix = "</a>"

                    // Commenting it out to solve the bug of @Everybody getting untagged on editing the message
//                    String prefix_everybody = "<a style=\"color=#007bff;text-decoration:none\" contenteditable=\"false\" data-uid=\"" + actionablementionList.get(i).getUserId() + "\" href=\"" + "mention://" + actionablementionList.get(i).getUserId() + "\">";
//                    if (!actionablementionList.get(i).getMentionName().equals("Everyone")) {
//                        removsinglequote = removsinglequote.replace(actionablementionList.get(i).getMentionName(), prefix + actionablementionList.get(i).getMentionName() + postfix);
//                    } else {
//                        removsinglequote = removsinglequote.replace(actionablementionList.get(i).getMentionName(), prefix_everybody + actionablementionList.get(i).getMentionName() + postfix);
//                    }
                    removsinglequote = removsinglequote.replace(actionableMentionList[i].mentionName.toString(), prefix + actionableMentionList[i].mentionName + postfix)
                } else {
                    mentionsArrayList.removeAt(i)
                }
            }
        }
        return removsinglequote
    }

    override fun onLongClick(v: View): Boolean {
        when (v.id) {
            R.id.emojiLl, R.id.emojiLl2, R.id.emojiLl3, R.id.emojiLl4, R.id.tvEmoji, R.id.tvEmoji2, R.id.tvEmoji3, R.id.tvEmoji4, R.id.tvEmojiCount, R.id.tvEmojiCount2, R.id.tvEmojiCount3, R.id.tvEmojiCount4 -> openReactionDialog(message!!.userReaction)
        }
        return true
    }

    override fun getClickedEmoji(unicode: String, isEmoji: Boolean, muid: String) {
        viewTranslucent!!.performClick()
        if (!TextUtils.isEmpty(muid)) {
            clickedEmojiMuid = muid
            isThread = true
        }
        if (isEmoji) {
            emojiReceived(unicode, true, myUserId, myName, isThread)
        } else {
            emojiReceived(getEmojiByUnicode(unicode.toInt(16)), true, myUserId, myName, isThread)
        }
    }

    private fun emojiReceived(emoji: String, isToBePublished: Boolean, userId: String, fullName: String, isThread: Boolean) {
        var selectedPos = 0
        var alreadyReacted = false
        var emojiExists = false
        val isAlreadyReactedByMe = false
        var sentEmpty = false
        var emojiList: MutableList<Reaction?> = ArrayList()
        if (!isThread) {
            if (message!!.userReaction != null) {
                emojiList = message!!.userReaction.reaction
            }
        } else {
            for (fuguItemLoop in fuguThreadMessageList.indices) {
                val uniqueId = fuguThreadMessageList[fuguItemLoop].uuid
                if (!TextUtils.isEmpty(uniqueId) && uniqueId == clickedEmojiMuid) {
                    selectedPos = fuguItemLoop
                    if (fuguThreadMessageList[fuguItemLoop].userReaction != null) {
                        emojiList = fuguThreadMessageList[fuguItemLoop].userReaction.reaction
                        break
                    }
                }
            }
        }
        //Dismiss Dialog Fragment
        val prev = supportFragmentManager.findFragmentByTag("EmojiFragment")
        if (prev != null) {
            val df = prev as DialogFragment
            df.dismiss()
        }

        //case 1: not already reacted and selected emoji does not exist      ---Add Emoji
        //case 2: not already reacted and selected emoji exist               ---Update already added emoji
        //case 3: already reacted and selected emoji does not exist          ---Remove old reaction and add new
        //case 4: already reacted and selected emoji from dialog exists      ---Remove emoji
        //case 5: already reacted and selected emoji from already added list ---Remove old update already added emoji
        if (!TextUtils.isEmpty(emoji)) {
            //check if user has already reacted if yes set "alreadyReacted = true"
            for (reaction in emojiList.indices) {
                if (emojiList[reaction]!!.users.contains(userId)) {
                    alreadyReacted = true
                    break
                }
            }

            //check if emoji already exists if yes set "emojiExists="true"
            for (user in emojiList.indices) {
                if (emojiList[user]!!.reaction == emoji) {
                    emojiExists = true
                }
            }
            var users: MutableList<String?> = ArrayList()
            var fullNames: MutableList<String?> = ArrayList()
            if (!alreadyReacted && !emojiExists) {
                val reaction = Reaction()
                users.add(userId)
                fullNames.add(fullName)
                reaction.reaction = emoji
                reaction.users = users
                reaction.fullNames = fullNames
                emojiList.add(reaction)
            } else if (!alreadyReacted && emojiExists) {
                for (i in emojiList.indices) {
                    if (emojiList[i]!!.reaction == emoji) {
                        if (emojiList[i]!!.users != null) {
                            users = emojiList[i]!!.users
                            fullNames = emojiList[i]!!.fullNames
                        }
                        users.add(userId)
                        fullNames.add(fullName)
                        emojiList[i]!!.users = users
                        emojiList[i]!!.fullNames = fullNames
                        emojiList[i]!!.reaction = emoji
                    }
                }
            } else if (alreadyReacted && !emojiExists) {
                for (i in emojiList.indices) {
                    if (emojiList[i]!!.users != null) {
                        users = emojiList[i]!!.users
                        fullNames = emojiList[i]!!.fullNames
                    }
                    if (users.contains(userId)) {
                        users.remove(userId)
                        fullNames.remove(fullName)
                        emojiList[i]!!.users = users
                        emojiList[i]!!.fullNames = fullNames
                        if (emojiList[i]!!.users.size == 0) {
                            emojiList.removeAt(i)
                        }
                        break
                    }
                }
                val reaction = Reaction()
                users = ArrayList()
                fullNames = ArrayList()
                users.add(userId)
                fullNames.add(fullName)
                reaction.reaction = emoji
                reaction.users = users
                reaction.fullNames = fullNames
                emojiList.add(reaction)
            } else if (alreadyReacted && emojiExists) {
                var emojiPos = -1
                for (i in emojiList.indices) {
                    if (emojiList[i]!!.reaction == emoji) {
                        if (emojiList[i]!!.users.contains(userId)) {
                            emojiPos = i
                        }
                    }
                }
                if (emojiPos > -1) {
                    sentEmpty = true
                    for (i in emojiList.indices) {
                        if (emojiList[i]!!.users != null) {
                            users = emojiList[i]!!.users
                            fullNames = emojiList[i]!!.fullNames
                        }
                        if (users.contains(userId)) {
                            users.remove(userId)
                            fullNames.remove(fullName)
                            emojiList[i]!!.users = users
                            emojiList[i]!!.fullNames = fullNames
                            if (emojiList[i]!!.users.size == 0) {
                                emojiList.removeAt(i)
                            }
                            break
                        }
                    }
                } else {
                    for (i in emojiList.indices) {
                        if (emojiList[i]!!.users != null) {
                            users = emojiList[i]!!.users
                            fullNames = emojiList[i]!!.fullNames
                        }
                        if (users.contains(userId)) {
                            users.remove(userId)
                            fullNames.remove(fullName)
                            emojiList[i]!!.users = users
                            emojiList[i]!!.fullNames = fullNames
                            if (emojiList[i]!!.users.size == 0) {
                                emojiList.removeAt(i)
                            }
                            break
                        }
                    }
                    var hasEmoji = false
                    var pos = -1
                    for (i in emojiList.indices) {
                        if (emojiList[i]!!.reaction == emoji) {
                            hasEmoji = true
                            pos = i
                        }
                    }
                    if (hasEmoji) {
                        users = emojiList[pos]!!.users
                        fullNames = emojiList[pos]!!.fullNames
                        users.add(userId)
                        fullNames.add(fullName)
                        emojiList[pos]!!.users = users
                        emojiList[pos]!!.fullNames = fullNames
                    } else {
                        val reaction = Reaction()
                        users.add(userId)
                        fullNames.add(fullName)
                        reaction.reaction = emoji
                        reaction.users = users
                        reaction.fullNames = fullNames
                        emojiList.add(reaction)
                    }
                }
            }
        } else {
            if (!TextUtils.isEmpty(userId)) {
                for (i in emojiList.indices) {
                    if (emojiList[i]!!.users.contains(userId)) {
                        val users = emojiList[i]!!.users
                        val fullNames = emojiList[i]!!.fullNames
                        users.remove(userId)
                        users.remove(fullName)
                        if (emojiList[i]!!.users.size == 0) {
                            emojiList.removeAt(i)
                        }
                    }
                }
            }
        }
        var userPos = -1
        for (i in emojiList.indices) {
            if (emojiList[i]!!.users.contains(myUserId)) {
                userPos = i
            }
        }
        val ran = Random()
        val random = ran.nextInt(3)
        if (userPos > 3) {
            Collections.swap(emojiList, random, userPos)
        }
        reactedPosition = -1
        if (!isThread) {
            if (message!!.userReaction != null) {
                message!!.userReaction.reaction = emojiList
            } else {
                val userReaction = UserReaction()
                userReaction.reaction = emojiList
                message!!.userReaction = userReaction
            }
        }
        runOnUiThread {
            if (!isThread) {
                //setEmojis(message.getUserReaction());
            }
        }
        Log.i("selected position", selectedPos.toString())
        if (!isThread || selectedPos == 0) {
            val fuguMessageList = getMessageList(channelId)
            for (i in fuguMessageList.indices) {
                if (fuguMessageList[i].muid == message!!.muid) {
                    val userReaction = UserReaction()
                    userReaction.reaction = emojiList
                    message!!.userReaction = userReaction
                    fuguMessageList[i].userReaction = userReaction
                    fuguThreadMessageList[selectedPos].userReaction = userReaction
                    break
                }
            }
            setMessageList(fuguMessageList, channelId)
            val finalSelectedPos = selectedPos
            runOnUiThread {
//                    fuguMessageAdapter.notifyDataSetChanged();
                fuguMessageAdapter!!.notifyItemChanged(finalSelectedPos)
            }
        } else {
            if (fuguThreadMessageList[selectedPos].userReaction == null) {
                val userReaction = UserReaction()
                userReaction.reaction = emojiList
                fuguThreadMessageList[selectedPos].userReaction = userReaction
            } else {
                fuguThreadMessageList[selectedPos].userReaction.reaction = emojiList
            }
            val finalSelectedPos = selectedPos
            runOnUiThread { //                    fuguMessageAdapter.notifyDataSetChanged();
                fuguMessageAdapter!!.notifyItemChanged(finalSelectedPos)
            }
        }
        if (isToBePublished) {
            if (sentEmpty) {
                publishEmojiOnFaye("", isThread)
            } else {
                publishEmojiOnFaye(emoji, isThread)
            }
        }
    }

    fun openReactionDialogLocation(muid: String, location: IntArray) {
        if (!TextUtils.isEmpty(muid)) {
            clickedEmojiMuid = muid
        }
        val emojiAdapter = BottomSheetEmojiAdapter(loadJSONFromAsset(), this, muid)
        val gridLayoutManager: GridLayoutManager = object : GridLayoutManager(this, 7) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        rvEmoji!!.layoutManager = gridLayoutManager
        rvEmoji!!.adapter = emojiAdapter
        val params2 = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                Utils.dpToPx(this, 50f))
        rvEmoji!!.layoutParams = params2
        params2.setMargins(50, location[1] - 200, 50, 0)
        rvEmoji!!.visibility = View.VISIBLE
        viewTranslucent!!.visibility = View.VISIBLE
        viewTranslucent!!.setOnClickListener { v: View? ->
            rvEmoji!!.visibility = View.GONE
            viewTranslucent!!.visibility = View.GONE
        }
    }

    fun openDialog(muid: String) {
        isThread = true
        clickedEmojiMuid = muid
        EmojiFragment().show(supportFragmentManager, "EmojiFragment")
    }

    fun selectImage() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
//        fuguImageUtils.showImageChooser(OPEN_CAMERA_ADD_IMAGE, OPEN_GALLERY_ADD_IMAGE, SELECT_FILE, SELECT_AUDIO, FuguAppConstant.SELECT_VIDEO, FuguAppConstant.START_POLL, false, false);
        dialogView = if (isKeyBoardVisible) {
            View.inflate(this, R.layout.gallery_bottom_sheet_with_keyboard, null)
        } else {
            View.inflate(this, R.layout.gallery_bottom_sheet, null)
        }
        dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(dialogView!!)
        val window = dialog!!.window!!
//        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH)
        window.setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM, WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        val wlp = window.attributes
        wlp.gravity = Gravity.BOTTOM
        window.attributes = wlp
        val viewBottom = dialog!!.findViewById<View>(R.id.viewBottom)
        viewBottom.setOnClickListener { view: View? -> dialog!!.dismiss() }
        val rvAttachment: RecyclerView = dialog!!.findViewById(R.id.rvAttachment)
        rvAttachment.layoutManager = GridLayoutManager(this@FuguInnerChatActivity, 3)
        fuguImageUtils!!.setCallbaks(FuguAppConstant.OPEN_CAMERA_ADD_IMAGE, FuguAppConstant.REQUEST_CODE_PICK_IMAGE, FuguAppConstant.REQUEST_CODE_PICK_FILE,
                FuguAppConstant.REQUEST_CODE_PICK_AUDIO, FuguAppConstant.REQUEST_CODE_PICK_VIDEO, FuguAppConstant.START_POLL, true, true)
        val fuguAttachmentAdaper = FuguAttachmentAdapter(this@FuguInnerChatActivity, true, true)
        if (isKeyBoardVisible) {
            fuguAttachmentAdaper.showEmpty(true)
        }
        rvAttachment.adapter = fuguAttachmentAdaper
        fuguAttachmentAdaper.setOnAttachListener { action: Int ->
            dialog!!.dismiss()
            when (action) {
                FuguAppConstant.OPEN_CAMERA_ADD_IMAGE -> {
                    fuguImageUtils!!.startCamera()
                }
                FuguAppConstant.REQUEST_CODE_PICK_IMAGE -> {
                    val intent1 = Intent(this@FuguInnerChatActivity, ImagePickActivity::class.java)
                    startActivityForResult(intent1, FuguAppConstant.REQUEST_CODE_PICK_IMAGE)
                }
                FuguAppConstant.REQUEST_CODE_PICK_AUDIO -> {
                    val intent3 = Intent(this@FuguInnerChatActivity, AudioPickActivity::class.java)
                    startActivityForResult(intent3, FuguAppConstant.REQUEST_CODE_PICK_AUDIO)
                }
                FuguAppConstant.REQUEST_CODE_PICK_VIDEO -> {
                    val intent2 = Intent(this@FuguInnerChatActivity, VideoPickActivity::class.java)
                    startActivityForResult(intent2, FuguAppConstant.REQUEST_CODE_PICK_VIDEO)
                }
                FuguAppConstant.REQUEST_CODE_PICK_FILE -> {
                    val intent4 = Intent(this@FuguInnerChatActivity, NormalFilePickActivity::class.java)
                    intent4.putExtra(FuguAppConstant.SUFFIX, arrayOf("txt", "xlsx", "xls", "doc", "docX", "ppt", "pptx", "pdf", "ODT", "apk", "zip", "CSV", "SQL", "PSD"))
                    startActivityForResult(intent4, FuguAppConstant.REQUEST_CODE_PICK_FILE)
                }
                FuguAppConstant.START_POLL -> {
                    fuguImageUtils!!.openCreatePollActivity()
                }
            }
        }
        dialog!!.setOnShowListener { dialogInterface: DialogInterface? -> revealShow(dialogView, true, null) }
        dialog!!.setOnKeyListener { dialogInterface: DialogInterface?, i: Int, keyEvent: KeyEvent? ->
            if (i == KeyEvent.KEYCODE_BACK) {
                revealShow(dialogView, false, dialog)
                return@setOnKeyListener true
            }
            false
        }
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setCancelable(true)
        dialog!!.setCanceledOnTouchOutside(true)
        dialog!!.show()
    }

    private fun revealShow(dialogView: View?, b: Boolean, dialog: Dialog?) {
        val view = dialogView!!.findViewById<LinearLayout>(R.id.dialog)
        val w = view.width
        val h = view.height
        val endRadius = Math.hypot(w.toDouble(), h.toDouble()).toFloat()
        val cx = ivAttachment!!.x + ivAttachment!!.width / 2
        val cy = ivAttachment!!.y + ivAttachment!!.height + 56
        if (b) {
            val revealAnimator = ViewAnimationUtils.createCircularReveal(view, Math.round(cx) + 40, Math.round(cy) + 400, 0f, endRadius)
            view.visibility = View.VISIBLE
            revealAnimator.duration = 450
            revealAnimator.start()
        } else {
            var anim: Animator? = null
            anim = if (isKeyBoardVisible) {
                ViewAnimationUtils.createCircularReveal(view, Math.round(cx) + 40, Math.round(cy) - 100, endRadius, 0f)
            } else {
                ViewAnimationUtils.createCircularReveal(view, Math.round(cx) + 40, Math.round(cy) + 400, endRadius, 0f)
            }
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    dialog!!.dismiss()
                    view.visibility = View.INVISIBLE
                }
            })
            anim.duration = 450
            anim.start()
        }
    }

    fun makeTextViewResizable(tv: TextView, maxLine: Int, expandText: String, viewMore: Boolean) {
        if (tv.tag == null) {
            tv.tag = tv.text
        }
        val vto = tv.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val obs = tv.viewTreeObserver
                obs.removeGlobalOnLayoutListener(this)
                if (maxLine == 0) {
                    val lineEndIndex = tv.layout.getLineEnd(0)
                    val text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1).toString() + " " + expandText
                    tv.text = text
                    tv.movementMethod = LinkMovementMethod.getInstance()
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.text.toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE)
                } else if (maxLine > 0 && tv.lineCount >= maxLine) {
                    val lineEndIndex = tv.layout.getLineEnd(maxLine - 1)
                    val text = tv.text.subSequence(0, lineEndIndex - expandText.length).toString() + " " + expandText
                    tv.text = text
                    tv.movementMethod = LinkMovementMethod.getInstance()
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.text.toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE)
                } else {
                    val lineEndIndex = tv.layout.getLineEnd(tv.layout.lineCount - 1)
                    val text = tv.text.subSequence(0, lineEndIndex).toString() + " " + expandText
                    tv.text = text
                    tv.movementMethod = LinkMovementMethod.getInstance()
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.text.toString()), tv, lineEndIndex, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE)
                }
            }
        })
    }

    private fun addClickablePartTextViewResizable(strSpanned: Spanned, tv: TextView, maxLine: Int, spanableText: String, viewMore: Boolean): SpannableStringBuilder {
        val str = strSpanned.toString()
        val ssb = SpannableStringBuilder(strSpanned)
        if (str.contains(spanableText)) {
            ssb.setSpan(object : MySpannable(false) {
                override fun onClick(widget: View) {
                    if (viewMore) {
                        tv.layoutParams = tv.layoutParams
                        tv.setText(tv.tag.toString(), TextView.BufferType.SPANNABLE)
                        tv.invalidate()
                        makeTextViewResizable(tv, -1, "See Less", false)
                    } else {
                        tv.layoutParams = tv.layoutParams
                        tv.setText(tv.tag.toString(), TextView.BufferType.SPANNABLE)
                        tv.invalidate()
                        makeTextViewResizable(tv, 3, ".. See More", true)
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length, 0)
        }
        return ssb
    }

    private fun getDirectory(extension: String): String? {
        return try {
            var filePath = Environment.getExternalStorageDirectory().toString() + File.separator + FuguAppConstant.APP_NAME_SHORT +
                    File.separator + com.skeleton.mvp.fugudatabase.CommonData.getWorkspaceResponse(workspacesInfo!!.fuguSecretKey).workspaceName.replace(" ", "").replace("'s", "") + File.separator + FuguAppConstant.FILE_TYPE_MAP[extension.toLowerCase()]!!.directory
            val filePathArray = filePath.split("/".toRegex()).toTypedArray()
            if (filePathArray[filePathArray.size - 1] == FuguAppConstant.IMAGE) {
                if (workspacesInfo!!.mediaVisibility == 0) {
                    filePath = filePath.replace(FuguAppConstant.IMAGE, FuguAppConstant.PRIVATE_IMAGES)
                }
            }
            Log.i("Path", filePath)
            val folder = File(filePath)
            if (!folder.exists()) {
                folder.mkdirs()
            }
            if (filePathArray[filePathArray.size - 1] == FuguAppConstant.IMAGE) {
                if (workspacesInfo!!.mediaVisibility == 0) {
                    val f = File("$filePath/.nomedia")
                    if (!f.exists()) {
                        f.createNewFile()
                    }
                    Log.i("FilePath", filePath)
                }
            }
            filePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun displaySuggestions(display: Boolean) {
        if (display) {
            llMention!!.visibility = View.VISIBLE
            if (imageLlMentions != null) {
                imageLlMentions!!.visibility = View.VISIBLE
            }
        } else {
            llMention!!.visibility = View.GONE
            if (imageLlMentions != null) {
                imageLlMentions!!.visibility = View.GONE
            }
        }
    }

    override fun onProgressUpdate(percentage: Int, mMessageIndex: Int, muid: String) {
        val mIntent = Intent(FuguAppConstant.PROGRESS_INTENT)
        Log.e("TAGProgress", percentage.toString() + "")
        mIntent.putExtra("position", mMessageIndex)
        mIntent.putExtra("progress", percentage)
        mIntent.putExtra("muid", muid)
        mIntent.putExtra("statusUpload", FuguAppConstant.UploadStatus.UPLOAD_IN_PROGRESS.uploadStatus)
        mIntent.putExtra("statusDownload", FuguAppConstant.DownloadStatus.DOWNLOAD_FAILED.downloadStatus)
        LocalBroadcastManager.getInstance(this@FuguInnerChatActivity).sendBroadcast(mIntent)
    }

    override fun onError(percentage: Int, FuguInnerChatActivity: Int, muid: String) {
        val mIntent = Intent(FuguAppConstant.PROGRESS_INTENT)
        mIntent.putExtra("position", FuguInnerChatActivity)
        mIntent.putExtra("progress", percentage)
        mIntent.putExtra("muid", muid)
        mIntent.putExtra("statusUpload", FuguAppConstant.UploadStatus.UPLOAD_FAILED.uploadStatus)
        mIntent.putExtra("statusDownload", FuguAppConstant.DownloadStatus.DOWNLOAD_FAILED.downloadStatus)
        LocalBroadcastManager.getInstance(this@FuguInnerChatActivity).sendBroadcast(mIntent)
    }

    override fun onFinish(percentage: Int, mMessageIndex: Int, muid: String) {
        val mIntent = Intent(FuguAppConstant.PROGRESS_INTENT)
        mIntent.putExtra("position", mMessageIndex)
        mIntent.putExtra("progress", percentage)
        mIntent.putExtra("muid", muid)
        mIntent.putExtra("statusUpload", FuguAppConstant.UploadStatus.UPLOAD_COMPLETED.uploadStatus)
        mIntent.putExtra("statusDownload", FuguAppConstant.DownloadStatus.DOWNLOAD_FAILED.downloadStatus)
        LocalBroadcastManager.getInstance(this@FuguInnerChatActivity).sendBroadcast(mIntent)
    }

    fun starMessage(position: Int, muid: String?, isStarred: Int, location: IntArray?) {
        val commonParams = CommonParams.Builder()
        if (position == 0) {
            commonParams.add("muid", muid)

//            ArrayList<Message> fuguMessageList = ChatDatabase.INSTANCE.getMessageList(channelId);
//            for (int i = 0; i < fuguMessageList.size(); i++) {
//                if (fuguMessageList.get(i).getMuid().equals(fuguThreadMessageList.get(0).getMuid())) {
//                    if(fuguMessageList.get(i).getIsStarred() == 0){
//                        fuguMessageList.get(i).setIsStarred(1);
//                        //message.setIsStarred(0);
//                       // fuguThreadMessageList.get(0).setIsStarred(1);
//                    } else{
//                        fuguMessageList.get(i).setIsStarred(0);
//                        //message.setIsStarred(1);
//                        //fuguThreadMessageList.get(0).setIsStarred(0);
//                    }
//                    break;
//                }}
//            ChatDatabase.INSTANCE.setMessageList(fuguMessageList, channelId);
        } else {
            commonParams.add("thread_muid", muid)
        }
        commonParams.add(FuguAppConstant.CHANNEL_ID, channelId)
        commonParams.add(FuguAppConstant.EN_USER_ID, workspacesInfo!!.enUserId)
        if (isStarred == 1) {
            commonParams.add("is_starred", 0)
        } else {
            commonParams.add("is_starred", 1)
        }
        RestClient.getApiInterface(false).starMessage(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), workspacesInfo!!.fuguSecretKey,
                1, BuildConfig.VERSION_CODE, commonParams.build().map)
                .enqueue(object : ResponseResolver<com.skeleton.mvp.data.model.CommonResponse?>() {
                    override fun success(commonResponse: com.skeleton.mvp.data.model.CommonResponse?) {
                        if (position == 0) {
                            val fuguMessageList = getMessageList(channelId)
                            for (i in fuguMessageList.indices) {
                                if (fuguMessageList[i].muid == fuguThreadMessageList[0].muid) {
                                    if (fuguMessageList[i].isStarred == 0) {
                                        fuguMessageList[i].isStarred = 1
                                        //message.setIsStarred(0);
                                        // fuguThreadMessageList.get(0).setIsStarred(1);
                                    } else {
                                        fuguMessageList[i].isStarred = 0
                                        //message.setIsStarred(1);
                                        //fuguThreadMessageList.get(0).setIsStarred(0);
                                    }
                                    break
                                }
                            }
                            setMessageList(fuguMessageList, channelId)
                        }
                        val message = fuguThreadMessageList[position]
                        if (message.isStarred == 1) {
                            message.isStarred = 0
                            //                            if (rl != null && iv != null) {
//                                rl.removeView(iv);
//                            }
                        } else {
                            message.isStarred = 1
                            //                            rl = findViewById(R.id.my_relative_layout);
//                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 500);
//                            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
//                                    LinearLayout.LayoutParams.WRAP_CONTENT);
//
//                            iv = new ImageView(FuguInnerChatActivity.this);
//                            iv.setImageResource(R.drawable.ic_star_animate_24dp);
//                            iv.setLayoutParams(params);
//                            params2.setMargins(location[0] - 60, location[1] - 1185, 0, 0);
//                            rl.setLayoutParams(params2);
//                            rl.addView(iv);
//                            Animation starAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
//                                    R.anim.star_enlarge);
//                            starAnimation.setAnimationListener(FuguInnerChatActivity.this);
//                            iv.clearAnimation();
//                            iv.startAnimation(starAnimation);
                        }
                        fuguThreadMessageList[position] = message
                        fuguMessageAdapter!!.updateThreadMessageList(fuguThreadMessageList)
                        fuguMessageAdapter!!.notifyItemChanged(position)
                    }

                    override fun failure(error: APIError) {}
                })
    }

    override fun onAnimationStart(animation: Animation) {}
    override fun onAnimationEnd(animation: Animation) {}
    override fun onAnimationRepeat(animation: Animation) {}
    fun forwardMessage(position: Int, muid: String?) {
        val mIntent = Intent(this, ForwardActivity::class.java)
        val forwardMessage = fuguThreadMessageList[position]
        if (TextUtils.isEmpty(forwardMessage.sharableImage_url)) {
            forwardMessage.sharableImage_url = forwardMessage.image_url
        }
        if (TextUtils.isEmpty(forwardMessage.sharableThumbnailUrl)) {
            forwardMessage.sharableThumbnailUrl = forwardMessage.thumbnailUrl
        }
        mIntent.putExtra("MESSAGE", forwardMessage)
        mIntent.putExtra("BUSINESS_NAME", conversation.label)
        mIntent.putExtra("chatType", conversation.chat_type)
        startActivity(mIntent)
    }

    override fun onEmojiClick(emojiUnicode: String) {
        mPhotoEditor!!.addEmoji(emojiUnicode)
    }

    override fun onColorChanged(colorCode: Int) {
        mPhotoEditor!!.brushColor = colorCode
    }

    override fun onOpacityChanged(opacity: Int) {
        mPhotoEditor!!.setOpacity(opacity)
    }

    override fun onBrushSizeChanged(brushSize: Int) {
        mPhotoEditor!!.brushSize = brushSize.toFloat()
    }

    override fun onEditTextChangeListener(rootView: View, text: String, colorCode: Int) {
        val textEditorDialogFragment = TextEditorDialogFragment.show(this, text, colorCode)
        textEditorDialogFragment.setOnTextEditorListener { inputText, colorCode -> mPhotoEditor!!.editText(rootView, inputText, colorCode) }
    }

    override fun onAddViewListener(viewType: ViewType, numberOfAddedViews: Int) {
        if (numberOfAddedViews > 0) {
            imgUndo!!.visibility = View.VISIBLE
        } else {
            imgUndo!!.visibility = View.GONE
        }
    }

    override fun onRemoveViewListener(viewType: ViewType, numberOfAddedViews: Int) {
        if (numberOfAddedViews == 0) {
            imgUndo!!.visibility = View.GONE
        }
    }

    override fun onStartViewChangeListener(viewType: ViewType) {}
    override fun onStopViewChangeListener(viewType: ViewType) {}
    fun openMessageInfo(b: Boolean, muid: String?, position: Int) {
        val intent = Intent(this@FuguInnerChatActivity, MessageInformationActivity::class.java)
        intent.putExtra(FuguAppConstant.CHANNEL_ID, channelId)
        intent.putExtra(TEXTMESSAGE, fuguThreadMessageList[position])
        intent.putExtra(FuguAppConstant.IS_THREAD_MESSAGE, b)
        startActivity(intent)
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

    fun openScreenFromSheet(id: Int?) {
        when (id) {
            R.id.llCamera -> {
                fuguImageUtils!!.startCamera()
            }
            R.id.llGallery -> {
                val intent1 = Intent(this@FuguInnerChatActivity, ImagePickActivity::class.java)
                startActivityForResult(intent1, FuguAppConstant.REQUEST_CODE_PICK_IMAGE)
            }
            R.id.llAudio -> {
                val intent3 = Intent(this@FuguInnerChatActivity, AudioPickActivity::class.java)
                startActivityForResult(intent3, FuguAppConstant.REQUEST_CODE_PICK_AUDIO)
            }
            R.id.llVideo -> {
                val intent2 = Intent(this@FuguInnerChatActivity, VideoPickActivity::class.java)
                startActivityForResult(intent2, FuguAppConstant.REQUEST_CODE_PICK_VIDEO)
            }
            R.id.llFiles -> {
                val intent4 = Intent(this@FuguInnerChatActivity, NormalFilePickActivity::class.java)
                intent4.putExtra(FuguAppConstant.SUFFIX, arrayOf("txt", "xlsx", "xls", "doc", "docX", "ppt", ".pptx", "pdf",
                        "ODT", "apk", "zip", "CSV", "SQL", "PSD"))
                startActivityForResult(intent4, FuguAppConstant.REQUEST_CODE_PICK_FILE)
            }
            R.id.llPoll -> {
                fuguImageUtils!!.openCreatePollActivity()
            }
        }
    }

    open inner class MySpannable(isUnderline: Boolean) : ClickableSpan() {
        private var isUnderline = true
        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = isUnderline
            ds.color = Color.parseColor("#1b76d3")
        }

        override fun onClick(widget: View) {}

        /**
         * Constructor
         */
        init {
            this.isUnderline = isUnderline
        }
    }

    private fun stopTyping() {
        if (isTyping == FuguAppConstant.TYPING_STARTED) {
            isTyping = FuguAppConstant.TYPING_STOPPED
            publishOnFaye(getString(R.string.fugu_empty), FuguAppConstant.TEXT_MESSAGE,
                    getString(R.string.fugu_empty), getString(R.string.fugu_empty), null, FuguAppConstant.NOTIFICATION_DEFAULT, null)
        }
    }

    private fun publishOnFaye(textMessage: String, messageType: Int, url: String, thumbnailUrl: String, fileDetails: FuguFileDetails?, notificationType: Int, uuid: String?) {
        try {
            if (!textMessage.isEmpty() && messageType == FuguAppConstant.TEXT_MESSAGE) {
                addMessageToList(textMessage, messageType, url, thumbnailUrl, null, uuid, ArrayList())
                //                if (mClient.isFayeConnected()) {
//                    addMessageToList(textMessage, messageType, url, thumbnailUrl, null, uuid, new ArrayList<Integer>());
//                } else {
//                    isFayeChannelActive = false;
//                }
//                if (!mClient.isFayeConnected()) {
//                    addMessageToList(textMessage, messageType, url, thumbnailUrl, null, uuid, new ArrayList<Integer>());
//                }
                if (!TextUtils.isEmpty(textMessage)) {
                    publishMessage(textMessage, messageType, url, thumbnailUrl, fileDetails, notificationType, uuid, 0)
                } else {
                    CustomAlertDialog.Builder(this@FuguInnerChatActivity)
                            .setMessage("Error in publishing message to faye")
                            .setPositiveButton("Ok", null)
                            .show()
                }
            } else {
                publishMessage(textMessage, messageType, url, thumbnailUrl, fileDetails, notificationType, uuid, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun apiGetThreadedMessages(muid: String?) {
        val commonParams = com.skeleton.mvp.retrofit.CommonParams.Builder()
                .add("muid", muid)
                .add(FuguAppConstant.EN_USER_ID, workspacesInfo!!.enUserId)
                .add(FuguAppConstant.CHANNEL_ID, channelId)
                .build()
        com.skeleton.mvp.retrofit.RestClient.getApiInterface().getThreadedMessages(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), workspacesInfo!!.fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.map).enqueue(object : ResponseResolver<ThreadedMessagesResponse>(this@FuguInnerChatActivity, false, false) {
            override fun success(threadedMessagesResponse: ThreadedMessagesResponse) {
                toolbar!!.menu.clear()
                fuguThreadMessageList = ArrayList()
                try {
                    userType = threadedMessagesResponse.data.other_user_type
                } catch (e: Exception) {
                }
                if (conversation.chat_type != 2) {
                    if (!TextUtils.isEmpty(threadedMessagesResponse.data.label)) {
                        tvTitle!!.text = threadedMessagesResponse.data.label
                    } else {
                        tvTitle!!.text = "Message"
                    }
                }
                try {
                    fuguMessageAdapter!!.updateUserType(userType)
                } catch (e: Exception) {
                }
                val messageObj = threadedMessagesResponse.data.message
                message = Message()
                message!!.message = messageObj.message
                message!!.sentAtUtc = messageObj.dateTime
                val userReaction = messageObj.userReaction
                messageObj.userReaction.reaction.sortWith(Comparator { one, other -> other.totalCount.compareTo(one.totalCount) })
                val formattedStrings2 = getFormattedString(message!!.message)
                message!!.alteredMessage = formattedStrings2[0]
                message!!.formattedMessage = formattedStrings2[1]
                message!!.userReaction = userReaction
                message!!.fromName = messageObj.fullName
                message!!.id = java.lang.Long.valueOf(messageObj.id.toLong())
                message!!.email = messageObj.email
                message!!.userId = java.lang.Long.valueOf(messageObj.userId.toLong())
                message!!.muid = messageObj.muid
                message!!.messageType = messageObj.messageType
                message!!.thumbnailUrl = messageObj.thumbnailUrl
                message!!.image_url = messageObj.imageUrl
                message!!.url = messageObj.url
                val extensions = messageObj.url.split(Pattern.quote(".").toRegex()).toTypedArray()
                val extension = extensions[extensions.size - 1]
                message!!.fileExtension = extension
                message!!.fileSize = messageObj.fileSize
                message!!.fileName = messageObj.fileName
                message!!.userImage = messageObj.userImage
                message!!.threadMessageCount = threadedMessagesResponse.data.threadMessage.size
                message!!.messageState = messageObj.messageState
                message!!.userType = messageObj.userType
                message!!.filePath = com.skeleton.mvp.fugudatabase.CommonData.getCachedFilePath(messageObj.url, muid)
                val fuguMessageList = getMessageList(channelId)
                for (i in fuguMessageList.indices) {
                    if (fuguMessageList[i].uuid == messageObj.muid) {
                        message!!.isStarred = fuguMessageList[i].isStarred
                        //message.setDownloadStatus(fuguMessageList.get(i).getDownloadStatus());
                        val tempMessage = intent.getSerializableExtra(TEXTMESSAGE) as Message?
                        val isFromChatActivity = intent.getIntExtra("isFromChatActivity", 0)
                        if (isFromChatActivity == 1) {
                            message!!.downloadStatus = tempMessage?.downloadStatus ?: 1
                            message!!.downloadId = intent.getIntExtra("downloadId", 0)
                        } else {
                            if (!TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getCachedFilePath(fuguMessageList[i].url, fuguMessageList[i].muid))) {
                                message!!.filePath = com.skeleton.mvp.fugudatabase.CommonData.getCachedFilePath(fuguMessageList[i].url, fuguMessageList[i].muid)
                                message!!.downloadStatus = FuguAppConstant.DownloadStatus.DOWNLOAD_COMPLETED.downloadStatus
                                message!!.uploadStatus = FuguAppConstant.DownloadStatus.DOWNLOAD_COMPLETED.downloadStatus
                            } else {
                                message!!.downloadStatus = FuguAppConstant.DOWNLOAD_FAILED
                            }
                        }
                        Log.i("downloadStatus", fuguMessageList[i].downloadStatus.toString())
                        message!!.uploadStatus = fuguMessageList[i].uploadStatus
                        break
                    }
                }
                if (messageObj.messageState == 0) {
                    llMessageLayout!!.visibility = View.GONE
                    tvCannotReply!!.visibility = View.VISIBLE
                    val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    params.addRule(RelativeLayout.ABOVE, R.id.tvCannotReply)
                    params.addRule(RelativeLayout.BELOW, R.id.my_toolbar)
                } else {
                    if (message!!.userType == FuguAppConstant.UserType.SCRUM_BOT && conversation.chat_type == FuguAppConstant.ChatType.BOT) {
                        llMessageLayout!!.visibility = View.GONE
                    } else {
                        if (intent.hasExtra("only_admin_can_message")) {
                            only_admin_can_message = intent.getBooleanExtra("only_admin_can_message", false)
                            if (!intent.getBooleanExtra("only_admin_can_message", false)) {
                                llMessageLayout!!.visibility = View.VISIBLE
                                tvCannotReply!!.visibility = View.GONE
                            } else if (intent.getBooleanExtra("only_admin_can_message", false) && userRole == "ADMIN") {
                                llMessageLayout!!.visibility = View.VISIBLE
                                tvCannotReply!!.visibility = View.GONE
                            } else {
                                llMessageLayout!!.visibility = View.GONE
                                tvCannotReply!!.visibility = View.VISIBLE
                            }
                        }
                    }
                }
                object : Thread() {
                    override fun run() {
                        super.run()
                        val unreadCountArrayList = com.skeleton.mvp.fugudatabase.CommonData.getNotificationCountList()
                        val unreadCountArrayListFinal = ArrayList<UnreadCount>()
                        try {
                            for (i in unreadCountArrayList.indices) {
                                if (unreadCountArrayList[i].channelId.compareTo(channelId) == 0 && unreadCountArrayList[i].muid == muid && !unreadCountArrayList[i].isTagged
                                        && unreadCountArrayList[i].isThreadMessage) {
                                } else {
                                    unreadCountArrayListFinal.add(unreadCountArrayList[i])
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        com.skeleton.mvp.fugudatabase.CommonData.setNotificationsCountList(unreadCountArrayListFinal)
                    }
                }.start()
                if (conversation.chat_type != 2) {
                    if (showToolbar) {
                        if (threadedMessagesResponse.data.userFollowingStatus == 0) {
                            toolbar!!.inflateMenu(R.menu.follow_menu)
                        } else if (threadedMessagesResponse.data.userFollowingStatus == 1) {
                            toolbar!!.inflateMenu(R.menu.unfollow_menu)
                        } else {
                            if (message!!.userId.compareTo(java.lang.Long.valueOf(myUserId)) == 0) {
                                toolbar!!.inflateMenu(R.menu.unfollow_menu)
                            } else {
                                toolbar!!.inflateMenu(R.menu.follow_menu)
                            }
                        }
                    }
                }
                var tempSentAtUtc = ""
                val threadMessage = threadedMessagesResponse.data.threadMessage
                fuguThreadMessageList = ArrayList()
                when (message!!.messageType) {
                    FuguAppConstant.TEXT_MESSAGE -> {
                        message!!.rowType = TEXT_MESSAGE_THREAD
                    }
                    FuguAppConstant.IMAGE_MESSAGE -> {
                        message!!.rowType = IMAGE_MESSAGE_THREAD
                    }
                    FuguAppConstant.VIDEO_MESSAGE -> {
                        message!!.rowType = VIDEO_MESSAGE_THREAD
                    }
                    FuguAppConstant.FILE_MESSAGE -> {
                        message!!.rowType = FILE_MESSAGE_THREAD
                    }
                }
                fuguThreadMessageList.add(0, message!!)
                runOnUiThread {
                    fuguMessageAdapter!!.updateThreadMessageList(fuguThreadMessageList)
                    fuguMessageAdapter!!.notifyDataSetChanged()
                }
                //}
                for (i in threadMessage.indices) {
                    val datum = threadMessage[i]
                    //                    if (i != 0 || (threadMessage.get(0).getUserId().compareTo(Long.valueOf(workspacesInfo.getUserId())) == 0)) {
                    val date = DateUtils.getDate(dateUtils!!.convertToLocal(datum.dateTime))
                    if (!tempSentAtUtc.equals(date, ignoreCase = true)) {
                        val message = Message()
                        message.sentAtUtc = date
                        message.rowType = HEADER_ITEM
                        mMessageIndices[date] = fuguThreadMessageList.size
                        fuguThreadMessageList.add(message)
                        tempSentAtUtc = date
                        dateItemCount += 1
                        //                        }
                    }
                    var isSelf = TEXT_MESSAGE_SELF
                    if (datum.messageType == FuguAppConstant.TEXT_MESSAGE) {
                        isSelf = TEXT_MESSAGE_OTHER
                        if (datum.userId.toString() == myUserId) {
                            isSelf = TEXT_MESSAGE_SELF
                        }
                    } else if (datum.messageType == FuguAppConstant.IMAGE_MESSAGE) {
                        isSelf = if (datum.userId.toString() == myUserId) {
                            IMAGE_MESSAGE_SELF
                        } else {
                            IMAGE_MESSAGE_OTHER
                        }
                    } else if (datum.messageType == FuguAppConstant.VIDEO_MESSAGE) {
                        isSelf = if (datum.userId.toString() == myUserId) {
                            VIDEO_MESSAGE_SELF
                        } else {
                            VIDEO_MESSAGE_OTHER
                        }
                    } else if (datum.messageType == FuguAppConstant.FILE_MESSAGE) {
                        isSelf = if (datum.userId.toString() == myUserId) {
                            FILE_MESSAGE_SELF
                        } else {
                            FILE_MESSAGE_OTHER
                        }
                    }
                    mMessageIndices[datum.threadMuid] = fuguThreadMessageList.size
                    val message = Message(datum.threadMessageId.toLong(), datum.fullName,
                            datum.userId,
                            datum.message,
                            datum.dateTime,
                            isSelf,
                            FuguAppConstant.MESSAGE_SENT,
                            i,
                            datum.imageUrl,
                            datum.thumbnailUrl,
                            datum.messageType,
                            true,
                            datum.threadMuid,
                            0,
                            datum.email,
                            datum.url)
                    message.isThreadMessageClick = false
                    message.isStarred = datum.isStarred
                    message.fileName = datum.fileName
                    val formattedStrings = getFormattedString(message.message)
                    message.alteredMessage = formattedStrings[0]
                    message.formattedMessage = formattedStrings[1]
                    if (datum.userReaction != null) {
                        val userReactions = datum.userReaction
                        Collections.sort(datum.userReaction.reaction) { one, other -> other.totalCount.compareTo(one.totalCount) }
                        message.userReaction = userReactions
                    }
                    if (message.messageType == FuguAppConstant.IMAGE_MESSAGE) {
                        message.downloadStatus = 0
                        message.uploadStatus = 3
                        message.imageWidth = datum.imageWidth
                        message.imageHeight = datum.imageHeight
                    }
                    if (message.messageType == FuguAppConstant.VIDEO_MESSAGE) {
                        message.fileName = datum.fileName
                        message.fileSize = datum.fileSize
                        message.filePath = com.skeleton.mvp.fugudatabase.CommonData.getCachedFilePath(datum.url, datum.threadMuid)
                    }
                    if (!TextUtils.isEmpty(message.filePath)) {
                        message.uploadStatus = 3
                        message.downloadStatus = 3
                    } else {
                        message.uploadStatus = 0
                        message.downloadStatus = 0
                    }
                    message.fileSize = datum.fileSize
                    message.fileName = datum.fileName
                    message.sharableImage_url = datum.imageUrl
                    message.sharableThumbnailUrl = datum.thumbnailUrl
                    message.userType = datum.userType
                    message.chatType = conversation.chat_type
                    message.messageState = datum.messageState
                    try {
                        if (datum.messageType == FuguAppConstant.FILE_MESSAGE || datum.messageType == FuguAppConstant.VIDEO_MESSAGE) {
                            val link = datum.url.split("\\.".toRegex()).toTypedArray()
                            message.fileExtension = link[link.size - 1]
                            message.fileName = datum.fileName
                            message.fileSize = datum.fileSize
                            if (!TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getCachedFilePath(datum.url, datum.threadMuid))) {
                                message.filePath = com.skeleton.mvp.fugudatabase.CommonData.getCachedFilePath(datum.url, datum.threadMuid)
                                message.downloadStatus = FuguAppConstant.DownloadStatus.DOWNLOAD_COMPLETED.downloadStatus
                                message.uploadStatus = FuguAppConstant.DownloadStatus.DOWNLOAD_COMPLETED.downloadStatus
                            } else {
                                message.downloadStatus = FuguAppConstant.DownloadStatus.DOWNLOAD_FAILED.downloadStatus
                                message.uploadStatus = FuguAppConstant.DownloadStatus.DOWNLOAD_FAILED.downloadStatus
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    if (datum.messageState == 0) {
                        if (isSelf == TEXT_MESSAGE_SELF || isSelf == IMAGE_MESSAGE_SELF || isSelf == VIDEO_MESSAGE_SELF || isSelf == FILE_MESSAGE_SELF) {
                            message.rowType = MESSAGE_DELETED_SELF
                        } else {
                            message.rowType = MESSAGE_DELETED_OTHER
                        }
                        if (message.messageType == FuguAppConstant.IMAGE_MESSAGE) {
                            try {
                                val extensions2 = messageObj.imageUrl.split(Pattern.quote(".").toRegex()).toTypedArray()
                                var extension2 = extensions2[extensions2.size - 1]
                                if (extension2.toLowerCase() == "png") {
                                    extension2 = "jpg"
                                }
                                val fileName = message.fileName + "_" + message.muid + "." + extension2
                                val filePath = File(getDirectory(extension2) + "/" + fileName)
                                if (filePath.exists()) {
                                    filePath.delete()
                                    fuguMessageAdapter!!.deleteImageFromImageList(message.id.toString())
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        fuguThreadMessageList.add(message)
                    } else {
                        fuguThreadMessageList.add(message)
                    }
                }
                val keys = ArrayList(unsentMessageMap.keys)
                for (i in keys.indices) {
                    if (mMessageIndices[keys[i]] == null && !TextUtils.isEmpty(unsentMessageMap[keys[i]]!!.threadMuid)
                            && unsentMessageMap[keys[i]]!!.threadMuid == muid) {
                        mMessageIndices[keys[i]] = fuguThreadMessageList.size
                        if (unsentMessageMap[keys[i]]!!.messageType == FuguAppConstant.IMAGE_MESSAGE) {
                            unsentMessageMap[keys[i]]!!.messageStatus = FuguAppConstant.MESSAGE_IMAGE_RETRY
                            unsentMessageMap[keys[i]]!!.uploadStatus = 0
                            unsentMessageMap[keys[i]]!!.downloadStatus = 0
                        }
                        if (unsentMessageMap[keys[i]]!!.messageType == FuguAppConstant.VIDEO_MESSAGE) {
                            unsentMessageMap[keys[i]]!!.messageStatus = FuguAppConstant.MESSAGE_IMAGE_RETRY
                            unsentMessageMap[keys[i]]!!.uploadStatus = 0
                            unsentMessageMap[keys[i]]!!.downloadStatus = 0
                            unsentMessageMap[keys[i]]!!.rowType = VIDEO_MESSAGE_SELF
                        }
                        fuguThreadMessageList.add(unsentMessageMap[keys[i]]!!)
                    } else {
                        unsentMessageMap.remove(keys[i])
                    }
                }
                setUnsentMessageMapByChannel(channelId, unsentMessageMap)
                if (TextUtils.isEmpty(conversation.businessName)) {
                    conversation.businessName = ""
                }
                Handler().postDelayed({ //swipeRefresh.setRefreshing(false);
                    llRoot!!.visibility = View.VISIBLE
                }, 100)
                fuguMessageAdapter = MessageAdapter(fuguThreadMessageList, this@FuguInnerChatActivity, conversation.businessName, channelId, rvMessages, conversation.chat_type, -1L, myName, "", muid!!)
                rvMessages.adapter = fuguMessageAdapter
                fuguMessageAdapter!!.updateThreadMessageList(fuguThreadMessageList)
                fuguMessageAdapter!!.notifyDataSetChanged()
                pbLoading!!.visibility = View.GONE
                try {
                    fuguMessageAdapter!!.updateUserType(userType)
                } catch (e: Exception) {
                }
                fuguMessageAdapter!!.setOnRetryListener(object : OnRetryListener {
                    override fun onRetry(message: String, file: String, messageIndex: Int, messageType: Int, fileDetails: FuguFileDetails?, uuid: String, thumbnailUrl: String, imageUrl100x100: String, imageUrl: String, url: String, height: Int, width: Int) {
                        if (messageType == FuguAppConstant.IMAGE_MESSAGE) {
                            val dimens = ArrayList<Int>()
                            dimens.add(height)
                            dimens.add(width)
                            if (!TextUtils.isEmpty(imageUrl) && !TextUtils.isEmpty(thumbnailUrl) && imageUrl.contains("https") && thumbnailUrl.contains("http")) {
                                val jsonObject = createFileJson(ArrayList(), message, FuguAppConstant.IMAGE_MESSAGE,
                                        imageUrl, thumbnailUrl,
                                        imageUrl100x100, fileDetails,
                                        muid, messageIndex, java.lang.Long.valueOf(myUserId), myName, channelId, uuid, dimens)
                                if (jsonObject != null) {
                                    sendThreadMessage(jsonObject)
                                }
                            } else {
                                uploadFileServerCall(ArrayList(), message, uuid, file, messageIndex, messageType, fileDetails, dimens)
                            }
                        } else if (messageType == FuguAppConstant.FILE_MESSAGE) {
                            if (!TextUtils.isEmpty(url) && url.contains("http")) {
                                val jsonObject = createFileJson(ArrayList(), message, FuguAppConstant.FILE_MESSAGE,
                                        url, thumbnailUrl,
                                        imageUrl100x100, fileDetails,
                                        muid, messageIndex, java.lang.Long.valueOf(myUserId), myName, channelId, uuid, ArrayList())
                                if (jsonObject != null) {
                                    sendThreadMessage(jsonObject)
                                }
                            } else {
                                uploadFileServerCall(ArrayList(), "", uuid, file, messageIndex, messageType, fileDetails, ArrayList())
                            }
                        } else if (messageType == FuguAppConstant.VIDEO_MESSAGE) {
                            if (!TextUtils.isEmpty(url) && url.contains("http")) {
                                val jsonObject = createFileJson(ArrayList(), message, FuguAppConstant.VIDEO_MESSAGE,
                                        url, thumbnailUrl,
                                        imageUrl100x100, fileDetails,
                                        muid, messageIndex, java.lang.Long.valueOf(myUserId), myName, channelId, uuid, ArrayList())
                                if (jsonObject != null) {
                                    sendThreadMessage(jsonObject)
                                }
                            } else {
                                uploadFileServerCall(ArrayList(), "", uuid, file, messageIndex, messageType, fileDetails, ArrayList())
                            }
                        }
                    }
                })
                etMsg!!.requestFocus()
                if (intent.hasExtra("scroll") && intent.getBooleanExtra("scroll", false)) {
                    runOnUiThread {
                        Handler().postDelayed({
                            if (fuguThreadMessageList.size - 1 < 20) {
                                rvMessages.smoothScrollToPosition(fuguThreadMessageList.size - 1)
                            } else {
                                rvMessages.scrollToPosition(fuguThreadMessageList.size - 1)
                            }
                        }, 300)
                    }
                }
                if (intent.hasExtra("scroll") && intent.getBooleanExtra("scroll", false) && !isFirstScrollDone) {
                    rvMessages.viewTreeObserver.addOnGlobalLayoutListener { ProgressDialog.dismissProgressDialog() }
                } else {
                }
                userRole = threadedMessagesResponse.data.userChannelRole
                if (!threadedMessagesResponse.data.isOnlyAdminsCanMessage) {
                } else if (threadedMessagesResponse.data.isOnlyAdminsCanMessage && userRole == "ADMIN") {
                } else {
                    llMessageLayout!!.visibility = View.GONE
                    tvCannotReply!!.visibility = View.VISIBLE
                    val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    params.addRule(RelativeLayout.ABOVE, R.id.tvCannotReply)
                    params.addRule(RelativeLayout.BELOW, R.id.my_toolbar)
                }
            }

            override fun failure(error: APIError) {
                pbLoading!!.visibility = View.GONE
                //swipeRefresh.setRefreshing(false);
//                pbLoading.setVisibility(View.GONE);
            }
        })
    }

    private fun addMessageToList(messageText: String, messageType: Int, imageUrl: String, thumbnailUrl: String, fileDetails: FuguFileDetails?, uuid: String?, dimens: ArrayList<Int>) {
        try {
            val localDate = DateUtils.getFormattedDate(Date())
            val date = DateUtils.getDate(localDate)
            if (!TextUtils.isEmpty(sentAtUTC) && !sentAtUTC.equals(date, ignoreCase = true)) {
                val message = Message()
                message.sentAtUtc = date
                message.rowType = HEADER_ITEM
                fuguThreadMessageList.add(message)
                sentAtUTC = date
                dateItemCount += 1
            }
            var rowType = TEXT_MESSAGE_SELF
            when (messageType) {
                FuguAppConstant.IMAGE_MESSAGE -> {
                    rowType = IMAGE_MESSAGE_SELF
                }
                FuguAppConstant.VIDEO_MESSAGE -> {
                    rowType = VIDEO_MESSAGE_SELF
                }
                FuguAppConstant.FILE_MESSAGE -> {
                    rowType = FILE_MESSAGE_SELF
                }
            }
            val messageObj = Message(0, myName,
                    java.lang.Long.valueOf(myUserId),
                    messageText,
                    DateUtils.getInstance().convertToUTC(localDate),
                    rowType,
                    FuguAppConstant.MESSAGE_UNSENT,
                    fuguThreadMessageList.size,
                    if (imageUrl.isEmpty()) "" else imageUrl,
                    if (thumbnailUrl.isEmpty()) "" else thumbnailUrl,
                    messageType,
                    true,
                    uuid, conversation.chat_type, CommonData.getCommonResponse().getData().getUserInfo().email,
                    "")
            messageObj.threadMuid = muid
            val formattedStrings = getFormattedString(messageText)
            messageObj.alteredMessage = formattedStrings[0]
            messageObj.formattedMessage = formattedStrings[1]
            if (fileDetails != null) {
                messageObj.fileName = fileDetails.fileName
                messageObj.fileSize = fileDetails.fileSize
                messageObj.fileExtension = fileDetails.fileExtension
            }
            if (CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].userImage != null) {
                messageObj.userImage = CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].userImage
            }
            if (dimens.size == 2) {
                messageObj.imageHeight = dimens[0]
                messageObj.imageWidth = dimens[1]
            }
            taggedUsers!!.clear()
            if (mentionsArrayList.size != 0) {
                taggedUsers = ArrayList()
                for (mention in mentionsArrayList) {
                    if (!taggedUsers!!.contains(mention.userId.toInt())) {
                        taggedUsers!!.add(mention.userId.toInt())
                    }
                }
                messageObj.taggedUsers = taggedUsers
            }
            mMessageIndices[uuid] = fuguThreadMessageList.size
            fuguThreadMessageList.add(messageObj)
            fuguMessageAdapter!!.notifyItemInserted(fuguThreadMessageList.size)
            if (fuguThreadMessageList.size - 1 < 20) {
                rvMessages.smoothScrollToPosition(fuguThreadMessageList.size - 1)
            } else {
                rvMessages.scrollToPosition(fuguThreadMessageList.size - 1)
            }

//            scrollView.post(new Runnable() {
//                @Override
//                public void run() {
//                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//                    scrollView.clearFocus();
//                    etMsg.requestFocus();
//                    etMsg.requestFocusFromTouch();
//                }
//            });
            etMsg!!.setText("")
            //            updateRecycler(true);

            //TODO uncomment
            if (messageType == FuguAppConstant.IMAGE_MESSAGE || messageType == FuguAppConstant.FILE_MESSAGE || messageType == FuguAppConstant.VIDEO_MESSAGE) {
                if (uuid != null) unsentMessageMap[uuid] = fuguThreadMessageList[fuguThreadMessageList.size - 1]
                object : Thread() {
                    override fun run() {
                        super.run()
                        setUnsentMessageMapByChannel(channelId, unsentMessageMap)
                    }
                }.start()

//                if (!mClient.getChannels().contains("/" + channelId)) {
//                    mClient.subscribeChannel("/" + channelId);
//                }
                Handler().postDelayed({ uploadFileServerCall(taggedUsers, messageText, uuid, imageUrl, fuguThreadMessageList.size - 1, messageType, fileDetails, dimens) }, 100)
                //                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        scrollView.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//                                scrollView.clearFocus();
//                                etMsg.requestFocus();
//                                etMsg.requestFocusFromTouch();
//                            }
//                        });
//                    }
//                }, 500);
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Set up mentions
     */
    private fun setUpMentions() {
        mentions = Mentions.Builder(this, etMsg)
                .suggestionsListener(this)
                .queryListener(this)
                .highlightColor(R.color.colorPrimary)
                .build()
    }

    /**
     * Setups the mentions suggestions list. Creates and sets and adapter for
     * the mentions list and sets the on item click listener.
     */
    private fun setupMentionsList() {
        val mentionsList = findViewById<RecyclerView>(R.id.rv_mentions)
        mentionsList.layoutManager = LinearLayoutManager(this)
        usersAdapter = UsersAdapter(this)
        mentionsList.adapter = usersAdapter

        // set on item click listener
        mentionsList.addOnItemTouchListener(RecyclerItemClickListener(this) { view, position ->
            isMentionSelected = true
            val user = usersAdapter!!.getItem(position)
            /*
                 * We are creating a mentions object which implements the
                 * <code>Mentionable</code> interface this allows the library to set the offset
                 * and length of the mention.
                 */if (user != null) {
            val mention = Mention()
            //String name = "<font color='#007BFF' contenteditable='false' >" + "@" + user.getName() + " " + "</font>";
            mention.mentionName = "@" + user.name
            mention.userId = user.userId
            mention.setEmail(user.email)
            mentions!!.insertMention(mention)
            var isAlreadyTagged = false
            for (oldmention in mentionsArrayList) {
                if (java.lang.Long.compare(oldmention.userId, mention.userId) == 0) {
                    isAlreadyTagged = true
                    break
                }
            }
            if (!isAlreadyTagged) {
                mentionsArrayList.add(mention)
            }
        }
        })
    }

    /**
     * Set up mentions in immage recycler view
     */
    private fun setUpMentionsforImage(etMsg: EmojiGifEditText) {
        mentionsInImage = Mentions.Builder(this, etMsg)
                .suggestionsListener(this)
                .queryListener(this)
                .highlightColor(R.color.colorPrimary)
                .build()
    }

    /**
     * Set up mentionsList for Images in Thread message
     */
    private fun setUpMentionsListForImage(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        usersAdapterImage = UsersAdapter(this)
        recyclerView.adapter = usersAdapterImage
        recyclerView.addOnItemTouchListener(RecyclerItemClickListener(this) { view, position ->
            isMentionSelected = true
            val user = usersAdapterImage!!.getItem(position)
            if (user != null) {
                val mention = Mention()
                //String name = "<font color='#007BFF' contenteditable='false' >" + "@" + user.getName() + " " + "</font>";
                mention.mentionName = "@" + user.name
                mention.userId = user.userId
                mention.setEmail(user.email)
                mentionsInImage!!.insertMention(mention)
                var isAlreadyTagged = false
                for (oldmention in mentionsArrayList) {
                    if (java.lang.Long.compare(oldmention.userId, mention.userId) == 0) {
                        isAlreadyTagged = true
                        break
                    }
                }
                if (!isAlreadyTagged) {
                    mentionsArrayList.add(mention)
                }
            }
        })
    }

    /**
     * Fetches members involved
     */
    private fun fetchMembers() {
        if (!fetchMemberCallInProgress) {
            fetchMemberCallInProgress = true
            val commonParams = com.skeleton.mvp.retrofit.CommonParams.Builder()
                    .add(FuguAppConstant.CHANNEL_ID, channelId)
                    .add(FuguAppConstant.EN_USER_ID, workspacesInfo!!.enUserId)
                    .add(FuguAppConstant.GET_DATA_TYPE, "MEMBERS")
                    .add("user_page_start", 0)
                    .build()
            com.skeleton.mvp.retrofit.RestClient.getApiInterface().getGroupInfo(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey,
                    1, BuildConfig.VERSION_CODE, commonParams.map).enqueue(object : ResponseResolver<MediaResponse>(this@FuguInnerChatActivity, false, false) {
                override fun success(getMembersResponse: MediaResponse) {
                    fetchMemberCallInProgress = false
                    userCount = getMembersResponse.data.userCount
                    userPageSize = getMembersResponse.data.userPageSize
                    membersList!!.clear()
                    object : Thread() {
                        override fun run() {
                            super.run()
                            for (data in getMembersResponse.data.chatMembers) {
                                if (java.lang.Long.valueOf(data.userId.toLong()).compareTo(java.lang.Long.valueOf(CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].userId)) != 0 && data.status.toInt() != 0) {
                                    val member = Member(data.fullName, java.lang.Long.valueOf(data.userId.toLong()),
                                            data.email, data.userImage, data.email, data.userType, data.status, data.leaveType)
                                    membersList.add(member)
                                    membersMap[java.lang.Long.valueOf(data.userId.toLong())] = member
                                }
                            }
                            //                            Collections.sort(membersList, new Comparator<Member>() {
//                                public int compare(Member one, Member other) {
//                                    return one.getName().compareTo(other.getName());
//                                }
//                            });
                            membersList.add(0, Member("Everyone", -1L, "", "https://fuguchat.s3.ap-south-1.amazonaws.com/default/WwX5qYGSEb_1518441286074.png", "", 1, "", ""))
                            membersMap[-1L] = Member("Everyone", -1L, "", "https://fuguchat.s3.ap-south-1.amazonaws.com/default/WwX5qYGSEb_1518441286074.png", "", 1, "", "")
                        }
                    }.start()
                }

                override fun failure(error: APIError) {
                    fetchMemberCallInProgress = false
                }
            })
        }
    }

    /**
     * Toggle the mentions list's visibility if there are search results returned for search
     * query. Shows the empty list view
     *
     * @param display boolean   true if the mentions list should be shown or false if
     * the empty suggestions list view should be shown.
     */
    private fun showMentionsList(display: Boolean) {
        llMention!!.visibility = View.VISIBLE
        if (imageLlMentions != null) {
            imageLlMentions!!.visibility = View.VISIBLE
        }
        if (display) {
            rvMentions!!.visibility = View.VISIBLE
            if (imageRecycler != null) {
                imageRecycler!!.visibility = View.VISIBLE
            }
        } else {
            rvMentions!!.visibility = View.GONE
            if (imageRecycler != null) {
                imageRecycler!!.visibility = View.GONE
            }
        }
    }

    //    @kotlin.Throws(JSONException::class)
    private fun publishMessage(textMessage: String, messageType: Int, url: String, thumbnailUrl: String, fileDetails: FuguFileDetails?, notificationType: Int, uuid: String?, position: Int) {
        if (isNetworkAvailable()) {
            val localDate = DateUtils.getFormattedDate(Date())
            //To be shifted
            val messageJson = JSONObject()
            if (notificationType == FuguAppConstant.NOTIFICATION_READ_ALL) {
                messageJson.put(FuguAppConstant.NOTIFICATION_TYPE, notificationType)
                messageJson.put(FuguAppConstant.CHANNEL_ID, channelId)
            } else {
                messageJson.put(FuguAppConstant.FULL_NAME, myName)
                messageJson.put(TEXTMESSAGE, textMessage)
                messageJson.put(FuguAppConstant.FORMATTED_MESSAGE, getFormattedString(textMessage)[1])
                messageJson.put(FuguAppConstant.MESSAGE_TYPE, messageType)
                messageJson.put(FuguAppConstant.DATE_TIME, DateUtils.getInstance().convertToUTC(localDate))
                if (position == 0) {
                    messageJson.put(FuguAppConstant.MESSAGE_INDEX, fuguThreadMessageList.size - 1)
                } else {
                    messageJson.put(FuguAppConstant.MESSAGE_INDEX, position)
                }
                if (uuid != null) {
                    messageJson.put("thread_muid", uuid)
                }
                if (uuid != null) {
                    messageJson.put(FuguAppConstant.MESSAGE_UNIQUE_ID, message!!.uuid)
                }
                messageJson.put("is_thread_message", true)
                if (messageType == FuguAppConstant.IMAGE_MESSAGE && !url.trim { it <= ' ' }.isEmpty() && !thumbnailUrl.trim { it <= ' ' }.isEmpty()) {
                    messageJson.put(FuguAppConstant.IMAGE_URL, url)
                    messageJson.put(FuguAppConstant.THUMBNAIL_URL, thumbnailUrl)
                }
                if (messageType == FuguAppConstant.FILE_MESSAGE && !url.trim { it <= ' ' }.isEmpty()) {
                    messageJson.put("url", url)
                    messageJson.put("file_name", fileDetails!!.fileName)
                    messageJson.put("file_size", fileDetails.fileSize)
                }
                if (messageType == FuguAppConstant.TEXT_MESSAGE) {
                    messageJson.put(FuguAppConstant.IS_TYPING, isTyping)
                    if (taggedUsers != null && taggedUsers!!.size != 0) {
                        val jsonArrayTaggedUsers = JSONArray()
                        for (id in taggedUsers!!) {
                            jsonArrayTaggedUsers.put(id)
                        }
                        messageJson.put(FuguAppConstant.TAGGED_USERS, jsonArrayTaggedUsers)
                        if (jsonArrayTaggedUsers.toString().contains("-1")) {
                            messageJson.put("tagged_all", true)
                        }
                    }
                } else {
                    messageJson.put(FuguAppConstant.IS_TYPING, FuguAppConstant.TYPING_SHOW_MESSAGE)
                }
                messageJson.put(FuguAppConstant.MESSAGE_STATUS, FuguAppConstant.MESSAGE_UNSENT)
                val devicePayload = JSONObject()
                devicePayload.put(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(this@FuguInnerChatActivity))
                devicePayload.put(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER)
                devicePayload.put(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME)
                devicePayload.put(FuguAppConstant.DEVICE_DETAILS, com.skeleton.mvp.fugudatabase.CommonData.deviceDetails(this@FuguInnerChatActivity))
                messageJson.put("device_payload", devicePayload)
            }
            messageJson.put(FuguAppConstant.USER_ID, myUserId)
            messageJson.put(FuguAppConstant.USER_TYPE, FuguAppConstant.ANDROID_USER)
            messageJson.put(FuguAppConstant.CHANNEL_ID, channelId)
            taggedUsers!!.clear()
            mentionsArrayList = ArrayList()
            sendThreadMessage(messageJson)
            if (uuid != null) {
//                    unsentMessageMap.remove(uuid);
            }
            //                CommonData.setUnsentMessageMapByChannel(channelId, unsentMessageMap);
            try {
                if (messageType == FuguAppConstant.IMAGE_MESSAGE) {
//                        unsentMessageMap.remove(uuid);
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            //TODO uncomment

//            if (mClient.isConnectedServer()) {
//                Log.e(FuguInnerChatActivity.class.getSimpleName(), messageJson.toString());
//                if (!mClient.isFayeConnected()) {
//                    mClient.subscribeChannel("/" + channelId);
//                }
//                taggedUsers.clear();
//                mentionsArrayList = new ArrayList<>();
//                mClient.publish(channelId, "/" + String.valueOf(channelId), messageJson);
//                if (uuid != null) {
////                    unsentMessageMap.remove(uuid);
//                }
////                CommonData.setUnsentMessageMapByChannel(channelId, unsentMessageMap);
//                try {
//                    if (messageType == IMAGE_MESSAGE) {
////                        unsentMessageMap.remove(uuid);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if (isTyping == TYPING_SHOW_MESSAGE && messageType == TEXT_MESSAGE) {
//                    isTyping = TYPING_STARTED;
//                }
//            } else {
//                mClient.connectServer();
//            }
            //end to be shifted
        }
    }

    override fun onQueryReceived(query: String) {
        val isApiBeingHit = booleanArrayOf(false)
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
        countDownTimer = object : CountDownTimer(200, 100) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                filteredMembers = ArrayList()
                if (!TextUtils.isEmpty(query)) {
                    val queryLower = query.toLowerCase(Locale.US)
                    if (countDownTimer != null) {
                        countDownTimer!!.cancel()
                    }
                    if (!membersList!!.isEmpty()) {
                        if (queryLower.length > 1 && !queryLower.startsWith(searchPrefix) && userCount > userPageSize) {
                            isApiBeingHit[0] = true
                            apiUserSearch(queryLower, object : UserSearchApi {
                                override fun onSuccess(memberList: ArrayList<Member>) {
                                    isApiBeingHit[0] = false
                                    filteredMembers = ArrayList()
                                    filteredMembers.addAll(memberList)
                                    if (!filteredMembers.isEmpty()) {
                                        usersAdapter!!.clear()
                                        if (usersAdapterImage != null) {
                                            usersAdapterImage!!.clear()
                                        }
                                        usersAdapter!!.setCurrentQuery(query)
                                        if (usersAdapterImage != null) {
                                            usersAdapterImage!!.setCurrentQuery(query)
                                        }
                                        usersAdapter!!.addAll(filteredMembers)
                                        if (usersAdapterImage != null) {
                                            usersAdapterImage!!.addAll(filteredMembers)
                                        }
                                        showMentionsList(true)
                                    } else {
                                        showMentionsList(false)
                                    }
                                }
                            })
                        } else {
                            filteredMembers = ArrayList()
                            for (member in membersList) {
                                if (member.name.toLowerCase().contains(queryLower) && !isMentionSelected) {
                                    filteredMembers.add(member)
                                }
                            }
                            if (!filteredMembers.isEmpty()) {
                                usersAdapter!!.clear()
                                if (usersAdapterImage != null) {
                                    usersAdapterImage!!.clear()
                                }
                                usersAdapter!!.setCurrentQuery(query)
                                if (usersAdapterImage != null) {
                                    usersAdapterImage!!.setCurrentQuery(query)
                                }
                                usersAdapter!!.addAll(filteredMembers)
                                if (usersAdapterImage != null) {
                                    usersAdapterImage!!.addAll(filteredMembers)
                                }
                                showMentionsList(true)
                            } else {
                                showMentionsList(false)
                            }
                        }
                    } else {
                        fetchMembers()
                    }
                }
                if (TextUtils.isEmpty(query)) {
                    isMentionSelected = false
                    filteredMembers = ArrayList()
                    usersAdapter!!.clear()
                    if (usersAdapterImage != null) {
                        usersAdapterImage!!.clear()
                    }
                    usersAdapter!!.setCurrentQuery(query)
                    if (usersAdapterImage != null) {
                        usersAdapterImage!!.setCurrentQuery(query)
                    }
                    usersAdapter!!.addAll(filteredMembers)
                    if (usersAdapterImage != null) {
                        usersAdapterImage!!.addAll(filteredMembers)
                    }
                }
                // show all suggestions in case of empty query ( @ should be there )
                val length = etMsg!!.text!!.length
                if (length != 0 || imageText.length != 0) {
                    val lastChar: Char
                    lastChar = if (length != 0) {
                        etMsg!!.text!![length - 1]
                    } else {
                        imageText[imageText.length - 1]
                    }
                    if (lastChar.toString().equals("@", ignoreCase = true) && query != null && query.trim { it <= ' ' }.isEmpty()) {
                        // load entire list
                        if (membersList != null && !membersList.isEmpty()) {
                            filteredMembers.addAll(membersList)
                            if (!filteredMembers.isEmpty()) {
                                usersAdapter!!.clear()
                                if (usersAdapterImage != null) {
                                    usersAdapterImage!!.clear()
                                }
                                usersAdapter!!.setCurrentQuery(query)
                                if (usersAdapterImage != null) {
                                    usersAdapterImage!!.setCurrentQuery(query)
                                }
                                usersAdapter!!.addAll(filteredMembers)
                                if (usersAdapterImage != null) {
                                    usersAdapterImage!!.addAll(filteredMembers)
                                }
                                showMentionsList(true)
                            } else {
                                showMentionsList(false)
                            }
                        } else {
                            // we dont have members try to fetch again
                            fetchMembers()
                        }
                    }
                }
            }
        }.start()
    }

    private fun getUriFromPath(path: String): Uri {
        return FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", File(path))
    }

    private fun showImageWithMessageDialog(extension: String?, dimens: ArrayList<Int>, uri: Uri, fuguFileDetails: FuguFileDetails) {
        imageWithMessageDialog = Dialog(this@FuguInnerChatActivity, android.R.style.Theme_Translucent_NoTitleBar)
        imageWithMessageDialog!!.setContentView(R.layout.image_message_dialog)
        val emojiGifEditText: EmojiGifEditText = imageWithMessageDialog!!.findViewById(R.id.etMsg)
        val ivSend = imageWithMessageDialog!!.findViewById<ImageView>(R.id.ivSend)
        //        PhotoView ivImage = imageWithMessageDialog.findViewById(R.id.ivImageBig);
        mPhotoEditorView = imageWithMessageDialog!!.findViewById(R.id.photoEditorView)
        val dialogRecyclerView: RecyclerView = imageWithMessageDialog!!.findViewById(R.id.rv_mentions)
        val ivCrop: AppCompatImageView = imageWithMessageDialog!!.findViewById(R.id.iv_crop)
        imgUndo = imageWithMessageDialog!!.findViewById(R.id.ivUndo)
        imgText = imageWithMessageDialog!!.findViewById(R.id.ivText)
        imgEdit = imageWithMessageDialog!!.findViewById(R.id.ivEdit)
        imgEmoji = imageWithMessageDialog!!.findViewById(R.id.ivEmoji)
        mPhotoEditorView = imageWithMessageDialog!!.findViewById(R.id.photoEditorView)
        mPropertiesBSFragment = PropertiesBSFragment()
        mEmojiBSFragment = EmojiBSFragment()
        mEmojiBSFragment!!.setEmojiListener(this)
        mPropertiesBSFragment!!.setPropertiesChangeListener(this)
        mPhotoEditor = PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true)
                .build()
        mPhotoEditor?.setOnPhotoEditorListener(this)

        //setting custom layout to dialog
        val lp = imageWithMessageDialog!!.window?.attributes
        lp?.dimAmount = 1.0f // Dim level. 0.0 - no dim, 1.0 - completely opaque
        imageWithMessageDialog!!.window?.attributes = lp
        imageWithMessageDialog!!.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        imageWithMessageDialog!!.setCancelable(true)
        imageWithMessageDialog!!.setCanceledOnTouchOutside(false)
        ivSend.alpha = 1f

//        LinearLayoutCompat ivImage = dialog.findViewById(R.id.llRoot);
        val options = RequestOptions()
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .fitCenter()
                .priority(Priority.HIGH)
        var exif: ExifInterface? = null
        try {
            exif = ExifInterface(File(getRealPathFromURI(uri)).absolutePath)
            val rotation = exif.getAttribute(ExifInterface.TAG_ORIENTATION)!!.toInt()
            var angle = 0f
            when (rotation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> {
                    angle = 90f
                }
                ExifInterface.ORIENTATION_ROTATE_180 -> {
                    angle = 180f
                }
                ExifInterface.ORIENTATION_ROTATE_270 -> {
                    angle = 270f
                }
            }
            val mat = Matrix()
            mat.postRotate(angle)
            val bmp = BitmapFactory.decodeStream(FileInputStream(File(getRealPathFromURI(uri))), null, null)!!
            val correctBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, mat, true)
            mPhotoEditorView?.source?.setImageBitmap(correctBmp)
            //            ivImage.setBackground(new BitmapDrawable(getResources(), correctBmp));

//            Glide.with(this)
//                    .load(new BitmapDrawable(getResources(), correctBmp))
//                    .into(ivImage);
            imageWithMessageDialog!!.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        } catch (e: IOException) {
            e.printStackTrace()
            mPhotoEditorView?.source?.setImageURI(uri)
        }
        Log.e("Chat Type", conversation.chat_type.toString() + "")
        if (conversation.chat_type == FuguAppConstant.ChatType.DEFAULT_GROUP || conversation.chat_type == FuguAppConstant.ChatType.GENERAL_GROUP || conversation.chat_type == FuguAppConstant.ChatType.PUBLIC_GROUP || conversation.chat_type == FuguAppConstant.ChatType.PRIVATE_GROUP) {
            setUpMentionsforImage(emojiGifEditText)
            setUpMentionsListForImage(dialogRecyclerView)
            imageRecycler = dialogRecyclerView
            imageLlMentions = imageWithMessageDialog!!.findViewById(R.id.ll_mentions_layout)
            imageRecycler!!.visibility = View.VISIBLE
            imageLlMentions?.visibility = View.VISIBLE
            emojiGifEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                    imageText = s.toString()
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    imageText = s.toString()
                }

                override fun afterTextChanged(s: Editable) {
                    imageText = s.toString()
                }
            })
        }
        imgUndo?.setOnClickListener { mPhotoEditor?.undo() }
        imgEmoji?.setOnClickListener { mEmojiBSFragment!!.show(supportFragmentManager, mEmojiBSFragment!!.tag) }
        imgEdit?.setOnClickListener {
            mPhotoEditor?.setBrushDrawingMode(true)
            mPropertiesBSFragment!!.show(supportFragmentManager, mPropertiesBSFragment!!.tag)
        }
        imgText?.setOnClickListener {
            val textEditorDialogFragment = TextEditorDialogFragment.show(this)
            textEditorDialogFragment.setOnTextEditorListener { inputText, colorCode -> mPhotoEditor?.addText(inputText, colorCode) }
        }
        ivSend.setOnClickListener { saveImage(extension, dimens, fuguFileDetails, imageWithMessageDialog!!, emojiGifEditText, mentionsInImage) }
        ivCrop.setOnClickListener {
            CropImage.activity(uri)
                    .start(this@FuguInnerChatActivity)
        }
        imageWithMessageDialog!!.show()
        //
    }

    @SuppressLint("MissingPermission")
    private fun saveImage(extension: String?, dimens: ArrayList<Int>, fuguFileDetails: FuguFileDetails, imageDialog: Dialog, etMsg: EmojiGifEditText, mentionsInImage: Mentions?) {
        if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showImageEditorLoading("Saving...")
            val file = File(fuguFileDetails.filePath)
            try {
                val saveSettings = SaveSettings.Builder()
                        .setClearViewsEnabled(true)
                        .setTransparencyEnabled(true)
                        .build()
                mPhotoEditor!!.saveAsFile(file.absolutePath, saveSettings, object : OnSaveListener {
                    override fun onSuccess(imagePath: String) {
                        hideImageEditorLoading()
                        var fileOutputStream: FileOutputStream? = null
                        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                        var inputStream: InputStream? = null
                        try {
                            inputStream = contentResolver.openInputStream(FileProvider.getUriForFile(this@FuguInnerChatActivity, BuildConfig.APPLICATION_ID + ".provider", File(file.absolutePath)))!!
                            fileOutputStream = FileOutputStream(file)
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                            val buffer = ByteArray(1024)
                            val bytesRead = 0
                            var i = inputStream.read(buffer)
                            while (i > -1) {
                                i = inputStream.read(buffer)
                                fileOutputStream.write(buffer, 0, bytesRead)
                            }
                            fileOutputStream.close()
                            val message = getTaggedMessage(etMsg, mentionsInImage)
                            compressAndSaveImageBitmap(message, extension, dimens, FuguAppConstant.IMAGE_MESSAGE, fuguFileDetails)
                            imageDialog.dismiss()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        try {
                            if (File(com.skeleton.mvp.fugudatabase.CommonData.getImageUri()).exists()) {
                                File(com.skeleton.mvp.fugudatabase.CommonData.getImageUri()).delete()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(exception: Exception) {
                        exception.printStackTrace()
                        hideImageEditorLoading()
                        showSnackbar("Failed to save Image")
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                hideImageEditorLoading()
            }
        }
    }

    private fun showVideoWithMessageDialog(extension: String?, dimens: ArrayList<Int>, uri: Uri, fuguFileDetails: FuguFileDetails) {
        val dialog = Dialog(this@FuguInnerChatActivity, android.R.style.Theme_Translucent_NoTitleBar)
        dialog.setContentView(R.layout.dialog_video_file)
        val emojiGifEditText: EmojiGifEditText = dialog.findViewById(R.id.etMsg)
        val ivSend = dialog.findViewById<ImageView>(R.id.ivSend)
        val dialogRecyclerView: RecyclerView = dialog.findViewById(R.id.rv_mentions)
        //setting custom layout to dialog
        val lp = dialog.window?.attributes
        lp?.dimAmount = 1.0f // Dim level. 0.0 - no dim, 1.0 - completely opaque
        dialog.window?.attributes = lp
        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(false)
        ivSend.alpha = 1f
        var mVideoView = dialog.findViewById<VideoView>(R.id.vMsg)
        val mediaCOntrolled = MediaController(this)
        mVideoView = dialog.findViewById(R.id.vMsg)
        mediaCOntrolled.setAnchorView(mVideoView)
        mediaCOntrolled.setMediaPlayer(mVideoView)
        mediaCOntrolled.bringToFront()
        mVideoView.setMediaController(mediaCOntrolled)
        mediaCOntrolled.isEnabled = true
        mVideoView.setVideoURI(uri)
        mVideoView.requestFocus()
        mVideoView.setZOrderOnTop(true)
        mVideoView.start()
        if (conversation.chat_type == FuguAppConstant.ChatType.DEFAULT_GROUP || conversation.chat_type == FuguAppConstant.ChatType.GENERAL_GROUP || conversation.chat_type == FuguAppConstant.ChatType.PUBLIC_GROUP || conversation.chat_type == FuguAppConstant.ChatType.PRIVATE_GROUP) {
            setUpMentionsforImage(emojiGifEditText)
            setUpMentionsListForImage(dialogRecyclerView)
            imageRecycler = dialogRecyclerView
            imageLlMentions = dialog.findViewById(R.id.ll_mentions_layout)
            imageRecycler!!.visibility = View.VISIBLE
            imageLlMentions?.visibility = View.VISIBLE
            emojiGifEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                    imageText = s.toString()
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    imageText = s.toString()
                }

                override fun afterTextChanged(s: Editable) {
                    imageText = s.toString()
                }
            })
        }
        ivSend.setOnClickListener {
            compressAndSaveImageBitmap(getTaggedMessage(emojiGifEditText, mentionsInImage), extension, dimens, FuguAppConstant.VIDEO_MESSAGE, fuguFileDetails)
            dialog.dismiss()
        }
        dialog.show()
        //
    }

    private fun compressAndSaveImageBitmap(message: String, extension: String?, dimens: ArrayList<Int>, messgeType: Int, fuguFileDetails: FuguFileDetails) {
        try {
            val image = fuguImageUtils!!.compressAndSaveBitmap(this@FuguInnerChatActivity, extension, fuguFileDetails.fileName) //(null, squareEdge);
            if (image == null) {
                Toast.makeText(this@FuguInnerChatActivity, "Could not read from source", Toast.LENGTH_LONG).show()
            } else {
                addMessageToList(message, messgeType, image, image, fuguFileDetails, fuguFileDetails.muid, dimens)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this@FuguInnerChatActivity, "Could not read from source", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) when (requestCode) {
            FuguAppConstant.OPEN_CAMERA_ADD_IMAGE -> {
                val fileName = com.skeleton.mvp.fugudatabase.CommonData.getWorkspaceResponse(CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey).workspaceName.replace(" ".toRegex(), "").replace("'s".toRegex(), "") + "_" + com.skeleton.mvp.fugudatabase.CommonData.getTime() + ".jpg"
                val uri = Uri.fromFile(File(fuguImageUtils!!.getDirectory(FuguAppConstant.FileType.IMAGE_FILE), fileName))
                val dimens = getDropboxIMGSize(uri)
                val localDate = DateUtils.getFormattedDate(Date())
                val date = DateUtils.getDate(localDate)
                val fileDetails = fuguImageUtils!!.saveFile(uri, FuguAppConstant.FILE_TYPE_MAP["jpg"], channelId, localDate)
                showImageWithMessageDialog("jpg", dimens, uri, fileDetails)
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == RESULT_OK) {
                    val resultUri = result.uri
                    val cropDimens = getDropboxIMGSize(resultUri)
                    val cropLocalDate = DateUtils.getFormattedDate(Date())
                    val cropDate = DateUtils.getDate(cropLocalDate)
                    //                        FuguFileDetails cropFileDetails = fuguImageUtils.saveFile(resultUri, FILE_TYPE_MAP.get("jpg"), channelId, cropLocalDate);
//                        if (imageWithMessageDialog != null && imageWithMessageDialog.isShowing()) {
//                            imageWithMessageDialog.dismiss();
//                        }
//                        showImageWithMessageDialog("jpg", cropDimens, resultUri, cropFileDetails);
                    mPhotoEditorView!!.source.setImageURI(resultUri)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                }
            }
            FuguAppConstant.REQUEST_CODE_PICK_IMAGE -> {
                val list: ArrayList<ImageFile> = data!!.getParcelableArrayListExtra(FuguAppConstant.RESULT_PICK_IMAGE)!!
                try {
                    val cursor = this@FuguInnerChatActivity.contentResolver.query(getUriFromPath(list[0].path), null, null, null, null)
                    try {
                        if (cursor != null && cursor.moveToFirst()) {
                            try {
                                val extensions = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)).split("\\.".toRegex()).toTypedArray()
                                extension = extensions[extensions.size - 1].toLowerCase()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    if (extension!!.toLowerCase() == "png") {
                        extension = "jpg"
                        val bitmap = BitmapFactory.decodeFile(list[0].path)
                        val inputStream = contentResolver.openInputStream(FileProvider.getUriForFile(this@FuguInnerChatActivity, BuildConfig.APPLICATION_ID + ".provider", File(list[0].path)))!!
                        val fileOutputStream = FileOutputStream(list[0].path)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                        val buffer = ByteArray(1024)
                        val bytesRead = 0
                        var i = inputStream.read(buffer)
                        while (i > -1) {
                            i = inputStream.read(buffer)
                            fileOutputStream.write(buffer, 0, bytesRead)
                        }
                        fileOutputStream.close()
                    }
                    val dimensGallery = getDropboxIMGSize(getUriFromPath(list[0].path))
                    val localDate2 = DateUtils.getFormattedDate(Date())
                    val date2 = DateUtils.getDate(localDate2)
                    val fileDetails2 = fuguImageUtils!!.saveFile(getUriFromPath(list[0].path), FuguAppConstant.FILE_TYPE_MAP[extension!!.toLowerCase()], channelId, localDate2)
                    if (list.size > 1) {
                        val intent = Intent(this, MultipleImageDisplayActivity::class.java)
                        intent.putExtra(FuguAppConstant.RESULT_PICK_IMAGE, list)
                        startActivityForResult(intent, FuguAppConstant.REQUEST_MULTIPLE_IMAGES)
                    } else {
                        if (extension == "gif") {
                            showGifImageDialog(getUriFromPath(list[0].path), getUriFromPath(list[0].path), this@FuguInnerChatActivity, getUriFromPath(list[0].path).toString(), dimensGallery, fileDetails2, list[0].path)
                        } else {
                            showImageWithMessageDialog(extension, dimensGallery, getUriFromPath(list[0].path), fileDetails2)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Problem in fetching image...", Toast.LENGTH_LONG).show()
                }
            }
            FuguAppConstant.REQUEST_CODE_PICK_VIDEO -> {
                val list2: ArrayList<VideoFile> = data!!.getParcelableArrayListExtra(FuguAppConstant.RESULT_PICK_VIDEO)!!
                val cursor = this@FuguInnerChatActivity.contentResolver.query(getUriFromPath(list2[0].path), null, null, null, null)
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        try {
                            val extensions = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)).split("\\.".toRegex()).toTypedArray()
                            extension = extensions[extensions.size - 1].toLowerCase()
                        } catch (e: Exception) {
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val dimensGallery = getDropboxIMGSize(getUriFromPath(list2[0].path))
                try {
                    val localDate3 = DateUtils.getFormattedDate(Date())
                    val date3 = DateUtils.getDate(localDate3)
                    val fileDetails3 = fuguImageUtils!!.saveFile(getUriFromPath(list2[0].path), FuguAppConstant.FILE_TYPE_MAP[extension!!.toLowerCase()], channelId, localDate3)
                    if (fileDetails3.filePath.isEmpty()) {
                        Toast.makeText(this, "File not found...", Toast.LENGTH_LONG).show()
                    } else {
                        if (fileDetails3.fileSize.contains("KB") || fileDetails3.fileSize.contains("Bytes") || fileDetails3.fileSizeDouble.toDouble() * 1024 * 1024 <= maxUploadSize) {
                            if (supportedFormats.contains(fileDetails3.fileExtension.toLowerCase())) {
                                if (GetMimeTypeFromUrl.getMimeType(fileDetails3.filePath, this) == null) {
                                    if (GetMimeTypeFromUrl.getMimeType(fileDetails3.filePath, this) == null) {
                                        val alert = AlertDialog.Builder(this@FuguInnerChatActivity)
                                        alert.setMessage("File Corrupted")
                                        alert.setPositiveButton("Ok", null)
                                        alert.setCancelable(false)
                                        alert.show()
                                        return
                                    }
                                } else {
                                    showVideoWithMessageDialog(extension, dimensGallery, getUriFromPath(list2[0].path), fileDetails3)
                                }
                            } else {
                                val alert = AlertDialog.Builder(this@FuguInnerChatActivity)
                                alert.setMessage("Please send document file only.")
                                alert.setPositiveButton("Ok", null)
                                alert.setCancelable(false)
                                alert.show()
                            }
                        } else {
                            val alert = AlertDialog.Builder(this@FuguInnerChatActivity)
                            alert.setMessage("File size cannot be greater than " + (maxUploadSize / 1024 / 1024).toInt() + "MB.")
                            alert.setPositiveButton("Ok", null)
                            alert.setCancelable(false)
                            alert.show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            FuguAppConstant.REQUEST_CODE_PICK_FILE -> {
                val list4: ArrayList<NormalFile> = data!!.getParcelableArrayListExtra(FuguAppConstant.RESULT_PICK_FILE)!!
                val cursor3 = this@FuguInnerChatActivity.contentResolver.query(getUriFromPath(list4[0].path), null, null, null, null)
                try {
                    if (cursor3 != null && cursor3.moveToFirst()) {
                        try {
                            val extensions = cursor3.getString(cursor3.getColumnIndex(OpenableColumns.DISPLAY_NAME)).split("\\.".toRegex()).toTypedArray()
                            extension = extensions[extensions.size - 1].toLowerCase()
                        } catch (e: Exception) {
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val localDate3 = DateUtils.getFormattedDate(Date())
                if (FuguAppConstant.FILE_TYPE_MAP[extension!!.toLowerCase()] == null) {
                    extension = "default"
                }
                val fileDetails3 = fuguImageUtils!!.saveFile(getUriFromPath(list4[0].path), FuguAppConstant.FILE_TYPE_MAP[extension!!.toLowerCase()], channelId, localDate3)
                if (fileDetails3.filePath.isEmpty()) {
                    Toast.makeText(this, "File not found...", Toast.LENGTH_LONG).show()
                } else {
                    if (fileDetails3.fileSize.contains("KB") || fileDetails3.fileSize.contains("Bytes") || fileDetails3.fileSizeDouble.toDouble() * 1024 * 1024 <= maxUploadSize) {
                        addMessageToList(getString(R.string.fugu_empty), FuguAppConstant.FILE_MESSAGE, fileDetails3.filePath, "", fileDetails3, UUID.randomUUID().toString(), ArrayList())
                    } else {
                        val alert = AlertDialog.Builder(this@FuguInnerChatActivity)
                        alert.setMessage("File size cannot be greater than " + (maxUploadSize / 1024 / 1024).toInt() + "MB.")
                        alert.setPositiveButton("Ok", null)
                        alert.setCancelable(false)
                        alert.show()
                    }
                }
            }
            FuguAppConstant.REQUEST_CODE_PICK_AUDIO -> {
                val list3: ArrayList<AudioFile> = data!!.getParcelableArrayListExtra(FuguAppConstant.RESULT_PICK_AUDIO)!!
                val cursor2 = this@FuguInnerChatActivity.contentResolver.query(getUriFromPath(list3[0].path), null, null, null, null)
                try {
                    if (cursor2 != null && cursor2.moveToFirst()) {
                        try {
                            val extensions = cursor2.getString(cursor2.getColumnIndex(OpenableColumns.DISPLAY_NAME)).split("\\.".toRegex()).toTypedArray()
                            extension = extensions[extensions.size - 1].toLowerCase()
                        } catch (e: Exception) {
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val localDate2 = DateUtils.getFormattedDate(Date())
                if (FuguAppConstant.FILE_TYPE_MAP[extension!!.toLowerCase()] == null) {
                    extension = "default"
                }
                val fileDetails2 = fuguImageUtils!!.saveFile(getUriFromPath(list3[0].path), FuguAppConstant.FILE_TYPE_MAP[extension!!.toLowerCase()], channelId, localDate2)
                if (fileDetails2.filePath.isEmpty()) {
                    Toast.makeText(this, "File not found...", Toast.LENGTH_LONG).show()
                } else {
                    if (fileDetails2.fileSize.contains("KB") || fileDetails2.fileSize.contains("Bytes") || fileDetails2.fileSizeDouble.toDouble() * 1024 * 1024 <= maxUploadSize) {
                        addMessageToList(getString(R.string.fugu_empty), FuguAppConstant.FILE_MESSAGE, fileDetails2.filePath, "", fileDetails2, UUID.randomUUID().toString(), ArrayList())
                    } else {
                        val alert = AlertDialog.Builder(this@FuguInnerChatActivity)
                        alert.setMessage("File size cannot be greater than " + (maxUploadSize / 1024 / 1024).toInt() + "MB.")
                        alert.setPositiveButton("Ok", null)
                        alert.setCancelable(false)
                        alert.show()
                    }
                }
            }
            FuguAppConstant.REQUEST_MULTIPLE_IMAGES -> {
                val list5: ArrayList<ImageFile> = data!!.getParcelableArrayListExtra(FuguAppConstant.RESULT_PICK_IMAGE)!!
                var i = 0
                while (i < list5.size) {
                    sendImage(list5[i])
                    i++
                }
            }
        }
    }

    private fun uploadFileServerCall(taggedUsers: ArrayList<Int>?, messageText: String, uuid: String?, file: String, messageIndex: Int, messageType: Int, fileDetails: FuguFileDetails?, dimens: ArrayList<Int>) {
        com.skeleton.mvp.fugudatabase.CommonData.setImageUri("")
        val link = file.split("\\.".toRegex()).toTypedArray()
        val fileBody = ProgressRequestBody(File(file), this, GetMimeTypeFromUrl.getMimeType(file, this), file, messageIndex, uuid)
        val filePart = MultipartBody.Part.createFormData("file", link[link.size - 1], fileBody)
        try {
            if (isNetworkAvailable()) {
                val multipartBuilder = MultipartParams.Builder()
                val multipartParams = multipartBuilder
                        .add(FuguAppConstant.APP_SECRET_KEY, workspacesInfo!!.fuguSecretKey)
                        .add(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_CODE)
                        .add(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER)
                        .add("file_name", fileDetails!!.fileName)
                        .add(FuguAppConstant.MESSAGE_TYPE, messageType)
                        .add("muid", uuid)
                        .build()
                com.skeleton.mvp.retrofit.RestClient.getApiInterface()
                        .uploadFile(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey, 1, BuildConfig.VERSION_CODE, filePart, multipartParams.map)
                        .enqueue(object : ResponseResolver<FuguUploadImageResponse>(this@FuguInnerChatActivity, false, false) {
                            override fun success(fuguUploadImageResponse: FuguUploadImageResponse) {
                                if (fuguUploadImageResponse.data.muid == null) {
                                    fuguUploadImageResponse.data.muid = uuid
                                }
                                if (messageIndex < fuguThreadMessageList.size
                                        && fuguThreadMessageList[messageIndex].muid == fuguUploadImageResponse.data.muid) {
                                    messagePosition = messageIndex
                                } else {
                                    var position = -1
                                    for (i in fuguThreadMessageList.indices.reversed()) {
                                        if (fuguThreadMessageList[i].muid == fuguUploadImageResponse.data.muid) {
                                            position = i
                                            break
                                        }
                                    }
                                    messagePosition = position
                                }
                                if (fuguUploadImageResponse.data.imageSize != null && !TextUtils.isEmpty(fuguUploadImageResponse.data.imageSize.toString())) {
                                    if (fuguUploadImageResponse.data.imageSize < 1024) {
                                        fileDetails.fileSize = fuguUploadImageResponse.data.imageSize.toString() + "B"
                                    } else if ((fuguUploadImageResponse.data.imageSize / 1024).toInt() > 1024) {
                                        fileDetails.fileSize = (fuguUploadImageResponse.data.imageSize / (1024 * 1024)).toString() + "MB"
                                    } else {
                                        fileDetails.fileSize = (fuguUploadImageResponse.data.imageSize / 1024).toString() + "KB"
                                    }
                                }
                                if (messagePosition != -1) {
                                    try {
                                        com.skeleton.mvp.fugudatabase.CommonData.setFilesMap(uuid, fuguUploadImageResponse.data.url, fileDetails.filePath)
                                        com.skeleton.mvp.fugudatabase.CommonData.setCachedFilePath(fuguUploadImageResponse.data.url, uuid, fileDetails.filePath)
                                        fuguThreadMessageList[messageIndex].filePath = fileDetails.filePath
                                        fuguThreadMessageList[messageIndex].sentFilePath = fileDetails.filePath
                                        com.skeleton.mvp.fugudatabase.CommonData.setFileLocalPath(uuid, fileDetails.filePath)
                                        fuguThreadMessageList[messageIndex].uploadStatus = FuguAppConstant.UploadStatus.UPLOAD_COMPLETED.uploadStatus
                                        fuguThreadMessageList[messageIndex].downloadStatus = FuguAppConstant.UploadStatus.UPLOAD_COMPLETED.uploadStatus
                                        fuguThreadMessageList[messageIndex].thumbnailUrl = fuguUploadImageResponse.data.thumbnailUrl
                                        fuguThreadMessageList[messageIndex].image_url_100x100 = fuguUploadImageResponse.data.image_url_100x100
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    fuguMessageAdapter!!.notifyItemChanged(messageIndex)
                                    if (channelId.compareTo(-1L) > 0) {
                                        fuguThreadMessageList[messageIndex].sharableThumbnailUrl = fuguUploadImageResponse.data.thumbnailUrl
                                        fuguThreadMessageList[messageIndex].sharableImage_url = fuguUploadImageResponse.data.url
                                        fuguThreadMessageList[messageIndex].sharableImage_url_100x100 = fuguUploadImageResponse.data.image_url_100x100
                                        val jsonObject = createFileJson(taggedUsers, messageText, messageType,
                                                fuguUploadImageResponse.data.url, fuguUploadImageResponse.data.thumbnailUrl,
                                                fuguUploadImageResponse.data.image_url_100x100, fileDetails,
                                                muid, messageIndex, java.lang.Long.valueOf(myUserId), myName, channelId, uuid, dimens)
                                        if (jsonObject != null) {
                                            sendThreadMessage(jsonObject)
                                        }
                                        try {
                                            if (messageType == FuguAppConstant.IMAGE_MESSAGE || messageType == FuguAppConstant.FILE_MESSAGE || messageType == FuguAppConstant.VIDEO_MESSAGE) {
                                                unsentMessageMap[uuid]!!.messageStatus = FuguAppConstant.MESSAGE_SENT
                                                unsentMessageMap[uuid]!!.downloadStatus = 3
                                                unsentMessageMap[uuid]!!.uploadStatus = 3
                                                unsentMessageMap[uuid]!!.thumbnailUrl = fuguUploadImageResponse.data.thumbnailUrl
                                                setUnsentMessageMapByChannel(channelId, unsentMessageMap)
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                }
                            }

                            override fun failure(error: APIError) {
                                try {
                                    when (messageType) {
                                        FuguAppConstant.IMAGE_MESSAGE -> {
                                            fuguThreadMessageList[messageIndex].messageStatus = FuguAppConstant.MESSAGE_IMAGE_RETRY
                                            fuguThreadMessageList[messageIndex].uploadStatus = 0
                                        }
                                        FuguAppConstant.FILE_MESSAGE -> {
                                            fuguThreadMessageList[messageIndex].messageStatus = FuguAppConstant.MESSAGE_FILE_RETRY
                                            fuguThreadMessageList[messageIndex].uploadStatus = 0
                                        }
                                        FuguAppConstant.VIDEO_MESSAGE -> {
                                            fuguThreadMessageList[messageIndex].messageStatus = FuguAppConstant.MESSAGE_FILE_RETRY
                                            fuguThreadMessageList[messageIndex].uploadStatus = 0
                                        }
                                    }
                                    fuguMessageAdapter!!.notifyItemChanged(messageIndex)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        })
            } else {
                if (messageType == FuguAppConstant.IMAGE_MESSAGE) {
                    fuguThreadMessageList[messageIndex].messageStatus = FuguAppConstant.MESSAGE_IMAGE_RETRY
                    fuguThreadMessageList[messageIndex].uploadStatus = 0
                } else if (messageType == FuguAppConstant.FILE_MESSAGE) {
                    fuguThreadMessageList[messageIndex].messageStatus = FuguAppConstant.MESSAGE_FILE_RETRY
                    fuguThreadMessageList[messageIndex].uploadStatus = 0
                } else if (messageType == FuguAppConstant.VIDEO_MESSAGE) {
                    fuguThreadMessageList[messageIndex].messageStatus = FuguAppConstant.MESSAGE_FILE_RETRY
                    fuguThreadMessageList[messageIndex].uploadStatus = 0
                }
                fuguMessageAdapter!!.notifyItemChanged(messageIndex)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createFileJson(taggesUsers: ArrayList<Int>?, textMessage: String, messageType: Int,
                               url: String?, thumbnailUrl: String?, image_url_100x100: String?,
                               fileDetails: FuguFileDetails?, uuid: String?,
                               messageIndex: Int, userId: Long, userName: String,
                               channelId: Long, threadMuid: String?, dimens: ArrayList<Int>): JSONObject? {
        val messageJson = JSONObject()
        val localDate = DateUtils.getFormattedDate(Date())
        try {
            when (messageType) {
                FuguAppConstant.IMAGE_MESSAGE -> {
                    messageJson.put(FuguAppConstant.DOCUMENT_TYPE, "image")
                }
                FuguAppConstant.FILE_MESSAGE -> {
                    val link = url!!.split("\\.".toRegex()).toTypedArray()
                    val extension = link[link.size - 1]
                    if (FuguAppConstant.SUPPORTED_AUDIO_FORMATS.contains(extension)) {
                        messageJson.put(FuguAppConstant.DOCUMENT_TYPE, "audio")
                    } else {
                        messageJson.put(FuguAppConstant.DOCUMENT_TYPE, "file")
                    }
                }
                FuguAppConstant.VIDEO_MESSAGE -> {
                    messageJson.put(FuguAppConstant.DOCUMENT_TYPE, "video")
                }
            }
            messageJson.put(FuguAppConstant.CHANNEL_ID, channelId)
            messageJson.put(FuguAppConstant.FULL_NAME, userName)
            messageJson.put(TEXTMESSAGE, textMessage)
            messageJson.put(FuguAppConstant.FORMATTED_MESSAGE, getFormattedString(textMessage)[1])
            messageJson.put(FuguAppConstant.MESSAGE_TYPE, messageType)
            messageJson.put(FuguAppConstant.DATE_TIME, DateUtils.getInstance().convertToUTC(localDate))
            messageJson.put(FuguAppConstant.MESSAGE_INDEX, fuguThreadMessageList.size - 1)
            messageJson.put(FuguAppConstant.MESSAGE_UNIQUE_ID, uuid)
            if (messageType == FuguAppConstant.IMAGE_MESSAGE && !url!!.trim { it <= ' ' }.isEmpty() && !thumbnailUrl!!.trim { it <= ' ' }.isEmpty()) {
                messageJson.put(FuguAppConstant.IMAGE_URL, url)
                messageJson.put(FuguAppConstant.THUMBNAIL_URL, thumbnailUrl)
                if (fileDetails != null) {
                    if (!TextUtils.isEmpty(fileDetails.fileName)) {
                        messageJson.put(FuguAppConstant.FILE_NAME, fileDetails.fileName)
                    }
                    if (!TextUtils.isEmpty(fileDetails.fileSize)) {
                        messageJson.put(FuguAppConstant.FILE_SIZE, fileDetails.fileSize)
                    }
                    if (!TextUtils.isEmpty(image_url_100x100)) {
                        messageJson.put(FuguAppConstant.IMAGE_URL_100X100, image_url_100x100)
                    }
                }
            }
            if (!TextUtils.isEmpty(threadMuid)) {
                messageJson.put(FuguAppConstant.THREAD_MUID, threadMuid)
                messageJson.put(FuguAppConstant.IS_THREAD_MESSAGE, true)
            } else {
                messageJson.put(FuguAppConstant.IS_THREAD_MESSAGE, false)
            }
            if (messageType == FuguAppConstant.FILE_MESSAGE && url != null && !url.trim { it <= ' ' }.isEmpty()) {
                messageJson.put("url", url)
                messageJson.put(FuguAppConstant.FILE_NAME, fileDetails!!.fileName)
                messageJson.put(FuguAppConstant.FILE_SIZE, fileDetails.fileSize)
            }
            if (messageType == FuguAppConstant.VIDEO_MESSAGE && url != null && !url.trim { it <= ' ' }.isEmpty()
                    && thumbnailUrl != null && !thumbnailUrl.trim { it <= ' ' }.isEmpty()) {
                messageJson.put("url", url)
                messageJson.put(FuguAppConstant.THUMBNAIL_URL, thumbnailUrl)
                if (image_url_100x100 != null && !image_url_100x100.trim { it <= ' ' }.isEmpty()) {
                    messageJson.put(FuguAppConstant.IMAGE_URL_100X100, image_url_100x100)
                }
                messageJson.put(FuguAppConstant.FILE_NAME, fileDetails!!.fileName)
                messageJson.put(FuguAppConstant.FILE_SIZE, fileDetails.fileSize)
            }
            if (dimens.size == 2) {
                messageJson.put(FuguAppConstant.IMAGE_HEIGHT, dimens[0])
                messageJson.put(FuguAppConstant.IMAGE_WIDTH, dimens[1])
            }
            messageJson.put(FuguAppConstant.DEVICE_TOKEN, CommonData.getFcmToken())
            val devicePayload = JSONObject()
            devicePayload.put(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(this))
            devicePayload.put(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER)
            devicePayload.put(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME)
            devicePayload.put(FuguAppConstant.DEVICE_DETAILS, com.skeleton.mvp.fugudatabase.CommonData.deviceDetails(this))
            messageJson.put(FuguAppConstant.DEVICE_PAYLOAD, devicePayload)
            if (messageType == FuguAppConstant.TEXT_MESSAGE) {
                messageJson.put(FuguAppConstant.IS_TYPING, isTyping)
            } else {
                messageJson.put(FuguAppConstant.IS_TYPING, FuguAppConstant.TYPING_SHOW_MESSAGE)
            }
            if (taggesUsers != null && taggesUsers.size != 0) {
                val jsonArrayTaggedUsers = JSONArray()
                for (id in taggesUsers) {
                    jsonArrayTaggedUsers.put(id)
                }
                messageJson.put(FuguAppConstant.TAGGED_USERS, jsonArrayTaggedUsers)
                if (jsonArrayTaggedUsers.toString().contains("-1")) {
                    messageJson.put(FuguAppConstant.TAGGED_ALL, true)
                }
            }
            messageJson.put(FuguAppConstant.MESSAGE_STATUS, FuguAppConstant.MESSAGE_UNSENT)
            messageJson.put(FuguAppConstant.USER_ID, userId.toString())
            messageJson.put(FuguAppConstant.USER_TYPE, FuguAppConstant.ANDROID_USER)
        } catch (e: JSONException) {
            e.printStackTrace()
            return null
        }
        return messageJson
    }

    override fun onResume() {
        super.onResume()
        registerReceivers()
        try {
            setUpSocketListeners("Resume Thread")
            if (message != null) {
                pushMuid = message!!.muid
            }
            } catch (e: Exception) {
                e.printStackTrace()
        }
        fuguMessageAdapter!!.attachObservers(true)
    }

    private val mBroadcast: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                FuguAppConstant.NETWORK_STATE_INTENT -> setUpSocketListeners("Network Thread")
            }
        }
    }

    private val intentFilter: IntentFilter
        get() {
            val intent = IntentFilter()
            intent.addAction(FuguAppConstant.NETWORK_STATE_INTENT)
            return intent
        }

    override fun onPause() {
        super.onPause()
        fuguMessageAdapter!!.attachObservers(false)
        //        mClient.unsubscribeAll();
//        mClient.setListener(null);
        pushMuid = ""
        unregisterReceivers()
    }

    override fun onStop() {
        unsubscribeChannel(channelId)
        unregisterReceivers()
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        KeyboardUtil.removeKeyboardToggleListener(keyboardListener)
        //        mClient.unsubscribeAll();
//        mClient.setListener(null);
        unregisterReceivers()
        pushMuid = ""
    }

    private fun registerReceivers(){
        try{
            LocalBroadcastManager.getInstance(this).registerReceiver(mClearChatReceiver, IntentFilter(FuguAppConstant.CLEAR_INTENT))
        } catch (e: Exception){
            e.printStackTrace()
        }
        try{
            LocalBroadcastManager.getInstance(this).registerReceiver(mDeleteChatReceiver, IntentFilter(FuguAppConstant.DELETE_INTENT))
        } catch (e: Exception){
            e.printStackTrace()
        }
        try{
            LocalBroadcastManager.getInstance(this).registerReceiver(mThreadDeleteChatReceiver, IntentFilter(FuguAppConstant.THREAD_DELETE_INTENT))
        } catch (e: Exception){
            e.printStackTrace()
        }
        try{
            LocalBroadcastManager.getInstance(this).registerReceiver(mEditMessageReciever, IntentFilter(FuguAppConstant.EDIT_MESSAGE_INTENT))
        } catch (e: Exception){
            e.printStackTrace()
        }
        try{
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageNotificationReceiver, IntentFilter(FuguAppConstant.MESSAGE_NOTIFICATION_INTENT))
        } catch (e: Exception){
            e.printStackTrace()
        }
        try{
            LocalBroadcastManager.getInstance(this).registerReceiver(mPublicChatReceiver, IntentFilter(FuguAppConstant.PUBLIC_INTENT))
        } catch (e: Exception){
            e.printStackTrace()
        }
        try {
            LocalBroadcastManager.getInstance(this@FuguInnerChatActivity).registerReceiver(mBroadcast, intentFilter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun unregisterReceivers(){
        if(mBroadcast != null)
            try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcast)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if(mClearChatReceiver != null)
            try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mClearChatReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if(mDeleteChatReceiver != null)
            try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mDeleteChatReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if(mEditMessageReciever != null)
            try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mEditMessageReciever)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if(mThreadDeleteChatReceiver != null)
            try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mThreadDeleteChatReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if(mMessageNotificationReceiver != null)
            try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageNotificationReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if(mPublicChatReceiver != null)
            try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mPublicChatReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun sendMessage(position: Int) {
        try {
            val message = fuguThreadMessageList[position]
            if (message.isExpired) {
                try {
                    isTyping = FuguAppConstant.TYPING_SHOW_MESSAGE
                    val localDate = DateUtils.getFormattedDate(Date())
                    message.sentAtUtc = DateUtils.getInstance().convertToUTC(localDate)
                    publishMessage(message.message,
                            FuguAppConstant.TEXT_MESSAGE, message.url,
                            "",
                            null,
                            FuguAppConstant.NOTIFICATION_DEFAULT,
                            message.uuid, position)
                    message.isSent = true
                    message.isExpired = false
                    fuguMessageAdapter!!.notifyItemChanged(position)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun cancelMessage(position: Int) {
        try {
            val message = fuguThreadMessageList[position]
            try {
                unsentMessageMap.remove(message.uuid)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            //eg: size =10

            // pos removed=5;
            //size=9
            val keys = ArrayList(mMessageIndices.keys)
            for (i in position until keys.size) {
                val value = ArrayList(mMessageIndices.values)[i] - 1
                mMessageIndices[keys[i]] = value
            }
            mMessageIndices.remove(message.uuid)
            fuguThreadMessageList.removeAt(position)
            fuguMessageAdapter!!.updateThreadMessageList(fuguThreadMessageList)
            fuguMessageAdapter!!.notifyDataSetChanged()
            setUnsentMessageMapByChannel(channelId, unsentMessageMap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateChatScreenData() {
        try {
            for (i in messageList.indices.reversed()) {
                if (isMessageDeleted) {
                    if (messageList[i].muid == message!!.uuid) {
                        var rowType: Int
                        var text = ""
                        var hasReplies = false
                        if (messageList[i].rowType == TEXT_MESSAGE_SELF || messageList[i].rowType == IMAGE_MESSAGE_SELF || messageList[i].rowType == FILE_MESSAGE_SELF || messageList[i].rowType == VIDEO_MESSAGE_SELF || messageList[i].rowType == MESSAGE_DELETED_SELF) {
                            rowType = MESSAGE_DELETED_SELF
                            text = "You deleted this message."
                        } else {
                            rowType = MESSAGE_DELETED_OTHER
                            text = "This message was deleted."
                        }
                        if (fuguThreadMessageList.size > 0) {
                            hasReplies = true
                        }
                        messageList[i].rowType = rowType
                        messageList[i].message = text
                        val intent = Intent()
                        intent.putExtra("BUNDLE", messageList)
                        intent.putExtra(Intent.EXTRA_TEXT, "Test")
                        intent.putExtra("only_admin_can_message", only_admin_can_message)
                        intent.putExtra("userRole", userRole)
                        setResult(RESULT_OK, intent)
                    }
                } else {
                    if (messageList[i].uuid == message!!.uuid) {
                        messageList[i].threadMessageCount = fuguThreadMessageList.size - dateItemCount - unsentMessageMap.size - 1
                        if (message!!.userReaction != null) {
                            Log.i("message", messageList[i].message)
                            Log.i("reaction", "not empty")
                        } else {
                            Log.i("message", messageList[i].message)
                            Log.i("reaction", "not empty")
                        }
                        Log.i("downloadstatus", fuguThreadMessageList[0].downloadStatus.toString())
                        messageList[i].filePath = fuguThreadMessageList[0].filePath
//                        if(fuguThreadMessageList.get(0).getDownloadStatus() == DOWNLOAD_COMPLETED){
//                            messageList.get(i).setDownloadStatus(fuguThreadMessageList.get(0).getDownloadStatus());
//                        } else{
//                            messageList.get(i).setDownloadStatus(DOWNLOAD_FAILED);
//                        }
                        messageList[i].downloadStatus = fuguThreadMessageList[0].downloadStatus
                        if (fuguThreadMessageList[0].downloadId != 0) {
                            PRDownloader.pause(fuguThreadMessageList[0].downloadId)
                            messageList[i].downloadId = fuguThreadMessageList[0].downloadId
                        } else if (messageList[i].downloadId != 0) {
                            PRDownloader.pause(messageList[i].downloadId)
                            messageList[i].downloadId = messageList[i].downloadId
                        }
                        messageList[i].uploadStatus = fuguThreadMessageList[0].uploadStatus
                        if (fuguThreadMessageList[0].isStarred == 1) {
                            messageList[i].isStarred = 1
                        } else {
                            messageList[i].isStarred = 0
                        }
                        messageList[i].userReaction = message!!.userReaction
                        if (fuguThreadMessageList.size - 1 > 0) {
                            messageList[i].threadMessage = true
                        }
                        val intent = Intent()
                        intent.putExtra("BUNDLE", messageList)
                        intent.putExtra(Intent.EXTRA_TEXT, "Test")
                        intent.putExtra("threadMuid", message!!.uuid)
                        intent.putExtra("only_admin_can_message", only_admin_can_message)
                        intent.putExtra("userRole", userRole)
                        setResult(RESULT_OK, intent)
                        break
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendFirstUnsentMessageOfList() {
        val expiryTime = 10
        val localDate = DateUtils.getFormattedDate(Date())
        val messagesTobeSent = LinkedHashMap<String?, Message?>()
        for (name in unsentMessageMap.keys) {
            val messageJson = JSONObject()
            val messageObj = unsentMessageMap[name]
            val newTime = DateUtils.getTimeInMinutes(DateUtils.getInstance().convertToUTC(localDate))
            val oldTime = DateUtils.getTimeInMinutes(messageObj!!.sentAtUtc)
            //            if (TextUtils.isEmpty(((EventItem) unsentMessageMap.get(name)).getEvent().getThreadMuid())) {
            if (unsentMessageMap[name]!!.messageType == FuguAppConstant.TEXT_MESSAGE && !messageObj.isExpired && newTime - oldTime < expiryTime) {
                unsentMessageMap[name]!!.isThreadMessage = !TextUtils.isEmpty(unsentMessageMap[name]!!.threadMuid)
                messagesTobeSent[name] = unsentMessageMap[name]
            } else {
                if (unsentMessageMap[name]!!.messageType == FuguAppConstant.TEXT_MESSAGE) {
                    messageObj.isExpired = true
                }
                //                }
            }
        }
        runOnUiThread { fuguMessageAdapter!!.notifyDataSetChanged() }
        val keys = ArrayList(messagesTobeSent.keys)
        val messageJson = JSONObject()
        if (keys.size > 0) {
            val messageObj = messagesTobeSent[keys[0]]
            try {
                if (messageObj != null) {
                    if (messageObj.userId.toString() == myUserId && messageObj.messageStatus == FuguAppConstant.MESSAGE_UNSENT) {
                        messageJson.put(FuguAppConstant.CHANNEL_ID, channelId)
                        messageJson.put(FuguAppConstant.USER_ID, messageObj.userId.toString())
                        messageJson.put(FuguAppConstant.FULL_NAME, messageObj.getfromName())
                        messageJson.put(TEXTMESSAGE, messageObj.message)
                        messageJson.put(FuguAppConstant.MESSAGE_TYPE, messageObj.messageType)
                        val date = DateUtils.getFormattedDate(Calendar.getInstance().time)
                        messageJson.put(FuguAppConstant.USER_TYPE, FuguAppConstant.ANDROID_USER)
                        messageJson.put(FuguAppConstant.DATE_TIME, DateUtils.getInstance().convertToUTC(localDate))
                        messageJson.put(FuguAppConstant.MESSAGE_INDEX, messageObj.messageIndex)
                        messageJson.put(FuguAppConstant.IS_TYPING, FuguAppConstant.TYPING_SHOW_MESSAGE)
                        messageJson.put(FuguAppConstant.MESSAGE_STATUS, messageObj.messageStatus)
                        if (messageObj.isThreadMessage) {
                            messageJson.put(FuguAppConstant.MESSAGE_UNIQUE_ID, messageObj.threadMuid)
                            messageJson.put("thread_muid", messageObj.uuid)
                            messageJson.put("is_thread_message", true)
                        } else {
                            messageJson.put(FuguAppConstant.MESSAGE_UNIQUE_ID, messageObj.uuid)
                            messageJson.put("is_thread_message", false)
                        }
                        runOnUiThread {
                            Handler().postDelayed({
                                if (messageObj.messageType == FuguAppConstant.TEXT_MESSAGE) {
                                    sendThreadMessage(messageJson)
                                }
                            }, 500)
                        }
                    } else {
                        FirebaseCrashlytics.getInstance().log(messageJson.toString())
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private val mClearChatReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val channelIdReceived = intent.getLongExtra(FuguAppConstant.CHANNEL_ID, -1L)
            if (intent.hasExtra(FuguAppConstant.APP_SECRET_KEY)
                    && intent.getStringExtra(FuguAppConstant.APP_SECRET_KEY) == CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey && channelIdReceived.compareTo(channelId) == 0) {
                isChatDeleted = true
            }
        }
    }

    private fun addMessageLinkToList(message: String, messageType: Int, imageUrl: String, thumbnailUrl: String, imageUrl100x100: String, shareUrl: String, fileDetails: FuguFileDetails?, uuid: String, dimens: ArrayList<Int>) {
        try {
            val localDate = DateUtils.getFormattedDate(Date())
            val date = DateUtils.getDate(localDate)
            if (!TextUtils.isEmpty(sentAtUTC) && !sentAtUTC.equals(date, ignoreCase = true)) {
                val message1 = Message()
                message1.sentAtUtc = date
                message1.rowType = HEADER_ITEM
                fuguThreadMessageList.add(message1)
                mMessageIndices[date] = fuguThreadMessageList.size - 1
                sentAtUTC = date
                dateItemCount += 1
            }
            val messageObj = Message(0, myName,
                    java.lang.Long.valueOf(myUserId),
                    message,
                    DateUtils.getInstance().convertToUTC(localDate),
                    IMAGE_MESSAGE_SELF,
                    FuguAppConstant.MESSAGE_UNSENT,
                    fuguThreadMessageList.size,
                    if (imageUrl.isEmpty()) "" else imageUrl,
                    if (thumbnailUrl.isEmpty()) "" else thumbnailUrl,
                    messageType,
                    true,
                    uuid, conversation.chat_type, CommonData.getCommonResponse().getData().getUserInfo().email,
                    "")
            messageObj.threadMuid = muid
            messageObj.image_url_100x100 = imageUrl100x100
            val formattedStrings = getFormattedString(message)
            messageObj.alteredMessage = formattedStrings[0]
            messageObj.formattedMessage = formattedStrings[1]
            if (fileDetails != null) {
                messageObj.fileName = fileDetails.fileName
                messageObj.fileSize = fileDetails.fileSize
                messageObj.fileExtension = fileDetails.fileExtension
            }
            if (CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].userImage != null) {
                messageObj.userImage = CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].userImage
            }
            if (dimens.size == 2) {
                messageObj.imageHeight = dimens[0]
                messageObj.imageWidth = dimens[1]
            }
            messageObj.sharableImage_url = shareUrl
            messageObj.sharableThumbnailUrl = shareUrl
            taggedUsers!!.clear()
            if (mentionsArrayList.size > 0) {
                taggedUsers = ArrayList()
                for (mention in mentionsArrayList) {
                    if (!taggedUsers!!.contains(mention.userId.toInt())) {
                        taggedUsers!!.add(mention.userId.toInt())
                    }
                }
                messageObj.taggedUsers = taggedUsers
            }
            mMessageIndices[uuid] = fuguThreadMessageList.size
            fuguThreadMessageList.add(messageObj)
            fuguMessageAdapter!!.notifyItemInserted(fuguThreadMessageList.size - 1)
            etMsg!!.setText("")
            Handler().postDelayed({
                //                    scrollView.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//                            scrollView.clearFocus();
//                            etMsg.requestFocus();
//                        }
//                    });
            }, 500)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val mEditMessageReciever: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                val intentChannelId = intent.getLongExtra(FuguAppConstant.CHANNEL_ID, -1L)
                if (intentChannelId.compareTo(channelId) == 0 && intent.getStringExtra(FuguAppConstant.MESSAGE_UNIQUE_ID) == muid) {
                    if (intent.getBooleanExtra(FuguAppConstant.IS_THREAD_MESSAGE, false)) {
                        if (fuguThreadMessageList[mMessageIndices[intent.getStringExtra(FuguAppConstant.THREAD_MUID)]!!] != null) {
                            val message = fuguThreadMessageList[mMessageIndices[intent.getStringExtra(FuguAppConstant.THREAD_MUID)]!!]
                            message.messageState = 4
                            message.message = intent.getStringExtra(TEXTMESSAGE)
                            message.alteredMessage = getFormattedString(intent.getStringExtra(TEXTMESSAGE))[0]
                            fuguThreadMessageList[mMessageIndices[intent.getStringExtra(FuguAppConstant.THREAD_MUID)]!!] = message
                            fuguMessageAdapter!!.updateThreadMessageList(fuguThreadMessageList)
                            fuguMessageAdapter!!.notifyItemChanged(mMessageIndices[intent.getStringExtra(FuguAppConstant.THREAD_MUID)]!!)
                        }
                    } else {
                        val textView = findViewById<TextView>(R.id.tvTime)
                        val date = DateUtils.getDate(dateUtils!!.convertToLocalWithoutYear(message!!.sentAtUtc))
                        tvUserMessage!!.text = Html.fromHtml(getFormattedString(intent.getStringExtra(TEXTMESSAGE))[0])
                        tvUserImageMessage!!.text = Html.fromHtml(intent.getStringExtra(TEXTMESSAGE))
                        textView.text = date + " at " + DateUtils.getTime(fuguDateUtil.convertToLocal(message!!.sentAtUtc))
                        textView.append(Html.fromHtml("<font><small>&nbsp;&nbsp;(edited)</small></font>"))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun openBottomSheet(muid: String, messageType: Int, isSelf: Boolean, isMessageDeleted: Boolean, messageStatus: Int, sentAtUTC: String?, messageState: Int, isStarred: Int, location: IntArray?) {
        try {
            var positionToBeOpened = 0
            try {
                for (i in fuguThreadMessageList.size - 1 downTo 1) {
                    if (!TextUtils.isEmpty(fuguThreadMessageList[i].muid) && fuguThreadMessageList[i].muid == muid) {
                        positionToBeOpened = i
                        break
                    }
                }
            } catch (e: Exception) {
                positionToBeOpened = 0
            }
            val localDate = DateUtils.getFormattedDate(Date())
            val newTime = DateUtils.getTimeInMinutes(DateUtils.getInstance().convertToUTC(localDate))
            val oldTime = DateUtils.getTimeInMinutes(sentAtUTC)
            var downloadStatus = ""
            try {
                val extensions2 = fuguThreadMessageList[positionToBeOpened].image_url.split(Pattern.quote(".").toRegex()).toTypedArray()
                var extension2 = extensions2[extensions2.size - 1]
                if (extension2.toLowerCase() == "png") {
                    extension2 = "jpg"
                }
                val fileName = fuguThreadMessageList[positionToBeOpened].fileName + "_" + fuguThreadMessageList[positionToBeOpened].muid + "." + extension2
                val filePath = File(getDirectory(extension2) + "/" + fileName)
                downloadStatus = if (filePath.exists()) {
                    FuguAppConstant.DownloadStatus.DOWNLOAD_COMPLETED.toString()
                } else {
                    FuguAppConstant.DownloadStatus.DOWNLOAD_FAILED.toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            threadBottomSheetFragment = ThreadBottomSheetFragment.newInstance(0, positionToBeOpened, muid, messageType, isSelf, isMessageDeleted, messageStatus, sentAtUTC, isStarred, location, downloadStatus, conversation.chat_type)
            threadBottomSheetFragment?.show(supportFragmentManager, "ThreadBottomSheetFragment")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun copyText(position: Int) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val message = fuguThreadMessageList[position].message.replace("<br/>", "fuguLineBreak").replace("<br>", "fuguLineBreak").replace("\n", "fuguLineBreak")
        val clip = ClipData.newPlainText("", Html.fromHtml(message).toString().replace("fuguLineBreak", "\n"))
        clipboard.setPrimaryClip(clip)
    }

    fun editText(position: Int) {
        if (isMessageInEditMode) {
            val message = fuguThreadMessageList[mMessageIndices[firstEditMuid]!!]
            message.editMode = false
            fuguThreadMessageList[mMessageIndices[firstEditMuid]!!] = message
            fuguMessageAdapter!!.updateThreadMessageList(fuguThreadMessageList)
            fuguMessageAdapter!!.notifyItemChanged(mMessageIndices[firstEditMuid]!!)
            firstEditMuid = ""
        }
        KeyboardUtil.toggleKeyboardVisibility(this)
        taggedUsers = ArrayList()
        etMsg!!.setText(Html.fromHtml(fuguThreadMessageList[position].message.replace("\n", "<br>")))
        etMsg!!.setSelection(etMsg!!.text!!.length)
        val s = fuguThreadMessageList[position].message
        val matcher = Pattern.compile("href=\"mention://(.*?)\"").matcher(s)
        val matcherName = Pattern.compile("class=\"tagged-agent\">(.*?)</a>").matcher(s)
        val taggedList = ArrayList<Mention>()
        while (matcher.find() && matcherName.find()) {
            val mention = Mention()
            mention.mentionName = matcherName.group(1)
            mention.userId = java.lang.Long.valueOf(matcher.group(1))
            var isAlreadyTagged = false
            for (oldMention in taggedList) {
                if (oldMention.userId == mention.userId) {
                    isAlreadyTagged = true
                    break
                }
            }
            if (!isAlreadyTagged) {
                taggedList.add(mention)
                //                mentionsArrayList.add(mention);
            }
        }
        mentionsArrayList.addAll(taggedList)
        val message = fuguThreadMessageList[position]
        message.editMode = true
        fuguThreadMessageList[position] = message
        fuguMessageAdapter!!.updateThreadMessageList(fuguThreadMessageList)
        fuguMessageAdapter!!.notifyItemChanged(position)
        ivSend!!.setImageResource(R.drawable.success)
        ivAttachment!!.visibility = View.GONE
        ivCancelEdit!!.visibility = View.VISIBLE
        isMessageInEditMode = true
        firstEditMuid = fuguThreadMessageList[position].muid
    }

    fun deleteMessage(position: Int, muid: String?, messageStatus: Int) {
        if (messageStatus == FuguAppConstant.MESSAGE_SENT || messageStatus == FuguAppConstant.MESSAGE_DELIVERED || messageStatus == FuguAppConstant.MESSAGE_READ) {
            val commonParams = com.skeleton.mvp.retrofit.CommonParams.Builder()
                    .add(FuguAppConstant.EN_USER_ID, CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].enUserId)
                    .add("thread_muid", muid)
                    .add(FuguAppConstant.CHANNEL_ID, channelId)
                    .build()
            com.skeleton.mvp.retrofit.RestClient.getApiInterface().deleteMessage(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.map)
                    .enqueue(object : ResponseResolver<CommonResponse?>() {
                        override fun success(commonResponse: CommonResponse?) {
                            val rowType = if (fuguThreadMessageList[position].rowType == TEXT_MESSAGE_SELF || fuguThreadMessageList[position].rowType == VIDEO_MESSAGE_SELF || fuguThreadMessageList[position].rowType == IMAGE_MESSAGE_SELF || fuguThreadMessageList[position].rowType == FILE_MESSAGE_SELF) {
                                MESSAGE_DELETED_SELF
                            } else {
                                MESSAGE_DELETED_OTHER
                            }
                            fuguThreadMessageList[position].message = "You deleted this message"
                            fuguThreadMessageList[position].rowType = rowType
                            fuguMessageAdapter!!.updateThreadMessageList(fuguThreadMessageList)
                            fuguMessageAdapter!!.notifyItemChanged(position)
                        }

                        override fun failure(error: APIError) {}
                    })
        } else {
            cancelMessage(position)
            //            LinkedHashMap<Long, LinkedHashMap<String, ListItem>> unsentMessageMap = new LinkedHashMap<>();
//            try {
//                if (CommonData.getUnsentMessageMap() != null) {
//                    unsentMessageMap = CommonData.getUnsentMessageMap();
//                    unsentMessageMap.get(channelId).remove(muid);
//                    CommonData.setUnsentMessageMapByChannel(channelId, unsentMessageMap.get(channelId));
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    }

    private fun getDropboxIMGSize(uri: Uri): ArrayList<Int> {
        return try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(File(getRealPathFromURI(uri)).absolutePath, options)
            val imageHeight = options.outHeight
            val imageWidth = options.outWidth
            val dimens = ArrayList<Int>()
            dimens.add(imageHeight)
            dimens.add(imageWidth)
            dimens
        } catch (e: Exception) {
            ArrayList()
        }
    }

    private fun getRealPathFromURI(contentURI: Uri): String {
        val result: String
        val cursor = contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path!!
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = try {
                cursor.getString(idx)
            } catch (e: Exception) {
                contentURI.path!!
            }
            cursor.close()
        }
        return result
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    private fun showGifImageDialogUriLink(uri: Uri, sendUri: Uri?, activity: Context, imgUrl: String?) {
        try {
            val dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar)
            //setting custom layout to dialog
            dialog.setContentView(R.layout.fugu_image_dialog_send)
            val lp = dialog.window?.attributes
            lp?.dimAmount = 1.0f // Dim level. 0.0 - no dim, 1.0 - completely opaque
            dialog.window?.attributes = lp
            dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(false)
            val ivImage: PhotoView = dialog.findViewById(R.id.ivImage)
            val dialogRecyclerView: RecyclerView = dialog.findViewById(R.id.rv_mentions)
            val emojiGifEditText: EmojiGifEditText = dialog.findViewById(R.id.etMsg)
            val options = RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .fitCenter()
                    .priority(Priority.HIGH)
            Glide.with(activity)
                    .load(imgUrl)
                    .apply(options)
                    .into(ivImage)
            val dimenns = ArrayList<Int>()
            val tvCross: AppCompatImageView = dialog.findViewById(R.id.ivSend)
            if (conversation.chat_type == FuguAppConstant.ChatType.DEFAULT_GROUP || conversation.chat_type == FuguAppConstant.ChatType.GENERAL_GROUP || conversation.chat_type == FuguAppConstant.ChatType.PUBLIC_GROUP || conversation.chat_type == FuguAppConstant.ChatType.PRIVATE_GROUP) {
                setUpMentionsforImage(emojiGifEditText)
                setUpMentionsListForImage(dialogRecyclerView)
                imageRecycler = dialogRecyclerView
                imageLlMentions = dialog.findViewById(R.id.ll_mentions_layout)
                imageRecycler!!.visibility = View.VISIBLE
                imageLlMentions?.visibility = View.VISIBLE
                emojiGifEditText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                        imageText = s.toString()
                    }

                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        imageText = s.toString()
                    }

                    override fun afterTextChanged(s: Editable) {
                        imageText = s.toString()
                    }
                })
            }
            tvCross.setOnClickListener {
                dialog.dismiss()
                dimenns.add(ivImage.drawable.intrinsicHeight)
                dimenns.add(ivImage.drawable.intrinsicWidth)
                val globalUuid = UUID.randomUUID().toString()
                addMessageLinkToList(getTaggedMessage(emojiGifEditText, mentionsInImage), FuguAppConstant.IMAGE_MESSAGE, uri.toString(), uri.toString(), uri.toString(), sendUri.toString(), null, globalUuid, dimenns)
                //                    if (!mClient.getChannels().contains("/" + channelId)) {
//                        mClient.subscribeChannel("/" + channelId);
//                    }
                var jsonObject: JSONObject? = JSONObject()
                jsonObject = if (sendUri != null) {
                    createFileJson(taggedUsers, getTaggedMessage(emojiGifEditText, mentionsInImage), FuguAppConstant.IMAGE_MESSAGE,
                            sendUri.toString(), sendUri.toString(),
                            sendUri.toString(), null,
                            muid, fuguThreadMessageList.size - 1, java.lang.Long.valueOf(myUserId), myName, channelId, globalUuid, dimenns)
                } else {
                    createFileJson(taggedUsers, getTaggedMessage(emojiGifEditText, mentionsInImage), FuguAppConstant.IMAGE_MESSAGE,
                            uri.toString(), uri.toString(),
                            uri.toString(), null,
                            muid, fuguThreadMessageList.size - 1, java.lang.Long.valueOf(myUserId), myName, channelId, globalUuid, dimenns)
                }
                if (jsonObject != null) {
                    sendThreadMessage(jsonObject)
                }
                //                    if (sendUri != null) {
//                        mClient.publish(taggedUsers, getTaggedMessage(emojiGifEditText, mentionsInImage), IMAGE_MESSAGE, sendUri.toString(), sendUri.toString(), sendUri.toString(), null,
//                                NOTIFICATION_DEFAULT, message.getUuid(), fuguThreadMessageList.size() - 1, "/" + String.valueOf(channelId),
//                                Long.valueOf(myUserId), myName, channelId, isTyping, globalUuid, com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getEmail(), dimenns, FuguInnerChatActivity.this);
//                    } else {
//                        mClient.publish(taggedUsers, getTaggedMessage(emojiGifEditText, mentionsInImage), IMAGE_MESSAGE, uri.toString(), uri.toString(), uri.toString(), null,
//                                NOTIFICATION_DEFAULT, message.getUuid(), fuguThreadMessageList.size() - 1, "/" + String.valueOf(channelId),
//                                Long.valueOf(myUserId), myName, channelId, isTyping, globalUuid, com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getEmail(), dimenns, FuguInnerChatActivity.this);
//                    }
            }
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showGifImageDialog(uri: Uri?, sendUri: Uri?, activity: Context, imgUrl: String?, dimens: ArrayList<Int>, fuguFileDetails: FuguFileDetails, path: String) {
        try {
            val dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar)
            //setting custom layout to dialog
            dialog.setContentView(R.layout.fugu_image_dialog_send)
            val lp = dialog.window?.attributes
            lp?.dimAmount = 1.0f // Dim level. 0.0 - no dim, 1.0 - completely opaque
            dialog.window?.attributes = lp
            dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(false)
            val ivImage: PhotoView = dialog.findViewById(R.id.ivImage)
            val dialogRecyclerView: RecyclerView = dialog.findViewById(R.id.rv_mentions)
            val emojiGifEditText: EmojiGifEditText = dialog.findViewById(R.id.etMsg)

//            RequestOptions options = new RequestOptions()
//                    .centerCrop()
//                    //.dontAnimate()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .placeholder(R.drawable.placeholder)
//                    .error(R.drawable.placeholder)
//                    .fitCenter()
//                    .priority(Priority.HIGH);
            Glide.with(activity)
                    .load(imgUrl) //                    .apply(options)
                    .into(ivImage)
            val dimenns = ArrayList<Int>()
            val tvCross: AppCompatImageView = dialog.findViewById(R.id.ivSend)
            if (conversation.chat_type == FuguAppConstant.ChatType.DEFAULT_GROUP || conversation.chat_type == FuguAppConstant.ChatType.GENERAL_GROUP || conversation.chat_type == FuguAppConstant.ChatType.PUBLIC_GROUP || conversation.chat_type == FuguAppConstant.ChatType.PRIVATE_GROUP) {
                setUpMentionsforImage(emojiGifEditText)
                setUpMentionsListForImage(dialogRecyclerView)
                imageRecycler = dialogRecyclerView
                imageLlMentions = dialog.findViewById(R.id.ll_mentions_layout)
                imageRecycler!!.visibility = View.VISIBLE
                imageLlMentions?.visibility = View.VISIBLE
                emojiGifEditText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                        imageText = s.toString()
                    }

                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        imageText = s.toString()
                    }

                    override fun afterTextChanged(s: Editable) {
                        imageText = s.toString()
                    }
                })
            }
            tvCross.setOnClickListener {
                dialog.dismiss()
                dimenns.add(ivImage.drawable.intrinsicHeight)
                dimenns.add(ivImage.drawable.intrinsicWidth)
                val globalUuid = UUID.randomUUID().toString()
                addMessageToList(getTaggedMessage(emojiGifEditText, mentions), FuguAppConstant.IMAGE_MESSAGE, path, path, fuguFileDetails, fuguFileDetails.muid, dimens)
                //addMessageLinkToList(getTaggedMessage(emojiGifEditText, mentionsInImage), IMAGE_MESSAGE, uri.toString(), uri.toString(), uri.toString(), sendUri.toString(), null, globalUuid, dimenns);
//                    if (!mClient.getChannels().contains("/" + channelId)) {
//                        mClient.subscribeChannel("/" + channelId);
//                    }
//                    JSONObject jsonObject = new JSONObject();
//                    if (sendUri != null) {
//                        jsonObject = createFileJson(taggedUsers, getTaggedMessage(emojiGifEditText, mentionsInImage), IMAGE_MESSAGE,
//                                sendUri.toString(), sendUri.toString(),
//                                sendUri.toString(), null,
//                                muid, fuguThreadMessageList.size() - 1, Long.valueOf(myUserId), myName, channelId, globalUuid, dimenns);
//                    } else {
//                        jsonObject = createFileJson(taggedUsers, getTaggedMessage(emojiGifEditText, mentionsInImage), IMAGE_MESSAGE,
//                                uri.toString(), uri.toString(),
//                                uri.toString(), null,
//                                muid, fuguThreadMessageList.size() - 1, Long.valueOf(myUserId), myName, channelId, globalUuid, dimenns);
//                    }
//                    if (jsonObject != null) {
//                        SocketConnection.INSTANCE.sendThreadMessage(jsonObject);
//                    }
//                    if (sendUri != null) {
//                        mClient.publish(taggedUsers, getTaggedMessage(emojiGifEditText, mentionsInImage), IMAGE_MESSAGE, sendUri.toString(), sendUri.toString(), sendUri.toString(), null,
//                                NOTIFICATION_DEFAULT, message.getUuid(), fuguThreadMessageList.size() - 1, "/" + String.valueOf(channelId),
//                                Long.valueOf(myUserId), myName, channelId, isTyping, globalUuid, com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getEmail(), dimenns, FuguInnerChatActivity.this);
//                    } else {
//                        mClient.publish(taggedUsers, getTaggedMessage(emojiGifEditText, mentionsInImage), IMAGE_MESSAGE, uri.toString(), uri.toString(), uri.toString(), null,
//                                NOTIFICATION_DEFAULT, message.getUuid(), fuguThreadMessageList.size() - 1, "/" + String.valueOf(channelId),
//                                Long.valueOf(myUserId), myName, channelId, isTyping, globalUuid, com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getEmail(), dimenns, FuguInnerChatActivity.this);
//                    }
            }
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FuguAppConstant.PERMISSION_CONSTANT_CAMERA && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
            fuguImageUtils!!.startCamera()
        }
    }

    private fun enterTransition(): Transition {
        val bounds = ChangeBounds()
        bounds.duration = 400
        return bounds
    }

    private fun returnTransition(): Transition {
        val bounds = ChangeBounds()
        bounds.interpolator = DecelerateInterpolator()
        bounds.duration = 2000
        return bounds
    }

    private val mMessageNotificationReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                val recievedChannelId = intent.getLongExtra(FuguAppConstant.CHANNEL_ID, -1L)
                if (intent.hasExtra(FuguAppConstant.CHANNEL_ID) && recievedChannelId.compareTo(channelId) == 0) {
                    val messageRecieved = intent.getSerializableExtra(TEXTMESSAGE) as Message
                    var messageTobeAdded = true
                    for (i in messageList.indices.reversed()) {
                        if (intent.hasExtra("is_thread_message") && !intent.getBooleanExtra("is_thread_message", false)) {
                            if (messageList[i].muid == intent.getStringExtra(FuguAppConstant.MESSAGE_UNIQUE_ID)) {
                                messageTobeAdded = false
                                break
                            }
                        } else {
                            if (messageList[i].muid == intent.getStringExtra(FuguAppConstant.MESSAGE_UNIQUE_ID)) {
                                messageTobeAdded = false
                                val message = messageList[i]
                                message.threadMessage = true
                                message.threadMessageCount = message.threadMessageCount + 1
                                messageList[i] = message
                                break
                            }
                        }
                    }
                    if (messageTobeAdded && messageRecieved != null) {
                        messageList.add(messageRecieved)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private val mPublicChatReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                if (intent.hasExtra(FuguAppConstant.USER_IDS_TO_REMOVE_ADMIN)) {
                    val userIdsToRemoveAdmin = intent.getIntegerArrayListExtra(FuguAppConstant.USER_IDS_TO_REMOVE_ADMIN)!!
                    for (i in userIdsToRemoveAdmin.indices) {
                        if (java.lang.Long.valueOf(myUserId).compareTo(userIdsToRemoveAdmin[i].toLong()) == 0) {
                            userRole = "USER"
                            break
                        }
                    }
                }
                if (intent.hasExtra(FuguAppConstant.USER_IDS_TO_MAKE_ADMIN)) {
                    val userIdsToMakeAdmin = intent.getIntegerArrayListExtra(FuguAppConstant.USER_IDS_TO_MAKE_ADMIN)!!
                    for (i in userIdsToMakeAdmin.indices) {
                        if (java.lang.Long.valueOf(myUserId).compareTo(userIdsToMakeAdmin[i].toLong()) == 0) {
                            userRole = "ADMIN"
                            break
                        }
                    }
                }
                if (intent.hasExtra("only_admin_can_message")) {
                    only_admin_can_message = intent.getBooleanExtra("only_admin_can_message", false)
                    if (!intent.getBooleanExtra("only_admin_can_message", false)) {
                        llMessageLayout!!.visibility = View.VISIBLE
                        tvCannotReply!!.visibility = View.GONE
                    } else if (intent.getBooleanExtra("only_admin_can_message", false) && userRole == "ADMIN") {
                        llMessageLayout!!.visibility = View.VISIBLE
                        tvCannotReply!!.visibility = View.GONE
                    } else {
                        llMessageLayout!!.visibility = View.GONE
                        tvCannotReply!!.visibility = View.VISIBLE
                        val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                        params.addRule(RelativeLayout.ABOVE, R.id.tvCannotReply)
                        params.addRule(RelativeLayout.BELOW, R.id.my_toolbar)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //    private BroadcastReceiver mMessageNotificationReceiver = new BroadcastReceiver() {
    //        override fun
    //
    //        onReceive(context:Context?, intent:Intent?) {
    //            Handler().postDelayed({
    //            try {
    //                if (intent ?.hasExtra(CHANNEL_ID)
    //                !! && intent.getLongExtra(CHANNEL_ID, -1L).compareTo(channelId) == 0){
    //                    val messageRecieved = intent ?.getSerializableExtra(MESSAGE) as Message
    //                    var messageTobeAdded = true
    //                    for (message in messageList.reversed()) {
    //                        if (message.muid.equals(messageRecieved.muid)) {
    //                            messageTobeAdded = false
    //                            break
    //                        }
    //                    }
    //                    runOnUiThread {
    //                        if (messageTobeAdded) {
    //                            Log.e("Message", "Added")
    //                            messageList.add(messageRecieved)
    //                            messagesMap[messageRecieved.muid] = messageRecieved
    //                            messageAdapter ?.updateMessageList(messageList)
    //                            messageAdapter ?.notifyDataSetChanged()
    //                            rvMessages.scrollToPosition(messageList.size - 1)
    //                        }
    //                        Log.e("Message", "Not Added")
    //                    }
    //
    //                }
    //            } catch (e:Exception){
    //                e.printStackTrace()
    //            }
    //            },500)
    //        }
    //    }
    fun copyUrl(url: String?) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("", url)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this@FuguInnerChatActivity, "Link Copied to Clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun sendImage(imageFile: ImageFile) {
        try {
            val cursor = this@FuguInnerChatActivity.contentResolver.query(getUriFromPath(imageFile.path), null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    try {
                        val extensions = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)).split("\\.".toRegex()).toTypedArray()
                        extension = extensions[extensions.size - 1].toLowerCase()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (extension!!.toLowerCase() == "png") {
                extension = "jpg"
                val bitmap = BitmapFactory.decodeFile(imageFile.path)
                val inputStream = contentResolver.openInputStream(FileProvider.getUriForFile(this@FuguInnerChatActivity, BuildConfig.APPLICATION_ID + ".provider", File(imageFile.path)))!!
                val fileOutputStream = FileOutputStream(imageFile.path)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                val buffer = ByteArray(1024)
                val bytesRead = 0
                var i = inputStream.read(buffer)
                while (i > -1) {
                    i = inputStream.read(buffer)
                    fileOutputStream.write(buffer, 0, bytesRead)
                }
                fileOutputStream.close()
            }
            val dimensGallery = getDropboxIMGSize(getUriFromPath(imageFile.path))
            val localDate2 = DateUtils.getFormattedDate(Date())
            val date2 = DateUtils.getDate(localDate2)
            val fileDetails2 = fuguImageUtils!!.saveFile(getUriFromPath(imageFile.path), FuguAppConstant.FILE_TYPE_MAP[extension!!.toLowerCase()], channelId, localDate2)
            compressAndSaveImageBitmap("", extension, dimensGallery, FuguAppConstant.IMAGE_MESSAGE, fileDetails2)
        } catch (e: Exception) {
        }
    }

    fun apiUserSearch(searchStrLowerCase: String, userSearchApi: UserSearchApi) {
        if (!isMentionSelected) {
            val workspaceInfo = CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()]
            val commonParams = com.skeleton.mvp.retrofit.CommonParams.Builder()
                    .add(FuguAppConstant.EN_USER_ID, workspaceInfo.enUserId)
                    .add(AppConstants.SEARCH_TEXT, searchStrLowerCase)
                    .add(FuguAppConstant.CHANNEL_ID, channelId)
                    .add("include_current_user", true)
                    .add("tagging", true)
            RestClient.getApiInterface(true).userSearch(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), workspaceInfo.fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                    .enqueue(object : com.skeleton.mvp.data.network.ResponseResolver<UserSearch>() {
                        override fun onSuccess(userSearch: UserSearch) {
                            if (userSearch.data.users.size == 0) {
                                searchPrefix = searchStrLowerCase
                            }
                            val matchValues = ArrayList<Member>()
                            for (user in userSearch.data.users) {
                                if (user.userId.toString() != myUserId) {
                                    val getAllMembers = Member(user.fullName, user.userId, "",
                                            user.userThumbnailImage, user.email, user.userType, user.status, user.leaveType)
                                    matchValues.add(getAllMembers)
                                }
                            }
                            if ("Everyone".toLowerCase().contains(searchStrLowerCase)) {
                                matchValues.add(0, Member("Everyone", -1L, "", "https://fuguchat.s3.ap-south-1.amazonaws.com/default/WwX5qYGSEb_1518441286074.png", "", 1, "", ""))
                            }
                            userSearchApi.onSuccess(matchValues)
                        }

                        override fun onError(error: ApiError) {}
                        override fun onFailure(throwable: Throwable) {}
                    })
        } else {
            isMentionSelected = false
        }
    }

    companion object {
        private const val MESSAGE = "MESSAGE"
        private const val TEXTMESSAGE = "message"
        private const val NOT_CONNECTED = 0
        private const val CONNECTED_TO_INTERNET = 1
        private const val CONNECTED_TO_INTERNET_VIA_WIFI = 2
        var currentViewPagerposition = 0
    }
}