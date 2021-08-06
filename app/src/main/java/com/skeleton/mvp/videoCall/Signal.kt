package com.skeleton.mvp.videoCall

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import org.json.JSONObject

/**
 * Created by rajatdhamija
 * 21/09/18.
 */
@Parcelize
data class Signal(val signalUniqueUserId: Long? = -1L,
                  var signalUniqueId: String? = "",
                  var fullNameOfCalledPerson: String? = "",
                  var turnApiKey: String? = "",
                  var turnUserName: String? = "",
                  var turnCredential: String? = "",
                  var stunServers: ArrayList<String>? = ArrayList(),
                  var turnServers: ArrayList<String>? = ArrayList(),
                  var deviceDetails: @RawValue JSONObject? = JSONObject(),
                  var callType: String = "") : Parcelable