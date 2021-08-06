package com.skeleton.mvp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.FuguConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.adapter.MultipleContactsAdapter
import com.skeleton.mvp.adapter.SelectedAdapter
import com.skeleton.mvp.constant.FuguAppConstant.ANDROID
import com.skeleton.mvp.constant.FuguAppConstant.SESSION_EXPIRE
import com.skeleton.mvp.data.model.invitation.InvitationResponse
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.data.network.CommonParams
import com.skeleton.mvp.data.network.ResponseResolver
import com.skeleton.mvp.data.network.RestClient
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.model.ContactsList
import com.skeleton.mvp.model.inviteContacts.GetUserContactsResponse
import com.skeleton.mvp.model.inviteContacts.WorkspaceContact
import com.skeleton.mvp.payment.CalculatePaymentActivity
import com.skeleton.mvp.ui.AppConstants.EMAILS
import com.skeleton.mvp.ui.AppConstants.WORKSPACE_ID
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.ui.base.BaseView
import com.skeleton.mvp.ui.intro.IntroActivity
import com.skeleton.mvp.util.Log
import com.skeleton.mvp.util.SearchAnimationToolbar
import kotlinx.android.synthetic.main.activity_multiple_invite.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class MultipleInviteActivity : BaseActivity(), MultipleContactsAdapter.SetTags, SearchAnimationToolbar.OnSearchQueryChangedListener {


    var contactsList = ArrayList<ContactsList>()
    internal var phoneContactsObject = JSONObject()
    internal var phoneContactsArray = JSONArray()
    lateinit var multipleContactsAdapter: MultipleContactsAdapter
    lateinit var selectedAdapter: SelectedAdapter
    var isPermissionGranted = false
    var isHitRequired = false
    var phoneNumbers = ArrayList<String>()
    var emails = ArrayList<String>()
    var countryCodes = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiple_invite)
        initViews()
    }

    private fun initViews() {
        rvContactsList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        rvSelected.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        selectedAdapter = SelectedAdapter(ArrayList())
        rvSelected.adapter = selectedAdapter
        Thread {
            kotlin.run {
//                if (CommonData.getUserContacts() != null) {
//                    contactsList.addAll(CommonData.getUserContacts())
//                    Log.e("test", "test1")
//                }
                if (CommonData.getWorkspaceContacts() != null) {
                    contactsList.addAll(CommonData.getWorkspaceContacts())
                    Log.e("test", "test2")
                }
                runOnUiThread {
                    multipleContactsAdapter = MultipleContactsAdapter(contactsList, this)
                    rvContactsList.adapter = multipleContactsAdapter
                }
            }

        }.start()

        toolbar?.setSupportActionBar(this)
        toolbar?.setOnSearchQueryChangedListener(this)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        fabInvite.setOnClickListener {
            if (isNetworkConnected) {
                if (phoneNumbers.size > 0 || emails.size > 0) {
                    showLoading()
                    apiInviteMembers()
                } else {
                    Toast.makeText(this, "Please select atleast one contact!", Toast.LENGTH_SHORT).show()
                }
            } else {
                showErrorMessage("Please check your Internet Connection!")
            }
        }
        apiGetUserContacts()
    }

    private fun apiInviteMembers() {
        val emailJsonArray = JSONArray(emails)
        val contactArray = JSONArray()

        for (i in phoneNumbers.indices) {
            val contactObject = JSONObject()
            try {
                contactObject.put("contact_number", phoneNumbers[i])
                contactObject.put("country_code", countryCodes[i])
                contactArray.put(contactObject)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        val commonParams = CommonParams.Builder()
        if (emails.size > 0) {
            commonParams.add(EMAILS, emailJsonArray)
        }
        if (phoneNumbers.size > 0) {
            commonParams.add("contact_numbers", contactArray)
        }
        commonParams.add(WORKSPACE_ID, com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo.get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).workspaceId)
        RestClient.getApiInterface(true).inviteUsers(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), BuildConfig.VERSION_CODE, ANDROID, commonParams.build().map)
                .enqueue(object : ResponseResolver<InvitationResponse>() {
                    override fun onSuccess(invitationResponse: InvitationResponse) {
                        hideLoading()
                        showErrorMessage("Invitation sent successfully", object : BaseView.OnErrorHandleCallback {
                            override fun onErrorCallback() {
                                finishAffinity()
                                val view = this@MultipleInviteActivity.currentFocus
                                if (view != null) {
                                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                                }
                                val intent = Intent(this@MultipleInviteActivity, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                            }
                        })
                    }

                    override fun onError(error: ApiError) {
                        hideLoading()
                        if (error.statusCode == SESSION_EXPIRE) {
                            com.skeleton.mvp.data.db.CommonData.clearData()
                            FuguConfig.clearFuguData(this@MultipleInviteActivity)
                            finishAffinity()
                            startActivity(Intent(this@MultipleInviteActivity, IntroActivity::class.java))
                        } else if (error.statusCode == 402) {
                            startActivity(Intent(this@MultipleInviteActivity, CalculatePaymentActivity::class.java))
                            finish()
                        } else {
                            showErrorMessage(error.message)
                        }
                    }

                    override fun onFailure(throwable: Throwable) {
                        hideLoading()
                    }
                })
    }

    override fun setTags(list: ArrayList<String>, phoneNumbers: ArrayList<String>, emails: ArrayList<String>, countryCodes: ArrayList<String>) {
        if (list.size > 0) {
            rvSelected.visibility = View.VISIBLE
        } else {
            rvSelected.visibility = View.GONE
        }
        selectedAdapter.update(list)
        selectedAdapter.notifyDataSetChanged()
        rvSelected.scrollToPosition(list.size - 1)
        this.emails = emails
        this.phoneNumbers = phoneNumbers
        this.countryCodes = countryCodes
    }

    override fun onSearchCollapsed() {

    }

    override fun onSearchQueryChanged(query: String?) {
        multipleContactsAdapter.updateList(getAllFilteredList(query!!))
        multipleContactsAdapter.notifyDataSetChanged()

    }

    override fun onSearchExpanded() {
    }

    override fun onSearchSubmitted(query: String?) {
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.sync_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val itemId = item.itemId

        if (itemId == R.id.action_search) {
            toolbar?.onSearchIconClick()
            return true
        } else if (itemId == R.id.sync) {
            isHitRequired = true

            return true
        } else if (itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }



    private fun apiGetUserContacts() {
        val commonParams = CommonParams.Builder()
                .add("workspace_id", com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].workspaceId)
                .add("contact_type", "CONTACTS")
                .build()
        RestClient.getApiInterface(true).getUserContacts(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), BuildConfig.VERSION_CODE, ANDROID, commonParams.map)
                .enqueue(object : ResponseResolver<GetUserContactsResponse>() {
                    override fun onSuccess(getUserContactsResponse: GetUserContactsResponse) {
                        object : Thread() {
                            override fun run() {
                                super.run()
                                val workspaceContacts = getUserContactsResponse.data.workspaceContacts as java.util.ArrayList<WorkspaceContact>
                                var workspaceContact = ArrayList<ContactsList>()
                                for (i in workspaceContacts.indices) {
                                    if (!TextUtils.isEmpty(workspaceContacts[i].email) && !workspaceContacts[i].email.contains("@fuguchat.com") && !workspaceContacts[i].email.contains("@junglework.auth")) {
                                        if (!workspaceContacts[i].email.equals(com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.email)) {
                                            workspaceContact.add(ContactsList(workspaceContacts[i].fullName,
                                                    workspaceContacts[i].email, false))
                                        }
                                    }
                                    if (!TextUtils.isEmpty(workspaceContacts[i].contactNumber)) {
                                        if (!workspaceContacts[i].contactNumber.equals(com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.userInfo.contactNumber)) {
                                            workspaceContact.add(ContactsList(workspaceContacts[i].fullName,
                                                    workspaceContacts[i].contactNumber, false))
                                        }
                                    }

                                }
                                contactsList.addAll(workspaceContact)
                                com.skeleton.mvp.fugudatabase.CommonData.setWorkspaceContacts(workspaceContact)
                                runOnUiThread {
                                    multipleContactsAdapter.updateList(contactsList)
                                    multipleContactsAdapter.notifyDataSetChanged()
                                }
                            }
                        }.start()
                        hideLoading()
                    }

                    override fun onError(error: ApiError) {
                        hideLoading()
                    }

                    override fun onFailure(throwable: Throwable) {
                        hideLoading()
                    }
                })
    }

    private fun getAllFilteredList(text: String): java.util.ArrayList<ContactsList> {
        val newfilteredGroupList = java.util.ArrayList<ContactsList>()
        newfilteredGroupList.clear()
        for (i in contactsList.indices) {
            if (contactsList.get(i).data.toLowerCase().contains(text.toLowerCase())) {
                newfilteredGroupList.add(contactsList.get(i))
            }
        }
        return newfilteredGroupList
    }

}
