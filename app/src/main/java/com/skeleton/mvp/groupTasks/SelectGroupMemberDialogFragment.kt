package com.skeleton.mvp.groupTasks

/********************************
Created by Amandeep Chauhan     *
Date :- 05/08/2020              *
 ********************************/

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.model.GroupMember
import com.skeleton.mvp.model.userSearch.User
import com.skeleton.mvp.model.userSearch.UserSearch
import com.skeleton.mvp.ui.AppConstants
import kotlinx.android.synthetic.main.fragment_select_group_member_dialog.*
import java.util.*
import kotlin.collections.ArrayList

class SelectGroupMemberDialogFragment : BottomSheetDialogFragment() {

    private var selectionDoneListener: SelectionDoneListener? = null
    private var membersList: ArrayList<GroupMember> = ArrayList()
    private var selectedMembers: LinkedHashMap<Long, GroupMember> = LinkedHashMap()
    private var channelId: Long = -1L
    private lateinit var activity: Activity
    private lateinit var groupMemberAdapter: GroupMemberAdapter

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity
    }

    companion object {
        fun newInstance(selectionDoneListener: SelectionDoneListener, membersList: ArrayList<GroupMember>, selectedMembers: LinkedHashMap<Long, GroupMember>, channelId: Long): SelectGroupMemberDialogFragment {
            val frag = SelectGroupMemberDialogFragment()
            frag.selectionDoneListener = selectionDoneListener
            val args = Bundle()
            args.putSerializable("membersList", membersList)
            args.putSerializable("selectedMembers", selectedMembers)
            args.putLong("channelId", channelId)
            frag.arguments = args
            return frag
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        membersList = arguments?.getSerializable("membersList") as ArrayList<GroupMember>
        selectedMembers = arguments?.getSerializable("selectedMembers") as LinkedHashMap<Long, GroupMember>
        channelId = arguments?.getLong("channelId") ?: -1L
        return inflater.inflate(R.layout.fragment_select_group_member_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        groupMemberAdapter = GroupMemberAdapter(activity, membersList, selectedMembers)

        rvMembersList.layoutManager = LinearLayoutManager(context)
        rvMembersList.adapter = groupMemberAdapter
        rvMembersList.itemAnimator = null

        tvSelectionDone?.setOnClickListener {
            dismiss()
            selectionDoneListener?.onSelectionDone(selectedMembers)
        }

        etSearchMembers?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val filteredList = ArrayList<GroupMember>()
                if (s!!.isNotEmpty() && s.length <= 2) {
                    for (member in membersList) {
                        if (member.name.toLowerCase().contains(s.toString().toLowerCase())) {
                            filteredList.add(member)
                        }
                    }
                    groupMemberAdapter.updateList(filteredList)
                    groupMemberAdapter.notifyDataSetChanged()
                } else if (s.isNotEmpty() && s.length > 2) {
                    apiUserSearch(s.toString())
                } else {
                    groupMemberAdapter.updateList(membersList)
                    groupMemberAdapter.notifyDataSetChanged()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        ivCloseSelectionSheet?.setOnClickListener {
            dismiss()
        }
    }

    fun updateUserIds(selectedMembers: LinkedHashMap<Long, GroupMember>) {
        this.selectedMembers = selectedMembers
    }

    private fun apiUserSearch(text: String) {
        val workspaceInfo = CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()]
        val commonParams = com.skeleton.mvp.retrofit.CommonParams.Builder()
                .add(FuguAppConstant.EN_USER_ID, workspaceInfo.enUserId)
                .add(AppConstants.SEARCH_TEXT, text)
                .add(FuguAppConstant.CHANNEL_ID, channelId)
        com.skeleton.mvp.data.network.RestClient.getApiInterface(true).userSearch(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), workspaceInfo.fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                .enqueue(object : com.skeleton.mvp.data.network.ResponseResolver<UserSearch>() {
                    override fun onSuccess(userSearch: UserSearch?) {
                        val filteredSearchedMemberArrayList = ArrayList<GroupMember>()
                        filteredSearchedMemberArrayList.clear()
                        for (i: Int in userSearch?.data?.users!!.indices) {
                            val user: User = userSearch.data.users[i]
                            filteredSearchedMemberArrayList.add(GroupMember(user.fullName, user.userId, "", user.userThumbnailImage, user.email, user.role, 0))
                        }
                        groupMemberAdapter.updateList(filteredSearchedMemberArrayList)
                        groupMemberAdapter.notifyDataSetChanged()
                    }

                    override fun onError(error: ApiError?) {
                    }

                    override fun onFailure(throwable: Throwable?) {
                    }

                })

    }

    fun setSelectionDoneListener(selectionDoneListener: SelectionDoneListener) {
        this.selectionDoneListener = selectionDoneListener
    }

    interface SelectionDoneListener {
        fun onSelectionDone(selectedMembers: LinkedHashMap<Long, GroupMember>)
    }


}