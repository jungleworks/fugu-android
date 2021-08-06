package com.skeleton.mvp.adapter

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.ChatActivity

class FuguBotAdapter(actionList: ArrayList<BotAction>, mContext: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<FuguBotAdapter.MyViewHolder>() {

    private var actionList: ArrayList<BotAction>
    private var mContext: Context

    init {
        this.actionList = actionList
        this.mContext = mContext
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FuguBotAdapter.MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_fugu_bot, parent, false))
    }

    fun setFilteredList(actionList: ArrayList<BotAction>) {
        this.actionList = actionList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: FuguBotAdapter.MyViewHolder, pos: Int) {
        val position = holder.adapterPosition
        val botAction = actionList[position]
        holder.tvDescription.text = botAction.description
        holder.tvTag.text = botAction.displayTag
        holder.tvInputParameter.text = botAction.inputParamerter
        if (position == actionList.size - 1) {
            holder.divider.visibility = View.GONE
        } else {
            holder.divider.visibility = View.VISIBLE
        }
        holder.itemView.setOnClickListener {
            (mContext as ChatActivity).onItemClick(botAction)
        }
    }

    override fun getItemCount(): Int {
        return actionList.size
    }

    inner class MyViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var tvTag: AppCompatTextView = itemView.findViewById(R.id.tvTag)
        var tvDescription: AppCompatTextView = itemView.findViewById(R.id.tvDescription)
        var tvInputParameter: AppCompatTextView = itemView.findViewById(R.id.tvInputParameter)
        var divider: View = itemView.findViewById(R.id.divider)
    }

    data class BotAction(val displayTag: String,val tag: String, val inputParamerter: String, val description: String)

    interface OnItemClick {
        fun onItemClick(botAction: BotAction)
    }
}
