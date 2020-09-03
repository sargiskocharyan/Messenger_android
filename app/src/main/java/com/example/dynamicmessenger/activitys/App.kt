package com.example.dynamicmessenger.activitys

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.util.Log
import com.example.dynamicmessenger.common.ChanelConstants
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.chatRooms.SocketManager
import com.example.dynamicmessenger.utils.NetworkUtils
import com.github.nkzawa.socketio.client.Socket


class App: Application() {

    override fun onCreate() {
        super.onCreate()
        SharedConfigs.init(this)
        if (SharedConfigs.token != "") {
            SocketManager.connectSocket()
//            SocketManager.callSocketEvents()
        }
        NetworkUtils.createConnectivityManager()
        createNotificationChannels()
    }

    //    override fun attachBaseContext(base: Context?) {
//        val lang = SharedConfigs.appLang.value ?: Locale.getDefault().getLanguage()
//        super.attachBaseContext(LocalizationUtil.applyLanguage(base!!, lang))
//    }

    override fun onTerminate() {
        super.onTerminate()
        SocketManager.closeSocket()
    }



    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val messageChannel = NotificationChannel(
                ChanelConstants.MESSAGE_CHANNEL_ID,
                "Messages",
                NotificationManager.IMPORTANCE_HIGH
            )
            messageChannel.description = "New message"
            messageChannel.enableLights(true)
            messageChannel.lightColor = Color.BLUE
            messageChannel.enableVibration(true)

            val callChannel = NotificationChannel(
                ChanelConstants.CALL_CHANNEL_ID,
                "Calls",
                NotificationManager.IMPORTANCE_HIGH
            )
            callChannel.enableLights(true)
            callChannel.lightColor = Color.RED
            callChannel.enableVibration(true)

            val missedCallChannel = NotificationChannel(
                ChanelConstants.MISSED_CALL_CHANNEL_ID,
                "Missed calls",
                NotificationManager.IMPORTANCE_HIGH
            )
            missedCallChannel.enableLights(true)
            missedCallChannel.lightColor = Color.RED
            missedCallChannel.enableVibration(true)

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(messageChannel)
            manager.createNotificationChannel(callChannel)
            manager.createNotificationChannel(missedCallChannel)
        }
    }

}
