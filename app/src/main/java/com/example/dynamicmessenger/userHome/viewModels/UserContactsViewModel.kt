package com.example.dynamicmessenger.userHome.viewModels

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.authorization.ContactsApi
import com.example.dynamicmessenger.network.authorization.models.UserContacts
import com.example.dynamicmessenger.userDataController.SaveToken
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.userDataController.database.SignedUserDatabase
import com.example.dynamicmessenger.userDataController.database.UserTokenRepository
import kotlinx.coroutines.launch

class UserContactsViewModel(application: Application) : AndroidViewModel(application) {
    private val tokenDao = SignedUserDatabase.getSignedUserDatabase(application)!!.userTokenDao()
    private val tokenRep = UserTokenRepository(tokenDao)
    fun getUserContactsFromNetwork(context: Context?, closure: (List<UserContacts>) -> Unit) {
        viewModelScope.launch {
            try {
                val response = ContactsApi.retrofitService.contactsResponseAsync(SharedConfigs.token!!)
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