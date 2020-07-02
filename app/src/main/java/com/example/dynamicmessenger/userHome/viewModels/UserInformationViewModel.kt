package com.example.dynamicmessenger.userHome.viewModels

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.*
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.common.AppLangKeys
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.databinding.FragmentUserInformationBinding
import com.example.dynamicmessenger.network.authorization.LogoutApi
import com.example.dynamicmessenger.network.authorization.SaveAvatarApi
import com.example.dynamicmessenger.userDataController.SaveToken
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.userDataController.database.SignedUserDatabase
import com.example.dynamicmessenger.userDataController.database.UserTokenRepository
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
                    Toast.makeText(context, "getUserAvatarFromNetwork else ", Toast.LENGTH_SHORT).show()
                }
                binding.imageUploadProgressBar.visibility = View.INVISIBLE
            } catch (e: Exception) {
                binding.imageUploadProgressBar.visibility = View.INVISIBLE
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
                closure(true)
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