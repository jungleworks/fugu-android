package com.skeleton.mvp.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.scheduleMeets.MeetDetails
import com.skeleton.mvp.data.network.RestClient
import com.skeleton.mvp.model.GetAllMembers
import com.skeleton.mvp.retrofit.APIError
import com.skeleton.mvp.retrofit.CommonParams
import com.skeleton.mvp.retrofit.CommonResponse
import com.skeleton.mvp.retrofit.ResponseResolver
import com.skeleton.mvp.ui.AppConstants
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.utils.*
import kotlinx.android.synthetic.main.activity_schedule_meet.*
import org.json.JSONArray
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ScheduleMeetActivity : BaseActivity(), View.OnClickListener {
    private var frequency: Int? = null
    private var startDateTime = Calendar.getInstance()
    private var endDateTime = Calendar.getInstance()
    private lateinit var cbEveryDay: CheckBox
    private lateinit var cbWeekdays: CheckBox
    private lateinit var cbEveryWeek: CheckBox
    private lateinit var cbMonthly: CheckBox
    private lateinit var tvMemberCount: TextView
    private var membersToInvite: HashMap<Long, GetAllMembers> = HashMap()
    private var reminderMinutes: Int = 10
    private var isEditMode: Boolean = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_meet)
        tvMemberCount = findViewById(R.id.tvMemberCount)
        val llInviteMembers: LinearLayout = findViewById(R.id.llInviteMembers)
        cbEveryDay = findViewById(R.id.cbEveryDay)
        cbWeekdays = findViewById(R.id.cbWeekdays)
        cbEveryWeek = findViewById(R.id.cbEveryWeek)
        cbMonthly = findViewById(R.id.cbMonthly)
        ivBack.setOnClickListener(this)
        cbEveryDay.setOnClickListener(this)
        cbWeekdays.setOnClickListener(this)
        cbEveryWeek.setOnClickListener(this)
        cbMonthly.setOnClickListener(this)
        llInviteMembers.setOnClickListener(this)
        endDateTime.add(Calendar.MINUTE, 10)
        if (intent.getBooleanExtra("isEditMode", false)) {
            isEditMode = true
            val meetDetails = intent.getSerializableExtra("meetDetails") as MeetDetails
            val dateUtils = DateUtils.getInstance()
            etNameOfMeeting.setText(meetDetails.title)
            startDateTime = Calendar.getInstance()
            startDateTime.time = dateUtils.getLocalDateObject(meetDetails.startDatetime)
            tvStartTime?.text = "${startDateTime.get(Calendar.DAY_OF_MONTH)}/${startDateTime.get(Calendar.MONTH) + 1}/${startDateTime.get(Calendar.YEAR)} ${getFormattedTime(startDateTime.get(Calendar.HOUR_OF_DAY), startDateTime.get(Calendar.MINUTE))}"
            endDateTime = Calendar.getInstance()
            endDateTime.time = dateUtils.getLocalDateObject(meetDetails.endDatetime)
            tvEndTime?.text = "${endDateTime.get(Calendar.DAY_OF_MONTH)}/${endDateTime.get(Calendar.MONTH) + 1}/${endDateTime.get(Calendar.YEAR)} ${getFormattedTime(endDateTime.get(Calendar.HOUR_OF_DAY), endDateTime.get(Calendar.MINUTE))}"
            reminderMinutes = meetDetails.reminderTime
            when (reminderMinutes) {
                10 -> spinnerReminderTime.setSelection(0)
                15 -> spinnerReminderTime.setSelection(1)
                30 -> spinnerReminderTime.setSelection(2)
                60 -> spinnerReminderTime.setSelection(3)
                else -> spinnerReminderTime.setSelection(0)
            }
            frequency = meetDetails.frequency
            when (frequency) {
                1 -> {
                    switchRepeatMeet.isChecked = true
                    cbEveryDay.isEnabled = true
                    cbEveryDay.isChecked = true
                }
                2 -> {
                    switchRepeatMeet.isChecked = true
                    cbEveryWeek.isEnabled = true
                    cbEveryWeek.isChecked = true
                }
                3 -> {
                    switchRepeatMeet.isChecked = true
                    cbWeekdays.isEnabled = true
                    cbWeekdays.isChecked = true
                }
                4 -> {
                    switchRepeatMeet.isChecked = true
                    cbMonthly.isEnabled = true
                    cbMonthly.isChecked = true
                }
                else -> switchRepeatMeet.isChecked = false
            }
            val commonResponseData = CommonData.getCommonResponse().data
            val workspacesInfo = commonResponseData.workspacesInfo
            val currentPosition = CommonData.getCurrentSignedInPosition()
            for (attendee in meetDetails.attendees) {
                if (attendee.userId != workspacesInfo[currentPosition].userId.toLong()) {
                    val member = GetAllMembers(attendee.userId, attendee.fullName, attendee.email, attendee.userThumbnailImage, attendee.userThumbnailImage, "", 0, "", "")
                    membersToInvite[attendee.userId] = member
                }
            }
            tvMemberCount.text = membersToInvite.size.toString()
            ivDeleteMeet?.visible()
            btnSchedule?.text = "Update Meeting"
            ivDeleteMeet?.setOnClickListener {
                val commonParams = CommonParams.Builder()
                        .add(FuguAppConstant.USER_ID, workspacesInfo[currentPosition].userId)
                        .add("workspace_id", workspacesInfo[currentPosition].workspaceId)
                        .add("meet_id", intent.getLongExtra("meet_id", -1L))
                        .add("is_deleted", 1)
                RestClient.getApiInterface(true).editMeeting(commonResponseData.userInfo.accessToken, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                        .enqueue(object : ResponseResolver<CommonResponse>() {
                            override fun success(t: CommonResponse?) {
                                showToast("Meeting Deleted Successfully")
                                setResult(Activity.RESULT_OK)
                                this@ScheduleMeetActivity.finish()
                            }

                            override fun failure(error: APIError?) {
                                showErrorMessage(error?.message ?: "Error")
                            }

                        })
            }
        }

        switchRepeatMeet.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                cbEveryDay.isEnabled = false
                cbWeekdays.isEnabled = false
                cbEveryWeek.isEnabled = false
                cbMonthly.isEnabled = false
            } else {
                cbEveryDay.isEnabled = true
                cbWeekdays.isEnabled = true
                cbEveryWeek.isEnabled = true
                cbMonthly.isEnabled = true
            }
        }

        tvStartTime?.setOnClickListener {
            showDialogs("start")
        }

        tvEndTime?.setOnClickListener {
            showDialogs("end")
        }

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

        btnSchedule.setOnClickListener {
            val nameOfMeet = etNameOfMeeting?.text.toString().trim()
            if (TextUtils.isEmpty(nameOfMeet)) {
                showErrorMessage("Meeting name cannot be empty.")
                return@setOnClickListener
            }
            val current = Calendar.getInstance().time
            val temp = startDateTime.clone() as Calendar
            temp.add(Calendar.MINUTE, 1)
            if (!isEditMode && startDateTime.timeInMillis < Calendar.getInstance().timeInMillis) {
                showErrorMessage("Please choose a meet start time greater than ${getFormattedDate(current)} ${com.skeleton.mvp.utils.getFormattedTime(current)}.")
                return@setOnClickListener
            } else if (startDateTime.timeInMillis > endDateTime.timeInMillis) {
                showErrorMessage("Meeting start time cannot be greater than meeting end time.")
                return@setOnClickListener
            } else if (temp.timeInMillis > endDateTime.timeInMillis) {
                showErrorMessage("Meeting start and end time cannot have same value.")
                return@setOnClickListener
            }
            val attendees = ArrayList<Long>()
            for (member in membersToInvite.values) {
                attendees.add(member.userId)
            }
            if (attendees.isEmpty()) {
                showErrorMessage("You should have at least 1 attendee for the meeting.")
                return@setOnClickListener
            }
            val commonResponseData = CommonData.getCommonResponse().data
            val workspacesInfo = commonResponseData.workspacesInfo
            val currentPosition = CommonData.getCurrentSignedInPosition()
            val dateUtils = DateUtils.getInstance()
            val sb = StringBuilder()
            val random = Random()
            val allowedCharacters = "qwertyuiopasdfghjklzxcvbnm"
            for (i in 0 until 10)
                sb.append(allowedCharacters[random.nextInt(allowedCharacters.length)])
            val commonParams = CommonParams.Builder()
                    .add(FuguAppConstant.USER_ID, workspacesInfo[currentPosition].userId)
                    .add("workspace_id", workspacesInfo[currentPosition].workspaceId)
                    .add("title", nameOfMeet)
                    .add("start_datetime", dateUtils.convertToUTC(DateUtils.getFormattedDate(startDateTime.time)))
                    .add("end_datetime", dateUtils.convertToUTC(DateUtils.getFormattedDate(endDateTime.time)))
                    .add("reminder_time", reminderMinutes)
                    .add("meet_type", FuguAppConstant.MeetType.JITSI.toString())
                    .add("room_id", sb.toString())
                    .add("attendees", JSONArray(attendees))
            if (switchRepeatMeet.isChecked)
                commonParams.add("frequency", frequency)
            if (isEditMode) {
                commonParams.add("meet_id", intent.getLongExtra("meet_id", -1L))
                RestClient.getApiInterface(true).editMeeting(commonResponseData.userInfo.accessToken, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                        .enqueue(object : ResponseResolver<CommonResponse>() {
                            override fun success(t: CommonResponse?) {
                                showToast(t?.message ?: "Meeting Updated Successfully")
                                setResult(Activity.RESULT_OK)
                                this@ScheduleMeetActivity.finish()
                            }

                            override fun failure(error: APIError?) {
                                showErrorMessage(error?.message ?: "Error")
                            }

                        })
            } else {
                commonParams.addDomain()
                RestClient.getApiInterface(true).scheduleMeeting(commonResponseData.userInfo.accessToken, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                        .enqueue(object : ResponseResolver<CommonResponse>() {
                            override fun success(t: CommonResponse?) {
                                showToast(t?.message ?: "Meeting Scheduled Successfully")
                                setResult(Activity.RESULT_OK)
                                this@ScheduleMeetActivity.finish()
                            }

                            override fun failure(error: APIError?) {
                                showErrorMessage(error?.message ?: "Error")
                            }

                        })
            }
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
            val datePickerDialog = DatePickerDialog(this@ScheduleMeetActivity, { _, year, month, dayOfMonth ->
                selectedDate = dayOfMonth
                selectedMonth = month
                selectedYear = year
                val timePickerDialog = TimePickerDialog(this@ScheduleMeetActivity, { _, hourOfDay, minute ->
                    selectedHour = hourOfDay
                    selectedMinute = minute
                    calendar.set(selectedYear, selectedMonth, selectedDate, selectedHour, selectedMinute, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    when (type) {
                        "start" -> {
                            this.startDateTime = calendar
                            tvStartTime?.text = "$selectedDate/${selectedMonth + 1}/$selectedYear ${getFormattedTime(selectedHour, selectedMinute)}"
                        }
                        "end" -> {
                            this.endDateTime = calendar
                            tvEndTime?.text = "$selectedDate/${selectedMonth + 1}/$selectedYear ${getFormattedTime(selectedHour, selectedMinute)}"
                        }
                    }
                }, if (type == "start") startDateTime.get(Calendar.HOUR_OF_DAY) else endDateTime.get(Calendar.HOUR_OF_DAY), if (type == "start") startDateTime.get(Calendar.MINUTE) else endDateTime.get(Calendar.MINUTE), false)
                timePickerDialog.show()
            }, if (type == "start") startDateTime.get(Calendar.YEAR) else endDateTime.get(Calendar.YEAR), if (type == "start") startDateTime.get(Calendar.MONTH) else endDateTime.get(Calendar.MONTH), if (type == "start") startDateTime.get(Calendar.DAY_OF_MONTH) else endDateTime.get(Calendar.DAY_OF_MONTH))
            datePickerDialog.show()
        }
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.ivBack -> {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
            R.id.cbEveryDay -> {
                cbEveryDay.isChecked = true
                cbWeekdays.isChecked = false
                cbEveryWeek.isChecked = false
                cbMonthly.isChecked = false
                frequency = 1
            }
            R.id.cbWeekdays -> {
                cbEveryDay.isChecked = false
                cbWeekdays.isChecked = true
                cbEveryWeek.isChecked = false
                cbMonthly.isChecked = false
                frequency = 3
            }
            R.id.cbEveryWeek -> {
                cbEveryDay.isChecked = false
                cbWeekdays.isChecked = false
                cbEveryWeek.isChecked = true
                cbMonthly.isChecked = false
                frequency = 2
            }
            R.id.cbMonthly -> {
                cbEveryDay.isChecked = false
                cbWeekdays.isChecked = false
                cbEveryWeek.isChecked = false
                cbMonthly.isChecked = true
                frequency = 4
            }
            R.id.llInviteMembers -> {
                val intent = Intent(this@ScheduleMeetActivity, MembersSearchActivity::class.java)
                intent.putExtra("searchingFor", "MEET_SCHEDULE")
                if (membersToInvite.isNotEmpty()) {
                    val list: String = Gson().toJson(membersToInvite)
                    intent.putExtra(AppConstants.SELECTED_MEMBERS, list)
                }
                startActivityForResult(intent, FuguAppConstant.RequestCodes.SELECT_MEMBERS_FOR_MEET_REQUEST)
            }
        }
    }

    /**
     * Format a 24 Hour time into 12 Hour time string.
     *
     * @param hours Hours in 24 hour format
     * @param minutes Minutes for the time.
     * @return The formatted string with 12 Hours time including AM/PM
     */
    private fun getFormattedTime(hours: Int, minutes: Int): String {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            FuguAppConstant.RequestCodes.SELECT_MEMBERS_FOR_MEET_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) {
                    val list = data!!.getStringExtra("list")
                    val entityType = object : TypeToken<LinkedHashMap<Long, GetAllMembers>>() {}.type
                    membersToInvite = Gson().fromJson(list, entityType)
                    tvMemberCount.text = membersToInvite.size.toString()
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
