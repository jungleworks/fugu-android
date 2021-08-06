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
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.adapter.SwitchCallInvitationAdapter
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.model.creategroup.MembersInfo
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.model.FuguCacheSearchResult
import com.skeleton.mvp.model.FuguSearchResult
import com.skeleton.mvp.model.searchgroupuser.SearchUserResponse
import com.skeleton.mvp.model.searchgroupuser.User
import com.skeleton.mvp.retrofit.APIError
import com.skeleton.mvp.retrofit.CommonParams
import com.skeleton.mvp.retrofit.ResponseResolver
import com.skeleton.mvp.retrofit.RestClient
import com.skeleton.mvp.util.KeyboardUtil
import com.skeleton.mvp.util.Log
import com.skeleton.mvp.videoCall.FuguCallActivity

class SwitchToConfBottomSheetFragment : BottomSheetDialogFragment() {

    private var mContext: Context? = null
    private var rvMembers: androidx.recyclerview.widget.RecyclerView? = null
    private var fuguSearchResultList = ArrayList<FuguSearchResult>()
    private var cacheSearchResults = ArrayList<FuguCacheSearchResult>()
    private var userIds = ArrayList<Long>()
    private var videoCallInvitationAdapter: SwitchCallInvitationAdapter? = null
    private var etSearch: EditText? = null
    private var clMain: CoordinatorLayout? = null
    private var workspaceInfo: WorkspacesInfo? = null
    private var otherUserId: Long = -1L
    private var multiMemberAddGroupMap = java.util.LinkedHashMap<Long, FuguSearchResult>()
    private var behavior: BottomSheetBehavior<FrameLayout>? = null
    private var llClose: LinearLayout? = null
    private var keyboardVisibility = false
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.video_call_invitation_fragment, container)
        rvMembers = view?.findViewById(R.id.rvMembers)
        etSearch = view.findViewById(R.id.etSearch)
        clMain = view.findViewById(R.id.clMain)
        workspaceInfo = com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()]
        cacheSearchResults = CommonData.getSearchResults(workspaceInfo!!.fuguSecretKey)
        cacheSearchResults.sortWith { one, other -> other.clickCount!!.compareTo(one.clickCount) }
        fuguSearchResultList = getMembersFromCache()
        videoCallInvitationAdapter = SwitchCallInvitationAdapter(fuguSearchResultList, mContext!!, userIds, multiMemberAddGroupMap)
        rvMembers?.layoutManager = LinearLayoutManager(mContext)
        llClose = view.findViewById(R.id.llClose)
        rvMembers?.itemAnimator = null
        rvMembers?.adapter = videoCallInvitationAdapter


        llClose?.setOnClickListener {
            llClose?.visibility = View.GONE
            behavior?.isHideable = true
            behavior?.state= BottomSheetBehavior.STATE_HALF_EXPANDED
            rvMembers?.minimumHeight = 1300
            behavior?.peekHeight = 1300
            if(keyboardVisibility) {
                KeyboardUtil.toggleKeyboardVisibility(context as FuguCallActivity)
            }
        }

        KeyboardUtil.addKeyboardToggleListener(context as FuguCallActivity, keyboardListener)

        etSearch?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(searchedText: Editable) {

                if (searchedText.trim().length > 2) {
                    apiSearchMember()
                } else {
                    fuguSearchResultList = getMembersFromCache()
                    (activity as FuguCallActivity).runOnUiThread {
                        videoCallInvitationAdapter?.updateList(fuguSearchResultList, userIds, multiMemberAddGroupMap)
                        videoCallInvitationAdapter?.notifyDataSetChanged()
                    }
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        KeyboardUtil.removeKeyboardToggleListener(keyboardListener)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        bottomSheetDialog.setOnShowListener {
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

    private fun getMembersFromCache(): ArrayList<FuguSearchResult> {
        fuguSearchResultList = ArrayList()
        fuguSearchResultList.add(FuguSearchResult("Everyone",
                -1L,
                "",
                "",
                false, true, 2, false, ArrayList<MembersInfo>(),""))

        for (i in cacheSearchResults.indices) {
            if (!cacheSearchResults[i].name.toLowerCase().contains("bot")) {
                fuguSearchResultList.add(FuguSearchResult(cacheSearchResults[i].name,
                        cacheSearchResults[i].user_id,
                        cacheSearchResults[i].user_image,
                        cacheSearchResults[i].email,
                        false, true, 2, false, cacheSearchResults[i].membersInfos,""))
            }
        }

        for (member in fuguSearchResultList) {
            if (member.user_id.compareTo(otherUserId) == 0) {
                fuguSearchResultList.remove(member)
                break
            }
        }
        return fuguSearchResultList
    }

    companion object {
        fun newInstance(arg: Int, context: Context, otherUserId: Long): SwitchToConfBottomSheetFragment {
            val frag = SwitchToConfBottomSheetFragment()
            val args = Bundle()
            frag.arguments = args
            frag.setContext(context)
            frag.setUserId(otherUserId)
            return frag
        }
    }

    private fun setUserId(otherUserId: Long) {
        this.otherUserId = otherUserId
    }


    fun updateBottomSheet(userIds: ArrayList<Long>, multiMemberAddGroupMap: java.util.LinkedHashMap<Long, FuguSearchResult>) {
        etSearch?.setText("")
        this.userIds = userIds
        this.multiMemberAddGroupMap = multiMemberAddGroupMap
        videoCallInvitationAdapter?.updateList(getMembersFromCache(), userIds, multiMemberAddGroupMap)
        videoCallInvitationAdapter?.notifyDataSetChanged()
    }

    private fun setContext(context: Context) {
        this.mContext = context
    }


    private fun apiSearchMember() {
        val commonParams = CommonParams.Builder()
                .add(FuguAppConstant.EN_USER_ID, com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].enUserId)
                .add("search_text", etSearch?.text?.trim().toString())
                .build()
        RestClient.getApiInterface().groupChatSearch(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo.get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.map).enqueue(object : ResponseResolver<SearchUserResponse>() {
            override fun success(searchUserResponse: SearchUserResponse) {
                fuguSearchResultList.clear()
                val user: List<User> = searchUserResponse.data.users
                fuguSearchResultList.add(FuguSearchResult("Everyone",
                        -1L,
                        "",
                        "",
                        false, true, 2, false, ArrayList<MembersInfo>(),""))
                for (i: Int in user.indices) {
                    if (java.lang.Long.valueOf(user[i].userId!!.toLong()).compareTo(java.lang.Long.valueOf(com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userId)) != 0) {
                        fuguSearchResultList.add(FuguSearchResult(user[i].fullName,
                                java.lang.Long.valueOf(user[i].userId!!.toLong()),
                                user[i].userImage,
                                user[i].email, false, true, 2, false, ArrayList(),""))
                    }
                }

                for (member in fuguSearchResultList) {
                    if (member.user_id.compareTo(otherUserId) == 0) {
                        fuguSearchResultList.remove(member)
                        break
                    }
                }

                (activity as FuguCallActivity).runOnUiThread {
                    videoCallInvitationAdapter?.updateList(fuguSearchResultList, userIds, multiMemberAddGroupMap)
                    videoCallInvitationAdapter?.notifyDataSetChanged()
                }
            }

            override fun failure(error: APIError?) {
            }
        })
    }
}