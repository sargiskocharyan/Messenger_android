package com.example.dynamicmessenger.utils.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.common.ChanelConstants
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.authorization.models.CallNotificationForSocket
import com.example.dynamicmessenger.userCalls.CallRoomActivity
import java.net.URL


class NotificationMessages {
    companion object {
        fun setNotificationMessage(messageTitle: String, message: String, context: Context, manager: NotificationManagerCompat) {
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

        fun setMissedCallNotification(message: CallNotificationForSocket, context: Context, manager: NotificationManagerCompat) {
            val builder = NotificationCompat.Builder(context, ChanelConstants.MESSAGE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_call_24)
                .setSubText("New message")
                .setContentTitle(message.senderUsername)
                .setContentText("You have ${message.call?.status} call from ${message.senderUsername}")
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setOnlyAlertOnce(true)
//                .setGroup(messageTitle)
                .build()
            manager.notify(2345, builder)
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

        fun setNewCallNotification(context: Context, manager: NotificationManagerCompat) {

            // Create Notification
            val intent = Intent(context, CallRoomActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            val pendingIntent = PendingIntent.getActivity(context, 1155, intent, PendingIntent.FLAG_UPDATE_CURRENT)
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
            }
            val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
//            val mp: MediaPlayer = MediaPlayer.create(context, alarmSound)
//            mp.start()
//            SocketManager.callSocketEvents()
            val builder = NotificationCompat.Builder(context, ChanelConstants.CALL_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_call)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setTimeoutAfter(30000)
                .setCustomContentView(collapsedView)
                .setContent(collapsedView)
                .setContentIntent(pendingIntent)
                .setVibrate(ChanelConstants.callVibratePattern)
                .setFullScreenIntent(pendingIntent, true)

            manager.notify(1155, builder.build())
        }
    }
}