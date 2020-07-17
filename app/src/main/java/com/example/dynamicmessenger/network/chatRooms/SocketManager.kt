package com.example.dynamicmessenger.network.chatRooms

import android.app.Activity
import android.util.Log
import android.widget.EditText
import androidx.recyclerview.widget.DiffUtil
import com.example.dynamicmessenger.common.ResponseUrls
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.authorization.models.ChatRoom
import com.example.dynamicmessenger.network.authorization.models.Message
import com.example.dynamicmessenger.userChatRoom.adapters.ChatRoomAdapter
import com.example.dynamicmessenger.userChatRoom.adapters.ChatRoomDiffUtilCallback
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject


object SocketManager {
    private var mSocket: Socket? = null
    private var ErosSocket: Socket? = null

    fun getSocketInstance(): Socket? {
        if (mSocket == null) {
            Log.i("+++", "socket@ taza sarqvec")
            val opts =
                IO.Options()
            opts.forceNew = true
            opts.reconnection = false
            mSocket = IO.socket(ResponseUrls.herokuIPForSocket + "?token=" + SharedConfigs.token, opts)
//            mSocket = IO.socket(ResponseUrls.ErosServerIPForSocket + "?token=" + SharedConfigs.token, opts)
        }
        return mSocket
    }

    fun getErosSocketInstance(): Socket? {
        if (ErosSocket == null) {
            Log.i("+++", "Eroi socket@ taza sarqvec")
            val opts =
                IO.Options()
            opts.forceNew = true
            opts.reconnection = false
            ErosSocket = IO.socket(ResponseUrls.ErosServerIPForSocket + "?token=" + SharedConfigs.token, opts)
        }
        return ErosSocket
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

    fun sendMessage(receiverID: String, editText: EditText) {
        mSocket?.emit("sendMessage" , editText.text.toString() , receiverID)
        editText.text.clear()
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
                        val newData = adapter.data.toMutableList()
                        newData += message
                        val userChatDiffUtilCallback = ChatRoomDiffUtilCallback(adapter.data, newData)
                        val authorDiffResult = DiffUtil.calculateDiff(userChatDiffUtilCallback)
                        adapter.data += message
                        authorDiffResult.dispatchUpdatesTo(adapter)
                    }
                } catch (e: JSONException) {
                    Log.i("+++", "onMessage $e")
                    return@Runnable
                }
            })
        }
    }

    fun onMessageForAllChats(activity: Activity?, closure: (Boolean) -> Unit): Emitter.Listener {
        return Emitter.Listener {
            activity?.runOnUiThread(Runnable {
                try {
                    closure(true)
                } catch (e: JSONException) {
                    Log.i("+++", "onMessageForAllChats $e")
                    return@Runnable
                }
            })
        }
    }

    fun onMessageForNotification(activity: Activity?, closure: (ChatRoom) -> Unit): Emitter.Listener {
        return Emitter.Listener { args ->
            activity?.runOnUiThread(Runnable {
                try {
                    val data = args[0] as JSONObject
                    val gson = Gson()
                    val gsonMessage = gson.fromJson(data.toString(), Message::class.java)
                    val message = ChatRoom(gsonMessage.sender,gsonMessage.text,gsonMessage.reciever)
                    closure(message)
                } catch (e: JSONException) {
                    Log.i("+++", "onMessageForNotification $e")
                    return@Runnable
                }
            })
        }
    }

}