package com.skeleton.mvp.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.ChatActivity
import com.skeleton.mvp.adapter.VideoCallInvitationAdapter
import com.skeleton.mvp.constant.FuguAppConstant.CHANNEL_ID
import com.skeleton.mvp.constant.FuguAppConstant.EN_USER_ID
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.model.Member
import com.skeleton.mvp.model.userSearch.User
import com.skeleton.mvp.model.userSearch.UserSearch
import com.skeleton.mvp.ui.AppConstants.SEARCH_TEXT
import com.skeleton.mvp.util.KeyboardUtil
import com.skeleton.mvp.util.Log

class VideoCallInvitationBottomSheetFragment(private val isHangoutsMeet: Boolean = false) : BottomSheetDialogFragment() {

    private var mContext: Context? = null
    private var rvMembers: androidx.recyclerview.widget.RecyclerView? = null
    private var membersList = ArrayList<Member>()
    private var userIds = ArrayList<Long>()
    private var videoCallInvitationAdapter: VideoCallInvitationAdapter? = null
    private var etSearch: EditText? = null
    private var clMain: CoordinatorLayout? = null
    private var behavior: BottomSheetBehavior<FrameLayout>? = null
    private var llClose: LinearLayout? = null
    private var keyboardVisibility = false
    private var channelId = 1L
    private var multiMemberAddGroupMap = java.util.LinkedHashMap<Long, Member>()
    var cbSelectAll: AppCompatCheckBox? = null
    private val keyboardListener = KeyboardUtil.SoftKeyboardToggleListener { isVisible ->
        keyboardVisibility = isVisible
        if (isVisible) {
            behavior?.isHideable = false
            behavior?.peekHeight = 3000
            behavior?.state = BottomSheetBehavior.STATE_EXPANDED
            rvMembers?.minimumHeight = 3000
            llClose?.visibility = View.VISIBLE
        }
    }

    companion object {
        fun newInstance(arg: Int, context: Context, membersList: ArrayList<Member>, channelId: Long, isHangoutsMeet: Boolean = false): VideoCallInvitationBottomSheetFragment {
            val frag = VideoCallInvitationBottomSheetFragment(isHangoutsMeet)
            val args = Bundle()
            frag.arguments = args
            frag.setContext(context)
            frag.setMembersList(membersList)
            frag.setChannelId(channelId)
            return frag
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.video_call_invitation_fragment, container)
        rvMembers = view?.findViewById(R.id.rvMembers)
        etSearch = view.findViewById(R.id.etSearch)
        clMain = view.findViewById(R.id.clMain)
        llClose = view.findViewById(R.id.llClose)
        videoCallInvitationAdapter = VideoCallInvitationAdapter(membersList, mContext!!, userIds, multiMemberAddGroupMap, isHangoutsMeet)
        rvMembers?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(mContext)
        rvMembers?.itemAnimator = null
        rvMembers?.adapter = videoCallInvitationAdapter
        cbSelectAll = view.findViewById(R.id.cbSelectAll)
        if (CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()].config.max_conference_participants >= membersList.size) {
            cbSelectAll?.visibility = View.VISIBLE
        }
        llClose?.setOnClickListener {
            llClose?.visibility = View.GONE
            behavior?.isHideable = true
            behavior?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            rvMembers?.minimumHeight = 1300
            behavior?.peekHeight = 1300
            if (keyboardVisibility) {
                KeyboardUtil.toggleKeyboardVisibility(context as ChatActivity)
            }
        }

        KeyboardUtil.addKeyboardToggleListener(context as ChatActivity, keyboardListener)
        etSearch?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val filteredList = ArrayList<Member>()
                if (s!!.isNotEmpty() && s.length <= 2) {
                    filteredList.add(membersList[0])
                    for (member in membersList) {
                        if (member.name.toLowerCase().contains(s.toString().toLowerCase())) {
                            filteredList.add(member)
                        }
                    }
                    videoCallInvitationAdapter?.updateList(filteredList, userIds, multiMemberAddGroupMap)
                    videoCallInvitationAdapter?.notifyDataSetChanged()
                } else if (s.isNotEmpty() && s.length > 2) {
                    apiUserSearch(s.toString())
                } else {
                    videoCallInvitationAdapter?.updateList(membersList, userIds, multiMemberAddGroupMap)
                    videoCallInvitationAdapter?.notifyDataSetChanged()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        cbSelectAll?.setOnCheckedChangeListener { _, isChecked ->
            if (multiMemberAddGroupMap.isEmpty() && !isChecked){
                return@setOnCheckedChangeListener
            }
            for (member in membersList) {
                (mContext as ChatActivity).setRecyclerViewAddedMembers(member)
            }
            (mContext as ChatActivity).setRecyclerViewAddedMembers(membersList[0])
        }
        return view
    }

