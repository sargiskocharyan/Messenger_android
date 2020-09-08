package com.example.dynamicmessenger.utils.notifications

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationManagerCompat
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
            |notification!!.title ${p0.notification?.title} 
            |notification!!.body ${p0.notification?.body}
            |notification!!.imageUrl ${p0.notification?.imageUrl}
            |notification!!.clickAction ${p0.notification?.clickAction}
        """.trimMargin())
        val managers = NotificationManagerCompat.from(this)
        when {
            p0.data["roomName"] != null -> {
                SharedConfigs.callingOpponentId = p0.data["id"].toString()
                SharedConfigs.callRoomName = p0.data["roomName"].toString()
                SharedConfigs.isCalling = true
                SharedConfigs.callType = p0.data["type"].toString()
    //            NotificationMessages.setCallNotification(this, managers)
                NotificationMessages.setNewCallNotification(this, managers)
    //            this.scheduleNotification(true)
            }
            p0.data["type"] == "contactRequest" -> {
                NotificationMessages.setContactRequestNotification(p0.data["userId"]!!, p0.data["image"], p0.data["username"], this, managers)
            }
            p0.notification != null -> {
                NotificationMessages.setNotificationMessage(p0.notification!!.title ?: "", p0.notification!!.body ?: "", this, managers)
            }
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

