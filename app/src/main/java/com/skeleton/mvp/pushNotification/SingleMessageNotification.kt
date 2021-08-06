package com.skeleton.mvp.pushNotification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import android.text.Html
import android.text.TextUtils
import android.view.View
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.constant.FuguAppConstant.MESSAGE
import com.skeleton.mvp.pushNotification.PushReceiver.PushChannel.CHANNEL_ONE_ID
import com.skeleton.mvp.pushNotification.PushReceiver.PushChannel.CHANNEL_ONE_NAME
import com.skeleton.mvp.pushNotification.PushReceiver.PushChannel.GROUP_KEY_WORK_EMAIL
import com.skeleton.mvp.pushNotification.PushReceiver.PushChannel.SUMMARY_NOTIFICATION_ID
import com.skeleton.mvp.service.FuguPushIntentService
import com.skeleton.mvp.util.Utils.getRandomNumberBetween
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class SingleMessageNotification {
    private var notificationManager: NotificationManager? = null
    fun publishNotification(
            context: Context, notificationIntent: Intent,
            messageJson: JSONObject, priority: Int,
            smallIcon: Int
    ) {

        createNotificationChannel(context)
        val pi: PendingIntent = PendingIntent.getService(
                context, 12345,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationDefaults: Int = Notification.DEFAULT_ALL
        val notification: Notification?
        val mBuilder: NotificationCompat.Builder
        val messagingStyle = NotificationCompat.MessagingStyle("You")

        val senderName = if (messageJson.has(FuguAppConstant.LAST_SENT_BY_FULL_NAME)) {
            messageJson.getString(FuguAppConstant.LAST_SENT_BY_FULL_NAME)
        } else {
            messageJson.getString(FuguAppConstant.TITLE)
        }

        val senderImage = if (messageJson.has(FuguAppConstant.USER_THUMBNAIL_IMAGE)) {
            messageJson.getString(FuguAppConstant.USER_THUMBNAIL_IMAGE)
        } else {
            ""
        }
        var targetImageBitmap: Bitmap? = null
        if (!TextUtils.isEmpty(senderImage)) {
            targetImageBitmap = NotificationImageManager().getImageBitmap(senderImage)
        } else {

        }
        if (targetImageBitmap != null) {
            val user = androidx.core.app.Person.Builder()
                    .setUri(senderImage)
                    .setIcon(IconCompat.createWithBitmap(targetImageBitmap))
                    .setName(senderName).build()
            val myDate = messageJson.getString("date_time")
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
            val date = sdf.parse(myDate)
            val millis = date.time
            messagingStyle.addMessage(
                    NotificationCompat.MessagingStyle.Message(
                            Html.fromHtml(messageJson.getString(FuguAppConstant.NOTI_MSG)),
                            millis,
                            user
                    )
            )
        } else {
            val user = androidx.core.app.Person.Builder()
                    .setUri(senderImage)
                    .setIcon(null)
                    .setName(senderName).build()
            val myDate = messageJson.getString("date_time")
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
            val date = sdf.parse(myDate)
            val millis = date.time
            messagingStyle.addMessage(
                    NotificationCompat.MessagingStyle.Message(
                            Html.fromHtml(messageJson.getString(FuguAppConstant.NOTI_MSG)),
                            millis,
                            user
                    )
            )
        }

        mBuilder = NotificationCompat.Builder(context, CHANNEL_ONE_ID)
                .setStyle(messagingStyle)
                .setSmallIcon(if (smallIcon == -1) R.drawable.default_notif_icon else smallIcon)
                .setColor(context.resources.getColor(R.color.fugu_icon_light_green))
                .setLargeIcon(NotificationImageManager().getImageBitmap(senderImage))
                .setContentTitle(messageJson.getString(FuguAppConstant.TITLE))
                .setContentText(messageJson.getString(MESSAGE))
                .setContentIntent(pi)
                .setGroup(GROUP_KEY_WORK_EMAIL)
                .setPriority(priority)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            val summaryNotification = NotificationCompat.Builder(context, CHANNEL_ONE_ID)
                    .setColor(context.resources.getColor(R.color.fugu_icon_light_green))
                    .setSmallIcon(if (smallIcon == -1) R.drawable.default_notif_icon else smallIcon)
                    .setLargeIcon(
                            BitmapFactory.decodeResource(
                                    context.resources,
                                    R.drawable.ic_fugu
                            )
                    )
                    .setContentText(BuildConfig.APP_NAME)
                    //build summary info into InboxStyle template
                    //specify which group this notification belongs to
                    .setGroup(GROUP_KEY_WORK_EMAIL)
                    //set this notification as the summary for the group
                    .setGroupSummary(true)
                    .setAutoCancel(true)
                    .setContentIntent(pi)
                    .build()
            notificationManager?.notify(SUMMARY_NOTIFICATION_ID, summaryNotification)
        }
        mBuilder.setDefaults(notificationDefaults)
        mBuilder.setChannelId(CHANNEL_ONE_ID)
        notification = mBuilder.build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val smallIconViewId = context.resources.getIdentifier("right_icon", "id", "android")
            if (notification != null && smallIconViewId != 0) {
                if (notification.contentIntent != null)
                    if (notification.headsUpContentView != null)
                        notification.headsUpContentView.setViewVisibility(
                                smallIconViewId,
                                View.INVISIBLE
                        )


                if (notification.bigContentView != null)
                    notification.bigContentView.setViewVisibility(smallIconViewId, View.INVISIBLE)
            }
        }
        notificationManager?.notify(getRandomNumberBetween(99999999, 90000000), notification)

    }

    private fun createNotificationChannel(context: Context) {
        notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null) {
                notificationManager?.createNotificationChannel(
                        NotificationChannel(
                                CHANNEL_ONE_ID,
                                CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH
                        )
                )
            }
        }
    }

}