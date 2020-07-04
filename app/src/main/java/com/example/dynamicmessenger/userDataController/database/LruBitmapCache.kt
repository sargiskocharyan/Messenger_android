package com.example.dynamicmessenger.userDataController.database

import android.graphics.Bitmap
import android.util.Log
import android.util.LruCache

//class LruBitmapCache constructor(sizeInKB: Int = defaultLruCacheSize):
//    LruCache<String, Bitmap>(sizeInKB) {
//
//    override fun sizeOf(key: String, value: Bitmap): Int = value.rowBytes * value.height / 1024
//
//    fun getBitmap(url: String): Bitmap? {
//        Log.i("+++getBitmap", url)
//        return get(url)
//    }
//
//    fun putBitmap(url: String, bitmap: Bitmap) {
//        Log.i("+++putBitmap", url)
//        put(url, bitmap)
//    }
//
//    companion object {
//        val defaultLruCacheSize: Int
//            get() {
//                val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
//                val cacheSize = maxMemory / 8
//                return cacheSize
//            }
//    }
//}

val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

// Use 1/8th of the available memory for this memory cache.
val cacheSize = maxMemory / 8
class LruBitmapCache: LruCache<String, Bitmap>(cacheSize) {
    override fun sizeOf(key: String, value: Bitmap): Int = value.rowBytes * value.height / 1024

    fun addBitmapToMemoryCache(key: String?, bitmap: Bitmap?) {
        if (getBitmapFromMemCache(key) == null) {
            put(key, bitmap)
        }
    }

    fun getBitmapFromMemCache(key: String?): Bitmap? {
        return get(key)
    }

};

