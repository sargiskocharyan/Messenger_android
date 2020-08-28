package com.example.dynamicmessenger.userCalls

import android.util.Log
import androidx.lifecycle.MutableLiveData
import org.webrtc.DataChannel
import java.nio.ByteBuffer


object SocketEventsForVideoCalls {
    val offer = MutableLiveData<Array<Any>>()
    val callAccepted = MutableLiveData<Array<Any>>()
    val answer = MutableLiveData<Array<Any>>()
    val candidates = MutableLiveData<Array<Any>>()
}

class DcObserver : DataChannel.Observer {
    override fun onMessage(buffer: DataChannel.Buffer) {
        val data: ByteBuffer = buffer.data
        val bytes = ByteArray(data.remaining())
        data.get(bytes)
        val command = String(bytes)
        Log.i("+++--", "on meaasassa ge $command")
//        executor.execute { events.onReceivedData(command) }
    }

    override fun onBufferedAmountChange(p0: Long) {
        TODO("Not yet implemented")
    }

    override fun onStateChange() {
//        Log.d(TAG, "DataChannel: onStateChange: " + dataChannel.state())
    }


//    fun qddsa() {
//        val message: String = "messageET.getText().toString()"
//        if (!message.isEmpty()) {
//            if (sendChannel.state() === DataChannel.State.OPEN) {
//                val buffer = ByteBuffer.wrap(message.toByteArray())
//                sendChannel.send(DataChannel.Buffer(buffer, false))
//            }
//        }
//    }


}