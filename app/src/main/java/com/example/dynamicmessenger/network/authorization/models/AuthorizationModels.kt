package com.example.dynamicmessenger.network.authorization.models

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

data class CallNotification(val caller: String, val roomName: String, val username: String, val image: String, val name: String)
