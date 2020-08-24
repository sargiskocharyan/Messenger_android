package com.example.dynamicmessenger.userHome.viewModels

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.LoadAvatarApi
import com.example.dynamicmessenger.network.ReadCallHistoryApi
import com.example.dynamicmessenger.network.authorization.models.ReadCallHistoryTask
import com.example.dynamicmessenger.network.authorization.models.User
import com.example.dynamicmessenger.userDataController.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserCallViewModel(application: Application) : AndroidViewModel(application) {
    private val diskLruCache = DiskCache.getInstance(application)

//    fun readCallHistory() {
//        viewModelScope.launch {
//            try {
//                val response = ReadCallHistoryApi.retrofitService.readCallHistory(SharedConfigs.token, ReadCallHistoryTask())
//                if (response.isSuccessful) {
//                    val inputStream = response.body()!!.byteStream()
//                    val bitmap = BitmapFactory.decodeStream(inputStream)
//                    diskLruCache.put(avatarURL, bitmap)
//                    closure(bitmap)
//                }
//            } catch (e: Exception) {
//                Log.i("+++exception", "userInformationViewModel getAvatar $e")
//            }
//
//        }
//    }

    fun getAvatar(avatarURL: String, closure: (Bitmap) -> Unit) {
        viewModelScope.launch {
            try {
                if (diskLruCache.get(avatarURL) != null) {
                    closure(diskLruCache.get(avatarURL)!!)
                } else {
                    val response = LoadAvatarApi.retrofitService.loadAvatarResponseAsync(avatarURL)
                    if (response.isSuccessful) {
                        val inputStream = response.body()!!.byteStream()
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        diskLruCache.put(avatarURL, bitmap)
                        closure(bitmap)
                    }
                }
            } catch (e: Exception) {
                Log.i("+++exception", "userInformationViewModel getAvatar $e")
            }

        }
    }
}