package com.example.dynamicmessenger.userHome.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.dynamicmessenger.network.authorization.ContactsApi
import com.example.dynamicmessenger.network.authorization.models.UserContacts
import com.example.dynamicmessenger.userDataController.SaveToken
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserContactsViewModel: ViewModel() {
    fun getUserContactsFromNetwork(context: Context?, closure: (List<UserContacts>) -> Unit) {
        val myEncryptedToken = SharedPreferencesManager.getUserToken(context!!)
        val myToken = SaveToken.decrypt(myEncryptedToken)
        val getProperties: Call<List<UserContacts>> = ContactsApi.retrofitService.contactsResponse(myToken!!)
        try {
            getProperties.enqueue(object : Callback<List<UserContacts>?> {
                override fun onResponse(
                    call: Call<List<UserContacts>?>,
                    response: Response<List<UserContacts>?>
                ) {
                    if (response.isSuccessful) {
                        closure(response.body()!!)
                    } else {
                        Toast.makeText(context, "User is already in your contacts", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(
                    call: Call<List<UserContacts>?>,
                    t: Throwable
                ) {
                    Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            Toast.makeText(context, "Something gone a wrong", Toast.LENGTH_SHORT).show()
        }
    }
}