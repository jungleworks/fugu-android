package com.skeleton.mvp.util

import androidx.recyclerview.widget.DiffUtil
import com.skeleton.mvp.model.Message

class MessageDiffCallback(
        private val oldList: List<Message>,
        private val newList: List<Message>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].muid == newList[newItemPosition].muid
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].equals(newList[newItemPosition])
    }

}