package com.example.dynamicmessenger.userHome.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamicmessenger.network.authorization.GetAvatarApi
import com.example.dynamicmessenger.network.authorization.LogoutApi
import com.example.dynamicmessenger.network.authorization.SaveAvatarApi
import com.example.dynamicmessenger.userDataController.SaveToken
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.utils.MyAlertMessage
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.ResponseBody

class UserInformationViewModel : ViewModel() {

    fun getUserAvatarFromNetwork(context: Context?, userID: String, closure: (ResponseBody) -> Unit) {
        val myEncryptedToken = SharedPreferencesManager.getUserToken(context!!)
        val myToken = SaveToken.decrypt(myEncryptedToken)
        viewModelScope.launch {
            try {
                val response = GetAvatarApi.retrofitService.getAvatarResponseAsync(myToken!!, userID).await()
                if (response.isSuccessful) {
                    closure(response.body()!!)
                } else {
                    Toast.makeText(context, "getUserAvatarFromNetwork else ", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun saveUserAvatarFromNetwork(context: Context?, avatar: MultipartBody.Part) {
        val myEncryptedToken = SharedPreferencesManager.getUserToken(context!!)
        val myToken = SaveToken.decrypt(myEncryptedToken)
        viewModelScope.launch {
            try {
                val response = SaveAvatarApi.retrofitService.saveAvatarResponseAsync(myToken!!, avatar).await()
                if (response.isSuccessful) {
                    Toast.makeText(context, "Avatar uploaded", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "getUserAvatarFromNetwork else ", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun logoutNetwork(token: String, context: Context?, closure: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = LogoutApi.retrofitService.logoutResponseAsync(token).await()
                if (response.isSuccessful) {
                    closure(true)
                } else {
                    closure(true)
                }
            } catch (e: Exception) {
                MyAlertMessage.showAlertDialog(context, "Please check yur internet connection")
            }
        }
    }
}