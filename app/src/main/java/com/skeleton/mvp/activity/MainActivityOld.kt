package com.skeleton.mvp.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.provider.MediaStore
import android.text.TextUtils
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.FuguConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.data.db.ChatDatabase
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.creategroup.MembersInfo
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.data.network.CommonParams
import com.skeleton.mvp.data.network.ResponseResolver
import com.skeleton.mvp.data.network.RestClient
import com.skeleton.mvp.fragment.CreateGroupFragment
import com.skeleton.mvp.fragment.HomeFragment
import com.skeleton.mvp.fragment.MoreFragment
import com.skeleton.mvp.fragment.NotificationsActivity
import com.skeleton.mvp.interfaces.RecyclerViewAddedMembers
import com.skeleton.mvp.interfaces.UpdateAllMemberCallback
import com.skeleton.mvp.model.*
import com.skeleton.mvp.model.channelResponse.ChannelResponse
import com.skeleton.mvp.model.unreadNotification.UnreadNotificationResponse
import com.skeleton.mvp.pushNotification.PushReceiver
import com.skeleton.mvp.service.NotificationSockets
import com.skeleton.mvp.service.VideoCallService
import com.skeleton.mvp.socket.SocketConnection
import com.skeleton.mvp.ui.AppConstants
import com.skeleton.mvp.ui.AppConstants.*
import com.skeleton.mvp.ui.UniqueIMEIID
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.ui.home.SpacesAdapter
import com.skeleton.mvp.ui.yourspaces.YourSpacesActivity
import com.skeleton.mvp.util.*
import com.skeleton.mvp.utils.BottomBarAdapter
import com.skeleton.mvp.utils.DateUtils
import com.skeleton.mvp.utils.FuguImageUtils
import com.skeleton.mvp.utils.FuguUtils.Companion.getTimeZoneOffset
import com.skeleton.mvp.utils.NoSwipePager
import com.skeleton.mvp.videoCall.FuguCallActivity
import io.branch.referral.Branch
import io.socket.client.Socket
import org.json.JSONException
import org.json.JSONObject
import java.io.FileOutputStream
import java.net.SocketTimeoutException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap
import kotlin.collections.set


/**
 * Created
 * rajatdhamija on 26/07/18.
 */

