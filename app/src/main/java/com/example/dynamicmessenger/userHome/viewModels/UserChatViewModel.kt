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
import com.example.dynamicmessenger.userDataController.database.SignedUserDatabase
import com.example.dynamicmessenger.userDataController.database.UserChatsRepository
import kotlinx.coroutines.launch

class UserChatViewModel(application: Application) : AndroidViewModel(application) {
    private val chatsDao = SignedUserDatabase.getUserDatabase(application)!!.userChatsDao()
    private val chatsRepository = UserChatsRepository(chatsDao)

    fun getUserChats(): List<Chat>? {
        return chatsRepository.getUserAllChats
    }

    fun getUserChatsFromNetwork(context: Context?, swipeRefreshLayout: SwipeRefreshLayout, closure: (List<Chat>) -> Unit) {
        viewModelScope.launch {
            try {
                val response = ChatsApi.retrofitService.chatsResponseAsync(SharedConfigs.token)
                if (response.isSuccessful) {
                    chatsRepository.insert(response.body()!!)
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

    fun getUserChatsFromRepo(swipeRefreshLayout: SwipeRefreshLayout) {
//        SharedConfigs.userRepository.getUserChats(swipeRefreshLayout).
    }


}