package com.skeleton.mvp.meet

/********************************
Created by Amandeep Chauhan     *
Date :- 27/04/2020              *
 ********************************/

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.MainActivity
import com.skeleton.mvp.activity.MembersSearchActivity
import com.skeleton.mvp.activity.ScheduleMeetActivity
import com.skeleton.mvp.calendar.AuthorizeGoogleActivity
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.data.model.scheduleMeets.GetMeetingsResponse
import com.skeleton.mvp.data.network.RestClient
import com.skeleton.mvp.model.GetAllMembers
import com.skeleton.mvp.retrofit.APIError
import com.skeleton.mvp.retrofit.CommonParams
import com.skeleton.mvp.retrofit.ResponseResolver
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.util.Log
import com.skeleton.mvp.utils.DateUtils
import com.skeleton.mvp.utils.FuguUtils
import com.skeleton.mvp.utils.gone
import com.skeleton.mvp.utils.visible
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MeetFragment : Fragment() {

    private var startMeetWithMembers: HashMap<Long, GetAllMembers> = LinkedHashMap()
    private lateinit var mContext: Context
    private var isHangoutsMeet = false
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private lateinit var llSwitchAccount: LinearLayout
    private lateinit var llGoogleMeetContainer: RelativeLayout
    private lateinit var llLinkGoogleAccount: LinearLayoutCompat
    private lateinit var llNoScheduleMeetings: LinearLayoutCompat
    private lateinit var rvScheduledMeetings: RecyclerView
    private lateinit var tvNoScheduledMeetings: AppCompatTextView
    private lateinit var ivNoScheduledMeetings: AppCompatImageView
    var isGoogleAccountLinked: Boolean = false
    lateinit var adapter: ScheduledMeetsAdapter
    private val isGoogleMeetEnabled: Boolean = CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()].config.googleMeetEnabled

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onResume() {
        super.onResume()
        if (!FuguUtils.isWhiteLabel() && CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()].config.googleMeetEnabled && isGoogleAccountLinked)
            toggleGoogleMeetVisibility()
        if (userVisibleHint)
            getMeetings()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser)
            getMeetings()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.fragment_meet, container, false)
        val btnNewMeeting: LinearLayout = v.findViewById(R.id.btn_new_meeting)
        val btnJoinMeeting: LinearLayout = v.findViewById(R.id.btn_join_meeting)
        val tvLinkGoogleAccount: AppCompatTextView = v.findViewById(R.id.tvLinkGoogleAccount)
        llLinkGoogleAccount = v.findViewById(R.id.llLinkGoogleAccount)
        llGoogleMeetContainer = v.findViewById(R.id.llGoogleMeetContainer)
        llNoScheduleMeetings = v.findViewById(R.id.llNoScheduleMeetings)
        ivNoScheduledMeetings = v.findViewById(R.id.ivNoScheduledMeetings)
        tvNoScheduledMeetings = v.findViewById(R.id.tvNoScheduledMeetings)
        rvScheduledMeetings = v.findViewById(R.id.rvScheduledMeetings)
        val llGoogleMeet: LinearLayout = v.findViewById(R.id.llGoogleMeet)
        llSwitchAccount = v.findViewById(R.id.llSwitchAccount)
        val llScheduleMeeting: LinearLayout = v.findViewById(R.id.ll_schedule_meeting)
        isGoogleAccountLinked = CommonData.getCommonResponse().data.userInfo.isCalendarLinked

        adapter = ScheduledMeetsAdapter(ArrayList(), mContext)
        rvScheduledMeetings.layoutManager = LinearLayoutManager(mContext)
        rvScheduledMeetings.adapter = adapter
        if (FuguUtils.isWhiteLabel() || !isGoogleMeetEnabled) {
            llLinkGoogleAccount.visibility = View.GONE
            llGoogleMeetContainer.visibility = View.GONE
        } else {
            if (!isGoogleAccountLinked) {
                llLinkGoogleAccount.visibility = View.VISIBLE
                llGoogleMeetContainer.visibility = View.GONE
            } else {
                llLinkGoogleAccount.visibility = View.GONE
                llGoogleMeetContainer.visibility = View.VISIBLE
            }


            llSwitchAccount.setOnClickListener {
                linkGoogleAccount()
            }
            tvLinkGoogleAccount.setOnClickListener {
                linkGoogleAccount()
            }

            llGoogleMeet.setOnClickListener {
                val intent = Intent(mContext as MainActivity, MembersSearchActivity::class.java)
                intent.putExtra("searchingFor", "MEET")
                isHangoutsMeet = true
                startActivityForResult(intent, FuguAppConstant.RequestCodes.START_MEET_REQUEST)
            }
        }

        val workspaceInfo = CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()]
        var createMeetRoles = workspaceInfo.config.createMeetPermission
        if (!TextUtils.isEmpty(createMeetRoles)) {
            createMeetRoles = createMeetRoles!!.replace("[", "")
            createMeetRoles = createMeetRoles.replace("]", "")
            createMeetRoles = createMeetRoles.replace("\"".toRegex(), "")
            if (createMeetRoles.isNotEmpty()) {
                val createMeetRolesArray = createMeetRoles.split(",").toTypedArray()
                val createMeetRolesList = ArrayList(listOf(*createMeetRolesArray))
                val presentRole: String = workspaceInfo.role
                if (createMeetRolesList.contains(presentRole)) {
                    btnNewMeeting.setOnClickListener {
                        run {
                            isHangoutsMeet = false
                            val intent = Intent(mContext as MainActivity, MembersSearchActivity::class.java)
                            intent.putExtra("searchingFor", "MEET")
                            startActivityForResult(intent, FuguAppConstant.RequestCodes.START_MEET_REQUEST)
                        }
                    }
                } else {
                    btnNewMeeting.setOnClickListener {
                        Toast.makeText(mContext as MainActivity, mContext.getString(R.string.your_are_not_allowed_to_create_meeting), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        btnJoinMeeting.setOnClickListener {
            JoinMeetBottomSheetFragment(mContext).show((mContext as MainActivity).supportFragmentManager, "JoinMeetBottomSheetFragment")
        }
        llScheduleMeeting.setOnClickListener {
            startActivityForResult(Intent(mContext as MainActivity, ScheduleMeetActivity::class.java), FuguAppConstant.RequestCodes.SCHEDULE_MEET_REQUEST)
        }
        return v
    }

    private fun linkGoogleAccount() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode("1067615629789-qs9p7v7o3ur01tf7oanq3ds4k9ashmjh.apps.googleusercontent.com")
                .requestScopes(Scope("https://www.googleapis.com/auth/calendar"))
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(mContext as MainActivity, gso)
        val signInIntent: Intent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, BaseActivity.REQUEST_CALENDAR_AUTHORIZATION)
    }

    private fun startNewMeeting(list: String) {
        val workspaceInfoList = CommonData.getCommonResponse().data.workspacesInfo as ArrayList<WorkspacesInfo>
        val currentSignedInPosition = CommonData.getCurrentSignedInPosition()
        val gson = Gson()
        val entityType = object : TypeToken<LinkedHashMap<Long, GetAllMembers>>() {}.type
        startMeetWithMembers = gson.fromJson(list, entityType)
        StartNewMeetBottomSheetFragment(
                startMeetWithMembers,
                mContext as MainActivity,
                workspaceInfoList[currentSignedInPosition].enUserId,
                workspaceInfoList[currentSignedInPosition].fuguSecretKey,
                isHangoutsMeet
        ).show(fragmentManager!!, "VideoCallInvitationBottomSheetFragment")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FuguAppConstant.RequestCodes.START_MEET_REQUEST) {
            (mContext as MainActivity).viewpager.currentItem = 1
            if (resultCode == Activity.RESULT_OK) {
                startNewMeeting(data!!.getStringExtra("list")!!)
            }
        } else if (requestCode == FuguAppConstant.RequestCodes.SCHEDULE_MEET_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                getMeetings()
            }
        } else if (requestCode == BaseActivity.REQUEST_CALENDAR_AUTHORIZATION) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    val account = task.getResult(ApiException::class.java)
                    account?.let { googleSignInAccount ->
                        (mContext as MainActivity).onCalendarAuthorizationGranted(googleSignInAccount.serverAuthCode
                                ?: "", object : BaseActivity.CalendarLinkingCallback {
                            override fun onAuthorizationSuccess() {
                                isGoogleAccountLinked = true
                                toggleGoogleMeetVisibility()
//                                if (isCallbackNeeded) {
//                                    val intent = Intent(mContext as MainActivity, MembersSearchActivity::class.java)
//                                    intent.putExtra("searchingFor", "MEET")
//                                    isHangoutsMeet = true
//                                    startActivityForResult(intent, START_MEET_REQUEST)
//                                }
                            }
                        })
                    }
                    mGoogleSignInClient?.signOut()
                } catch (e: ApiException) {
                    Log.i("GoogleAuthorization", "GoogleAuth:failed code=" + e.statusCode)
                    startActivity(Intent((mContext), AuthorizeGoogleActivity::class.java))
                    (mContext as MainActivity).overridePendingTransition(R.anim.right_in, R.anim.left_out)
                }
            } else {
                (mContext as MainActivity).onCalendarAuthorizationCanceled("Activity Result Cancelled")
            }
        }
    }

    fun getMeetings() {
        try {
            Glide.with(mContext).load(R.raw.loading).into(ivNoScheduledMeetings)
            tvNoScheduledMeetings.text = mContext.getString(R.string.fetching_your_meetings)
            val commonResponseData = CommonData.getCommonResponse().data
            val workspacesInfo = commonResponseData.workspacesInfo
            val currentPosition = CommonData.getCurrentSignedInPosition()
            val commonParams = CommonParams.Builder()
                    .add(FuguAppConstant.USER_ID, workspacesInfo[currentPosition].userId)
                    .add("workspace_id", workspacesInfo[currentPosition].workspaceId)
                    .add("start_datetime", DateUtils.getInstance().convertToUTC(DateUtils.getFormattedDate(Calendar.getInstance().time)))
            RestClient.getApiInterface(true).getMeetings(commonResponseData.userInfo.accessToken, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                    .enqueue(object : ResponseResolver<GetMeetingsResponse>() {
                        override fun success(response: GetMeetingsResponse?) {
                            ivNoScheduledMeetings.setImageDrawable(mContext.getDrawable(R.drawable.no_meetings))
                            tvNoScheduledMeetings.text = mContext.getString(R.string.no_scheduled_meetings)
                            if (response?.data != null && response.data.size > 0) {
                                adapter.updateList(response.data)
                                rvScheduledMeetings.visible()
                                llNoScheduleMeetings.gone()
                            } else {
                                llNoScheduleMeetings.visible()
                                rvScheduledMeetings.gone()
                            }
                        }

                        override fun failure(error: APIError?) {
                            ivNoScheduledMeetings.setImageDrawable(mContext.getDrawable(R.drawable.no_meetings))
                            tvNoScheduledMeetings.text = mContext.getString(R.string.no_scheduled_meetings)
                            (mContext as BaseActivity).showErrorMessage(error?.message ?: "Error")
                        }

                    })
        } catch (e: Exception) {
        }
    }

    fun toggleGoogleMeetVisibility() {
        llLinkGoogleAccount.visibility = View.GONE
        llGoogleMeetContainer.visibility = View.VISIBLE
    }
}