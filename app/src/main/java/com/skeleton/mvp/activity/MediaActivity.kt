package com.skeleton.mvp.activity

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import com.skeleton.mvp.R
import com.skeleton.mvp.adapter.MediaGalleryAdapter
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.model.Media
import java.io.File
import java.util.*


class MediaActivity : AppCompatActivity() {

    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var linearLayoutManager: androidx.recyclerview.widget.LinearLayoutManager
    private lateinit var mediaGalleryAdapter: MediaGalleryAdapter
    private var mediaList = ArrayList<Media>()
    private var downloadedMediaList = ArrayList<Media>()
    private var position =0
    private var nonDownloadCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)
        recyclerView = findViewById(R.id.rvMedia)
        linearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        val snapHelper = androidx.recyclerview.widget.PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
        recyclerView.layoutManager = linearLayoutManager
        mediaList = intent.getSerializableExtra("images") as ArrayList<Media>
        for(mediaItem in mediaList){
            if(mediaItem.messageType == 10){
                val dirPath = Environment.getExternalStorageDirectory().toString() + File.separator + FuguAppConstant.APP_NAME_SHORT  +
                        File.separator + CommonData.getWorkspaceResponse(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey).workspaceName.replace(" ".toRegex(), "").replace("'s".toRegex(), "") + File.separator + FuguAppConstant.IMAGE

                val dirPathPrivate = Environment.getExternalStorageDirectory().toString() + File.separator + FuguAppConstant.APP_NAME_SHORT  +
                        File.separator + CommonData.getWorkspaceResponse(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey).workspaceName.replace(" ".toRegex(), "").replace("'s".toRegex(), "") + File.separator + FuguAppConstant.PRIVATE_IMAGES

                val jpgFile = File(dirPath + File.separator + mediaItem.message.fileName + "_" + mediaItem.muid + ".jpg")
                val jpgPrivateFile = File(dirPathPrivate + File.separator + mediaItem.message.fileName + "_" + mediaItem.muid + ".jpg")
                val jpegFile = File(dirPath + File.separator + mediaItem.message.fileName + "_" + mediaItem.muid + ".jpeg")
                val jpegPrivateFile = File(dirPathPrivate + File.separator + mediaItem.message.fileName + "_" + mediaItem.muid + ".jpeg")
                if(jpgFile.exists() || jpegFile.exists() || jpgPrivateFile.exists() || jpegPrivateFile.exists() || mediaItem.message.imageUrl.split(".")[mediaItem.message.imageUrl.split(".").size - 1].equals("gif")) {
                    downloadedMediaList.add(mediaItem)
                }
            }
            if((mediaItem.messageType == 11 || mediaItem.messageType == 12) &&!TextUtils.isEmpty(CommonData.getCachedFilePath(mediaItem.message.url,mediaItem.muid))) {
                downloadedMediaList.add(mediaItem)
            }
        }
        //mediaGalleryAdapter = MediaGalleryAdapter(intent.getSerializableExtra("images") as ArrayList<Media>, this)
        mediaGalleryAdapter = if(intent.hasExtra("isFromShowMore")){
            MediaGalleryAdapter(downloadedMediaList, this)
        } else{
            MediaGalleryAdapter(mediaList, this)
        }

        recyclerView.adapter = mediaGalleryAdapter
        if(intent.hasExtra("isFromShowMore")){
        for(i in 0..intent.getIntExtra("position", 0)){
            if(mediaList[i].messageType == 10){
                val dirPath = Environment.getExternalStorageDirectory().toString() + File.separator + FuguAppConstant.APP_NAME_SHORT  +
                        File.separator + CommonData.getWorkspaceResponse(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey).workspaceName.replace(" ".toRegex(), "").replace("'s".toRegex(), "") + File.separator + FuguAppConstant.IMAGE

                val dirPathPrivate = Environment.getExternalStorageDirectory().toString() + File.separator + FuguAppConstant.APP_NAME_SHORT  +
                        File.separator + CommonData.getWorkspaceResponse(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].fuguSecretKey).workspaceName.replace(" ".toRegex(), "").replace("'s".toRegex(), "") + File.separator + FuguAppConstant.PRIVATE_IMAGES
                val jpgFile = File(dirPath + File.separator + mediaList[i].message.fileName + "_" + mediaList[i].muid + ".jpg")
                val jpgPrivateFile = File(dirPathPrivate + File.separator + mediaList[i].message.fileName + "_" + mediaList[i].muid + ".jpg")
                val jpegFile = File(dirPath + File.separator + mediaList[i].message.fileName + "_" + mediaList[i].muid + ".jpeg")
                val jpegPrivateFile = File(dirPathPrivate + File.separator + mediaList[i].message.fileName + "_" + mediaList[i].muid + ".jpeg")
                if(!jpgFile.exists() && !jpegFile.exists() && !jpgPrivateFile.exists() && !jpegPrivateFile.exists() &&
                !mediaList[i].message.imageUrl.split(".")[mediaList[i].message.imageUrl.split(".").size - 1].equals("gif")) {
                    nonDownloadCount++
                }
            }
            if((mediaList[i].messageType == 11 || mediaList[i].messageType == 12) && TextUtils.isEmpty(CommonData.getCachedFilePath(mediaList[i].message.url,mediaList[i].muid))) {
                nonDownloadCount++
            }
        }} else{
            nonDownloadCount=0
        }

        recyclerView.scrollToPosition(intent.getIntExtra("position", 0) - nonDownloadCount)
        //llClose.setOnClickListener { onBackPressed() }
    }
}