    private fun apiUserSearch(text: String) {
        val workspaceInfo = CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()]
        val commonParams = com.skeleton.mvp.retrofit.CommonParams.Builder()
                .add(EN_USER_ID, workspaceInfo.enUserId)
                .add(SEARCH_TEXT, text)
                .add(CHANNEL_ID, channelId)
        com.skeleton.mvp.data.network.RestClient.getApiInterface(true).userSearch(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), workspaceInfo.fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                .enqueue(object : com.skeleton.mvp.data.network.ResponseResolver<UserSearch>() {
                    override fun onSuccess(userSearch: UserSearch?) {
                        val filteredSearchedMemberArrayList = ArrayList<Member>()
                        filteredSearchedMemberArrayList.clear()
                        filteredSearchedMemberArrayList.add(membersList[0])
                        for (i: Int in userSearch?.data?.users!!.indices) {
                            val user: User = userSearch.data.users[i]
                            filteredSearchedMemberArrayList.add(Member(user.fullName, user.userId, "", user.userThumbnailImage, user.email, user.userType, user.status, user.leaveType))
                        }
                        videoCallInvitationAdapter?.updateList(filteredSearchedMemberArrayList, userIds, multiMemberAddGroupMap)
                        videoCallInvitationAdapter?.notifyDataSetChanged()
                    }

                    override fun onError(error: ApiError?) {
                    }

                    override fun onFailure(throwable: Throwable?) {
                    }

                })

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)

            if (null != bottomSheet) {
                behavior = BottomSheetBehavior.from(bottomSheet)
                behavior?.isHideable = true
                behavior?.peekHeight = 1300
                rvMembers?.minimumHeight = 1300
                behavior?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onSlide(p0: View, p1: Float) {
                    }

                    override fun onStateChanged(p0: View, state: Int) {
                        Log.e("State --->", state.toString())
                        if (state == BottomSheetBehavior.STATE_EXPANDED && behavior?.peekHeight!! <= 3000) {
                            behavior?.isHideable = false
                            behavior?.peekHeight = 3000
                            behavior?.state = BottomSheetBehavior.STATE_EXPANDED
                            rvMembers?.minimumHeight = 3000
                            llClose?.visibility = View.VISIBLE
                        } else if (state == BottomSheetBehavior.STATE_HIDDEN) {
                            dismiss()
                        }
                    }

                })
            }
        }
        return bottomSheetDialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        KeyboardUtil.removeKeyboardToggleListener(keyboardListener)
    }

    private fun setChannelId(channelId: Long) {
        this.channelId = channelId
    }

    private fun setMembersList(membersList: ArrayList<Member>) {
        this.membersList = membersList

        userIds = ArrayList()
    }

    fun updateBottomSheet(membersList: ArrayList<Member>, userIds: ArrayList<Long>, multiMemberAddGroupMap: java.util.LinkedHashMap<Long, Member>, delay: Boolean) {
        etSearch?.setText("")
        this.membersList = membersList
        this.userIds = userIds
        this.multiMemberAddGroupMap = multiMemberAddGroupMap
        videoCallInvitationAdapter?.updateList(membersList, userIds, multiMemberAddGroupMap)
        videoCallInvitationAdapter?.notifyDataSetChanged()
        cbSelectAll?.setOnCheckedChangeListener(null)
        if (userIds.size == membersList.size-1) {
            cbSelectAll?.isChecked = true
        }
        cbSelectAll?.setOnCheckedChangeListener { _, isChecked ->
            for (member in membersList) {
                (mContext as ChatActivity).setRecyclerViewAddedMembers(member)
            }
            (mContext as ChatActivity).setRecyclerViewAddedMembers(membersList[0])
        }

    }

    private fun setContext(context: Context) {
        this.mContext = context
    }
}