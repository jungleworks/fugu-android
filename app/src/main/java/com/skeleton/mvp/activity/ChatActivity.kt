package  com.skeleton.mvp.activity

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.media.ExifInterface
import android.net.Uri
import android.os.*
import android.provider.OpenableColumns
import android.text.*
import android.text.format.Time
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.PermissionChecker
import androidx.fragment.app.DialogFragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.downloader.PRDownloader
import com.downloader.Status
import com.easyfilepicker.activity.AudioPickActivity
import com.easyfilepicker.activity.ImagePickActivity
import com.easyfilepicker.activity.NormalFilePickActivity
import com.easyfilepicker.activity.VideoPickActivity
import com.easyfilepicker.filter.entity.AudioFile
import com.easyfilepicker.filter.entity.ImageFile
import com.easyfilepicker.filter.entity.NormalFile
import com.easyfilepicker.filter.entity.VideoFile
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.percolate.mentions.Mentions
import com.percolate.mentions.QueryListener
import com.percolate.mentions.SuggestionsListener
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.adapter.*
import com.skeleton.mvp.adapter.mentions.UsersAdapter
import com.skeleton.mvp.apis.*
import com.skeleton.mvp.calendar.AuthorizeGoogleActivity
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.data.db.ChatDatabase
import com.skeleton.mvp.data.model.addCalendarEvent.AddEventResponse
import com.skeleton.mvp.data.model.fcCommon.UserAttendanceConfig
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.fragment.*
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.model.*
import com.skeleton.mvp.model.Message
import com.skeleton.mvp.model.botConfig.Example
import com.skeleton.mvp.model.customAction.Button
import com.skeleton.mvp.model.getAllMembers.AllMember
import com.skeleton.mvp.model.getAllMembers.AllMemberResponse
import com.skeleton.mvp.model.media.ChatMember
import com.skeleton.mvp.model.media.MediaResponse
import com.skeleton.mvp.model.mentions.Mention
import com.skeleton.mvp.model.starredmessage.StarredMessagelResponse
import com.skeleton.mvp.model.userSearch.UserSearch
import com.skeleton.mvp.photoEditor.EmojiBSFragment
import com.skeleton.mvp.photoEditor.PropertiesBSFragment
import com.skeleton.mvp.photoEditor.TextEditorDialogFragment
import com.skeleton.mvp.pushNotification.PushReceiver
import com.skeleton.mvp.retrofit.*
import com.skeleton.mvp.service.OngoingCallService
import com.skeleton.mvp.service.VideoCallService
import com.skeleton.mvp.socket.SocketConnection
import com.skeleton.mvp.socket.SocketConnection.initSocketConnection
import com.skeleton.mvp.socket.SocketConnection.setSocketListeners
import com.skeleton.mvp.ui.AppConstants
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.ui.base.BaseView
import com.skeleton.mvp.ui.browsegroup.BrowseGroupActivity
import com.skeleton.mvp.ui.fcinvite.InviteOnboardActivity
import com.skeleton.mvp.ui.profile.ProfileActivity
import com.skeleton.mvp.util.*
import com.skeleton.mvp.util.RecyclerItemClickListener
import com.skeleton.mvp.util.RecyclerItemClickListener.OnItemClickListener
import com.skeleton.mvp.utils.*
import com.skeleton.mvp.utils.FuguUtils.Companion.getTimeZoneOffset
import com.skeleton.mvp.utils.beatAnimation.AVLoadingIndicatorViewFugu
import com.skeleton.mvp.videoCall.FuguCallActivity
import com.skeleton.mvp.videoCall.VideoCallModel
import com.skeleton.mvp.videoCall.WebRTCCallConstants
import com.theartofdev.edmodo.cropper.CropImage
import com.wang.avi.AVLoadingIndicatorView
import com.wang.avi.indicators.BallClipRotateMultipleIndicator
import io.socket.client.Socket
import ja.burhanrashid52.photoeditor.*
import okhttp3.MultipartBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap


@Suppress("ConvertToStringTemplate", "DEPRECATION", "NAME_SHADOWING", "UNCHECKED_CAST", "unused", "PrivatePropertyName", "ReplaceRangeToWithUntil", "ReplaceRangeToWithUntil")
class ChatActivity : BaseActivity(), View.OnClickListener, SuggestionsListener, QueryListener,
        ProgressRequestBody.UploadCallbacks, EmojiAdapter.GetClickedEmoji, EmojiAdapter.ThreadOpened, Readfunctionality, Animation.AnimationListener, MessageAdapter.SendPollOption,
        FuguBotAdapter.OnItemClick, ButtonsAdapter.ButtonClick, MessageAdapter.CommentInterface, CustomActionsAdapter.TextChange, LocationFetcher.GetCurrentLocation,
        ExtendedTouchListener.OnAdapterItemTouchListener, OnPhotoEditorListener,
        PropertiesBSFragment.Properties, EmojiBSFragment.EmojiListener {

    private lateinit var retryProgressBar: ProgressBar
    private lateinit var llRetryMsg: LinearLayout
    private lateinit var llChannelInfo: LinearLayout
    private lateinit var tvRetry: TextView
    private lateinit var frameChat: FrameLayout
    private lateinit var ivBack: AppCompatImageView
    private lateinit var ivEmoji: AppCompatImageView
    private lateinit var ivAttachment: AppCompatImageView
    private lateinit var btnLl: LinearLayout
    private lateinit var btnTryAgain: AppCompatButton
    private lateinit var ivSend: AppCompatImageView
    private lateinit var tvTitle: TextView
    private lateinit var rvMessages: RecyclerView
    private lateinit var etMessage: EmojiGifEditText
    private lateinit var conversation: FuguConversation
    private var messageAdapter: MessageAdapter? = null
    private var messageList = ArrayList<Message>()
    private var diffUtilMessageList = ArrayList<Message>()
    private var localMessageList = ArrayList<Message>()
    private lateinit var layoutManager: CustomLinearLayoutManager
    private var channelId: Long = -1L
    private var enUserId: String = ""
    private var appSecretKey: String = ""
    private val emptyString: String = ""
    private var userId: Long = -1L
    private lateinit var workspaceInfoList: ArrayList<WorkspacesInfo>
    private var currentPosition: Int = 0
    private var isTyping = FuguAppConstant.TYPING_SHOW_MESSAGE
    private var userName: String = ""
    private val membersMap = HashMap<Long, Member>()
    private val membersList = java.util.ArrayList<Member>()
    private var chatType: Int = 2
    private lateinit var rvMentions: RecyclerView
    private lateinit var llMention: LinearLayoutCompat
    private lateinit var mentions: Mentions
    private lateinit var mentionsInImage: Mentions
    private lateinit var llJoin: LinearLayout
    private var usersAdapter: UsersAdapter? = null
    private var usersAdapterImage: UsersAdapter? = null
    private var taggedUsers: java.util.ArrayList<Int>? = java.util.ArrayList()
    private var messagesMap: LinkedHashMap<String, Message> = LinkedHashMap()
    private var mLastClickTime: Long = 0
    private var fuguImageUtils: FuguImageUtils? = null
    private val REQUEST_GROUP_INFO = 1254
    val PUBLIC_NOTE = 7
    private var isDialogOpened: Boolean = false
    private var emojiPopup: EmojiPopup? = null
    private lateinit var clickedEmojiMuid: String
    private var pageSize = 1
    private var getMessagePageSize = 100
    private lateinit var progressBar: ProgressBar
    private lateinit var llRoot: CustomLinear
    private lateinit var llTyping: LinearLayout
    private lateinit var llMessageLayout: LinearLayout
    private var aviTyping: AVLoadingIndicatorViewFugu? = null
    private var unsentMessageMap = java.util.LinkedHashMap<String, Message>()
    private lateinit var dateUtils: DateUtils
    private var isScrolled = false
    private var animSlideUp: Animation? = null
    private var animSlideDown: Animation? = null
    private var starAnimation: Animation? = null
    private var tvDateLabel: TextView? = null
    private var isUnreadItemAdded: Boolean = false
    private var isAlreadyScrolled = false
    private var threadOpened: Boolean = false
    private var positionToBeInserted = 0
    private var sharedMuid: String = ""
    private lateinit var rlScrollBottom: RelativeLayout
    private var scrollBottomCount = 0
    private lateinit var tvUnread: TextView
    private var fileShared: Boolean = false
    private var fileForwarded: Boolean = false
    private var isGroupOpened: Boolean = false
    private var mentionsList: ArrayList<Mention> = ArrayList()
    private lateinit var ivImageMsg: LinearLayoutCompat
    private lateinit var ivLogo: ImageView
    private lateinit var tvChannelName: TextView
    private lateinit var rlChannelIcon: RelativeLayout
    private var imageText = ""
    private var imageRecycler: RecyclerView? = null
    private var imageLlMentions: LinearLayoutCompat? = null
    private var isSearchedMessage: Boolean = false
    private var isNotificationMessage: Boolean = false
    private var messageSearchIndex: Int = 0
    private var messageSearchMuid: String = ""
    private var notificationMuid: String = ""
    private var isAlreadyHighlighted = false
    private var isChatCountIncremented = false
    private var isCustomLabel = false
    private var maxUploadSize = 0L
    private lateinit var llCannotReply: LinearLayout
    private lateinit var ivVideoCall: ImageView
    private lateinit var ivHangoutsCall: ImageView
    private lateinit var ivAudioCall: ImageView
    private lateinit var ivCancelEdit: ImageView
    private lateinit var userImage: String
    private lateinit var tvReturnCall: TextView
    private lateinit var mainRoot: LinearLayoutCompat
    private var dialog: Dialog? = null
    private var rvBotActions: RecyclerView? = null
    private var botAdapter: FuguBotAdapter? = null
    var actionList = ArrayList<FuguBotAdapter.BotAction>()
    var selectedBotAction: FuguBotAdapter.BotAction? = null
    var otherUserid = -1L
    private var llBotAction: LinearLayout? = null
    private var tvTag: AppCompatTextView? = null
    private var tvInputParameter: AppCompatTextView? = null
    lateinit var avi: AVLoadingIndicatorView
    private var dialogView: View? = null
    var iv: ImageView? = null
    var rl: RelativeLayout? = null
    var isKeyBoardVisible = false
    var isMessageInEditMode = false
    var firstEditMuid = ""
    var typingTimer: CountDownTimer? = null
    var startTypingTimer: CountDownTimer? = null
    private var tvTyping: TextView? = null
    private var lat = 0.0
    private var lon = 0.0
    private var pbVerify: ProgressBar? = null
    private var camerabutton: Button? = null
    private var cameraMuid: String? = null
    var action: String = ""
    var isMockLocation = false
    var isScreenOpenedFromThread = false
    var touchState: Int = -1
    var isCountdownRunning = false
    var tvInfo: TextView? = null
    private var fileToBeDelete: File? = null
    private var messageWithCroppedImageDialog: Dialog? = null
    var userIdsSearch = ArrayList<Long>()
    var multiMemberAddGroupMap = java.util.LinkedHashMap<Long, Member>()
    private var mPhotoEditor: PhotoEditor? = null
    private var mPhotoEditorView: PhotoEditorView? = null
    private var mPropertiesBSFragment: PropertiesBSFragment? = null
    private var mEmojiBSFragment: EmojiBSFragment? = null
    private var imgUndo: AppCompatImageView? = null
    private var imgEdit: AppCompatImageView? = null
    private var imgEmoji: AppCompatImageView? = null
    private var imgText: AppCompatImageView? = null
    private var imgCrop: AppCompatImageView? = null
    private var imgSend: AppCompatImageView? = null
    val stickyLabel = StickyLabel()
    val messageFileJson = MessageFileJson()
    var chatHandler: Handler? = null
    var uuidsList = ArrayList<String>()
    private var messagePosition: Int = -1
    var isApiGetMessagesCompleted = false
    var ivSendVideo: ImageView? = null
    var ivSendImage: ImageView? = null
    var imageProgress: ProgressBar? = null
    var videoProgress: ProgressBar? = null
    var tempSentAtUtc = ""
    var searchPrefix = "-"
    private var ivGreenDot: AppCompatImageView? = null
    private var llStatus: LinearLayout? = null
    var isMentionCLicked = false
    var searchCountDown: CountDownTimer? = null
    var countDownTimer: CountDownTimer? = null
    var userCount = 0L
    var userPageSize = 0
    var firstMessageId = 0L
    var lastMessageId = 0L
    var onlyAdminsCanMessage = false
    var userRole = "USER"
    private var bottomSheetFragment: BottomSheetFragment? = null
    private var rvEmoji: RecyclerView? = null
    private var cvEmoji: CardView? = null
    private var viewTranslucent: View? = null
    private var rlEmoji: RelativeLayout? = null
    var mCallType: String = ""
    private var isPermissionsGranted = false
    private var mGoogleSignInClient: GoogleSignInClient? = null
    var leaveType: String? = ""
    lateinit var llInternet: LinearLayout
    lateinit var tvStatus: TextView
    lateinit var tvRetryMsg: TextView
    private val keyboardListener = KeyboardUtil.SoftKeyboardToggleListener { isVisible -> isKeyBoardVisible = isVisible }

    object PagerPosition {
        var currentViewPagerPosition = 0
    }

    companion object {
        private const val NOT_CONNECTED = 0
        private const val CONNECTED_TO_INTERNET = 1
        private const val CONNECTED_TO_INTERNET_VIA_WIFI = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        initializeViews()
        addClickListeners()
        fetchIntentData()
//        if (chatType == ChatType.O2O) {
//            ivHangoutsCall.visibility = View.VISIBLE
//        }
        if (chatType != ChatType.O2O && chatType != ChatType.BOT) {
            llStatus?.visibility = View.VISIBLE
            tvInfo?.visibility = View.VISIBLE
            tvInfo?.text = "tap to view Info"
        }
        if (intent.hasExtra("fromHome") && intent.getBooleanExtra("fromHome", false)) {
            sentSharedAttachment()
        } else {
            CommonData.setSharedText("")
            CommonData.setImageUri("")
            CommonData.setOtherFilesUriString("")
        }

        GeneralFunctions().setToolbarLabel(
                conversation.label,
                conversation.businessName,
                tvTitle,
                chatType,
                leaveType!!
        )
        if (!isNetworkConnected) {
            llInternet.visibility = View.VISIBLE
            llInternet.setBackgroundColor(Color.parseColor("#FF0000"))
            tvStatus.setText(R.string.fugu_not_connected_to_internet)
        }
        setRecyclerView()
        setUpStickyLabel()
        Handler().postDelayed({
            setUpStickyLabel()
        }, 2000)
        setUpSocketListeners("Create Chat")
        llRoot.setOnKeyBoardStateChanged { false }
        checkAndFetchMessage()
        setUpMentions()
        when (chatType) {
            3, 4, 5, 6 -> {
                setUpMentionsList()
                if (membersList.size == 0 && isNetworkConnected) {
                    apiFetchMembers()
                }
            }
            7 -> {
                setUpMentionsList()
                if (membersList.size == 0 && isNetworkConnected && conversation.otherUserType != UserType.FUGU_SUPPORT) {
                    apiGetMembers()
                }
            }
        }
        GeneralFunctions().removeNotification(channelId)
    }

    fun changeInternetStatus(status: Int) {
        try {
            when (status) {
                NOT_CONNECTED -> {
                    llInternet.visibility = View.VISIBLE
                    llInternet.setBackgroundColor(Color.parseColor("#FF0000"))
                    tvStatus.setText(R.string.fugu_not_connected_to_internet)
                }
                CONNECTED_TO_INTERNET, CONNECTED_TO_INTERNET_VIA_WIFI -> {
                    try {
                        llInternet.setBackgroundColor(Color.parseColor("#FFA500"))
                        tvStatus.setText(R.string.fugu_connecting)
                        Handler().postDelayed({
                            llInternet.setBackgroundColor(Color.parseColor("#00FF00"))
                            tvStatus.setText(R.string.fugu_connected)
                            llInternet.visibility = View.GONE
                        }, 1500)
                    } catch (e: java.lang.Exception) {
                        Log.e("ChatActivity", e.message)
                        e.printStackTrace()
                    }
                }
                else -> {
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpStickyLabel() {
        stickyLabel.updateMessageList(diffUtilMessageList)
        stickyLabel.setUpStickyLabel(this@ChatActivity,
                rvMessages,
                animSlideUp,
                tvDateLabel,
                layoutManager,
                diffUtilMessageList,
                tvUnread,
                object : StickyLabel.ScrollManipulation {
                    override fun hideScroll() {
                        scrollBottomCount = 0
                        rlScrollBottom.visibility = View.GONE
//                rvMessages?.smoothScrollToPosition(messageList.size - 1)
                    }

                    override fun showScroll() {
                        isAlreadyScrolled = true
                        rlScrollBottom.visibility = View.VISIBLE
                    }
                })
    }

    private fun checkAndFetchMessage() {
        if (isNetworkConnected) {
            apiGetMessages(false)
            btnLl.visibility = View.GONE
            frameChat.visibility = View.VISIBLE
        } else {
            isApiGetMessagesCompleted = true
            if (diffUtilMessageList.isEmpty()) {
                frameChat.visibility = View.GONE
                btnLl.visibility = View.VISIBLE
            } else {
                btnLl.visibility = View.GONE
                frameChat.visibility = View.VISIBLE
            }
        }
    }

    private fun apiGetMembers() {
        ApiGetAllMembers().apiGetMembers(object : ApiGetAllMembers.GetMembersCallback {
            override fun onSuccess(allMemberResponse: AllMemberResponse) {
                userCount = allMemberResponse.data?.userCount!!.toLong()
                userPageSize = allMemberResponse.data?.getAllMemberPageSize!!
                membersList.clear()
                Thread {
                    kotlin.run {
                        for (member in 0 until allMemberResponse.data.allMemberResponse.size) {
                            val datum: MutableList<AllMember>? = allMemberResponse.data?.allMemberResponse!!
                            if (datum?.get(member)?.fuguUserId?.toLong()?.compareTo(userId) != 0
                                    && (!TextUtils.isEmpty(datum?.get(member)?.fullName)
                                            && !TextUtils.isEmpty(datum?.get(member)?.email) && datum?.get(member)?.fuguUserId != null)
                            ) {
                                val memberObject = Member(
                                        datum[member].fullName,
                                        datum[member].fuguUserId?.toLong(),
                                        datum[member].email,
                                        datum[member].userImage,
                                        datum[member].email,
                                        1,
                                        datum[member].status,
                                        datum[member].leaveType
                                )
                                membersList.add(memberObject)
                                membersMap[datum[member].fuguUserId] = memberObject
                            }
                        }
//                        try {
//                            membersList.sortWith(Comparator { one, other -> one.name.compareTo(other.name) })
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
                        if (chatType != 7) {
                            membersList.add(
                                    0, Member(
                                    "Everyone",
                                    -1L,
                                    "",
                                    "https://fuguchat.s3.ap-south-1.amazonaws.com/default/WwX5qYGSEb_1518441286074.png",
                                    "",
                                    1,
                                    "",
                                    ""
                            )
                            )
                            membersMap.put(
                                    -1L, Member(
                                    "Everyone",
                                    -1L,
                                    "",
                                    "https://fuguchat.s3.ap-south-1.amazonaws.com/default/WwX5qYGSEb_1518441286074.png",
                                    "",
                                    1,
                                    "",
                                    ""
                            )
                            )
                        }
                    }
                }.start()
            }
        })
    }

    private fun initializeViews() {
        chatHandler = Handler()
        retryProgressBar = findViewById(R.id.retryLoading)
        llRetryMsg = findViewById(R.id.ll_retry_msg)
        tvRetryMsg = findViewById(R.id.tv_retry_message)
        frameChat = findViewById(R.id.frameChat)
        tvRetry = findViewById(R.id.tv_retry)
        btnLl = findViewById(R.id.btn_layout_try_again)
        ivBack = findViewById(R.id.ivBack)
        ivEmoji = findViewById(R.id.ivEmoji)
        ivAttachment = findViewById(R.id.ivAttachment)
        ivSend = findViewById(R.id.ivSend)
        rvMessages = findViewById(R.id.rvMessages)
        etMessage = findViewById(R.id.etMsg)
        llChannelInfo = findViewById(R.id.llChannelInfo)
        tvTitle = findViewById(R.id.tvTitle)
        rvMentions = findViewById(R.id.rv_mentions)
        llMention = findViewById(R.id.ll_mentions_layout)
        llMessageLayout = findViewById(R.id.llMessageLayout)
        llJoin = findViewById(R.id.llJoin)
        rlScrollBottom = findViewById(R.id.rlScrollBottom)
        tvUnread = findViewById(R.id.tvUnread)
        llInternet = findViewById(R.id.llInternet)
        ivLogo = findViewById(R.id.ivLogo)
        tvChannelName = findViewById(R.id.tvChannelIcon)
        rlChannelIcon = findViewById(R.id.rlChannelIcon)
        llCannotReply = findViewById(R.id.tvCannotReply)
        ivVideoCall = findViewById(R.id.ivCall)
        ivHangoutsCall = findViewById(R.id.ivHangoutsCall)
        ivAudioCall = findViewById(R.id.ivAudioCall)
        tvReturnCall = findViewById(R.id.tvReturnCall)
        mainRoot = findViewById(R.id.mainRoot)
        rvBotActions = findViewById(R.id.rvBotActions)
        llBotAction = findViewById(R.id.llBotAction)
        tvTag = findViewById(R.id.tvTag)
        tvInputParameter = findViewById(R.id.tvInputParameter)
        ivCancelEdit = findViewById(R.id.ivCancelEdit)
        tvTyping = findViewById(R.id.tvTyping)
        pbVerify = findViewById(R.id.pbVerify)
        tvInfo = findViewById(R.id.tvInfo)
        ivGreenDot = findViewById(R.id.ivGreenDot)
        llStatus = findViewById(R.id.llStatus)
        viewTranslucent = findViewById(R.id.viewTranslucent)
        rvEmoji = findViewById(R.id.rvEmoji)
        rlEmoji = findViewById(R.id.rlEmojiLayout)
//        cvEmoji = findViewById(R.id.cvEmoji)

        KeyboardUtil.addKeyboardToggleListener(this, keyboardListener)
        etMessage.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(inputMessage: CharSequence?, start: Int, before: Int, count: Int) {
                runOnUiThread {
                    Timer()
                    if (inputMessage?.length!! > 0) {
                        ivSend.isClickable = true
                        ivSend.alpha = 1f
//                        ivSend.visibility=View.VISIBLE
                        if (chatType == ChatType.BOT && inputMessage.substring(0, 1).equals("/")) {
                            startFilteringBotActions(inputMessage.toString())
                        }
                    } else {
                        ivSend.isClickable = false
                        ivSend.alpha = 0.5f
//                        ivSend.visibility=View.GONE
                        if (chatType == ChatType.BOT) {
                            rvBotActions?.visibility = View.GONE
                        }
                    }
                    if (startTypingTimer == null) {
                        isTyping = FuguAppConstant.TYPING_STARTED
                        if (channelId > -1 && !etMessage.text.toString().isEmpty() && !isMessageInEditMode) {
                            SocketConnection.startTyping(GeneralFunctions().getTypingObject(userName, userId, channelId))
                        }
                        startTypingTimer = object : CountDownTimer(800, 800) {
                            override fun onTick(millisUntilFinished: Long) {
                            }

                            override fun onFinish() {
                                if (isTyping != FuguAppConstant.TYPING_STOPPED) {
                                    isTyping = FuguAppConstant.TYPING_STARTED
                                    if (channelId > -1 && !etMessage.text.toString().isEmpty() && !isMessageInEditMode) {
                                        SocketConnection.startTyping(GeneralFunctions().getTypingObject(userName, userId, channelId))
                                    }
                                    startTypingTimer = null
                                }
                            }
                        }.start()
                    }
                    typingTimer?.cancel()
                    typingTimer = object : CountDownTimer(3000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                        }

                        override fun onFinish() {
                            if (isTyping == FuguAppConstant.TYPING_STARTED) {
                                isTyping = FuguAppConstant.TYPING_STOPPED
                                if (channelId > -1 && !isMessageInEditMode) {
                                    SocketConnection.stopTyping(GeneralFunctions().getTypingObject(userName, userId, channelId))
                                }
                            }
                        }
                    }.start()
                }
            }
        })
        fuguImageUtils = FuguImageUtils(this)
        llRoot = findViewById(R.id.llRoot)
        avi = findViewById(R.id.avi)
        progressBar = findViewById(R.id.pbLoading)
        llTyping = findViewById(R.id.llTyping)
        aviTyping = findViewById(R.id.aviTyping)
        aviTyping?.setIndicatorColor(Color.parseColor("#2c2333"))
        tvStatus = findViewById(R.id.tvStatus)
        dateUtils = DateUtils()
        tvDateLabel = findViewById(R.id.tvDateLabel)
        animSlideUp = AnimationUtils.loadAnimation(applicationContext, R.anim.fugu_slide_up_time)
        animSlideUp?.setAnimationListener(this)
        animSlideDown = AnimationUtils.loadAnimation(applicationContext, R.anim.fugu_slide_down_time)
        animSlideDown?.setAnimationListener(this)
    }

    private fun startFilteringBotActions(input: String) {
        var filteredList = actionList
        rvBotActions?.visibility = View.VISIBLE
        if (!TextUtils.isEmpty(input)) {
            if (input.equals("/")) {
                if (botAdapter != null) {
                    botAdapter?.setFilteredList(actionList)
                }
            } else {
                filteredList = ArrayList()
                for (action in actionList) {
                    if (action.displayTag.contains(input)) {
                        filteredList.add(action)
                    }
                }
                if (botAdapter != null) {
                    botAdapter?.setFilteredList(filteredList)
                }
                if (filteredList.size == 0) {
                    rvBotActions?.visibility = View.GONE
                }
            }
            if (selectedBotAction != null && !input.contains(selectedBotAction?.displayTag!!)) {
                llBotAction?.visibility = View.GONE
            }
        }
    }

    private fun addClickListeners() {
        ivBack.setOnClickListener(this)
        ivEmoji.setOnClickListener(this)
        ivAttachment.setOnClickListener(this)
        ivSend.setOnClickListener(this)
        ivVideoCall.setOnClickListener(this)
        ivHangoutsCall.setOnClickListener(this)
        ivAudioCall.setOnClickListener(this)
        ivCancelEdit.setOnClickListener(this)
        tvRetry.setOnClickListener {
            checkAndFetchMessage()
        }
        llJoin.setOnClickListener {
            apiJoinGroup()
        }
        ivEmoji.setOnClickListener {
            if (emojiPopup == null) {
                setUpEmojiPopup()
            }
            if (isDialogOpened) {
                isDialogOpened = false
                ivEmoji.setImageResource(R.drawable.ic_happiness)
            } else {
                isDialogOpened = true
                ivEmoji.setImageResource(R.drawable.ic_keyboard)
            }
            emojiPopup?.toggle()
        }
        etMessage.setCommitListener { uri, sendUri ->
            val dimens = java.util.ArrayList<Int>()
            val localDate = DateUtils.getFormattedDate(Date())
            val fileDetails =
                    fuguImageUtils?.saveFile(uri, FILE_TYPE_MAP["gif"], channelId, localDate)
            if (sendUri != null) {
                showImageDialogUriLink(uri, sendUri, this@ChatActivity, uri.toString())
            } else {
                showImageDialog(
                        uri,
                        uri,
                        this@ChatActivity,
                        uri.toString(),
                        dimens,
                        fileDetails,
                        fileDetails!!.filePath
                )
            }

        }
        rlScrollBottom.setOnClickListener {
            rlScrollBottom.visibility = View.GONE
            scrollBottomCount = 0
            rvMessages.scrollToPosition(diffUtilMessageList.size - 1)
        }
    }

    private fun showImageDialogUriLink(uri: Uri?, sendUri: Uri?, chatActivity: ChatActivity, imgUrl: String) {
        try {
            val dialog = Dialog(chatActivity, android.R.style.Theme_Translucent_NoTitleBar)
            dialog.setContentView(R.layout.fugu_image_dialog_send)
            val lp = dialog.window!!.attributes
            val ivSend: ImageView
            val dialogRecyclerView: RecyclerView = dialog.findViewById(R.id.rv_mentions)!!
            val etMsg: EmojiGifEditText
            val llImageWithMessage: LinearLayoutCompat = dialog.findViewById(R.id.llimagewithmessgae)!!
            llImageWithMessage.alpha = 0.5f
            ivSend = dialog.findViewById(R.id.ivSend)
            ivSend.alpha = 1f
            etMsg = dialog.findViewById(R.id.etMsg)
            setUpMentionsForImage(etMsg)
            setUpMentionsListForImage(dialogRecyclerView)
            imageRecycler = dialogRecyclerView
            imageLlMentions = dialog.findViewById(R.id.ll_mentions_layout)
            imageRecycler!!.visibility = View.VISIBLE
            imageLlMentions!!.visibility = View.VISIBLE
            etMsg.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    imageText = s.toString()
                }
            })
            lp.dimAmount = 1.0f
            dialog.window!!.attributes = lp
            dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(false)
            val ivImage: PhotoView
            ivImage = dialog.findViewById(R.id.ivImage)
            val dimenns = java.util.ArrayList<Int>()
            Glide.with(chatActivity)
                    .load(imgUrl)
                    .into(ivImage)
            ivSend.setOnClickListener {
                val message: String = GeneralFunctions().getTaggedMessage(mentionsList, etMsg)
                dimenns.add(ivImage.drawable.intrinsicHeight)
                dimenns.add(ivImage.drawable.intrinsicWidth)
                val muid = UUID.randomUUID().toString()
                addMessageLinkToList(
                        message, IMAGE_MESSAGE, uri.toString(),
                        uri.toString(), uri.toString(), sendUri.toString(), null, muid, dimenns, true
                )
                var messageJson: JSONObject? = null
                if (sendUri != null) {
                    messageJson = MessageFileJson().createFileJson(
                            taggedUsers!!,
                            message,
                            IMAGE_MESSAGE,
                            sendUri.toString(),
                            sendUri.toString(),
                            sendUri.toString(),
                            null,
                            muid,
                            diffUtilMessageList.size - 1,
                            userId,
                            userName,
                            channelId,
                            "",
                            dimenns,
                            diffUtilMessageList,
                            this@ChatActivity
                    )
                } else {
                    messageJson = MessageFileJson().createFileJson(
                            taggedUsers!!,
                            message,
                            IMAGE_MESSAGE,
                            uri.toString(),
                            uri.toString(),
                            uri.toString(),
                            null,
                            muid,
                            diffUtilMessageList.size - 1,
                            userId,
                            userName,
                            channelId,
                            "",
                            dimenns,
                            diffUtilMessageList,
                            this@ChatActivity
                    )
                }
                if (messageJson != null) {
                    SocketConnection.sendMessage(messageJson)
                }
                dialog.dismiss()
            }
            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun apiJoinGroup() {
        ApiJoinGroup().apiJoinGroup(enUserId, channelId, appSecretKey,
                this@ChatActivity, object : ApiJoinGroup.JoinGroupCallback {
            override fun onSuccess(commonResponse: CommonResponse) {
                conversation.joined = true
                if (conversation.joined!!) {
                    llMessageLayout.visibility = View.VISIBLE
                    llJoin.visibility = View.GONE
                } else {
                    llJoin.visibility = View.VISIBLE
                    llMessageLayout.visibility = View.GONE
                }
                setResult(Activity.RESULT_OK)
            }
        })
    }

    fun showImageDialog(uri: Uri?, sendUri: Uri?, chatActivity: ChatActivity, imgUrl: String, dimens: ArrayList<Int>, fileDetails: FuguFileDetails?, path: String) {
        try {
            val dialog = Dialog(chatActivity, android.R.style.Theme_Translucent_NoTitleBar)
            dialog.setContentView(R.layout.fugu_image_dialog_send)
            val lp = dialog.window!!.attributes
            val ivSend: ImageView
            val dialogRecyclerView: RecyclerView = dialog.findViewById(R.id.rv_mentions)!!
            val etMsg: EmojiGifEditText
            val llimagewithmessgae: LinearLayoutCompat =
                    dialog.findViewById(R.id.llimagewithmessgae)!!
            llimagewithmessgae.alpha = 0.5f
            ivSend = dialog.findViewById(R.id.ivSend)
            ivSend.alpha = 1f
            etMsg = dialog.findViewById(R.id.etMsg)
            setUpMentionsForImage(etMsg)
            setUpMentionsListForImage(dialogRecyclerView)
            imageRecycler = dialogRecyclerView
            imageLlMentions = dialog.findViewById(R.id.ll_mentions_layout)
            imageRecycler!!.visibility = View.VISIBLE
            imageLlMentions!!.visibility = View.VISIBLE
            etMsg.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    imageText = s.toString()
                }
            })
            lp.dimAmount = 1.0f
            dialog.window!!.attributes = lp
            dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(false)
            val ivImage: PhotoView
            ivImage = dialog.findViewById(R.id.ivImage)
            val dimenns = java.util.ArrayList<Int>()
