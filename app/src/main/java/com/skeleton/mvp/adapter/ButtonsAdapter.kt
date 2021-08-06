package com.skeleton.mvp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.ChatActivity
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.model.customAction.Button
import com.skeleton.mvp.utils.joinHangoutsCall

class ButtonsAdapter(context: Context, buttons: ArrayList<Button>, muid: String, pos: Int, comment: String) : androidx.recyclerview.widget.RecyclerView.Adapter<ButtonsAdapter.MyViewHolder>() {
    var context: Context
    var buttons: ArrayList<Button>
    var muid: String
    var pos: Int
    var comment: String
    var minLength: Int

    init {
        this.context = context
        this.buttons = buttons
        this.muid = muid
        this.pos = pos
        this.comment = comment
        this.minLength = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonsAdapter.MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.row_button, parent, false))
    }

    override fun getItemCount(): Int {
        return buttons.size
    }

    fun setData(comment: String, minLength: Int) {
        this.comment = comment
        this.minLength = minLength
    }

    override fun onBindViewHolder(holder: ButtonsAdapter.MyViewHolder, position: Int) {
        val button = buttons.get(holder.adapterPosition)
        holder.button.text = button.label
        when (button.style.toUpperCase()) {
            FuguAppConstant.Style.DANGER.toString() -> {
                holder.button.setBackgroundResource(R.drawable.danger_rectangular_button_empty)
                holder.button.setTextColor(holder.itemView.context.resources.getColor(R.color.red))
            }
            FuguAppConstant.Style.SUCCESS.toString() -> {
                holder.button.setBackgroundResource(R.drawable.success_rectangular_button_empty)
                holder.button.setTextColor(holder.itemView.context.resources.getColor(R.color.teal))
            }
            FuguAppConstant.Style.DEFAULT.toString() -> {
                holder.button.setBackgroundResource(R.drawable.default_rectangular_button_empty)
                holder.button.setTextColor(holder.itemView.context.resources.getColor(R.color.colorPrimary))
            }
            else -> {
                holder.button.setBackgroundResource(R.drawable.default_rectangular_button_empty)
                holder.button.setTextColor(holder.itemView.context.resources.getColor(R.color.colorPrimary))
            }

        }
        holder.itemView.setOnClickListener {
            if (buttons.get(holder.adapterPosition).action.equals(FuguAppConstant.FuguEvents.OPEN_CAMERA.toString())) {
                (context as ChatActivity).onCameraButtonClick(buttons.get(holder.adapterPosition), muid, pos)
            } else if (buttons.get(holder.adapterPosition).action.equals(FuguAppConstant.FuguEvents.VIDEO_CONFERENCE.toString())) {
                if (buttons.get(holder.adapterPosition).inviteLink.contains("meet.google.com")) {
                    (context as ChatActivity).joinHangoutsCall(button.inviteLink)
                } else {
                    (context as ChatActivity).onVideoConferenceJoined(buttons.get(holder.adapterPosition), muid, pos, buttons.get(holder.adapterPosition).inviteLink)
                }
            } else if (buttons.get(holder.adapterPosition).action.equals(FuguAppConstant.FuguEvents.CREATE_GROUP.toString())) {
                if (getCreateGroupRolesList().contains(CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()].role)) {
                    (context as ChatActivity).onCreateGroup(buttons.get(holder.adapterPosition), muid, pos)
                } else {
                    (context as ChatActivity).showErrorMessage("You are not authorized to perform this action!")
                }
            } else if (buttons.get(holder.adapterPosition).action.equals(FuguAppConstant.FuguEvents.BROWSE_GROUP.toString())) {
                (context as ChatActivity).onBrowseGroup(buttons.get(holder.adapterPosition), muid, pos)
            } else if (buttons.get(holder.adapterPosition).action.equals(FuguAppConstant.FuguEvents.INVITE_MEMBER.toString())) {
                (context as ChatActivity).onInviteMember(buttons.get(holder.adapterPosition), muid, pos)
            } else if (buttons.get(holder.adapterPosition).action.equals(FuguAppConstant.FuguEvents.CREATE_WORKSPACE.toString())) {
                (context as ChatActivity).onCreateWorkspace(buttons.get(holder.adapterPosition), muid, pos)
            } else {
                (context as ChatActivity).onButtonClick(buttons.get(holder.adapterPosition), muid, pos)
            }
        }
    }

    class MyViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val button: AppCompatButton = itemView.findViewById(R.id.button)
    }

    interface ButtonClick {
        fun onButtonClick(button: Button, muid: String, pos: Int)
        fun onVideoConferenceJoined(button: Button, muid: String, pos: Int, inviteLink: String)
        fun onCameraButtonClick(button: Button, muid: String, pos: Int)
        fun onCreateGroup(button: Button, muid: String, pos: Int)
        fun onBrowseGroup(button: Button, muid: String, pos: Int)
        fun onInviteMember(button: Button, muid: String, pos: Int)
        fun onCreateWorkspace(button: Button, muid: String, pos: Int)
    }

    private fun getCreateGroupRolesList(): ArrayList<String> {
        var workspacesInfo = CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()]
        var roles = workspacesInfo.config.enableCreateGroup
        roles = roles.replace("[", "")
        roles = roles.replace("]", "")
        roles = roles.replace("\"".toRegex(), "")
        val rolesArray = roles.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return java.util.ArrayList(listOf(*rolesArray))
    }
}