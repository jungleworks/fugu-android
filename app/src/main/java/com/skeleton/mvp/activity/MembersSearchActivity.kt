package com.skeleton.mvp.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mikepenz.itemanimators.ScaleUpAnimator
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.adapter.AllMemberAdapter
import com.skeleton.mvp.adapter.MultiMemberAddGroupAdapter
import com.skeleton.mvp.constant.FuguAppConstant.ANDROID
import com.skeleton.mvp.constant.FuguAppConstant.PAGE_START
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.data.network.CommonParams
import com.skeleton.mvp.data.network.ResponseResolver
import com.skeleton.mvp.data.network.RestClient
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.interfaces.RecyclerViewAddedMembers
import com.skeleton.mvp.interfaces.UpdateAllMemberCallback
import com.skeleton.mvp.model.GetAllMembers
import com.skeleton.mvp.model.getAllMembers.AllMember
import com.skeleton.mvp.model.getAllMembers.AllMemberResponse
import com.skeleton.mvp.model.userSearch.User
import com.skeleton.mvp.model.userSearch.UserSearch
import com.skeleton.mvp.ui.AppConstants.EXTRA_CREATE_GROUP_FROM_SEARCH
import com.skeleton.mvp.ui.AppConstants.SELECTED_MEMBERS
import com.skeleton.mvp.ui.creategroup.CreateGroupActivity
import com.skeleton.mvp.util.SearchAnimationToolbar
import com.skeleton.mvp.utils.EndlessScrolling


class MembersSearchActivity : AppCompatActivity(), SearchAnimationToolbar.OnSearchQueryChangedListener, UpdateAllMemberCallback, RecyclerViewAddedMembers {

