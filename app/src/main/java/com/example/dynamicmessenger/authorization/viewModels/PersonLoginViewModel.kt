package com.example.dynamicmessenger.authorization.viewModels

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.databinding.FragmentPersonLoginBinding
import com.example.dynamicmessenger.network.LoginApi
import com.example.dynamicmessenger.network.MailExistApi
import com.example.dynamicmessenger.network.RegistrationApi
import com.example.dynamicmessenger.network.authorization.models.*
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.userDataController.database.*
import com.example.dynamicmessenger.utils.MyAlertMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PersonLoginViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application


    fun loginRegisterVisibilityChange(isExist: Boolean, binding: FragmentPersonLoginBinding) {
        if (isExist) {
            binding.loginRegisterTextView.setText(R.string.login)
        } else {
            binding.loginRegisterTextView.setText(R.string.register)
        }
    }

    fun loginNetwork(view: View, isExist: Boolean, task: LoginTask, binding: FragmentPersonLoginBinding, closure: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isExist) {
                binding.progressBar.visibility = View.INVISIBLE
                try {
                    val response = LoginApi.retrofitService.loginResponseAsync(task)
                    if (response.isSuccessful) {
                        binding.progressBar.visibility = View.INVISIBLE
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
                        MyAlertMessage.showAlertDialog(context, "Enter correct code")
                    }
                } catch (e: Exception) {
                    binding.progressBar.visibility = View.INVISIBLE
                    Log.i("+++", "person login $e")
//                    MyAlertMessage.showAlertDialog(context, "Please check yur internet connection")
                }
            } else {
                binding.progressBar.visibility = View.INVISIBLE
                try {
                    val response = RegistrationApi.retrofitService.registrationResponseAsync(task)
                        if (response.isSuccessful) {
                            binding.progressBar.visibility = View.INVISIBLE
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
                            MyAlertMessage.showAlertDialog(context, "Enter correct code")
                        }
                } catch (e: Exception) {
                    Log.i("+++", "person register $e")
//                    MyAlertMessage.showAlertDialog(context, "Please check yur internet connection")
                }
            }
        }
    }

    fun emailNetwork(task: EmailExistTask, binding: FragmentPersonLoginBinding) {
        viewModelScope.launch {
            try {
                val response = MailExistApi.retrofitService.isMailExistAsync(task)
                binding.progressBar.visibility = View.INVISIBLE
                if (response.isSuccessful) {
                    SharedPreferencesManager.setUserMailExists(context, response.body()!!.mailExist)
                    SharedPreferencesManager.setUserCode(context, response.body()!!.code)
                    SharedPreferencesManager.setUserMail(context, task.email)
                    binding.verificationCode.setText(response.body()!!.code)
                } else {
                    MyAlertMessage.showAlertDialog(context, "Try again")
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.INVISIBLE
                MyAlertMessage.showAlertDialog(context, "Please check yur internet connection")
            }
        }
    }
}