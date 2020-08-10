package com.example.dynamicmessenger.userHome.viewModels

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.*
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.common.AppLangKeys
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.databinding.FragmentUserInformationBinding
import com.example.dynamicmessenger.network.LoadAvatarApi
import com.example.dynamicmessenger.network.LogoutApi
import com.example.dynamicmessenger.network.SaveAvatarApi
import com.example.dynamicmessenger.userDataController.database.DiskCache
import com.example.dynamicmessenger.utils.MyAlertMessage
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class UserInformationViewModel(application: Application) : AndroidViewModel(application) {
    private val _username = MutableLiveData<String>()
    val username: LiveData<String> get() = _username

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name

    private val _lastName = MutableLiveData<String>()
    val lastName: LiveData<String> get() = _lastName

    private val _phoneNumber = MutableLiveData<String>()
    val phoneNumber: LiveData<String> get() = _phoneNumber

    val appLanguage = MutableLiveData<AppLangKeys>(AppLangKeys.EN)
    val avatarBitmap = MutableLiveData<Bitmap>()

    init {
        setUserProperty()
    }

    fun saveUserAvatarFromNetwork(context: Context?, avatar: MultipartBody.Part, binding: FragmentUserInformationBinding) {
        binding.imageUploadProgressBar.visibility = View.VISIBLE
        viewModelScope.launch {
            try {
                val response = SaveAvatarApi.retrofitService.saveAvatarResponseAsync(SharedConfigs.token, avatar)
                if (response.isSuccessful) {
                    val user = SharedConfigs.signedUser
                    user!!.avatarURL = response.body()!!.string()
                    SharedConfigs.signedUser = user
                    Toast.makeText(context, "Avatar uploaded", Toast.LENGTH_SHORT).show()
                } else {
                    Log.i("+++", "error body ${response}")
                    Toast.makeText(context, "getUserAvatarFromNetwork else ", Toast.LENGTH_SHORT).show()
                }
                binding.imageUploadProgressBar.visibility = View.INVISIBLE
            } catch (e: Exception) {
                binding.imageUploadProgressBar.visibility = View.INVISIBLE
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun logoutNetwork(token: String?, context: Context?, closure: (Boolean) -> Unit) {
        if (token == null || token == "") {
            closure(true)
            return
        }
        viewModelScope.launch {
            try {
                val response = LogoutApi.retrofitService.logoutResponseAsync(token)
                if (response.isSuccessful) {
                    closure(true)
                } else {
                    closure(true)
                }
            } catch (e: Exception) {
                MyAlertMessage.showAlertDialog(context, "Please check yur internet connection")
                closure(true)
            }
        }
    }

    private fun setUserProperty() {
        val userObject = SharedConfigs.signedUser
        _username.value = userObject?.username
        _email.value = userObject?.email
        _name.value = userObject?.name
        _lastName.value = userObject?.lastname
        _phoneNumber.value = userObject?.phoneNumber
    }
}