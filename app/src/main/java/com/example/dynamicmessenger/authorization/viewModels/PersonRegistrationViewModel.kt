package com.example.dynamicmessenger.authorization.viewModels

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.CheckUsernameExistsApi
import com.example.dynamicmessenger.network.UniversityApi
import com.example.dynamicmessenger.network.UpdateUserApi
import com.example.dynamicmessenger.network.authorization.models.UniversityProperty
import com.example.dynamicmessenger.network.authorization.models.UpdateUserTask
import com.example.dynamicmessenger.network.authorization.models.UsernameExistsTask
import com.example.dynamicmessenger.userDataController.database.SignedUser
import com.example.dynamicmessenger.utils.ClassConverter
import com.example.dynamicmessenger.utils.MyAlertMessage
import kotlinx.coroutines.launch

class PersonRegistrationViewModel(application: Application): AndroidViewModel(application), Observable {

    @Bindable
    val userEnteredName = MutableLiveData<String>()
    @Bindable
    val userEnteredLastName = MutableLiveData<String>()
    @Bindable
    val userEnteredUsername = MutableLiveData<String>()
    val userEnteredGender = MutableLiveData<String?>()
    val isValidUsername = MutableLiveData<Boolean?>()
    val isValidName = MutableLiveData<Boolean>()
    val isValidLastName = MutableLiveData<Boolean>()
    val isValidParameters = MutableLiveData<Boolean>()

    init {
        isValidUsername.value = false
        isValidName.value = true
        isValidLastName.value = true
    }

    fun changeIsValidParameters() {
        isValidParameters.value = isValidUsername.value ?: false && isValidName.value!! && isValidLastName.value!!
    }

    fun updateUserInformation(view: View) {
        viewModelScope.launch {
            try {
                if (userEnteredName.value?.isEmpty()!!) {userEnteredName.value = null}
                if (userEnteredLastName.value?.isEmpty()!!) {userEnteredLastName.value = null}
                val usernameEditText = UpdateUserTask(userEnteredName.value, userEnteredLastName.value, userEnteredUsername.value, userEnteredGender.value)
                val response = UpdateUserApi.retrofitService.updateUserResponseAsync(SharedConfigs.token ,usernameEditText)
                if (response.isSuccessful) {
                    val user = ClassConverter.userToSignedUser(response.body()!!)
                    Log.i("+++userResponse", user.toString())
                    SharedConfigs.signedUser = user
                    view.findNavController().navigate(R.id.action_personRegistrationFragment_to_finishRegistrationFragment)
                } else {
                    MyAlertMessage.showAlertDialog(view.context, "Enter correct email")
                }
            } catch (e: Exception) {
                MyAlertMessage.showAlertDialog(view.context, "Check your internet connection")
            }
        }
    }

    fun checkUsernameExists(): MutableLiveData<Boolean> {
        val isUsernameExists = MutableLiveData<Boolean>()
        viewModelScope.launch {
            try {
                val response = CheckUsernameExistsApi.retrofitService.checkUsernameExists(SharedConfigs.token, UsernameExistsTask(userEnteredUsername.value))
                if (response.isSuccessful) {
                    if (response.body()!!.usernameExists) {
                        isUsernameExists.postValue(false)
                    } else {
                        isUsernameExists.postValue(true)
                    }
                }
            } catch (e: Exception) {

            }
        }
        return isUsernameExists
    }

    fun skipRegistration(view: View) {
        view.findNavController().navigate(R.id.action_personRegistrationFragment_to_finishRegistrationFragment)
    }

    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.remove(callback)
    }
}