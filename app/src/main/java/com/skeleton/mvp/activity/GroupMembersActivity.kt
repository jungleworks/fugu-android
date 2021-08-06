package com.skeleton.mvp.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.adapter.MembersAdapter
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.CHANNEL_ID
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.model.GroupMember
import com.skeleton.mvp.model.editInfo.EditInfoResponse
import com.skeleton.mvp.model.group.GroupResponse
import com.skeleton.mvp.model.media.ChatMember
import com.skeleton.mvp.model.media.MediaResponse
import com.skeleton.mvp.model.userSearch.User
import com.skeleton.mvp.model.userSearch.UserSearch
import com.skeleton.mvp.retrofit.*
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.ui.profile.ProfileActivity
import com.skeleton.mvp.util.Log
import com.skeleton.mvp.utils.EndlessScrolling
import org.json.JSONArray

class GroupMembersActivity : BaseActivity() {
    private var rvMembers: androidx.recyclerview.widget.RecyclerView? = null
    private var membersAdapter: MembersAdapter? = null
    private var membersList: ArrayList<GroupMember>? = null
    private var currentUser: ChatMember? = null
    private var isJoined: Boolean = false
    private var channelId: Long = -1L
    private var workspaceInfo: WorkspacesInfo? = null
    private var isAnyInfoChanged = false
    private var mCountDownTimer: CountDownTimer? = null
    private var etSearchMembers: EditText? = null
    private var ivCross: AppCompatImageView? = null
    private var ivBack: AppCompatImageView? = null
    private var userCount = 0L
    private var userPageSize = 0
    private var pageStart = 0
    private var chatType = FuguAppConstant.ChatType.PUBLIC_GROUP
    private var endlessScrolling: EndlessScrolling? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_members)
        fetchIntentData()
        rvMembers = findViewById(R.id.rvMembers)
        rvMembers?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        membersAdapter = MembersAdapter(membersList, this, channelId, isJoined, currentUser, true, userCount)
        rvMembers?.adapter = membersAdapter
        etSearchMembers = findViewById(R.id.etSearchMembers)
        ivCross = findViewById(R.id.ivCross)
        ivBack = findViewById(R.id.ivBack)
        addListeners()
        if (userCount > userPageSize) {
            setEndlessScrolling()
        }
    }


    private fun setEndlessScrolling() {
        try {
            if (endlessScrolling == null) {
                endlessScrolling = object : EndlessScrolling(rvMembers?.layoutManager as androidx.recyclerview.widget.LinearLayoutManager) {
                    override fun onLoadMore(currentPages: Int) {
                        pageStart += userPageSize
                        apiGetGroupInfo()
                    }

                    override fun onHide() {
                    }

                    override fun onShow() {
                    }
                }
                rvMembers?.addOnScrollListener(endlessScrolling!!)
            }
        } catch (e: Exception) {
        }
    }

    private fun apiGetGroupInfo() {
        val commonParams = CommonParams.Builder()
        commonParams.add(FuguAppConstant.CHANNEL_ID, channelId)
        commonParams.add(FuguAppConstant.EN_USER_ID, workspaceInfo?.enUserId)
        commonParams.add(FuguAppConstant.GET_DATA_TYPE, FuguAppConstant.MEMBERS)
        commonParams.add(FuguAppConstant.USER_PAGE_START, pageStart)
        RestClient.getApiInterface().getGroupInfo(
                CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                workspaceInfo?.fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                .enqueue(object : ResponseResolver<MediaResponse>() {
                    @SuppressLint("SetTextI18n")
                    override fun success(mediaResponse: MediaResponse?) {
                        Log.e("Size", mediaResponse?.data?.chatMembers?.size.toString())
                        for (member in mediaResponse?.data?.chatMembers!!) {
                            if (chatType == FuguAppConstant.ChatType.GENERAL_GROUP || chatType == FuguAppConstant.ChatType.DEFAULT_GROUP) {
                                membersList?.add(GroupMember(member.fullName, member.userId!!.toLong(),
                                        member.email, member.userImage, member.email, "USER", 0))
                            } else {
                                membersList?.add(GroupMember(member.fullName, member.userId!!.toLong(),
                                        member.email, member.userImage, member.email, member.role, 0))
                            }
                        }
                        membersAdapter?.updateList(membersList)
                        membersAdapter?.notifyDataSetChanged()
                    }

                    override fun failure(error: APIError?) {
                        Log.e("Error", error?.message)

                    }

                })
    }

    private fun addListeners() {
        ivCross?.setOnClickListener {
            etSearchMembers?.setText("")
        }
        ivBack?.setOnClickListener {
            onBackPressed()
        }

        etSearchMembers?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s.toString().isNotEmpty()) {
                    ivCross?.visibility = View.VISIBLE
                } else {
                    ivCross?.visibility = View.GONE
                }

                try {
                    mCountDownTimer?.cancel()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                mCountDownTimer = object : CountDownTimer(200, 100) {
                    override fun onFinish() {
                        if (s.toString().isNotEmpty()) {
                            if (userCount < userPageSize) {
                                val filteredList = ArrayList<GroupMember>()
                                for (member in membersList!!) {
                                    if (member.name.toLowerCase().contains(s.toString().toLowerCase())) {
                                        filteredList.add(member)
                                    }
                                }
                                membersAdapter?.updateList(filteredList)
                                membersAdapter?.notifyDataSetChanged()
                            } else {
                                if (s.toString().length > 2) {
                                    apiUserSearch(s.toString())
                                }
                            }
                        } else {
                            membersAdapter?.updateList(membersList)
                            membersAdapter?.notifyDataSetChanged()
                            setEndlessScrolling()
                        }
                    }

                    override fun onTick(millisUntilFinished: Long) {
                    }

                }
                mCountDownTimer?.start()
            }

        })
    }

    private fun apiUserSearch(text: String) {

        val workspaceInfo = CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()]
        val commonParams = CommonParams.Builder()
                .add("en_user_id", workspaceInfo.enUserId)
                .add("search_text", text)
                .add("channel_id", channelId)
        com.skeleton.mvp.data.network.RestClient.getApiInterface(true).userSearch(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), workspaceInfo.fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                .enqueue(object : com.skeleton.mvp.data.network.ResponseResolver<UserSearch>() {
                    override fun onSuccess(userSearch: UserSearch?) {
                        if (endlessScrolling != null) {
                            rvMembers?.removeOnScrollListener(endlessScrolling!!)
                            endlessScrolling = null
                        }
                        val filteredSearchedMemberArrayList = ArrayList<GroupMember>()
                        filteredSearchedMemberArrayList.clear()
                        for (i: Int in userSearch?.data?.users!!.indices) {

                            val user: User = userSearch.data.users[i]

                            if (chatType == FuguAppConstant.ChatType.GENERAL_GROUP || chatType == FuguAppConstant.ChatType.DEFAULT_GROUP) {
                                filteredSearchedMemberArrayList.add(GroupMember(user.fullName, user.userId!!.toLong(),
                                        user.email, user.userImage, user.email, "USER", 0))
                            } else {
                                filteredSearchedMemberArrayList.add(GroupMember(user.fullName, user.userId!!.toLong(),
                                        user.email, user.userImage, user.email, user.role, 0))
                            }
                        }
                        membersAdapter?.updateList(filteredSearchedMemberArrayList)
                        membersAdapter?.notifyDataSetChanged()
                    }

                    override fun onError(error: ApiError?) {
                    }

                    override fun onFailure(throwable: Throwable?) {
                    }

                })

    }

    private fun fetchIntentData() {
        workspaceInfo = CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()]
        membersList = intent.getSerializableExtra("membersList") as ArrayList<GroupMember>
        try {
            currentUser = intent.getSerializableExtra("currentUser") as ChatMember
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isJoined = intent.getBooleanExtra("isJoined", false)
        channelId = intent.getLongExtra(CHANNEL_ID, -1L)
        userCount = intent.getLongExtra("userCount", -1L)
        userPageSize = intent.getIntExtra("userPageSize", 0)
        chatType = intent.getIntExtra("chatType", 0)

    }

    fun dismissGroupAdmin(userId: Long?, pos: Int?) {
        showLoading()
        val userIds = ArrayList<Long>()
        userIds.add(userId!!)
        val userIdsArray = JSONArray(userIds)
        val commonParams = MultipartParams.Builder()
        commonParams.add(FuguAppConstant.CHANNEL_ID, channelId)
        commonParams.add(FuguAppConstant.USER_IDS_TO_REMOVE_ADMIN, userIdsArray)
        commonParams.add(FuguAppConstant.EN_USER_ID, workspaceInfo?.enUserId)
        RestClient.getApiInterface().editChannelInfo(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                workspaceInfo?.fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                .enqueue(object : ResponseResolver<EditInfoResponse>() {
                    override fun success(t: EditInfoResponse?) {
                        isAnyInfoChanged = true
                        val membersList = membersAdapter?.items
                        membersList?.get(pos!!)?.role = FuguAppConstant.Role.USER.toString()
                        membersAdapter?.updateList(membersList)
                        membersAdapter?.notifyDataSetChanged()
                        hideLoading()
                    }

                    override fun failure(error: APIError?) {
                        hideLoading()
                    }

                })
    }

    fun makeGroupAdmin(userId: Long?, pos: Int?) {
        showLoading()
        val userIds = ArrayList<Long>()
        userIds.add(userId!!)
        val userIdsArray = JSONArray(userIds)
        val commonParams = MultipartParams.Builder()
        commonParams.add(FuguAppConstant.CHANNEL_ID, channelId)
        commonParams.add(FuguAppConstant.EN_USER_ID, workspaceInfo?.enUserId)
        commonParams.add(FuguAppConstant.USER_IDS_TO_MAKE_ADMIN, userIdsArray)
        RestClient.getApiInterface().editChannelInfo(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                workspaceInfo?.fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                .enqueue(object : ResponseResolver<EditInfoResponse>() {
                    override fun success(t: EditInfoResponse?) {
                        isAnyInfoChanged = true
                        val membersList = membersAdapter?.items
                        membersList?.get(pos!!)?.role = FuguAppConstant.Role.ADMIN.toString()
                        membersAdapter?.updateList(membersList)
                        membersAdapter?.notifyDataSetChanged()
                        hideLoading()
                    }

                    override fun failure(error: APIError?) {
                        hideLoading()
                    }

                })
    }

    fun removeMember(userId: Long?, pos: Int?, name: String) {
        AlertDialog.Builder(this@GroupMembersActivity)
                .setMessage("Do you want to remove $name from the group?")
                .setPositiveButton("No", null)
                .setNegativeButton("Yes") { dialogInterface, i ->
                    val commonParams = CommonParams.Builder()
                            .add("user_id_to_remove", userId)
                            .add(FuguAppConstant.EN_USER_ID, workspaceInfo?.enUserId)
                            .add(CHANNEL_ID, channelId)
                            .build()
                    showLoading()
                    RestClient.getApiInterface().removeMember(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(),
                            com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey,
                            1, BuildConfig.VERSION_CODE, commonParams.map)
                            .enqueue(object : ResponseResolver<GroupResponse>(this@GroupMembersActivity, true, false) {
                                @SuppressLint("SetTextI18n")
                                override fun success(groupResponse: GroupResponse) {
                                    val membersList = membersAdapter?.items
                                    isAnyInfoChanged = true
                                    hideLoading()
                                    membersList?.removeAt(pos!!)
                                    membersAdapter?.notifyDataSetChanged()
                                    com.skeleton.mvp.fugudatabase.CommonData.setGroupMembers(membersList)
                                }

                                override fun failure(error: APIError) {
                                    hideLoading()
                                }
                            })
                }
                .show()
    }

    fun viewProfile(userId: Long?) {
        val mIntent = Intent(Intent(this@GroupMembersActivity, ProfileActivity::class.java))
        mIntent.putExtra("open_profile", userId!!.toString() + "")
        if (userId.compareTo(java.lang.Long.valueOf(workspaceInfo!!.userId)) == 0) {
            mIntent.putExtra("no_chat", "no_chat")
        }
        startActivity(mIntent)
    }

    override fun onBackPressed() {
        if (isAnyInfoChanged) {
            val intent = Intent()
            intent.putExtra("membersList", membersList)
            setResult(Activity.RESULT_OK, intent)
        }
        super.onBackPressed()
    }
}
