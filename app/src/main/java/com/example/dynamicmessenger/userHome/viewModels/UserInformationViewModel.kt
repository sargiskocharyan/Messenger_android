package com.example.dynamicmessenger.userHome.viewModels

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.dynamicmessenger.network.authorization.GetAvatarApi
import com.example.dynamicmessenger.network.authorization.LogoutApi
import com.example.dynamicmessenger.network.authorization.SaveAvatarApi
import com.example.dynamicmessenger.userDataController.SaveToken
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.utils.MyAlertMessage
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UserInformationViewModel : ViewModel() {

    fun getUserAvatarFromNetwork(context: Context?, userID: String, closure: (ResponseBody) -> Unit) {
        val myEncryptedToken = SharedPreferencesManager.getUserToken(context!!)
        val myToken = SaveToken.decrypt(myEncryptedToken)
        val getProperties: Call<ResponseBody> = GetAvatarApi.retrofitService.getAvatarResponse(myToken!!, userID)
        try {
            getProperties.enqueue(object  : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    Log.i("+++" , response.toString())
                    if (response.isSuccessful) {
                        closure(response.body()!!)
                    } else {
                        Toast.makeText(context, "getUserAvatarFromNetwork else ", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(
                    call: Call<ResponseBody>,
                    t: Throwable) {
                    Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            Toast.makeText(context, "Something gone a wrong", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveUserAvatarFromNetwork(context: Context?, avatar: MultipartBody.Part) {
        val myEncryptedToken = SharedPreferencesManager.getUserToken(context!!)
        val myToken = SaveToken.decrypt(myEncryptedToken)
        val getProperties: Call<Void> = SaveAvatarApi.retrofitService.saveAvatarResponse(myToken!!, avatar)
        try {
            getProperties.enqueue(object  : Callback<Void> {
                override fun onResponse(
                    call: Call<Void>,
                    response: Response<Void>
                ) {
                    if (response.isSuccessful) {
                    } else {
                        Toast.makeText(context, "getUserAvatarFromNetwork else ", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(
                    call: Call<Void>,
                    t: Throwable) {
                    Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            Toast.makeText(context, "Something gone a wrong", Toast.LENGTH_SHORT).show()
        }
    }

    fun logoutNetwork(token: String, context: Context?, closure: (Boolean) -> Unit) {
        val getProperties: Call<Void> = LogoutApi.retrofitService.logoutResponse(token)
        try {
            getProperties.enqueue(object : Callback<Void> {
                override fun onResponse(
                    call: Call<Void>,
                    response: Response<Void>
                ) {
                    if (response.isSuccessful) {
                        closure(true)
                    } else {
                        closure(true)
                    }
                }
                override fun onFailure(
                    call: Call<Void>,
                    t: Throwable
                ) {
                    MyAlertMessage.showAlertDialog(context, "Please check yur internet connection$t")
                }
            })
        } catch (e: Exception) {
            Toast.makeText(context, "Something gone a wrong", Toast.LENGTH_SHORT).show()
        }
    }
}