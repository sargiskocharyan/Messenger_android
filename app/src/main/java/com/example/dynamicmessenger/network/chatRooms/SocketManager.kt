package com.example.dynamicmessenger.network.chatRooms

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.widget.EditText
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.DiffUtil
import com.example.dynamicmessenger.common.MyFragments
import com.example.dynamicmessenger.common.ResponseUrls
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.authorization.models.*
import com.example.dynamicmessenger.userCalls.webRtc.SignallingClient
import com.example.dynamicmessenger.userChatRoom.adapters.ChatRoomAdapter
import com.example.dynamicmessenger.userChatRoom.adapters.ChatRoomDiffUtilCallback
import com.example.dynamicmessenger.userChatRoom.fragments.ChatRoomFragment
import com.example.dynamicmessenger.userHome.fragments.UserChatFragment
import com.example.dynamicmessenger.utils.notifications.NotificationMessages
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


object SocketManager {
    private var mSocket: Socket? = null

    private var signalingClient: SignallingClient? = null
    private var chatFragment: UserChatFragment? = null
    private var chatRoomFragment: ChatRoomFragment? = null

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

    fun connectSocket() {
        getSocketInstance()?.connect()
        callSocketEvents()
    }

    //Signaling client
    fun addSignalingClient(client: SignallingClient) {
        this.signalingClient = client
    }

    fun removeSignallingClient() {
        this.signalingClient = null
    }

    private fun onCallAccepted(array: Array<Any>) {
        signalingClient?.onCallAccepted(array)
    }

    private fun onOffer(array: Array<Any>) {
        signalingClient?.onOffer(array)
    }

    private fun onAnswer(array: Array<Any>) {
        signalingClient?.onAnswer(array)
    }

    private fun onCandidate(array: Array<Any>) {
        signalingClient?.onCandidate(array)
    }

    private fun onCallEnded() {
        signalingClient?.onCallEnded()
    }

    //User Chat Fragment
    fun addChatFragment(fragment: UserChatFragment) {
        chatFragment = fragment
    }

    fun removeChatFragment() {
        chatFragment = null
    }

    private fun refreshChatFragment() {
        chatFragment?.updateRecyclerView()
    }

    //User Chat Room Fragment
    fun addChatRoomFragment(fragment: ChatRoomFragment) {
        chatRoomFragment = fragment
    }

    fun removeChatRoomFragment() {
        chatRoomFragment = null
    }

    private fun refreshChatRoomFragment(newMessage: ChatRoom) {
        chatRoomFragment?.receiveMessage(newMessage)
    }

    fun sendMessage(receiverID: String, editText: EditText) {
        mSocket?.emit("sendMessage" , editText.text.toString() , receiverID)
        editText.text.clear()
    }

    private fun onMessageForNotification(activity: Activity?, closure: (Message, ChatRoom) -> Unit): Emitter.Listener {
        return Emitter.Listener { args ->
            activity?.runOnUiThread(Runnable {
                try {
                    val data = args[0] as JSONObject
                    val gson = Gson()
                    val gsonMessage = gson.fromJson(data.toString(), Message::class.java)
                    val message = ChatRoom(gsonMessage.senderId,null , gsonMessage.type, gsonMessage.text!!, gsonMessage.reciever, gsonMessage.createdAt)
                    closure(gsonMessage, message)
                } catch (e: Exception) {
                    Log.i("+++", "onMessageForNotification $e")
                    return@Runnable
                }
            })
        }
    }

    private fun callSocketEvents() {
        val manager = NotificationManagerCompat.from(SharedConfigs.myContext)

        mSocket?.on("callAccepted") {
            onCallAccepted(it)
            Log.d("SignallingClientAcc", "Home activity call accepted: args = " + Arrays.toString(it))
        }

        mSocket?.on("offer") {
            onOffer(it)
            Log.d("SignallingClientAcc", "Home activity offer ")
        }

        mSocket?.on("answer") {
            onAnswer(it)
            Log.d("SignallingClientAcc", "Home activity answer ")
        }

        mSocket?.on("candidates") {
            onCandidate(it)
            Log.d("SignallingClientAcc", "Home activity candidates ")
        }

        mSocket?.on("callEnded") {
            onCallEnded()
            Log.d("SignallingClientAcc", "Home activity call Ended ")
        }

        mSocket?.on("message", onMessageForNotification(Activity()){ message: Message, chatRoom: ChatRoom ->
            try {
                when {
                    SharedConfigs.currentFragment.value == MyFragments.CHAT_ROOM -> refreshChatRoomFragment(chatRoom)
                    SharedConfigs.currentFragment.value == MyFragments.CHATS -> refreshChatFragment()
                    message.senderId != SharedConfigs.signedUser?._id ?: true -> {
                        message.senderUsername?.let { senderName -> NotificationMessages.setNotificationMessage(senderName, message.text!!, SharedConfigs.myContext, manager) }
                    }
                }
            } catch (e: Exception) {
                Log.i("+++catch", e.toString())
            }
        })

        mSocket?.on("call") {
            if (!SharedConfigs.isCallingInProgress) {
                try {
                    val data = it[0] as JSONObject
                    val gson = Gson()
                    val gsonMessage = gson.fromJson(data.toString(), CallNotification::class.java)
                    SharedConfigs.callingOpponentId = gsonMessage.caller
                    SharedConfigs.callRoomName = gsonMessage.roomName
                    SharedConfigs.isCalling = true
//                    NotificationMessages.setCallNotification(this, managers)
                    NotificationMessages.setNewCallNotification(SharedConfigs.myContext, manager)
                } catch (e: Exception) {
                    Log.i("+++", "onMessageForNotification $e")
                }
                Log.i("+++", """on call
                    |${Arrays.toString(it)}
                """.trimMargin())
            } else {
                mSocket?.emit("callAccepted", SharedConfigs.callingOpponentId, false)
                NotificationMessages.setNotificationMessage("Incoming Call", "Incoming Call", SharedConfigs.myContext, manager)
            }
        }
    }

}