package com.example.dynamicmessenger.utils.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.example.dynamicmessenger.common.ChanelConstants
import com.example.dynamicmessenger.common.SharedConfigs

class RequestNotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.getStringExtra("contactId")?.let {
            SharedConfigs.userRepository.acceptContactRequest(it)
        }
        context?.let {
            NotificationManagerCompat.from(it).cancel(ChanelConstants.CONTACT_REQUEST_MANAGER_ID)
        }
    }

}