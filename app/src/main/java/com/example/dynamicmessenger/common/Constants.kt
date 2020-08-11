package com.example.dynamicmessenger.common

import java.util.regex.Pattern

class ResponseUrls {
    companion object {
        const val ErosServerIP = "https://192.168.0.105:3000/"
        const val ErosServerIPForSocket = "https://192.168.0.105:3000"
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
        const val users = "users"
        const val avatar = "avatar"
        const val saveAvatar = "users/me/avatar"
        const val deleteUser = "users/me"
        const val deactivateUser = "deactivate/me"
        const val userInfoById = "user"
        const val onlineUsers = "onlineusers"
        const val removeContact = "removecontact"
        const val hideData = "hidedata"
        const val usernameExists = "usernameExists"
        const val callHistory = "callhistory"
        const val call = "call"

//        users/me/avatar
//        users/5ee76c90bfa0980017a39013/avatar
    }
}

object MyTime {
    const val oneDay = 86400000
    const val threeMinutes = 180000L
}

object ChanelConstants {
    const val CHANNEL_ID = "channel_id"
    const val MESSAGE_CHANNEL_ID = "message_channel_id"
    const val CALL_CHANNEL_ID = "call_channel_id"
//    const val
}

class SharedPrefConstants {
    companion object {
        const val sharedPrefCreate = "messengerAuth"
        const val sharedPrefMail = "mail"
        const val sharedPrefIsMailExist = "isMailExist"
        const val sharedPrefToken = "sharedToken"
        const val sharedPrefCode = "code"
        const val sharedPrefDarkMode = "darkMode"
        const val sharedPrefReceiverID = "receiverID"
        const val sharedPrefIsContacts = "isContacts"
        const val sharedPrefAppLang = "appLanguage"
        const val sharedPrefReceiverAvatar = "receiverAvatar"
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
        val USERNAME_REGEX = Pattern.compile("^[a-zA-Z0-9](_(?!(\\.|_|-))|\\.(?!(_|-|\\.))|-(?!(\\.|_|-))|[a-zA-Z0-9]){2,18}[a-zA-Z0-9]\$")
    }
}

class IntentExtra {
    companion object {
        const val receiverId = "receiverID"
    }
}

enum class AppLangKeys(var value: String) {
    EN("en"),
    RU("ru"),
    AM("hy");

    companion object {
        private val mapping = values().associateBy(AppLangKeys::value)
        fun fromValue(value: String) = mapping[value] ?: EN
    }
}

enum class AppMode {
    dark, light
}

enum class MyFragments {
    CALLS,
    CHATS,
    INFORMATION,
    CONTACTS,
    CHAT_ROOM,
    OPPONENT_INFORMATION
}