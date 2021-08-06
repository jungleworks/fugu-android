package com.skeleton.mvp.utils

import android.content.Context
import android.hardware.Camera
import android.util.Size
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import com.skeleton.mvp.util.Log
import android.view.View.resolveSize
import android.view.ViewGroup
import java.io.IOException


class CameraPreview(private val mContext: Context, private val mCamera: Camera) : SurfaceView(mContext), SurfaceHolder.Callback {
    private val mHolder: SurfaceHolder
    private val mSupportedPreviewSizes: List<Camera.Size>?
    private var mPreviewSize: Camera.Size? = null

    init {
        // supported preview sizes
        mSupportedPreviewSizes = mCamera.parameters.supportedPreviewSizes
//        for (str in mSupportedPreviewSizes!!)
//            Log.e(TAG, str.width.toString() + "/" + str.height)

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = holder
        mHolder.addCallback(this)
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // empty. surfaceChanged will take care of stuff
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {
        Log.e("surfaceChanged", "surfaceChanged => w=$w, h=$h")
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        if (mHolder.surface == null) {
            // preview surface does not exist
            return
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview()
        } catch (e: Exception) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or reformatting changes here
        // start preview with new settings
        try {
            val parameters = mCamera.parameters

            Log.i("qwerty","asdfg")

            try{
                //Log.i("previewWidth",mPreviewSize!!.width.toString())
                //Log.i("previewHeight",mPreviewSize!!.height.toString())

                val sizes = parameters.getSupportedPictureSizes()
                var size: Camera.Size = sizes.get(0)
                for (i in sizes.indices) {
                    if (sizes.get(i).width > size.width)
                        size = sizes.get(i)
                }
                parameters.setPictureSize(size.width, size.height)
                parameters.setPreviewSize(mPreviewSize!!.width , mPreviewSize!!.height)

                //parameters.setPreviewSize(getOptimalPreviewSize(mSupportedPreviewSizes, mPreviewSize!!.width, mPreviewSize!!.height)!!.width,
                        //getOptimalPreviewSize(mSupportedPreviewSizes, mPreviewSize!!.width, mPreviewSize!!.height)!!.height)

            }
            catch (e: Exception){
                Log.i("errorCameraPreview","error")
                e.printStackTrace()
            }

            mCamera.parameters = parameters
            mCamera.setDisplayOrientation(90)
            mCamera.setPreviewDisplay(mHolder)
            mCamera.startPreview()

        } catch (e: Exception) {
            Log.d(TAG, "Error starting camera preview: " + e.message)
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.resolveSize(suggestedMinimumWidth, widthMeasureSpec)
        val height = View.resolveSize(suggestedMinimumHeight, heightMeasureSpec)

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height)
        }

        if (mPreviewSize != null) {
            var ratio: Float = 1f
            if (mPreviewSize!!.height >= mPreviewSize!!.width){

                try{
                    ratio = mPreviewSize!!.height.toFloat() / mPreviewSize!!.width.toFloat()
                }
                catch(e : Exception){
                    e.printStackTrace()
                }

            }

            else{
                try{
                    ratio = mPreviewSize!!.width.toFloat() / mPreviewSize!!.height.toFloat()
                }
                catch(e : Exception){
                    e.printStackTrace()
                }

            }

            // One of these methods should be used, second method squishes preview slightly
            setMeasuredDimension(width, (width * ratio).toInt())
            //        setMeasuredDimension((int) (width * ratio), height);
        }
    }


    private fun getOptimalPreviewSize(sizes: List<Camera.Size>?, w: Int, h: Int): Camera.Size? {
        val ASPECT_TOLERANCE = 0.1
        val targetRatio = h.toDouble() / w


        if (sizes == null)
            return null

        var optimalSize: Camera.Size? = null
        var minDiff = java.lang.Double.MAX_VALUE

        for (size in sizes) {
            val ratio = size.height.toDouble() / size.width
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue

            if (Math.abs(size.height - h) < minDiff) {
                optimalSize = size
                minDiff = Math.abs(size.height - h).toDouble()
            }
        }

        if (optimalSize == null) {
            minDiff = java.lang.Double.MAX_VALUE
            for (size in sizes) {
                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size
                    minDiff = Math.abs(size.height - h).toDouble()
                }
            }
        }
        return optimalSize
    }

    companion object {
        private val TAG = "CameraPreview"
    }
}
