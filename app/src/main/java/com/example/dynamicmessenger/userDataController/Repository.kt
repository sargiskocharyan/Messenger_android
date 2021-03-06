package com.example.dynamicmessenger.userDataController

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.*
import com.example.dynamicmessenger.network.authorization.models.Chat
import com.example.dynamicmessenger.network.authorization.models.DeleteUserCallTask
import com.example.dynamicmessenger.network.authorization.models.ReadCallHistoryTask
import com.example.dynamicmessenger.network.authorization.models.User
import com.example.dynamicmessenger.userDataController.database.DiskCache
import com.example.dynamicmessenger.userDataController.database.SignedUserDatabase
import com.example.dynamicmessenger.userDataController.database.UserCalls
import kotlinx.coroutines.*

interface RepositoryInterface {
    fun deleteAllData()

}

class Repository private constructor(val context: Context): RepositoryInterface {

    private val signedUserRepository = SignedUserDatabase.getUserDatabase(context)!!.signedUserDao()
    private val tokenRepository = SignedUserDatabase.getUserDatabase(context)!!.userTokenDao()
    private val userCallsRepository = SignedUserDatabase.getUserDatabase(context)!!.userCallsDao()
    private val savedUserRepository = SignedUserDatabase.getUserDatabase(context)!!.savedUserDao()
    private val userChatsRepository = SignedUserDatabase.getUserDatabase(context)!!.userChatsDao()
    private val userContactsRepository = SignedUserDatabase.getUserDatabase(context)!!.userContactsDao()
    private val diskLruCache = DiskCache.getInstance(context)


    //Chats
    fun getUserChats(swipeRefreshLayout: SwipeRefreshLayout? = null): LiveData<List<Chat>?> {
        swipeRefreshLayout?.isRefreshing = true
        val userChats = MutableLiveData<List<Chat>?>()
        if (userChatsRepository.getAllChats() != null) {
            userChats.value = userChatsRepository.getAllChats()
        }
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = ChatsApi.retrofitService.chatsResponseAsync(SharedConfigs.token)
                if (response.isSuccessful) {
                    response.body()?.let {allChats ->
                        allChats.array?.let {
                            userChatsRepository.insert(it)
                            userChats.postValue(it)
                        }
                    }
                } else {
                    Log.i("++++", "get chats else ${response}")
                    userChats.postValue(null)
                }
            } catch (e: Exception) {
                Log.i("++++", "get chats catch ${e}")
                userChats.postValue(null)
            }
            this.cancel()
        }
