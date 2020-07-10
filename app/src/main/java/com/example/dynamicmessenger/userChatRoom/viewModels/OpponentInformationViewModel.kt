package com.example.dynamicmessenger.userChatRoom.viewModels

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamicmessenger.activitys.HomeActivity.Companion.opponentUser
import com.example.dynamicmessenger.network.LoadAvatarApi
import com.example.dynamicmessenger.userDataController.database.DiskCache
import kotlinx.coroutines.launch

class OpponentInformationViewModel(application: Application) : AndroidViewModel(application) {

    private val diskLruCache = DiskCache.getInstance(application)

    fun getAvatar(closure: (Bitmap) -> Unit) {
        viewModelScope.launch {
            if (opponentUser?.avatarURL != null) {
                try {
                    if (diskLruCache.get(opponentUser!!.avatarURL!!) != null) {
                        closure(diskLruCache.get(opponentUser!!.avatarURL!!)!!)
                    } else {
                        val response = LoadAvatarApi.retrofitService.loadAvatarResponseAsync(
                            opponentUser!!.avatarURL!!
                        )
                        if (response.isSuccessful) {
                            val inputStream = response.body()!!.byteStream()
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            diskLruCache.put(opponentUser!!.avatarURL!!, bitmap)
                            closure(bitmap)
                        }
                    }
                } catch (e: Exception) {
                    Log.i("+++exception", "userInformationViewModel getAvatar $e")
                }
            }
        }
    }
}