class MainActivityOld : BaseActivity(), SpacesAdapter.SelectBusiness,
        SearchAnimationToolbar.OnSearchQueryChangedListener, UpdateAllMemberCallback,
        RecyclerViewAddedMembers {

    private val REQUEST_CODE_PLAY_STORE = 12221
    private var viewPager: NoSwipePager? = null
    private var pagerAdapter: BottomBarAdapter? = null

    //    private var bottomNavigation: AHBottomNavigation? = null
    private var homeToolbar: Toolbar? = null
    private var notificationToolbar: Toolbar? = null
    private var moreToolbar: Toolbar? = null
    private var searchLayout: RelativeLayout? = null
    private var searchToolbar: SearchAnimationToolbar? = null
    private var llHome: LinearLayout? = null
    private var workspacesInfoList = ArrayList<WorkspacesInfo>()
    private lateinit var tvHomeTitle: TextView
    private var currentSigninedInPosition: Int = 0
    private var newSignedInPosition: Int = 0
    private var TAG = "MainActivityNew"
    private var ivArraow: ImageView? = null
    private var isOpened: Boolean = false
    private var invite: Boolean = false
    private var isPopupShowing = false
    private var workspace = ""
    private val NOT_CONNECTED = 0
    private val CONNECTED_TO_INTERNET = 1
    private val CONNECTED_TO_INTERNET_VIA_WIFI = 2
    private var tvStatus: TextView? = null
    private var llInternet: LinearLayout? = null
    private var tvReturnCall: TextView? = null
    private var imageUri = ""
    private var sharedText = ""
    private var extension = ""
    var unsentMessageMap = java.util.LinkedHashMap<Long, java.util.LinkedHashMap<String, Message>>()
    private var userId: Long = -1L
    private var targetChannelId: Long = -1L
    private var fileOutputStream: FileOutputStream? = null
    private var rlCount: RelativeLayout? = null
    private var tvCount: AppCompatTextView? = null
    private var rlNoti: RelativeLayout? = null
    private var ivMore: AppCompatImageView? = null

    //    private var navigationAdapter: AHBottomNavigationAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_old)
        checkIfUserIsLoggedIn()
        if (savedInstanceState != null) {

        }
        val fcmKeepAlive = FcmKeepAlive(this)
        fcmKeepAlive.broadcastIntents()

    }


    /**
     * Share file from external applications
     */
    private fun fileSharingSystem() {
        ActivityCompat.requestPermissions(
                this,
                arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ), 100
        )
        val fuguImageUtils = FuguImageUtils(this)
        val intent = intent
        val action = intent.action
        val type = intent.type
        if (Intent.ACTION_SEND == action && type != null) {
            var fuguFileDetails: FuguFileDetails? = null
            if (type.startsWith(IMAGE_SLASH)) {
                var getRealpathFromUri = ""
                try {
                    if (type.equals("image/png")) {
                        extension = "jpg"
                        val bitmap = BitmapFactory.decodeFile(
                                GetRealpathFromUri.getFilePathFromURI(
                                        MimeTypes.lookupExt(type),
                                        this@MainActivityOld,
                                        intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri
                                )
                        )
                        val inputStream = contentResolver.openInputStream(intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri)!!
                        fileOutputStream = FileOutputStream(
                                GetRealpathFromUri.getFilePathFromURI(
                                        MimeTypes.lookupExt(type),
                                        this@MainActivityOld,
                                        intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri
                                )
                        )
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                        val buffer = ByteArray(1024)
                        val bytesRead = 0
                        var i = inputStream.read(buffer)
                        while (i > -1) {
                            i = inputStream.read(buffer)
                            fileOutputStream!!.write(buffer, 0, bytesRead)
                        }
                        fileOutputStream!!.close()

                        fuguImageUtils.copyFileFromUri(
                                intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri,
                                extension
                        )
                        fuguFileDetails = fuguImageUtils.saveFile(
                                intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri,
                                FuguAppConstant.FILE_TYPE_MAP[extension.toLowerCase()],
                                -1L,
                                ""
                        )
                        getRealpathFromUri = fuguImageUtils.compressAndSaveBitmap(
                                this,
                                extension,
                                fuguFileDetails?.fileName!!
                        )
                    } else {
                        getRealpathFromUri = GetRealpathFromUri.getFilePathFromURI(
                                MimeTypes.lookupExt(type),
                                this@MainActivityOld,
                                intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri
                        )
                    }

                    imageUri = getRealpathFromUri
                    com.skeleton.mvp.fugudatabase.CommonData.setImageUri(getRealpathFromUri)
                    com.skeleton.mvp.fugudatabase.CommonData.setImageUriMain(
                            intent.getParcelableExtra<Parcelable>(
                                    Intent.EXTRA_STREAM
                            ) as Uri
                    )
                    com.skeleton.mvp.fugudatabase.CommonData.setSharedText("")
                    com.skeleton.mvp.fugudatabase.CommonData.setOtherFilesUriString("")
                    com.skeleton.mvp.fugudatabase.CommonData.setVideoUri("")
                    com.skeleton.mvp.fugudatabase.CommonData.setMultipleImage("")
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

            } else if (type.startsWith("text/") && type.endsWith("plain") && intent.getStringExtra(
                            Intent.EXTRA_TEXT
                    ) != null
            ) {
                sharedText = intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
                Log.e(SHARED_TEXT, sharedText)
                com.skeleton.mvp.fugudatabase.CommonData.setImageUri("")
                com.skeleton.mvp.fugudatabase.CommonData.setSharedText(sharedText)
                com.skeleton.mvp.fugudatabase.CommonData.setOtherFilesUriString("")
                com.skeleton.mvp.fugudatabase.CommonData.setVideoUri("")
                com.skeleton.mvp.fugudatabase.CommonData.setMultipleImage("")
            } else if (type.startsWith(VIDEO_SLASH)) {
                var getRealpathFromUri = ""
                try {
                    getRealpathFromUri = GetRealpathFromUri.getPath(
                            this@MainActivityOld,
                            intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri
                    )
                } catch (e: Exception) {
                    try {
                        getRealpathFromUri = GetRealpathFromUri.getFilePathFromURI(
                                "video",
                                this@MainActivityOld,
                                intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri
                        )
                    } catch (e: Exception) {

                    }
                }
                com.skeleton.mvp.fugudatabase.CommonData.setImageUri("")
                com.skeleton.mvp.fugudatabase.CommonData.setSharedText("")
                com.skeleton.mvp.fugudatabase.CommonData.setOtherFilesUriString("")
                com.skeleton.mvp.fugudatabase.CommonData.setVideoUri(getRealpathFromUri)
                com.skeleton.mvp.fugudatabase.CommonData.setVideoUriMain(
                        intent.getParcelableExtra<Parcelable>(
                                Intent.EXTRA_STREAM
                        ) as Uri
                )
                com.skeleton.mvp.fugudatabase.CommonData.setMultipleImage("")
            } else {
                var getRealpathFromUri = ""
                try {
                    getRealpathFromUri = GetRealpathFromUri.getPath(
                            this@MainActivityOld,
                            intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri
                    )
                    com.skeleton.mvp.fugudatabase.CommonData.setOtherFilesUriString(
                            getRealpathFromUri.toString()
                    )
                    com.skeleton.mvp.fugudatabase.CommonData.setOtherFilesUri(
                            intent.getParcelableExtra<Parcelable>(
                                    Intent.EXTRA_STREAM
                            ) as Uri
                    )
                } catch (e: Exception) {
                    try {
                        getRealpathFromUri = GetRealpathFromUri.getFilePathFromURI(
                                MimeTypes.lookupExt(type),
                                this@MainActivityOld,
                                intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri
                        )
                        com.skeleton.mvp.fugudatabase.CommonData.setOtherFilesUriString(
                                getRealpathFromUri.toString()
                        )
                        com.skeleton.mvp.fugudatabase.CommonData.setOtherFilesUri(
                                intent.getParcelableExtra<Parcelable>(
                                        Intent.EXTRA_STREAM
                                ) as Uri
                        )
                    } catch (e: Exception) {
                        Toast.makeText(
                                this@MainActivityOld,
                                "Unable to share file!",
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                com.skeleton.mvp.fugudatabase.CommonData.setImageUri("")
                com.skeleton.mvp.fugudatabase.CommonData.setSharedText("")
                com.skeleton.mvp.fugudatabase.CommonData.setVideoUri("")
                com.skeleton.mvp.fugudatabase.CommonData.setMultipleImage("")
            }

            if (intent.hasExtra("targetChannelId")) {
                directFileShare(intent)
            }
        } else if (Intent.ACTION_SEND_MULTIPLE == action && type != null) {
            if (type.startsWith(IMAGE_SLASH)) {
                var shareImageList: ArrayList<Parcelable> =
                        intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM)!!
                if (shareImageList.size > 5) {
                    val toast = Toast.makeText(
                            this@MainActivityOld,
                            "Currently can't share more than 5 media items.",
                            Toast.LENGTH_LONG
                    )
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    finish()
                } else {
                    var shareImageUriList = ArrayList<String>()
//                com.skeleton.mvp.fugudatabase.CommonData.setImageUri(getRealpathFromUri)
//                com.skeleton.mvp.fugudatabase.CommonData.setImageUriMain(intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri)
//                com.skeleton.mvp.fugudatabase.CommonData.setSharedText("")
//                com.skeleton.mvp.fugudatabase.CommonData.setOtherFilesUriString("")
//                com.skeleton.mvp.fugudatabase.CommonData.setVideoUri("")
                    for (shareImage in shareImageList) {
                        val uri = shareImage as Uri
                        shareImageUriList.add(GetRealpathFromUri.getPath(this@MainActivityOld, uri))
                    }
                    com.skeleton.mvp.fugudatabase.CommonData.setMultipleImageList(shareImageUriList)
                    com.skeleton.mvp.fugudatabase.CommonData.setMultipleImage("multipleImage")
                    com.skeleton.mvp.fugudatabase.CommonData.setSharedText("")
                    com.skeleton.mvp.fugudatabase.CommonData.setOtherFilesUriString("")
                    com.skeleton.mvp.fugudatabase.CommonData.setVideoUri("")
                }

            } else if (type.startsWith("text/") && type.endsWith("plain") && intent.getStringExtra(
                            Intent.EXTRA_TEXT
                    ) != null
            ) {
                val toast = Toast.makeText(
                        this@MainActivityOld,
                        "Currently can't share multiple text files.",
                        Toast.LENGTH_LONG
                )
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
                finish()
            } else if (type.startsWith(VIDEO_SLASH)) {
                val toast = Toast.makeText(
                        this@MainActivityOld,
                        "Currently can't share multiple videos/files.",
                        Toast.LENGTH_LONG
                )
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
                finish()
            } else {
                val toast = Toast.makeText(
                        this@MainActivityOld,
                        "Currently can't share multiple videos/files.",
                        Toast.LENGTH_LONG
                )
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
                finish()
            }

            if (intent.hasExtra("targetChannelId")) {
                directFileShare(intent)
            }
        } else {
            com.skeleton.mvp.fugudatabase.CommonData.setOtherFilesUriString("")
            com.skeleton.mvp.fugudatabase.CommonData.setImageUri("")
            com.skeleton.mvp.fugudatabase.CommonData.setSharedText("")
            com.skeleton.mvp.fugudatabase.CommonData.setVideoUri("")
            com.skeleton.mvp.fugudatabase.CommonData.setMultipleImage("")
        }
    }

    fun getRealPathFromURI(contentURI: Uri?): String {
        var result: String
        val cursor = contentResolver.query(contentURI!!, null, null, null, null)
        if (cursor == null) {
            result = contentURI.path!!
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = try {
                cursor.getString(idx)
            } catch (e: java.lang.Exception) {
                contentURI.path!!
            }

            cursor.close()
        }
        return result
    }

    /**
     * check if user is already logged in or not
     */
    private fun checkIfUserIsLoggedIn() {
        if (CommonData.getCommonResponse() != null) {
            fileSharingSystem()
            workspacesInfoList =
                    CommonData.getCommonResponse().data.workspacesInfo as ArrayList<WorkspacesInfo>
            if (CommonData.getCurrentSignedInPosition() >= workspacesInfoList.size) {
                CommonData.setCurrentSignedInPosition(0)
            }
            currentSigninedInPosition = CommonData.getCurrentSignedInPosition()

            val workpsacesMap = LinkedHashMap<String, String>()
            for (i in 0 until workspacesInfoList.size) {
                workpsacesMap[workspacesInfoList[i].fuguSecretKey] =
                        workspacesInfoList[i].workspaceName
            }

            CommonData.setWorkspacesMap(workpsacesMap)
            if (workspacesInfoList.size > 0) {
                initViews()
                Handler().postDelayed({
                    if (!invite) {
                        apiLoginViaAccessToken(
                                "",
                                CommonData.getCommonResponse().getData().getUserInfo().getAccessToken()
                        )
//                        apiGetUnreadNotifications();
                    }
                }, 1000)
            } else {
                startActivity(Intent(this@MainActivityOld, YourSpacesActivity::class.java))
                finishAffinity()

            }
        } else {
            startActivity(Intent(this@MainActivityOld, OnboardActivity::class.java))
            finishAffinity()
        }
    }

    @Suppress("unused")
    private fun apiGetUnreadNotifications() {
        if (CommonData.getCurrentSignedInPosition() >= workspacesInfoList.size) {
            CommonData.setCurrentSignedInPosition(0)
        }
        val commonparams = com.skeleton.mvp.retrofit.CommonParams.Builder()
                .add(
                        EN_USER_ID,
                        CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()].enUserId
                )
        commonparams.build()
        com.skeleton.mvp.retrofit.RestClient.getApiInterface().getUnreadNotifications(
                com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey,
                1, BuildConfig.VERSION_CODE, commonparams.build().map
        ).enqueue(object : ResponseResolver<UnreadNotificationResponse>() {
            override fun onSuccess(unreadNotificationResponse: UnreadNotificationResponse?) {
                Thread {
                    kotlin.run {
                        val unreadCountList = ArrayList<UnreadCount>()
                        for (notification in unreadNotificationResponse?.data?.unreadNotification!!) {
                            var isTagged: Boolean
                            var isThreadMessage = false

                            isTagged = notification.isTagged == 1
                            if (!TextUtils.isEmpty(notification.threadMuid)) {
                                isThreadMessage = true
                            } else {
                                isTagged = false
                            }
                            unreadCountList.add(
                                    UnreadCount(
                                            notification.channelId,
                                            notification.muid,
                                            notification.notificationType,
                                            isTagged, isThreadMessage
                                    )
                            )
                        }
                        com.skeleton.mvp.fugudatabase.CommonData.setNotificationsCountList(
                                unreadCountList
                        )
                        setNotificationsCount(unreadCountList.size)
                    }
                }.start()
            }

            override fun onError(error: ApiError?) {
            }

            override fun onFailure(throwable: Throwable?) {
            }

        })
    }

    @Suppress("DEPRECATION")
    private fun initViews() {
//        bottomNavigation = findViewById<View>(R.id.bottom_navigation) as AHBottomNavigation
        viewPager = findViewById<View>(R.id.viewpager) as NoSwipePager
        viewPager?.setPagingEnabled(false)
        val rolesList = getCreateGroupRolesList()
        val presentRole = workspacesInfoList[currentSigninedInPosition].role

//        navigationAdapter =
//                if (rolesList.contains(presentRole)) {
//                    AHBottomNavigationAdapter(this, R.menu.bottom_navigation_main)
//                } else {
//                    AHBottomNavigationAdapter(this, R.menu.bottom_navigation_guest)
//                }
//        bottomNavigation?.accentColor = resources.getColor(R.color.colorPrimary)
        homeToolbar = findViewById(R.id.homeToolbar)
        searchToolbar = findViewById(R.id.searchToolbar)
        notificationToolbar = findViewById(R.id.notificationToolbar)
        moreToolbar = findViewById(R.id.moreToolbar)
        searchLayout = findViewById(R.id.searchLayout)
        llHome = findViewById(R.id.llHome)
        tvHomeTitle = findViewById(R.id.tvHomeTitle)
        ivArraow = findViewById(R.id.ivArrow)
        rlCount = findViewById(R.id.rlCount)
        tvCount = findViewById(R.id.tvCount)
        ivArraow?.visibility = View.VISIBLE
        llInternet = findViewById(R.id.llInternet)
        tvStatus = findViewById(R.id.tvStatus)
        tvReturnCall = findViewById(R.id.tvReturnCall)
        rlNoti = findViewById(R.id.rlNoti)
        ivMore = findViewById(R.id.ivMore)
        searchToolbar?.searchToolbar
        searchToolbar?.setSupportActionBar(this)
        searchToolbar?.setOnSearchQueryChangedListener(this@MainActivityOld)
        homeToolbar?.visibility = View.VISIBLE
        notificationToolbar?.visibility = View.GONE
        notificationToolbar?.inflateMenu(R.menu.read_menu)
        notificationToolbar?.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.readAll -> {
                    showLoading()
//                    (pagerAdapter!!.getFragments()[1] as NotificationsFragment).apiMarkAllNotificationsRead()
                }
            }
            false
        }
        rlNoti?.setOnClickListener {
            val intent = Intent(this@MainActivityOld, NotificationsActivity::class.java)
            startActivity(intent)
        }
        ivMore?.setOnClickListener {
            val intent = Intent(this@MainActivityOld, MoreFragment::class.java)
            startActivity(intent)
        }

        searchLayout?.visibility = View.GONE
        moreToolbar?.visibility = View.GONE
