package com.example.dynamicmessenger.userDataController.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.dynamicmessenger.network.authorization.models.Chat
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
    fun insert(userCalls: UserCalls) {
        userCallsDao.insert(userCalls)
    }

    fun delete() {
        userCallsDao.deleteAll()
    }
}
