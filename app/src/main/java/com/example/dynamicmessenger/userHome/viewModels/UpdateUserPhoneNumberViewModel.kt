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
import com.example.dynamicmessenger.databinding.FragmentUpdateUserPhoneNumberBinding
import com.example.dynamicmessenger.network.UpdateEmailApi
import com.example.dynamicmessenger.network.UpdatePhoneNumberApi
import com.example.dynamicmessenger.network.VerifyEmailApi
import com.example.dynamicmessenger.network.VerifyPhoneNumberApi
import com.example.dynamicmessenger.network.authorization.models.UpdateEmailTask
import com.example.dynamicmessenger.network.authorization.models.UpdatePhoneNumberTask
import com.example.dynamicmessenger.network.authorization.models.VerifyEmailTask
import com.example.dynamicmessenger.network.authorization.models.VerifyPhoneNumberTask
import com.example.dynamicmessenger.utils.ClassConverter
import kotlinx.coroutines.launch
import org.json.JSONObject

class UpdateUserPhoneNumberViewModel : ViewModel() , Observable {
    @Bindable
    val userEnteredPhoneNumber = MutableLiveData<String>()
    @Bindable
    val userEnteredCode = MutableLiveData<String>()

    val isCodeValid = MutableLiveData<Boolean>()
    val isPhoneNumberValid = MutableLiveData<Boolean>()
    val progressBarVisibility = MutableLiveData<Boolean>()

    fun updateUserPhoneNumber(context: Context?, binding: FragmentUpdateUserPhoneNumberBinding) {
        progressBarVisibility.value = true
        viewModelScope.launch {
            try {
                val response = UpdatePhoneNumberApi.retrofitService.updatePhoneNumber(SharedConfigs.token, UpdatePhoneNumberTask(userEnteredPhoneNumber.value!!))
                if (response.isSuccessful) {
                    userEnteredCode.postValue(response.body()!!.code)
                    binding.verificationCode.visibility = View.VISIBLE
                } else {
                    Log.i("+++", "error body ${response}")
                    Toast.makeText(context, "UpdatePhoneNumberApi else ", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show()
            }
            progressBarVisibility.value = false
        }
    }

    fun verifyUserPhoneNumber(context: Context?, closure: (Boolean) -> Unit) {
        progressBarVisibility.value = true
        viewModelScope.launch {
            try {
                val response = VerifyPhoneNumberApi.retrofitService.verifyPhoneNumber(SharedConfigs.token, VerifyPhoneNumberTask(userEnteredPhoneNumber.value!!, userEnteredCode.value ?: ""))
                if (response.isSuccessful) {
                    SharedConfigs.signedUser = ClassConverter.changePhoneNumberOrEmailPropertyToSignedUser(response.body()!!)
                    closure(true)
                    Toast.makeText(context, "Phone number updated", Toast.LENGTH_SHORT).show()
                } else {
                    Log.i("+++", "error body ${response}")
                    Toast.makeText(context, "VerifyPhoneNumberApi else ", Toast.LENGTH_SHORT).show()
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