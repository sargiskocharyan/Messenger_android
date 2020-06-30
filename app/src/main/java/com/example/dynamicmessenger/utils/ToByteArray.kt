package com.example.dynamicmessenger.utils

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

class ToByteArray {
    companion object{
        @Throws(IOException::class)
        fun getBytes(inputStream: InputStream): ByteArray? {
            val byteBuff = ByteArrayOutputStream()
            val buffSize = 1024
            val buff = ByteArray(buffSize)
            var len = 0
            while (inputStream.read(buff).also { len = it } != -1) {
                byteBuff.write(buff, 0, len)
            }
            return byteBuff.toByteArray()
        }
    }
}