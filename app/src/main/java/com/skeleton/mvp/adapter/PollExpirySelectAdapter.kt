package com.skeleton.mvp.adapter

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.CreatePollActivity

internal class PollExpirySelectAdapter(context: Context, numberList: ArrayList<String>, selectedOption: Int, type: String) : androidx.recyclerview.widget.RecyclerView.Adapter<PollExpirySelectAdapter.MyViewHolder>() {
    private var context: Context
    private var numberList: ArrayList<String>
    private var selectedOption: Int
    private var previousSelectedPos: Int
    private var type: String

    init {
        this.context = context;
        this.numberList = numberList
        this.selectedOption = selectedOption
        previousSelectedPos = selectedOption
        this.type = type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PollExpirySelectAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_poll_time, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PollExpirySelectAdapter.MyViewHolder, position: Int) {
        holder.tvOption.text = numberList.get(holder.adapterPosition)
        holder.radio.isChecked = holder.adapterPosition == selectedOption
        holder.itemView.setOnClickListener {
            selectedOption = holder.adapterPosition
            notifyItemChanged(selectedOption)
            notifyItemChanged(previousSelectedPos)
            previousSelectedPos = selectedOption
            (context as CreatePollActivity).onItemSelected(selectedOption, type, holder.tvOption.text.toString())
        }
        holder.radio.setOnClickListener {
            selectedOption = holder.adapterPosition
            notifyItemChanged(selectedOption)
            notifyItemChanged(previousSelectedPos)
            previousSelectedPos = selectedOption
            (context as CreatePollActivity).onItemSelected(selectedOption, type, holder.tvOption.text.toString())
        }
    }

    override fun getItemCount(): Int {
        return numberList.size
    }

    inner class MyViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var tvOption: AppCompatTextView = itemView.findViewById(R.id.tvOption)
        var radio: RadioButton = itemView.findViewById(R.id.radio)
    }

    fun getSeletedOption(): Int {
        return selectedOption
    }

    interface SelectedPosition {
        fun onItemSelected(selectedPosition: Int, type: String, value: String)
    }
}
