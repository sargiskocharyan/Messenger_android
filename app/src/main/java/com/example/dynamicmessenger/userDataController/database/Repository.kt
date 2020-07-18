package com.example.dynamicmessenger.userDataController.database

import androidx.annotation.WorkerThread
import com.example.dynamicmessenger.network.authorization.models.Chat
import com.example.dynamicmessenger.userDataController.SaveToken

class SignedUserRepository(private val signedUserDao: SignedUserDao) {
    val signedUser: SignedUser = signedUserDao.getSignedUser()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun insert(signedUser: SignedUser) {
        signedUserDao.insert(signedUser)
    }

    fun deleteAvatarFromRepository() {
        val user = signedUserDao.getSignedUser()
        user.avatarURL = null
        insert(user)
    }

    fun delete() {
        signedUserDao.deleteAll()
    }
}

class UserTokenRepository(private val userTokenDao: UserTokenDao) {
    fun getToken() = SaveToken.decrypt(userTokenDao.getUserToken()?.token)


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun insert(token: String) {
        userTokenDao.insert(UserToken(SaveToken.encrypt(token)))
    }

    fun delete() {
        userTokenDao.deleteAll()
    }
}

class UserCallsRepository(private val userCallsDao: UserCallsDao) {
    fun getUserCalls() = userCallsDao.getUserCalls()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun insert(userCalls: UserCalls) {
        userCallsDao.insert(userCalls)
    }

    fun delete() {
        userCallsDao.deleteAll()
    }
}
