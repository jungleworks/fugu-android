package com.skeleton.mvp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.TextView
import com.mikepenz.itemanimators.ScaleUpAnimator
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.adapter.AllMemberAdapter
import com.skeleton.mvp.adapter.MultiMemberAddGroupAdapter
import com.skeleton.mvp.adapter.MultiMemberAddGuestInviteAdapter
//import com.skeleton.mvp.adapter.MultiMemberAddGuestInviteAdapter
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.REQ_CODE_INVITE_GUEST
import com.skeleton.mvp.data.model.creategroup.MembersInfo
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.interfaces.RecyclerViewAddedMembers
import com.skeleton.mvp.interfaces.UpdateAllMemberCallback
import com.skeleton.mvp.model.FuguSearchResult
import com.skeleton.mvp.model.GetAllMembers
import com.skeleton.mvp.model.GroupMember
import com.skeleton.mvp.model.getAllMembers.AllMember
import com.skeleton.mvp.model.getAllMembers.AllMemberResponse
import com.skeleton.mvp.model.searchgroupuser.SearchUserResponse
import com.skeleton.mvp.model.searchgroupuser.User
import com.skeleton.mvp.model.userSearch.UserSearch
import com.skeleton.mvp.retrofit.APIError
import com.skeleton.mvp.retrofit.CommonParams
import com.skeleton.mvp.retrofit.ResponseResolver
import com.skeleton.mvp.retrofit.RestClient
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.util.SearchAnimationToolbar
import com.skeleton.mvp.videoCall.FuguCallActivity

class GuestContactsAccessActivity : BaseActivity(), SearchAnimationToolbar.OnSearchQueryChangedListener, RecyclerViewAddedMembers {

