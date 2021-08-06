package com.skeleton.mvp.groupTasks.selectedTaskUsers

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skeleton.mvp.R
import com.skeleton.mvp.data.model.groupTasks.UserData
import com.skeleton.mvp.databinding.ActivitySelectedTaskUsersBinding
import kotlinx.android.synthetic.main.activity_selected_task_users.*

class SelectedTaskUsersActivity : AppCompatActivity(), SelectedUserListener {
    var selectedTaskUsersAdapter: SelectedTaskUsersAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivitySelectedTaskUsersBinding>(this, R.layout.activity_selected_task_users)
        val viewModel = ViewModelProvider(this).get(SelectedUsersViewModel::class.java)
        binding.selectedUsersViewModel = viewModel
        viewModel.listener = this
        if (intent.hasExtra("taskId") && intent.hasExtra("userData")) {
            val usersData = intent.getSerializableExtra("userData") as ArrayList<UserData>
            selectedTaskUsersAdapter = SelectedTaskUsersAdapter(intent.getLongExtra("taskId", -1L), usersData)
            rvMembersList?.layoutManager = LinearLayoutManager(this)
            rvMembersList?.adapter = selectedTaskUsersAdapter

            etSearchMembers?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val filteredList = ArrayList<UserData>()
                    if (s!!.isNotEmpty() && s.length >= 2) {
                        for (user in usersData) {
                            if (user.fullName.contains(s.toString(), true)) {
                                filteredList.add(user)
                            }
                        }
                        selectedTaskUsersAdapter?.updateList(filteredList)
                    } else {
                        selectedTaskUsersAdapter?.updateList(usersData)
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

            })
        }
    }

    override fun onDataUpdated(userData: ArrayList<UserData>) {

    }

}