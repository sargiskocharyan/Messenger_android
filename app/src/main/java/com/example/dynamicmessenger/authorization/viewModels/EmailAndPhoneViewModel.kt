package com.example.dynamicmessenger.authorization.viewModels

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.databinding.FragmentEmailAndPhoneBinding
import com.example.dynamicmessenger.network.authorization.MailExistApi
import com.example.dynamicmessenger.network.authorization.models.EmailExistTask
import com.example.dynamicmessenger.network.authorization.models.MailExistProperty
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.utils.MyAlertMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EmailAndPhoneViewModel : ViewModel() {

    fun emailNetwork(view: View, task: EmailExistTask, context: Context?, binding: FragmentEmailAndPhoneBinding) {
        val getProperties: Call<MailExistProperty> = MailExistApi.retrofitService.isMailExist(task)
        try {
            getProperties.enqueue(object : Callback<MailExistProperty?> {
                override fun onResponse(
                    call: Call<MailExistProperty?>,
                    response: Response<MailExistProperty?>
                ) {
                    if (response.isSuccessful) {
                        SharedPreferencesManager.setUserMailExists(context!!, response.body()!!.mailExist)
                        SharedPreferencesManager.setUserCode(context, response.body()!!.code)
                        SharedPreferencesManager.setUserMail(context, task.email)
                        view.findNavController().navigate(R.id.action_emailAndPhoneFragment_to_personLoginFragment)
                    } else {
                        binding.progressBar.visibility = View.INVISIBLE
                        MyAlertMessage.showAlertDialog(context, "Enter correct email")
                    }
                }
                override fun onFailure(
                    call: Call<MailExistProperty?>,
                    t: Throwable
                ) {
                    binding.progressBar.visibility = View.INVISIBLE
                    MyAlertMessage.showAlertDialog(context, "Please check yur internet connection")
                }
            })
        } catch (e: Exception) {
            binding.progressBar.visibility = View.INVISIBLE
            Toast.makeText(context, "Something gone a wrong", Toast.LENGTH_SHORT).show()
        }
    }
}
