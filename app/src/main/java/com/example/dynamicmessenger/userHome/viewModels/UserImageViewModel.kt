package com.example.dynamicmessenger.userHome.viewModels

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.DeleteAvatarApi
import com.example.dynamicmessenger.network.LoadAvatarApi
import com.example.dynamicmessenger.userDataController.database.DiskCache
import kotlinx.coroutines.launch

class UserImageViewModel(application: Application): AndroidViewModel(application) {
    private val diskLruCache = DiskCache.getInstance(application)

    private val _avatarBitmap = MutableLiveData<Bitmap>()
    val avatarBitmap: LiveData<Bitmap> get() = _avatarBitmap

    fun getAvatar() {
        viewModelScope.launch {
            if (SharedConfigs.signedUser?.avatarURL != null) {
                try {
                    if (diskLruCache.get(SharedConfigs.signedUser?.avatarURL!!) != null) {
                        _avatarBitmap.value = diskLruCache.get(SharedConfigs.signedUser?.avatarURL!!)!!
                    } else {
                        val response = LoadAvatarApi.retrofitService.loadAvatarResponseAsync(
                            SharedConfigs.signedUser!!.avatarURL!!)
                        if (response.isSuccessful) {
                            val inputStream = response.body()!!.byteStream()
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            diskLruCache.put(SharedConfigs.signedUser?.avatarURL!!, bitmap)
                            _avatarBitmap.value = bitmap
                        }
                    }
                } catch (e: Exception) {
                    Log.i("+++exception", "userInformationViewModel getAvatar $e")
                }
            }
        }
    }

    fun deleteUserAvatar(closure: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = DeleteAvatarApi.retrofitService.deleteUserAvatar(SharedConfigs.token)
                if (response.isSuccessful) {
                    SharedConfigs.deleteAvatar()
                    closure(true)
                } else {
                    Log.i("+++avatar", response.code().toString())
                    closure(false)
                }
            } catch (e: Exception) {
                Log.i("+++exception", "userInformationViewModel getAvatar $e")
            }
        }
    }
}