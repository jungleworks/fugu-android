package com.skeleton.mvp.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.mikepenz.itemanimators.ScaleUpAnimator
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.FuguColorConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.MainActivity
import com.skeleton.mvp.adapter.AllMemberAdapter
import com.skeleton.mvp.adapter.MultiMemberAddGroupAdapter
import com.skeleton.mvp.constant.FuguAppConstant.ANDROID
import com.skeleton.mvp.constant.FuguAppConstant.PAGE_START
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.data.network.CommonParams
import com.skeleton.mvp.data.network.ResponseResolver
import com.skeleton.mvp.data.network.RestClient
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.model.GetAllMembers
import com.skeleton.mvp.model.getAllMembers.AllMember
import com.skeleton.mvp.model.getAllMembers.AllMemberResponse
import com.skeleton.mvp.model.userSearch.User
import com.skeleton.mvp.model.userSearch.UserSearch
import com.skeleton.mvp.ui.AppConstants.EXTRA_CREATE_GROUP_FROM_SEARCH
import com.skeleton.mvp.ui.creategroup.CreateGroupActivity
import com.skeleton.mvp.util.Log
import com.skeleton.mvp.utils.EndlessScrolling

/**
 * Created
 * rajatdhamija on 26/07/18.
 */

class CreateGroupFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    var ivBack: ImageView? = null
    var rvaddedMembers: androidx.recyclerview.widget.RecyclerView? = null
    var rvSearchresults: androidx.recyclerview.widget.RecyclerView? = null
    private var allMemberMap = HashMap<Long, GetAllMembers>()
    private var allMembersArrayList = ArrayList<GetAllMembers>()
    var allMemberAdapter: AllMemberAdapter? = null
    var multiMembersAddGroupAdapter: MultiMemberAddGroupAdapter? = null
    private var userIdsSearch = ArrayList<Long>()
    private var multiMemberAddGroupMap = java.util.LinkedHashMap<Long, GetAllMembers>()
    private var vDim: View? = null
    private var fabNext: FloatingActionButton? = null
    private val REQ_CREATE_GROUP = 30001
    private var llNoResumts: LinearLayout? = null
    private var swipeSearch: SwipeRefreshLayout? = null
    private var userSize = 0
    private var pageSize = 0
    private var pageStart = 0
    private var endlessScrolling: EndlessScrolling? = null
    private var currentSignedInPosition = 0
    var searchTimer: CountDownTimer? = null

    private var mContext: Context? = null
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mContext = activity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_create_group, container, false)
        initView(view)
        Thread {
            kotlin.run {
                currentSignedInPosition = com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()
                if (CommonData.getPaperAllMembersMap(com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey) != null) {
                    allMemberMap = CommonData.getPaperAllMembersMap(com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey)
                }
                (mContext as MainActivity).runOnUiThread {
                    allMembersArrayList = ArrayList(allMemberMap.values)
                    rvSearchresults?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(mContext as MainActivity)
                    allMemberAdapter = AllMemberAdapter(allMembersArrayList, mContext as MainActivity, userIdsSearch)
                    rvSearchresults?.adapter = allMemberAdapter
                    multiMembersAddGroupAdapter = MultiMemberAddGroupAdapter(multiMemberAddGroupMap, mContext as MainActivity)
                    rvaddedMembers?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(mContext as MainActivity, androidx.recyclerview.widget.RecyclerView.HORIZONTAL, false)
                    rvaddedMembers?.itemAnimator = ScaleUpAnimator()
                    rvaddedMembers?.adapter = multiMembersAddGroupAdapter
                    userIdsSearch = ArrayList(multiMemberAddGroupMap.keys)
                    if (userIdsSearch.size > 0) {
                        rvaddedMembers?.visibility = View.VISIBLE
                    } else {
                        rvaddedMembers?.visibility = View.GONE
                    }
                }
            }
        }.start()
        return view
    }


    private fun setEndlessScrolling() {
        try {
            if (endlessScrolling == null) {
                endlessScrolling = object : EndlessScrolling(rvSearchresults?.layoutManager as androidx.recyclerview.widget.LinearLayoutManager) {
                    override fun onLoadMore(currentPages: Int) {
//                        if (pageStart == 0) {
//                            pageStart += (pageSize + 1)
//                        } else {
                        pageStart += pageSize
//                        }
                        apiGetAllMembers()
                    }

                    override fun onHide() {
                    }

                    override fun onShow() {
                    }
                }
                rvSearchresults?.addOnScrollListener(endlessScrolling!!)
            }
        } catch (e: Exception) {
        }
    }

    private fun initView(view: View) {
        llNoResumts = view.findViewById(R.id.llNewSearch)
        rvaddedMembers = view.findViewById(R.id.rvaddedMembers)
        rvSearchresults = view.findViewById(R.id.rvSearchresults)
        vDim = view.findViewById(R.id.vDim)
        fabNext = view.findViewById(R.id.fabNext)
        swipeSearch = view.findViewById(R.id.swipeSearch)
        swipeSearch?.setOnRefreshListener(this)
        swipeSearch?.setColorSchemeColors(FuguColorConfig().fuguThemeColorPrimary)
        fabNext?.setOnClickListener {
            val mIntent = Intent(mContext as MainActivity, CreateGroupActivity::class.java)
            mIntent.putExtra(EXTRA_CREATE_GROUP_FROM_SEARCH, EXTRA_CREATE_GROUP_FROM_SEARCH)
            val gson = Gson()
            val list = gson.toJson(multiMemberAddGroupMap)
            mIntent.putExtra("createGroupMap", list)
            startActivityForResult(mIntent, REQ_CREATE_GROUP)

        }
    }

    fun apiGetAllMembers() {
        if (com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition() != currentSignedInPosition) {
            currentSignedInPosition = com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()
            pageStart = 0
        }
        val commonParams: CommonParams.Builder = CommonParams.Builder()
        commonParams.add("workspace_id", com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].workspaceId)
        commonParams.add("user_type", "ALL_MEMBERS")
        commonParams.add("user_status", "ENABLED")
        commonParams.add("include_user_guests", true)
        if (pageStart == 0) {
            commonParams.add(PAGE_START, 0)
        } else {
            commonParams.add(PAGE_START, pageStart)
        }
        RestClient.getApiInterface(true).getAllMembers(com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.accessToken, BuildConfig.VERSION_CODE, ANDROID, commonParams.build().map)
                .enqueue(object : ResponseResolver<AllMemberResponse>() {
                    override fun onSuccess(allMemberResponse: AllMemberResponse) {
                        Log.e("All Members Size", allMemberResponse.data.allMemberResponse.size.toString())
                        if (pageStart == 0) {
                            userSize = allMemberResponse.data.userCount
                            pageSize = allMemberResponse.data.getAllMemberPageSize
                            allMemberMap = LinkedHashMap()
                            allMembersArrayList = ArrayList()
                        }

                        if (userSize > pageSize) {
                            if (endlessScrolling != null && pageStart == 0) {
                                rvSearchresults?.removeOnScrollListener(endlessScrolling!!)
                                endlessScrolling = null
                            }
                            setEndlessScrolling()
                        }

                        val myUserId = com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userId.toLong()
                        for (i: Int in allMemberResponse.data.allMemberResponse.indices) {
                            val allMember: AllMember = allMemberResponse.data.allMemberResponse[i]
                            if (allMember.fuguUserId != null && (!TextUtils.isEmpty(allMember.email) || !TextUtils.isEmpty(allMember.contactNumber) || !TextUtils.isEmpty(allMember.fullName))) {
                                if (allMember.fuguUserId.compareTo(myUserId) != 0) {
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
                        swipeSearch?.isRefreshing = false
                        Thread {
                            kotlin.run {
                                CommonData.setPaperAllMembersMap(allMemberMap, com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey)
                            }
                        }.start()

                    }

                    override fun onError(error: ApiError?) {
                        swipeSearch?.isRefreshing = false
                    }

                    override fun onFailure(throwable: Throwable?) {
                        swipeSearch?.isRefreshing = false
                    }
                })
    }

    fun setRecyclerViewAddedMembers(getAllMembers: GetAllMembers) {
        vDim?.visibility = View.VISIBLE
        userIdsSearch = ArrayList(multiMemberAddGroupMap.keys)
        if (multiMemberAddGroupMap.size != 0) {
            if (multiMemberAddGroupMap[getAllMembers.userId] != null) {
                multiMemberAddGroupMap.remove(getAllMembers.userId)
                multiMembersAddGroupAdapter?.updateList(multiMemberAddGroupMap)
                userIdsSearch.remove(getAllMembers.userId)
                if (userIdsSearch.size == 0) {
                    rvaddedMembers?.visibility = View.GONE
                    vDim?.visibility = View.GONE
                    fabNext?.visibility = View.GONE
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


    fun addMemberToList(getAllMembers: GetAllMembers) {
        userIdsSearch.add(getAllMembers.userId)
        multiMemberAddGroupMap.put(getAllMembers.userId, getAllMembers)
        multiMembersAddGroupAdapter?.updateList(multiMemberAddGroupMap)
        rvaddedMembers?.visibility = View.VISIBLE
        fabNext?.visibility = View.VISIBLE
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
                vDim?.visibility = View.GONE
                rvaddedMembers?.visibility = View.GONE
                fabNext?.visibility = View.GONE
            }
        }
        allMemberAdapter?.updateList(allMembersArrayList, userIdsSearch)
        allMemberAdapter?.notifyDataSetChanged()

    }


    fun setFilteredList(text: String) {

        if (!TextUtils.isEmpty(text)) {
            if (userSize < pageSize) {
                val filteredSearchedMemberArrayList = ArrayList<GetAllMembers>()
                filteredSearchedMemberArrayList.clear()
                for (i: Int in allMembersArrayList.indices) {
                    if (allMembersArrayList[i].fullName.toString().toLowerCase().contains(text.toLowerCase())) {
                        filteredSearchedMemberArrayList.add(allMembersArrayList[i])
                    }
                }
                if (filteredSearchedMemberArrayList.size > 0) {
                    rvSearchresults?.visibility = View.VISIBLE
                    llNoResumts?.visibility = View.GONE
                } else {
                    rvSearchresults?.visibility = View.GONE
                    llNoResumts?.visibility = View.VISIBLE
                }
                allMemberAdapter?.updateList(filteredSearchedMemberArrayList, userIdsSearch)
                allMemberAdapter?.notifyDataSetChanged()
            } else {
                if (text.length >= 2) {
                    searchTimer?.cancel()
                    searchTimer = object : CountDownTimer(250, 250) {
                        override fun onFinish() {
                            apiSearchMembers(text)
                        }

                        override fun onTick(millisUntilFinished: Long) {

                        }

                    }.start()
                }

            }
        } else {
            allMemberAdapter?.updateList(allMembersArrayList, userIdsSearch)
            allMemberAdapter?.notifyDataSetChanged()
            setEndlessScrolling()
        }


    }

    private fun apiSearchMembers(text: String) {
        val workspaceInfo = com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()]
        val commonParams = com.skeleton.mvp.retrofit.CommonParams.Builder()
                .add("en_user_id", workspaceInfo.enUserId)
                .add("search_text", text)
        RestClient.getApiInterface(true).userSearch(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), workspaceInfo.fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                .enqueue(object : ResponseResolver<UserSearch>() {
                    override fun onSuccess(userSearch: UserSearch?) {
                        if (endlessScrolling != null) {
                            rvSearchresults?.removeOnScrollListener(endlessScrolling!!)
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
                                    "",
                                    0, user.contactNumber, user.leaveType)
                            filteredSearchedMemberArrayList.add(getAllMembers)
                        }
                        allMemberAdapter?.updateList(filteredSearchedMemberArrayList, userIdsSearch)
                        allMemberAdapter?.notifyDataSetChanged()
                    }

                    override fun onError(error: ApiError?) {
                    }

                    override fun onFailure(throwable: Throwable?) {
                    }

                })
    }

    override fun onRefresh() {
        if ((mContext as MainActivity).isNetworkConnected) {
            pageStart = 0
            swipeSearch?.isRefreshing = true
            apiGetAllMembers()
        } else {
            (mContext as MainActivity).showErrorMessage(R.string.fugu_no_internet_connection_retry)
        }
    }

}
