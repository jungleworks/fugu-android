package com.skeleton.mvp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import com.hbb20.CountryCodePicker
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.REQ_CODE_INVITE_GUEST
import com.skeleton.mvp.retrofit.APIError
import com.skeleton.mvp.retrofit.ResponseResolver
import com.skeleton.mvp.ui.AppConstants
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.util.ExtendedEditText
import com.skeleton.mvp.util.ValidationUtil
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class InviteGuestsActivity : BaseActivity() {

    private var llEmails: LinearLayout? = null
    private var btnNext: AppCompatButton? = null
    private val emailEts = ArrayList<ExtendedEditText>()
    private val addMap = HashMap<ExtendedEditText, Boolean>()
    private val countryCodePickers = ArrayList<CountryCodePicker>()
    private val emails = ArrayList<String>()
    private val numbers = ArrayList<String>()
    private val countryCodes = ArrayList<String>()
    private var ivBack: AppCompatImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_guests)

        llEmails = findViewById(R.id.llEmails)
        btnNext = findViewById(R.id.btnNext)
        emailEts.add(findViewById<View>(R.id.etOne) as ExtendedEditText)
        emailEts.add(findViewById<View>(R.id.etTwo) as ExtendedEditText)
        countryCodePickers.add(findViewById<View>(R.id.etCountryCode) as CountryCodePicker)
        countryCodePickers.add(findViewById<View>(R.id.etCountryCode2) as CountryCodePicker)
        emailEts[0].addTextChangedListener(MyTextWatcher(0))
        emailEts[1].addTextChangedListener(MyTextWatcher(1))
        addMap.put(emailEts[0], true)
        addMap.put(emailEts[1], false)
        ivBack = findViewById(R.id.ivBack)

        ivBack?.setOnClickListener {
            onBackPressed()
        }

        btnNext?.setOnClickListener {
            emails.clear()
            numbers.clear()
            for (i in emailEts.indices) {
                if (!TextUtils.isEmpty(emailEts[i].text.toString().trim { it <= ' ' })) {
                    val regexStr = "^[0-9]*$"
                    if (emailEts[i].text.toString().trim { it <= ' ' }.matches(regexStr.toRegex())) {
                        numbers.add("+" + countryCodePickers[i].selectedCountryCode.trim { it <= ' ' } + "-" + emailEts[i].text.toString().trim { it <= ' ' })
                        countryCodes.add(countryCodePickers[i].selectedCountryNameCode.trim { it <= ' ' })
                    } else {
                        emails.add(emailEts[i].text.toString().trim { it <= ' ' })
                    }
                }
            }


            if (emails.size == 0 && numbers.size == 0) {
                showErrorMessage("Please enter a valid email/phone number")
            }

            var invite = false
            if(numbers.size == 0 && validateEmailsAndInvite()){
                val intent = Intent(this@InviteGuestsActivity, GuestContactsAccessActivity::class.java)
                intent.putExtra("emailArray", emails)
                intent.putExtra("phoneArray", numbers)
                intent.putExtra("countryCodeArray", countryCodes)
                startActivityForResult(intent, REQ_CODE_INVITE_GUEST)
            }
            if (numbers.size > 0) {
                if(emails.size == 0){
                    invite = true
                } else {
                    invite = emails.size != 0 && validateEmailsAndInvite()
                }
            }
            if (invite) {
                if (isNetworkConnected) {
                    showLoading()
                    apiCheckInvitedContacts()
                } else {
                    showErrorMessage(R.string.error_internet_not_connected)
                }
            }

        }

    }

    private fun apiCheckInvitedContacts() {

        val contactArray = JSONArray()
        var contactJsonArray: ArrayList<String>? = null
        try {
            contactJsonArray = numbers
        } catch (e: java.lang.Exception) {

        }
        var countryCodesJsonArray: ArrayList<String>? = null
        try {
            countryCodesJsonArray = countryCodes
        } catch (e: Exception) {

        }

        val fcCommonResponse = com.skeleton.mvp.data.db.CommonData.getCommonResponse()
        val commonParams = com.skeleton.mvp.data.network.CommonParams.Builder()

        if (contactJsonArray != null) {

            for (i in contactJsonArray.indices) {
                val contactObject = JSONObject()
                try {
                    contactObject.put("contact_number", contactJsonArray.get(i))
                    contactObject.put("country_code", countryCodesJsonArray?.get(i))
                    contactArray.put(contactObject)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            if (contactArray.length() > 0) {
                commonParams.add("contact_numbers", contactArray)
            }
        }
        commonParams.add(AppConstants.WORKSPACE_ID, fcCommonResponse.getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].workspaceId)


        com.skeleton.mvp.data.network.RestClient.getApiInterface(true).checkInvitedContacts(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), BuildConfig.VERSION_CODE, FuguAppConstant.ANDROID, commonParams.build().map)
                .enqueue(object : ResponseResolver<com.skeleton.mvp.data.model.CommonResponse>() {
                    override fun success(t: com.skeleton.mvp.data.model.CommonResponse?) {
                        hideLoading()
                        val intent = Intent(this@InviteGuestsActivity, GuestContactsAccessActivity::class.java)
                        intent.putExtra("emailArray", emails)
                        intent.putExtra("phoneArray", numbers)
                        intent.putExtra("countryCodeArray", countryCodes)
                        startActivityForResult(intent, REQ_CODE_INVITE_GUEST)
                    }

                    override fun failure(error: APIError?) {
                        hideLoading()
                        showErrorMessage(error?.message)
                    }

                })
    }

    private fun validateEmailsAndInvite(): Boolean {
        var suffix: String
        var invite = false
        for (j in emails.indices) {
            if (!ValidationUtil.checkEmail(emails[j])) {
                when (j) {
                    0 -> suffix = "st"
                    1 -> suffix = "nd"
                    2 -> suffix = "rd"
                    else -> suffix = "th"
                }
                if (emails.size == 1) {
                    invite = false
                    showErrorMessage("Please enter a valid email")
                    break
                } else {
                    invite = false
                    showErrorMessage("Please enter a valid email at " + (j + 1) + suffix + " email entry")
                    break
                }
            } else {
                invite = true

            }
        }
        return invite
    }

    //MyTextWatcher to add view as soon as user types a character in last added edit text
    inner class MyTextWatcher constructor(private val position: Int) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (emailEts[emailEts.size - 1].isFocused && (!addMap.containsKey(emailEts[emailEts.size - 1]) || addMap.containsKey(emailEts[emailEts.size - 1]) && !addMap.get(emailEts[emailEts.size - 1])!!)) {
                val view: View = layoutInflater.inflate(R.layout.item_invite, llEmails, false)
                addMap.put(emailEts[emailEts.size - 1], true)
                view.findViewById<View>(R.id.etEmail).clearFocus()
                llEmails!!.addView(view)
                val etEmail = llEmails!!.getChildAt(position + 1).findViewById<ExtendedEditText>(R.id.etEmail)
                val etCountryCode = llEmails!!.getChildAt(position + 1).findViewById<CountryCodePicker>(R.id.etCountryCode3)
                emailEts.add(etEmail)
                countryCodePickers.add(etCountryCode)
                emailEts[position + 1].addTextChangedListener(MyTextWatcher(position + 1))
                emailEts[position + 1].onItemClickListener = AdapterView.OnItemClickListener { parent, _, itemPosition, _ ->
                    val selectedItem = parent.getItemAtPosition(itemPosition).toString().split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (TextUtils.isDigitsOnly(selectedItem[selectedItem.size - 1])) {
                        emailEts[position + 1].setText(selectedItem[selectedItem.size - 1])
                        emailEts[position + 1].setSelection(selectedItem[selectedItem.size - 1].length)
                        countryCodePickers[position + 1].setCountryForPhoneCode(Integer.parseInt(selectedItem[0]))
                    } else {
                        emailEts[position + 1].setText(parent.getItemAtPosition(itemPosition).toString())
                        emailEts[position + 1].setSelection(parent.getItemAtPosition(itemPosition).toString().length)
                    }
                }
            }
            val regexStr = "^[0-9]*$"
            if (s.isNotEmpty()) {
                if (s.toString().matches(regexStr.toRegex())) {
                    countryCodePickers.get(position).visibility = View.VISIBLE
                    emailEts[position].setPadding(0, 18, 7, 18)
                } else {
                    countryCodePickers.get(position).visibility = View.GONE
                    emailEts[position].setPadding(40, 18, 7, 18)
                }
            } else if (s.length == 0) {
                countryCodePickers.get(position).visibility = View.GONE
                emailEts[position].setPadding(40, 18, 7, 18)
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQ_CODE_INVITE_GUEST) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

}
