package com.skeleton.mvp.ui.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.FuguConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.CreateWorkspaceActivity
import com.skeleton.mvp.activity.MainActivity
import com.skeleton.mvp.activity.WebViewActivity
import com.skeleton.mvp.calendar.AuthorizeGoogleActivity
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.db.ChatDatabase.getNotificationsMap
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.setPassword.CommonResponseFugu
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.data.network.CommonParams
import com.skeleton.mvp.data.network.ResponseResolver
import com.skeleton.mvp.data.network.RestClient
import com.skeleton.mvp.pushNotification.PushReceiver
import com.skeleton.mvp.pushNotification.PushReceiver.PushChannel.SUMMARY_NOTIFICATION_ID
import com.skeleton.mvp.retrofit.APIError
import com.skeleton.mvp.retrofit.CommonResponse
import com.skeleton.mvp.ui.AppConstants.TOKEN
import com.skeleton.mvp.ui.dialog.ProgressDialog
import com.skeleton.mvp.ui.intro.IntroActivity
import com.skeleton.mvp.ui.yourspaces.YourSpacesActivity
import com.skeleton.mvp.util.AppConstant
import com.skeleton.mvp.util.CommonUtil
import com.skeleton.mvp.util.Log
import com.skeleton.mvp.utils.FuguLog
import com.skeleton.mvp.utils.UniqueIMEIID
import com.skeleton.mvp.utils.addDomain
import com.skeleton.mvp.utils.showToast
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import io.paperdb.Paper
import java.io.PrintWriter
import java.io.StringWriter
import java.net.URLDecoder

/**
 * Developer: Click Labs
 *
 *
 * Base Activity
 */

abstract class BaseActivity : AppCompatActivity(), BaseView {
    private var mProgressDialog: android.app.ProgressDialog? = null
    private var isNightMode = false
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mNotificationDialog: AlertDialog? = null

    companion object {
        const val REQUEST_CALENDAR_AUTHORIZATION = 9001
        private const val OVERLAY_TEXT_SIZE_INT = 15
        private const val TEN = 10
        private const val OVERLAY_TEXT = BuildConfig.APP_NAME + "_" + BuildConfig.FLAVOR + "_v" + BuildConfig.VERSION_CODE
        private var dialog: Dialog? = null
        const val READ_WRITE_STORAGE = 52
    }