    private var searchToolbar: SearchAnimationToolbar? = null
    private var ivBack: AppCompatImageView? = null
    private var rvaddedMembers: androidx.recyclerview.widget.RecyclerView? = null
    private var rvSearchresults: androidx.recyclerview.widget.RecyclerView? = null
    private var allMemberMap = HashMap<Long, GetAllMembers>()
    private var allMembersArrayList = ArrayList<GetAllMembers>()
    private var allMemberAdapter: AllMemberAdapter? = null
    private var multiMembersAddGroupAdapter: MultiMemberAddGuestInviteAdapter? = null
    private var userIdsSearch = ArrayList<Long>()
    private var multiMemberAddGroupMap = java.util.LinkedHashMap<Long, GetAllMembers>()
    private var fabNext: FloatingActionButton? = null
    private var tvTitle: TextView? = null
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest_contacts_access)
        searchToolbar = findViewById(R.id.searchToolbar)
        fabNext = findViewById(R.id.fabNext)
        ivBack = findViewById(R.id.ivBack)
        rvaddedMembers = findViewById(R.id.rvaddedMembers)
        rvSearchresults = findViewById(R.id.rvSearchresults)
        searchToolbar?.searchToolbar
        searchToolbar?.setSupportActionBar(this)
        searchToolbar?.setOnSearchQueryChangedListener(this)

        tvTitle = findViewById(R.id.tvTitle)

        fabNext?.setOnClickListener {
            val guestIntent = Intent(this@GuestContactsAccessActivity, GuestGroupsAccessActivity::class.java)
            guestIntent.putExtra("userIdsArray", userIdsSearch)
            guestIntent.putExtra("emailArray", intent.getSerializableExtra("emailArray"))
            guestIntent.putExtra("phoneArray", intent.getSerializableExtra("phoneArray"))
            guestIntent.putExtra("countryCodeArray", intent.getSerializableExtra("countryCodeArray"))
            startActivityForResult(guestIntent, REQ_CODE_INVITE_GUEST)
        }
        ivBack?.setOnClickListener {
            onBackPressed()
        }
        apiGetAllMembers()
        Thread {
            kotlin.run {
                if (CommonData.getPaperAllMembersMap(com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey) != null) {
                    allMemberMap = CommonData.getPaperAllMembersMap(com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey)
                }

                runOnUiThread {
                    allMembersArrayList = ArrayList(allMemberMap.values)
                    val myData = com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()]
                    multiMemberAddGroupMap.put(myData.userId.toLong(), GetAllMembers(myData.userId.toLong(), myData.fullName, "", myData.userImage, myData.userImage, "USER", 100, "1",""))
                    rvSearchresults?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@GuestContactsAccessActivity)
                    allMemberAdapter = AllMemberAdapter(allMembersArrayList, this@GuestContactsAccessActivity, userIdsSearch)
                    rvSearchresults?.adapter = allMemberAdapter
                    multiMembersAddGroupAdapter = MultiMemberAddGuestInviteAdapter(multiMemberAddGroupMap, this@GuestContactsAccessActivity)
                    rvaddedMembers?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@GuestContactsAccessActivity, androidx.recyclerview.widget.RecyclerView.HORIZONTAL, false)
                    rvaddedMembers?.itemAnimator = ScaleUpAnimator()
                    rvaddedMembers?.adapter = multiMembersAddGroupAdapter
                    userIdsSearch = ArrayList(multiMemberAddGroupMap.keys)
                    rvSearchresults?.itemAnimator = null
                }
            }
        }.start()
    }
    private fun apiGetAllMembers() {
        val commonParams: com.skeleton.mvp.data.network.CommonParams.Builder = com.skeleton.mvp.data.network.CommonParams.Builder()
        commonParams.add("workspace_id", com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].workspaceId)
        commonParams.add("user_type", "ALL_MEMBERS")
        commonParams.add("user_status", "ENABLED")
        commonParams.add("include_user_guests", true)
        commonParams.add(FuguAppConstant.PAGE_START, 1)
        com.skeleton.mvp.data.network.RestClient.getApiInterface(true).getAllMembers(com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.accessToken, BuildConfig.VERSION_CODE, FuguAppConstant.ANDROID, commonParams.build().map)
                .enqueue(object : com.skeleton.mvp.data.network.ResponseResolver<AllMemberResponse>() {
                    override fun onSuccess(allMemberResponse: AllMemberResponse) {
                            allMemberMap = LinkedHashMap()
                            allMembersArrayList = ArrayList()
                        var myEmail = com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.email
                        var myNumber = com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.contactNumber
                        var myUserId = com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo.get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).userId.toLong()
                        for (i: Int in allMemberResponse.data.allMemberResponse.indices) {
                            val allMember: AllMember = allMemberResponse.data.allMemberResponse[i]
                            if (allMember.fuguUserId != null && (!TextUtils.isEmpty(allMember.email) || !TextUtils.isEmpty(allMember.contactNumber) || !TextUtils.isEmpty(allMember.fullName))) {
                                if (myUserId.compareTo(allMember.fuguUserId.toLong()) != 0) {
                                    val getAllMembers = GetAllMembers(allMember.fuguUserId, allMember.fullName,
                                            allMember.email,
                                            allMember.userImage,
                                            allMember.userThumbnailImage,
                                            allMember.role,
                                            0, allMember.contactNumber,allMember.leaveType)
                                    allMemberMap.put(allMember.fuguUserId, getAllMembers)
                                }


                            }
                        }
                        allMembersArrayList = ArrayList(allMemberMap.values)
                        allMemberAdapter?.updateList(allMembersArrayList, userIdsSearch)
                        allMemberAdapter?.notifyDataSetChanged()
                        Thread {
                            kotlin.run {
                                CommonData.setPaperAllMembersMap(allMemberMap, com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey)
                            }
                        }.start()

                    }

                    override fun onError(error: ApiError?) {
                    }

                    override fun onFailure(throwable: Throwable?) {
                    }
                })
    }
    override fun onSearchCollapsed() {
        ivBack?.visibility = View.VISIBLE
    }

    override fun onSearchQueryChanged(query: String?) {
        if (query!!.isNotEmpty() && query.toString().length > 1) {
            countDownTimer?.cancel()
            countDownTimer = object : CountDownTimer(300, 100) {
                override fun onFinish() {
                    apiSearchMember(query)
                }

                override fun onTick(millisUntilFinished: Long) {
                }
            }.start()

        } else {
            allMemberAdapter?.updateList(allMembersArrayList, userIdsSearch)
            allMemberAdapter?.notifyDataSetChanged()
        }
    }

    override fun onSearchExpanded() {
        ivBack?.visibility = View.GONE
    }

    override fun onSearchSubmitted(query: String?) {
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val itemId = item.itemId

        if (itemId == R.id.action_search) {
            searchToolbar?.onSearchIconClick()
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    fun addMemberToList(getAllMembers: GetAllMembers) {
        userIdsSearch.add(getAllMembers.userId)
        multiMemberAddGroupMap.put(getAllMembers.userId, getAllMembers)
        multiMembersAddGroupAdapter?.updateList(multiMemberAddGroupMap)
        rvaddedMembers?.visibility = View.VISIBLE
        rvaddedMembers?.post {
            try {
                rvaddedMembers?.smoothScrollToPosition(multiMembersAddGroupAdapter?.itemCount!! - 1)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        multiMembersAddGroupAdapter?.notifyItemInserted(allMemberMap.size - 1)
    }

    fun updateAllMemberAdapter(userId: Long) {
        if (userIdsSearch.contains(userId)) {
            userIdsSearch.remove(userId)
            if (userIdsSearch.size == 0) {
                rvaddedMembers?.visibility = View.GONE
            }
        }
        allMemberAdapter?.updateList(allMembersArrayList, userIdsSearch)
        allMemberAdapter?.notifyDataSetChanged()
    }

    private fun apiSearchMember(query: String) {
        val commonParams = CommonParams.Builder()
                .add(FuguAppConstant.EN_USER_ID, com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].enUserId)
                .add("search_text", query)
                .build()
        com.skeleton.mvp.data.network.RestClient.getApiInterface(true).userSearch(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo.get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.map).enqueue(object : ResponseResolver<UserSearch>() {
            override fun success(searchUserResponse: UserSearch) {
                val filteredList = ArrayList<GetAllMembers>()
                val user: List<com.skeleton.mvp.model.userSearch.User> = searchUserResponse.data.users
                for (i: Int in user.indices) {
                    if (java.lang.Long.valueOf(user[i].userId!!.toLong()).compareTo(java.lang.Long.valueOf(com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userId)) != 0) {
                        filteredList.add(GetAllMembers(user[i].userId.toLong(), user[i].fullName,
                                user[i].email,
                                user[i].userImage,
                                user[i].userImage,
                                "", 0, "",""))
                    }
                }


                allMemberAdapter?.updateList(filteredList, userIdsSearch)
                allMemberAdapter?.notifyDataSetChanged()
            }

            override fun failure(error: APIError?) {
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQ_CODE_INVITE_GUEST) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }


    override fun recyclerViewAddedMembersCallback(getAllMembers: GetAllMembers) {
        userIdsSearch = ArrayList(multiMemberAddGroupMap.keys)
        if (multiMemberAddGroupMap.size != 0) {
            if (multiMemberAddGroupMap[getAllMembers.userId] != null) {
                multiMemberAddGroupMap.remove(getAllMembers.userId)
                multiMembersAddGroupAdapter?.updateList(multiMemberAddGroupMap)
                userIdsSearch.remove(getAllMembers.userId)
                if (userIdsSearch.size == 0) {
                    rvaddedMembers?.visibility = View.GONE
                }
                multiMembersAddGroupAdapter?.notifyDataSetChanged()
                rvaddedMembers?.post {
                    try {
                        rvaddedMembers?.smoothScrollToPosition(multiMembersAddGroupAdapter?.itemCount!! - 1)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                addMemberToList(getAllMembers)
            }
        } else {
            addMemberToList(getAllMembers)
        }
    }
}
