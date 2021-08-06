package com.skeleton.mvp.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Color
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.FuguConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.adapter.GalleryAdapter
import com.skeleton.mvp.adapter.GroupSettingsAdapter
import com.skeleton.mvp.adapter.MembersAdapter
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.data.db.ChatDatabase
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.creategroup.MembersInfo
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.fragment.NotificationBottomSheetFragment
import com.skeleton.mvp.fragment.SendMessagesBottomSheetFragment
import com.skeleton.mvp.groupTasks.TaskActivity
import com.skeleton.mvp.groupTasks.ViewGroupTasksActivity
import com.skeleton.mvp.model.GroupMember
import com.skeleton.mvp.model.Image
import com.skeleton.mvp.model.Media
import com.skeleton.mvp.model.editInfo.EditInfoResponse
import com.skeleton.mvp.model.group.GroupResponse
import com.skeleton.mvp.model.media.ChatMember
import com.skeleton.mvp.model.media.MediaResponse
import com.skeleton.mvp.retrofit.*
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.ui.dialog.CustomAlertDialog
import com.skeleton.mvp.ui.profile.ProfileActivity
import com.skeleton.mvp.util.KeyboardUtil
import com.skeleton.mvp.util.Log
import com.skeleton.mvp.utils.FuguImageUtils
import com.skeleton.mvp.utils.FuguUtils
import com.skeleton.mvp.utils.FuguUtils.Companion.getFirstCharInUpperCase
import com.skeleton.mvp.utils.HeaderView
import com.theartofdev.edmodo.cropper.CropImage
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class GroupInformationActivity : BaseActivity(), AppBarLayout.OnOffsetChangedListener, View.OnClickListener, MembersAdapter.ExpandList {
    protected var toolbarHeaderView: HeaderView? = null
    protected var floatHeaderView: HeaderView? = null
    protected var appBarLayout: AppBarLayout? = null
    protected var toolbar: Toolbar? = null
    private var isHideToolbarView = false
    private var rvTopOptions: androidx.recyclerview.widget.RecyclerView? = null
    private var groupSettingList = ArrayList<GroupSettingsAdapter.GroupSetting>()
    private var groupSettingsAdapter: GroupSettingsAdapter? = null
    private var workspaceInfo: WorkspacesInfo? = null
    private var isCustomLabel: Boolean = false
    private var isJoined: Boolean = false
    private var isPrivate: Boolean = false
    private var currentUser: ChatMember? = null
    private var currentGroupMember: GroupMember? = null
    private var membersList = java.util.ArrayList<GroupMember>()
    private var membersAdapter: MembersAdapter? = null
    private var image_one: ImageView? = null
    private var image_main: ImageView? = null
    private var image_two: ImageView? = null
    private var image_three: ImageView? = null
    private var llOne: LinearLayout? = null
    private var llTwo: LinearLayout? = null
    private var viewOne: View? = null
    private var viewTwo: View? = null
    private var groupInfoName: TextView? = null
    private var groupInfoCreatedBy: TextView? = null
    var chatType = ChatType.PRIVATE_GROUP
    private var ivEdit: ImageView? = null
    private var collapsingToolbarLayout: CollapsingToolbarLayout? = null
    private var groupNameGlobal: String = ""
    private var llAddMember: LinearLayout? = null
    private var isDilogOpened = false
    private var tvParticipants: TextView? = null
    private var ivSearchUsers: ImageView? = null
    private var cvExit: LinearLayout? = null
    private var cvDelete: LinearLayout? = null
    private var cvDeleteGroup: LinearLayout? = null
    private var cvMedia: LinearLayout? = null
    private var cvSettings: LinearLayout? = null
    private var divider: View? = null
    private var isGroupImageAvailable = false
    private var imageUrl = ""
    private var extension = ""
    private var channelId = -1L
    private var mediaResponseMain: MediaResponse? = null
    private var rvMedia: androidx.recyclerview.widget.RecyclerView? = null
    private var mediaAdapter: GalleryAdapter? = null
    private var mediaList = java.util.ArrayList<Media>()
    private var tvShowMore: LinearLayout? = null
    private var isExpanded = false

    private var rlDummy: RelativeLayout? = null
    private var rlDummyTwo: RelativeLayout? = null
    private var rlDummyThree: RelativeLayout? = null

    private var imageDummy: ImageView? = null
    private var imageDummyTwo: ImageView? = null
    private var imageDummyThree: ImageView? = null

    private var tvDummy: TextView? = null
    private var tvDummyTwo: TextView? = null
    private var tvDummyThree: TextView? = null
    var fuguImageUtils: FuguImageUtils? = null
    private var userCount = 0L
    private var userPageSize = 0
    private var notificationLevel = ""
    private var sendMessages = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_information)
        fuguImageUtils = FuguImageUtils(this)
        fuguImageUtils?.setCallbaks(FuguAppConstant.OPEN_CAMERA_ADD_IMAGE, REQUEST_CODE_PICK_IMAGE, REQUEST_CODE_PICK_FILE,
                REQUEST_CODE_PICK_AUDIO, REQUEST_CODE_PICK_VIDEO, FuguAppConstant.START_POLL, true, true)
        initializeViews()
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        fetchIntentData()
        initUi()
    }

    private fun initializeViews() {
        toolbarHeaderView = findViewById(R.id.toolbar_header_view)
        floatHeaderView = findViewById(R.id.float_header_view)
        appBarLayout = findViewById(R.id.appbar)
        toolbar = findViewById(R.id.toolbar)
        image_one = findViewById(R.id.image)
        image_main = findViewById(R.id.imageMain)
        image_two = findViewById(R.id.image_two)
        image_three = findViewById(R.id.image_three)
        llOne = findViewById(R.id.llOne)
        llTwo = findViewById(R.id.llTwo)
        viewOne = findViewById(R.id.view_one)
        viewTwo = findViewById(R.id.view_two)
        ivEdit = findViewById(R.id.ivEdit)
        tvShowMore = findViewById(R.id.tvShowMore)
        groupInfoName = findViewById(R.id.group_info_name)
        groupInfoCreatedBy = findViewById(R.id.group_info_last_seen)
        llAddMember = findViewById(R.id.llAddMember)
        tvParticipants = findViewById(R.id.tvParticipants)
        ivSearchUsers = findViewById(R.id.ivSearchUsers)
        cvExit = findViewById(R.id.cvExit)
        cvDelete = findViewById(R.id.cvDelete)
        cvMedia = findViewById(R.id.cvMedia)
        cvSettings = findViewById(R.id.cvSettings)
        divider = findViewById(R.id.divider)
        rvMedia = findViewById(R.id.rvMedia)
        cvDeleteGroup = findViewById(R.id.cvDeleteGroup)

        rlDummy = findViewById(R.id.rlDummy)
        rlDummyTwo = findViewById(R.id.rlDummyTwo)
        rlDummyThree = findViewById(R.id.rlDummyThree)
        imageDummy = findViewById(R.id.imageDummy)
        imageDummyTwo = findViewById(R.id.imageDummyTwo)
        imageDummyThree = findViewById(R.id.imageDummyThree)
        tvDummy = findViewById(R.id.tvDummy)
        tvDummyTwo = findViewById(R.id.tvDummyTwo)
        tvDummyThree = findViewById(R.id.tvDummyThree)


        llAddMember?.setOnClickListener(this)
        ivSearchUsers?.setOnClickListener(this)
        cvSettings?.setOnClickListener(this)
        image_main?.setOnClickListener(this)
        cvExit?.setOnClickListener(this)
        cvDelete?.setOnClickListener(this)
        cvDeleteGroup?.setOnClickListener(this)
    }

    private fun fetchIntentData() {
        isCustomLabel = intent.getBooleanExtra("isCustomLabel", false)
        workspaceInfo = CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()]
        isJoined = intent.getBooleanExtra("isJoined", true)
        chatType = com.skeleton.mvp.fugudatabase.CommonData.getChatType()
        groupNameGlobal = intent.getStringExtra("groupName") ?: ""
        channelId = intent.getLongExtra("channelId", -1L)
        appBarLayout?.addOnOffsetChangedListener(this)
    }

    private fun initUi() {
        showLoading()
        apiGetGroupInfo(true)
    }

    private fun setUpToolBar(mutedColor: Int?) {
        collapsingToolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        collapsingToolbarLayout?.setContentScrimColor(mutedColor!!)
        window.statusBarColor = manipulateColor(mutedColor!!, 0.7f)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        val maxScroll = appBarLayout?.totalScrollRange
        val percentage = Math.abs(verticalOffset).toFloat() / maxScroll!!.toFloat()
        collapsingToolbarLayout?.title = " "
        if (percentage == 1f && isHideToolbarView) {
            toolbarHeaderView?.visibility = View.VISIBLE
            isHideToolbarView = !isHideToolbarView

        } else if (percentage < 1f && !isHideToolbarView) {
            toolbarHeaderView?.visibility = View.GONE
            isHideToolbarView = !isHideToolbarView
        }
    }

    fun apiGetGroupInfo(updateGroupMedia: Boolean) {
        val commonParams = CommonParams.Builder()
                .add(CHANNEL_ID, channelId)
                .add(EN_USER_ID, workspaceInfo?.enUserId)
                .add(GET_DATA_TYPE, DEFAULT)
                .add(USER_PAGE_START, 0)
                .build()

        RestClient.getApiInterface().getGroupInfo(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), workspaceInfo?.fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.map)
                .enqueue(object : ResponseResolver<MediaResponse>() {
                    @SuppressLint("SetTextI18n")
                    override fun success(mediaResponse: MediaResponse?) {
                        hideLoading()
                        mediaResponseMain = mediaResponse
                        setUpData(mediaResponse, updateGroupMedia)

                    }

                    override fun failure(error: APIError?) {
                        hideLoading()
                    }

                })
    }

    private fun setUpData(mediaResponse: MediaResponse?, updateGroupMedia: Boolean) {
        membersList = ArrayList()
        groupSettingList = ArrayList()
        mediaList = ArrayList()
        rvTopOptions = findViewById(R.id.rvTopOptions)
        for (member in mediaResponse?.data?.chatMembers!!) {
            if (member.userId.toLong().compareTo(workspaceInfo?.userId!!.toLong()) == 0) {
//                if (chatType == ChatType.GENERAL_GROUP || chatType == ChatType.DEFAULT_GROUP) {
//                    member.role = "USER"
//                }
                currentUser = member

                if (currentUser?.role.equals(Role.ADMIN.toString()) && (chatType == ChatType.PRIVATE_GROUP || chatType == ChatType.PUBLIC_GROUP)) {
                    cvDeleteGroup?.visibility = View.VISIBLE
                } else {
                    cvDeleteGroup?.visibility = View.GONE
                }
                if (currentUser?.role.equals(Role.ADMIN.toString()) && chatType == ChatType.PRIVATE_GROUP) {
                    ivEdit?.visibility = View.VISIBLE
                    ivEdit?.setOnClickListener(this@GroupInformationActivity)
                    llOne?.setOnClickListener(this@GroupInformationActivity)
                    llTwo?.setOnClickListener(this@GroupInformationActivity)
                } else if (chatType == ChatType.PUBLIC_GROUP && isJoined) {
                    ivEdit?.visibility = View.VISIBLE
                    ivEdit?.setOnClickListener(this@GroupInformationActivity)
                    llOne?.setOnClickListener(this@GroupInformationActivity)
                    llTwo?.setOnClickListener(this@GroupInformationActivity)
                } else if ((chatType == ChatType.GENERAL_GROUP || chatType == ChatType.DEFAULT_GROUP) && workspaceInfo?.role.equals("ADMIN") || workspaceInfo?.role.equals("OWNER")) {
                    ivEdit?.visibility = View.GONE
                    llOne?.setOnClickListener(this@GroupInformationActivity)
                    llTwo?.setOnClickListener(this@GroupInformationActivity)
                } else {
                    ivEdit?.visibility = View.GONE
                    llOne?.setOnClickListener(null)
                    llTwo?.setOnClickListener(null)
                }
                if (chatType == FuguAppConstant.ChatType.DEFAULT_GROUP && workspaceInfo?.role.equals("ADMIN") || workspaceInfo?.role.equals("OWNER")) {
                    ivEdit?.visibility = View.VISIBLE
                    ivEdit?.setOnClickListener(this@GroupInformationActivity)
                }
            } else {
                if (chatType == ChatType.GENERAL_GROUP || chatType == ChatType.DEFAULT_GROUP) {
                    membersList.add(GroupMember(member.fullName, member.userId!!.toLong(), member.email, member.userImage, member.email, member.role, 0))
                } else {
                    membersList.add(GroupMember(member.fullName, member.userId!!.toLong(), member.email, member.userImage, member.email, member.role, 0))
                }
            }
            if (isJoined) {
                cvDelete?.visibility = View.VISIBLE
            } else {
                cvDelete?.visibility = View.GONE
            }
            if (isJoined) {
                if ((chatType == FuguAppConstant.ChatType.DEFAULT_GROUP || chatType == FuguAppConstant.ChatType.GENERAL_GROUP)) {
                    cvExit?.visibility = View.GONE
                } else {
                    cvExit?.visibility = View.VISIBLE
                }
            } else {
                cvExit?.visibility = View.GONE
            }
            if (isJoined && (chatType != 5 && chatType != 6)) {
                llAddMember?.visibility = View.VISIBLE
                divider?.visibility = View.GONE
            } else {
                llAddMember?.visibility = View.GONE
                divider?.visibility = View.GONE
            }
//            Collections.sort(membersList) { one, other -> one.name.compareTo(other.name) }
        }
        val adminList = ArrayList<GroupMember>()
        val userList = ArrayList<GroupMember>()
        for (member in membersList) {
            if (member.role.equals(Role.ADMIN.toString())) {
                adminList.add(member)
            } else {
                userList.add(member)
            }
        }
        if (chatType != 5 && chatType != 6) {
            adminList.sortWith(Comparator { one, other -> one.name.compareTo(other.name) })
            userList.sortWith(Comparator { one, other -> one.name.compareTo(other.name) })
        }

        membersList = ArrayList()
        if (currentUser != null) {
            if (chatType == ChatType.GENERAL_GROUP || chatType == ChatType.DEFAULT_GROUP) {
                membersList.add(GroupMember(currentUser?.fullName, currentUser?.userId!!.toLong(), currentUser?.email, currentUser?.userImage, currentUser?.email, currentUser?.role, 0))
            } else {
                membersList.add(GroupMember(currentUser?.fullName, currentUser?.userId!!.toLong(), currentUser?.email, currentUser?.userImage, currentUser?.email, currentUser?.role, 0))
            }
        }
        membersList.addAll(adminList)
        membersList.addAll(userList)
        if (!isExpanded && membersList.size > 10) {
            membersList.add(10, GroupMember("", -1L, "", "", "", "", 1))
        }
        tvParticipants?.visibility = View.VISIBLE
        userCount = mediaResponse.data?.userCount!!
        userPageSize = mediaResponse.data?.userPageSize!!
        tvParticipants?.text = userCount.toString() + " members"
        if (mediaResponse.data.channelInfo != null) {
            isPrivate = mediaResponse.data.channelInfo.chatType == ChatType.PRIVATE_GROUP
            if (!TextUtils.isEmpty(mediaResponse.data.channelInfo.ownerName)) {
                toolbarHeaderView?.bindTo(groupNameGlobal, "Created by " + mediaResponse.data.channelInfo.ownerName)
                floatHeaderView?.bindTo(groupNameGlobal, "Created by " + mediaResponse.data.channelInfo.ownerName)
                groupInfoName?.text = groupNameGlobal
                groupInfoCreatedBy?.text = "Created by " + mediaResponse.data.channelInfo.ownerName
            } else {
                toolbarHeaderView?.bindTo(groupNameGlobal, " ")
                floatHeaderView?.bindTo(groupNameGlobal, " ")
            }
        } else {
            toolbarHeaderView?.bindTo(groupNameGlobal, " ")
            floatHeaderView?.bindTo(groupNameGlobal, " ")
        }
        if (mediaResponse.data.channelImageUrl.channelThumbnailUrl == "https://fuguchat.s3.ap-south-1.amazonaws.com/default/WwX5qYGSEb_1518441286074.png") {
            setUpImages(mediaResponse.data.membersInfo)
            isGroupImageAvailable = false
        } else {
            isGroupImageAvailable = true
            llTwo?.visibility = View.GONE
            llOne?.visibility = View.GONE
            viewOne?.visibility = View.GONE
            image_main?.visibility = View.VISIBLE
            setImageToolbar(mediaResponse.data.channelImageUrl.channelThumbnailUrl, image_main)
            imageUrl = mediaResponse.data.channelImageUrl.channelThumbnailUrl
        }
        for (media in mediaResponse.data.chatMedia) {
            if ((media.messageType == 10 || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getCachedFilePath(media.message.url, media.muid))) && mediaList.size < 6) {
                var localPath = ""
                localPath = com.skeleton.mvp.fugudatabase.CommonData.getCachedFilePath(media.message.url, media.muid)
                mediaList.add(Media(media.message.imageUrl,
                        media.message.thumbnailUrl,
                        media.messageType!!,
                        media.message.url,
                        media.message.fileName, localPath,
                        media.message, media.muid,
                        media.isThreadMessage, media.createdAt,
                        media.messageId, media.threadMessageId))
            }
        }
        if (updateGroupMedia) {
            setUpMedia(mediaList)
        }
        setUpGroupSettingsRecycler(currentUser)
        setUpGroupMembers(membersList)
    }

    private fun setUpMedia(mediaList: java.util.ArrayList<Media>) {
        mediaAdapter = GalleryAdapter(this, mediaList)
        rvMedia = findViewById(R.id.rvMedia)
        rvMedia?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        rvMedia?.adapter = mediaAdapter
        rvMedia?.addOnItemTouchListener(GalleryAdapter.RecyclerTouchListener(applicationContext, rvMedia) { _, position ->
            val intent = Intent(this@GroupInformationActivity, MediaActivity::class.java)
            intent.putExtra("position", position)
            intent.putExtra("images", mediaList)
            startActivity(intent)
        })

        if (mediaList.size > 0) {
            cvMedia?.visibility = View.VISIBLE
            if (mediaList.size >= 0) {
                tvShowMore?.visibility = View.VISIBLE
                tvShowMore?.setOnClickListener {
                    val searchIntent = Intent(this@GroupInformationActivity, ShowMoreActivity::class.java)
                    isDilogOpened = false
                    searchIntent.putExtra(CHANNEL_ID, intent.getLongExtra("channelId", -1L))
                    startActivity(searchIntent)
                    overridePendingTransition(R.anim.left_in, R.anim.right_out)
                }
            } else {
                tvShowMore?.visibility = View.GONE
            }
        } else {
            cvMedia?.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

    private fun setUpImages(membersInfo: List<MembersInfo>) {
        when (membersInfo.size) {
            1 -> {
                if (!TextUtils.isEmpty(membersInfo.get(0).userImage)) {
                    llOne?.visibility = View.VISIBLE
                    llTwo?.visibility = View.GONE
                    image_one?.visibility = View.VISIBLE
                    image_two?.visibility = View.GONE
                    image_three?.visibility = View.GONE
                    rlDummy?.visibility = View.GONE
                    rlDummyTwo?.visibility = View.GONE
                    rlDummyThree?.visibility = View.GONE

                    setImageToolbar(membersInfo.get(0).userImage!!, image_one)
                } else {
                    llOne?.visibility = View.VISIBLE
                    llTwo?.visibility = View.GONE
                    image_one?.visibility = View.GONE
                    rlDummy?.visibility = View.VISIBLE
                    tvDummy?.text = getFirstCharInUpperCase(membersInfo[0].fullName)
                    setImageToolbarWithDummy(membersInfo.get(0).userId, imageDummy!!)
                }

            }
            2 -> {
                llOne?.visibility = View.VISIBLE
                llTwo?.visibility = View.VISIBLE
                viewOne?.visibility = View.VISIBLE
                viewTwo?.visibility = View.GONE
                image_three?.visibility = View.GONE
                rlDummyThree?.visibility = View.GONE
                if (TextUtils.isEmpty(membersInfo.get(0).userImage)) {
                    rlDummy?.visibility = View.VISIBLE
                    imageDummy?.visibility = View.VISIBLE
                    image_one?.visibility = View.GONE
                    setImageToolbarWithDummy(membersInfo.get(0).userId, imageDummy!!)
                    tvDummy?.text = getFirstCharInUpperCase(membersInfo[0].fullName)
                } else {
                    image_one?.visibility = View.VISIBLE
                    rlDummy?.visibility = View.GONE
                    setImageToolbar(membersInfo.get(0).userImage!!, image_one)
                }
                if (TextUtils.isEmpty(membersInfo.get(1).userImage)) {
                    rlDummyTwo?.visibility = View.VISIBLE
                    imageDummyTwo?.visibility = View.VISIBLE
                    image_two?.visibility = View.GONE
                    setImageDummy(membersInfo.get(1).userId, imageDummyTwo!!)
                    tvDummyTwo?.text = getFirstCharInUpperCase(membersInfo[1].fullName)
                } else {
                    rlDummyTwo?.visibility = View.GONE
                    image_two?.visibility = View.VISIBLE
                    setImage(membersInfo.get(1).userImage!!, image_two)
                }
            }
            3 -> {
                llOne?.visibility = View.VISIBLE
                viewOne?.visibility = View.VISIBLE
                viewTwo?.visibility = View.VISIBLE
                llTwo?.visibility = View.VISIBLE
                if (TextUtils.isEmpty(membersInfo.get(0).userImage)) {
                    rlDummy?.visibility = View.VISIBLE
                    imageDummy?.visibility = View.VISIBLE
                    image_one?.visibility = View.GONE
                    setImageToolbarWithDummy(membersInfo.get(0).userId, imageDummy!!)
                    tvDummy?.text = getFirstCharInUpperCase(membersInfo[0].fullName)
                } else {
                    rlDummy?.visibility = View.GONE
                    image_one?.visibility = View.VISIBLE
                    setImageToolbar(membersInfo.get(0).userImage!!, image_one)
                }
                if (TextUtils.isEmpty(membersInfo.get(1).userImage)) {
                    rlDummyTwo?.visibility = View.VISIBLE
                    imageDummyTwo?.visibility = View.VISIBLE
                    image_two?.visibility = View.GONE
                    setImageDummy(membersInfo.get(1).userId, imageDummyTwo!!)
                    tvDummyTwo?.text = getFirstCharInUpperCase(membersInfo[1].fullName)
                } else {
                    rlDummyTwo?.visibility = View.GONE
                    image_two?.visibility = View.VISIBLE
                    setImage(membersInfo.get(1).userImage!!, image_two)
                }

                if (TextUtils.isEmpty(membersInfo.get(2).userImage)) {
                    image_three?.visibility = View.GONE
                    rlDummyThree?.visibility = View.VISIBLE
                    imageDummyThree?.visibility = View.VISIBLE
                    setImageDummy(membersInfo.get(2).userId, imageDummyThree!!)
                    tvDummyThree?.text = getFirstCharInUpperCase(membersInfo[2].fullName)
                } else {
                    rlDummyThree?.visibility = View.GONE
                    image_three?.visibility = View.VISIBLE
                    setImage(membersInfo.get(2).userImage!!, image_three)
                }
            }
            else -> {
                llOne?.visibility = View.GONE
                llTwo?.visibility = View.GONE
            }
        }
    }

    private fun setImage(url: String, image: ImageView?) {

        val options = RequestOptions()
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .fitCenter()
                .priority(Priority.HIGH)
        if (!isFinishing) {
            Glide.with(this@GroupInformationActivity)
                    .load(url)
                    .apply(options)
                    .into(image!!)
        }
    }

    private fun setImageDummy(userId: Long, image: ImageView) {

        if (userId % 5 == 1L) {
            image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rectangle_grey))
        } else if (userId % 5 == 2L) {
            image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rectangle_indigo))
        } else if (userId % 5 == 3L) {
            image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rectangle_purple))
        } else if (userId % 5 == 4L) {
            image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rectangle_red))
        } else {
            image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rectangle_teal))
        }

    }

    private fun setImageToolbarWithDummy(userId: Long, image: ImageView) {
        when {
            userId % 5 == 1L -> {
                image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rectangle_grey))
                setUpToolBar(Color.parseColor("#4CAF50"))
            }
            userId % 5 == 2L -> {
                image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rectangle_indigo))
                setUpToolBar(Color.parseColor("#5C6BC0"))
            }
            userId % 5 == 3L -> {
                image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rectangle_purple))
                setUpToolBar(Color.parseColor("#935AA4"))
            }
            userId % 5 == 4L -> {
                image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rectangle_red))
                setUpToolBar(Color.parseColor("#EF5350"))
            }
            else -> {
                image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rectangle_teal))
                setUpToolBar(Color.parseColor("#26A69A"))
            }
        }
    }

    private fun setImageToolbar(url: String, image: ImageView?) {
        val options = RequestOptions()
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .fitCenter()
                .priority(Priority.HIGH)

        if (!isFinishing) {
            Glide.with(this@GroupInformationActivity)
                    .asBitmap()
                    .load(url)
                    .apply(options)
                    .listener(object : RequestListener<Bitmap> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            if (resource != null) {
                                val p = androidx.palette.graphics.Palette.from(resource).generate()
                                val mutedColor = p.getDominantColor(ContextCompat.getColor(this@GroupInformationActivity, R.color.colorPrimary))
                                setUpToolBar(mutedColor)
                            }
                            return false
                        }

                    })
                    .into(image!!)
        }
    }

    private fun setUpGroupMembers(membersList: java.util.ArrayList<GroupMember>) {
        val rvMembers: androidx.recyclerview.widget.RecyclerView = findViewById(R.id.rvMembers)
        membersAdapter = MembersAdapter(membersList, this@GroupInformationActivity, channelId, isJoined, currentUser, isExpanded, userCount)
        rvMembers.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@GroupInformationActivity)
        rvMembers.adapter = membersAdapter

    }

    private fun setUpGroupSettingsRecycler(currentUser: ChatMember?) {
        var text = ""
        var sendMessageText = ""
        try {
            notificationLevel = currentUser!!.notification
            text = ""
            if (notificationLevel.equals(NOTIFICATION_LEVEL.ALL_MENTIONS.toString())) {
                text = "Mentions only"
            } else if (notificationLevel.equals(NOTIFICATION_LEVEL.ALL_MESSAGES.toString())) {
                text = "All messages"
            } else {
                text = "Direct mentions only"
            }
        } catch (e: Exception) {

        }
        try {
            sendMessageText = ""
            if (intent.hasExtra("onlyAdminsCanMessage") && intent.getBooleanExtra("onlyAdminsCanMessage", false)) {
                sendMessages = true
                sendMessageText = "Only Admins"
            } else {
                sendMessages = false
                sendMessageText = "All Participants"
            }
        } catch (e: Exception) {

        }

        val isTasksEnabled = CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()].config.tasksEnabled
        if (currentUser?.role.equals(Role.ADMIN.toString())) {
            if (isJoined) {
                groupSettingList.add(GroupSettingsAdapter.GroupSetting("Manage Notifications", text, false, false))
                groupSettingList.add(GroupSettingsAdapter.GroupSetting("Send Messages", sendMessageText, false, false))
            }
            if (chatType != 5 && chatType != 6) {
                groupSettingList.add(GroupSettingsAdapter.GroupSetting("Private Group", "Private groups are invite-only, and do not show up in the group browse list", true, isPrivate))
                if (isTasksEnabled) {
                    groupSettingList.add(GroupSettingsAdapter.GroupSetting("Assign Task", "Assign new tasks to group members", false, false))
                    groupSettingList.add(GroupSettingsAdapter.GroupSetting("View Task", "View group's task calendar", false, false))
                }
            }
//            groupSettingList.add(GroupSettingsAdapter.GroupSetting("Edit Group Admins", "", false, false))
        } else {
            if (isJoined) {
                groupSettingList.add(GroupSettingsAdapter.GroupSetting("Manage Notifications", text, false, false))
                if (chatType != 5 && chatType != 6 && isTasksEnabled) {
                    groupSettingList.add(GroupSettingsAdapter.GroupSetting("View Task", "View group's task calendar", false, false))
                }
            }
        }
        if (groupSettingList.size > 0) {
            cvSettings?.visibility = View.VISIBLE
        } else {
            cvSettings?.visibility = View.GONE
        }
        groupSettingsAdapter = GroupSettingsAdapter(groupSettingList, this@GroupInformationActivity)
        rvTopOptions?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@GroupInformationActivity)
        rvTopOptions?.adapter = groupSettingsAdapter
    }

    fun onGroupSettingItemClicked(groupSetting: GroupSettingsAdapter.GroupSetting, pos: Int, isChecked: Boolean) {
        when (pos) {
            0 -> {
                changeNotificationSettings()
            }
            1 -> {
                if (currentUser?.role.equals(Role.ADMIN.toString()))
                    changeSendMessageSettings()
                else
                    viewTasks()
            }
            2 -> {
                changePrivacy(isChecked)
            }
            3 -> {
                assignTask()
            }
            4 -> viewTasks()
        }
    }

    private fun manipulateColor(color: Int, factor: Float): Int {
        val a = Color.alpha(color)
        val r = Math.round(Color.red(color) * factor)
        val g = Math.round(Color.green(color) * factor)
        val b = Math.round(Color.blue(color) * factor)
        return Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255))
    }

    fun makeGroupAdmin(userId: Long?, pos: Int?) {
        showLoading()
        val userIds = ArrayList<Long>()
        userIds.add(userId!!)
        val userIdsArray = JSONArray(userIds)
        val commonParams = MultipartParams.Builder()
        commonParams.add(CHANNEL_ID, channelId)
        commonParams.add(EN_USER_ID, workspaceInfo?.enUserId)
        commonParams.add(USER_IDS_TO_MAKE_ADMIN, userIdsArray)
        RestClient.getApiInterface().editChannelInfo(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), workspaceInfo?.fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                .enqueue(object : ResponseResolver<EditInfoResponse>() {
                    override fun success(t: EditInfoResponse?) {
                        membersList.get(pos!!).role = Role.ADMIN.toString()
                        membersAdapter?.updateList(membersList)
                        membersAdapter?.notifyDataSetChanged()
                        hideLoading()
                    }

                    override fun failure(error: APIError?) {
                        hideLoading()
                    }

                })
    }

    fun dismissGroupAdmin(userId: Long?, pos: Int?) {
        showLoading()
        val userIds = ArrayList<Long>()
        userIds.add(userId!!)
        val userIdsArray = JSONArray(userIds)
        val commonParams = MultipartParams.Builder()
        commonParams.add(CHANNEL_ID, channelId)
        commonParams.add(USER_IDS_TO_REMOVE_ADMIN, userIdsArray)
        commonParams.add(EN_USER_ID, workspaceInfo?.enUserId)
        RestClient.getApiInterface().editChannelInfo(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), workspaceInfo?.fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                .enqueue(object : ResponseResolver<EditInfoResponse>() {
                    override fun success(t: EditInfoResponse?) {
                        membersList.get(pos!!).role = Role.USER.toString()
                        membersAdapter?.updateList(membersList)
                        membersAdapter?.notifyDataSetChanged()
                        hideLoading()
                    }

                    override fun failure(error: APIError?) {
                        hideLoading()
                        Toast.makeText(this@GroupInformationActivity, error?.message, Toast.LENGTH_LONG).show()
                    }

                })
    }

    fun editGroupName(groupName: String) {
        showLoading()
        val commonParams = MultipartParams.Builder()
        commonParams.add(CHANNEL_ID, channelId)
        commonParams.add(EN_USER_ID, workspaceInfo?.enUserId)
        commonParams.add("custom_label", groupName)
        RestClient.getApiInterface().editChannelInfo(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), workspaceInfo?.fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                .enqueue(object : ResponseResolver<EditInfoResponse>() {
                    override fun success(t: EditInfoResponse?) {
                        toolbarHeaderView?.setName(groupName)
                        floatHeaderView?.setName(groupName)
                        groupInfoName?.text = groupName
                        groupNameGlobal = groupName
                        hideLoading()
                    }

                    override fun failure(error: APIError?) {
                        hideLoading()
                    }

                })
    }

    private fun changeNotificationSettings() {
        val newFragment = NotificationBottomSheetFragment.newInstance(0, notificationLevel, this@GroupInformationActivity, 0, 0L)
        newFragment.show(supportFragmentManager, "NotificationBottomSheetFragment")
    }

    private fun changeSendMessageSettings() {
        val newFragment = SendMessagesBottomSheetFragment.newInstance(0, sendMessages, this@GroupInformationActivity, 0, 0L)
        newFragment.show(supportFragmentManager, "SendMessagesBottomSheetFragment")
    }

    private fun assignTask() {
        val intent = Intent(this, TaskActivity::class.java)
        intent.putExtra(CHANNEL_ID, channelId)
        intent.putExtra("membersList", membersList)
        startActivity(intent)
    }

    private fun viewTasks() {
        val intent = Intent(this, ViewGroupTasksActivity::class.java)
        intent.putExtra(CHANNEL_ID, channelId)
        startActivity(intent)
    }

    fun updateNotificationSettings(notificationLevelUpdated: String) {
        val commonParams = CommonParams.Builder()
        commonParams.add(EN_USER_ID, workspaceInfo?.enUserId)
        commonParams.add(CHANNEL_ID, intent.getLongExtra("channelId", -1L))
        commonParams.add("notification", notificationLevelUpdated)
        showLoading()
        RestClient.getApiInterface().editInfo(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), workspaceInfo?.fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                .enqueue(object : ResponseResolver<EditInfoResponse>() {
                    override fun success(t: EditInfoResponse?) {
                        hideLoading()
                        object : Thread() {
                            override fun run() {
                                super.run()
                                var text = ""
                                notificationLevel = notificationLevelUpdated
                                if (notificationLevel.equals(NOTIFICATION_LEVEL.ALL_MENTIONS.toString())) {
                                    text = "Mentions only"
                                } else if (notificationLevel.equals(NOTIFICATION_LEVEL.ALL_MESSAGES.toString())) {
                                    text = "All messages"
                                } else {
                                    text = "Direct mentions only"
                                }
                                groupSettingList.set(0, GroupSettingsAdapter.GroupSetting("Manage Notifications", text, false, false))
                                runOnUiThread {
                                    groupSettingsAdapter?.notifyDataSetChanged()
                                }

                                val conversationMap = ChatDatabase.getConversationMap(workspaceInfo?.fuguSecretKey!!)
                                val conversation = conversationMap[channelId]
                                try {
                                    if (!notificationLevel.equals(NOTIFICATION_LEVEL.ALL_MESSAGES.toString())) {
                                        conversation?.notification = "MUTED"
                                    } else {
                                        conversation?.notification = "UNMUTED"
                                    }
                                    conversationMap[channelId] = conversation!!
                                    ChatDatabase.setConversationMap(conversationMap, workspaceInfo?.fuguSecretKey!!)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            }
                        }.start()
                    }

                    override fun failure(error: APIError?) {
                        hideLoading()
                    }

                })
    }

    fun updateSendMessages(onlyAdminsCanMessage: Boolean) {
        val commonParams = MultipartParams.Builder()
        commonParams.add(EN_USER_ID, workspaceInfo?.enUserId)
        commonParams.add(CHANNEL_ID, intent.getLongExtra("channelId", -1L))
        commonParams.add("only_admin_can_message", onlyAdminsCanMessage)
        showLoading()
        RestClient.getApiInterface().editChannelInfo(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), workspaceInfo?.fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                .enqueue(object : ResponseResolver<EditInfoResponse>() {
                    override fun success(t: EditInfoResponse?) {
                        hideLoading()
                        object : Thread() {
                            override fun run() {
                                super.run()
                                var sendMessageText = ""
                                if (onlyAdminsCanMessage) {
                                    sendMessages = true
                                    sendMessageText = "Only Admins"
                                } else {
                                    sendMessages = false
                                    sendMessageText = "All Participants"
                                }
                                groupSettingList.set(1, GroupSettingsAdapter.GroupSetting("Send Messages", sendMessageText, false, false))
                                runOnUiThread {
                                    groupSettingsAdapter?.notifyDataSetChanged()
                                }
                            }
                        }.start()
                    }

                    override fun failure(error: APIError?) {
                        hideLoading()
                    }

                })
    }

    private fun changePrivacy(isPrivate: Boolean) {
        showLoading()
        val commonParams = MultipartParams.Builder()
        commonParams.add(CHANNEL_ID, channelId)
        commonParams.add(EN_USER_ID, workspaceInfo?.enUserId)
        if (isPrivate) {
            commonParams.add("chat_type", 3)
        } else {
            commonParams.add("chat_type", 4)
        }
        RestClient.getApiInterface().editChannelInfo(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), workspaceInfo?.fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                .enqueue(object : ResponseResolver<EditInfoResponse>() {
                    override fun success(t: EditInfoResponse?) {
                        hideLoading()
                    }

                    override fun failure(error: APIError?) {
                        hideLoading()
                    }

                })
    }

    private fun showGroupNameEditDialog(activity: Context, groupName: String) {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.group_edit_dialog)
        val lp = dialog.window!!.attributes
        lp.dimAmount = 0.5f
        dialog.window!!.attributes = lp
        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(false)
        val etGroupName = dialog.findViewById<EditText>(R.id.etGroupName)
        val btnSave = dialog.findViewById<AppCompatButton>(R.id.btnSave)
        val btnCancel = dialog.findViewById<AppCompatButton>(R.id.btnCancel)
        btnCancel.setOnClickListener {
            dialog.dismiss()
            KeyboardUtil.toggleKeyboardVisibility(this@GroupInformationActivity)
        }
        etGroupName.setText(groupName)
        etGroupName.setSelection(etGroupName.text.length)
        KeyboardUtil.toggleKeyboardVisibility(this@GroupInformationActivity)
        etGroupName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString() == groupName || s.toString().isEmpty()) {
                    btnSave.setTextColor(resources.getColor(R.color.gray_dark))
                    btnSave.setOnClickListener(null)
                } else {
                    btnSave.setTextColor(resources.getColor(R.color.colorPrimary))
                    btnSave.setOnClickListener {
                        dialog.dismiss()
                        KeyboardUtil.toggleKeyboardVisibility(this@GroupInformationActivity)
                        editGroupName(etGroupName.text.toString().trim { it <= ' ' })
                    }
                }
            }
        })
        dialog.show()
    }

    @SuppressLint("RestrictedApi")
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.ivEdit -> {
                if (isCustomLabel) {
                    showGroupNameEditDialog(this@GroupInformationActivity, "")
                } else {
                    showGroupNameEditDialog(this@GroupInformationActivity, groupInfoName?.text.toString().trim({ it <= ' ' }))
                }
            }
            R.id.llAddMember -> {
                addMembersIntent()
            }

            R.id.cvDeleteGroup -> {
                AlertDialog.Builder(this)
                        .setTitle("Are you sure you want to delete this group?")
                        .setPositiveButton("Yes") { _, _ -> apiDeleteGroup() }
                        .setNegativeButton("No", null)
                        .show()
            }

            R.id.cvExit -> {

                AlertDialog.Builder(this)
                        .setTitle("Are you sure you want to exit group?")
                        .setPositiveButton("Yes") { _, _ -> apiLeaveGroup() }
                        .setNegativeButton("No", null)
                        .show()
            }
            R.id.cvDelete -> {
                AlertDialog.Builder(this)
                        .setTitle("Are you sure you want to delete chat?")
                        .setPositiveButton("Yes") { _, _ -> apiDeleteChat() }
                        .setNegativeButton("No", null)
                        .show()
            }
            R.id.imageMain -> {
                image_main?.transitionName = "imageOne"
                val imageIntent = Intent(this, ImageDisplayActivity::class.java)
                val image = Image(imageUrl, imageUrl, "imageOne", "", groupNameGlobal)
                var options: ActivityOptionsCompat?

                options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        image_main as View, "imageOne")

                imageIntent.putExtra("image", image)
                imageIntent.putExtra("BUSINESS_NAME", groupNameGlobal)
                imageIntent.putExtra("chatType", chatType)
                imageIntent.putExtra("channelId", channelId)
                try {
                    imageIntent.putExtra("editable", (chatType == ChatType.PRIVATE_GROUP && currentUser!!.role.equals(Role.ADMIN.toString())) ||
                            (chatType == ChatType.PUBLIC_GROUP && isJoined) || ((chatType == ChatType.GENERAL_GROUP || chatType == ChatType.DEFAULT_GROUP) && workspaceInfo?.role.equals("ADMIN") ||
                            workspaceInfo?.role.equals("OWNER")))
                } catch (e: Exception) {
                    imageIntent.putExtra("editable", false)
                }

                startActivityForResult(imageIntent, 1003, options.toBundle())
            }
            R.id.llOne, R.id.llTwo -> {
                CustomAlertDialog.Builder(this@GroupInformationActivity)
                        .setTitle("Select option")
                        .setPositiveButton("Camera") { fuguImageUtils?.startCamera() }
                        .setNegativeButton("Gallery") { openGallery() }
                        .show()
            }
        }
    }

    private fun apiDeleteGroup() {
        showLoading()
        val commonParams = MultipartParams.Builder()
        commonParams.add(CHANNEL_ID, channelId)
        commonParams.add(EN_USER_ID, workspaceInfo?.enUserId)
        commonParams.add("status", 0)
        RestClient.getApiInterface().editChannelInfo(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), workspaceInfo?.fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                .enqueue(object : ResponseResolver<EditInfoResponse>() {
                    override fun success(t: EditInfoResponse?) {
                        hideLoading()
                        val i = baseContext.packageManager
                                .getLaunchIntentForPackage(baseContext.packageName)
                        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        i.putExtra("open_home", "open_home")
                        startActivity(i)
                    }

                    override fun failure(error: APIError?) {
                        hideLoading()
                    }

                })
    }

    private fun apiDeleteChat() {
        val commonParams = CommonParams.Builder()
                .add(EN_USER_ID, workspaceInfo?.enUserId)
                .add("muid", intent.getStringExtra(MESSAGE_UNIQUE_ID))
                .add(CHANNEL_ID, channelId)
                .build()
        RestClient.getApiInterface().clearChatHistory(
                CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                workspaceInfo?.fuguSecretKey,
                1, BuildConfig.VERSION_CODE, commonParams.map)
                .enqueue(object : com.skeleton.mvp.retrofit.ResponseResolver<CommonResponse>(this, true, false) {
                    override fun success(commonResponse: CommonResponse) {
                        ChatDatabase.setMessageList(java.util.ArrayList(), channelId)
                        ChatDatabase.setMessageMap(LinkedHashMap(), channelId)
                        ChatDatabase.setUnsentMessageMapByChannel(channelId, LinkedHashMap())
                        getFromSdcardAndDelete(channelId)
                        val i = baseContext.packageManager
                                .getLaunchIntentForPackage(baseContext.packageName)
                        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        i.putExtra("open_home", "open_home")
                        startActivity(i)
                    }

                    override fun failure(error: APIError) {

                    }
                })
    }


    private fun getFromSdcardAndDelete(channelId: Long) {
        var listFile: Array<File>?
        try {
            val filesNormal = File(Environment.getExternalStorageDirectory(), APP_NAME_SHORT +
                    File.separator + workspaceInfo?.workspaceName?.replace(" ".toRegex(), "")?.replace("'s".toRegex(), "")
                    + File.separator + IMAGE)

            val filesPrivate = File(Environment.getExternalStorageDirectory(), APP_NAME_SHORT +
                    File.separator + workspaceInfo?.workspaceName?.replace(" ".toRegex(), "")?.replace("'s".toRegex(), "")
                    + File.separator + PRIVATE_IMAGES)

            if (filesNormal.isDirectory) {
                listFile = filesNormal.listFiles()

                for (i in listFile!!.indices) {
                    val exifFile = ExifInterface(listFile[i].absolutePath)
                    Log.e(exifFile.getAttribute(ExifInterface.TAG_MAKE), channelId.toString())
                    if (!TextUtils.isEmpty(exifFile.getAttribute(ExifInterface.TAG_MAKE)) && exifFile.getAttribute(ExifInterface.TAG_MAKE)!!.contains(channelId.toString())) {
                        listFile.get(i).delete()
                    }
                }
            }
            if (filesPrivate.isDirectory) {
                listFile = filesPrivate.listFiles()

                for (i in listFile!!.indices) {
                    val exifFile = ExifInterface(listFile[i].absolutePath)
                    Log.e(exifFile.getAttribute(ExifInterface.TAG_MAKE), channelId.toString())
                    if (!TextUtils.isEmpty(exifFile.getAttribute(ExifInterface.TAG_MAKE)) && exifFile.getAttribute(ExifInterface.TAG_MAKE)!!.contains(channelId.toString())) {
                        listFile.get(i).delete()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addMembersIntent() {
        val searchIntent = Intent(this@GroupInformationActivity, FuguSearchsActivity::class.java)
        isDilogOpened = false
        searchIntent.putExtra(CHANNEL_ID, channelId)
        val REQUEST_CODE_ADD_MEMBER = 13241
        startActivityForResult(searchIntent, REQUEST_CODE_ADD_MEMBER)
    }

    override fun onResume() {
        super.onResume()
        apiGetGroupInfo(false)

        unregisterReceivers()
        LocalBroadcastManager.getInstance(this).registerReceiver(mPublicChatReciever,
                IntentFilter(FuguAppConstant.PUBLIC_INTENT))
        LocalBroadcastManager.getInstance(this@GroupInformationActivity).registerReceiver(mUserRemovedReciever,
                IntentFilter(FuguAppConstant.USER_REMOVED_INTENT))
    }

    private fun unregisterReceivers() {
        try {
            LocalBroadcastManager.getInstance(this@GroupInformationActivity).unregisterReceiver(mPublicChatReciever)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            LocalBroadcastManager.getInstance(this@GroupInformationActivity).unregisterReceiver(mUserRemovedReciever)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val mUserRemovedReciever = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            try {
                if (intent?.hasExtra(CHANNEL_ID)!! && intent.getLongExtra(CHANNEL_ID, -1L).compareTo(channelId) == 0) {
                    if (intent.getLongExtra("removed_user_id", -1L).compareTo(currentUser!!.userId) == 0) {
                        AlertDialog.Builder(this@GroupInformationActivity)
                                .setTitle("You are removed from $groupNameGlobal!!")
                                .setCancelable(false)
                                .setPositiveButton("Ok") { dialog, which ->
                                    unregisterReceivers()
                                    setResult(Activity.RESULT_OK)
                                    finish()
                                }
                                .show()
                    }
                }
            } catch (e: java.lang.Exception) {

            }

        }
    }

    private val mPublicChatReciever = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            try {
                if (intent?.hasExtra(CHANNEL_ID)!!
                        && intent.getLongExtra(CHANNEL_ID, -1L).compareTo(channelId) == 0 && intent.getBooleanExtra("is_deleted_group", false)) {
                    AlertDialog.Builder(this@GroupInformationActivity)
                            .setTitle("This group has been deleted!")
                            .setCancelable(false)
                            .setPositiveButton("Ok") { dialog, which ->
                                unregisterReceivers()
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                            .show()
                }
                if (intent.hasExtra(CHANNEL_ID)
                        && intent.getLongExtra(CHANNEL_ID, -1L).compareTo(channelId) == 0
                        && workspaceInfo?.userId!!.toLong().compareTo(intent.getLongExtra(USER_ID, -1L)) != 0) {
                    if (intent.hasExtra(CHAT_TYPE)) {
                        chatType = intent.getIntExtra(CHAT_TYPE, chatType)
                        com.skeleton.mvp.fugudatabase.CommonData.setChatType(chatType)
                        mediaResponseMain!!.data.channelInfo.chatType = chatType
                        setUpData(mediaResponseMain, false)
                    }
                    if (intent.hasExtra(USER_IDS_TO_REMOVE_ADMIN)) {
                        for (removedMember in intent.getIntegerArrayListExtra(USER_IDS_TO_REMOVE_ADMIN)!!) {
                            for (member in membersList) {
                                if (member.userId.toInt() == removedMember) {
                                    member.role = "USER"
                                    break
                                }
                            }
                        }
                        apiGetGroupInfo(false)
                    }
                    if (intent.hasExtra(USER_IDS_TO_MAKE_ADMIN)) {
                        for (addedMember in intent.getIntegerArrayListExtra(USER_IDS_TO_MAKE_ADMIN)!!) {
                            for (member in membersList) {
                                if (member.userId.toInt() == addedMember) {
                                    member.role = "ADMIN"
                                    break
                                }
                            }
                        }
                    }
                    apiGetGroupInfo(false)
                }

            } catch (e: java.lang.Exception) {
            }
        }
    }

    fun removeMember(userId: Long?, pos: Int?, name: String) {
        AlertDialog.Builder(this@GroupInformationActivity)
                .setMessage("Do you want to remove " + name + " from the group?")
                .setPositiveButton("No", null)
                .setNegativeButton("Yes") { dialogInterface, i ->
                    val commonParams = CommonParams.Builder()
                            .add("user_id_to_remove", userId)
                            .add(EN_USER_ID, workspaceInfo?.enUserId)
                            .add(CHANNEL_ID, channelId)
                            .build()
                    showLoading()
                    RestClient.getApiInterface().removeMember(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.map)
                            .enqueue(object : ResponseResolver<GroupResponse>(this@GroupInformationActivity, true, false) {
                                @SuppressLint("SetTextI18n")
                                override fun success(groupResponse: GroupResponse) {
                                    hideLoading()
                                    val newMembersList = ArrayList<GroupMember>()
                                    newMembersList.addAll(membersList)
                                    for (member in membersList.indices.reversed()) {
                                        if (membersList[member].userId.compareTo(-1L) == 0) {
                                            newMembersList.removeAt(member)
                                            break
                                        }
                                    }
                                    newMembersList.removeAt(pos!!)
                                    membersList = ArrayList()
                                    membersList.addAll(newMembersList)

                                    if (membersList.size > 10) {
                                        membersList.add(10, GroupMember("", -1L, "", "", "", "", 1))
                                    }

                                    membersAdapter?.updateList(membersList)
                                    membersAdapter?.notifyDataSetChanged()
                                    com.skeleton.mvp.fugudatabase.CommonData.setGroupMembers(membersList)
                                    if (membersList.size > 10) {
                                        tvParticipants?.text = (membersList.size - 1).toString() + " members"
                                    } else {
                                        tvParticipants?.text = (membersList.size).toString() + " members"
                                    }

                                }

                                override fun failure(error: APIError) {
                                    hideLoading()
                                }
                            })
                }
                .show()
    }

    fun viewProfile(userId: Long?) {
        val mIntent = Intent(Intent(this@GroupInformationActivity, ProfileActivity::class.java))
        mIntent.putExtra("open_profile", userId!!.toString() + "")
        if (userId.compareTo(java.lang.Long.valueOf(workspaceInfo!!.userId)) == 0) {
            mIntent.putExtra("no_chat", "no_chat")
        }
        startActivity(mIntent)
    }

    private fun apiLeaveGroup() {
        val commonParams = CommonParams.Builder()
                .add(EN_USER_ID, workspaceInfo?.enUserId)
                .add(CHANNEL_ID, channelId)
                .build()
        RestClient.getApiInterface().leaveGroup(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), workspaceInfo?.fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.map)
                .enqueue(object : ResponseResolver<GroupResponse>(this@GroupInformationActivity, true, false) {
                    override fun success(groupResponse: GroupResponse) {
                        unregisterReceivers()
                        setResult(Activity.RESULT_OK)
                        finish()
                    }

                    override fun failure(error: APIError) {
                    }
                })
    }


    /**
     * Method to open the Gallery view
     */

    private fun openGallery() {
        isDilogOpened = true
        // Check and ask for Permissions
        if (!FuguConfig.getInstance().askUserToGrantPermission(this@GroupInformationActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, "Please grant permission to Storage",
                        FuguAppConstant.PERMISSION_CONSTANT_GALLERY))
            return

        try {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, 1000)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(applicationContext,
                    getString(R.string.no_gallery), Toast.LENGTH_SHORT).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var tempUri: Uri? = null

        if (requestCode == OPEN_CAMERA_ADD_IMAGE && resultCode == Activity.RESULT_OK) {
            try {
                val fileName = CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].workspaceName.replace(" ", "").replace("'s", "") + "_" + com.skeleton.mvp.fugudatabase.CommonData.getTime() + ".jpg"
                tempUri = Uri.fromFile(File(fuguImageUtils?.getDirectory(FuguAppConstant.FileType.IMAGE_FILE), fileName))
                try {
                    CropImage.activity(tempUri)
                            .setFixAspectRatio(true)
                            .start(this)
                } finally {
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (requestCode == 1000 && data != null) {
            CropImage.activity(data.data)
                    .setFixAspectRatio(true)
                    .start(this)
        } else if (requestCode == 1003 && data != null) {
            image_main?.invalidate()
            setImageToolbar(data.getStringExtra("imageUrl")!!, image_main)
        } else if (resultCode == Activity.RESULT_OK && requestCode == 1004 && data != null) {
            membersList = data.getSerializableExtra("membersList") as java.util.ArrayList<GroupMember>
            membersAdapter?.updateList(membersList)
            membersAdapter?.notifyDataSetChanged()
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                apiEditChannelImage(resultUri)

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }

    }

    private fun apiEditChannelImage(tempUri: Uri) {
        showLoading()
        val commonParams = MultipartParams.Builder()
        commonParams.add(CHANNEL_ID, channelId)
        commonParams.add(EN_USER_ID, workspaceInfo?.enUserId)
        if (!TextUtils.isEmpty(tempUri.path)) {
            commonParams.addFile("files", File(tempUri.path))
            RestClient.getApiInterface().editChannelInfo(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), workspaceInfo?.fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                    .enqueue(object : ResponseResolver<EditInfoResponse>() {
                        override fun success(t: EditInfoResponse?) {
                            hideLoading()
                            llOne?.visibility = View.GONE
                            llTwo?.visibility = View.GONE
                            viewOne?.visibility = View.GONE
                            viewTwo?.visibility = View.GONE
                            image_main?.visibility = View.VISIBLE
                            setImageToolbar(t!!.data.channelImageUrl.channelThumbnailUrl, image_main)
                        }

                        override fun failure(error: APIError?) {
                            hideLoading()
                        }

                    })
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    override fun expandList() {
        val intent = Intent(this, GroupMembersActivity::class.java)
        if (membersList.size > 10) {
            try {
                membersList.removeAt(10)
            } catch (e: java.lang.Exception) {

            }
        }
        intent.putExtra("membersList", membersList)
        intent.putExtra("currentUser", currentUser)
        intent.putExtra("isJoined", isJoined)
        intent.putExtra(CHANNEL_ID, channelId)
        intent.putExtra("userCount", userCount)
        intent.putExtra("userPageSize", userPageSize)
        intent.putExtra("chatType", chatType)
        startActivityForResult(intent, 1004)
    }

    override fun onDestroy() {
        unregisterReceivers()
        super.onDestroy()
    }
}
