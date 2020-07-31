package com.example.dynamicmessenger.userCalls.webRtc

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.chatRooms.SocketManager
import com.github.nkzawa.socketio.client.Socket
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import java.net.URISyntaxException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.util.*

class SignallingClient {
    private var roomName: String? = null
    private lateinit var mSocket: Socket
    var isChannelReady = false
    var isInitiator = false
    val isCallingNotProgress = MutableLiveData<Boolean>(true)
    val isCalling = MutableLiveData<Boolean>(true)
    var isStarted = false
    private lateinit var callback: SignalingInterface

    fun init(signalingInterface: SignalingInterface) {
        callback = signalingInterface
        try {
            SocketManager.addSignalingClient(this)
            mSocket = SocketManager.getSocketInstance()!!
/*
//            mSocket.once("callAccepted") {
//                Log.d("SignallingClientAcc", "call accepted: args = " + Arrays.toString(it))
//                if (it[0] == true) {
////                    if (number == 1) {
//                        roomName = it[1].toString()
//                        callback.onCallAccepted(it[1].toString())
////                    }
//                }
//            }



//            mSocket.on("call") {
//                Log.d("SignallingClient", "created call() called with: args = [" + Arrays.toString(it) + "]")
//                mSocket.emit("callAccepted", it[0].toString(), true)
//                isInitiator = false
////                callback.onCreatedRoom()
//            }

//            mSocket.once("offer") {
//                number++
////                SocketEventsForVideoCalls.offer.observe(this, androidx.lifecycle.Observer {
////
////                })
//                Log.d("SignallingClient", "on offer " + Arrays.toString(it))
//                roomName = it[0].toString()
//                Log.d("SignallingClientAcc", "$number")
//                try {
//                    val data = it[1] as JSONObject
//                    callback.onOfferReceived(data)
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                }
//                isChannelReady = true
//            }

//            mSocket.once("answer") {
//                Log.d("SignallingClient", "on answer " + Arrays.toString(it))
//                try {
//                    val data = it[0] as JSONObject
//                    callback.onAnswerReceived(data)
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                }
//                isChannelReady = true
//            }

//            mSocket.once("candidates") {
//                Log.d("SignallingClient", "candidates " + Arrays.toString(it))
//                try {
//                    val data = it[0] as JSONObject
//                    callback.onIceCandidateReceived(data)
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                }
//            }
*/
            /*
            //messages - SDP and ICE candidates are transferred through this
//            socket.on("message") { args: Array<Any> ->
//                Log.d(
//                    "SignallingClient",
//                    "message call() called with: args = [" + args.contentToString() + "]"
//                )
//                if (args[0] is String) {
//                    Log.d("SignallingClient", "String received :: " + args[0])
//                    val data = args[0] as String
//                    if (data.equals("got user media", ignoreCase = true)) {
//                        callback.onTryToStart()
//                    }
//                    if (data.equals("bye", ignoreCase = true)) {
//                        callback.onRemoteHangUp(data)
//                    }
//                } else if (args[0] is JSONObject) {
//                    try {
//                        val data = args[0] as JSONObject
//                        Log.d(
//                            "SignallingClient",
//                            "Json Received :: $data"
//                        )
//                        val type = data.getString("type")
//                        if (type.equals("offer", ignoreCase = true)) {
//                            callback.onOfferReceived(data)
//                        } else if (type.equals("answer", ignoreCase = true) && isStarted) {
//                            callback.onAnswerReceived(data)
//                        } else if (type.equals("candidate", ignoreCase = true) && isStarted) {
//                            callback.onIceCandidateReceived(data)
//                        }
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
//                }
//            }
            */
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }
    }

    fun onCallAccepted(array: Array<Any>) {
        Log.d("SignallingClientAcc", "call accepted: args = " + array.contentToString())
        if (array[0] == true) {
            roomName = array[1].toString()
            callback.onCallAccepted(array[1].toString())
        }
    }

    fun onOffer(array: Array<Any>) {
        Log.d("SignallingClient", "on offer " + array.contentToString())
        roomName = array[0].toString()
        try {
            val data = array[1] as JSONObject
            callback.onOfferReceived(data)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        isChannelReady = true
    }

    fun onAnswer(array: Array<Any>) {
        Log.d("SignallingClient", "on answer " + array.contentToString())
        try {
            val data = array[0] as JSONObject
            callback.onAnswerReceived(data)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        isChannelReady = true
    }

    fun onCandidate(array: Array<Any>) {
        Log.d("SignallingClient", "candidates " + array.contentToString())
        try {
            val data = array[0] as JSONObject
            callback.onIceCandidateReceived(data)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun onCallEnded() {
        callback.onRemoteHangUp()
    }

    fun leaveRoom() {
        mSocket.emit("leaveRoom", roomName)
    }

    fun emitMessage(message: SessionDescription) {
        try {
            Log.d("SignallingClient", "emitMessage() called with: message = [$message]")
            val obj = JSONObject()
            obj.put("type", message.type.canonicalForm())
            obj.put("sdp", message.description)
            Log.d("emitMessage", obj.toString())
            mSocket.emit("message", obj)
            Log.d("vivek1794", obj.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun emitOffer(message: SessionDescription) {
        try {
            Log.d("SignallingClient", "emitOffer() called with: message = [$message]")
            val obj = JSONObject()
            obj.put("sdp", message.description)
            obj.put("type", message.type.canonicalForm())
            Log.d("SignallingClient", "emitOffer $roomName, $obj")
            mSocket.emit("offer", roomName, obj)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun emitAnswer(message: SessionDescription) {
        try {
            Log.d("SignallingClient", "emitAnswer() called with: message = [$message]")
            val obj = JSONObject()
            obj.put("sdp", message.description)
            obj.put("type", message.type.canonicalForm())
            Log.d("SignallingClient", "emitAnswer $obj")
            mSocket.emit("answer", roomName, obj)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun emitIceCandidate(iceCandidate: IceCandidate) {
        try {
            val obj = JSONObject()
            obj.put("sdpMLineIndex", iceCandidate.sdpMLineIndex)
            obj.put("sdpMid", iceCandidate.sdpMid)
            obj.put("candidate", iceCandidate.sdp)
            mSocket.emit("candidates", roomName, obj)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun emitCallAccepted(answer: Boolean) {
        Log.d("SignallingClient", "emit call accepted $answer")
        mSocket.emit("callAccepted", SharedConfigs.callingOpponentId, answer)
        isInitiator = false
    }

    fun callOpponent() {
        Log.d("SignallingClient", "call call")
        mSocket.emit("call", SharedConfigs.callingOpponentId)
    }


//    fun close() {
////        socket.emit("bye", roomName)
//        mSocket.disconnect()
//        mSocket.close()
//        mSocket.connect()
//    }

    interface SignalingInterface {
        fun onRemoteHangUp()
        fun onOfferReceived(data: JSONObject?)
        fun onAnswerReceived(data: JSONObject?)
        fun onIceCandidateReceived(data: JSONObject?)
        fun onCallAccepted(room: String?)
        fun onTryToStart()
        fun onNewPeerJoined()
    }

    companion object {
        private var instance: SignallingClient? = null
        fun getInstance(): SignallingClient? {
            if (instance == null) {
                Log.i("+++", "getInstance called")
                instance = SignallingClient()
            }
            return instance
        }
        fun destroyInstance() {
            Log.i("+++", "destroyInstance called")
            SocketManager.removeSignallingClient()
            instance = null
        }
    }
}