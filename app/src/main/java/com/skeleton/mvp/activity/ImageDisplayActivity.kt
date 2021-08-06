package com.skeleton.mvp.activity

import android.Manifest
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.github.chrisbanes.photoview.PhotoView
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.FuguConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.*
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.model.FuguFileDetails
import com.skeleton.mvp.model.Image
import com.skeleton.mvp.model.Message
import com.skeleton.mvp.model.editInfo.EditInfoResponse
import com.skeleton.mvp.retrofit.APIError
import com.skeleton.mvp.retrofit.MultipartParams
import com.skeleton.mvp.retrofit.ResponseResolver
import com.skeleton.mvp.retrofit.RestClient
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.ui.dialog.CustomAlertDialog
import com.skeleton.mvp.util.Log
import com.skeleton.mvp.utils.DateUtils
import com.skeleton.mvp.utils.FuguImageUtils
import com.skeleton.mvp.utils.beatAnimation.AVLoadingIndicatorViewFugu
import com.theartofdev.edmodo.cropper.CropImage
import ua.zabelnikov.swipelayout.layout.frame.SwipeableLayout
import ua.zabelnikov.swipelayout.layout.listener.OnLayoutPercentageChangeListener
import ua.zabelnikov.swipelayout.layout.listener.OnLayoutSwipedListener
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*


class ImageDisplayActivity : BaseActivity() {
    var ivImageBig: PhotoView? = null
    var ivOriginalImage: PhotoView? = null
    var isTopBarVisivle = true
    var forwardMessage: Message? = null
    private var isDilogOpened = false
    private var extension = ""
    private var imageUrl = ""
    private var workspaceInfo: WorkspacesInfo? = null
    var fuguImageUtils: FuguImageUtils? = null
    private var angle = 0f
    var lastPerc = 0f
    var swipedFromDefault = OnLayoutSwipedListener.SWIPE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_display)
        ivImageBig = findViewById(R.id.ivImageBigNew)
        fuguImageUtils = FuguImageUtils(this)
        fuguImageUtils?.setCallbaks(FuguAppConstant.OPEN_CAMERA_ADD_IMAGE, REQUEST_CODE_PICK_IMAGE, REQUEST_CODE_PICK_FILE,
                REQUEST_CODE_PICK_AUDIO, REQUEST_CODE_PICK_VIDEO, FuguAppConstant.START_POLL, true, true)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val tvChannelName: TextView = findViewById(R.id.tvChannelName)
        val tvDateTime: TextView = findViewById(R.id.tvDateTime)
        val ivBack: ImageView = findViewById(R.id.ivBack)
        val ivEdit: ImageView = findViewById(R.id.ivEdit)
        val ivForward: ImageView = findViewById(R.id.ivForward)
        val swipeableLayout = findViewById<View>(R.id.swipeableLayout) as SwipeableLayout
        val colorContainer: FrameLayout = findViewById(R.id.colorContainer)
        swipeableLayout.setOnLayoutPercentageChangeListener(object : OnLayoutPercentageChangeListener() {
            override fun percentageY(percentage: Float) {
                Log.e("percent change", percentage.toString())
                if (lastPerc != 1.0f) {
                    colorContainer.alpha = 1 - percentage
                    toolbar.alpha = 1 - percentage
                }
                lastPerc = percentage
            }
        })

        swipeableLayout.setLayoutShiftListener { positionX, positionY, isTouched ->
            Log.e("position Y", positionY.toString())
            if (!isTouched && lastPerc != 1f && swipedFromDefault == OnLayoutSwipedListener.SWIPE) {
                colorContainer.alpha = 1f
                toolbar.alpha = 1f
            }
        }



        swipeableLayout.setOnSwipedListener(object : OnLayoutSwipedListener {
            override fun onLayoutSwiped(swipedFrom: Int) {
                swipedFromDefault = swipedFrom
                onBackPressed()
            }
        })
        toolbar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
