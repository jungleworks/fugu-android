package com.skeleton.mvp.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.LinearLayout
import com.skeleton.mvp.R
import com.skeleton.mvp.adapter.PollExpirySelectAdapter
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.fragment.PollRecyclerDialogFragment
import com.skeleton.mvp.ui.base.BaseActivity

class CreatePollActivity : BaseActivity(), View.OnClickListener, PollExpirySelectAdapter.SelectedPosition {

    private var llEdit: LinearLayout? = null
    private var llEditableDays: LinearLayout? = null
    private var llStatic: LinearLayout? = null
    private var tvPoll: AppCompatTextView? = null
    private var tvAddOption: AppCompatTextView? = null
    private var llOptions: LinearLayout? = null
    private var tvDays: AppCompatTextView? = null
    private var tvHours: AppCompatTextView? = null
    private var selectedDayPosition = 7
    private var selectedHourPosition = 0
    private var btnCreatePoll: AppCompatButton? = null
    private var etQuestion: AppCompatEditText? = null
    private var optionsList = ArrayList<AppCompatEditText>()
    private var optionsStringList = ArrayList<String>()
    private var cbMultipleSelect: CheckBox? = null
    private var POLL_RECYCLER_DIALOG_FRAGMENT_DAYS: String = "PollRecyclerDialogFragmentDays"
    private var POLL_RECYCLER_DIALOG_FRAGMENT_HOURS: String = "PollRecyclerDialogFragmentHours"
    private var DAYS: String = "Days"
    private var ivBack: AppCompatImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_poll)
        initViews()
        clickListeners()
    }

    private fun initViews() {
        llEdit = findViewById(R.id.llEdit)
        llEditableDays = findViewById(R.id.llEditableDays)
        llStatic = findViewById(R.id.llStatic)
        tvPoll = findViewById(R.id.tvPoll)
        tvAddOption = findViewById(R.id.tvAddOption)
        llOptions = findViewById(R.id.llOptions)
        tvDays = findViewById(R.id.tvDays)
        tvHours = findViewById(R.id.tvHours)
        btnCreatePoll = findViewById(R.id.btnCreatePoll)
        etQuestion = findViewById(R.id.etQuestion)
        optionsList.add(findViewById(R.id.option_one))
        optionsList.add(findViewById(R.id.option_two))
        cbMultipleSelect = findViewById(R.id.cbMultipleSelect)
        ivBack = findViewById(R.id.ivBack)
        ivBack?.setOnClickListener {
            onBackPressed()
        }
    }

    private fun clickListeners() {
        llEdit?.setOnClickListener(this)
        tvAddOption?.setOnClickListener(this)
        tvHours?.setOnClickListener(this)
        tvDays?.setOnClickListener(this)
        btnCreatePoll?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.llEdit -> {
                llStatic?.visibility = View.GONE
                llEditableDays?.visibility = View.VISIBLE
                tvPoll?.visibility = View.VISIBLE
            }
            R.id.tvAddOption -> {
                val view = layoutInflater.inflate(R.layout.row_option, llOptions, false)
                view.findViewById(R.id.tvOption) as AppCompatEditText
                view.clearFocus()
                (view as AppCompatEditText).hint = "Option " + (llOptions?.childCount!!.plus(1))
                llOptions?.addView(view)
                optionsList.add(view)
                if (llOptions?.childCount!! < 10) {
                } else {
                    tvAddOption?.text = "Maximum 10 options are allowed!"
                    tvAddOption?.setOnClickListener(null)
                }
            }
            R.id.tvDays -> {
                val manager = supportFragmentManager
                val ft = manager.beginTransaction()
                val newFragment = PollRecyclerDialogFragment.newInstance(0, this, 15, selectedDayPosition, "Days", 0)
                newFragment.show(ft, POLL_RECYCLER_DIALOG_FRAGMENT_DAYS)
            }
            R.id.tvHours -> {
                val manager = supportFragmentManager
                val ft = manager.beginTransaction()
                if (selectedDayPosition == 0) {
                    val newFragment = PollRecyclerDialogFragment.newInstance(0, this, 23, selectedHourPosition, "Hours", 1)
                    newFragment.show(ft, POLL_RECYCLER_DIALOG_FRAGMENT_HOURS)

                } else {
                    val newFragment = PollRecyclerDialogFragment.newInstance(0, this, 23, selectedHourPosition, "Hours", 0)
                    newFragment.show(ft, POLL_RECYCLER_DIALOG_FRAGMENT_HOURS)
                }
            }
            R.id.btnCreatePoll -> {
                if (TextUtils.isEmpty(etQuestion?.text!!.trim().toString())) {
                    showErrorMessage(R.string.error_add_question_to_create_poll)
                } else {
                    var numberOfOptions = 0
                    optionsStringList = ArrayList()
                    for (view in optionsList) {
                        if (!TextUtils.isEmpty(view.text.toString())) {
                            numberOfOptions += 1
                            optionsStringList.add(view.text.toString())
                        }
                    }
                    if (numberOfOptions < 2) {
                        showErrorMessage(R.string.error_add_atleast_two_options)
                    } else {
                        val view = this@CreatePollActivity.getCurrentFocus()
                        if (view != null) {
                            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(view!!.getWindowToken(), 0)
                        }
                        val intent = Intent()
                        intent.putExtra(QUESTION, etQuestion?.text!!.trim().toString())
                        intent.putStringArrayListExtra(POLL_OPTIONS, optionsStringList)
                        intent.putExtra(IS_MULTIPLE_SELECT, cbMultipleSelect?.isChecked)
                        intent.putExtra("is_expired", false)

                        intent.putExtra(EXPIRY_TIME, ((tvDays?.text.toString().toInt()) * 24 * 60 * 60 + ((tvHours?.text.toString().toInt()) * 60 * 60)).toLong())
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val view = this@CreatePollActivity.getCurrentFocus()
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view!!.getWindowToken(), 0)
        }
    }
    @SuppressLint("SetTextI18n")
    override fun onItemSelected(selectedPosition: Int, type: String, value: String) {
        if (type == DAYS) {
            selectedDayPosition = selectedPosition
            tvDays?.text = value
            if (selectedDayPosition == 0) {
                tvHours?.text = "01"
                selectedHourPosition = 0
            } else {
                tvHours?.text = "00"
                selectedHourPosition = 0
            }
            val prev = supportFragmentManager.findFragmentByTag(POLL_RECYCLER_DIALOG_FRAGMENT_DAYS)
            if (prev != null) {
                val df = prev as DialogFragment
                df.dismiss()
            }
        } else {
            selectedHourPosition = selectedPosition
            tvHours?.text = value
            val prev = supportFragmentManager.findFragmentByTag(POLL_RECYCLER_DIALOG_FRAGMENT_HOURS)
            if (prev != null) {
                val df = prev as DialogFragment
                df.dismiss()
            }
        }
    }
}
