package com.example.dynamicmessenger.userHome.viewModels

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.ChatsApi
import com.example.dynamicmessenger.network.authorization.models.Chat
import kotlinx.coroutines.launch

class UserChatViewModel(application: Application) : AndroidViewModel(application) {
    fun getUserChatsFromNetwork(context: Context?, swipeRefreshLayout: SwipeRefreshLayout, closure: (List<Chat>) -> Unit) {
        viewModelScope.launch {
            try {
                val response = ChatsApi.retrofitService.chatsResponseAsync(SharedConfigs.token)
                if (response.isSuccessful) {
                    closure(response.body()!!)
                } else {
                    Toast.makeText(context, "User chats else", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.i("+++catch",e.toString())
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show()
            }
            swipeRefreshLayout.isRefreshing = false
        }
    }
}