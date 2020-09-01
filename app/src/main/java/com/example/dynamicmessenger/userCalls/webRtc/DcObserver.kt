package com.example.dynamicmessenger.userCalls.webRtc

import android.util.Log
import com.example.dynamicmessenger.common.DataChanelMessages
import com.example.dynamicmessenger.userCalls.viewModels.CallRoomViewModel
import org.webrtc.DataChannel
import java.nio.ByteBuffer

class DcObserver(val viewModel: CallRoomViewModel) : DataChannel.Observer {
    override fun onMessage(buffer: DataChannel.Buffer) {
        Log.i("+++--", "on Data buffer $buffer")
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
            DataChanelMessages.turnCameraOn ->
                viewModel.opponentCameraIsEnabled.postValue(true)
            DataChanelMessages.turnCameraOff ->
                viewModel.opponentCameraIsEnabled.postValue(false)
        }

//        executor.execute { events.onReceivedData(command) }
    }

    override fun onBufferedAmountChange(p0: Long) {
        Log.i("+++--", "on DataChannel message :: onBufferedAmountChange $p0")

    }

    override fun onStateChange() {
//        Log.d(TAG, "DataChannel: onStateChange: " + dataChannel.state())
    }

}