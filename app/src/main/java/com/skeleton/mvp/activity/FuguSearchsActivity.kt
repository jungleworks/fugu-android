package com.skeleton.mvp.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mikepenz.itemanimators.ScaleUpAnimator
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.adapter.FuguMultiMemberAddGroupAdapter
import com.skeleton.mvp.adapter.FuguSearchResultsAdapter
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.model.*
import com.skeleton.mvp.model.group.GroupResponse
import com.skeleton.mvp.model.searchgroupuser.SearchUserResponse
import com.skeleton.mvp.model.searchgroupuser.User
import com.skeleton.mvp.retrofit.APIError
import com.skeleton.mvp.retrofit.CommonParams
import com.skeleton.mvp.retrofit.ResponseResolver
import com.skeleton.mvp.retrofit.RestClient
import com.skeleton.mvp.ui.base.BaseActivity
import org.json.JSONArray
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList


class FuguSearchsActivity : BaseActivity() {
    private lateinit var etSearchMember: EditText
    private lateinit var ivBack: ImageView
    private lateinit var tvNoResultsFound: TextView
    private lateinit var tvMostSearched: TextView
    private lateinit var rvSearchresults: androidx.recyclerview.widget.RecyclerView
    private lateinit var rvaddedMembers: androidx.recyclerview.widget.RecyclerView
    lateinit var multiMembersAddGroupAdapter: FuguMultiMemberAddGroupAdapter
    private var isJoined: Boolean = false
    private var fuguSearchResultList
            = ArrayList<FuguSearchResult>()
    private var multiMemberAddGroupMap = LinkedHashMap<Long, FuguSearchResult>()
    private var tempmultiMemberaddgroupList = ArrayList<FuguSearchResult>()
    lateinit var fuguSearchResultAdapter: FuguSearchResultsAdapter
    private var cacheSearchResults = ArrayList<FuguCacheSearchResult>()
    private var memberList = ArrayList<GroupMember>()
    lateinit var mLayoutManager: androidx.recyclerview.widget.LinearLayoutManager
    private var userIdsSearch = ArrayList<Long>()
    private lateinit var fabAddMembers: FloatingActionButton
    private var isCreateGroup: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fugu_searchs)
        etSearchMember = findViewById(R.id.etSearchMember)
        ivBack = findViewById(R.id.ivBack)
        tvNoResultsFound = findViewById(R.id.tvNoResultsFound)
        rvSearchresults = findViewById(R.id.rvSearchresults)
        rvaddedMembers = findViewById(R.id.rvaddedMembers)
        tvMostSearched = findViewById(R.id.tvMostSearched)
        fabAddMembers = findViewById(R.id.fabAddMembers)
        if (CommonData.getGroupList() != null) {
            memberList = CommonData.getGroupList()
        }
        tvMostSearched.visibility = View.GONE
        isJoined = intent.getBooleanExtra("isJoined", true)
        rvSearchresults.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@FuguSearchsActivity)
        ivBack.setOnClickListener { onBackPressed() }
        if (intent != null) {
            if (intent.hasExtra(EXTRA_CREATE_GROUP)) {
                memberList = ArrayList()
                isCreateGroup = true
                val gson = Gson()
                val str = intent.getStringExtra(EXTRA_CREATE_GROUP_MAP)
                val entityType = object : TypeToken<LinkedHashMap<Long, FuguSearchResult>>() {}.type
                multiMemberAddGroupMap = gson.fromJson(str, entityType)
                userIdsSearch = ArrayList(multiMemberAddGroupMap.keys)
                if (userIdsSearch.size > 0) {
                    rvaddedMembers.visibility = View.VISIBLE
                } else {
                    rvaddedMembers.visibility = View.GONE
                }
            }
        }
        fabAddMembers.setOnClickListener {
            val view = this@FuguSearchsActivity.currentFocus
            if (view != null) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            if (!isCreateGroup) {
                if (userIdsSearch.size > 0) {
                    showLoading()
                    var userIds: JSONArray? = null
                    userIds = JSONArray(userIdsSearch)
                    val commonParams: CommonParams = CommonParams.Builder().add(EN_USER_ID, com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].enUserId)
                            .add("user_ids_to_add", userIds)
                            .add(CHANNEL_ID, intent.getLongExtra(CHANNEL_ID, -1L))
                            .build()
                    RestClient.getApiInterface().addMember(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.map)
                            .enqueue(object : ResponseResolver<GroupResponse>() {
                                override fun success(groupResponse: GroupResponse) {
                                    setResult(Activity.RESULT_OK)
                                    finish()
                                    hideLoading()
                                }

                                override fun failure(error: APIError?) {
                                    hideLoading()
                                    Toast.makeText(applicationContext, error?.message.toString(), Toast.LENGTH_SHORT).show()
                                }

                            })
                } else {
                    Toast.makeText(applicationContext, "Please add atleast 1 member", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (multiMemberAddGroupMap.size > 0) {
                    val intent = Intent()
                    val gson = Gson()
                    val list = gson.toJson(multiMemberAddGroupMap)
                    intent.putExtra("searchResultMap", list)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    Toast.makeText(applicationContext, "Please add atleast 1 member", Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (CommonData.getSearchResults(com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey) != null) {
            fuguSearchResultList.clear()
            cacheSearchResults = CommonData.getSearchResults(com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey)
            cacheSearchResults.sortWith(Comparator { one, other -> other.clickCount!!.compareTo(one.clickCount) })

            for (i in cacheSearchResults.indices) {
                if(!cacheSearchResults[i].name.toLowerCase().contains("bot")) {
                    fuguSearchResultList.add(FuguSearchResult(cacheSearchResults[i].name,
                            cacheSearchResults[i].user_id,
                            cacheSearchResults[i].user_image,
                            cacheSearchResults[i].email,
                            false, true, 2, false, cacheSearchResults[i].membersInfos,""))
                }
            }
            if (fuguSearchResultList.size > 0) {
                tvMostSearched.visibility = View.VISIBLE
            }
//            for (j in memberList.indices) {
//                for (i in fuguSearchResultList.indices) {
//                    if (fuguSearchResultList[i].user_id!!.compareTo(memberList[j].userId) == 0) {
//                        fuguSearchResultList.removeAt(i)
//                        break
//                    }
//                }
//            }
            if (fuguSearchResultList.size == 0) {
                tvMostSearched.visibility = View.GONE
            }
            fuguSearchResultAdapter = FuguSearchResultsAdapter(fuguSearchResultList, this@FuguSearchsActivity, true, intent.getLongExtra(CHANNEL_ID, -1L), userIdsSearch)
            rvSearchresults.animation = null
            rvSearchresults.adapter = fuguSearchResultAdapter
        }
        //etSearchMember.addTextChangedListener({TextWatcher()})
        etSearchMember.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun afterTextChanged(editable: Editable) {
                if (editable.length < 3) {
                    if (CommonData.getSearchResults(com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey) != null) {
                        fuguSearchResultList.clear()
                        cacheSearchResults = CommonData.getSearchResults(com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey)
                        cacheSearchResults.sortWith(Comparator { one, other -> other.clickCount!!.compareTo(one.clickCount) })

                        for (i in cacheSearchResults.indices) {
                            if(!cacheSearchResults[i].name.toLowerCase().contains("bot")) {
                                fuguSearchResultList.add(FuguSearchResult(cacheSearchResults[i].name,
                                        cacheSearchResults[i].user_id,
                                        cacheSearchResults[i].user_image,
                                        cacheSearchResults[i].email,
                                        false, true, 2, false, cacheSearchResults[i].membersInfos,""))
                            }
                        }
//                        for (j in memberList.indices) {
//                            for (i in fuguSearchResultList.indices) {
//                                if (fuguSearchResultList[i].user_id!!.compareTo(memberList[j].userId) == 0) {
//                                    fuguSearchResultList.removeAt(i)
//                                    break
//                                }
//                            }
//                        }
                        fuguSearchResultAdapter.notifyDataSetChanged()
                    }
                    tvNoResultsFound.visibility = View.GONE
                    if (fuguSearchResultList.size > 0) {
                        tvMostSearched.visibility = View.VISIBLE
                    }
                    rvSearchresults.visibility = View.VISIBLE
                }
                if (editable.length > 2) {
                    tvMostSearched.visibility = View.GONE
                    rvSearchresults.visibility = View.VISIBLE
                    fuguSearchResultList.clear()
                    apiSearchMember()
                }
            }
        })
        mLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.RecyclerView.HORIZONTAL, false)
        rvaddedMembers.layoutManager = mLayoutManager
        multiMembersAddGroupAdapter = FuguMultiMemberAddGroupAdapter(multiMemberAddGroupMap, this@FuguSearchsActivity)
        rvaddedMembers.itemAnimator = ScaleUpAnimator()
        rvaddedMembers.adapter = multiMembersAddGroupAdapter

    }

    private fun apiSearchMember() {
        val commonParams = CommonParams.Builder()
                .add(EN_USER_ID, com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].enUserId)
                .add("search_text", etSearchMember.text.toString())
                .build()
        RestClient.getApiInterface().groupChatSearch(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo.get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.map).enqueue(object : ResponseResolver<SearchUserResponse>() {
            override fun success(searchUserResponse: SearchUserResponse) {
                fuguSearchResultList.clear()
                val user: List<User> = searchUserResponse.data.users
                for (i: Int in user.indices) {
                    if (java.lang.Long.valueOf(user[i].userId!!.toLong()).compareTo(java.lang.Long.valueOf(com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userId)) != 0) {
                        fuguSearchResultList.add(FuguSearchResult(user[i].fullName,
                                java.lang.Long.valueOf(user[i].userId!!.toLong()),
                                user[i].userImage,
                                user[i].email, false, true, 2, false, ArrayList(),user[i].leaveType))
                    }
                }
                if (fuguSearchResultList.size == 0) {
                    tvNoResultsFound.visibility = View.VISIBLE
                    tvMostSearched.visibility = View.GONE
                    rvSearchresults.visibility = View.GONE
                } else {
                    tvNoResultsFound.visibility = View.GONE
                    rvSearchresults.visibility = View.VISIBLE
                }
//                for (j: Int in memberList.indices) {
//                    for (i in fuguSearchResultList.indices) {
//                        if (fuguSearchResultList[i].user_id!!.compareTo(memberList[j].userId) == 0) {
//                            fuguSearchResultList.removeAt(i)
//                            break
//                        }
//                    }
//                }
                fuguSearchResultAdapter = FuguSearchResultsAdapter(fuguSearchResultList, this@FuguSearchsActivity, true, intent.getLongExtra(CHANNEL_ID, -1L), userIdsSearch)
                rvSearchresults.animation = null
                rvSearchresults.adapter = fuguSearchResultAdapter
            }

            override fun failure(error: APIError?) {
            }
        })
    }


    fun setViewOnAdddingMemberToGroup(fuguSearchResultlist: ArrayList<FuguSearchResult>, clickedItem: FuguSearchResult, posi: Int) {
        userIdsSearch = ArrayList(multiMemberAddGroupMap.keys)
        if (multiMemberAddGroupMap.size != 0) {
            if (multiMemberAddGroupMap[clickedItem.user_id] != null) {
                multiMemberAddGroupMap.remove(clickedItem.user_id)
                multiMembersAddGroupAdapter.updateList(multiMemberAddGroupMap)
                userIdsSearch.remove(clickedItem.user_id)
                if (userIdsSearch.size == 0) {
                    rvaddedMembers.visibility = View.GONE
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
                addMemberToList(clickedItem)
            }

        } else {
            addMemberToList(clickedItem)
        }
        etSearchMember.setText("")
    }

    private fun addMemberToList(clickedItem: FuguSearchResult) {
        userIdsSearch.add(clickedItem.user_id)
        multiMemberAddGroupMap.put(clickedItem.user_id, clickedItem)
        multiMembersAddGroupAdapter.updateList(multiMemberAddGroupMap)
        rvaddedMembers.visibility = View.VISIBLE
        rvaddedMembers.post {
            try {
                rvaddedMembers.smoothScrollToPosition(multiMembersAddGroupAdapter.itemCount - 1)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        multiMembersAddGroupAdapter.notifyItemInserted(multiMemberAddGroupMap.size - 1)
    }

    fun updateSearchAdapter(userId: Long) {
        if (userIdsSearch.contains(userId)) {
            userIdsSearch.remove(userId)
        }
        fuguSearchResultAdapter.updateList(fuguSearchResultList, userIdsSearch)
        fuguSearchResultAdapter.notifyDataSetChanged()
    }
}



