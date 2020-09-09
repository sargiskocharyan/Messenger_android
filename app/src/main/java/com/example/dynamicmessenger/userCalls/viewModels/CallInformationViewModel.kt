package com.example.dynamicmessenger.userCalls.viewModels

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.dynamicmessenger.activitys.HomeActivity
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.authorization.models.User
import com.example.dynamicmessenger.userDataController.database.UserCalls
import com.example.dynamicmessenger.utils.Utils

class CallInformationViewModel(application: Application) : AndroidViewModel(application) {
    val callInformation = MutableLiveData<UserCalls>()
    val callTimeDay = MutableLiveData<String>()
    val callTimeHour = MutableLiveData<String>()
    val callDuration = MutableLiveData<String>()
    val callState = MutableLiveData<String>()
    val opponentAvatarBitmap = MutableLiveData<Bitmap>()
    val opponentInformation = MutableLiveData<User>()

    init {
        callInformation.value = HomeActivity.callId?.let { SharedConfigs.userRepository.getUserCallById(it) }
    }

    private fun getCallingState(state: String) {
        when (state) {
            "missed" -> callState.value = "missed" //TODO change string
            "ongoing" -> callState.value = "ongoing"
            "accepted" -> callState.value = "accepted"
        }
    }

    fun configureCallInformation(userCalls: UserCalls) {
        val callStartTime = Utils.convertStringToDate(userCalls.callStartTime)
        val callEndTime = Utils.convertStringToDate(userCalls.callEndTime)
        if (callEndTime != null && callStartTime != null) {
            callDuration.value = Utils.getCallDurationInSeconds(callEndTime.time - callStartTime.time)
//            getCallDurationInSeconds(callEndTime.time - callStartTime.time)
        }
        callTimeHour.value = userCalls.callSuggestTime?.let { Utils.convertDateToHour(it) }
        callTimeDay.value = Utils.convertStringToDate(userCalls.callSuggestTime)?.time?.let { Utils.convertLongToTimeForCall(it) }
        callState.value = userCalls.status

    }
}