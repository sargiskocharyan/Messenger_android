package com.example.dynamicmessenger.authorization.viewModels

import android.util.Log
import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.*
import androidx.navigation.findNavController
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.LoginApi
import com.example.dynamicmessenger.network.MailExistApi
import com.example.dynamicmessenger.network.RegistrationApi
import com.example.dynamicmessenger.network.authorization.models.*
import com.example.dynamicmessenger.network.chatRooms.SocketManager
import com.example.dynamicmessenger.utils.ClassConverter
import com.example.dynamicmessenger.utils.MyAlertMessage
import kotlinx.coroutines.launch

class PersonLoginViewModel: ViewModel(), Observable {

    @Bindable
    val userEnteredCode = MutableLiveData<String>()
    val isEmailExist = MutableLiveData<Boolean>()
    val hintVisibility = MutableLiveData<Boolean>()
    val isCodeValid = MutableLiveData<Boolean>()
    val progressBarVisibility = MutableLiveData<Boolean>()
    val personEmail = MutableLiveData<String>()
    val isEmailExists = MutableLiveData<Boolean>()

    private val _goToNextPage = MutableLiveData<Boolean>(false)
    val goToNextPage: LiveData<Boolean> = _goToNextPage

    fun loginNetwork(view: View) {
        progressBarVisibility.value = true
        viewModelScope.launch {
            if (isEmailExist.value!!) {
                try {
                    val response = LoginApi.retrofitService.loginResponseAsync(LoginTask(personEmail.value!!, userEnteredCode.value!!))
                    if (response.isSuccessful) {
                        saveAndConfigureUser(response.body()!!)
                        _goToNextPage.value = true
                    } else {
                        MyAlertMessage.showAlertDialog(view.context, "Enter correct code")
                    }
                } catch (e: Exception) {
                    Log.i("+++", "person login $e")
                    MyAlertMessage.showAlertDialog(view.context, "Please check yur internet connection")
                }
                progressBarVisibility.value = false
            } else {
                try {
                    val response = RegistrationApi.retrofitService.registrationResponseAsync(LoginTask(personEmail.value!!, userEnteredCode.value!!))
                    if (response.isSuccessful) {
                        saveAndConfigureUser(response.body()!!)
                        view.findNavController().navigate(R.id.action_personLoginFragment_to_personRegistrationFragment)
                    } else {
                        MyAlertMessage.showAlertDialog(view.context, "Enter correct code")
                    }
                } catch (e: Exception) {
                    Log.i("+++", "person register $e")
                    MyAlertMessage.showAlertDialog(view.context, "Please check yur internet connection")
                }
                progressBarVisibility.value = false
            }
        }
    }

    fun resendVerificationCode(view: View) {
        progressBarVisibility.value = true
        viewModelScope.launch {
            try {
                val response = MailExistApi.retrofitService.isMailExistAsync(EmailExistTask(personEmail.value!!))
                if (response.isSuccessful) {
                    isEmailExists.value = response.body()!!.mailExist
                    userEnteredCode.value = response.body()!!.code
                } else {
                    MyAlertMessage.showAlertDialog(view.context, "Try again")
                }
            } catch (e: Exception) {
                MyAlertMessage.showAlertDialog(view.context, "Please check yur internet connection")
            }
            progressBarVisibility.value = false
        }
    }

    private fun saveAndConfigureUser(response: LoginProperty) {
        SharedConfigs.signedUser = ClassConverter.loginPropertyToSignedUser(response)
        SharedConfigs.saveToken(response.token, response.tokenExpire)
        SocketManager.connectSocket()
    }

    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.remove(callback)
    }
}