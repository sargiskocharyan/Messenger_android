package com.example.dynamicmessenger.common

import java.util.regex.Pattern

class ResponseUrls {
    companion object {
        const val ErosServerIP = "http://192.168.0.105:3000/"
        const val ErosServerIPForSocket = "http://192.168.0.101:3000"
        const val herokuIP = "https://messenger-dynamic.herokuapp.com/"
        const val herokuIPForSocket = "https://messenger-dynamic.herokuapp.com"
        const val reg = "register"
        const val mailIsExist = "mailExists"
        const val login = "login"
        const val updateUser = "updateuser"
        const val verifyToken = "tokenExists"
        const val allUniversity = "university/all"
        const val contacts = "contacts"
        const val searchContacts = "findusers"
        const val addContact = "addcontact"
        const val logout = "user/logout"
        const val chats = "chats"
    }
}

class SharedPrefConstants {
    companion object {
        const val sharedPrefCreate = "messengerAuth"
        const val sharedPrefUniversity = "university"
        const val sharedPrefMail = "mail"
        const val sharedPrefIsMailExist = "isMailExist"
        const val sharedPrefToken = "sharedToken"
        const val sharedPrefCode = "code"
        const val sharedPrefUser = "user"
        const val sharedPrefDarkMode = "darkMode"
        const val sharedPrefReceiverID = "receiverID"
        const val sharedPrefIsContacts = "isContacts"
        const val sharedPrefAppLang = "appLanguage"
    }
}

class MyHeaders {
    companion object {
        const val contentType = "Content-Type: application/json"
        const val accept = "Accept: application/json"
        const val tokenAuthorization = "Authorization"
    }
}

class ValidationConstants {
    companion object {
        val NAME_REGEX = Pattern.compile("[a-zA-Z]{2,31}")
        val LAST_NAME_REGEX = Pattern.compile("[a-zA-Z]{2,31}")
        val USERNAME_REGEX = Pattern.compile("^[a-zA-Z0-9](_(?!(\\.|_|-))|\\.(?!(_|-|\\.))|-(?!(\\.|_|-))|[a-zA-Z0-9]){6,18}[a-zA-Z0-9]\$")
    }
}

class IntentExtra {
    companion object {
        const val receiverId = "receiverID"
    }
}

//class AppLangKeys {
//    companion object {
//        val EN = "en"
//        val RU = "ru"
//        val AM = "hy"
//    }
//}
enum class AppLangKeys(val value: String) {
    EN("en"),
    RU("ru"),
    AM("hy")
}

enum class AppMode {
    dark, light
}