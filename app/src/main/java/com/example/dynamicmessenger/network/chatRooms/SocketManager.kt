package com.example.dynamicmessenger.network.chatRooms

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.EditText
import com.example.dynamicmessenger.common.ResponseUrls
import com.example.dynamicmessenger.network.authorization.models.ChatRoom
import com.example.dynamicmessenger.network.authorization.models.Message
import com.example.dynamicmessenger.userChatRoom.adapters.ChatRoomAdapter
import com.example.dynamicmessenger.userDataController.SaveToken
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject


class SocketManager(val context: Context) {

     private var mSocket: Socket? = null

    fun getSocketInstance(): Socket? {
        val opts =
            IO.Options()
            opts.forceNew = true
            opts.reconnection = false
        val myEncryptedToken = SharedPreferencesManager.getUserToken(context)
        val myToken = SaveToken.decrypt(myEncryptedToken)
        mSocket = IO.socket(ResponseUrls.herokuIPForSocket + "?token=" + myToken, opts)

        return mSocket
    }

    private fun deleteSocket() {
        mSocket = null
    }

    fun closeSocket() {
        mSocket?.disconnect()
        mSocket?.close()
        //mSocket?.off()
        deleteSocket()
    }

    fun sendMessage(receiverID: String, text: EditText) {
        mSocket?.emit("sendMessage" , text.text.toString() , receiverID)
        text.text.clear()
    }



    fun onMessage(adapter: ChatRoomAdapter, chatID: String, activity: Activity?): Emitter.Listener {
        return Emitter.Listener { args ->
            activity?.runOnUiThread(Runnable {
                val data = args[0] as JSONObject
                val gson = Gson()
                val gsonMessage = gson.fromJson(data.toString(), Message::class.java)
                val message = ChatRoom(gsonMessage.sender,gsonMessage.text,gsonMessage.reciever)
                try {
                    if (message.sender.id == chatID || message.reciever == chatID) {
                        adapter.data += message
                        adapter.notifyDataSetChanged()
                    }
                } catch (e: JSONException) {
                    Log.i("+++", e.toString())
                    return@Runnable
                }
            })
        }
    }

}