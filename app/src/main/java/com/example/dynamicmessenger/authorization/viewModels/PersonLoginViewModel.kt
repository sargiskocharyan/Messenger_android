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
import com.example.dynamicmessenger.userDataController.database.*
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
    private val _goToNextPage = MutableLiveData<Boolean>(false)
    val goToNextPage: LiveData<Boolean> = _goToNextPage

    val personEmail = MutableLiveData<String>()
    val isEmailExists = MutableLiveData<Boolean>()

    fun loginNetwork(view: View) {
        progressBarVisibility.value = true
        viewModelScope.launch {
            if (isEmailExist.value!!) {
                try {
                    val response = LoginApi.retrofitService.loginResponseAsync(LoginTask(personEmail.value!!, userEnteredCode.value!!))
                    if (response.isSuccessful) {
                        //TODO:Use GsonFactory or Moshi?
                        SharedConfigs.signedUser = ClassConverter.loginPropertyToSignedUser(response.body()!!)
//                        SharedConfigs.token = response.body()!!.token
                        SharedConfigs.saveToken(response.body()!!.token, response.body()!!.tokenExpire)
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
                        SharedConfigs.signedUser = ClassConverter.loginPropertyToSignedUser(response.body()!!)
//                        SharedConfigs.token = response.body()!!.token
                        SharedConfigs.saveToken(response.body()!!.token, response.body()!!.tokenExpire)
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

    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.remove(callback)
    }
}