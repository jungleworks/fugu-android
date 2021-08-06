package com.skeleton.mvp.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.github.chrisbanes.photoview.PhotoView
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.ImageDisplayViewpagerActivity
import com.skeleton.mvp.model.ImageItem
import com.skeleton.mvp.util.Log
import ua.zabelnikov.swipelayout.layout.frame.SwipeableLayout
import ua.zabelnikov.swipelayout.layout.listener.OnLayoutPercentageChangeListener
import ua.zabelnikov.swipelayout.layout.listener.OnLayoutSwipedListener
import java.io.File
import java.io.FileInputStream

class ImageItemSliderFragment : Fragment() {
    var isTopBarVisivle = true
    private var image: ImageItem? = null
    private var imageView: PhotoView? = null
    var lastPerc = 0f
    var swipedFromDefault = OnLayoutSwipedListener.SWIPE
    var myView: View? = null
    private var angle = 0f
    var swipeableLayout: SwipeableLayout? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        myView = inflater.inflate(R.layout.fragment_image_item_slider, container, false)
       try{
           iniitViews()
       }catch (e:Exception){
           e.printStackTrace()
       }
        return myView
    }

    private fun iniitViews() {
        swipeableLayout = myView?.findViewById<View>(R.id.swipeableLayout) as SwipeableLayout
        imageView = myView?.findViewById(R.id.image)
        imageView?.transitionName = image?.transitionName
        val requestOptions = RequestOptions()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontTransform()
        Glide.with(activity!!).load(image?.absolutepath)
                .apply(requestOptions)
                .into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: com.bumptech.glide.request.transition.Transition<in Drawable>?) {
                        Handler().postDelayed({ activity?.supportStartPostponedEnterTransition() }, 100)
                        imageView?.setImageDrawable(resource)
                    }
                })

        val colorContainer: FrameLayout = myView?.findViewById(R.id.colorContainer)!!
        swipeableLayout?.setOnLayoutPercentageChangeListener(object : OnLayoutPercentageChangeListener() {
            override fun percentageY(percentage: Float) {
                Log.e("percent change", percentage.toString())
                if (lastPerc != 1.0f) {
                    colorContainer.alpha = 1 - percentage
                    (activity as ImageDisplayViewpagerActivity).setAlpha(1 - percentage)
                }
                lastPerc = percentage
            }
        })

        swipeableLayout?.setLayoutShiftListener { positionX, positionY, isTouched ->
            Log.e("position Y", positionY.toString())
            if (!isTouched && lastPerc != 1f && swipedFromDefault == OnLayoutSwipedListener.SWIPE) {
                colorContainer.alpha = 1f
                if (activity != null) {
                    (activity as ImageDisplayViewpagerActivity).setAlpha(1f)
                }
            }
        }
        imageView?.setOnClickListener {
            try {
                if (isTopBarVisivle) {
                    (activity as ImageDisplayViewpagerActivity).showToolbar(false)
                } else {
                    (activity as ImageDisplayViewpagerActivity).showToolbar(true)
                }
                isTopBarVisivle = !isTopBarVisivle
            } catch (e: Exception) {
            }
        }


        swipeableLayout?.setOnSwipedListener(object : OnLayoutSwipedListener {
            override fun onLayoutSwiped(swipedFrom: Int) {
                swipedFromDefault = swipedFrom
                activity?.onBackPressed()
            }
        })
        imageView?.setOnDoubleTap { zoom ->
            Log.e("isSwipeable", zoom.toString())
            if (zoom <= 1.0f) {
                swipeableLayout?.isSwipeable(true)
            } else {
                swipeableLayout?.isSwipeable(false)
            }
        }

    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            Handler().postDelayed({
                val downTime = SystemClock.uptimeMillis()
                val eventTime = SystemClock.uptimeMillis() + 100
                val x = 0.0f
                val y = 0.0f
                val metaState = 0
                val motionEvent = MotionEvent.obtain(
                        downTime,
                        eventTime,
                        MotionEvent.ACTION_UP,
                        x,
                        y,
                        metaState
                )
                swipeableLayout?.dispatchTouchEvent(motionEvent)
            }, 100)

        }
    }

    fun rotateImageToRight(imageUrl: String) {
        val mat = Matrix()
        angle += 90f
        mat.postRotate(angle)
        val bmp = BitmapFactory.decodeStream(FileInputStream(File(imageUrl)), null, null)!!
        val correctBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, mat, true)
        val d = BitmapDrawable(resources, correctBmp)
        Glide.with(activity as ImageDisplayViewpagerActivity)
                .load(d)
                .into(imageView!!)
    }

    fun rotateImageToLeft(imageUrl: String) {
        val mat = Matrix()
        angle -= 90f
        mat.postRotate(angle)
        val bmp = BitmapFactory.decodeStream(FileInputStream(File(imageUrl)), null, null)!!
        val correctBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, mat, true)
        val d = BitmapDrawable(resources, correctBmp)
        Glide.with(activity as ImageDisplayViewpagerActivity)
                .load(d)
                .into(imageView!!)
    }

    fun newInstance(arg: Int, image: ImageItem): ImageItemSliderFragment {
        val frag = ImageItemSliderFragment()
        val args = Bundle()
        frag.arguments = args
        frag.setImage(image)
        return frag
    }

    private fun setImage(image: ImageItem) {
        this.image = image
    }

    interface SetMainLayoutAlpha {
        fun setAlpha(alpha: Float)
        fun showToolbar(boolean: Boolean)
    }
}