    lateinit var etSearchMember: EditText
    lateinit var ivBack: ImageView
    lateinit var rvaddedMembers: RecyclerView
    lateinit var tvMostSearched: TextView
    lateinit var rvSearchresults: RecyclerView
    private var allMemberMap = HashMap<Long, GetAllMembers>()
    private var allMembersArrayList = ArrayList<GetAllMembers>()
    lateinit var allMemberAdapter: AllMemberAdapter
    lateinit var multiMembersAddGroupAdapter: MultiMemberAddGroupAdapter
    private var userIdsSearch = ArrayList<Long>()
    private var selectedMembers = java.util.LinkedHashMap<Long, GetAllMembers>()
    private lateinit var searchToolbar: SearchAnimationToolbar
    private lateinit var vDim: View
    private lateinit var fabNext: FloatingActionButton
    private val REQ_CREATE_GROUP = 30001
    private lateinit var llNoResumts: LinearLayout
    private var endlessScrolling: EndlessScrolling? = null
    private var pageStart = 0
    private var userPageSize = 0
    private var userCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members_search)
        initview()
        Thread {
            kotlin.run {
                if (CommonData.getPaperAllMembersMap(com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey) != null) {
                    allMemberMap = CommonData.getPaperAllMembersMap(com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey)
                }

                runOnUiThread {

                    if (intent.hasExtra(SELECTED_MEMBERS)) {
                        selectedMembers.clear()
                        val str = intent.getStringExtra(SELECTED_MEMBERS)
                        val gson = Gson()
                        val entityType = object : TypeToken<java.util.LinkedHashMap<Long, GetAllMembers>>() {
                        }.type
                        selectedMembers = gson.fromJson(str, entityType)
                        if (selectedMembers.size > 0) {
                            fabNext.visibility = View.VISIBLE
                        } else {
                            fabNext.visibility = View.GONE
                        }
                    }

                    allMembersArrayList = ArrayList(allMemberMap.values)
                    rvSearchresults.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@MembersSearchActivity)
                    allMemberAdapter = AllMemberAdapter(allMembersArrayList, this@MembersSearchActivity, userIdsSearch)
                    rvSearchresults.adapter = allMemberAdapter

                    setEndlessScrolling()

                    multiMembersAddGroupAdapter = MultiMemberAddGroupAdapter(selectedMembers, this@MembersSearchActivity)
                    rvaddedMembers.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.RecyclerView.HORIZONTAL, false)
                    rvaddedMembers.itemAnimator = ScaleUpAnimator()
                    rvaddedMembers.adapter = multiMembersAddGroupAdapter
                    userIdsSearch = ArrayList(selectedMembers.keys)
                    if (userIdsSearch.size > 0) {
                        rvaddedMembers.visibility = View.VISIBLE
                    } else {
                        rvaddedMembers.visibility = View.GONE
                    }
                    apiGetAllMembers()
                }
            }
        }.start()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            this.window.statusBarColor = Color.BLACK
        }
    }

    private fun setEndlessScrolling() {
        try {
            if (endlessScrolling == null) {
                endlessScrolling = object : EndlessScrolling(rvSearchresults.layoutManager as androidx.recyclerview.widget.LinearLayoutManager) {
                    override fun onLoadMore(currentPages: Int) {
//                        if (pageStart == 0) {
//                            pageStart = userPageSize + 1
//                        } else {
                        pageStart += userPageSize
//                        }
                        apiGetAllMembers()
                    }

                    override fun onHide() {
                    }

                    override fun onShow() {
                    }
                }
                rvSearchresults.addOnScrollListener(endlessScrolling!!)
            }
        } catch (e: java.lang.Exception) {
        }
    }

    private fun initview() {
        searchToolbar = findViewById(R.id.toolbar)
        llNoResumts = findViewById(R.id.llNewSearch)
        searchToolbar.searchToolbar
        searchToolbar.setSupportActionBar(this)
        searchToolbar.setOnSearchQueryChangedListener(this@MembersSearchActivity)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        rvaddedMembers = findViewById(R.id.rvaddedMembers)
        rvSearchresults = findViewById(R.id.rvSearchresults)
        vDim = findViewById(R.id.vDim)
        fabNext = findViewById(R.id.fabNext)
        if(intent.hasExtra("searchingFor") && intent.getStringExtra("searchingFor").equals("MEET")) {
            searchToolbar.setTitle("New Meet")
            fabNext.setOnClickListener {
                val result = Intent()
                val gson = Gson()
                val list = gson.toJson(selectedMembers)
                result.putExtra("list", list)
                setResult(Activity.RESULT_OK, result)
                finish()
            }
        }else if(intent.hasExtra("searchingFor") && intent.getStringExtra("searchingFor").equals("MEET_SCHEDULE")) {
            searchToolbar.setTitle("Invite Members")
            fabNext.setOnClickListener {
                val result = Intent()
                val gson = Gson()
                val list = gson.toJson(selectedMembers)
                result.putExtra("list", list)
                setResult(Activity.RESULT_OK, result)
                finish()
            }
        } else {
            fabNext.setOnClickListener {
                val mIntent = Intent(this@MembersSearchActivity, CreateGroupActivity::class.java)
                mIntent.putExtra(EXTRA_CREATE_GROUP_FROM_SEARCH, EXTRA_CREATE_GROUP_FROM_SEARCH)
                val gson = Gson()
                val list = gson.toJson(selectedMembers)
                mIntent.putExtra("createGroupMap", list)
                startActivityForResult(mIntent, REQ_CREATE_GROUP)
            }
        }
    }

    override fun onSearchCollapsed() {
    }

    override fun onSearchQueryChanged(query: String?) {

        setFilteredList(query!!)

    }

    override fun onSearchExpanded() {
    }

    override fun onSearchSubmitted(query: String?) {

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        searchToolbar.onSearchIconClick()
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val itemId = item.itemId

        if (itemId == R.id.action_search) {
            searchToolbar.onSearchIconClick()
            return true
        } else if (itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {

        val handledByToolbar = searchToolbar.onBackPressed()

        if (!handledByToolbar) {
            super.onBackPressed()
        }
    }

    fun updateAllMemberAdapter(userId: Long) {
        if (userIdsSearch.contains(userId)) {
            userIdsSearch.remove(userId)
            if (userIdsSearch.size == 0) {
                vDim.visibility = View.GONE
                rvaddedMembers.visibility = View.GONE
                fabNext.visibility = View.GONE
            }
        }
        allMemberAdapter.updateList(allMembersArrayList, userIdsSearch)
        allMemberAdapter.notifyDataSetChanged()

    }


    private fun apiGetAllMembers() {
        val commonParams: CommonParams.Builder = CommonParams.Builder()
        commonParams.add("workspace_id", com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].workspaceId)
        commonParams.add("user_type", "ALL_MEMBERS")
        commonParams.add("user_status", "ENABLED")
        commonParams.add("include_user_guests", true)
        commonParams.add(PAGE_START, pageStart)
        RestClient.getApiInterface(true).getAllMembers(com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.accessToken, BuildConfig.VERSION_CODE, ANDROID, commonParams.build().map)
                .enqueue(object : ResponseResolver<AllMemberResponse>() {
                    override fun onSuccess(allMemberResponse: AllMemberResponse) {
                        if (pageStart == 0) {
                            userPageSize = allMemberResponse.data.getAllMemberPageSize
                            userCount = allMemberResponse.data.userCount
                            allMemberMap = LinkedHashMap()
                            allMembersArrayList = ArrayList()
                        }
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
                        allMemberAdapter.updateList(allMembersArrayList, userIdsSearch)
                        allMemberAdapter.notifyDataSetChanged()
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

    fun addMemberToList(getAllMembers: GetAllMembers) {
        searchToolbar.txtSearch.setText("")
        userIdsSearch.add(getAllMembers.userId)
        selectedMembers.put(getAllMembers.userId, getAllMembers)
        multiMembersAddGroupAdapter.updateList(selectedMembers)
        rvaddedMembers.visibility = View.VISIBLE
        fabNext.visibility = View.VISIBLE
        rvaddedMembers.post {
            try {
                rvaddedMembers.smoothScrollToPosition(multiMembersAddGroupAdapter.itemCount - 1)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        multiMembersAddGroupAdapter.notifyItemInserted(allMemberMap.size - 1)
    }


    fun setFilteredList(text: String) {

        if (!TextUtils.isEmpty(text)) {
            if (userCount < userPageSize || text.length < 2) {
                val filteredSearchedMemberArrayList = ArrayList<GetAllMembers>()
                filteredSearchedMemberArrayList.clear()
                for (i: Int in allMembersArrayList.indices) {
                    if (allMembersArrayList[i].fullName.toString().toLowerCase().contains(text.toLowerCase())) {
                        filteredSearchedMemberArrayList.add(allMembersArrayList[i])
                    }
                }

                if (filteredSearchedMemberArrayList.size > 0) {
                    rvSearchresults.visibility = View.VISIBLE
                    llNoResumts.visibility = View.GONE
                } else {
                    rvSearchresults.visibility = View.GONE
                    llNoResumts.visibility = View.VISIBLE
                }

                allMemberAdapter.updateList(filteredSearchedMemberArrayList, userIdsSearch)
                allMemberAdapter.notifyDataSetChanged()
            } else {
                apiUserSearch(text)
            }
        } else {
            allMemberAdapter.updateList(allMembersArrayList, userIdsSearch)
            allMemberAdapter.notifyDataSetChanged()
        }
    }

    private fun apiUserSearch(text: String) {
        val workspaceInfo = com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()]
        val commonParams = com.skeleton.mvp.retrofit.CommonParams.Builder()
                .add("en_user_id", workspaceInfo.enUserId)
                .add("search_text", text)
        com.skeleton.mvp.data.network.RestClient.getApiInterface(true).userSearch(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), workspaceInfo.fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                .enqueue(object : com.skeleton.mvp.data.network.ResponseResolver<UserSearch>() {
                    override fun onSuccess(userSearch: UserSearch?) {
                        if (endlessScrolling != null) {
                            rvSearchresults.removeOnScrollListener(endlessScrolling!!)
                            endlessScrolling = null
                        }
                        val filteredSearchedMemberArrayList = ArrayList<GetAllMembers>()
                        filteredSearchedMemberArrayList.clear()
                        for (i: Int in userSearch?.data?.users!!.indices) {

                            val user: User = userSearch.data.users[i]
                            val getAllMembers = GetAllMembers(user.userId, user.fullName,
                                    user.email,
                                    user.userImage,
                                    user.userThumbnailImage,
                                    user.role,
                                    0, user.contactNumber, user.leaveType)
                            allMemberMap.put(user.userId, getAllMembers)
                            filteredSearchedMemberArrayList.add(getAllMembers)
                        }
                        allMemberAdapter.updateList(filteredSearchedMemberArrayList, userIdsSearch)
                        allMemberAdapter.notifyDataSetChanged()
                    }

                    override fun onError(error: ApiError?) {
                    }

                    override fun onFailure(throwable: Throwable?) {
                    }

                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQ_CREATE_GROUP) {
            try {
                val str = data?.getStringExtra("createGroupMap")
                val gson = Gson()
                val entityType = object : TypeToken<java.util.LinkedHashMap<Long, GetAllMembers>>() {

                }.type

                selectedMembers = gson.fromJson<java.util.LinkedHashMap<Long, GetAllMembers>>(str, entityType)
                if (selectedMembers.size == 0) {
                    rvaddedMembers.visibility = View.GONE
                    vDim.visibility = View.GONE
                }
                multiMembersAddGroupAdapter.updateList(selectedMembers)
                allMemberAdapter.updateList(allMembersArrayList, ArrayList(selectedMembers.keys))
                multiMembersAddGroupAdapter.notifyDataSetChanged()
                allMemberAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                rvaddedMembers.visibility = View.GONE
                vDim.visibility = View.GONE
                allMemberAdapter.updateList(ArrayList(), ArrayList())
                multiMembersAddGroupAdapter.updateList(HashMap())
                multiMembersAddGroupAdapter.notifyDataSetChanged()
                allMemberAdapter.notifyDataSetChanged()
            }
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }

    override fun updateAllMemberAdapterCallback(userId: Long) {
        if (userIdsSearch.contains(userId)) {
            userIdsSearch.remove(userId)
            if (userIdsSearch.size == 0) {
                vDim.visibility = View.GONE
                rvaddedMembers.visibility = View.GONE
                fabNext.visibility = View.GONE
            }
        }
        allMemberAdapter.updateList(allMembersArrayList, userIdsSearch)
        allMemberAdapter.notifyDataSetChanged()
    }

    override fun recyclerViewAddedMembersCallback(getAllMembers: GetAllMembers) {
        vDim.visibility = View.VISIBLE
        userIdsSearch = ArrayList(selectedMembers.keys)
        if (selectedMembers.size != 0) {
            if (selectedMembers[getAllMembers.userId] != null) {
                selectedMembers.remove(getAllMembers.userId)
                multiMembersAddGroupAdapter.updateList(selectedMembers)
                userIdsSearch.remove(getAllMembers.userId)
                if (userIdsSearch.size == 0) {
                    rvaddedMembers.visibility = View.GONE
                    vDim.visibility = View.GONE
                    fabNext.visibility = View.GONE
                }
                multiMembersAddGroupAdapter.notifyDataSetChanged()
                rvaddedMembers.post {
                    try {
                        rvaddedMembers.smoothScrollToPosition(multiMembersAddGroupAdapter.itemCount - 1)
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
