package com.example.dynamicmessenger.authorization.viewModels

import android.app.Application
import android.util.Log
import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.MainActivity
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.LoginApi
import com.example.dynamicmessenger.network.MailExistApi
import com.example.dynamicmessenger.network.RegistrationApi
import com.example.dynamicmessenger.network.authorization.models.*
import com.example.dynamicmessenger.userDataController.database.*
import com.example.dynamicmessenger.utils.MyAlertMessage
import kotlinx.coroutines.launch

class PersonLoginViewModel: ViewModel(), Observable {

    @Bindable
    val userEnteredCode = MutableLiveData<String>()
    val isEmailExist = MutableLiveData<Boolean>()
    val hintVisibility = MutableLiveData<Boolean>()
    val isCodeValid = MutableLiveData<Boolean>()
    val progressBarVisibility = MutableLiveData<Boolean>()
    var personEmail: String? = null

    fun loginNetwork(view: View, closure: (Boolean) -> Unit) {
        progressBarVisibility.value = true
        viewModelScope.launch {
            if (isEmailExist.value!!) {
                try {
                    val response = LoginApi.retrofitService.loginResponseAsync(LoginTask(personEmail!!, userEnteredCode.value!!))
                    if (response.isSuccessful) {
                        //TODO:Use GsonFactory or Moshi?
                        val signedUSer = SignedUser(response.body()!!.user._id,
                                                    response.body()!!.user.name,
                                                    response.body()!!.user.lastname,
                                                    response.body()!!.user.username,
                                                    response.body()!!.user.email,
//                                                    response.body()!!.user.university, TODO
                            null,
                                                    response.body()!!.user.avatarURL)
                        SharedConfigs.signedUser = signedUSer
                        SharedConfigs.token = response.body()!!.token
                        closure(true)
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
                    val response = RegistrationApi.retrofitService.registrationResponseAsync(LoginTask(personEmail!!, userEnteredCode.value!!))
                    if (response.isSuccessful) {
                        val signedUSer = SignedUser(response.body()!!.user._id,
                                                    response.body()!!.user.name,
                                                    response.body()!!.user.lastname,
                                                    response.body()!!.user.username,
                                                    response.body()!!.user.email,
//                                                        response.body()!!.user.university,//TODO
                            null,
                                                    response.body()!!.user.avatarURL)
                        SharedConfigs.signedUser = signedUSer
                        SharedConfigs.token = response.body()!!.token
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
                val response = MailExistApi.retrofitService.isMailExistAsync(EmailExistTask(personEmail!!))
                if (response.isSuccessful) {
                    MainActivity.userMailExists = response.body()!!.mailExist
                    MainActivity.userCode = response.body()!!.code
                    MainActivity.userMail = personEmail
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