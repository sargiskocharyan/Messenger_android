package com.example.dynamicmessenger.userCalls

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.databinding.ActivityCallRoomBinding
import com.example.dynamicmessenger.userCalls.viewModels.CallRoomViewModel
import com.example.dynamicmessenger.userCalls.webRtc.CustomPeerConnectionObserver
import com.example.dynamicmessenger.userCalls.webRtc.CustomSdpObserver
import com.example.dynamicmessenger.userCalls.webRtc.SignallingClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.*
import java.util.*
import kotlin.math.roundToInt

class CallRoomActivity : AppCompatActivity(), SignallingClient.SignalingInterface {
    private lateinit var peerConnectionFactory: PeerConnectionFactory
    private lateinit var audioConstraints: MediaConstraints
    private lateinit var videoConstraints: MediaConstraints
    private lateinit var sdpConstraints: MediaConstraints
    private lateinit var videoSource: VideoSource
    private lateinit var localVideoTrack: VideoTrack
    private lateinit var audioSource: AudioSource
    private lateinit var localAudioTrack: AudioTrack
    private lateinit var surfaceTextureHelper: SurfaceTextureHelper
    private lateinit var localVideoView: SurfaceViewRenderer
    private lateinit var remoteVideoView: SurfaceViewRenderer
    var localPeer: PeerConnection? = null
    private var rootEglBase: EglBase? = null
    private var gotUserMedia = false
    private var peerIceServers: MutableList<PeerConnection.IceServer> = ArrayList()
    private val ALL_PERMISSIONS_CODE = 1
    private lateinit var binding: ActivityCallRoomBinding
    private lateinit var viewModel: CallRoomViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_call_room)
        viewModel = ViewModelProvider(this).get(CallRoomViewModel::class.java)
        binding.viewModel = viewModel
        binding.signalingClient = SignallingClient.getInstance()
        binding.lifecycleOwner = this
        SharedConfigs.isCallingInProgress = true

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !== PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !== PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO), ALL_PERMISSIONS_CODE)
        } else {
            start()
        }
        viewModel.getOpponentInfoFromNetwork()
        viewModel.opponentAvatarUrl.observe(this, androidx.lifecycle.Observer {
            viewModel.getAvatar(it)
        })
        viewModel.opponentAvatarBitmap.observe(this, androidx.lifecycle.Observer {
            binding.callerCircleImageView.setImageBitmap(it)
        })

        binding.disableMicrophoneCircleImageView.setOnClickListener {
            if (viewModel.isEnabledMicrophone.value!!) {
                viewModel.isEnabledMicrophone.value = false
                localAudioTrack.setEnabled(false)
            } else {
                viewModel.isEnabledMicrophone.value = true
                localAudioTrack.setEnabled(true)
            }
        }

        binding.disableAudioCircleImageView.setOnClickListener {
            if (viewModel.isEnabledVolume.value!!) {
                viewModel.isEnabledVolume.value = false
            } else {
                viewModel.isEnabledVolume.value = true
            }
        }

        binding.acceptCallCardView.setOnClickListener {
            if (SharedConfigs.isCalling) {
                Log.d("SignallingClient", "accept call on click")
                SignallingClient.getInstance()!!.emitCallAccepted(true)
            } else {
                SignallingClient.getInstance()!!.callOpponent()
            }
            SignallingClient.getInstance()!!.isCallingNotProgress.value = false
        }

        binding.hangUpCallCardView.setOnClickListener {
            hangup()
        }

        viewModel.connectionStatus.observe(this, androidx.lifecycle.Observer {
            Log.i("++++", "connection status $it")
//            if (it == PeerConnection.IceConnectionState.FAILED || it == PeerConnection.IceConnectionState.CLOSED) {
//                hangup()
//            }
        })



    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ALL_PERMISSIONS_CODE && grantResults.size == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            start()
        } else {
            finish()
        }
    }

    private fun initViews() {
        localVideoView = binding.localView
        remoteVideoView = binding.remoteView
    }

    private fun initVideos() {
        val rootEglBase: EglBase = EglBase.create()
        localVideoView.init(rootEglBase.eglBaseContext, null)
        remoteVideoView.init(rootEglBase.eglBaseContext, null)
        localVideoView.setZOrderMediaOverlay(true)
        remoteVideoView.setZOrderMediaOverlay(true)
    }

    private fun getIceServers() {
        peerIceServers.add(PeerConnection.IceServer.builder("stun:stun1.l.google.com:19302").createIceServer())
        peerIceServers.add(PeerConnection.IceServer.builder("stun:stun2.l.google.com:19302").createIceServer())
        peerIceServers.add(PeerConnection.IceServer.builder("stun:stun3.l.google.com:19302").createIceServer())
        peerIceServers.add(PeerConnection.IceServer.builder("stun:stun4.l.google.com:19302").createIceServer())
    }

    private fun start() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        initViews()
        initVideos()
        getIceServers()
        SignallingClient.getInstance()!!.init(this)

        val initializationOptions =
            PeerConnectionFactory.InitializationOptions.builder(this)
                .createInitializationOptions()
        PeerConnectionFactory.initialize(initializationOptions)

        val options = PeerConnectionFactory.Options()
        val defaultVideoEncoderFactory = DefaultVideoEncoderFactory(
            rootEglBase?.eglBaseContext,  /* enableIntelVp8Encoder */
            true,  /* enableH264HighProfile */
            true
        )
        val defaultVideoDecoderFactory =
            DefaultVideoDecoderFactory(rootEglBase?.eglBaseContext)
        peerConnectionFactory = PeerConnectionFactory.builder()
            .setOptions(options)
            .setVideoEncoderFactory(defaultVideoEncoderFactory)
            .setVideoDecoderFactory(defaultVideoDecoderFactory)
            .createPeerConnectionFactory()

        val videoCapturerAndroid: VideoCapturer? = createCameraCapturer(Camera1Enumerator(false))

        audioConstraints = MediaConstraints()
        videoConstraints = MediaConstraints()

        if (videoCapturerAndroid != null) {
            surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase?.eglBaseContext)
            videoSource = peerConnectionFactory.createVideoSource(videoCapturerAndroid.isScreencast)
            videoCapturerAndroid.initialize(
                surfaceTextureHelper,
                this,
                videoSource.capturerObserver
            )
        }
        localVideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource)

        audioSource = peerConnectionFactory.createAudioSource(audioConstraints)
        localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource)
        videoCapturerAndroid?.startCapture(1024, 720, 30)
