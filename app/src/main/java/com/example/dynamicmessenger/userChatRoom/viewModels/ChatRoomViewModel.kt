package com.example.dynamicmessenger.userChatRoom.viewModels

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.ChatRoomApi
import com.example.dynamicmessenger.network.authorization.models.ChatRoomMessage
import com.example.dynamicmessenger.network.authorization.models.MessageStatus
import kotlinx.coroutines.launch

class ChatRoomViewModel(application: Application) : AndroidViewModel(application), Observable {
    val isKeyboardVisible = MutableLiveData<Boolean>()
    val toolbarOpponentUsername = MutableLiveData<String>()
    @Bindable
    val userEnteredMessage = MutableLiveData<String>()
    val opponentTypingTextVisibility = MutableLiveData<Boolean>()

    fun getMessagesFromNetwork(context: Context?, receiverID: String, closure: (List<ChatRoomMessage>, List<MessageStatus>) -> Unit) {
        viewModelScope.launch {
            try {
                val response = ChatRoomApi.retrofitService.chatRoomResponseAsync(SharedConfigs.token, receiverID)
                if (response.isSuccessful) {
                    response.body()?.let {
                        closure(it.array, it.statuses)
                    }
                } else {
                    Toast.makeText(context, "User chat room else", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.i("+++exception", e.toString())
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.remove(callback)
    }
}