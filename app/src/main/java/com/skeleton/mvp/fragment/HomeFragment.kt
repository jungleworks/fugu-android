package com.skeleton.mvp.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.ExifInterface
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.FuguColorConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.ChatActivity
import com.skeleton.mvp.activity.CreateWorkspaceActivity
import com.skeleton.mvp.activity.GroupInformationActivity
import com.skeleton.mvp.activity.MainActivity
import com.skeleton.mvp.adapter.ConversationAdapter
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.data.db.ChatDatabase
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.CommonResponse
import com.skeleton.mvp.data.model.Space
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.data.model.openandinvited.OpenAndInvited
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.model.FuguConversation
import com.skeleton.mvp.model.FuguGetConversationsResponse
import com.skeleton.mvp.model.media.MediaResponse
import com.skeleton.mvp.pushNotification.PushReceiver
import com.skeleton.mvp.retrofit.APIError
import com.skeleton.mvp.retrofit.CommonParams
import com.skeleton.mvp.retrofit.ResponseResolver
import com.skeleton.mvp.retrofit.RestClient
import com.skeleton.mvp.ui.AppConstants
import com.skeleton.mvp.ui.AppConstants.EXTRA_ALREADY_MEMBER
import com.skeleton.mvp.ui.fcinvite.InviteOnboardActivity
import com.skeleton.mvp.ui.home.SpacesAdapter
import com.skeleton.mvp.ui.profile.ProfileActivity
import com.skeleton.mvp.ui.search.CreateGroupSearchActivity
import com.skeleton.mvp.ui.yourspaces.YourSpacesActivity
import com.skeleton.mvp.util.Log
import com.skeleton.mvp.utils.EndlessScrolling
import com.skeleton.mvp.utils.UniqueIMEIID
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

class HomeFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemSelectedListener {

    private var workspacesInfoList = ArrayList<WorkspacesInfo>()
    private var currentSigninedInPosition = 0
    private var newSigninedInPosition = 0
    private var pageStart = 1
    private var pageEnd = 0
    private var pageSize = 0
    private var rvConversation: RecyclerView? = null
    private var rvSpaces: RecyclerView? = null
    private var llAddSpace: LinearLayout? = null
    private var vDim: View? = null
    private var fabCreateGroup: FloatingActionButton? = null

