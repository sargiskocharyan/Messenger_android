package com.example.dynamicmessenger.userCalls.webRtc

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.dynamicmessenger.common.DataChanelMessages
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.userCalls.viewModels.CallRoomViewModel
import org.webrtc.DataChannel
import java.nio.ByteBuffer

class DcObserver(val viewModel: CallRoomViewModel) : DataChannel.Observer {
    override fun onMessage(buffer: DataChannel.Buffer) {
        val data: ByteBuffer = buffer.data
        val bytes = ByteArray(data.remaining())
        data.get(bytes)
        val command = String(bytes)
        Log.i("+++--", "on DataChannel message $command")
        viewModel.receivedText.postValue(command)
        when (command) {
            DataChanelMessages.turnOffMicrophone ->
                viewModel.opponentTurnOffMicrophoneVisibility.postValue(true)
            DataChanelMessages.turnOnMicrophone ->
                viewModel.opponentTurnOffMicrophoneVisibility.postValue(false)
            DataChanelMessages.turnCameraToBack ->
                viewModel.opponentCameraIsFront.postValue(false)
            DataChanelMessages.turnCameraToFront ->
                viewModel.opponentCameraIsFront.postValue(true)
        }

//        executor.execute { events.onReceivedData(command) }
    }

    override fun onBufferedAmountChange(p0: Long) {
        Log.i("+++--", "on DataChannel message :: onBufferedAmountChange")
        TODO("Not yet implemented")
    }

    override fun onStateChange() {
//        Log.d(TAG, "DataChannel: onStateChange: " + dataChannel.state())
    }

}