package com.example.dynamicmessenger.userCalls

import androidx.lifecycle.MutableLiveData


object SocketEventsForVideoCalls {
    val offer = MutableLiveData<Array<Any>>()
    val callAccepted = MutableLiveData<Array<Any>>()
    val answer = MutableLiveData<Array<Any>>()
    val candidates = MutableLiveData<Array<Any>>()
}