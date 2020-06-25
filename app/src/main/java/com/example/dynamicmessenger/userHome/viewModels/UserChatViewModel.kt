package com.example.dynamicmessenger.userHome.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.dynamicmessenger.network.authorization.ChatsApi
import com.example.dynamicmessenger.network.authorization.models.Chat
import com.example.dynamicmessenger.userDataController.SaveToken
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserChatViewModel : ViewModel() {
    fun getUserChatsFromNetwork(context: Context?, closure: (List<Chat>) -> Unit) {
        val myEncryptedToken = SharedPreferencesManager.getUserToken(context!!)
        val myToken = SaveToken.decrypt(myEncryptedToken)
        val getProperties: Call<List<Chat>> = ChatsApi.retrofitService.chatsResponse(myToken!!)
        try {
            getProperties.enqueue(object : Callback<List<Chat>?> {
                override fun onResponse(
                    call: Call<List<Chat>?>,
                    response: Response<List<Chat>?>
                ) {
                    if (response.isSuccessful) {
                        closure(response.body()!!)
                    } else {
                        Toast.makeText(context, "User chats else", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(
                    call: Call<List<Chat>?>,
                    t: Throwable
                ) {
                    Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            Toast.makeText(context, "Something gone a wrong", Toast.LENGTH_SHORT).show()
        }
    }
}