package com.example.dynamicmessenger.userDataController.database

import android.content.Context
import android.graphics.Bitmap
import java.io.BufferedOutputStream
import java.io.OutputStream
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import android.graphics.BitmapFactory
import com.jakewharton.disklrucache.DiskLruCache
import java.io.*

interface ImageCache {
    fun put(url: String, bitmap: Bitmap)
    fun get(url: String): Bitmap?
    fun clear()
}

class DiskCache private constructor(val context: Context) : ImageCache {
    private var cache: DiskLruCache = DiskLruCache.open(context.cacheDir, 1, 1, 10 * 1024 * 1024)

    override fun get(url: String): Bitmap? {
        val key = md5(url)
        val snapshot: DiskLruCache.Snapshot? = cache.get(key)
        return if (snapshot != null) {
            val inputStream: InputStream = snapshot.getInputStream(0)
            val buffIn = BufferedInputStream(inputStream, 8 * 1024)
            BitmapFactory.decodeStream(buffIn)
        } else {
            null
        }
    }

    override fun put(url: String, bitmap: Bitmap) {
        val key = md5(url)
        var editor: DiskLruCache.Editor? = null
        try {
            editor = cache.edit(key)
            if (editor == null) {
                return
            }
            if (writeBitmapToFile(bitmap, editor)) {
                cache.flush()
                editor.commit()
            } else {
                editor.abort()
            }
        } catch (e: IOException) {
            try {
                editor?.abort()
            } catch (ignored: IOException) {
            }
        }
    }

    override fun clear() {
        cache.delete()
        cache = DiskLruCache.open(context.cacheDir, 1, 1, 10 * 1024 * 1024)
    }

    private fun writeBitmapToFile(bitmap: Bitmap, editor: DiskLruCache.Editor): Boolean {
        var out: OutputStream? = null
        try {
            out = BufferedOutputStream(editor.newOutputStream(0), 8 * 1024)
            return bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        } finally {
            out?.close()
        }
    }

    private fun md5(url: String): String? {
        try {
            val md = MessageDigest.getInstance("MD5")
            val messageDigest = md.digest(url.toByteArray())
            val no = BigInteger(1, messageDigest)
            var hashtext = no.toString(16)
            while (hashtext.length < 32) {
                hashtext = "0$hashtext"
            }
            return hashtext
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }

    companion object {
        private val INSTANCE: DiskCache? = null
        @Synchronized
        fun getInstance(context: Context): DiskCache {
            return INSTANCE?.let { return INSTANCE }
                ?: run {
                    return DiskCache(context)
                }
        }
    }
}
