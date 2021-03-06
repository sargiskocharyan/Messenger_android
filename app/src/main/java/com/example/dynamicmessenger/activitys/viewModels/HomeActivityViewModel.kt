package com.example.dynamicmessenger.activitys.viewModels

import android.app.Application
import android.os.Handler
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamicmessenger.common.MyTime
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.GetOnlineUsersApi
import com.example.dynamicmessenger.network.authorization.models.Chat
import com.example.dynamicmessenger.network.authorization.models.OnlineUsersTask
import com.example.dynamicmessenger.network.authorization.models.User
import com.example.dynamicmessenger.userDataController.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val usersDao = SignedUserDatabase.getUserDatabase(application)!!.savedUserDao()
    private val usersRepository = SavedUserRepository(usersDao)
    private val chatsDao = SignedUserDatabase.getUserDatabase(application)!!.userChatsDao()
    private val chatsRepository = UserChatsRepository(chatsDao)
    private var handler: Handler = Handler()
    private var runnable: Runnable? = null

    init {
        SharedConfigs.userRepository.getUserContacts()
        repeat()
    }

    private fun repeat() { //TODO change name
        handler.postDelayed(Runnable {
            handler.postDelayed(runnable, MyTime.threeMinutes)
            getOnlineUsers()
        }.also { runnable = it }, 0)
    }

    private fun getOnlineUsers() {
        viewModelScope.launch(Dispatchers.IO) {
//            while (true) { //TODO set when user have internet connection
                try {
                    val list = mutableListOf<String>()
                    val chat = chatsRepository.getUserAllChats
                    chat?.forEach { list.add(it.id) }
                    val task = OnlineUsersTask(list)
                    val response = GetOnlineUsersApi.retrofitService.getOnlineUsers(SharedConfigs.token, task)
                    if (response.isSuccessful) {
                        SharedConfigs.onlineUsers.postValue(response.body()!!.usersOnline)
                    }
                } catch (e: Exception) {
                    Log.i("+++", "online users exception $e")
                }
                Log.i("+++", "${SharedConfigs.onlineUsers.value}")
//                delay(10000L)
//            }
        }
    }

//    fun updateUserInformation() {
//        viewModelScope.launch(Dispatchers.IO) {
//            SharedConfigs.userRepository.getUserInformation(SharedConfigs.signedUser?._id).
//        }
//    }

    fun getUserById(id: String): User? {
        return usersRepository.getUserById(id)
    }

    var opponentUser: User? = null
        set(value) {
            field = value
            Log.i("+++", "Opponent user set $value")
        }
    var receiverChatInfo: Chat? = null
        set(value) {
            field = value
            Log.i("+++", "receiver Chat Info set $value")
        }
    var receiverID: String? = null
        set(value) {
            field = value
            Log.i("+++", "receiver id set $value")
        }
    var isAddContacts: Boolean? = null
        set(value) {
            field = value
            Log.i("+++", "is Add Contacts set $value")
        }
    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(runnable)
    }
}
