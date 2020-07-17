package com.example.dynamicmessenger.userCalls.webRtc

import android.util.Log
import org.webrtc.*

internal open class CustomPeerConnectionObserver(logTag: String) : PeerConnection.Observer {
    private var logTag: String? = this.javaClass.canonicalName
    override fun onSignalingChange(signalingState: PeerConnection.SignalingState) {
        Log.d(
            logTag,
            "onSignalingChange() called with: signalingState = [$signalingState]"
        )
    }

    override fun onIceConnectionChange(iceConnectionState: PeerConnection.IceConnectionState) {
        Log.d(
            logTag,
            "onIceConnectionChange() called with: iceConnectionState = [$iceConnectionState]"
        )
    }

    override fun onIceConnectionReceivingChange(b: Boolean) {
        Log.d(logTag, "onIceConnectionReceivingChange() called with: b = [$b]")
    }

    override fun onIceGatheringChange(iceGatheringState: PeerConnection.IceGatheringState) {
        Log.d(
            logTag,
            "onIceGatheringChange() called with: iceGatheringState = [$iceGatheringState]"
        )
    }

    override fun onIceCandidate(iceCandidate: IceCandidate) {
        Log.d(
            logTag,
            "onIceCandidate() called with: iceCandidate = [$iceCandidate]"
        )
    }

    override fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate>) {
        Log.d(
            logTag,
            "onIceCandidatesRemoved() called with: iceCandidates = [$iceCandidates]"
        )
    }

    override fun onAddStream(mediaStream: MediaStream) {
        Log.d(logTag, "onAddStream() called with: mediaStream = [$mediaStream]")
    }

    override fun onRemoveStream(mediaStream: MediaStream) {
        Log.d(
            logTag,
            "onRemoveStream() called with: mediaStream = [$mediaStream]"
        )
    }

    override fun onDataChannel(dataChannel: DataChannel) {
        Log.d(
            logTag,
            "onDataChannel() called with: dataChannel = [$dataChannel]"
        )
    }

    override fun onRenegotiationNeeded() {
        Log.d(logTag, "onRenegotiationNeeded() called")
    }

    override fun onAddTrack(
        rtpReceiver: RtpReceiver,
        mediaStreams: Array<MediaStream>
    ) {
        Log.d(
            logTag,
            "onAddTrack() called with: rtpReceiver = [$rtpReceiver], mediaStreams = [$mediaStreams]"
        )
    }

    init {
        this.logTag = this.logTag + " " + logTag
    }
}