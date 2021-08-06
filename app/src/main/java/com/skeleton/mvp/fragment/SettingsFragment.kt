package com.skeleton.mvp.fragment

/********************************
Created by Amandeep Chauhan     *
Date :- 27/04/2020              *
 ********************************/

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.facebook.react.bridge.UiThreadUtil.runOnUiThread
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.FuguConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.*
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.EN_USER_ID
import com.skeleton.mvp.constant.FuguAppConstant.REQ_CODE_INVITE_GUEST
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.data.model.notifications.NotificationSettingsActivity
import com.skeleton.mvp.data.model.setPassword.CommonResponseFugu
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.data.network.CommonParams
import com.skeleton.mvp.data.network.ResponseResolver
import com.skeleton.mvp.data.network.RestClient
import com.skeleton.mvp.pushNotification.PushReceiver
import com.skeleton.mvp.socket.SocketConnection
import com.skeleton.mvp.ui.AppConstants.*
import com.skeleton.mvp.ui.HelpActivity
import com.skeleton.mvp.ui.UniqueIMEIID
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.ui.browsegroup.BrowseGroupActivity
import com.skeleton.mvp.ui.fcinvite.InviteOnboardActivity
import com.skeleton.mvp.ui.profile.ProfileActivity
import io.paperdb.Paper
import java.io.File

class SettingsFragment : Fragment(), View.OnClickListener {

    private lateinit var ivUserImage: ImageView
    private lateinit var tvUserName: TextView
    private lateinit var workspaceInfo: WorkspacesInfo
    private lateinit var userProfile: LinearLayout
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var moreOptionsList = ArrayList<TextView>()
    private var mContext: Context? = null
    private var collapsingToolbarLayout: CollapsingToolbarLayout? = null
    private var ivEdit: AppCompatImageView? = null
    private var account: TextView? = null
    private var toolbar: androidx.appcompat.widget.Toolbar? = null

