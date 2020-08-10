package com.example.dynamicmessenger.userHome.viewModels

import android.app.Application
import android.content.Context
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
import com.example.dynamicmessenger.network.ContactsApi
import com.example.dynamicmessenger.network.LoadAvatarApi
import com.example.dynamicmessenger.network.SearchContactsApi
import com.example.dynamicmessenger.network.authorization.models.SearchTask
import com.example.dynamicmessenger.network.authorization.models.User
import com.example.dynamicmessenger.userDataController.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserContactsViewModel(application: Application) : AndroidViewModel(application) {
    private val usersDao = SignedUserDatabase.getUserDatabase(application)!!.savedUserDao()
    private val usersRepository = SavedUserRepository(usersDao)
    private val contactsDao = SignedUserDatabase.getUserDatabase(application)!!.userContactsDao()
    private val contactsRepository = UserContactsRepository(contactsDao)
    val searchResult = MutableLiveData<String>()

    fun saveUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            usersRepository.insert(user)
        }
    }

    fun getUserById(id: String): User? {
        return usersRepository.getUserById(id)
    }

//    fun getSavedContacts(): LiveData<List<User>> {
//        val contacts = MutableLiveData<List<User>>()
//        val contactsList = mutableListOf<User>()
//        val savedContacts = contactsRepository.getUserAllContacts
//        savedContacts.forEach {
//            getUserById(it._id)?.let { it1 ->
//                contactsList.add(it1)
//            }
//        }
//        contacts.value = contactsList
//        return contacts
//    }

    fun saveContacts(contactsList: List<User>) {
        viewModelScope.launch(Dispatchers.IO) {
            val contacts = mutableListOf<Contacts>()
            contactsList.forEach {
                contacts.add(Contacts(it._id))
                saveUser(it)
            }
            contactsRepository.insert(contacts)
        }
    }

//    fun getUserContactsFromNetwork(context: Context?, closure: (List<User>) -> Unit) {
//        viewModelScope.launch {
//            try {
//                val response = ContactsApi.retrofitService.contactsResponseAsync(SharedConfigs.token)
//                if (response.isSuccessful) {
//                    closure(response.body()!!)
//                } else {
//                    Toast.makeText(context, "User is already in your contacts", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

    fun getSearchedContacts(name: String, closure: (List<User>) -> Unit) {
        val task = SearchTask(name)
        viewModelScope.launch {
            try {
                val response = SearchContactsApi.retrofitService.contactsSearchResponseAsync(SharedConfigs.token, task)
                if (response.isSuccessful) {
                    HomeActivity.isAddContacts = true
                    closure(response.body()!!.users)
                } else {
//                    Toast.makeText(context, "Something gone a wrong", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                //                            Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }
}