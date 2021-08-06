package com.skeleton.mvp.ui.creategroup

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mikepenz.itemanimators.ScaleUpAnimator
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.FuguConfig
import com.skeleton.mvp.FuguConfig.clearFuguData
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.ChatActivity
import com.skeleton.mvp.activity.MainActivity
import com.skeleton.mvp.adapter.CreateGroupListAdapter
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.PERMISSION_CONSTANT_GALLERY
import com.skeleton.mvp.data.db.ChatDatabase
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.creategroup.CreateGroupResponse
import com.skeleton.mvp.data.model.creategroup.MembersInfo
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.data.network.MultipartParams
import com.skeleton.mvp.data.network.ResponseResolver
import com.skeleton.mvp.data.network.RestClient
import com.skeleton.mvp.model.FuguConversation
import com.skeleton.mvp.model.FuguSearchResult
import com.skeleton.mvp.model.GetAllMembers
import com.skeleton.mvp.ui.AppConstants.*
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.ui.dialog.CustomAlertDialog
import com.skeleton.mvp.ui.intro.IntroActivity
import com.skeleton.mvp.utils.FuguImageUtils
import com.skeleton.mvp.utils.StringUtil
import com.theartofdev.edmodo.cropper.CropImage
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class CreateGroupActivity : BaseActivity(), View.OnClickListener {
    private val searchText: String? = null
    private var rvSearchResult: androidx.recyclerview.widget.RecyclerView? = null
    private var fcCommonResponse: FcCommonResponse? = null
    private var fuguSearchResultLinkedHashMap: HashMap<Long, GetAllMembers> = LinkedHashMap()
    private val searchResultList = ArrayList<FuguSearchResult>()
    private val usersToBeAdded = TreeMap<String, Long>()
    private var tvSkipAndCreateGroup: TextView? = null
    private var tvNoResultsFound: TextView? = null
    private var etGroupName: EditText? = null
    private var etGroupName2: EditText? = null
    private var ivBack: ImageView? = null
    private var sPrivate: SwitchCompat? = null
    private var tvPrivate: TextView? = null
    private var tvSubPrivate: TextView? = null
    private var chatType = FuguAppConstant.ChatType.PRIVATE_GROUP
    private var llAddMembers: LinearLayout? = null
    private var createGroupListAdapter: CreateGroupListAdapter? = null
    private var tvMembers: TextView? = null
    private var fabCreateGroup: FloatingActionButton? = null
    private var imageUri: String? = null
    private var ivGroupPhoto: ImageView? = null
    private var extension = "jpg"
    var fuguImageUtils:FuguImageUtils?=null
    /**
     * Method to check whether the Camera feature
     * is Available or not
     *
     * @return
     */
    private val isCameraAvailable: Boolean
        get() = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)

    /**
     * Method to check whether the Camera feature
     * is Available or not
     *
     * @return
     */
    private val isExternalStorageAvailable: Boolean
        get() = Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)
        fuguImageUtils = FuguImageUtils(this)
        fuguImageUtils?.setCallbaks(FuguAppConstant.OPEN_CAMERA_ADD_IMAGE, FuguAppConstant.REQUEST_CODE_PICK_IMAGE, FuguAppConstant.REQUEST_CODE_PICK_FILE,
                FuguAppConstant.REQUEST_CODE_PICK_AUDIO, FuguAppConstant.REQUEST_CODE_PICK_VIDEO, FuguAppConstant.START_POLL, true, true)
        fcCommonResponse = CommonData.getCommonResponse()
        tvSkipAndCreateGroup = findViewById(R.id.tvSkipAndCreate)
        rvSearchResult = findViewById(R.id.rvSearchResult)
        etGroupName = findViewById(R.id.etGroupName)
        etGroupName2 = findViewById(R.id.etGroupName2)
        tvMembers = findViewById(R.id.tvMembers)
        tvNoResultsFound = findViewById(R.id.tvNoResultsFound)
        fabCreateGroup = findViewById(R.id.fabCreateGroup)
        sPrivate = findViewById(R.id.sPrivate)
        tvPrivate = findViewById(R.id.tvPrivate)
        tvSubPrivate = findViewById(R.id.tvSubPrivate)
        llAddMembers = findViewById(R.id.llAddMembers)
        ivGroupPhoto = findViewById(R.id.ivGroupPhoto)
        ivGroupPhoto!!.setOnClickListener(this)
        ivBack = findViewById(R.id.ivBack)
        createGroupListAdapter = CreateGroupListAdapter(fuguSearchResultLinkedHashMap, this)
        rvSearchResult!!.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, Utility.calculateNoOfColumns(applicationContext))
        rvSearchResult!!.itemAnimator = ScaleUpAnimator()
        rvSearchResult!!.adapter = createGroupListAdapter
        fabCreateGroup!!.setOnClickListener(this)
        tvSkipAndCreateGroup!!.setOnClickListener(this)
        ivBack!!.setOnClickListener { onBackPressed() }
        sPrivate!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!buttonView.isChecked) {
                tvPrivate!!.text = getString(R.string.public_text)
                tvSubPrivate!!.text = getString(R.string.public_group_desc)
                chatType = FuguAppConstant.ChatType.PUBLIC_GROUP
            } else {
                tvPrivate!!.text = getString(R.string.private_text)
                tvSubPrivate!!.text = getString(R.string.private_group_desc)
                chatType = FuguAppConstant.ChatType.PRIVATE_GROUP
            }
        }

        if (intent.hasExtra(EXTRA_CREATE_GROUP_FROM_SEARCH)) {
            fuguSearchResultLinkedHashMap.clear()
            val str = intent.getStringExtra("createGroupMap")
            val gson = Gson()
            val entityType = object : TypeToken<LinkedHashMap<Long, GetAllMembers>>() {

            }.type
            fuguSearchResultLinkedHashMap = gson.fromJson(str, entityType)
            createGroupListAdapter!!.updateMap(fuguSearchResultLinkedHashMap)
            createGroupListAdapter!!.notifyDataSetChanged()
            updateMap(fuguSearchResultLinkedHashMap)
        }

    }

    private fun createGroup() {
        if (fuguSearchResultLinkedHashMap.keys.size < 1) {
            showErrorMessage(getString(R.string.you_need_to_add_atleast_three_members_to_create_a_group))
        } else {
            if (isNetworkConnected) {
                showLoading()
                apiCreateGroup()
            } else {
                showErrorMessage(R.string.error_internet_not_connected)
            }
        }
    }

    private fun apiCreateGroup() {
        val commonParams = createBody()
        RestClient.getApiInterface(false).createGroup(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), fcCommonResponse!!.getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.map)
                .enqueue(object : ResponseResolver<CreateGroupResponse>() {
                    override fun onSuccess(createGroupResponse: CreateGroupResponse) {
                        val channelId = java.lang.Long.valueOf(createGroupResponse.data.channelId!!.toLong())
                        val chatIntent = Intent(this@CreateGroupActivity, ChatActivity::class.java)
                        val conversation = FuguConversation()
                        conversation.businessName = createGroupResponse.data.label
                        conversation.isOpenChat = true
                        conversation.channelId = channelId
                        conversation.chat_type = chatType
                        conversation.url = createGroupResponse.data.url
                        conversation.thumbnailUrl = createGroupResponse.data.thumbnailUrl
                        if (!TextUtils.isEmpty(createGroupResponse.data.label)) {
                            conversation.label = createGroupResponse.data.label
                        } else {
                            var name = ""
                            for (i in 0 until createGroupResponse.data.membersInfo.size) {
                                if (i > 0) {
                                    name = "$name, "
                                }
                                name = name + createGroupResponse.data.membersInfo[i].fullName
                            }
                            conversation.label = name
                        }
                        conversation.userName = StringUtil.toCamelCase(CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].fullName)
                        conversation.userId = java.lang.Long.valueOf(CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].userId)
                        conversation.enUserId = CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].enUserId
                        conversation.membersInfo = createGroupResponse.data.membersInfo as ArrayList<MembersInfo>
                        conversation.thumbnailUrl = "https://fuguchat.s3.ap-south-1.amazonaws.com/default/WwX5qYGSEb_1518441286074.png"
                        chatIntent.putExtra(FuguAppConstant.CONVERSATION, Gson().toJson(conversation, FuguConversation::class.java))
                        Thread {
                            kotlin.run {
                                val conversationMap = ChatDatabase.getConversationMap(CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey)
                                conversationMap[channelId] = conversation
                                ChatDatabase.setConversationMap(conversationMap, CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].fuguSecretKey)
                            }
                        }.start()
                        hideLoading()
                        if (intent.hasExtra(EXTRA_CREATE_GROUP_FROM_SEARCH)) {
                            val homeScreen = Intent(this@CreateGroupActivity, MainActivity::class.java)
                            startActivities(arrayOf(homeScreen, chatIntent))
                            finishAffinity()
                        } else {
                            startActivity(chatIntent)
                            finish()
                        }
                    }

                    override fun onError(error: ApiError) {
                        hideLoading()
                        if (error.statusCode == FuguAppConstant.SESSION_EXPIRE) {
                            CommonData.clearData()

                            clearFuguData(this@CreateGroupActivity)
                            finishAffinity()
                            startActivity(Intent(this@CreateGroupActivity, IntroActivity::class.java))
                        } else {
                            showErrorMessage(error.message)
                        }
                    }

                    override fun onFailure(throwable: Throwable) {
                        hideLoading()
                    }
                })
    }

    /**
     * create group body
     *
     * @return ccommon param for create group
     */
    private fun createBody(): MultipartParams {
        var userIds: JSONArray? = null
        usersToBeAdded.clear()
        val userIdsToBeAdded = fuguSearchResultLinkedHashMap.keys.toTypedArray()
        userIds = JSONArray(Arrays.asList(*userIdsToBeAdded))
        val multipartParams = MultipartParams.Builder()
        multipartParams.add(FuguAppConstant.EN_USER_ID, CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].enUserId)
        multipartParams.add(USER_IDS_TO_ADD, userIds)
        if (!TextUtils.isEmpty(etGroupName!!.text.toString())) {
            multipartParams.add(CUSTOM_LABEL, etGroupName!!.text.toString().trim { it <= ' ' })
        }
        multipartParams.add("chat_type", chatType)
        if (!TextUtils.isEmpty(imageUri)) {
            multipartParams.addFile("files", File(imageUri!!))
        }

        return multipartParams.build()
    }

    override fun onBackPressed() {
        val view = this@CreateGroupActivity.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        val mIntent = Intent()
        val gson = Gson()
        val list = gson.toJson(fuguSearchResultLinkedHashMap)
        mIntent.putExtra("createGroupMap", list)
        setResult(Activity.RESULT_OK, mIntent)
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
        finish()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        etGroupName2!!.requestFocus()
        var tempUri: Uri? = null
            if (requestCode == REQUEST_CREATE_GROUP && resultCode == Activity.RESULT_OK && data != null) {
                fuguSearchResultLinkedHashMap.clear()
                val str = data.getStringExtra("searchResultMap")
                val gson = Gson()
                val entityType = object : TypeToken<LinkedHashMap<Long, FuguSearchResult>>() {

                }.type
                fuguSearchResultLinkedHashMap = gson.fromJson(str, entityType)
                createGroupListAdapter!!.updateMap(fuguSearchResultLinkedHashMap)
                if (fuguSearchResultLinkedHashMap.size > 0) {
                    rvSearchResult!!.visibility = View.VISIBLE
                    tvMembers!!.visibility = View.VISIBLE
                    tvMembers!!.text = "Members: " + fuguSearchResultLinkedHashMap.size
                } else {
                    tvMembers!!.visibility = View.GONE
                    rvSearchResult!!.visibility = View.GONE
                }
                createGroupListAdapter!!.notifyDataSetChanged()
            } else if (requestCode == FuguAppConstant.OPEN_CAMERA_ADD_IMAGE && resultCode == Activity.RESULT_OK) {
                try {
                    val fileName = CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].workspaceName.replace(" ", "").replace("'s", "") + "_" + com.skeleton.mvp.fugudatabase.CommonData.getTime() + ".jpg"
                    tempUri = Uri.fromFile(File(fuguImageUtils?.getDirectory(FuguAppConstant.FileType.IMAGE_FILE), fileName))


                    CropImage.activity(tempUri)
                            .setFixAspectRatio(true)
                            .start(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else if (requestCode == 1000 && data != null) {
                if (data != null) {
                    CropImage.activity(data.data)
                            .setFixAspectRatio(true)
                            .start(this)
                }
            }
        if (data != null) {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    val resultUri = result.uri

                    val options = RequestOptions()
                            .centerCrop()
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .fitCenter()
                            .priority(Priority.HIGH)
                            .transforms(CenterCrop(), RoundedCorners(100))


                    Glide.with(this)
                            .asBitmap()
                            .apply(options)
                            .load(resultUri)
                            .into(ivGroupPhoto!!)

                    imageUri = resultUri.path

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                }
            }
        }
    }

    fun updateMap(fuguSearchResultLinkedHashMap: HashMap<Long, GetAllMembers>?) {
        if (fuguSearchResultLinkedHashMap!!.size > 0) {
            rvSearchResult!!.visibility = View.VISIBLE
            tvMembers!!.visibility = View.VISIBLE
            tvMembers!!.text = "Members: " + fuguSearchResultLinkedHashMap.size
        } else {
            rvSearchResult!!.visibility = View.GONE
            tvMembers!!.visibility = View.GONE
        }
        this.fuguSearchResultLinkedHashMap = fuguSearchResultLinkedHashMap
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabCreateGroup -> createGroup()
            R.id.tvSkipAndCreate -> createGroup()
            R.id.ivGroupPhoto -> CustomAlertDialog.Builder(this@CreateGroupActivity)
                    .setTitle("Select option")
                    .setPositiveButton("Camera") { fuguImageUtils?.startCamera() }
                    .setNegativeButton("Gallery") { openGallery() }
                    .show()
            else -> {
            }
        }
    }

    object Utility {
        internal fun calculateNoOfColumns(context: Context): Int {
            val displayMetrics = context.resources.displayMetrics
            val dpWidth = displayMetrics.widthPixels / displayMetrics.density
            return (dpWidth / 80).toInt()
        }
    }

    /**
     * Method to open the Gallery view
     */

    fun openGallery() {

        // Check and ask for Permissions
        if (!FuguConfig.getInstance().askUserToGrantPermission(this@CreateGroupActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, "Please grant permission to Storage",
                        PERMISSION_CONSTANT_GALLERY))
            return

        try {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, 1000)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(applicationContext,
                    getString(R.string.no_gallery), Toast.LENGTH_SHORT).show()
        }

    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }


    companion object {
        private val REQUEST_CREATE_GROUP = 20202
    }
}
