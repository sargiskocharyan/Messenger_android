package com.example.dynamicmessenger.userCalls.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CallRoomViewModel: ViewModel() {
    val isCallingInProgress = MutableLiveData<Boolean>()
}