//        localVideoView.visibility = View.VISIBLE
        localVideoTrack.addSink(localVideoView)
        localVideoView.setMirror(true)
        remoteVideoView.setMirror(true)
        gotUserMedia = true
        if (SignallingClient.getInstance()!!.isInitiator) {
            onTryToStart()
        }
    }

    override fun onTryToStart() {
        runOnUiThread {
            if (!SignallingClient.getInstance()!!.isStarted && SignallingClient.getInstance()!!.isChannelReady) {
                Log.d("SignallingClient", "on try to start")
                createPeerConnection()
                SignallingClient.getInstance()!!.isStarted = true
                if (SignallingClient.getInstance()!!.isInitiator) {
                    doCall()
                }
            }
        }
    }

    private fun createPeerConnection() {
        val rtcConfig = PeerConnection.RTCConfiguration(peerIceServers)
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
        rtcConfig.continualGatheringPolicy =
            PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA
        localPeer = peerConnectionFactory.createPeerConnection(
            rtcConfig,
            object : CustomPeerConnectionObserver("localPeerCreation") {
                override fun onIceConnectionChange(iceConnectionState: PeerConnection.IceConnectionState) {
                    Log.i("++++", "iceConnection State $iceConnectionState")
//                    viewModel.connectionStatus.value = iceConnectionState
//                    if (iceConnectionState == PeerConnection.IceConnectionState.FAILED || iceConnectionState == PeerConnection.IceConnectionState.CLOSED) {
//                        hangup()
//                    }
                    super.onIceConnectionChange(iceConnectionState)
                }

                override fun onIceCandidate(iceCandidate: IceCandidate) {
                    super.onIceCandidate(iceCandidate)
                    onIceCandidateReceived(iceCandidate)
                }

                override fun onAddStream(mediaStream: MediaStream) {
                    showToast("Received Remote stream")
                    super.onAddStream(mediaStream)
                    gotRemoteStream(mediaStream)
                }
            })
        addStreamToLocalPeer()
    }

    private fun addStreamToLocalPeer() {
        //creating local mediastream
        val stream = peerConnectionFactory.createLocalMediaStream("102")
        stream.addTrack(localAudioTrack)
        stream.addTrack(localVideoTrack)
        localPeer!!.addStream(stream)
    }

    private fun doCall() {
        sdpConstraints = MediaConstraints()
        sdpConstraints.mandatory.add(
            MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true")
        )
        sdpConstraints.mandatory.add(
            MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true")
        )
        localPeer!!.createOffer(object : CustomSdpObserver("SignallingClient") {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                super.onCreateSuccess(sessionDescription)
                localPeer!!.setLocalDescription(
                    CustomSdpObserver("SignallingClient"),
                    sessionDescription
                )
                Log.d("SignallingClient", "onCreateSuccess emit ")
                SignallingClient.getInstance()!!.emitMessage(sessionDescription)
            }
        }, sdpConstraints)
    }

    private fun gotRemoteStream(stream: MediaStream) {
        val videoTrack = stream.videoTracks[0]
        runOnUiThread {
            try {
                remoteVideoView.visibility = View.VISIBLE
                localVideoView.visibility = View.VISIBLE
                videoTrack.addSink(remoteVideoView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onIceCandidateReceived(iceCandidate: IceCandidate?) {
        SignallingClient.getInstance()!!.emitIceCandidate(iceCandidate!!)
    }

    override fun onNewPeerJoined() {
        showToast("Remote Peer Joined")
    }

    override fun onRemoteHangUp() {
        showToast("Remote Peer hungup")
        runOnUiThread { hangup() }
    }

    override fun onCallAccepted(room: String?) {
        runOnUiThread {
            createPeerConnection()
            doOffer()
        }
    }
    override fun onOfferReceived(data: JSONObject?) {
        runOnUiThread {
            if (!SignallingClient.getInstance()!!.isInitiator && !SignallingClient.getInstance()!!.isStarted) {
                onTryToStart()
            }
            try {
                if (data != null) {
                    localPeer?.setRemoteDescription(
                        CustomSdpObserver("localSetRemote"),
                        SessionDescription(SessionDescription.Type.OFFER, data.getString("sdp"))
                    )
                }
                doAnswer()
//                updateVideoViews(true)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun doOffer() {
        sdpConstraints = MediaConstraints()
        sdpConstraints.mandatory.add(
            MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true")
        )
        sdpConstraints.mandatory.add(
            MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true")
        )
        localPeer?.createOffer(object : CustomSdpObserver("SignallingClient doOffer") {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                super.onCreateSuccess(sessionDescription)
                localPeer?.setLocalDescription(
                    CustomSdpObserver("SignallingClient set local"),
                    sessionDescription
                )
                SignallingClient.getInstance()!!.emitOffer(sessionDescription)
            }
        }, sdpConstraints)

    }

    private fun doAnswer() {
        localPeer?.createAnswer(object : CustomSdpObserver("localCreateAns") {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                super.onCreateSuccess(sessionDescription)
                localPeer!!.setLocalDescription(
                    CustomSdpObserver("localSetLocal"),
                    sessionDescription
                )
                SignallingClient.getInstance()!!.emitAnswer(sessionDescription)
            }
        }, MediaConstraints())
    }

    override fun onAnswerReceived(data: JSONObject?) {
        try {
            if (data != null) {
                localPeer?.setRemoteDescription(
                    CustomSdpObserver("SignallingClient localSetRemote"),
                    SessionDescription(
                        SessionDescription.Type.fromCanonicalForm(
                            data.getString("type").toLowerCase(Locale.ROOT)
                        ), data.getString("sdp")
                    )
                )
            }
//            updateVideoViews(true)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onIceCandidateReceived(data: JSONObject?) {
        try {
            if (data != null) {
                Log.d("SignallingClient", "onIceCandidateReceived $data")
                localPeer?.addIceCandidate(
                    IceCandidate(
                        data.getString("sdpMid"),
                        data.getInt("sdpMLineIndex"),
                        data.getString("candidate")
                    )
                )
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

//    private fun updateVideoViews(remoteVisible: Boolean) {
//        runOnUiThread {
//            var params = localVideoView.layoutParams
//            if (remoteVisible) {
//                params.height = dpToPx(130)
//                params.width = dpToPx(100)
//            } else {
//                params = FrameLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT
//                )
//            }
//            localVideoView.layoutParams = params
//        }
//    }

    private fun hangup() {
//        try {
//            if (localPeer != null) {
//                localPeer!!.close()
//            }
//            localPeer = null
////            SignallingClient.getInstance()!!.close()
////            updateVideoViews(false)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
        if (SignallingClient.getInstance()!!.isCallingNotProgress.value == false) {
            SignallingClient.getInstance()!!.leaveRoom()
        } else {
            SignallingClient.getInstance()!!.emitCallAccepted(false)
        }
        SignallingClient.getInstance()!!.isStarted = false
        SharedConfigs.isCalling = false
        try {
            if (localPeer != null) {
                localPeer!!.close()
            }
            peerConnectionFactory.dispose()
            localPeer = null
//                updateVideoViews(false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
//            SignallingClient.getInstance()!!.close()
        onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
//        SignallingClient.getInstance()!!.close()
        if (localPeer != null) {
            localPeer!!.close()
        }
        localPeer = null
        SharedConfigs.callingOpponentId = null
        SharedConfigs.isCallingInProgress = false
        SignallingClient.getInstance()!!.isCallingNotProgress.value = true
        SignallingClient.destroyInstance()
//        if (surfaceTextureHelper != null) {
        surfaceTextureHelper.dispose()
//            surfaceTextureHelper = null
//        }
    }

    private fun dpToPx(dp: Int): Int {
        val displayMetrics = resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    fun showToast(msg: String?) {
        runOnUiThread { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
    }

    private fun createCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {
        val deviceNames = enumerator.deviceNames

        Logging.d(TAG, "Looking for front facing cameras.")
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Logging.d(
                    TAG,
                    "Creating front facing camera capturer."
                )
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }

        Logging.d(TAG, "Looking for other cameras.")
        for (deviceName in deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating other camera capturer.")
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        return null
    }

    companion object {
        private const val TAG = "MainActivity"
//        val socket = SocketManager.getErosSocketInstance()!!
    }
}