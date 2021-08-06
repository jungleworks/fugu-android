package com.skeleton.mvp.activity

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mikepenz.itemanimators.ScaleUpAnimator
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.adapter.AllMemberAdapter
import com.skeleton.mvp.adapter.MultiMemberAddGroupAdapter
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.ANDROID
import com.skeleton.mvp.data.db.ChatDatabase
import com.skeleton.mvp.data.model.invitation.InvitationResponse
import com.skeleton.mvp.interfaces.RecyclerViewAddedMembers
import com.skeleton.mvp.interfaces.UpdateAllMemberCallback
import com.skeleton.mvp.model.FuguConversation
import com.skeleton.mvp.model.GetAllMembers
import com.skeleton.mvp.model.searchgroupuser.Channel
import com.skeleton.mvp.model.searchgroupuser.SearchUserResponse
import com.skeleton.mvp.payment.CalculatePaymentActivity
import com.skeleton.mvp.retrofit.APIError
import com.skeleton.mvp.retrofit.CommonParams
import com.skeleton.mvp.retrofit.ResponseResolver
import com.skeleton.mvp.retrofit.RestClient
import com.skeleton.mvp.ui.AppConstants.EMAILS
import com.skeleton.mvp.ui.AppConstants.WORKSPACE_ID
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.util.KeyboardUtil
import com.skeleton.mvp.util.SearchAnimationToolbar
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class GuestGroupsAccessActivity : BaseActivity(), SearchAnimationToolbar.OnSearchQueryChangedListener, UpdateAllMemberCallback, RecyclerViewAddedMembers {


    private var searchToolbar: SearchAnimationToolbar? = null
    private var ivBack: AppCompatImageView? = null
    private var rvaddedMembers: androidx.recyclerview.widget.RecyclerView? = null
    private var rvSearchresults: androidx.recyclerview.widget.RecyclerView? = null
    private var allMemberMap = HashMap<Long, GetAllMembers>()
    private var recentlyContactedMap = HashMap<Long, GetAllMembers>()
    private var allMembersArrayList = ArrayList<GetAllMembers>()
    private var allMemberAdapter: AllMemberAdapter? = null
    private var multiMembersAddGroupAdapter: MultiMemberAddGroupAdapter? = null
    private var userIdsSearch = ArrayList<Long>()
    private var multiMemberAddGroupMap = java.util.LinkedHashMap<Long, GetAllMembers>()
    private var fabNext: FloatingActionButton? = null
    private var llCreateGroup: LinearLayout? = null
    private var channelName = ""
    private var tvTitle: TextView? = null
    private var countDownTimer: CountDownTimer? = null
    private var tvCreateGroup: AppCompatTextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest_groups_access)
        searchToolbar = findViewById(R.id.searchToolbar)
        fabNext = findViewById(R.id.fabNext)
        ivBack = findViewById(R.id.ivBack)
        tvCreateGroup = findViewById(R.id.tvCreateGroup)
        llCreateGroup = findViewById(R.id.llCreateGroup)
        rvaddedMembers = findViewById(R.id.rvaddedMembers)
        rvSearchresults = findViewById(R.id.rvSearchresults)
        searchToolbar?.searchToolbar
        searchToolbar?.setSupportActionBar(this)
        searchToolbar?.setOnSearchQueryChangedListener(this)
        tvTitle = findViewById(R.id.tvTitle)

        val anim = ScaleAnimation(1f, 1.1f, 1f, 1.1f, 0f, 0f)
        anim?.startOffset = 500
        anim?.duration = 300
        anim?.fillAfter = true
        tvTitle?.animation = anim
        anim?.start()
        anim?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                tvTitle?.clearAnimation()
                val anim = ScaleAnimation(1.1f, 1f, 1.1f, 1f, 0f, 0f)
                anim.duration = 300
                anim.fillAfter = true
                tvTitle?.animation = anim
                anim.start()
            }

            override fun onAnimationStart(animation: Animation?) {
            }

        })

        if (!getCreateGroupRolesList().contains(com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].role)) {
            llCreateGroup?.visibility = View.GONE
        }

        llCreateGroup?.setOnClickListener {
            showGroupNameEditDialog(this, channelName)
        }

        fabNext?.setOnClickListener {
            if (isNetworkConnected) {
                showLoading()
                apiInviteGuests()
            } else {
                showErrorMessage(resources.getString(R.string.fugu_not_connected_to_internet))
            }
        }
        ivBack?.setOnClickListener {
            onBackPressed()
        }

        val conversations = ChatDatabase.getConversationMap(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey)
        val conversationList = ArrayList<FuguConversation>(conversations.values)
        for (conversation in conversationList) {
            if (conversation.chat_type != 2 && conversation.chat_type != 7) {
                if (conversation.thumbnailUrl.equals("https://fuguchat.s3.ap-south-1.amazonaws.com/default/WwX5qYGSEb_1518441286074.png")) {
                    conversation.thumbnailUrl = ""
                }
                allMemberMap.put(conversation.channelId, GetAllMembers(conversation.channelId, conversation.label, "", conversation.thumbnailUrl, conversation.thumbnailUrl, "", 0, "", ""))
            }
        }

        allMembersArrayList = ArrayList(allMemberMap.values)
        rvSearchresults?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@GuestGroupsAccessActivity)
        allMemberAdapter = AllMemberAdapter(allMembersArrayList, this@GuestGroupsAccessActivity, userIdsSearch)
        rvSearchresults?.adapter = allMemberAdapter
        multiMembersAddGroupAdapter = MultiMemberAddGroupAdapter(multiMemberAddGroupMap, this@GuestGroupsAccessActivity)
        rvaddedMembers?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@GuestGroupsAccessActivity, androidx.recyclerview.widget.RecyclerView.HORIZONTAL, false)
        rvaddedMembers?.itemAnimator = ScaleUpAnimator()
        rvaddedMembers?.adapter = multiMembersAddGroupAdapter
        userIdsSearch = ArrayList(multiMemberAddGroupMap.keys)
        rvSearchresults?.itemAnimator = null
    }

    private fun apiInviteGuests() {
        val emailJsonArray = JSONArray(intent.getSerializableExtra("emailArray") as ArrayList<String>)

        var contactJsonArray: ArrayList<String>? = null
        try {
            contactJsonArray = intent.getSerializableExtra("phoneArray") as ArrayList<String>
        } catch (e: java.lang.Exception) {

        }
        var countryCodesJsonArray: ArrayList<String>? = null
        try {
            countryCodesJsonArray = intent.getSerializableExtra("countryCodeArray") as ArrayList<String>
        } catch (e: Exception) {

        }

        val usersJsonArray = JSONArray(intent.getSerializableExtra("userIdsArray") as ArrayList<String>)
        val channelsJsonArray = JSONArray(userIdsSearch)
        val contactArray = JSONArray()
        val fcCommonResponse = com.skeleton.mvp.data.db.CommonData.getCommonResponse()
        val commonParams = com.skeleton.mvp.data.network.CommonParams.Builder()
        if (intent.getSerializableExtra("emailArray") != null) {
            commonParams.add(EMAILS, emailJsonArray)
        }
        if (contactJsonArray != null) {
            for (i in contactJsonArray.indices) {
                val contactObject = JSONObject()
                try {
                    contactObject.put("contact_number", contactJsonArray.get(i))
                    contactObject.put("country_code", countryCodesJsonArray?.get(i))
                    contactArray.put(contactObject)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            if (contactArray.length() > 0) {
                commonParams.add("contact_numbers", contactArray)
            }
        }
        if (!TextUtils.isEmpty(channelName)) {
            commonParams.add("custom_label", channelName)
        }
        commonParams.add("is_guest", true)
        if (usersJsonArray.length() > 0) {
            commonParams.add("user_ids_to_connect", usersJsonArray)
        }
        if (channelsJsonArray.length() > 0) {
            commonParams.add("channel_ids_to_connect", channelsJsonArray)
        }
        commonParams.add(WORKSPACE_ID, fcCommonResponse.getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].workspaceId)
        com.skeleton.mvp.data.network.RestClient.getApiInterface(true).inviteUsers(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), BuildConfig.VERSION_CODE, ANDROID, commonParams.build().map)
                .enqueue(object : ResponseResolver<InvitationResponse>() {
                    override fun success(t: InvitationResponse?) {
                        hideLoading()
                        showErrorMessage(t?.message) {
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    }

                    override fun failure(error: APIError?) {
                        hideLoading()
                        if (error!!.statusCode == 402) {
                            startActivity(Intent(this@GuestGroupsAccessActivity, CalculatePaymentActivity::class.java))
                            finish()
                        } else {
                            showErrorMessage(error!!.message)
                        }
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

    private fun showGroupNameEditDialog(activity: Context, groupName: String) {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.create_group_dialog)
        val lp = dialog.window!!.attributes
        lp.dimAmount = 0.5f
        dialog.window!!.attributes = lp
        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(false)
        val etGroupName = dialog.findViewById<EditText>(R.id.etGroupName)
        val btnSave = dialog.findViewById<AppCompatButton>(R.id.btnSave)
        val btnCancel = dialog.findViewById<AppCompatButton>(R.id.btnCancel)
        btnCancel.setOnClickListener {
            dialog.dismiss()
            KeyboardUtil.toggleKeyboardVisibility(this@GuestGroupsAccessActivity)
        }
        etGroupName.setText(groupName)
        etGroupName.setSelection(etGroupName.text.length)
        etGroupName.requestFocus()
        KeyboardUtil.toggleKeyboardVisibility(this@GuestGroupsAccessActivity)
        etGroupName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString() == groupName || s.toString().isEmpty()) {
                    btnSave.setTextColor(resources.getColor(R.color.gray_dark))
                    btnSave.setOnClickListener(null)
                } else {
                    btnSave.setTextColor(resources.getColor(R.color.colorPrimary))
                    btnSave.setOnClickListener {
                        dialog.dismiss()
                        KeyboardUtil.toggleKeyboardVisibility(this@GuestGroupsAccessActivity)
                        channelName = etGroupName.text.toString()
                        tvCreateGroup?.text = "Create new Group ($channelName)"
                    }
                }
            }
        })
        dialog.show()
    }

    private fun apiSearchMember(query: String) {
        val commonParams = CommonParams.Builder()
                .add(FuguAppConstant.EN_USER_ID, com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].enUserId)
                .add("search_text", query)
                .build()
        RestClient.getApiInterface().groupChatSearch(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo.get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.map).enqueue(object : ResponseResolver<SearchUserResponse>() {
            override fun success(searchUserResponse: SearchUserResponse) {
                val filteredList = ArrayList<GetAllMembers>()
                val user: List<Channel> = searchUserResponse.data.channels
                for (i: Int in user.indices) {
                    filteredList.add(GetAllMembers(user[i].channelId.toLong(), user[i].label,
                            "",
                            user[i].channelImage,
                            user[i].channelImage,
                            "", 0, "", ""))
                }


                allMemberAdapter?.updateList(filteredList, userIdsSearch)
                allMemberAdapter?.notifyDataSetChanged()
            }

            override fun failure(error: APIError?) {
            }
        })
    }

    override fun updateAllMemberAdapterCallback(userId: Long) {
        if (userIdsSearch.contains(userId)) {
            userIdsSearch.remove(userId)
            if (userIdsSearch.size == 0) {
                rvaddedMembers?.visibility = View.GONE
            }
        }
        allMemberAdapter?.updateList(allMembersArrayList, userIdsSearch)
        allMemberAdapter?.notifyDataSetChanged()
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

    private fun getCreateGroupRolesList(): ArrayList<String> {
        val workspaceInfo=com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()]
        var roles = workspaceInfo.config.enableCreateGroup
        roles = roles.replace("[", "")
        roles = roles.replace("]", "")
        roles = roles.replace("\"".toRegex(), "")
        val rolesArray = roles.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return java.util.ArrayList(listOf(*rolesArray))
    }
}
