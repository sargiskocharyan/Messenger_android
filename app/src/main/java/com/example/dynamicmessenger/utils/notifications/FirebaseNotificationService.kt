package com.example.dynamicmessenger.utils.notifications

import android.R
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.example.dynamicmessenger.activitys.MainActivity
import com.example.dynamicmessenger.common.SharedConfigs
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FirebaseNotificationService: FirebaseMessagingService() {
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        Log.i("+++", """remote message ${p0}
            |data ${p0.data}
            |notification ${p0.notification}
            |messageType ${p0.messageType}
            |rawData ${p0.rawData}
            |priority ${p0.priority}
        """.trimMargin())
        if (p0.data["roomName"] != null) {
            val managers = NotificationManagerCompat.from(this)
            SharedConfigs.callingOpponentId = p0.data["id"].toString()
            SharedConfigs.callRoomName = p0.data["roomName"].toString()
            SharedConfigs.isCalling = true
//            NotificationMessages.setCallNotification(this, managers)
            NotificationMessages.setNewCallNotification(this, managers)
        }
    }



    @SuppressLint("HardwareIds")
    override fun onNewToken(token: String) {
        val androidId: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        RemoteNotificationManager.registerDeviceToken(androidId, token)
    }

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            if (action == "android.provider.Telephony.SMS_RECEIVED") {
                //action for sms received
            } else if (action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
                //action for phone state changed
            }
        }
    }
}

