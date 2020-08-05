package com.example.dynamicmessenger.network.chatRooms

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.widget.EditText
import androidx.recyclerview.widget.DiffUtil
import com.example.dynamicmessenger.common.ResponseUrls
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.authorization.models.Call
import com.example.dynamicmessenger.network.authorization.models.ChatRoom
import com.example.dynamicmessenger.network.authorization.models.Message
import com.example.dynamicmessenger.network.authorization.models.Sender
import com.example.dynamicmessenger.userCalls.webRtc.SignallingClient
import com.example.dynamicmessenger.userChatRoom.adapters.ChatRoomAdapter
import com.example.dynamicmessenger.userChatRoom.adapters.ChatRoomDiffUtilCallback
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


object SocketManager {
    private var mSocket: Socket? = null

    private var signalingClient: SignallingClient? = null

    @SuppressLint("TrustAllX509TrustManager")
    private val trustAllCerts =
        arrayOf<TrustManager>(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {
            }
        })

    fun addSignalingClient(client: SignallingClient) {
        this.signalingClient = client
    }

    fun removeSignallingClient() {
        this.signalingClient = null
    }

    fun getSocketInstance(): Socket? {
        if (mSocket == null) {
            val sslcontext = SSLContext.getInstance("TLS")
            sslcontext.init(null, trustAllCerts, null)
            IO.setDefaultHostnameVerifier { _: String?, _: SSLSession? -> true }
            IO.setDefaultSSLContext(sslcontext)
            val opts = IO.Options()
            opts.forceNew = true
            opts.reconnection = true
//            mSocket = IO.socket(ResponseUrls.herokuIPForSocket + "?token=" + SharedConfigs.token, opts)
            mSocket = IO.socket(ResponseUrls.ErosServerIPForSocket + "?token=" + SharedConfigs.token, opts)
            Log.i("+++", "socket@ taza sarqvec")
        }
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

    fun onCallAccepted(array: Array<Any>) {
        signalingClient?.onCallAccepted(array)
    }

    fun onOffer(array: Array<Any>) {
        signalingClient?.onOffer(array)
    }

    fun onAnswer(array: Array<Any>) {
        signalingClient?.onAnswer(array)
    }

    fun onCandidate(array: Array<Any>) {
        signalingClient?.onCandidate(array)
    }

    fun onCallEnded() {
        signalingClient?.onCallEnded()
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
                val message = ChatRoom(gsonMessage.sender,null , gsonMessage.type, gsonMessage.text!!, gsonMessage.reciever, gsonMessage.createdAt)//TODO change
                try {
                    if (message.sender.id == chatID || message.reciever == chatID) {
                        val newData = adapter.data.toMutableList()
                        newData += message
                        val userChatDiffUtilCallback = ChatRoomDiffUtilCallback(adapter.data, newData)
                        val authorDiffResult = DiffUtil.calculateDiff(userChatDiffUtilCallback)
                        adapter.data.add(message)
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
                    val message = ChatRoom(gsonMessage.sender,null , gsonMessage.type, gsonMessage.text!!, gsonMessage.reciever, gsonMessage.createdAt)
                    closure(message)
                } catch (e: JSONException) {
                    Log.i("+++", "onMessageForNotification $e")
                    return@Runnable
                }
            })
        }
    }

}