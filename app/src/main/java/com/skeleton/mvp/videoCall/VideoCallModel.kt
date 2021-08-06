package com.skeleton.mvp.videoCall

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by rajatdhamija
 * 21/09/18.
 */
@Parcelize
data class VideoCallModel(val channelId: Long,
                          val userThumbnailImage: String = "",
                          val channelName: String = "",
                          val userId: Long = -1L,
                          val otherUserId: Long = -1L,
                          val fullName: String = "",
                          var turnApiKey: String = "",
                          var turnUserName: String = "",
                          var turnCredential: String = "",
                          var stunServers: ArrayList<String> = ArrayList(),
                          var turnServers: ArrayList<String> = ArrayList(),
                          var activityLaunchState: String? = "",
                          var signalUniqueId: String? = "",
                          val callType: String? = "") : Parcelable