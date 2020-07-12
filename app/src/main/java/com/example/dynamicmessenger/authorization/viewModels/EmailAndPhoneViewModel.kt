package com.example.dynamicmessenger.authorization.viewModels

import android.app.Application
import android.content.Context
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.MainActivity
import com.example.dynamicmessenger.databinding.FragmentEmailAndPhoneBinding
import com.example.dynamicmessenger.network.MailExistApi
import com.example.dynamicmessenger.network.authorization.models.EmailExistTask
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.utils.MyAlertMessage
import kotlinx.coroutines.launch

class EmailAndPhoneViewModel(application: Application) : AndroidViewModel(application) {

    fun emailNetwork(view: View, task: EmailExistTask, context: Context?, binding: FragmentEmailAndPhoneBinding) {
        viewModelScope.launch {
            try {
                val response = MailExistApi.retrofitService.isMailExistAsync(task)
                if (response.isSuccessful) {
                    MainActivity.userMailExists = response.body()!!.mailExist
                    MainActivity.userCode = response.body()!!.code
                    MainActivity.userMail = task.email
                    view.findNavController().navigate(R.id.action_emailAndPhoneFragment_to_personLoginFragment)
                } else {
                    binding.progressBar.visibility = View.INVISIBLE
                    MyAlertMessage.showAlertDialog(context, "Enter correct email")
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.INVISIBLE
                MyAlertMessage.showAlertDialog(context, "Please check yur internet connection")
            }
        }
    }
}
