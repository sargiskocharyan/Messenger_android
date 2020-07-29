package com.example.dynamicmessenger.activitys.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.dynamicmessenger.network.authorization.models.Chat
import com.example.dynamicmessenger.network.authorization.models.User

class HomeActivityViewModel: ViewModel() {
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