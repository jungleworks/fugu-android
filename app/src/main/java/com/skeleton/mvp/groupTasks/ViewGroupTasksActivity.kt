package com.skeleton.mvp.groupTasks

/********************************
Created by Amandeep Chauhan     *
Date :- 05/08/2020              *
 ********************************/

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.fcCommon.Data
import com.skeleton.mvp.data.model.groupTasks.GetTaskResponse
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.data.network.ResponseResolver
import com.skeleton.mvp.data.network.RestClient
import com.skeleton.mvp.retrofit.CommonParams
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.utils.gone
import com.skeleton.mvp.utils.visible
import kotlinx.android.synthetic.main.activity_view_group_tasks.*
import java.util.*
import kotlin.collections.ArrayList

class ViewGroupTasksActivity : BaseActivity() {

    val commonResponseData: Data = CommonData.getCommonResponse().data
    val accessToken: String = commonResponseData.userInfo.accessToken
    val userId: String = commonResponseData.workspacesInfo[CommonData.getCurrentSignedInPosition()].userId
    private var taskType = 3
    private var month = Calendar.getInstance().get(Calendar.MONTH) + 1
    private var year = Calendar.getInstance().get(Calendar.YEAR)
    private lateinit var taskListAdapter: TaskListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_group_tasks)
        taskListAdapter = TaskListAdapter(ArrayList(), this)
        rvGroupTasks?.layoutManager = LinearLayoutManager(this)
        rvGroupTasks?.adapter = taskListAdapter

        fetchAndUpdateTasks()

        ivFilterTasks?.setOnClickListener {
            if (llFilterOptions?.visibility == View.VISIBLE)
                llFilterOptions?.gone()
            else
                llFilterOptions?.visible()
        }

        ivBack?.setOnClickListener {
            onBackPressed()
        }

        ivFilterArrow?.setOnClickListener {
            spinnerTaskType?.performClick()
        }

        spinnerMonth?.setSelection(month - 1)

        spinnerMonth?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                month = position + 1
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerYear?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                year = 2020 + position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        spinnerTaskType?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                when (position) {
                    0 -> taskType = 3
                    1 -> taskType = 1
                    2 -> taskType = 0
                    3 -> taskType = 2
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        tvApplyFilter?.setOnClickListener {
            fetchAndUpdateTasks()
        }

    }

    override fun onResume() {
        super.onResume()
        fetchAndUpdateTasks()
    }

    private fun fetchAndUpdateTasks() {
        showLoading()
        val commonParams = CommonParams.Builder()
                .add(FuguAppConstant.CHANNEL_ID, intent.getLongExtra(FuguAppConstant.CHANNEL_ID, -1L))
                .add(FuguAppConstant.USER_ID, userId)
                .add("month", month)
                .add("year", year)
        if (taskType != 3) commonParams.add("is_completed", taskType)
        RestClient.getApiInterface(true).getAssignedTasks(accessToken, FuguAppConstant.ANDROID_USER, BuildConfig.VERSION_CODE, commonParams.build().map)
                .enqueue(object : ResponseResolver<GetTaskResponse?>() {
                    override fun onSuccess(getTaskResponse: GetTaskResponse?) {
                        hideLoading()
                        if (getTaskResponse?.data != null) {
                            if (getTaskResponse.data.size > 0) {
                                tvNoTasks.visibility = View.GONE
                                rvGroupTasks?.visibility = View.VISIBLE
                                taskListAdapter.updateList(getTaskResponse.data)
                            } else {
                                tvNoTasks.visibility = View.VISIBLE
                                rvGroupTasks?.visibility = View.GONE
                            }
                        }
                    }

                    override fun onError(error: ApiError) {
                        hideLoading()
                        showErrorMessage(error.message)
                    }

                    override fun onFailure(throwable: Throwable) {
                        hideLoading()
                        showErrorMessage("API Failure")
                    }
                })
    }
}