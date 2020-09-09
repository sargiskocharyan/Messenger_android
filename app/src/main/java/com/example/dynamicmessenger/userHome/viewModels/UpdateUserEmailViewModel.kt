package com.example.dynamicmessenger.userHome.viewModels

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.databinding.FragmentUpdateUserEmailBinding
import com.example.dynamicmessenger.network.UpdateEmailApi
import com.example.dynamicmessenger.network.VerifyEmailApi
import com.example.dynamicmessenger.network.authorization.models.UpdateEmailTask
import com.example.dynamicmessenger.network.authorization.models.VerifyEmailTask
import com.example.dynamicmessenger.utils.ClassConverter
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class UpdateUserEmailViewModel : ViewModel() , Observable {
    @Bindable
    val userEnteredEmail = MutableLiveData<String>()
    @Bindable
    val userEnteredCode = MutableLiveData<String>()

    val isCodeValid = MutableLiveData<Boolean>()
    val isEmailValid = MutableLiveData<Boolean>()
    val progressBarVisibility = MutableLiveData<Boolean>()

    fun updateUserEmail(context: Context?, binding: FragmentUpdateUserEmailBinding) {
        progressBarVisibility.value = true
        viewModelScope.launch {
            try {
                val response = UpdateEmailApi.retrofitService.updateEmail(SharedConfigs.token, UpdateEmailTask(userEnteredEmail.value!!))
                if (response.isSuccessful) {
                    userEnteredCode.postValue(response.body()!!.code)
                    binding.verificationCode.visibility = View.VISIBLE
                } else {
                    Log.i("+++", "error body ${response}")
                    Toast.makeText(context, "UpdateEmailApi else ", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show()
            }
            progressBarVisibility.value = false
        }
    }

    fun verifyUserEmail(context: Context?, closure: (Boolean) -> Unit) {
        progressBarVisibility.value = true
        viewModelScope.launch {
            try {
                val response = VerifyEmailApi.retrofitService.verifyEmail(SharedConfigs.token, VerifyEmailTask(userEnteredEmail.value!!, userEnteredCode.value ?: ""))
                if (response.isSuccessful) {
                    SharedConfigs.signedUser = ClassConverter.changePhoneNumberOrEmailPropertyToSignedUser(response.body()!!)
                    closure(true)
                    Toast.makeText(context, "Email updated", Toast.LENGTH_SHORT).show()
                } else {
                    Log.i("+++", "error body ${response}")
                    Toast.makeText(context, "VerifyEmailApi else ", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show()
            }
            progressBarVisibility.value = false
        }
    }



    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.remove(callback)
    }
}