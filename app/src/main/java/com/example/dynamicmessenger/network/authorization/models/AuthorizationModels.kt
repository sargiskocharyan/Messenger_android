package com.example.dynamicmessenger.network.authorization.models

data class EmailExistTask(val email: String)

data class RegistrationTask(val email: String, val code: String)

data class LoginTask (val email: String, val code: String)

data class UpdateUserTask (
    val name: String,
    val lastname: String,
    val username: String,
    val university: String? = null,
    val phoneNumber: String? = null,
    val info: String? = null,
    val address: String? = null,
    val gender: String? = null,
    val birthday: String? = null)

data class SearchTask (val term: String)

data class AddUserContactTask (val contactId: String)
