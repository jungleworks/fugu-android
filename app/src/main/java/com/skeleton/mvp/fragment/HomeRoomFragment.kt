package com.skeleton.mvp.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.MainActivity
import com.skeleton.mvp.adapter.ConversationAdapter
import com.skeleton.mvp.data.model.Space
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.ui.home.SpacesAdapter
import com.skeleton.mvp.utils.EndlessScrolling

/**
 * Created by rajatdhamija
 * 13/08/18.
 */
class OldHomeRoomFragment : Fragment(),
        AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
        try {
            (activity as MainActivity).changeBusiness(pos, false, false, null)
        } catch (e: Exception) {

        }
    }

    private var rvConversation: androidx.recyclerview.widget.RecyclerView? = null
    private var rvSpaces: androidx.recyclerview.widget.RecyclerView? = null
    private var llAddSpace: LinearLayout? = null
    private var vDim: View? = null
    private var fabCreateGroup: FloatingActionButton? = null
    private var conversationAdapter: ConversationAdapter? = null
    private var mContext: Context? = null
    private var spacesAdapter: SpacesAdapter? = null
    private var spacesList = ArrayList<Space>()
    private lateinit var endlessScrolling: EndlessScrolling
    private var workspacesInfoList = ArrayList<WorkspacesInfo>()
    private var currentSigninedInPosition = 0
    private var newSigninedInPosition = 0
    private var unreadCounts = 0
    private var offset = 0
    private var limit = 21
    private val IS_HIT_REQUIRED = 200
    private var btnInvite: AppCompatButton? = null
    private var tvInvite: TextView? = null
    private var swipteConversation: SwipeRefreshLayout? = null
    private var hideFab = false
    var country = arrayOf("India", "USA", "China", "Japan", "Other")
    var workspacesSpinner: Spinner? = null
    var cvSpaces: androidx.cardview.widget.CardView? = null
    var tvChoose: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
//        Thread {
//            kotlin.run {
//                workspacesInfoList = CommonData.getCommonResponse().data.workspacesInfo as ArrayList<WorkspacesInfo>
//                currentSigninedInPosition = CommonData.getCurrentSignedInPosition()
//                (mContext as MainActivity).runOnUiThread {
//                    initializeViews(view)
//                    setClickListeners()
//                    getAllConversationsFromRoomAndUpdate(limit, offset)//0 to 20 first fecth
//                    setSpacesRecyclerData()
//                    setEndlessScrollingListener()
//                    apiGetAllConversation()
//                }
//            }
//        }.start()
        return view
    }

