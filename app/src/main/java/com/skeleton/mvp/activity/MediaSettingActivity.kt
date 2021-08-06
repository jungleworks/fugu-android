package com.skeleton.mvp.activity

import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SwitchCompat
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.editprofile.EditProfileResponse
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.data.network.MultipartParams
import com.skeleton.mvp.data.network.ResponseResolver
import com.skeleton.mvp.data.network.RestClient
import com.skeleton.mvp.ui.AppConstants.ANDROID
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.util.Log

class MediaSettingActivity : BaseActivity(), View.OnClickListener {

    private var workspacesInfoList = ArrayList<WorkspacesInfo>()
    private var currentSigninedInPosition = 0
    private var mediaVisibility = true
    private var mSwitch: SwitchCompat? = null
    private var spinner: Spinner? = null
    private val notificationArray = arrayOf("None", "Wifi", "Mobile Network", "Both")
    private val notificationArrayToBeStored = arrayOf("NONE", "WIFI", "MOBILE_NETWORK", "BOTH")
    private var autoDownloadType: String = ""
    private lateinit var ivBack: AppCompatImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_setting)
        ivBack = findViewById(R.id.ivBack)
        ivBack.setOnClickListener(this)
        mSwitch = findViewById(R.id.switchGallery)
        spinner = findViewById<Spinner>(R.id.notificationType)
        val adapter = object : ArrayAdapter<String>(this,
                R.layout.item_notification_type, notificationArray) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)

                val externalFont = Typeface.createFromAsset(assets, FuguAppConstant.FONT_REGULAR)
                (v as TextView).typeface = externalFont
                return v
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getDropDownView(position, convertView, parent)

                val externalFont = Typeface.createFromAsset(assets, FuguAppConstant.FONT_REGULAR)
                (v as TextView).typeface = externalFont

                return v
            }
        }
        adapter.setDropDownViewResource(R.layout.item_notification_type)
        spinner?.adapter = adapter


        workspacesInfoList = CommonData.getCommonResponse().data.workspacesInfo as ArrayList<WorkspacesInfo>
        currentSigninedInPosition = CommonData.getCurrentSignedInPosition()
        mediaVisibility = workspacesInfoList[currentSigninedInPosition].mediaVisibility == 1
        autoDownloadType = workspacesInfoList[currentSigninedInPosition].autoDownloadLevel.toString()
        //Log.i("autoDownloadType",autoDownloadType)
        spinner?.setSelection(func(autoDownloadType))
        mSwitch?.isChecked = mediaVisibility
        Handler().postDelayed({
            spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {

                    if (isNetworkConnected) {
                        apiEditUserInfo(i, null)
                    } else {
                        showErrorMessage("Please check your internet connection and Try again")
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {

                }
            }

            mSwitch?.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {

                override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {

                    if (isNetworkConnected) {
                        apiEditUserInfo(null, isChecked)
                    } else {
                        showErrorMessage("Please check your internet connection and Try again")
                    }


                }

            })

        }, 500)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

    fun func(str: String): Int {
        if (str.equals("NONE")) {
            return 0
        } else if (str.equals("WIFI")) {
            return 1
        } else if (str.equals("MOBILE_NETWORK")) {
            return 2
        } else {
            return 3
        }
    }

    fun apiEditUserInfo(itemPosition: Int?, isChecked: Boolean?) {
        showLoading()
        val commonParams = MultipartParams.Builder()

        if (itemPosition != null) {
            commonParams.add("auto_download_level", notificationArrayToBeStored[itemPosition])
        }

        if (isChecked != null) {
            commonParams.add("gallery_media_visibility", if (isChecked) {
                1
            } else {
                0
            })
        }

        commonParams.add("workspace_id", workspacesInfoList[currentSigninedInPosition].workspaceId)
        commonParams.add("fugu_user_id", CommonData.getCommonResponse().getData().workspacesInfo.get(CommonData.getCurrentSignedInPosition()).userId)

        RestClient.getApiInterface(true).editProfile(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), BuildConfig.VERSION_CODE, ANDROID, commonParams.build().map)
                .enqueue(object : ResponseResolver<EditProfileResponse>() {
                    override fun onSuccess(t: EditProfileResponse?) {
                        val data = CommonData.getCommonResponse()
                        val workspacesInfoListToBeChanged = data.data.workspacesInfo
                        if (isChecked != null) {
                            for (i in 0..workspacesInfoList.size - 1) {
                                workspacesInfoListToBeChanged[i].mediaVisibility = if (isChecked) {
                                    1
                                } else {
                                    0
                                }
                            }
                            Log.i("Switch State=", "" + isChecked)
                        }

                        if (itemPosition != null) {
                            for (i in 0..workspacesInfoList.size - 1) {
                                workspacesInfoListToBeChanged[i].autoDownloadLevel = notificationArrayToBeStored[itemPosition]
                            }
                            Log.i("success", workspacesInfoList[currentSigninedInPosition].autoDownloadLevel)
                        }
                        CommonData.setCommonResponse(data)
                        hideLoading()
                    }

                    override fun onError(error: ApiError?) {
                        hideLoading()
                    }

                    override fun onFailure(throwable: Throwable?) {
                        hideLoading()
                    }


                })
    }
}
