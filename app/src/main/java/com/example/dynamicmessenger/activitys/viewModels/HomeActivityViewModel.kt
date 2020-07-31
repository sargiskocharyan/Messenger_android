package com.example.dynamicmessenger.activitys.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamicmessenger.network.authorization.models.Chat
import com.example.dynamicmessenger.network.authorization.models.User
import com.example.dynamicmessenger.userDataController.database.SavedUserRepository
import com.example.dynamicmessenger.userDataController.database.SignedUserDatabase
import com.example.dynamicmessenger.userDataController.database.UserCalls
import com.example.dynamicmessenger.userDataController.database.UserCallsRepository
import kotlinx.coroutines.launch

class HomeActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val callsDao = SignedUserDatabase.getUserDatabase(application)!!.userCallsDao()
    private val callsRepository = UserCallsRepository(callsDao)
    private val usersDao = SignedUserDatabase.getUserDatabase(application)!!.savedUserDao()
    private val usersRepository = SavedUserRepository(usersDao)

    fun saveCall(userCalls: UserCalls) {
        viewModelScope.launch {
            callsRepository.insert(userCalls)
        }
    }

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
}