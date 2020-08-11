package com.example.dynamicmessenger.activitys

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.dynamicmessenger.common.ChanelConstants
import com.example.dynamicmessenger.common.SharedConfigs


class App: Application() {
    override fun onCreate() {
        super.onCreate()
        SharedConfigs.init(this)
        createNotificationChannels()
    }

    //    override fun attachBaseContext(base: Context?) {
//        val lang = SharedConfigs.appLang.value ?: Locale.getDefault().getLanguage()
//        super.attachBaseContext(LocalizationUtil.applyLanguage(base!!, lang))
//    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val messageChannel = NotificationChannel(
                ChanelConstants.MESSAGE_CHANNEL_ID,
                "Messages",
                NotificationManager.IMPORTANCE_HIGH
            )
            messageChannel.description = "New message"

            val callChannel = NotificationChannel(
                ChanelConstants.CALL_CHANNEL_ID,
                "Calls",
                NotificationManager.IMPORTANCE_HIGH
            )


            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(messageChannel)
            manager.createNotificationChannel(callChannel)
        }
    }

}
