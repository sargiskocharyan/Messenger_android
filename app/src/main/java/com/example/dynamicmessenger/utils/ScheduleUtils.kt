package com.example.dynamicmessenger.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import com.example.dynamicmessenger.utils.notifications.NotificationReceiver
import java.util.concurrent.TimeUnit

fun Context.scheduleNotification(isLockScreen: Boolean) {
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val timeInMillis = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(SCHEDULE_TIME)

    with(alarmManager) {
        setExact(AlarmManager.RTC_WAKEUP, timeInMillis, getReceiver(isLockScreen))
    }
}

private fun Context.getReceiver(isLockScreen: Boolean): PendingIntent {
    // for demo purposes no request code and no flags
    return PendingIntent.getBroadcast(
        this,
        0,
        NotificationReceiver.build(this, isLockScreen),
        0
    )
}

private const val SCHEDULE_TIME = 5L
