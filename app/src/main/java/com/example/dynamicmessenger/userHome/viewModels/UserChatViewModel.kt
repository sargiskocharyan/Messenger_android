package com.example.dynamicmessenger.userHome.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamicmessenger.network.authorization.ChatsApi
import com.example.dynamicmessenger.network.authorization.models.Chat
import com.example.dynamicmessenger.userDataController.SaveToken
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import kotlinx.coroutines.launch

class UserChatViewModel : ViewModel() {
    fun getUserChatsFromNetwork(context: Context?, closure: (List<Chat>) -> Unit) {
        val myEncryptedToken = SharedPreferencesManager.getUserToken(context!!)
        val myToken = SaveToken.decrypt(myEncryptedToken)
        viewModelScope.launch {
            try {
                val response = ChatsApi.retrofitService.chatsResponseAsync(myToken!!).await()
                if (response.isSuccessful) {
                    closure(response.body()!!)
                } else {
                    Toast.makeText(context, "User chats else", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }
}