//            Glide.with(chatActivity)
//                    .load(imgUrl)
//                    .into(ivImage)
            Glide.with(chatActivity)
                    .load(uri)
                    .into(ivImage)
            ivSend.setOnClickListener {
                //                dimenns.add(ivImage.drawable.intrinsicHeight)
//                dimenns.add(ivImage.drawable.intrinsicWidth)
                addMessageToList(
                        GeneralFunctions().getTaggedMessage(mentionsList, etMsg),
                        IMAGE_MESSAGE,
                        path,
                        path,
                        fileDetails,
                        fileDetails?.muid!!,
                        dimens,
                        null
                )
                //compressAndSaveImageBitmap(getTaggedMessage(etMsg),"gif",dimenns,FuguAppConstant.IMAGE_MESSAGE,fileDetails!!)
                dialog.dismiss()
                //saveImage("gif", dimens, fileDetails!!, dialog, etMsg)
//                val message: String = getTaggedMessage(etMsg)

//                val muid = UUID.randomUUID().toString()
//                addMessageLinkToList(message, FuguAppConstant.IMAGE_MESSAGE, uri.toString(),
//                        uri.toString(), uri.toString(), sendUri.toString(), null, muid, dimenns, true)
//                var messageJson: JSONObject? = null
//                if (sendUri != null) {
//                    messageJson = createFileJson(taggedUsers!!, message, FuguAppConstant.IMAGE_MESSAGE,
//                            sendUri.toString(), sendUri.toString(), sendUri.toString(), null, muid,
//                            messageList.size - 1, userId, userName, channelId, "", dimenns)
//                } else {
//                    messageJson = createFileJson(taggedUsers!!, message, FuguAppConstant.IMAGE_MESSAGE,
//                            uri.toString(), uri.toString(), uri.toString(), null, muid,
//                            messageList.size - 1, userId, userName, channelId, "", dimenns)
//                }
//                if (messageJson != null) {
//                    SocketConnection.sendMessage(messageJson)
//                }
//                dialog.dismiss()
            }
            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showImageWithMessageDialog(extension: String, dimens: ArrayList<Int>, uri: Uri?, fileDetails: FuguFileDetails?) {
        val messageWithImageDialog = Dialog(this@ChatActivity, R.style.AppCompatAlertDialogStyle)
        messageWithImageDialog.setContentView(R.layout.image_message_dialog)
        ivSendImage = messageWithImageDialog.findViewById(R.id.ivSend)
        imageProgress = messageWithImageDialog.findViewById(R.id.imageProgress)
        val etMsg: EmojiGifEditText = messageWithImageDialog.findViewById(R.id.etMsg)
        val dialogRecyclerView: RecyclerView = messageWithImageDialog.findViewById(R.id.rv_mentions)
        val lp = messageWithImageDialog.window!!.attributes
        val llImageWithMessage: LinearLayoutCompat = messageWithImageDialog.findViewById(R.id.llMessageLayout)
        val ivCrop: AppCompatImageView = messageWithImageDialog.findViewById(R.id.iv_crop)
        imgUndo = messageWithImageDialog.findViewById(R.id.ivUndo)
        imgText = messageWithImageDialog.findViewById(R.id.ivText)
        imgEdit = messageWithImageDialog.findViewById(R.id.ivEdit)
        imgEmoji = messageWithImageDialog.findViewById(R.id.ivEmoji)
        mPhotoEditorView = messageWithImageDialog.findViewById(R.id.photoEditorView)
        mPropertiesBSFragment = PropertiesBSFragment()
        mEmojiBSFragment = EmojiBSFragment()
        mEmojiBSFragment?.setEmojiListener(this)
        mPropertiesBSFragment?.setPropertiesChangeListener(this)
        mPhotoEditor = PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true)
                .build()
        mPhotoEditor?.setOnPhotoEditorListener(this)
        when (chatType) {
            3, 4, 5, 6 -> {
                setUpMentionsForImage(etMsg)
                setUpMentionsListForImage(dialogRecyclerView)
                imageRecycler = dialogRecyclerView
                imageLlMentions = messageWithImageDialog.findViewById(R.id.ll_mentions_layout)
                imageRecycler!!.visibility = View.VISIBLE
                imageLlMentions!!.visibility = View.VISIBLE
                etMsg.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                    }

                    override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                    ) {
                    }

                    override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                    ) {
                        imageText = s.toString()
                    }
                })
            }
        }
        ivSendImage?.alpha = 1f
        lp.dimAmount = 1.0f // Dim level. 0.0 - no dim, 1.0 - completely opaque
        llImageWithMessage.alpha = 0.7f
        messageWithImageDialog.window!!.attributes = lp
        messageWithImageDialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        messageWithImageDialog.setCancelable(true)
        messageWithImageDialog.setCanceledOnTouchOutside(false)
        ivImageMsg = messageWithImageDialog.findViewById(R.id.llRoot)
        try {
            val exif = ExifInterface(File(GeneralFunctions().getRealPathFromURI(uri, this@ChatActivity)).absolutePath)
            val rotation: Int = exif.getAttribute(ExifInterface.TAG_ORIENTATION)!!.toInt()
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
            val bmp = BitmapFactory.decodeStream(FileInputStream(File(GeneralFunctions().getRealPathFromURI(uri, this@ChatActivity))), null, null)!!
            val correctBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, mat, true)
            mPhotoEditorView?.source?.setImageBitmap(correctBmp)
        } catch (e: java.lang.Exception) {
            mPhotoEditorView?.source?.setImageURI(uri)
        }
        messageWithImageDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        messageWithImageDialog.show()
        ivSendImage?.setOnClickListener {
            if (isApiGetMessagesCompleted) {
                try {
                    saveImage(extension, dimens, fileDetails!!, messageWithImageDialog, etMsg)
                } catch (e: Exception) {
                    messageWithImageDialog.dismiss()
                    Toast.makeText(this@ChatActivity, "Error while sending image!", Toast.LENGTH_SHORT).show()
                }
            } else {
                ivSendImage?.visibility = View.GONE
                imageProgress?.visibility = View.VISIBLE
                Toast.makeText(this@ChatActivity, "Slow Internet Connection! Please wait...", Toast.LENGTH_SHORT).show()
            }
        }

        ivCrop.setOnClickListener {
            CropImage.activity(uri)
                    .start(this@ChatActivity)
        }
        imgUndo?.setOnClickListener {
            mPhotoEditor?.undo()
        }
        imgEmoji?.setOnClickListener {
            mEmojiBSFragment?.show(supportFragmentManager, mEmojiBSFragment?.tag)
        }
        imgEdit?.setOnClickListener {
            mPhotoEditor?.setBrushDrawingMode(true)
            mPropertiesBSFragment?.show(supportFragmentManager, mPropertiesBSFragment?.tag)
        }
        imgText?.setOnClickListener {
            val textEditorDialogFragment = TextEditorDialogFragment.show(this)
            textEditorDialogFragment.setOnTextEditorListener { inputText, colorCode ->
                mPhotoEditor?.addText(
                        inputText,
                        colorCode
                )
            }
        }
    }

    /**
     * set emoji keyboard callbacks
     */
    private fun setUpEmojiPopup() {
        emojiPopup = EmojiPopup.Builder.fromRootView(llRoot)
                .setOnEmojiBackspaceClickListener { }
                .setOnEmojiClickListener { _, _ -> }
                .setOnEmojiPopupShownListener { }
                .setOnSoftKeyboardOpenListener { }
                .setOnEmojiPopupDismissListener {
                    isDialogOpened = false
                    ivEmoji.setImageResource(R.drawable.ic_happiness)
                }
                .setOnSoftKeyboardCloseListener { }
                .build(etMessage)
    }

    private fun setUpSocketListeners(callingMethod: String) {
//        NotificationSockets.init(applicationContext, false) // Changed activity context to application context to avoid memory leak
        initSocketConnection(
                com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().userInfo.accessToken,
                workspaceInfoList[currentPosition].enUserId,
                com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.userId,
                com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.userChannel,
                callingMethod,
                false,
                com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.pushToken
        )
        setSocketListeners(object : SocketConnection.SocketClientCallback {

            override fun onCalling(messageJson: String) {}

            override fun onPresent(messageJson: String) {
                val messageJson = JSONObject(messageJson)
                if (chatType == ChatType.O2O && otherUserid.compareTo(messageJson.getLong("user_id")) == 0) {
                    Log.d("onPresentType", messageJson.getString("type").toString())
                    if (messageJson.getString("type") == "available") {
                        runOnUiThread {
                            llStatus?.visibility = View.VISIBLE
                            tvInfo?.visibility = View.VISIBLE
                            tvInfo?.text = "online"
                            ivGreenDot?.visibility = View.VISIBLE
                        }
                    } else if (messageJson.getString("type") == "unavailable") {
                        val formatter = SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH)
                        val dateString: String = formatter.format(Date(messageJson.getString("timestamp").toLong()))
                        val timeFormatter = SimpleDateFormat("h:mm aa", Locale.ENGLISH)
                        val timeString: String = timeFormatter.format(Date(messageJson.getString("timestamp").toLong()))
//                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
//                    val tempDate = sdf.parse(messageJson.getString("timestamp").toString())
                        Log.d("currentTime", System.currentTimeMillis().toString())
                        Log.d("timestamp", messageJson.getLong("timestamp").toString())
                        runOnUiThread {
                            when {
                                android.text.format.DateUtils.isToday(messageJson.getLong("timestamp")) -> {
                                    llStatus?.visibility = View.VISIBLE
                                    tvInfo?.visibility = View.VISIBLE
                                    tvInfo?.text =
                                            "Last seen today at " + timeString.toLowerCase()
                                    ivGreenDot?.visibility = View.GONE
                                }
                                android.text.format.DateUtils.isToday(messageJson.getLong("timestamp") + android.text.format.DateUtils.DAY_IN_MILLIS) -> {
                                    llStatus?.visibility = View.VISIBLE
                                    tvInfo?.visibility = View.VISIBLE
                                    tvInfo?.text =
                                            "Last seen yesterday at " + timeString.toLowerCase()
                                    ivGreenDot?.visibility = View.GONE
                                }
                                else -> {
                                    llStatus?.visibility = View.VISIBLE
                                    tvInfo?.visibility = View.VISIBLE
                                    tvInfo?.text = "Last seen " + dateString
                                    ivGreenDot?.visibility = View.GONE
                                }
                            }

                        }

                    }
                }
            }

            override fun onErrorReceived(messageJson: String) {
            }

            override fun onVideoCallReceived(messageJson: String) {
            }

            override fun onAudioCallReceived(messageJson: String) {
            }

            override fun onChannelSubscribed() {
                try {
                    if (unsentMessageMap.size > 0) {
                        sendFirstUnsentMessageOfList()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onThreadMessageSent(messageJson: String) {
            }

            override fun onReactionReceived(messageJson: String) {
                try {
                    val messageJson = JSONObject(messageJson)
                    if (GeneralFunctions().isMyChannelId(channelId, messageJson)) {
                        if (messagesMap[messageJson.getString(MESSAGE_UNIQUE_ID)] != null) {
                            if (messageJson.has(FuguAppConstant.MESSAGE_UNIQUE_ID) && messageJson.get(
                                            FuguAppConstant.USER_ID
                                    ) != userId.toString()
                            ) {
                                clickedEmojiMuid = messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID)
                                EmojiReceived().emojiReceived(clickedEmojiMuid,
                                        diffUtilMessageList,
                                        messageJson.getString(FuguAppConstant.USER_REACTION_EMOJI),
                                        false,
                                        messageJson.get(FuguAppConstant.USER_ID).toString(),
                                        messageJson.getString(FuguAppConstant.FULL_NAME),
                                        object : EmojiReceived.UpdateAndPublishEmoji {
                                            override fun updateAndPublishEmoji(
                                                    emoji: String,
                                                    selectedPos: Int,
                                                    messageList: ArrayList<Message>,
                                                    isToBePublished: Boolean,
                                                    sentEmpty: Boolean
                                            ) {
                                                runOnUiThread {
                                                    messageAdapter?.updateMessageList(messageList)
                                                    messageAdapter?.notifyItemChanged(selectedPos)
                                                }
                                                messagesMap[messageList[selectedPos].muid] =
                                                        messageList[selectedPos]
                                                if (isToBePublished) {
                                                    if (sentEmpty) {
                                                        publishEmoji("")
                                                    } else {
                                                        publishEmoji(emoji)
                                                    }
                                                }
                                            }
                                        })
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onPollVoteReceived(messageJson: String) {
                try {
                    val messageJson = JSONObject(messageJson)
                    if (GeneralFunctions().isMyChannelId(
                                    channelId,
                                    messageJson
                            ) && messageJson.getLong(USER_ID).compareTo(userId) != 0
                    ) {
                        val message =
                                messagesMap[messageJson.getString(MESSAGE_UNIQUE_ID)] as Message
                        runOnUiThread {
                            try {
                                messagesMap[messageJson.getString(MESSAGE_UNIQUE_ID)] =
                                        ParsePollVote().parsePollVote(messageJson, message)
                                diffUtilMessageList.set(
                                        messagesMap[messageJson.getString(
                                                MESSAGE_UNIQUE_ID
                                        )]!!.messageIndex, message
                                )
                                messageList = ArrayList()
                                //messageList.addAll(diffUtilMessageList)
                                for (message in diffUtilMessageList) {
                                    val messageClone = message.clone()
                                    messageList.add(messageClone as Message)
                                }
                                messageAdapter?.updateMessageList(messageList)
                                //messageAdapter?.notifyItemChanged(message.messageIndex)
                            } catch (e: Exception) {
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onReadAll(messageJson: String) {
                try {
                    if (GeneralFunctions().isMyChannelId(channelId, JSONObject(messageJson))) {
                        readAll(JSONObject(messageJson))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onPinChat(messageJson: String) {}

            override fun onUnpinChat(messageJson: String) {}

            override fun onTypingStarted(messageJson: String) {
                try {
                    val messageJson = JSONObject(messageJson)
                    if (GeneralFunctions().isMyChannelId(channelId, messageJson)) {
                        if (messageJson.getLong(USER_ID).compareTo(userId) != 0) {
                            runOnUiThread {
                                readAll(messageJson)
                                startAnim(messageJson.getString(FULL_NAME))
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onTypingStopped(messageJson: String) {
                try {
                    val messageJson = JSONObject(messageJson)
                    if (GeneralFunctions().isMyChannelId(channelId, messageJson)) {
                        if (messageJson.getLong(USER_ID).compareTo(userId) != 0) {
                            runOnUiThread {
                                stopAnim()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onMessageSent(messageJson: String) {
                try {
                    val messageJson = JSONObject(messageJson)
                    if (GeneralFunctions().isMyChannelId(channelId, messageJson)) {
                        val muid = messageJson.getString(MESSAGE_UNIQUE_ID)
                        if (messagesMap[muid] != null) {
                            val message = messagesMap[muid]!!
                            message.messageStatus = MESSAGE_SENT
                            message.uploadStatus = UploadStatus.UPLOAD_COMPLETED.uploadStatus
                            message.downloadStatus =
                                    DownloadStatus.DOWNLOAD_COMPLETED.downloadStatus
                            messagesMap[muid] = message
                            //messageList[message.messageIndex] = message
                            diffUtilMessageList[message.messageIndex] = message
                            unsentMessageMap.remove(messageJson.getString(MESSAGE_UNIQUE_ID))
                            Thread {
                                kotlin.run {
                                    ChatDatabase.setMessageList(diffUtilMessageList, channelId)
                                    ChatDatabase.setMessageMap(messagesMap, channelId)
                                    ChatDatabase.setUnsentMessageMapByChannel(
                                            channelId,
                                            unsentMessageMap
                                    )
                                }
                            }.start()

                            runOnUiThread {
                                uuidsList.add(muid)
                                messageList = ArrayList()
                                //messageList.addAll(diffUtilMessageList)
                                for (message in diffUtilMessageList) {
                                    val messageClone = message.clone()
                                    messageList.add(messageClone as Message)
                                }
                                messageAdapter?.updateMessageList(messageList)
                                //messageAdapter?.updateMessageList(diffUtilMessageList)
                                //messageAdapter?.notifyItemChanged(message.messageIndex)
                            }
                        }
                        if (unsentMessageMap.size > 0) {
                            sendFirstUnsentMessageOfList()
                        }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            override fun onMessageReceived(messageJson: String) {
                try {
                    val readAllJson = JSONObject()
                    readAllJson.put(CHANNEL_ID, channelId)
                    readAllJson.put(NOTIFICATION_TYPE, 6)
                    readAllJson.put(USER_ID, userId)
                    if (GeneralFunctions().foregrounded()) {
                        SocketConnection.readAll(readAllJson)
                    }
                    val messageJson = JSONObject(messageJson)
                    if (GeneralFunctions().isMyChannelId(channelId, messageJson)) {
                        val muid = messageJson.getString(MESSAGE_UNIQUE_ID)
                        if (messagesMap[muid] == null) {
                            val message = GeMessageObject().createMessageObject(messageJson, diffUtilMessageList, userId)
                            messagesMap[message.muid] = message
                            if (userId.compareTo(messageJson.getLong(USER_ID)) != 0) {
                                try {
                                    val unreadObject: Message =
                                            diffUtilMessageList[positionToBeInserted]
                                    unreadObject.count = unreadObject.count + 1
                                    diffUtilMessageList[positionToBeInserted] = unreadObject
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else {
                                if (positionToBeInserted > 0 && positionToBeInserted < diffUtilMessageList.size && diffUtilMessageList.get(positionToBeInserted).rowType == UNREAD_ITEM) {
                                    messagesMap.remove(diffUtilMessageList[positionToBeInserted].muid)
                                    diffUtilMessageList.removeAt(positionToBeInserted)
                                    for (i in diffUtilMessageList.indices) {
                                        diffUtilMessageList[i].messageIndex = i
                                        val message = messagesMap[diffUtilMessageList[i].muid]
                                        message?.messageIndex = i
                                        messagesMap[diffUtilMessageList[i].muid] = message!!
                                    }

                                    positionToBeInserted = 0
                                }
                            }
                            diffUtilMessageList.add(message)
                            runOnUiThread {
                                messageList = ArrayList()
                                //messageList.addAll(diffUtilMessageList)
                                for (message in diffUtilMessageList) {
                                    val messageClone = message.clone()
                                    messageList.add(messageClone as Message)
                                }
                                messageAdapter?.updateMessageList(messageList)
                                //messageAdapter?.notifyItemInserted(messageList.size - 1)
                                if (rlScrollBottom.visibility == android.view.View.GONE) {
                                    rvMessages.scrollToPosition(diffUtilMessageList.size - 1)
                                } else {
                                    scrollBottomCount += 1
                                    stickyLabel.setScrollCount(scrollBottomCount)
                                    tvUnread.text = scrollBottomCount.toString()
                                    tvUnread.visibility = android.view.View.VISIBLE
                                }
                            }
                        } else {
                            val message = messagesMap[muid]!!
                            if (messageJson.has(MESSAGE_ID))
                                message.id = messageJson.getLong(MESSAGE_ID)
                            if (message.thread_message_data == null) {
                                message.thread_message_data = ArrayList()
                            }
                            messagesMap[muid] = message
                            diffUtilMessageList[message.messageIndex] = message
                            runOnUiThread {
                                messageList = ArrayList()
                                //messageList.addAll(diffUtilMessageList)
                                for (message in diffUtilMessageList) {
                                    val messageClone = message.clone()
                                    messageList.add(messageClone as Message)
                                }
                                messageAdapter?.updateMessageList(messageList)
                                // messageAdapter?.notifyItemChanged(message.messageIndex)
                            }
                        }
                        //read all added
                        try {
                            readAll(messageJson)
                        } catch (e: java.lang.Exception) {

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onThreadMessageReceived(messageJson: String) {
                val messageJson = JSONObject(messageJson)
                if (GeneralFunctions().isMyChannelId(channelId, messageJson)) {
                    try {
                        if (messagesMap[messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID)] != null) {
                            val pos =
                                    messagesMap[messageJson.getString(FuguAppConstant.MESSAGE_UNIQUE_ID)]?.messageIndex
                            if (diffUtilMessageList[pos!!].threadMessage) {
                                diffUtilMessageList[pos].threadMessageCount =
                                        diffUtilMessageList[pos].threadMessageCount + 1
                            } else {
                                diffUtilMessageList[pos].threadMessage = true
                                diffUtilMessageList[pos].threadMessageCount = 1
                            }
//                            try {
//                                threadMessagesMap[messageJson.getString(com.skeleton.mvp.constant.FuguAppConstant.MESSAGE_UNIQUE_ID)] = messageList[pos].replyCount
//                                ChatDatabase.setThreadMap(threadMessagesMap, channelId)
//                            } catch (e: Exception) {
//
//                            }
                            if (diffUtilMessageList[pos].thread_message_data == null) {
                                diffUtilMessageList[pos].thread_message_data = ArrayList()
                            } else {
                                val threadMessageDataList: ArrayList<ThreadMessageData> = diffUtilMessageList[pos].thread_message_data
                                if (threadMessageDataList.size <= 3) {
                                    for (threadData in threadMessageDataList) {
                                        if (threadData.userId.compareTo(messageJson.getLong(USER_ID)) == 0) {
                                            threadMessageDataList.remove(threadData)
                                            break
                                        }
                                    }
                                    if (messageJson.has("user_image_50x50")) {
                                        threadMessageDataList.add(ThreadMessageData(messageJson.getString(FULL_NAME), messageJson.getString("user_image_50x50"), messageJson.getLong(USER_ID)))
                                    } else {
                                        threadMessageDataList.add(ThreadMessageData(messageJson.getString(FULL_NAME), "", messageJson.getLong(USER_ID)))
                                    }
                                }

                                diffUtilMessageList[pos].thread_message_data = threadMessageDataList
                            }
                            runOnUiThread {
                                messageList = ArrayList()
                                //messageList.addAll(diffUtilMessageList)
                                for (message in diffUtilMessageList) {
                                    val messageClone = message.clone()
                                    messageList.add(messageClone as Message)
                                }
                                messageAdapter?.updateMessageList(messageList)
                                //messageAdapter?.notifyItemChanged(pos)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onConnect() {
                SocketConnection.subscribe(otherUserid)
                try {
                    if (unsentMessageMap.size > 0) {
                        sendFirstUnsentMessageOfList()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onDisconnect() {}

            override fun onConnectError(socket: Socket, message: String) {
            }

            override fun onTaskAssigned(messageJson: String) {

            }

            override fun onMeetScheduled(messageJson: String) {

            }

            override fun onUpdateNotificationCount(messageJson: String) {

            }

        })
        SocketConnection.subscribeChannel(channelId)
    }

    private fun readAll(messageJson: JSONObject) {
        try {
            if (userId.compareTo(messageJson.optLong(FuguAppConstant.USER_ID)) != 0) {
                for (position in diffUtilMessageList.indices.reversed()) {
                    try {
                        if (diffUtilMessageList[position].rowType == TEXT_MESSGAE_SELF
                                || diffUtilMessageList[position].rowType == IMAGE_MESSGAE_SELF
                                || diffUtilMessageList[position].rowType == VIDEO_MESSGAE_SELF
                                || diffUtilMessageList[position].rowType == VIDEO_CALL_SELF
                                || diffUtilMessageList[position].rowType == POLL_SELF
                                || diffUtilMessageList[position].rowType == MESSAGE_DELETED_SELF
                                || diffUtilMessageList[position].rowType == FILE_MESSGAE_SELF
                        ) {
                            if (diffUtilMessageList[position].messageStatus == FuguAppConstant.MESSAGE_SENT) {
                                diffUtilMessageList[position].messageStatus =
                                        FuguAppConstant.MESSAGE_READ
                            } else {
                                val status = diffUtilMessageList[position].messageStatus!!
                                diffUtilMessageList[position].messageStatus = status
                            }
                        }
                        messagesMap[diffUtilMessageList[position].muid] =
                                diffUtilMessageList[position]
                    } catch (e: Exception) {

                    }
                }
                runOnUiThread {
                    messageList = ArrayList<Message>()
                    //messageList.addAll(diffUtilMessageList)
                    try {
                        if (diffUtilMessageList.size > 0) {
                            for (message in diffUtilMessageList) {
                                if (diffUtilMessageList.size > 0) {
                                    val messageClone = message.clone()
                                    messageList.add(messageClone as Message)
                                } else {
                                    break
                                }
                            }
                            if (diffUtilMessageList.size > 0) {
                                if ((messageJson.getLong(CHANNEL_ID).compareTo(channelId) == 0)) {
                                    messageAdapter?.updateMessageList(messageList)
                                }
                            }
                        }
                    } catch (e: Exception) {
                    }
                    //messageAdapter?.notifyDataSetChanged()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun apiGetMessages(isToBeScrolled: Boolean) {
        positionToBeInserted = 0
        val start: Int
        val commonParams = CommonParams.Builder()
        commonParams.add(CHANNEL_ID, channelId)
        commonParams.add(EN_USER_ID, enUserId)
        commonParams.add(DEVICE_DETAILS, CommonData.deviceDetails(this@ChatActivity))
        commonParams.add(DEVICE_TOKEN, com.skeleton.mvp.data.db.CommonData.getFcmToken())
        commonParams.add(DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(this@ChatActivity))
        commonParams.add("store_promise", "true")
        if (!isSearchedMessage) {
            commonParams.add(PAGE_START, pageSize)
        } else {
            start = if ((messageSearchIndex - 19) < 1) {
                1
            } else {
                messageSearchIndex - 19
            }
            commonParams.add(PAGE_START, start)
            commonParams.add(PAGE_END, messageSearchIndex + 19)
        }

        RestClient.getApiInterface()
                .getMessages(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), appSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                .enqueue(object : ResponseResolver<FuguGetMessageResponse>() {
                    override fun success(fuguGetMessageResponse: FuguGetMessageResponse) {

                        try {
                            messageAdapter?.updateUserType(fuguGetMessageResponse.data.userType)
                        } catch (e: java.lang.Exception) {

                        }

                        onlyAdminsCanMessage = fuguGetMessageResponse.data.isOnlyAdminsCanMessage
                        try {
                            messageAdapter?.updateOnlyAdminCanReply(onlyAdminsCanMessage)
                        } catch (e: Exception) {

                        }
                        frameChat.visibility = View.VISIBLE
                        btnLl.visibility = View.GONE
                        llRetryMsg.visibility = View.GONE
                        getMessagePageSize = fuguGetMessageResponse.data.pageSize
                        otherUserid = fuguGetMessageResponse.data.userId
                        SocketConnection.subscribe(otherUserid)
                        if (chatType == ChatType.BOT && fuguGetMessageResponse.data.fuguBotTags.size > 0) {
                            actionList = ArrayList()
                            for (fuguBotTag in fuguGetMessageResponse.data.fuguBotTags) {
                                actionList.add(
                                        FuguBotAdapter.BotAction(
                                                "/" + fuguBotTag.tag,
                                                fuguBotTag.tag,
                                                fuguBotTag.inputParameter,
                                                fuguBotTag.description
                                        )
                                )
                            }
                            rvBotActions?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@ChatActivity)
                            botAdapter = FuguBotAdapter(actionList, this@ChatActivity)
                            rvBotActions?.adapter = botAdapter
                        }
                        if (workspaceInfoList[currentPosition].config.videoCallEnabled == "1") {
                            if (chatType == ChatType.O2O) {
                                ivVideoCall.visibility = View.VISIBLE
                            }
                        } else {
                            ivVideoCall.visibility = View.INVISIBLE
                        }
                        if (workspaceInfoList[currentPosition].config.audioCallEnabled == "1") {
                            if (chatType == ChatType.O2O) {
                                ivAudioCall.visibility = View.VISIBLE
                            } else {
                                ivAudioCall.visibility = View.GONE
                            }
                        } else {
                            ivAudioCall.visibility = View.GONE
                        }
                        runOnUiThread {
                            if (chatType == ChatType.PRIVATE_GROUP || chatType == ChatType.PUBLIC_GROUP) {
//                                ivHangoutsCall.visibility = View.VISIBLE
                                val workspaceInfo = workspaceInfoList[currentPosition]
                                var roles = workspaceInfo.config.createMeetPermission
                                if (workspaceInfo.isConferencingEnabled == 1 && !TextUtils.isEmpty(roles)) {
                                    roles = roles!!.replace("[", "")
                                    roles = roles.replace("]", "")
                                    roles = roles.replace("\"".toRegex(), "")
                                    if (roles.isNotEmpty()) {
                                        val rolesArray = roles.split(",").toTypedArray()
                                        val rolesList = java.util.ArrayList(listOf(*rolesArray))
                                        val presentRole: String = workspaceInfo.role
                                        if (rolesList.contains(presentRole)) {
                                            ivVideoCall.visibility = View.VISIBLE
                                        }
                                    }
                                }
                            }
                        }
                        userImage = if (!TextUtils.isEmpty(fuguGetMessageResponse.data.userImage)) {
                            fuguGetMessageResponse.data.userImage
                        } else {
                            ""
                        }
                        leaveType = fuguGetMessageResponse.data.leaveType
                        isCustomLabel =
                                if (!TextUtils.isEmpty(fuguGetMessageResponse.data.customLabel)) {
                                    GeneralFunctions().setToolBarText(
                                            fuguGetMessageResponse.data.customLabel,
                                            tvTitle,
                                            chatType,
                                            fuguGetMessageResponse.data.leaveType
                                    )
                                    true
                                } else {
                                    GeneralFunctions().setToolBarText(
                                            fuguGetMessageResponse.data.label,
                                            tvTitle,
                                            chatType,
                                            fuguGetMessageResponse.data.leaveType
                                    )
                                    false
                                }

                        if (!isChatCountIncremented && fuguGetMessageResponse.data.userId != null) {
                            isChatCountIncremented = true
                            val getAllMembersHashMap: HashMap<Long, GetAllMembers>
                            if (CommonData.getPaperAllMembersChatMap(com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey) != null) {
                                getAllMembersHashMap =
                                        CommonData.getPaperAllMembersChatMap(
                                                com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey
                                        )
                                if (getAllMembersHashMap[fuguGetMessageResponse.data.userId] == null) {
                                    val getAllMembers = GetAllMembers(
                                            fuguGetMessageResponse.data.userId,
                                            "", "", "", "", "", 1, "", ""
                                    )
                                    getAllMembersHashMap[fuguGetMessageResponse.data.userId] = getAllMembers
                                } else {
                                    val getAllMembers =
                                            getAllMembersHashMap[fuguGetMessageResponse.data.userId]
                                    getAllMembers?.searchCount = (getAllMembers?.searchCount?.plus(1)!!)
                                    getAllMembersHashMap[fuguGetMessageResponse.data.userId] = getAllMembers
                                }
                                CommonData.setPaperAllMembersChatMap(
                                        getAllMembersHashMap,
                                        com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey
                                )

                            } else {
                                getAllMembersHashMap = HashMap()
                                if (getAllMembersHashMap[fuguGetMessageResponse.data.userId] == null) {
                                    val getAllMembers = GetAllMembers(
                                            fuguGetMessageResponse.data.userId,
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            1,
                                            "",
                                            ""
                                    )
                                    getAllMembersHashMap[fuguGetMessageResponse.data.userId] =
                                            getAllMembers
                                } else {
                                    val getAllMembers =
                                            getAllMembersHashMap[fuguGetMessageResponse.data.userId]
                                    getAllMembers?.searchCount = (getAllMembers?.searchCount?.plus(1)!!)
                                    getAllMembersHashMap[fuguGetMessageResponse.data.userId] =
                                            getAllMembers
                                }
                                CommonData.setPaperAllMembersChatMap(
                                        getAllMembersHashMap,
                                        com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey
                                )

                            }
                        }
                        avi.visibility = View.GONE
//                        Thread {
//                            kotlin.run {
                        var tempSentAtUtc = ""
                        var dateCount = 0
                        if (pageSize == 1) {
                            diffUtilMessageList = ArrayList()
                            messageList = ArrayList()
                            messagesMap = LinkedHashMap()
                        }
                        val tempMessageList = ArrayList<Message>()
                        if (fuguGetMessageResponse.data.messages.size > 0) {
                            firstMessageId = fuguGetMessageResponse.data.messages[0].id
                            lastMessageId =
                                    fuguGetMessageResponse.data.messages[fuguGetMessageResponse.data.messages.size - 1].id
                        }
                        for (messageIndex in 0 until fuguGetMessageResponse.data.messages.size) {
                            val messageObject: Message =
                                    GeMessageObject().getServerMessageObject(fuguGetMessageResponse.data?.messages?.get(messageIndex),
                                            messageIndex,
                                            userId,
                                            object : GeMessageObject.UpdateChatData {
                                                override fun deleteMessageFromLocal(id: String) {
                                                    messageAdapter?.deleteImageFromImageList(id)
                                                }
                                            })
                            val date =
                                    DateUtils.getDate(dateUtils.convertToLocal(messageObject.sentAtUtc))
                            if (!tempSentAtUtc.equals(date, ignoreCase = true)) {
                                val dateMessageObject = Message()
                                val globalMuid = UUID.randomUUID().toString()
                                dateMessageObject.muid = globalMuid
                                dateMessageObject.sentAtUtc = date
                                dateMessageObject.threadMessage = true
                                dateMessageObject.rowType = HEADER_ITEM
                                dateMessageObject.localSentAtUtc = messageObject.sentAtUtc
                                tempMessageList.add(dateMessageObject)
                                messagesMap[globalMuid] = dateMessageObject
                                tempSentAtUtc = date
                                dateCount += 1
                            }
                            tempMessageList.add(messageObject)
                            messagesMap[fuguGetMessageResponse.data?.messages!![messageIndex].muid] = messageObject
                        }
                        if (!isSearchedMessage) {
                            val keys = ArrayList(unsentMessageMap.keys)
                            for (i in keys.indices) {
                                val messageObj = unsentMessageMap[keys[i]]!!
                                messageObj.uuid = keys[i]
                                messageObj.messageStatus = MESSAGE_UNSENT
                                messageObj.downloadStatus = 0
                                messageObj.uploadStatus = 0
                                var add = true
                                if (messageObj.messageType == FILE_MESSAGE) {
                                    messageObj.fileName = messageObj.fileName
                                    messageObj.fileSize = messageObj.fileSize
                                    messageObj.filePath =
                                            CommonData.getFileLocalPath(messageObj.uuid)
                                    if (!TextUtils.isEmpty(messageObj.filePath)) {
                                        link = messageObj.filePath.split("\\.".toRegex())
                                                .dropLastWhile { it.isEmpty() }.toTypedArray()
                                        messageObj.fileExtension = link[link.size - 1]
                                    } else if (!TextUtils.isEmpty(messageObj.url)) {
                                        link = messageObj.url.split("\\.".toRegex())
                                                .dropLastWhile { it.isEmpty() }.toTypedArray()
                                        messageObj.fileExtension = link[link.size - 1]
                                    } else {
                                        add = false
                                    }
                                }
                                try {
                                    if (TextUtils.isEmpty(messageObj.threadMuid) && add) {
                                        if (messagesMap[messageObj.uuid] == null && messageObj.userId.compareTo(userId) == 0) {
                                            tempMessageList.add(messageObj)
                                            messagesMap[messageObj.uuid] = messageObj
                                        } else {
                                            unsentMessageMap.remove(messageObj.uuid)
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                        tempMessageList.addAll(diffUtilMessageList)
                        diffUtilMessageList = ArrayList()
                        diffUtilMessageList.addAll(tempMessageList)
                        //tempMessageList.addAll(messageList)
                        //messageList = ArrayList()
                        //messageList.addAll(tempMessageList)
                        if (!isUnreadItemAdded) {
                            isUnreadItemAdded = true
                            if (conversation.unreadCount > 0) {
                                val unreadMessage = Message()
                                unreadMessage.count = conversation.unreadCount
                                unreadMessage.rowType = 8
                                unreadMessage.muid = UUID.randomUUID().toString()
                                var countEventItems = 0
                                for (i in diffUtilMessageList.size - 1 downTo 0) {
                                    if (((diffUtilMessageList[i].rowType == TEXT_MESSGAE_OTHER
                                                    || diffUtilMessageList[i].rowType == IMAGE_MESSGAE_OTHER
                                                    || diffUtilMessageList[i].rowType == FILE_MESSGAE_OTHER
                                                    || diffUtilMessageList[i].rowType == VIDEO_MESSGAE_OTHER
                                                    || diffUtilMessageList[i].rowType == VIDEO_CALL_OTHER
                                                    || diffUtilMessageList[i].rowType == POLL_OTHER
                                                    || diffUtilMessageList[i].rowType == MESSAGE_DELETED_OTHER))
                                            && countEventItems < conversation.unreadCount
                                    ) {
                                        countEventItems++
                                        positionToBeInserted = i
                                    } else if ((diffUtilMessageList[i].rowType == TEXT_MESSGAE_SELF
                                                    || diffUtilMessageList[i].rowType == IMAGE_MESSGAE_SELF
                                                    || diffUtilMessageList[i].rowType == FILE_MESSGAE_SELF
                                                    || diffUtilMessageList[i].rowType == VIDEO_MESSGAE_SELF
                                                    || diffUtilMessageList[i].rowType == VIDEO_CALL_SELF
                                                    || diffUtilMessageList[i].rowType == POLL_SELF
                                                    || diffUtilMessageList[i].rowType == MESSAGE_DELETED_SELF)
                                            && countEventItems < conversation.unreadCount
                                    ) {
                                        positionToBeInserted = 0
                                        break
                                    }
                                }
                                if (positionToBeInserted > 0) {
                                    diffUtilMessageList.add(positionToBeInserted, unreadMessage)
                                }
                            }
                        }
                        try {
                            for (messageIndex in 0 until diffUtilMessageList.size) {
                                diffUtilMessageList[messageIndex].messageIndex = messageIndex
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                        runOnUiThread {
                            messageList = ArrayList<Message>()
                            //messageList.addAll(diffUtilMessageList)
                            for (message in diffUtilMessageList) {
                                val messageClone = message.clone()
                                messageList.add(messageClone as Message)
                            }

                            //Commented for star issue
//                                    messageAdapter?.updateMessageList(messageList)

//                                    if (pageSize == 1) {
//
//                                        messageAdapter?.notifyDataSetChanged()
//                                    } else {
//                                        messageAdapter?.notifyItemRangeInserted(0, fuguGetMessageResponse.data.messages.size + dateCount)
//                                    }


//                            for (message in messagesMap.values) {
//                            }
                            apiGetStarredMessages(isToBeScrolled, fuguGetMessageResponse.data.pageSize)
                        }
                        Thread {
                            kotlin.run {
                                if (!isSearchedMessage) {
                                    ChatDatabase.setMessageList(messageList, channelId)
                                    ChatDatabase.setMessageMap(messagesMap, channelId)
                                }
                            }
                        }.start()
                        Thread {
                            kotlin.run {
                                if (!isSearchedMessage) {
                                    ChatDatabase.setMessageList(diffUtilMessageList, channelId)
                                    ChatDatabase.setMessageMap(messagesMap, channelId)
                                }
                            }
                        }.start()

                        if (!fileForwarded) {
                            forwardAttachment(true)
                        }
                        runOnUiThread {
                            if (intent.hasExtra("SendCustomMessage")
                                    && !TextUtils.isEmpty(intent.getStringExtra("SendCustomMessage"))
                                    && !TextUtils.isEmpty(CommonData.getCustomText())
                            ) {
                                CommonData.setCustomText("")
                                etMessage.setText(intent.getStringExtra("SendCustomMessage"))
                                ivSend.performClick()
                            }
                        }
                        runOnUiThread {
                            isApiGetMessagesCompleted = true
                            try {
                                when {
                                    imageProgress?.visibility == View.VISIBLE -> {
                                        imageProgress?.visibility = View.GONE
                                        ivSendImage?.performClick()
                                    }
                                    videoProgress?.visibility == View.VISIBLE -> {
                                        videoProgress?.visibility = View.GONE
                                        ivSendVideo?.performClick()
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
//                                }


                        }
//                        }.start()
                        userRole = fuguGetMessageResponse.data.userChannelRole
                        if (fuguGetMessageResponse.data.status == 0) {
                            llCannotReply.visibility = View.VISIBLE
                            ivVideoCall.visibility = View.GONE
                            ivAudioCall.visibility = View.GONE
                            llCannotReply.setOnClickListener { }
                        } else {

                            if (!fuguGetMessageResponse.data.isOnlyAdminsCanMessage) {
                                llCannotReply.visibility = View.GONE
                            } else if (fuguGetMessageResponse.data.isOnlyAdminsCanMessage && userRole.equals("ADMIN")) {
                                llCannotReply.visibility = View.GONE
                            } else {
                                llCannotReply.visibility = View.VISIBLE
                            }
                        }
                    }

                    override fun failure(error: APIError?) {
                        runOnUiThread {
                            isApiGetMessagesCompleted = true
                            try {
                                when {
                                    imageProgress?.visibility == View.VISIBLE -> {
                                        imageProgress?.visibility = View.GONE
                                        ivSendImage?.performClick()
                                    }
                                    videoProgress?.visibility == View.VISIBLE -> {
                                        videoProgress?.visibility = View.GONE
                                        ivSendVideo?.performClick()
                                    }
                                    else -> {

                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        if (!fileForwarded) {
                            forwardAttachment(false)
                        }
                        if (diffUtilMessageList.isEmpty()) {
                            btnLl.visibility = View.VISIBLE
                            frameChat.visibility = View.GONE
                        } else if (diffUtilMessageList.isNotEmpty()) {
                            llRetryMsg.visibility = View.VISIBLE
                            llRetryMsg.setBackgroundColor(Color.parseColor("#FBE799"))
                            GeneralFunctions().spannableRetryText(
                                    tvRetryMsg, getString(R.string.error_msg_yellow_bar),
                                    getString(R.string.retry)
                            )
                            llRetryMsg.setOnClickListener {
                                apiGetMessages(false)
                                retryProgressBar.visibility = View.VISIBLE
                                Handler().postDelayed({
                                    retryProgressBar.visibility = View.INVISIBLE
                                }, 1000)
                            }
                        }
                    }
                })
    }


    private fun apiGetStarredMessages(isToBeScrolled: Boolean, newPageSize: Int) {
        val commonParams = com.skeleton.mvp.data.network.CommonParams.Builder()
                .add(EN_USER_ID, enUserId)
                .add(CHANNEL_ID, channelId)
                .add("start_message_id", firstMessageId)
                .add("end_message_id", lastMessageId)
        com.skeleton.mvp.data.network.RestClient.getApiInterface(false).getStarredMessages(
                com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                workspaceInfoList[currentPosition].fuguSecretKey,
                1,
                BuildConfig.VERSION_CODE,
                commonParams.build().map
        ).enqueue(object : ResponseResolver<StarredMessagelResponse>() {
            override fun success(t: StarredMessagelResponse?) {
                try {
                    for (muid in t?.data?.starredMuids!!) {
                        if (messagesMap[muid] != null) {
                            val message = messagesMap[muid]
                            message?.isStarred = 1
                            messagesMap[muid] = message!!
                            diffUtilMessageList[message.messageIndex] = message
                        }
                    }
                    messageList = ArrayList<Message>()
                    for (message in diffUtilMessageList) {
                        val messageClone = message.clone()
                        messageList.add(messageClone as Message)
                    }
                    messageAdapter?.updateMessageList(messageList)
//                        messageAdapter?.updateMessageList(messageList)
//                        messageAdapter?.notifyDataSetChanged()

                    if (!isSearchedMessage) {
                        if (isToBeScrolled) {
                            rvMessages.scrollToPosition(diffUtilMessageList.size - 1)
                        } else {
                            if (pageSize == 1) {
                                if (!isAlreadyScrolled && conversation.unreadCount != 0) {
                                    rvMessages.smoothScrollToPosition(diffUtilMessageList.size - conversation.unreadCount - 1)
                                    conversation.unreadCount = 0
                                } else if (isNotificationMessage) {

                                    isNotificationMessage = false
                                    if (messagesMap[notificationMuid] != null) {
                                        val message = messagesMap[notificationMuid]
                                        rvMessages.scrollToPosition(message?.messageIndex!!)
                                    }
                                } else if (!isAlreadyScrolled) {
                                    rvMessages.scrollToPosition(messageList.size - 1)
                                }
                            } else {
//                                                rvMessages.scrollToPosition(fuguGetMessageResponse.data.pageSize + 4)
                            }
                        }
                    } else {
                        if (!isAlreadyHighlighted) {
                            Handler().postDelayed({
                                try {
                                    rvMessages.smoothScrollToPosition(messagesMap[messageSearchMuid]?.messageIndex!!)
                                    isAlreadyHighlighted = true
                                    val message: Message? = messagesMap[messageSearchMuid]
                                    message?.isAnimate = true
                                    messagesMap[messageSearchMuid] = message!!
                                    diffUtilMessageList[message.messageIndex] = message
                                    messageList = ArrayList<Message>()
                                    //messageList.addAll(diffUtilMessageList)
                                    for (message in diffUtilMessageList) {
                                        val messageClone = message.clone()
                                        messageList.add(messageClone as Message)
                                    }
                                    //commented for star
                                    messageAdapter?.updateMessageList(messageList)
                                    //messageAdapter?.notifyItemChanged(message.messageIndex)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }, 500)

                        } else {
                            val message: Message? = messagesMap[messageSearchMuid]
                            message?.isAnimate = false
                            messagesMap[messageSearchMuid] = message!!
                            diffUtilMessageList[message.messageIndex] = message
                            messageList = ArrayList<Message>()
                            //messageList.addAll(diffUtilMessageList)
                            for (message in diffUtilMessageList) {
                                val messageClone = message.clone()
                                messageList.add(messageClone as Message)
                            }
                            //commented for star
                            messageAdapter?.updateMessageList(messageList)
                            // messageAdapter?.notifyItemChanged(message.messageIndex)

                        }
                    }
                    pageSize += newPageSize
                    stickyLabel.updateMessageList(diffUtilMessageList)
                    runOnUiThread { progressBar.visibility = View.GONE }
                } catch (e: java.lang.Exception) {

                }
            }

            override fun failure(error: APIError?) {
                messageList = ArrayList<Message>()
                for (message in diffUtilMessageList) {
                    var messageClone = message.clone()
                    messageList.add(messageClone as Message)
                }
                messageAdapter?.updateMessageList(messageList)
                stickyLabel.updateMessageList(diffUtilMessageList)
                runOnUiThread { progressBar.visibility = View.GONE }
            }

        })
    }

    private fun sentSharedAttachment() {
        ShareAttachment().sentSharedAttachment(this@ChatActivity,
                channelId,
                fuguImageUtils,
                etMessage,
                maxUploadSize,
                object : ShareAttachment.ShareAttachmentCallBack {
                    override fun onFileShared() {
                        fileShared = true
                    }

                    override fun onTextShared(text: String) {
                        etMessage.setText(text)
                        CommonData.setSharedText("")
                    }
                })
    }

    private fun forwardAttachment(isSent: Boolean) {
        fileForwarded = true
        if (intent.hasExtra("MESSAGE")) {
            val message = intent.getSerializableExtra("MESSAGE") as Message
            val globalUuid = UUID.randomUUID().toString()
            when (message.messageType) {
                IMAGE_MESSAGE -> {
                    val dimens = java.util.ArrayList<Int>()
                    dimens.add(message.imageHeight)
                    dimens.add(message.imageWidth)
                    val splitArray = message.sharableImage_url.split(".")
                    var extension = splitArray[splitArray.size - 1].toLowerCase()
                    if (extension.equals("png")) {
                        extension = "jpg"
                    }
                    val type = FuguAppConstant.FILE_TYPE_MAP[extension]
                    var path = ""
                    path = Environment.getExternalStorageDirectory().toString() + File.separator + FuguAppConstant.APP_NAME_SHORT + File.separator +
                            workspaceInfoList[currentPosition].workspaceName.replace(" ".toRegex(), "")
                                    .replace("'s".toRegex(), "") + File.separator + type?.directory
                    path = path + File.separator + message.fileName + "_" + message.muid + "." + extension

                    if (!File(path).exists()) {
                        path = Environment.getExternalStorageDirectory().toString() + File.separator + FuguAppConstant.APP_NAME_SHORT + File.separator +
                                workspaceInfoList.get(currentPosition).workspaceName.replace(" ".toRegex(), "")
                                        .replace("'s".toRegex(), "") + File.separator + FuguAppConstant.PRIVATE_IMAGES
                        path = path + File.separator + message.fileName + "_" + message.muid + "." + extension
                    }
                    val fileDetails: FuguFileDetails?
                    fileDetails = if (extension.contains("gif")) {
                        null
                    } else {
                        val localDate = DateUtils.getFormattedDate(Date())
                        val date = DateUtils.getDate(localDate)
                        fuguImageUtils?.saveFile(
                                Uri.fromFile(File(path)),
                                type,
                                channelId,
                                localDate
                        )
                    }
                    sharedMuid = if (fileDetails != null) {
                        fileDetails.muid!!
                    } else {
                        globalUuid
                    }
                    addMessageLinkToList(
                            getString(R.string.fugu_empty),
                            IMAGE_MESSAGE,
                            message.sharableImage_url,
                            message.sharableThumbnailUrl,
                            message.image_url_100x100,
                            message.sharableThumbnailUrl,
                            fileDetails,
                            sharedMuid,
                            dimens,
                            isSent
                    )
                    val fileJson = messageFileJson.createFileJson(
                            ArrayList(),
                            "",
                            IMAGE_MESSAGE,
                            message.sharableImage_url,
                            message.sharableThumbnailUrl,
                            message.sharableImage_url_100x100,
                            fileDetails,
                            sharedMuid,
                            diffUtilMessageList.size - 1,
                            userId,
                            userName,
                            channelId,
                            "",
                            dimens,
                            diffUtilMessageList,
                            this@ChatActivity
                    )
                    if (fileJson != null) {
                        SocketConnection.sendMessage(fileJson)
                    }
                }
                FuguAppConstant.FILE_MESSAGE -> {
                    sharedMuid = globalUuid
                    val fuguFileDetails = FuguFileDetails()
                    fuguFileDetails.fileName = message.fileName
                    fuguFileDetails.fileExtension = message.fileExtension
                    if (TextUtils.isEmpty(fuguFileDetails.fileExtension)) {
                        val splitArray = message.fileName.split(Pattern.quote(".").toRegex())
                                .dropLastWhile { it.isEmpty() }.toTypedArray()
                        fuguFileDetails.fileExtension = splitArray[splitArray.size - 1]
                    }
                    fuguFileDetails.fileSize = message.fileSize
                    fuguFileDetails.filePath = message.filePath
                    addMessageLinkToList(
                            getString(R.string.fugu_empty),
                            FuguAppConstant.FILE_MESSAGE,
                            message.url,
                            message.thumbnailUrl,
                            message.image_url_100x100,
                            message.sharableThumbnailUrl,
                            fuguFileDetails,
                            globalUuid,
                            java.util.ArrayList(),
                            isSent
                    )
                    val fileJson = messageFileJson.createFileJson(
                            ArrayList(),
                            "",
                            FuguAppConstant.FILE_MESSAGE,
                            message.sharableThumbnailUrl,
                            message.sharableThumbnailUrl,
                            message.sharableImage_url_100x100,
                            fuguFileDetails,
                            sharedMuid,
                            diffUtilMessageList.size - 1,
                            userId,
                            userName,
                            channelId,
                            "",
                            ArrayList(),
                            diffUtilMessageList,
                            this@ChatActivity
                    )
                    if (fileJson != null) {
                        SocketConnection.sendMessage(fileJson)
                    }
                }
                FuguAppConstant.VIDEO_MESSAGE -> {
                    val fuguFileDetails = FuguFileDetails()
                    fuguFileDetails.fileName = message.fileName
                    fuguFileDetails.fileExtension = message.fileExtension
                    fuguFileDetails.fileSize = message.fileSize
                    fuguFileDetails.filePath = message.filePath
                    val dimens = java.util.ArrayList<Int>()
                    dimens.add(message.imageHeight)
                    dimens.add(message.imageWidth)
                    sharedMuid = globalUuid
                    addMessageLinkToList(
                            getString(R.string.fugu_empty),
                            FuguAppConstant.VIDEO_MESSAGE,
                            message.url,
                            message.sharableThumbnailUrl,
                            message.image_url_100x100,
                            message.sharableThumbnailUrl,
                            fuguFileDetails,
                            globalUuid,
                            dimens,
                            isSent
                    )
                    val fileJson = messageFileJson.createFileJson(
                            ArrayList(),
                            "",
                            FuguAppConstant.VIDEO_MESSAGE,
                            message.sharableImage_url,
                            message.sharableThumbnailUrl,
                            message.sharableImage_url_100x100,
                            fuguFileDetails,
                            sharedMuid,
                            diffUtilMessageList.size - 1,
                            userId,
                            userName,
                            channelId,
                            "",
                            ArrayList(),
                            diffUtilMessageList,
                            this@ChatActivity
                    )
                    if (fileJson != null) {
                        SocketConnection.sendMessage(fileJson)
                    }
                }
            }
        }
    }

    private fun apiFetchMembers() {
        val commonParams = CommonParams.Builder()
                .add(FuguAppConstant.CHANNEL_ID, channelId)
                .add(FuguAppConstant.EN_USER_ID, enUserId)
                .add(FuguAppConstant.GET_DATA_TYPE, "MEMBERS")
                .add("user_page_start", 0)
                .build()
        RestClient.getApiInterface().getGroupInfo(
                com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                workspaceInfoList[currentPosition].fuguSecretKey,
                1,
                BuildConfig.VERSION_CODE,
                commonParams.map
        )
                .enqueue(object : ResponseResolver<MediaResponse>() {
                    override fun success(getMembersResponse: MediaResponse?) {
                        userCount = getMembersResponse?.data?.userCount!!
                        userPageSize = getMembersResponse.data?.userPageSize!!
                        membersList.clear()
                        Thread {
                            kotlin.run {
                                for (member in 0 until getMembersResponse.data?.chatMembers?.size!!) {
                                    val datum: MutableList<ChatMember>? =
                                            getMembersResponse.data.chatMembers
                                    if (datum?.get(member)?.userId?.toLong()?.compareTo(userId) != 0 && datum?.get(
                                                    member
                                            )?.status?.toInt() != 0
                                    ) {
                                        val memberObject = Member(
                                                datum?.get(member)?.fullName,
                                                datum?.get(member)?.userId?.toLong(),
                                                datum?.get(member)?.email,
                                                datum?.get(member)?.userImage,
                                                datum?.get(member)?.email,
                                                datum?.get(member)?.userType,
                                                datum?.get(member)?.status,
                                                datum?.get(member)?.leaveType
                                        )
                                        membersList.add(memberObject)
                                        membersMap.put(
                                                datum?.get(member)?.userId?.toLong()!!,
                                                memberObject
                                        )
                                    }
                                }
//                                try {
//                                    membersList.sortWith(Comparator { one, other -> one.name.compareTo(other.name) })
//                                } catch (e: Exception) {
//                                    e.printStackTrace()
//                                }
                                if (chatType != 7) {
                                    membersList.add(
                                            0,
                                            Member(
                                                    "Everyone",
                                                    -1L,
                                                    "",
                                                    "https://fuguchat.s3.ap-south-1.amazonaws.com/default/WwX5qYGSEb_1518441286074.png",
                                                    "",
                                                    1,
                                                    "",
                                                    ""
                                            )
                                    )
                                    membersMap.put(
                                            -1L,
                                            Member(
                                                    "Everyone",
                                                    -1L,
                                                    "",
                                                    "https://fuguchat.s3.ap-south-1.amazonaws.com/default/WwX5qYGSEb_1518441286074.png",
                                                    "",
                                                    1,
                                                    "",
                                                    ""
                                            )
                                    )
                                }


                            }
                        }.start()
                    }

                    override fun failure(error: APIError?) {
                    }
                })
    }

//    private fun apiGetLatestThreadMessage() {
//        ApiGetLatestThreadMessages().apiGetLatestThreadMessage(threadedMessagesArray, enUserId,
//                channelId, appSecretKey, object : ApiGetLatestThreadMessages.LatestThreadMessagesCallBack {
//            override fun onSuccess(latestThreadedMessagesResponse: LatestThreadedMessagesResponse?) {
//                Thread {
//                    kotlin.run {
//                        try {
//                            for (threadMessage in 0 until latestThreadedMessagesResponse?.data?.threadedMessageInfo?.size!!) {
//                                if (messagesMap[latestThreadedMessagesResponse.data?.threadedMessageInfo!![threadMessage].muid] != null) {
//                                    val message: Message = messagesMap[latestThreadedMessagesResponse.data?.threadedMessageInfo!![threadMessage].muid]!!
//                                    message.replyCount = latestThreadedMessagesResponse.data?.threadedMessageInfo!![threadMessage].count
//                                    messagesMap[latestThreadedMessagesResponse.data?.threadedMessageInfo!![threadMessage].muid] = message
//                                    messageList[message.messageIndex] = message
//                                    runOnUiThread {
//                                        messageAdapter?.updateMessageList(messageList)
//                                        messageAdapter?.notifyItemChanged(message.messageIndex)
//                                    }
//                                    threadMessagesMap[message.muid] = message.replyCount
//                                }
//                            }
//                            if (!isSearchedMessage) {
//                                ChatDatabase.setMessageList(messageList, channelId)
//                                ChatDatabase.setThreadMap(threadMessagesMap, channelId)
//                            }
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//                    }
//                }.start()
//            }
//        })
//    }

    private fun apiUserSearch(text: String, userSearchApi: UserSearchApi) {

        if (!isMentionCLicked) {
            val commonParams = com.skeleton.mvp.retrofit.CommonParams.Builder()
            commonParams.add("en_user_id", enUserId)
            if (chatType != 7) {
                commonParams.add("channel_id", channelId)
            }
            commonParams.add("search_text", text)
            commonParams.add("tagging", true)
            com.skeleton.mvp.data.network.RestClient.getApiInterface(true).userSearch(
                    com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                    workspaceInfoList[currentPosition].fuguSecretKey,
                    1,
                    BuildConfig.VERSION_CODE,
                    commonParams.build().map
            )
                    .enqueue(object : com.skeleton.mvp.data.network.ResponseResolver<UserSearch>() {
                        override fun onSuccess(userSearch: UserSearch?) {

                            if (userSearch?.data?.users!!.size == 0) {
                                searchPrefix = text
                            }

                            val filteredMembers = java.util.ArrayList<Member>()
                            val datum: MutableList<com.skeleton.mvp.model.userSearch.User>? =
                                    userSearch.data?.users!!
                            for (member in 0 until userSearch.data.users.size) {
                                if (datum?.get(member)?.userId?.toLong()?.compareTo(userId) != 0
                                        && (!TextUtils.isEmpty(datum?.get(member)?.fullName)
                                                && !TextUtils.isEmpty(datum?.get(member)?.email) && datum?.get(
                                                member
                                        )?.userId != null)
                                ) {
                                    val memberObject = Member(
                                            datum[member].fullName,
                                            datum[member].userId?.toLong(),
                                            datum[member].email,
                                            datum[member].userImage,
                                            datum[member].email,
                                            1,
                                            datum[member].status,
                                            datum[member].leaveType
                                    )
                                    filteredMembers.add(memberObject)
                                    membersMap[datum[member].userId] = memberObject
                                }
                            }

                            if (chatType != 7 && "Everyone".toLowerCase().contains(text)) {
                                filteredMembers.add(
                                        0, Member(
                                        "Everyone",
                                        -1L,
                                        "",
                                        "https://fuguchat.s3.ap-south-1.amazonaws.com/default/WwX5qYGSEb_1518441286074.png",
                                        "",
                                        1,
                                        "",
                                        ""
                                )
                                )
                                membersMap.put(
                                        -1L, Member(
                                        "Everyone",
                                        -1L,
                                        "",
                                        "https://fuguchat.s3.ap-south-1.amazonaws.com/default/WwX5qYGSEb_1518441286074.png",
                                        "",
                                        1,
                                        "",
                                        ""
                                )
                                )
                            }
                            userSearchApi.onSuccess(filteredMembers)
                        }

                        override fun onError(error: ApiError?) {
                        }

                        override fun onFailure(throwable: Throwable?) {
                        }

                    })
        } else {
            isMentionCLicked = false
        }
    }


    interface UserSearchApi {
        fun onSuccess(memberList: ArrayList<Member>)
    }

    @SuppressLint("SetTextI18n")
    private fun fetchIntentData() {
        maxUploadSize = com.skeleton.mvp.data.db.CommonData.getCommonResponse()
                .data.fuguConfig.max_upload_file_size
        if (intent.hasExtra("IS_SEARCHED_MESSAGE")) {
            isSearchedMessage = true
            messageSearchIndex = intent.getIntExtra("messageSearchIndex", 0)
            messageSearchMuid = intent.getStringExtra("messageSearchMuid") ?: ""
        }
        if (intent.hasExtra("isNotificationMessage")) {
            isNotificationMessage = true
            notificationMuid = intent.getStringExtra("NotificationMuid") ?: ""
        }
        conversation = Gson().fromJson(
                intent.getStringExtra(FuguAppConstant.CONVERSATION),
                FuguConversation::class.java
        )
        channelId = conversation.channelId
        workspaceInfoList =
                com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo as ArrayList<WorkspacesInfo>
        currentPosition = com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()
        enUserId = workspaceInfoList[currentPosition].enUserId
        appSecretKey = workspaceInfoList[currentPosition].fuguSecretKey
        userId = workspaceInfoList[currentPosition].userId.toLong()
        userName = workspaceInfoList[currentPosition].fullName
        chatType = conversation.chat_type
        CommonData.setChatType(conversation.chat_type)
        if (conversation.joined) {
            llJoin.visibility = View.GONE
            llMessageLayout.visibility = View.VISIBLE
            if (isSearchedMessage) {
                llMessageLayout.visibility = View.GONE
                val btnNavigate: AppCompatButton = findViewById(R.id.btnNavigate)
                btnNavigate.visibility = View.VISIBLE
                btnNavigate.text = "Go to " + conversation.label
                avi.indicator = BallClipRotateMultipleIndicator()
                avi.visibility = View.VISIBLE
                btnNavigate.setOnClickListener {
                    val intent = Intent(this@ChatActivity, ChatActivity::class.java)
                    conversation.labelId = 12345
                    intent.putExtra(
                            FuguAppConstant.CONVERSATION,
                            Gson().toJson(conversation, FuguConversation::class.java)
                    )
                    startActivity(intent)
                    finish()
                }
            }

        } else {
            llJoin.visibility = View.VISIBLE
            llMessageLayout.visibility = View.GONE
        }
        var message: String
        Thread {
            message = ChatDatabase.getUnsentTypedMessage(channelId)
            if (chatType == ChatType.BOT) {
                selectedBotAction = ChatDatabase.getUnsentTypedBotMessage(channelId)
            }
            runOnUiThread {
                if (TextUtils.isEmpty(CommonData.getSharedText())) {
                    if (!TextUtils.isEmpty(message)) {
                        etMessage.setText(Html.fromHtml(message))
                    }
                } else {
                    etMessage.setText(CommonData.getSharedText())
                    CommonData.setSharedText("")
                }
                etMessage.requestFocus()
                etMessage.setSelection(etMessage.text?.length!!)
            }
        }.start()
        if (!isSearchedMessage) {
            llChannelInfo.setOnClickListener(this)
            tvInfo?.setOnClickListener(this)
        }
        if (chatType == ChatType.BOT) {
            tvInfo?.visibility = View.GONE
            llChannelInfo.setOnClickListener(null)
            tvInfo?.setOnClickListener(null)
        }
        try {
            Thread {
                kotlin.run {
                    val unreadCountList = CommonData.getNotificationCountList()
                    val unreadCountListFinal = ArrayList<UnreadCount>()
                    for (data in unreadCountList) {
                        if (data.channelId != null && data.channelId.compareTo(channelId) == 0 && !data.isTagged && !data.isThreadMessage) {
                        } else {
                            unreadCountListFinal.add(data)
                        }
                    }
                    CommonData.setNotificationsCountList(unreadCountListFinal)
                }
            }.start()
        } catch (e: Exception) {

        }

        if (chatType == ChatType.BOT && (conversation.otherUserType == UserType.ATTENDANCE_BOT || conversation.otherUserType == UserType.HRM_BOT)) {
            apiGetBotConfiguration()
        }
    }

    private fun apiGetBotConfiguration() {

        val commonParams = CommonParams.Builder()
                .add(EN_USER_ID, enUserId)
                .add(CHANNEL_ID, channelId)

        RestClient.getApiInterface().getBotConfiguration(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), appSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                .enqueue(object : ResponseResolver<Example>() {
                    override fun success(response: Example) {
                        try {
                            val fcCommonResponse = com.skeleton.mvp.data.db.CommonData.getCommonResponse()
                            val workspacesInfoListNew = fcCommonResponse.data.workspacesInfo as ArrayList<WorkspacesInfo>
                            workspacesInfoListNew[currentPosition].userAttendanceConfig = UserAttendanceConfig(response.data?.userAttendanceConfig?.punchInPermission
                                    ?: AttendanceAuthenticationLevel.NONE.toString(), response.data?.userAttendanceConfig?.punchOutPermission
                                    ?: AttendanceAuthenticationLevel.NONE.toString())
                            workspaceInfoList = workspacesInfoListNew
                            fcCommonResponse.data.workspacesInfo = workspacesInfoListNew
                            com.skeleton.mvp.data.db.CommonData.setCommonResponse(fcCommonResponse)
                        } catch (e: java.lang.Exception) {

                        }
                    }

                    override fun failure(error: APIError?) {
                    }

                })
    }

    private lateinit var link: Array<String>
    private fun setRecyclerView() {
        if (!isSearchedMessage) {
            messageList = ChatDatabase.getMessageList(channelId)
            diffUtilMessageList = ChatDatabase.getMessageList(channelId)
            messagesMap = ChatDatabase.getMessageMap(channelId)
            unsentMessageMap = ChatDatabase.getUnsentMessageMapByChannel(channelId)
            for (i in diffUtilMessageList.indices) {
                if (diffUtilMessageList[i].rowType == HEADER_ITEM && !TextUtils.isEmpty(
                                diffUtilMessageList[i].localSentAtUtc
                        )
                ) {
                    diffUtilMessageList[i].sentAtUtc =
                            DateUtils.getDate(dateUtils.convertToLocal(diffUtilMessageList[i].localSentAtUtc))
                }
            }
        }
        val keys = ArrayList(unsentMessageMap.keys)
        if (keys.size > 0) {
            for (i in keys.indices) {
                var add = true
                val messageObj = unsentMessageMap[keys[i]]!!
                messageObj.muid = keys[i]
                messageObj.downloadStatus = 0
                messageObj.uploadStatus = 0
                if (messageObj.messageType == FuguAppConstant.FILE_MESSAGE || messageObj.messageType == FuguAppConstant.VIDEO_MESSAGE) {
                    messageObj.fileName = messageObj.fileName
                    messageObj.fileSize = messageObj.fileSize
                    messageObj.filePath = CommonData.getFileLocalPath(messageObj.uuid)

                    if (!TextUtils.isEmpty(messageObj.filePath)) {
                        link = messageObj.filePath.split("\\.".toRegex())
                                .dropLastWhile { it.isEmpty() }.toTypedArray()
                        messageObj.fileExtension = link[link.size - 1]
                    } else if (!TextUtils.isEmpty(messageObj.url)) {
                        link = messageObj.url.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        messageObj.fileExtension = link[link.size - 1]
                    } else {
                        add = false
                    }
                }
                try {
                    if (TextUtils.isEmpty(messageObj.threadMuid) && add) {
                        if (messagesMap[messageObj.uuid] == null) {
                            diffUtilMessageList.add(messageObj)
                            messagesMap[messageObj.muid] = messageObj
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
        messageAdapter = MessageAdapter(
                diffUtilMessageList,
                this,
                conversation.label,
                channelId,
                rvMessages,
                chatType,
                userId,
                workspaceInfoList[currentPosition].fullName,
                workspaceInfoList[currentPosition].userImage,
                ""
        )
        layoutManager = CustomLinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        rvMessages.layoutManager = layoutManager
        rvMessages.adapter = messageAdapter
        rvMessages.itemAnimator = null
        if (diffUtilMessageList.size > 0) {
            if (conversation.unreadCount != 0) {
                rvMessages.smoothScrollToPosition(diffUtilMessageList.size - conversation.unreadCount)
            } else {
                rvMessages.smoothScrollToPosition(diffUtilMessageList.size - 1)
            }
        }

        rvMessages.addOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        if (layoutManager.findFirstVisibleItemPosition() == 0 && diffUtilMessageList.size + 1 >= pageSize
                                && progressBar.visibility == View.GONE
                        ) {
                            if (diffUtilMessageList.size > getMessagePageSize - 5 && !isSearchedMessage) {
                                progressBar.visibility = View.VISIBLE
                                if (isNetworkConnected) {
                                    apiGetMessages(false)
                                }

                            }
                        }
                    }
                })
        messageAdapter?.setOnRetryListener(
                object : MessageAdapter.OnRetryListener {
                    override fun onRetry(
                            message: String,
                            file: String,
                            messageIndex: Int,
                            messageType: Int,
                            fileDetails: FuguFileDetails?,
                            uuid: String,
                            thumbnailUrl: String,
                            imageUrl: String,
                            imageUrl100x100: String,
                            url: String,
                            height: Int,
                            width: Int
                    ) {
                        if (messageType == IMAGE_MESSAGE) {
                            val dimens = java.util.ArrayList<Int>()
                            dimens.add(height)
                            dimens.add(width)
                            if (!TextUtils.isEmpty(imageUrl) && !TextUtils.isEmpty(thumbnailUrl) && imageUrl.contains(
                                            "https"
                                    ) && thumbnailUrl.contains("http")
                            ) {
                                val fileJson = messageFileJson.createFileJson(
                                        ArrayList(),
                                        message,
                                        IMAGE_MESSAGE,
                                        imageUrl,
                                        thumbnailUrl,
                                        imageUrl100x100,
                                        fileDetails,
                                        uuid,
                                        diffUtilMessageList.size - 1,
                                        userId,
                                        userName,
                                        channelId,
                                        "",
                                        dimens,
                                        diffUtilMessageList,
                                        this@ChatActivity
                                )
                                if (fileJson != null) {
                                    SocketConnection.sendMessage(fileJson)
                                }
                            } else {
                                uploadFileServerCall(
                                        ArrayList(),
                                        message,
                                        uuid,
                                        file,
                                        messageIndex,
                                        messageType,
                                        fileDetails,
                                        java.util.ArrayList()
                                )
                            }
                        } else if (messageType == FuguAppConstant.FILE_MESSAGE) {
                            if (!TextUtils.isEmpty(url) && url.contains("http")) {
                                val fileJson = messageFileJson.createFileJson(
                                        ArrayList(),
                                        message,
                                        FuguAppConstant.FILE_MESSAGE,
                                        url,
                                        thumbnailUrl,
                                        imageUrl100x100,
                                        fileDetails,
                                        uuid,
                                        diffUtilMessageList.size - 1,
                                        userId,
                                        userName,
                                        channelId,
                                        "",
                                        ArrayList(),
                                        diffUtilMessageList,
                                        this@ChatActivity
                                )
                                if (fileJson != null) {
                                    SocketConnection.sendMessage(fileJson)
                                }
                            } else {
                                uploadFileServerCall(
                                        ArrayList(),
                                        "",
                                        uuid,
                                        file,
                                        messageIndex,
                                        messageType,
                                        fileDetails,
                                        java.util.ArrayList()
                                )
                            }
                        } else if (messageType == FuguAppConstant.VIDEO_MESSAGE) {
                            if (!TextUtils.isEmpty(url) && url.contains("http")) {
                                val fileJson = messageFileJson.createFileJson(
                                        ArrayList(),
                                        message,
                                        FuguAppConstant.VIDEO_MESSAGE,
                                        url,
                                        thumbnailUrl,
                                        imageUrl100x100,
                                        fileDetails,
                                        uuid,
                                        diffUtilMessageList.size - 1,
                                        userId,
                                        userName,
                                        channelId,
                                        "",
                                        ArrayList(),
                                        diffUtilMessageList,
                                        this@ChatActivity
                                )
                                if (fileJson != null) {
                                    SocketConnection.sendMessage(fileJson)
                                }
                            } else {
                                uploadFileServerCall(
                                        ArrayList(),
                                        "",
                                        uuid,
                                        file,
                                        messageIndex,
                                        messageType,
                                        fileDetails,
                                        java.util.ArrayList()
                                )
                            }
                        }
                    }
                })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> onBackPressed()
            R.id.ivSend -> {
                SocketConnection.stopTyping(GeneralFunctions().getTypingObject(userName, userId, channelId))
                if (!isMessageInEditMode) {
                    if (chatType == ChatType.BOT && (conversation.otherUserType == UserType.ATTENDANCE_BOT || conversation.otherUserType == UserType.HRM_BOT)) {
                        val message: String = GeneralFunctions().getTaggedMessage(mentionsList, etMessage)
                        if (message.toLowerCase().equals("in") ||
                                message.toLowerCase().equals("/in") ||
                                message.toLowerCase().equals("out") ||
                                message.toLowerCase().equals("/out")
                        ) {
                            checkActionAndHitApi()
                        } else {
                            normalMessageSendingFlow()
                        }
                    } else {
                        normalMessageSendingFlow()
                    }
                } else {
                    if (!TextUtils.isEmpty(firstEditMuid)) {
                        if (isNetworkConnected) {
                            showLoading()
                            apiEditMessage()
                        } else {
                            showErrorMessage("No Internet Connection!")
                        }
                    } else {
                        showErrorMessage("Something went wrong! Try Again.")
                        firstEditMuid = ""
                        etMessage.setText("")
                        isMessageInEditMode = false
                        ivAttachment.visibility = View.VISIBLE
                        ivCancelEdit.visibility = View.GONE
                        ivSend.setImageResource(R.drawable.ivsend)
                    }
                }
            }
            R.id.ivCancelEdit -> {
                cancelEditing()
            }
            R.id.ivAttachment -> openBottomSheet()
            R.id.ivAudioCall -> {
                openVideoCallActivity("AUDIO")
            }
            R.id.ivHangoutsCall -> {
                if (!com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.isCalendarLinked) {
                    showErrorMessage("You need to link your Google Calender in order to connect hangouts. Do you want to link now?", {
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestServerAuthCode("1067615629789-qs9p7v7o3ur01tf7oanq3ds4k9ashmjh.apps.googleusercontent.com")
                                .requestScopes(Scope("https://www.googleapis.com/auth/calendar"))
                                .build()
                        mGoogleSignInClient = GoogleSignIn.getClient(this@ChatActivity, gso)
                        val signInIntent: Intent = mGoogleSignInClient!!.signInIntent
                        startActivityForResult(signInIntent, REQUEST_CALENDAR_AUTHORIZATION)
                    }, {}, "Yes", "Cancel")
                    return
                } else if (chatType == ChatType.O2O) {
                    makeHangoutsCall()
                } else {
                    userIdsSearch = ArrayList()
                    multiMemberAddGroupMap = LinkedHashMap()
                    val membersToBeSent = ArrayList<Member>()
                    membersToBeSent.addAll(membersList)
                    val newFragment = VideoCallInvitationBottomSheetFragment.newInstance(
                            0,
                            this@ChatActivity,
                            membersToBeSent,
                            channelId,
                            isHangoutsMeet = true
                    )
                    newFragment.show(supportFragmentManager, "VideoCallInvitationBottomSheetFragment")
                }
            }
            R.id.ivCall -> {
                if (chatType == ChatType.O2O) {
                    openVideoCallActivity("VIDEO")
                } else {
                    userIdsSearch = ArrayList()
                    multiMemberAddGroupMap = LinkedHashMap()
                    val membersToBeSent = ArrayList<Member>()
                    membersToBeSent.addAll(membersList)
                    val newFragment = VideoCallInvitationBottomSheetFragment.newInstance(
                            0,
                            this@ChatActivity,
                            membersToBeSent,
                            channelId
                    )
                    newFragment.show(supportFragmentManager, "VideoCallInvitationBottomSheetFragment")
                }
            }
            R.id.tvInfo,
            R.id.llChannelInfo -> {
                var groupIntent = Intent(this@ChatActivity, GroupInformationActivity::class.java)

                when (chatType) {
                    3, 4, 5, 6 -> {
                        isGroupOpened = true
                        groupIntent.putExtra("channelId", channelId)
                        groupIntent.putExtra(
                                "groupName",
                                tvTitle.text.toString().trim { it <= ' ' })
                        groupIntent.putExtra("isJoined", conversation.joined)
                        groupIntent.putExtra("isMuted", conversation.notifications)
                        groupIntent.putExtra("isCustomLabel", isCustomLabel)
                        if (diffUtilMessageList.size > 0) {
                            groupIntent.putExtra(MESSAGE_UNIQUE_ID, diffUtilMessageList[diffUtilMessageList.size - 1].muid)
                        } else {
                            groupIntent.putExtra(MESSAGE_UNIQUE_ID, UUID.randomUUID().toString())
                        }
                        groupIntent.putExtra("only_admin_can_message", onlyAdminsCanMessage)
                        startActivityForResult(groupIntent, REQUEST_GROUP_INFO)
                        overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    }
                    else -> {
                        val commonParams = CommonParams.Builder()
                                .add(FuguAppConstant.CHANNEL_ID, channelId)
                                .add(FuguAppConstant.EN_USER_ID, enUserId)
                                .add(FuguAppConstant.GET_DATA_TYPE, "MEMBERS")
                                .build()
                        RestClient.getApiInterface().getGroupInfo(
                                com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                                appSecretKey,
                                1,
                                BuildConfig.VERSION_CODE,
                                commonParams.map
                        ).enqueue(object : ResponseResolver<MediaResponse>() {
                            override fun success(getMembersResponse: MediaResponse) {
                                for (i in 0 until getMembersResponse.data.chatMembers.size) {
                                    if (java.lang.Long.valueOf(getMembersResponse.data.chatMembers[i].userId!!.toLong()).compareTo(userId) != 0) {
                                        groupIntent = Intent(this@ChatActivity, ProfileActivity::class.java)
                                        groupIntent.putExtra("open_profile", (getMembersResponse.data.chatMembers[i].userId).toString())
                                        groupIntent.putExtra("channelId", channelId)
                                        groupIntent.putExtra("onlyGroup", "onlyGroup")
                                        startActivity(groupIntent)
                                        overridePendingTransition(R.anim.right_in, R.anim.left_out)
                                        break
                                    }
                                }
                            }

                            override fun failure(error: APIError?) {
                            }
                        })
                    }
                }
            }
        }
    }

    private fun checkActionAndHitApi() {
        val message: String = GeneralFunctions().getTaggedMessage(mentionsList, etMessage)
        if (workspaceInfoList[currentPosition].userAttendanceConfig != null) {
            if (message.toLowerCase().equals("in") || message.toLowerCase().equals("/in")) {
                action = workspaceInfoList[currentPosition].userAttendanceConfig.attendanceIn
            } else if (message.toLowerCase().equals("out") || message.toLowerCase().equals("/out")) {
                action = workspaceInfoList[currentPosition].userAttendanceConfig.attendanceOut
            }
        }
        if (action.equals(AttendanceAuthenticationLevel.BOTH.toString())) {
            if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
            ) {
                pbVerify?.visibility = View.VISIBLE
                ivSend.visibility = View.GONE
                val locationFetcher = LocationFetcher()
                locationFetcher.init(this@ChatActivity)
                if (GeneralFunctions().isGPSTurnedOn(this)) {
                    Handler().postDelayed({
                        val takePictureIntent = Intent(this, CameraActivity::class.java)
                        startActivityForResult(takePictureIntent, 1004)
                    }, 200)
                }
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                                android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.CAMERA
                        ),
                        1001
                )
            }
        } else if (action.equals(AttendanceAuthenticationLevel.CAMERA.toString())) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                pbVerify?.visibility = View.VISIBLE
                ivSend.visibility = View.GONE

                Handler().postDelayed({
                    val takePictureIntent = Intent(this, CameraActivity::class.java)
                    startActivityForResult(takePictureIntent, 1004)
                }, 300)

            } else {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 1002)
            }
        } else if (action.equals(AttendanceAuthenticationLevel.LOCATION.toString())) {
            if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
            ) {
                pbVerify?.visibility = View.VISIBLE
                ivSend.visibility = View.GONE
                val locationfetcher = LocationFetcher()
                locationfetcher.init(this@ChatActivity)
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        1003
                )
            }
        } else {
            apiVerifyAttendanceCredentials(null)
        }
    }

    private fun normalMessageSendingFlow() {
        try {
            val message: String = GeneralFunctions().getTaggedMessage(mentionsList, etMessage)
            val globalUuid = UUID.randomUUID().toString()
            if (positionToBeInserted > 0 && positionToBeInserted < diffUtilMessageList.size && diffUtilMessageList.get(
                            positionToBeInserted
                    ).rowType == UNREAD_ITEM
            ) {
                messagesMap.remove(diffUtilMessageList[positionToBeInserted].muid)
                diffUtilMessageList.removeAt(positionToBeInserted)
                for (i in diffUtilMessageList.indices) {
                    diffUtilMessageList[i].messageIndex = i
                    val message = messagesMap[diffUtilMessageList[i].muid]
                    message?.messageIndex = i
                    messagesMap[diffUtilMessageList[i].muid] = message!!
                }
                messageList = ArrayList<Message>()
                //messageList.addAll(diffUtilMessageList)
                for (message in diffUtilMessageList) {
                    val messageClone = message.clone()
                    messageList.add(messageClone as Message)
                }
                messageAdapter?.updateMessageList(messageList)
                //messageAdapter?.notifyDataSetChanged()
                rvMessages.scrollToPosition(diffUtilMessageList.size - 1)
                positionToBeInserted = 0
            }
            addMessageToListAndPublishOnFaye(message, TEXT_MESSAGE, "", "", null, globalUuid, null)
            etMessage.setText("")
            mentionsList.clear()
            Thread {
                ChatDatabase.setUnsentTypedMessage("", channelId)
                if (chatType == ChatType.BOT && selectedBotAction != null) {
                    ChatDatabase.setUnsentTypedBotMessage(null, channelId)
                }
            }.start()
            if (diffUtilMessageList.size != 0) {
                unsentMessageMap[globalUuid] = diffUtilMessageList[diffUtilMessageList.size - 1]
            }

            Thread {
                kotlin.run {
                    ChatDatabase.setUnsentMessageMapByChannel(
                            channelId,
                            unsentMessageMap
                    )
                }
            }.start()
        } catch (e: Exception) {

        }
    }


    private fun cancelEditing() {
        try {
            val message = messagesMap[firstEditMuid]
            message?.editMode = false
            messagesMap[firstEditMuid] = message!!
            diffUtilMessageList[message.messageIndex] = message
            messageList = ArrayList<Message>()
            //messageList.addAll(diffUtilMessageList)
            for (message in diffUtilMessageList) {
                var messageClone = message.clone()
                messageList.add(messageClone as Message)
            }
            messageAdapter?.updateMessageList(messageList)
            //messageAdapter?.notifyItemChanged(message.messageIndex)
            etMessage.setText("")
            firstEditMuid = ""
            isMessageInEditMode = false
            ivAttachment.visibility = View.VISIBLE
            ivCancelEdit.visibility = View.GONE
            ivSend.setImageResource(R.drawable.ivsend)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun apiEditMessage() {
        KeyboardUtil.toggleKeyboardVisibility(this@ChatActivity)
        val commonParams = CommonParams.Builder()
        commonParams.add(EN_USER_ID, enUserId)
        commonParams.add(MESSAGE_UNIQUE_ID, firstEditMuid)
        commonParams.add(CHANNEL_ID, channelId)
        commonParams.add(MESSAGE, GeneralFunctions().getTaggedMessage(mentionsList, etMessage))
        commonParams.add(FORMATTED_MESSAGE, FormatStringUtil.FormatString.getFormattedString(GeneralFunctions().getTaggedMessage(mentionsList, etMessage))[1])
        if (mentionsList.size != 0) {
            taggedUsers = java.util.ArrayList()
            for (mention in mentionsList) {
                if (GeneralFunctions().getTaggedMessage(
                                mentionsList,
                                etMessage
                        ).contains(mention.mentionName)
                ) {
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
        com.skeleton.mvp.data.network.RestClient.getApiInterface(false)
                .editMessage(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), appSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                .enqueue(object : com.skeleton.mvp.data.network.ResponseResolver<CommonResponse>() {
                    override fun onSuccess(t: CommonResponse?) {
                        hideLoading()
                        try {
                            val message = messagesMap[firstEditMuid]
                            message?.message =
                                    GeneralFunctions().getTaggedMessage(mentionsList, etMessage)
                            val formattedStrings = FormatStringUtil.FormatString.getFormattedString(
                                    GeneralFunctions().getTaggedMessage(
                                            mentionsList,
                                            etMessage
                                    )
                            )
                            message?.alteredMessage = formattedStrings[0]
                            message?.formattedMessage = formattedStrings[1]
                            message?.editMode = false
                            taggedUsers = ArrayList()
                            mentionsList = ArrayList()
                            message?.messageState = 4
                            messagesMap[firstEditMuid] = message!!
                            diffUtilMessageList[message.messageIndex] = message
                            messageList = ArrayList<Message>()
                            //messageList.addAll(diffUtilMessageList)
                            for (message in diffUtilMessageList) {
                                val messageClone = message.clone()
                                messageList.add(messageClone as Message)
                            }
                            messageAdapter?.updateMessageList(messageList)
                            //messageAdapter?.notifyItemChanged(message.messageIndex)
                            etMessage.setText("")
                            firstEditMuid = ""
                            isMessageInEditMode = false
                            ivAttachment.visibility = View.VISIBLE
                            ivCancelEdit.visibility = View.GONE
                            ivSend.setImageResource(R.drawable.ivsend)
                        } catch (e: Exception) {
                            showErrorMessage("Failed to edit message! Try Again")
                        }
                    }

                    override fun onError(error: ApiError?) {
                        hideLoading()
                        cancelEditing()
                        showErrorMessage(error!!.message)
                    }

                    override fun onFailure(throwable: Throwable?) {
                    }
                })
    }

    private fun openBottomSheet() {
        val bottomSheetFragment = AttachmentSheetFragment().newInstance(0, chatType, false)
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }

    private fun selectImage() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        if (isKeyBoardVisible) {
            dialogView = View.inflate(this, R.layout.gallery_bottom_sheet_with_keyboard, null)
        } else {
            dialogView = View.inflate(this, R.layout.gallery_bottom_sheet, null)
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(dialogView!!)
        val window = dialog!!.window!!
        window.setFlags(
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        )
        window.setFlags(
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        )
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        val wlp = window.attributes
        wlp.gravity = Gravity.BOTTOM
        window.attributes = wlp
        val rvAttachment = dialog!!.findViewById(R.id.rvAttachment) as RecyclerView
        val viewBottom: View = dialog!!.findViewById(R.id.viewBottom)
        rvAttachment.layoutManager = GridLayoutManager(this@ChatActivity, 3)
        var fuguAttachmentAdapter: FuguAttachmentAdapter? = null
        if (chatType == ChatType.O2O || chatType == ChatType.BOT) {
            fuguImageUtils?.setCallbaks(
                    FuguAppConstant.OPEN_CAMERA_ADD_IMAGE,
                    REQUEST_CODE_PICK_IMAGE,
                    REQUEST_CODE_PICK_FILE,
                    REQUEST_CODE_PICK_AUDIO,
                    REQUEST_CODE_PICK_VIDEO,
                    FuguAppConstant.START_POLL,
                    true,
                    true
            )
            fuguAttachmentAdapter = FuguAttachmentAdapter(this@ChatActivity, true, true)
            rvAttachment.adapter = fuguAttachmentAdapter
        } else {
            fuguImageUtils?.setCallbaks(
                    FuguAppConstant.OPEN_CAMERA_ADD_IMAGE,
                    REQUEST_CODE_PICK_IMAGE,
                    REQUEST_CODE_PICK_FILE,
                    REQUEST_CODE_PICK_AUDIO,
                    REQUEST_CODE_PICK_VIDEO,
                    FuguAppConstant.START_POLL,
                    true,
                    true
            )
            fuguAttachmentAdapter = FuguAttachmentAdapter(this@ChatActivity, true, false)
            rvAttachment.adapter = fuguAttachmentAdapter
        }
        viewBottom.setOnClickListener {
            revealShow(dialogView, false, null)
            dialog!!.dismiss()
        }
        fuguAttachmentAdapter.setOnAttachListener { action ->
            revealShow(dialogView, false, dialog)

        }
        dialog!!.setOnShowListener { revealShow(dialogView, true, null) }
        dialog!!.setOnKeyListener(object : DialogInterface.OnKeyListener {
            override fun onKey(
                    dialogInterface: DialogInterface,
                    i: Int,
                    keyEvent: KeyEvent
            ): Boolean {
                if (i == KeyEvent.KEYCODE_BACK) {
                    revealShow(dialogView, false, dialog)
                    return true
                }
                return false
            }
        })
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setCancelable(true)
        dialog!!.setCanceledOnTouchOutside(true)
        dialog!!.show()
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_OUTSIDE) {
            dialog?.dismiss()
        }
        return false
    }

    private fun revealShow(dialogView: View?, b: Boolean, dialog: Dialog?) {
        try {
            val view = dialogView!!.findViewById(R.id.dialog) as LinearLayout
            val w = view.width
            val h = view.height
            val endRadius = Math.hypot(w.toDouble(), h.toDouble()).toFloat()
            val cx = (ivAttachment.x + ivAttachment.width / 2).toInt()
            val cy = ivAttachment.y.toInt() + ivAttachment.height + 56
            if (b) {
                val revealAnimator =
                        ViewAnimationUtils.createCircularReveal(view, cx + 40, cy + 400, 0f, endRadius)
                view.visibility = View.VISIBLE
                revealAnimator.duration = 450
                revealAnimator.start()
            } else {
                var anim: Animator? = null
                if (isKeyBoardVisible) {
                    anim = ViewAnimationUtils.createCircularReveal(
                            view,
                            cx + 40,
                            cy - 100,
                            endRadius,
                            0f
                    )
                } else {
                    anim = ViewAnimationUtils.createCircularReveal(
                            view,
                            cx + 40,
                            cy + 400,
                            endRadius,
                            0f
                    )
                }
                anim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        dialog?.dismiss()
                        view.visibility = View.INVISIBLE
                    }
                })
                anim.duration = 450
                anim.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addMessageToListAndPublishOnFaye(
            textMessage: String,
            messageType: Int,
            thumbnailUrl: String,
            imageurl: String,
            fuguFileDetails: FuguFileDetails?,
            muid: String,
            pollJson: JSONObject?
    ) {
        try {
            if (!TextUtils.isEmpty(textMessage) && messageType == TEXT_MESSAGE) {
                addMessageToList(
                        textMessage,
                        messageType,
                        thumbnailUrl,
                        imageurl,
                        fuguFileDetails,
                        muid,
                        java.util.ArrayList(),
                        null
                )
            } else if (messageType == POLL_MESSAGE) {
                addMessageToList(
                        textMessage,
                        messageType,
                        thumbnailUrl,
                        imageurl,
                        fuguFileDetails,
                        muid,
                        java.util.ArrayList(),
                        pollJson
                )
            }
            if (!TextUtils.isEmpty(textMessage)) run {
                publishMessage(
                        textMessage,
                        messageType,
                        thumbnailUrl,
                        imageurl,
                        null,
                        muid,
                        0,
                        true
                )
            }
        } catch (e: Exception) {

        }
    }

    private fun publishMessage(
            textMessage: String, messageType: Int,
            thumbnailUrl: String, url: String, fileDetails: FuguFileDetails?,
            muid: String, position: Int, isSendMessage: Boolean
    ) {
        if (isNetworkConnected) {
            val localDate = DateUtils.getFormattedDate(Date())
            val messageJson = JSONObject()
            messageJson.put(CHANNEL_ID, channelId)
            if (chatType == ChatType.BOT && isSendMessage) {
                if (selectedBotAction != null) {
                    if (textMessage.split(" ")[0] == selectedBotAction!!.displayTag) {
                        messageJson.put("metric", selectedBotAction?.tag)
                    } else {
                        selectedBotAction = null
                    }
                }
                messageJson.put(BOT_USER_ID, otherUserid)
                messageJson.put(CHANNEL_ID, channelId)
                selectedBotAction = null
                llBotAction?.visibility = View.GONE
            }
            messageJson.put(FULL_NAME, userName)
            messageJson.put(MESSAGE, textMessage)
            messageJson.put(FORMATTED_MESSAGE, FormatStringUtil.FormatString.getFormattedString(textMessage)[1].trim())
            messageJson.put(MESSAGE_TYPE, messageType)
            messageJson.put(
                    DATE_TIME,
                    DateUtils.getInstance().convertToUTC(localDate)
            )
            if (position == 0) {
                messageJson.put(MESSAGE_INDEX, diffUtilMessageList.size - 1)
            } else {
                messageJson.put(MESSAGE_INDEX, position)
            }
            messageJson.put(MESSAGE_UNIQUE_ID, muid)

            if (messageType == IMAGE_MESSAGE && !url.trim { it <= ' ' }.isEmpty() && !thumbnailUrl.trim { it <= ' ' }.isEmpty()) {
                messageJson.put(IMAGE_URL, url)
                messageJson.put(THUMBNAIL_URL, thumbnailUrl)
            }
            messageJson.put(IS_THREAD_MESSAGE, false)
            if (messageType == FILE_MESSAGE && !url.trim { it <= ' ' }.isEmpty()) {
                messageJson.put("url", url)
                messageJson.put(FILE_NAME, fileDetails?.fileName)
                messageJson.put(FILE_SIZE, fileDetails?.fileSize)
            }
            if (taggedUsers != null && taggedUsers!!.size != 0) {
                val jsonArrayTaggedUsers = JSONArray()
                for (id in taggedUsers!!) {
                    jsonArrayTaggedUsers.put(id)
                }
                messageJson.put(TAGGED_USERS, jsonArrayTaggedUsers)
                if (jsonArrayTaggedUsers.toString().contains("-1")) {
                    messageJson.put(TAGGED_ALL, true)
                }
            }
            messageJson.put(DEVICE_TOKEN, com.skeleton.mvp.data.db.CommonData.getFcmToken())
            messageJson.put(MESSAGE_STATUS, MESSAGE_UNSENT)
            val devicePayload = JSONObject()
            devicePayload.put(DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(this@ChatActivity))
            devicePayload.put(DEVICE_TYPE, ANDROID_USER)
            devicePayload.put(APP_VERSION, BuildConfig.VERSION_NAME)
            devicePayload.put(DEVICE_DETAILS, CommonData.deviceDetails(this@ChatActivity))
            messageJson.put(DEVICE_PAYLOAD, devicePayload)
            messageJson.put(USER_ID, userId.toString())
            messageJson.put(USER_TYPE, ANDROID_USER)
            if ((textMessage.toLowerCase().contains(LEAVE) || textMessage.toLowerCase().contains(WORK_FROM_HOME)) && chatType == ChatType.BOT) {
                try {
                    messageJson.put("time_zone", getTimeZoneOffset())
                    val dateList = DateStringUtil.FormatString.getFormattedString(textMessage)
                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
                    if (dateList.size == 2) {
                        if (sdf.parse(dateList[0]).time < sdf.parse(dateList[1]).time) {
                            messageJson.put(LEAVE_START_DATE, dateList[0])
                            messageJson.put(LEAVE_END_DATE, dateList[1])
                        } else {
                            messageJson.put(LEAVE_START_DATE, dateList[1])
                            messageJson.put(LEAVE_END_DATE, dateList[0])
                        }
                    } else {
                        messageJson.put(LEAVE_START_DATE, dateList[0])
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            SocketConnection.sendMessage(messageJson)
        }
    }

    fun addMessageToList(
            textMessage: String,
            messageType: Int,
            imageurl: String,
            thumbnailUrl: String,
            fileDetails: FuguFileDetails?,
            muid: String,
            dimens: ArrayList<Int>,
            pollJson: JSONObject?
    ) {
        try {
            val localDate = DateUtils.getFormattedDate(Date())
            val messageObject = Message()
            messageObject.muid = muid
            messageObject.isSent = true
            messageObject.fromName = userName
            messageObject.id = 0
            messageObject.userId = userId
            messageObject.sentAtUtc = DateUtils.getInstance().convertToUTC(localDate)
            messageObject.message = textMessage
            val formattedStrings = FormatStringUtil.FormatString.getFormattedString(textMessage)
            messageObject.alteredMessage = formattedStrings[0]
            messageObject.formattedMessage = formattedStrings[1]
            messageObject.messageStatus = MESSAGE_UNSENT
            messageObject.thumbnailUrl = thumbnailUrl
            messageObject.image_url = imageurl
            messageObject.sharableImage_url = imageurl
            messageObject.sharableThumbnailUrl = thumbnailUrl
            messageObject.url = fileDetails?.filePath
            messageObject.messageType = messageType
            messageObject.email = com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.email
            messageObject.userReaction = UserReaction()
            messageObject.userImage = emptyString
            messageObject.threadMessage = false
            messageObject.userType = UserType.CUSTOMER
            messageObject.messageState = 1
            taggedUsers?.clear()
            if (mentionsList.size != 0) {
                taggedUsers = java.util.ArrayList()
                for (mention in mentionsList) {
                    if (textMessage.contains(mention.mentionName)) {
                        if (!taggedUsers!!.contains(mention.userId.toInt())) {
                            taggedUsers!!.add(mention.userId.toInt())
                        }
                    }
                }
                mentionsList.clear()
                messageObject.taggedUsers = taggedUsers
            }
            if (dimens.size == 2) {
                messageObject.imageHeight = dimens[0]
                messageObject.imageWidth = dimens[1]
            }
            if (fileDetails != null) {
                messageObject.fileName = fileDetails.fileName
                messageObject.fileSize = fileDetails.fileSize
                messageObject.fileExtension = fileDetails.fileExtension
            }
            if (pollJson != null) {
                messageObject.multipleSelect = pollJson.getBoolean(IS_MULTIPLE_SELECT)
                messageObject.question = pollJson.getString(QUESTION)
                messageObject.total_votes = 0
                messageObject.expireTime = pollJson.getLong(EXPIRY_TIME)
                val pollOptions = ArrayList<PollOption>()
                val pollJson = pollJson.getJSONArray(POLL_OPTIONS)
                for (i in 0..pollJson.length() - 1) {
                    val jsonObject = pollJson.getJSONObject(i)
                    val pollOption = PollOption()
                    pollOption.label = jsonObject?.getString("label")
                    pollOption.users = ArrayList<User>()
                    pollOption.pollCount = 0
                    pollOption.puid = jsonObject?.getString("puid")
                    pollOptions.add(pollOption)
                }
                messageObject.pollOptions = pollOptions
                if (messageObject.pollOptions != null) {
                    for (poll in 0..(messageObject.pollOptions.size - 1)) {
                        messageObject.pollOptions[poll].voteMap = HashMap()
                        for (user in (0..messageObject.pollOptions[poll].users.size - 1)) {

                            messageObject.pollOptions[poll].voteMap[messageObject.pollOptions[poll].users[user].userId] =
                                    messageObject.pollOptions[poll].users[user]
                        }
                    }
                }
            }
            messageObject.messageIndex = diffUtilMessageList.size
            if (userId.compareTo(messageObject.userId) == 0) {
                when (messageObject.messageType) {
                    TEXT_MESSAGE -> messageObject.rowType = TEXT_MESSGAE_SELF
                    IMAGE_MESSAGE -> messageObject.rowType = IMAGE_MESSGAE_SELF
                    FILE_MESSAGE -> messageObject.rowType = FILE_MESSGAE_SELF
                    FuguAppConstant.PUBLIC_NOTE -> messageObject.rowType = PUBLIC_NOTE
                    VIDEO_MESSAGE -> messageObject.rowType = VIDEO_MESSGAE_SELF
                    VIDEO_CALL -> messageObject.rowType = VIDEO_CALL_SELF
                    POLL_MESSAGE -> messageObject.rowType = POLL_SELF
                }
            } else {
                when (messageObject.messageType) {
                    TEXT_MESSAGE -> messageObject.rowType = TEXT_MESSGAE_OTHER
                    IMAGE_MESSAGE -> messageObject.rowType = IMAGE_MESSGAE_OTHER
                    FILE_MESSAGE -> messageObject.rowType = FILE_MESSGAE_OTHER
                    FuguAppConstant.PUBLIC_NOTE -> messageObject.rowType = PUBLIC_NOTE
                    VIDEO_MESSAGE -> messageObject.rowType = VIDEO_MESSGAE_OTHER
                    VIDEO_CALL -> messageObject.rowType = VIDEO_CALL_OTHER
                    POLL_MESSAGE -> messageObject.rowType = POLL_OTHER
                }
            }
            if (messageObject.rowType == VIDEO_MESSGAE_SELF) {
                messageObject.thumbnailUrl = ""
            }
            if (positionToBeInserted > 0 && diffUtilMessageList.get(positionToBeInserted).rowType == UNREAD_ITEM) {
                messagesMap.remove(diffUtilMessageList[positionToBeInserted].muid)
                diffUtilMessageList.removeAt(positionToBeInserted)
                for (i in diffUtilMessageList.indices) {
                    diffUtilMessageList[i].messageIndex = i
                    val message = messagesMap[diffUtilMessageList[i].muid]
                    message?.messageIndex = i
                    messagesMap[diffUtilMessageList[i].muid] = message!!
                }
                rvMessages.post {
                    messageList = ArrayList<Message>()
                    //messageList.addAll(diffUtilMessageList)
                    for (message in diffUtilMessageList) {
                        val messageClone = message.clone()
                        messageList.add(messageClone as Message)
                    }
                    messageAdapter?.updateMessageList(messageList)
                    //messageAdapter?.notifyDataSetChanged()
                    rvMessages.scrollToPosition(diffUtilMessageList.size - 1)
                }
                positionToBeInserted = 0
            }
            diffUtilMessageList.add(messageObject)
            messageList = ArrayList<Message>()
            // messageList.addAll(diffUtilMessageList)
            for (message in diffUtilMessageList) {
                val messageClone = message.clone()
                messageList.add(messageClone as Message)
            }
            messageAdapter?.updateMessageList(messageList)
            rvMessages.post {
                messageList = ArrayList<Message>()
                //messageList.addAll(diffUtilMessageList)
                for (message in diffUtilMessageList) {
                    val messageClone = message.clone()
                    messageList.add(messageClone as Message)
                }
                messageAdapter?.updateMessageList(messageList)
                //messageAdapter?.notifyItemInserted(messageList.size - 1)
                scrollBottomCount = 0
                rlScrollBottom.visibility = View.GONE
                rvMessages.scrollToPosition(diffUtilMessageList.size - 1)
            }
            messagesMap[muid] = messageObject
            if (messageType == IMAGE_MESSAGE || messageType == FuguAppConstant.FILE_MESSAGE || messageType == FuguAppConstant.VIDEO_MESSAGE) {
                unsentMessageMap[muid] = diffUtilMessageList[diffUtilMessageList.size - 1]
                ChatDatabase.setUnsentMessageMapByChannel(channelId, unsentMessageMap)
                uploadFileServerCall(
                        taggedUsers!!,
                        textMessage,
                        muid,
                        imageurl,
                        diffUtilMessageList.size - 1,
                        messageType,
                        fileDetails,
                        dimens
                )
            }
            Thread {
                kotlin.run {
                    if (!isSearchedMessage) {
                        ChatDatabase.setMessageList(diffUtilMessageList, channelId)
                        ChatDatabase.setMessageMap(messagesMap, channelId)
                    }
                }
            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun uploadFileServerCall(
            taggesUsers: java.util.ArrayList<Int>,
            textMessage: String,
            uuid: String,
            url: String,
            messageIndex: Int,
            messageType: Int,
            fileDetails: FuguFileDetails?,
            dimens: ArrayList<Int>
    ) {
        val fileBody: ProgressRequestBody
        val filePart: MultipartBody.Part
        if (messageType == FILE_MESSAGE || messageType == VIDEO_MESSAGE || fileDetails?.fileExtension.equals("gif")) {
            fileBody = ProgressRequestBody(
                    File(url),
                    this,
                    GeneralFunctions().getMimeType(fileDetails?.filePath!!, this@ChatActivity),
                    url,
                    messageIndex,
                    uuid
            )
            filePart =
                    MultipartBody.Part.createFormData("file", fileDetails.fileExtension, fileBody)
        } else {
            val link = url.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            fileBody = ProgressRequestBody(
                    File(url),
                    this,
                    GeneralFunctions().getMimeType(url, this@ChatActivity),
                    url,
                    messageIndex,
                    uuid
            )
            filePart = MultipartBody.Part.createFormData("file", link[link.size - 1], fileBody)
        }

        val fuguSecretkey: String = workspaceInfoList[currentPosition].fuguSecretKey
        try {
            val multipartBuilder = MultipartParams.Builder()
            val multipartParams = multipartBuilder
                    .add(FuguAppConstant.APP_SECRET_KEY, fuguSecretkey)
                    .add(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_CODE)
                    .add("file_name", fileDetails?.fileName)
                    .add(MESSAGE_TYPE, messageType)
                    .add("muid", uuid)
                    .add(FuguAppConstant.DEVICE_TYPE, 1).build()
            RestClient.getApiInterface().uploadFile(
                    com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                    fuguSecretkey,
                    1,
                    BuildConfig.VERSION_CODE,
                    filePart,
                    multipartParams.map
            ).enqueue(object : ResponseResolver<FuguUploadImageResponse>() {
                override fun success(fuguUploadImageResponse: FuguUploadImageResponse?) {
                    Thread {
                        kotlin.run {
                            if (fileDetails != null) {
                                try {
                                    if (!TextUtils.isEmpty(fuguUploadImageResponse!!.data.imageSize.toString())) {
                                        if (fuguUploadImageResponse.data.imageSize!! < 1024) {
                                            fileDetails.fileSize =
                                                    fuguUploadImageResponse.data.imageSize.toString() + " B"
                                        } else if ((fuguUploadImageResponse.data?.imageSize!! / 1024).toInt() > 1024) {
                                            fileDetails.fileSize =
                                                    (fuguUploadImageResponse.data.imageSize / (1024 * 1024)).toString() + " MB"
                                        } else {
                                            fileDetails.fileSize =
                                                    (fuguUploadImageResponse.data.imageSize / 1024).toString() + " KB"
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                CommonData.setFilesMap(
                                        uuid,
                                        fuguUploadImageResponse?.data?.url,
                                        fileDetails.filePath
                                )
                                CommonData.setFileLocalPath(uuid, fileDetails.filePath)
                                CommonData.setCachedFilePath(
                                        fuguUploadImageResponse?.data?.url,
                                        uuid,
                                        fileDetails.filePath
                                )
                            }
                            this@ChatActivity.runOnUiThread {

                                if (fuguUploadImageResponse?.data?.muid == null) {
                                    fuguUploadImageResponse?.data?.muid = uuid
                                }

                                if ((messageIndex < diffUtilMessageList.size) && diffUtilMessageList[messageIndex].muid.equals(
                                                fuguUploadImageResponse?.data?.muid
                                        )
                                ) {
                                    messagePosition = messageIndex
                                } else {
                                    var position = -1
                                    for (i in diffUtilMessageList.size - 1 downTo 0) {
                                        if (diffUtilMessageList[i].muid == fuguUploadImageResponse?.data?.muid) {
                                            position = i
                                            break
                                        }
                                    }
                                    messagePosition = position
                                }
                                if (messagePosition != -1) {
                                    diffUtilMessageList[messagePosition].filePath =
                                            fileDetails?.filePath
                                    diffUtilMessageList[messagePosition].sentFilePath =
                                            fileDetails?.filePath
                                    diffUtilMessageList[messagePosition].uploadStatus =
                                            FuguAppConstant.UploadStatus.UPLOAD_COMPLETED.uploadStatus
                                    diffUtilMessageList[messagePosition].downloadStatus =
                                            FuguAppConstant.UploadStatus.UPLOAD_COMPLETED.uploadStatus
                                    diffUtilMessageList[messagePosition].thumbnailUrl =
                                            fuguUploadImageResponse?.data?.thumbnailUrl
                                    diffUtilMessageList[messagePosition].sharableImage_url =
                                            fuguUploadImageResponse?.data?.thumbnailUrl
                                    diffUtilMessageList[messagePosition].image_url_100x100 =
                                            fuguUploadImageResponse?.data?.image_url_100x100
                                    diffUtilMessageList[messagePosition].sharableImage_url_100x100 =
                                            fuguUploadImageResponse?.data?.image_url_100x100
                                    diffUtilMessageList[messagePosition].fileName =
                                            fileDetails?.fileName
                                    diffUtilMessageList[messagePosition].url =
                                            fuguUploadImageResponse?.data?.url
                                    messagesMap[diffUtilMessageList[messagePosition].muid] =
                                            diffUtilMessageList[messagePosition]
                                    messageList = ArrayList<Message>()
                                    //messageList.addAll(diffUtilMessageList)
                                    for (message in diffUtilMessageList) {
                                        var messageClone = message.clone()
                                        messageList.add(messageClone as Message)
                                    }
                                    messageAdapter?.updateMessageList(messageList)
                                    //messageAdapter?.notifyItemChanged(messagePosition)
                                    if (channelId > -1 && messagePosition != -1) {
                                        try {
                                            publishImageOrFileOnFaye(
                                                    taggesUsers,
                                                    textMessage,
                                                    messagePosition,
                                                    fuguUploadImageResponse!!,
                                                    messageType,
                                                    fileDetails,
                                                    dimens,
                                                    fuguUploadImageResponse.data?.muid!!
                                            )
                                        } catch (e: Exception) {
                                        }
                                    }
                                }
                            }
                        }
                    }.start()

                }

                override fun failure(error: APIError?) {
                    if (error?.message == ResponseResolver.UNEXPECTED_ERROR_OCCURRED) {
                        try {
                            val alert = AlertDialog.Builder(this@ChatActivity)
                            alert.setMessage(error.message)
                            alert.setPositiveButton("Ok", null)
                            alert.setCancelable(false)
                            alert.show()
                            unsentMessageMap.remove(uuid)
                            diffUtilMessageList.removeAt(diffUtilMessageList.size - 1)
                            messageList = ArrayList<Message>()
                            //messageList.addAll(diffUtilMessageList)
                            for (message in diffUtilMessageList) {
                                var messageClone = message.clone()
                                messageList.add(messageClone as Message)
                            }
                            messageAdapter?.updateMessageList(messageList)
                            //messageAdapter?.notifyDataSetChanged()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        try {
                            when (messageType) {
                                IMAGE_MESSAGE -> {
                                    diffUtilMessageList[messageIndex].messageStatus =
                                            FuguAppConstant.MESSAGE_IMAGE_RETRY
                                }
                                FuguAppConstant.FILE_MESSAGE -> {
                                    diffUtilMessageList[messageIndex].messageStatus =
                                            FuguAppConstant.MESSAGE_FILE_RETRY
                                }
                                FuguAppConstant.VIDEO_MESSAGE -> {
                                    diffUtilMessageList[messageIndex].messageStatus =
                                            FuguAppConstant.MESSAGE_FILE_RETRY
                                }
                            }
                            diffUtilMessageList[messageIndex].uploadStatus = 0
                            diffUtilMessageList[messageIndex].downloadStatus = 0
                            messageList = ArrayList<Message>()
                            //messageList.addAll(diffUtilMessageList)
                            for (message in diffUtilMessageList) {
                                val messageClone = message.clone()
                                messageList.add(messageClone as Message)
                            }
                            messageAdapter?.updateMessageList(messageList)
                            //messageAdapter?.notifyItemChanged(messageIndex)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            })
        } catch (e: Exception) {
        }
    }

    private fun publishImageOrFileOnFaye(
            taggesUsers: ArrayList<Int>,
            textMessage: String,
            messageIndex: Int,
            fuguUploadImageResponse: FuguUploadImageResponse,
            messageType: Int,
            fileDetails: FuguFileDetails?,
            dimens: ArrayList<Int>,
            uuid: String
    ) {
        diffUtilMessageList[messageIndex].sharableImage_url = fuguUploadImageResponse.data?.url
        diffUtilMessageList[messageIndex].sharableImage_url_100x100 = fuguUploadImageResponse.data?.image_url_100x100
        diffUtilMessageList[messageIndex].sharableThumbnailUrl = fuguUploadImageResponse.data?.thumbnailUrl
        diffUtilMessageList[messageIndex].url = fuguUploadImageResponse.data?.url
        val messageJson = messageFileJson.createFileJson(
                taggesUsers,
                textMessage,
                messageType,
                fuguUploadImageResponse.data?.url,
                fuguUploadImageResponse.data?.thumbnailUrl,
                fuguUploadImageResponse.data?.image_url_100x100,
                fileDetails,
                uuid,
                messageIndex,
                userId,
                userName,
                channelId,
                "",
                dimens,
                diffUtilMessageList,
                this@ChatActivity
        )
        if (messageJson != null) {
            SocketConnection.sendMessage(messageJson)
        }
    }

    override fun onQueryReceived(query: String?) {
        var isApiBeingHit = false
        searchCountDown?.cancel()
        searchCountDown = object : CountDownTimer(200, 100) {
            override fun onFinish() {
                var filteredMembers = java.util.ArrayList<Member>()
                if (!TextUtils.isEmpty(query?.trim())) {
                    val queryLower = query?.toLowerCase(Locale.US)
                    runOnUiThread {
                        if (membersList.isNotEmpty()) {
                            if (queryLower?.length!! > 1 && !queryLower.startsWith(searchPrefix) && userCount > userPageSize) {
                                isApiBeingHit = true
                                apiUserSearch(queryLower, object : UserSearchApi {
                                    override fun onSuccess(memberList: ArrayList<Member>) {
                                        isApiBeingHit = false
                                        filteredMembers = java.util.ArrayList<Member>()
                                        filteredMembers.addAll(memberList)
                                        if (!filteredMembers.isEmpty()) {
                                            usersAdapter?.clear()
                                            usersAdapterImage?.clear()
                                            usersAdapter?.setCurrentQuery(query)
                                            usersAdapterImage?.setCurrentQuery(query)
                                            usersAdapter?.addAll(filteredMembers)
                                            usersAdapterImage?.addAll(filteredMembers)
                                            showMentionsList(true)
                                        } else {
                                            showMentionsList(false)
                                        }
                                    }
                                })
                            } else {
                                filteredMembers = java.util.ArrayList<Member>()
                                if (!isMentionCLicked && query.isNotEmpty()) {
                                    for (member in membersList) {
                                        if (member.name.toLowerCase().contains(queryLower)) {
                                            filteredMembers.add(member)
                                        }
                                    }
                                    if (filteredMembers.isNotEmpty()) {
                                        usersAdapter?.clear()
                                        usersAdapterImage?.clear()
                                        usersAdapter?.setCurrentQuery(query)
                                        usersAdapterImage?.setCurrentQuery(query)
                                        usersAdapter?.addAll(filteredMembers)
                                        usersAdapterImage?.addAll(filteredMembers)
                                        showMentionsList(true)
                                    } else {
                                        showMentionsList(false)
                                    }
                                }

                            }
                        } else {
                            if (chatType == ChatType.BOT && conversation.otherUserType != UserType.FUGU_SUPPORT) {
                                apiGetMembers()
                            } else if (chatType != 7) {
                                apiFetchMembers()
                            }
                        }
                    }
                }



                if (TextUtils.isEmpty(query?.trim())) {
                    isMentionCLicked = false
                    filteredMembers = java.util.ArrayList<Member>()
                    isMentionCLicked = false
                    usersAdapter?.clear()
                    usersAdapterImage?.clear()
                    usersAdapter?.setCurrentQuery(query)
                    usersAdapterImage?.setCurrentQuery(query)
                    usersAdapter?.addAll(filteredMembers)
                    usersAdapterImage?.addAll(filteredMembers)
                }
                val length = etMessage.text!!.length
                if (length != 0 || imageText.isNotEmpty()) {
                    val lastChar: Char = if (length != 0) {
                        etMessage.text!![length - 1]
                    } else {
                        imageText[imageText.length - 1]
                    }
                    if (lastChar.toString().equals("@", ignoreCase = true) && query != null && query.trim { it <= ' ' }.isEmpty()) {
                        // load entire list
                        if (membersList.isNotEmpty()) {
                            filteredMembers.clear()
                            filteredMembers.addAll(membersList)
                            if (filteredMembers.isNotEmpty()) {
                                usersAdapter?.clear()
                                usersAdapterImage?.clear()
                                usersAdapter?.setCurrentQuery(query)
                                usersAdapterImage?.setCurrentQuery(query)
                                usersAdapter?.addAll(filteredMembers)
                                usersAdapterImage?.addAll(filteredMembers)
                                showMentionsList(true)
                            } else {
                                showMentionsList(false)
                            }
                        } else {
                            // we dont have members try to fetch again
                            if (chatType == ChatType.BOT && conversation.otherUserType != UserType.FUGU_SUPPORT) {
                                apiGetMembers()
                            } else if (chatType != 7) {
                                apiFetchMembers()
                            }
                        }
                    }
                }

            }

            override fun onTick(millisUntilFinished: Long) {
            }
        }.start()
    }

    private fun showMentionsList(display: Boolean) {
        llMention.visibility = View.VISIBLE
        if (imageLlMentions != null) {
            imageLlMentions!!.visibility = View.VISIBLE
        }
        if (display) {
            rvMentions.visibility = View.VISIBLE
            if (imageRecycler != null) {
                imageRecycler!!.visibility = View.VISIBLE
            }
        } else if (rvMentions.visibility == View.VISIBLE) {
            rvMentions.visibility = View.GONE
            if (imageRecycler != null) {
                imageRecycler!!.visibility = View.GONE
            }
        }
    }

    override fun displaySuggestions(display: Boolean) {
        if (display) {
            llMention.visibility = View.VISIBLE
            if (imageLlMentions != null) {
                imageLlMentions!!.visibility = View.VISIBLE
            }
        } else if (llMention.visibility == View.VISIBLE) {
            llMention.visibility = View.GONE
            if (imageLlMentions != null) {
                imageLlMentions!!.visibility = View.GONE
            }
        }
    }

    private fun setUpMentionsList() {
        rvMentions.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        usersAdapter = UsersAdapter(this)
        rvMentions.adapter = usersAdapter
        rvMentions.addOnItemTouchListener(object : RecyclerItemClickListener(
                this,
                OnItemClickListener { _: View, position: Int ->
                    isMentionCLicked = true
                    val user = usersAdapter!!.getItem(position)
                    if (user != null) {
                        val mention = Mention()
                        mention.mentionName = "@" + user.name
                        mention.userId = user.userId!!
                        mention.setEmail(user.email)
                        mentions.insertMention(mention)
                        var isAlreadyTagged = false
                        for (oldMention in mentionsList) {
                            if (oldMention.userId.compareTo(mention.userId) == 0) {
                                isAlreadyTagged = true
                                break
                            }
                        }
                        if (!isAlreadyTagged) {
                            mentionsList.add(mention)
                        }
                    }
                }) {})
    }

    private fun setUpMentionsListForImage(recyclerView: RecyclerView) {
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        usersAdapterImage = UsersAdapter(this)
        recyclerView.adapter = usersAdapterImage
        recyclerView.addOnItemTouchListener(object : RecyclerItemClickListener(
                this,
                OnItemClickListener { _: View, position: Int ->
                    isMentionCLicked = true
                    val user = usersAdapterImage!!.getItem(position)
                    if (user != null) {
                        val mention = Mention()
                        mention.mentionName = "@" + user.name
                        mention.userId = user.userId!!
                        mention.setEmail(user.email)
                        mentionsInImage.insertMention(mention)
                        var isAlreadyTagged = false
                        for (oldmention in mentionsList) {
                            if (oldmention.userId.toLong().compareTo(mention.userId.toLong()) == 0) {
                                isAlreadyTagged = true
                                break
                            }
                        }
                        if (!isAlreadyTagged) {
                            mentionsList.add(mention)
                        }
                    }
                }) {})
    }

    private fun setUpMentions() {
        mentions = Mentions.Builder(this, etMessage)
                .suggestionsListener(this)
                .queryListener(this)
                .highlightColor(R.color.mentionAndLinks)
                .build()
        Thread {
            val cachedMentions: ArrayList<Mention>?
            if (ChatDatabase.getMentions(channelId) != null) {
                cachedMentions = ChatDatabase.getMentions(channelId)
                for (i in cachedMentions!!.iterator()) {
                    runOnUiThread {
                        mentionsList.add(i)
                    }
                }
            }

        }.start()
    }

    private fun setUpMentionsForImage(etMsg: EmojiGifEditText) {
        mentionsInImage = Mentions.Builder(this, etMsg)
                .suggestionsListener(this)
                .queryListener(this)
                .highlightColor(R.color.mentionAndLinks)
                .build()
        Thread {
            val cachedMentions: ArrayList<Mention>?
            if (ChatDatabase.getMentions(channelId) != null) {
                cachedMentions = ChatDatabase.getMentions(channelId)
                for (i in cachedMentions!!.iterator()) {
                    runOnUiThread {
                        mentionsList.add(i)
                    }
                }
            }
        }.start()
    }

    private fun compressAndSaveImageBitmap(
            message: String,
            extension: String,
            dimens: java.util.ArrayList<Int>,
            messageType: Int,
            fileDetails: FuguFileDetails?
    ) {
        try {
            CommonData.setTime(fileDetails?.muid)
            val image = fuguImageUtils?.compressAndSaveBitmap(
                    this@ChatActivity,
                    extension,
                    fileDetails?.fileName
            )
            if (image == null) {
                Toast.makeText(this@ChatActivity, "Could not read from source", Toast.LENGTH_LONG)
                        .show()
            } else {
                addMessageToList(
                        message,
                        messageType,
                        image,
                        image,
                        fileDetails,
                        fileDetails?.muid!!,
                        dimens,
                        null
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this@ChatActivity, "Could not read from source", Toast.LENGTH_LONG)
                    .show()
        }
    }

    private fun getImageHeightAndWidth(uri: Uri?): java.util.ArrayList<Int> {
        return try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(
                    File(
                            GeneralFunctions().getRealPathFromURI(
                                    uri,
                                    this@ChatActivity
                            )
                    ).absolutePath, options
            )
            val imageHeight = options.outHeight
            val imageWidth = options.outWidth
            val dimens = java.util.ArrayList<Int>()
            val exif = ExifInterface(
                    File(
                            GeneralFunctions().getRealPathFromURI(
                                    uri,
                                    this@ChatActivity
                            )
                    ).absolutePath
            )
            val rotation: Int = exif.getAttribute(ExifInterface.TAG_ORIENTATION)!!.toInt()
            if (rotation == 6 || rotation == 8) {
                dimens.add(imageWidth)
                dimens.add(imageHeight)
            } else {
                dimens.add(imageHeight)
                dimens.add(imageWidth)
            }
            dimens
        } catch (e: Exception) {
            java.util.ArrayList()
        }
    }

    private fun addMessageLinkToList(message: String, messageType: Int, imageUrl: String, thumbnailUrl: String, imageUrl100x100: String, sharableUrl: String, fileDetails: FuguFileDetails?, uuid: String, dimens: java.util.ArrayList<Int>, isSent: Boolean) {
        try {
            val localDate = DateUtils.getFormattedDate(Date())
            val messageObject = Message()
            messageObject.muid = uuid
            messageObject.isSent = true
            messageObject.fromName = userName
            messageObject.id = 0
            messageObject.userId = userId
            messageObject.sentAtUtc = DateUtils.getInstance().convertToUTC(localDate)
            messageObject.message = message
            val formattedStrings = FormatStringUtil.FormatString.getFormattedString(message)
            messageObject.alteredMessage = formattedStrings[0]
            messageObject.formattedMessage = formattedStrings[1]
            messageObject.messageStatus = MESSAGE_UNSENT
            messageObject.thumbnailUrl = thumbnailUrl
            messageObject.image_url = imageUrl
            messageObject.image_url_100x100 = imageUrl100x100
            messageObject.sharableImage_url = sharableUrl
            messageObject.sharableThumbnailUrl = sharableUrl
            messageObject.sharableImage_url_100x100 = sharableUrl
            messageObject.url = fileDetails?.filePath
            messageObject.messageType = messageType
            messageObject.email = com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.email
            val userReactions = UserReaction()
            messageObject.userReaction = userReactions
            messageObject.userImage = emptyString
            messageObject.threadMessage = false
            messageObject.userType = UserType.CUSTOMER
            messageObject.messageState = 1
            taggedUsers?.clear()
            if (mentionsList.size != 0) {
                taggedUsers = java.util.ArrayList()
                for (mention in mentionsList) {
                    if (!taggedUsers!!.contains(mention.userId.toInt())) {
                        taggedUsers!!.add(mention.userId.toInt())
                    }
                }
                mentionsList.clear()
                messageObject.taggedUsers = taggedUsers
            }
            if (dimens.size == 2) {
                messageObject.imageHeight = dimens[0]
                messageObject.imageWidth = dimens[1]
            }
            if (fileDetails != null) {
                messageObject.fileName = fileDetails.fileName
                messageObject.fileSize = fileDetails.fileSize
                messageObject.fileExtension = fileDetails.fileExtension
                messageObject.filePath = fileDetails.filePath
                messageObject.unsentFilePath = fileDetails.filePath
                messageObject.sentFilePath = fileDetails.filePath
                CommonData.setFileLocalPath(uuid, fileDetails.filePath)
                CommonData.setCachedFilePath(imageUrl, uuid, fileDetails.filePath)
                CommonData.setFilesMap(uuid, fileDetails.filePath, fileDetails.filePath)
            }
            messageObject.downloadStatus = 3
            messageObject.uploadStatus = 3

            if (!isSent) {
                messageObject.uploadStatus = 0
                messageObject.downloadStatus = 0
                messageObject.messageStatus = FuguAppConstant.MESSAGE_IMAGE_RETRY
            } else {
                messageObject.uploadStatus = 1
                messageObject.downloadStatus = 1
                messageObject.messageStatus = FuguAppConstant.MESSAGE_UNSENT
            }

            messageObject.messageIndex = diffUtilMessageList.size
            if (userId.compareTo(messageObject.userId) == 0) {
                when (messageObject.messageType) {
                    TEXT_MESSAGE -> messageObject.rowType = TEXT_MESSGAE_SELF
                    IMAGE_MESSAGE -> messageObject.rowType = IMAGE_MESSGAE_SELF
                    FILE_MESSAGE -> messageObject.rowType = FILE_MESSGAE_SELF
                    FuguAppConstant.PUBLIC_NOTE -> messageObject.rowType = PUBLIC_NOTE
                    VIDEO_MESSAGE -> messageObject.rowType = VIDEO_MESSGAE_SELF
                    VIDEO_CALL -> messageObject.rowType = VIDEO_CALL_SELF
                    POLL_MESSAGE -> messageObject.rowType = POLL_SELF
                }
            } else {
                when (messageObject.messageType) {
                    TEXT_MESSAGE -> messageObject.rowType = TEXT_MESSGAE_OTHER
                    IMAGE_MESSAGE -> messageObject.rowType = IMAGE_MESSGAE_OTHER
                    FILE_MESSAGE -> messageObject.rowType = FILE_MESSGAE_OTHER
                    FuguAppConstant.PUBLIC_NOTE -> messageObject.rowType = PUBLIC_NOTE
                    VIDEO_MESSAGE -> messageObject.rowType = VIDEO_MESSGAE_OTHER
                    VIDEO_CALL -> messageObject.rowType = VIDEO_CALL_OTHER
                    POLL_MESSAGE -> messageObject.rowType = POLL_OTHER
                }
            }
            diffUtilMessageList.add(messageObject)
//            messageList = ArrayList<Message>()
//            //messageList.addAll(diffUtilMessageList)
//            for(message in diffUtilMessageList){
//                var messageClone = message.clone()
//                messageList.add(messageClone as Message)
//            }
//            messageAdapter?.updateMessageList(messageList)
            runOnUiThread {
                messageList = ArrayList<Message>()
                //messageList.addAll(diffUtilMessageList)
                for (message in diffUtilMessageList) {
                    var messageClone = message.clone()
                    messageList.add(messageClone as Message)
                }
                messageAdapter?.updateMessageList(messageList)
                //messageAdapter?.notifyItemInserted(messageList.size - 1)
                rvMessages.scrollToPosition(diffUtilMessageList.size - 1)
            }
            messagesMap[uuid] = messageObject
            if (messageType == IMAGE_MESSAGE || messageType == FuguAppConstant.FILE_MESSAGE) {
                unsentMessageMap[uuid] = diffUtilMessageList[diffUtilMessageList.size - 1]
                ChatDatabase.setUnsentMessageMapByChannel(channelId, unsentMessageMap)
            }
            Thread {
                kotlin.run {
                    if (!isSearchedMessage) {
                        ChatDatabase.setMessageList(diffUtilMessageList, channelId)
                        ChatDatabase.setMessageMap(messagesMap, channelId)
                    }
                }
            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onProgressUpdate(percentage: Int, messageIndex: Int, muid: String?) {
        val mIntent = Intent(PROGRESS_INTENT)
        mIntent.putExtra("position", messageIndex)
        mIntent.putExtra("progress", percentage)
        mIntent.putExtra("muid", muid)
        mIntent.putExtra(
                "statusUpload",
                FuguAppConstant.UploadStatus.UPLOAD_IN_PROGRESS.uploadStatus
        )
        mIntent.putExtra(
                "statusDownload",
                FuguAppConstant.DownloadStatus.DOWNLOAD_FAILED.downloadStatus
        )
        LocalBroadcastManager.getInstance(this@ChatActivity).sendBroadcast(mIntent)
    }

    override fun onError(percentage: Int, messageIndex: Int, muid: String?) {
        val mIntent = Intent(PROGRESS_INTENT)
        mIntent.putExtra("position", messageIndex)
        mIntent.putExtra("progress", percentage)
        mIntent.putExtra("muid", muid)
        mIntent.putExtra("statusUpload", FuguAppConstant.UploadStatus.UPLOAD_FAILED.uploadStatus)
        mIntent.putExtra(
                "statusDownload",
                FuguAppConstant.DownloadStatus.DOWNLOAD_FAILED.downloadStatus
        )
        LocalBroadcastManager.getInstance(this@ChatActivity).sendBroadcast(mIntent)
    }

    override fun onFinish(percentage: Int, messageIndex: Int, muid: String?) {
        val mIntent = Intent(PROGRESS_INTENT)
        mIntent.putExtra("position", messageIndex)
        mIntent.putExtra("progress", percentage)
        mIntent.putExtra("muid", muid)
        mIntent.putExtra("statusUpload", FuguAppConstant.UploadStatus.UPLOAD_COMPLETED.uploadStatus)
        mIntent.putExtra(
                "statusDownload",
                FuguAppConstant.DownloadStatus.DOWNLOAD_FAILED.downloadStatus
        )
        LocalBroadcastManager.getInstance(this@ChatActivity).sendBroadcast(mIntent)
    }

    @SuppressLint("Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CALENDAR_AUTHORIZATION -> {
                    if (resultCode == Activity.RESULT_OK)
                        try {
                            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                            val account = task.getResult(ApiException::class.java)
                            account?.let { googleSignInAccount ->
                                onCalendarAuthorizationGranted(googleSignInAccount.serverAuthCode
                                        ?: "", object : CalendarLinkingCallback {
                                    override fun onAuthorizationSuccess() {
                                        if (chatType == ChatType.O2O) {
                                            makeHangoutsCall()
                                        } else {
                                            userIdsSearch = ArrayList()
                                            multiMemberAddGroupMap = LinkedHashMap()
                                            val membersToBeSent = ArrayList<Member>()
                                            membersToBeSent.addAll(membersList)
                                            val newFragment = VideoCallInvitationBottomSheetFragment.newInstance(
                                                    0,
                                                    this@ChatActivity,
                                                    membersToBeSent,
                                                    channelId,
                                                    isHangoutsMeet = true
                                            )
                                            newFragment.show(supportFragmentManager, "VideoCallInvitationBottomSheetFragment")
                                        }
                                    }
                                })
                            }
                            mGoogleSignInClient?.signOut()
                        } catch (e: ApiException) {
                            Log.i("GoogleAuthorization", "GoogleAuth:failed code=" + e.statusCode)
                            startActivity(Intent((this@ChatActivity), AuthorizeGoogleActivity::class.java))
                            (this@ChatActivity).overridePendingTransition(R.anim.right_in, R.anim.left_out)
                        }
                    else {
                        onCalendarAuthorizationCanceled("Activity Result Cancelled")
                    }
                }
                1090 -> {
                    openOldVideoCallActivity(mCallType)
                }
                FuguAppConstant.OPEN_CAMERA_ADD_IMAGE -> {
                    val fileName = workspaceInfoList[currentPosition].workspaceName.replace(
                            " ".toRegex(),
                            ""
                    ).replace("'s".toRegex(), "") + "_" + CommonData.getTime() + ".jpg"
                    fileToBeDelete = File(
                            fuguImageUtils?.getDirectory(FuguAppConstant.FileType.IMAGE_FILE),
                            fileName
                    )
                    val uri = Uri.fromFile(fileToBeDelete)
                    val dimens = getImageHeightAndWidth(uri)
                    val localDate = DateUtils.getFormattedDate(Date())
                    val date = DateUtils.getDate(localDate)
                    val fileDetails =
                            fuguImageUtils?.saveFile(uri, FILE_TYPE_MAP["jpg"], channelId, localDate)

                    showImageWithMessageDialog("jpg", dimens, uri, fileDetails)
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    if (resultCode == Activity.RESULT_OK) {
                        val resultUri = result.uri
                        val localDate = DateUtils.getFormattedDate(Date())
                        mPhotoEditorView?.source?.setImageURI(resultUri)
                    }
                }
                1004 -> {
                    PushReceiver.PushChannel.pushChannelId = channelId
                    llBotAction?.visibility = View.GONE
                    try {
                        val mediaStorageDir = File(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                FuguAppConstant.ATTENDANCE
                        )
                        val finalFile = File("${mediaStorageDir.path}${File.separator}IMG_FUGU.jpg")
                        apiVerifyAttendanceCredentials(finalFile)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
                1005 -> {
                    PushReceiver.PushChannel.pushChannelId = channelId
                    llBotAction?.visibility = View.GONE
                    try {
                        val mediaStorageDir = File(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                FuguAppConstant.ATTENDANCE
                        )
                        val finalFile = File("${mediaStorageDir.path}${File.separator}IMG_FUGU.jpg")
                        apiSetDefaultImage(finalFile)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }

                }
                1006 -> {
                    PushReceiver.PushChannel.pushChannelId = channelId
                    val locationFetcher = LocationFetcher()
                    locationFetcher.init(this@ChatActivity)
                    if (action.equals(AttendanceAuthenticationLevel.CAMERA.toString()) || action.equals(
                                    AttendanceAuthenticationLevel.BOTH.toString()
                            )
                    ) {
                        Handler().postDelayed({
                            val takePictureIntent = Intent(this, CameraActivity::class.java)
                            startActivityForResult(takePictureIntent, 1004)
                        }, 0)
                    }
                }
                REQUEST_CODE_PICK_IMAGE -> {
                    try {
                        val list: ArrayList<ImageFile> =
                                data!!.getParcelableArrayListExtra(RESULT_PICK_IMAGE)!!
                        var extension = ""
                        val cursor = this@ChatActivity.contentResolver.query(
                                getUriFromPath(list.get(0).path),
                                null,
                                null,
                                null,
                                null
                        )
                        cursor.use { cursor ->
                            if (cursor != null && cursor.moveToFirst()) {
                                try {
                                    val splitArray =
                                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                                                    .split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                                                    .toTypedArray()
                                    extension = splitArray[splitArray.size - 1].toLowerCase()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                        if (extension.equals("png")) {
                            extension = "jpg"
                            val bitmap = BitmapFactory.decodeFile(list.get(0).path)
                            val inputStream = contentResolver.openInputStream(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", File(list.get(0).path)))!!
                            val fileOutputStream = FileOutputStream(File(list.get(0).path))
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
                        val dimensGallery = getImageHeightAndWidth(getUriFromPath(list.get(0).path))
                        val localDate = DateUtils.getFormattedDate(Date())
                        val fileDetails = fuguImageUtils?.saveFile(
                                getUriFromPath(list.get(0).path),
                                FILE_TYPE_MAP[extension],
                                channelId,
                                localDate
                        )
                        if (list.size > 1) {
                            val intent =
                                    Intent(this@ChatActivity, MultipleImageDisplayActivity::class.java)
                            intent.putExtra(RESULT_PICK_IMAGE, list)
                            startActivityForResult(intent, REQUEST_MULTIPLE_IMAGES)
                        } else {
                            if (extension.equals("gif")) {
                                showImageDialog(
                                        getUriFromPath(list.get(0).path),
                                        getUriFromPath(list.get(0).path),
                                        this@ChatActivity,
                                        getUriFromPath(list.get(0).path).toString(),
                                        dimensGallery,
                                        fileDetails,
                                        list.get(0).path
                                )
                            } else {
                                showImageWithMessageDialog(
                                        extension,
                                        dimensGallery,
                                        getUriFromPath(list.get(0).path),
                                        fileDetails
                                )
                            }
                        }

                    } catch (e: java.lang.Exception) {
                        showErrorMessage("Something went wrong!")
                    }
                }
                REQUEST_CODE_PICK_VIDEO -> {
                    val list: ArrayList<VideoFile> =
                            data!!.getParcelableArrayListExtra(RESULT_PICK_VIDEO)!!
                    var extension = ""
                    val cursor = this@ChatActivity.contentResolver.query(
                            getUriFromPath(list.get(0).path),
                            null,
                            null,
                            null,
                            null
                    )
                    cursor.use { cursor ->
                        if (cursor != null && cursor.moveToFirst()) {
                            try {
                                val splitArray =
                                        cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                                                .split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                                                .toTypedArray()
                                extension = splitArray[splitArray.size - 1].toLowerCase()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    val dimensGallery = getImageHeightAndWidth(getUriFromPath(list.get(0).path))
                    val localDate = DateUtils.getFormattedDate(Date())
                    val fileDetails = fuguImageUtils?.saveFile(
                            getUriFromPath(list.get(0).path),
                            FILE_TYPE_MAP[extension],
                            channelId,
                            localDate
                    )
                    try {
                        if (fileDetails?.filePath?.isEmpty()!!) {
                            Toast.makeText(this, "File not found...", Toast.LENGTH_LONG).show()
                        } else {
                            if (fileDetails.fileSize.contains("KB") || fileDetails.fileSize.contains(
                                            "Bytes"
                                    ) || java.lang.Double.parseDouble(fileDetails.fileSizeDouble) * 1024 * 1024 <= maxUploadSize
                            ) {
                                if (supportedFormats.contains(fileDetails.fileExtension.toLowerCase())) {
                                    if (GeneralFunctions().getMimeType(fileDetails.filePath, this@ChatActivity) == null) {
                                        showErrorMessage("File Corrupted")
                                        return
                                    } else {
                                        showVideoWithMessageDialog(extension, dimensGallery, getUriFromPath(list.get(0).path), fileDetails)
                                    }
                                } else {
                                    showErrorMessage("Please send document file only.")
                                }
                            } else {
                                showErrorMessage("File size cannot be greater than " + (((maxUploadSize) / 1024) / 1024).toInt() + "MB.")
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, "Video not available on device", Toast.LENGTH_LONG)
                                .show()
                    }
                }
                REQUEST_CODE_PICK_FILE -> {
                    val list: ArrayList<NormalFile> =
                            data!!.getParcelableArrayListExtra(RESULT_PICK_FILE)!!
                    var extension = ""
                    if (!getUriFromPath(list.get(0).path).toString().contains("com.google")) {
                        val cursor = this@ChatActivity.contentResolver.query(
                                getUriFromPath(list.get(0).path),
                                null,
                                null,
                                null,
                                null
                        )
                        try {
                            if (cursor != null && cursor.moveToFirst()) {
                                try {
                                    val splitArray =
                                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                                                    .split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                                                    .toTypedArray()
                                    extension = splitArray[splitArray.size - 1].toLowerCase()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        } finally {
                            cursor!!.close()
                        }
                        val localDate = DateUtils.getFormattedDate(Date())
                        if (FILE_TYPE_MAP[extension] == null) {
                            extension = "default"
                        }
                        val fileDetails = fuguImageUtils?.saveFile(
                                getUriFromPath(list.get(0).path),
                                FILE_TYPE_MAP[extension],
                                channelId,
                                localDate
                        )
                        try {
                            if (fileDetails?.filePath?.isEmpty()!!) {
                                Toast.makeText(this, "File not found...", Toast.LENGTH_LONG).show()
                            } else {
                                if (fileDetails.fileSize.contains("KB") || fileDetails.fileSize.contains(
                                                "Bytes"
                                        ) || java.lang.Double.parseDouble(fileDetails.fileSizeDouble) * 1024 * 1024 <= maxUploadSize
                                ) {
                                    addMessageToList(
                                            getString(R.string.fugu_empty),
                                            FuguAppConstant.FILE_MESSAGE,
                                            fileDetails.filePath,
                                            "",
                                            fileDetails,
                                            UUID.randomUUID().toString(),
                                            java.util.ArrayList(),
                                            null
                                    )
                                } else {
                                    showErrorMessage("File size cannot be greater than " + (((maxUploadSize) / 1024) / 1024).toInt() + "MB.")
                                }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this, "File not available on device", Toast.LENGTH_LONG)
                                    .show()
                        }

                    } else run {
                        showErrorMessage("Drive files not supported yet.")
                    }
                }
                REQUEST_CODE_PICK_AUDIO -> {
                    val list: ArrayList<AudioFile> =
                            data!!.getParcelableArrayListExtra(RESULT_PICK_AUDIO)!!
                    var extension = ""
                    if (!getUriFromPath(list.get(0).path).toString().contains("com.google")) {
                        val cursor = this@ChatActivity.contentResolver.query(
                                getUriFromPath(list.get(0).path),
                                null,
                                null,
                                null,
                                null
                        )
                        try {
                            if (cursor != null && cursor.moveToFirst()) {
                                try {
                                    val splitArray =
                                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                                                    .split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                                                    .toTypedArray()
                                    extension = splitArray[splitArray.size - 1].toLowerCase()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        } finally {
                            cursor!!.close()
                        }
                        val localDate = DateUtils.getFormattedDate(Date())
                        if (FILE_TYPE_MAP[extension] == null) {
                            extension = "default"
                        }

                        val fileDetails = fuguImageUtils?.saveFile(
                                getUriFromPath(list.get(0).path),
                                FILE_TYPE_MAP[extension],
                                channelId,
                                localDate
                        )
                        try {
                            if (fileDetails?.filePath?.isEmpty()!!) {
                                Toast.makeText(this, "File not found...", Toast.LENGTH_LONG).show()
                            } else {
                                if (fileDetails.fileSize.contains("KB") || fileDetails.fileSize.contains(
                                                "Bytes"
                                        ) || java.lang.Double.parseDouble(fileDetails.fileSizeDouble) * 1024 * 1024 <= maxUploadSize
                                ) {
                                    addMessageToList(
                                            getString(R.string.fugu_empty),
                                            FuguAppConstant.FILE_MESSAGE,
                                            fileDetails.filePath,
                                            "",
                                            fileDetails,
                                            UUID.randomUUID().toString(),
                                            java.util.ArrayList(),
                                            null
                                    )
                                } else {
                                    showErrorMessage("File size cannot be greater than " + (((maxUploadSize) / 1024) / 1024).toInt() + "MB.")
                                }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this, "Audio not available on device", Toast.LENGTH_LONG)
                                    .show()
                        }

                    } else run {
                        showErrorMessage("Drive files not supported yet.")
                    }
                }
                FuguAppConstant.START_POLL -> {
                    if (isNetworkConnected) {
                        val pollJson = JSONObject()
                        pollJson.put(QUESTION, data?.getStringExtra(QUESTION))
                        pollJson.put(
                                IS_MULTIPLE_SELECT,
                                data?.getBooleanExtra(IS_MULTIPLE_SELECT, false)
                        )
                        pollJson.put(EXPIRY_TIME, data?.getLongExtra(EXPIRY_TIME, 604800L))
                        pollJson.put(MESSAGE_TYPE, POLL_MESSAGE)
                        pollJson.put(CHANNEL_ID, channelId)
                        val pollOptionsList = data?.getStringArrayListExtra(POLL_OPTIONS)
                        val pollOptionsArray = JSONArray()
                        for (option in pollOptionsList!!) {
                            val pollOptionsObject = JSONObject()
                            pollOptionsObject.put("puid", UUID.randomUUID().toString())
                            pollOptionsObject.put("label", option)
                            pollOptionsArray.put(pollOptionsObject)
                        }
                        pollJson.put(POLL_OPTIONS, pollOptionsArray)
                        val devicePayload = JSONObject()
                        devicePayload.put(
                                FuguAppConstant.DEVICE_ID,
                                UniqueIMEIID.getUniqueIMEIId(this@ChatActivity)
                        )
                        devicePayload.put(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER)
                        devicePayload.put(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME)
                        devicePayload.put(
                                FuguAppConstant.DEVICE_DETAILS,
                                CommonData.deviceDetails(this@ChatActivity)
                        )
                        pollJson.put("device_payload", devicePayload)
                        pollJson.put(IS_TYPING, TYPING_SHOW_MESSAGE)
                        val globalUuid = UUID.randomUUID().toString()
                        pollJson.put(MESSAGE_UNIQUE_ID, globalUuid)
                        pollJson.put(USER_ID, userId)
                        pollJson.put(MESSAGE, emptyString)
                        addMessageToListAndPublishOnFaye(
                                "",
                                POLL_MESSAGE,
                                "",
                                "",
                                null,
                                globalUuid,
                                pollJson
                        )
                        SocketConnection.sendMessage(pollJson)
                    } else {
                        showErrorMessage("No Internet Connection!")
                    }
                }
                112 -> {
                    pageSize = 1
                    if (isNetworkConnected) {
                        apiGetMessages(false)
                    }
                }
                REQUEST_GROUP_INFO -> {
                    if (data != null && data.hasExtra(FuguAppConstant.GROUP_SIZE_CHANGED)) {
                        apiFetchMembers()
                    } else {
                        finish()
                    }
                    if (data != null && data.hasExtra("tvGroupName")) {
                        GeneralFunctions().setToolBarText(
                                data.getStringExtra("tvGroupName"),
                                tvTitle,
                                chatType,
                                leaveType!!
                        )
                    }
                }
                20022 -> {

                    if (data!!.hasExtra("only_admin_can_message")) {
                        if (!data.getBooleanExtra("only_admin_can_message", false)) {
                            llCannotReply.visibility = View.GONE
                        } else if (data.getBooleanExtra("only_admin_can_message", false) && data.getStringExtra("userRole").equals("ADMIN")) {
                            llCannotReply.visibility = View.GONE
                        } else {
                            llCannotReply.visibility = View.VISIBLE
                        }
                    }

                    isScreenOpenedFromThread = true
                    if (data.getStringExtra(Intent.EXTRA_TEXT) == FuguAppConstant.CLEAR_INTENT) {
                        diffUtilMessageList = ArrayList()
                        object : Thread() {
                            override fun run() {
                                super.run()
                                if (!isSearchedMessage) {
                                    ChatDatabase.setMessageList(java.util.ArrayList(), channelId)
                                }
                                runOnUiThread {
                                    messageList = ArrayList<Message>()
                                    //messageList.addAll(diffUtilMessageList)
                                    for (message in diffUtilMessageList) {
                                        var messageClone = message.clone()
                                        messageList.add(messageClone as Message)
                                    }
                                    messageAdapter?.updateMessageList(messageList)
                                    //messageAdapter?.notifyDataSetChanged()
                                }
                            }
                        }.start()


                    } else {

                        runOnUiThread {
                            if (diffUtilMessageList.size != (data.getSerializableExtra("BUNDLE") as java.util.ArrayList<Message>).size) {
                                val diff = (data.getSerializableExtra("BUNDLE") as java.util.ArrayList<Message>).size - diffUtilMessageList.size
                                rlScrollBottom.visibility = View.VISIBLE
                                tvUnread.text = (scrollBottomCount + diff).toString()
                                tvUnread.visibility = android.view.View.VISIBLE
                            }
                        }

                        diffUtilMessageList = ArrayList()
                        diffUtilMessageList.addAll(data.getSerializableExtra("BUNDLE") as java.util.ArrayList<Message>)

                        for (i in diffUtilMessageList.size - 1 downTo 0) {
                            if (diffUtilMessageList[i].muid == data.getStringExtra("threadMuid")) {
                                diffUtilMessageList[i].progressUpdate = true
                            }
                        }
                        for (message in diffUtilMessageList) {
                            messagesMap[message.muid] = message
                        }
                        messageList = ArrayList<Message>()
                        //messageList.addAll(diffUtilMessageList)
                        for (message in diffUtilMessageList) {
                            val messageClone = message.clone()
                            messageList.add(messageClone as Message)
                        }
                        messageAdapter?.updateMessageList(messageList)
                        //messageAdapter?.notifyDataSetChanged()
                        Thread {
                            kotlin.run {
                                if (!isSearchedMessage) {
                                    ChatDatabase.setMessageList(diffUtilMessageList, channelId)
                                    ChatDatabase.setMessageMap(messagesMap, channelId)
                                }
//                                for (i in messageList.indices) {
//                                    if (messageList[i].threadMessage) {
//                                        threadMessagesMap[messageList[i].muid] = messageList[i].threadMessageCount
//                                    }
//                                }
//                                if (!isSearchedMessage) {
//                                    ChatDatabase.setThreadMap(threadMessagesMap, channelId)
//                                }
                            }
                        }.start()
                    }
                    pageSize = 1
                    if (positionToBeInserted > 0 && diffUtilMessageList.get(positionToBeInserted).rowType == UNREAD_ITEM) {
                        diffUtilMessageList.removeAt(positionToBeInserted)
                        for (i in diffUtilMessageList.indices) {
                            diffUtilMessageList[i].messageIndex = i
                            val message = messagesMap[diffUtilMessageList[i].muid]
                            message?.messageIndex = i
                            messagesMap[diffUtilMessageList[i].muid] = message!!
                        }
                        positionToBeInserted = 0
                        Thread {
                            kotlin.run {
                                if (!isSearchedMessage) {
                                    ChatDatabase.setMessageList(diffUtilMessageList, channelId)
                                }
                            }
                        }.start()
                    }
                    object : Thread() {
                        override fun run() {
                            super.run()
                            val conversations =
                                    CommonData.getConversationList(appSecretKey) as java.util.LinkedHashMap<Long, FuguConversation>
                            conversations[channelId]?.unreadCount = 0
                            CommonData.setConversationList(appSecretKey, conversations)
                        }
                    }.start()
                }
                REQUEST_MULTIPLE_IMAGES -> {
                    val list: ArrayList<ImageFile> =
                            data!!.getParcelableArrayListExtra(RESULT_PICK_IMAGE)!!
                    for (image in list) {
                        sendImageFile(image)
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED && requestCode == 1004) {
            pbVerify?.visibility = View.GONE
            ivSend.visibility = View.VISIBLE
        }
    }

    private fun apiVerifyAttendanceCredentials(finalFile: File?) {
        ApiVerifyAttendanceCredentials().apiVerifyAttendanceCredentials(finalFile,
                workspaceInfoList,
                currentPosition,
                this@ChatActivity,
                channelId,
                isMockLocation,
                lat,
                lon,
                action,
                GeneralFunctions().getTaggedMessage(mentionsList, etMessage),
                conversation.otherUserType == UserType.HRM_BOT,
                object : ApiVerifyAttendanceCredentials.VerifyAttendanceCallback {
                    override fun onMockLocationDetected() {
                        val snackbar = Snackbar
                                .make(ivSend, "Please turn off your mock location", Snackbar.LENGTH_LONG)
                        snackbar.show()
                        pbVerify?.visibility = View.GONE
                        ivSend.visibility = View.VISIBLE
                    }

                    override fun onSuccess(t: CommonResponse?) {
                        pbVerify?.visibility = View.GONE
                        ivSend.visibility = View.VISIBLE
                        etMessage.setText("")
                    }

                    override fun onFailure(error: APIError?) {
                        pbVerify?.visibility = View.GONE
                        ivSend.visibility = View.VISIBLE
                        etMessage.setText("")
                        Thread {
                            kotlin.run {
                                try {
                                    val fcCommonresponse =
                                            com.skeleton.mvp.data.db.CommonData.getCommonResponse()
                                    val workspacesInfoListNew = fcCommonresponse.data.workspacesInfo


                                    workspacesInfoListNew[currentPosition].userAttendanceConfig = UserAttendanceConfig(error?.jsonObject?.getJSONObject("data")
                                            ?.getJSONObject("user_attendance_config")
                                            ?.getString("punch_in_permission"), error?.jsonObject?.getJSONObject("data")
                                            ?.getJSONObject("user_attendance_config")
                                            ?.getString("punch_out_permission"))
//                                    workspacesInfoListNew[currentPosition].userAttendanceConfig.attendanceIn =
//                                            error?.jsonObject?.getJSONObject("data")
//                                                    ?.getJSONObject("user_attendance_config")
//                                                    ?.getString("punch_in_permission")
//                                    workspacesInfoListNew[currentPosition].userAttendanceConfig.attendanceOut =
//                                            error?.jsonObject?.getJSONObject("data")
//                                                    ?.getJSONObject("user_attendance_config")
//                                                    ?.getString("punch_out_permission")
                                    workspaceInfoList =
                                            (workspacesInfoListNew as ArrayList<WorkspacesInfo>?)!!
                                    fcCommonresponse.data.workspacesInfo = workspacesInfoListNew
                                    com.skeleton.mvp.data.db.CommonData.setCommonResponse(
                                            fcCommonresponse
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }.start()
                    }
                })
    }

    private fun apiSetDefaultImage(finalFile: File) {
        ApiSetDefaultImage().apiSetDefaultImage(finalFile,
                workspaceInfoList,
                currentPosition,
                this@ChatActivity,
                cameraMuid!!,
                channelId,
                object : ApiSetDefaultImage.DefaultImageCallback {
                    override fun onSuccess(t: CommonResponse?) {
                        val message = messagesMap[cameraMuid!!]!!
                        message.customActions!![0].isActionTaken = true
                        messagesMap[cameraMuid!!] = message
                        diffUtilMessageList[message.messageIndex] = message
                        messageList = ArrayList<Message>()
                        //messageList.addAll(diffUtilMessageList)
                        for (message in diffUtilMessageList) {
                            var messageClone = message.clone()
                            messageList.add(messageClone as Message)
                        }
                        messageAdapter?.updateMessageList(messageList)
                        // messageAdapter?.notifyDataSetChanged()
                        pbVerify?.visibility = View.GONE
                        ivSend.visibility = View.VISIBLE
                        etMessage.setText("")
                    }

                    override fun onFailure(error: APIError?) {
                        pbVerify?.visibility = View.GONE
                        ivSend.visibility = View.VISIBLE
                        etMessage.setText("")
                    }
                })
    }


    fun getUriFromPath(path: String): Uri {
        return FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", File(path))
    }

    fun showVideoWithMessageDialog(
            extension: String,
            dimensGallery: java.util.ArrayList<Int>,
            uri: Uri?,
            fileDetails: FuguFileDetails?
    ) {
        val messageWithImageDialog = Dialog(this@ChatActivity, R.style.AppCompatAlertDialogStyle)
        messageWithImageDialog.setContentView(R.layout.dialog_video_file)
        videoProgress = messageWithImageDialog.findViewById(R.id.videoProgress)
        ivSendVideo = messageWithImageDialog.findViewById(R.id.ivSend)
        val etMsg: EmojiGifEditText = messageWithImageDialog.findViewById(R.id.etMsg)
        val dialogRecyclerView: RecyclerView = messageWithImageDialog.findViewById(R.id.rv_mentions)
        val lp = messageWithImageDialog.window!!.attributes
        val llImageWithMessgae: LinearLayoutCompat =
                messageWithImageDialog.findViewById(R.id.llMessageLayout)
        when (chatType) {
            3, 4, 5, 6 -> {
                setUpMentionsForImage(etMsg)
                setUpMentionsListForImage(dialogRecyclerView)
                imageRecycler = dialogRecyclerView
                imageLlMentions = messageWithImageDialog.findViewById(R.id.ll_mentions_layout)
                imageRecycler!!.visibility = View.VISIBLE
                imageLlMentions!!.visibility = View.VISIBLE
                etMsg.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                    }

                    override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                    ) {
                    }

                    override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                    ) {
                        imageText = s.toString()
                    }
                })
            }
        }
        ivSendVideo?.alpha = 1f
        llImageWithMessgae.alpha = 0.7f
        messageWithImageDialog.window!!.attributes = lp
        messageWithImageDialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        messageWithImageDialog.setCancelable(true)
        messageWithImageDialog.setCanceledOnTouchOutside(false)
        val mVideoView: VideoView
        val mediaControlled = MediaController(this@ChatActivity)
        mVideoView = messageWithImageDialog.findViewById(R.id.vMsg)
        mediaControlled.setAnchorView(mVideoView)
        mediaControlled.setMediaPlayer(mVideoView)
        mediaControlled.isEnabled = true
        mVideoView.setVideoURI(Uri.parse(fileDetails?.filePath!!))
        mVideoView.setZOrderOnTop(true)
        mVideoView.setOnPreparedListener { mVideoView.start() }
        mVideoView.setOnErrorListener { mp, what, extra ->
            showErrorMessage("File type not supported!", BaseView.OnErrorHandleCallback {
            })
            true
        }
        messageWithImageDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        messageWithImageDialog.show()
        ivSendVideo?.setOnClickListener {
            if (isApiGetMessagesCompleted) {
                val message: String = GeneralFunctions().getTaggedMessage(mentionsList, etMsg)
                compressAndSaveImageBitmap(
                        message,
                        extension,
                        dimensGallery,
                        FuguAppConstant.VIDEO_MESSAGE,
                        fileDetails
                )
                messageWithImageDialog.dismiss()
            } else {
                ivSendVideo?.visibility = View.GONE
                videoProgress?.visibility = View.VISIBLE
                Toast.makeText(
                        this@ChatActivity,
                        "Slow Internet Connection! Please wait...",
                        Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    override fun onBackPressed() {
        chatHandler = null
        SocketConnection.unsubscribeChannel(channelId)
        SocketConnection.unsubscribe(otherUserid)
        if (isMessageInEditMode) {
            cancelEditing()
        } else {
            val intent = Intent()
            val conversationMap = ChatDatabase.getConversationMap(appSecretKey)
            val conversationToBeStored = conversationMap[channelId]
            if (diffUtilMessageList.size > 0 && conversation.joined) {
                Thread {
                    kotlin.run {
                        if (conversationToBeStored != null) {
                            for (message in diffUtilMessageList.reversed()) {
                                if (message.messageStatus == MESSAGE_SENT || message.messageStatus == MESSAGE_READ || message.messageStatus == MESSAGE_DELIVERED) {
                                    conversationToBeStored.message = message.message
                                    conversationToBeStored.dateTime = message.sentAtUtc
                                    conversationToBeStored.last_sent_by_id = message.userId
                                    conversationToBeStored.last_sent_by_full_name = message.fromName
                                    conversationToBeStored.message_type = message.messageType
                                    conversationToBeStored.muid = message.muid
                                    conversationToBeStored.chat_type = chatType
                                    conversationToBeStored.unreadCount = 0
                                    conversationToBeStored.last_message_status =
                                            message.messageStatus
                                    conversationToBeStored.messageState = message.messageState
                                    if (conversation.membersInfo != null && conversation.membersInfo.size > 0) {
                                        conversationToBeStored.membersInfo =
                                                conversation.membersInfo
                                    }
                                    conversationMap[channelId] = conversationToBeStored
                                    break
                                }
                            }
                        }
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                }.start()

            } else {
                try {
                    conversationMap.remove(channelId)
                    setResult(RESULT_CANCELED, intent)
                    finish()
                } catch (e: Exception) {
                    setResult(RESULT_CANCELED, intent)
                    finish()
                }
            }
            ChatDatabase.setConversationMap(conversationMap, appSecretKey)

            val view = this@ChatActivity.currentFocus
            if (view != null) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            PushReceiver.PushChannel.pushChannelId = -2L
            try {
                if (positionToBeInserted > 0 && diffUtilMessageList.get(positionToBeInserted).rowType == UNREAD_ITEM) {
                    diffUtilMessageList.removeAt(positionToBeInserted)
                    for (i in diffUtilMessageList.indices) {
                        diffUtilMessageList[i].messageIndex = i
                        val message = messagesMap[diffUtilMessageList[i].muid]
                        message?.messageIndex = i
                        messagesMap[diffUtilMessageList[i].muid] = message!!
                    }
                    Thread {
                        kotlin.run {
                            if (!isSearchedMessage) {
                                ChatDatabase.setMessageList(diffUtilMessageList, channelId)
                            }
                        }
                    }.start()
                }
            } catch (e: Exception) {
            }
            unregisterReceivers()
            if (fileShared || conversation.labelId.compareTo(12345) == 0) {
                val i = baseContext.packageManager
                        .getLaunchIntentForPackage(baseContext.packageName)
                i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                i.putExtra("open_home", "open_home")
                startActivity(i)
            }
        }
    }

    fun openBottomSheet(muid: String, isSelf: Boolean, location: IntArray) {
        try {
            val messageObject = messagesMap[muid]
            if (messageObject?.messageType == 15) {
                messageObject.messageStatus = MESSAGE_SENT
            }
            val view = this@ChatActivity.currentFocus
            if (view != null) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            var downloadStatus = ""
            if (messageObject?.messageType == IMAGE_MESSAGE) {
                var extension =
                        messageObject.image_url!!.split(".")[messageObject.image_url.split(".").size - 1]
                if (extension.toLowerCase().equals("png")) {
                    extension = "jpg"
                }
                val fileName = messageObject.fileName + "_" + messageObject.muid + "." + extension
                val filePath = File(
                        GeneralFunctions().getDirectory(
                                extension,
                                workspaceInfoList,
                                currentPosition
                        ) + "/" + fileName
                )
                downloadStatus = if (filePath.exists()) {
                    DownloadStatus.DOWNLOAD_COMPLETED.toString()
                } else {
                    DownloadStatus.DOWNLOAD_FAILED.toString()
                }
            } else if (messageObject?.messageType == VIDEO_MESSAGE || messageObject?.messageType == FILE_MESSAGE) {
                downloadStatus = if (!TextUtils.isEmpty(
                                CommonData.getCachedFilePath(
                                        messageObject.url,
                                        messageObject.muid
                                )
                        )
                ) {
                    DownloadStatus.DOWNLOAD_COMPLETED.toString()
                } else {
                    DownloadStatus.DOWNLOAD_FAILED.toString()
                }
            }
            bottomSheetFragment = BottomSheetFragment.newInstance(
                    0,
                    messageObject?.messageIndex!!,
                    muid,
                    messageObject.messageType,
                    isSelf,
                    messageObject.messageStatus,
                    messageObject.sentAtUtc,
                    messageObject.isStarred,
                    location,
                    downloadStatus,
                    chatType
            )
            bottomSheetFragment?.show(supportFragmentManager, "BottomSheetFragment")
        } catch (e: Exception) {
        }
    }

    fun initClickedEmojiMuid(muid: String) {
        bottomSheetFragment?.dismiss()
        bottomSheetFragment = null
        if (!TextUtils.isEmpty(muid)) {
            clickedEmojiMuid = muid
        }
    }

    fun copyText(position: Int) {
        try {
            messageAdapter?.setOnLongClickValue(false)
            GeneralFunctions().copyMessage(this@ChatActivity, diffUtilMessageList, position)
        } catch (e: java.lang.Exception) {
            messageAdapter?.setOnLongClickValue(false)
            e.printStackTrace()
        }
    }

    fun copyUrl(url: String) {
        GeneralFunctions().copyUrl(url, this@ChatActivity)
    }

    fun setOnLongClickValue() {
        messageAdapter?.setOnLongClickValue(false)
    }

    fun editText(position: Int) {
        var pos = position
        if (pos > diffUtilMessageList.size - 1) {
            pos = diffUtilMessageList.size - 1
        }
        messageAdapter?.setOnLongClickValue(false)
        if (isMessageInEditMode) {
            val message = messagesMap[firstEditMuid]
            message?.editMode = false
            messagesMap[diffUtilMessageList[message!!.messageIndex].muid] = message
            diffUtilMessageList[message.messageIndex] = message
            messageList = ArrayList<Message>()
            //messageList.addAll(diffUtilMessageList)
            for (message in diffUtilMessageList) {
                val messageClone = message.clone()
                messageList.add(messageClone as Message)
            }
            messageAdapter?.updateMessageList(messageList)
            //messageAdapter?.notifyItemChanged(message.messageIndex)
            firstEditMuid = ""
        }
        KeyboardUtil.toggleKeyboardVisibility(this@ChatActivity)
        isMessageInEditMode = true
        taggedUsers = ArrayList()
        etMessage.setText(Html.fromHtml((diffUtilMessageList[pos].message.replace("\n", "<br>"))))
        etMessage.setSelection(etMessage.text!!.length)
        Handler().postDelayed({
            val s = diffUtilMessageList[pos].message
            val matcher = Pattern.compile("href=\"mention://(.*?)\"").matcher(s)
            val matcherName = Pattern.compile("class=\"tagged-agent\">(.*?)</a>").matcher(s)

            val taggedList = ArrayList<Mention>()
            while (matcher.find() && matcherName.find()) {
                val mention = Mention()
                mention.userId = matcher.group(1).toLong()
                mention.mentionName = matcherName.group(1).toString()
                var isAlreadyTagged = false
                for (oldMention in taggedList) {
                    if (oldMention.userId.compareTo(mention.userId) == 0) {
                        isAlreadyTagged = true
                        break
                    }
                }
                if (!isAlreadyTagged) {
                    Log.e("Tag ", matcher.group(1).toString())
                    Log.e("Tag ", matcherName.group(1).toString())
                    taggedList.add(mention)
                }

            }
            mentionsList.addAll(taggedList)
        }, 0)
        val message = diffUtilMessageList[pos]
        message.editMode = true
        messagesMap[diffUtilMessageList[pos].muid] = message
        diffUtilMessageList[pos] = message
        messageList = ArrayList<Message>()
        //messageList.addAll(diffUtilMessageList)
        for (message in diffUtilMessageList) {
            val messageClone = message.clone()
            messageList.add(messageClone as Message)
        }
        messageAdapter?.updateMessageList(messageList)
        //messageAdapter?.notifyItemChanged(pos)
        ivSend.setImageResource(R.drawable.success)
        ivAttachment.visibility = View.GONE
        ivCancelEdit.visibility = View.VISIBLE
        firstEditMuid = diffUtilMessageList[pos].muid
    }

    fun openDialog(muid: String) {
        messageAdapter?.setOnLongClickValue(false)
        if (!TextUtils.isEmpty(muid)) {
            clickedEmojiMuid = muid
        }
        EmojiFragment().show(supportFragmentManager, "EmojiFragment")
    }

    fun openReactionDialog(muid: String, location: IntArray) {
        if (!TextUtils.isEmpty(muid)) {
            clickedEmojiMuid = muid
        }

        val emojiAdapter = BottomSheetEmojiAdapter(loadJSONFromAsset(), this@ChatActivity, muid)
        val gridLayoutManager = object : GridLayoutManager(this@ChatActivity, 7) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        rvEmoji?.layoutManager = gridLayoutManager
        rvEmoji?.adapter = emojiAdapter

        val params2: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                Utils.dpToPx(this@ChatActivity, 50f)
        )
        rvEmoji?.layoutParams = params2
        params2.setMargins(50, location[1] - 200, 50, 0)

        rvEmoji?.visibility = View.VISIBLE
        viewTranslucent?.visibility = View.VISIBLE
        val animation = AnimationUtils.loadAnimation(this@ChatActivity, R.anim.emoji_layout_anim)
        animation?.setAnimationListener(this)
        rvEmoji?.startAnimation(animation)
        viewTranslucent?.setOnClickListener {
            rvEmoji?.clearAnimation()
            rvEmoji?.visibility = View.GONE
            viewTranslucent?.visibility = View.GONE
        }
//        EmojiFragment().show(supportFragmentManager, "EmojiFragment")
    }

    fun loadJSONFromAsset(): java.util.ArrayList<Emoji> {
        val emojiList = java.util.ArrayList<Emoji>()
        val emojiMap = com.skeleton.mvp.data.db.CommonData.getEmojiMap()
        for (i in emojiMap.size - 1 downTo -0 + 1) {
            emojiList.add(Emoji(emojiMap.get(i), emojiMap.get(i)))
        }
        return emojiList
    }

    override fun getClickedEmoji(unicode: String?, isEmoji: Boolean, muid: String?) {
        viewTranslucent?.performClick()
        if (!TextUtils.isEmpty(muid)) {
            clickedEmojiMuid = muid!!
        }
        val prev = supportFragmentManager.findFragmentByTag("EmojiFragment")
        if (prev != null) {
            val df = prev as DialogFragment
            df.dismiss()
        }
        if (isEmoji) {
            EmojiReceived().emojiReceived(clickedEmojiMuid, diffUtilMessageList, unicode!!, true, userId.toString(), userName, object : EmojiReceived.UpdateAndPublishEmoji {
                override fun updateAndPublishEmoji(emoji: String, selectedPos: Int, diffUtilMessageList: ArrayList<Message>, isToBePublished: Boolean, sentEmpty: Boolean) {
                    runOnUiThread {
                        messageList = ArrayList<Message>()
                        //messageList.addAll(diffUtilMessageList)
                        for (message in diffUtilMessageList) {
                            var messageClone = message.clone()
                            messageList.add(messageClone as Message)
                        }
                        messageAdapter?.updateMessageList(messageList)
                        //messageAdapter?.notifyItemChanged(selectedPos)
                    }
                    messagesMap[diffUtilMessageList[selectedPos].muid] =
                            diffUtilMessageList[selectedPos]
                    if (isToBePublished) {
                        if (sentEmpty) {
                            publishEmoji("")
                        } else {
                            publishEmoji(emoji)
                        }
                    }
                }
            })
        } else {
            EmojiReceived().emojiReceived(clickedEmojiMuid, diffUtilMessageList, getEmojiByUnicode(Integer.parseInt(unicode!!, 16)), true, userId.toString(), userName, object : EmojiReceived.UpdateAndPublishEmoji {
                override fun updateAndPublishEmoji(emoji: String, selectedPos: Int, diffUtilMessageList: ArrayList<Message>, isToBePublished: Boolean, sentEmpty: Boolean) {
                    runOnUiThread {
                        messageList = ArrayList<Message>()
                        //messageList.addAll(diffUtilMessageList)
                        for (message in diffUtilMessageList) {
                            var messageClone = message.clone()
                            messageList.add(messageClone as Message)
                        }
                        messageAdapter?.updateMessageList(messageList)
                        // messageAdapter?.notifyItemChanged(selectedPos)
                    }
                    messagesMap[diffUtilMessageList[selectedPos].muid] =
                            diffUtilMessageList[selectedPos]
                    if (isToBePublished) {
                        if (sentEmpty) {
                            publishEmoji("")
                        } else {
                            publishEmoji(emoji)
                        }
                    }
                }
            })
        }
    }

    private fun publishEmoji(emoji: String) {
        if (isNetworkConnected) {
            try {
                val localDate = DateUtils.getFormattedDate(Date())
                val messageJson = JSONObject()
                messageJson.put(FuguAppConstant.FULL_NAME, userName)
                messageJson.put(FuguAppConstant.CHANNEL_ID, channelId)
                messageJson.put(
                        FuguAppConstant.DATE_TIME,
                        DateUtils.getInstance().convertToUTC(localDate)
                )
                messageJson.put(FuguAppConstant.MESSAGE_INDEX, diffUtilMessageList.size - 1)
                messageJson.put(FuguAppConstant.MESSAGE_UNIQUE_ID, clickedEmojiMuid)
                messageJson.put("is_thread_reaction", false)
                messageJson.put(FuguAppConstant.CHANNEL_ID, channelId)
                messageJson.put(FuguAppConstant.IS_TYPING, FuguAppConstant.TYPING_SHOW_MESSAGE)
                val devicePayload = JSONObject()
                devicePayload.put(
                        FuguAppConstant.DEVICE_ID,
                        UniqueIMEIID.getUniqueIMEIId(this@ChatActivity)
                )
                devicePayload.put(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER)
                devicePayload.put(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME)
                devicePayload.put(
                        FuguAppConstant.DEVICE_DETAILS,
                        CommonData.deviceDetails(this@ChatActivity)
                )
                messageJson.put("device_payload", devicePayload)
                messageJson.put(FuguAppConstant.USER_ID, userId.toString())
                messageJson.put(FuguAppConstant.USER_TYPE, FuguAppConstant.ANDROID_USER)
                messageJson.put(FuguAppConstant.USER_REACTION_EMOJI, emoji)
                SocketConnection.reactOnMessage(messageJson)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun getEmojiByUnicode(unicode: Int): String {
        return String(Character.toChars(unicode))
    }

    fun deleteMessage(position: Int, muid: String, messageStatus: Int) {
        messageAdapter?.setOnLongClickValue(false)
        if (position < diffUtilMessageList.size && diffUtilMessageList[position] != null) {
            if (messageStatus == FuguAppConstant.MESSAGE_SENT || messageStatus == FuguAppConstant.MESSAGE_DELIVERED || messageStatus == FuguAppConstant.MESSAGE_READ) {
                val commonParams = CommonParams.Builder()
                        .add(FuguAppConstant.EN_USER_ID, enUserId)
                        .add(MESSAGE_UNIQUE_ID, muid)
                        .add(FuguAppConstant.CHANNEL_ID, channelId)
                        .build()
                RestClient.getApiInterface()
                        .deleteMessage(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), appSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.map)
                        .enqueue(object : ResponseResolver<CommonResponse>() {
                            override fun success(commonResponse: CommonResponse?) {
                                try {
                                    var extension =
                                            diffUtilMessageList[position].image_url!!.split(".")[diffUtilMessageList[position].image_url.split(
                                                    "."
                                            ).size - 1]
                                    if (extension.toLowerCase().equals("png")) {
                                        extension = "jpg"
                                    }
                                    val fileName =
                                            diffUtilMessageList[position].fileName + "_" + diffUtilMessageList[position].muid + "." + extension
                                    val filePath = File(
                                            GeneralFunctions().getDirectory(
                                                    extension,
                                                    workspaceInfoList,
                                                    currentPosition
                                            ) + "/" + fileName
                                    )
                                    if (filePath.exists()) {
                                        filePath.delete()
                                        messageAdapter?.getFromSdcard()
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                if (diffUtilMessageList[position].rowType == TEXT_MESSGAE_SELF
                                        || diffUtilMessageList[position].rowType == IMAGE_MESSGAE_SELF
                                        || diffUtilMessageList[position].rowType == VIDEO_MESSGAE_SELF
                                        || diffUtilMessageList[position].rowType == VIDEO_CALL_SELF
                                        || diffUtilMessageList[position].rowType == POLL_SELF
                                        || diffUtilMessageList[position].rowType == FILE_MESSGAE_SELF
                                ) {
                                    diffUtilMessageList[position].rowType = MESSAGE_DELETED_SELF
                                    diffUtilMessageList[position].message = "You deleted this message "
                                    diffUtilMessageList[position].messageState = 0
                                } else {
                                    diffUtilMessageList[position].message = "This message was deleted "
                                    diffUtilMessageList[position].messageState = 0
                                }
                                messagesMap[diffUtilMessageList[position].muid] =
                                        diffUtilMessageList[position]
                                messageList = ArrayList<Message>()
                                //messageList.addAll(diffUtilMessageList)
                                for (message in diffUtilMessageList) {
                                    val messageClone = message.clone()
                                    messageList.add(messageClone as Message)
                                }
                                messageAdapter?.updateMessageList(messageList)
                                //messageAdapter?.notifyItemChanged(position)
                            }

                            override fun failure(error: APIError?) {
                            }
                        })
            } else {
                cancelMessage(position)
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun startAnim(fullName: String) {
        ivGreenDot?.visibility = View.GONE
        if (chatType == ChatType.O2O || chatType == ChatType.BOT) {
            tvInfo?.text = "is typing..."
        } else {
            tvInfo?.text = fullName.split(" ")[0] + " is typing..."
        }
    }

    private fun stopAnim() {
        runOnUiThread {
            if (chatType == ChatType.O2O) {
                ivGreenDot?.visibility = View.VISIBLE
                tvInfo?.text = "online"
            } else {
                ivGreenDot?.visibility = View.GONE
                tvInfo?.text = "tap to view Info"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val readAllJson = JSONObject()
        readAllJson.put(CHANNEL_ID, channelId)
        readAllJson.put(NOTIFICATION_TYPE, 6)
        readAllJson.put(USER_ID, userId)
        SocketConnection.readAll(readAllJson)
        Handler().postDelayed({
            if (GeneralFunctions().isMyServiceRunning(
                            OngoingCallService::class.simpleName,
                            this@ChatActivity
                    )
            ) {
//                tvReturnCall.visibility = View.VISIBLE
                ivVideoCall.setOnClickListener(null)
                ivAudioCall.setOnClickListener(null)
                ivVideoCall.alpha = 0.5f
                ivAudioCall.alpha = 0.5f
                tvReturnCall.setOnClickListener {
                    startActivity(
                            Intent(
                                    this@ChatActivity,
                                    FuguCallActivity::class.java
                            )
                    )
                }
            } else {
//                tvReturnCall.visibility = View.GONE
                ivAudioCall.setOnClickListener(this)
                ivVideoCall.setOnClickListener(this)
                ivVideoCall.alpha = 1f
                ivAudioCall.alpha = 1f
            }
        }, 1000)

        val newSignedInPosition = com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()
        if (newSignedInPosition != currentPosition) {
            currentPosition = newSignedInPosition
            finish()
        }
        Thread {
            kotlin.run {
                messageAdapter?.attachObservers(true)
            }
        }.start()
        GeneralFunctions().removeNotification(channelId)
        unregisterReceivers()
        if (!isScreenOpenedFromThread) {
            try {
                var scrollToBottom = false
                val messageListNew = ChatDatabase.getMessageList(channelId)
                if (messageListNew.size > diffUtilMessageList.size) {
                    scrollToBottom = true
                }
                diffUtilMessageList = ChatDatabase.getMessageList(channelId)

                for (i in diffUtilMessageList.indices) {
                    if (diffUtilMessageList[i].rowType == HEADER_ITEM && !TextUtils.isEmpty(
                                    diffUtilMessageList[i].localSentAtUtc
                            )
                    ) {
                        diffUtilMessageList[i].sentAtUtc =
                                DateUtils.getDate(dateUtils.convertToLocal(diffUtilMessageList[i].localSentAtUtc))
                    }
                }

                messagesMap = ChatDatabase.getMessageMap(channelId)
//                threadMessagesMap = ChatDatabase.getThreadMap(channelId)
                messageList = ArrayList<Message>()
                //messageList.addAll(diffUtilMessageList)
                for (message in diffUtilMessageList) {
                    var messageClone = message.clone()
                    messageList.add(messageClone as Message)
                }
                messageAdapter?.updateMessageList(messageList)
                //messageAdapter?.notifyDataSetChanged()
                if (scrollToBottom) {
                    rvMessages.scrollToPosition(diffUtilMessageList.size - 1)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            isScreenOpenedFromThread = false
        }
        if (isGroupOpened) {
            isGroupOpened = false
            pageSize = 1
            if (isNetworkConnected) {
                apiGetMessages(false)
                apiFetchMembers()
            }
        }
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(channelId.toInt())
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mDeleteMessageReceiver,
                IntentFilter(FuguAppConstant.DELETE_INTENT)
        )
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mVideoCallHungUp,
                IntentFilter(FuguAppConstant.VIDEO_CALL_HUNGUP)
        )
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mClearChatReceiver,
                IntentFilter(FuguAppConstant.CLEAR_INTENT)
        )
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mPublicChatReceiver,
                IntentFilter(FuguAppConstant.PUBLIC_INTENT)
        )
        LocalBroadcastManager.getInstance(this@ChatActivity).registerReceiver(
                mMessageReceiver,
                getIntentFilter()
        )
        LocalBroadcastManager.getInstance(this@ChatActivity).registerReceiver(
                mUserAddedReceiver,
                IntentFilter(FuguAppConstant.USER_ADDED_INTENT)
        )
        LocalBroadcastManager.getInstance(this@ChatActivity).registerReceiver(
                mUserRemovedReceiver,
                IntentFilter(FuguAppConstant.USER_REMOVED_INTENT)
        )
        LocalBroadcastManager.getInstance(this@ChatActivity).registerReceiver(
                mEditMessageReceiver,
                IntentFilter(FuguAppConstant.EDIT_MESSAGE_INTENT)
        )
        LocalBroadcastManager.getInstance(this@ChatActivity).registerReceiver(
                mMessageNotificationReceiver,
                IntentFilter(FuguAppConstant.MESSAGE_NOTIFICATION_INTENT)
        )
        runOnUiThread {
            Handler().postDelayed({
                setUpSocketListeners("Resume chat")
                if (!isSearchedMessage) {
                    PushReceiver.PushChannel.pushChannelId = channelId
                } else {
                    PushReceiver.PushChannel.pushChannelId = -2L
                }
            }, 1000)
        }
    }

    private fun unregisterReceivers() {
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mVideoCallHungUp)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mClearChatReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mDeleteMessageReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            LocalBroadcastManager.getInstance(this@ChatActivity)
                    .unregisterReceiver(mMessageReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            LocalBroadcastManager.getInstance(this@ChatActivity)
                    .unregisterReceiver(mEditMessageReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            LocalBroadcastManager.getInstance(this@ChatActivity)
                    .unregisterReceiver(mPublicChatReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            LocalBroadcastManager.getInstance(this@ChatActivity)
                    .unregisterReceiver(mUserAddedReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            LocalBroadcastManager.getInstance(this@ChatActivity)
                    .unregisterReceiver(mUserRemovedReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            LocalBroadcastManager.getInstance(this@ChatActivity)
                    .unregisterReceiver(mMessageNotificationReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        diffUtilMessageList = messageAdapter?.getMessages()!!
        try {
            if (positionToBeInserted > 0 && diffUtilMessageList.get(positionToBeInserted).rowType == UNREAD_ITEM) {
                diffUtilMessageList.removeAt(positionToBeInserted)
                for (i in diffUtilMessageList.indices) {
                    diffUtilMessageList[i].messageIndex = i
                    val message = messagesMap[diffUtilMessageList[i].muid]
                    message?.messageIndex = i
                    messagesMap[diffUtilMessageList[i].muid] = message!!
                }
            }
        } catch (e: Exception) {

        }
        GeneralFunctions().resetChat(
                chatType, selectedBotAction, channelId,
                mentionsList, etMessage, diffUtilMessageList, messagesMap
        )
        messageAdapter?.attachObservers(false)
        if (isTyping == FuguAppConstant.TYPING_STARTED) {
            isTyping = FuguAppConstant.TYPING_STOPPED
            SocketConnection.stopTyping(
                    GeneralFunctions().getTypingObject(
                            userName,
                            userId,
                            channelId
                    )
            )
        }
//        stopAnim()
        messageAdapter?.stopMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        KeyboardUtil.removeKeyboardToggleListener(keyboardListener)
        GeneralFunctions().removeNotification(channelId)
    }

    private fun stopTyping() {
        if (isTyping == FuguAppConstant.TYPING_STARTED) {
            isTyping = FuguAppConstant.TYPING_STOPPED
            SocketConnection.stopTyping(
                    GeneralFunctions().getTypingObject(
                            userName,
                            userId,
                            channelId
                    )
            )
        }
    }

    private val mClearChatReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val channelIdReceived = intent.getLongExtra(FuguAppConstant.CHANNEL_ID, -1L)
            if (intent.hasExtra(FuguAppConstant.APP_SECRET_KEY)
                    && intent.getStringExtra(FuguAppConstant.APP_SECRET_KEY) == appSecretKey
                    && channelIdReceived.compareTo(channelId) == 0
            ) {
                diffUtilMessageList = ArrayList<Message>()
                messagesMap = LinkedHashMap()
                messageList = ArrayList()
                //messageList.addAll(diffUtilMessageList)
                for (message in diffUtilMessageList) {
                    val messageClone = message.clone()
                    messageList.add(messageClone as Message)
                }
                messageAdapter?.updateMessageList(messageList)
                //messageAdapter?.notifyDataSetChanged()
                GeneralFunctions().getFromSdcardAndDelete(
                        channelIdReceived,
                        workspaceInfoList,
                        currentPosition
                )
                showErrorMessage("This chat has been deleted!", { onBackPressed() }, { }, "Ok", "")
            }
        }
    }

    override fun onStop() {
        unregisterReceivers()
        super.onStop()
    }

    private val mUserAddedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.hasExtra(CHANNEL_ID)!! && intent.getLongExtra(CHANNEL_ID, -1L).compareTo(
                            channelId
                    ) == 0
            ) {
                val message = Message()
                message.message = intent.getStringExtra(NOTI_MSG)
                message.muid = intent.getStringExtra(MESSAGE_UNIQUE_ID)
                message.messageType = 5
                message.rowType = PUBLIC_NOTE
                message.messageIndex = diffUtilMessageList.size
                diffUtilMessageList.add(message)
                messagesMap[intent.getStringExtra(MESSAGE_UNIQUE_ID)!!] = message
                messageList = ArrayList<Message>()
                //messageList.addAll(diffUtilMessageList)
                for (message in diffUtilMessageList) {
                    var messageClone = message.clone()
                    messageList.add(messageClone as Message)
                }
                messageAdapter?.updateMessageList(messageList)
                //messageAdapter?.notifyItemInserted(messageList.size - 1)
                rvMessages.scrollToPosition(diffUtilMessageList.size - 1)
                for (i in membersList) {
                    if (i.userId.compareTo(-1L) == 0) {
                        membersList.remove(i)
                        break
                    }
                }
                if (intent.hasExtra("added_user_id")) {
                    membersList.add(
                            0, Member(
                            intent.getStringExtra("added_user_name"), intent.getLongExtra("added_user_id", -2L),
                            "", intent.getStringExtra("added_user_image"),
                            "", 1, "", ""
                    )
                    )
                }
//                try {
//                    membersList.sortWith(Comparator { one, other -> one.name.compareTo(other.name) })
//                } catch (e: java.lang.Exception) {
//                    e.printStackTrace()
//                }
                if (chatType != 7) {
                    membersList.add(
                            0, Member(
                            "Everyone", -1L, "",
                            "https://fuguchat.s3.ap-south-1.amazonaws.com/default/WwX5qYGSEb_1518441286074.png",
                            "", 1, "", ""
                    )
                    )
                }
            }
        }
    }

    private val mUserRemovedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.hasExtra(CHANNEL_ID)!! && intent.getLongExtra(CHANNEL_ID, -1L).compareTo(
                            channelId
                    ) == 0
            ) {
                if (intent.getLongExtra("removed_user_id", -1L).compareTo(userId) == 0) {
                    try {
                        androidx.appcompat.app.AlertDialog.Builder(this@ChatActivity)
                                .setTitle("You are removed from " + tvTitle.text + "!!")
                                .setCancelable(false)
                                .setPositiveButton("Ok") { dialog, which ->
                                    finish()
                                }
                                .show()
                    } catch (e: java.lang.Exception) {
                    }
                } else {
                    val message = Message()
                    message.message = intent.getStringExtra(NOTI_MSG)
                    message.muid = intent.getStringExtra(MESSAGE_UNIQUE_ID)
                    message.messageType = 5
                    message.rowType = PUBLIC_NOTE
                    message.messageIndex = diffUtilMessageList.size
                    diffUtilMessageList.add(message)
                    messagesMap[intent.getStringExtra(MESSAGE_UNIQUE_ID)!!] = message
                    messageList = ArrayList<Message>()
                    //messageList.addAll(diffUtilMessageList)
                    for (message in diffUtilMessageList) {
                        val messageClone = message.clone()
                        messageList.add(messageClone as Message)
                    }
                    messageAdapter?.updateMessageList(messageList)
                    //messageAdapter?.notifyItemInserted(messageList.size - 1)
                    rvMessages.scrollToPosition(diffUtilMessageList.size - 1)
                    for (i in membersList) {
                        if (i.userId.compareTo(intent.getLongExtra("removed_user_id", -1L)) == 0) {
                            membersList.remove(i)
                            break
                        }
                    }
                }
            }
        }
    }


    private val mDeleteMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val channelIdReceived = intent.getLongExtra(FuguAppConstant.CHANNEL_ID, -1L)
            if (intent.hasExtra(FuguAppConstant.APP_SECRET_KEY) && intent.getStringExtra(
                            FuguAppConstant.APP_SECRET_KEY
                    ) == appSecretKey && channelIdReceived.compareTo(channelId) == 0
            ) {
                for (position in diffUtilMessageList.indices.reversed()) {
                    try {
                        if (diffUtilMessageList[position].muid == intent.getStringExtra(FuguAppConstant.MESSAGE_UNIQUE_ID)) {
                            if (diffUtilMessageList[position].rowType == TEXT_MESSGAE_SELF
                                    || diffUtilMessageList[position].rowType == IMAGE_MESSGAE_SELF
                                    || diffUtilMessageList[position].rowType == FILE_MESSGAE_SELF
                                    || diffUtilMessageList[position].rowType == MESSAGE_DELETED_SELF
                            ) {
                                diffUtilMessageList[position].rowType = MESSAGE_DELETED_SELF
                                diffUtilMessageList[position].message = "You deleted this message "
                                diffUtilMessageList[position].messageState = 0
                            } else {
                                diffUtilMessageList[position].rowType = MESSAGE_DELETED_OTHER
                                diffUtilMessageList[position].message = "This message was deleted "
                                diffUtilMessageList[position].messageState = 0
                            }
//                            messageList = ArrayList<Message>()
//                            messageList.addAll(diffUtilMessageList)
//                            messageAdapter?.updateMessageList(messageList)
                            messagesMap[diffUtilMessageList[position].muid] =
                                    diffUtilMessageList[position]
                            messageList = ArrayList<Message>()
                            //messageList.addAll(diffUtilMessageList)
                            for (message in diffUtilMessageList) {
                                val messageClone = message.clone()
                                messageList.add(messageClone as Message)
                            }
                            messageAdapter?.updateMessageList(messageList)
                            //messageAdapter?.notifyDataSetChanged()
                            if (diffUtilMessageList[position].messageType == IMAGE_MESSAGE && (diffUtilMessageList[position].rowType == MESSAGE_DELETED_SELF || diffUtilMessageList[position].rowType == MESSAGE_DELETED_OTHER)) {
                                try {
                                    var extension =
                                            diffUtilMessageList[position].image_url!!.split(".")[diffUtilMessageList[position].image_url.split(
                                                    "."
                                            ).size - 1]
                                    if (extension.toLowerCase().equals("png")) {
                                        extension = "jpg"
                                    }
                                    val fileName =
                                            diffUtilMessageList[position].fileName + "_" + diffUtilMessageList[position].muid + "." + extension
                                    val filePath = File(
                                            GeneralFunctions().getDirectory(
                                                    extension,
                                                    workspaceInfoList,
                                                    currentPosition
                                            ) + "/" + fileName
                                    )
                                    if (filePath.exists()) {
                                        filePath.delete()
                                        messageAdapter?.deleteImageFromImageList(diffUtilMessageList[position].id.toString())
                                    }

                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }

                            }
                            isMessageInEditMode = false
                            ivSend.setImageResource(R.drawable.ivsend)
                            ivCancelEdit.visibility = View.GONE
                            ivAttachment.visibility = View.VISIBLE
                            firstEditMuid = ""
                            Thread {
                                kotlin.run {
                                    if (!isSearchedMessage) {
                                        ChatDatabase.setMessageList(diffUtilMessageList, channelId)
                                        ChatDatabase.setMessageMap(messagesMap, channelId)
                                    }
                                }
                            }.start()
                            break
                        }
                    } catch (e: Exception) {

                    }
                }
            }
        }
    }

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                NETWORK_STATE_INTENT -> {
                    if (intent.getBooleanExtra("isConnected", false)) {
                        setUpSocketListeners("Network change chat")
                    } else if (llTyping.visibility == View.VISIBLE) {
                        //                        stopAnim()
                    }
                    if (intent.hasExtra("status")) {
                        changeInternetStatus(intent.getIntExtra("status", 2))
                    }
                }
            }
        }

    }

    private val mMessageNotificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Handler().postDelayed({
                try {
                    if (intent?.hasExtra(CHANNEL_ID)!! && intent.getLongExtra(CHANNEL_ID, -1L).compareTo(channelId) == 0) {
                        val messageRecieved = intent.getSerializableExtra(MESSAGE) as Message
                        var messageTobeAdded = true
                        for (message in diffUtilMessageList.reversed()) {
                            if (message.muid.equals(messageRecieved.muid)) {
                                messageTobeAdded = false
                                break
                            }
                        }
                        runOnUiThread {
                            if (messageTobeAdded) {
                                diffUtilMessageList.add(messageRecieved)
                                messagesMap[messageRecieved.muid] = messageRecieved
                                messageList = ArrayList<Message>()
                                //messageList.addAll(diffUtilMessageList)
                                for (message in diffUtilMessageList) {
                                    var messageClone = message.clone()
                                    messageList.add(messageClone as Message)
                                }
                                messageAdapter?.updateMessageList(messageList)
                                //messageAdapter?.notifyDataSetChanged()
                                rvMessages.scrollToPosition(diffUtilMessageList.size - 1)
                            }
                        }

                        if (messageRecieved.userId.compareTo(userId) != 0) {
                            for (position in diffUtilMessageList.indices.reversed()) {
                                try {
                                    if (diffUtilMessageList[position].rowType == TEXT_MESSGAE_SELF
                                            || diffUtilMessageList[position].rowType == IMAGE_MESSGAE_SELF
                                            || diffUtilMessageList[position].rowType == VIDEO_MESSGAE_SELF
                                            || diffUtilMessageList[position].rowType == VIDEO_CALL_SELF
                                            || diffUtilMessageList[position].rowType == POLL_SELF
                                            || diffUtilMessageList[position].rowType == MESSAGE_DELETED_SELF
                                            || diffUtilMessageList[position].rowType == FILE_MESSGAE_SELF
                                    ) {
                                        if (diffUtilMessageList[position].messageStatus == FuguAppConstant.MESSAGE_SENT) {
                                            diffUtilMessageList[position].messageStatus =
                                                    FuguAppConstant.MESSAGE_READ
                                        } else {
                                            val status =
                                                    diffUtilMessageList[position].messageStatus!!
                                            diffUtilMessageList[position].messageStatus = status
                                        }
                                    }
                                    messagesMap[diffUtilMessageList[position].muid] =
                                            diffUtilMessageList[position]
                                } catch (e: Exception) {

                                }
                            }
                            runOnUiThread {
                                messageList = ArrayList()
                                //messageList.addAll(diffUtilMessageList)
                                try {
                                    if (diffUtilMessageList.size > 0) {
                                        for (message in diffUtilMessageList) {
                                            if (diffUtilMessageList.size > 0) {
                                                var messageClone = message.clone()
                                                messageList.add(messageClone as Message)
                                            } else {
                                                break
                                            }
                                        }
                                        if (diffUtilMessageList.size > 0) {
                                            messageAdapter?.updateMessageList(messageList)
                                        }
                                    }
                                } catch (e: Exception) {
                                }
                                //messageAdapter?.notifyDataSetChanged()
                            }
                        }


                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, 500)
        }
    }
    private val mPublicChatReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.hasExtra(CHANNEL_ID)!!
                    && intent.getLongExtra(CHANNEL_ID, -1L).compareTo(channelId) == 0 && intent.getBooleanExtra("is_deleted_group", false)
            ) {
                androidx.appcompat.app.AlertDialog.Builder(this@ChatActivity)
                        .setTitle("This group has been deleted!")
                        .setCancelable(false)
                        .setPositiveButton("Ok") { _, _ ->
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                        .show()
            }
            try {
                if (intent.hasExtra(CHANNEL_ID) && intent.getLongExtra(CHANNEL_ID, -1L).compareTo(channelId) == 0) {
                    if (!intent.hasExtra(MESSAGE_TYPE)) {
                        val message = Message()
                        message.message = intent.getStringExtra(MESSAGE)
                        message.messageType = intent.getIntExtra(MESSAGE_TYPE, 5)
                        message.sentAtUtc = intent.getStringExtra(DATE_TIME)
                        message.muid = intent.getStringExtra(MESSAGE_UNIQUE_ID)
                        message.rowType = PUBLIC_NOTE
                        diffUtilMessageList.add(message)
                        messagesMap.put(intent.getStringExtra(MESSAGE_UNIQUE_ID)!!, message)
                        messageList = ArrayList<Message>()
                        // messageList.addAll(diffUtilMessageList)
                        for (message in diffUtilMessageList) {
                            var messageClone = message.clone()
                            messageList.add(messageClone as Message)
                        }
                        messageAdapter?.updateMessageList(messageList)
                        //messageAdapter?.notifyItemInserted(messageList.size - 1)
                        rvMessages.scrollToPosition(diffUtilMessageList.size - 1)
                    }

                    if (intent.hasExtra(USER_IDS_TO_REMOVE_ADMIN)) {
                        for (removedMember in intent.getIntegerArrayListExtra(USER_IDS_TO_REMOVE_ADMIN)!!) {
                            if (userId.compareTo(removedMember) == 0) {
                                userRole = "USER"
                                break
                            }
                        }
                    }
                    if (intent.hasExtra(USER_IDS_TO_MAKE_ADMIN)) {
                        for (addedMember in intent.getIntegerArrayListExtra(USER_IDS_TO_MAKE_ADMIN)!!) {
                            if (userId.compareTo(addedMember) == 0) {
                                userRole = "ADMIN"
                                break
                            }
                        }
                    }

                    if (intent.hasExtra("only_admin_can_message")) {
                        if (!intent.getBooleanExtra(
                                        "only_admin_can_message", false)) {
                            llCannotReply.visibility = View.GONE
                        } else if (intent.getBooleanExtra("only_admin_can_message", false) && userRole.equals("ADMIN")) {
                            llCannotReply.visibility = View.GONE
                        } else {
                            llCannotReply.visibility = View.VISIBLE
                        }
                    }
                }
            } catch (e: java.lang.Exception) {
            }
        }
    }

    private val mEditMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            try {
                val mngr = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val taskList = mngr.getRunningTasks(10)
                if (taskList[0].topActivity!!.className == "com.skeleton.mvp.activity.ChatActivity" && intent.getLongExtra(CHANNEL_ID, -1L).compareTo(channelId) == 0 && !intent.getBooleanExtra(IS_THREAD_MESSAGE, false)) {
                    if (messagesMap[intent.getStringExtra(MESSAGE_UNIQUE_ID)] != null) {
                        val message = messagesMap[intent.getStringExtra(MESSAGE_UNIQUE_ID)]!!
                        message.messageState = 4
                        message.message = intent.getStringExtra(MESSAGE)
                        message.alteredMessage = FormatStringUtil.FormatString.getFormattedString(
                                intent.getStringExtra(MESSAGE)
                        )[0]
                        messagesMap[intent.getStringExtra(MESSAGE_UNIQUE_ID)!!] = message
                        diffUtilMessageList[message.messageIndex] = message
                        messageList = ArrayList<Message>()
                        //messageList.addAll(diffUtilMessageList)
                        for (message in diffUtilMessageList) {
                            var messageClone = message.clone()
                            messageList.add(messageClone as Message)
                        }
                        messageAdapter?.updateMessageList(messageList)
                        //messageAdapter?.notifyItemChanged(message.messageIndex)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun sendMessage(position: Int) {
        try {
            val message = diffUtilMessageList[position]
            if (message.isExpired) {
                try {
                    isTyping = FuguAppConstant.TYPING_SHOW_MESSAGE
                    val localDate = DateUtils.getFormattedDate(Date())
                    message.sentAtUtc = DateUtils.getInstance().convertToUTC(localDate)
                    publishMessage(
                            message.message,
                            FuguAppConstant.TEXT_MESSAGE, message.url,
                            "",
                            null,
                            message.uuid, position, false
                    )
                    message.isSent = true
                    message.isExpired = false
                    messageAdapter?.updateMessageList(messageList)
                    messageAdapter?.notifyItemChanged(position)
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
            val message = (diffUtilMessageList[position])
            try {
                unsentMessageMap.remove(message.muid)
                diffUtilMessageList.removeAt(position)
                messagesMap.remove(message.muid)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            for (i in diffUtilMessageList.indices) {
                diffUtilMessageList[i].messageIndex = i
            }
            messageList = ArrayList<Message>()
            //messageList.addAll(diffUtilMessageList)
            for (message in diffUtilMessageList) {
                val messageClone = message.clone()
                messageList.add(messageClone as Message)
            }
            messageAdapter?.updateMessageList(messageList)
            //messageAdapter?.notifyDataSetChanged()
            Thread {
                kotlin.run {
                    if (isSearchedMessage) {
                        ChatDatabase.setUnsentMessageMapByChannel(channelId, unsentMessageMap)
                        ChatDatabase.setMessageList(diffUtilMessageList, channelId)
                        ChatDatabase.setMessageMap(messagesMap, channelId)
                    }
                }
            }.start()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun sendFirstUnsentMessageOfList() {
        val expiryTime = 10
        val localDate = DateUtils.getFormattedDate(Date())
        val messagesTobeSent = java.util.LinkedHashMap<String, Message?>()
        for (name in unsentMessageMap.keys) {
            val messageObj = unsentMessageMap[name]!!
            val newTime =
                    DateUtils.getTimeInMinutes(DateUtils.getInstance().convertToUTC(localDate))
            val oldTime = DateUtils.getTimeInMinutes(messageObj.sentAtUtc)
            if (unsentMessageMap[name]!!.messageType == FuguAppConstant.TEXT_MESSAGE && !messageObj.isExpired && newTime - oldTime < expiryTime) {
                unsentMessageMap[name]?.isThreadMessage =
                        !TextUtils.isEmpty(unsentMessageMap[name]?.threadMuid)
                messagesTobeSent[name] = unsentMessageMap[name]
            } else {
                if (unsentMessageMap[name]?.messageType == FuguAppConstant.TEXT_MESSAGE) {
                    messageObj.isExpired = true
                    for (message in diffUtilMessageList.indices.reversed()) {
                        if (diffUtilMessageList[message].muid == unsentMessageMap[name]?.muid) {
                            diffUtilMessageList[message].isExpired = true
                            messagesMap[diffUtilMessageList[message].muid] =
                                    diffUtilMessageList[message]
                            break
                        }
                    }
                }
            }
        }
        runOnUiThread {
            messageList = ArrayList<Message>()
            //messageList.addAll(diffUtilMessageList)
            for (message in diffUtilMessageList) {
                var messageClone = message.clone()
                messageList.add(messageClone as Message)
            }
            messageAdapter?.updateMessageList(messageList)
            //messageAdapter?.notifyDataSetChanged()
        }
        val keys = java.util.ArrayList(messagesTobeSent.keys)
        val messageJson = JSONObject()
        if (keys.size > 0) {
            val messageObj = messagesTobeSent[keys[0]]!!
            try {
                if (messageObj.userId.toString().equals(userId.toString()) && messageObj.messageStatus == MESSAGE_UNSENT) {
                    messageJson.put(CHANNEL_ID, channelId)
                    messageJson.put(FuguAppConstant.USER_ID, messageObj.userId.toString())
                    messageJson.put(FuguAppConstant.FULL_NAME, messageObj.getfromName())
                    messageJson.put(FuguAppConstant.MESSAGE, messageObj.message)
                    messageJson.put(FuguAppConstant.MESSAGE_TYPE, messageObj.messageType)
                    DateUtils.getFormattedDate(Calendar.getInstance().time)
                    messageJson.put(FuguAppConstant.USER_TYPE, FuguAppConstant.ANDROID_USER)
                    messageJson.put(
                            FuguAppConstant.DATE_TIME,
                            DateUtils.getInstance().convertToUTC(localDate)
                    )
                    messageJson.put(FuguAppConstant.MESSAGE_INDEX, messageObj.messageIndex)
                    messageJson.put(FuguAppConstant.IS_TYPING, FuguAppConstant.TYPING_SHOW_MESSAGE)
                    messageJson.put(FuguAppConstant.MESSAGE_STATUS, messageObj.messageStatus)
                    if (messageObj.isThreadMessage!!) {
                        messageJson.put(FuguAppConstant.MESSAGE_UNIQUE_ID, messageObj.threadMuid)
                        messageJson.put("thread_muid", messageObj.uuid)
                        messageJson.put("is_thread_message", true)
                    } else {
                        messageJson.put(FuguAppConstant.MESSAGE_UNIQUE_ID, messageObj.uuid)
                        messageJson.put("is_thread_message", false)
                    }

                    val devicePayload = JSONObject()
                    devicePayload.put(
                            FuguAppConstant.DEVICE_ID,
                            UniqueIMEIID.getUniqueIMEIId(this@ChatActivity)
                    )
                    devicePayload.put(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER)
                    devicePayload.put(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME)
                    devicePayload.put(
                            FuguAppConstant.DEVICE_DETAILS,
                            CommonData.deviceDetails(this@ChatActivity)
                    )
                    messageJson.put(DEVICE_PAYLOAD, devicePayload)

                    runOnUiThread {
                        Handler().postDelayed({
                            if (messageObj.messageType == FuguAppConstant.TEXT_MESSAGE) {
                                SocketConnection.sendMessage(messageJson)
                            }
                        }, 500)
                    }
                } else {
                    FirebaseCrashlytics.getInstance().log(messageObj.toString())
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun getIntentFilter(): IntentFilter {
        val intent = IntentFilter()
        intent.addAction(NETWORK_STATE_INTENT)
        intent.addAction(FuguAppConstant.NOTIFICATION_TAPPED)
        return intent
    }

    override fun onAnimationRepeat(animation: Animation?) {
    }

    override fun onAnimationEnd(animation: Animation?) {
    }

    override fun onAnimationStart(animation: Animation?) {
        if (tvDateLabel?.visibility != View.VISIBLE) {
            tvDateLabel?.clearAnimation()
        }
    }

    override fun openedThread() {
        threadOpened = true
    }

    fun openThread(position: Int) {
        messageAdapter?.setOnLongClickValue(false)
        var position = position
        if (position > diffUtilMessageList.size) {
            position = diffUtilMessageList.size
        }

        if (!TextUtils.isEmpty((diffUtilMessageList[position].uuid))
                && isNetworkConnected
        ) {
            val innectChatIntent = Intent(this@ChatActivity, FuguInnerChatActivity::class.java)
            val message = (diffUtilMessageList[position])
            innectChatIntent.putExtra("isFromChatActivity", 1)
            innectChatIntent.putExtra("MESSAGE", message)
            innectChatIntent.putExtra(FuguAppConstant.CHANNEL_ID, channelId)
            innectChatIntent.putExtra("BUSINESS_NAME", conversation.businessName)
            innectChatIntent.putExtra("chatType", chatType)
            innectChatIntent.putExtra("label", conversation.label)
            innectChatIntent.putExtra("userType", messageAdapter?.getUserType())
            innectChatIntent.putExtra("only_admin_can_message", onlyAdminsCanMessage)
            innectChatIntent.putExtra(MESSAGE, message)
            if (PRDownloader.getStatus(message.downloadId) == Status.RUNNING) {
                PRDownloader.pause(message.downloadId)
            }
            messageAdapter?.switchPerformClickCount()
            openedThread()
            object : Thread() {
                override fun run() {
                    super.run()
                    val listItems = java.util.ArrayList<Message>()
                    listItems.addAll(diffUtilMessageList)
                    if (isSearchedMessage) {
                        ChatDatabase.setMessageList(listItems, channelId)
                        ChatDatabase.setMessageMap(messagesMap, channelId)
                    }
                }
            }.start()
            startActivityForResult(innectChatIntent, 20022)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }
    }

    fun makeHangoutsCall() {
        val commonResponseData = com.skeleton.mvp.data.db.CommonData.getCommonResponse().data
        val workspacesInfo = commonResponseData.workspacesInfo
        val currentPosition = com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()
        val commonParams = CommonParams.Builder()
                .add(FuguAppConstant.EN_USER_ID, workspacesInfo[currentPosition].enUserId)
                .add("is_scheduled", 0)
                .add("timezone", TimeZone.getTimeZone(Time.getCurrentTimezone()).getOffset(Date().time) / 1000 / 60)
                .add(USER_ID, otherUserid)
                .addDomain()
        showLoading()
        RestClient.getApiInterface().addCalendarEvent(commonResponseData.userInfo.accessToken, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                .enqueue(object : ResponseResolver<AddEventResponse>() {
                    override fun success(t: AddEventResponse?) {
                        hideLoading()
                        val hangoutLink = t?.data?.hangoutLink
                        if (hangoutLink == null) {
                            showErrorMessage("Meet is disabled in your google account settings. Please change your connected google account.")
                            return
                        }
                        val hangoutIntent = Intent(this@ChatActivity, HangoutsCallActivity::class.java)
                        hangoutIntent.putExtra("hangoutLink", hangoutLink)
                        val name = if (!TextUtils.isEmpty(conversation.label)) {
                            conversation.label
                        } else {
                            conversation.businessName
                        }
                        val turnCredential = com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.turnCredentials
                        val videoCallModel = VideoCallModel(
                                channelId, userImage, name,
                                userId, otherUserid, userName, turnCredential.turnApiKey,
                                turnCredential.username, turnCredential.credentials,
                                (turnCredential.iceServers.stun as java.util.ArrayList<String>?)!!,
                                (turnCredential.iceServers.turn as java.util.ArrayList<String>?)!!,
                                WebRTCCallConstants.AcitivityLaunchState.SELF.toString(),
                                UUID.randomUUID().toString(),
                                "HANGOUTS"
                        )
                        hangoutIntent.putExtra("videoCallModel", videoCallModel)
                        startActivity(hangoutIntent)
                    }

                    override fun failure(error: APIError?) {
                        hideLoading()
                        showErrorMessage(error?.message ?: "Error")
                    }

                })
    }

    fun openVideoCallActivity(callType: String) {
        mCallType = callType
        if (!GeneralFunctions().isMyServiceRunning(OngoingCallService::class.simpleName, this@ChatActivity)) {
            val videoIntent = Intent(this@ChatActivity, JitsiCallActivity::class.java)
            var name = ""
            name = if (!TextUtils.isEmpty(conversation.label)) {
                conversation.label
            } else {
                conversation.businessName
            }
            val turnCredential =
                    com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.turnCredentials
            val videoCallModel = VideoCallModel(
                    channelId, userImage, name,
                    userId, otherUserid, userName, turnCredential.turnApiKey,
                    turnCredential.username, turnCredential.credentials,
                    (turnCredential.iceServers.stun as java.util.ArrayList<String>?)!!,
                    (turnCredential.iceServers.turn as java.util.ArrayList<String>?)!!,
                    WebRTCCallConstants.AcitivityLaunchState.SELF.toString(),
                    UUID.randomUUID().toString(),
                    callType
            )
            videoIntent.putExtra("videoCallModel", videoCallModel)
            startActivityForResult(videoIntent, 1090)
        }
    }


    private fun openOldVideoCallActivity(callType: String) {
        if (!GeneralFunctions().isMyServiceRunning(VideoCallService::class.simpleName, this@ChatActivity)) {
            val videoIntent = Intent(this@ChatActivity, FuguCallActivity::class.java)
            var name = ""
            name = if (!TextUtils.isEmpty(conversation.label)) {
                conversation.label
            } else {
                conversation.businessName
            }
            val turnCredential =
                    com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.turnCredentials
            val videoCallModel = VideoCallModel(
                    channelId, userImage, name,
                    userId, otherUserid, userName, turnCredential.turnApiKey,
                    turnCredential.username, turnCredential.credentials,
                    (turnCredential.iceServers.stun as java.util.ArrayList<String>?)!!,
                    (turnCredential.iceServers.turn as java.util.ArrayList<String>?)!!,
                    WebRTCCallConstants.AcitivityLaunchState.SELF.toString(),
                    UUID.randomUUID().toString(),
                    callType
            )
            videoIntent.putExtra("videoCallModel", videoCallModel)
            startActivity(videoIntent)
        }
    }

    private val mVideoCallHungUp = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            tvReturnCall.visibility = View.GONE
            ivVideoCall.setOnClickListener(this@ChatActivity)
            ivAudioCall.setOnClickListener(this@ChatActivity)
            ivVideoCall.alpha = 1f
            ivAudioCall.alpha = 1f
        }

    }

    override fun sendPollOption(jsonObject: JSONObject) {
        GeneralFunctions().sendPollOption(
                jsonObject, channelId, userId,
                workspaceInfoList, currentPosition, this@ChatActivity
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onItemClick(botAction: FuguBotAdapter.BotAction) {
        selectedBotAction = botAction
        if (rvBotActions != null) {
            etMessage.setText(Html.fromHtml("<b>" + botAction.displayTag + " " + "</b>"))
            etMessage.setSelection(etMessage.text!!.length)
            llBotAction?.visibility = View.VISIBLE
            tvTag?.text = botAction.displayTag
            tvInputParameter?.text = botAction.inputParamerter
        }
    }

    fun starMessage(position: Int, muid: String, isStarred: Int, location: IntArray) {
        messageAdapter?.setOnLongClickValue(false)
        showLoading()
        ApiStarMessage().starMessage(muid, isStarred, channelId, enUserId,
                workspaceInfoList, currentPosition, object : ApiStarMessage.StarMessageCallBack {
            override fun onSuccess(t: com.skeleton.mvp.data.model.CommonResponse?) {
                hideLoading()
                val message = messagesMap[muid]
                if (isStarred == 1) {
                    message?.isStarred = 0
                    if (rl != null && iv != null) {
                        rl?.removeView(iv)
                    }
                } else {
                    message?.isStarred = 1
                    rl = findViewById(R.id.my_relative_layout)
                    val params: RelativeLayout.LayoutParams =
                            RelativeLayout.LayoutParams(50, 500)
                    val params2: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT
                    )
                    iv = ImageView(this@ChatActivity)
                    iv?.setImageResource(R.drawable.ic_star_animate_24dp)
                    iv?.layoutParams = params
                    params2.setMargins(location[0] - 60, location[1] - 485, 0, 0)
                    rl?.layoutParams = params2
                    rl?.addView(iv)
                    starAnimation =
                            AnimationUtils.loadAnimation(applicationContext, R.anim.star_enlarge)
                    starAnimation?.setAnimationListener(this@ChatActivity)
                    iv?.clearAnimation()
                    iv?.startAnimation(starAnimation)
                }
                messagesMap[muid] = message!!
                messageList = ArrayList<Message>()
                //messageList.addAll(diffUtilMessageList)
                for (message in diffUtilMessageList) {
                    val messageClone = message.clone()
                    messageList.add(messageClone as Message)
                }
                messageAdapter?.updateMessageList(messageList)
                //messageAdapter?.notifyItemChanged(message.messageIndex)
            }

            override fun onFailure(error: APIError?) {
                hideLoading()
            }
        })
    }

    override fun onCameraButtonClick(button: Button, muid: String, pos: Int) {
        camerabutton = button
        cameraMuid = muid
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            pbVerify?.visibility = View.VISIBLE
            ivSend.visibility = View.GONE
            Handler().postDelayed({
                val takePictureIntent = Intent(this, CameraActivity::class.java)
                startActivityForResult(takePictureIntent, 1005)
            }, 300)
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    1004
            )
        }
    }

    override fun onVideoConferenceJoined(button: Button, muid: String, pos: Int, inviteLink: String) {
        try {
            val linkArray = inviteLink.split("invite_link=")
            val intent = Intent(this@ChatActivity, VideoConfActivity::class.java)
            intent.putExtra("base_url", com.skeleton.mvp.fugudatabase.CommonData.getConferenceUrl())
            intent.putExtra("room_name", linkArray[linkArray.size - 1])
            startActivity(intent)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onButtonClick(button: Button, muid: String, pos: Int) {
        val message = messagesMap[muid]!!
        when (button.actionType.toUpperCase()) {
            "TEXT_FIELD" -> {
                message.customActions[pos].isShowTextField = true
                message.customActions[pos].lastClickedButton = button
                diffUtilMessageList[message.messageIndex] = message
                messagesMap[muid] = message
                messageList = ArrayList<Message>()
                //messageList.addAll(diffUtilMessageList)
                for (message in diffUtilMessageList) {
                    val messageClone = message.clone()
                    messageList.add(messageClone as Message)
                }
                messageAdapter?.updateMessageList(messageList)
                //messageAdapter?.notifyItemChanged(message.messageIndex)
            }
            "MESSAGE_PUBLISH" -> {
            }
            else -> {
                if (message.customActions[pos].defaultTextField != null && button.id == message.customActions[pos].defaultTextField.button_id) {
                    if (message.customActions[pos].defaultTextField.isRequired
                            && message.customActions[pos].comment.length >= message.customActions[pos].defaultTextField.minimumLength
                    ) {
                        try {
                            publishSocketClick(button, muid, pos)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        showErrorMessage("Please enter comment of atleast " + message.customActions[pos].defaultTextField.minimumLength + " characters!")
                    }
                } else {
                    try {
                        publishSocketClick(button, muid, pos)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun onCreateGroup(button: Button, muid: String, pos: Int) {
        val intent = Intent(this@ChatActivity, MembersSearchActivity::class.java)
        startActivity(intent)
        publishSocketClick(button, muid, pos)
    }

    override fun onBrowseGroup(button: Button, muid: String, pos: Int) {
        startActivity(Intent(this@ChatActivity, BrowseGroupActivity::class.java))
        publishSocketClick(button, muid, pos)
    }

    override fun onInviteMember(button: Button, muid: String, pos: Int) {
        val intent = Intent(this@ChatActivity, InviteOnboardActivity::class.java)
        intent.putExtra(AppConstants.EXTRA_ALREADY_MEMBER, AppConstants.EXTRA_ALREADY_MEMBER)
        startActivity(intent)
        publishSocketClick(button, muid, pos)
    }

    override fun onCreateWorkspace(button: Button, muid: String, pos: Int) {
        val intent = Intent(this@ChatActivity, CreateWorkspaceActivity::class.java)
        startActivity(intent)
        publishSocketClick(button, muid, pos)
    }

    private fun publishSocketClick(button: Button, muid: String, pos: Int) {
        PublishBotMessage().publishSocketClick(button,
                muid,
                pos,
                this@ChatActivity,
                diffUtilMessageList,
                messagesMap,
                messageAdapter,
                userId,
                channelId,
                object : BotButtonActions.BotButtonsCallBack {
                    override fun onDoneClicked(
                            updatedMessagesMap: LinkedHashMap<String, Message>,
                            updatedMessageList: ArrayList<Message>
                    ) {
                        diffUtilMessageList = updatedMessageList
                        messagesMap = updatedMessagesMap
                    }
                })
        if (messagesMap[muid]!!.customActions[pos].confirmationType == "DISABLE_WORKSPACE" && button.action == "Confirm") {
            val fcCommonResponse = com.skeleton.mvp.data.db.CommonData.getCommonResponse()
            if (fcCommonResponse.getData().workspacesInfo.size > 0) {
                fcCommonResponse.getData().workspacesInfo.removeAt(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition())
                fcCommonResponse.getData().workspacesInfo[0].currentLogin = true
                com.skeleton.mvp.data.db.CommonData.setCommonResponse(fcCommonResponse)
                com.skeleton.mvp.data.db.CommonData.setCurrentSignedInPosition(0)
                finish()
            } else {
                finishAffinity()
                startActivity(Intent(this@ChatActivity, CreateWorkspaceActivity::class.java))
            }
        }
    }

    override fun onCancelClick(muid: String) {
    }

    override fun onDoneClick(muid: String, comment: String, pos: Int) {
        BotButtonActions().onDoneClick(muid,
                comment,
                pos,
                this@ChatActivity,
                messagesMap,
                diffUtilMessageList,
                messageAdapter,
                userId,
                channelId,
                object : BotButtonActions.BotButtonsCallBack {
                    override fun onDoneClicked(
                            updatedMessagesMap: LinkedHashMap<String, Message>,
                            updatedMessageList: ArrayList<Message>
                    ) {
                        diffUtilMessageList = updatedMessageList
                        messagesMap = updatedMessagesMap
                    }
                })
    }

    override fun onTextChange(pos: Int, text: String, muid: String) {
        val message = messagesMap[muid]!!
        if (message.customActions[pos].defaultTextField != null && message.customActions[pos].defaultTextField.output.toUpperCase().equals(
                        "COMMENT"
                )
        ) {
            message.customActions[pos].comment = text
        } else {
            message.customActions[pos].remark = text
        }
        messagesMap[muid] = message
//        diffUtilMessageList[message.messageIndex] = message
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CONSTANT_CAMERA && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
            fuguImageUtils?.startCamera()
        } else if (requestCode == 1001 && grantResults[0] == PermissionChecker.PERMISSION_GRANTED
                && grantResults[1] == PermissionChecker.PERMISSION_GRANTED
        ) {
            pbVerify?.visibility = View.VISIBLE
            ivSend.visibility = View.GONE
            val locationFetcher = LocationFetcher()
            locationFetcher.init(this@ChatActivity)
            if (GeneralFunctions().isGPSTurnedOn(this)) {
                Handler().postDelayed({
                    val takePictureIntent = Intent(this, CameraActivity::class.java)
                    startActivityForResult(takePictureIntent, 1004)
                }, 200)
            }
        } else if (requestCode == 1002 && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
            pbVerify?.visibility = View.VISIBLE
            ivSend.visibility = View.GONE
            Handler().postDelayed({
                val takePictureIntent = Intent(this, CameraActivity::class.java)
                startActivityForResult(takePictureIntent, 1004)
            }, 300)
        } else if (requestCode == 1003 && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
            pbVerify?.visibility = View.VISIBLE
            ivSend.visibility = View.GONE
            val locationFetcher = LocationFetcher()
            locationFetcher.init(this@ChatActivity)
        } else if (requestCode == 1004 && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
            Handler().postDelayed({
                val takePictureIntent = Intent(this, CameraActivity::class.java)
                startActivityForResult(takePictureIntent, 1005)
            }, 300)
        }
    }

    fun forwardMessage(position: Int, muid: String) {
        if (position < diffUtilMessageList.size && diffUtilMessageList[position] != null) {
            val mIntent = Intent(this, ForwardActivity::class.java)
            val forwardMessage = diffUtilMessageList[position]
            mIntent.putExtra("MESSAGE", forwardMessage)
            mIntent.putExtra("BUSINESS_NAME", conversation.label)
            mIntent.putExtra("chatType", conversation.chat_type)
            startActivity(mIntent)
        }
    }

    override fun OnLocationFetchingComplete(location: Location) {
        if (location.isFromMockProvider) {
            isMockLocation = location.isFromMockProvider
        } else {
            lat = location.latitude
            lon = location.longitude
        }
        if (action.equals(AttendanceAuthenticationLevel.LOCATION.toString())) {
            apiVerifyAttendanceCredentials(null)
        }
    }

    fun getState(): Int {
        return touchState
    }

    override fun onAdapterItemTouch(state: Int) {
        touchState = state
    }

    fun setRecyclerViewAddedMembers(member: Member?) {
        if (member != null) {
            val membersToBeSent = ArrayList<Member>()
            membersToBeSent.addAll(membersList)
            userIdsSearch = ArrayList(multiMemberAddGroupMap.keys)
            if (multiMemberAddGroupMap.size != 0) {
                if (multiMemberAddGroupMap[member.userId] != null) {
                    multiMemberAddGroupMap.remove(member.userId)
                    userIdsSearch.remove(member.userId)
                    val videoCallInvitationBottomSheetFragment =
                            supportFragmentManager.findFragmentByTag("VideoCallInvitationBottomSheetFragment") as VideoCallInvitationBottomSheetFragment
                    videoCallInvitationBottomSheetFragment.updateBottomSheet(
                            membersToBeSent,
                            userIdsSearch,
                            multiMemberAddGroupMap,
                            false
                    )
                } else {
                    addMemberToList(member)
                }
            } else {
                addMemberToList(member)
            }
        } else {
            val membersToBeSent = ArrayList<Member>()
            membersToBeSent.addAll(membersList)
            val videoCallInvitationBottomSheetFragment =
                    supportFragmentManager.findFragmentByTag("VideoCallInvitationBottomSheetFragment") as VideoCallInvitationBottomSheetFragment
            videoCallInvitationBottomSheetFragment.updateBottomSheet(
                    membersToBeSent,
                    userIdsSearch,
                    multiMemberAddGroupMap,
                    false
            )
        }
    }

    private fun addMemberToList(getAllMembers: Member) {
        val membersToBeSent = ArrayList<Member>()
        membersToBeSent.addAll(membersList)
        userIdsSearch.add(getAllMembers.userId)
        multiMemberAddGroupMap[getAllMembers.userId] = getAllMembers
        val videoCallInvitationBottomSheetFragment =
                supportFragmentManager.findFragmentByTag("VideoCallInvitationBottomSheetFragment") as VideoCallInvitationBottomSheetFragment
        if (userIdsSearch.size > 0) {
            videoCallInvitationBottomSheetFragment.updateBottomSheet(
                    membersToBeSent,
                    userIdsSearch,
                    multiMemberAddGroupMap,
                    false
            )
        } else {
            videoCallInvitationBottomSheetFragment.updateBottomSheet(
                    membersToBeSent,
                    userIdsSearch,
                    multiMemberAddGroupMap,
                    false
            )
        }
    }

    fun updateAllMemberAdapter(userId: Long) {
        val membersToBeSent = ArrayList<Member>()
        membersToBeSent.addAll(membersList)
        if (userIdsSearch.contains(userId)) {
            userIdsSearch.remove(userId)
        }
        val videoCallInvitationBottomSheetFragment =
                supportFragmentManager.findFragmentByTag("VideoCallInvitationBottomSheetFragment") as VideoCallInvitationBottomSheetFragment
        videoCallInvitationBottomSheetFragment.updateBottomSheet(
                membersToBeSent,
                userIdsSearch,
                multiMemberAddGroupMap,
                true
        )
    }

    fun apiInitiateVideoConference(userIds: java.util.ArrayList<Long>, isHangoutsMeet: Boolean = false) {
        if (isHangoutsMeet)
            ApiInitiateVideoConference().apiInitiateHangoutsConference(
                    userIds,
                    this,
                    enUserId,
                    appSecretKey
            )
        else
            ApiInitiateVideoConference().apiInitiateVideoConference(
                    userIds,
                    this,
                    enUserId,
                    appSecretKey
            )
    }

    override fun onEditTextChangeListener(rootView: View, text: String, colorCode: Int) {
        val textEditorDialogFragment = TextEditorDialogFragment.show(this, text, colorCode)
        textEditorDialogFragment.setOnTextEditorListener { inputText, colorCode ->
            mPhotoEditor?.editText(
                    rootView,
                    inputText,
                    colorCode
            )
        }
    }

    override fun onAddViewListener(viewType: ViewType, numberOfAddedViews: Int) {
        if (numberOfAddedViews > 0) {
            imgUndo?.visibility = View.VISIBLE
        } else {
            imgUndo?.visibility = View.GONE
        }
    }

//    override fun onRemoveViewListener(numberOfAddedViews: Int) {
//        if (numberOfAddedViews == 0) {
//            imgUndo?.visibility = View.GONE
//        }
//    }


    override fun onRemoveViewListener(viewType: ViewType, numberOfAddedViews: Int) {
        if (numberOfAddedViews == 0) {
            imgUndo?.visibility = View.GONE
        }
    }

    override fun onStartViewChangeListener(viewType: ViewType) {
    }

    override fun onStopViewChangeListener(viewType: ViewType) {
    }

    @SuppressLint("MissingPermission")
    private fun saveImage(
            extension: String,
            dimens: ArrayList<Int>,
            fuguFileDetails: FuguFileDetails,
            imageDialog: Dialog?,
            etMsg: EmojiGifEditText
    ) {
        if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showImageEditorLoading("Saving...")
            val file = File(fuguFileDetails.filePath)
            try {
                val saveSettings =
                        SaveSettings.Builder().setClearViewsEnabled(true).setTransparencyEnabled(true)
                                .build()
                mPhotoEditor?.saveAsFile(
                        file.absolutePath,
                        saveSettings,
                        object : PhotoEditor.OnSaveListener {
                            override fun onSuccess(imagePath: String) {
                                hideImageEditorLoading()
                                var fileOutputStream: FileOutputStream?
                                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                                val inputStream = contentResolver.openInputStream(FileProvider.getUriForFile(this@ChatActivity, BuildConfig.APPLICATION_ID + ".provider", File(file.absolutePath)))!!
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
                                val message: String =
                                        GeneralFunctions().getTaggedMessage(mentionsList, etMsg)
                                compressAndSaveImageBitmap(
                                        message,
                                        extension,
                                        dimens,
                                        IMAGE_MESSAGE,
                                        fuguFileDetails
                                )
                                imageDialog?.dismiss()
                                try {
                                    if (File(CommonData.getImageUri()).exists()) {
                                        File(CommonData.getImageUri()).delete()
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
                showSnackbar(e.message!!)
            }
        }
    }

    override fun onColorChanged(colorCode: Int) {
        mPhotoEditor?.brushColor = colorCode
    }

    override fun onOpacityChanged(opacity: Int) {
        mPhotoEditor?.setOpacity(opacity)
    }

    override fun onBrushSizeChanged(brushSize: Int) {
        mPhotoEditor?.brushSize = brushSize.toFloat()
    }

    override fun onEmojiClick(emojiUnicode: String) {
        mPhotoEditor?.addEmoji(emojiUnicode)
    }

    override fun isPermissionGranted(isGranted: Boolean, permission: String) {

    }

    fun sendImageFile(image: ImageFile) {
        var extension = ""
        val cursor = this@ChatActivity.contentResolver.query(
                getUriFromPath(image.path),
                null,
                null,
                null,
                null
        )
        cursor.use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                try {
                    val splitArray =
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                                    .split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    extension = splitArray[splitArray.size - 1].toLowerCase()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        if (extension.equals("png")) {
            extension = "jpg"
            val bitmap = BitmapFactory.decodeFile(image.path)
            val inputStream = contentResolver.openInputStream(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", File(image.path)))!!
            val fileOutputStream = FileOutputStream(File(image.path))
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
        val dimensGallery = getImageHeightAndWidth(getUriFromPath(image.path))
        val localDate = DateUtils.getFormattedDate(Date())
        val fileDetails = fuguImageUtils?.saveFile(
                getUriFromPath(image.path),
                FILE_TYPE_MAP[extension],
                channelId,
                localDate
        )
        compressAndSaveImageBitmap(
                "",
                extension,
                dimensGallery,
                IMAGE_MESSAGE,
                fileDetails!!
        )
    }

    fun openMessageInfo(isThreadMessage: Boolean, muid: String) {
        if (messagesMap[muid] != null) {
            val intent = Intent(this@ChatActivity, MessageInformationActivity::class.java)
            intent.putExtra(CHANNEL_ID, channelId)
            intent.putExtra(MESSAGE, messagesMap[muid])
            intent.putExtra(IS_THREAD_MESSAGE, isThreadMessage)
            startActivity(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }
    }

    fun ellipsizeText(text: String): CharSequence {
        val s = SpannableString(text)
        s.setSpan(
                TrimmedTextView.EllipsizeRange.ELLIPSIS_AT_END, 0, s.length,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return s
    }

    fun openScreenFromSheet(id: Int?) {
        when (id) {
            R.id.llCamera -> {
                fuguImageUtils?.startCamera()
            }
            R.id.llGallery -> {
                val intent1 = Intent(this, ImagePickActivity::class.java)
                startActivityForResult(intent1, REQUEST_CODE_PICK_IMAGE)
            }
            R.id.llAudio -> {
                val intent = Intent(this, AudioPickActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE_PICK_AUDIO)
            }
            R.id.llVideo -> {
                val intent = Intent(this, VideoPickActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE_PICK_VIDEO)
            }
            R.id.llFiles -> {
                val intent = Intent(this, NormalFilePickActivity::class.java)
                val mimeTypes = arrayOf(
                        "txt", "xlsx", "xls", "doc", "docX", "ppt", ".pptx", "pdf",
                        "ODT", "apk", "zip", "CSV", "SQL", "PSD"
                )
                intent.putExtra(FuguAppConstant.SUFFIX, mimeTypes)
                startActivityForResult(intent, REQUEST_CODE_PICK_FILE)
            }
            R.id.llPoll -> {
                fuguImageUtils?.openCreatePollActivity()
            }
            else -> {
            }
        }
    }

    fun getChannelId(): Long {
        return channelId
    }

}

