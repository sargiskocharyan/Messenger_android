package com.example.dynamicmessenger.userChatRoom.viewModels

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamicmessenger.activitys.HomeActivity
import com.example.dynamicmessenger.activitys.HomeActivity.Companion.opponentUser
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.AddContactApi
import com.example.dynamicmessenger.network.LoadAvatarApi
import com.example.dynamicmessenger.network.authorization.models.AddUserContactTask
import com.example.dynamicmessenger.userDataController.database.DiskCache
import com.example.dynamicmessenger.userDataController.database.SignedUserDatabase
import com.example.dynamicmessenger.userDataController.database.UserCalls
import com.example.dynamicmessenger.userDataController.database.UserCallsRepository
import kotlinx.coroutines.launch

class OpponentInformationViewModel(application: Application) : AndroidViewModel(application) {

    private val diskLruCache = DiskCache.getInstance(application)
    private val context = application
    private val callsDao = SignedUserDatabase.getUserDatabase(application)!!.userCallsDao()
    private val callsRepository = UserCallsRepository(callsDao)

    fun saveCall(userCalls: UserCalls) {
        viewModelScope.launch {
            callsRepository.insert(userCalls)
        }
    }

    fun getAvatar(closure: (Bitmap) -> Unit) {
        viewModelScope.launch {
            if (opponentUser?.avatarURL != null) {
                try {
                    if (diskLruCache.get(opponentUser!!.avatarURL!!) != null) {
                        closure(diskLruCache.get(opponentUser!!.avatarURL!!)!!)
                    } else {
                        val response = LoadAvatarApi.retrofitService.loadAvatarResponseAsync(
                            opponentUser!!.avatarURL!!
                        )
                        if (response.isSuccessful) {
                            val inputStream = response.body()!!.byteStream()
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            diskLruCache.put(opponentUser!!.avatarURL!!, bitmap)
                            closure(bitmap)
                        }
                    }
                } catch (e: Exception) {
                    Log.i("+++exception", "userInformationViewModel getAvatar $e")
                }
            }
        }
    }

    fun addUserToContacts(closure: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val task = AddUserContactTask(HomeActivity.receiverID!!)
                val response =
                    AddContactApi.retrofitService.addContactResponseAsync(SharedConfigs.token, task)
                if (response.isSuccessful) {
                    HomeActivity.isAddContacts = false
                    Toast.makeText(context, "User added in your contacts", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "User is already in your contacts", Toast.LENGTH_SHORT).show()
                }
                //TODO:Use LiveData
                closure(true)
            } catch (e: Exception) {
                Log.i("+++", "add contact exception $e")
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

}