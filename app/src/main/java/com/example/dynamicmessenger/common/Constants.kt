package com.example.dynamicmessenger.common

import java.util.regex.Pattern

object ResponseUrls {
    const val ErosServerIP = "https://192.168.0.105:3000/"
    const val ErosServerIPForSocket = "https://192.168.0.105:3000"
    const val herokuIP = "https://messenger-dynamic.herokuapp.com/"
    const val herokuIPForSocket = "https://messenger-dynamic.herokuapp.com"
    const val reg = "register"
    const val mailIsExist = "mailExists"
    const val login = "login"
    const val updateUser = "updateuser"
    const val verifyToken = "tokenExists"
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
    const val registerDevice = "registerdevice"
    const val updateEmail = "updatemail"
    const val verifyEmail = "verifyemail"
    const val updatePhoneNumber = "updatephonenumber"
    const val verifyPhoneNumber = "verifyphonenumber"
    const val readCallHistory = "readcallhistory"
    const val confirmContactRequest = "confirmContactRequest"

//        users/me/avatar
//        users/5ee76c90bfa0980017a39013/avatar
}

object MyTime {
    const val oneDay = 86400000
    const val threeMinutes = 180000L
}

object ChanelConstants {
    const val CHANNEL_ID = "channel_id"
    const val MESSAGE_CHANNEL_ID = "message_channel_id"
    const val CALL_CHANNEL_ID = "call_channel_id"
    const val MISSED_CALL_CHANNEL_ID = "missed_call_channel_id"
    const val CONTACT_REQUEST_CHANNEL_ID = "contact_request_channel_id"
    val callVibratePattern = longArrayOf(500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500)
    const val CALL_MANAGER_ID = 1155
    const val MESSAGE_MANAGER_ID = 6578
    const val MISSED_CALL_MANAGER_ID = 2345
    const val CONTACT_REQUEST_MANAGER_ID = 3578
//    const val
}

object SharedPrefConstants {
    const val sharedPrefCreate = "messengerAuth"
    const val sharedPrefToken = "sharedToken"
    const val sharedPrefDarkMode = "darkMode"
    const val sharedPrefAppLang = "appLanguage"
}

object MyHeaders {
    const val contentType = "Content-Type: application/json"
    const val accept = "Accept: application/json"
    const val tokenAuthorization = "Authorization"
}

class ValidationConstants {
    companion object {
        val NAME_REGEX: Pattern = Pattern.compile("[a-zA-Z]{2,31}")
        val LAST_NAME_REGEX: Pattern = Pattern.compile("[a-zA-Z]{2,31}")
        val USERNAME_REGEX: Pattern = Pattern.compile("^[a-zA-Z0-9](_(?!(\\.|_|-))|\\.(?!(_|-|\\.))|-(?!(\\.|_|-))|[a-zA-Z0-9]){2,18}[a-zA-Z0-9]\$")
        val PHONE_NUMBER_REGEX: Pattern = Pattern.compile("([+])[0-9]{2,31}")
    }
}

object DataChanelMessages {
    const val turnOffMicrophone = "turn off microphone"
    const val turnOnMicrophone = "turn on microphone"
    const val turnCameraToFront = "turn camera to front"
    const val turnCameraToBack = "turn camera to back"
    const val turnCameraOff = "turn camera off"
    const val turnCameraOn = "turn camera on"
    const val opponentLeaveCall = "opponent leave call"
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
    NOT_SELECTED,
    CALLS,
    CHATS,
    INFORMATION,
    CONTACTS,
    CHAT_ROOM,
    OPPONENT_INFORMATION,
    UPDATE_INFORMATION,
    USER_IMAGE,
    CALL_INFORMATION,
    UPDATE_EMAIL,
    UPDATE_PHONE_NUMBER
}

//func speakerOff() {
//    self.audioQueue.async { [weak self] in
//            guard let self = self else {
//        return
//    }
//        self.rtcAudioSession.lockForConfiguration()
//        do {
//            try self.rtcAudioSession.setCategory(AVAudioSession.Category.playAndRecord.rawValue)
//            try self.rtcAudioSession.overrideOutputAudioPort(.none)
//            } catch let error {
//                debugPrint("Error setting AVAudioSession category: \(error)")
//            }
//            self.rtcAudioSession.unlockForConfiguration()
//        }
//}
//
//func speakerOn() {
//    self.audioQueue.async { [weak self] in
//            guard let self = self else {
//        return
//    }
//        self.rtcAudioSession.lockForConfiguration()
//        do {
//            try self.rtcAudioSession.setCategory(AVAudioSession.Category.playAndRecord.rawValue)
//            try self.rtcAudioSession.overrideOutputAudioPort(.speaker)
//            try self.rtcAudioSession.setActive(true)
//                } catch let error {
//                    debugPrint("Couldn't force audio to speaker: \(error)")
//                }
//                self.rtcAudioSession.unlockForConfiguration()
//            }
//}