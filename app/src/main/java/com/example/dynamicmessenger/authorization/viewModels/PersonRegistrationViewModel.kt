package com.example.dynamicmessenger.authorization.viewModels

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.UniversityApi
import com.example.dynamicmessenger.network.UpdateUserApi
import com.example.dynamicmessenger.network.authorization.models.UniversityProperty
import com.example.dynamicmessenger.network.authorization.models.UpdateUserTask
import com.example.dynamicmessenger.userDataController.database.SignedUser
import com.example.dynamicmessenger.utils.MyAlertMessage
import kotlinx.coroutines.launch

class PersonRegistrationViewModel(application: Application): AndroidViewModel(application), Observable {

    @Bindable
    val userEnteredName = MutableLiveData<String>()
    @Bindable
    val userEnteredLastName = MutableLiveData<String>()
    @Bindable
    val userEnteredUsername = MutableLiveData<String>()
    val userEnteredUniversity = MutableLiveData<String>()
    val isValidParameters = MutableLiveData<Boolean>()

    fun updateUserInformation(view: View) {
        viewModelScope.launch {
            try {
                val usernameEditText = UpdateUserTask(userEnteredName.value, userEnteredLastName.value, userEnteredUsername.value, userEnteredUniversity.value)
                val response = UpdateUserApi.retrofitService.updateUserResponseAsync(SharedConfigs.token ,usernameEditText)
                if (response.isSuccessful) {
                    val user = SignedUser(response.body()!!._id,
                        response.body()!!.name,
                        response.body()!!.lastname,
                        response.body()!!.username,
                        response.body()!!.email,
//                        response.body()!!.university,
                        null, //TODO
                        response.body()!!.avatarURL)
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

    fun getAllUniversity(context: Context?, closure: (List<UniversityProperty>) -> Unit){
        var allUniversity: List<UniversityProperty>? = null
        viewModelScope.launch {
            try {
                val response = UniversityApi.retrofitService.universityResponseAsync(SharedConfigs.token)
                if (response.isSuccessful) {
                    allUniversity = response.body()
                    closure(allUniversity!!)
                } else {
                    MyAlertMessage.showAlertDialog(context, "Get all university error")
                }
            } catch (e: Exception) {
                MyAlertMessage.showAlertDialog(context, "Check your internet connection")
            }
        }
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