    companion object {
        const val STORAGE_PERMISSION_REQUEST = 907
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        initViews(view)
        setProfileData()
        val llToolbar = view.findViewById<LinearLayout>(R.id.llToolbar)
        val mAppBarLayout = view.findViewById<View>(R.id.appbar) as AppBarLayout
        collapsingToolbarLayout?.title = " "
//        if ((mContext as MainActivity).isNightMode()) {
//            (mContext as MainActivity).setLightStatusBar((mContext as MainActivity))
//        }
        mAppBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = false
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true
                    llToolbar?.visibility = View.VISIBLE
                    collapsingToolbarLayout?.title = "Settings"
                } else if (isShow) {
                    isShow = false
                    llToolbar?.visibility = View.GONE
                    collapsingToolbarLayout?.title = " "
                }
            }
        })
        return view
    }


    private fun setProfileData() {
        Thread {
            kotlin.run {
                if (CommonData.getCommonResponse() != null && CommonData.getCommonResponse().data != null && CommonData.getCommonResponse().data.workspacesInfo.size == 0) {
                    FuguConfig.clearFuguData(mContext as MainActivity)
                    CommonData.clearData()
                    try {
                        val i = (mContext as MainActivity).packageManager
                                ?.getLaunchIntentForPackage((mContext as MainActivity).packageName)
                        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        i.putExtra("open_home", "open_home")
                        startActivity(i)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    workspaceInfo = CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()]
                    runOnUiThread {
                        tvUserName.text = workspaceInfo.fullName

                        val options = RequestOptions()
                                .centerCrop()
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.fugu_ic_channel_icon)
                                .error(R.drawable.fugu_ic_channel_icon)
                                .fitCenter()
                                .priority(Priority.HIGH)

                        try {
                            Glide.with(mContext as MainActivity)
                                    .asBitmap()
                                    .apply(options)
                                    .load(workspaceInfo.userImage)
                                    .listener(object : RequestListener<Bitmap> {
                                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                                            return false
                                        }

                                        override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                            if (resource != null) {
                                                val p = androidx.palette.graphics.Palette.from(resource).generate()
                                                val mutedColor = p.getDominantColor(ContextCompat.getColor(mContext as MainActivity, R.color.colorPrimary))
//                                                setUpToolBar(mutedColor)
                                            }
                                            return false
                                        }

                                    })
                                    .into(ivUserImage)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        (mContext as MainActivity).runOnUiThread {
                            if ((workspaceInfo.role == "OWNER" || workspaceInfo.role == "ADMIN" || workspaceInfo.config.anyUserCanInvite == "1") && workspaceInfo.role != "GUEST") {
                                moreOptionsList[2].visibility = View.VISIBLE
                            } else {
                                moreOptionsList[2].visibility = View.GONE
                            }
                            if (workspaceInfo.config.enablePublicInvite == "1" && workspaceInfo.role != "OWNER") {
                                moreOptionsList[4].visibility = View.VISIBLE
                            } else {
                                moreOptionsList[4].visibility = View.GONE
                            }
                            if (workspaceInfo.role == "GUEST") {
                                moreOptionsList[0].visibility = View.GONE
                            } else {
                                moreOptionsList[0].visibility = View.VISIBLE
                            }
                            if (workspaceInfo.role == "OWNER") {
                                moreOptionsList[5].visibility = View.VISIBLE
                            } else {
                                moreOptionsList[5].visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }.start()
    }

    private fun setUpToolBar(mutedColor: Int) {
        collapsingToolbarLayout?.setContentScrimColor(mutedColor)
        (mContext as MainActivity).window.statusBarColor = manipulateColor(mutedColor, 0.7f)
    }

    private fun manipulateColor(color: Int, factor: Float): Int {
        val a = Color.alpha(color)
        val r = Math.round(Color.red(color) * factor)
        val g = Math.round(Color.green(color) * factor)
        val b = Math.round(Color.blue(color) * factor)
        return Color.argb(a, Math.min(r, 255), Math.min(g, 255), Math.min(b, 255))
    }

    private fun initViews(view: View) {
        toolbar = view.findViewById(R.id.toolbar)
        ivUserImage = view.findViewById(R.id.ivUserImage)
        tvUserName = view.findViewById(R.id.tvUserName)
        ivEdit = view.findViewById(R.id.ivEdit)
        moreOptionsList.add(view.findViewById(R.id.browseGroups))
        moreOptionsList.add(view.findViewById(R.id.notificationSettings))
        moreOptionsList.add(view.findViewById(R.id.inviteMembers))
        moreOptionsList.add(view.findViewById(R.id.help))
        moreOptionsList.add(view.findViewById(R.id.exitSpace))
        moreOptionsList.add(view.findViewById(R.id.disableSpace))
        moreOptionsList.add(view.findViewById(R.id.logout))
        moreOptionsList.add(view.findViewById(R.id.starredMessages))
        moreOptionsList.add(view.findViewById(R.id.mediaSetting))
        userProfile = view.findViewById(R.id.userProfile)
        account = view.findViewById(R.id.account)
        collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar) as CollapsingToolbarLayout
        account?.setOnClickListener {
            if ((mContext as MainActivity).isNetworkConnected) {
                val infoIntent = Intent((mContext as MainActivity), ProfileActivity::class.java)
                infoIntent.putExtra("open_profile", CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].userId)
                infoIntent.putExtra("no_chat", "no_chat")
                startActivity(infoIntent)
                (mContext as MainActivity).overridePendingTransition(R.anim.right_in, R.anim.left_out)
            } else {
                (mContext as MainActivity).showErrorMessage("Please check your Internet Connection and Try Again !")
            }
        }

        ivEdit?.setOnClickListener {
            if ((mContext as MainActivity).isNetworkConnected) {
                val infoIntent = Intent((mContext as MainActivity), ProfileActivity::class.java)
                infoIntent.putExtra(
                        "open_profile",
                        CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].userId
                )
                infoIntent.putExtra("no_chat", "no_chat")
                infoIntent.putExtra("isEditable", "isEditable")
                startActivity(infoIntent)
                (mContext as MainActivity).overridePendingTransition(R.anim.right_in, R.anim.left_out)
            } else {
                (mContext as MainActivity).showErrorMessage("Please check your Internet Connection and Try Again !")
            }
        }

        for (view in moreOptionsList) {
            view.setOnClickListener(this)
        }
    }

    override fun onClick(v: View?) {
        if ((mContext as MainActivity).isNetworkConnected) {
            when (v!!.id) {
                R.id.starredMessages -> {
                    startActivity(Intent((mContext as MainActivity), StarredMessagesActivity::class.java))
                    (mContext as MainActivity).overridePendingTransition(R.anim.right_in, R.anim.left_out)
                }
                R.id.browseGroups -> {
                    startActivity(Intent((mContext as MainActivity), BrowseGroupActivity::class.java))
                    (mContext as MainActivity).overridePendingTransition(R.anim.right_in, R.anim.left_out)
                }
                R.id.notificationSettings -> {
                    startActivity(Intent((mContext as MainActivity), NotificationSettingsActivity::class.java))
                    (mContext as MainActivity).overridePendingTransition(R.anim.right_in, R.anim.left_out)
                }
                R.id.mediaSetting -> {
                    startActivity(Intent((mContext as MainActivity), MediaSettingActivity::class.java))
                    (mContext as MainActivity).overridePendingTransition(R.anim.right_in, R.anim.left_out)
                }
                R.id.inviteMembers -> {
                    val intent = if (workspaceInfo.config.isGuestAllowed == "1") {
                        Intent((mContext as MainActivity), InviteChooserActivity::class.java)
                    } else {
                        Intent((mContext as MainActivity), InviteOnboardActivity::class.java)
                    }
                    intent.putExtra(EXTRA_ALREADY_MEMBER, EXTRA_ALREADY_MEMBER)
                    startActivityForResult(intent, REQ_CODE_INVITE_GUEST)
                    (mContext as MainActivity).overridePendingTransition(R.anim.right_in, R.anim.left_out)
                }
                R.id.help -> {
                    startActivity(Intent((mContext as MainActivity), HelpActivity::class.java))
                    (mContext as MainActivity).overridePendingTransition(R.anim.right_in, R.anim.left_out)
                }
                R.id.exitSpace -> {
                    (mContext as MainActivity).showErrorMessage(
                            "Are you sure you want exit space ?",
                            {
                                if ((mContext as MainActivity).isNetworkConnected) {
                                    (mContext as MainActivity).showLoading()
                                    apiExitSpace()
                                    Paper.init(mContext!!)
                                    try {
                                        com.skeleton.mvp.fugudatabase.CommonData.clearData()
                                        object : Thread() {
                                            override fun run() {
                                                super.run()
                                                CommonData.clearData()
                                            }
                                        }.start()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }

                                    Paper.init(mContext)
                                } else {
                                    (mContext as MainActivity).showErrorMessage(R.string.error_internet_not_connected)
                                }
                            },
                            { },
                            "Yes",
                            "No"
                    )
                }
                R.id.disableSpace -> {
                    (mContext as MainActivity).showErrorMessage(
                            "Are you sure you want deactivate space ?",
                            {
                                if ((mContext as MainActivity).isNetworkConnected) {
                                    (mContext as MainActivity).showLoading()
                                    apiDisableSpace()
                                } else {
                                    (mContext as MainActivity).showErrorMessage(R.string.error_internet_not_connected)
                                }
                            },
                            { },
                            "Yes",
                            "No"
                    )
                }
                R.id.logout -> {
                    (mContext as MainActivity).showErrorMessage(
                            "Are you sure you want to logout ?",
                            {
                                if ((mContext as MainActivity).isNetworkConnected) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        val permissionsNeeded: ArrayList<String> = ArrayList()
                                        val requiredPermissions = arrayOf(
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                Manifest.permission.READ_EXTERNAL_STORAGE)
                                        for (permission in requiredPermissions) {
                                            if ((mContext as Activity).checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                                                permissionsNeeded.add(permission)
                                        }
                                        if (permissionsNeeded.isNotEmpty()) {
                                            Toast.makeText((mContext as BaseActivity), "Storage permission is required to remove temporary files.", Toast.LENGTH_SHORT).show()
                                            //                        Toast.makeText((mContext as BaseActivity), "${permissionsNeeded[0].replace("android.permission.", "")} permission not granted.", Toast.LENGTH_SHORT).show()
                                            ActivityCompat.requestPermissions((mContext as BaseActivity), permissionsNeeded.toTypedArray() as Array<out String>, STORAGE_PERMISSION_REQUEST)
                                        } else {
                                            (mContext as MainActivity).showLoading()
                                            val userImages = File(Environment.getExternalStorageDirectory(), FuguAppConstant.USER_IMAGES)
                                            if (userImages.isDirectory) {
                                                val listFile = userImages.listFiles()
                                                for (i in listFile!!.indices) {
                                                    listFile.get(i).delete()
                                                }
                                            }
                                            apiLogout()
                                            Paper.init(mContext)
                                        }
                                    } else {
                                        (mContext as MainActivity).showLoading()
                                        val userImages = File(Environment.getExternalStorageDirectory(), FuguAppConstant.USER_IMAGES)
                                        if (userImages.isDirectory) {
                                            val listFile = userImages.listFiles()
                                            for (i in listFile!!.indices) {
                                                listFile.get(i).delete()
                                            }
                                        }
                                        apiLogout()
                                        Paper.init(mContext)
                                    }
                                } else {
                                    (mContext as MainActivity).showErrorMessage(R.string.error_internet_not_connected)
                                }
                            },
                            { },
                            "Yes",
                            "No"
                    )
                }
            }
        } else {
            (mContext as MainActivity).showErrorMessage("Please check your Internet Connection and Try Again !")
        }
    }

    private fun apiExitSpace() {
        val commonParams = CommonParams.Builder()
                .add(WORKSPACE_ID, CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].workspaceId)
                .build()
        RestClient.getApiInterface(true).exitSpace(
                CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                BuildConfig.VERSION_CODE,
                FuguAppConstant.ANDROID,
                commonParams.map
        ).enqueue(object : ResponseResolver<CommonResponseFugu>() {
            override fun onSuccess(commonResponseFugu: CommonResponseFugu) {
                (mContext as MainActivity).hideLoading()
                val fcCommonResponse = CommonData.getCommonResponse()
                fcCommonResponse.getData()
                        .workspacesInfo.removeAt(CommonData.getCurrentSignedInPosition())
                if (fcCommonResponse.getData().workspacesInfo.size > 0) {
                    CommonData.setCommonResponse(fcCommonResponse)
//                    (mContext as MainActivity).changeBusiness(0, false, false)
                } else {
                    try {
                        val i = (mContext as MainActivity).packageManager
                                ?.getLaunchIntentForPackage((mContext as MainActivity).packageName)
                        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        i.putExtra("open_home", "open_home")
                        startActivity(i)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onError(error: ApiError) {
                (mContext as MainActivity).hideLoading()
                if (error.statusCode == FuguAppConstant.SESSION_EXPIRE) {
                    CommonData.clearData()
                    FuguConfig.clearFuguData(mContext as MainActivity)
//                    activity!!.finishAffinity()
//                    startActivity(Intent(activity, IntroActivity::class.java))
                    try {
                        val i = (mContext as MainActivity).packageManager
                                ?.getLaunchIntentForPackage((mContext as MainActivity).packageName)
                        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        i.putExtra("open_home", "open_home")
                        startActivity(i)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    (mContext as MainActivity).showErrorMessage(error.message)
                }

            }

            override fun onFailure(throwable: Throwable) {

            }
        })

    }


    private fun apiDisableSpace() {
        val workspacesInfo = CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()]
        val commonParams = CommonParams.Builder()
                .add(WORKSPACE_ID, workspacesInfo.workspaceId)
                .add(EN_USER_ID, workspacesInfo.enUserId)
                .add("status", "DISABLED")
                .build()
        RestClient.getApiInterface(true).editWorkspaceInfo(
                CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                BuildConfig.VERSION_CODE,
                FuguAppConstant.ANDROID,
                commonParams.map
        ).enqueue(object : ResponseResolver<CommonResponseFugu>() {
            override fun onSuccess(commonResponseFugu: CommonResponseFugu) {
                (mContext as MainActivity).hideLoading()
                val fcCommonResponse = CommonData.getCommonResponse()
                if (fcCommonResponse.getData().workspacesInfo.size > 1) {
                    fcCommonResponse.getData().workspacesInfo.removeAt(CommonData.getCurrentSignedInPosition())
                    CommonData.setCommonResponse(fcCommonResponse)
                    CommonData.setCurrentSignedInPosition(0)
                    try {
                        val i = Intent(mContext, MainActivity::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        PushReceiver.PushChannel.isEmailVerificationScreen = false
                        (mContext as MainActivity).finish()
                        (mContext as MainActivity).overridePendingTransition(0, 0)
                        (mContext as MainActivity).startActivity(i)
                        (mContext as MainActivity).overridePendingTransition(0, 0)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        (mContext as MainActivity).finish()
                    }
                } else {
                    finishAffinity(mContext as BaseActivity)
                    startActivity(Intent(mContext, CreateWorkspaceActivity::class.java))
                }
            }

            override fun onError(error: ApiError) {
                (mContext as MainActivity).hideLoading()
                if (error.statusCode == FuguAppConstant.SESSION_EXPIRE) {
                    CommonData.clearData()
                    FuguConfig.clearFuguData(mContext as MainActivity)
                    try {
                        val i = Intent(mContext, MainActivity::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        PushReceiver.PushChannel.isEmailVerificationScreen = false
                        (mContext as MainActivity).finish()
                        (mContext as MainActivity).overridePendingTransition(0, 0)
                        (mContext as MainActivity).startActivity(i)
                        (mContext as MainActivity).overridePendingTransition(0, 0)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    (mContext as MainActivity).showErrorMessage(error.message)
                }

            }

            override fun onFailure(throwable: Throwable) {
                (mContext as MainActivity).hideLoading()
            }
        })

    }

    private fun apiLogout() {
        val commonParams = CommonParams.Builder()
                .add(TOKEN, CommonData.getFcmToken())
                .add(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(mContext as MainActivity))
                .add(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails(mContext as MainActivity))
                .build()
        RestClient.getApiInterface(true).logoutUser(
                CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                BuildConfig.VERSION_CODE,
                FuguAppConstant.ANDROID,
                commonParams.map
        ).enqueue(object : ResponseResolver<CommonResponseFugu>() {
            override fun onSuccess(commonResponseFugu: CommonResponseFugu) {

                FuguConfig.clearFuguData(mContext as MainActivity)
                CommonData.clearData()
//                        (activity as MoreFragment).hideLoading()
//                        (activity as MoreFragment).finishAffinity()
//                        startActivity(Intent(activity, IntroActivity::class.java))
                SocketConnection.disconnectSocket()
                try {
                    val i = Intent(mContext, MainActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    PushReceiver.PushChannel.isEmailVerificationScreen = false
                    (mContext as MainActivity).finish()
                    (mContext as MainActivity).overridePendingTransition(0, 0)
                    (mContext as MainActivity).startActivity(i)
                    (mContext as MainActivity).overridePendingTransition(0, 0)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onError(error: ApiError) {
                (mContext as MainActivity).hideLoading()
                if (error.statusCode == FuguAppConstant.SESSION_EXPIRE) {
                    CommonData.clearData()
                    SocketConnection.disconnectSocket()
                    FuguConfig.clearFuguData(mContext as MainActivity)
//                            (activity as MoreFragment).finishAffinity()
//                            startActivity(Intent(activity, IntroActivity::class.java))
                    try {
                        val i = Intent(mContext, MainActivity::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        PushReceiver.PushChannel.isEmailVerificationScreen = false
                        (mContext as MainActivity).finish()
                        (mContext as MainActivity).overridePendingTransition(0, 0)
                        (mContext as MainActivity).startActivity(i)
                        (mContext as MainActivity).overridePendingTransition(0, 0)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    (mContext as MainActivity).showErrorMessage(error.message)
                }
            }

            override fun onFailure(throwable: Throwable) {

            }
        })
    }

    override fun onResume() {
        super.onResume()
        setProfileData()
    }
}
