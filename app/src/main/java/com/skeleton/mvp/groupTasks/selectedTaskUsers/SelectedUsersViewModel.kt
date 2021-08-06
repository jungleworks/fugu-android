package com.skeleton.mvp.groupTasks.selectedTaskUsers

import androidx.lifecycle.ViewModel
import com.skeleton.mvp.data.model.groupTasks.UserData

class SelectedUsersViewModel : ViewModel() {
    var searchText = ""
    var listener: SelectedUserListener? = null
    var adapter: SelectedTaskUsersAdapter? = null


}

interface SelectedUserListener {
    fun onDataUpdated(userData: ArrayList<UserData>)
}
