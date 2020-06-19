package com.example.dynamicmessenger.authorization.viewModels

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PersonLoginViewModel: ViewModel() {

    fun loginRegisterVisibilityChange(isExist: Boolean, binding: FragmentPersonLoginBinding) {
        if (isExist) {
            binding.loginRegisterTextView.setText(R.string.login)
        } else {
            binding.loginRegisterTextView.setText(R.string.registration)
        }
    }

    fun loginNetwork(view: View, isExist: Boolean, task: LoginTask, context: Context?, binding: FragmentPersonLoginBinding, closure: (Boolean) -> Unit) {
        if (isExist) {
            binding.progressBar.visibility = View.INVISIBLE
            val resultLogin: Call<LoginProperty> = LoginApi.retrofitService.loginResponse(task)
            try {
                resultLogin.enqueue(object : Callback<LoginProperty?> {
                    override fun onResponse(
                        call: Call<LoginProperty?>,
                        response: Response<LoginProperty?>
                    ) {
                        if (response.isSuccessful) {
                            binding.progressBar.visibility = View.INVISIBLE
                            SharedPreferencesManager.setUserToken(context!!, SaveToken.encrypt(response.body()!!.token))
                            SharedPreferencesManager.saveUserObject(context, response.body()!!.user)
                            closure(true)
                        } else {
                            MyAlertMessage.showAlertDialog(context, "Enter correct code")
                        }
                    }
                    override fun onFailure(
                        call: Call<LoginProperty?>,
                        t: Throwable
                    ) {
                        MyAlertMessage.showAlertDialog(context, "Please check yur internet connection")
                    }
                })
            } catch (e: Exception) {
                binding.progressBar.visibility = View.INVISIBLE
                Toast.makeText(context, "Something gone a wrong", Toast.LENGTH_SHORT).show()
            }
        } else {
            binding.progressBar.visibility = View.INVISIBLE
            val resultRegister = RegistrationApi.retrofitService.registrationResponse(task)
            try {
                resultRegister.enqueue(object : Callback<RegistrationProperty?> {
                    override fun onResponse(
                        call: Call<RegistrationProperty?>,
                        response: Response<RegistrationProperty?>
                    ) {

                        if (response.isSuccessful) {
                            binding.progressBar.visibility = View.INVISIBLE
                            SharedPreferencesManager.setUserToken(context!!, SaveToken.encrypt(response.body()!!.token))
                            SharedPreferencesManager.saveUserObject(context, response.body()!!.user)
                            view.findNavController().navigate(R.id.action_personLoginFragment_to_personRegistrationFragment)
                        } else {
                            MyAlertMessage.showAlertDialog(context, "Enter correct code")
                        }
                    }
                    override fun onFailure(
                        call: Call<RegistrationProperty?>,
                        t: Throwable
                    ) {
                        MyAlertMessage.showAlertDialog(context, "Please check yur internet connection$t")
                    }
                })
            } catch (e: Exception) {
                Toast.makeText(context, "Something gone a wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun emailNetwork(task: EmailExistTask, context: Context?, binding: FragmentPersonLoginBinding) {
        val getProperties: Call<MailExistProperty> = MailExistApi.retrofitService.isMailExist(task)
        try {
            getProperties.enqueue(object : Callback<MailExistProperty?> {
                override fun onResponse(
                    call: Call<MailExistProperty?>,
                    response: Response<MailExistProperty?>
                ) {
                    binding.progressBar.visibility = View.INVISIBLE
                    if (response.isSuccessful) {
                        SharedPreferencesManager.setUserMailExists(context!!, response.body()!!.mailExist)
                        SharedPreferencesManager.setUserCode(context, response.body()!!.code)
                        SharedPreferencesManager.setUserMail(context, task.email)
                        binding.verificationCode.setText(response.body()!!.code)
                    } else {
                        MyAlertMessage.showAlertDialog(context, "Try again")
                    }
                }
                override fun onFailure(
                    call: Call<MailExistProperty?>,
                    t: Throwable
                ) {
                    binding.progressBar.visibility = View.INVISIBLE
                    MyAlertMessage.showAlertDialog(context, "Please check yur internet connection$t")
                }
            })
        } catch (e: Exception) {
            binding.progressBar.visibility = View.INVISIBLE
            Toast.makeText(context, "Something gone a wrong", Toast.LENGTH_SHORT).show()
        }
    }
}