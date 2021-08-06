package com.skeleton.mvp.groupTasks

/********************************
Created by Amandeep Chauhan     *
Date :- 05/08/2020              *
 ********************************/

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.easyfilepicker.activity.NormalFilePickActivity
import com.easyfilepicker.filter.entity.NormalFile
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.groupTasks.GetTaskDetailsResponse
import com.skeleton.mvp.data.model.groupTasks.TaskDetail
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.data.network.RestClient
import com.skeleton.mvp.groupTasks.selectedTaskUsers.SelectedTaskUsersActivity
import com.skeleton.mvp.model.FuguFileDetails
import com.skeleton.mvp.model.FuguUploadImageResponse
import com.skeleton.mvp.model.GroupMember
import com.skeleton.mvp.retrofit.*
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.util.GeneralFunctions
import com.skeleton.mvp.util.Log
import com.skeleton.mvp.util.Utils
import com.skeleton.mvp.utils.*
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.android.synthetic.main.task_file_item_layout.*
import okhttp3.MultipartBody
import org.json.JSONArray
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TaskActivity : BaseActivity(), View.OnClickListener, ProgressRequestBody.UploadCallbacks {
    private var startCalendar = Calendar.getInstance()
    private var endCalendar = Calendar.getInstance()

    //    private var remindCalendar = Calendar.getInstance()
    private var isAllSelected = false
    private lateinit var tvMemberCount: TextView
    private var membersList: ArrayList<GroupMember> = ArrayList()
    private var selectedMembers: LinkedHashMap<Long, GroupMember> = LinkedHashMap()
    private var fileDetails: FuguFileDetails? = null
    private var downloadId: Int = -1
    private var reminderMinutes: Int = 10
    private var channelId: Long = -1L
    private var taskId: Long = -1L
    private var isEditMode: Boolean = false
    private var isViewMode: Boolean = false
    private val commonResponseData = CommonData.getCommonResponse().data!!
    private val workspacesInfo = commonResponseData.workspacesInfo!!
    private val currentPosition = CommonData.getCurrentSignedInPosition()
    private val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
    private val dateUtils = DateUtils.getInstance()!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        tvMemberCount = findViewById(R.id.tvMemberCount)
        ivBack.setOnClickListener(this)
        channelId = intent.getLongExtra(CHANNEL_ID, -1L)
        taskId = intent.getLongExtra("eventId", -1L)
        if (intent.hasExtra("isViewMode")) {
            isViewMode = true
            apiGetTaskDetails()
        } else {
            llSelectMembers?.setOnClickListener(this)
            if (intent.hasExtra("membersList"))
                membersList = intent.getSerializableExtra("membersList") as ArrayList<GroupMember>
            membersList.removeAt(0)

            switchSelectAll.setOnCheckedChangeListener { _, isChecked ->
                isAllSelected = isChecked
                if (isChecked) {
                    llSelectMembers?.gone()
                } else {
                    llSelectMembers?.visible()
                }
            }

        }
        if (isViewMode) btnAssignTask?.text = "Update Task"

        ivStartCal.setOnClickListener {
            showDialogs("start")
        }
        ivEndCal.setOnClickListener {
            showDialogs("end")
        }
//            ivRemindCal.setOnClickListener {
//                showDialogs("remind")
//            }

        spinnerReminderTime?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> reminderMinutes = 10
                    1 -> reminderMinutes = 15
                    2 -> reminderMinutes = 30
                    3 -> reminderMinutes = 60
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
        btnAssignTask.setOnClickListener {
            val current = Calendar.getInstance().time
            if (!isViewMode && startCalendar.timeInMillis < Calendar.getInstance().timeInMillis) {
                showErrorMessage("Please choose a task start time greater than ${getFormattedDate(current)} ${getFormattedTime(current)}.")
                return@setOnClickListener
            } else if (startCalendar.timeInMillis > endCalendar.timeInMillis) {
                showErrorMessage("Task start time cannot be greater than meeting end time.")
                return@setOnClickListener
            }
            if (!isViewMode && !isAllSelected && selectedMembers.isEmpty()) {
                showErrorMessage("Please select at least 1 user to assign the task.")
            }
            showLoading()
            val commonParams = CommonParams.Builder()
                    .add("assigner_user_id", workspacesInfo[currentPosition].userId)
                    .add("title", etTaskTitle.text.toString().trim())
                    .add("description", etTaskDescription.text.toString().trim())
                    .add("start_datetime", DateUtils.getInstance().convertToUTC(DateUtils.getFormattedDate(startCalendar.time)))
                    .add("end_datetime", DateUtils.getInstance().convertToUTC(DateUtils.getFormattedDate(endCalendar.time)))
                    .add("reminder", reminderMinutes)
                    .add(CHANNEL_ID, channelId)
                    .add("workspace_id", workspacesInfo[currentPosition].workspaceId)
//                        .add("reminder_datetime", DateUtils.getInstance().convertToUTC(DateUtils.getFormattedDate(remindCalendar.time)))
            if (isViewMode) {
                commonParams.add("task_id", taskId)
                RestClient.getApiInterface(true).editTaskDetails(commonResponseData.userInfo.accessToken, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                        .enqueue(object : ResponseResolver<GetTaskDetailsResponse>() {
                            override fun success(t: GetTaskDetailsResponse?) {
                                hideLoading()
                                showToast(t?.message ?: "Task Updated Successfully")
                                if (t?.data != null)
                                    manipulateUiWithTaskDetails(t.data)
                            }

                            override fun failure(error: APIError?) {
                                hideLoading()
                                showErrorMessage(error?.message ?: "Some Error Occurred")
                            }

                        })
                isEditMode = false
                ivEdit?.setImageDrawable(getDrawable(R.drawable.ic_edit_black_24dp))
            } else {
                commonParams.add("is_selected_all", if (isAllSelected) 1 else 0)
                if (!isAllSelected)
                    commonParams.add("user_ids", JSONArray(selectedMembers.keys))
                RestClient.getApiInterface(true).assignTask(commonResponseData.userInfo.accessToken, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                        .enqueue(object : ResponseResolver<CommonResponse>() {
                            override fun success(t: CommonResponse?) {
                                hideLoading()
                                showToast(t?.message ?: "Task Created Successfully")
                                this@TaskActivity.finish()
                            }

                            override fun failure(error: APIError?) {
                                hideLoading()
                                showErrorMessage(error?.message ?: "Some Error Occurred")
                            }

                        })
            }
        }
        etTaskDescription.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
            }
            v.performClick()
            false
        }
    }

    private fun startDownload(url: String, directory: String, fileName: String): Int {
        return PRDownloader.download(url, directory, fileName)
                .build()
                .setOnStartOrResumeListener { android.util.Log.i("TAG: complete", "DownloadStarted") }
                .setOnPauseListener { ivFileDownload?.setImageDrawable(getDrawable(R.drawable.ic_cloud_download_24)) }
                .setOnCancelListener { ivFileDownload?.setImageDrawable(getDrawable(R.drawable.ic_cloud_download_24)) }
                .setOnProgressListener { progress ->
                    progressCircle.progress = progress.currentBytes * 100F / progress.totalBytes
                    Log.e("Progress", (progress.currentBytes * 100 / progress.totalBytes).toString())
                }
                .start(object : OnDownloadListener {
                    override fun onDownloadComplete() {
                        ivFileDownload?.gone()
                        progressCircle?.gone()
                        rlSubmittedFile?.setOnClickListener {
                            openFile(File(directory + File.separator + fileName))
                        }
                    }

                    override fun onError(error: Error?) {
                        ivFileDownload?.setOnClickListener {
                            PRDownloader.resume(downloadId)
                        }
                    }

                })
    }

    private fun getDirectory(): String? {
        return try {
            val filePath = (Environment.getExternalStorageDirectory()).toString() + File.separator + APP_NAME_SHORT +
                    File.separator +
                    CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()].workspaceName.replace(" ".toRegex(), "").replace("'s".toRegex(), "") +
                    File.separator +
                    APP_NAME_SHORT + " Tasks"
//            Log.i("TaskFilePath", filePath)
            val folder = File(filePath)
            if (!folder.exists()) {
                folder.mkdirs()
            }
            filePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun apiGetTaskDetails() {
        tvScreenTitle?.text = "Task Details"
        showLoading()
        val userId = if (intent.hasExtra("user_id")) intent.getStringExtra("user_id") else workspacesInfo[currentPosition].userId
        val commonParams = CommonParams.Builder()
                .add("task_id", taskId)
                .add("user_id", userId)
        RestClient.getApiInterface(true).getTaskDetails(commonResponseData.userInfo.accessToken, ANDROID_USER, BuildConfig.VERSION_CODE, commonParams.build().map)
                .enqueue(object : com.skeleton.mvp.data.network.ResponseResolver<GetTaskDetailsResponse?>() {
                    override fun onSuccess(getTaskDetailsResponse: GetTaskDetailsResponse?) {
                        hideLoading()
                        val taskDetail = getTaskDetailsResponse?.data
                        if (taskDetail != null) {
                            channelId = taskDetail.channelID
                            manipulateUiWithTaskDetails(taskDetail)
                        } else {
                            showErrorMessage("Task Details not available.") { finish() }
                        }
                    }

                    override fun onError(error: ApiError) {
                        hideLoading()
                        showErrorMessage(error.message)
                    }

                    override fun onFailure(throwable: Throwable) {
                        hideLoading()
                        showErrorMessage("Failed to fetch task details")
                    }
                })
    }

    @SuppressLint("SetTextI18n")
    private fun manipulateUiWithTaskDetails(taskDetail: TaskDetail) {
        ivStartCal?.gone()
        ivEndCal?.gone()
//            ivRemindCal?.gone()
        tvSelectAll?.gone()
        llSelectMembers?.gone()
        switchSelectAll?.gone()
        btnAssignTask?.gone()
        divider_6?.gone()
//            tvTaskTitle?.gone()
//            tvTaskDescription?.gone()
//            divider_2?.gone()
        setTransparentBg(etTaskStartDate)
        setTransparentBg(etTaskEndDate)
        setTransparentBg(etStartTaskTime)
        setTransparentBg(etEndTaskTime)
//            setTransparentBg(etTaskReminderDate)
//            setTransparentBg(etTaskReminderTime)
        setTransparentBg(etTaskDescription)
        setTransparentBg(etTaskTitle)
//            spinnerReminderTime?.setBackgroundColor(Color.parseColor("#00000000"))
//            spinnerReminderTime?.setPadding(0, 0, 0, 0)
        spinnerReminderTime?.gone()
        etTaskTitle?.isFocusable = false
        etTaskDescription?.isFocusable = false
        etTaskTitle?.setText(taskDetail.title)
        etTaskDescription?.setText(taskDetail.description)
        etTaskStartDate?.text = getFormattedDate(sdf.parse(dateUtils.convertToLocal(taskDetail.startDateTime)))
        etStartTaskTime?.text = getFormattedTime(sdf.parse(dateUtils.convertToLocal(taskDetail.startDateTime))).toUpperCase()
        etTaskEndDate?.text = getFormattedDate(sdf.parse(dateUtils.convertToLocal(taskDetail.endDateTime)))
        etEndTaskTime?.text = getFormattedTime(sdf.parse(dateUtils.convertToLocal(taskDetail.endDateTime))).toUpperCase()
        tvReminderText1?.text = "Reminder : ${getReminderTime(taskDetail.reminder)}"
//                            spinnerReminderTime?.text?.text = "${taskDetail.reminder} Min"
//                            etTaskReminderDate?.text = getFormattedDate(sdf.parse(utils.convertToLocal(taskDetail.reminderDateTime)))
//                            etTaskReminderTime?.text = getFormattedTime(sdf.parse(utils.convertToLocal(taskDetail.reminderDateTime))).toUpperCase()

        startCalendar.time = dateUtils.getLocalDateObject(taskDetail.startDateTime)
        endCalendar.time = dateUtils.getLocalDateObject(taskDetail.endDateTime)
        when {
            taskDetail.userData != null && taskDetail.assignerID == workspacesInfo[currentPosition].userId.toLong() -> {
                ivEdit?.visible()
                tvAttachFile?.gone()
                switchAttachFile?.gone()
                llSelectMembers?.visible()
                llUploadFile?.gone()
                tvDisplayMessage?.gone()
                tvMemberCount.text = taskDetail.userData.size.toString()

                btnDeleteTask?.visible()
                btnDeleteTask.setOnClickListener {
                    val deleteParams = CommonParams.Builder()
                            .add("is_deleted", 1)
                            .add("task_id", taskId)
                            .add(CHANNEL_ID, taskDetail.channelID)
                            .add("assigner_user_id", workspacesInfo[currentPosition].userId)
                    RestClient.getApiInterface(true).editTaskDetails(CommonData.getCommonResponse().data.userInfo.accessToken, 1, BuildConfig.VERSION_CODE, deleteParams.build().map)
                            .enqueue(object : ResponseResolver<GetTaskDetailsResponse>() {
                                override fun success(t: GetTaskDetailsResponse?) {
                                    hideLoading()
                                    showToast("Task Deleted Successfully")
                                    this@TaskActivity.finish()
                                }

                                override fun failure(error: APIError?) {
                                    hideLoading()
                                    showErrorMessage(error?.message ?: "Some Error Occurred")
                                }

                            })
                }

                llSelectMembers.setOnClickListener {
                    val intent = Intent(this@TaskActivity, SelectedTaskUsersActivity::class.java)
                    intent.putExtra("taskId", taskDetail.taskID)
                    intent.putExtra("userData", taskDetail.userData)
                    this@TaskActivity.startActivity(intent)
                }

                ivEdit?.setOnClickListener {
                    if (isEditMode) {
                        isEditMode = false
                        ivEdit?.setImageDrawable(getDrawable(R.drawable.ic_edit_black_24dp))
                        tvScreenTitle?.text = "Task Details"
                        etTaskTitle?.setText(taskDetail.title)
                        etTaskDescription?.setText(taskDetail.description)
                        etTaskStartDate?.text = getFormattedDate(sdf.parse(dateUtils.convertToLocal(taskDetail.startDateTime)))
                        etStartTaskTime?.text = getFormattedTime(sdf.parse(dateUtils.convertToLocal(taskDetail.startDateTime))).toUpperCase()
                        etTaskEndDate?.text = getFormattedDate(sdf.parse(dateUtils.convertToLocal(taskDetail.endDateTime)))
                        etEndTaskTime?.text = getFormattedTime(sdf.parse(dateUtils.convertToLocal(taskDetail.endDateTime))).toUpperCase()
                        tvReminderText1?.text = "Reminder : ${getReminderTime(taskDetail.reminder)}"
//                        spinnerReminderTime?.text?.text = "${taskDetail.reminder} Min"
//                        etTaskReminderDate?.text = getFormattedDate(sdf.parse(utils.convertToLocal(taskDetail.reminderDateTime)))
//                        etTaskReminderTime?.text = getFormattedTime(sdf.parse(utils.convertToLocal(taskDetail.reminderDateTime))).toUpperCase()
                        ivStartCal?.gone()
                        ivEndCal?.gone()
                        btnAssignTask?.gone()
                        divider_6?.gone()
                        tvReminderText1?.text = "Reminder : ${getReminderTime(taskDetail.reminder)}"
                        spinnerReminderTime?.gone()
                        setTransparentBg(etTaskStartDate)
                        setTransparentBg(etTaskEndDate)
                        setTransparentBg(etStartTaskTime)
                        setTransparentBg(etEndTaskTime)
                        setTransparentBg(etTaskDescription)
                        setTransparentBg(etTaskTitle)
                        etTaskTitle?.isFocusable = false
                        etTaskDescription?.isFocusable = false
                        btnAssignTask?.isEnabled = false
                        btnDeleteTask?.isEnabled = false
                    } else {
                        isEditMode = true
                        ivEdit?.setImageDrawable(getDrawable(R.drawable.ic_cancel_black_24dp))
                        tvScreenTitle?.text = "Edit Task"
                        ivStartCal?.visible()
                        ivEndCal?.visible()
                        btnAssignTask?.visible()
                        divider_6?.visible()

                        setRectGreyBg(etTaskStartDate)
                        setRectGreyBg(etTaskEndDate)
                        setRectGreyBg(etStartTaskTime)
                        setRectGreyBg(etEndTaskTime)
                        setRectGreyBg(etTaskDescription)
                        setRectGreyBg(etTaskTitle)
                        spinnerReminderTime?.gone()
                        etTaskTitle?.isFocusableInTouchMode = true
                        etTaskDescription?.isFocusableInTouchMode = true
                        btnAssignTask?.isEnabled = true
                        btnDeleteTask?.isEnabled = true

                        etTaskTitle.setText(taskDetail.title)
                        etTaskDescription.setText(taskDetail.description)
                        etTaskStartDate.text = "${startCalendar.get(Calendar.DAY_OF_MONTH)}/${startCalendar.get(Calendar.MONTH) + 1}/${startCalendar.get(Calendar.YEAR)}"
                        etStartTaskTime.text = getFormattedTime(startCalendar.get(Calendar.HOUR_OF_DAY), startCalendar.get(Calendar.MINUTE))
                        etTaskEndDate.text = "${endCalendar.get(Calendar.DAY_OF_MONTH)}/${endCalendar.get(Calendar.MONTH) + 1}/${endCalendar.get(Calendar.YEAR)}"
                        etEndTaskTime.text = getFormattedTime(endCalendar.get(Calendar.HOUR_OF_DAY), endCalendar.get(Calendar.MINUTE))

                        tvReminderText1?.text = "Remind"
                        spinnerReminderTime?.visible()
                        when (taskDetail.reminder) {
                            10 -> spinnerReminderTime.setSelection(0)
                            15 -> spinnerReminderTime.setSelection(1)
                            30 -> spinnerReminderTime.setSelection(2)
                            60 -> spinnerReminderTime.setSelection(3)
                            else -> spinnerReminderTime.setSelection(0)
                        }
                    }
                }
            }
            taskDetail.isCompleted == 1 -> {
                if (intent.getBooleanExtra("isAdminViewingUser", false) || Calendar.getInstance().timeInMillis > DateUtils.getInstance().getLocalDateObject(taskDetail.endDateTime).time)
                    ivEdit?.gone()
                else {
                    ivEdit?.visible()
                    ivEdit?.setOnClickListener {
                        if (isEditMode) {
                            isEditMode = false
                            showCompletedUI(taskDetail)
                        } else {
                            isEditMode = true
                            showSubmitUI(taskDetail)
                        }
                    }
                }
                showCompletedUI(taskDetail)
            }
            else -> showSubmitUI(taskDetail)
        }
    }

    private fun showSubmitUI(taskDetail: TaskDetail) {
        rlSubmittedFile?.gone()
        val start = DateUtils.getInstance().getLocalDateObject(taskDetail.startDateTime)
        val end = DateUtils.getInstance().getLocalDateObject(taskDetail.endDateTime)
        if (intent.getBooleanExtra("isAdminViewingUser", false) || Calendar.getInstance().timeInMillis < start.time) {
            tvAttachFile?.gone()
            switchAttachFile?.gone()
            tvSubmissionContent?.gone()
            etSubmissionContent?.gone()
            btnAssignTask?.gone()
            tvDisplayMessage?.visible()
            tvDisplayMessage?.text = "Submission starts ${getFormattedTime(start)} on ${getFormattedDate(start)}"
        } else if (Calendar.getInstance().timeInMillis > end.time) {
            tvAttachFile?.gone()
            switchAttachFile?.gone()
            tvSubmissionContent?.gone()
            etSubmissionContent?.gone()
            btnAssignTask?.gone()
            tvDisplayMessage?.visible()
            tvDisplayMessage?.text = "Submission already ended at ${getFormattedTime(start)} on ${getFormattedDate(start)}"
        } else {
            tvAttachFile?.visible()
            switchAttachFile?.visible()
            tvSubmissionContent?.visible()
            etSubmissionContent?.visible()
            btnAssignTask?.visible()
            tvDisplayMessage?.gone()
            etSubmissionContent?.isFocusableInTouchMode = true
            etSubmissionContent?.setText(taskDetail.content)
            switchAttachFile?.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    llUploadFile?.visible()
                    tvUploadFile?.visible()
                } else {
                    llUploadFile?.gone()
                    tvUploadFile?.gone()
                }
            }
            tvUploadFile?.setOnClickListener {
                val intent = Intent(this@TaskActivity, NormalFilePickActivity::class.java)
                val mimeTypes = arrayOf(
                        "txt", "xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf",
                        "odt", "apk", "zip", "csv", "sql", "psd"
                )
                intent.putExtra(SUFFIX, mimeTypes)
                startActivityForResult(intent, REQUEST_CODE_PICK_FILE)
            }
            btnAssignTask?.text = "Submit Task"
            btnAssignTask?.setOnClickListener {
                apiSubmitTask()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showCompletedUI(taskDetail: TaskDetail) {
        if (!TextUtils.isEmpty(taskDetail.content)) {
            etSubmissionContent?.visible()
            etSubmissionContent?.setText(taskDetail.content)
            etSubmissionContent.isFocusable = false
        } else
            etSubmissionContent?.gone()
        if (taskDetail.taskWork != null) {
            rlSubmittedFile.visible()
            llUploadFile?.visible()
            val taskWork = taskDetail.taskWork
            tvFileName?.text = taskWork.fileName
            tvFileSize?.text = taskWork.fileSize
            tvSubmissionDateTime?.text = "${getFormattedDate(sdf.parse(dateUtils.convertToLocal(taskDetail.submissionDatetime)))} ${getFormattedTime(sdf.parse(dateUtils.convertToLocal(taskDetail.submissionDatetime)))}"
            val directory = getDirectory()
            val fileName = taskDetail.taskWork.fileName
            val ext = getFileExtension(fileName)
            val localFileName = fileName + "_" + taskWork.muid + "." + ext
            if (directory != null) {
                val localFile = File(directory + File.separator + localFileName)
                Log.i("FilePath", localFile.absolutePath)
                if (localFile.exists()) {
                    tvFileExt?.text = ext?.toUpperCase() ?: "FILE"
                    rlDownload?.visibility = View.INVISIBLE
                    rlSubmittedFile?.setOnClickListener {
                        openFile(localFile)
                    }
                } else {
                    rlDownload?.visibility = View.VISIBLE
                    ivFileDownload?.setOnClickListener {
                        downloadId = startDownload(taskWork.url, directory, localFileName)
                        if (downloadId != 1) {
                            progressCircle.visible()
                            ivFileDownload?.setImageDrawable(getDrawable(R.drawable.ic_pause_24))
                            ivFileDownload?.setOnClickListener {
                                PRDownloader.pause(downloadId)
                            }
                        }
                    }
                }
            }
        }
        tvUploadFile?.gone()
        tvAttachFile?.gone()
        switchAttachFile?.gone()
        btnAssignTask?.gone()
    }

    private fun apiSubmitTask() {
        if (Calendar.getInstance().timeInMillis < startCalendar.timeInMillis) {
            showErrorMessage("Please wait for the task to start! Try again after ${getFormattedTime(startCalendar.time).toUpperCase()} on ${getFormattedDate(startCalendar.time)}.")
            return
        }
        val taskContent = etSubmissionContent.text.toString().trim()
        if (TextUtils.isEmpty(taskContent) && fileDetails == null) {
            showToast("Please enter some content or attach a file to submit.")
            return
        }
        if (switchAttachFile.isChecked && fileDetails == null) {
            showToast("Please select a file to attach.")
            return
        }
        showLoading()
        if (switchAttachFile.isChecked && fileDetails != null)
            uploadFileServerCall(
                    UUID.randomUUID().toString(),
                    fileDetails!!.filePath,
                    FILE_MESSAGE,
                    fileDetails
            )
        else {
            val commonResponseData = CommonData.getCommonResponse().data
            val workspacesInfo = commonResponseData.workspacesInfo
            val currentPosition = CommonData.getCurrentSignedInPosition()
            val commonParams = CommonParams.Builder()
                    .add("task_id", intent.getLongExtra("eventId", -1L))
                    .add("user_id", workspacesInfo[currentPosition].userId)
                    .add("content", taskContent)
            RestClient.getApiInterface(true).submitTask(commonResponseData.userInfo.accessToken, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                    .enqueue(object : ResponseResolver<CommonResponse>() {
                        override fun success(t: CommonResponse?) {
                            hideLoading()
                            showToast("Task Submitted Successfully.")
                            btnAssignTask?.gone()
                            tvAttachFile?.gone()
                            switchAttachFile?.gone()
                            llUploadFile?.gone()
                            etSubmissionContent?.isFocusable = false
                        }

                        override fun failure(error: APIError?) {
                            hideLoading()
                            showErrorMessage(error?.message ?: "Some Error Occurred")
                        }

                    })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDialogs(type: String) {
        val calendar = Calendar.getInstance()
        run {
            var selectedDate: Int
            var selectedMonth: Int
            var selectedYear: Int
            var selectedHour: Int
            var selectedMinute: Int
            val datePickerDialog = DatePickerDialog(this@TaskActivity, { _, year, month, dayOfMonth ->
                selectedDate = dayOfMonth
                selectedMonth = month
                selectedYear = year
                val timePickerDialog = TimePickerDialog(this@TaskActivity, { _, hourOfDay, minute ->
                    selectedHour = hourOfDay
                    selectedMinute = minute
                    calendar.set(selectedYear, selectedMonth, selectedDate, selectedHour, selectedMinute, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    when (type) {
                        "start" -> {
                            this.startCalendar = calendar
                            etTaskStartDate.text = "$selectedDate/${selectedMonth + 1}/$selectedYear"
                            etStartTaskTime.text = getFormattedTime(selectedHour, selectedMinute)
                        }
                        "end" -> {
                            this.endCalendar = calendar
                            etTaskEndDate.text = "$selectedDate/${selectedMonth + 1}/$selectedYear"
                            etEndTaskTime.text = getFormattedTime(selectedHour, selectedMinute)
                        }
//                        "remind" -> {
//                            this.remindCalendar = calendar
//                            etTaskReminderDate.text = "$selectedDate/${selectedMonth + 1}/$selectedYear"
//                            etTaskReminderTime.text = getFormattedTime(selectedHour, selectedMinute)
//                        }
                    }
                }, if (type == "start") startCalendar.get(Calendar.HOUR) else endCalendar.get(Calendar.HOUR), if (type == "start") startCalendar.get(Calendar.MINUTE) else endCalendar.get(Calendar.MINUTE), false)
                timePickerDialog.show()
            }, if (type == "start") startCalendar.get(Calendar.YEAR) else endCalendar.get(Calendar.YEAR), if (type == "start") startCalendar.get(Calendar.MONTH) else endCalendar.get(Calendar.MONTH), if (type == "start") startCalendar.get(Calendar.DAY_OF_MONTH) else endCalendar.get(Calendar.DAY_OF_MONTH))
            datePickerDialog.show()
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.ivBack -> {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
            R.id.llSelectMembers -> {
                SelectGroupMemberDialogFragment.newInstance(object : SelectGroupMemberDialogFragment.SelectionDoneListener {
                    override fun onSelectionDone(selectedMembers: LinkedHashMap<Long, GroupMember>) {
                        this@TaskActivity.selectedMembers = selectedMembers
                        runOnUiThread {
                            tvMemberCount.text = selectedMembers.size.toString()
                        }
                    }
                }, membersList, selectedMembers.clone() as LinkedHashMap<Long, GroupMember>, intent.getLongExtra(CHANNEL_ID, -1L)).show(supportFragmentManager, "SelectGroupMemberDialogFragment")
            }
        }
    }

    fun getUriFromPath(path: String): Uri {
        return FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", File(path))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_PICK_FILE) {
                val maxUploadSize = CommonData.getCommonResponse().data.fuguConfig.max_upload_file_size
                val list: ArrayList<NormalFile> = data!!.getParcelableArrayListExtra(RESULT_PICK_FILE)!!
                var extension = ""
                if (!getUriFromPath(list[0].path).toString().contains("com.google")) {
                    val cursor = this@TaskActivity.contentResolver.query(
                            getUriFromPath(list[0].path),
                            null,
                            null,
                            null,
                            null
                    )
                    try {
                        if (cursor != null && cursor.moveToFirst()) {
                            try {
                                val splitArray =
                                        cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                                                .split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                                                .toTypedArray()
                                extension = splitArray[splitArray.size - 1].toLowerCase()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } finally {
                        cursor!!.close()
                    }
                    if (FILE_TYPE_MAP[extension] == null) {
                        extension = "default"
                    }
                    val fileDetails = FuguImageUtils(this@TaskActivity).saveFile(getUriFromPath(list[0].path), FILE_TYPE_MAP[extension], -1L, null)
                    try {
                        if (fileDetails?.filePath?.isEmpty()!!) {
                            Toast.makeText(this, "File not found...", Toast.LENGTH_LONG).show()
                        } else {
                            if (fileDetails.fileSize.contains("KB") || fileDetails.fileSize.contains("Bytes") || java.lang.Double.parseDouble(fileDetails.fileSizeDouble) * 1024 * 1024 <= maxUploadSize) {
                                this.fileDetails = fileDetails
                                tvUploadFile?.text = fileDetails.fileName
                                ViewCompat.setBackgroundTintList(tvUploadFile, ColorStateList.valueOf(Color.parseColor("#098D49")))
                            } else {
                                showErrorMessage("File size cannot be greater than " + (((maxUploadSize) / 1024) / 1024).toInt() + "MB.")
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, "File not available on device", Toast.LENGTH_LONG).show()
                    }
                } else run {
                    showErrorMessage("Drive files not supported yet.")
                }
            }
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }


    private fun uploadFileServerCall(uuid: String, url: String, messageType: Int, fileDetails: FuguFileDetails?) {
        val fileBody: ProgressRequestBody
        val filePart: MultipartBody.Part
        if (messageType == FILE_MESSAGE || messageType == VIDEO_MESSAGE || fileDetails?.fileExtension.equals("gif")) {
            fileBody = ProgressRequestBody(File(url), this, GeneralFunctions().getMimeType(fileDetails?.filePath!!, this@TaskActivity), url, 0, uuid)
            filePart = MultipartBody.Part.createFormData("file", fileDetails.fileExtension, fileBody)
        } else {
            val link = url.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            fileBody = ProgressRequestBody(
                    File(url),
                    this,
                    GeneralFunctions().getMimeType(url, this@TaskActivity),
                    url,
                    0,
                    uuid
            )
            filePart = MultipartBody.Part.createFormData("file", link[link.size - 1], fileBody)
        }
        val workspaceInfoList = CommonData.getCommonResponse().data.workspacesInfo
        val fuguSecretKey: String = workspaceInfoList[CommonData.getCurrentSignedInPosition()].fuguSecretKey
        try {
            val multipartBuilder = MultipartParams.Builder()
            val multipartParams = multipartBuilder
                    .add(APP_SECRET_KEY, fuguSecretKey)
                    .add(APP_VERSION, BuildConfig.VERSION_CODE)
                    .add("file_name", fileDetails?.fileName)
                    .add(MESSAGE_TYPE, messageType)
                    .add("muid", uuid)
                    .add(DEVICE_TYPE, 1).build()
            com.skeleton.mvp.retrofit.RestClient.getApiInterface().uploadFile(CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), fuguSecretKey, 1, BuildConfig.VERSION_CODE, filePart, multipartParams.map)
                    .enqueue(object : ResponseResolver<FuguUploadImageResponse>() {
                        override fun success(fuguUploadImageResponse: FuguUploadImageResponse?) {
                            Thread {
                                kotlin.run {
                                    if (fileDetails != null) {
                                        try {
                                            if (!TextUtils.isEmpty(fuguUploadImageResponse!!.data.imageSize.toString())) {
                                                when {
                                                    fuguUploadImageResponse.data.imageSize!! < 1024 -> {
                                                        fileDetails.fileSize = fuguUploadImageResponse.data.imageSize.toString() + " B"
                                                    }
                                                    (fuguUploadImageResponse.data?.imageSize!! / 1024).toInt() > 1024 -> {
                                                        fileDetails.fileSize =
                                                                (fuguUploadImageResponse.data.imageSize / (1024 * 1024)).toString() + " MB"
                                                    }
                                                    else -> {
                                                        fileDetails.fileSize =
                                                                (fuguUploadImageResponse.data.imageSize / 1024).toString() + " KB"
                                                    }
                                                }
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                        com.skeleton.mvp.fugudatabase.CommonData.setFilesMap(
                                                uuid,
                                                fuguUploadImageResponse?.data?.url,
                                                fileDetails.filePath
                                        )
                                        com.skeleton.mvp.fugudatabase.CommonData.setFileLocalPath(uuid, fileDetails.filePath)
                                        com.skeleton.mvp.fugudatabase.CommonData.setCachedFilePath(
                                                fuguUploadImageResponse?.data?.url,
                                                uuid,
                                                fileDetails.filePath
                                        )
                                        val commonResponseData = CommonData.getCommonResponse().data
                                        val workspacesInfo = commonResponseData.workspacesInfo
                                        val currentPosition = CommonData.getCurrentSignedInPosition()
                                        val commonParams = CommonParams.Builder()
                                                .add("file_name", fileDetails.fileName)
                                                .add("file_size", fileDetails.fileSize)
                                                .add("url", fuguUploadImageResponse?.data?.url)
                                                .add("muid", fuguUploadImageResponse?.data?.muid)
                                                .add("task_id", intent.getLongExtra("eventId", -1L))
                                                .add("user_id", workspacesInfo[currentPosition].userId)
                                        if (!TextUtils.isEmpty(etSubmissionContent?.text.toString()))
                                            commonParams.add("content", etSubmissionContent?.text.toString())
                                        RestClient.getApiInterface(true).submitTask(commonResponseData.userInfo.accessToken, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                                                .enqueue(object : ResponseResolver<CommonResponse>() {
                                                    override fun success(t: CommonResponse?) {
                                                        hideLoading()
                                                        showToast("Task Submitted Successfully.")
                                                        val datetime = Calendar.getInstance().time
                                                        btnAssignTask?.gone()
                                                        tvAttachFile?.gone()
                                                        switchAttachFile?.gone()
                                                        rlSubmittedFile?.visible()
                                                        tvUploadFile?.gone()
                                                        tvFileName?.text = fileDetails.fileName
                                                        tvFileSize?.text = fileDetails.fileSize
                                                        tvSubmissionDateTime?.text = "${getFormattedDate(datetime)} ${getFormattedTime(datetime)}"
                                                        if (!TextUtils.isEmpty(etSubmissionContent?.text.toString())) {
                                                            etSubmissionContent?.isFocusable = false
                                                        } else
                                                            etSubmissionContent?.gone()
                                                    }

                                                    override fun failure(error: APIError?) {
                                                        hideLoading()
                                                        showErrorMessage(error?.message
                                                                ?: "Some Error Occurred")
                                                    }

                                                })
                                    }
                                }
                            }.start()

                        }

                        override fun failure(error: APIError?) {
                            if (error?.message == UNEXPECTED_ERROR_OCCURRED) {
                                try {
                                    AlertDialog.Builder(this@TaskActivity)
                                            .setMessage(error.message)
                                            .setPositiveButton("Ok", null)
                                            .setCancelable(false)
                                            .show()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else {

                            }
                        }
                    })
        } catch (e: Exception) {
        }
    }

    private fun setTransparentBg(editable: TextView) {
        editable.setBackgroundColor(Color.parseColor("#00000000"))
        editable.setPadding(0, 0, 0, 0)
    }

    private fun setRectGreyBg(editable: TextView) {
        editable.setBackgroundResource(R.drawable.round_rect_grey_border)
        val px = Utils.dpToPx(this, 10f)
        editable.setPadding(px, px, px, px)
    }

    override fun onProgressUpdate(percentage: Int, messageIndex: Int, muid: String?) {

    }

    override fun onError(percentage: Int, messageIndex: Int, muid: String?) {
        showErrorMessage("Upload error occurred at $percentage%")
    }

    override fun onFinish(percentage: Int, messageIndex: Int, muid: String?) {

    }

    /**
     * Format a 24 Hour time into 12 Hour time string.
     *
     * @param hours Hours in 24 hour format
     * @param minutes Minutes for the time.
     * @return The formatted string with 12 Hours time including AM/PM
     */
    private fun getFormattedTime(hours: Int, minutes: Int): String {
        Log.i("SelectedTime", "Hours = $hours -- Minutes = $minutes")
        val m = if (minutes < 10)
            "0$minutes"
        else
            minutes
        val suffix = if (hours >= 12)
            "PM"
        else
            "AM"
        var hours = hours % 12
        hours = if (hours == 0)
            12
        else
            hours
        return "$hours:$m $suffix"
    }

    /**
     * Format a Date object to dd/MM/yyyy
     *
     * @param date An object of Date
     * @return The formatted string with 12 Hours time including AM/PM
     */
    private fun getFormattedDate(date: Date): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        return sdf.format(date)
    }

    private fun getReminderTime(minutes: Int): String {
        var hours: Int = minutes / 60
        val days: Int = hours / 24
        hours -= (days * 24)
        val min: Int = minutes - (((days * 24) + hours) * 60)
        return if (days != 0)
            if (days == 1)
                "$days Day"
            else
                "$days Days"
        else if (hours != 0)
            if (hours == 1)
                "$hours Hour"
            else
                "$hours Hours"
        else
            "$min Min"
    }
}
