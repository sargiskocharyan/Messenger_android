package com.example.dynamicmessenger.userChatRoom.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.dynamicmessenger.network.authorization.ChatRoomApi
import com.example.dynamicmessenger.network.authorization.models.ChatRoom
import com.example.dynamicmessenger.userDataController.SaveToken
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatRoomViewModel : ViewModel() {
    fun getMessagesFromNetwork(context: Context?, receiverID: String, closure: (List<ChatRoom>) -> Unit) {
        val myEncryptedToken = SharedPreferencesManager.getUserToken(context!!)
        val myToken = SaveToken.decrypt(myEncryptedToken)
        val getProperties: Call<List<ChatRoom>> = ChatRoomApi.retrofitService.chatRoomResponse(myToken!!, receiverID)
        try {
            getProperties.enqueue(object  : Callback<List<ChatRoom>> {
                override fun onResponse(
                    call: Call<List<ChatRoom>>,
                    response: Response<List<ChatRoom>>
                ) {
                    if (response.isSuccessful) {
                        closure(response.body()!!)
                    } else {
                        Toast.makeText(context, "User chat room else", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(
                    call: Call<List<ChatRoom>>,
                    t: Throwable) {
                    Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            Toast.makeText(context, "Something gone a wrong", Toast.LENGTH_SHORT).show()
        }
    }
}