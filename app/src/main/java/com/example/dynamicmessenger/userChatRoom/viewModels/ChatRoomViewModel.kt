package com.example.dynamicmessenger.userChatRoom.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamicmessenger.network.authorization.ChatRoomApi
import com.example.dynamicmessenger.network.authorization.models.ChatRoom
import com.example.dynamicmessenger.userDataController.SaveToken
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import kotlinx.coroutines.launch

class ChatRoomViewModel : ViewModel() {
    fun getMessagesFromNetwork(context: Context?, receiverID: String, closure: (List<ChatRoom>) -> Unit) {
        val myEncryptedToken = SharedPreferencesManager.getUserToken(context!!)
        val myToken = SaveToken.decrypt(myEncryptedToken)
        viewModelScope.launch {
            try {
                val response = ChatRoomApi.retrofitService.chatRoomResponseAsync(myToken!!, receiverID)
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
}