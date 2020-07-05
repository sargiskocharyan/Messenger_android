package com.example.dynamicmessenger.userChatRoom.viewModels

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.authorization.ChatRoomApi
import com.example.dynamicmessenger.network.authorization.LoadAvatarApi
import com.example.dynamicmessenger.network.authorization.models.ChatRoom
import com.example.dynamicmessenger.userDataController.database.SignedUserDatabase
import com.example.dynamicmessenger.userDataController.database.UserTokenRepository
import kotlinx.coroutines.launch

class ChatRoomViewModel(application: Application) : AndroidViewModel(application) {

    fun getMessagesFromNetwork(context: Context?, receiverID: String, closure: (List<ChatRoom>) -> Unit) {
        viewModelScope.launch {
            try {
                val response = ChatRoomApi.retrofitService.chatRoomResponseAsync(SharedConfigs.token, receiverID)
                if (response.isSuccessful) {
                    closure(response.body()!!)
                } else {
                    Toast.makeText(context, "User chat room else", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getAvatar(receiverURL: String?, closure: (Bitmap) -> Unit) {
        viewModelScope.launch {
            if (receiverURL != null) {
                try {
                    val response = LoadAvatarApi.retrofitService.loadAvatarResponseAsync(receiverURL)
                    Log.i("+++avatara", SharedConfigs.signedUser!!.avatarURL!!)
                    if (response.isSuccessful) {
                        val inputStream = response.body()!!.byteStream()
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        closure(bitmap)
                    }
                } catch (e: Exception) {
                    Log.i("+++exception", e.toString())
                }
            }
        }
    }
}