package com.example.dynamicmessenger.utils

import com.example.dynamicmessenger.network.authorization.models.LoginProperty
import com.example.dynamicmessenger.network.authorization.models.User
import com.example.dynamicmessenger.userDataController.database.SignedUser

object ClassConverter {
    fun loginPropertyToSignedUser(loginProperty: LoginProperty): SignedUser {
        return SignedUser(
            loginProperty.user._id,
            loginProperty.user.name,
            loginProperty.user.lastname,
            loginProperty.user.username,
            loginProperty.user.email,
//                                                        loginProperty.user.university,//TODO
            null,
            loginProperty.user.avatarURL)
    }

    fun userToSignedUser(user: User): SignedUser {
        return SignedUser(user._id,
            user.name,
            user.lastname,
            user.username,
            user.email,
//                        user.university,
            null, //TODO
            user.avatarURL)
    }
}