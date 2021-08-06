package com.skeleton.mvp.adapter

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.MultipleInviteActivity
import com.skeleton.mvp.model.ContactsList
import com.skeleton.mvp.util.Log
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil

/**
 * Created by rajatdhamija
 * 28/06/18.
 */

class MultipleContactsAdapter(contactsList: ArrayList<ContactsList>, mContext: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<MultipleContactsAdapter.MyViewHolder>() {

    private var contactsList: ArrayList<ContactsList>
    private var mContext: Context
    private var list = ArrayList<String>()
    private var phoneNumbers = ArrayList<String>()
    private var emails = ArrayList<String>()
    private var countryCodes = ArrayList<String>()

    init {
        this.contactsList = contactsList
        this.mContext = mContext
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultipleContactsAdapter.MyViewHolder {
        return MyViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.contacts_row, parent, false))
    }

    fun updateList(contactsList: ArrayList<ContactsList>) {
        this.contactsList = contactsList
    }

    override fun onBindViewHolder(holder: MultipleContactsAdapter.MyViewHolder, pos: Int) {
        Log.e("test", pos.toString())
        val position = holder.adapterPosition
        val contact: ContactsList = contactsList.get(holder.adapterPosition)
        holder.tvName.text = contact.name
        holder.tvData.text = contact.data

        if (TextUtils.isEmpty(contact.name)) {
            holder.tvName.visibility = View.GONE
            holder.tvData.textSize = 14f
        } else {
            holder.tvName.visibility = View.VISIBLE
            holder.tvData.textSize = 12f
        }

        holder.cbInvite.isChecked = list.contains(contact.data)
        holder.itemView.setOnClickListener {
            if (holder.cbInvite.isChecked) {
                holder.cbInvite.isChecked = false
                list.remove(contact.data)
                if (contact.data.split("-").size > 1 && TextUtils.isDigitsOnly(contact.data.split("-")[1])) {
                    phoneNumbers.remove(contact.data)
                    countryCodes.remove(contact.data.split("-")[0])
                } else {
                    emails.remove(contact.data)
                }
                (mContext as MultipleInviteActivity).setTags(list, phoneNumbers, emails, countryCodes)

            } else {
                holder.cbInvite.isChecked = true
                list.add(contact.data)
                if (contact.data.split("-").size > 1 && TextUtils.isDigitsOnly(contact.data.split("-")[1])) {
                    phoneNumbers.add(contact.data)
                    var phoneNumberUtil = PhoneNumberUtil.createInstance(mContext)
                    countryCodes.add(phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(contact.data.split("-")[0])))
                } else {
                    emails.add(contact.data)
                }
                (mContext as MultipleInviteActivity).setTags(list, phoneNumbers, emails, countryCodes)
            }
        }
        if (position == contactsList.size - 1) {
            holder.llRow.setPadding(dpToPx(10), dpToPx(10), dpToPx(10), 180);
        } else {
            holder.llRow.setPadding(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10));
        }
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }

    inner class MyViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var tvName: AppCompatTextView = itemView.findViewById(R.id.tvName)
        var tvData: AppCompatTextView = itemView.findViewById(R.id.tvData)
        var cbInvite: CheckBox = itemView.findViewById(R.id.cbInvite)
        var llRow: LinearLayout = itemView.findViewById(R.id.llRow)
    }

    interface SetTags {
        fun setTags(list: ArrayList<String>, phoneNumbers: ArrayList<String>, emails: ArrayList<String>, countryCodes: ArrayList<String>)
    }

    private fun dpToPx(dpParam: Int): Int {
        val d = mContext.getResources().getDisplayMetrics().density
        return (dpParam * d).toInt()
    }
}