//        navigationAdapter?.setupWithBottomNavigation(bottomNavigation)
//        bottomNavigation?.currentItem = 0
//        homeToolbar?.inflateMenu(R.menu.menu_my)
        setupViewPager()
        if (!TextUtils.isEmpty(sharedText) || !TextUtils.isEmpty(imageUri) ||
                !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getOtherFilesUriString()) ||
                !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getVideoUri())
                || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getMultipleImage())
        ) {
//            bottomNavigation?.visibility = View.GONE
            (pagerAdapter!!.getFragments()[0] as HomeFragment).hideFab(true)
            ivArraow?.visibility = View.INVISIBLE
        } else {
//            bottomNavigation?.visibility = View.VISIBLE
            (pagerAdapter!!.getFragments()[0] as HomeFragment).hideFab(false)
            ivArraow?.visibility = View.VISIBLE
        }
//        bottomNavigation?.isTranslucentNavigationEnabled = true
//        bottomNavigation?.isBehaviorTranslationEnabled = false
//        bottomNavigation?.titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW
//        bottomNavigation?.setOnTabSelectedListener { position, wasSelected ->
//            if (!wasSelected) {
//                viewPager?.currentItem = position
//            } else {
//                if (position == 0) {
//                    (pagerAdapter!!.getFragments()[0] as HomeFragment).navigateToTopOfScreen()
//                }
//            }
//            true
//        }
        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
