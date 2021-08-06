package com.skeleton.mvp.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.FuguConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.InviteChooserActivity
import com.skeleton.mvp.activity.MediaSettingActivity
import com.skeleton.mvp.activity.StarredMessagesActivity
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.REQ_CODE_INVITE_GUEST
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.data.model.notifications.NotificationSettingsActivity
import com.skeleton.mvp.data.model.setPassword.CommonResponseFugu
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.data.network.CommonParams
import com.skeleton.mvp.data.network.ResponseResolver
import com.skeleton.mvp.data.network.RestClient
import com.skeleton.mvp.ui.AppConstants.*
import com.skeleton.mvp.ui.HelpActivity
import com.skeleton.mvp.ui.UniqueIMEIID
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.ui.base.BaseView
import com.skeleton.mvp.ui.browsegroup.BrowseGroupActivity
import com.skeleton.mvp.ui.fcinvite.InviteOnboardActivity
import com.skeleton.mvp.ui.profile.ProfileActivity
import io.paperdb.Paper
import java.io.File

@Suppress("NAME_SHADOWING")
/**
 * Created
 * rajatdhamija on 26/07/18.
 */

class MoreFragment : BaseActivity(), View.OnClickListener {


    private lateinit var ivUserImage: ImageView
    private lateinit var tvUserName: TextView
    private lateinit var workspacesInfo: WorkspacesInfo
    private lateinit var userProfile: LinearLayout
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var moreoptionsList = ArrayList<TextView>()
    private var mContext: Context? = null
    private var collapsingToolbarLayout: CollapsingToolbarLayout? = null
    private var ivEdit: AppCompatImageView? = null

