package com.example.dynamicmessenger.network.authorization.models

data class EmailExistTask(val email: String)

data class RegistrationTask(val email: String, val code: String)

data class LoginTask (val email: String, val code: String)

data class UpdateUserTask (val name: String, val lastname: String, val username: String, val university: String)

data class SearchTask (val term: String)

data class AddUserContactTask (val contactId: String)
