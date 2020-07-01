package com.example.dynamicmessenger.userHome.viewModels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.common.AppLangKeys
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.authorization.LogoutApi
import com.example.dynamicmessenger.network.authorization.SaveAvatarApi
import com.example.dynamicmessenger.userDataController.SaveToken
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.utils.MyAlertMessage
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class UserInformationViewModel : ViewModel() {
    private val _username = MutableLiveData<String>()
    val username: LiveData<String> get() = _username

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name

    private val _lastName = MutableLiveData<String>()
    val lastName: LiveData<String> get() = _lastName

    init {
        setUserProperty()
    }

    fun saveUserAvatarFromNetwork(context: Context?, avatar: MultipartBody.Part) {
        val myEncryptedToken = SharedPreferencesManager.getUserToken(context!!)
        val myToken = SaveToken.decrypt(myEncryptedToken)
        viewModelScope.launch {
            try {
                val response = SaveAvatarApi.retrofitService.saveAvatarResponseAsync(myToken!!, avatar)
                if (response.isSuccessful) {
                    val user = SharedPreferencesManager.loadUserObject(context)
                    response.body()
                    user!!.avatarURL = response.body()!!
                    user
                    Log.i("+++upload", response.body()!!)
                    SharedPreferencesManager.saveUserObject(context, user)
                    Toast.makeText(context, "Avatar uploaded", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "getUserAvatarFromNetwork else ", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.i("+++exception", e.toString())
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun logoutNetwork(token: String?, context: Context?, closure: (Boolean) -> Unit) {
        if (token == null) {
            closure(true)
            return
        }
        viewModelScope.launch {
            try {
                val response = LogoutApi.retrofitService.logoutResponseAsync(token!!)
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

    private fun setUserProperty() {
        val userObject = SharedConfigs.signedUser
        _username.value = userObject?.username ?: ""
        _email.value = userObject?.email ?: ""
        _name.value = userObject?.name ?: "Name"
        _lastName.value = userObject?.lastname ?: "Last Name"
    }

    fun setLanguageImage(): Int {
        return when (SharedConfigs.appLang) {
            AppLangKeys.EN -> R.drawable.ic_united_kingdom
            AppLangKeys.RU -> R.drawable.ic_russia
            else -> R.drawable.ic_armenia
        }
    }
}