//    /**
//     * Set Scrolling listener to recycler view
//     * to hit pagination in local database
//     */
//    private fun setEndlessScrollingListener() {
//        endlessScrolling = object : EndlessScrolling(rvConversation?.layoutManager as LinearLayoutManager?) {
//            override fun onLoadMore(currentPages: Int) {
//                offset += limit
//                if (unreadCounts >= conversationList.size) {
//                    apiGetAllConversation()
//                } else {
//                    Thread {
//                        kotlin.run {
//                            val tempList: ArrayList<Conversation> = db?.conversationDao()?.getAllConversations(limit,
//                                    offset + 1,
//                                    workspacesInfoList[currentSigninedInPosition].fuguSecretKey, "") as ArrayList<Conversation>
//                            if (tempList.size > 0) {
//                                for (conversation in tempList) {
//                                    conversationList.add(conversation)
//                                    conversationMap[conversation.channelId] = conversation
//                                }
//                                (mContext as MainActivity).runOnUiThread {
//                                    conversationAdapter?.updateList(conversationMap, workspacesInfoList[currentSigninedInPosition].userId.toLong())
//                                    inviteButtonManipulation()
//                                }
//                            }
//                        }
//                    }.start()
//
//                }
//
//            }
//
//            override fun onHide() {
//            }
//
//            override fun onShow() {
//            }
//
//        }
//        rvConversation?.addOnScrollListener(endlessScrolling)
//    }
//
//    fun hideFab(hidefab: Boolean) {
//        this.hideFab = hidefab
//        if (hideFab) {
//            fabCreateGroup?.visibility = View.GONE
//            cvSpaces?.visibility = View.VISIBLE
//            tvChoose?.visibility = View.VISIBLE
//        } else {
//            fabCreateGroup?.visibility = View.VISIBLE
//            cvSpaces?.visibility = View.GONE
//            tvChoose?.visibility = View.GONE
//        }
//    }
//
//    /**
//     * Initialize and set recycler here
//     */
//    private fun initializeViews(view: View) {
//        rvConversation = view.findViewById(R.id.rvConversation)
//        rvSpaces = view.findViewById(R.id.rvSpaces)
//        llAddSpace = view.findViewById(R.id.llAddSpace)
//        vDim = view.findViewById(R.id.vDim)
//        btnInvite = view.findViewById(R.id.btnInvite)
//        tvInvite = view.findViewById(R.id.tvInvite)
//        swipteConversation = view.findViewById(R.id.swipteConversation)
//        fabCreateGroup = view.findViewById(R.id.fabCreateGroup)
//        cvSpaces = view.findViewById(R.id.cvSpaces)
//        tvChoose = view.findViewById(R.id.tvChooseSpace)
//        conversationAdapter = ConversationAdapter(mContext as MainActivity, conversationMap,
//                workspacesInfoList[currentSigninedInPosition].fullName,
//                workspacesInfoList[currentSigninedInPosition].userId.toLong(),
//                workspacesInfoList[currentSigninedInPosition].workspaceName,
//                workspacesInfoList[currentSigninedInPosition].enUserId,
//                ConversationAdapter.Callback { FuguConversation ->
//                    val chatIntent = Intent((mContext as MainActivity), ChatActivity::class.java)
//                    val fuguConversation = FuguConversation()
//                    fuguConversation.label = FuguConversation.label
//                    fuguConversation.channelId = FuguConversation.channelId
//                    fuguConversation.channelImage = FuguConversation.channelThumbnailUrl
//                    fuguConversation.userId = workspacesInfoList[currentSigninedInPosition].userId.toLong()
//                    fuguConversation.chat_type = FuguConversation.chatType
//                    fuguConversation.last_sent_by_id = FuguConversation.lastSentById
//                    fuguConversation.unreadCount = FuguConversation.unreadCount
//                    fuguConversation.notification = FuguConversation.notification
//                    fuguConversation.membersInfo = ConversationDao.Converters.arrayListFromString(FuguConversation.membersInfo)
//                    fuguConversation.otherUserType = FuguConversation.otherUserType
//                    chatIntent.putExtra(FuguAppConstant.CONVERSATION, Gson().toJson(fuguConversation, com.skeleton.mvp.model.FuguConversation::class.java))
//                    if (hideFab) {
//                        chatIntent.putExtra("fromHome", true)
//                    }
//                    swipteConversation?.setOnRefreshListener(null)
//                    startActivityForResult(chatIntent, IS_HIT_REQUIRED)
//                })
//        workspacesSpinner = view.findViewById(R.id.spinner)
//        workspacesSpinner?.setOnItemSelectedListener(this)
//        rvConversation?.itemAnimator = null
//        rvConversation?.layoutManager = LinearLayoutManager(mContext)
//        rvConversation?.adapter = conversationAdapter
//        db = AppDatabase.getAppDatabase(mContext)
//        spacesAdapter = SpacesAdapter(spacesList, (mContext as MainActivity))
//        rvSpaces?.layoutManager = LinearLayoutManager((mContext as MainActivity))
//        rvSpaces?.adapter = spacesAdapter
//        swipteConversation?.setOnRefreshListener(this)
//        swipteConversation?.setColorSchemeColors(FuguColorConfig().fuguThemeColorPrimary)
//    }
//
//    /**
//     * fetch list from local database (Room)
//     * Update list to adapter
//     */
//    private fun getAllConversationsFromRoomAndUpdate(size: Int, offset: Int) {
//        Thread {
//            kotlin.run {
//                conversationList = db?.conversationDao()?.getAllConversations(size, offset, workspacesInfoList[currentSigninedInPosition].fuguSecretKey, "") as ArrayList<Conversation>
//                for (conversation in conversationList) {
//                    conversationMap[conversation.channelId] = conversation
//                }
//                (mContext as MainActivity).runOnUiThread {
//                    if (offset == 0) {
//                        conversationAdapter?.updateList(conversationMap, workspacesInfoList[currentSigninedInPosition].userId.toLong())
//                    } else {
//                        conversationAdapter?.addToList(conversationMap)
//                    }
//                    if (conversationList.size > 0) {
//                        inviteButtonManipulation()
//                    }
//                }
//            }
//        }.start()
//    }
//
//    /**
//     * get Conversations from server get all conversations and update local
//     */
//    fun apiGetAllConversation() {
//        val commonParams = getConversationRequestParams()
//        RestClient.getApiInterface().getConversations(workspacesInfoList[currentSigninedInPosition].fuguSecretKey,
//                1,
//                BuildConfig.VERSION_CODE,
//                commonParams.build().map).enqueue(object : ResponseResolver<FuguGetConversationsResponse>() {
//            override fun success(fuguGetConversationsResponse: FuguGetConversationsResponse?) {
//                swipteConversation?.isRefreshing = false
//                Handler().postDelayed({
//                    Thread {
//                        kotlin.run {
//                            db?.conversationDao()?.deleteAllFromSpace(workspacesInfoList[currentSigninedInPosition].fuguSecretKey)
//                            for (i in 0 until fuguGetConversationsResponse!!.data?.conversationList?.size!!) {
//                                val fuguConversation = fuguGetConversationsResponse.data?.conversationList!![i]
//                                if (i == 19) {
//                                    getAllConversationsFromRoomAndUpdate(limit, offset)//0 to 20 refreshed data
//                                }
//                                val conversation = Conversation(fuguConversation.channelId,
//                                        fuguConversation.userId, fuguConversation.last_sent_by_full_name,
//                                        fuguConversation.dateTime, fuguConversation.message,
//                                        fuguConversation.label, fuguConversation.thumbnailUrl,
//                                        fuguConversation.message_type, fuguConversation.chat_type,
//                                        fuguConversation.last_sent_by_id, fuguConversation.messageState,
//                                        fuguConversation.unreadCount, fuguConversation.muid,
//                                        fuguConversation.notifications,
//                                        ConversationDao.Converters.fromArrayListoString(fuguConversation.membersInfo as java.util.ArrayList<MembersInfo>?),
//                                        fuguConversation.customLabel, fuguConversation.last_message_status,
//                                        workspacesInfoList[currentSigninedInPosition].fuguSecretKey,
//                                        fuguConversation.calltype,
//                                        fuguConversation.otherUserType)
//                                db?.conversationDao()?.insertAll(conversation)
//                            }
//
//                            getAllConversationsFromRoomAndUpdate(limit, offset)//0 to 20 refreshed data
//                        }
//                    }.start()
//                }, 100)
//            }
//
//            override fun failure(error: APIError?) {
//                swipteConversation?.isRefreshing = false
//            }
//
//        })
//    }
//
//    /**
//     * Create Request Paramas for getConverstion api Hit
//     */
//    private fun getConversationRequestParams(): CommonParams.Builder {
//        val commonParams = CommonParams.Builder()
//        commonParams.add(FuguAppConstant.EN_USER_ID, workspacesInfoList[currentSigninedInPosition].enUserId)
//        commonParams.add(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails((mContext as MainActivity)))
//        commonParams.add(FuguAppConstant.DEVICE_TOKEN, CommonData.getFcmToken())
//        commonParams.add(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId((mContext as MainActivity)))
//        return commonParams
//    }
//
//    override fun onAttach(context: Context?) {
//        super.onAttach(context)
//        mContext = context!!
//    }
//
//    override fun onResume() {
//        super.onResume()
//        unregisterRecievers()
//        NotificationReciever.PushChannel.pushChannelId = -2L
//        LocalBroadcastManager.getInstance((mContext as MainActivity)).registerReceiver(mChannelReciever,
//                IntentFilter(CHANNEL_INTENT))
//        LocalBroadcastManager.getInstance((mContext as MainActivity)).registerReceiver(mBackgroundSending,
//                IntentFilter(BACKGROUND_SENDING_COMPLETE))
//        LocalBroadcastManager.getInstance(mContext as MainActivity).registerReceiver(mEditMessageReciever,
//                IntentFilter(FuguAppConstant.EDIT_MESSAGE_INTENT))
//        newSigninedInPosition = CommonData.getCurrentSignedInPosition()
//        if (newSigninedInPosition != currentSigninedInPosition) {
//            currentSigninedInPosition = newSigninedInPosition
//            offset = 0
//        }
//        Thread {
//            kotlin.run {
//                if (db != null) {
//                    if (offset == 0) {
//                        offset = 20
//                    }
//                    conversationList = db?.conversationDao()
//                            ?.getAllConversations(offset,
//                                    0,
//                                    workspacesInfoList[newSigninedInPosition].fuguSecretKey, "") as ArrayList<Conversation>
//                    conversationMap.clear()
//                    for (conversation in conversationList) {
//                        conversationMap[conversation.channelId] = conversation
//                        if (conversation.dateTime == "") {
//                            conversation.dateTime = DateUtils.getInstance().convertToUTC(DateUtils.getFormattedDate(Date()))
//                            db?.conversationDao()?.insertAll(conversation)
//                        }
//                    }
//                    (mContext as MainActivity).runOnUiThread {
//                        conversationAdapter?.updateList(conversationMap, workspacesInfoList[currentSigninedInPosition].userId.toLong())
//                    }
//                    inviteButtonManipulation()
//                }
//            }
//        }.start()
//        swipteConversation?.setOnRefreshListener(this);
//    }
//
//    private val mEditMessageReciever = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent) {
//            if (conversationMap[intent.getLongExtra(CHANNEL_ID, -1L)] != null && !intent.getBooleanExtra(IS_THREAD_MESSAGE, false)) {
//                val conversation = conversationMap[intent.getLongExtra(CHANNEL_ID, -1L)]!!
//                if (conversation.muid.equals(intent.getStringExtra(MESSAGE_UNIQUE_ID))) {
//                    conversation.message = intent.getStringExtra(MESSAGE)
//                    conversationMap[intent.getLongExtra(CHANNEL_ID, -1L)] = conversation
//                    conversationAdapter?.updateList(conversationMap, workspacesInfoList[currentSigninedInPosition].userId.toLong())
//                }
//            }
//        }
//    }
//
//    private fun inviteButtonManipulation() {
//        (mContext as MainActivity).runOnUiThread {
//            if (workspacesInfoList[currentSigninedInPosition].role == "OWNER" ||
//                    workspacesInfoList[currentSigninedInPosition].role == "ADMIN" ||
//                    workspacesInfoList[currentSigninedInPosition].config.anyUserCanInvite == "1") {
//                if (conversationList.size <= 4) {
//                    btnInvite?.visibility = View.VISIBLE
//                    tvInvite?.visibility = View.VISIBLE
//                } else {
//                    btnInvite?.visibility = View.GONE
//                    tvInvite?.visibility = View.GONE
//                }
//            } else {
//                btnInvite?.visibility = View.GONE
//                tvInvite?.visibility = View.GONE
//            }
//        }
//    }
//
//    fun backgroundSendingDone() {
//        endlessScrolling.setCurrentPage(0)
//        offset = 0
//        apiGetAllConversation()
//    }
//
//    /**
//     * Hit get conversation api when sending messages in background is complete
//     */
//    private var mBackgroundSending: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            apiGetAllConversation()
//        }
//    }
//
//    /**
//     * Unregister all the broadcast recievers to avoid multiple registering
//     */
//    private fun unregisterRecievers() {
//        try {
//            LocalBroadcastManager.getInstance((mContext as MainActivity)).unregisterReceiver(mChannelReciever)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        try {
//            LocalBroadcastManager.getInstance((mContext as MainActivity)).unregisterReceiver(mBackgroundSending)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        try {
//            LocalBroadcastManager.getInstance((mContext as MainActivity)).unregisterReceiver(mEditMessageReciever)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    /**
//     * Add channel whenever a user is added in the channel or self is added in a group add channel
//     */
//    private val mChannelReciever = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            Thread {
//                kotlin.run {
//                    try {
//                        conversationList = db?.conversationDao()?.getAllConversations(limit, 0,
//                                workspacesInfoList[currentSigninedInPosition].fuguSecretKey, "") as ArrayList<Conversation>
//                        conversationList.sortWith(Comparator { one, other -> other.dateTime.compareTo(one.dateTime) })
//                        conversationMap.clear()
//                        for (conversation in conversationList) {
//                            conversationMap[conversation.channelId] = conversation
//                        }
//                        (mContext as MainActivity).runOnUiThread {
//                            conversationAdapter?.updateList(conversationMap, workspacesInfoList[currentSigninedInPosition].userId.toLong())
//                            inviteButtonManipulation()
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//            }.start()
//
//        }
//    }
//
//    /**
//     * toolbar dropdwon open or closed manipulation
//     */
//    fun toolBarDropDownManipulation() {
//        try {
//            if (rvSpaces?.visibility == View.VISIBLE) {
//                rvSpaces?.visibility = View.GONE
//                llAddSpace?.visibility = View.GONE
//                vDim?.visibility = View.GONE
//            } else {
//                rvSpaces?.visibility = View.VISIBLE
//                llAddSpace?.visibility = View.VISIBLE
//                vDim?.visibility = View.VISIBLE
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    /**
//     * click listeners of other elements of the screen
//     */
//    private fun setClickListeners() {
//        vDim?.setOnClickListener {
//            toolBarDropDownManipulation()
//            (activity as MainActivity).arrowAnimation()
//        }
//        fabCreateGroup?.setOnClickListener {
//            val mIntent = Intent((mContext as MainActivity), CreateGroupSearchActivity::class.java)
//            mIntent.putExtra("fab_click", "fab_click")
//            startActivity(mIntent)
//            (mContext as MainActivity).overridePendingTransition(R.anim.right_in, R.anim.left_out)
//        }
//        btnInvite?.setOnClickListener {
//            val intent = Intent((mContext as MainActivity), InviteOnboardActivity::class.java)
//            intent.putExtra(EXTRA_ALREADY_MEMBER, EXTRA_ALREADY_MEMBER)
//            startActivity(intent)
//            (mContext as MainActivity).overridePendingTransition(R.anim.right_in, R.anim.left_out)
//        }
//        llAddSpace?.setOnClickListener {
//            if ((activity as MainActivity).isNetworkConnected) {
//                (activity as MainActivity).showLoading()
//                apiGetOpenAndInvited()
//            } else {
//                (activity as MainActivity).showErrorMessage(R.string.fugu_no_internet_connection_retry)
//            }
//        }
//    }
//
//    /**
//     * Api to get open and invited workspace before openning YouSpacesActivity
//     */
//    private fun apiGetOpenAndInvited() {
//        com.skeleton.mvp.data.network.RestClient.getApiInterface(true).getOpenAndInvited(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), BuildConfig.VERSION_CODE, FuguAppConstant.ANDROID)
//                .enqueue(object : com.skeleton.mvp.data.network.ResponseResolver<OpenAndInvited>() {
//                    override fun onSuccess(openAndInvited: OpenAndInvited) {
//                        CommonData.getCommonResponse().getData().invitationToWorkspaces = openAndInvited.data.invitationToWorkspaces
//                        CommonData.getCommonResponse().getData().openWorkspacesToJoin = openAndInvited.data.openWorkspacesToJoin
//                        (activity as MainActivity).hideLoading()
//                        if (CommonData.getCommonResponse().getData().openWorkspacesToJoin.size + CommonData.getCommonResponse().getData().invitationToWorkspaces.size > 0) {
//                            val intent = Intent((mContext as MainActivity), YourSpacesActivity::class.java)
//                            intent.putExtra(AppConstants.EXTRA_ALREADY_MEMBER, AppConstants.EXTRA_ALREADY_MEMBER)
//                            startActivity(intent)
//                        } else {
//                            val intent = Intent((mContext as MainActivity), SignUpActivity::class.java)
//                            intent.putExtra(AppConstants.EXTRA_ALREADY_MEMBER, AppConstants.EXTRA_ALREADY_MEMBER)
//                            startActivity(intent)
//                        }
//                    }
//
//                    override fun onError(error: ApiError) {
//                        (activity as MainActivity).hideLoading()
//                    }
//
//                    override fun onFailure(throwable: Throwable) {
//                        (activity as MainActivity).hideLoading()
//                    }
//                })
//    }
//
//    /**
//     * Choose a business on selection of that particulat business from spaces recycler
//     */
//    fun selectBusiness() {
//        toolBarDropDownManipulation()
//    }
//
//    /**
//     * Choose a business on selection of that particulat business from spaces recycler
//     */
//    fun changeBusiness(position: Int, isAnimation: Boolean, changeBusiness: ChangeBusiness) {
//        if (position != currentSigninedInPosition) {
//            (activity!! as MainActivity).showLoading()
//            val commonParams = com.skeleton.mvp.data.network.CommonParams.Builder()
//                    .add(AppConstants.WORKSPACE_ID, CommonData.getCommonResponse().getData().workspacesInfo[position].workspaceId)
//                    .add(AppConstants.TOKEN, CommonData.getFcmToken())
//                    .add(FuguAppConstant.DEVICE_ID, UniqueIMEIID.getUniqueIMEIId((mContext as MainActivity)))
//                    .add(FuguAppConstant.DEVICE_DETAILS, CommonData.deviceDetails((mContext as MainActivity)))
//                    .build()
//
//            com.skeleton.mvp.data.network.RestClient.getApiInterface(true).switchWorkspace(CommonData.getCommonResponse().data.userInfo.accessToken, BuildConfig.VERSION_CODE, AppConstants.ANDROID, commonParams.map)
//                    .enqueue(object : ResponseResolver<com.skeleton.mvp.data.model.CommonResponse>() {
//                        override fun success(t: com.skeleton.mvp.data.model.CommonResponse?) {
//                            Thread {
//                                currentSigninedInPosition = position
//                                CommonData.setCurrentSignedInPosition(position)
//                                conversationMap.clear()
//                                conversationList = db?.conversationDao()?.getAllConversations(limit,
//                                        0, workspacesInfoList[currentSigninedInPosition].fuguSecretKey, "") as ArrayList<Conversation>
//                                if (conversationList.size > 0) {
//                                    inviteButtonManipulation()
//                                }
//                                if (conversationList.size > 0) {
//                                    for (conversation in conversationList) {
//                                        conversationMap[conversation.channelId] = conversation
//                                    }
//                                } else {
//                                    offset = 0
//                                }
//                                (mContext as MainActivity).runOnUiThread {
//                                    conversationAdapter?.updateList(conversationMap, workspacesInfoList[currentSigninedInPosition].userId.toLong())
//                                    Handler().postDelayed({
//                                        (activity!! as MainActivity).hideLoading()
//                                        if (isAnimation) {
//                                            toolBarDropDownManipulation()
//                                        }
//                                        changeBusiness.changeBusinessSuccess()
//                                    }, 500)
//                                    if (conversationList.size == 0) {
//                                        offset = 0
//                                    }
//                                    apiGetAllConversation()
//                                }
//                            }.start()
//                        }
//
//                        override fun failure(error: APIError?) {
//
//                        }
//
//                    })
//
//        }
//    }
//
//    /**
//     * Clear chat from channel options dialog delete chat option
//     * remove the item from conversation map and from local database
//     */
//    fun apiClearChat(muid: String, channelId: Long?) {
//        val commonParams = com.skeleton.mvp.retrofit.CommonParams.Builder()
//                .add(FuguAppConstant.EN_USER_ID, CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].enUserId)
//                .add("muid", muid)
//                .add(FuguAppConstant.CHANNEL_ID, channelId)
//                .build()
//        com.skeleton.mvp.retrofit.RestClient.getApiInterface().clearChatHistory(workspacesInfoList[currentSigninedInPosition].fuguSecretKey,
//                1, BuildConfig.VERSION_CODE, commonParams.map)
//                .enqueue(object : com.skeleton.mvp.retrofit.ResponseResolver<com.skeleton.mvp.retrofit.CommonResponse>((mContext as MainActivity), true, false) {
//                    override fun success(commonResponse: com.skeleton.mvp.retrofit.CommonResponse) {
//                        ChatDatabase.setMessageList(java.util.ArrayList(), channelId)
//                        ChatDatabase.setMessageMap(java.util.LinkedHashMap(), channelId)
//                        ChatDatabase.setUnsentMessageMapByChannel(channelId, java.util.LinkedHashMap())
//                        getFromSdcardAndDelete(channelId!!)
//                        conversationMap.remove(channelId)
//                        Thread {
//                            kotlin.run {
//                                db?.conversationDao()?.deleteChannel(channelId)
//                                (mContext as MainActivity).runOnUiThread {
//                                    conversationAdapter?.updateList(conversationMap, workspacesInfoList[currentSigninedInPosition].userId.toLong())
//                                    inviteButtonManipulation()
//                                }
//                            }
//                        }.start()
//
//                    }
//
//                    override fun failure(error: APIError) {
//
//                    }
//                })
//    }
//
//    private fun getFromSdcardAndDelete(channelId: Long) {
//        var listFile: Array<File>? = null
//        try {
//            val file = File(Environment.getExternalStorageDirectory(), FuguAppConstant.APP_NAME_SHORT  +
//                    File.separator + workspacesInfoList.get(currentSigninedInPosition).workspaceName?.replace(" ".toRegex(), "")?.replace("'s".toRegex(), "")
//                    + File.separator + FuguAppConstant.IMAGE)
//            if (file.isDirectory) {
//                listFile = file.listFiles()
//
//                for (i in listFile!!.indices) {
//                    val exifFile = ExifInterface(listFile[i].absolutePath)
//                    Log.e(exifFile.getAttribute(ExifInterface.TAG_MAKE), channelId.toString())
//                    if (!TextUtils.isEmpty(exifFile.getAttribute(ExifInterface.TAG_MAKE)) && exifFile.getAttribute(ExifInterface.TAG_MAKE)!!.contains(channelId.toString())) {
//                        listFile.get(i).delete()
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    /**
//     * Clear chat from channel options dialog leave group option
//     * remove the item from conversation map and from local database
//     */
//    fun removeChat(channelId: Long?) {
//        conversationMap.remove(channelId)
//        Thread {
//            kotlin.run {
//                db?.conversationDao()?.deleteChannel(channelId)
//                (mContext as MainActivity).runOnUiThread {
//                    conversationAdapter?.updateList(conversationMap, workspacesInfoList[currentSigninedInPosition].userId.toLong())
//                    inviteButtonManipulation()
//                }
//            }
//        }.start()
//    }
//
//    /**
//     * Mute/Unmute chat from channel options dialog mute/unmute group option
//     */
//    fun muteGroup(channelId: Long?, isMuted: String) {
//        if (conversationMap[channelId] != null) {
//            val conversation = conversationMap[channelId]
//            conversation?.notification = isMuted
//            Thread {
//                kotlin.run {
//                    db?.conversationDao()?.insertAll(conversation)
//                }
//            }.start()
//            conversationAdapter?.updateList(conversationMap, workspacesInfoList[currentSigninedInPosition].userId.toLong())
//            inviteButtonManipulation()
//        }
//    }
//
//    /**
//     * Interface to change the business i.e switch workspace
//     */
//    interface ChangeBusiness {
//        fun changeBusinessSuccess()
//    }
//
//    /**
//     * Update spaces list with refreshed data after loginViaAccessToken
//     */
//    fun updateSpacesList() {
//        Thread {
//            kotlin.run {
//                workspacesInfoList = CommonData.getCommonResponse().data.workspacesInfo as ArrayList<WorkspacesInfo>
//                setSpacesRecyclerData()
//            }
//        }.start()
//    }
//
//    /**
//     * Set spaces recycler view
//     */
//    private fun setSpacesRecyclerData() {
//        val spacesDropdownList = ArrayList<String>()
//        Thread {
//            kotlin.run {
//                spacesList.clear()
//                for (workspaceInfo in workspacesInfoList) {
//                    spacesList.add(Space(workspaceInfo.workspaceName,
//                            workspaceInfo.currentLogin,
//                            workspaceInfo.workspaceId,
//                            false))
//                    spacesDropdownList.add(workspaceInfo.workspaceName)
//                }
//
//                try {
//                    (activity as MainActivity).runOnUiThread {
//                        if (hideFab) {
//                            fabCreateGroup?.visibility = View.GONE
//                            cvSpaces?.visibility = View.VISIBLE
//                            tvChoose?.visibility = View.VISIBLE
//                        } else {
//                            fabCreateGroup?.visibility = View.VISIBLE
//                            cvSpaces?.visibility = View.GONE
//                            tvChoose?.visibility = View.GONE
//                        }
//                    }
//                } catch (e: Exception) {
//
//                }
//
//                if (spacesDropdownList.size > 1 && hideFab) {
//                    try {
//                        (activity as MainActivity).runOnUiThread {
//                            cvSpaces?.visibility = View.VISIBLE
//                            tvChoose?.visibility = View.VISIBLE
//                        }
//                        (activity as MainActivity).runOnUiThread {
//                            val aa = ArrayAdapter(activity, android.R.layout.simple_spinner_dropdown_item, spacesDropdownList)
//                            workspacesSpinner?.setAdapter(aa)
//                            workspacesSpinner?.setSelection(CommonData.getCurrentSignedInPosition())
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                } else {
//                    try {
//                        (activity as MainActivity).runOnUiThread {
//                            cvSpaces?.visibility = View.GONE
//                            tvChoose?.visibility = View.GONE
//                        }
//                    } catch (e: Exception) {
//
//                    }
//
//                }
//                try {
//                    (activity as MainActivity).runOnUiThread {
//                        spacesAdapter?.updateList(spacesList)
//                        spacesAdapter?.notifyDataSetChanged()
//                        if (spacesList.size <= 4) {
//                            rvSpaces?.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
//                        } else {
//                            rvSpaces?.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, dpToPx(310))
//                        }
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//
//
//            }
//        }.start()
//    }
//
//    override fun onRefresh() {
//        swipteConversation?.isRefreshing = true
//        endlessScrolling.setCurrentPage(0)
//        offset = 0
//        apiGetAllConversation()
//    }
//
//    private fun dpToPx(dpParam: Int): Int {
//        val d = mContext?.resources?.displayMetrics?.density
//        return (dpParam * d!!).toInt()
//    }
//
//
//    fun navigateToTopOfScreen() {
//        try {
//            rvConversation?.scrollToPosition(0)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
}