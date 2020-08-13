package com.example.dynamicmessenger.utils.notifications

import android.annotation.SuppressLint
import android.provider.Settings
import android.util.Log
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.RegisterDeviceApi
import com.example.dynamicmessenger.network.authorization.models.RegisterDeviceTask
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.*
/**
 *
 * static func registerDeviceToken(pushDeviceToken: String, voipDeviceToken)
 * static func unRegister()
 * static func checkForRegisteredDeviceToken()
 * static func didReceivePushDeviceToken(token: Data)
 * static func didReceiveVoipDeviceToken(token: Data)
 * static func requestPermissions()
 */
object RemoteNotificationManager {

    fun registerDeviceToken(deviceUUID: String, firebaseToken: String? = getFirebaseToken() ) {
        val signedUser = SharedConfigs.signedUser
        if (!signedUser?.deviceRegistered!!) {
            firebaseToken?.let {
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        val response = RegisterDeviceApi.retrofitService.registerDevice(SharedConfigs.token, RegisterDeviceTask(deviceUUID, it))
                        if (response.isSuccessful) {
                            signedUser.deviceRegistered = true
                            SharedConfigs.signedUser = signedUser
                        }
                        Log.i("+++", "register device response ${response}")
                    } catch (e: Exception) {
                        Log.i("+++", "register device exception ${e}")
                    }
                    this.cancel()
                }
            }
        }

    }

    private fun getFirebaseToken(): String? {
        var firebaseToken: String? = null
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                firebaseToken = task.result?.token
            }
        }
        return firebaseToken
    }

}