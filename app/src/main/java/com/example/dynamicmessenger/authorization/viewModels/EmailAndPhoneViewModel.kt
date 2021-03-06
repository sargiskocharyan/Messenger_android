package com.example.dynamicmessenger.authorization.viewModels

import android.app.Application
import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.network.MailExistApi
import com.example.dynamicmessenger.network.authorization.models.EmailExistTask
import com.example.dynamicmessenger.utils.MyAlertMessage
import kotlinx.coroutines.launch

class EmailAndPhoneViewModel(application: Application) : AndroidViewModel(application), Observable {

    @Bindable
    val userEnteredEmail = MutableLiveData<String>()
    val isEmailValid = MutableLiveData<Boolean>()
    val progressBarVisibility = MutableLiveData<Boolean>()
    val isEmailExists = MutableLiveData<Boolean>()
    val userCode = MutableLiveData<String>()

    fun checkIsMailExist(view: View) {
        progressBarVisibility.value = true
        viewModelScope.launch {
            try {
                val response = MailExistApi.retrofitService.isMailExist(EmailExistTask(userEnteredEmail.value!!))
                if (response.isSuccessful) {
                    userCode.value = response.body()!!.code
                    isEmailExists.value = response.body()!!.mailExist
                    view.findNavController().navigate(R.id.action_emailAndPhoneFragment_to_personLoginFragment)
                } else {
                    MyAlertMessage.showAlertDialog(view.context, "Enter correct email")
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
