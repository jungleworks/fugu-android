package com.skeleton.mvp.activity


import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import androidx.core.content.FileProvider
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_invite_onboard.*
import java.io.File

class VideoPlayerActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        val mediaControlled = MediaController(this)
        val mVideoView: VideoView = findViewById(R.id.videoView)
        mediaControlled.setAnchorView(mVideoView)
        mediaControlled.setMediaPlayer(mVideoView)
        mediaControlled.isEnabled = true
        mVideoView.setMediaController(mediaControlled)
        mVideoView.setVideoURI(Uri.parse(intent.getStringExtra("url")))
        mVideoView.requestFocus()
        mVideoView.setOnPreparedListener { mVideoView.start() }
        mVideoView.setOnErrorListener { _, _, _ ->
            showErrorMessage("File type not supported!", {

            }, {
                try {
                    val uri = FileProvider.getUriForFile(this@VideoPlayerActivity, BuildConfig.APPLICATION_ID + ".provider", File(intent.getStringExtra("url")))
                    val openIntent = Intent(Intent.ACTION_VIEW)
                    openIntent.setDataAndType(uri, "video/*")
                    openIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    openIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    try {
                        startActivity(openIntent)
                    } catch (e: ActivityNotFoundException) {
                        showErrorMessage("You may not have a proper app for viewing this content.") {
                            finish()
                        }
                    }

                } catch (e: Exception) {
                    showErrorMessage("You may not have a proper app for viewing this content.") {
                        finish()
                    }
                }
            }, "", "Ok")
            true
        }
        ivBack.setOnClickListener { onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}
