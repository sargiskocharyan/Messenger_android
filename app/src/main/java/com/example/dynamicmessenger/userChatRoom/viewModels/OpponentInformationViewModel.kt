package com.example.dynamicmessenger.userChatRoom.viewModels

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dynamicmessenger.activitys.HomeActivity
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.AddContactApi
import com.example.dynamicmessenger.network.LoadAvatarApi
import com.example.dynamicmessenger.network.RemoveContactApi
import com.example.dynamicmessenger.network.authorization.models.AddUserContactTask
import com.example.dynamicmessenger.network.authorization.models.RemoveContactTask
import com.example.dynamicmessenger.network.authorization.models.User
import com.example.dynamicmessenger.userDataController.database.*
import kotlinx.coroutines.launch

class OpponentInformationViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application
    private val callsDao = SignedUserDatabase.getUserDatabase(application)!!.userCallsDao()
//    private val callsRepository = UserCallsRepository(callsDao)
    private val userContactsDao = SignedUserDatabase.getUserDatabase(application)!!.userContactsDao()
    private val contactsRepository = UserContactsRepository(userContactsDao)
    private val contactsList: List<String> = getSavedContacts()
    val isUserInContacts = MutableLiveData<Boolean>()
    val opponentUser = MutableLiveData<User>()

    fun getOpponentInformation(user: User?) {
        if (user != null) {
            opponentUser.value = user
            isUserInContacts.value = contactsList.contains(opponentUser.value?._id)
        }
    }

    private fun getSavedContacts(): MutableList<String> {
        val list = mutableListOf<String>()
        val savedContacts = contactsRepository.getUserAllContacts
        savedContacts.forEach { list.add(it._id) }
        return list
    }

    fun saveCall(userCalls: UserCalls) {
//        viewModelScope.launch {
//            callsRepository.insert(userCalls)
//        }
    }

    fun addUserToContacts() {
        viewModelScope.launch {
            try {
                if (isUserInContacts.value!!) {
                    val task = RemoveContactTask(HomeActivity.receiverID!!)
                    val response =
                        RemoveContactApi.retrofitService.removeContact(SharedConfigs.token, task)
                    if (response.isSuccessful) {
                        HomeActivity.isAddContacts = false
                        Toast.makeText(context, "User removed  contacts", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "User is already in your contacts", Toast.LENGTH_SHORT).show()
                    }
                    isUserInContacts.postValue(false)
                } else {
                    val task = AddUserContactTask(HomeActivity.receiverID!!)
                    val response =
                        AddContactApi.retrofitService.addContactResponseAsync(SharedConfigs.token, task)
                    if (response.isSuccessful) {
                        HomeActivity.isAddContacts = false
                        Toast.makeText(context, "User added in your contacts", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "User is already in your contacts", Toast.LENGTH_SHORT).show()
                    }
                    isUserInContacts.postValue(true)
                }
            } catch (e: Exception) {
                Log.i("+++", "add contact exception $e")
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

}