//                R.id.forward -> forwardImage()
//                R.id.rotateLeft -> rotateImageToLeft(image.imageUrl)
//                R.id.rotateRight -> rotateImageToRight(image.imageUrl)
//                R.id.viewInGallery -> openImageInGallery(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", File(image.imageUrl)))
//                R.id.share -> shareImageToExternalApps(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", File(image.imageUrl)))
            }
            false
        }

        val image = intent.getSerializableExtra("image") as Image
        if (intent.hasExtra("MESSAGE")) {
            tvDateTime.visibility = View.VISIBLE
            if (image.imageUrl.split(".")[image.imageUrl.split(".").size - 1].contains("gif")) {
                ivForward.visibility = View.VISIBLE
            } else {
                toolbar.inflateMenu(R.menu.image_display)
            }

            ivEdit.visibility = View.GONE
        } else {
            tvDateTime.visibility = View.GONE
            if (intent.getBooleanExtra("editable", false)) {
                ivEdit.visibility = View.VISIBLE
            } else {
                ivEdit.visibility = View.GONE
            }
        }
        workspaceInfo = CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()]
        val pbLoading = findViewById<AVLoadingIndicatorViewFugu>(R.id.pbLoading)
        pbLoading.visibility = View.GONE

        if (!TextUtils.isEmpty(image.channelName)) {
            tvChannelName.text = image.channelName
        } else if (TextUtils.isEmpty(image.channelName) && intent.hasExtra("isFromProfileActivity") && intent.getBooleanExtra("isFromProfileActivity", true)) {
            tvChannelName.text = ""
        } else {
            tvChannelName.text = "Message"
        }
        val formatter = SimpleDateFormat("dd MMM, hh:mm a")
        val formatter2 = SimpleDateFormat("hh:mm a")
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        try {
            val date = formatter.format(sdf.parse(DateUtils.getInstance().convertToLocal(image.dateTime)))
            if (android.text.format.DateUtils.isToday(sdf.parse(DateUtils.getInstance().convertToLocal(image.dateTime)).time)) {
                val time = formatter2.format(sdf.parse(DateUtils.getInstance().convertToLocal(image.dateTime)))
                tvDateTime.text = "Today, $time"
            } else {
                tvDateTime.text = formatter.format(sdf.parse(DateUtils.getInstance().convertToLocal(image.dateTime)))
            }
        } catch (e: java.lang.Exception) {
            tvDateTime.visibility = View.GONE
        }

        ivEdit.setOnClickListener {
            CustomAlertDialog.Builder(this@ImageDisplayActivity)
                    .setTitle("Select option")
                    .setPositiveButton("Camera") { fuguImageUtils?.startCamera() }
                    .setNegativeButton("Gallery") { openGallery() }
                    .show()
        }

        ivForward.setOnClickListener {
            forwardImage()
        }

        toolbar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.forward -> forwardImage()
                R.id.rotateLeft -> rotateImageToLeft(image.imageUrl)
                R.id.rotateRight -> rotateImageToRight(image.imageUrl)
                R.id.viewInGallery -> openImageInGallery(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", File(image.imageUrl)))
                R.id.share -> shareImageToExternalApps(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", File(image.imageUrl)))
            }
            false
        }

        ivBack.setOnClickListener {
            onBackPressed()
        }
        ivImageBig?.setOnClickListener {
            if (isTopBarVisivle) {
                toolbar.animate().translationY(-150f).duration = 100
            } else {
                toolbar.animate().translationY(0f).duration = 100
            }
            isTopBarVisivle = !isTopBarVisivle
        }


        val requestOptions = RequestOptions()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontTransform()
        supportPostponeEnterTransition()

        if (image.thumbnailUrl.split(".")[image.thumbnailUrl.split(".").size - 1].equals("gif")) {
            val alphaAnimation = ObjectAnimator.ofFloat(toolbar, View.ALPHA, 0f, 1f)
            alphaAnimation.duration = 500
            alphaAnimation.startDelay = 200
            alphaAnimation.start()
            supportStartPostponedEnterTransition()
            Glide.with(this@ImageDisplayActivity)
                    .asGif()
                    .load(image.thumbnailUrl)
                    .into(ivImageBig!!)
        } else {
            ivImageBig?.transitionName = image.transitionName
            Glide.with(this@ImageDisplayActivity).load(image.thumbnailUrl)
                    .apply(requestOptions)
                    .into(object : SimpleTarget<Drawable>() {
                        override fun onResourceReady(resource: Drawable, transition: com.bumptech.glide.request.transition.Transition<in Drawable>?) {
                            val alphaAnimation = ObjectAnimator.ofFloat(toolbar, View.ALPHA, 0f, 1f)
                            alphaAnimation.duration = 500
                            alphaAnimation.startDelay = 200
                            alphaAnimation.start()
                            supportStartPostponedEnterTransition()
                            ivImageBig?.setImageDrawable(resource)
                        }
                    })
        }
