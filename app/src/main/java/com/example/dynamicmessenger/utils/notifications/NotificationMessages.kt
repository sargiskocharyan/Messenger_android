package com.example.dynamicmessenger.utils.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.common.ChanelConstants


class NotificationMessages {
    companion object {
        fun setNotificationMessage(messageTitle: String, message: String, context: Context, manager: NotificationManager) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                setupChannels(manager)
//            }

//            val builder = NotificationCompat.Builder(context, "101")
            val builder = NotificationCompat.Builder(context, ChanelConstants.MESSAGE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_message_24)
                .setContentTitle(messageTitle)
                .setContentText(message)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setGroup(messageTitle)
    //            .setContentIntent(pendingIntent)
                .build()
            manager.notify(6578, builder)
        }

        fun setCallNotification(context: Context, manager: NotificationManagerCompat) {
            val collapsedView = RemoteViews(context.packageName, R.layout.notification_call)

            val builder = NotificationCompat.Builder(context, ChanelConstants.CALL_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_message_24)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_CALL)
//                .setAutoCancel(true)
                .setCustomContentView(collapsedView)
//                .setCustomBigContentView(collapsedView)
                .setContent(collapsedView)
                .build()
            manager.notify(6578, builder)


        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private fun setupChannels(notificationManager: NotificationManager?) {
            val adminChannelName: CharSequence = "New notification"
            val adminChannelDescription = "Device to device notification"
            val adminChannel: NotificationChannel
            adminChannel = NotificationChannel(
                "101",
                adminChannelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            adminChannel.description = adminChannelDescription
            adminChannel.enableLights(true)
            adminChannel.lightColor = Color.RED
            adminChannel.enableVibration(true)
            notificationManager?.createNotificationChannel(adminChannel)
        }
    }
}