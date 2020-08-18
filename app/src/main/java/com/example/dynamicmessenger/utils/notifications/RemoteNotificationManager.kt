package com.example.dynamicmessenger.utils.notifications

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.RegisterDeviceApi
import com.example.dynamicmessenger.network.authorization.models.RegisterDeviceTask
import com.example.dynamicmessenger.userDataController.database.SignedUser
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

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


    fun registerDeviceToken(deviceUUID: String, firebaseToken: String? = null) {
        SharedConfigs.signedUser?.let { signedUser ->
            if (!signedUser.deviceRegistered!!) {
                if (firebaseToken != null) {
                    sendFirebaseToken(deviceUUID, firebaseToken, signedUser)
                } else {
                    getFirebaseToken {
                        sendFirebaseToken(deviceUUID, it, signedUser)
                    }
                }
            }
        }
    }

    private fun sendFirebaseToken(deviceUUID: String, token: String?, signedUser: SignedUser) {
        token?.let {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val response = RegisterDeviceApi.retrofitService.registerDevice(SharedConfigs.token, RegisterDeviceTask(deviceUUID, it))
                    if (response.isSuccessful) {
                        signedUser.deviceRegistered = true
                        SharedConfigs.signedUser = signedUser
                    }
                } catch (e: Exception) {
                }
                this.cancel()
            }
        }
    }

    private fun getFirebaseToken(closure: (String) -> Unit) {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.token?.let { closure(it) }
            }
        }
    }

}