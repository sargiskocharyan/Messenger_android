package com.example.dynamicmessenger.userCalls.viewModels

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dynamicmessenger.network.authorization.models.User

class CallRoomViewModel : ViewModel() {
    val opponentInformation = MutableLiveData<User>()
    val opponentAvatarUrl = MutableLiveData<String>()
    val opponentAvatarBitmap = MutableLiveData<Bitmap>()
    val opponentTurnOffMicrophoneVisibility = MutableLiveData<Boolean>(false)
    val opponentCameraIsFront = MutableLiveData<Boolean>(true)
    val opponentCameraIsEnabled = MutableLiveData<Boolean>()
    val opponentReconnecting = MutableLiveData<Boolean>()
    val isEnabledMicrophone = MutableLiveData<Boolean>(true)
    val isEnabledVolume = MutableLiveData<Boolean>(true)
    val isEnableSwitchCamera = MutableLiveData<Boolean>(false)
    val isFrontCamera = MutableLiveData<Boolean>(true)
    val isCameraEnabled = MutableLiveData<Boolean>(true)
    val receivedText= MutableLiveData<String>()



}