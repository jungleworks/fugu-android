package com.skeleton.mvp.activity

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.adapter.GroupSuggestion
import com.skeleton.mvp.adapter.SearchSuggestionsAdapter
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.data.db.ChatDatabase
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.allgroups.AllGroupsResponse
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.data.model.searchgroupuser.SearchUserResponse
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.data.network.CommonParams
import com.skeleton.mvp.data.network.ResponseResolver
import com.skeleton.mvp.data.network.RestClient
import com.skeleton.mvp.fragment.MembersSearchFragment
import com.skeleton.mvp.fragment.MessagesSearchFragment
import com.skeleton.mvp.model.FuguSearchResult
import com.skeleton.mvp.model.SearchMessage
import com.skeleton.mvp.model.searchedMessages.SearchMessagesResponse
import com.skeleton.mvp.ui.AppConstants.SEARCH_TEXT
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.util.ExtendedEditText
import com.skeleton.mvp.util.KeyboardUtil
import com.skeleton.mvp.util.Log
import com.skeleton.mvp.util.Utils
import com.wang.avi.indicators.BallClipRotateMultipleIndicator
import kotlinx.android.synthetic.main.activity_home_search.*
import java.util.*


class HomeSearchActivity : BaseActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private var MEMBERS_FRAGMENT = 0
    private var MESSAGES_FRAGMENT = 1
    private lateinit var fcCommonResponse: FcCommonResponse
    var membersList = ArrayList<FuguSearchResult>()
    var messagesList = ArrayList<SearchMessage>()
    var keypadHeight: Int = 0
    var screenHeight: Int = 0
    var lastSearchedText = ""
    var pageStart = 1
    var userId = -1L
    var cvConversation: CardView? = null
    var llBackground: LinearLayout? = null
    var llAdvancedSearch: LinearLayout? = null
    var isAdvancedSearchActive = false
    var allGroupresponse: AllGroupsResponse? = null
    var etSearchConversation: ExtendedEditText? = null
    var selectedGroupSuggestion: GroupSuggestion? = null
    var oldGroupSuggestion: GroupSuggestion? = null
    var conversationMap = HashMap<String, Long>()
    var isShared = false
    var pagerAdapter: ViewPagerAdapter? = null
    var searchTimer: CountDownTimer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_search)
        initViews()
    }

    private fun initViews() {
        viewPager = findViewById(R.id.viewpager)
        tabLayout = findViewById(R.id.tabs)
        cvConversation = findViewById(R.id.cvConversation)
        etSearchConversation = findViewById(R.id.etSearchConversation)
        tabLayout.setupWithViewPager(viewPager)
        llBackground = findViewById(R.id.llBackground)
        llAdvancedSearch = findViewById(R.id.llAdvancedSearch)
        ivCross.visibility = View.INVISIBLE
        fcCommonResponse = CommonData.getCommonResponse()
        avi.indicator = BallClipRotateMultipleIndicator()
        Thread {
            kotlin.run {
                userId = fcCommonResponse.data.workspacesInfo[CommonData.getCurrentSignedInPosition()].userId.toLong()
            }
        }.start()
        ivBack.setOnClickListener { onBackPressed() }
        ivCross.setOnClickListener {
            etSearchMembers.setText("")
            etSearchConversation?.setText("")
            oldGroupSuggestion = null
            selectedGroupSuggestion = null
            searchAndSetMessages()
            KeyboardUtil.toggleKeyboardVisibility(this@HomeSearchActivity)
        }

        if (intent.hasExtra("fromHome") && intent.getBooleanExtra("fromHome", false)) {
            isShared = true
        }


        setUpViewPager(viewPager)
        setUpListeners()
        etSearchMembers.addTextChangedListener(object : TextWatcher {
            var lastChange: Long = 0
            override fun afterTextChanged(s: Editable?) {
                if (viewPager.currentItem == MEMBERS_FRAGMENT) {
                    if (s.toString().isEmpty()) {
                        ivCross.visibility = View.INVISIBLE
                    } else {
                        ivCross.visibility = View.VISIBLE
                    }

                    if (s.toString().length > 1) {
                        searchTimer?.cancel()
                        searchTimer = object : CountDownTimer(250, 250) {
                            override fun onFinish() {
                                searchAndSetMembers()
                            }

                            override fun onTick(millisUntilFinished: Long) {

                            }

                        }.start()
//                        Handler().postDelayed({
//                            if (System.currentTimeMillis() - lastChange >= 250) {
//                                searchAndSetMembers()
//                            }
//                        }, 250)
//                        lastChange = System.currentTimeMillis()
                    } else {
                        setConversationList()
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        etSearchMembers.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_SEARCH) {
                val view = this@HomeSearchActivity.currentFocus
                if (view != null) {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
            false
        }
        etSearchConversation?.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_SEARCH) {
                val view = this@HomeSearchActivity.currentFocus
                if (view != null) {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
            false
        }
        etSearchMembers.requestFocus()
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInputFromWindow(
                llRoot.applicationWindowToken,
                InputMethodManager.SHOW_FORCED, 0)
        (pagerAdapter!!.getItem(0) as MembersSearchFragment).setConversationList(isShared)
    }

    private fun searchAndSetMembers() {
        apiSearchMembers(object : GetMembers {
            override fun getMembersSuccess() {
                if (viewPager.currentItem == MEMBERS_FRAGMENT) {
                    val page = supportFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.currentItem)
                    if (page != null) {
                        (page as MembersSearchFragment).updateList(membersList, isShared)
                    }
                }
            }

            override fun getMembersFailure() {
            }

        })
    }

    private fun setUpListeners() {
        vDim.setOnClickListener {
            val view = this@HomeSearchActivity.currentFocus
            if (view != null) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        vDim.setOnLongClickListener { true }
        llRoot.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            llRoot.getWindowVisibleDisplayFrame(r)
            screenHeight = llRoot.rootView.height
            keypadHeight = screenHeight - r.bottom
            if (keypadHeight > screenHeight * 0.15) {
                if (vDim.visibility == View.GONE && viewPager.currentItem == MESSAGES_FRAGMENT) {
                    vDim.visibility = View.VISIBLE
                    val fadeIn = AlphaAnimation(0f, 1f)
                    fadeIn.interpolator = DecelerateInterpolator() //add this
                    fadeIn.duration = 200
                    vDim.animation = fadeIn
                }

            } else {
                if (vDim.visibility == View.VISIBLE && viewPager.currentItem == MESSAGES_FRAGMENT) {
                    vDim.visibility = View.GONE
                    val fadeOut = AlphaAnimation(1f, 0f)
                    fadeOut.interpolator = AccelerateInterpolator() //add this
                    fadeOut.duration = 200
                    vDim.animation = fadeOut
                    searchAndSetMessages()
                }
            }
        }
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (position == MEMBERS_FRAGMENT) {
                    llAdvancedSearch?.visibility = View.GONE
                    cvConversation?.visibility = View.GONE
                    val param = LinearLayout.LayoutParams(
                            /*width*/ ViewGroup.LayoutParams.MATCH_PARENT,
                            /*height*/ Utils.dpToPx(this@HomeSearchActivity, 60f)
                    )
                    llBackground?.layoutParams = param
                    llBackground?.requestFocus()

                    if (etSearchMembers.text.isEmpty()) {
                        avi.visibility = View.GONE
                        ivCross.visibility = View.GONE
                    }
                    if (etSearchMembers.text.length > 1) {
                        searchAndSetMembers()
                    }
                } else if (position == MESSAGES_FRAGMENT) {
                    llAdvancedSearch?.visibility = View.VISIBLE
                    llAdvancedSearch?.setOnClickListener {
                        if (isAdvancedSearchActive) {
                            cvConversation?.visibility = View.GONE
                            val param = LinearLayout.LayoutParams(
                                    /*width*/ ViewGroup.LayoutParams.MATCH_PARENT,
                                    /*height*/ Utils.dpToPx(this@HomeSearchActivity, 100f)
                            )
                            llBackground?.layoutParams = param
                            llBackground?.requestFocus()
                        } else {
                            cvConversation?.visibility = View.VISIBLE
                            val param = LinearLayout.LayoutParams(
                                    /*width*/ ViewGroup.LayoutParams.MATCH_PARENT,
                                    /*height*/ Utils.dpToPx(this@HomeSearchActivity, 150f)
                            )
                            llBackground?.layoutParams = param
                            llBackground?.requestFocus()
                        }
                        isAdvancedSearchActive = !isAdvancedSearchActive
                    }
                    val param = LinearLayout.LayoutParams(
                            /*width*/ ViewGroup.LayoutParams.MATCH_PARENT,
                            /*height*/ Utils.dpToPx(this@HomeSearchActivity, 100f)
                    )
                    llBackground?.layoutParams = param
                    llBackground?.requestFocus()
                    if (etSearchMembers.text.isEmpty()) {
                        avi.visibility = View.GONE
                        ivCross.visibility = View.GONE
                    }
                    if (keypadHeight > screenHeight * 0.15) {
                        if (vDim.visibility == View.GONE) {
                            vDim.visibility = View.VISIBLE
                            val fadeIn = AlphaAnimation(0f, 1f)
                            fadeIn.interpolator = DecelerateInterpolator() //add this
                            fadeIn.duration = 200
                            vDim.animation = fadeIn
                        }
                    }

                    searchAndSetMessages()
                    Handler().postDelayed({
                        if (allGroupresponse == null) {
                            apiGetMyGroups()
                        }
                    }, 100)

                }
            }

        })
    }

    private fun apiGetMyGroups() {
        val commonParams = CommonParams.Builder()
                .add(EN_USER_ID, fcCommonResponse.getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].enUserId)
                .add("channel_type", "JOINED")
                .build()
        RestClient.getApiInterface(false).getGroups(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), fcCommonResponse.data.workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey,
                1, BuildConfig.VERSION_CODE, commonParams.map).enqueue(object : ResponseResolver<AllGroupsResponse>() {
            override fun onSuccess(t: AllGroupsResponse?) {
                val conversationList = ArrayList<GroupSuggestion>()
                conversationMap = HashMap()
                for (conversation in t!!.data.joinedChannels) {
                    conversationList.add(GroupSuggestion(conversation.channelId.toLong(), conversation.label))
                    conversationMap[conversation.label] = conversation.channelId.toLong()
                }
                for (conversation in t.data.o2oChannels) {
                    conversationList.add(GroupSuggestion(conversation.channelId.toLong(), conversation.label))
                    conversationMap[conversation.label] = conversation.channelId.toLong()
                }
                allGroupresponse = t
                var searchSuggestionsAdapter: SearchSuggestionsAdapter? = null
                searchSuggestionsAdapter = SearchSuggestionsAdapter(this@HomeSearchActivity, R.layout.simple_dropdown_two_line, conversationList)
                etSearchConversation?.setAdapter(searchSuggestionsAdapter)
                etSearchConversation?.setOnItemClickListener { adapterView, view, pos, l ->
                    oldGroupSuggestion = selectedGroupSuggestion
                    selectedGroupSuggestion = GroupSuggestion(conversationMap[adapterView.getItemAtPosition(pos).toString()], adapterView.getItemAtPosition(pos).toString())
                }
            }

            override fun onError(error: ApiError?) {
            }

            override fun onFailure(throwable: Throwable?) {
            }

        })
    }

    private fun searchAndSetMessages() {
        pageStart = 1
        apiSearchMessage(object : GetMessages {
            override fun getMessagesSuccess() {
                if (viewPager.currentItem == MESSAGES_FRAGMENT) {
                    val page = supportFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.currentItem)
                    if (page != null) {
                        (page as MessagesSearchFragment).updateList(messagesList, etSearchMembers.text.toString(), selectedGroupSuggestion?.channelId)
                    }
                    avi.visibility = View.GONE
                    ivCross.visibility = View.VISIBLE
                    val view = this@HomeSearchActivity.currentFocus
                    if (view != null) {
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                }
            }

            override fun getMessagesFailure() {
                messagesList.clear()
                if (viewPager.currentItem == MESSAGES_FRAGMENT) {
                    val page = supportFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.currentItem)
                    if (page != null) {
                        (page as MessagesSearchFragment).updateList(messagesList, etSearchMembers.text.toString(), selectedGroupSuggestion?.channelId)
                    }
                }
                avi.visibility = View.GONE
                ivCross.visibility = View.INVISIBLE
            }

        })
    }

    private fun apiSearchMembers(getMembers: GetMembers) {
        val workspacesInfoList = CommonData.getCommonResponse().getData().workspacesInfo as ArrayList<WorkspacesInfo>
        val currentSignedInPosition = CommonData.getCurrentSignedInPosition()
        val commonParams = CommonParams.Builder()
        commonParams.add(EN_USER_ID, workspacesInfoList[currentSignedInPosition].enUserId)
        commonParams.add(SEARCH_TEXT, etSearchMembers.text.toString())

        if (!getOneToOneRoles().contains(workspacesInfoList[currentSignedInPosition].role)) {
            commonParams.add("searchOnlyGroupsAndBots", true)
        }

        RestClient.getApiInterface(false).groupChatSearch(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), fcCommonResponse.getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                .enqueue(object : ResponseResolver<SearchUserResponse>() {

                    override fun onSuccess(searchUserResponse: SearchUserResponse?) {
                        val membersListNew = ArrayList<FuguSearchResult>()
                        val user = searchUserResponse?.data?.users
                        val channels = searchUserResponse?.data?.channels
                        val openGroups = searchUserResponse?.data?.openGroups
                        val bots = searchUserResponse?.data?.bot
                        for (i in user!!.indices) {
                            var subText = ""
                            if (workspacesInfoList.get(currentSignedInPosition).config.hideEmail.equals("1")) {
                                if (!TextUtils.isEmpty(user[i].phoneNumber) && workspacesInfoList.get(currentSignedInPosition).config.hideContactNumber.equals("0")) {
                                    subText = user[i].phoneNumber
                                } else {
                                    subText = ""
                                }
                            } else if (workspacesInfoList.get(currentSignedInPosition).config.hideContactNumber.equals("1")) {
                                subText = ""
                            } else {
                                subText = if (TextUtils.isEmpty(user[i].email) || user[i].email.contains("@fuguchat")) {
                                    user[i].phoneNumber
                                } else {
                                    user[i].email
                                }
                            }

                            if (java.lang.Long.valueOf(user[i].userId!!.toLong()).compareTo(java.lang.Long.valueOf(CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].userId)) != 0) {
                                membersListNew.add(FuguSearchResult(user[i].fullName,
                                        java.lang.Long.valueOf(user[i].userId!!.toLong()),
                                        user[i].userImage,
                                        subText, false, true, 2, false, user[i].membersInfo, ""))
                            }

                        }
                        for (i in channels!!.indices) {
                            if (!TextUtils.isEmpty(channels[i].label)) {
                                membersListNew.add(FuguSearchResult(channels[i].label,
                                        java.lang.Long.valueOf(channels[i].channelId!!.toLong()),
                                        channels[i].channelImage,
                                        channels[i].members, true, true, channels[i].chatType, false, channels[i].membersInfo, ""))
                            }
                        }
                        for (i in openGroups!!.indices) {
                            if (!TextUtils.isEmpty(openGroups[i].label)) {
                                membersListNew.add(FuguSearchResult(openGroups[i].label,
                                        java.lang.Long.valueOf(openGroups[i].channelId!!.toLong()),
                                        openGroups[i].channelImage,
                                        "Public Group", true, false, 4, false, openGroups[i].membersInfo, ""))
                            }
                        }

                        for (i in bots!!.indices) {
                            membersListNew.add(FuguSearchResult(bots[i].fullName,
                                    bots[i].userId.toLong(),
                                    bots[i].userImage,
                                    "", false, true, 7, false, null, ""))
                        }

                        membersList.clear()
                        membersList.addAll(membersListNew)
                        getMembers.getMembersSuccess()
                    }

                    override fun onError(error: ApiError?) {
                        getMembers.getMembersFailure()
                    }

                    override fun onFailure(throwable: Throwable?) {
                        getMembers.getMembersFailure()
                    }

                })
    }

    private fun setConversationList() {
        Thread {
            kotlin.run {
                val conversationMap = ChatDatabase.getConversationMap(CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey)
                val conversationList = ArrayList(conversationMap.values)
                Collections.sort(conversationList, Comparator { one, other -> other.dateTime.compareTo(one.dateTime) })
                if (conversationList.size > 10) {
                    membersList.clear()
                    var count = 0
                    var k = 10
                    while (k < conversationList.size && count < 10) {
                        if (conversationList[k].message_type != FuguAppConstant.TEXT_MESSAGE && conversationList[k].message_type != FuguAppConstant.PUBLIC_NOTE) {
                            membersList.add(FuguSearchResult(conversationList[k].label,
                                    conversationList[k].channelId,
                                    conversationList[k].thumbnailUrl,
                                    "Attachment", true,
                                    true, conversationList[k].chat_type, false,
                                    conversationList[k].membersInfo, ""))
                            count++
                        } else {
                            var message = conversationList[k].message
                            if (conversationList[k].messageState == 0) {
                                message = if (conversationList[k].last_sent_by_id.compareTo(userId) == 0) {
                                    "You deleted this message"
                                } else {
                                    "This message was deleted"
                                }
                            }
                            membersList.add(FuguSearchResult(conversationList[k].label,
                                    conversationList[k].channelId,
                                    conversationList[k].thumbnailUrl,
                                    message, true,
                                    true, conversationList[k].chat_type, false,
                                    conversationList[k].membersInfo, ""))
                            count += 1

                        }
                        k++
                    }
                    runOnUiThread {
                        if (viewPager.currentItem == MEMBERS_FRAGMENT) {
                            val page = supportFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.currentItem)
                            if (page != null) {
                                (page as MembersSearchFragment).updateList(membersList, isShared)
                            }
                        }
                    }

                }
            }
        }.start()
    }

    private fun apiSearchMessage(getMessages: GetMessages) {
        if (lastSearchedText != etSearchMembers.text.toString() || (oldGroupSuggestion != selectedGroupSuggestion)) {
            messagesList.clear()
            avi.visibility = View.VISIBLE
            ivCross.visibility = View.GONE
            lastSearchedText = etSearchMembers.text.toString()
            val commonParams = CommonParams.Builder()
            commonParams.add(EN_USER_ID, CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].enUserId)
            commonParams.add(SEARCH_TEXT, etSearchMembers.text.toString())
            commonParams.add(PAGE_START, pageStart)

            if (selectedGroupSuggestion?.channelId?.compareTo(-1L) != 0) {
                commonParams.add(CHANNEL_ID, selectedGroupSuggestion?.channelId)
            }
            if (selectedGroupSuggestion != null) {
                etSearchConversation?.setText(selectedGroupSuggestion?.channelName)
            } else {
                etSearchConversation?.setText("")
            }
            etSearchMembers.requestFocus()
            RestClient.getApiInterface(false).getSearchMessages(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), fcCommonResponse.getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                    .enqueue(object : ResponseResolver<SearchMessagesResponse>() {
                        override fun onSuccess(searchMessagesResponse: SearchMessagesResponse?) {
                            oldGroupSuggestion = selectedGroupSuggestion
                            val searchableMessages = searchMessagesResponse?.data?.searchableMessages
                            val threadMessages = searchMessagesResponse?.data?.threadMessages
                            var channelImage: String
                            for (i in searchMessagesResponse?.data?.searchableMessages!!.indices) {
                                channelImage = if (searchableMessages?.get(i)?.channelImage?.replace("\\", "")?.split("\"")!!.size > 2) {
                                    searchableMessages[i]?.channelImage?.replace("\\", "")?.split("\"")!![3]
                                } else {
                                    ""
                                }
                                var name = ""
                                if (!TextUtils.isEmpty(searchableMessages[i]?.label)) {
                                    name = searchableMessages[i]?.label!!
                                } else if (!TextUtils.isEmpty(searchableMessages[i]?.fullName)) {
                                    name = searchableMessages[i]?.fullName!!
                                }

                                messagesList.add(SearchMessage(name,
                                        searchableMessages[i]?.searchableMessage,
                                        searchableMessages[i]?.dateTime,
                                        searchableMessages[i]?.fullName,
                                        false,
                                        searchableMessages[i]?.chatType!!,
                                        searchableMessages[i]?.userId,
                                        searchableMessages[i]?.messageIndex!!,
                                        "",
                                        searchableMessages[i]?.muid,
                                        searchableMessages[i].channelId,
                                        searchableMessages[i].notification,
                                        searchableMessages[i].messageType,
                                        searchableMessages[i].fileName,
                                        searchableMessages[i].fileSize,
                                        "", searchableMessages[i].imageurl))
                            }
                            for (i in searchMessagesResponse.data?.threadMessages!!.indices) {
                                var name = ""
                                if (!TextUtils.isEmpty(threadMessages?.get(i)?.label)) {
                                    name = threadMessages?.get(i)?.label!!
                                } else if (!TextUtils.isEmpty(threadMessages?.get(i)?.fullName)) {
                                    name = threadMessages?.get(i)?.fullName!!
                                }
                                channelImage = (if (threadMessages?.get(i)?.channelImage?.replace("\\", "")?.split("\"")!!.size > 2) {
                                    threadMessages[i]?.channelImage?.replace("\\", "")?.split("\"")!![3]
                                } else "")
                                Log.e("position", i.toString())
                                messagesList.add(SearchMessage(name,
                                        threadMessages[i]?.searchableMessage,
                                        threadMessages[i]?.dateTime,
                                        threadMessages[i]?.fullName,
                                        true,
                                        threadMessages[i]?.chatType!!,
                                        threadMessages[i]?.threadUserId,
                                        0,
                                        "",
                                        threadMessages[i]?.muid,
                                        threadMessages[i].channelId,
                                        "UNMUTED",
                                        threadMessages[i].messageType,
                                        threadMessages[i].fileName,
                                        threadMessages[i].fileSize,
                                        "", threadMessages[i].imageurl))
                            }
                            getMessages.getMessagesSuccess()
                            if (lastSearchedText.isEmpty()) {
                                avi.visibility = View.GONE
                                ivCross.visibility = View.INVISIBLE
                            }
                            pageStart += searchMessagesResponse.data.pageSize
                        }

                        override fun onError(error: ApiError?) {
                            getMessages.getMessagesFailure()
                            avi.visibility = View.GONE
                            ivCross.visibility = View.INVISIBLE
                        }

                        override fun onFailure(throwable: Throwable?) {
                            getMessages.getMessagesFailure()
                            avi.visibility = View.GONE
                            ivCross.visibility = View.INVISIBLE
                        }

                    })
        } else {
            if (selectedGroupSuggestion != null) {
                etSearchConversation?.setText(selectedGroupSuggestion?.channelName)
                etSearchMembers.requestFocus()
            }
        }
    }

    private fun setUpViewPager(viewPager: ViewPager) {
        pagerAdapter = ViewPagerAdapter(supportFragmentManager)
        val membersSearchFragment = MembersSearchFragment()
        val messagesSearchFragment = MessagesSearchFragment()
        pagerAdapter?.addFragment(membersSearchFragment, "Chats")
        pagerAdapter?.addFragment(messagesSearchFragment, "Messages")
        viewPager.adapter = pagerAdapter
    }

    inner class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }
    }

    interface GetMembers {
        fun getMembersSuccess()
        fun getMembersFailure()
    }

    interface GetMessages {
        fun getMessagesSuccess()
        fun getMessagesFailure()
    }

    interface UpdateMembersData {
        fun updateList(membersList: ArrayList<FuguSearchResult>, isShared: Boolean)
    }

    interface UpdateMessagesData {
        fun updateList(messageList: ArrayList<SearchMessage>, searchMessage: String, channelId: Long?)
    }

    override fun onBackPressed() {
        val view = this@HomeSearchActivity.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

    private fun getOneToOneRoles(): ArrayList<String> {
        val workspacesInfo = CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()]
        var roles = workspacesInfo.config.enableOneToOneChat
        roles = roles.replace("[", "")
        roles = roles.replace("]", "")
        roles = roles.replace("\"".toRegex(), "")
        val rolesArray = roles.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return java.util.ArrayList(listOf(*rolesArray))
    }
}
