package com.example.dynamicmessenger.userCalls.webRtc

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.dynamicmessenger.common.ResponseUrls
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.chatRooms.SocketManager
import com.example.dynamicmessenger.userCalls.CallRoomActivity
import com.github.nkzawa.socketio.client.Socket
import com.github.nkzawa.socketio.client.IO
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import java.net.URISyntaxException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

internal class SignallingClient {
    private var roomName: String? = null
    private lateinit var socket: Socket
    var isChannelReady = false
    var isInitiator = false
    var isStarted = false
    private lateinit var callback: SignalingInterface

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

    fun init(signalingInterface: SignalingInterface) {
        callback = signalingInterface
        try {
            val sslcontext = SSLContext.getInstance("TLS")
            sslcontext.init(null, trustAllCerts, null)
            IO.setDefaultHostnameVerifier { _: String?, _: SSLSession? -> true }
            IO.setDefaultSSLContext(sslcontext)
            val opts =
                IO.Options()
            opts.forceNew = true
            opts.reconnection = false
//            socket = SocketManager.getSocketInstance()!!
            socket = IO.socket(ResponseUrls.ErosServerIPForSocket + "?token=" + SharedConfigs.token, opts)
            socket.connect()
            Log.d("SignallingClient", "init() called")

            socket.on("callAccepted") {
                Log.d(
                    "SignallingClient",
                    "call accepted: args = [" + Arrays.toString(it) + "]"
                )
                this.roomName = it[1].toString()
                callback.onCallAccepted(it[1].toString())

            }

            socket.on("call") {
                Log.d("SignallingClient", "created call() called with: args = [" + Arrays.toString(it) + "]")
                socket.emit("callAccepted", it[0].toString(), true)
                isInitiator = false
//                callback.onCreatedRoom()
            }

            socket.on("offer") {
                roomName = it[0].toString()
                try {
                    val data = it[1] as JSONObject
                    callback.onOfferReceived(data)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                isChannelReady = true
            }

            socket.on("answer") {
                Log.d(
                    "SignallingClient",
                    "answer called with: args = [" + Arrays.toString(it) + "]"
                )
                try {
                    val data = it[0] as JSONObject
                    callback.onAnswerReceived(data)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                isChannelReady = true
            }

            socket.on("candidates") {
                try {
                    val data = it[0] as JSONObject
                    callback.onIceCandidateReceived(data)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            //messages - SDP and ICE candidates are transferred through this
            socket.on("message") { args: Array<Any> ->
                Log.d(
                    "SignallingClient",
                    "message call() called with: args = [" + args.contentToString() + "]"
                )
                if (args[0] is String) {
                    Log.d("SignallingClient", "String received :: " + args[0])
                    val data = args[0] as String
                    if (data.equals("got user media", ignoreCase = true)) {
                        callback.onTryToStart()
                    }
                    if (data.equals("bye", ignoreCase = true)) {
                        callback.onRemoteHangUp(data)
                    }
                } else if (args[0] is JSONObject) {
                    try {
                        val data = args[0] as JSONObject
                        Log.d(
                            "SignallingClient",
                            "Json Received :: $data"
                        )
                        val type = data.getString("type")
                        if (type.equals("offer", ignoreCase = true)) {
                            callback.onOfferReceived(data)
                        } else if (type.equals("answer", ignoreCase = true) && isStarted) {
                            callback.onAnswerReceived(data)
                        } else if (type.equals("candidate", ignoreCase = true) && isStarted) {
                            callback.onIceCandidateReceived(data)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }
    }



    fun emitMessage(message: SessionDescription) {
        try {
            Log.d(
                "SignallingClient",
                "emitMessage() called with: message = [$message]"
            )
            val obj = JSONObject()
            obj.put("type", message.type.canonicalForm())
            obj.put("sdp", message.description)
            Log.d("emitMessage", obj.toString())
            socket.emit("message", obj)
            Log.d("vivek1794", obj.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun emitOffer(message: SessionDescription) {
        try {
            Log.d(
                "SignallingClient",
                "emitOffer() called with: message = [$message]"
            )
            val obj = JSONObject()
            obj.put("sdp", message.description)
            obj.put("type", message.type.canonicalForm())
            Log.d("SignallingClient", "emitOffer $obj")
            socket.emit("offer", roomName, obj)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun emitAnswer(message: SessionDescription) {
        try {
            Log.d(
                "SignallingClient",
                "emitOffer() called with: message = [$message]"
            )
            val obj = JSONObject()
            obj.put("sdp", message.description)
            obj.put("type", message.type.canonicalForm())
            Log.d("SignallingClient", "emitAnswer $obj")
            socket.emit("answer", roomName, obj)
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
            socket.emit("candidates", roomName, obj)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun close() {
//        socket.emit("bye", roomName)
        socket.disconnect()
        socket.close()
    }

    fun callOpponentYelena() {
        Log.d("SignallingClient", "call call Yelena")
        socket.emit("call", "5f06b7add526610017115fea") //Yelena
    }

    fun callOpponentErik() {
        Log.d("SignallingClient", "call call Erik")
        socket.emit("call", "5f05c4851e41d40d29360d08") //Erik
    }

    internal interface SignalingInterface {
        fun onRemoteHangUp(msg: String?)
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
                instance = SignallingClient()
            }
            return instance
        }
    }
}