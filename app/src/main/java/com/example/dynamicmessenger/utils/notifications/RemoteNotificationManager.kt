package com.example.dynamicmessenger.utils.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
 * static func registerDeviceToken(pushDeviceToken: String, voipDeviceToken)+
 * static func unRegister()
 * static func checkForRegisteredDeviceToken()+
 * static func didReceivePushDeviceToken(token: Data)+
 * static func didReceiveVoipDeviceToken(token: Data)
 * static func requestPermissions()+
 */
object RemoteNotificationManager {

    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )

    private const val ALL_PERMISSIONS_CODE = 1

    fun checkPermissions(context: Context) = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(activity, REQUIRED_PERMISSIONS, ALL_PERMISSIONS_CODE)
    }


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