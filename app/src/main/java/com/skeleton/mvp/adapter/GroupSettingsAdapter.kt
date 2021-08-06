package com.skeleton.mvp.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.GroupInformationActivity

class GroupSettingsAdapter(private var groupSettingList: ArrayList<GroupSetting>, private var mContext: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<GroupSettingsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupSettingsAdapter.MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_group_settings, parent, false))
    }

    override fun onBindViewHolder(holder: GroupSettingsAdapter.MyViewHolder, pos: Int) {
        val position = holder.adapterPosition
        val setting = groupSettingList[position]
        holder.tvSettingTitle.text = setting.settingTitle
        holder.tvSettingSubTitle.text = setting.settingSubtitle
        holder.isActivated.isChecked = setting.isActivated
        if (!TextUtils.isEmpty(setting.settingSubtitle)) {
            holder.llSubtitle.visibility = View.VISIBLE
        } else {
            holder.llSubtitle.visibility = View.GONE
        }

        if (position == groupSettingList.size - 1) {
            holder.divider.visibility = View.GONE
        } else {
            holder.divider.visibility = View.VISIBLE
        }

        if (setting.isSwitch) {
            holder.isActivated.visibility = View.VISIBLE
        } else {
            holder.isActivated.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val currentPos = holder.adapterPosition
            if (groupSettingList[currentPos].isSwitch)
                holder.isActivated.toggle()
            else
                (mContext as GroupInformationActivity).onGroupSettingItemClicked(groupSettingList[currentPos], currentPos, false)
        }
        holder.isActivated.setOnCheckedChangeListener { compoundButton, _ ->
            (mContext as GroupInformationActivity).onGroupSettingItemClicked(groupSettingList[holder.adapterPosition], holder.adapterPosition, compoundButton.isChecked)
        }

    }

    override fun getItemCount(): Int {
        return groupSettingList.size
    }

    inner class MyViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var tvSettingTitle: AppCompatTextView = itemView.findViewById(R.id.tvSettingTitle)
        var tvSettingSubTitle: AppCompatTextView = itemView.findViewById(R.id.tvSettingSubTitle)
        var llSubtitle: LinearLayout = itemView.findViewById(R.id.llSubtitle)
        var isActivated: SwitchCompat = itemView.findViewById(R.id.isActivated)
        var divider: View = itemView.findViewById(R.id.divider)
    }

    data class GroupSetting(val settingTitle: String, val settingSubtitle: String, val isSwitch: Boolean, val isActivated: Boolean)

    interface OnSettingItemClickListener {
        fun onItemClick(groupSetting: GroupSetting, pos: Int)
    }
}
