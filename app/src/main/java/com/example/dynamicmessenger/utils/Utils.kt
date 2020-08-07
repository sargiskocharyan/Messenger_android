package com.example.dynamicmessenger.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import com.example.dynamicmessenger.R
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    @SuppressLint("SimpleDateFormat")
    fun dateConverter(time: String): String? {
        var timeInHours: String? = null
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        format.timeZone = TimeZone.getTimeZone("UTC")
        try {
            val date: Date = format.parse(time)!!
            val currentDate: Date = Calendar.getInstance().time
            timeInHours = if ((currentDate.day == date.day) && (currentDate.month == date.month) && (currentDate.year == date.year)) {
                val newFormat = SimpleDateFormat("HH:mm")
                newFormat.format(date)
            } else if ((currentDate.day != date.day) || (currentDate.month != date.month) && (currentDate.year == date.year)) {
                val newFormat = SimpleDateFormat("dd MMM")
                newFormat.format(date)
            } else {
                Resources.getSystem().getString(R.string.long_time_ago)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return timeInHours
    }

    @SuppressLint("SimpleDateFormat")
    fun convertLongToDate(time: Long): String {
        val date = Date(time)
        val currentDate: Date = Calendar.getInstance().time
        return if ((currentDate.day == date.day) && (currentDate.month == date.month) && (currentDate.year == date.year)) {
            val newFormat = SimpleDateFormat("HH:mm")
            newFormat.format(date)
        } else if ((currentDate.day != date.day) || (currentDate.month != date.month) && (currentDate.year == date.year)) {
            val newFormat = SimpleDateFormat("dd MMM")
            newFormat.format(date)
        } else {
            Resources.getSystem().getString(R.string.long_time_ago)
        }
    }
}