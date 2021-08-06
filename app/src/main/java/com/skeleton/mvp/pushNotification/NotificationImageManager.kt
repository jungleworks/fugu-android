package com.skeleton.mvp.pushNotification

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.os.StrictMode
import android.text.TextUtils
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.util.Utils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.File.separator
import java.io.FileOutputStream
import java.net.URL
import java.util.*


class NotificationImageManager {
    fun getImageBitmap(link: String): Bitmap? {
        var notificationImageLink: String? = CommonData.getNotificationImage(link)
        val imageBitmap: Bitmap?
        try {
            val policy = StrictMode.ThreadPolicy.Builder()
                    .permitAll().build()
            StrictMode.setThreadPolicy(policy)
            if (TextUtils.isEmpty(notificationImageLink) || !File(notificationImageLink).exists()) {
                val randomName = UUID.randomUUID()
                val imageUrl = if (!TextUtils.isEmpty(link))
                    URL(link)
                else
                    URL("https://app.fugu.chat/assets/img/placeholder.png")
                val bitmap: Bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream())
                val bytes = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                val folder = File(Environment.getExternalStorageDirectory(), FuguAppConstant.USER_IMAGES)
                if (!folder.exists()) {
                    folder.mkdirs()
                }
                val noMedia = File(Environment.getExternalStorageDirectory().toString() + separator + FuguAppConstant.USER_IMAGES + "/.nomedia")
                if (!noMedia.exists()) {
                    noMedia.createNewFile()
                }
                val f = File(Environment.getExternalStorageDirectory(), FuguAppConstant.USER_IMAGES + "$separator$randomName.png")
                f.createNewFile()
                val fo = FileOutputStream(f)
                fo.write(bytes.toByteArray())
                fo.close()
                notificationImageLink = Environment.getExternalStorageDirectory().toString() + separator + FuguAppConstant.USER_IMAGES + "$separator$randomName.png"
                CommonData.setNotificationImagesMap(link,  notificationImageLink)
            }
        } catch (e: Exception) {
            notificationImageLink = ""
            e.printStackTrace()
        }
        imageBitmap = createBitmapFromLink(notificationImageLink!!)
        return imageBitmap
    }

    private fun createBitmapFromLink(link: String): Bitmap? {
        return try {
            val bmOptions = BitmapFactory.Options()
            Utils.getCircleBitmap(BitmapFactory.decodeFile(link, bmOptions))
        } catch (e: Exception) {
            null
        }
    }
}