//        swipeRefreshLayout?.isRefreshing = false
        return userChats
    }

    //Avatar
    fun getAvatar(avatarURL: String?): LiveData<Bitmap?> {
        val userAvatarBitmap = MutableLiveData<Bitmap?>()
        if (avatarURL != null) {
            if (diskLruCache.get(avatarURL) != null) {
                userAvatarBitmap.value = diskLruCache.get(avatarURL)
                return userAvatarBitmap
            }
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val response = LoadAvatarApi.retrofitService.loadAvatarResponseAsync(avatarURL)
                    if (response.isSuccessful) {
                        val inputStream = response.body()!!.byteStream()
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        diskLruCache.put(avatarURL, bitmap)
                        userAvatarBitmap.postValue(bitmap)
                    }
                } catch (e: Exception) {
                    Log.i("+++exception", e.toString())
                    userAvatarBitmap.postValue(null)
                }
                this.cancel()
            }
        }
        return userAvatarBitmap
    }

    fun getAvatarFromDB(avatarURL: String?): Bitmap? {
        if (avatarURL != null) {
            if (diskLruCache.get(avatarURL) != null) {
                return diskLruCache.get(avatarURL)
            }
        }
        return null
    }

    //Users
    fun getUserInformation(userId: String?): LiveData<User?> {
        val user = MutableLiveData<User?>()
        if (userId != null) {
            user.postValue(savedUserRepository.getUserById(userId))
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val response = GetUserInfoByIdApi.retrofitService.getUserInfoById(SharedConfigs.token, userId)
                    if (response.isSuccessful) {
                        user.postValue(response.body())
                        response.body()?.let { savedUserRepository.insert(it) }
                    } else {
                        user.postValue(null)
                    }
                } catch (e: Exception) {
                    user.postValue(null)
                }
                this.cancel()
            }
        }
        return user
    }

    fun getUserInformationFromDB(userId: String?): User? {
        if (userId != null) {
            return (savedUserRepository.getUserById(userId))
        }
        return null
    }


    //Contacts
    fun getUserContacts(): LiveData<List<User>?> {
        val userContacts = MutableLiveData<List<User>?>()
        val contactsList = mutableListOf<User>()
        val savedContacts = userContactsRepository.getAllContacts()
        savedContacts.forEach {
            getUserInformationFromDB(it._id)?.let { it1 -> contactsList.add(it1) }
        }
        userContacts.value = contactsList
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = ContactsApi.retrofitService.getUserContacts(SharedConfigs.token)
                if (response.isSuccessful) {
                    userContacts.postValue(response.body())
                    response.body()?.let { savedUserRepository.insertList(it) }
                } else {
                    userContacts.postValue(null)
                }
            } catch (e: Exception) {
                userContacts.postValue(null)
            }
            this.cancel()
        }
        return userContacts
    }

    //Calls
    fun getUserCalls(): Pair<LiveData<List<UserCalls>?>, LiveData<Boolean>> {
        val userCalls = MutableLiveData<List<UserCalls>?>()
        val readCallHistory = MutableLiveData<Boolean>()
        if (userCallsRepository.getUserCalls() != null) {
            userCalls.value = userCallsRepository.getUserCalls()
        }
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = GetUserCallsApi.retrofitService.getUserCalls(SharedConfigs.token)
                if (response.isSuccessful) {
                    userCalls.postValue(response.body())
                    response.body()?.let {
                        userCallsRepository.insertList(it)
                        readCallHistory.postValue(readCallHistory(it[it.size - 1]._id))
                    }
                } else {
                    userCalls.postValue(null)
                }
            } catch (e: Exception) {
                userCalls.postValue(null)
            }
            this.cancel()
        }
        return Pair(userCalls, readCallHistory)
    }

    fun getUserCallById(callId: String) = userCallsRepository.getCallById(callId)

    fun deleteCallById(callId: String) {
        GlobalScope.launch(Dispatchers.IO) {
            userCallsRepository.deleteCallById(callId)
            try {
                val response = DeleteUserCallApi.retrofitService.deleteUserCall(SharedConfigs.token, DeleteUserCallTask(callId))
                if (response.isSuccessful) {
//                    userCalls.postValue(response.body())
                } else {
//                    userCalls.postValue(null)
                }
                Log.i("+++", "deleteCallById $response")
            } catch (e: Exception) {
                Log.i("+++", "deleteCallById $e")
//                userCalls.postValue(null)
            }
            this.cancel()
        }
    }

    private suspend fun readCallHistory(lastCallId: String): Boolean {
        return try {
            val response = ReadCallHistoryApi.retrofitService.readCallHistory(SharedConfigs.token, ReadCallHistoryTask(lastCallId))
            response.isSuccessful
        } catch (e: Exception) {
            Log.i("+++exception", "userInformationViewModel getAvatar $e")
            false
        }
    }












    override fun deleteAllData() {
        GlobalScope.launch(Dispatchers.IO) {
            signedUserRepository.deleteAll()
            tokenRepository.deleteAll()
            userCallsRepository.deleteAll()
            savedUserRepository.deleteAll()
            userChatsRepository.deleteAll()
            userContactsRepository.deleteAll()
            this.cancel()
        }
    }

    companion object {
        private var instance: Repository? = null
        fun getInstance(context: Context): Repository? {
            if (instance == null) {
                instance = Repository(context)
            }
            return instance
        }

        fun destroyInstance() {
            instance = null
        }
    }

}