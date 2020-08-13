package com.example.dynamicmessenger.utils.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.common.ChanelConstants
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.userCalls.CallRoomActivity
import java.net.URL


class NotificationMessages {
    companion object {
        fun setNotificationMessage(messageTitle: String, message: String, context: Context, manager: NotificationManager) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                setupChannels(manager)
//            }

//            val builder = NotificationCompat.Builder(context, "101")
            val builder = NotificationCompat.Builder(context, ChanelConstants.MESSAGE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_message_24)
                .setSubText("New message")
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
            val intent = Intent(context, CallRoomActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 1 , intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val collapsedView = RemoteViews(context.packageName, R.layout.notification_call)
            collapsedView.setOnClickPendingIntent(R.id.notificationCallAcceptButton, pendingIntent)

            SharedConfigs.userRepository.getUserInformationFromDB(SharedConfigs.callingOpponentId)?.let {user ->
                collapsedView.setTextViewText(R.id.notificationCallName, user.username)
                try {
                    val url = URL(user.avatarURL)
                    val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    collapsedView.setImageViewBitmap(R.id.notificationCallImage, image)
                } catch (e: Exception) {
                    println(e)
                }
//                SharedConfigs.userRepository.getAvatarFromDB(user.avatarURL)?.let {
//                    collapsedView.setImageViewBitmap(R.id.notificationCallImage, it)
//                }
            }

//            try {
//                val url = URL(SharedConfigs.signedUser?.avatarURL)
//                val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
////                val image = SharedConfigs.userRepository.getAvatar(SharedConfigs.signedUser?.avatarURL).value
//                collapsedView.setImageViewBitmap(R.id.notificationCallImage, image)
//            } catch (e: Exception) {
//                println(e)
//            }
//            SharedConfigs.userRepository.getUserInformation(SharedConfigs.callingOpponentId).observeForever {user ->
//                if (user != null) {
//                    collapsedView.setTextViewText(R.id.notificationCallName, user.username)
//                    SharedConfigs.userRepository.getAvatar(user.avatarURL).observeForever {
//                        if (it != null) {
//                            collapsedView.setImageViewBitmap(R.id.notificationCallImage, it)
//                        }
//                    }
//                }
//            }

            val builder = NotificationCompat.Builder(context, ChanelConstants.CALL_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_call)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setAutoCancel(true)
                .setTimeoutAfter(30000)
                .setCustomContentView(collapsedView)
//                .setContentIntent(pendingIntent)
//                .setCustomBigContentView(collapsedView)
                .setContent(collapsedView)
                .build()
            manager.notify(1155, builder)


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