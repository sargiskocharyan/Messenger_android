package com.example.dynamicmessenger.userHome.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.dynamicmessenger.network.authorization.LogoutApi
import com.example.dynamicmessenger.utils.MyAlertMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserInformationViewModel : ViewModel() {




    fun logoutNetwork(token: String, context: Context?, closure: (Boolean) -> Unit) {
        val getProperties: Call<Void> = LogoutApi.retrofitService.logoutResponse(token)
        try {
            getProperties.enqueue(object : Callback<Void> {
                override fun onResponse(
                    call: Call<Void>,
                    response: Response<Void>
                ) {
                    if (response.isSuccessful) {
                        closure(true)
                    } else {
                        closure(true)
                    }
                }
                override fun onFailure(
                    call: Call<Void>,
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