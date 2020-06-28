package com.example.dynamicmessenger.authorization.viewModels

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.databinding.FragmentEmailAndPhoneBinding
import com.example.dynamicmessenger.network.authorization.MailExistApi
import com.example.dynamicmessenger.network.authorization.models.EmailExistTask
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.utils.MyAlertMessage
import kotlinx.coroutines.launch

class EmailAndPhoneViewModel : ViewModel() {

    fun emailNetwork(view: View, task: EmailExistTask, context: Context?, binding: FragmentEmailAndPhoneBinding) {
        viewModelScope.launch {
            try {
                val response = MailExistApi.retrofitService.isMailExistAsync(task).await()
                Log.i("+++", "$response")
                if (response.isSuccessful) {
                    SharedPreferencesManager.setUserMailExists(context!!, response.body()!!.mailExist)
                    SharedPreferencesManager.setUserCode(context, response.body()!!.code)
                    SharedPreferencesManager.setUserMail(context, task.email)
                    view.findNavController().navigate(R.id.action_emailAndPhoneFragment_to_personLoginFragment)
                } else {
                    binding.progressBar.visibility = View.INVISIBLE
                    MyAlertMessage.showAlertDialog(context, "Enter correct email")
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.INVISIBLE
                Log.i("+++", "$e")
                MyAlertMessage.showAlertDialog(context, "Please check yur internet connection")
            }
        }
    }
}
