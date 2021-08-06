package com.skeleton.mvp.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mikepenz.itemanimators.ScaleUpAnimator
import com.skeleton.mvp.R
import com.skeleton.mvp.adapter.SelectedSendEmailMembersAdapter
import com.skeleton.mvp.adapter.SendEmailMembersAdapter
import com.skeleton.mvp.baseRecycler.OnRecyclerViewObjectClickListener
import com.skeleton.mvp.model.GetAllMembers
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.viewHolders.SendEmailViewHolder

/**
 * Created by rajatdhamija
 * 10/08/18.
 */
class SendEmailActivity : BaseActivity(), OnRecyclerViewObjectClickListener<GetAllMembers> {
    private lateinit var rvSearchResults: androidx.recyclerview.widget.RecyclerView
    private lateinit var rvAddedMembers: androidx.recyclerview.widget.RecyclerView
    private lateinit var vDim: View
    private var allUsersMap = LinkedHashMap<Long, GetAllMembers>()
    private var selectedUsersMap = LinkedHashMap<Long, GetAllMembers>()
    private lateinit var sendEmailMembersAdapter: SendEmailMembersAdapter
    private lateinit var selectedSendEmailMembersAdapter: SelectedSendEmailMembersAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_email)
        initializeViews()
        fetchIntentData()
    }

    private fun initializeViews() {
        rvSearchResults = findViewById(R.id.rvSearchresults)
        rvAddedMembers = findViewById(R.id.rvaddedMembers)
        sendEmailMembersAdapter = SendEmailMembersAdapter(this)
        selectedSendEmailMembersAdapter = SelectedSendEmailMembersAdapter(this)
        sendEmailMembersAdapter.setListener(this)
        selectedSendEmailMembersAdapter.setListener(this)
        rvSearchResults.setAdapter(sendEmailMembersAdapter)
        rvAddedMembers.setAdapter(selectedSendEmailMembersAdapter)
        rvSearchResults.setLayoutManager(androidx.recyclerview.widget.LinearLayoutManager(this))
        rvAddedMembers.setLayoutManager(androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.RecyclerView.HORIZONTAL, false))
        rvSearchResults.itemAnimator = null
        rvAddedMembers.itemAnimator = ScaleUpAnimator()
    }

    private fun fetchIntentData() {
        val str = intent.getStringExtra("sendEmailMap")
        val gson = Gson()
        val entityType = object : TypeToken<java.util.LinkedHashMap<Long, GetAllMembers>>() {}.type
        allUsersMap = gson.fromJson(str, entityType)
        val getAllMembers = ArrayList<GetAllMembers>(allUsersMap.values)
        sendEmailMembersAdapter.items = getAllMembers
        selectedSendEmailMembersAdapter.items = ArrayList<GetAllMembers>(selectedUsersMap.values)
    }

    override fun onItemClicked(item: GetAllMembers?, holder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
        if (holder is SendEmailViewHolder) {
            val getAllMembers: ArrayList<GetAllMembers> = sendEmailMembersAdapter.items as ArrayList<GetAllMembers>
            getAllMembers[holder.adapterPosition].isSelected = !getAllMembers[holder.adapterPosition].isSelected
            sendEmailMembersAdapter.notifyItemChanged(holder.adapterPosition)
            addMemberToSelectedList(getAllMembers[holder.adapterPosition])
        } else {
            val getSelectedMembers: ArrayList<GetAllMembers> = selectedSendEmailMembersAdapter.items as ArrayList<GetAllMembers>
            getSelectedMembers[holder.adapterPosition].isSelected = !getSelectedMembers[holder.adapterPosition].isSelected
            selectedSendEmailMembersAdapter.remove(item)
        }
    }

    private fun addMemberToSelectedList(getAllMembers: GetAllMembers?) {
        if (selectedUsersMap[getAllMembers?.userId] == null) {
            selectedUsersMap.put(getAllMembers?.userId!!, getAllMembers)
            if (selectedUsersMap.size > 0 && rvAddedMembers.visibility == View.GONE) {
                rvAddedMembers.visibility = View.VISIBLE
            }
            selectedSendEmailMembersAdapter.add(getAllMembers)
            rvAddedMembers.smoothScrollToPosition(selectedUsersMap.size - 1)
        } else {
            selectedUsersMap.remove(getAllMembers?.userId)
            selectedSendEmailMembersAdapter.remove(getAllMembers)
            if (selectedUsersMap.size == 0 && rvAddedMembers.visibility == View.VISIBLE) {
                rvAddedMembers.visibility = View.GONE
            }
        }
    }
}