//        swipeableLayout.isSwipeable(true)
        ivImageBig?.setOnDoubleTap { zoom ->
            Log.e("isSwipeable", zoom.toString())
            if (zoom <= 1.0f) {
                swipeableLayout.isSwipeable(true)
            } else {
                swipeableLayout.isSwipeable(false)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (!TextUtils.isEmpty(imageUrl)) {
            val intent = Intent()
            intent.putExtra("imageUrl", imageUrl)
            setResult(Activity.RESULT_OK, intent)
        }

        supportFinishAfterTransition()
    }

    override fun onResume() {
        super.onResume()
        try {
            unregisterReceiver(onComplete)
        } catch (e: java.lang.Exception) {
        }
        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private val onComplete = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Toast.makeText(this@ImageDisplayActivity, "Download Completed", Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * Method to open the Gallery view
     */

    fun openGallery() {
        isDilogOpened = true
        // Check and ask for Permissions
        if (!FuguConfig.getInstance().askUserToGrantPermission(this@ImageDisplayActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, "Please grant permission to Storage",
                        FuguAppConstant.PERMISSION_CONSTANT_GALLERY))
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

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(onComplete)
        } catch (e: java.lang.Exception) {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var tempUri: Uri? = null

        if (requestCode == OPEN_CAMERA_ADD_IMAGE && resultCode == Activity.RESULT_OK) {
            var fuguFileDetails: FuguFileDetails? = null
            try {
                val fileName = CommonData.getCommonResponse().getData().workspacesInfo[CommonData.getCurrentSignedInPosition()].workspaceName.replace(" ", "").replace("'s", "") + "_" + com.skeleton.mvp.fugudatabase.CommonData.getTime() + ".jpg"
                tempUri = Uri.fromFile(File(fuguImageUtils?.getDirectory(FuguAppConstant.FileType.IMAGE_FILE), fileName))


                CropImage.activity(tempUri)
                        .setFixAspectRatio(true)
                        .start(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (requestCode == 1000) {
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
                    apiEditChannelImage(resultUri)

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                }
            }
        }
    }

    private fun apiEditChannelImage(tempUri: Uri) {
        showLoading()
        val commonParams = MultipartParams.Builder()
        commonParams.add(FuguAppConstant.CHANNEL_ID, intent.getLongExtra("channelId", -1L))
        commonParams.add(FuguAppConstant.EN_USER_ID, workspaceInfo?.enUserId)
        if (!TextUtils.isEmpty(tempUri.path)) {
            var compressedImage: File? = null
//            try {
//                compressedImage = Compressor(this)
//                        .setMaxWidth(2048)
//                        .setMaxHeight(2048)
//                        .setQuality(75)
//                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
//                        .compressToFile(File(tempUri.path))
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }

            commonParams.addFile("files", File(tempUri.path))
            RestClient.getApiInterface().editChannelInfo(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), workspaceInfo?.fuguSecretKey, 1, BuildConfig.VERSION_CODE, commonParams.build().map)
                    .enqueue(object : ResponseResolver<EditInfoResponse>() {
                        override fun success(t: EditInfoResponse?) {
                            hideLoading()


                            val options = RequestOptions()
                                    .centerCrop()
                                    .dontAnimate()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .placeholder(R.drawable.placeholder)
                                    .error(R.drawable.placeholder)
                                    .fitCenter()
                                    .priority(Priority.HIGH)


                            Glide.with(this@ImageDisplayActivity)
                                    .asBitmap()
                                    .apply(options)
                                    .load(t!!.data.channelImageUrl.channelThumbnailUrl)
                                    .into(ivImageBig!!)

//                            Glide.with(this@ImageDisplayActivity).load(t!!.data.channelImageUrl.channelThumbnailUrl)
//                                    .placeholder(R.drawable.placeholder)
//                                    .dontAnimate()
//                                    .dontTransform()
//                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                                    .error(ContextCompat.getDrawable(this@ImageDisplayActivity, R.drawable.placeholder))
//                                    .into(ivImageBig)
                        }

                        override fun failure(error: APIError?) {
                            hideLoading()
                        }

                    })
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    fun openImageInGallery(path: Uri) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.setDataAndType(path, "image/*")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }

    fun shareImageToExternalApps(path: Uri) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        val screenshotUri = path
        try {
            val stream = contentResolver.openInputStream(screenshotUri)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        sharingIntent.type = "image/*"
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri)
        startActivity(Intent.createChooser(sharingIntent, "Share image using"))
    }

    fun rotateImageToRight(imageUrl: String) {
        val mat = Matrix()
        angle += 90f
        mat.postRotate(angle)
        val bmp = BitmapFactory.decodeStream(FileInputStream(File(imageUrl)), null, null)!!
        val correctBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, mat, true)
        val d = BitmapDrawable(resources, correctBmp)
        Glide.with(this@ImageDisplayActivity)
                .load(d)
                .into(ivImageBig!!)
    }

    fun forwardImage() {
        val mIntent = Intent(this, ForwardActivity::class.java)
        if (intent.hasExtra("MESSAGE")) {

            forwardMessage = intent.getSerializableExtra("MESSAGE") as Message


            if (TextUtils.isEmpty(forwardMessage?.sharableImage_url)) {
                forwardMessage?.sharableImage_url = forwardMessage?.image_url
            }

            if (TextUtils.isEmpty(forwardMessage?.sharableThumbnailUrl)) {
                forwardMessage?.sharableThumbnailUrl = forwardMessage?.thumbnailUrl
            }

            mIntent.putExtra("MESSAGE", forwardMessage)
            mIntent.putExtra("BUSINESS_NAME", intent.getStringExtra("BUSINESS_NAME"))
            mIntent.putExtra("chatType", intent.getStringExtra("chatType"))
            startActivity(mIntent)
        }
    }

    fun rotateImageToLeft(imageUrl: String) {
        val mat = Matrix()
        angle -= 90f
        mat.postRotate(angle)
        val bmp = BitmapFactory.decodeStream(FileInputStream(File(imageUrl)), null, null)!!
        val correctBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, mat, true)
        val d = BitmapDrawable(resources, correctBmp)
        Glide.with(this@ImageDisplayActivity)
                .load(d)
                .into(ivImageBig!!)
    }

}

