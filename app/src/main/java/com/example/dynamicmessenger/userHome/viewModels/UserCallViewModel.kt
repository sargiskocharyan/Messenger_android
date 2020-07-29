package com.example.dynamicmessenger.userHome.viewModels

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.LoadAvatarApi
import com.example.dynamicmessenger.userDataController.database.DiskCache
import com.example.dynamicmessenger.userDataController.database.SignedUserDatabase
import com.example.dynamicmessenger.userDataController.database.UserCalls
import com.example.dynamicmessenger.userDataController.database.UserCallsRepository
import kotlinx.coroutines.launch

class UserCallViewModel(application: Application) : AndroidViewModel(application) {
    private val callsDao = SignedUserDatabase.getUserDatabase(application)!!.userCallsDao()
    private val callsRepository = UserCallsRepository(callsDao)
    private val diskLruCache = DiskCache.getInstance(application)

    fun saveCall(userCalls: UserCalls) {
        viewModelScope.launch {
            callsRepository.insert(userCalls)
        }
    }

    fun deleteCallByTime(time: Long) {
        viewModelScope.launch {
            callsRepository.deleteCallByTime(time)
        }
    }

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