//                bottomNavigation?.currentItem = position
                seUpToolbar(position)
            }
        })
        homeToolbar?.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_search -> {
                    val intent = Intent(this@MainActivityOld, HomeSearchActivity::class.java)
                    if (!TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getImageUri())
                            || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getSharedText())
                            || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getOtherFilesUriString())
                            || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getVideoUri())
                            || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getMultipleImage())
                    ) {
                        intent.putExtra("fromHome", true)
                    }
                    startActivity(intent)
                }
            }
            false
        }
        if (!TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getImageUri())
                || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getSharedText())
                || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getOtherFilesUriString())
                || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getVideoUri())
                || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getMultipleImage())
        ) {
            tvHomeTitle.text = "Share To..."
            llHome?.setOnClickListener(null)
        } else {
            tvHomeTitle.text = workspacesInfoList[currentSigninedInPosition].workspaceName
            llHome?.setOnClickListener {
                (pagerAdapter!!.getFragments()[0] as HomeFragment).toolBarDropDownManipulation()
                arrowAnimation()
            }
        }
    }

    fun arrowAnimation() {
        if (ivArraow?.visibility == View.VISIBLE) {
            val rotate: RotateAnimation
            if (!isOpened) {
                isOpened = true
                rotate = RotateAnimation(
                        0f,
                        180f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                )
            } else {
                isOpened = false
                rotate = RotateAnimation(
                        180f,
                        0f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                )
            }
            rotate.duration = 300
            rotate.fillAfter = true
            rotate.interpolator = LinearInterpolator()
            ivArraow?.startAnimation(rotate)
        }
    }

    @SuppressLint("DefaultLocale")
    private fun seUpToolbar(position: Int) {
        when (position) {
            0 -> {
                homeToolbar?.visibility = View.VISIBLE
                notificationToolbar?.visibility = View.GONE
                searchLayout?.visibility = View.GONE
                moreToolbar?.visibility = View.GONE
                tvHomeTitle.text = workspacesInfoList[currentSigninedInPosition].workspaceName
//                (pagerAdapter!!.getFragments()[1] as NotificationsFragment).terminateFayeConnection()
                (pagerAdapter!!.getFragments()[0] as HomeFragment).navigateToTopOfScreen()
            }
            1 -> {
                homeToolbar?.visibility = View.GONE
                notificationToolbar?.visibility = View.VISIBLE
                searchLayout?.visibility = View.GONE
                moreToolbar?.visibility = View.GONE
                setNotificationsCount(0)
                val unreadList = ArrayList<UnreadCount>()
                com.skeleton.mvp.fugudatabase.CommonData.setNotificationsCountList(unreadList)
//                Handler().postDelayed({
//                    (pagerAdapter!!.getFragments()[1] as NotificationsFragment).apiGetNotifications(
//                            1
//                    )
//                    (pagerAdapter!!.getFragments()[1] as NotificationsFragment).setUpfayeConection()
//                }, 300)
            }
            2 -> {
                currentSigninedInPosition = CommonData.getCurrentSignedInPosition()
                val rolesList = getCreateGroupRolesList()
                val presentRole = workspacesInfoList[currentSigninedInPosition].role
                if (rolesList.contains(presentRole)) {
                    homeToolbar?.visibility = View.GONE
                    notificationToolbar?.visibility = View.GONE
                    searchLayout?.visibility = View.VISIBLE
                    moreToolbar?.visibility = View.GONE
                    (pagerAdapter!!.getFragments()[2] as CreateGroupFragment).apiGetAllMembers()
//                    (pagerAdapter!!.getFragments()[1] as NotificationsFragment).terminateFayeConnection()
                } else {
                    homeToolbar?.visibility = View.GONE
                    notificationToolbar?.visibility = View.GONE
                    searchLayout?.visibility = View.GONE
                    moreToolbar?.visibility = View.VISIBLE
//                    (pagerAdapter!!.getFragments()[1] as NotificationsFragment).terminateFayeConnection()
                    (pagerAdapter!!.getFragments()[2] as MoreFragment).setProfileData()
                }

            }
            3 -> {
                homeToolbar?.visibility = View.GONE
                notificationToolbar?.visibility = View.GONE
                searchLayout?.visibility = View.GONE
                moreToolbar?.visibility = View.VISIBLE
//                (pagerAdapter!!.getFragments()[1] as NotificationsFragment).terminateFayeConnection()
                (pagerAdapter!!.getFragments()[3] as MoreFragment).setProfileData()
            }
            else -> {
                homeToolbar?.visibility = View.VISIBLE
                notificationToolbar?.visibility = View.GONE
                searchLayout?.visibility = View.GONE
                moreToolbar?.visibility = View.GONE
//                (pagerAdapter!!.getFragments()[1] as NotificationsFragment).terminateFayeConnection()
            }
        }
    }

    private fun getCreateGroupRolesList(): ArrayList<String> {
        var roles = workspacesInfoList[currentSigninedInPosition].config.enableCreateGroup
        roles = roles.replace("[", "")
        roles = roles.replace("]", "")
        roles = roles.replace("\"".toRegex(), "")
        val rolesArray = roles.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return java.util.ArrayList(listOf(*rolesArray))
    }

    private fun setupViewPager() {
        viewPager?.setPagingEnabled(false)
        viewPager?.offscreenPageLimit = 3
        pagerAdapter = BottomBarAdapter(supportFragmentManager)
        pagerAdapter?.addFragments(HomeFragment())
//        pagerAdapter?.addFragments(NotificationsFragment())
        currentSigninedInPosition = CommonData.getCurrentSignedInPosition()
        val rolesList = getCreateGroupRolesList()
        val presentRole = workspacesInfoList[currentSigninedInPosition].role
        if (rolesList.contains(presentRole)) {
            pagerAdapter?.addFragments(CreateGroupFragment())
        }
//        pagerAdapter?.addFragments(MoreFragment())
        viewPager?.adapter = pagerAdapter
    }

    fun apiClearChat(muid: String, channelId: Long?) {
        (pagerAdapter!!.getFragments()[0] as HomeFragment).apiClearChat(muid, channelId)
    }

    fun removeChat(channelId: Long?) {
        (pagerAdapter!!.getFragments()[0] as HomeFragment).removeChat(channelId)

    }

    fun muteGroup(channelId: Long?, isMuted: String) {
        (pagerAdapter!!.getFragments()[0] as HomeFragment).muteGroup(channelId, isMuted)
    }

    override fun selectBusiness(position: Int) {
        (pagerAdapter!!.getFragments()[0] as HomeFragment).selectBusiness()
    }

    override fun changeBusiness(
            position: Int,
            isAnimation: Boolean,
            refreshCurrentPosition: Boolean,
            changeBusiness: HomeFragment.ChangeBusiness?
    ) {
        val oldPos = currentSigninedInPosition
        CommonData.setCurrentSignedInPosition(position)
        currentSigninedInPosition = CommonData.getCurrentSignedInPosition()
        (pagerAdapter!!.getFragments()[0] as HomeFragment).changeBusiness(
                position,
                isAnimation,
                refreshCurrentPosition,
                object : HomeFragment.ChangeBusiness {
                    override fun changeBusinessSuccess() {

                        SocketConnection.disconnectSocket()
                        NotificationSockets.init(this@MainActivityOld, false)
                        SocketConnection.initSocketConnection(
                                com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().userInfo.accessToken,
                                workspacesInfoList[currentSigninedInPosition].enUserId,
                                com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.userId,
                                com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.userChannel,
                                "ChangeBusiness",
                                false,
                                com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.pushToken
                        )
                        val rolesList = getCreateGroupRolesList()
                        val oldWorkspaceRole = workspacesInfoList[oldPos].role
                        val presentRole = workspacesInfoList[currentSigninedInPosition].role
                        if (oldWorkspaceRole != presentRole && !((oldWorkspaceRole == "ADMIN" && presentRole.equals("OWNER"))
                                        || (presentRole.equals("ADMIN") && oldWorkspaceRole.equals("OWNER")))) {
//                            navigationAdapter =
//                                    if (rolesList.contains(presentRole)) {
//                                        AHBottomNavigationAdapter(
//                                                this@MainActivityNew,
//                                                R.menu.bottom_navigation_main
//                                        )
//                                    } else {
//                                        AHBottomNavigationAdapter(
//                                                this@MainActivityNew,
//                                                R.menu.bottom_navigation_guest
//                                        )
//                                    }
//                            navigationAdapter?.setupWithBottomNavigation(bottomNavigation)
                            setupViewPager()
                        }

                        if (!TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getImageUri())
                                || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getSharedText())
                                || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getOtherFilesUriString())
                                || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getVideoUri())
                                || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getMultipleImage())
                        ) {
                            tvHomeTitle.text = "Share To..."
                            (pagerAdapter!!.getFragments()[0] as HomeFragment).hideFab(true)
                        } else {
                            tvHomeTitle.text =
                                    workspacesInfoList[currentSigninedInPosition].workspaceName
                        }
                        if (!refreshCurrentPosition) {
                            arrowAnimation()
                        }
                        try {
                            Thread(Runnable {
                                try {
                                    val workspacesInfo =
                                            CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()]
                                    val notificationsList =
                                            ChatDatabase.getNotificationsMap().get(workspacesInfo.businessId)!!
                                    runOnUiThread {
                                        for (notification in notificationsList) {
                                            try {
                                                val nm =
                                                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                                                nm.cancel(notification)
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }
                                        Handler().postDelayed({
                                            val nm =
                                                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                if (nm.activeNotifications.size == 1) {
                                                    val notifications = nm.activeNotifications
                                                    for (notification in notifications) {
                                                        if (notification.id == PushReceiver.PushChannel.SUMMARY_NOTIFICATION_ID) {
                                                            nm.cancel(PushReceiver.PushChannel.SUMMARY_NOTIFICATION_ID)
                                                            break
                                                        }
                                                    }
                                                }
                                            }
                                        }, 100)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }).start()
                        } catch (e: Exception) {

                        }
                        onTrailExpired()

                    }

                })
    }

    fun changeBusinessAndOpenChat(position: Int, isAnimation: Boolean, openChat: OpenChat) {
        CommonData.setCurrentSignedInPosition(position)
        currentSigninedInPosition = CommonData.getCurrentSignedInPosition()
        (pagerAdapter!!.getFragments()[0] as HomeFragment).changeBusiness(
                position,
                isAnimation,
                false,
                object : HomeFragment.ChangeBusiness {
                    override fun changeBusinessSuccess() {
                        viewPager?.currentItem = 0
                        SocketConnection.disconnectSocket()
                        NotificationSockets.init(this@MainActivityOld, false)
                        SocketConnection.initSocketConnection(
                                com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().userInfo.accessToken,
                                workspacesInfoList[currentSigninedInPosition].enUserId,
                                com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.userId,
                                com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.userChannel,
                                "ChangeBusiness",
                                false,
                                com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.pushToken
                        )
                        val rolesList = getCreateGroupRolesList()
                        val presentRole = workspacesInfoList[currentSigninedInPosition].role
//                        navigationAdapter =
//                                if (rolesList.contains(presentRole)) {
//                                    AHBottomNavigationAdapter(
//                                            this@MainActivityNew,
//                                            R.menu.bottom_navigation_main
//                                    )
//                                } else {
//                                    AHBottomNavigationAdapter(
//                                            this@MainActivityNew,
//                                            R.menu.bottom_navigation_guest
//                                    )
//                                }
//                        navigationAdapter?.setupWithBottomNavigation(bottomNavigation)
                        setupViewPager()
                        viewPager?.currentItem = 0
                        tvHomeTitle.text =
                                workspacesInfoList[currentSigninedInPosition].workspaceName

                        openChat.openChat()
                    }

                })
    }

    override fun onSearchCollapsed() {
    }

    override fun onSearchQueryChanged(query: String?) {
        (pagerAdapter!!.getFragments()[2] as CreateGroupFragment).setFilteredList(query!!)
    }

    override fun onSearchExpanded() {
    }

    override fun onSearchSubmitted(query: String?) {
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu_my, menu)
//        val menuItem = menu.findItem(R.id.action_del)
//        val badgeView = menuItem.actionView.findViewById(R.id.ibv_icon) as ImageBadgeView
//        badgeView.badgeValue = 17
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
//        val menuItem = menu.findItem(R.id.action_del)
//        val badgeView = menuItem.actionView.findViewById(R.id.ibv_icon) as ImageBadgeView
//        badgeView.badgeValue = 17
//        return super.onPrepareOptionsMenu(menu)
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val itemId = item.itemId

        if (itemId == R.id.action_search) {
            searchToolbar?.onSearchIconClick()
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onStart() {
        super.onStart()
        loginViaBranch()
    }

    /**
     * Login in app via branch
     */
    private fun loginViaBranch() {
        Branch.getInstance().initSession({ referringParams, error ->
            if (error == null) {
                try {
                    if (CommonData.getCommonResponse() != null) {
                        if (referringParams?.has(FuguAppConstant.EMAIL) == true && referringParams.getString(
                                        FuguAppConstant.EMAIL
                                ) != CommonData.getCommonResponse().getData().getUserInfo().email
                        ) {
                            Log.e(TAG, "Nothing Email")
                        } else if (referringParams?.has(FuguAppConstant.EMAIL) == true && referringParams.getString(
                                        CONTACT_NUMBER
                                ) != CommonData.getCommonResponse().getData().getUserInfo().contactNumber
                        ) {
                            Log.e(TAG, "Nothing Contact")
                        } else {
                            Log.e(TAG, "Branch Login")
                            if (referringParams?.has(FuguAppConstant.EMAIL) == true || referringParams?.has(
                                            CONTACT_NUMBER
                                    ) == true
                            ) {
                                invite = true
                                showLoading()
                                workspace = referringParams.getString(WORKSPACE)
                                apiLoginViaAccessToken(BRANCH_IO, referringParams.getString(TOKEN))
                            }
                        }
                    } else {
                        Log.e(TAG, "Branch Login")
                        if (referringParams?.has(FuguAppConstant.EMAIL) == true || referringParams?.has(
                                        CONTACT_NUMBER
                                ) == true
                        ) {
                            invite = true
                            showLoading()
                            workspace = referringParams.getString(WORKSPACE)
                            apiLoginViaAccessToken(BRANCH_IO, referringParams.getString(TOKEN))
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            } else {
                Log.e(TAG, error.message)
            }
        }, this.intent.data, this)
    }

    private fun apiLoginViaAccessToken(source: String, accessToken: String?) {
        val commonParams: CommonParams.Builder = getLoginViaAccessTokenParams(source)

        RestClient.getApiInterface(true).accessTokenLogin(
                accessToken,
                BuildConfig.VERSION_CODE,
                FuguAppConstant.ANDROID,
                commonParams.build().map
        )
                .enqueue(object : ResponseResolver<FcCommonResponse>() {
                    override fun onSuccess(fcCommonResponse: FcCommonResponse?) {
                        hideLoading()

                        try {
                            setNotificationsCount(fcCommonResponse!!.data.unread_notification_count)
                        } catch (e: Exception) {
                            setNotificationsCount(com.skeleton.mvp.fugudatabase.CommonData.getNotificationCountList().size)
                        }

                        if (CommonData.getLocalNotificationId().compareTo(0) == 0) {
                            CommonData.setLocalNotificationId(fcCommonResponse?.data?.lastNotificationId!!)
                        }
                        checkVersion(fcCommonResponse)
                    }

                    override fun onError(error: ApiError?) {
                        hideLoading()
                        if (error?.statusCode == 401) {
                            CommonData.clearData()
                            FuguConfig.clearFuguData(this@MainActivityOld)
                            startActivity(Intent(this@MainActivityOld, OnboardActivity::class.java))
                            finishAffinity()
                        } else {
                            showErrorMessage(error?.message!!)
                        }
                    }

                    override fun onFailure(throwable: Throwable?) {
                        hideLoading()
                        if (throwable is SocketTimeoutException) {
                            try {
                                showErrorMessage("Please check your Internet connection and Try Again !") {
                                    val fcCommonResponse = CommonData.getCommonResponse()
                                    if (isNetworkConnected) {
                                        showLoading()
                                        if (fcCommonResponse.getData() != null && fcCommonResponse.getData().getUserInfo() != null) {
                                            apiLoginViaAccessToken(
                                                    "",
                                                    fcCommonResponse.getData().getUserInfo().getAccessToken()
                                            )
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }
                    }

                })

    }

    @SuppressLint("UseSparseArrays")
    private fun checkVersion(fcCommonResponse: FcCommonResponse?) {
        when (fcCommonResponse?.getData()?.getUserInfo()?.appUpdateConfig?.appUpdateMessage) {
            HARD_UPDATE -> showUpdateMessage(fcCommonResponse, HARD_UPDATE, "Exit")
            SOFT_UPDATE -> if (com.skeleton.mvp.fugudatabase.CommonData.getAppVersionCount() != null && com.skeleton.mvp.fugudatabase.CommonData.getAppVersionCount()[BuildConfig.VERSION_CODE] != null) {
                if (com.skeleton.mvp.fugudatabase.CommonData.getAppVersionCount()[BuildConfig.VERSION_CODE]!! < 3) {
                    val versionMap = HashMap<Int, Int>()
                    versionMap[BuildConfig.VERSION_CODE] =
                            com.skeleton.mvp.fugudatabase.CommonData.getAppVersionCount()[BuildConfig.VERSION_CODE]!! + 1
                    com.skeleton.mvp.fugudatabase.CommonData.setAppVersionCount(versionMap)
                    showUpdateMessage(fcCommonResponse, SOFT_UPDATE, "Cancel")
                }
            } else {
                val versionMap = HashMap<Int, Int>()
                versionMap[BuildConfig.VERSION_CODE] = 1
                com.skeleton.mvp.fugudatabase.CommonData.setAppVersionCount(versionMap)
                showUpdateMessage(fcCommonResponse, SOFT_UPDATE, "Cancel")
            }
            else -> {
                getInsideApp(fcCommonResponse!!)
            }
        }
    }

    private fun getLoginViaAccessTokenParams(source: String): CommonParams.Builder {
        val commonParams = CommonParams.Builder()
        commonParams.add("time_zone", getTimeZoneOffset())
        if (!TextUtils.isEmpty(source)) {
            commonParams.add(SOURCE, source)
        }

        if (TextUtils.isEmpty(CommonData.getFcmToken())) {
            val token = FirebaseInstanceId.getInstance().token
            CommonData.updateFcmToken(token)
        }

        commonParams.add(TOKEN, CommonData.getFcmToken())
        commonParams.add(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(this@MainActivityOld))
        val devicePayload = JSONObject()
        devicePayload.put(FuguAppConstant.DEVICE_ID, com.skeleton.mvp.utils.UniqueIMEIID.getUniqueIMEIId(this))
        devicePayload.put(FuguAppConstant.DEVICE_TYPE, FuguAppConstant.ANDROID_USER)
        devicePayload.put(FuguAppConstant.APP_VERSION, BuildConfig.VERSION_NAME)
        commonParams.add(FuguAppConstant.DEVICE_DETAILS, com.skeleton.mvp.fugudatabase.CommonData.deviceDetails(this))
        val workspaceIds = java.util.ArrayList<Int>()
        for (i in 0 until workspacesInfoList.size) {
            workspaceIds.add(workspacesInfoList[i].workspaceId)
        }
        commonParams.add("user_workspace_ids", workspaceIds)
        return commonParams
    }

    override fun onBackPressed() {
        if (viewPager?.currentItem != 0) {
            viewPager?.currentItem = 0
        } else {
            finish()
        }
    }

    interface OpenChat {
        fun openChat()
    }


//    override fun onResume() {
//        super.onResume()
//        unregisterRecievers()
//        LocalBroadcastManager.getInstance(this).registerReceiver(mCountReciever,
//                IntentFilter(FuguAppConstant.NOTIFICATION_COUNTER_INTENT))
//        LocalBroadcastManager.getInstance(this).registerReceiver(mNetworkState,
//                IntentFilter(FuguAppConstant.NETWORK_STATE_INTENT))
//        LocalBroadcastManager.getInstance(this).registerReceiver(mVideoCallHungUp,
//                IntentFilter(FuguAppConstant.VIDEO_CALL_HUNGUP))
//        setNotificationsCount(com.skeleton.mvp.fugudatabase.CommonData.getNotificationCountList().size)
//        if (CommonData.getCurrentSignedInPosition() >= workspacesInfoList.size) {
//            CommonData.setCurrentSignedInPosition(0)
//        }
//        newSignedInPosition = CommonData.getCurrentSignedInPosition()
//
//        if (newSignedInPosition != currentSigninedInPosition || viewPager?.currentItem == 1) {
//            viewPager?.currentItem = 0
//            currentSigninedInPosition = newSignedInPosition
//            if (!TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getImageUri())
//                    || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getSharedText())
//                    || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getOtherFilesUriString())
//                    || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getVideoUri())) {
//                tvHomeTitle.text = "Share To..."
//                llHome?.setOnClickListener(null)
//            } else {
//                tvHomeTitle.text = workspacesInfoList[currentSigninedInPosition].workspaceName
//                llHome?.setOnClickListener {
//                    (pagerAdapter!!.getFragments()[0] as HomeFragment).toolBarDropDownManipulation()
//                    arrowAnimation()
//                }
//
//            }
//        }
//        Handler().postDelayed({
//            if (isMyServiceRunning(VideoCallService::class.simpleName)) {
//                tvReturnCall?.visibility = View.VISIBLE
//                tvReturnCall?.setOnClickListener { startActivity(Intent(this@MainActivityNew, FuguCallActivity::class.java)) }
//            } else {
//                tvReturnCall?.visibility = View.GONE
//            }
//        }, 1000)
//        (pagerAdapter!!.getFragments()[0] as HomeFragment).setUpSpacesRecycler()
//
//        try {
//            if (TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getImageUri())
//                    && TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getSharedText())
//                    && TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getOtherFilesUriString())
//                    && TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getVideoUri())) {
//                (pagerAdapter!!.getFragments()[0] as HomeFragment).hideFab(false)
//                bottomNavigation?.visibility = View.VISIBLE
//                tvHomeTitle.text = workspacesInfoList[currentSigninedInPosition].workspaceName
//                ivArraow?.visibility = View.VISIBLE
//                llHome?.setOnClickListener {
//                    (pagerAdapter!!.getFragments()[0] as HomeFragment).toolBarDropDownManipulation()
//                    arrowAnimation()
//                }
//            }
//        } catch (e: Exception) {
//        }
//
//        if (CommonData.getCommonResponse() != null) {
//            setUpSocketListeners("Resume Main")
//        }
//
//
//        if (IncomingVideoConferenceActivity.IncomingCall.incomingConferenceStatus) {
//            val intent = Intent(this@MainActivityNew, VideoConfActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
//            startActivity(intent)
//        }
//
//        try {
//            Thread(Runnable {
//                try {
//                    val workspacesInfo = CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()]
//                    val missedCallNotificationsList = ChatDatabase.getCallNotificationsMap()[workspacesInfo.businessId]
//                    runOnUiThread {
//                        if (missedCallNotificationsList != null) {
//                            try {
//                                val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//                                nm.cancel("CALL", workspacesInfo.businessId!!.toInt())
//                                ChatDatabase.removeCallNotifications(java.lang.Long.valueOf(workspacesInfo.businessId!!.toLong()))
//                            } catch (e: Exception) {
//
//                            }
//
//                        }
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }).start()
//        } catch (e: Exception) {
//
//        }
//    }


    override fun onResume() {
        super.onResume()

        if (!isNetworkConnected) {
            llInternet?.visibility = View.VISIBLE
            llInternet?.setBackgroundColor(Color.parseColor("#FF0000"))
            tvStatus?.setText(R.string.fugu_not_connected_to_internet)
        } else {
            llInternet?.visibility = View.GONE
        }

        unregisterRecievers()
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mCountReciever,
                IntentFilter(FuguAppConstant.NOTIFICATION_COUNTER_INTENT)
        )
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mNetworkState,
                IntentFilter(FuguAppConstant.NETWORK_STATE_INTENT)
        )
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mVideoCallHungUp,
                IntentFilter(FuguAppConstant.VIDEO_CALL_HUNGUP)
        )
        try {
            setNotificationsCount(CommonData.getCommonResponse().data.unread_notification_count)
        } catch (e: Exception) {
            setNotificationsCount(com.skeleton.mvp.fugudatabase.CommonData.getNotificationCountList().size)
        }
        if (CommonData.getCurrentSignedInPosition() >= workspacesInfoList.size) {
            CommonData.setCurrentSignedInPosition(0)
        }
        newSignedInPosition = CommonData.getCurrentSignedInPosition()

        if (newSignedInPosition != currentSigninedInPosition || viewPager?.currentItem == 1) {
            viewPager?.currentItem = 0
            val rolesList = getCreateGroupRolesList()
            val presentRole = workspacesInfoList[currentSigninedInPosition].role
//            navigationAdapter =
//                    if (rolesList.contains(presentRole)) {
//                        AHBottomNavigationAdapter(this@MainActivityNew, R.menu.bottom_navigation_main)
//                    } else {
//                        AHBottomNavigationAdapter(this@MainActivityNew, R.menu.bottom_navigation_guest)
//                    }
//            navigationAdapter?.setupWithBottomNavigation(bottomNavigation)
            setupViewPager()


            currentSigninedInPosition = newSignedInPosition
            if (!TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getImageUri())
                    || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getSharedText())
                    || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getOtherFilesUriString())
                    || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getVideoUri())
                    || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getMultipleImage())
            ) {
                tvHomeTitle.text = "Share To..."
                llHome?.setOnClickListener(null)
            } else {
                tvHomeTitle.text = workspacesInfoList[currentSigninedInPosition].workspaceName
                llHome?.setOnClickListener {
                    (pagerAdapter!!.getFragments()[0] as HomeFragment).toolBarDropDownManipulation()
                    arrowAnimation()
                }

            }
        }

        Handler().postDelayed({
            if (isMyServiceRunning(VideoCallService::class.simpleName)) {
                tvReturnCall?.visibility = View.VISIBLE
                tvReturnCall?.setOnClickListener {
                    startActivity(
                            Intent(
                                    this@MainActivityOld,
                                    FuguCallActivity::class.java
                            )
                    )
                }
            } else {
                tvReturnCall?.visibility = View.GONE
            }
        }, 1000)
        (pagerAdapter!!.getFragments()[0] as HomeFragment).setUpSpacesRecycler()

        try {
            if (TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getImageUri())
                    && TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getSharedText())
                    && TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getOtherFilesUriString())
                    && TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getVideoUri())
                    && TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getMultipleImage())
            ) {
                (pagerAdapter!!.getFragments()[0] as HomeFragment).hideFab(false)
//                bottomNavigation?.visibility = View.VISIBLE
                tvHomeTitle.text = workspacesInfoList[currentSigninedInPosition].workspaceName
                ivArraow?.visibility = View.VISIBLE
                llHome?.setOnClickListener {
                    (pagerAdapter!!.getFragments()[0] as HomeFragment).toolBarDropDownManipulation()
                    arrowAnimation()
                }
            }
        } catch (e: Exception) {
        }

        if (CommonData.getCommonResponse() != null) {
            setUpSocketListeners("Resume Main")
        }


        if (IncomingVideoConferenceActivity.IncomingCall.incomingConferenceStatus) {
            val intent = Intent(this@MainActivityOld, VideoConfActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            startActivity(intent)
        }

        try {
            Thread(Runnable {
                try {
                    val workspacesInfo =
                            CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()]
                    val missedCallNotificationsList =
                            ChatDatabase.getCallNotificationsMap()[workspacesInfo.businessId]
                    runOnUiThread {
                        if (missedCallNotificationsList != null) {
                            try {
                                val nm =
                                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                                nm.cancel("CALL", workspacesInfo.businessId!!.toInt())
                                ChatDatabase.removeCallNotifications(
                                        java.lang.Long.valueOf(
                                                workspacesInfo.businessId!!.toLong()
                                        )
                                )
                            } catch (e: Exception) {

                            }

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }).start()
        } catch (e: Exception) {

        }
    }

    override fun onStop() {
        super.onStop()
        unregisterRecievers()
    }

    private fun unregisterRecievers() {
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mCountReciever)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mNetworkState)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mVideoCallHungUp)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private val mCountReciever = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            setNotificationsCount(com.skeleton.mvp.fugudatabase.CommonData.getNotificationCountList().size)
        }

    }

    override fun deactivatedUser() {
        (pagerAdapter!!.getFragments()[0] as HomeFragment).setUpSpacesRecycler()
    }

    private val mVideoCallHungUp = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            tvReturnCall?.visibility = View.GONE
        }

    }

    private val mNetworkState = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.getIntExtra("status", 1)) {
                NOT_CONNECTED -> {
                    llInternet?.visibility = View.VISIBLE
                    llInternet?.setBackgroundColor(Color.parseColor("#FF0000"))
                    tvStatus?.setText(R.string.fugu_not_connected_to_internet)
                }
                CONNECTED_TO_INTERNET, CONNECTED_TO_INTERNET_VIA_WIFI -> {
                    llInternet?.setBackgroundColor(Color.parseColor("#FFA500"))
                    tvStatus?.setText(R.string.fugu_connecting)
                    Handler().postDelayed({
                        llInternet?.setBackgroundColor(Color.parseColor("#00FF00"))
                        tvStatus?.setText(R.string.fugu_connected)
                        llInternet?.visibility = View.GONE
                        (pagerAdapter!!.getFragments()[0] as HomeFragment).backgroundSendingDone()
                        if (CommonData.getCommonResponse() != null) {
                            setUpSocketListeners("Network chat main")
                        }
                    }, 1500)
                }
                else -> {
                }
            }
        }

    }

    fun publishMessage(notificationId: Int) {
//        (pagerAdapter!!.getFragments()[1] as NotificationsFragment).publishMessage(notificationId)
    }

    fun setWorkspaceName(name: String) {
        if (!TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getImageUri())
                || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getSharedText())
                || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getOtherFilesUriString())
                || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getVideoUri())
                || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getMultipleImage())
        ) {
            tvHomeTitle.text = "Share To..."
        } else {
            tvHomeTitle.text = name

        }
    }

    fun setNotificationsCount(count: Int) {
        runOnUiThread {
            if (count > 0) {
                rlCount?.visibility = View.VISIBLE
                tvCount?.text = count.toString()
            } else {
                rlCount?.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterRecievers()
    }

    fun navigateToHome() {
        viewPager?.currentItem = 0
        seUpToolbar(0)
    }

    fun inflateMenu(isToBeInflated: Boolean) {
        if (isToBeInflated) {
            notificationToolbar?.menu?.clear()
            notificationToolbar?.inflateMenu(R.menu.read_menu)
        } else {
            notificationToolbar?.menu?.clear()
        }
    }

    /**
     * Show update message hard/soft
     */
    private fun showUpdateMessage(
            fcCommonResponse: FcCommonResponse,
            hard_update: String,
            negativeMessage: String
    ) {
        if (!isPopupShowing) {
            isPopupShowing = true
            showErrorMessage(
                    fcCommonResponse.getData().getUserInfo().appUpdateConfig.appUpdateText,
                    {
                        isPopupShowing = false
                        openPlayStore(fcCommonResponse)
                    },
                    {
                        isPopupShowing = false
                        if (hard_update == HARD_UPDATE) {
                            finish()
                        } else {
                            getInsideApp(fcCommonResponse)
                        }
                    },
                    "Update",
                    negativeMessage
            )
        }
    }

    private fun getInsideApp(fcCommonResponse: FcCommonResponse) {
        Thread {
            kotlin.run {
                try {
                    setNotificationsCount(fcCommonResponse.data.unread_notification_count)
                } catch (e: Exception) {
                    setNotificationsCount(com.skeleton.mvp.fugudatabase.CommonData.getNotificationCountList().size)
                }
                val workspaceIdsNew = java.util.ArrayList<Int>()
                workspacesInfoList =
                        fcCommonResponse.data?.workspacesInfo as ArrayList<WorkspacesInfo>
                val workpsacesMap = LinkedHashMap<String, String>()
                for (i in 0 until workspacesInfoList.size) {
                    workspaceIdsNew.add(workspacesInfoList[i].workspaceId)
                    workpsacesMap[workspacesInfoList[i].fuguSecretKey] =
                            workspacesInfoList[i].workspaceName
                }
                for (i in 0 until fcCommonResponse.getData()?.workspacesInfo?.size!!) {
                    com.skeleton.mvp.fugudatabase.CommonData.setWorkspaceResponse(
                            fcCommonResponse.getData()?.workspacesInfo!![i].fuguSecretKey,
                            fcCommonResponse.getData().workspacesInfo[i]
                    )
                }
                if (workspacesInfoList.size > 0) {
                    if (fcCommonResponse.getData().workspacesInfo[0].enUserId == null) {
                        for (i in 0 until fcCommonResponse.getData()?.workspacesInfo?.size!!) {
                            if (fcCommonResponse.getData()?.workspacesInfo!![i]?.userId == null) {
                                fcCommonResponse.data?.workspacesInfo?.get(i)?.userId =
                                        CommonData.getCommonResponse().getData().workspacesInfo[i].userId
                            }
                            if (fcCommonResponse.getData().workspacesInfo[i].enUserId == null) {
                                fcCommonResponse.data?.workspacesInfo?.get(i)?.enUserId =
                                        CommonData.getCommonResponse().getData().workspacesInfo[i].enUserId
                            }
                        }
                    }
                    CommonData.setCommonResponse(fcCommonResponse)
                    CommonData.setWorkspacesMap(workpsacesMap)
                    (pagerAdapter!!.getFragments()[0] as HomeFragment).setUpSpacesRecycler()
                } else {
                    CommonData.setCommonResponse(fcCommonResponse)
                    CommonData.setWorkspacesMap(workpsacesMap)
                    runOnUiThread {
                        val intent = Intent(this@MainActivityOld, CreateWorkspaceActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.right_in, R.anim.left_out)
                    }
                }

                (pagerAdapter!!.getFragments()[0] as HomeFragment).checkIfUserIsDisabled()
                runOnUiThread {
                    onTrailExpired()
                }
            }
        }.start()
    }

    /**
     * open playstore
     */
    private fun openPlayStore(fcCommonResponse: FcCommonResponse) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("market://details?id=$packageName")
        try {
            startActivityForResult(intent, REQUEST_CODE_PLAY_STORE)
        } catch (e: Exception) {
            intent.data =
                    Uri.parse(fcCommonResponse.getData().getUserInfo().appUpdateConfig.appLink)
        }

    }

    @Suppress("DEPRECATION")
    private fun isMyServiceRunning(serviceClass: String?): Boolean {
        val manager: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Int.MAX_VALUE)) {
            if (service.service.className.contains(serviceClass!!)) {
                return true
            }
        }
        return false
    }

    fun setUpSocketListeners(callingMethod: String) {

        if (workspacesInfoList == null && CommonData.getCommonResponse() != null && CommonData.getCommonResponse().data != null) {
            workspacesInfoList =
                    CommonData.getCommonResponse().data.workspacesInfo as ArrayList<WorkspacesInfo>
        }
        if (currentSigninedInPosition == 0) {
            currentSigninedInPosition = CommonData.getCurrentSignedInPosition()
        }
        if (workspacesInfoList != null && !workspacesInfoList.isEmpty() && workspacesInfoList[currentSigninedInPosition] != null &&
                CommonData.getCommonResponse() != null && CommonData.getCommonResponse().data != null && CommonData.getCommonResponse().data.userInfo != null
                && workspacesInfoList[currentSigninedInPosition].enUserId != null && CommonData.getCommonResponse().data.userInfo.userId != null && CommonData.getCommonResponse().data.userInfo.userChannel != null
        ) {
            NotificationSockets.init(this@MainActivityOld, false)
            SocketConnection.initSocketConnection(
                    com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().userInfo.accessToken,
                    workspacesInfoList[currentSigninedInPosition].enUserId,
                    CommonData.getCommonResponse().data.userInfo.userId,
                    CommonData.getCommonResponse().data.userInfo.userChannel, callingMethod, false,
                    com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.pushToken
            )
        }

        SocketConnection.setSocketListeners(object : SocketConnection.SocketClientCallback {
            override fun onCalling(messageJson: String) {

            }

            override fun onPresent(messageJson: String) {

            }

            override fun onMessageSent(messageJson: String) {
                try {
                    val messageJson = JSONObject(messageJson)
                    unsentMessageMap[messageJson.getLong(CHANNEL_ID)]!!.remove(
                            messageJson.getString(
                                    MESSAGE_UNIQUE_ID
                            )
                    )
                    ChatDatabase.setUnsentMessageMapByChannel(
                            messageJson.getLong(CHANNEL_ID),
                            unsentMessageMap.get(messageJson.getLong(CHANNEL_ID))!!
                    )
                    if (unsentMessageMap.size > 0) {
                        var keys = unsentMessageMap.keys
                        sendFirstUnsentMessageOfList(
                                keys.first(),
                                unsentMessageMap.get(keys.first())!!
                        )
                    } else {
                        (pagerAdapter!!.getFragments()[0] as HomeFragment).backgroundSendingDone()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onThreadMessageSent(messageJson: String) {
                try {
                    val messageJson = JSONObject(messageJson)
                    unsentMessageMap[messageJson.getLong(CHANNEL_ID)]!!.remove(
                            messageJson.getString(
                                    THREAD_MUID
                            )
                    )
                    ChatDatabase.setUnsentMessageMapByChannel(
                            messageJson.getLong(CHANNEL_ID),
                            unsentMessageMap.get(messageJson.getLong(CHANNEL_ID))!!
                    )
                    if (unsentMessageMap.size > 0) {
                        sendFirstUnsentMessageOfList(
                                messageJson.getLong(CHANNEL_ID),
                                unsentMessageMap.get(messageJson.getLong(CHANNEL_ID))!!
                        )
                    } else {
                        (pagerAdapter!!.getFragments()[0] as HomeFragment).backgroundSendingDone()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onMessageReceived(messageJson: String) {
                try {
                    userId = workspacesInfoList[currentSigninedInPosition].userId.toLong()
                    val messageJson = JSONObject(messageJson)
                    if (userId == messageJson.getLong(FuguAppConstant.USER_ID)) {

                        try {
                            Thread {
                                kotlin.run {
                                    var currentConversation: FuguConversation? = null
                                    val conversationMap = ChatDatabase.getConversationMap(
                                            messageJson.getString(FuguAppConstant.APP_SECRET_KEY)
                                    )
                                    currentConversation =
                                            conversationMap[messageJson.getLong(CHANNEL_ID)]
                                    if (currentConversation != null) {
                                        var new_message: String
                                        if (messageJson.has(FuguAppConstant.MESSAGE)) {
                                            new_message =
                                                    messageJson.getString(FuguAppConstant.MESSAGE)
                                        } else {
                                            new_message = ""
                                        }

                                        currentConversation.message = new_message
                                        currentConversation.message =
                                                FormatStringUtil.FormatString.getFormattedString(
                                                        new_message
                                                )[1]
                                        currentConversation.dateTime =
                                                messageJson.getString(FuguAppConstant.DATE_TIME)
                                                        .replace("+00:00", ".000Z")
                                        currentConversation.message_type =
                                                messageJson.getInt(MESSAGE_TYPE)
                                        currentConversation.chat_type =
                                                messageJson.getInt(CHAT_TYPE)
                                        currentConversation.unreadCount = 0
                                        currentConversation.last_sent_by_id =
                                                messageJson.getLong(LAST_SENT_BY_ID)
                                        currentConversation.last_sent_by_full_name =
                                                messageJson.getString(LAST_SENT_BY_FULL_NAME)
                                        currentConversation.messageState = 1
                                        currentConversation.muid =
                                                messageJson.getString(MESSAGE_UNIQUE_ID)
                                        val membersInfoList = currentConversation.membersInfo
                                        var isPersonAlreadyPresent = false
                                        if (membersInfoList != null && membersInfoList.size > 0) {
                                            for (i in 0 until membersInfoList.size) {
                                                if (membersInfoList[i].userId.compareTo(
                                                                messageJson.getLong(
                                                                        FuguAppConstant.LAST_SENT_BY_ID
                                                                )
                                                        ) == 0
                                                ) {
                                                    isPersonAlreadyPresent = true
                                                    val memberInfo = membersInfoList[i]
                                                    membersInfoList[i] = membersInfoList[0]
                                                    membersInfoList[0] = memberInfo
                                                }
                                            }
                                            if (!isPersonAlreadyPresent) {
                                                when (membersInfoList.size) {
                                                    2 -> {
                                                        membersInfoList[1] = membersInfoList[0]
                                                        val membersInfo = MembersInfo()
                                                        membersInfo.userId =
                                                                messageJson.getLong(FuguAppConstant.LAST_SENT_BY_ID)
                                                        membersInfo.fullName =
                                                                messageJson.getString(FuguAppConstant.LAST_SENT_BY_FULL_NAME)
                                                        membersInfo.userImage =
                                                                messageJson.getString(
                                                                        USER_THUMBNAIL_IMAGE
                                                                )
                                                        membersInfoList[0] = membersInfo
                                                    }
                                                    3 -> {
                                                        membersInfoList[2] = membersInfoList[1]
                                                        membersInfoList[1] = membersInfoList[0]
                                                        val membersInfo = MembersInfo()
                                                        membersInfo.userId =
                                                                messageJson.getLong(FuguAppConstant.LAST_SENT_BY_ID)
                                                        membersInfo.fullName =
                                                                messageJson.getString(FuguAppConstant.LAST_SENT_BY_FULL_NAME)
                                                        membersInfo.userImage =
                                                                messageJson.getString(
                                                                        USER_THUMBNAIL_IMAGE
                                                                )
                                                        membersInfoList[0] = membersInfo
                                                    }
                                                    else -> {
                                                    }
                                                }
                                            }
                                        }
                                        Thread {
                                            kotlin.run {
                                                conversationMap[messageJson.getLong(CHANNEL_ID)] =
                                                        currentConversation
                                                ChatDatabase.setConversationMap(
                                                        conversationMap,
                                                        messageJson.getString(FuguAppConstant.APP_SECRET_KEY)
                                                )
                                                val mIntent = Intent(FuguAppConstant.CHANNEL_INTENT)
                                                try {
                                                    mIntent.putExtra(
                                                            CHANNEL_ID,
                                                            messageJson.getLong(CHANNEL_ID)
                                                    )
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                                LocalBroadcastManager.getInstance(this@MainActivityOld)
                                                        .sendBroadcast(mIntent)
                                            }
                                        }.start()

                                    } else {
                                        if (com.skeleton.mvp.data.db.CommonData.getCommonResponse() != null && com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData() != null
                                                && com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()] != null &&
                                                com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].enUserId != null
                                        ) {
                                            apiGetuserChannelInfo(
                                                    getChannelInfoRequestParams(
                                                            messageJson
                                                    ), messageJson, this@MainActivityOld
                                            )
                                        }

                                    }
                                }
                            }.start()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onTypingStarted(messageJson: String) {
            }

            override fun onTypingStopped(messageJson: String) {
            }

            override fun onThreadMessageReceived(messageJson: String) {
            }

            override fun onReadAll(messageJson: String) {
                try {
                    val messageJson = JSONObject(messageJson)
                    if (messageJson.has(FuguAppConstant.APP_SECRET_KEY) && messageJson.getLong(
                                    FuguAppConstant.USER_ID
                            ).compareTo(workspacesInfoList[currentSigninedInPosition].userId.toLong()) == 0
                    ) {
                        val conversationMap =
                                ChatDatabase.getConversationMap(messageJson.getString(FuguAppConstant.APP_SECRET_KEY))
                        val conversation = conversationMap[messageJson.getLong(CHANNEL_ID)]
                        conversation?.unreadCount = 0
                        conversationMap[messageJson.getLong(CHANNEL_ID)] = conversation!!
                        ChatDatabase.setConversationMap(
                                conversationMap,
                                messageJson.getString(FuguAppConstant.APP_SECRET_KEY)
                        )
                        (pagerAdapter!!.getFragments()[0] as HomeFragment).updateList()
                    }
                } catch (e: Exception) {
                }
            }

            override fun onPinChat(messageJson: String) {

            }

            override fun onUnpinChat(messageJson: String) {

            }

            override fun onPollVoteReceived(messageJson: String) {
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

                initiateMessageAutoSending()

            }

            override fun onDisconnect() {
            }

            override fun onConnectError(socket: Socket, message: String) {
            }

            override fun onErrorReceived(messageJson: String) {
            }

            override fun onTaskAssigned(messageJson: String) {

            }

            override fun onMeetScheduled(messageJson: String) {

            }

            override fun onUpdateNotificationCount(messageJson: String) {

            }

        })
    }

    private fun initiateMessageAutoSending() {
        if (ChatDatabase.getUnsentMessageMap() != null) {
            unsentMessageMap = ChatDatabase.getUnsentMessageMap()
        }
        val keys = ArrayList(unsentMessageMap.keys)
        if (keys.size > 0) {
            sendFirstUnsentMessageOfList(keys.get(0), unsentMessageMap.get(keys.get(0))!!)
        }
    }

    private fun sendFirstUnsentMessageOfList(
            key: Long,
            mUnsentMessageMapByChannel: java.util.LinkedHashMap<String, Message>
    ) {
        try {
            val localDate = DateUtils.getFormattedDate(Date())
            val myUnsentMessagemapByChannel = java.util.LinkedHashMap<String, Message>()
            for (name in mUnsentMessageMapByChannel.keys) {
                val messageJson = JSONObject()
                val messageObj = mUnsentMessageMapByChannel[name]!!
                val newTime =
                        DateUtils.getTimeInMinutes(DateUtils.getInstance().convertToUTC(localDate))
                val oldTime = DateUtils.getTimeInMinutes(messageObj.sentAtUtc)
                if (mUnsentMessageMapByChannel[name]!!.messageType == FuguAppConstant.TEXT_MESSAGE && !messageObj.isExpired && newTime - oldTime < 10) {
                    mUnsentMessageMapByChannel[name]!!.isThreadMessage = !TextUtils.isEmpty(mUnsentMessageMapByChannel[name]!!.threadMuid)
                    myUnsentMessagemapByChannel[name] = mUnsentMessageMapByChannel[name]!!
                } else {
                    if (mUnsentMessageMapByChannel[name]!!.messageType == FuguAppConstant.TEXT_MESSAGE) {
                        messageObj.isExpired = true
                    }
                }
            }

            val keys = ArrayList(myUnsentMessagemapByChannel.keys)
            val fuguMessageList = ChatDatabase.getMessageList(key)
            val messageJson = JSONObject()
            if (keys.size > 0) {
                val messageObj = myUnsentMessagemapByChannel[keys[0]]!!
                try {
                    if (messageObj.userId.toString().equals(userId.toString()) && messageObj.messageStatus == MESSAGE_UNSENT) {
                        messageJson.put(FuguAppConstant.CHANNEL_ID, key)
                        messageJson.put(FuguAppConstant.USER_ID, messageObj.userId.toString())
                        messageJson.put(FuguAppConstant.FULL_NAME, messageObj.getfromName())
                        messageJson.put(FuguAppConstant.MESSAGE, messageObj.message)
                        if (messageObj.image_url.isEmpty()) {
                            messageJson.put("message_type", messageObj.messageType)

                        } else {
                            messageJson.put("message_type", messageObj.messageType)
                            try {
                                val messageObj2 = fuguMessageList[0]
                                messageObj2.messageStatus = FuguAppConstant.MESSAGE_UNSENT
                                messageObj2.isSent = false
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }
                        val date = DateUtils.getFormattedDate(Calendar.getInstance().time)
                        messageJson.put(FuguAppConstant.USER_TYPE, FuguAppConstant.ANDROID_USER)
                        messageJson.put(
                                FuguAppConstant.DATE_TIME,
                                DateUtils.getInstance().convertToUTC(localDate)
                        )
                        messageJson.put(FuguAppConstant.MESSAGE_INDEX, messageObj.messageIndex)
                        messageJson.put(
                                FuguAppConstant.IS_TYPING,
                                FuguAppConstant.TYPING_SHOW_MESSAGE
                        )
                        messageJson.put(FuguAppConstant.MESSAGE_STATUS, messageObj.messageStatus)
                        if (messageObj.isThreadMessage!!) {
                            messageJson.put(
                                    FuguAppConstant.MESSAGE_UNIQUE_ID,
                                    messageObj.threadMuid
                            )
                            messageJson.put("thread_muid", messageObj.uuid)
                            messageJson.put("is_thread_message", true)
                        } else {
                            messageJson.put(FuguAppConstant.MESSAGE_UNIQUE_ID, messageObj.uuid)
                            messageJson.put("is_thread_message", false)
                        }
                        messageJson.put(FuguAppConstant.EMAIL, "")
                        runOnUiThread {
                            Handler().postDelayed({
                                if (messageObj.messageType == FuguAppConstant.TEXT_MESSAGE) {
                                    if (messageObj.isThreadMessage) {
                                        SocketConnection.sendThreadMessage(messageJson)
                                    } else {
                                        SocketConnection.sendMessage(messageJson)
                                    }
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
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.e("New Intent", intent.toString())
    }


    private fun apiGetuserChannelInfo(
            channelInfoRequestParams: com.skeleton.mvp.retrofit.CommonParams?,
            messageJson: JSONObject,
            context: Context
    ) {

        if (channelInfoRequestParams != null) {
            com.skeleton.mvp.retrofit.RestClient.getApiInterface().getChannelInfo(
                    com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                    com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey,
                    1, BuildConfig.VERSION_CODE, channelInfoRequestParams.map
            ).enqueue(object : ResponseResolver<ChannelResponse>() {
                override fun onSuccess(channelResponse: ChannelResponse?) {
                    val conversation = FuguConversation()
                    conversation.membersInfo =
                            channelResponse?.data?.membersInfo as java.util.ArrayList<MembersInfo>?
                    if (!TextUtils.isEmpty(channelResponse?.data?.customLabel)) {
                        conversation.customLabel = channelResponse?.data?.customLabel
                        conversation.label = channelResponse?.data?.label
                    } else {
                        conversation.customLabel = ""
                        conversation.label = channelResponse?.data?.label
                    }
                    conversation.muid = messageJson.getString(MESSAGE_UNIQUE_ID)
                    conversation.message = messageJson.getString(MESSAGE)
                    conversation.message_type = messageJson.getInt(MESSAGE_TYPE)
                    conversation.messageState = 1
                    conversation.notifications = "UNMUTED"
                    conversation.chat_type = messageJson.getInt(CHAT_TYPE)
                    if (messageJson.has(LAST_SENT_BY_FULL_NAME)) {
                        conversation.last_sent_by_full_name =
                                messageJson.getString(LAST_SENT_BY_FULL_NAME)
                    }
                    if (messageJson.has(LAST_SENT_BY_ID)) {
                        conversation.last_sent_by_id = messageJson.getLong(LAST_SENT_BY_ID)
                    }
                    conversation.dateTime =
                            messageJson.getString(DATE_TIME).replace("+00:00", ".000Z")
                    conversation.channelId = messageJson.getLong(CHANNEL_ID)

                    if (messageJson.getInt(CHAT_TYPE) != 2) {
                        if (messageJson.has("channel_image") && !TextUtils.isEmpty(
                                        messageJson.getString(
                                                "channel_image"
                                        )
                                )
                        ) {
                            conversation.thumbnailUrl = messageJson.getString("channel_image")
                            conversation.thumbnailUrl = messageJson.getString("channel_image")

                        } else {
                            conversation.thumbnailUrl = channelResponse?.data?.channelThumbnailUrl
                        }
                    } else {
                        conversation.thumbnailUrl = channelResponse?.data?.channelThumbnailUrl
                    }

                    if (messageJson.getInt(CHAT_TYPE) != 2) {
                        conversation.label = messageJson.getString(AppConstants.LABEL)
                    } else {
                        conversation.label = channelResponse?.data?.label
                    }

                    try {
                        if (messageJson.getInt(MESSAGE_TYPE) == 5) {
                            conversation.unreadCount = 0
                        } else {
                            if (conversation.unreadCount == null) {
                                conversation.unreadCount = 0
                            } else {
                                conversation.unreadCount = channelResponse?.data?.unreadCount!!
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    if (messageJson.getLong(LAST_SENT_BY_ID).compareTo(userId) == 0) {
                        conversation.unreadCount = 0
                    }

                    Thread {
                        kotlin.run {
                            val conversationMap = ChatDatabase.getConversationMap(
                                    messageJson.getString(FuguAppConstant.APP_SECRET_KEY)
                            )
                            conversationMap[messageJson.getLong(CHANNEL_ID)] = conversation
                            ChatDatabase.setConversationMap(
                                    conversationMap,
                                    messageJson.getString(FuguAppConstant.APP_SECRET_KEY)
                            )
                            val mIntent = Intent(FuguAppConstant.CHANNEL_INTENT)
                            LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent)
                        }
                    }.start()
                }

                override fun onError(error: ApiError?) {
                }

                override fun onFailure(throwable: Throwable?) {
                }

            })
        }
    }

    private fun getChannelInfoRequestParams(messageJson: JSONObject): com.skeleton.mvp.retrofit.CommonParams? {
        try {
            if (CommonData.getCurrentSignedInPosition() >= workspacesInfoList.size) {
                CommonData.setCurrentSignedInPosition(0)
            }
            return com.skeleton.mvp.retrofit.CommonParams.Builder()
                    .add(
                            EN_USER_ID,
                            com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].enUserId
                    )
                    .add(CHANNEL_ID, messageJson.getLong(CHANNEL_ID))
                    .build()
        } catch (e: Exception) {
            return null
        }
    }

    private fun directFileShare(intent: Intent) {

        val chatIntent = Intent(this@MainActivityOld, ChatActivity::class.java)
        targetChannelId = intent.getLongExtra("targetChannelId", 0)
        val fuguConversation = FuguConversation()
        val gson = Gson()
        val token = object : TypeToken<ArrayList<MembersInfo>>() {
        }
        val targetMembersInfoList = gson.fromJson<ArrayList<MembersInfo>>(
                intent.getStringExtra("targetMembersInfo"),
                token.type
        )
        fuguConversation.label = intent.getStringExtra("targetLabel")
        fuguConversation.channelId = targetChannelId
        fuguConversation.channelImage = intent.getStringExtra("targetThumbnailUrl")
        fuguConversation.userId = intent.getLongExtra("targetMyUserId", 0)
        fuguConversation.chat_type = intent.getIntExtra("targetChatType", 0)
        fuguConversation.last_sent_by_id = intent.getLongExtra("targetLastSentById", 0)
        fuguConversation.unreadCount = intent.getIntExtra("targetUnreadCount", 0)
        fuguConversation.notification = intent.getStringExtra("targetNotifications")
        fuguConversation.membersInfo = targetMembersInfoList

        fuguConversation.otherUserType = intent.getIntExtra("targetOtherUserType", 0)
        chatIntent.putExtra(
                FuguAppConstant.CONVERSATION,
                Gson().toJson(fuguConversation, com.skeleton.mvp.model.FuguConversation::class.java)
        )
        chatIntent.putExtra("fromHome", true)
        android.app.AlertDialog.Builder(this@MainActivityOld)
                .setMessage("Share with " + intent.getStringExtra("targetLabel") + "?")
                .setPositiveButton("OK") { dialog, which -> startActivity(chatIntent) }
                .setNegativeButton("CANCEL") { dialog, which -> }.show()
    }

    override fun updateAllMemberAdapterCallback(userId: Long) {
        (pagerAdapter!!.getFragments()[2] as CreateGroupFragment).updateAllMemberAdapter(userId)
    }

    override fun recyclerViewAddedMembersCallback(getAllMembers: GetAllMembers) {
        (pagerAdapter!!.getFragments()[2] as CreateGroupFragment).setRecyclerViewAddedMembers(
                getAllMembers
        )
    }


}



