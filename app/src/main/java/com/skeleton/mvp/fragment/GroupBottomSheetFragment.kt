package com.skeleton.mvp.fragment

/**
 * Created by rajatdhamija
 * 19/04/18.
 */

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.GroupInformationActivity
import com.skeleton.mvp.activity.GroupMembersActivity
import com.skeleton.mvp.constant.FuguAppConstant


class GroupBottomSheetFragment : BottomSheetDialogFragment(), View.OnClickListener {

    private var groupInformationActivity: GroupInformationActivity? = null
    private var membersSearchActivity: GroupMembersActivity? = null
    private var userId: Long? = null
    private var pos: Int? = null
    private var chatType: Int? = null
    private var name: String = ""
    private var selectedUserRole: String = ""
    private var currentUserRole: String = ""
    private var remove: LinearLayout? = null
    private var viewProfile: LinearLayout? = null
    private var admin: LinearLayout? = null
    private var tvAdmin: TextView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.group_bottom_sheet, container, false)
        if (activity is GroupInformationActivity) {
            groupInformationActivity = activity as GroupInformationActivity
        } else {
            membersSearchActivity = activity as GroupMembersActivity
        }
        remove = view.findViewById(R.id.remove)
        admin = view.findViewById(R.id.admin)
        viewProfile = view.findViewById(R.id.view)
        tvAdmin = view.findViewById(R.id.tvAdmin)
        if (selectedUserRole.equals(FuguAppConstant.Role.ADMIN.toString())) {
            tvAdmin?.text = "Dismiss as Admin"

        } else {
            tvAdmin?.text = "Make group Admin"
        }

        if (currentUserRole.equals(FuguAppConstant.Role.ADMIN.toString())) {
            admin?.visibility = View.VISIBLE
            remove?.visibility = View.VISIBLE
        } else {
            if (chatType == FuguAppConstant.ChatType.PUBLIC_GROUP) {
                admin?.visibility = View.GONE
                remove?.visibility = View.VISIBLE
            } else {
                admin?.visibility = View.GONE
                remove?.visibility = View.GONE
            }
        }

        if (chatType == FuguAppConstant.ChatType.DEFAULT_GROUP || chatType == FuguAppConstant.ChatType.GENERAL_GROUP) {
            remove?.visibility = View.GONE
        } else {
            if (remove?.visibility == View.VISIBLE) {
                remove?.visibility = View.VISIBLE
            } else {
                remove?.visibility = View.GONE
            }
        }

        admin?.setOnClickListener(this)
        remove?.setOnClickListener(this)
        viewProfile?.setOnClickListener(this)


        return view
    }

    override fun onClick(v: View) {
        dismiss()
        when (v.id) {
            R.id.admin -> {
                if (selectedUserRole.equals(FuguAppConstant.Role.ADMIN.toString())) {
                    if (groupInformationActivity!=null) {
                        groupInformationActivity?.dismissGroupAdmin(userId, pos)
                    }else{
                        membersSearchActivity?.dismissGroupAdmin(userId, pos)
                    }
                } else {
                    if (groupInformationActivity!=null) {
                        groupInformationActivity?.makeGroupAdmin(userId, pos)
                    }else{
                        membersSearchActivity?.makeGroupAdmin(userId, pos)
                    }
                }
            }
            R.id.remove -> {
                if (groupInformationActivity!=null) {
                    groupInformationActivity?.removeMember(userId, pos, name)
                }else{
                    membersSearchActivity?.removeMember(userId, pos, name)
                }
            }
            R.id.view -> {
                if (groupInformationActivity!=null) {
                    groupInformationActivity?.viewProfile(userId)
                }else{
                    membersSearchActivity?.viewProfile(userId)
                }
            }
        }
    }

    companion object {

        fun newInstance(arg: Int, userId: Long?, selectedUserRole: String, name: String, pos: Int, currentUserRole: String, chatType: Int): GroupBottomSheetFragment {
            val frag = GroupBottomSheetFragment()
            val args = Bundle()
            frag.arguments = args
            frag.setUserId(userId)
            frag.setSelecteduserRole(selectedUserRole)
            frag.setCurrentuserRole(currentUserRole)
            frag.setPos(pos)
            frag.setName(name)
            frag.setChatType(chatType)
            return frag
        }
    }

    private fun setChatType(chatType: Int) {
        this.chatType = chatType
    }

    private fun setCurrentuserRole(currentUserRole: String) {
        this.currentUserRole = currentUserRole
    }

    private fun setSelecteduserRole(role: String) {
        this.selectedUserRole = role
    }

    private fun setUserId(userId: Long?) {
        this.userId = userId
    }

    private fun setPos(pos: Int?) {
        this.pos = pos
    }

    private fun setName(name: String) {
        this.name = name
    }

}
