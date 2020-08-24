package com.example.dynamicmessenger.network.authorization.models

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dynamicmessenger.common.AppLangKeys
import com.example.dynamicmessenger.common.SharedConfigs
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat

@Parcelize
data class MailExistProperty(val mailExist: Boolean, val code: String) : Parcelable

@Parcelize
data class PhoneNumberExistProperty(val phonenumberExists: Boolean, val code: String) : Parcelable

@Parcelize
data class LoginProperty(val token: String, val tokenExpire: String, val user: User) : Parcelable

@Parcelize
data class ChangePhoneNumberOrEmailProperty(val user: User) : Parcelable

@Parcelize
@Entity(tableName = "user")
data class User (
    val gender : String?,
    @PrimaryKey
    val _id: String,
    var name: String?,
    val lastname: String?,
    val email: String?,
    val username: String?,
    val phoneNumber: String?,
    val address: String?,
    val birthday: String?,
    val info: String?,
    val avatarURL: String?,
    var missedCallHistory: List<String?>?
) : Parcelable

@Parcelize
data class UserTokenProperty(val tokenExists: Boolean?, val Error: String?) : Parcelable

@Parcelize
data class Users(val users: List<User>) : Parcelable

@Parcelize
@Entity(tableName = "chat")
data class Chat(
    @PrimaryKey
    val id: String,
    val name: String?,
    val lastname: String?,
    val username: String?,
    @Embedded
    val message: Message?,
    val recipientAvatarURL: String?,
    val chatCreateDay: String) : Parcelable

@Parcelize
data class Message(
    val senderId: String?,
    @Embedded
    val call: Call?,
    val type: String,
    val text: String?,
    var createdAt: String,
    val reciever: String,
    val senderUsername: String?) : Parcelable, Comparable<Message> {
    @SuppressLint("SimpleDateFormat")
    override fun compareTo(other: Message): Int {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val area1 = format.parse(createdAt)
        val area2 = format.parse(other.createdAt)
        if (area1 == area2) {
            return 0
        } else if(area1 < area2) {
            return -1
        }
        return 1
    }
//    "senderUsername":"dacuke",
//    "senderName":"David",
//    "senderLastname":"Melikyan"

}

@Parcelize
data class Sender(
    @PrimaryKey
    @ColumnInfo(name = "senderId") val id: String,
    @ColumnInfo(name = "senderName")val name: String) : Parcelable

@Parcelize
data class Call(
    val status: String?,
    val callSuggestTime: String?,
    @ColumnInfo(name = "callType")
    val type: String?,
    val duration: Double?) : Parcelable

@Parcelize
data class ChatRoom(
    val senderId: String?,
    val call: Call?,
    val type: String,
    val text: String?,
    val reciever: String,
    val createdAt: String) : Parcelable

@Parcelize
data class OnlineUsers(val usersOnline: List<String>) : Parcelable

@Parcelize
data class UsernameExists(val usernameExists: Boolean) : Parcelable