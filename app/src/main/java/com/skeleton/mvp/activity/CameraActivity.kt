package com.skeleton.mvp.activity

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.hardware.Camera
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.utils.CameraPreview
import java.io.*

class CameraActivity : BaseActivity() {
    private var mCamera: Camera? = null
    private var mPreview: CameraPreview? = null
    private var mButton: Button? = null
    private var TAG = "CameraActivity"
    private var retake: AppCompatImageView? = null
    private var done: AppCompatImageView? = null
    private var cancel: AppCompatImageView? = null
    private var clickedImage: AppCompatImageView? = null
    private var llOptions: LinearLayout? = null
    private var llButton: LinearLayout? = null
    private var preview: RelativeLayout? = null
    private var pbLoader: ProgressBar? = null
    private var llScanner: RelativeLayout? = null
    private var rlScanner: RelativeLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        mButton = findViewById(R.id.button_capture)
        llOptions = findViewById(R.id.llOptions)
        llButton = findViewById(R.id.llButton)
        retake = findViewById(R.id.retake)
        done = findViewById(R.id.done)
        clickedImage = findViewById(R.id.clickedImage)
        cancel = findViewById(R.id.cancel)
        pbLoader = findViewById(R.id.pbLoader)
        rlScanner = findViewById(R.id.rlScanner)
        llScanner = findViewById(R.id.llScanner)
        llScanner?.visibility = View.GONE
        setUpCamera()

        mButton?.setOnClickListener {
            llScanner?.visibility = View.VISIBLE
            val anim = TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0f,
                    TranslateAnimation.RELATIVE_TO_SELF, 0f,
                    TranslateAnimation.RELATIVE_TO_SELF, 0f,
                    TranslateAnimation.RELATIVE_TO_SELF, 5.5f) // this is distance of top and bottom form current positiong

            anim.duration = 1500
            anim.repeatCount = 20
            anim.repeatMode = Animation.REVERSE
            llScanner?.startAnimation(anim)
            try {
                mCamera?.takePicture(null, null, mPicture)
                llButton?.visibility = View.GONE
                pbLoader?.visibility = View.VISIBLE
            } catch (e: Exception) {
                mCamera?.release()
                e.printStackTrace()
            }
        }
        retake?.setOnClickListener {
            llScanner?.visibility = View.GONE
            llScanner?.clearAnimation()
            rlScanner?.visibility=View.VISIBLE
            setUpCamera()
            llButton?.visibility = View.VISIBLE
            llOptions?.visibility = View.GONE
            preview?.visibility = View.VISIBLE
            clickedImage?.visibility = View.GONE
        }
        done?.setOnClickListener {
            mCamera?.release()
            setResult(Activity.RESULT_OK)
            finish()
        }
        cancel?.setOnClickListener {
            onBackPressed()
        }

    }

    override fun onResume() {
        super.onResume()
        llButton?.visibility = View.VISIBLE
        llOptions?.visibility = View.GONE
        preview?.visibility = View.VISIBLE
        clickedImage?.visibility = View.GONE

    }

    private fun setUpCamera() {
        if (checkCameraHardware(this)) {
            mCamera = getCameraInstance()
            mCamera?.setDisplayOrientation(90)
            mPreview = mCamera?.let {
                // Create our Preview view
                CameraPreview(this, it)
            }


            mPreview?.also {
                preview = findViewById(R.id.camera_preview)
                var params: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                preview?.addView(it, 0, params)

            }
        }
    }

    private val mPicture = Camera.PictureCallback { data, _ ->
        val pictureFile: File = getOutputMediaFile(MEDIA_TYPE_IMAGE) ?: run {
            Log.d(TAG, ("Error creating media file, check storage permissions"))
            return@PictureCallback
        }

        Handler().postDelayed({
            try {
                rlScanner?.visibility = View.GONE
                val fos = FileOutputStream(pictureFile)
                fos.write(data)
                fos.close()

                try {
                    val mediaStorageDir = File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                            FuguAppConstant.ATTENDANCE)
                    val finalFile = File("${mediaStorageDir.path}${File.separator}IMG_FUGU.jpg")
                    val angle = -90f
                    val mat = Matrix()
                    mat.postRotate(angle)
                    val bmp = BitmapFactory.decodeStream(FileInputStream(finalFile), null, null)!!
                    mat.postScale(-1f, 1f, bmp.width / 2f, bmp.height / 2f)
                    val correctBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, mat, true)
                    val d = BitmapDrawable(resources, correctBmp)

                    Glide.with(this)
                            .load(d)
                            .into(clickedImage!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }


                preview?.visibility = View.GONE
                clickedImage?.visibility = View.VISIBLE
                llButton?.visibility = View.GONE
                llOptions?.visibility = View.VISIBLE
                pbLoader?.visibility = View.GONE

            } catch (e: FileNotFoundException) {
                showErrorMessage(e.message)
                Log.d(TAG, "File not found: ${e.message}")
            } catch (e: IOException) {
                showErrorMessage(e.message)
                Log.d(TAG, "Error accessing file: ${e.message}")
            }
        }, 3000)
    }


    /** Create a File for saving an image or video */
    private fun getOutputMediaFile(type: Int): File? {
        val mediaStorageDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                FuguAppConstant.ATTENDANCE
        )
        mediaStorageDir.apply {
            if (!exists()) {
                if (!mkdirs()) {
                    Log.d(FuguAppConstant.ATTENDANCE, "failed to create directory")
                    return null
                }
            }
        }
        return when (type) {
            MEDIA_TYPE_IMAGE -> {
                File("${mediaStorageDir.path}${File.separator}IMG_FUGU.jpg")
            }
            MEDIA_TYPE_VIDEO -> {
                File("${mediaStorageDir.path}${File.separator}VID_FUGU.mp4")
            }
            else -> null
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    /** Check if this device has a camera */
    private fun checkCameraHardware(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    /** A safe way to get an instance of the Camera object. */
    fun getCameraInstance(): Camera? {
        return try {
            Camera.open(1) // attempt to get a Camera instance
        } catch (e: Exception) {
            try {
                Camera.open(0)
            } catch (e: Exception) {

            }
            null
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

}
