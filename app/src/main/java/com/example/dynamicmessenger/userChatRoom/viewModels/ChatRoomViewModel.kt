package com.example.dynamicmessenger.userChatRoom.viewModels

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.authorization.ChatRoomApi
import com.example.dynamicmessenger.network.authorization.models.ChatRoom
import com.example.dynamicmessenger.userDataController.database.SignedUserDatabase
import com.example.dynamicmessenger.userDataController.database.UserTokenRepository
import kotlinx.coroutines.launch

class ChatRoomViewModel(application: Application) : AndroidViewModel(application) {
    private val tokenDao = SignedUserDatabase.getSignedUserDatabase(application)!!.userTokenDao()
    private val tokenRep = UserTokenRepository(tokenDao)

    fun getMessagesFromNetwork(context: Context?, receiverID: String, closure: (List<ChatRoom>) -> Unit) {
        viewModelScope.launch {
            try {
                val response = ChatRoomApi.retrofitService.chatRoomResponseAsync(SharedConfigs.token!!, receiverID)
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