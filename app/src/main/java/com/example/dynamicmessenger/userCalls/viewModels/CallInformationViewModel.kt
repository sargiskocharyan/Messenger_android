package com.example.dynamicmessenger.userCalls.viewModels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.network.LoadAvatarApi
import com.example.dynamicmessenger.userDataController.database.DiskCache
import com.example.dynamicmessenger.userDataController.database.SignedUserDatabase
import com.example.dynamicmessenger.userDataController.database.UserCalls
import com.example.dynamicmessenger.userDataController.database.UserCallsRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CallInformationViewModel(application: Application) : AndroidViewModel(application) {
    private val diskLruCache = DiskCache.getInstance(application)
    private val callsDao = SignedUserDatabase.getUserDatabase(application)!!.userCallsDao()
    private val callsRepository = UserCallsRepository(callsDao)
    private val context = application
    val callInformation = MutableLiveData<UserCalls>()
    val callTimeDay = MutableLiveData<String>()
    val callTimeHour = MutableLiveData<String>()
    val callDuration = MutableLiveData<String>()
    val callState = MutableLiveData<String>()
    val opponentAvatarBitmap = MutableLiveData<Bitmap>()

    fun getAvatar(receiverURL: String?) {
        viewModelScope.launch {
            if (receiverURL != null) {
                try {
                    if (diskLruCache.get(receiverURL) != null) {
                        opponentAvatarBitmap.value = diskLruCache.get(receiverURL)!!
                    } else {
                        val response = LoadAvatarApi.retrofitService.loadAvatarResponseAsync(receiverURL)
                        if (response.isSuccessful) {
                            val inputStream = response.body()!!.byteStream()
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            opponentAvatarBitmap.value = bitmap
                        }
                    }
                } catch (e: Exception) {
                    Log.i("+++exception", e.toString())
                }
            }
        }
    }

    fun saveCall(userCalls: UserCalls) {
        viewModelScope.launch {
            callsRepository.insert(userCalls)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertLongToTime(time: Long) {
        val date = Date(time)
        val currentDate: Date = Calendar.getInstance().time
        val newFormat = SimpleDateFormat("HH:mm")
        callTimeHour.value = newFormat.format(date)
        if ((currentDate.day == date.day) && (currentDate.month == date.month) && (currentDate.year == date.year)) {
            callTimeDay.value = context.getString(R.string.today)
        } else if ((currentDate.day == date.day + 1) && (currentDate.month == date.month) && (currentDate.year == date.year)) {
            callTimeDay.value = context.getString(R.string.yesterday)
        } else {
            val newFormat = SimpleDateFormat("MMMM-dd")
            callTimeDay.value = newFormat.format(date)
        }
    }

    private fun getCallDurationInSeconds(time: Long) {
        val hours = time / (1000 * 60 * 60) % 24
        val minutes = time / (1000 * 60) % 60
        val seconds = (time / 1000) % 60

        return if (minutes == 0L && hours == 0L) {
            callDuration.value = "${seconds}s"
        } else if (hours == 0L) {
            callDuration.value = "${minutes}m ${seconds}s"
        } else {
            callDuration.value ="${hours}h ${minutes}m ${seconds}s"
        }
    }

    private fun getCallingState(state: Int) {
        when (state) {
            1 -> callState.value = "Outgoing" //TODO change string
            2 -> callState.value = "Incoming"
        }
    }

    fun callInformationByTime(time: Long) {
        callInformation.value = callsRepository.getCallByTime(time)
        getAvatar(callInformation.value?.avatarURL)
        callInformation.value?.let {
            getCallDurationInSeconds(it.duration)
            getCallingState(it.callingState)
            convertLongToTime(it.time)
        }
        Log.i("+++", "call duration ${callDuration.value}")
    }
}