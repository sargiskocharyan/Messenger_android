package com.example.dynamicmessenger.network.authorization.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

data class EmailExistTask(val email: String)

data class RegistrationTask(val email: String, val code: String)

data class LoginTask (val email: String, val code: String)

data class UpdateUserTask (
    val name: String? = null,
    val lastname: String? = null,
    val username: String? = null,
    val info: String? = null,
    val address: String? = null,
    val gender: String? = null,
    val birthday: String? = null)

data class SearchTask (val term: String)

data class AddUserContactTask (val contactId: String)

data class DateUntil (val dateUntil: String?)

data class OnlineUsersTask (val usersArray: List<String>)

data class RemoveContactTask (val userId: String)

data class HideDataTask (val hide: Boolean)

data class UsernameExistsTask (val username: String?)

data class DeleteUserCallTask (val callId: String)

data class RegisterDeviceTask (val deviceUUID: String, val token: String, val voIPToken: String? = null, val platform: String = "android")

data class LogoutUserTask (val deviceUUID: String)

data class UpdateEmailTask (val mail: String)

data class VerifyEmailTask (val mail: String, val code: String)

data class UpdatePhoneNumberTask (val number: String)

data class VerifyPhoneNumberTask (val number: String, val code: String)

data class ReadCallHistoryTask (val callId: String)

data class AcceptContactRequestTask (val userId: String, val confirm: Boolean)

data class CallNotification(val caller: String, val roomName: String, val type: String, val username: String, val image: String, val name: String)

data class CallNotificationForSocket(
    val call: Call?,
    val createdAt: String?,
    val id: String?,
    val owner: String?,
    val reciever: String?,
    val senderId: String?,
    val senderLastname: String?,
    val senderName: String?,
    val senderUsername: String?,
    val type: String?,
    val updatedAt: String?
)

//data class StatusMessageReceived(val caller: String, val id: String)
//
//data class StatusMessageRead(val caller: String, val roomName: String)
