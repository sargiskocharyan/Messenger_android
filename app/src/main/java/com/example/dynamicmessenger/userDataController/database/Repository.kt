package com.example.dynamicmessenger.userDataController.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.dynamicmessenger.userDataController.SaveToken

class SignedUserRepository(private val signedUserDao: SignedUserDao) {
    val signedUser: SignedUser = signedUserDao.getSignedUser()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun insert(signedUser: SignedUser) {
        signedUserDao.insert(signedUser)
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