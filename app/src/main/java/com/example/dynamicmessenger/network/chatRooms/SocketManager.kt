package com.example.dynamicmessenger.network.chatRooms

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.MutableLiveData
import com.example.dynamicmessenger.activitys.HomeActivity
import com.example.dynamicmessenger.common.MyFragments
import com.example.dynamicmessenger.common.ResponseUrls
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.authorization.models.*
import com.example.dynamicmessenger.userCalls.webRtc.SignallingClient
import com.example.dynamicmessenger.userChatRoom.fragments.ChatRoomFragment
import com.example.dynamicmessenger.userHome.fragments.UserChatFragment
import com.example.dynamicmessenger.utils.notifications.NotificationMessages
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
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
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, null)
            IO.setDefaultHostnameVerifier { _: String?, _: SSLSession? -> true }
            IO.setDefaultSSLContext(sslContext)
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
        allSocketEvents()
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

    private fun refreshChatRoomFragment(newMessage: ChatRoomMessage) {
        chatRoomFragment?.receiveMessage(newMessage)
    }

    private fun opponentIsTyping(array: Array<Any>) {
        chatRoomFragment?.opponentTyping(array)
    }

    private fun onMessageReceived(array: Array<Any>) {
        chatRoomFragment?.statusMessageReceived(array)
    }

    private fun onMessageRead(array: Array<Any>) {
        chatRoomFragment?.statusMessageRead(array)
    }

    //Messaging events
    fun sendMessage(receiverID: String, editText: MutableLiveData<String>) {
        mSocket?.emit("sendMessage" , editText.value , receiverID)
        editText.value = ""
    }

    fun messageTyping(receiverID: String) {
        mSocket?.emit("messageTyping" , receiverID)
    }

    private fun messageReceived(receiverID: String, messageId: String) {
        mSocket?.emit("messageReceived" , receiverID, messageId)
    }

    fun messageRead(receiverID: String, messageId: String) {
        mSocket?.emit("messageRead" , receiverID, messageId)
    }

    private fun onMessageForNotification(activity: Activity?, closure: (Message, ChatRoomMessage) -> Unit): Emitter.Listener {
        return Emitter.Listener { args ->
            activity?.runOnUiThread(Runnable {
                try {
                    val data = args[0] as JSONObject
                    val gson = Gson()
                    val gsonMessage = gson.fromJson(data.toString(), Message::class.java)
                    val message = ChatRoomMessage(gsonMessage._id, gsonMessage.senderId,null , gsonMessage.type, gsonMessage.text!!, gsonMessage.reciever, gsonMessage.createdAt)
                    closure(gsonMessage, message)
                } catch (e: Exception) {
                    Log.i("+++", "onMessageForNotification $e")
                    return@Runnable
                }
            })
        }
    }

    private fun allSocketEvents() {
        val manager = NotificationManagerCompat.from(SharedConfigs.myContext)
        callSocketEvents(manager)
        messageSocketEvents(manager)
    }

    private fun messageSocketEvents(manager: NotificationManagerCompat) {
        mSocket?.on("message", onMessageForNotification(Activity()){ message: Message, chatRoom: ChatRoomMessage ->
            try {
                Log.i("+++", "chat Room $chatRoom")
                if (chatRoom.senderId != SharedConfigs.signedUser?._id) {
                    chatRoom.senderId?.let { messageReceived(it, chatRoom._id) }
                }
                when {
                    SharedConfigs.currentFragment.value == MyFragments.CHAT_ROOM -> {
                        refreshChatRoomFragment(chatRoom)
                        if (chatRoom.senderId != SharedConfigs.signedUser?._id) {
                            chatRoom.senderId?.let {
                                if (it == HomeActivity.receiverID!!) {
                                    messageRead(it, chatRoom._id)
                                }
                            }
                        }
                    }
                    SharedConfigs.currentFragment.value == MyFragments.CHATS -> refreshChatFragment()
                    message.senderId != SharedConfigs.signedUser?._id ?: true -> {
                        message.senderUsername?.let { senderName ->
                            NotificationMessages.setNotificationMessage(senderName, message.text!!, SharedConfigs.myContext, manager)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.i("+++catch", e.toString())
            }
        })

        mSocket?.on("messageTyping") {
            opponentIsTyping(it)
        }

        mSocket?.on("messageReceived") {
            Log.i("+++", "messageReceived")
            onMessageReceived(it)
        }

        mSocket?.on("messageRead") {
            Log.i("+++", "messageRead")
            onMessageRead(it)
        }
    }

    private fun callSocketEvents(manager: NotificationManagerCompat) {

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