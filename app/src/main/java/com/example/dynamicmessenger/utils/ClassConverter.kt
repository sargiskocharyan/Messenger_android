package com.example.dynamicmessenger.utils

import com.example.dynamicmessenger.network.authorization.models.ChangePhoneNumberOrEmailProperty
import com.example.dynamicmessenger.network.authorization.models.LoginProperty
import com.example.dynamicmessenger.network.authorization.models.Message
import com.example.dynamicmessenger.network.authorization.models.User
import com.example.dynamicmessenger.network.chatRooms.SocketManager
import com.example.dynamicmessenger.userDataController.database.SignedUser
import com.example.dynamicmessenger.userDataController.database.UserCalls
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

object ClassConverter {
    fun loginPropertyToSignedUser(loginProperty: LoginProperty): SignedUser {
        return SignedUser(
            loginProperty.user._id,
            loginProperty.user.gender,
            loginProperty.user.name,
            loginProperty.user.lastname,
            loginProperty.user.username,
            loginProperty.user.email,
            loginProperty.user.birthday,
            loginProperty.user.phoneNumber,
            loginProperty.user.info,
            loginProperty.user.avatarURL,
            loginProperty.user.missedCallHistory
        )
    }

    fun userToSignedUser(user: User): SignedUser {
        return SignedUser(
            user._id,
            user.gender,
            user.name,
            user.lastname,
            user.username,
            user.email,
            user.birthday,
            user.phoneNumber,
            user.info,
            user.avatarURL,
            user.missedCallHistory
        )
    }

    fun changePhoneNumberOrEmailPropertyToSignedUser(changePhoneNumberOrEmailProperty: ChangePhoneNumberOrEmailProperty): SignedUser {
        return SignedUser(
            changePhoneNumberOrEmailProperty.user._id,
            changePhoneNumberOrEmailProperty.user.gender,
            changePhoneNumberOrEmailProperty.user.name,
            changePhoneNumberOrEmailProperty.user.lastname,
            changePhoneNumberOrEmailProperty.user.username,
            changePhoneNumberOrEmailProperty.user.email,
            changePhoneNumberOrEmailProperty.user.birthday,
            changePhoneNumberOrEmailProperty.user.phoneNumber,
            changePhoneNumberOrEmailProperty.user.info,
            changePhoneNumberOrEmailProperty.user.avatarURL,
            changePhoneNumberOrEmailProperty.user.missedCallHistory
        )
    }

    fun jsonToUserCalls(json: JSONObject): UserCalls {
        val call = Gson().fromJson(json.getString("call").toString(), PushNotificationCall::class.java)
        return UserCalls(
            json.getString("type"),
            call.status,
            emptyList(),
            json.getString("_id"),
            call.callSuggestTime,
            call.caller,
            json.getString("reciever"),
            call.message,
            json.getString("createdAt"),
            json.getString("updatedAt"),
            null,
            null
        )
    }
}

data class PushNotificationCall(
    val callSuggestTime: String?,
    val caller: String?,
    val createdAt: String?,
    val id: String?,
    val message: String?,
    val participants: List<String>?,
    val receiver: String?,
    val status: String?,
    val type: String?,
    val updatedAt: String?
)