    private var account: TextView? = null
    private var toolbar: androidx.appcompat.widget.Toolbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        setContentView(R.layout.fragment_more)
        initViews()
        setProfileData()
        setSupportActionBar(toolbar!!)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val llToolbar = findViewById<LinearLayout>(R.id.llToolbar)
        val mAppBarLayout = findViewById<View>(R.id.appbar) as AppBarLayout
        collapsingToolbarLayout?.title = " "
        if (isNightMode()) {
            setLightStatusBar(this@MoreFragment)
        }
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
                    collapsingToolbarLayout?.title = "More Options"
                } else if (isShow) {
                    isShow = false
                    llToolbar?.visibility = View.GONE
                    collapsingToolbarLayout?.title = " "
                }
            }
        })
    }


    fun setProfileData() {
        Thread {
            kotlin.run {
                if (CommonData.getCommonResponse() != null &&
                        CommonData.getCommonResponse().data != null &&
                        CommonData.getCommonResponse().data.workspacesInfo.size == 0
                ) {
                    FuguConfig.clearFuguData(this@MoreFragment)
                    CommonData.clearData()
                    try {
                        val i = baseContext?.packageManager
                                ?.getLaunchIntentForPackage(packageName)
                        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        i.putExtra("open_home", "open_home")
                        startActivity(i)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    workspacesInfo =
                            CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()]
                    runOnUiThread {
                        tvUserName.text = workspacesInfo.fullName

                        val options = RequestOptions()
                                .centerCrop()
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.fugu_ic_channel_icon)
                                .error(R.drawable.fugu_ic_channel_icon)
                                .fitCenter()
                                .priority(Priority.HIGH)

                        try {
                            Glide.with(mContext as MoreFragment)
                                    .asBitmap()
                                    .apply(options)
                                    .load(workspacesInfo.userImage)
                                    .listener(object : RequestListener<Bitmap> {
                                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                                            return false
                                        }

                                        override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                            if (resource != null) {
                                                val p = androidx.palette.graphics.Palette.from(resource).generate()
                                                val mutedColor = p.getDominantColor(ContextCompat.getColor(this@MoreFragment, R.color.colorPrimary))
                                                setUpToolBar(mutedColor)
                                            }
                                            return false
                                        }

                                    })
                                    .into(ivUserImage)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        (mContext as MoreFragment).runOnUiThread {
                            if ((workspacesInfo.role == "OWNER" ||
                                            workspacesInfo.role == "ADMIN" ||
                                            workspacesInfo.config.anyUserCanInvite == "1") && workspacesInfo.role != "GUEST"
                            ) {
                                moreoptionsList[2].visibility = View.VISIBLE

                            } else {
                                moreoptionsList[2].visibility = View.GONE

                            }

                            if (workspacesInfo.config.enablePublicInvite == "1"
                                    && workspacesInfo.role != "OWNER"
                            ) {
                                moreoptionsList[4].visibility = View.VISIBLE

                            } else {
                                moreoptionsList[4].visibility = View.GONE

                            }

                            if (workspacesInfo.role == "GUEST") {
                                moreoptionsList[0].visibility = View.GONE

                            } else {
                                moreoptionsList[0].visibility = View.VISIBLE


                            }

                        }
                    }
                }

            }
        }.start()
    }

    private fun setUpToolBar(mutedColor: Int) {
        collapsingToolbarLayout?.setContentScrimColor(mutedColor)
        window.statusBarColor = manipulateColor(mutedColor, 0.7f)
    }

    fun manipulateColor(color: Int, factor: Float): Int {
        val a = Color.alpha(color)
        val r = Math.round(Color.red(color) * factor)
        val g = Math.round(Color.green(color) * factor)
        val b = Math.round(Color.blue(color) * factor)
        return Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255))
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        ivUserImage = findViewById(R.id.ivUserImage)
        tvUserName = findViewById(R.id.tvUserName)
        ivEdit = findViewById(R.id.ivEdit)
        moreoptionsList.add(findViewById(R.id.browseGroups))
        moreoptionsList.add(findViewById(R.id.notificationSettings))
        moreoptionsList.add(findViewById(R.id.inviteMembers))
        moreoptionsList.add(findViewById(R.id.help))
        moreoptionsList.add(findViewById(R.id.exitSpace))
        moreoptionsList.add(findViewById(R.id.logout))
        moreoptionsList.add(findViewById(R.id.starredMessages))
        moreoptionsList.add(findViewById(R.id.mediaSetting))
        userProfile = findViewById(R.id.userProfile)
        account = findViewById(R.id.account)
        collapsingToolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)


        account?.setOnClickListener {
            if ((mContext as MoreFragment).isNetworkConnected) {
                val infoIntent = Intent((mContext as MoreFragment), ProfileActivity::class.java)
                infoIntent.putExtra(
                        "open_profile",
                        CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].userId
                )
                infoIntent.putExtra("no_chat", "no_chat")
                startActivity(infoIntent)
                overridePendingTransition(R.anim.right_in, R.anim.left_out)
            } else {
                (mContext as MoreFragment).showErrorMessage("Please check your Internet Connection and Try Again !")
            }
        }


        ivEdit?.setOnClickListener {
            if ((mContext as MoreFragment).isNetworkConnected) {
                val infoIntent = Intent((mContext as MoreFragment), ProfileActivity::class.java)
                infoIntent.putExtra(
                        "open_profile",
                        CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].userId
                )
                infoIntent.putExtra("no_chat", "no_chat")
                infoIntent.putExtra("isEditable", "isEditable")
                startActivity(infoIntent)
                overridePendingTransition(R.anim.right_in, R.anim.left_out)
            } else {
                (mContext as MoreFragment).showErrorMessage("Please check your Internet Connection and Try Again !")
            }
        }

        for (view in moreoptionsList) {
            view.setOnClickListener(this)
        }
    }

    override fun onClick(v: View?) {
        if ((mContext as MoreFragment).isNetworkConnected) {
            when (v!!.id) {
                R.id.starredMessages -> {
                    startActivity(
                            Intent(
                                    (mContext as MoreFragment),
                                    StarredMessagesActivity::class.java
                            )
                    )
                    overridePendingTransition(
                            R.anim.right_in,
                            R.anim.left_out
                    )
                }

                R.id.browseGroups -> {
                    startActivity(
                            Intent(
                                    (mContext as MoreFragment),
                                    BrowseGroupActivity::class.java
                            )
                    )
                    overridePendingTransition(
                            R.anim.right_in,
                            R.anim.left_out
                    )
                }
                R.id.notificationSettings -> {
                    startActivity(
                            Intent(
                                    (mContext as MoreFragment),
                                    NotificationSettingsActivity::class.java
                            )
                    )
                    overridePendingTransition(
                            R.anim.right_in,
                            R.anim.left_out
                    )
                }
                R.id.mediaSetting -> {
                    startActivity(
                            Intent(
                                    (mContext as MoreFragment),
                                    MediaSettingActivity::class.java
                            )
                    )
                    (mContext as MoreFragment).overridePendingTransition(
                            R.anim.right_in,
                            R.anim.left_out
                    )
                }

                R.id.inviteMembers -> {
                    val intent: Intent
                    if (workspacesInfo.config.isGuestAllowed.equals("1")) {
                        intent =
                                Intent((mContext as MoreFragment), InviteChooserActivity::class.java)
                    } else {
                        intent =
                                Intent((mContext as MoreFragment), InviteOnboardActivity::class.java)
                    }
                    intent.putExtra(EXTRA_ALREADY_MEMBER, EXTRA_ALREADY_MEMBER)
                    startActivityForResult(intent, REQ_CODE_INVITE_GUEST)
                    (mContext as MoreFragment).overridePendingTransition(
                            R.anim.right_in,
                            R.anim.left_out
                    )
                }
                R.id.help -> {
                    startActivity(Intent((mContext as MoreFragment), HelpActivity::class.java))
                    (mContext as MoreFragment).overridePendingTransition(
                            R.anim.right_in,
                            R.anim.left_out
                    )
                }
                R.id.exitSpace -> {
                    (mContext as MoreFragment).showErrorMessage(
                            "Are you sure you want exit space ?",
                            object : BaseView.OnErrorHandleCallback {
                                override fun onErrorCallback() {
                                    if ((mContext as MoreFragment).isNetworkConnected) {
                                        (mContext as MoreFragment).showLoading()
                                        apiExistSpace()
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
                                        (mContext as MoreFragment).showErrorMessage(R.string.error_internet_not_connected)
                                    }
                                }
                            },
                            object : BaseView.OnPositiveButtonCallback {
                                override fun onPositiveButtonClick() {

                                }
                            },
                            "Yes",
                            "No"
                    )
                }
                R.id.logout -> {
                    (mContext as MoreFragment).showErrorMessage(
                            "Are you sure you want to logout ?",
                            object : BaseView.OnErrorHandleCallback {
                                override fun onErrorCallback() {
                                    if ((mContext as MoreFragment).isNetworkConnected) {
                                        (mContext as MoreFragment).showLoading()

                                        val userImages = File(
                                                Environment.getExternalStorageDirectory(), FuguAppConstant.USER_IMAGES
                                        )

                                        if (userImages.isDirectory) {
                                            val listFile = userImages.listFiles()
                                            for (i in listFile!!.indices) {
                                                listFile.get(i).delete()
                                            }
                                        }

                                        apiLogout()
                                        Paper.init(mContext)


                                    } else {
                                        (mContext as MoreFragment).showErrorMessage(R.string.error_internet_not_connected)
                                    }
                                }
                            },
                            object : BaseView.OnPositiveButtonCallback {
                                override fun onPositiveButtonClick() {

                                }
                            },
                            "Yes",
                            "No"
                    )
                }
            }
        } else {
            (mContext as MoreFragment).showErrorMessage("Please check your Internet Connection and Try Again !")
        }
    }

    fun apiExistSpace() {
        val commonParams = CommonParams.Builder()
                .add(
                        WORKSPACE_ID,
                        CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].workspaceId
                )
                .build()
        RestClient.getApiInterface(true).exitSpace(
                CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                BuildConfig.VERSION_CODE,
                FuguAppConstant.ANDROID,
                commonParams.map
        ).enqueue(object : ResponseResolver<CommonResponseFugu>() {
            override fun onSuccess(commonResponseFugu: CommonResponseFugu) {
                (mContext as MoreFragment).hideLoading()
                val fcCommonResponse = CommonData.getCommonResponse()
                fcCommonResponse.getData()
                        .workspacesInfo.removeAt(CommonData.getCurrentSignedInPosition())
                if (fcCommonResponse.getData().workspacesInfo.size > 0) {
                    CommonData.setCommonResponse(fcCommonResponse)
//                    (mContext as MoreFragment).changeBusiness(0, false, false)
                } else {
                    try {
                        val i = baseContext?.packageManager
                                ?.getLaunchIntentForPackage(packageName)
                        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        i.putExtra("open_home", "open_home")
                        startActivity(i)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onError(error: ApiError) {
                (mContext as MoreFragment).hideLoading()
                if (error.statusCode == FuguAppConstant.SESSION_EXPIRE) {
                    CommonData.clearData()
                    FuguConfig.clearFuguData(this@MoreFragment)
//                    activity!!.finishAffinity()
//                    startActivity(Intent(activity, IntroActivity::class.java))
                    try {
                        val i = baseContext?.packageManager
                                ?.getLaunchIntentForPackage(packageName)
                        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        i.putExtra("open_home", "open_home")
                        startActivity(i)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    (mContext as MoreFragment).showErrorMessage(error.message)
                }

            }

            override fun onFailure(throwable: Throwable) {

            }
        })

    }

    private fun apiLogout() {
        val commonParams = CommonParams.Builder()
                .add(TOKEN, CommonData.getFcmToken())
                .add(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(this@MoreFragment))
                .add(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails(this@MoreFragment))
                .build()
        RestClient.getApiInterface(true).logoutUser(
                CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                BuildConfig.VERSION_CODE,
                FuguAppConstant.ANDROID,
                commonParams.map
        )
                .enqueue(object : ResponseResolver<CommonResponseFugu>() {
                    override fun onSuccess(commonResponseFugu: CommonResponseFugu) {

                        FuguConfig.clearFuguData(this@MoreFragment)
                        CommonData.clearData()
//                        (activity as MoreFragment).hideLoading()
//                        (activity as MoreFragment).finishAffinity()
//                        startActivity(Intent(activity, IntroActivity::class.java))
                        try {
                            val i = baseContext?.packageManager
                                    ?.getLaunchIntentForPackage(packageName)
                            i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            i.putExtra("open_home", "open_home")
                            startActivity(i)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }

                    override fun onError(error: ApiError) {
                        (mContext as MoreFragment).hideLoading()
                        if (error.statusCode == FuguAppConstant.SESSION_EXPIRE) {
                            CommonData.clearData()
                            FuguConfig.clearFuguData(this@MoreFragment)
//                            (activity as MoreFragment).finishAffinity()
//                            startActivity(Intent(activity, IntroActivity::class.java))
                            try {
                                val i = baseContext?.packageManager
                                        ?.getLaunchIntentForPackage(packageName)
                                i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                i.putExtra("open_home", "open_home")
                                startActivity(i)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            (mContext as MoreFragment).showErrorMessage(error.message)
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
