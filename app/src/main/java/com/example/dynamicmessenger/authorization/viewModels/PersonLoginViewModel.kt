package com.example.dynamicmessenger.authorization.viewModels

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.databinding.FragmentPersonLoginBinding
import com.example.dynamicmessenger.network.authorization.LoginApi
import com.example.dynamicmessenger.network.authorization.MailExistApi
import com.example.dynamicmessenger.network.authorization.RegistrationApi
import com.example.dynamicmessenger.network.authorization.models.*
import com.example.dynamicmessenger.userDataController.SaveToken
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.utils.MyAlertMessage
import kotlinx.coroutines.launch

class PersonLoginViewModel: ViewModel() {

    fun loginRegisterVisibilityChange(isExist: Boolean, binding: FragmentPersonLoginBinding) {
        if (isExist) {
            binding.loginRegisterTextView.setText(R.string.login)
        } else {
            binding.loginRegisterTextView.setText(R.string.registration)
        }
    }

    fun loginNetwork(view: View, isExist: Boolean, task: LoginTask, context: Context?, binding: FragmentPersonLoginBinding, closure: (Boolean) -> Unit) {
        viewModelScope.launch {
            if (isExist) {
                binding.progressBar.visibility = View.INVISIBLE
                try {
                    val response = LoginApi.retrofitService.loginResponseAsync(task).await()
                    if (response.isSuccessful) {
                        binding.progressBar.visibility = View.INVISIBLE
                        SharedPreferencesManager.setUserToken(context!!, SaveToken.encrypt(response.body()!!.token))
                        SharedPreferencesManager.saveUserObject(context, response.body()!!.user)
                        closure(true)
                    } else {
                        MyAlertMessage.showAlertDialog(context, "Enter correct code")
                    }
                } catch (e: Exception) {
                    binding.progressBar.visibility = View.INVISIBLE
                    MyAlertMessage.showAlertDialog(context, "Please check yur internet connection")
                }
            } else {
                binding.progressBar.visibility = View.INVISIBLE
                try {
                    val response = RegistrationApi.retrofitService.registrationResponseAsync(task).await()
                        if (response.isSuccessful) {
                            binding.progressBar.visibility = View.INVISIBLE
                            SharedPreferencesManager.setUserToken(context!!, SaveToken.encrypt(response.body()!!.token))
                            SharedPreferencesManager.saveUserObject(context, response.body()!!.user)
                            view.findNavController().navigate(R.id.action_personLoginFragment_to_personRegistrationFragment)
                        } else {
                            MyAlertMessage.showAlertDialog(context, "Enter correct code")
                        }
                } catch (e: Exception) {
                    MyAlertMessage.showAlertDialog(context, "Please check yur internet connection")
                }
            }
        }
    }

    fun emailNetwork(task: EmailExistTask, context: Context?, binding: FragmentPersonLoginBinding) {
        viewModelScope.launch {
            try {
                val response = MailExistApi.retrofitService.isMailExistAsync(task).await()
                binding.progressBar.visibility = View.INVISIBLE
                if (response.isSuccessful) {
                    SharedPreferencesManager.setUserMailExists(context!!, response.body()!!.mailExist)
                    SharedPreferencesManager.setUserCode(context, response.body()!!.code)
                    SharedPreferencesManager.setUserMail(context, task.email)
                    binding.verificationCode.setText(response.body()!!.code)
                } else {
                    MyAlertMessage.showAlertDialog(context, "Try again")
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.INVISIBLE
                Log.i("+++", "$e")
                MyAlertMessage.showAlertDialog(context, "Please check yur internet connection")
            }
        }
    }
}