    //    private var fabSearch: FloatingActionButton? = null
    private var spacesAdapter: SpacesAdapter? = null
    private var spacesList = ArrayList<Space>()
    private var btnInvite: AppCompatButton? = null
    private var tvInvite: TextView? = null
    private var llInviteCard: ConstraintLayout? = null
    private var swipeConversation: SwipeRefreshLayout? = null
    private var workspacesSpinner: Spinner? = null
    private var cvSpaces: androidx.cardview.widget.CardView? = null
    private var tvChoose: TextView? = null
    private var conversationList = ArrayList<FuguConversation>()
    private var conversationMap = LinkedHashMap<Long, FuguConversation>()
    private var conversationAdapter: ConversationAdapter? = null
    private var endlessScrolling: EndlessScrolling? = null
    private val IS_HIT_REQUIRED = 200
    private var hidefab: Boolean = false
    private var isFirstCall = true
    private var myView: View? = null
    private var changeBussiness = false
    private var mContext: Context? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        myView = inflater.inflate(R.layout.fragment_home, container, false)
        Thread {
            kotlin.run {
                workspacesInfoList = CommonData.getCommonResponse().data.workspacesInfo as ArrayList<WorkspacesInfo>
                currentSigninedInPosition = CommonData.getCurrentSignedInPosition()
                (mContext as MainActivity).runOnUiThread {
                    initializeViews(myView)
                    setClickListeners()
                    setUpSpacesRecycler()
                    setUpConversationFromLocal()
                    setSpacesRecyclerData()
                    apiGetAllConversation()
                    setEndlessScrolling()
                }
            }
        }.start()
        return myView
    }

    /**
     * click listeners of other elements of the screen
     */
    private fun setClickListeners() {
        vDim?.setOnClickListener {
            toolBarDropDownManipulation()
            (mContext as MainActivity).arrowAnimation()
        }
        fabCreateGroup?.setOnClickListener {
            val mIntent = Intent((mContext as MainActivity), CreateGroupSearchActivity::class.java)
            mIntent.putExtra("fab_click", "fab_click")
            startActivity(mIntent)
            (mContext as MainActivity).overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }
//        fabCreateGroup?.setOnLongClickListener {
//            return@setOnLongClickListener true
//        }
        btnInvite?.setOnClickListener {
            val intent = Intent((mContext as MainActivity), InviteOnboardActivity::class.java)
            intent.putExtra(EXTRA_ALREADY_MEMBER, EXTRA_ALREADY_MEMBER)
            startActivity(intent)
            (mContext as MainActivity).overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }
        llAddSpace?.setOnClickListener {
            if ((mContext as MainActivity).isNetworkConnected) {
                (mContext as MainActivity).showLoading()
                apiGetOpenAndInvited()
            } else {
                (mContext as MainActivity).showErrorMessage(R.string.fugu_no_internet_connection_retry)
            }
        }
    }

    /**
     * Api to get open and invited workspace before openning YouSpacesActivity
     */
    private fun apiGetOpenAndInvited() {
        com.skeleton.mvp.data.network.RestClient.getApiInterface(true).getOpenAndInvited(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), BuildConfig.VERSION_CODE, FuguAppConstant.ANDROID)
                .enqueue(object : com.skeleton.mvp.data.network.ResponseResolver<OpenAndInvited>() {
                    override fun onSuccess(openAndInvited: OpenAndInvited) {
                        val commonResponse = CommonData.getCommonResponse()
                        commonResponse.data.invitationToWorkspaces = openAndInvited.data.invitationToWorkspaces
                        commonResponse.data.openWorkspacesToJoin = openAndInvited.data.openWorkspacesToJoin
                        CommonData.setCommonResponse(commonResponse)
                        (mContext as MainActivity).hideLoading()
                        if (commonResponse.data.openWorkspacesToJoin.size + commonResponse.data.invitationToWorkspaces.size > 0) {
                            val intent = Intent((mContext as MainActivity), YourSpacesActivity::class.java)
                            intent.putExtra(AppConstants.EXTRA_ALREADY_MEMBER, AppConstants.EXTRA_ALREADY_MEMBER)
                            startActivity(intent)
                        } else if (CommonData.getCommonResponse().getData().workspacesInfo.size != 0) {
                            val workspaceInfo = CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()]
                            var roles = workspaceInfo.config.createWorkspacePermisson
                            roles = roles.replace("[", "")
                            roles = roles.replace("]", "")
                            roles = roles.replace("\"".toRegex(), "")
                            val rolesArray = roles.split(",").toTypedArray()
                            val rolesList = java.util.ArrayList(Arrays.asList(*rolesArray))
                            val presentRole = workspaceInfo.role
                            if (rolesList.contains(presentRole)) {
                                val intent = Intent((mContext as MainActivity), CreateWorkspaceActivity::class.java)
                                intent.putExtra(AppConstants.EXTRA_ALREADY_MEMBER, AppConstants.EXTRA_ALREADY_MEMBER)
                                startActivity(intent)
                            } else {
                                toolBarDropDownManipulation()
                                (mContext as MainActivity).arrowAnimation()
                                Toast.makeText(mContext, "You don't have any pending invitations.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onError(error: ApiError) {
                        (mContext as MainActivity).hideLoading()
                    }

                    override fun onFailure(throwable: Throwable) {
                        (mContext as MainActivity).hideLoading()
                    }
                })
    }

    private fun setUpConversationFromLocal() {
        conversationMap = ChatDatabase.getConversationMap(workspacesInfoList[currentSigninedInPosition].fuguSecretKey)
        conversationAdapter?.updateList(conversationMap, workspacesInfoList[currentSigninedInPosition].userId.toLong())
    }

    private fun setEndlessScrolling() {
        try {
            if (endlessScrolling == null) {
                endlessScrolling = object : EndlessScrolling(rvConversation?.layoutManager as LinearLayoutManager) {
                    override fun onLoadMore(currentPages: Int) {
                        pageStart += pageSize
                        pageEnd = conversationList.size + pageSize // Fixed pagination problem when coming back from a chat.
                        apiGetAllConversation()
                    }

                    override fun onHide() {
                    }

                    override fun onShow() {
                    }
                }
                rvConversation?.addOnScrollListener(endlessScrolling!!)
            }
        } catch (e: Exception) {
        }

    }

    override fun onAttach(context: Activity) {
        super.onAttach(context)
        this.mContext = context
    }

    fun setContext(context: Context?) {
        this.mContext = context
    }

    private fun initializeViews(view: View?) {
        rvConversation = view?.findViewById(R.id.rvConversation)
        rvSpaces = view?.findViewById(R.id.rvSpaces)
        llAddSpace = view?.findViewById(R.id.llAddSpace)
        vDim = view?.findViewById(R.id.vDim)
        fabCreateGroup = view?.findViewById(R.id.fabCreateGroup)
//        fabSearch = view?.findViewById(R.id.fabSearch)
        btnInvite = view?.findViewById(R.id.btnInvite)
        tvInvite = view?.findViewById(R.id.tvInvite)
        llInviteCard = view?.findViewById(R.id.llInviteCard)
        fabCreateGroup = view?.findViewById(R.id.fabCreateGroup)
        cvSpaces = view?.findViewById(R.id.cvSpaces)
        tvChoose = view?.findViewById(R.id.tvChooseSpace)
        workspacesSpinner = view?.findViewById(R.id.spinner)
        workspacesSpinner?.onItemSelectedListener = this
        swipeConversation = view?.findViewById(R.id.swipteConversation)
        swipeConversation?.setOnRefreshListener(this)
        swipeConversation?.setColorSchemeColors(FuguColorConfig().fuguThemeColorPrimary)

        spacesAdapter = SpacesAdapter(spacesList, mContext)
        rvSpaces?.layoutManager = LinearLayoutManager(mContext)
        rvSpaces?.adapter = spacesAdapter

        conversationAdapter = ConversationAdapter((mContext as MainActivity), conversationMap,
                workspacesInfoList[currentSigninedInPosition].fullName,
                workspacesInfoList[currentSigninedInPosition].userId.toLong(),
                workspacesInfoList[currentSigninedInPosition].workspaceName,
                workspacesInfoList[currentSigninedInPosition].enUserId,
                object : ConversationAdapter.Callback {
                    override fun onClick(fuguConversation: FuguConversation?) {
                        val chatIntent = Intent((mContext as MainActivity), ChatActivity::class.java)
                        chatIntent.putExtra(FuguAppConstant.CONVERSATION, Gson().toJson(fuguConversation, com.skeleton.mvp.model.FuguConversation::class.java))
                        if (hidefab) {
                            chatIntent.putExtra("fromHome", true)
                        }
                        startActivityForResult(chatIntent, IS_HIT_REQUIRED)
                    }

                    override fun onIconClick(fuguConversation: FuguConversation?) {
                        when (fuguConversation?.chat_type) {
                            3, 4, 5, 6 -> {
                                val intent = Intent(mContext, GroupInformationActivity::class.java)
                                com.skeleton.mvp.fugudatabase.CommonData.setChatType(fuguConversation.chat_type)
                                intent.putExtra("channelId", fuguConversation.channelId)
                                intent.putExtra("groupName", fuguConversation.label)
                                intent.putExtra("isJoined", true)
                                intent.putExtra("isMuted", fuguConversation.notification)
                                intent.putExtra(MESSAGE_UNIQUE_ID, UUID.randomUUID().toString())
                                startActivity(intent)
                                (mContext as MainActivity).overridePendingTransition(R.anim.right_in, R.anim.left_out)
                            }
                            1, 2 -> {
                                val workspaceInfo = CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()]
                                val commonParams = CommonParams.Builder()
                                        .add(CHANNEL_ID, fuguConversation.channelId)
                                        .add(EN_USER_ID, workspaceInfo.enUserId)
                                        .add(GET_DATA_TYPE, "MEMBERS")
                                        .build()
                                RestClient.getApiInterface().getGroupInfo(
                                        CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                                        workspaceInfo.fuguSecretKey,
                                        1,
                                        BuildConfig.VERSION_CODE,
                                        commonParams.map
                                ).enqueue(object : ResponseResolver<MediaResponse>() {
                                    override fun success(getMembersResponse: MediaResponse) {
                                        for (i in 0 until getMembersResponse.data.chatMembers.size) {
                                            if (java.lang.Long.valueOf(getMembersResponse.data.chatMembers[i].userId!!.toLong()).compareTo(workspaceInfo.userId.toLong()) != 0) {
                                                val intent = Intent(mContext, ProfileActivity::class.java)
                                                intent.putExtra("open_profile", (getMembersResponse.data.chatMembers[i].userId).toString())
                                                intent.putExtra("channelId", fuguConversation.channelId)
                                                startActivity(intent)
                                                (mContext as MainActivity).overridePendingTransition(R.anim.right_in, R.anim.left_out)
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
                })
        rvConversation?.layoutManager = LinearLayoutManager(mContext)
        rvConversation?.adapter = conversationAdapter
        rvConversation?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        fabCreateGroup?.show()
//                        fabSearch?.show()
                    }
                    AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL -> {
                        fabCreateGroup?.hide()
//                        fabSearch?.hide()
                    }
                }
            }
        })

//        fabSearch?.setOnClickListener {
//            val intent = Intent(activity, HomeSearchActivity::class.java)
//            if (!TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getImageUri())
//                    || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getSharedText())
//                    || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getOtherFilesUriString())
//                    || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getVideoUri())
//                    || !TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getMultipleImage())
//            ) {
//                intent.putExtra("fromHome", true)
//            }
//            startActivity(intent)
//        }
    }

    fun setUpSpacesRecycler() {
        try {
            (mContext as MainActivity).runOnUiThread {
                workspacesInfoList = CommonData.getCommonResponse().data.workspacesInfo as ArrayList<WorkspacesInfo>
                workspacesInfoList[CommonData.getCurrentSignedInPosition()].currentLogin = true
                spacesList = ArrayList()
                for (space in workspacesInfoList) {
                    spacesList.add(Space(space.workspaceName, space.currentLogin, space.workspaceId, false))
                }
                spacesAdapter?.updateList(spacesList)
                spacesAdapter?.notifyDataSetChanged()
            }
        } catch (e: Exception) {

        }
    }

    /**
     * toolbar dropdown open or closed manipulation
     */
    fun toolBarDropDownManipulation() {

        try {
            if (rvSpaces == null || llAddSpace == null || vDim == null) {
                initializeViews(myView)
                setUpSpacesRecycler()
                setSpacesRecyclerData()
            }
            try {
                if (rvSpaces?.visibility == View.VISIBLE) {
                    rvSpaces?.visibility = View.GONE
//                    fabSearch?.show()
                    fabCreateGroup?.show()
                    llAddSpace?.visibility = View.GONE
                    vDim?.visibility = View.GONE
                } else {
                    rvSpaces?.visibility = View.VISIBLE
//                    fabSearch?.hide()
                    fabCreateGroup?.hide()
                    llAddSpace?.visibility = View.VISIBLE
                    vDim?.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    override fun onRefresh() {
        pageStart = 1
        pageEnd = 0
        pageSize = 0
        apiGetAllConversation()
        swipeConversation?.isRefreshing = true
        if (endlessScrolling != null) {
            rvConversation?.removeOnScrollListener(endlessScrolling!!)
            endlessScrolling = null
        }
        setEndlessScrolling()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        try {
            (mContext as MainActivity).changeBusiness(position, false, false, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Choose a business on selection of that particular business from spaces recycler
     */
    fun changeBusiness(position: Int, isAnimation: Boolean, refreshCurrentPosition: Boolean, changeBusiness: ChangeBusiness) {
        if (position != currentSigninedInPosition || refreshCurrentPosition) {
            (mContext!! as MainActivity).showLoading()
            val commonParams = com.skeleton.mvp.data.network.CommonParams.Builder()
                    .add(AppConstants.WORKSPACE_ID, CommonData.getCommonResponse().getData().workspacesInfo[position].workspaceId)
                    .add(AppConstants.TOKEN, CommonData.getFcmToken())
                    .add(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId((mContext as MainActivity)))
                    .add(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails((mContext as MainActivity)))
                    .build()

            com.skeleton.mvp.data.network.RestClient.getApiInterface(true).switchWorkspace(CommonData.getCommonResponse().data.userInfo.accessToken, BuildConfig.VERSION_CODE, AppConstants.ANDROID, commonParams.map)
                    .enqueue(object : ResponseResolver<CommonResponse>() {
                        override fun success(t: com.skeleton.mvp.data.model.CommonResponse?) {
                            Thread {
                                currentSigninedInPosition = position
                                CommonData.setCurrentSignedInPosition(position)
                                pageStart = 1
                                pageEnd = 0
                                pageSize = 0
                                endlessScrolling?.setCurrentPage(0)
                                apiGetAllConversation()

                                (mContext as MainActivity).runOnUiThread {

                                    conversationAdapter?.updateList(conversationMap, workspacesInfoList[currentSigninedInPosition].userId.toLong())
                                    Handler().postDelayed({
                                        (mContext!! as MainActivity).hideLoading()
                                        if (isAnimation) {
                                            toolBarDropDownManipulation()
                                        }
                                        changeBusiness.changeBusinessSuccess()
                                    }, 500)
                                }

                            }.start()
                        }

                        override fun failure(error: APIError?) {

                        }

                    })
        }
    }

    private fun apiGetAllConversation() {
        if (CommonData.getCommonResponse()?.data != null) {
            val commonParams = getConversationRequestParams()
            if (commonParams != null) {
                RestClient.getApiInterface().getConversations(
                        CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                        workspacesInfoList[currentSigninedInPosition].fuguSecretKey,
                        1,
                        BuildConfig.VERSION_CODE,
                        commonParams.build().map).enqueue(object : ResponseResolver<FuguGetConversationsResponse>() {
                    override fun success(fuguConversationResponse: FuguGetConversationsResponse?) {
                        isFirstCall = false
                        pageSize = fuguConversationResponse?.data?.pageSize!!
                        if (pageStart == 1) {
                            conversationList = ArrayList()
                            conversationMap = LinkedHashMap()
                        }
                        if (pageEnd > 0) {
                            pageEnd = 0
                        }
                        for (fuguConversation in fuguConversationResponse.data?.conversationList!!) {
                            val conversation = FuguConversation(fuguConversation.channelId,
                                    fuguConversation.userId, fuguConversation.last_sent_by_full_name,
                                    fuguConversation.dateTime, fuguConversation.message,
                                    fuguConversation.label, fuguConversation.thumbnailUrl,
                                    fuguConversation.message_type, fuguConversation.chat_type,
                                    fuguConversation.last_sent_by_id, fuguConversation.messageState,
                                    fuguConversation.unreadCount, fuguConversation.muid,
                                    fuguConversation.notifications,
                                    fuguConversation.membersInfo,
                                    fuguConversation.customLabel, fuguConversation.last_message_status,
                                    fuguConversation.callType,
                                    fuguConversation.otherUserType)
                            conversation.isPinned = fuguConversation.isPinned
                            conversationList.add(conversation)
                            conversationMap[fuguConversation.channelId] = conversation

                        }
                        conversationAdapter?.updateList(conversationMap, workspacesInfoList[currentSigninedInPosition].userId.toLong())
                        conversationAdapter?.notifyDataSetChanged()
                        swipeConversation?.isRefreshing = false
                        inviteButtonManipulation()
                        Thread {
                            kotlin.run {
                                ChatDatabase.setConversationMap(conversationMap, workspacesInfoList[currentSigninedInPosition].fuguSecretKey)
                            }
                        }.start()

                    }

                    override fun failure(error: APIError?) {
                        swipeConversation?.isRefreshing = false
                        try {
                            (mContext as MainActivity).hideLoading()
                            if (error?.statusCode == 405) {
                                changeBussiness = true
                                (mContext as MainActivity).showLoading()

                            }
                        } catch (e: Exception) {

                        }
                    }

                })
            }
        }
    }

    /**
     * Create Request Params for getConversation api Hit
     */
    private fun getConversationRequestParams(): CommonParams.Builder? {
        try {
            val commonParams = CommonParams.Builder()
            if (workspacesInfoList.size == 0) {
                workspacesInfoList = CommonData.getCommonResponse().data.workspacesInfo as ArrayList<WorkspacesInfo>
                currentSigninedInPosition = CommonData.getCurrentSignedInPosition()
            }
            commonParams.add(FuguAppConstant.EN_USER_ID, workspacesInfoList[currentSigninedInPosition].enUserId)
            commonParams.add(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails((mContext as MainActivity)))
            commonParams.add(FuguAppConstant.DEVICE_TOKEN, CommonData.getFcmToken())
            commonParams.add(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId((mContext as MainActivity)))
            if (pageStart >= 1 && pageEnd == 0) {
                commonParams.add(FuguAppConstant.PAGE_START, pageStart)
            } else {
                commonParams.add(FuguAppConstant.PAGE_START, pageStart)
                commonParams.add(FuguAppConstant.PAGE_END, pageEnd)
            }
            return commonParams
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * Interface to change the business i.e switch workspace
     */
    interface ChangeBusiness {
        fun changeBusinessSuccess()
    }

    override fun onResume() {
        super.onResume()
        unregisterReceivers()
        PushReceiver.PushChannel.pushChannelId = -2L
        LocalBroadcastManager.getInstance((mContext as MainActivity)).registerReceiver(mChannelReceiver,
                IntentFilter(CHANNEL_INTENT))
        LocalBroadcastManager.getInstance((mContext as MainActivity)).registerReceiver(mBackgroundSending,
                IntentFilter(BACKGROUND_SENDING_COMPLETE))
        LocalBroadcastManager.getInstance(mContext as MainActivity).registerReceiver(mEditMessageReceiver,
                IntentFilter(FuguAppConstant.EDIT_MESSAGE_INTENT))
        if (!isFirstCall) {
            newSigninedInPosition = CommonData.getCurrentSignedInPosition()
            if (newSigninedInPosition != currentSigninedInPosition) {
                currentSigninedInPosition = CommonData.getCurrentSignedInPosition()
                refreshList()
            } else {
                backgroundSendingDone()
            }

        }
    }

    /**
     * Unregister all the broadcast receivers to avoid multiple registering
     */
    private fun unregisterReceivers() {
        try {
            LocalBroadcastManager.getInstance((mContext as MainActivity)).unregisterReceiver(mChannelReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            LocalBroadcastManager.getInstance((mContext as MainActivity)).unregisterReceiver(mBackgroundSending)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            LocalBroadcastManager.getInstance((mContext as MainActivity)).unregisterReceiver(mEditMessageReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Add channel whenever a user is added in the channel or self is added in a group add channel
     */
    private val mChannelReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Thread {
                kotlin.run {
                    if (intent.hasExtra("is_deleted_group")) {
                        pageStart = 1
                        pageEnd = conversationList.size
                        apiGetAllConversation()
                    }
                    try {
                        conversationMap = ChatDatabase.getConversationMap(workspacesInfoList[currentSigninedInPosition].fuguSecretKey)
                        (mContext as MainActivity).runOnUiThread {
                            conversationAdapter?.updateList(conversationMap, workspacesInfoList[currentSigninedInPosition].userId.toLong())
                            Thread {
                                kotlin.run {
                                    ChatDatabase.setConversationMap(conversationMap, workspacesInfoList[currentSigninedInPosition].fuguSecretKey)
                                }
                            }.start()
                            inviteButtonManipulation()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }.start()
        }
    }

    /**
     * Hit get conversation api when sending messages in background is complete
     */
    private var mBackgroundSending: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            pageStart = 1
            pageEnd = 0
            pageSize = 0
            apiGetAllConversation()
            if (endlessScrolling != null) {
                rvConversation?.removeOnScrollListener(endlessScrolling!!)
                endlessScrolling = null
            }
            setEndlessScrolling()
        }
    }

    private val mEditMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (conversationMap[intent.getLongExtra(CHANNEL_ID, -1L)] != null && !intent.getBooleanExtra(IS_THREAD_MESSAGE, false)) {
                val conversation = conversationMap[intent.getLongExtra(CHANNEL_ID, -1L)]!!
                if (conversation.muid.equals(intent.getStringExtra(MESSAGE_UNIQUE_ID))) {
                    conversation.message = intent.getStringExtra(MESSAGE)
                    conversationMap[intent.getLongExtra(CHANNEL_ID, -1L)] = conversation
                    conversationAdapter?.updateList(conversationMap, workspacesInfoList[currentSigninedInPosition].userId.toLong())

                    Thread {
                        kotlin.run {
                            ChatDatabase.setConversationMap(conversationMap, workspacesInfoList[currentSigninedInPosition].fuguSecretKey)
                        }
                    }.start()

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceivers()
    }

    fun backgroundSendingDone() {
        endlessScrolling?.setCurrentPage(0)
        pageStart = 1
        pageEnd = conversationList.size + 1
        pageSize = 0
        apiGetAllConversation()
        if (endlessScrolling != null) {
            rvConversation?.removeOnScrollListener(endlessScrolling!!)
            endlessScrolling = null
        }
        setEndlessScrolling()
    }

    private fun refreshList() {
        endlessScrolling?.setCurrentPage(0)
        pageStart = 1
        pageEnd = 0
        pageSize = 0
        apiGetAllConversation()
        if (endlessScrolling != null) {
            rvConversation?.removeOnScrollListener(endlessScrolling!!)
            endlessScrolling = null
        }
        setEndlessScrolling()
    }

    fun navigateToTopOfScreen() {
        try {
            rvConversation?.scrollToPosition(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun inviteButtonManipulation() {
        try {
            (mContext as MainActivity).runOnUiThread {
                if ((workspacesInfoList[currentSigninedInPosition].role == "OWNER" ||
                                workspacesInfoList[currentSigninedInPosition].role == "ADMIN" ||
                                workspacesInfoList[currentSigninedInPosition].config.anyUserCanInvite == "1") && workspacesInfoList[currentSigninedInPosition].role != "GUEST") {
                    if (conversationMap.size <= 5) {
                        btnInvite?.visibility = View.VISIBLE
                        tvInvite?.visibility = View.VISIBLE
                        llInviteCard?.visibility = View.VISIBLE
                    } else {
                        btnInvite?.visibility = View.GONE
                        tvInvite?.visibility = View.GONE
                        llInviteCard?.visibility = View.GONE
                    }
                } else {
                    btnInvite?.visibility = View.GONE
                    tvInvite?.visibility = View.GONE
                    llInviteCard?.visibility = View.GONE
                }
            }
        } catch (e: Exception) {
        }
    }

    @SuppressLint("RestrictedApi")
    fun hideFab(isFabHidden: Boolean) {
        this.hidefab = isFabHidden
        if (isFabHidden) {
            fabCreateGroup?.visibility = View.GONE
            cvSpaces?.visibility = View.VISIBLE
            tvChoose?.visibility = View.VISIBLE
        } else {
            fabCreateGroup?.visibility = View.VISIBLE
            cvSpaces?.visibility = View.GONE
            tvChoose?.visibility = View.GONE
        }
    }


    /**
     * Set spaces recycler view
     */
    @SuppressLint("RestrictedApi")
    private fun setSpacesRecyclerData() {
        val spacesDropdownList = ArrayList<String>()
        Thread {
            kotlin.run {
                spacesList.clear()
                workspacesInfoList[CommonData.getCurrentSignedInPosition()].currentLogin = true
                for (workspaceInfo in workspacesInfoList) {
                    spacesList.add(Space(workspaceInfo.workspaceName,
                            workspaceInfo.currentLogin,
                            workspaceInfo.workspaceId,
                            false))
                    spacesDropdownList.add(workspaceInfo.workspaceName)
                }

                try {
                    (mContext as MainActivity).runOnUiThread {
                        if (hidefab) {
                            fabCreateGroup?.visibility = View.GONE
                            cvSpaces?.visibility = View.VISIBLE
                            tvChoose?.visibility = View.VISIBLE
                        } else {
                            fabCreateGroup?.visibility = View.VISIBLE
                            cvSpaces?.visibility = View.GONE
                            tvChoose?.visibility = View.GONE
                        }
                    }
                } catch (e: Exception) {

                }

                if (spacesDropdownList.size > 1 && hidefab) {
                    try {
                        (mContext as MainActivity).runOnUiThread {
                            cvSpaces?.visibility = View.VISIBLE
                            tvChoose?.visibility = View.VISIBLE
                        }
                        (mContext as MainActivity).runOnUiThread {
                            val aa = ArrayAdapter(mContext as MainActivity, android.R.layout.simple_spinner_dropdown_item, spacesDropdownList)
                            workspacesSpinner?.adapter = aa
                            workspacesSpinner?.setSelection(CommonData.getCurrentSignedInPosition())
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    try {
                        (mContext as MainActivity).runOnUiThread {
                            cvSpaces?.visibility = View.GONE
                            tvChoose?.visibility = View.GONE
                        }
                    } catch (e: Exception) {

                    }

                }
                try {
                    (mContext as MainActivity).runOnUiThread {
                        val displayMetrics = DisplayMetrics()
                        (mContext as MainActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
                        spacesAdapter?.updateList(spacesList)
                        spacesAdapter?.notifyDataSetChanged()
                        if (spacesList.size <= 6) {
                            rvSpaces?.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                        } else {
                            rvSpaces?.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, displayMetrics.heightPixels / 2)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }
        }.start()
    }

    /**
     * Clear chat from channel options dialog delete chat option
     * remove the item from conversation map and from local database
     */
    fun apiClearChat(muid: String, channelId: Long?) {
        val commonParams = com.skeleton.mvp.retrofit.CommonParams.Builder()
                .add(FuguAppConstant.EN_USER_ID, CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].enUserId)
                .add("muid", muid)
                .add(FuguAppConstant.CHANNEL_ID, channelId)
                .build()
        com.skeleton.mvp.retrofit.RestClient.getApiInterface().clearChatHistory(
                com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                workspacesInfoList[currentSigninedInPosition].fuguSecretKey,
                1, BuildConfig.VERSION_CODE, commonParams.map)
                .enqueue(object : com.skeleton.mvp.retrofit.ResponseResolver<com.skeleton.mvp.retrofit.CommonResponse>((mContext as MainActivity), true, false) {
                    override fun success(commonResponse: com.skeleton.mvp.retrofit.CommonResponse) {
                        ChatDatabase.setMessageList(java.util.ArrayList(), channelId)
                        ChatDatabase.setMessageMap(java.util.LinkedHashMap(), channelId)
                        ChatDatabase.setUnsentMessageMapByChannel(channelId, java.util.LinkedHashMap())
                        getFromSdcardAndDelete(channelId!!)
                        conversationMap.remove(channelId)
                        Thread {
                            kotlin.run {
                                ChatDatabase.setConversationMap(conversationMap, workspacesInfoList[currentSigninedInPosition].fuguSecretKey)
                                (mContext as MainActivity).runOnUiThread {
                                    conversationAdapter?.updateList(conversationMap, workspacesInfoList[currentSigninedInPosition].userId.toLong())
                                    inviteButtonManipulation()
                                }
                            }
                        }.start()

                    }

                    override fun failure(error: APIError) {

                    }
                })
    }

    private fun getFromSdcardAndDelete(channelId: Long) {
        var listFile: Array<File>? = null
        try {
            val filesNormal = File(Environment.getExternalStorageDirectory(), FuguAppConstant.APP_NAME_SHORT +
                    File.separator + workspacesInfoList.get(currentSigninedInPosition).workspaceName?.replace(" ".toRegex(), "")?.replace("'s".toRegex(), "")
                    + File.separator + FuguAppConstant.IMAGE)

            val filesPrivate = File(Environment.getExternalStorageDirectory(), FuguAppConstant.APP_NAME_SHORT +
                    File.separator + workspacesInfoList.get(currentSigninedInPosition).workspaceName?.replace(" ".toRegex(), "")?.replace("'s".toRegex(), "")
                    + File.separator + FuguAppConstant.PRIVATE_IMAGES)

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

    /**
     * Clear chat from channel options dialog leave group option
     * remove the item from conversation map and from local database
     */
    fun removeChat(channelId: Long?) {
        conversationMap.remove(channelId)
        Thread {
            kotlin.run {
                ChatDatabase.setConversationMap(conversationMap, workspacesInfoList[currentSigninedInPosition].fuguSecretKey)
                (mContext as MainActivity).runOnUiThread {
                    conversationAdapter?.updateList(conversationMap, workspacesInfoList[currentSigninedInPosition].userId.toLong())

                    Thread {
                        kotlin.run {
                            ChatDatabase.setConversationMap(conversationMap, workspacesInfoList[currentSigninedInPosition].fuguSecretKey)
                        }
                    }.start()

                    inviteButtonManipulation()
                }
            }
        }.start()
    }

    /**
     * Pin/Unpin chat from channel options dialog pin/unpin chat option
     */
    fun togglePinChannel(channelId: Long?, isPinned: Boolean) {
        if (conversationMap[channelId] != null) {
            val conversation = conversationMap[channelId]
            conversation?.isPinned = if (isPinned) 1 else 0
            conversationMap[channelId!!] = conversation!!
            conversationAdapter?.updateList(conversationMap, workspacesInfoList[currentSigninedInPosition].userId.toLong())
            Thread {
                kotlin.run {
                    ChatDatabase.setConversationMap(conversationMap, workspacesInfoList[currentSigninedInPosition].fuguSecretKey)
                }
            }.start()
        }
    }

    /**
     * Mute/Unmute chat from channel options dialog mute/unmute group option
     */
    fun muteGroup(channelId: Long?, isMuted: String) {
        if (conversationMap[channelId] != null) {
            val conversation = conversationMap[channelId]
            conversation?.notification = isMuted
            conversationAdapter?.updateList(conversationMap, workspacesInfoList[currentSigninedInPosition].userId.toLong())
            Thread {
                kotlin.run {
                    ChatDatabase.setConversationMap(conversationMap, workspacesInfoList[currentSigninedInPosition].fuguSecretKey)
                }
            }.start()
        }
    }

    /**
     * Choose a business on selection of that particular business from spaces recycler
     */
    fun selectBusiness() {
        toolBarDropDownManipulation()
    }

    fun updateList() {
        conversationMap = ChatDatabase.getConversationMap(workspacesInfoList[currentSigninedInPosition].fuguSecretKey)
        (mContext as MainActivity).runOnUiThread {
            conversationAdapter?.updateList(conversationMap, workspacesInfoList[currentSigninedInPosition].userId.toLong())
        }
    }

    private fun dpToPx(dpParam: Int): Int {
        val d = mContext?.resources?.displayMetrics?.density
        return (dpParam * d!!).toInt()
    }

    fun checkIfUserIsDisabled() {
        if (changeBussiness) {
            (mContext as MainActivity).runOnUiThread {
                AlertDialog.Builder(mContext as MainActivity)
                        .setMessage("You are no longer part of this space! Please contact your space admin in case of any discrepancy.")
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok) { dialog, which ->
                            (mContext as MainActivity).changeBusiness(0, false, true, null)
                        }
                        .show()
            }
        }
    }
}
