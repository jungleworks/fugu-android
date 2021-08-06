package com.skeleton.mvp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.HomeSearchActivity
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.db.ChatDatabase
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.model.FuguConversation
import com.skeleton.mvp.model.FuguSearchResult
import com.skeleton.mvp.ui.creategroup.HomeSearchResultAdapter
import com.skeleton.mvp.ui.fcinvite.InviteOnboardActivity
import java.util.*

/**
 * Created
 * rajatdhamija on 03/07/18.
 */
class MembersSearchFragment : Fragment(), HomeSearchActivity.UpdateMembersData {
    private var searchResultAdapter: HomeSearchResultAdapter? = null
    private lateinit var rvSearchMembers: androidx.recyclerview.widget.RecyclerView
    var isShared = false
    private var memberList = ArrayList<FuguSearchResult>()
    var userId = -1L
    lateinit var llNewSearch: LinearLayout
    lateinit var llInviteCard: ConstraintLayout
    lateinit var btnInvite: Button
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_members_search, container, false)
        rvSearchMembers = view.findViewById(R.id.rvSearchMembers)
        Thread {
            kotlin.run {
                userId = CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()].userId.toLong()
            }
        }.start()
        llNewSearch = view.findViewById(R.id.llNewSearch)
        llInviteCard = view.findViewById(R.id.llInviteCard)
        btnInvite = view.findViewById(R.id.btnInvite)
        btnInvite.setOnClickListener { _->
            val intent = Intent(activity, InviteOnboardActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }
        searchResultAdapter = HomeSearchResultAdapter(memberList, activity, true)
        rvSearchMembers.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        rvSearchMembers.adapter = searchResultAdapter
        return view
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
        }
    }

    override fun updateList(membersList: ArrayList<FuguSearchResult>, isShared: Boolean) {
        this.isShared = isShared
        searchResultAdapter?.updateList(membersList, isShared)
        searchResultAdapter?.notifyDataSetChanged()
        if (membersList.size == 0) {
            rvSearchMembers.visibility = View.GONE
            llNewSearch.visibility = View.VISIBLE
            val workspaceInfo = CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()]
            if (!((workspaceInfo.role == "OWNER" ||
                            workspaceInfo.role == "ADMIN" ||
                            workspaceInfo.config.anyUserCanInvite == "1") && workspaceInfo.role != "GUEST"
            )) {
                llInviteCard.visibility = View.GONE
            }
        } else {
            rvSearchMembers.visibility = View.VISIBLE
            llNewSearch.visibility = View.GONE
        }
    }

    fun setConversationList(isShared: Boolean) {
        this.isShared = isShared
        Thread {
            kotlin.run {
                val conversationMap = ChatDatabase.getConversationMap(com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey)
                val conversationList = ArrayList<FuguConversation>(conversationMap.values)
                Collections.sort(conversationList, Comparator<FuguConversation> { one, other -> other.dateTime.compareTo(one.dateTime) })
                if (conversationList.size > 10) {
                    memberList.clear()
                    var count = 0
                    var k = 10
                    while (k < conversationList.size && count < 10) {
                        if (conversationList[k].message_type != FuguAppConstant.TEXT_MESSAGE && conversationList[k].message_type != FuguAppConstant.PUBLIC_NOTE) {
                            memberList.add(FuguSearchResult(conversationList[k].label,
                                    conversationList[k].channelId,
                                    conversationList[k].thumbnailUrl,
                                    "Attachment", true,
                                    true, conversationList[k].chat_type, false,
                                    conversationList[k].membersInfo,""))
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
                            memberList.add(FuguSearchResult(conversationList[k].label,
                                    conversationList[k].channelId,
                                    conversationList[k].thumbnailUrl,
                                    message, true,
                                    true, conversationList[k].chat_type, false,
                                    conversationList[k].membersInfo,""))
                            count += 1

                        }
                        k++
                    }
                    try {
                        (context as HomeSearchActivity).runOnUiThread {
                            searchResultAdapter?.updateList(memberList, isShared)
                            searchResultAdapter?.notifyDataSetChanged()
                        }
                    } catch (e: Exception) {

                    }

                }
            }
        }.start()
    }
}