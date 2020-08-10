package com.example.dynamicmessenger.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.common.SharedConfigs
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
                SharedConfigs.myContext.getString(R.string.long_time_ago)
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
            SharedConfigs.myContext.getString(R.string.long_time_ago)
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun convertStringToDate(time: String?): Date? {
        var timeInDate: Date? = null
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        format.timeZone = TimeZone.getTimeZone("UTC")
        if (time != null) {
            try {
                timeInDate = format.parse(time)!!
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return timeInDate
    }

    @SuppressLint("SimpleDateFormat")
    fun convertLongToTimeForCall(time: Long): String {
        val date = Date(time)
        val currentDate: Date = Calendar.getInstance().time
        SimpleDateFormat("HH:mm")
        return if ((currentDate.day == date.day) && (currentDate.month == date.month) && (currentDate.year == date.year)) {
            SharedConfigs.myContext.getString(R.string.today)
        } else if ((currentDate.day == date.day + 1) && (currentDate.month == date.month) && (currentDate.year == date.year)) {
            SharedConfigs.myContext.getString(R.string.yesterday)
        } else {
            val newFormat = SimpleDateFormat("MMMM-dd")
            newFormat.format(date)
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun convertDateToHour(date: Date): String {
        val newFormat = SimpleDateFormat("HH:mm")
        return newFormat.format(date)
    }

    @SuppressLint("SimpleDateFormat")
    fun convertDateToHour(dateString: String): String {
        return convertStringToDate(dateString)?.let { convertDateToHour(it) } ?: ""
    }

    fun getCallDurationInSeconds(time: Long): String {
        val hours = time / (1000 * 60 * 60) % 24
        val minutes = time / (1000 * 60) % 60
        val seconds = (time / 1000) % 60

        return if (minutes == 0L && hours == 0L) {
            "${seconds}s"
        } else if (hours == 0L) {
            "${minutes}m ${seconds}s"
        } else {
            "${hours}h ${minutes}m ${seconds}s"
        }
    }
}