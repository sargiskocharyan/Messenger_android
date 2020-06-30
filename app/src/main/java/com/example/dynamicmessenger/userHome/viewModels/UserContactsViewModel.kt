package com.example.dynamicmessenger.userHome.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamicmessenger.network.authorization.ContactsApi
import com.example.dynamicmessenger.network.authorization.models.UserContacts
import com.example.dynamicmessenger.userDataController.SaveToken
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import kotlinx.coroutines.launch

class UserContactsViewModel: ViewModel() {
    fun getUserContactsFromNetwork(context: Context?, closure: (List<UserContacts>) -> Unit) {
        val myEncryptedToken = SharedPreferencesManager.getUserToken(context!!)
        val myToken = SaveToken.decrypt(myEncryptedToken)
        viewModelScope.launch {
            try {
                val response = ContactsApi.retrofitService.contactsResponseAsync(myToken!!)
                if (response.isSuccessful) {
                    closure(response.body()!!)
                } else {
                    Toast.makeText(context, "User is already in your contacts", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }
}