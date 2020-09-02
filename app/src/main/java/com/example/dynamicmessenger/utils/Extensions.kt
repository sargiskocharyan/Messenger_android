package com.example.dynamicmessenger.utils

import android.annotation.SuppressLint
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.text.SimpleDateFormat
import java.util.*

//Live data
fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(
        lifecycleOwner,
        object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        }
    )
}

fun <T> LiveData<T>.observeOnceWithoutOwner(observer: Observer<T>) {
    observeForever(
        object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        }
    )
}

//String
@SuppressLint("SimpleDateFormat")
fun String?.toDate(): Date? {
    if (this == null) return null
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    format.timeZone = TimeZone.getTimeZone("UTC")
    return try {
        format.parse(this)!!
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

//Long
fun Long?.toDate(): Date? {
    return this?.let { Date(it) }
}