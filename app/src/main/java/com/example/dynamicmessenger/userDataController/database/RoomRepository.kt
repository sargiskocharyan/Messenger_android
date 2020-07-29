package com.example.dynamicmessenger.userDataController.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.dynamicmessenger.network.authorization.models.Chat
import com.example.dynamicmessenger.network.authorization.models.User
import com.example.dynamicmessenger.userDataController.SaveToken

class SignedUserRepository(private val signedUserDao: SignedUserDao) {
    val signedUser: SignedUser = signedUserDao.getSignedUser()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun update(signedUser: SignedUser) {
        signedUserDao.update(signedUser)
    }

    fun deleteAvatarFromRepository() {
        val user = signedUserDao.getSignedUser()
        user.avatarURL = null
        update(user)
    }

    fun delete() {
        signedUserDao.deleteAll()
    }
}

class UserTokenRepository(private val userTokenDao: UserTokenDao) {
    fun getToken() = SaveToken.decrypt(userTokenDao.getUserToken()?.token)
    val tokenExpire = userTokenDao.getUserToken()?.tokenExpire

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun update(token: String, tokenExpire: String) {
        userTokenDao.update(UserToken(SaveToken.encrypt(token), tokenExpire))
    }

    fun delete() {
        userTokenDao.deleteAll()
    }
}

class UserCallsRepository(private val userCallsDao: UserCallsDao) {
    fun getUserCalls() = userCallsDao.getUserCalls()
    val getUserCalls: LiveData<List<UserCalls>> = userCallsDao.getUserCalls()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(userCalls: UserCalls) {
        userCallsDao.insert(userCalls)
    }

    suspend fun deleteCallByTime(time: Long) {
        userCallsDao.deleteCallByTime(time)
    }

    suspend fun delete() {
        userCallsDao.deleteAll()
    }
}

class SavedUserRepository(private val savedUserDao: SavedUserDao) {
    fun getUserById(id: String) = savedUserDao.getUserById(id)
    val getUserAllUsers: List<User> = savedUserDao.getAllUsers()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(user: User) {
        savedUserDao.insert(user)
    }
    suspend fun deleteUserById(id: String) {
        savedUserDao.deleteUserById(id)
    }

    suspend fun deleteAll() {
        savedUserDao.deleteAll()
    }
}

class UserChatsRepository(private val userChatsDao: UserChatsDao) {
    val getUserAllChats: List<Chat> = userChatsDao.getAllChats()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(chats: List<Chat>) {
        userChatsDao.insert(chats)
    }

    suspend fun deleteAll() {
        userChatsDao.deleteAll()
    }
}