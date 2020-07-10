package com.example.dynamicmessenger.userHome.viewModels

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.ContactsApi
import com.example.dynamicmessenger.network.LoadAvatarApi
import com.example.dynamicmessenger.network.authorization.models.UserContacts
import com.example.dynamicmessenger.userDataController.database.DiskCache
import com.example.dynamicmessenger.userDataController.database.SignedUserDatabase
import com.example.dynamicmessenger.userDataController.database.UserTokenRepository
import kotlinx.coroutines.launch

class UserContactsViewModel(application: Application) : AndroidViewModel(application) {
    private val tokenDao = SignedUserDatabase.getUserDatabase(application)!!.userTokenDao()
    private val tokenRep = UserTokenRepository(tokenDao)
    private val diskLruCache = DiskCache.getInstance(application)

    fun getUserContactsFromNetwork(context: Context?, closure: (List<UserContacts>) -> Unit) {
        viewModelScope.launch {
            try {
                val response = ContactsApi.retrofitService.contactsResponseAsync(SharedConfigs.token)
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

    fun getAvatar(avatarURL: String, closure: (Bitmap) -> Unit) {
        viewModelScope.launch {
            try {
                if (diskLruCache.get(avatarURL) != null) {
                    closure(diskLruCache.get(avatarURL)!!)
                } else {
                    val response = LoadAvatarApi.retrofitService.loadAvatarResponseAsync(avatarURL)
                    if (response.isSuccessful) {
                        val inputStream = response.body()!!.byteStream()
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        diskLruCache.put(avatarURL, bitmap)
                        closure(bitmap)
                    }
                }
            } catch (e: Exception) {
                Log.i("+++exception", "userInformationViewModel getAvatar $e")
            }
        }
    }
}