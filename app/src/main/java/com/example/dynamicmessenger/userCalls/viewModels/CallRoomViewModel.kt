package com.example.dynamicmessenger.userCalls.viewModels

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamicmessenger.activitys.HomeActivity
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.GetUserInfoByIdApi
import com.example.dynamicmessenger.network.LoadAvatarApi
import com.example.dynamicmessenger.network.authorization.models.User
import com.example.dynamicmessenger.userDataController.database.DiskCache
import kotlinx.coroutines.launch

class CallRoomViewModel(application: Application) : AndroidViewModel(application) {
    private val diskLruCache = DiskCache.getInstance(application)
    val opponentInformation = MutableLiveData<User>()
    val opponentAvatarUrl = MutableLiveData<String>()
    val opponentAvatarBitmap = MutableLiveData<Bitmap>()

    fun getOpponentInfoFromNetwork() {
        val receiverId = SharedConfigs.callingOpponentId
        viewModelScope.launch {
            if (receiverId != null) {
                try {
                    val response = GetUserInfoByIdApi.retrofitService.getUserInfoByIdResponseAsync(
                        SharedConfigs.token, receiverId)
                    if (response.isSuccessful) {
                        opponentInformation.value = response.body()
                        opponentAvatarUrl.value = response.body()?.avatarURL
                    } else {
                        Log.i("+++else", "getOpponentInfoFromNetwork $response")
                    }
                } catch (e: Exception) {
                    Log.i("+++exception", "getOpponentInfoFromNetwork $e")
                }
            }
        }
    }

    fun getAvatar(receiverURL: String?) {
        viewModelScope.launch {
            if (receiverURL != null) {
                try {
                    if (diskLruCache.get(receiverURL) != null) {
                        opponentAvatarBitmap.value = diskLruCache.get(receiverURL)!!
                    } else {
                        val response = LoadAvatarApi.retrofitService.loadAvatarResponseAsync(receiverURL)
                        if (response.isSuccessful) {
                            val inputStream = response.body()!!.byteStream()
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            opponentAvatarBitmap.value = bitmap
                        }
                    }
                } catch (e: Exception) {
                    Log.i("+++exception", e.toString())
                }
            }
        }
    }
}