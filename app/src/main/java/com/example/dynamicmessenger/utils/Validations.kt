package com.example.dynamicmessenger.utils

import com.example.dynamicmessenger.common.ValidationConstants

class Validations {
    companion object {

        fun isEmailValid(email: String): Boolean {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun isNameValid(name: String): Boolean {
            return ValidationConstants.NAME_REGEX.matcher(name).matches()
        }

        fun isLastNameValid(lastName: String): Boolean {
            return ValidationConstants.LAST_NAME_REGEX.matcher(lastName).matches()
        }

        fun isUsernameValid(username: String): Boolean {
            return ValidationConstants.USERNAME_REGEX.matcher(username).matches()
        }

    }
}