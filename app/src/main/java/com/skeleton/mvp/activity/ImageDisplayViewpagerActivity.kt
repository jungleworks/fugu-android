package com.skeleton.mvp.activity

import android.animation.ObjectAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.SharedElementCallback
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo
import com.skeleton.mvp.fragment.ImageItemSliderFragment
import com.skeleton.mvp.model.ImageItem
import com.skeleton.mvp.model.Message
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.util.CustomPageTransformer
import com.skeleton.mvp.util.ViewPagerFixed
import com.skeleton.mvp.utils.DateUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Suppress("UNCHECKED_CAST")
class ImageDisplayViewpagerActivity : BaseActivity(), ImageItemSliderFragment.SetMainLayoutAlpha {
    override fun setAlpha(alpha: Float) {
        mainRoot?.alpha = alpha
        toolbar?.alpha = alpha
    }

    override fun showToolbar(boolean: Boolean) {
        if (boolean) {
            toolbar?.animate()?.translationY(0f)?.duration = 100
        } else {
            toolbar?.animate()?.translationY(-150f)?.duration = 100
        }
    }

    var isTopBarVisivle = true
    private var isDilogOpened = false
    private var extension = ""
    private var imageUrl = ""
    private var toolbar: Toolbar? = null
    private var workspaceInfo: WorkspacesInfo? = null
    private var message: Message? = null
    var imagePager: ViewPagerFixed? = null
    var imageList = ArrayList<ImageItem>()
    var presentViewHolderList = ArrayList<String>()
    var mainRoot: FrameLayout? = null
    var rootFrame: FrameLayout? = null
    var isChatActivity: Boolean = true
    var adapter: ViewPagerAdapter? = null
    var appSecretKey: String = ""
    var channelId: Long = -1L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_display_viewpager)
        imagePager = findViewById(R.id.imagePager)
        mainRoot = findViewById(R.id.mainRoot)
        rootFrame = findViewById(R.id.rootFrame)

        toolbar = findViewById(R.id.toolbar)
        val tvChannelName: TextView? = findViewById(R.id.tvChannelName)
        val tvDateTime: TextView? = findViewById(R.id.tvDateTime)
        val ivBack: ImageView? = findViewById(R.id.ivBack)

        ActivityCompat.postponeEnterTransition(this)
        imageList = intent.getSerializableExtra("imageList") as ArrayList<ImageItem>
        presentViewHolderList = intent.getStringArrayListExtra("presentViewHolderList")!!
        isChatActivity = intent.getBooleanExtra("isChatActivity", true)
        appSecretKey = intent.getStringExtra(FuguAppConstant.APP_SECRET_KEY)!!
        channelId = intent.getLongExtra(FuguAppConstant.CHANNEL_ID, -1L)
        tvChannelName?.text = intent.getStringExtra("channelName")

        val formatter = SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH)
        val formatter2 = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        var position: Int
        position = if (isChatActivity) {
            ChatActivity.PagerPosition.currentViewPagerPosition
        } else {
            FuguInnerChatActivity.currentViewPagerposition
        }
        if (imageList[position].message != null) {
            toolbar?.inflateMenu(R.menu.image_display)
        } else {
            toolbar?.inflateMenu(R.menu.image_display_without_forward)
        }
        try {
            val localDateTime = sdf.parse(DateUtils.getInstance().convertToLocal(imageList[position].datetime))
            if (android.text.format.DateUtils.isToday(localDateTime.time)) {
                val time = formatter2.format(localDateTime)
                tvDateTime?.text = "Today, $time"
            } else {
                tvDateTime?.text = formatter.format(localDateTime)
            }
        } catch (e: java.lang.Exception) {
            tvDateTime?.visibility = View.GONE
        }
        setupViewPager(imagePager)
        prepareSharedElementTransition()
        ivBack?.setOnClickListener { onBackPressed() }

        toolbar?.setOnMenuItemClickListener { item: MenuItem ->
            if (isChatActivity) {
                position = ChatActivity.PagerPosition.currentViewPagerPosition
            } else {
                position = FuguInnerChatActivity.currentViewPagerposition
            }
            when (item.itemId) {
                R.id.forward -> forwardImage()
                R.id.rotateLeft -> (adapter?.getItem(imagePager?.currentItem!!) as ImageItemSliderFragment).rotateImageToLeft(imageList[position].absolutepath)
                R.id.rotateRight -> (adapter?.getItem(imagePager?.currentItem!!) as ImageItemSliderFragment).rotateImageToRight(imageList[position].absolutepath)
                R.id.viewInGallery -> openImageInGallery(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", File(imageList[position].absolutepath)))
                R.id.share -> shareImageToExternalApps(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", File(imageList[position].absolutepath)), imageList[position].absolutepath)
            }
            false
        }


        imagePager?.addOnPageChangeListener(
                object : ViewPager.SimpleOnPageChangeListener() {
                    override fun onPageSelected(position: Int) {
                        try {
                            val localDateTime = sdf.parse(DateUtils.getInstance().convertToLocal(imageList[position].datetime))
                            if (android.text.format.DateUtils.isToday(localDateTime.time)) {
                                val time = formatter2.format(localDateTime)
                                tvDateTime?.text = "Today, $time"
                            } else {
                                tvDateTime?.text = formatter.format(localDateTime)
                            }
                        } catch (e: java.lang.Exception) {
                            tvDateTime?.visibility = View.GONE
                        }
                        if (isChatActivity) {
                            ChatActivity.PagerPosition.currentViewPagerPosition = position
                        } else {
                            FuguInnerChatActivity.currentViewPagerposition = position
                        }
                        toolbar?.menu?.clear()
                        if (imageList[position].message != null) {
                            toolbar?.inflateMenu(R.menu.image_display)
                        } else {
                            toolbar?.inflateMenu(R.menu.image_display_without_forward)
                        }
                    }


                })
        imagePager?.setPageTransformer(true, CustomPageTransformer())
    }

    private fun setupViewPager(imagePager: ViewPager?) {
        if (imageList.size > 0) {
            try {
                adapter = ViewPagerAdapter(supportFragmentManager)

                for (image in imageList) {
                    val fragment2 = ImageItemSliderFragment().newInstance(0, image)
                    adapter?.addFrag(fragment2)
                }
                imagePager?.adapter = adapter
                if (isChatActivity) {
                    imagePager?.currentItem = ChatActivity.PagerPosition.currentViewPagerPosition
                } else {
                    imagePager?.currentItem = FuguInnerChatActivity.currentViewPagerposition
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showErrorMessage("Unexpected Error! Cannot read file.")
                onBackPressed()
            }
        } else {
            finish()
        }
    }


    inner class ViewPagerAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFrag(fragment: Fragment) {
            mFragmentList.add(fragment)
        }
    }

    private fun prepareSharedElementTransition() {
        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(names: MutableList<String>, sharedElements: MutableMap<String, View>) {
                try {
                    val currentFragment = if (isChatActivity) {
                        imagePager?.adapter!!.instantiateItem(imagePager!!, ChatActivity.PagerPosition.currentViewPagerPosition) as Fragment
                    } else {
                        imagePager?.adapter!!.instantiateItem(imagePager!!, FuguInnerChatActivity.currentViewPagerposition) as Fragment
                    }
                    val view = currentFragment.view ?: return
                    sharedElements[names[0]] = view.findViewById(R.id.image)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    override fun onBackPressed() {
        if (!presentViewHolderList.contains(imageList[imagePager?.currentItem!!].transitionName)) {
            val alphaAnimation = ObjectAnimator.ofFloat(rootFrame!!, View.ALPHA, 1f, 0f)
            alphaAnimation.duration = 200
            alphaAnimation.start()

            Handler().postDelayed({
                finish()
                overridePendingTransition(R.anim.right_in_fast, R.anim.right_out_fast)
            }, 200)
        } else {
            super.onBackPressed()
        }
    }

    private fun forwardImage() {
        val mIntent = Intent(this, ForwardActivity::class.java)
        var position = 0
        if (isChatActivity) {
            position = ChatActivity.PagerPosition.currentViewPagerPosition
        } else {
            position = FuguInnerChatActivity.currentViewPagerposition
        }
        val forwardMessage = imageList[position].message

        mIntent.putExtra("MESSAGE", forwardMessage)
        mIntent.putExtra("BUSINESS_NAME", intent.getStringExtra("channelName"))
        mIntent.putExtra("chatType", intent.getStringExtra("chatType"))
        startActivity(mIntent)
    }

    fun openImageInGallery(path: Uri) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.setDataAndType(path, "image/*")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }

    fun shareImageToExternalApps(path: Uri, localpath: String) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        val screenshotUri = path

        val exif = ExifInterface(localpath)
        exif.setAttribute(ExifInterface.TAG_MAKE, "")
        exif.setAttribute(ExifInterface.TAG_MODEL, "")
        exif.saveAttributes()

        sharingIntent.type = "image/*"
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri)
        startActivity(Intent.createChooser(sharingIntent, "Share image using"))
    }

    private val mDeleteMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val channelIdReceived = intent.getLongExtra(FuguAppConstant.CHANNEL_ID, -1L)
            if (intent.hasExtra(FuguAppConstant.APP_SECRET_KEY)
                    && intent.getStringExtra(FuguAppConstant.APP_SECRET_KEY) == appSecretKey
                    && channelIdReceived.compareTo(channelId) == 0) {
                for (image in 0 until imageList.size) {
                    if (imageList[image].muid == intent.getStringExtra(FuguAppConstant.MESSAGE_UNIQUE_ID)) {
                        when {
                            ChatActivity.PagerPosition.currentViewPagerPosition == image -> {
                                if (imageList.size == 1) {
                                    finish()
                                }
                                imageList.remove(imageList[image])
                                setupViewPager(imagePager)
                            }
                            image < ChatActivity.PagerPosition.currentViewPagerPosition -> {
                                ChatActivity.PagerPosition.currentViewPagerPosition = ChatActivity.PagerPosition.currentViewPagerPosition - 1
                                imageList.remove(imageList[image])
                                setupViewPager(imagePager)
                            }
                            image > ChatActivity.PagerPosition.currentViewPagerPosition -> {
                                imageList.remove(imageList[image])
                                setupViewPager(imagePager)
                            }
                        }
                        break
                    }
                }
            }
        }

    }

    private val mThreadDeleteChatReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val channelIdReceived = intent.getLongExtra(FuguAppConstant.CHANNEL_ID, -1L)
            if (intent.hasExtra(FuguAppConstant.APP_SECRET_KEY)
                    && intent.getStringExtra(FuguAppConstant.APP_SECRET_KEY) == appSecretKey
                    && channelIdReceived.compareTo(channelId) == 0) {
                for (image in 0 until imageList.size) {
                    if (imageList[image].muid == intent.getStringExtra("thread_muid")) {
                        when {
                            imageList.size == 1 -> {
                                finish()
                            }
                            image != imageList.size - 1 -> {
                                imagePager?.currentItem = image + 1
                            }
                            else -> {
                                imagePager?.currentItem = image - 1
                            }
                        }
                        imageList.remove(imageList[image])
                        setupViewPager(imagePager)
                        break
                    }
                }
            }
        }

    }

    fun unregisterReceivers() {
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mDeleteMessageReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mThreadDeleteChatReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        unregisterReceivers()
        LocalBroadcastManager.getInstance(this).registerReceiver(mDeleteMessageReceiver,
                IntentFilter(FuguAppConstant.DELETE_INTENT))
        LocalBroadcastManager.getInstance(this).registerReceiver(mThreadDeleteChatReceiver,
                IntentFilter(FuguAppConstant.THREAD_DELETE_INTENT))
    }

    override fun onDestroy() {
        unregisterReceivers()
        super.onDestroy()
    }

}