    private val mDeactivateUserReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null && intent.hasExtra(FuguAppConstant.APP_SECRET_KEY)) {
                if (CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey == intent.getStringExtra(FuguAppConstant.APP_SECRET_KEY)) {
                    showNotificationDialog(intent.getStringExtra(FuguAppConstant.APP_SECRET_KEY)!!)
                } else {
                    val fcCommonResponse = CommonData.getCommonResponse()
                    if (fcCommonResponse.getData().workspacesInfo.size > 0) {
                        for (i in 0 until fcCommonResponse.getData().workspacesInfo.size) {
                            if (fcCommonResponse.getData().workspacesInfo[i].fuguSecretKey == intent.getStringExtra(FuguAppConstant.APP_SECRET_KEY)) {
                                fcCommonResponse.getData().workspacesInfo.removeAt(i)
                                break
                            }
                        }
                        CommonData.setCommonResponse(fcCommonResponse)
                        deactivatedUser()
                    } else {
                        finishAffinity()
                        startActivity(Intent(this@BaseActivity, CreateWorkspaceActivity::class.java))
                    }
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            finishAffinity()
        }
        val crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        try {
            if (CommonData.getCommonResponse() != null && CommonData.getCommonResponse().getData() != null) {
                crashlytics.setUserId(CommonData.getCommonResponse().getData().userInfo.userId)
//                crashlytics.setUserName(CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].fullName)
                crashlytics.setCustomKey("UUID", CommonData.getCommonResponse().getData().userInfo.userId)
                val workspaceInfo = CommonData.getCommonResponse().getData().workspacesInfo
                if (workspaceInfo != null && workspaceInfo.isNotEmpty()) {
                    crashlytics.setCustomKey("Name", workspaceInfo[CommonData.getCurrentSignedInPosition()].fullName)
                    crashlytics.setCustomKey("User ID", workspaceInfo[CommonData.getCurrentSignedInPosition()].userId)
                }
                if (BuildConfig.DEBUG) {
                    crashlytics.setCustomKey("isDebugBuild", true)
                } else {
                    crashlytics.setCustomKey("isDebugBuild", false)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        negotiateDeviceDarkMode()

    }

    private fun negotiateDeviceDarkMode() {
        isNightMode = this@BaseActivity.resources.configuration.uiMode
                .and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        val typedValue = TypedValue()

        this@BaseActivity.window.addFlags(
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
        )

        if (isNightMode) {
            clearLightStatusBar(this@BaseActivity)
        } else {
//            setLightStatusBar(this@BaseActivity)
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            this.window.statusBarColor = Color.BLACK
        }
    }

    fun isNightMode(): Boolean {
        return isNightMode
    }

    fun setLightStatusBar(activity: Activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = activity.window.decorView.systemUiVisibility // get current flag
            flags = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // use XOR here for remove LIGHT_STATUS_BAR from flags
            activity.window.decorView.systemUiVisibility = flags
            activity.window.statusBarColor = Color.WHITE // optional
        }
    }
    //
    //    public static void clearLightStatusBar(Activity activity) {
    //        if (Build.VERSION.SDK_INT >= Build.VERSION_C ODES.M) {
    //            Window window = activity.getWindow();
    //            window.setStatusBarColor(ContextCompat
    //                    .getColor(activity,R.color.colorPrimaryDark));
    //        }
    //    }

    fun clearLightStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = activity.window.decorView.systemUiVisibility // get current flag
            flags = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // use XOR here for remove LIGHT_STATUS_BAR from flags
            activity.window.decorView.systemUiVisibility = flags
            activity.window.statusBarColor = Color.BLACK // optional
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    fun requestPermission(permission: String): Boolean {
        val isGranted = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        if (!isGranted) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    READ_WRITE_STORAGE)
        }
        return isGranted
    }

    open fun isPermissionGranted(isGranted: Boolean, permission: String) {

    }

    /**
     * Uncaught Exception encountered
     */
    private fun uncaughtExceptionError() {
        Thread.setDefaultUncaughtExceptionHandler { paramThread, paramThrowable ->
            //Do your own error handling here
            FuguLog.e("unCaughtException paramThread", "---> $paramThread")
            FuguLog.e("unCaughtException paramThrowable", "---> $paramThrowable")
            val stackTrace = StringWriter()
            paramThrowable.printStackTrace(PrintWriter(stackTrace))
            FuguLog.e("unCaughtException stackTrace", "---> $stackTrace")
            System.err.println(stackTrace)
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(0)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mDeactivateUserReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        LocalBroadcastManager.getInstance(this@BaseActivity).registerReceiver(mDeactivateUserReceiver,
                IntentFilter(FuguAppConstant.DEACTIVATE_USER_INTENT))

        /**
         * Draw Code Version On the Every Screen Of the APP
         */
        if (BuildConfig.WATER_MARK) {
            val mDraw = DrawOnTop(this)
            addContentView(mDraw, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            mDraw.bringToFront()
        }
        Thread {
            if (CommonData.getCommonResponse() != null) {
                try {
                    val workspacesInfo = CommonData.getCommonResponse().data.workspacesInfo
                    runOnUiThread {
                        if (workspacesInfo != null && workspacesInfo.isNotEmpty()) {
                            val notificationsList = getNotificationsMap()[workspacesInfo[CommonData.getCurrentSignedInPosition()].businessId]
                            if (notificationsList != null) {
                                for (notification in notificationsList) {
                                    try {
                                        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                                        nm.cancel(notification)
                                    } catch (e: Exception) {
                                    }
                                }
                            }
                        }
                        Handler().postDelayed({
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                                Log.e("Notifications Count -> ", nm.activeNotifications.size.toString())
                                if (nm.activeNotifications.size == 1) {
                                    val notifications = nm.activeNotifications
                                    for (notification in notifications) {
                                        if (notification.id == SUMMARY_NOTIFICATION_ID) {
                                            nm.cancel(SUMMARY_NOTIFICATION_ID)
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
            }
        }.start()
        onTrailExpired()
    }

    override fun onPause() {
        super.onPause()
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mDeactivateUserReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun deactivatedUser() {

    }

    override fun showErrorMessage(resId: Int) {
        showErrorMessage(getString(resId), null, null, "Ok", "Cancel")
    }

    override fun showErrorMessage(resId: Int, positiveButtonCallback: BaseView.OnErrorHandleCallback) {
        showErrorMessage(getString(resId), positiveButtonCallback, null, "Ok", "Cancel")
    }

    override fun showErrorMessage(errorMessage: String?) {
        showErrorMessage(errorMessage!!, null, null, "Ok", "Cancel")
    }

    override fun showErrorMessage(errorMessage: String?, positiveButtonCallback: BaseView.OnErrorHandleCallback) {
        showErrorMessage(errorMessage!!, positiveButtonCallback, null, "Ok", "Cancel")
    }

    override fun showErrorMessage(errorMessage: String, positiveButtonCallback: BaseView.OnErrorHandleCallback?, nevativeButtonCallback: BaseView.OnPositiveButtonCallback?, positiveButtonText: String, negativeButtonText: String) {
        runOnUiThread {
            try {
                val alert = AlertDialog.Builder(this@BaseActivity)
                        .setMessage(errorMessage)
                        .setCancelable(false)
                        .setPositiveButton(positiveButtonText) { _, _ ->
                            positiveButtonCallback?.onErrorCallback()
                        }
                if (nevativeButtonCallback != null) {
                    alert.setNegativeButton(negativeButtonText) { _, _ ->
                        nevativeButtonCallback.onPositiveButtonClick()
                    }
                }
                alert.show()
            } catch (e: Exception) {

            }
        }
    }

    override fun showErrorMessage(apiError: ApiError) {
        showErrorMessage(apiError, null, null, "Ok", "Cancel")
    }

    override fun showErrorMessage(apiError: ApiError?, mOnErrorHandleCallback: BaseView.OnErrorHandleCallback?, onPositiveButtonCallback: BaseView.OnPositiveButtonCallback?, positiveButtonText: String, negativeButtonText: String) {
        if (apiError != null) {
            if (apiError.statusCode == AppConstant.SESSION_EXPIRED) {
                //todo handle session expired case
                CommonUtil.showToast(this, getString(R.string.error_session_expired))
            } else {
                showErrorMessage(apiError.message, mOnErrorHandleCallback, onPositiveButtonCallback, positiveButtonText, negativeButtonText)
            }
        } else {
            showErrorMessage(getString(R.string.error_unexpected_error), mOnErrorHandleCallback, onPositiveButtonCallback, positiveButtonText, negativeButtonText)
        }
    }


    override fun isNetworkConnected(): Boolean {
        return isNetworkAvailable()
    }


    override fun showLoading() {
        ProgressDialog.showProgressDialog(this)
    }

    override fun showLoading(message: String) {
        ProgressDialog.showProgressDialog(this, message)
    }

    override fun hideLoading() {
        ProgressDialog.dismissProgressDialog()
    }


    /**
     * Show notification dialog
     */
    fun showNotificationDialog(appSecretKey: String) {
        if (mNotificationDialog != null && mNotificationDialog!!.isShowing) {
            mNotificationDialog!!.dismiss()
        }
        mNotificationDialog = AlertDialog.Builder(this@BaseActivity)
                .setMessage("You are no longer part of this space! Please contact your space admin in case of any discrepancy.")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val fcCommonResponse = CommonData.getCommonResponse()
                    if (fcCommonResponse.getData().workspacesInfo.size > 0) {
                        for (i in 0 until fcCommonResponse.getData().workspacesInfo.size) {
                            if (fcCommonResponse.getData().workspacesInfo[i].fuguSecretKey == appSecretKey) {
                                fcCommonResponse.getData().workspacesInfo.removeAt(i)
                            }
                        }
                        CommonData.setCommonResponse(fcCommonResponse)
                        CommonData.setCurrentSignedInPosition(0)
                        try {
                            val i = Intent(this@BaseActivity, MainActivity::class.java)
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            PushReceiver.PushChannel.isEmailVerificationScreen = false
                            this@BaseActivity.finish()
                            this@BaseActivity.overridePendingTransition(0, 0)
                            this@BaseActivity.startActivity(i)
                            this@BaseActivity.overridePendingTransition(0, 0)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            this@BaseActivity.finish()
                        }
                    } else {
                        finishAffinity()
                        startActivity(Intent(this@BaseActivity, CreateWorkspaceActivity::class.java))
                    }
                }
                .show()
    }


    /**
     * Class to Draw the Version Code
     */
    inner class DrawOnTop
    /**
     * Instantiates a new Draw on top.
     *
     * @param activity current activity context
     */
    (activity: Context) : View(activity) {
        private val paintText = Paint()
        private val bounds = Rect()

        override fun onDraw(canvas: Canvas) {
            // put your drawing commands here
            paintText.color = Color.GRAY
            paintText.textSize = CommonUtil.dpToPx(this@BaseActivity, OVERLAY_TEXT_SIZE_INT).toFloat()
            paintText.getTextBounds(OVERLAY_TEXT, 0, OVERLAY_TEXT.length, bounds)
            canvas.drawText(OVERLAY_TEXT,
                    (width - (bounds.width() + TEN)).toFloat(),
                    (this.height - OVERLAY_TEXT_SIZE_INT).toFloat(),
                    paintText)
        }
    }

    /**
     * Method to set toolbar
     */
    fun setToolbar() {
        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
        ab.title = ""
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun initDialog() {
        dialog = null
    }

    @SuppressLint("SetTextI18n")
    fun onTrailExpired() {
        try {
            if (CommonData.getCommonResponse() != null) {
                val fcCommonResponse = CommonData.getCommonResponse()
                if (fcCommonResponse.getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].workspaceStatus == "EXPIRED") {
                    if (dialog == null) {
                        dialog = Dialog(this@BaseActivity, android.R.style.Theme_Light)
                        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog!!.setContentView(R.layout.dialog_plan_expired)

                        val textView = dialog!!.findViewById<AppCompatTextView>(R.id.tvExpiryText)
                        val isOwner = fcCommonResponse.getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].role == "OWNER"
                        if (isOwner) {
                            textView.text = ("Your space '" + fcCommonResponse.getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].workspaceName
                                    + "' trial has been expired. You can buy a plan by visiting our website or by clicking the button below.")
                        } else {
                            textView.text = ("Your space '" + fcCommonResponse.getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].workspaceName
                                    + "' trial has been expired. Please contact your space owner.")
                        }

                        val btnBuyPlan = dialog!!.findViewById<AppCompatButton>(R.id.btnBuyPlan)
                        val btnSwitchSpace = dialog!!.findViewById<AppCompatButton>(R.id.btnSwitchSpace)
                        val llLogout = dialog!!.findViewById<LinearLayout>(R.id.llLogout)
                        if (isOwner) {
                            btnBuyPlan.visibility = View.VISIBLE
                        }
                        btnBuyPlan.setOnClickListener {
                            val billingIntent = Intent(this@BaseActivity, WebViewActivity::class.java)
                            billingIntent.putExtra("billing_url", fcCommonResponse.getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].billingUrl + "?access_token=" + fcCommonResponse.getData().getUserInfo().getAccessToken() + "&is_mobile=true")
                            startActivityForResult(billingIntent, 1000)
                        }
                        btnSwitchSpace.setOnClickListener { startActivity(Intent(this@BaseActivity, YourSpacesActivity::class.java).putExtra("API_HIT", true)) }
                        llLogout.setOnClickListener {
                            showErrorMessage("Are you sure you want to logout ?", {
                                if (isNetworkConnected) {
                                    showLoading()
                                    apiLogout()
                                    Paper.init(this@BaseActivity)
                                } else {
                                    showErrorMessage(R.string.error_internet_not_connected)
                                }
                            }, { }, "Yes", "No")
                        }

                        dialog!!.setCanceledOnTouchOutside(false)
                        dialog!!.setCancelable(false)
                        dialog!!.show()
                    }
                } else {
                    if (dialog != null) {
                        dialog!!.dismiss()
                    }
                }
            }
        } catch (e: Exception) {
            //            e.printStackTrace();
        }

    }


    private fun apiLogout() {
        val commonParams = CommonParams.Builder()
                .add(TOKEN, CommonData.getFcmToken())
                .add(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(this@BaseActivity))
                .add(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails(this@BaseActivity))
                .build()
        RestClient.getApiInterface(true).logoutUser(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), BuildConfig.VERSION_CODE, FuguAppConstant.ANDROID, commonParams.map)
                .enqueue(object : ResponseResolver<CommonResponseFugu>() {
                    override fun onSuccess(commonResponseFugu: CommonResponseFugu) {
                        FuguConfig.clearFuguData(this@BaseActivity)
                        CommonData.clearData()
                        hideLoading()
                        finishAffinity()
                        startActivity(Intent(this@BaseActivity, IntroActivity::class.java))
                    }

                    override fun onError(error: ApiError) {
                        hideLoading()
                        if (error.statusCode == FuguAppConstant.SESSION_EXPIRE) {
                            CommonData.clearData()
                            FuguConfig.clearFuguData(this@BaseActivity)
                            finishAffinity()
                            startActivity(Intent(this@BaseActivity, IntroActivity::class.java))
                        } else {
                            showErrorMessage(error.message)
                        }
                    }

                    override fun onFailure(throwable: Throwable) {

                    }
                })
    }

    fun callOnActivityResult(requestCode: Int, resultCode: Int, data: Intent?, obj: Any?) {
        mGoogleSignInClient = obj as GoogleSignInClient
        onActivityResult(requestCode, resultCode, data)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            if (dialog != null) {
                dialog!!.dismiss()
            }
            dialog = null
            val fcCommonResponse = CommonData.getCommonResponse()
            fcCommonResponse.getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].workspaceStatus = "ENABLED"
            CommonData.setCommonResponse(fcCommonResponse)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun onCalendarAuthorizationGranted(authCode: String, callback: CalendarLinkingCallback) {
        showLoading()
        if (authCode.isNotEmpty()) {
            val authCode = URLDecoder.decode(authCode)
            Log.i("CalendarAuthCode", authCode)
//            showErrorMessage(authCode, {
            val commonResponseData = CommonData.getCommonResponse().data
            val commonParams = com.skeleton.mvp.retrofit.CommonParams.Builder()
                    .add(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails(this@BaseActivity))
                    .add("user_unique_key", commonResponseData.userInfo.userId)
                    .add("auth_token", authCode)
                    .addDomain()
                    .build()
            com.skeleton.mvp.retrofit.RestClient.getApiInterface().submitAuthorizeCode(CommonData.getCommonResponse().data.userInfo.accessToken, 1, BuildConfig.VERSION_CODE, commonParams.map)
                    .enqueue(object : com.skeleton.mvp.retrofit.ResponseResolver<CommonResponse>() {
                        override fun success(response: CommonResponse) {
                            hideLoading()
                            val commonResponse = CommonData.getCommonResponse()
                            commonResponse.data.userInfo.isCalendarLinked = true
                            CommonData.setCommonResponse(commonResponse)
                            showToast("Authorization Success")
                            callback.onAuthorizationSuccess()
                        }

                        override fun failure(error: APIError?) {
                            hideLoading()
                            showToast(error?.message ?: "Error updating token.")
                        }

                    })
//            }, { hideLoading() }, "Ok", "Cancel")
        } else
            showToast("Blank auth code received.")
    }

    fun onCalendarAuthorizationCanceled(errorInfo: String) {
        Log.e("CalendarAuthorizationCanceled", errorInfo)
        showErrorMessage("Authorization Failed. Do you want to try manual authorization?", {
            startActivity(Intent((this@BaseActivity), AuthorizeGoogleActivity::class.java))
            (this@BaseActivity).overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }, {}, "Yes", "Cancel")
    }

    fun makeFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_WRITE_STORAGE -> isPermissionGranted(grantResults[0] == PackageManager.PERMISSION_GRANTED, permissions[0])
        }
    }

    protected fun showImageEditorLoading(message: String) {
        mProgressDialog = android.app.ProgressDialog(this)
        mProgressDialog!!.setMessage(message)
        mProgressDialog!!.setProgressStyle(android.app.ProgressDialog.STYLE_SPINNER)
        mProgressDialog!!.setCancelable(false)
        mProgressDialog!!.show()
    }

    protected fun hideImageEditorLoading() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }

    protected fun showSnackbar(message: String) {
        val view = findViewById<View>(android.R.id.content)
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Check Network Connection
     *
     * @return boolean
     */
    open fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo: NetworkInfo? = null
        if (cm != null) {
            networkInfo = cm.activeNetworkInfo
        }
        return networkInfo != null && networkInfo.isConnected
    }


    interface CalendarLinkingCallback {
        fun onAuthorizationSuccess()
    }
}

